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

package org.cleverbus.core.throttling;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.cleverbus.common.log.Log;
import org.cleverbus.spi.throttling.ThrottleProps;
import org.cleverbus.spi.throttling.ThrottleScope;
import org.cleverbus.spi.throttling.ThrottlingConfiguration;


/**
 * Parent class for throttling configuration.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public abstract class AbstractThrottlingConfiguration implements ThrottlingConfiguration {

    /**
     * Default time interval (in seconds).
     */
    public static final int DEFAULT_INTERVAL = 60;

    /**
     * Default max. requests for time interval.
     */
    public static final int DEFAULT_LIMIT = 60;

    private Map<ThrottleScope, ThrottleProps> props = new HashMap<ThrottleScope, ThrottleProps>();

    /**
     * True for disabling throttling at all.
     */
    private boolean throttlingDisabled = false;


    @Nullable
    @Override
    public final ThrottleProps getThrottleProps(ThrottleScope inScope) {
        int maxMatch = -1;
        ThrottleScope matchedScope = null;
        for (ThrottleScope scope : props.keySet()) {
            int match = scope.match(inScope);

            if (match >= 0 && match > maxMatch) {
                matchedScope = scope;
                maxMatch = match;
            }
        }

        if (matchedScope != null) {
            return props.get(matchedScope);
        } else {
            return null;
        }
    }

    /**
     * Adds new configuration property or updates already existing property.
     *
     * @param sourceSystem the source system, can be used '*' for any system
     * @param serviceName the service name, can be used '*' for any system
     * @param interval the time interval in seconds
     * @param limit the limit of requests for specified interval
     */
    protected final void addProperty(String sourceSystem, String serviceName, int interval, int limit) {
        ThrottleScope scope = new ThrottleScope(sourceSystem, serviceName);

        ThrottleProps throttleProps = new ThrottleProps(interval, limit);

        if (props.put(scope, throttleProps) == null) {
            Log.debug("new throttle properties added: " + scope + ", props: " + throttleProps);
        } else {
            Log.debug("throttle properties updated: " + scope + ", props: " + throttleProps);
        }
    }

    /**
     * Gets all throttling properties.
     *
     * @return throttling properties
     */
    @Override
    public final Map<ThrottleScope, ThrottleProps> getProperties() {
        return Collections.unmodifiableMap(props);
    }

    /**
     * Is throttling disabled at all?
     *
     * @return {@code true} for disabling
     */
    @Override
    public boolean isThrottlingDisabled() {
        return throttlingDisabled;
    }

    /**
     * Sets true for disabling throttling.
     *
     * @param throttlingDisabled true for disabling throttling
     */
    public void setThrottlingDisabled(boolean throttlingDisabled) {
        this.throttlingDisabled = throttlingDisabled;
    }
}
