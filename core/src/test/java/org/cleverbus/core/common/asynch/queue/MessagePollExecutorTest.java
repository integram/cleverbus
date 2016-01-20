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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Nullable;
import javax.persistence.Query;

import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.core.AbstractCoreDbTest;
import org.cleverbus.core.common.asynch.AsynchMessageRoute;
import org.cleverbus.test.ActiveRoutes;
import org.cleverbus.test.ExternalSystemTestEnum;
import org.cleverbus.test.ServiceTestEnum;

import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.commons.lang.time.DateUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * Test suite for {@link MessagePollExecutor}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@ActiveRoutes(classes = AsynchMessageRoute.class)
public class MessagePollExecutorTest extends AbstractCoreDbTest {

    private static final String FUNNEL_VALUE = "774724557";

    private static final String FUNNEL_VALUE_TWO = "FUNNEL_VALUE_TWO";

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    @Autowired
    private MessagesPool messagesPool;

    @Autowired
    private MessagePollExecutor messagePollExecutor;

    @Before
    public void prepareData() {
        // set failed limit
        setPrivateField(messagesPool, "partlyFailedInterval", 0);
        setPrivateField(messagesPool, "postponedInterval", 0);
        setPrivateField(messagePollExecutor, "targetURI", "mock:test");
        setPrivateField(messagePollExecutor, "postponedIntervalWhenFailed", 0);

        // firstly commit messages to DB (we can commit because we have embedded DB for tests only)
        TransactionTemplate txTemplate = new TransactionTemplate(jpaTransactionManager);
        txTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                insertNewMessage("1234_4567", MsgStateEnum.POSTPONED, false);
                insertNewMessage("1234_4567_8", MsgStateEnum.PARTLY_FAILED, false, FUNNEL_VALUE);
                insertNewMessage("1234_4567_9", MsgStateEnum.PARTLY_FAILED, false, "somethingElse");
            }
        });
    }

    private Message insertNewMessage(String correlationId, MsgStateEnum state,
            boolean guaranteedOrder, @Nullable String... funnelValues) {
        Date currDate = new Date();

        Message msg = new Message();
        msg.setState(state);

        msg.setMsgTimestamp(currDate);
        msg.setReceiveTimestamp(currDate);
        msg.setLastUpdateTimestamp(currDate);
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setCorrelationId(correlationId);

        msg.setService(ServiceTestEnum.CUSTOMER);
        msg.setOperationName("setCustomer");
        msg.setObjectId(null);
        if (funnelValues != null && funnelValues.length != 0) {
            msg.setFunnelValues(Arrays.asList(funnelValues));
        }
        msg.setGuaranteedOrder(guaranteedOrder);

        msg.setPayload("xml");

        em.persist(msg);
        em.flush();

        return msg;
    }

    @Test
    public void testGetNextMessage_moreThreads() throws InterruptedException {
        // prepare threads
        int threads = 5;
        final CountDownLatch latch = new CountDownLatch(threads);
        Runnable task = new Runnable() {

            @Override
            public void run() {
                try {
                    messagePollExecutor.run();
                } finally {
                    latch.countDown();
                }
            }
        };

        mock.expectedMessageCount(3);

        // start processing and waits for result
        for (int i = 0; i < threads; i++) {
            new Thread(task).start();
        }

        latch.await();

        mock.assertIsSatisfied();

        // verify messages
        Message msg = findMessage("1234_4567");
        assertThat(msg, notNullValue());
        assertThat(msg.getState(), is(MsgStateEnum.PROCESSING));

        msg = findMessage("1234_4567_8");
        assertThat(msg, notNullValue());
        assertThat(msg.getState(), is(MsgStateEnum.PROCESSING));

        msg = findMessage("1234_4567_9");
        assertThat(msg, notNullValue());
        assertThat(msg.getState(), is(MsgStateEnum.PROCESSING));
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private Message findMessage(String correlationId) {
        String jSql = "SELECT m "
                + "FROM Message m "
                + "WHERE m.correlationId = ?1";

        Query q = em.createQuery(jSql);
        q.setParameter (1, correlationId);
        q.setMaxResults(1);
        List<Message> messages = (List<Message>) q.getResultList();

        if (messages.isEmpty()) {
            return null;
        } else {
            return messages.get(0);
        }
    }

    @Test
    @Transactional
    public void testGuaranteedOrder_postponedMessage() throws InterruptedException {
        // prepare message that should be postponed
        insertNewMessage("id1", MsgStateEnum.PROCESSING, true, FUNNEL_VALUE);
        Message msg = insertNewMessage("id2", MsgStateEnum.PROCESSING, true, FUNNEL_VALUE);
        msg.setReceiveTimestamp(DateUtils.addSeconds(new Date(), 10));  //postponedIntervalWhenFailed=0

        // action
        messagePollExecutor.startMessageProcessing(msg);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
    }

    @Test
    @Transactional
    public void testGuaranteedOrder_processing_onlyOneMessage() throws InterruptedException {
        // prepare only one message => continue processing
        Message msg = insertNewMessage("id2", MsgStateEnum.PROCESSING, true, FUNNEL_VALUE);

        // action
        messagePollExecutor.startMessageProcessing(msg);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));
    }

    @Test
    @Transactional
    public void testGuaranteedOrder_processing_msgIsNotGuaranteed() throws InterruptedException {
        // prepare messages that is not guaranteed => continue processing
        insertNewMessage("id1", MsgStateEnum.PROCESSING, false, FUNNEL_VALUE);
        Message msg = insertNewMessage("id2", MsgStateEnum.PROCESSING, true, FUNNEL_VALUE);

        // action
        messagePollExecutor.startMessageProcessing(msg);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));
    }

    @Test
    @Transactional
    public void testGuaranteedOrder_processing_differentFunnelValues() throws InterruptedException {
        // prepare messages that is not guaranteed => continue processing
        insertNewMessage("id1", MsgStateEnum.PROCESSING, true, "someValue");
        Message msg = insertNewMessage("id2", MsgStateEnum.PROCESSING, true, FUNNEL_VALUE);

        // action
        messagePollExecutor.startMessageProcessing(msg);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));
    }

    @Test
    @Transactional
    public void testGuaranteedOrder_failedMessage() throws InterruptedException {
        // prepare message that should fail
        insertNewMessage("id1", MsgStateEnum.PROCESSING, true, FUNNEL_VALUE);
        Message msg = insertNewMessage("id2", MsgStateEnum.PROCESSING, true, FUNNEL_VALUE);
        msg.setReceiveTimestamp(DateUtils.addSeconds(new Date(), -10)); //postponedIntervalWhenFailed=0

        // action
        messagePollExecutor.startMessageProcessing(msg);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.FAILED));
    }

    @Test
    @Transactional
    public void testGuaranteedOrder_multiFunnelMessage() throws InterruptedException {
        // prepare message that should be postponed
        insertNewMessage("id1", MsgStateEnum.PROCESSING, true, FUNNEL_VALUE, FUNNEL_VALUE_TWO);

        //first message
        Message msg = insertNewMessage("id2", MsgStateEnum.PROCESSING, true, FUNNEL_VALUE);
        msg.setReceiveTimestamp(DateUtils.addSeconds(new Date(), 10));  //postponedIntervalWhenFailed=0

        // action
        messagePollExecutor.startMessageProcessing(msg);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));

        //second message
        msg = insertNewMessage("id3", MsgStateEnum.PROCESSING, true, FUNNEL_VALUE, FUNNEL_VALUE_TWO);
        msg.setReceiveTimestamp(DateUtils.addSeconds(new Date(), 100));

        // action
        messagePollExecutor.startMessageProcessing(msg);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));

        //third message
        msg = insertNewMessage("id4", MsgStateEnum.PROCESSING, true, FUNNEL_VALUE_TWO);
        msg.setReceiveTimestamp(DateUtils.addSeconds(new Date(), 200));

        // action
        messagePollExecutor.startMessageProcessing(msg);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));

        //fifth message
        msg = insertNewMessage("id5", MsgStateEnum.PROCESSING, true, "someOtherValue", "someOtherValueTwo",
                "someValue");
        msg.setReceiveTimestamp(DateUtils.addSeconds(new Date(), 300));

        // action
        messagePollExecutor.startMessageProcessing(msg);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));
    }
}
