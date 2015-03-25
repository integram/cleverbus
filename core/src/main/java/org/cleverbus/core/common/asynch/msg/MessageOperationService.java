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

import org.cleverbus.api.entity.MsgStateEnum;


/**
 * Defines contract for manipulation with existing messages.
 *
 * @author <a href="mailto:viliam.elischer@cleverlance.com">Viliam Elischer</a>
 */
public interface MessageOperationService {

    /**
     * Method designed to change the state of an asynchronous message (moved from {@link MsgStateEnum#FAILED}
     * to {@link MsgStateEnum#PARTLY_FAILED}.
     *
     * @param messageId    Identifier of the message used in the restart process (must be in FAILED state)
     * @param totalRestart {@code true} if message should start from scratch again or {@code false}
     *                                 if message should continue when it failed
     */
    void restartMessage(long messageId, boolean totalRestart);

    /**
     * Cancels next message processing, sets {@link MsgStateEnum#CANCEL} state.
     * Only {@link MsgStateEnum#NEW}, {@link MsgStateEnum#PARTLY_FAILED} and {@link MsgStateEnum#POSTPONED}
     * messages can be canceled.
     *
     * @param messageId the message ID
     */
    void cancelMessage(long messageId);
}
