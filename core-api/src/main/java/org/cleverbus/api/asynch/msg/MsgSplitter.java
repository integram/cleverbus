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

package org.cleverbus.api.asynch.msg;

import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;

import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.apache.camel.Header;


/**
 * Contract for splitting original (parent) message into smaller messages which will be processed
 * separately.
 * When all child messages are successfully processed (state is {@link MsgStateEnum#OK OK}
 * then also parent message is successfully processed.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface MsgSplitter {

    /**
     * Splits specified message into smaller messages.
     *
     * @param parentMsg the parent message
     * @param body the body
     */
    @Handler
    void splitMessage(@Header(AsynchConstants.MSG_HEADER) Message parentMsg, @Body Object body);
}
