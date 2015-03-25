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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Properties;

import org.cleverbus.spi.throttling.ThrottleProps;
import org.cleverbus.spi.throttling.ThrottleScope;

import org.junit.Test;


/**
 * Test suite for {@link ThrottlingPropertiesConfiguration}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ThrottlingPropertiesConfigurationTest {

    @Test
    public void testConf() {
        // prepare properties
        String prefix = ThrottlingPropertiesConfiguration.PROPERTY_PREFIX;
        Properties props = new Properties();
        props.put(ThrottlingPropertiesConfiguration.DEFAULT_LIMIT_PROP, "5");
        props.put(ThrottlingPropertiesConfiguration.DEFAULT_INTERVAL_PROP, "15");

        props.put(prefix + "crm.op1", "10");
        props.put(prefix + "crm.op2", "10/70");
        props.put(prefix + "billing.*", "50");
        props.put(prefix + "*.sendSms", "100");
        props.put(prefix + "*.sendSms", "100/6");

        // create configuration
        ThrottlingPropertiesConfiguration conf = new ThrottlingPropertiesConfiguration(props);

        // verify
        assertThrottleProp(conf, "crm", "op1", 10, 15);
        assertThrottleProp(conf, "crm", "op2", 10, 70);
        assertThrottleProp(conf, "crm", ThrottleScope.ANY_SERVICE, 10, 15);
        assertThrottleProp(conf, "crm", "sendSms", 100, 6);

        assertThrottleProp(conf, "billing", ThrottleScope.ANY_SERVICE, 50, 15);
        assertThrottleProp(conf, "billing", "activateSubscriber", 50, 15);

        assertThrottleProp(conf, "billing", "sendSmsWithParams", 50, 15);
        assertThrottleProp(conf, ThrottleScope.ANY_SOURCE_SYSTEM, "sendSms", 100, 6);

        assertThrottleProp(conf, "erp", "createDeposit", 5, 15);
    }

    private void assertThrottleProp(ThrottlingPropertiesConfiguration conf, String sourceSystem, String serviceName,
                                    int expLimit, int expInterval) {
        ThrottleScope throttleScope = new ThrottleScope(sourceSystem, serviceName);

        ThrottleProps throttleProps = conf.getThrottleProps(throttleScope);
        assertThat(throttleProps, notNullValue());

        assertThat("assert limit for " + throttleScope, throttleProps.getLimit(), is(expLimit));
        assertThat("assert interval for " + throttleScope, throttleProps.getInterval(), is(expInterval));
    }
}
