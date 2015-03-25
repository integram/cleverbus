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

package org.cleverbus.api.extcall;

import org.apache.camel.Exchange;


/**
 * Constants for external call parameters.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public final class ExtCallComponentParams {

    /**
     * Optional exchange property, which specifies which operation to use for external call checks.
     */
    public static final String EXTERNAL_CALL_OPERATION = "externalCallOperation";

    /**
     * Optional exchange property, which is appended to the generated operation key or used instead of the key,
     * depending on the URI.
     */
    public static final String EXTERNAL_CALL_KEY = "externalCallKey";

    /**
     * Optional exchange property, which can be set during external call execution.
     * <ul>
     *     <li>If set to {@code true}, the external call is considered successful</li>
     *     <li>If set to {@code false}, the external call is considered failed</li>
     *     <li>If not set (default) or set to {@code null},
     *     the external call is considered successful unless:
     *     either an unhandled exception occurs ({@link Exchange#isFailed()} returns true),
     *     or the exchange is stopped ({@link Exchange#ROUTE_STOP} property is set to true)</li>
     * </ul>
     * If set to anything else, a boolean value will be acquired via Camel type conversion.
     */
    public static final String EXTERNAL_CALL_SUCCESS = "externalCallSuccess";


    private ExtCallComponentParams() {
    }
}
