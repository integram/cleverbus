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
import java.util.List;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;

import org.cleverbus.spi.throttling.ThrottleProps;
import org.cleverbus.spi.throttling.ThrottleScope;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;


/**
 * Throttling configuration via JMX.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class JmxThrottlingConfiguration implements DynamicMBean {

    private AbstractThrottlingConfiguration configuration;

    /**
     * Creates new JMX configuration.
     *
     * @param configuration throttling configuration
     */
    public JmxThrottlingConfiguration(AbstractThrottlingConfiguration configuration) {
        Assert.notNull(configuration, "configuration must not be null");

        this.configuration = configuration;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        Map<ThrottleScope, ThrottleProps> props = configuration.getProperties();

        List<MBeanAttributeInfo> attributes = new ArrayList<MBeanAttributeInfo>(props.size());

        // add all throttling properties
        for (Map.Entry<ThrottleScope, ThrottleProps> propsEntry : props.entrySet()) {
            String key = propsEntry.getKey().toHumanString();

            attributes.add(new MBeanAttributeInfo(key, "java.lang.String", key, true, true, false));
        }

        MBeanInfo mBeanInfo = new MBeanInfo(this.getClass().getName(), "Throttling Configuration",
                attributes.toArray(new MBeanAttributeInfo[] {}), null, null, null);

        return mBeanInfo;
    }

    @Override
    public Object getAttribute(String attrName) throws AttributeNotFoundException, MBeanException, ReflectionException {
        Assert.notNull(attrName, "attrName must not be null");

        // attr name: systemName . serviceName
        String[] parts = StringUtils.split(attrName, ThrottleScope.THROTTLE_SEPARATOR);

        if (parts.length != 2) {
            throw new AttributeNotFoundException("attribute name is not in expected format: 'systemName . serviceName'");
        }

        ThrottleScope throttleScope = new ThrottleScope(parts[0], parts[1]);

        ThrottleProps throttleProps = configuration.getProperties().get(throttleScope);
        if (throttleProps == null) {
            throw new AttributeNotFoundException("There is no throttling property for '" + throttleScope + "'");
        }

        return throttleProps.toHumanString();
    }

    @Override
    public void setAttribute(Attribute attribute)
            throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        Assert.notNull(attribute, "attribute must not be null");

        // attr name: systemName . serviceName
        String attrName = attribute.getName();

        String[] nameParts = StringUtils.split(attrName, ThrottleScope.THROTTLE_SEPARATOR);

        if (nameParts.length != 2) {
            throw new AttributeNotFoundException("attribute name is not in expected format: 'systemName . serviceName'");
        }

        // attr value: limit / interval
        String attrValue = (String)attribute.getValue();

        String[] valueParts = StringUtils.split(attrValue, ThrottleProps.PROP_VALUE_SEPARATOR);

        if (valueParts.length != 2) {
            throw new InvalidAttributeValueException("attribute value is not in expected format: 'limit / interval'");
        }

        configuration.addProperty(nameParts[0], nameParts[1], Integer.valueOf(valueParts[1]), Integer.valueOf(valueParts[0]));
    }

    @Override
    public AttributeList getAttributes(String[] attrNames) {
        return null;
    }

    @Override
    public AttributeList setAttributes(AttributeList objects) {
        return null;
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        return null;
    }
}
