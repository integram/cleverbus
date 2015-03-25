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


/**
 * Exception indicates non-valid, illegal data.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class IllegalDataException extends ValidationIntegrationException {

    /**
     * Creates exception with the message and {@link InternalErrorEnum#E109} error code.
     *
     * @param msg the message
     */
    public IllegalDataException(String msg) {
        this(InternalErrorEnum.E109, msg);
    }

    /**
     * Creates exception with the message, {@link InternalErrorEnum#E109} error code and specified cause.
     *
     * @param msg   the message
     * @param cause the throwable that caused this exception
     */
    public IllegalDataException(String msg, Throwable cause) {
        this(InternalErrorEnum.E109, msg, cause);
    }


    /**
     * Creates validation exception with the specified error code and message.
     *
     * @param error the error code
     * @param msg   the message
     */
    public IllegalDataException(ErrorExtEnum error, String msg) {
        super(error, msg);
    }

    /**
     * Creates validation exception with the specified error code, message and cause.
     *
     * @param error the error code
     * @param msg   the message
     * @param cause the throwable that caused this exception
     */
    public IllegalDataException(ErrorExtEnum error, @Nullable String msg, @Nullable Throwable cause) {
        super(error, msg, cause);
    }
}
