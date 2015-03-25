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

/**
 * Common route constants.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public final class RouteConstants {

    private RouteConstants() {
    }

    /**
     * Name of the servlet for incoming HTTP communication.
     */
    public static final String CAMEL_SERVLET = "CamelServlet";

    /**
     * URI prefix for web services - "spring-ws" in web.xml.
     */
    public static final String WS_URI_PREFIX = "/ws/";

    /**
     * URI prefix for CamelServlet (see web.xml).
     */
    public static final String HTTP_URI_PREFIX = "/http/";

    /**
     * URI prefix for web admin GUI - "spring-admin-mvc" in web.xml.
     */
    public static final String WEB_URI_PREFIX = "/web/admin/";
}
