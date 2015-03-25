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

import javax.annotation.Nullable;

import org.cleverbus.api.entity.ExternalCall;
import org.cleverbus.api.entity.ExternalCallStateEnum;
import org.cleverbus.api.exception.LockFailureException;


/**
 * Pools confirmations (=external calls) in the {@link ExternalCallStateEnum#FAILED} state.
 * If there is this confirmation available then try to get and lock it for further processing.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface ConfirmationPool {

    /**
     * Gets confirmation for next processing.
     *
     * @return external call or {@code null} if not available any confirmation
     * @throws LockFailureException if found a confirmation ({@link ExternalCall}), but failed to get a lock
     */
    @Nullable
    ExternalCall getNextConfirmation();
}
