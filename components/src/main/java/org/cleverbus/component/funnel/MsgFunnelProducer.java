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

import java.util.List;

import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.entity.Message;
import org.cleverbus.common.log.Log;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


/**
 * Producer for {@link MsgFunnelComponent msg-funnel} component.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class MsgFunnelProducer extends DefaultProducer {

    private static final String FUNNEL_COMP_PREFIX = "funnel_";

    /**
     * Creates new producer.
     *
     * @param endpoint the endpoint
     */
    public MsgFunnelProducer(MsgFunnelEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Message msg = exchange.getIn().getHeader(AsynchConstants.MSG_HEADER, Message.class);

        Assert.notNull(msg, "message must be defined, msg-funnel component is for asynchronous messages only");

        if (StringUtils.isEmpty(msg.getFunnelValue())) {
            Log.debug("Message " + msg.toHumanString() + " doesn't have funnel value => won't be filtered");
        } else {
            MsgFunnelEndpoint endpoint = (MsgFunnelEndpoint) getEndpoint();

            // set ID to message
            String funnelCompId = getFunnelCompId(exchange, endpoint);
            if (!funnelCompId.equals(msg.getFunnelComponentId())) {
                endpoint.getMessageService().setFunnelComponentId(msg, funnelCompId);
            }

            if (endpoint.isGuaranteedOrder()) {
                // By default classic funnel works with running messages (PROCESSING, WAITING, WAITING_FOR_RES) only
                // and if it's necessary to guarantee processing order then also PARTLY_FAILED, POSTPONED [and FAILED]
                // messages should be involved
                List<Message> messages = endpoint.getMessageService().getMessagesForGuaranteedOrderForFunnel(
                        msg.getFunnelValue(), endpoint.getIdleInterval(), endpoint.isExcludeFailedState(),
                        funnelCompId);

                if (messages.size() == 1) {
                    Log.debug("There is only one processing message with funnel value: " + msg.getFunnelValue()
                            + " => no filtering");

                // is specified message first one for processing?
                } else if (messages.get(0).equals(msg)) {
                    Log.debug("Processing message (msg_id = {}, funnel value = '{}') is the first one"
                            + " => no filtering", msg.getMsgId(), msg.getFunnelValue());

                } else {
                    Log.debug("There is at least one processing message with funnel value '{}'"
                            + " before current message (msg_id = {}); message {} will be postponed.",
                            msg.getFunnelValue(), msg.getMsgId(), msg.toHumanString());

                    postponeMessage(exchange, msg, endpoint);
                }

            } else {
                // is there processing message with same funnel value?
                int count = endpoint.getMessageService().getCountProcessingMessagesForFunnel(msg.getFunnelValue(),
                        endpoint.getIdleInterval(), funnelCompId);

                if (count > 1) {
                    // note: one processing message is this message
                    Log.debug("There are more processing messages with funnel value '" + msg.getFunnelValue()
                            + "', message " + msg.toHumanString() + " will be postponed.");

                    postponeMessage(exchange, msg, endpoint);

                } else {
                    Log.debug("There is only one processing message with funnel value: " + msg.getFunnelValue()
                            + " => no filtering");
                }
            }
        }
    }

    private String getFunnelCompId(Exchange exchange, MsgFunnelEndpoint endpoint) {
        if (endpoint.getId() != null) {
            // use custom ID
            return endpoint.getId();
        } else {
            // create ID from route ID
            return FUNNEL_COMP_PREFIX + exchange.getFromRouteId();
        }
    }

    private void postponeMessage(Exchange exchange, Message msg, MsgFunnelEndpoint endpoint) {
        // change state
        endpoint.getMessageService().setStatePostponed(msg);

        // generates event
        endpoint.getAsyncEventNotifier().notifyMsgPostponed(exchange);

        // set StopProcessor - mark the exchange to stop continue routing
        exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
    }
}
