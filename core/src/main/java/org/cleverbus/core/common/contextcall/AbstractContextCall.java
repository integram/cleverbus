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

import java.util.UUID;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;


/**
 * Parent implementation of {@link ContextCall} interface, defines base behaviour.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public abstract class AbstractContextCall implements ContextCall {

    @Autowired
    private ContextCallRegistry callRegistry;

    @Nullable
    @Override
    public <T> T makeCall(Class<?> targetType, String methodName, Class<T> responseType, Object... methodArgs) {
        // generate unique ID
        String callId = UUID.randomUUID().toString();

        // save params into registry
        ContextCallParams params = new ContextCallParams(targetType, methodName, methodArgs);

        try {
            callRegistry.addParams(callId, params);

            // call target service
            callTargetMethod(callId, targetType, methodName);

            // get response from the call
            return callRegistry.getResponse(callId, responseType);

        } finally {
            callRegistry.clearCall(callId);
        }
    }

    /**
     * Calls target method of the service by specific protocol.
     *
     * @param callId the unique call ID
     * @param targetType the class of target service
     * @param methodName the name of calling method on target service
     * @throws IllegalStateException when error occurs during calling target method
     */
    protected abstract void callTargetMethod(String callId, Class<?> targetType, String methodName);

}
