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

package org.cleverbus.core.common.asynch.queue;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.commons.lang.time.DateUtils;
import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.exception.IntegrationException;
import org.cleverbus.api.exception.InternalErrorEnum;
import org.cleverbus.api.exception.LockFailureException;
import org.cleverbus.common.log.Log;
import org.cleverbus.core.common.asynch.LogContextHelper;
import org.cleverbus.core.common.event.AsynchEventHelper;
import org.cleverbus.spi.msg.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;


/**
 * Reads messages from DB and sends them for next processing.
 * Execution will stop when there is no further message for processing.
 * <p/>
 * This executor is invoked by {@link JobStarterForMessagePooling}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class MessagePollExecutor implements Runnable {

    private static final int LOCK_FAILURE_LIMIT = 5;

    @Autowired
    private MessagesPool messagesPool;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private MessageService messageService;

    /**
     * Interval (in seconds) after that postponed messages will fail.
     */
    @Value("${asynch.postponedIntervalWhenFailed}")
    private int postponedIntervalWhenFailed;

    // note: this is because of setting different target URI for tests
    private String targetURI = AsynchConstants.URI_ASYNC_MSG;

    @Override
    public void run() {
        Log.debug("Message pooling starts ...");

        // is there message for processing?
        Message msg = null;
        int lockFailureCount = 0;
        while (true) {
            try {
                msg = messagesPool.getNextMessage();

                if (msg != null) {
                    LogContextHelper.setLogContextParams(msg, null);

                    startMessageProcessing(msg);
                } else {
                    //there is no new message for processing
                    //  => finish this executor and try it again after some time
                    break;
                }
            } catch (LockFailureException ex) {
                // try again to acquire next message with lock
                lockFailureCount++;

                if (lockFailureCount > LOCK_FAILURE_LIMIT) {
                    Log.warn("Probably problem with locking messages - count of lock failures exceeds limit ("
                            + LOCK_FAILURE_LIMIT + ").");
                    break;
                }
            } catch (Exception ex) {
                Log.error("Error occurred during getting message "
                        + (msg != null ? msg.toHumanString() : ""), ex);
            }
        }

        Log.debug("Message pooling finished.");
    }

    void startMessageProcessing(Message msg) {
        Assert.notNull(msg, "the msg must not be null");

        if (isMsgInGuaranteedOrder(msg)) {
            // sends message for next processing
            producerTemplate.sendBodyAndHeader(targetURI, ExchangePattern.InOnly, msg,
                    AsynchConstants.MSG_QUEUE_INSERT_HEADER, System.currentTimeMillis());

        } else {
            Date failedDate = DateUtils.addSeconds(new Date(), -postponedIntervalWhenFailed);

            final Message paramMsg = msg;

            if (msg.getReceiveTimestamp().before(failedDate)) {

                // change to failed message => redirect to "FAILED" route
                producerTemplate.send(AsynchConstants.URI_ERROR_FATAL, ExchangePattern.InOnly,
                        new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                IntegrationException ex = new IntegrationException(InternalErrorEnum.E121,
                                        "Message " + paramMsg.toHumanString() + " exceeded interval for starting "
                                                + "processing => changed to FAILED state");

                                exchange.setProperty(Exchange.EXCEPTION_CAUGHT, ex);

                                exchange.getIn().setHeader(AsynchConstants.MSG_HEADER, paramMsg);
                            }
                        });

            } else {
                // postpone message
                messageService.setStatePostponed(msg);

                // create Exchange for event only
                ExchangeBuilder exchangeBuilder = ExchangeBuilder.anExchange(producerTemplate.getCamelContext());
                Exchange exchange = exchangeBuilder.build();

                exchange.getIn().setHeader(AsynchConstants.MSG_HEADER, paramMsg);

                AsynchEventHelper.notifyMsgPostponed(exchange);
            }
        }
    }

    /**
     * Checks if specified message should be processed in guaranteed order and if yes
     * then checks if the message is in the right order.
     *
     * @param msg the asynchronous message
     * @return {@code true} if message's order is ok otherwise {@code false}
     */
    private boolean isMsgInGuaranteedOrder(Message msg) {
        if (!msg.isGuaranteedOrder()) {
            // no guaranteed order => continue
            return true;
        } else {
            // guaranteed order => is the message in the right order?
            List<Message> messages = messageService.getMessagesForGuaranteedOrderForRoute(msg.getFunnelValues(),
                    msg.isExcludeFailedState());

            if (messages.size() == 1) {
                Log.debug("There is only one processing message with funnel values: " + msg.getFunnelValues()
                        + " => continue");

                return true;

            // is specified message first one for processing?
            } else if (messages.get(0).equals(msg)) {
                Log.debug("Processing message (msg_id = {}, funnel values = '{}') is the first one"
                        + " => continue", msg.getMsgId(), msg.getFunnelValues());

                return true;

            } else {
                Log.debug("There is at least one processing message with funnel values '{}'"
                        + " before current message (msg_id = {}); message {} will be postponed.",
                        msg.getFunnelValues(), msg.getMsgId(), msg.toHumanString());

                return false;
            }
        }
    }
}
