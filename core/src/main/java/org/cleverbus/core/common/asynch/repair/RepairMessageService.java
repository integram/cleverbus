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

package org.cleverbus.core.common.asynch.repair;

import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.entity.MsgStateEnum;


/**
 * Repairs hooked messages in the state {@link MsgStateEnum#PROCESSING}: these messages are after specified time changed
 * to {@link MsgStateEnum#PARTLY_FAILED} state with increasing failed count.
 * If failed count exceeds threshold then message is redirected to {@link AsynchConstants#URI_ERROR_FATAL}
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface RepairMessageService {

    public static final String BEAN = "repairMessageService";

    /**
     * Finds messages in state {@link MsgStateEnum#PROCESSING} and repairs them.
     */
    void repairProcessingMessages();
}
