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

import java.util.Set;

import org.cleverbus.api.entity.ExternalSystemExtEnum;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;


/**
 * Contract for confirmation of processed asynchronous messages.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface ExternalSystemConfirmation {

    /**
     * Confirms the message to the external system.
     * If the {@link Message#getState()}
     * is {@link MsgStateEnum#OK},
     * then confirms the message as OK,
     * if it's {@link MsgStateEnum#FAILED},
     * then confirms the message as Failed.
     *
     * @param msg the message to confirm
     */
    void confirm(Message msg);

    /**
     * Returns supported external systems.
     *
     * @return the supported external systems
     */
    Set<ExternalSystemExtEnum> getExternalSystems();

}
