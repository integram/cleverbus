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

package org.cleverbus.modules.in.hello;

import static org.cleverbus.common.jaxb.JaxbDataFormatHelper.jaxb;

import javax.xml.namespace.QName;

import org.cleverbus.api.route.AbstractBasicRoute;
import org.cleverbus.api.route.CamelConfiguration;
import org.cleverbus.modules.ServiceEnum;
import org.cleverbus.modules.in.hello.model.SyncHelloRequest;
import org.cleverbus.modules.in.hello.model.SyncHelloResponse;

import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.apache.camel.LoggingLevel;
import org.springframework.util.Assert;


/**
 * Route definition for "syncHello" operation.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@CamelConfiguration(value = SyncHelloRoute.ROUTE_BEAN)
public class SyncHelloRoute extends AbstractBasicRoute {

    public static final String ROUTE_BEAN = "syncHelloRouteBean";

    private static final String OPERATION_NAME = "syncHello";

    public static final String ROUTE_ID_SYNC_HELLO = getRouteId(ServiceEnum.HELLO, OPERATION_NAME);

    public static final String HELLO_SERVICE_NS = "http://cleverbus.org/ws/HelloService-v1";

    @Override
    protected void doConfigure() throws Exception {
        from(getInWsUri(new QName(HELLO_SERVICE_NS, "syncHelloRequest")))
                .routeId(ROUTE_ID_SYNC_HELLO)

                .policy("roleWsAuthPolicy")

                .to("throttling:sync:" + OPERATION_NAME)

                .unmarshal(jaxb(SyncHelloRequest.class))

                .log(LoggingLevel.DEBUG, "Calling hello service with name: ${body.name}")

                .bean(this, "composeGreeting")

                .marshal(jaxb(SyncHelloResponse.class));
    }

    @Handler
    public SyncHelloResponse composeGreeting(@Body SyncHelloRequest req) {
        Assert.notNull(req, "req must not be null");

        String greeting = "Hello " + req.getName();

        SyncHelloResponse res = new SyncHelloResponse();
        res.setGreeting(greeting);
        return res;
    }
}
