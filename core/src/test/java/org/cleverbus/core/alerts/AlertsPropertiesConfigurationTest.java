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

package org.cleverbus.core.alerts;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Properties;

import org.cleverbus.spi.alerts.AlertInfo;

import org.junit.Test;


/**
 * Test suite for {@link AlertsPropertiesConfiguration}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 */
public class AlertsPropertiesConfigurationTest {

    @Test
    public void testConf() {
        // prepare properties
        Properties props = new Properties();

        // add alert (min. version)
        String prefix = AlertsPropertiesConfiguration.ALERT_PROP_PREFIX + "1.";
        props.put(prefix + AlertsPropertiesConfiguration.ID_PROP, "ID");
        props.put(prefix + AlertsPropertiesConfiguration.LIMIT_PROP, "11");
        props.put(prefix + AlertsPropertiesConfiguration.SQL_PROP, "select COUNT()");

        // add alert (min. version)
        prefix = AlertsPropertiesConfiguration.ALERT_PROP_PREFIX + "66.";
        props.put(prefix + AlertsPropertiesConfiguration.ID_PROP, "ID2");
        props.put(prefix + AlertsPropertiesConfiguration.LIMIT_PROP, "11");
        props.put(prefix + AlertsPropertiesConfiguration.SQL_PROP, "select COUNT()");
        props.put(prefix + AlertsPropertiesConfiguration.ENABLED_PROP, "true");
        props.put(prefix + AlertsPropertiesConfiguration.MAIL_SBJ_PROP, "subject");
        props.put(prefix + AlertsPropertiesConfiguration.MAIL_BODY_PROP, "body");

        // create configuration
        AlertsPropertiesConfiguration conf = new AlertsPropertiesConfiguration(props);

        // verify
        assertThat(conf.getAlert("ID"), notNullValue());
        assertThat(conf.getAlert("ID2"), notNullValue());

        AlertInfo alert1 = conf.getAlert("ID");
        assertThat(alert1.isEnabled(), is(true));
        assertThat(alert1.getSql(), is("select COUNT()"));
        assertThat(alert1.getLimit(), is(11L));
        assertThat(alert1.getNotificationSubject(), nullValue());
        assertThat(alert1.getNotificationBody(), nullValue());

        AlertInfo alert2 = conf.getAlert("ID2");
        assertThat(alert2.isEnabled(), is(true));
        assertThat(alert2.getSql(), is("select COUNT()"));
        assertThat(alert2.getLimit(), is(11L));
        assertThat(alert2.getNotificationSubject(), is("subject"));
        assertThat(alert2.getNotificationBody(), is("body"));
    }

    @Test(expected = IllegalStateException.class)
    public void testDuplicateId() {
        Properties props = new Properties();

        // add alert (min. version)
        String prefix = AlertsPropertiesConfiguration.ALERT_PROP_PREFIX + "1.";
        props.put(prefix + AlertsPropertiesConfiguration.ID_PROP, "ID");
        props.put(prefix + AlertsPropertiesConfiguration.ID_PROP, "ID");

        new AlertsPropertiesConfiguration(props);
    }
}
