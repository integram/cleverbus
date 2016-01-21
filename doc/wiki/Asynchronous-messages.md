# Asynchronous messages


## Receive asynchronous message

**Receive asynchronous messages** and storing them to DB:

-   all essential things are solved in *AsynchInMessageRoute* (trace header recognition, create message, persist to DB, exception handling)
-   use *AsynchRouteBuilder *to implement inbound asynchronous message

<i>AsynchRouteBuilder</i> is since 0.2 version

``` java
    /**
     * Route for asynchronous <strong>asyncHello</strong> input operation.
     * <p/>
     * Prerequisite: none
     * <p/>
     * Output: {@link AsyncHelloResponse}
     */
    private void createRouteForAsyncHelloRouteIn() throws FailedToCreateRouteException {
        Namespaces ns = new Namespaces("h", SyncHelloRoute.HELLO_SERVICE_NS);
   
        // note: mandatory parameters are set already in XSD, this validation is extra
        XPathValidator validator1 = new XPathValidator("/h:asyncHelloRequest", ns, "h:name1");
 
        // note: mandatory parameters are set already in XSD, this validation is extra
        XPathValidator validator2 = new XPathValidator("/h:asyncHelloRequest", ns, "h:name2");
   
        // note: only shows using but without any influence in this case
        Expression nameExpr = xpath("/h:asyncHelloRequest/h:name").namespaces(ns).stringResult();
  
        AsynchRouteBuilder.newInstance(ServiceEnum.HELLO, OPERATION_NAME,
                getInWsUri(new QName(SyncHelloRoute.HELLO_SERVICE_NS, "asyncHelloRequest")),
                new AsynchResponseProcessor() {
                    @Override
                    protected Object setCallbackResponse(CallbackResponse callbackResponse) {
                        AsyncHelloResponse res = new AsyncHelloResponse();
                        res.setConfirmAsyncHello(callbackResponse);
                        return res;
                    }
                }, jaxb(AsyncHelloResponse.class))
                .withValidator(validator1, validator2)
                .withObjectIdExpr(nameExpr)
                .build(this);
    }
```

