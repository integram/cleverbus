# Request/response tracking

Since version 0.4

## Description

Request/response tracking functionality allows to save internal communication between routes or communication with external systems into database. 

Functionality is disabled by default - see *requestSaving.enable* parameter to enable it. There is another parameter *requestSaving.endpointFilter* that defines pattern for filtering endpoints URI which requests/response should be saved. See [configuration](Configuration) for more details.

Look at <a href='../Architecture/Data-model'>data model</a> for more details about <i>request</i> and <i>response</i> tables.

## Implementation and limitations

Implementation is in *sc-core* module, package *org.cleverbus.core.reqres.*

Default implementation uses Camel events that has one possible disadvantage - it's necessary to join request and response together (= two Camel events) and if exchange is changed from sending request until response receive (e.g. using *wireTap*) then it's not possible to join it. But this limitation is mainly for internal communication, there is no problem with saving request/response to/from external system.

Requests/responses are saved into database, *RequestResponseService* defines contract. *RequestResponseServiceDefaultImpl* is default implementation that saves them directly to DB in synchronous manner.

