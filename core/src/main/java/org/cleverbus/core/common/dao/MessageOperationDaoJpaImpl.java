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

package org.cleverbus.core.common.dao;

import java.util.Arrays;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.cleverbus.api.entity.ExternalCall;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.common.log.Log;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


/**
 * JPA implementation of {@link MessageOperationDao} interface.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class MessageOperationDaoJpaImpl implements MessageOperationDao {

    @PersistenceContext(unitName = DbConst.UNIT_NAME)
    private EntityManager em;

    @Override
    public boolean setPartlyFailedState(Message msg) {
        Assert.notNull(msg, "the msg must not be null");

        // change state to PARTLY_FAILED
        String jSql = "UPDATE " + Message.class.getName()
                + " SET state = ?1, lastUpdateTimestamp = ?2"
                + " WHERE msgId = ?3 AND state IN (?4)";

        Query q = em.createQuery(jSql);
        q.setParameter(1, MsgStateEnum.PARTLY_FAILED);
        q.setParameter(2, new Date());
        q.setParameter(3, msg.getMsgId());
        q.setParameter(4, Arrays.asList(MsgStateEnum.CANCEL, MsgStateEnum.FAILED));

        return q.executeUpdate() > 0;
    }

    @Override
    public void removeExtCalls(Message msg, boolean totalRestart) {
        Assert.notNull(msg, "the msg must not be null");

        String jSql = "DELETE "
                + "FROM " + ExternalCall.class.getName() + " c "
                + "WHERE c.msgId = ?1 ";

        if (!totalRestart) {
            // delete only confirmations
            jSql += "AND c.operationName = '" + ExternalCall.CONFIRM_OPERATION + "'";
        }

        Query q = em.createQuery(jSql);
        q.setParameter (1, msg.getMsgId());
        int updatedCount = q.executeUpdate();

        Log.debug(updatedCount + " external calls were deleted for message with msgID=" + msg.getMsgId());
    }

    @Override
    public boolean setCancelState(Message msg) {
        Assert.notNull(msg, "the msg must not be null");

        String jSql = "UPDATE " + Message.class.getName()
                + " SET state = ?1 "
                + " WHERE msgId = ?2 AND state IN (?3)";

        Query q = em.createQuery(jSql);
        q.setParameter(1, MsgStateEnum.CANCEL);
        q.setParameter(2, msg.getMsgId());
        q.setParameter(3, Arrays.asList(MsgStateEnum.NEW, MsgStateEnum.PARTLY_FAILED, MsgStateEnum.POSTPONED));

        return q.executeUpdate() > 0;
    }
}
