# Monitoring

## Description

During maintenance and monitoring each integration platform is very important to rely mainly on application logs, where there are a substantial and detailed information about running platform - CleverBus.

## Application log

The main task for application log is hold the information from running of ESB which are required to solve any problems.

Sample of one logging line in following format:

*%d{ISO8601} [${serverId}, ${MACHINE}, %thread, %X{REQUEST\_URI}, %X{REQUEST\_ID}, %X{SESSION\_ID}, %X{SOURCE\_SYSTEM}, %X{CORRELATION\_ID}, %X{PROCESS\_ID}] %-5level %logger{36} - %msg%n*

One log line contains standard parameters which can configure in file *logback.xml* (see [documentation for Logback lib](http://logback.qos.ch) that is used as logging platform. In addition the following custom logging parameters are used:

-   LOG\_SESSION\_ID: unique HTTP session identifier. Thanks to this parameter can be grouped records from one user (system). This parameter is not filled if process is asynchronous and postponed (processed in the future).
-   REQUEST\_URI: URI of incoming request to integration platform. This parameter is not filled if process is asynchronous and postponed (processed in the future).
-   REQUEST\_ID: unique identifier of single request. Thanks to this parameter can grouped records from one request (HTTP request). This parameter is not filled if process is asynchronous and postponed (processed in the future).
-   SOURCE\_SYSTEM: identifier of source system which send inbound request
-   CORRELATION\_ID: unique identifier of single asynchronous message. Thanks to this parameter it can search all correlated records in log file for specific message. Note: this parameter is used by Log Viewer in admin gui to show correlated records.
-   PROCESS\_ID: optional identifier of process which identifies records correlated to one business process e.g. creating new user which is composed by two subprocess: create new user and activate it.

### Example

*2011-04-05 08:07:07,964 [server1, http-8080-Processor16, /sc-web-console/sc/sn/mock\_demo\_service/on/Demo, 127.0.1.1:4c8ed99e:12f244556e1:-7fdd, 6B6\*\*\*\*\*C6C2, deih3u36bh] INFO  c.c.s.f.w.f.screen.ScreenFormPanel - the message for key 'widget16\_label\_key' was not found: form='demo\_form1', locale='cs'*

*2013-08-07 02:01:34,542 [MACHINE\_IS\_UNDEFINED, Camel (camelContext) thread \#25 - \<seda://asynch_message_route\>, , 192.168.198.100:-7d3dda4f:1405477688f:-60c4, , a2e7cf84-f4fe-e211-b400-005056bc0011] DEBUG*

| Detection  | Severity | Recommended action | Description of problem |
| ---------- | -------- | ------------------ | ---------------------- |
| Search string "] ERROR " in log files /srv/cleverbus/logs/j2ee/ (or where files are stored) | ERROR | Forward to resolve by support team | Fault status in the ESB application |
| Search string "] FATAL " in log files /srv/cleverbus/logs/j2ee/ (or where files are stored) | FATAL | Forward to resolve by support team | Critical status in the ESB application |
| Search string "was changed to FAILED" | WARN | Forward to resolving to administrator, who should look at where the problem is and why the message could not be processed. | Asynchronous message is in FAILED status (some business or technical error occurs) |


## Database

In database CleverBus stores records to support asynchronous processing of messages. If integration platform is processing synchronous request than information about that can be search in log file, no in database. Exception is logging and monitoring all request/responses sent by integration platform into external systems.

From monitoring of database of view is recommended to observe followings:

-   statuses of asynchronous *messages* in table message, column *state* (status) stavy - either directly by select into database or observe reports, see Admin GUI - [Admin - Message report service](http://esbsit.cbss.cleverlance.com/esb/web/admin/messageReport).
-   when any information in message is changed, column *last\_update\_timestamp* is updated by actual time
-   statuses of external calls in table *external\_call*, column *state*.

| Detection | Severity | Recommended action | Description of problem |
| --------- | -------- | ------------------ | ---------------------- |
| Message in FAILED state                           | WARN | Using the application log determine the causes of errors. | Asynchronous message ended up in the final status FAILED, which means that during the processing occurred an error (business or technical error). |
| Message is long time in the state WAITING_FOR_RES | WARN | Using the application log to make sure that we sent a message to the external system properly and correctly and, if necessary, we even received an acknowledgment of receipt of our report from external system. | Asynchronous message is waiting to response from external system. <p>If there are messages in this status for long time (normal response is received during a few of milliseconds) it means that the problem is on external system side.</p> |
| Message is long time in the state PROCESSING      | WARN | Checks whether repair mechanisms works as expected and why messages are still in PROCESSING status. | Asynchronous message is in PROCESSING state. <p>If some error occurs during processing a message and the message remains in this status, CleverBus has corrigible mechanism which changes after configurable  time (default value is 300s) status of message to PARTLY_FAILED without changes of failed count (failed_count).</p> |
| External call is in FAILED_END status             | WARN | Using the application log determine the causes of errors. | Only external call - confirmations can be in this status and it means the problem where CleverBus could not confirm processing of asynchronous message. <p>Confirmation of result of processing asynchronous messages is joined only with some external systems. If the confirmation fails a workflow process is stopped because external system can have some dependencies to next processing of it.</p> |
| Message is long time in the state POSTPONED       | WARN |  	Check configurable parameters asynch.postponedInterval, which stands for after how long becomes inactive message in processing state (PROCESSING, WAITING, WAITING_FOR_RES) marked as "not processing". | Messages in status POSTPONED are processed with the same mechanism as messages in PARTLY_FAILED status. Only one difference is that postponed messages are processed preferably. <p>Note: if the behavior of this mechanism would be unexpected (around PARTLY_FAILED messages processing) so it will be the same unexpected behaviour during POSTPONED processing.</p> |


