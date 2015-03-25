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

import org.cleverbus.api.entity.Message;
import org.cleverbus.spi.throttling.ThrottleScope;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.springframework.util.Assert;


/**
 * Producer for {@link ThrottlingComponent throttling} component.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ThrottlingProducer extends DefaultProducer {

    /**
     * Creates new producer.
     *
     * @param endpoint the endpoint
     */
    public ThrottlingProducer(ThrottlingEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        ThrottlingEndpoint endpoint = (ThrottlingEndpoint) getEndpoint();

        ThrottleScope throttleScope;

        if (endpoint.getRequestType() == RequestTypeEnum.SYNC) {
            // sync
            throttleScope = new ThrottleScope(ThrottleScope.ANY_SOURCE_SYSTEM, endpoint.getOperationName());

        } else {
            // async
            Message msg = exchange.getIn().getBody(Message.class);

            Assert.notNull(msg, "the msg must not be null");

            throttleScope = new ThrottleScope(msg.getSourceSystem().getSystemName(), msg.getOperationName());
        }

        endpoint.getThrottlingProcessor().throttle(throttleScope);
    }
}
