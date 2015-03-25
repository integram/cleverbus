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

package org.cleverbus.test.route;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.cleverbus.api.route.WebServiceUriBuilder;

import org.springframework.util.Assert;


/**
 * Implementation of {@link WebServiceUriBuilder} for using in tests.
 * "direct" component is used instead of specific web service implementation, e.g. "spring-ws".
 * Advantage is that it's no further needed to use "advice" for changing input route URI.
 * <p/>
 * Direct URI has the following format: {@value #URI_WS_IN}/{@value #URI_WS_OUT} + local part of the request qName.
 * <p/>
 * Example of using in test:
 * <pre>
 *  Produce(uri = TestWsUriBuilder.URI_WS_IN + "syncHelloRequest")
    private ProducerTemplate producer;
 * </pre>
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class TestWsUriBuilder implements WebServiceUriBuilder {

    public static final String URI_WS_IN = "direct:inWS_";

    /**
     * URI of web service output endpoint for SOAP 1.1.
     */
    public static final String URI_WS_OUT = "direct:outWS_";

    /**
     * URI of web service output endpoint for SOAP 1.2.
     */
    public static final String URI_WS12_OUT = "direct:outWS12_";

    @Override
    public String getOutWsUri(String connectionUri, String messageSenderRef, String soapAction) {
        return URI_WS_OUT;
    }

    @Override
    public String getOutWsSoap12Uri(String connectionUri, String messageSenderRef, String soapAction) {
        return URI_WS12_OUT;
    }

    @Override
    public String getInWsUri(QName qName, String endpointMappingRef, @Nullable String params) {
        Assert.notNull(qName, "the qName must not be null");
        Assert.hasText(qName.getLocalPart(), "the localPart must not be empty");

        return URI_WS_IN + qName.getLocalPart();
    }
}