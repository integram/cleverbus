# Configuration

## Asynchronous algorithm configuration

CleverBus contains two configuration files:

-   ***applicationCore.cfg*** - file placed in *core* module, contains basic configuration parameters
-   ***application.cfg*** - file placed in *web-admin* module, contains web and project specific parameters

Common recommendations for configuration:<ul><li>set email addresses of administrators (<i>admin.mail</i>) because CleverBus sends notifications to these addresses, for example when</li><ul><li>message processing failed</li><li>if message waits for response from external system for more then defined interval</li></ul><li>set username and password to access (web) services - parameters <i>ws.user</i> a <i>ws.password</i> in <i>application.cfg</i></li><li>path to log folder (<i>log.folder.path</i> in <i>application.cfg</i>)</li></ul>

## applicationCore.cfg

| Parameter                  | Default value | Description |
| -------------------------- | ------------- | ----------- |
| *asynch.countPartlyFailsBeforeFailed* | 3    | Count of unsuccessful tries of message processing before message will be marked as completely *FAILED*<p>Time interval between tries is defined by *asynch.partlyFailedInterval*.</p> |
| *asynch.repairRepeatTime*             | 300  | Max. interval in seconds how long can be message being processed, in other words how long can be in *PROCESSING* state.<p>If message is still in processing then repair process (*org.cleverbus.core.common.asynch.repair.RepairProcessingMsgRoute*) will be started and the message state will be changed to *PARTLY_FAILED*.</p><p>This parameter is not only for messages themselves but also for external calls and confirmations.</p> |
| *asynch.concurrentConsumers*          | 30   | Input asynchronous messages are waiting for processing in the priority queue. This parameter determines how many concurrent consumers (=threads) can take message from the queue and start processing. In other words this parameter detemines how many concurrent messages can be processed.<p>For more informacetion see http://camel.apache.org/seda.html, parameter *concurrentConsumers*</p> |
| *asynch.partlyFailedRepeatTime*       | 60   | How often to run process (*org.cleverbus.core.common.asynch.queue.PartlyFailedMessagesPoolRoute*) for pooling *PARTLY_FAILED* messages (in seconds).<p>This parameter is relevant to *asynch.partlyFailedInterval*.</p> |
| *asynch.partlyFailedInterval*         | 60   | Interval (in seconds) between two tries of *PARTLY_FAILED* messages. When this interval expires then can be message be processed again.<p>This parameter is relevant to *asynch.partlyFailedRepeatTime*.</p> |
| *asynch.confirmation.failedLimit*     | 3    | Maximum count of confirmation fails when will finish further processing of confirmation, confirmation fails. |
| *asynch.partlyFailedRepeatTime*       | 60   | How often to run process (*org.cleverbus.core.common.asynch.confirm.ConfirmationsPoolRoute*) for pooling failed confirmations (in seconds)<p>This parameter is relevant to *asynch.confirmation.interval*</p>. |
| *asynch.confirmation.interval*        | 60   | Interval (in seconds) between two tries of failed confirmations.<p>This parameter is relevant to *asynch.confirmation.repeatTime*</p>. |
| *asynch.waitForResponse.timeout*      | 3600 | **Parameter was removed in version 0.4 because new similar functionality was added - [Alerts](Alerts).** |
| *asynch.externalCall.skipUriPattern*  |      | Regular expression that defines URIs which will be ignored by [extcall](../Architecture/CleverBus-components/extcall) component. Useful when you want to skip communication with an external system. |
| *asynch.postponedInterval*            | 5    | Interval (in seconds) after that can be postponed message processed again. |
| *asynch.postponedIntervalWhenFailed*  | 300  | Interval (in seconds) after that postponed messages will fail. See Guaranteed message processing order functionality for more details.<p>Since version 0.4</p> |

### Miscellaneous configuration

