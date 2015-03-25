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

package org.cleverbus.core.common.directcall;

import java.io.IOException;

import org.cleverbus.core.common.route.RouteConstants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;


/**
 * Implementation of {@link DirectCall} interface with HTTP client that calls {@link DirectCallWsRoute}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @see DirectCallWsRoute
 */
public class DirectCallHttpImpl implements DirectCall {

    /**
     * URI of this localhost application, including port number.
     */
    @Value("${contextCall.localhostUri}")
    private String localhostUri;

    @Override
    public String makeCall(String callId) throws IOException {
        Assert.hasText(callId, "callId must not be empty");

        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            HttpGet httpGet = new HttpGet(localhostUri + RouteConstants.HTTP_URI_PREFIX
                    + DirectCallWsRoute.SERVLET_URL + "?" + DirectCallWsRoute.CALL_ID_HEADER + "=" + callId);

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        // successful response
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new IOException(EntityUtils.toString(response.getEntity()));
                    }
                }
            };

            return httpClient.execute(httpGet, responseHandler);
        } finally {
            httpClient.close();
        }
    }
}
