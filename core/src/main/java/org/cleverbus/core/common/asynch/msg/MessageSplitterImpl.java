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

package org.cleverbus.core.common.asynch.msg;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.asynch.msg.ChildMessage;
import org.cleverbus.api.asynch.msg.MessageSplitterCallback;
import org.cleverbus.api.asynch.msg.MsgSplitter;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.common.log.Log;
import org.cleverbus.core.common.asynch.AsynchMessageRoute;
import org.cleverbus.spi.msg.MessageService;

import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.model.ModelCamelContext;
import org.springframework.util.Assert;


/**
 * Implementation of {@link MsgSplitter} interface.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public final class MessageSplitterImpl implements MsgSplitter {

    private final ModelCamelContext camelCtx;

    private final MessageService messageService;

    private final ExecutorService executor;

    private final MessageSplitterCallback splitterCallback;

    /**
     * Creates new message splitter.
     *
     * @param messageService the message service
     * @param camelCtx the Camel context
     * @param splitterCallback the callback for getting split messages
     */
    public MessageSplitterImpl(MessageService messageService, ModelCamelContext camelCtx,
            MessageSplitterCallback splitterCallback) {

        Assert.notNull(messageService, "the messageService must not be null");
        Assert.notNull(camelCtx, "the camelCtx must not be null");
        Assert.notNull(splitterCallback, "the splitterCallback must not be null");

        this.camelCtx = camelCtx;
        this.messageService = messageService;
        this.splitterCallback = splitterCallback;
        this.executor = camelCtx.getExecutorServiceManager().newThreadPool(this, "MessageSplitter", 1, 3);
    }

    @Override
    @Handler
    public final void splitMessage(@Header(AsynchConstants.MSG_HEADER) Message parentMsg, @Body Object body) {
        Assert.notNull(parentMsg, "the parentMsg must not be null");
        Assert.isTrue(parentMsg.getState() == MsgStateEnum.PROCESSING && parentMsg.getFailedCount() == 0,
                "only new message can be split to child messages");

        // get child messages
        List<ChildMessage> childMessages = splitterCallback.getChildMessages(parentMsg, body);

        Log.debug("Count of child messages: " + childMessages.size());

        // create messages
        final List<Message> messages = new ArrayList<Message>(childMessages.size());
        for (int i = 0; i < childMessages.size(); i++) {
            ChildMessage childMsg = childMessages.get(i);

            Message message = ChildMessage.createMessage(childMsg);

            // correlation ID is unique - add order to distinguish it
            message.setCorrelationId(parentMsg.getCorrelationId() + "_" + i);
            messages.add(message);
        }

        // mark original message as parent message
        parentMsg.setParentMessage(true);

        // save all messages at once
        messageService.insertMessages(messages);

        final ProducerTemplate msgProducer = camelCtx.createProducerTemplate();

        try {
            // process messages separately one by one
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    for (Message msg : messages) {
                        Log.debug("Message " + msg.toHumanString() + " will be processed ...");

                        // send to process (wait for reply and then process next child message); it's new exchange
                        msgProducer.requestBody(AsynchMessageRoute.URI_SYNC_MSG, msg);

                        Log.debug("Message " + msg.toHumanString() + " was successfully processed.");
                    }
                }
            });
        } finally {
            if (msgProducer != null) {
                try {
                    msgProducer.stop();
                } catch (Exception ex) {
                    Log.error("error occurred during stopping producerTemplate", ex);
                }
            }
        }
    }

}
