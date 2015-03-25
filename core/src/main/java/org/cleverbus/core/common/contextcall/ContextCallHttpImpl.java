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

import java.io.IOException;

import org.cleverbus.core.common.route.RouteConstants;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;


/**
 * Implementation of {@link ContextCall} interface with HTTP client that calls {@link ContextCallRoute}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ContextCallHttpImpl extends AbstractContextCall {

    /**
     * URI of this localhost application, including port number.
     */
    @Value("${contextCall.localhostUri}")
    private String localhostUri;

    @Override
    protected void callTargetMethod(String callId, Class<?> targetType, String methodName) {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            HttpGet httpGet = new HttpGet(localhostUri + RouteConstants.HTTP_URI_PREFIX
                    + ContextCallRoute.SERVLET_URL + "?" + ContextCallRoute.CALL_ID_HEADER + "=" + callId);

            httpClient.execute(httpGet);
        } catch (IOException ex) {
            throw new IllegalStateException("error occurs during calling target method '" + methodName
                    + "' of service type '" + targetType.getSimpleName() + "'", ex);
        } finally {
            IOUtils.closeQuietly(httpClient);
        }
    }
}