*[AsynchRouteBuilder](https://hudson.clance.local/hudson/job/CleverBus/javadoc/com/cleverlance/cleverbus/core/common/asynch/AsynchRouteBuilder.html)* creates uniform design of asynchronous routes (processes) with the following processing steps (low-level approach):

-   settings the name of source service and operation. This information (based name conventions) are necessary to resolving of start endpoint to process asynchronous part.
-   **execution of general validation (check required values)**. If a validation error occurs then CleverBus will throw *ValidationIntegrationException* or *org.apache.camel.ValidationException* (from Apache Camel).
-   redirection to "*to(AsynchConstants.URI\_ASYNCH\_IN\_MSG)*"
-   using *AsynchResponseProcessor *- checking whether created message was successfully persisted or some error did not occur, after that correct response will be produced.

Hereby accepting an asynchronous message ends, is persisted in internal database for further processing and CleverBus sent confirmation to external system that message was adopted.

### Asynchronous message processing

-   at first incoming asynchronous message is persisted and almost immediately is stored into queue for further processing. New inbound messages are not queried from database and therefore are not locked for the specific node of cluster. CleverBus pulls only messages in *PARTLY\_FAILED* and *POSTPONED* state - messages which failed or were postponed.
-   messages are stored in [SEDA queue](http://camel.apache.org/seda.html). To version 0.4 included there is classic FIFO queue where messages were processed in the order in which they were inserted into the queue. Since version 0.4 this behavior is implemented by priority queue where new messages are processed earlier than postponed messages or in next attempt of processing.
-   message is dynamically routed into start endpoint. Expected URI of this endpoint has to match with this format:

        "direct:SERVICE_OPERATION_OUTROUTE"

    where *SERVICE* is value from enum interface implementation *ServiceEnum*, *OPERATION* is name of operation and *OUTROUTE* is const [AbstractBasicRoute\#OUT\_ROUTE\_SUFFIX](https://hudson.clance.local/hudson/job/CleverBus/javadoc/com/cleverlance/cleverbus/core/common/route/AbstractBasicRoute.html#OUT_ROUTE_SUFFIX). Service and operation names are values configured via *AsynchRouteBuilder*, see also *AsynchConstants.SERVICE\_HEADER* and *AsynchConstants.OPERATION\_HEADER*

-   main algorithm for processing of asynchronous messages you can find in class *AsynchMessageRoute*

## Implementation of asynchronous route

**Implementation of route for asynchronous message is the same as synchronous message but there are some differences which** must be followed:

-   header (Camel header) *AsynchConstants.MSG\_HEADER* contains entity of *Message*, body contains storable (serialized, marshalled) payload of message (e.g. for communication via SOAP it will be XML of request) of original message
-   **for each external system call (for example only one system with multiple calls) it must check duplicate calls**. One asynchronous message (=one correlationID) can be processed repeatedly - for example: to create subscriber in [MVNO](http://en.wikipedia.org/wiki/Mobile_virtual_network_operator)solution the Billing and [MNO](http://en.wikipedia.org/wiki/Mobile_network_operator) systems have to be called. If the Billing system was called successfully but MNO failed the message is persisted with PARTLY\_FAILED status and in future will be reprocessed (redelivered). When this message will be reprocessed, successfully calls have to be skipped.
    -   **important**: asynchronous messages are processed repeatedly (number of attempts and how often are configurable values)
-   if processing of message is simulated - fake processing ("blank" = unsuccessfully processing is not error \> failed count must not be incremented) you have to setup properly header *AsynchConstants.NO\_EFFECT\_PROCESS\_HEADER* as *true* value

### Exceptions

-   if in the frame of process an external system is called so this call can be unsuccessfully - either some expected error is thrown (= exception declared in operation) or another exception is thrown as error during processing message. The type of error is "business" exception (e.g. value of invoice is negatively and it does not make sense or we want to create customer in external system which already exists), second type is internal error in external system, where it is necessary to try to call external system for some time. But if this error is expected "business" exception so process should remember these exceptions for confirmation scenario to system that invokes this process.
    -   **if some expected exception occurs so has to be catched and stored into property** with name that will end on** ***AsynchConstants.BUSINESS\_ERROR\_PROP\_SUFFIX* (there may be more exceptions, and each of them will be saved as a separate value in property under name with appropriate suffix)
-   **during processing of asynchronous message it is possible to call more external systems. The order of calling not has to be randomly therefore if any calling of external system fails, a error is thrown and process is stopped.** After some time (configurable) CleverBus will pick the message with *MessagePollExecutor *from DB and the message is processed again.
-   **validate exceptions (=*org.apache.camel.ValidationException*) are resolved as business exceptions**, so if this error occurs processing of message ends with status *FAILED*
-   each call which defines route, is extended from *AbstractBasicRoute* where is basic mechanism of error handling implemented, see [Error handling](Error-handling)
-   exceptions can be catched and processed immediately in the route or can be propagated to superior route, i.e. route whence the route was called - it is solved by *errorHandler(noErrorHandler())*. More information you can find in Apache Camel documentation - [Error handling in Camel](http://camel.apache.org/error-handling-in-camel.html)

### Transferring state among attempts

-   sometimes is necessary to transfer stateful information of message among individual attempts, for example information, which IDs of customer collection were processed
-   for these purposes there is property *AsynchConstants.CUSTOM\_DATA\_PROP*, which can contain random string data (e.g. map of string and so on). The value of this property is at the end of message processing included in message entity: *Message\#getCustomData*

### Check obsolete messages in the queue

-   control of obsolete messages is solved by [extcall](extcall) component
-   the messages that failed the first time to process and amends existing data, it is necessary to check whether the data to be processed again. You can imagine that new message for same operation and same entity is received but was processed.

        MSG1 setCustomer(externalCustomerId=5) OK
        MSG2 setCustomer(externalCustomerId=5) PARTLY_FAILED
        MSG3 setCustomer(externalCustomerId=5) OK

    Message *MSG2* must not be further processed because there exists the message *MSG3*, which is newer and changes the same entity with same "object ID".
-   **to make it possible to centrally check it so each entity has a specific unique identifier which we call "object ID". This identifier is mandatory and has to be set during processing inbound message via *AsynchConstants.OBJECT\_ID\_HEADER header.***

        .setHeader(AsynchConstants.OBJECT_ID_HEADER, ns.xpath("/cus:setCustomerRequest/cus:customer/cus1:externalCustomerID", String.class))
-   for accurate entity identification CleverBus uses (by default) the name of operation and object ID, but sometimes it is not sufficient because for example change of customer is contained in several operations. To change this behaviour is necessary to use header value *AsynchConstants.ENTITY\_TYPE\_HEADER*, which is then used instead of the name of the operation.
    -   Example: we have two operations *setCustomerExt* and *createCustomerExtAll*, which change customer. In this scenario the name of operation is not sufficient and therefore we use ENTITY\_TYPE\_HEADER.
-   if CleverBus evaluates that message is obsolete then will have new status *SKIPPED* and hereafter already will not be processed
-   this is not necessary to check for messages where new objects are created

### Processing of message by splitter pattern to child (partial) messages

-   if a message is too complex to process as altogether it is recommended (appropriate solution) to split into small child messages (partial messages). Main (parent) message will be successfully processed when all her child messages will be also successfully processed. Conversely, if any partial message will in *FAILED* status, then the main message will in *FAILED* status.
-   **to split message into partial messages use *MessageSplitter***, where is necessary to implement *getChildMessages* method*:*

``` java
/**
 * Gets child messages for next processing.
 * Order of child messages in the list determines order of first synchronous processing.
 *
 * @param parentMsg the parent message
 * @param body the exchange body
 * @return list of child messages
 */
protected abstract List<ChildMessage> getChildMessages(Message parentMsg, Object body);
```

Implementation of *MessageSplitter* must be as Spring bean to resolve next dependencies:

-   child messages are at first attempt processed synchronously in respectively order. When any processing of child message fails then the order during next processing is not guaranteed. **Therefore for this reason it is necessary to write implementation of child messages completely independent of the order.**

### Confirmation the result of processing asynchronous messages

when asynchronous message is processed (is in final status) then CleverBus can transmit information about result of processing - OK, FAILED or CANCEL final status.
main interface is *ConfirmationCallback*, which has now two implementations:
-   *DefaultConfirmationCallback* - default behaviour (rather suitable for tests), which only logs information about result
-   *DelegateConfirmationCallback* - based upon source system it chooses properly implementation of *ExternalSystemConfirmation* interface*, which *as callback calls external system to confirm result.

Design of this functionality is so flexible because:

-   not every system wants be informed about result of message processing
-   every system can have specific requirements to confirmation (confirmation via web service, db call and so on)
-   CleverBus provides own defined WSDL [asynchConfirmation-v1.0.wsdl](attachments/524326/917547.wsdl) with XSD [asynchConfirmationOperations-v1.0.xsd](attachments/524326/917548.xsd) to auto confirmation solution

