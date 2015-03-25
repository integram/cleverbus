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

package org.cleverbus.core.common.contextcall;

import org.cleverbus.api.route.AbstractBasicRoute;
import org.cleverbus.api.route.CamelConfiguration;
import org.cleverbus.common.log.Log;
import org.cleverbus.core.common.route.RouteConstants;

import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.apache.camel.LoggingLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;


/**
 * Route that serves as input URI for calling from one Spring context to another context.
 * Route expects HTTP GET call with unique call identifier to {@link ContextCallRegistry}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@CamelConfiguration
public class ContextCallRoute extends AbstractBasicRoute {

    static final String SERVLET_URL = "contextCall";
    static final String CALL_ID_HEADER = "callId";

    static final String ROUTE_ID_CONTEXT_CALL = "contextCall" + EXTERNAL_ROUTE_SUFFIX;

    @Autowired
    private ContextCallRegistry callRegistry;

    @Override
    protected void doConfigure() throws Exception {
        from("servlet:///" + SERVLET_URL + "?servletName=" + RouteConstants.CAMEL_SERVLET)
            .routeId(ROUTE_ID_CONTEXT_CALL)

            .validate(header(CALL_ID_HEADER).isNotNull())

            .log(LoggingLevel.DEBUG, "Incoming context call with ID: ${headers." + CALL_ID_HEADER + "} ")

            .bean(this, "makeCall");
    }

    /**
     * Makes call.
     *
     * @param callId Call ID for getting call parameters from {@link ContextCallRegistry}
     */
    @Handler
    public void makeCall(@Header(CALL_ID_HEADER) String callId) {
        Assert.hasText(callId, "the callId must not be empty");

        // get params
        ContextCallParams params = callRegistry.getParams(callId);

        Object res = ReflectionCallUtils.invokeMethod(params, getApplicationContext());

        // save response
        callRegistry.addResponse(callId, res);

        Log.debug("Response of the call ID '" + callId + "' was saved: " + res);
    }
}
