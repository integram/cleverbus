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

package org.cleverbus.api.exception;

import javax.annotation.Nullable;

import org.cleverbus.api.entity.MsgStateEnum;


/**
 * Exception indicates error during validation.
 * <p/>
 * If there is validation error than there is no next try, message gets to {@link MsgStateEnum#FAILED FAILED state}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ValidationIntegrationException extends IntegrationException {

    /**
     * Creates validation exception with the message and {@link InternalErrorEnum#E102} error code.
     *
     * @param msg the message
     */
    public ValidationIntegrationException(String msg) {
        super(InternalErrorEnum.E102, msg);
    }

    /**
     * Creates validation exception with the specified error code.
     *
     * @param error the error code
     */
    public ValidationIntegrationException(ErrorExtEnum error) {
        super(error);
    }

    /**
     * Creates validation exception with the specified error code and message.
     *
     * @param error the error code
     * @param msg   the message
     */
    public ValidationIntegrationException(ErrorExtEnum error, String msg) {
        super(error, msg);
    }

    /**
     * Creates validation exception with the specified error code, message and cause.
     *
     * @param error the error code
     * @param msg   the message
     * @param cause the throwable that caused this exception
     */
    public ValidationIntegrationException(ErrorExtEnum error, @Nullable String msg, @Nullable Throwable cause) {
        super(error, msg, cause);
    }
}
