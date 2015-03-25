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

package org.cleverbus.component;

import org.cleverbus.core.common.asynch.ExceptionTranslationRoute;
import org.cleverbus.test.AbstractDbTest;
import org.cleverbus.test.ActiveRoutes;

import org.springframework.test.context.ContextConfiguration;


/**
 * Parent class for all tests with database in components module.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@ActiveRoutes(classes = ExceptionTranslationRoute.class)
@ContextConfiguration(locations = {"classpath:/META-INF/test_components_db_conf.xml"})
public abstract class AbstractComponentsDbTest extends AbstractDbTest {

}
