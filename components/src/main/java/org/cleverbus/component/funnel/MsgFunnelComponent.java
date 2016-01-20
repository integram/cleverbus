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

package org.cleverbus.component.funnel;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.cleverbus.api.entity.Message;
import org.cleverbus.spi.AsyncEventNotifier;
import org.cleverbus.spi.msg.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Map;


/**
 * Apache Camel component "msg-funnel" for filtering processing messages.
 * This component ensures that there is only one processing message with same
 * {@link Message#getFunnelValue() funnel value} or with funnel value defined in uri.
 *
 * <p/>
 * Syntax: {@code msg-funnel:default[?options]} where options are
 * <ul>
 *     <li>idleInterval - Interval (in seconds) that determines how long can be message processing.
 *     <li>guaranteedOrder - if funnel component should guaranteed order of processing messages
 *     <li>excludeFailedState - if FAILED state should be involved in guaranteed order
 *     <li>id - funnel component identifier
 *     <li>funnelValue - funnel value
 * </ul>
 *
 * <p/>
 * Valid for asynchronous messages only.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class MsgFunnelComponent extends DefaultComponent {

    @Autowired
    private MessageService messageService;

    @Autowired
    private AsyncEventNotifier asyncEventNotifier;

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        MsgFunnelEndpoint endpoint = new MsgFunnelEndpoint(uri, this);

        return endpoint;
    }

    MessageService getMessageService() {
        return messageService;
    }

    AsyncEventNotifier getAsyncEventNotifier() {
        return asyncEventNotifier;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        // checking references
        Assert.notNull(messageService, "messageService mustn't be null");
        Assert.notNull(asyncEventNotifier, "asyncEventNotifier mustn't be null");
    }
}
