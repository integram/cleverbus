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

import java.util.ArrayList;
import java.util.List;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;

import org.cleverbus.spi.alerts.AlertInfo;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;


/**
 * Alerts configuration via JMX.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 */
public class AlertsJmxConfiguration implements DynamicMBean {

    private static final String LIMIT_SUFFIX = ".limit";
    private static final String ENABLE_SUFFIX = ".enabled";

    private AbstractAlertsConfiguration configuration;

    /**
     * Creates new JMX configuration.
     *
     * @param configuration alerts configuration
     */
    public AlertsJmxConfiguration(AbstractAlertsConfiguration configuration) {
        Assert.notNull(configuration, "configuration must not be null");

        this.configuration = configuration;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        List<AlertInfo> alerts = configuration.getAlerts(false);

        List<MBeanAttributeInfo> attributes = new ArrayList<MBeanAttributeInfo>(alerts.size());

        // add all alert properties
        for (AlertInfo alert : alerts) {
            String keyLimit = alert.getId() + LIMIT_SUFFIX;
            String keyEnable = alert.getId() + ENABLE_SUFFIX;

            attributes.add(new MBeanAttributeInfo(keyEnable, "java.lang.String", "Enable/disable alert",
                    true, true, false));
            attributes.add(new MBeanAttributeInfo(keyLimit, "java.lang.String", "Set alert limit",
                    true, true, false));
        }

        MBeanInfo mBeanInfo = new MBeanInfo(this.getClass().getName(), "Alerts Configuration",
                attributes.toArray(new MBeanAttributeInfo[] {}), null, null, null);

        return mBeanInfo;
    }

    @Override
    public Object getAttribute(String attrName) throws AttributeNotFoundException, MBeanException, ReflectionException {
        Assert.notNull(attrName, "attrName must not be null");

        AlertInfo alert = getAlert(attrName);

        if (StringUtils.endsWith(attrName, ENABLE_SUFFIX)) {
            return alert.isEnabled();
        } else if (StringUtils.endsWith(attrName, LIMIT_SUFFIX)) {
            return String.valueOf(alert.getLimit());
        } else {
            throw new IllegalStateException("unsupported attribute name '" + attrName + "'");
        }
    }

    private AlertInfo getAlert(String attrName) throws AttributeNotFoundException {
        String alertId;
        if (StringUtils.endsWith(attrName, ENABLE_SUFFIX)) {
            alertId = StringUtils.substringBefore(attrName, ENABLE_SUFFIX);
        } else if (StringUtils.endsWith(attrName, LIMIT_SUFFIX)) {
            alertId = StringUtils.substringBefore(attrName, LIMIT_SUFFIX);
        } else {
            throw new AttributeNotFoundException("attribute name is not in expected format");
        }

        AlertInfo alert = configuration.findAlert(alertId);

        if (alert == null) {
            throw new AttributeNotFoundException("There is no alert for id '" + alertId + "'");
        }

        return alert;
    }

    @Override
    public void setAttribute(Attribute attribute)
            throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        Assert.notNull(attribute, "attribute must not be null");

        String attrName = attribute.getName();
        String value = (String) attribute.getValue();

        AlertInfo alert = getAlert(attrName);

        if (StringUtils.endsWith(attrName, ENABLE_SUFFIX)) {
            alert.setEnabled(BooleanUtils.toBoolean(value));
        } else if (StringUtils.endsWith(attrName, LIMIT_SUFFIX)) {
            alert.setLimit(Long.valueOf(value));
        } else {
            throw new IllegalStateException("unsupported attribute name '" + attrName + "'");
        }
    }

    @Override
    public AttributeList getAttributes(String[] strings) {
        return null;
    }

    @Override
    public AttributeList setAttributes(AttributeList objects) {
        return null;
    }

    @Override
    public Object invoke(String s, Object[] objects, String[] strings) throws MBeanException, ReflectionException {
        return null;
    }
}
