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


/**
 * Contract for calling method of service in sibling (Spring) context.
 * <p/>
 * Example: {@code makeCall(EndpointRegistry.class, "getEndpointURIs", Collection.class)}
 * calls method getEndpointURIs (without parameters) in service EndpointRegistry and expects result as Collection.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface ContextCall {

    /**
     * Makes call to method of service in sibling (Spring) context.
     *
     * @param targetType the class of target service; there must be exactly one Spring bean of this type
     * @param methodName the name of calling method on target service
     * @param methodArgs the method arguments (if any)
     * @return response from calling (can be null if no response)
     * @throws IllegalStateException when error occurs during calling target method
     */
    @Nullable
    <T> T makeCall(Class<?> targetType, String methodName, Class<T> responseType, Object... methodArgs);

}
