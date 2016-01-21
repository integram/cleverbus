# Best practices

## Dependencies between routes

There is used IoC container by Spring framework for CleverBus routes configuration and initialization. Therefore it's not problem to use everything what Spring framework offers in this area (for example auto-wiring). Nevertheless, it's very useful to initialize or activate only those routes which there are necessary for unit tests and then it's good practice to leave dependency management on [Apache Camel](http://camel.apache.org/dependency-injection.html). In other words solve dependencies on the fly, not during application initialization as auto-wiring.

Camel uses registry abstraction for dependency management and most often implementation is Spring framework.

More information: <http://camel.apache.org/how-does-camel-look-up-beans-and-endpoints.html>

## Error codes for external calls

It's good practice to define error code, that extends *org.cleverbus.core.common.exceptions.ErrorExtEnum interface,* for each external call failure and this error code propagates to source system. 

Integration platform is central point and if there is any error then it's first point where somebody try to find out reasons of failure, where did error occur. Therefore is very useful to define unique error codes for all external call failures to have immediately information where is the problem.

There is error catalogue presentation in [Admin GUI](Admin-GUI).

## Use type converters

There is one very often EIP integration pattern - [message translation](http://camel.apache.org/message-translator.html). Often is necessary to convert one data structure to another data structure. You can use lot of approaches directly in route implementation but it's often related to specific route only. Therefore it's good practice to use [Type Converters](http://camel.apache.org/type-converter.html) in Camel.

### What are common goals?

-   encapsulate transformation logic to one place
-   limit to bad practices
    -   OUT modules can't depend on IN modules (for example billing routes shoudn't be dependant on customer routes)
    -   common project code can't depend on module specifics

IN routes are generally specific for the project but OUT routes are specific for calling system. And then if external system is used in more projects then it would be nice to reuse it or move it to [CleverBus extensions](CleverBus-extensions) for further use.

### How to make it?

-   use interfaces to solve dependency between common and specific code - common code defines interface and specific code implements it.
-   module dependency
    -   use converters and use *.convertBodyTo(class)* in route implementation
    -   place converters to IN modules because there should be no dependency to IN module in OUT module

## Interface versioning

If there is any public API (e.g. WSDL for Web Services) then it's necessary to handle backward compatibility of this interface.

Backward compatible changes:

-   adding new operation
-   adding new (XML) type to schema

Non-backward compatible changes:

-   removing operation
-   renaming operation
-   changes in (XML) types or message attributes
-   changes in namespace

### Versioning

Use versions in format *\<major\>.\<minor\>.*

*Minor* version is for compatible changes, *major* version indicates non-compatible changes.

Versioning should be explicit - it means to present version number in elements, URLs etc.:

-   add *major* and *minor* version to WSDL name: *MyService-v1.2.wsdl*
-   add *major* version to *targetNamespace* of WSDL: *\<definition targetNamespace="[http://cleverbss.org/ws/MyService-v1](http://cleverbss.cleverlance.com/ws/MyService-v1)" xmlns="<http://schemas.xmlsoap.org/wsdl/>"\>*
-   add *major* and *minor* version to *portType* element: *\<portType name="MyServicePort-v1.2"\>*
-   add *major* and *minor* version to  *service* element: *\<service name="MyService-v1.2"\>*
-   add *major* and *minor* version to *endpoint*: *\<[soap:address](http://soapaddress) location="http://cleverbss.org/myService/v1"/\>*

If there is change in WSDL's version then change versions of XSDs as well.
