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

package org.cleverbus.core.common.ws.component;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ws.InvalidXmlException;
import org.springframework.ws.context.DefaultMessageContext;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.transport.WebServiceConnection;
import org.springframework.ws.transport.context.DefaultTransportContext;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.WebServiceMessageReceiverHandlerAdapter;
import org.springframework.ws.transport.support.TransportUtils;


/**
 * Spring WS error handler that returns SOAP fault message when there is error during XML parsing
 * in incoming message.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ErrorAwareWebServiceMessageReceiverHandlerAdapter extends WebServiceMessageReceiverHandlerAdapter {

    @Override
    protected void handleInvalidXmlException(HttpServletRequest req, HttpServletResponse res, Object handler,
                                             InvalidXmlException ex) throws Exception {

        WebServiceConnection connection = new MyHttpServletConnection(req, res);

        TransportContext previousTransportContext = TransportContextHolder.getTransportContext();
        TransportContextHolder.setTransportContext(new DefaultTransportContext(connection));

        try {
            MessageContext messageContext = new DefaultMessageContext(getMessageFactory().createWebServiceMessage(),
                    getMessageFactory());

            SoapBody soapBody = ((SoapMessage) messageContext.getResponse()).getSoapBody();
            soapBody.addServerOrReceiverFault(getFaultString(ex), Locale.ENGLISH);
            connection.send(messageContext.getResponse());
        } finally {
            TransportUtils.closeConnection(connection);
            TransportContextHolder.setTransportContext(previousTransportContext);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // nothing to check - messageFactory will be set later in MessageDispatcherServlet
    }

    protected String getFaultString(InvalidXmlException ex) {
        return ex.getLocalizedMessage();
    }
}