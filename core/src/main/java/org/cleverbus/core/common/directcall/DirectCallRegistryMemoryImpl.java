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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.cleverbus.common.log.Log;

import org.joda.time.DateTime;
import org.springframework.util.Assert;


/**
 * Memory implementation of {@link DirectCallRegistry} interface.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class DirectCallRegistryMemoryImpl implements DirectCallRegistry {

    private static final int OLD_PARAMS_INTERVAL = 60 * 1000;

    private Map<String, DirectCallParams> registry = new ConcurrentHashMap<String, DirectCallParams>();

    @Override
    public void addParams(String callId, DirectCallParams params) {
        Assert.hasText(callId, "the callId must not be empty");
        Assert.notNull(params, "the params must not be null");

        if (registry.get(callId) != null) {
            throw new IllegalStateException("there are already call params with call ID = " + callId);
        }

        registry.put(callId, params);

        Log.debug("Call params with callId=" + callId + " added to registry: " + params);

        removeOldParams();
    }

    @Override
    public DirectCallParams getParams(String callId) {
        DirectCallParams params = registry.get(callId);

        if (params == null) {
            throw new IllegalStateException("there are no parameters for callId = '" + callId + "' ");
        }

        return params;
    }

    @Override
    public void removeParams(String callId) {
        if (registry.remove(callId) != null) {
            Log.debug("Call params with callId=" + callId + " were removed from registry");
        }
    }

    /**
     * Removes old params.
     */
    private void removeOldParams() {
        DateTime threshold = DateTime.now().minus(OLD_PARAMS_INTERVAL);

        Set<String> removes = new HashSet<String>();

        // find old params
        for (Map.Entry<String, DirectCallParams> en : registry.entrySet()) {
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
