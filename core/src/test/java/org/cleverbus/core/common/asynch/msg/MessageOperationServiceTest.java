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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.concurrent.CountDownLatch;

import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.core.AbstractCoreDbTest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;


/**
 * Test suite for {@link MessageOperationService}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@ContextConfiguration(locations = {"classpath:/org/cleverbus/core/camel/common/asynch/msg/test-context.xml"})
public class MessageOperationServiceTest extends AbstractCoreDbTest {

    @Autowired
    private MessageOperationService operationService;

    @Test
    public void testRestartForFailedMessage() {
        // prepare message in FAILED state
        Message[] messages = createAndSaveMessages(1, new MessageProcessor() {
            @Override
            public void process(Message message) {
                message.setState(MsgStateEnum.FAILED);
            }
        });

        // cancel message
        operationService.restartMessage(messages[0].getMsgId(), false);

        // verify
        Message msgDB = em.find(Message.class, messages[0].getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.PARTLY_FAILED));
    }

    @Test
    public void testRestartForFailedMessage_multiThreaded() throws Exception {
        // prepare message in FAILED state
        final Message[] messages = createAndSaveMessages(1, new MessageProcessor() {
            @Override
            public void process(Message message) {
                message.setState(MsgStateEnum.FAILED);
            }
        });

        // prepare threads
        int threads = 2;
        final CountDownLatch latch = new CountDownLatch(threads);
        Runnable task = new Runnable() {

            @Override
            public void run() {
                try {
                    // cancel message
                    operationService.restartMessage(messages[0].getMsgId(), false);
                } finally {
                    latch.countDown();
                }
            }
        };

        // start processing and waits for result
        for (int i = 0; i < threads; i++) {
            new Thread(task).start();
        }

        latch.await();

        // verify
        Message msgDB = em.find(Message.class, messages[0].getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.PARTLY_FAILED));
    }

    @Test
    public void testRestartForCancelMessage() {
        // prepare message in FAILED state
        Message[] messages = createAndSaveMessages(1, new MessageProcessor() {
            @Override
            public void process(Message message) {
                message.setState(MsgStateEnum.CANCEL);
            }
        });

        // cancel message
        operationService.restartMessage(messages[0].getMsgId(), false);

        // verify
        Message msgDB = em.find(Message.class, messages[0].getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.PARTLY_FAILED));
    }

    @Test
    public void testCancelForNEW() {
        // prepare message in NEW state
        Message[] messages = createAndSaveMessages(1, new MessageProcessor() {
            @Override
            public void process(Message message) {
                message.setState(MsgStateEnum.NEW);
            }
        });

        // cancel message
        operationService.cancelMessage(messages[0].getMsgId());

        // verify
        Message msgDB = em.find(Message.class, messages[0].getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.CANCEL));
    }

    @Test
    public void testCancelForPARTLY_FAILED() {
        // prepare message in PARTLY_FAILED state
        Message[] messages = createAndSaveMessages(1, new MessageProcessor() {
            @Override
            public void process(Message message) {
                message.setState(MsgStateEnum.PARTLY_FAILED);
            }
        });

        // cancel message
        operationService.cancelMessage(messages[0].getMsgId());

        // verify
        Message msgDB = em.find(Message.class, messages[0].getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.CANCEL));
    }

    @Test
    public void testCancelForPOSTPONED() {
        // prepare message in POSTPONED state
        Message[] messages = createAndSaveMessages(1, new MessageProcessor() {
            @Override
            public void process(Message message) {
                message.setState(MsgStateEnum.POSTPONED);
            }
        });

        // cancel message
        operationService.cancelMessage(messages[0].getMsgId());

        // verify
        Message msgDB = em.find(Message.class, messages[0].getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.CANCEL));
    }

    @Test(expected = IllegalStateException.class)
    public void testWrongCancel() {
        // prepare message in NEW state
        Message[] messages = createAndSaveMessages(1, new MessageProcessor() {
            @Override
            public void process(Message message) {
                message.setState(MsgStateEnum.PROCESSING);
            }
        });

        // cancel message
        operationService.cancelMessage(messages[0].getMsgId());
    }
}
