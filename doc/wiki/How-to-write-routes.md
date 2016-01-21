# How to write routes?

## Description

This page contains list of steps and tips for implementation of new routes.

Look at [How to start new project?](-start-new-project) for starting new project ...

Look at [Development tips](Development-tips) and [Best practices](Best-practices).

Adhere to [conventions](Source-code-conventions) how to write the code.

## Create new routes

-   class name should ends with suffix *Route*
-   adhere to the rules of [naming conventions](Source-code-conventions) for route IDs (use *getInRouteId()*, *getOutRouteId()* or *getRouteId()* functions) and URIs
-   use parent class *org.cleverbus.api.route.AbstractBasicRoute*
-   use *@CamelConfiguration* and define unique Spring bean name for this route - this annotation allows initialization by Spring auto-scanning funcionality. Don't forget to check auto-scanning configuration.
-   define operation name 

### Unit tests

Each route implementation must have corresponding unit tests, at least one successful scenario.

-   create unit test that extends *AbstractTest* or *AbstractDbTest *(both classes are from test *module*) if database support is needed
-   you can use Spring test profiles - see *com.cleverlance.cleverbus.modules.TestProfiles*
-   use *@ActiveRoutes* annotation that defines which routes will be activated for specific unit test

You can use *RouteBeanNameGenerator* for automatic bean names generation.

## New synchronous route implementation

Synchronous route is route where source system waits for response. This type of requests are not stored in CleverBus evidence, there are mentions in log files only. Possible error is immediately propagated to source system.

Steps for implementation are identical to those mentioned in previous chapter.

Every new synchronous route must contains ***traceHeader*** element that contains ***traceIdentifier*** element with tracing information of incoming message.

SOAP example:

``` xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:com="http://cleverbss.cleverlance.com/ws/Common-v1" xmlns:hel="http://cleverbus.cleverlance.com/ws/HelloService-v1">
   <soapenv:Header>
      <com:traceHeader>
         <com:traceIdentifier>
            <com:applicationID>APPL002</com:applicationID>
            <com:timestamp>2015-05-21T08:54:58.147+02:00</com:timestamp>
            <com:correlationID>${=java.util.UUID.randomUUID()}</com:correlationID>
            <!--Optional:-->
            <com:processID>process001</com:processID>
         </com:traceIdentifier>
      </com:traceHeader>
   </soapenv:Header>
   <soapenv:Body>
      <hel:syncHelloRequest>
         <hel:name>CleverBus team</hel:name>
      </hel:syncHelloRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

If traceHeader is not part of SOAP message, default traceHeader will be generated or route must implements TraceHeaderProvider interface for getting traceHeader.

Example of TraceHeaderProvider implementation:
``` java
CamelConfiguration(value = SyncOutageRoute.ROUTE_BEAN)
public class SyncOutageRoute extends AbstractBasicRoute implements TraceHeaderProvider {

  private static final String ROUTE_ID_SYNC_OUTAGE = getRouteId(ServiceEnum.OUTAGE, OPERATION_NAME);
  ...

  public String getRouteId() {
      return ROUTE_ID_SYNC_OUTAGE;
  }

  public TraceHeader getTreaceHeader(Exchange exchange) {
      TraceIdentifier traceIdentifier = new TraceIdentifier();
      traceIdentifier.setTimestamp(DateTime.now());
      traceIdentifier.setCorrelationID(UUID.randomUUID().toString());
      traceIdentifier.setApplicationID("SAP");

      TraceHeader result = new TraceHeader();
      result.setTraceIdentifier(traceIdentifier);
      return result;
  }
}
```

## New asynchronous route implementation

[How to implement asynchronous routes](Asynchronous-messages) is described in another page.

## Route implementation tips

Checklist of features which can be used during route implementation:

-   [throttling](throttling) - route for processing asynchronnous input requests contains throttling by default. You should add it to all synchronnous routes
-   [external call](extcall) - each call to external system should go through "external call" funcionality
-   route authorization is made by Camel policy, see [Camel Security](http://camel.apache.org/camel-security.html)
-   [Guaranteed message processing order](Guaranteed-message-processing-order)
-   checking of obsolete messages via object ID
-   look at [components](CleverBus-components) for use - [msg-funnel](msg-funnel), [asynch-child](asynch-child), ...
-   define senders *CloseableHttpComponentsMessageSender* for calling external system via Spring Web Service (SOAP messages). Add configuration into */META-INF/sp\_ws\_wsdl.xml*


Use [SoupUI](http://www.soapui.org) integration test for calling real web service to verify that everything works as expected. Unit test is good to have but it doesn't catch all possible problems.

## Example implementations

Example routes are in *examples* module. There are two basic examples - synchronous and asynchronous *hello world* service.

WSDL is available on */ws/hello.wsdl* and endpoint on the URL */ws/hello/v1*

### WSDL and XSD implementation

-   WSDL and XSD is defined in [Contract-First design approach](http://docs.spring.io/spring-ws/site/reference/html/why-contract-first.html) - firstly define web service interfaces, secondly implement them in specific language.
-   there are two files (in package *resources*/*org.cleverbus.modules.in.hello.ws.v1\_0*): WSDL definition (e.g. *hello-v1.0.wsdl*) and XSD with requests/responses definition (*helloOperations-v1.0.xsd*). XSD contains request definitions, imports *commonTypes-v1.0.xsd* with common types
-   publish WSDL/XSD files - add configuration to */META-INF/sp\_ws\_wsdl.xml.* *[sws:static-wsdl](http://swsstatic-wsdl)* is used for publishing WSDL, XSD files have to be published separately. See [Spring Web Services reference manual](http://docs.spring.io/spring-ws/site/reference/html/tutorial.html) where you find more information because Spring WS is underlying library for web service communication.
-   register XSD schemas for validation incoming/outgoing messages in configuration of "*validatingInterceptor*" (class *HeaderAndPayloadValidatingInterceptor*)
    -   *traceHeader* is mandatory for asynchronous requests only - set synchronnous requests in *ignoreRequests* property to ignore this validation
-   configure conversion to Java classes - use *jaxws-maven-plugin* Maven plugin for conversion from WSDL or *jaxb2-maven-plugin* for conversion from XSD

### Synchronous Hello service

Synchronous implementation is in class *[org.cleverbus.modules.in.hello.SyncHelloRoute](https://hudson.clance.local/hudson/view/CleverBus/job/CleverBus%20release/javadoc/com/cleverlance/cleverbus/modules/in/hello/SyncHelloRoute.html)*.

If comes web service request with element name *syncHelloRequest* and namespace *<http://cleverbus.org/ws/HelloService-v1>*) then this route is "activated" and request starts processing. Response is immediately send back to the source system.

### Asynchronous Hello service

Asynchronous implementation is in class *[org.cleverbus.modules.in.hello.AsyncHelloRoute](https://hudson.clance.local/hudson/view/CleverBus/job/CleverBus%20release/javadoc/com/cleverlance/cleverbus/modules/in/hello/AsyncHelloRoute.html)*.

If comes web service request with element name *asyncHelloRequest* and namespace *<http://cleverbus.org/ws/HelloService-v1>*) then this route is "activated" and request starts processing. Firstly synchronous part is proceeded - input request is validated and saved into queue (database) and synchronous response is immediately send back to the source system with information that CleverBus accepted input request and ensures request processing. When request processing finishes then asynchronous confirmation with processing result is send to source system.

### Unit tests implementation

There are corresponding unit tests to each route - *SyncHelloRouteTest* resp.*AsyncHelloRouteTest*.

