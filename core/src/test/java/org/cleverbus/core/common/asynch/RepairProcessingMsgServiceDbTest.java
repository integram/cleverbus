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

package org.cleverbus.core.common.asynch;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.core.AbstractCoreDbTest;
import org.cleverbus.core.common.asynch.repair.RepairMessageService;
import org.cleverbus.core.common.asynch.repair.RepairMessageServiceDbImpl;
import org.cleverbus.core.common.dao.MessageDao;
import org.cleverbus.test.ExternalSystemTestEnum;
import org.cleverbus.test.ServiceTestEnum;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;


/**
 * Test suite for {@link RepairMessageServiceDbImpl}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@Transactional
public class RepairProcessingMsgServiceDbTest extends AbstractCoreDbTest {

    @Autowired
    private RepairMessageService repairMsgService;

    @Autowired
    private MessageDao messageDao;

    private Message msg;

    @Before
    public void prepareData() {
        Date currDate = DateUtils.addDays(new Date(), -1);

        msg = new Message();
        msg.setState(MsgStateEnum.NEW);
        msg.setMsgTimestamp(currDate);
        msg.setReceiveTimestamp(currDate);
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setCorrelationId("123-456");

        msg.setService(ServiceTestEnum.CUSTOMER);
        msg.setOperationName("setCustomer");
        msg.setPayload("xml");
        msg.setLastUpdateTimestamp(currDate);
    }

    @Test
    public void testRepairProcessingMessages() {
        msg.setState(MsgStateEnum.PROCESSING);
        msg.setStartProcessTimestamp(msg.getMsgTimestamp());
        messageDao.insert(msg);

        em.flush();

        int msgCount = JdbcTestUtils.countRowsInTable(getJdbcTemplate(), "message");
        assertThat(msgCount, is(1));

        // call repairing
        repairMsgService.repairProcessingMessages();

        em.flush();

        // verify results
        msgCount = JdbcTestUtils.countRowsInTable(getJdbcTemplate(), "message");
        assertThat(msgCount, is(1));

        getJdbcTemplate().query("select * from message", new RowMapper<Message>() {
            @Override
            public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
                // verify row values
                assertThat(rs.getLong("msg_id"), is(1L));
                assertThat((int)rs.getShort("failed_count"), is(1));
                assertThat(rs.getTimestamp("last_update_timestamp"), notNullValue());
                assertThat(MsgStateEnum.valueOf(rs.getString("state")), is(MsgStateEnum.PARTLY_FAILED));

                return new Message();
            }
        });
    }
}
