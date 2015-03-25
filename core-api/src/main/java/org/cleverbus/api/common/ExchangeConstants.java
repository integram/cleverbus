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

package org.cleverbus.api.common;

import org.cleverbus.api.asynch.model.TraceHeader;

import org.apache.camel.Exchange;

/**
 * Constants regarding to holding information in {@link Exchange}
 *
 * @author <a href="mailto:tomas.hanus@cleverlance.com">Tomas Hanus</a>
 * @since 0.4
 */
public final class ExchangeConstants {

    /**
     * Header value that holds {@link TraceHeader}.
     */
    public static final String TRACE_HEADER = "ASYNCH_TRACE_HEADER";

    private ExchangeConstants() {
    }
}
