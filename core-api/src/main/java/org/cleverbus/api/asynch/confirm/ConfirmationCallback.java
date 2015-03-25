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

package org.cleverbus.api.asynch.confirm;

import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;

import org.apache.camel.Header;


/**
 * Callback contract that confirms successfully processed message to the source system.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface ConfirmationCallback {

    public static final String BEAN = "confirmationCallback";

    /**
     * Confirms that the message was fully processed.
     * The message must have a final {@link Message#getState() state},
     * that is either {@link MsgStateEnum#OK} or {@link MsgStateEnum#FAILED}.
     *
     * @param msg the message
     */
    void confirm(@Header(AsynchConstants.MSG_HEADER) Message msg);

}
