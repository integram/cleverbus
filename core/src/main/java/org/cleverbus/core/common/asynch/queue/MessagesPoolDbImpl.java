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

import javax.annotation.Nullable;

import org.cleverbus.api.entity.Message;
import org.cleverbus.api.exception.LockFailureException;
import org.cleverbus.common.log.Log;
import org.cleverbus.core.common.dao.MessageDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;


/**
 * DB implementation of {@link MessagesPool} interface.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class MessagesPoolDbImpl implements MessagesPool {

    @Autowired
    private MessageDao messageDao;

    private TransactionTemplate transactionTemplate;

    /**
     * Interval (in seconds) between two tries of partly failed messages.
     */
    @Value("${asynch.partlyFailedInterval}")
    private int partlyFailedInterval;

    /**
     * Interval (in seconds) after that can be postponed message processed again.
     */
    @Value("${asynch.postponedInterval}")
    private int postponedInterval;

    @Required
    public void setTransactionManager(JpaTransactionManager transactionManager) {
        Assert.notNull(transactionManager, "the transactionManager must not be null");

        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Nullable
    public Message getNextMessage() {
        // is there next message for processing?

        // firstly try postponed messages
        Message msg = findPostponedMessage();

        // then partly failed messages
        if (msg == null) {
            msg = findPartlyFailedMessage();
        }

        if (msg == null) {
            Log.debug("No POSTPONED and PARTLY_FAILED message found for re-processing.");
            return null;
        }

        // try to get lock for the message
        boolean isLock = lockMessage(msg);
        if (!isLock) {
            throw new LockFailureException("Failed to lock message for re-processing: " + msg.toHumanString());
        }

        return msg;
    }

    @Nullable
    private Message findPostponedMessage() {
        return transactionTemplate.execute(new TransactionCallback<Message>() {
            @Override
            public Message doInTransaction(final TransactionStatus transactionStatus) {
                return messageDao.findPostponedMessage(postponedInterval);
            }
        });
    }

    @Nullable
    private Message findPartlyFailedMessage() {
        return transactionTemplate.execute(new TransactionCallback<Message>() {
            @Override
            public Message doInTransaction(final TransactionStatus transactionStatus) {
                return messageDao.findPartlyFailedMessage(partlyFailedInterval);
            }
        });
    }

    private boolean lockMessage(final Message msg) {
        Assert.notNull(msg, "the msg must not be null");

        boolean isLock;
        try {
            isLock = transactionTemplate.execute(new TransactionCallback<Boolean>() {
                @Override
                public Boolean doInTransaction(final TransactionStatus transactionStatus) {
                    return messageDao.updateMessageForLock(msg);
                }
            });
        } catch (DataAccessException ex) {
            isLock = false;
        }

        if (isLock) {
            Log.debug("Successfully locked message for re-processing: {}", msg.toHumanString());
            return true;
        } else {
            Log.debug("Failed to lock message for re-processing: {}", msg.getMsgId());
            return false;
        }
    }
}
