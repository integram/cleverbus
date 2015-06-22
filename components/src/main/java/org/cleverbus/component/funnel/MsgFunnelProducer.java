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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.entity.Message;
import org.cleverbus.common.log.Log;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
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

        Collection<String> funnelValues = getFunnelValues(msg, endpoint);

        if (CollectionUtils.isEmpty(funnelValues)) {
            Log.debug("Message " + msg.toHumanString() + " doesn't have funnel value => won't be filtered");
        } else {
            // set ID to message
            String funnelCompId = getFunnelCompId(msg, exchange, endpoint);

            //is equal funnel values on endpoint and on message
            boolean equalFunnelValues = CollectionUtils.isEqualCollection(funnelValues, msg.getFunnelValues());
            //set funnel value if not equals and funnel id
            if (!equalFunnelValues || !funnelCompId.equals(msg.getFunnelComponentId())) {
                //add funnel value into message if is not equal and save it
                if (!equalFunnelValues) {
                    endpoint.getMessageService().setFunnelComponentIdAndValue(msg, funnelCompId, funnelValues);
                } else {
                    //funnel component id is not same, than we save it
                    endpoint.getMessageService().setFunnelComponentId(msg, funnelCompId);
                }
            }

            if (endpoint.isGuaranteedOrder()) {
                // By default classic funnel works with running messages (PROCESSING, WAITING, WAITING_FOR_RES) only
                // and if it's necessary to guarantee processing order then also PARTLY_FAILED, POSTPONED [and FAILED]
                // messages should be involved
                List<Message> messages = endpoint.getMessageService().getMessagesForGuaranteedOrderForFunnel(
                        funnelValues, endpoint.getIdleInterval(), endpoint.isExcludeFailedState(),
                        funnelCompId);

                if (messages.size() == 1) {
                    Log.debug("There is only one processing message with funnel values: " + funnelValues
                            + " => no filtering");

                // is specified message first one for processing?
                } else if (messages.get(0).equals(msg)) {
                    Log.debug("Processing message (msg_id = {}, funnel values = '{}') is the first one"
                            + " => no filtering", msg.getMsgId(), funnelValues);

                } else {
                    Log.debug("There is at least one processing message with funnel values '{}'"
                            + " before current message (msg_id = {}); message {} will be postponed.",
                            funnelValues, msg.getMsgId(), msg.toHumanString());

                    postponeMessage(exchange, msg, endpoint);
                }

            } else {
                // is there processing message with same funnel value?
                int count = endpoint.getMessageService().getCountProcessingMessagesForFunnel(funnelValues,
                        endpoint.getIdleInterval(), funnelCompId);

                if (count > 1) {
                    // note: one processing message is this message
                    Log.debug("There are more processing messages with funnel values '" + funnelValues
                            + "', message " + msg.toHumanString() + " will be postponed.");

                    postponeMessage(exchange, msg, endpoint);

                } else {
                    Log.debug("There is only one processing message with funnel values: " + funnelValues
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
     * Return funnelValues from message (if message has funnel values) and endpoint (endpoint can have only one
     * funnel value).
     *
     * @param msg      message
     * @param endpoint funnel endpoint
     * @return funnel values
     */
    private Collection<String> getFunnelValues(Message msg, MsgFunnelEndpoint endpoint) {
        Assert.notNull(msg, "msg must not be null");
        Assert.notNull(endpoint, "endpoint must not be null");

        List<String> result = new ArrayList<String>();

        //get funnel values from message
        result.addAll(msg.getFunnelValues());

        String endpointFunnelValue = endpoint.getFunnelValue();
        //funnel value from endpoint
        if (!StringUtils.isBlank(endpointFunnelValue)) {
            result.add(endpointFunnelValue);
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
