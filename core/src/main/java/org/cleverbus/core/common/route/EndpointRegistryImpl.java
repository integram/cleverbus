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

package org.cleverbus.core.common.route;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.Endpoint;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;


/**
 * Default implementation of {@link EndpointRegistry}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class EndpointRegistryImpl implements EndpointRegistry, CamelContextAware {

    private CamelContext camelContext;

    @Override
    public Collection<String> getEndpointURIs(String includePattern) {
        // gets endpoints
        Collection<Endpoint> endpoints = camelContext.getEndpoints();

        Collection<String> endpointURIs = new ArrayList<String>(endpoints.size());

        // compile pattern
        Pattern pattern = null;
        if (StringUtils.isNotEmpty(includePattern)) {
            pattern = Pattern.compile(includePattern);
        }

        // go through all endpoints and filter URIs
        for (Endpoint endpoint : endpoints) {
            String uri = endpoint.getEndpointUri();

            if (filter(uri, pattern)) {
                endpointURIs.add(uri);
            }
        }

        return endpointURIs;
    }

    /**
     * Returns {@code true} if specified URI matches specified pattern.
     *
     * @param endpointURI the endpoint URI
     * @param pattern pattern
     * @return {@code true} if specified URI matches at least one of specified patterns otherwise {@code false}
     */
    private boolean filter(String endpointURI, @Nullable Pattern pattern) {
        Assert.hasText(endpointURI, "the endpointURI must be defined");

        if (pattern == null) {
            return true;
        }

        Matcher matcher = pattern.matcher(endpointURI);
        if (matcher.matches()) {
            return true;
        }

        return false;
    }

    @Override
    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    @Override
    public CamelContext getCamelContext() {
        return camelContext;
    }
}
