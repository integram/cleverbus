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

package org.cleverbus.api.event;

import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;

import org.apache.camel.Exchange;
import org.springframework.util.Assert;


/**
 * Event for successfully completed message, i.e. the message is in {@link MsgStateEnum#OK} state.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class CompletedMsgAsynchEvent extends AbstractAsynchEvent {

    /**
     * Creates new event.
     *
     * @param exchange the exchange
     * @param message  the message
     */
    public CompletedMsgAsynchEvent(Exchange exchange, Message message) {
        super(exchange, message);

        Assert.isTrue(message.getState() == MsgStateEnum.OK, "the message must be in the state " + MsgStateEnum.OK);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + ": the message " + getMessage().toHumanString()
                + " successfully processed message (state = " + MsgStateEnum.OK + ").";
    }
}
