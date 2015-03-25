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
import org.cleverbus.core.common.version.VersionInfo;
import org.cleverbus.core.common.version.VersionInfoSource;
import org.cleverbus.core.common.version.VersionPrinter;

import org.springframework.beans.factory.annotation.Autowired;


/**
 * Route definition for version service - listens to {@code /http/version} url.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@CamelConfiguration
public class VersionRoute extends AbstractBasicRoute {

    private static final String ROUTE_ID_VERSION = "version" + IN_ROUTE_SUFFIX;

    @Autowired
    private VersionInfoSource versionInfoSource;

    @Override
    public void doConfigure() throws Exception {
        VersionInfo filter = new VersionInfo(VersionPrinter.APPLICATION_NAME, null, null, null, null);
        final VersionInfo[] versions = versionInfoSource.getVersionInformation(filter);

        String versionStr = "N/A";
        if (versions.length > 0) {
            versionStr = versions[0].getFullVersion() + " (" + versions[0].getDate() + ")";
        }

        from("servlet:///version?servletName=" + RouteConstants.CAMEL_SERVLET)
                .routeId(ROUTE_ID_VERSION)
                .transform(constant(versionStr));
    }
}
