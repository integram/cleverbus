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

package org.cleverbus.core.common.contextcall;

import javax.annotation.Nullable;

import org.cleverbus.api.exception.NoDataFoundException;


/**
 * Contract of registry calls for transferring calls' parameters and response between sibling Spring application
 * contexts, specifically from web application context to Spring WS (Camel) application context.
 * <p/>
 * This registry serves for saving call parameters and response. Registry is initialized in root application context
 * and therefore it's accessible from both child (sibling) application contexts.
 * <p/>
 * Basic workflow:
 * <ol>
 *     <li>client: add params
 *     <li>server: get params + add response
 *     <li>client: get response + clear memory
 * </ol>
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface ContextCallRegistry {

    /**
     * Adds new parameters under specified call ID.
     *
     * @param callId the call identifier
     * @param params call parameters
     */
    void addParams(String callId, ContextCallParams params);

    /**
     * Gets call parameters.
     *
     * @param callId the call identifier
     * @return call parameters
     * @throws NoDataFoundException if there is no response with specified call ID
     */
    ContextCallParams getParams(String callId);

    /**
     * Adds new response to specified call ID.
     *
     * @param callId the call identifier
     * @param res response of the call (can be null)
     */
    void addResponse(String callId, @Nullable Object res);

    /**
     * Gets response of the specified call.
     *
     * @param callId the call identifier
     * @return response of the call
     * @throws NoDataFoundException if there is no response with specified call ID
     */
    @Nullable
    <T> T getResponse(String callId, Class<T> requiredType);

    /**
     * Removes call parameters and response for specified call ID.
     *
     * @param callId the call identifier
     */
    void clearCall(String callId);

}
