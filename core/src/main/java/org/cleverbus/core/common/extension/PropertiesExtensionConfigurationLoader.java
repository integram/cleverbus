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

package org.cleverbus.core.common.extension;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;


/**
 * CleverBus extensions loader where extensions are defined in properties.
 * Relevant properties are with '{@value #PROPERTY_PREFIX}' prefix.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class PropertiesExtensionConfigurationLoader extends AbstractExtensionConfigurationLoader {

    public static final String PROPERTY_PREFIX = "context.ext";

    private Properties properties;

    /**
     * Creates new extension loader with specified properties.
     *
     * @param properties the properties
     */
    public PropertiesExtensionConfigurationLoader(Properties properties) {
        Assert.notNull(properties, "the properties must not be null");

        this.properties = properties;
    }

    /**
     * Initializes extension configuration from properties.
     */
    @PostConstruct
    private void initExtensions() {
        // gets extension config locations
        Set<String> confLocations = new HashSet<String>();

        Enumeration<?> propNamesEnum = properties.propertyNames();
        while (propNamesEnum.hasMoreElements()) {
            String propName = (String) propNamesEnum.nextElement();

            if (propName.startsWith(PROPERTY_PREFIX)) {
                String configLoc = properties.getProperty(propName);

                if (StringUtils.isNotEmpty(configLoc)) {
                    confLocations.add(configLoc);
                }
            }
        }

        // loads extension configuration
        loadExtensions(confLocations.toArray(new String[]{}));
    }
}
