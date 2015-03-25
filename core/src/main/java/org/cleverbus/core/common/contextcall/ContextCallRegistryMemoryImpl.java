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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import org.cleverbus.api.exception.NoDataFoundException;
import org.cleverbus.common.log.Log;

import org.joda.time.DateTime;
import org.springframework.util.Assert;


/**
 * Memory implementation of {@link ContextCallRegistry} interface.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ContextCallRegistryMemoryImpl implements ContextCallRegistry {

    private static final int OLD_PARAMS_INTERVAL = 60 * 1000;

    private Map<String, ContextCallParams> paramsRegistry = new ConcurrentHashMap<String, ContextCallParams>();

    private Map<String, Object> responseRegistry = new ConcurrentHashMap<String, Object>();

    @Override
    public void addParams(String callId, ContextCallParams params) {
        Assert.hasText(callId, "the callId must not be empty");
        Assert.notNull(params, "the params must not be null");

        if (paramsRegistry.get(callId) != null) {
            throw new IllegalStateException("there are already call params with call ID = " + callId);
        }

        paramsRegistry.put(callId, params);

        Log.debug("Call params with callId=" + callId + " added to registry: " + params);

        removeOldParams();
    }

    @Override
    public ContextCallParams getParams(String callId) {
        ContextCallParams params = paramsRegistry.get(callId);

        if (params == null) {
            throw new NoDataFoundException("there are no parameters for callId = '" + callId + "' ");
        }

        return params;
    }

    @Override
    public void addResponse(String callId, Object res) {
        Assert.hasText(callId, "the callId must not be empty");
        Assert.notNull(res, "the res must not be null");

        if (responseRegistry.get(callId) != null) {
            throw new IllegalStateException("there is already call response with call ID = " + callId);
        }

        responseRegistry.put(callId, res);

        Log.debug("Call response with callId=" + callId + " added to registry: " + res);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResponse(String callId, Class<T> requiredType) {
        T res = (T) responseRegistry.get(callId);

        if (res == null) {
            throw new NoDataFoundException("there are no response for callId = '" + callId + "' ");
        }

        return res;
    }

    @Override
    public void clearCall(String callId) {
        removeParams(callId);
        removeResponse(callId);
    }

    private void removeParams(String callId) {
        if (paramsRegistry.remove(callId) != null) {
            Log.debug("Call params with callId=" + callId + " were removed from registry");
        }
    }

    private void removeResponse(String callId) {
        if (responseRegistry.remove(callId) != null) {
            Log.debug("Call response with callId=" + callId + " were removed from registry");
        }
    }

    /**
     * Removes old params.
     */
    private void removeOldParams() {
        DateTime threshold = DateTime.now().minus(OLD_PARAMS_INTERVAL);

        Set<String> removes = new HashSet<String>();

        // find old params
        for (Map.Entry<String, ContextCallParams> en : paramsRegistry.entrySet()) {
            if (en.getValue().getCreationTimestamp().isBefore(threshold)) {
                removes.add(en.getKey());
            }
        }

        // remove old params
        for (String callId : removes) {
            removeParams(callId);
        }
    }
}
