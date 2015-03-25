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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.cleverbus.spi.throttling.ThrottleProps;
import org.cleverbus.spi.throttling.ThrottleScope;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;


/**
 * Throttling configuration via {@link Properties}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ThrottlingPropertiesConfiguration extends AbstractThrottlingConfiguration {

    public static final String PROPERTY_PREFIX = "throttling.";
    static final String DEFAULT_INTERVAL_PROP = PROPERTY_PREFIX + "defaultInterval";
    static final String DEFAULT_LIMIT_PROP = PROPERTY_PREFIX + "defaultLimit";

    private Properties properties;

    /**
     * Creates new configuration with specified properties.
     *
     * @param properties the properties
     */
    public ThrottlingPropertiesConfiguration(Properties properties) {
        Assert.notNull(properties, "the properties must not be null");

        this.properties = properties;

        initProps();
    }

    /**
     * Creates new configuration with disabled throttling.
     */
    public ThrottlingPropertiesConfiguration() {
        this.setThrottlingDisabled(true);
    }

    /**
     * Initializes configuration from properties.
     */
    private void initProps() {
        // extracts relevant property values
        int defaultInterval = DEFAULT_INTERVAL;
        int defaultLimit = DEFAULT_LIMIT;

        List<String> propNames = new ArrayList<String>();

        Enumeration<?> propNamesEnum = properties.propertyNames();
        while (propNamesEnum.hasMoreElements()) {
            String propName = (String) propNamesEnum.nextElement();

            if (propName.startsWith(PROPERTY_PREFIX)) {
                if (propName.equals(DEFAULT_INTERVAL_PROP)) {
                    defaultInterval = Integer.valueOf(properties.getProperty(DEFAULT_INTERVAL_PROP));
                } else if (propName.equals(DEFAULT_LIMIT_PROP)) {
                    defaultLimit = Integer.valueOf(properties.getProperty(DEFAULT_LIMIT_PROP));
                } else {
                    propNames.add(propName);
                }
            }
        }

        // add default throttle scope
        addProperty(ThrottleScope.ANY_SOURCE_SYSTEM, ThrottleScope.ANY_SERVICE, defaultInterval, defaultLimit);

        // create throttle scopes for relevant properties
        for (String propName : propNames) {
            // validate property name, possible values:
            //  throttling.crm.setActivityExt
            //  throttling.*.setActivityExt
            //  throttling.crm.*

            String[] nameParts = StringUtils.split(propName, ThrottleScope.THROTTLE_SEPARATOR);
            if (nameParts.length != 3) {
                throw new IllegalStateException("throttling property name must have exactly 3 parts, "
                        + "e.g. 'throttling.crm.setActivityExt'");
            }

            String propValue = properties.getProperty(propName);

            // format: limit [/interval]
            String[] valueParts = StringUtils.split(propValue, ThrottleProps.PROP_VALUE_SEPARATOR);
            int limit = Integer.valueOf(valueParts[0]);
            int interval = defaultInterval;
            if (valueParts.length > 1) {
                interval = Integer.valueOf(valueParts[1]);
            }

            addProperty(nameParts[1], nameParts[2], interval, limit);
        }
    }
}
