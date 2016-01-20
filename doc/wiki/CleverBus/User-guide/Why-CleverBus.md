# Why CleverBus?

CleverBus is Apache Camel on steroids. Why not get more?

-   CleverBus is based on matured, well-proofed and very popular integration engine **[Apache Camel](http://camel.apache.org)**
-   CleverBus extends basic Camel functionality to **be more productive and effective** in common integration implementation
-   CleverBus is **proven and stable** solution used in production environments
-   CleverBus is **[open-source](https://github.com/integram/cleverbus) with quality code,** covered by many unit tests
-   CleverBus uses database as queue for asynchronous messages processing. Database is well-known technology for most people to administrate or use it. Nevertheless, we want to offer alternative, we want to support JMS as queue.
-   CleverBus is **technologically neutral** to operation system, application server and database.
-   CleverBus together with application server like [Apache Tomcat](http://tomcat.apache.org) and database like [PostgreSQL](http://www.postgresql.org) represent **light-weight ESB solution** (server and database can be different)
-   CleverBus uses another well-proven open-source libraries such as [Spring framework](http://projects.spring.io/spring-framework/), [Spring security](http://projects.spring.io/spring-security/), [Spring Web Services](http://projects.spring.io/spring-ws/) or [HttpClient](http://hc.apache.org/httpclient-3.x/)
-   CleverBus can **use same tools which can be used by Apache Camel**, for example [Red Hat® JBoss® Fuse](http://www.redhat.com/en/technologies/jboss-middleware/fuse) or [Hawtio](http://hawt.io)
-   CleverBus has own **[web administration console](Admin-GUI)**

We have many years of experience with integration projects, we know common problems, we know how to solve it effectively. CleverBus is established on this know-how.

## Main CleverBus extensions

CleverBus extends Apache Camel in many ways, look at main points:

-   **[asynchronous message processing model](../Architecture/Asynchronous-messages)**
    -   **parent-child concept** that allows to divide main message into more child messages and process them separately
    -   **obsolete messages** checking when messages impact same data
    -   **funnel** component is for filtering concurrent messages at specific integration point. This filtering ensures that only one message at one moment will be processed, even in guaranteed order (optional choice).
    -   **[guaranteed message processing order](../Architecture/Asynchronous-messages/Guaranteed-message-processing-order)**
    -   algorithm is **[configurable](Configuration)**
    -   synchronous response that input request is saved in queue and **asynchronous confirmation** with processing result
    -   **monitoring** of processing in [Admin GUI](Admin-GUI) and via [JMX](../Running-CleverBus/Monitoring/JMX)
-   [**throttling**](../Architecture/CleverBus-components/throttling) - functionality that checks count of input requests to integration platform and if this count exceeds defined limit then new requests are restricted
-   extended [error handling](Error-handling) with many new [Camel events](../Architecture/Camel-events)
-   **[tracking external systems communication](Request-response-tracking)** - storing requests and responses
-   **[web administration console](Admin-GUI)**
    -   searching in asynchronous messages
    -   message details with requests/responses overview
    -   searching in logs
    -   manual cancel of next message processing 
    -   restart failed messages
    -   error codes catalogue
    -   exposed WSDLs overview
    -   endpoints overview

-   **extended logging** allows to group logs together of one request/message or process
-   **direct call console** allows to send custom requests to external system
-   **stopping mode** is useful function for correctly CleverBus shutdown
-   [**extensions**](../CleverBus-extensions/How-to-implement-new-extensions) allow to encapsulate new CleverBus functionality
-   **support for cluster** (in progress at this moment)
