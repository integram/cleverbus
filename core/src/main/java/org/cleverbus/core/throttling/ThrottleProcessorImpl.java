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

import org.cleverbus.api.exception.ThrottlingExceededException;
import org.cleverbus.common.log.Log;
import org.cleverbus.spi.throttling.ThrottleCounter;
import org.cleverbus.spi.throttling.ThrottleProps;
import org.cleverbus.spi.throttling.ThrottleScope;
import org.cleverbus.spi.throttling.ThrottlingConfiguration;
import org.cleverbus.spi.throttling.ThrottlingProcessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;


/**
 * Implementation of {@link ThrottlingProcessor} interface.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ThrottleProcessorImpl implements ThrottlingProcessor {

    @Autowired
    private ThrottlingConfiguration configuration;

    @Autowired
    private ThrottleCounter counter;

    @Override
    public void throttle(ThrottleScope throttleScope) {
        if (!configuration.isThrottlingDisabled()) {
            Assert.notNull(throttleScope, "throttleScope must not be null");

            Assert.isTrue(!throttleScope.getSourceSystem().equals(ThrottleScope.ANY_SOURCE_SYSTEM)
                || !throttleScope.getServiceName().equals(ThrottleScope.ANY_SERVICE),
                    "throttle scope must define source system or service name (one of them at least)");


            ThrottleProps throttleProps = configuration.getThrottleProps(throttleScope);
            if (throttleProps == null) {
                Log.warn("no throttling for input request: " + throttleScope);
                return;
            }

            int reqCount = counter.count(throttleScope, throttleProps.getInterval());

            if (reqCount > throttleProps.getLimit()) {
                String errMsg = "Actual count of requests for source system '" + throttleScope.getSourceSystem()
                        + "' and service '" + throttleScope.getServiceName()
                        + "' exceeded limit (interval=" + throttleProps.getInterval()
                        + "sec, limit=" + throttleProps.getLimit() + ", actual count=" + reqCount + ")";

                Log.warn(errMsg);

                throw new ThrottlingExceededException(errMsg);
            }
        }
    }
}
