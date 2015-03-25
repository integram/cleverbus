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

package org.cleverbus.api.route;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;


/**
 * Contract for creating web service URI.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface WebServiceUriBuilder {

    /**
     * Constructs a "to" URI for sending WS messages to external systems,
     * i.e., Camel Web Service Endpoint URI for contacting an external system via <strong>SOAP 1.1</strong>.
     *
     * @param connectionUri the URI to connect to the external system, e.g.: http://localhost:8080/vfmock/ws/mm7
     * @param messageSenderRef the message sender ref (bean id/name in Spring context)
     * @param soapAction the SOAP action to be invoked,
     *                   can be {@code null} for implicit handling of SOAP messages by the external system
     * @return the Camel Endpoint URI for producing (sending via To) SOAP messages to external system
     */
    String getOutWsUri(String connectionUri, String messageSenderRef, @Nullable String soapAction);


    /**
     * Constructs a "to" URI for sending WS messages to external systems,
     * i.e., Camel Web Service Endpoint URI for contacting an external system via <strong>SOAP 1.2</strong>.
     *
     * @param connectionUri the URI to connect to the external system, e.g.: http://localhost:8080/vfmock/ws/mm7
     * @param messageSenderRef the message sender ref (bean id/name in Spring context)
     * @param soapAction the SOAP action to be invoked,
     *                   can be {@code null} for implicit handling of SOAP messages by the external system
     * @return the Camel Endpoint URI for producing (sending via To) SOAP messages to external system
     */
    String getOutWsSoap12Uri(String connectionUri, String messageSenderRef, @Nullable String soapAction);


    /**
     * Gets "from" URI for handling incoming WS messages with default "endpointMapping" bean.
     *
     * @return from URI
     * @param qName the operation QName (namespace + local part)
     * @param endpointMappingRef the endpoint mapping ref (bean id/name in Spring context)
     * @param params the endpoint URI parameters
     */
    String getInWsUri(QName qName, String endpointMappingRef, @Nullable String params);

}
