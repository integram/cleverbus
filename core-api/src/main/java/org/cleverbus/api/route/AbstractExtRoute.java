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

package org.cleverbus.api.route;

import org.cleverbus.api.entity.ServiceExtEnum;

import org.springframework.util.Assert;


/**
 * Parent route definition for extension routes.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public abstract class AbstractExtRoute extends AbstractBasicRoute {

    /**
     * Suffix for extension routes.
     */
    public static final String EXT_ROUTE_SUFFIX = "_ext_route";

    /**
     * Suffix for asynchronous incoming routes, specific for extension routes.
     */
    public static final String EXT_IN_ROUTE_SUFFIX = "_in_route";

    /**
     * Suffix for asynchronous outbound routes, specific for extension routes.
     */
    public static final String EXT_OUT_ROUTE_SUFFIX = "_out_route";


    /**
     * Gets route ID for synchronous routes, specific for extension routes.
     *
     * @param service       the service name
     * @param operationName the operation name
     * @return route ID
     * @see #getRouteId(ServiceExtEnum, String)
     */
    public static String getExtRouteId(ServiceExtEnum service, String operationName) {
        Assert.notNull(service, "the service must not be null");
        Assert.hasText(operationName, "the operationName must not be empty");

        return service.getServiceName() + "_" + operationName + EXT_ROUTE_SUFFIX;
    }

    /**
     * Gets route ID for asynchronous incoming routes, specific for extension routes.
     *
     * @param service       the service name
     * @param operationName the operation name
     * @return route ID
     * @see #getInRouteId(ServiceExtEnum, String)
     */
    public static String getExtInRouteId(ServiceExtEnum service, String operationName) {
        Assert.notNull(service, "the service must not be null");
        Assert.hasText(operationName, "the operationName must not be empty");

        return service.getServiceName() + "_" + operationName + EXT_IN_ROUTE_SUFFIX;
    }

    /**
     * Gets route ID for asynchronous outbound routes, specific for extension routes.
     *
     * @param service       the service name
     * @param operationName the operation name
     * @return route ID
     * @see #getOutRouteId(ServiceExtEnum, String)
     */
    public static String getExtOutRouteId(ServiceExtEnum service, String operationName) {
        Assert.notNull(service, "the service must not be null");
        Assert.hasText(operationName, "the operationName must not be empty");

        return service.getServiceName() + "_" + operationName + EXT_OUT_ROUTE_SUFFIX;
    }
}
