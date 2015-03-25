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

import org.cleverbus.api.route.AbstractBasicRoute;
import org.cleverbus.api.route.CamelConfiguration;

import org.apache.camel.LoggingLevel;


/**
 * Route definition for ping service - listens to {@code /http/ping} url.
 * Simple ping service for monitoring of the server.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@CamelConfiguration
public class PingRoute extends AbstractBasicRoute {

    private static final String ROUTE_ID_PING = "ping" + IN_ROUTE_SUFFIX;

    @Override
    public void doConfigure() throws Exception {
        from("servlet:///ping?servletName=" + RouteConstants.CAMEL_SERVLET)
                .routeId(ROUTE_ID_PING)

                .log(LoggingLevel.INFO, "Incoming PING request ... ")
                .transform(constant("PONG\n"));
    }
}
