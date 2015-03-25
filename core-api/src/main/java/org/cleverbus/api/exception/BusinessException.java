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
 * Common exception for all cases where a business-logic-related exception occurred in some external system,
 * (e.g., the specified ID does not exist), which would not disappear by itself,
 * therefore it does not make sense to retry a message after this exception occurred.
 */
public class BusinessException extends IntegrationException {

    public BusinessException(ErrorExtEnum error) {
        super(error);
    }

    public BusinessException(ErrorExtEnum error, @Nullable String msg) {
        super(error, msg);
    }

    public BusinessException(ErrorExtEnum error, @Nullable String msg, @Nullable Throwable t) {
        super(error, msg, t);
    }
}
