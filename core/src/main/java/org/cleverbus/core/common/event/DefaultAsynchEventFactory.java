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

import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.event.CompletedMsgAsynchEvent;
import org.cleverbus.api.event.FailedMsgAsynchEvent;
import org.cleverbus.api.event.PartlyFailedMsgAsynchEvent;
import org.cleverbus.api.event.PostponedMsgAsynchEvent;
import org.cleverbus.api.event.ProcessingMsgAsynchEvent;
import org.cleverbus.api.event.WaitingMsgAsynchEvent;
import org.cleverbus.common.log.Log;

import org.apache.camel.Exchange;


/**
 * Default implementation of {@link AsynchEventFactory} interface.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class DefaultAsynchEventFactory implements AsynchEventFactory {

    @Override
    public CompletedMsgAsynchEvent createCompletedMsgEvent(Exchange exchange) {
        CompletedMsgAsynchEvent event = new CompletedMsgAsynchEvent(exchange, getMsgFromExchange(exchange));

        Log.debug("New event was created: {}", event);

        return event;
    }

    @Override
    public PartlyFailedMsgAsynchEvent createPartlyFailedMsgEvent(Exchange exchange) {
        PartlyFailedMsgAsynchEvent event = new PartlyFailedMsgAsynchEvent(exchange, getMsgFromExchange(exchange));

        Log.debug("New event was created: {}", event);

        return event;
    }

    @Override
    public FailedMsgAsynchEvent createFailedMsgEvent(Exchange exchange) {
        FailedMsgAsynchEvent event = new FailedMsgAsynchEvent(exchange, getMsgFromExchange(exchange));

        Log.debug("New event was created: {}", event);

        return event;
    }

    @Override
    public WaitingMsgAsynchEvent createWaitingMsgEvent(Exchange exchange) {
        WaitingMsgAsynchEvent event = new WaitingMsgAsynchEvent(exchange, getMsgFromExchange(exchange));

        Log.debug("New event was created: {}", event);

        return event;
    }

    @Override
    public ProcessingMsgAsynchEvent createProcessingMsgEvent(Exchange exchange) {
        ProcessingMsgAsynchEvent event = new ProcessingMsgAsynchEvent(exchange, getMsgFromExchange(exchange));

        Log.debug("New event was created: {}", event);

        return event;
    }

    @Override
    public PostponedMsgAsynchEvent createPostponedMsgEvent(Exchange exchange) {
        PostponedMsgAsynchEvent event = new PostponedMsgAsynchEvent(exchange, getMsgFromExchange(exchange));

        Log.debug("New event was created: {}", event);

        return event;
    }

    private Message getMsgFromExchange(Exchange exchange) {
        return (Message) exchange.getIn().getHeader(AsynchConstants.MSG_HEADER);
    }
}
