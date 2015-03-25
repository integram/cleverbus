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

package org.cleverbus.modules;

import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Test that verifies if all Camel routes are correctly initialized - if there are unique route IDs and unique URIs.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class RoutesInitTest extends AbstractModulesDbTest {

    @BeforeClass
    public static void setInitAllRoutes() {
        setInitAllRoutes(true);
    }

    @Test
    public void testInit() {
        // nothing to do - if all routes are successfully initialized then test is OK
        System.out.println("All routes were successfully initialized");
    }
}
