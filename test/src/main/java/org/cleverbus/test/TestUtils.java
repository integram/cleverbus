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

package org.cleverbus.test;

import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.asynch.model.CallbackResponse;
import org.cleverbus.api.asynch.model.ConfirmationTypes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.model.ToDefinition;


/**
 * Handy methods for testing.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public final class TestUtils {

    private TestUtils() {
    }

    /**
     * Replaces calling TO {@link AsynchConstants#URI_ASYNCH_IN_MSG} with processor that creates OK response.
     * <p/>
     * Useful when you want to test input (IN) route of asynchronous process.
     *
     * @param builder the advice builder
     */
    public static final void replaceToAsynch(AdviceWithRouteBuilder builder) {
        // remove AsynchInMessageRoute.URI_ASYNCH_IN_MSG
        builder.weaveByType(ToDefinition.class).replace().process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                // creates OK response
                CallbackResponse callbackResponse = new CallbackResponse();
                callbackResponse.setStatus(ConfirmationTypes.OK);

                exchange.getIn().setBody(callbackResponse);
            }
        });
    }

}
