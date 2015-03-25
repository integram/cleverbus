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

package org.cleverbus.core.common.ws;

import java.util.Locale;

import javax.xml.namespace.QName;

import org.cleverbus.api.exception.IntegrationException;
import org.cleverbus.api.exception.ThrottlingExceededException;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.AbstractEndpointExceptionResolver;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapMessage;


/**
 * Simple, SOAP-specific {@link org.springframework.ws.server.EndpointExceptionResolver EndpointExceptionResolver}
 * implementation that stores the exception's message as the fault string.
 * <p/>
 * The fault code is always set to a Server (in SOAP 1.1) or Receiver (SOAP 1.2).
 * <p/>
 * Example of the generated SOAP fault response for SOAP 1.1:
 * <pre>
    &lt;SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    &lt;SOAP-ENV:Header/>
    &lt;SOAP-ENV:Body>
       &lt;SOAP-ENV:Fault>
          &lt;faultcode>SOAP-ENV:Server</faultcode>
          &lt;faultstring xml:lang="en">E102: the validation error</faultstring>
          &lt;detail>
            &lt;errorCode xmlns="http://cleverbus.org">E102&lt;/errorCode>
          &lt;/detail>
       &lt;/SOAP-ENV:Fault>
    &lt;/SOAP-ENV:Body>
    &lt;/SOAP-ENV:Envelope>
 * </pre>
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ErrorCodeAwareSoapExceptionResolver extends AbstractEndpointExceptionResolver {

    private Locale locale = Locale.ENGLISH;
    private boolean throttlingAsServerError = false;

    private static final QName ERR_CODE = new QName("http://cleverbus.org", "errorCode");

    /**
     * Returns the locale for the faultstring or reason of the SOAP Fault.
     * <p/>
     * Defaults to {@link Locale#ENGLISH}.
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Sets the locale for the faultstring or reason of the SOAP Fault.
     * <p/>
     * Defaults to {@link Locale#ENGLISH}.
     */
    public void setLocale(Locale locale) {
        Assert.notNull(locale, "locale must not be null");
        this.locale = locale;
    }

    @Override
    protected final boolean resolveExceptionInternal(MessageContext messageContext, Object endpoint, Exception ex) {
        Assert.isInstanceOf(SoapMessage.class, messageContext.getResponse(),
                "SimpleSoapExceptionResolver requires a SoapMessage");

        if (throttlingAsServerError && ex instanceof ThrottlingExceededException) {
            // no SOAP fault => server error
            return false;
        } else {
            SoapMessage response = (SoapMessage) messageContext.getResponse();
            String faultString = StringUtils.hasLength(ex.getMessage()) ? ex.getMessage() : ex.toString();
            SoapBody body = response.getSoapBody();
            SoapFault fault = body.addServerOrReceiverFault(faultString, getLocale());
            customizeFault(messageContext, endpoint, ex, fault);

            return true;
        }
    }

    /**
     * Sets {@code false} if {@link ThrottlingExceededException} should be handled and SOAP fault generated
     * or {@code true} if {@link ThrottlingExceededException} should be treated as HTTP 500 error.
     * Default value is {@code false}.
     *
     * @param throttlingAsServerError sets behaviour for throttling exception
     */
    public void setThrottlingAsServerError(boolean throttlingAsServerError) {
        this.throttlingAsServerError = throttlingAsServerError;
    }

    /**
     * Adds error code element detail into SOAP fault.
     *
     * @param messageContext current message context
     * @param endpoint       the executed endpoint, or <code>null</code> if none chosen at the time of the exception
     * @param ex             the exception that got thrown during endpoint execution
     * @param fault          the SOAP fault to be customized.
     */
    protected void customizeFault(MessageContext messageContext, Object endpoint, Exception ex, SoapFault fault) {
        if (ex instanceof IntegrationException) {
            SoapFaultDetail detail = fault.addFaultDetail();
            detail.addFaultDetailElement(ERR_CODE).addText(((IntegrationException) ex).getError().getErrorCode());
        }
    }
}
