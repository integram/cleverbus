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

package org.cleverbus.core.common.asynch;

import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.route.AbstractBasicRoute;
import org.cleverbus.api.route.CamelConfiguration;
import org.cleverbus.core.common.exception.ExceptionTranslator;


/**
 * Route definition that defines route for exception translation.
 * See {@link ExceptionTranslator} for more details.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@CamelConfiguration(value = ExceptionTranslationRoute.ROUTE_BEAN)
public class ExceptionTranslationRoute extends AbstractBasicRoute {

    public static final String ROUTE_BEAN = "exTranslatorRouteBean";

    /**
     * Route for processing FATAL error.
     */
    public static final String ROUTE_ID_EX_TRANSLATION = "exceptionTranslation" + ROUTE_SUFFIX;

    @Override
    protected void doConfigure() throws Exception {

        from(AsynchConstants.URI_EX_TRANSLATION)
                .routeId(ROUTE_ID_EX_TRANSLATION)

                .errorHandler(noErrorHandler())

                .process(ExceptionTranslator.getInstance());
    }
}
