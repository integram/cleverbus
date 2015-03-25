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

package org.cleverbus.core.common.version;

import javax.annotation.PostConstruct;

import org.cleverbus.common.log.Log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Class for printing version info when application starts.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@Component
public class VersionPrinter {

    @Autowired
    private VersionInfoSource versionInfoSource;

    /**
     * Application name, must correspond with {@code <name>} element from root pom.xml.
     */
    public static final String APPLICATION_NAME = "CleverBus Core";

    @PostConstruct
    public void printVersion() {
        VersionInfo filter = new VersionInfo(APPLICATION_NAME, null, null, null, null);
        final VersionInfo[] versions = versionInfoSource.getVersionInformation(filter);

        String versionStr = "N/A";
        if (versions.length > 0) {
            versionStr = versions[0].getFullVersion() + " (" + versions[0].getDate() + ")";
        }

        Log.info("CleverBus version: " + versionStr);
    }
}
