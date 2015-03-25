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

package org.cleverbus.core.common.asynch.confirm;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.cleverbus.api.asynch.confirm.ConfirmationCallback;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.common.log.Log;

import org.springframework.util.Assert;


/**
 * Default implementation of {@link ConfirmationCallback} that only logs information and nothing more.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class DefaultConfirmationCallback implements ConfirmationCallback {

    private static final Set<MsgStateEnum> ALLOWED_STATES =
            Collections.unmodifiableSet(EnumSet.of(MsgStateEnum.OK, MsgStateEnum.FAILED));

    @Override
    public void confirm(Message msg) {
        Assert.notNull(msg, "Message must not be null");
        Assert.isTrue(ALLOWED_STATES.contains(msg.getState()), "Message must be in a final state to be confirmed");

        switch(msg.getState()) {
            case OK:
                Log.debug("Confirmation - the message " + msg.toHumanString() + " was successfully processed.");
                break;
            case FAILED:
                Log.debug("Confirmation - processing of the message " + msg.toHumanString() + " failed.");
                break;
        }
    }
}
