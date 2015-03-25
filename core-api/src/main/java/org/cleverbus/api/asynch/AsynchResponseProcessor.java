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

package org.cleverbus.api.asynch;

import org.cleverbus.api.asynch.model.CallbackResponse;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;


/**
 * Abstract processor that helps to create response for asynchronous incoming request.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @see "AsynchInMessageRoute"
 */
public abstract class AsynchResponseProcessor implements Processor {

    @Override
    public final void process(Exchange exchange) throws Exception {
        // check error
        CallbackResponse callbackResponse = (CallbackResponse)
                exchange.removeProperty(AsynchConstants.ERR_CALLBACK_RES_PROP);

        if (callbackResponse == null) {
            // no error
            callbackResponse = exchange.getIn().getBody(CallbackResponse.class);
        }

        exchange.getIn().setBody(setCallbackResponse(callbackResponse));
    }

    /**
     * Sets {@link CallbackResponse} to specific response and returns it.
     *
     * @param callbackResponse the callback response
     * @return response
     */
    protected abstract Object setCallbackResponse(CallbackResponse callbackResponse);
}
