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

package org.cleverbus.core.common.directcall;

/**
 * Contract of registry calls between sibling Spring application contexts, specifically from web application context
 * to Spring WS (Camel) application context.
 * <p/>
 * This registry serves for saving call parameters. Registry is initialized in root application context
 * and therefore it's accessible from both child (sibling) application contexts.
 * <p/>
 * There is the following procedure:
 * <ol>
 *     <li>client (web controller) generates unique call identifier
 *     <li>client {@link #addParams(String, DirectCallParams) inserts to registry call parameters under unique identifier}
 *     <li>client calls Camel route in Spring WS (Camel) application context - unique identifier is transferred
 *     <li>server accepts incoming call, gets unique call identifier that use
 *          for {@link #getParams(String) getting call parameters}
 *     <li>server calls external system
 *     <li>server returns response from external system
 *     <li>client {@link #removeParams(String) removes parameters for the call}
 * </ol>
 *
 * Initializes implementation of this interface in root application context because it's necessary
 * to have this instance available from both child contexts.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface DirectCallRegistry {

    /**
     * Adds new parameters under specified call ID.
     *
     * @param callId the call identifier
     * @param params call parameters
     */
    void addParams(String callId, DirectCallParams params);

    /**
     * Gets call parameters.
     *
     * @param callId the call identifier
     * @return call parameters
     * @throws IllegalArgumentException when there are no call parameters for specified call ID
     */
    DirectCallParams getParams(String callId);

    /**
     * Removes call parameters for specified call ID.
     *
     * @param callId the call identifier
     */
    void removeParams(String callId);

}
