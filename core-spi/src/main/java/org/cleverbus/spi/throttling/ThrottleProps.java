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

package org.cleverbus.spi.throttling;

import org.cleverbus.api.common.HumanReadable;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Throttle properties - max. count for specified time interval.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public final class ThrottleProps implements HumanReadable {

    public static final String PROP_VALUE_SEPARATOR = "/";

    private int interval;
    private int limit;

    /**
     * Creates throttle properties.
     *
     * @param interval the time interval in seconds
     * @param limit the limit of requests for specified interval
     */
    public ThrottleProps(int interval, int limit) {
        this.interval = interval;
        this.limit = limit;
    }

    public int getInterval() {
        return interval;
    }

    public int getLimit() {
        return limit;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("interval", interval)
                .append("limit", limit)
                .toString();
    }

    @Override
    public String toHumanString() {
        return limit + PROP_VALUE_SEPARATOR + interval;
    }
}
