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

package org.cleverbus.core.common.asynch;

import javax.annotation.Nullable;

import org.cleverbus.api.entity.Message;
import org.cleverbus.common.log.GUID;
import org.cleverbus.common.log.Log;
import org.cleverbus.common.log.LogContextFilter;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


/**
 * Helper class for setting log context parameters.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 */
public final class LogContextHelper {

    private LogContextHelper() {
    }

    /**
     * Set log context parameters, specifically:
     * <ul>
     *     <li>{@link LogContextFilter#CTX_SOURCE_SYSTEM}
     *     <li>{@link LogContextFilter#CTX_CORRELATION_ID}
     *     <li>{@link LogContextFilter#CTX_PROCESS_ID}
     *     <li>{@link LogContextFilter#CTX_REQUEST_ID}
     * </ul>
     *
     * It's because child threads don't inherits this information from parent thread automatically.
     * If there is no request ID defined then new ID is created.
     *
     * @param message the message
     * @param requestId the request ID
     */
    public static void setLogContextParams(Message message, @Nullable String requestId) {
        Assert.notNull(message, "the message must not be null");

        // source system
        Log.setContextValue(LogContextFilter.CTX_SOURCE_SYSTEM, message.getSourceSystem().getSystemName());

        // correlation ID
        Log.setContextValue(LogContextFilter.CTX_CORRELATION_ID, message.getCorrelationId());

        // process ID
        Log.setContextValue(LogContextFilter.CTX_PROCESS_ID, message.getProcessId());

        // request ID
        if (StringUtils.hasText(requestId)) {
            Log.setContextValue(LogContextFilter.CTX_REQUEST_ID, requestId);
        } else {
            Log.setContextValue(LogContextFilter.CTX_REQUEST_ID, new GUID().toString());
        }
    }
}
