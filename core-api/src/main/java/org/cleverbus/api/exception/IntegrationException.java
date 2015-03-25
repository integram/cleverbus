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

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


/**
 * Common integration exception, parent exception for all exceptions thrown by this integration platform.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class IntegrationException extends RuntimeException {

    private final ErrorExtEnum error;

    /**
     * Creates new integration exception with specified error code.
     *
     * @param error the error code
     */
    public IntegrationException(ErrorExtEnum error) {
        this(error, null);
    }

    /**
     * Creates new integration exception with specified error code and message.
     *
     * @param error the error code
     * @param msg   the message
     */
    public IntegrationException(ErrorExtEnum error, @Nullable String msg) {
        this(error, msg, null);
    }

    /**
     * Creates new integration exception with specified error code, message and exception.
     *
     * @param error the error code
     * @param msg   the message
     * @param t     the cause exception
     */
    public IntegrationException(ErrorExtEnum error, @Nullable String msg, @Nullable Throwable t) {
        super(msg, t);

        Assert.notNull(error, "the error must not be null");

        this.error = error;
    }

    /**
     * Gets the error code.
     *
     * @return error code
     */
    public ErrorExtEnum getError() {
        return error;
    }

    @Override
    public String getMessage() {
        String superMsg = super.getMessage();
        if (StringUtils.hasText(superMsg)) {
            return superMsg;
        }

        return getError().getErrDesc();
    }
}
