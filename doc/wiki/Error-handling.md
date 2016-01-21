# Error handling

Basic <a href='http://camel.apache.org/error-handling-in-camel.html'>error handling</a> concept is well-documented in Apache Camel.

## Exceptions hierarchy

All CleverBus exceptions are in package *org.cleverbus.api.exception:*

-   *IntegrationException*: parent Exception for all CleverBus exceptions
    -   *LockFailureException*: unsuccessful getting lock for the record from DB (most often it means that another process was faster and acquired lock before)
    -   *MultipleDataFoundException*: one record was expected but more records were found
    -   *NoDataFoundException*: at least one records was expected but no record was found
    -   *ThrottlingExceededException*: this exception is thrown when [throttling](CleverBus-Components/throttling) limits were exceeded
    -   *StoppingException*: when CleverBus is in [stopping mode](Maintenance) then this exception will be thrown when new requests arrive
    -   *ValidationIntegrationException*: input data are not valid
        -   *IllegalDataException*: wrong data

## How it works?

Basic algorithm of error handling is implemented in parent class of routes - *org.cleverbus.api.route.AbstractBasicRoute*.

### Is message asynchronous?

If **yes** then next processing steps are determined according to exception/error type:

-   there is **error that can't be resolved/repaired during next tries** (e.g. wrong input data, wrong data in database etc.). [Message state is changed](Operations-which-change-message-state) to *FAILED* state because next processing doesn't have sense. Message processing is redirected to URI: *AsynchConstants.URI\_ERROR\_FATAL*
-   there is **temporary error/exception where is chance to resolve/repair it in next tries** (e.g. external system is unavailable, error because of concurrent message procesing etc.). [Message state is changed](Operations-which-change-message-state) to *PARTLY\_FAILED* state. Message processing is redirected to URI: *AsynchConstants.URI\_ERROR\_HANDLING* - message will wait in this state for specified interval and then starts processing again.

Example of error handling in communication via Web Services with external system:

### asynchronous error handling

``` java
private static final Class[] FATAL_EXCEPTIONS = new Class[] {AlreadyExistsException.class,
    ValidityMismatchException.class, ChargingKeyNotFoundException.class, NonExistingProductOfferingException.class, IllegalOperationException.class};
private static final Class[] NEXT_HANDLING_EXCEPTIONS = new Class[] {CustomerNotFoundException.class,
    CustomerAccountNotFoundException.class};
 
 
.doTry()
    .to(getBillingUri())
    // explicitly converts to UTF-8
    .convertBodyTo(String.class, "UTF-8")
    // XML -> specific payload implementation of child for error check
    .unmarshal(getUnmarshalDataFormat())
.doCatch(WebServiceIOException.class)
    .setProperty(ExceptionTranslator.EXCEPTION_ERROR_CODE, constant(ErrorEnum.E600))
    .to(AsynchConstants.URI_ERROR_HANDLING)
    // we handle all exceptions in the same way there are two big catches here
.doCatch(NEXT_HANDLING_EXCEPTIONS)
    .setProperty(ExceptionTranslator.EXCEPTION_ERROR_CODE, constant(ErrorEnum.E602))
    .to(AsynchConstants.URI_ERROR_HANDLING)
.doCatch(FATAL_EXCEPTIONS)
    .setProperty(ExceptionTranslator.EXCEPTION_ERROR_CODE, constant(ErrorEnum.E601))
    .to(AsynchConstants.URI_ERROR_FATAL)
.end();
```

If **not** (=message is synchronous) then error handling is redirected to URL: *AsynchConstants.URI\_EX\_TRANSLATION* and then to *ExceptionTranslator.class*. Exception is propagated back to source system.

### synchronous error handling

``` java
.onException(WebServiceIOException.class)
    .handled(true)
    .setProperty(ExceptionTranslator.EXCEPTION_ERROR_CODE, constant(ErrorEnum.E600))
    .process(ExceptionTranslator.getInstance())
    .end()
.onException(FATAL_EXCEPTIONS)
    .handled(true)
    .setProperty(ExceptionTranslator.EXCEPTION_ERROR_CODE, constant(ErrorEnum.E601))
    .process(ExceptionTranslator.getInstance())
    .end()
.onException(NEXT_HANDLING_EXCEPTIONS)
    .handled(true)
    .setProperty(ExceptionTranslator.EXCEPTION_ERROR_CODE, constant(ErrorEnum.E602))
    .process(ExceptionTranslator.getInstance())
    .end()

.to(getSapUri())
// explicitly converts to UTF-8
.convertBodyTo(String.class, "UTF-8")
// XML -> specific payload implementation of child for error check
.unmarshal(getUnmarshalDataFormat())
```

### Custom error processing

[Error handling concept in described in Apache Camel](http://camel.apache.org/error-handling-in-camel.html) where you can find more details.

In common there are two types of error handling - *routes specific* and *global,* there are two ways how to catch exceptions - with *[Exception Clause](http://camel.apache.org/exception-clause.html)* or with *[DoTry Clause](http://camel.apache.org/try-catch-finally.html).*

### Mapping exceptions in WSDL

WSDL contract allows to define exceptions (aka faults) directly in *operation* definition.

``` xml
 <wsdl:operation name="createSubscriber">
            <wsdl:input message="tns:createSubscriber" name="createSubscriber">
            </wsdl:input>
            <wsdl:output message="tns:createSubscriberResponse" name="createSubscriberResponse">
            </wsdl:output>
            <wsdl:fault message="tns:CustomerAccountNotFoundException" name="CustomerAccountNotFoundException">
            </wsdl:fault>
            <wsdl:fault message="tns:CustomerNotFoundException" name="CustomerNotFoundException">
            </wsdl:fault>
            <wsdl:fault message="tns:AlreadyExistsException" name="AlreadyExistsException">
            </wsdl:fault>
        </wsdl:operation>
```

 

Example of fault response with exception detail:

``` xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
         <soap:Body>
             <soap:Fault>
                 <faultcode>soap:Server</faultcode>
                 <faultstring>Bussiness violation</faultstring>
                 <detail>
                     <ns2:ValidityMismatch xmlns:ns2="http://ws.lbss.com/">
                         <message>
                        Product offering with externalNo:-1 is not valid on date:Sat May 25 00:00:00 CEST 2013
                         </message>
                     </ns2:ValidityMismatch>
                 </detail>
             </soap:Fault>
         </soap:Body>
</soap:Envelope>
```

There is helper parent class *AbstractSoapExceptionFilter* for mapping SOAP fault exceptions (*SoapFaultClientException*) to internal Java exceptions from WSDL contract.

Example of *CrmSoapExceptionFilter*:

``` java
.onException(WebServiceIOException.class)
    .handled(true)
    .setProperty(AsynchConstants.EXCEPTION_ERROR_CODE, constant(ErrorEnum.E403))
    .process(ExceptionTranslator.getInstance())
    .end()
.onException(SoapFaultClientException.class)
    .handled(true)
    .process(new CrmSoapExceptionFilter(false))
    .end()
```
