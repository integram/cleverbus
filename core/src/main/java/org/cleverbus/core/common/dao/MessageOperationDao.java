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

import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;


/**
 * DAO interface for operations with messages.
 *
 * @author <a href="mailto:viliam.elischer@cleverlance.com">Viliam Elischer</a>
 */
public interface MessageOperationDao {

    /**
     * Restarts message = change the message state from FAILED -> PARTLY_FAILED.
     * <p/>
     * Only messages in FAILED state can be changed, otherwise exception will be thrown.
     *
     * @param msg the message
     * @return {@code true} if state was successfully set, otherwise {@code false}
     */
    boolean setPartlyFailedState(Message msg);

    /**
     * Removes external calls for specific message.
     *
     * @param msg the message
     * @param totalRestart {@code true} if all external call should be deleted (=message will be processed from scratch)
     *                                 or {@code false} if only external calls for confirmations should be deleted
     */
    void removeExtCalls(Message msg, boolean totalRestart);

    /**
     * Cancels next message processing = change the message state to {@link MsgStateEnum#CANCEL}.
     *
     * @param msg the message
     * @return {@code true} if state was successfully set, otherwise {@code false}
     */
    boolean setCancelState(Message msg);
}
