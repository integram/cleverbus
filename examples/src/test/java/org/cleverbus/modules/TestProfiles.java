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

import org.springframework.test.annotation.IfProfileValue;


/**
 * Defines test profiles which are useful when specific test runs with specific target environment
 * (=specific target system).
 * <p/>
 * When you want to run some test for specific environment, then you have to run VM with system parameter
 * for target environment, e.g. {@code -Dintegration=ALL}
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @see IfProfileValue
 */
public abstract class TestProfiles {

    // -- profile names

    /**
     * Integration tests (= tests which depends on external systems).
     */
    public static final String INTEGRATION = "integration";


    // -- profile values

    /**
     * All types of integration tests.
     */
    public static final String INTEGRATION_ALL = "ALL";


    private TestProfiles() {
    }
}
