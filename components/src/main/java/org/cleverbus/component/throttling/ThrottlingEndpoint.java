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

import javax.annotation.Nullable;

import org.cleverbus.spi.throttling.ThrottlingProcessor;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.springframework.util.Assert;


/**
 * Endpoint for {@link ThrottlingComponent throttling} component.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ThrottlingEndpoint extends DefaultEndpoint {

    private RequestTypeEnum requestType;

    private String operationName;

    /**
     * Creates new endpoint.
     *
     * @param endpointUri the URI
     * @param component the "throttling" component
     */
    public ThrottlingEndpoint(String endpointUri, Component component) {
        super(endpointUri, component);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new ThrottlingProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("you cannot send messages to this endpoint:" + getEndpointUri());
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public RequestTypeEnum getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestTypeEnum requestType) {
        Assert.notNull(requestType, "requestType mustn't be null");

        this.requestType = requestType;
    }

    @Nullable
    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(@Nullable String operationName) {
        this.operationName = operationName;
    }

    ThrottlingProcessor getThrottlingProcessor() {
        return ((ThrottlingComponent)getComponent()).getThrottlingProcessor();
    }
}
