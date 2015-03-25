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

package org.cleverbus.core.common.asynch.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.core.AbstractCoreDbTest;
import org.cleverbus.core.common.asynch.queue.MessagesPool;
import org.cleverbus.core.common.asynch.queue.MessagesPoolDbImpl;
import org.cleverbus.test.ExternalSystemTestEnum;
import org.cleverbus.test.ServiceTestEnum;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


/**
 * Test suite for {@link MessagesPoolDbImpl}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@Transactional
public class PartlyFailedMessagesPoolDbTest extends AbstractCoreDbTest {

    @Autowired
    private MessagesPool messagesPool;

    @Before
    public void prepareData() {
        // set failed limit
        setPrivateField(messagesPool, "partlyFailedInterval", 0);
    }

    @Test
    public void testGetNextMessage() {
        // add one message and try to lock it
        insertNewMessage("1234_4567", MsgStateEnum.PARTLY_FAILED);

        Message nextMsg = messagesPool.getNextMessage();
        assertThat(nextMsg, notNullValue());
        assertThat(nextMsg.getState(), is(MsgStateEnum.PROCESSING));
        assertThat(nextMsg.getStartProcessTimestamp(), notNullValue());
        assertThat(nextMsg.getLastUpdateTimestamp(), notNullValue());

        // try again
        nextMsg = messagesPool.getNextMessage();
        assertThat(nextMsg, nullValue());
    }

    @Test
    public void testGetNextMessage_noNextMessage() {
        Message nextMsg = messagesPool.getNextMessage();
        assertThat(nextMsg, nullValue());
    }

    private void insertNewMessage(String correlationId, MsgStateEnum state) {
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

        msg.setPayload("xml");

        em.persist(msg);
        em.flush();
    }
}
