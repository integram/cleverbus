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

/**
 * Standard Camel Spring WS component returns raw exception when invalid XML request occurs.
 * We need to have possibility to add own error code constant and returns fault response.
 * <p/>
 * Example:
 * <pre>
     &lt;SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
        &lt;SOAP-ENV:Header/>
        &lt;SOAP-ENV:Body>
           &lt;SOAP-ENV:Fault>
              &lt;faultcode>SOAP-ENV:Server&lt;/faultcode>
              &lt;faultstring xml:lang="en">E111: the request message is not valid XML (InvalidXmlException: 
 Could not parse XML; nested exception is org.xml.sax.SAXParseException: The element type "sub:ICCID" must be 
 terminated by the matching end-tag "&lt;/sub:ICCID>".)&lt;/faultstring>
           &lt;/SOAP-ENV:Fault>
        &lt;/SOAP-ENV:Body>
     &lt;/SOAP-ENV:Envelope>     
 * </pre>
 */
package org.cleverbus.core.common.ws.component;