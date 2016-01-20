# Guaranteed message processing order

Since version 0.4

## Description

CleverBus allows to garant processing order of messages. This functionality garants that incoming messages with the same *funnel\_value* will start processing in order by *msgTimestamp* (timestamp from source system) and next message won't start before previous message isn't finished.

Guaranteed message processing order takes all the following states into consideration: *PROCESSING*, *WAITING*, *WAITING\_FOR\_RES*, *PARTLY\_FAILED*, FAILED and *POSTPONED*. *FAILED* state is used by default but can be excluded. 

It can happen that first message failed and then all next messages will wait to start processing (message state will change to <i>POSTPONED</i> again and again).<p>There is new <a href='../../User-guide/Configuration'>configuration</a>) parameter <i>asynch.postponedIntervalWhenFailed</i> that determines interval (in seconds) after that postponed messages will fail.</p>

## Comparison to msg-funnel

[Msg-funnel](../CleverBus-components/msg-funnel) component filters messages in one specific place of the processing but this gauranteed processing order functionality is for wholes routes at the beginning.

There are the following types of message filtering:

-   **classic funnel** ([msg-funnel](../CleverBus-components/msg-funnel) component) - filters processing messages (states *PROCESSING*, *WAITING*, *WAITING\_FOR\_RES*) by *funnel\_value* in specific place of the route, most often before communication with external system to ensure that only one specific value at one time is send to it. For example telco provider can process one request of one specific subscriber (msisdn) at one moment only and we need to filter out requests to this system if there are more requests for one subscriber (msisdn).
-   **classic funnel with guaranteed order** ([msg-funnel](../CleverBus-components/msg-funnel) component) - component has optional parameter *guaranteedOrder* to turn on guaranteed order - messages are filtered out by *funnel\_value* (it's same as classic funnel)* *and also *msgTimestamp* of the message is taken into consideration. Messages must be processed at specific funnel in order by *msgTimestamp*. *PARTLY\_FAILED*, *FAILED* and *POSTPONED* states are used together with processing states
-   **guaranteed message processing order** - this functionality that is about whole routes

## How to use it?

Use *org.cleverbus.api.asynch.AsynchRouteBuilder* with methods *withGuaranteedOrder()* or *withGuaranteedOrderWithoutFailed()* (see [Asynchronous messages](../Asynchronous-messages) for more details) or sets *AsynchConstants.GUARANTEED\_ORDER\_HEADER* or *AsynchConstants.EXCLUDE\_FAILED\_HEADER* to *true* when you create route definition manually.

``` java
private void createRouteForAsyncHelloRouteIn() throws FailedToCreateRouteException { 
    Expression funnelValueExpr = xpath("/h:asyncHelloRequest/h:name").namespaces(ns).stringResult();
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
            .withFunnelValue(funnelValueExpr)
            .withGuaranteedOrder()
            .build(this);
}
```