| Parameter                      | Default value | Description |
| ------------------------------ | ------------- | ----------- |
| *mail.admin*                   |  | Administrator email(s)<p>If more emails, then separated them with semicolon if empty then email won't be sent.</p> |
| *mail.from*                    | CleverBus integration platform | Email address FROM for sending emails |
| *mail.smtp.server*             | localhost | SMTP server for sending emails |
| *dir.temp*                     |  | Directory for storing temporary files, related to [DefaultFileRepository](https://hudson.clance.local/hudson/view/CleverBus/job/CleverBus%20release/javadoc/com/cleverlance/cleverbus/core/common/file/DefaultFileRepository.html) |
| *dir.fileRepository*           |  | File repository directory where files will be stored, related to [DefaultFileRepository](https://hudson.clance.local/hudson/view/CleverBus/job/CleverBus%20release/javadoc/com/cleverlance/cleverbus/core/common/file/DefaultFileRepository.html) |
| *contextCall.localhostUri*     | http://localhost:8080 | URI of this localhost application, including port number. Related to external call in [Admin GUI](Admin-GUI) |
| *disable.throttling*           | false | True for disabling throttling at all. See [throttling](../Architecture/CleverBus-components/throttling) component. |
| *endpoints.includePattern*     | ^(spring-ws\|servlet).*$ | Pattern for filtering endpoints URI - only whose URIs will match specified pattern will be returned, related to [endpoints overview](Admin-GUI). |
| *requestSaving.enable*         | false | True for enabling saving requests/responses for filtered endpoints URI. |
| *requestSaving.endpointFilter* | ^(spring-ws\|servlet).*$ | Pattern for filtering endpoints URI which requests/response should be saved. |
| *alerts.repeatTime*            | 300 | How often to run checking of alerts (in seconds)<p>This parameter is enabled from 0.4 version.</p> |

## application.cfg

| Parameter             | Default value | Description |
| --------------------- | ------------- | ----------- |
| *db.driver*           | org.h2.Driver | Driver class name |
| *db.url*              | jdbc:h2:mem:cleverBusDB | Database URL |
| *db.username*         | sa | Database username |
| *db.password*         |  | Database password |
| *ws.user*             | wsUser | Username for accessing web services (Spring security configuration in *rootSecurity.xml*). |
| *ws.password*         | wsPassword | Password for accessing web services (Spring security configuration in *rootSecurity.xml*). |
| *web.user*            | webUser | Username for accessing web admin (Spring security configuration in *rootSecurity.xml*). |
| *web.password*        | webPassword | Password for accessing web admin (Spring security configuration in *rootSecurity.xml*). |
| *monitoring.user*     | monUser | Username for accessing [JavaMelody](../Running-CleverBus/Monitoring/JavaMelody) tool (Spring security configuration in *rootSecurity.xml*). |
| *monitoring.password* | monPassword | Password for accessing [JavaMelody](../Running-CleverBus/Monitoring/JavaMelody) tool (Spring security configuration in *rootSecurity.xml*). |
| *log.folder.path*     | ${log.folder}, value is from [Maven](../Architecture/Maven-and-Spring) profile | Path to folder with application logs, used in logs searching in [admin GUI](Admin-GUI). |
| *log.file.pattern*    | <pre><code>(^.\*\\\\.log$\|^.\*\\\\.log\\\\.2\\\\d{3}-(0[1-9]\|1[0-9])-[0\|1\|2\|3]\\\\d?_\\\\d\*\\\\.gz$)</code></pre> | Defines format of log file names (include filter) which will be taking into logs searching in [admin GUI.](Admin-GUI)<p>Since version 0.4</p> |

## Configuration hiearchy

There is the following configuration files hierarchy that determines processing order:

```
applicationCore.cfg -> application.cfg -> application0.cfg
```

In other words parameters defined in last file (*application0.cfg*) have higher priority then those defined in *applicationCore.cfg* (where there are default values).

## Throttling

Throttling functionality is implemented by [throttling component](../Architecture/CleverBus-components/throttling) where you can find configuration description.

## Alerts

[Alerts](Alerts) have own configuration files.

## Restrict Spring bean inicialization

Since version 0.2

If there are many Spring beans with route definitions then startup time for loading ESB application can be quite long. This functionality enables to **include** only those Spring beans which should be initialized or **exclude** those Spring beans which we don't want to initialize.

It can be handy during development because it's possible via system properties to restrict set of Spring beans (=Camel routes) which will be initialized.

There are two handy classes for these purposes: *org.cleverbus.common.spring.SystemIncludeRegexPatternTypeFilter* and *org.cleverbus.common.spring.SystemExcludeRegexPatternTypeFilter*

Both type filters are handled by system or environment property. *springIncludePattern* for *SystemIncludeRegexPatternTypeFilter* class, *springExcludePattern* for *SystemExcludeRegexPatternTypeFilter* class. System property has higher priority.

Example of system property definition for exclude filter (excludes all Spring beans under *org.cleverbusu.modules* package and sub-packages):

```
-DspringExcludePattern=org\.cleverbus\.modules\..*
```

Example of Spring configuration with custom type filters:

``` xml
<context:component-scan base-package="org.cleverbus.core, org.cleverbus.admin.routes, org.cleverbus.modules" use-default-filters="false">
	<context:include-filter type="custom" expression="org.cleverbus.common.spring.SystemIncludeRegexPatternTypeFilter"/>
	<context:exclude-filter type="custom" expression="org.cleverbus.common.spring.SystemExcludeRegexPatternTypeFilter"/>
</context:component-scan>
```

Include filters are applied after exclude filters.

## Configuration checking

Since version 0.4

Configuration checker (*org.cleverbus.core.conf.ConfigurationChecker*) enables to check selected configuration parameters during application start:

-   *directCall.localhostUri* - checks URI availability (must be explicitly enabled)
-   *endpoints.includePattern, requestSaving.endpointFilter* - checks regular expressions 

You can implement your own checking functionality by *org.cleverbus.core.conf.ConfCheck* (since version 1.1).

