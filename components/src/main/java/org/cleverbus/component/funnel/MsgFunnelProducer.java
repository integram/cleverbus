/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cleverbus.component.funnel;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.entity.Message;
import org.cleverbus.common.log.Log;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import java.util.List;


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

        MsgFunnelEndpoint endpoint = (MsgFunnelEndpoint) getEndpoint();

        String funnelValue = getFunnelValue(msg, endpoint);

        if (StringUtils.isBlank(funnelValue)) {
            Log.debug("Message " + msg.toHumanString() + " doesn't have funnel value => won't be filtered");
        } else {
            // set ID to message
            String funnelCompId = getFunnelCompId(msg, exchange, endpoint);

            //set funnel value if not equals and funnel id
            if (!ObjectUtils.equals(funnelValue, msg.getFunnelValue())
                    || !funnelCompId.equals(msg.getFunnelComponentId())){
                endpoint.getMessageService().setFunnelComponentIdAndValue(msg, funnelCompId, funnelValue);
            }

            if (endpoint.isGuaranteedOrder()) {
                // By default classic funnel works with running messages (PROCESSING, WAITING, WAITING_FOR_RES) only
                // and if it's necessary to guarantee processing order then also PARTLY_FAILED, POSTPONED [and FAILED]
                // messages should be involved
                List<Message> messages = endpoint.getMessageService().getMessagesForGuaranteedOrderForFunnel(
                        funnelValue, endpoint.getIdleInterval(), endpoint.isExcludeFailedState(),
                        funnelCompId);

                if (messages.size() == 1) {
                    Log.debug("There is only one processing message with funnel value: " + funnelValue
                            + " => no filtering");

                // is specified message first one for processing?
                } else if (messages.get(0).equals(msg)) {
                    Log.debug("Processing message (msg_id = {}, funnel value = '{}') is the first one"
                            + " => no filtering", msg.getMsgId(), funnelValue);

                } else {
                    Log.debug("There is at least one processing message with funnel value '{}'"
                            + " before current message (msg_id = {}); message {} will be postponed.",
                            funnelValue, msg.getMsgId(), msg.toHumanString());

                    postponeMessage(exchange, msg, endpoint);
                }

            } else {
                // is there processing message with same funnel value?
                int count = endpoint.getMessageService().getCountProcessingMessagesForFunnel(funnelValue,
                        endpoint.getIdleInterval(), funnelCompId);

                if (count > 1) {
                    // note: one processing message is this message
                    Log.debug("There are more processing messages with funnel value '" + funnelValue
                            + "', message " + msg.toHumanString() + " will be postponed.");

                    postponeMessage(exchange, msg, endpoint);

                } else {
                    Log.debug("There is only one processing message with funnel value: " + funnelValue
                            + " => no filtering");
                }
            }
        }
    }

    private String getFunnelCompId(Message msg, Exchange exchange, MsgFunnelEndpoint endpoint) {
        Assert.notNull(msg, "msg must not be null");
        Assert.notNull(exchange, "exchange must not be null");
        Assert.notNull(endpoint, "endpoint must not be null");

        //get funnel component id from uri
        String result = endpoint.getId();
        //if is not on endpoint then we get value from message
        if (StringUtils.isBlank(result)){
            result = msg.getFunnelComponentId();

            //is not on message we get value from routeId
            if (StringUtils.isBlank(result)){
                return FUNNEL_COMP_PREFIX + exchange.getFromRouteId();
            }
        }
        return result;
    }

    /**
     * Return funnelValue from message or endpoint.
     * <p>
     * If endpoint has funnel value ({@link MsgFunnelEndpoint#getFunnelValue()}), then we used it, or we
     * used funnel value on {@link Message} ({@link Message#getFunnelValue()}).
     * </p>
     *
     * @param msg      message
     * @param endpoint funnel endpoint
     * @return funnel value, {@code NULL} - no funnel value found
     */
    @Nullable
    private String getFunnelValue(Message msg, MsgFunnelEndpoint endpoint) {
        Assert.notNull(msg, "msg must not be null");
        Assert.notNull(endpoint, "endpoint must not be null");

        String result = endpoint.getFunnelValue();
        //if is blank, than we used from Message
        if (StringUtils.isBlank(result)) {
            result = msg.getFunnelValue();
        }
        return result;
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
