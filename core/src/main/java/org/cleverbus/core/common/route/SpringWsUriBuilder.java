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

package org.cleverbus.core.common.route;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.cleverbus.api.route.WebServiceUriBuilder;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;


/**
 * Implementation of {@link WebServiceUriBuilder} for using Spring WS component.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class SpringWsUriBuilder implements WebServiceUriBuilder {

    private static final String MESSAGE_FACTORY_SOAP11 = "messageFactorySOAP11";

    private static final String MESSAGE_FACTORY_SOAP12 = "messageFactorySOAP12";

    @Override
    public String getOutWsUri(String connectionUri, String messageSenderRef, String soapAction) {
        Assert.hasText(connectionUri, "the connectionUri must not be empty");
        Assert.hasText(messageSenderRef, "the messageSenderRef must not be empty");

        String wsUri = "spring-ws:" + connectionUri + "?messageSender=#" + messageSenderRef
                + "&messageFactory=#" + MESSAGE_FACTORY_SOAP11;

        if (StringUtils.isNotEmpty(soapAction)) {
            wsUri += "&soapAction=" + soapAction;
        }

        return wsUri;
    }


    @Override
    public String getOutWsSoap12Uri(String connectionUri, String messageSenderRef, String soapAction) {
        Assert.hasText(connectionUri, "the connectionUri must not be empty");
        Assert.hasText(messageSenderRef, "the messageSenderRef must not be empty");

        String wsUri = "spring-ws:" + connectionUri + "?messageSender=#" + messageSenderRef
                + "&messageFactory=#" + MESSAGE_FACTORY_SOAP12;

        if (StringUtils.isNotEmpty(soapAction)) {
            wsUri += "&soapAction=" + soapAction;
        }

        return wsUri;
    }


    @Override
    public String getInWsUri(QName qName, String endpointMappingRef, @Nullable String params) {
        Assert.notNull(qName, "the qName must not be null");
        Assert.hasText(endpointMappingRef, "the endpointMappingRef must not be empty");

        return "spring-ws:rootqname:" + qName + "?endpointMapping=#" + endpointMappingRef
                + (params != null ? "&" + params : "");
    }
}
