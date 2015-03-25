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

import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.common.log.Log;
import org.cleverbus.core.common.dao.MessageDao;
import org.cleverbus.core.common.dao.MessageOperationDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;


/**
 * MessageOperationService implements methods to interact with the integrations messages
 * and if necessary to manipulate their properties, such as state, timestamps, etc.
 *
 * @author <a href="mailto:viliam.elischer@cleverlance.com">Viliam Elischer</a>
 */
public class MessageOperationServiceImpl implements MessageOperationService {

    @Autowired
    private MessageOperationDao msgOpDao;

    @Autowired
    private MessageDao msgDao;

    /**
     * Method restarts message enables the option for the user to re-set/restart a message in a FAILED/CANCEL state.
     * Steps:
     * <ol>
     * <li>Determine, if msg with ID exists in <b>message</b> table</li>
     * <li>Update the state of the msg with ID to 'PARTLY_FAILED' in the <b>message</b> table</li>
     * <li>Try to delete, the record based on msg ID from <b>external_call</b> table</li>
     * </ol>
     * When the totalRestart is true the condition operation_name = 'confirmation' is NOT USED. And all records form
     * external_call with the given MessageID are removed.
     *
     * @param messageId    ID of the message that the user wants to restart
     * @param totalRestart Variable acts as SQL WHERE condition toggle between 2 statements
     */
    @Transactional
    @Override
    public synchronized void restartMessage(long messageId, boolean totalRestart) {
        try {
            Message msg = msgDao.getMessage(messageId);

            // check FAILED/CANCEL state
            if (msg.getState() != MsgStateEnum.FAILED && msg.getState() != MsgStateEnum.CANCEL) {
                throw new IllegalStateException("message " + msg.toHumanString() + " is not in FAILED or CANCEL state.");
            }

            if (!msgOpDao.setPartlyFailedState(msg)) {
                throw new IllegalStateException("Message (id = " + messageId + ") hasn't been restarted.");
            }

            msgOpDao.removeExtCalls(msg, totalRestart);

            Log.debug("Message (id = " + messageId + ", totalRestart = " + totalRestart + ") was successfully restarted ...");
        } catch (DataAccessException dx) {
            throw new RuntimeException("An error occurred during message restarting", dx);
        }
    }

    @Transactional
    @Override
    public synchronized void cancelMessage(long messageId) {
        try {
            Message msg = msgDao.getMessage(messageId);

            // check message state
            if (msg.getState() != MsgStateEnum.NEW && msg.getState() != MsgStateEnum.PARTLY_FAILED
                    && msg.getState() != MsgStateEnum.POSTPONED) {
                throw new IllegalStateException("Message (id = " + messageId + ") can be canceled only "
                        + "when its NEW or PARTLY_FAILED or POSTPONED.");
            }

            if (!msgOpDao.setCancelState(msg)) {
                throw new IllegalStateException("Message (id = " + messageId + ") hasn't been changed to CANCEL state.");
            }

            Log.debug("Message " + msg.toHumanString() + " was successfully canceled ...");
        } catch (DataAccessException dx) {
            throw new RuntimeException("An error occurred during message cancelling", dx);
        }
    }
}
