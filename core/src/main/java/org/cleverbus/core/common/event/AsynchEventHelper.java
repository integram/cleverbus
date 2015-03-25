/*
 * Copyright (C) 2015
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cleverbus.core.common.event;

import java.util.EventObject;
import java.util.List;

import org.cleverbus.api.event.AbstractAsynchEvent;
import org.cleverbus.common.log.Log;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.StatefulService;
import org.apache.camel.spi.EventNotifier;
import org.apache.camel.spi.ManagementStrategy;
import org.springframework.util.Assert;


/**
 * Helper class for easily sending event notifications in a single line of code.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public final class AsynchEventHelper {

    private static final AsynchEventFactory factory = new DefaultAsynchEventFactory();

    private AsynchEventHelper() {
    }

    public static void notifyMsgCompleted(Exchange exchange) {
        notifyMsg(exchange, new EventNotifierCallback() {
            @Override
            public boolean ignore(EventNotifier notifier) {
                return notifier.isIgnoreExchangeEvents() || notifier.isIgnoreExchangeCompletedEvent();
            }

            @Override
            public AbstractAsynchEvent createEvent(Exchange exchange) {
                return factory.createCompletedMsgEvent(exchange);
            }
        });
    }

    public static void notifyMsgPartlyFailed(Exchange exchange) {
        notifyMsg(exchange, new EventNotifierCallback() {
            @Override
            public boolean ignore(EventNotifier notifier) {
                return notifier.isIgnoreExchangeEvents() || notifier.isIgnoreExchangeFailedEvents();
            }

            @Override
            public AbstractAsynchEvent createEvent(Exchange exchange) {
                return factory.createPartlyFailedMsgEvent(exchange);
            }
        });
    }

    public static void notifyMsgFailed(Exchange exchange) {
        notifyMsg(exchange, new EventNotifierCallback() {
            @Override
            public boolean ignore(EventNotifier notifier) {
                return notifier.isIgnoreExchangeEvents() || notifier.isIgnoreExchangeFailedEvents();
            }

            @Override
            public AbstractAsynchEvent createEvent(Exchange exchange) {
                return factory.createFailedMsgEvent(exchange);
            }
        });
    }

    public static void notifyMsgWaiting(Exchange exchange) {
        notifyMsg(exchange, new EventNotifierCallback() {
            @Override
            public boolean ignore(EventNotifier notifier) {
                return notifier.isIgnoreExchangeEvents();
            }

            @Override
            public AbstractAsynchEvent createEvent(Exchange exchange) {
                return factory.createWaitingMsgEvent(exchange);
            }
        });
    }

    public static void notifyMsgProcessing(Exchange exchange) {
        notifyMsg(exchange, new EventNotifierCallback() {
            @Override
            public boolean ignore(EventNotifier notifier) {
                return notifier.isIgnoreExchangeEvents();
            }

            @Override
            public AbstractAsynchEvent createEvent(Exchange exchange) {
                return factory.createProcessingMsgEvent(exchange);
            }
        });
    }

    public static void notifyMsgPostponed(Exchange exchange) {
        notifyMsg(exchange, new EventNotifierCallback() {
            @Override
            public boolean ignore(EventNotifier notifier) {
                return notifier.isIgnoreExchangeEvents();
            }

            @Override
            public AbstractAsynchEvent createEvent(Exchange exchange) {
                return factory.createPostponedMsgEvent(exchange);
            }
        });
    }

    /**
     * Notifies event notifiers.
     *
     * @param exchange the exchange
     * @param callback the callback contract for creating new events.
     */
    public static void notifyMsg(Exchange exchange, EventNotifierCallback callback) {
        Assert.notNull(exchange, "the exchange must not be null");

        if (exchange.getProperty(Exchange.NOTIFY_EVENT, false, Boolean.class)) {
            // do not generate events for an notify event
            return;
        }

        CamelContext context = exchange.getContext();

        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return;
        }

        List<EventNotifier> notifiers = management.getEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return;
        }

        for (EventNotifier notifier : notifiers) {
            if (callback.ignore(notifier)) {
                continue;
            }

            // we want to have new event instance for all notifiers
            EventObject event = callback.createEvent(exchange);

            doNotifyEvent(notifier, event);
        }
    }

    private static void doNotifyEvent(EventNotifier notifier, EventObject event) {
        // only notify if notifier is started
        boolean started = true;
        if (notifier instanceof StatefulService) {
            started = ((StatefulService) notifier).isStarted();
        }

        if (!started) {
            Log.debug("Ignoring notifying event {}. The EventNotifier has not been started yet: {}", event, notifier);
            return;
        }

        if (!notifier.isEnabled(event)) {
            Log.debug("Notification of event is disabled: {}", event);
            return;
        }

        try {
            Log.debug("Event {} arrived to notifier {}", event, notifier.getClass().getName());

            notifier.notify(event);
        } catch (Throwable e) {
            Log.warn("Error notifying event " + event + ". This exception will be ignored. ", e);
        }
    }
}
