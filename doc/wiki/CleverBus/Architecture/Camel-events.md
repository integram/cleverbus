# Camel events

## Description

CleverBus uses Apache Camel events concept:

-   all Camel events extend *java.util.EventObject* resp. *org.apache.camel.management.event.AbstractExchangeEvent*. There are many events already defined in Camel, e.g. *ExchangeCompletedEvent*, *ExchangeFailedEvent*, *ExchangeSendingEvent* etc. These events are generated automatically by Camel.
-   if you want to catch these events then you have to implement *EventNotifier *interface and this notifier add to Camel context. It's possible to use method *addEventNotifier *in class *AbstractBasicRoute *for our routes.

Usage example: <http://camel.apache.org/eventnotifier-to-log-details-about-all-sent-exchanges.html>

There are new events defined in CleverBus which can be caught anywhere by *EventNotifier:*

-   *ProcessingMsgAsynchEvent*
-   *WaitingMsgAsynchEvent*
-   *PartlyFailedMsgAsynchEvent*
-   *FailedMsgAsynchEvent*
-   *CompletedMsgAsynchEvent*

There was a discussion about whether to use Spring or Camel events. Finally we use Camel events because we use native support in Camel with lot of predefined events and Spring framework is only one implementation of bean registry and therefore we didn't want to be dependant on specific implementation.

## Usage

There is *@EventNotifier* annotation that marks classes which implement *org.apache.camel.spi.EventNotifier *interface for listening to Camel events.

*EventNotifier* implementations have special behaviour in Camel, most of them are Camel services with well-defined [lifecycle](http://camel.apache.org/lifecycle.html). Use parent class *EventNotifierBase* to meet these requirements.

If event notifier extends <i>EventNotifierBase</i> class then it's not necessary to initialize them in Camel context, it's automatically by default.

### Custom event

``` java
/**
 * Event for failed VF call. This event occurs when asynch. message with calling VF failed.
 */
public class FailedFromVfEvent extends FailedMsgAsynchEvent {
    private static final long serialVersionUID = 8376176024372186281L;
 
    /**
     * Creates new event.
     *
     * @param exchange the exchange
     * @param message  the message
     */
    public FailedFromVfEvent(Exchange exchange, Message message) {
        super(exchange, message);
    }
}
```

 

### Event throwing, method *notifyFailedFromVf*

``` java
/**
 * Helper class for creating events.
 */
public final class TutanEventHelper {
    private TutanEventHelper() {
    }
 
    /**
     * Throws {@link FailedFromVfEvent} and notifies all registered event notifiers.
     *
     * @param exchange the exchange
     */
    public static void notifyFailedFromVf(Exchange exchange) {
        AsynchEventHelper.notifyMsg(exchange, new EventNotifierCallback() {
            @Override
            public boolean ignore(EventNotifier notifier) {
                return notifier.isIgnoreExchangeEvents() || notifier.isIgnoreExchangeCompletedEvent();
            }
            @Override
            public AbstractAsynchEvent createEvent(Exchange exchange) {
                return new FailedFromVfEvent(exchange, getMsgFromExchange(exchange));
            }
        });
    }
 
    private static Message getMsgFromExchange(Exchange exchange) {
        return (Message) exchange.getIn().getHeader(AsynchConstants.MSG_HEADER);
    }
}
```

 

### Notifier implementation

``` java
/**
 * Event notifier for listening to {@link FailedFromVfEvent} events.
 */
@EventNotifier
public class VfTopUpFailedEventNotifier extends EventNotifierBase<FailedFromVfEvent> {
 
    @Override
    public void doNotify(FailedFromVfEvent event) throws Exception {
        Message message = event.getExchange().getIn().getHeader(AsynchConstants.MSG_HEADER, Message.class);
        if ("createTopUp".equals(message.getOperationName())) {
            getProducerTemplate().send(CreateTopUpRoute.URI_CREATE_TOP_UP_VF_FAILED, event.getExchange());
        }
    }
}
```
