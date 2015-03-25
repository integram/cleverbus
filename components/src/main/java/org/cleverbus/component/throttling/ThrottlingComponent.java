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

package org.cleverbus.component.throttling;

import java.util.Map;

import org.cleverbus.spi.throttling.ThrottlingProcessor;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.util.ObjectHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;


/**
 * Apache Camel component "throttling" for throttling processing messages.
 * <p/>
 * Syntax: {@code throttling:requestType[:operationName]}, where
 * <ul>
 *     <li>requestType specifies request type, e.g. SYNC or ASYNC
 *     <li>operation name, e.g. "createCustomer" (mandatory for SYNC request type only)
 * </ul>
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ThrottlingComponent extends DefaultComponent {

    @Autowired
    private ThrottlingProcessor throttlingProcessor;

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        ThrottlingEndpoint endpoint = new ThrottlingEndpoint(uri, this);

        // parse URI - "requestType:operationName"
        String endpointURI = ObjectHelper.after(uri, ":");
        if (endpointURI != null && endpointURI.startsWith("//")) {
            endpointURI = endpointURI.substring(2);
        }

        endpointURI = StringUtils.trimToNull(endpointURI);
        Assert.hasText(endpointURI, "Throttling endpoint URI must not be empty");

        RequestTypeEnum requestTypeEnum;
        String requestType;
        String operationName = null;

        // endpointURI = "requestType:operationName"
        if (StringUtils.contains(endpointURI, ":")) {
            requestType = ObjectHelper.before(endpointURI, ":");
            operationName = ObjectHelper.after(endpointURI, ":");
        } else {
            requestType = endpointURI;
        }

        // check request type value
        if (requestType.equalsIgnoreCase(RequestTypeEnum.SYNC.name())
                || requestType.equalsIgnoreCase(RequestTypeEnum.ASYNC.name())) {
            requestTypeEnum = RequestTypeEnum.valueOf(requestType.toUpperCase());
        } else {
            throw new IllegalArgumentException("request type must have one of the following values: 'sync' or 'async'");
        }

        // check operation name for SYNC request type
        if (requestTypeEnum == RequestTypeEnum.SYNC && operationName == null) {
            throw new IllegalArgumentException("operation name is mandatory for 'sync' request type");
        }

        endpoint.setRequestType(requestTypeEnum);
        endpoint.setOperationName(operationName);

        return endpoint;
    }

    ThrottlingProcessor getThrottlingProcessor() {
        return throttlingProcessor;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        // checking references
        Assert.notNull(throttlingProcessor, "throttlingProcessor mustn't be null");
    }
}
