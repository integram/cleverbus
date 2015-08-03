/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cleverbus.core.common.ws;

import org.cleverbus.api.exception.InternalErrorEnum;
import org.cleverbus.api.exception.ValidationIntegrationException;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.*;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.util.*;


/**
 * Interceptor that validates the presence of trace header.
 * <p>
 * When the payload is invalid, this interceptor stops processing of the interceptor chain. Additionally, if the message
 * is a SOAP request message, a SOAP Fault is created as reply. Invalid SOAP responses do not result in a fault.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class HeaderAndPayloadValidatingInterceptor extends PayloadValidatingInterceptor {

    private static final String DEFAULT_FAULT_HEADER_REASON
            = InternalErrorEnum.E104.getErrorCode() + ": " + InternalErrorEnum.E104.getErrDesc();

    private QName traceHeaderElm = new QName("http://cleverbus.org/ws/Common-v1", "traceHeader");

    private boolean validateHeader = true;

    private String faultHeaderStringOrReason = DEFAULT_FAULT_HEADER_REASON;

    private Set<QName> ignoreRequests;

    @Override
    public boolean handleRequest(MessageContext messageContext, Object endpoint)
            throws IOException, SAXException, TransformerException {

        // validate payload
        boolean reqValResult = super.handleRequest(messageContext, endpoint);

        // validate header
        if (reqValResult && validateHeader && !ignoreRequest(messageContext)) {
            SaajSoapMessage soapMessage = (SaajSoapMessage) messageContext.getRequest();
            SoapHeader soapHeader = soapMessage.getSoapHeader();

            ValidationIntegrationException[] errors = validateHeader(soapHeader);
            if (!ObjectUtils.isEmpty(errors)) {
                return handleHeaderValidationErrors(messageContext, errors);
            } else if (logger.isDebugEnabled()) {
                logger.debug("Request header validated");
            }
        }

        return reqValResult;
    }

    /**
     * Checks if input request should be ignored from header checking.
     *
     * @param messageContext the msg context
     * @return {@code true} when input request should be ignored, otherwise {@code false}
     */
    private boolean ignoreRequest(MessageContext messageContext) {
        Node reqNode = ((DOMSource) messageContext.getRequest().getPayloadSource()).getNode();
        QName reqName = new QName(reqNode.getNamespaceURI(), reqNode.getLocalName());
        return ignoreRequests.contains(reqName);
    }

    /**
     * Validate SOAP header - check existence of trace header.
     *
     * @param soapHeader the SOAP header
     * @return array of possible validation errors
     */
    private ValidationIntegrationException[] validateHeader(SoapHeader soapHeader) {
        List<ValidationIntegrationException> errors = new ArrayList<ValidationIntegrationException>();

        boolean headerFound = false;
        if (soapHeader != null) {
            // iterate over header elements
            Iterator<SoapHeaderElement> itElements = soapHeader.examineAllHeaderElements();
            while (!headerFound && itElements.hasNext()) {
                SoapHeaderElement elm = itElements.next();
                if (traceHeaderElm.equals(elm.getName())) {
                    headerFound = true;
                }
            }
        }

        if (!headerFound) {
            errors.add(new ValidationIntegrationException("there is no header element: " + traceHeaderElm));
        }

        return errors.toArray(new ValidationIntegrationException[errors.size()]);
    }

    /**
     * Template method that is called when the request SOAP headers contains validation errors.
     * Default implementation logs all errors, and returns <code>false</code>, i.e. do not process the request.
     *
     * @param messageContext the message context
     * @param errors         the validation errors
     * @return <code>true</code> to continue processing the request, <code>false</code> (the default) otherwise
     */
    protected boolean handleHeaderValidationErrors(MessageContext messageContext, ValidationIntegrationException[] errors)
            throws TransformerException {

        for (ValidationIntegrationException error : errors) {
            logger.warn("XML validation error on request: " + error.getMessage());
        }

        if (messageContext.getResponse() instanceof SoapMessage) {
            SoapMessage response = (SoapMessage) messageContext.getResponse();
            SoapBody body = response.getSoapBody();
            SoapFault fault = body.addClientOrSenderFault(faultHeaderStringOrReason, getFaultStringOrReasonLocale());

            if (getAddValidationErrorDetail()) {
                SoapFaultDetail detail = fault.addFaultDetail();
                for (ValidationIntegrationException error : errors) {
                    SoapFaultDetailElement detailElement = detail.addFaultDetailElement(getDetailElementName());
                    detailElement.addText(error.getMessage());
                }
            }
        }

        return false;
    }

    /**
     * Sets whether validate SOAP header.
     *
     * @param validateHeader {@code true} for validation, otherwise {@code false}
     */
    public void setValidateHeader(boolean validateHeader) {
        this.validateHeader = validateHeader;
    }

    /**
     * Sets fault reason when there is no requested header.
     *
     * @param faultHeaderStringOrReason the fault message
     */
    public void setFaultHeaderStringOrReason(String faultHeaderStringOrReason) {
        this.faultHeaderStringOrReason = faultHeaderStringOrReason;
    }

    /**
     * Sets request root element names which will be ignored from trace header checking.
     *
     * @param ignoreRequests the array of element names, e.g.
     *                       {@code {http://cleverbus.org/ws/SubscriberService-v1}getCounterDataRequest }
     */
    public void setIgnoreRequests(Collection<String> ignoreRequests) {
        this.ignoreRequests = new HashSet<QName>();
        for (String ignoreRequest : ignoreRequests) {
            this.ignoreRequests.add(QName.valueOf(ignoreRequest));
        }
    }

    /**
     * Sets namespace for trace header.
     *
     * @param namespaceUri namespace of trace header
     */
    public void setTraceHeaderElmNamespace(String namespaceUri) {
        Assert.hasText(namespaceUri, "namespaceUri must not be empty");

        setTraceHeaderElm(namespaceUri, traceHeaderElm.getLocalPart());
    }

    /**
     * Sets local part for trace header.
     *
     * @param localPart local part of trace header
     */
    public void setTraceHeaderElmLocalPart(String localPart) {
        Assert.hasText(localPart, "localPart must not be empty");

        setTraceHeaderElm(traceHeaderElm.getNamespaceURI(), localPart);
    }

    /**
     * Sets trace header namespace and local part.
     *
     * @param namespaceUri namespace of trace header
     * @param localPart    local part of trace header
     */
    public void setTraceHeaderElm(String namespaceUri, String localPart) {
        Assert.hasText(namespaceUri, "namespaceUri must not be empty");
        Assert.hasText(localPart, "localPart must not be empty");

        traceHeaderElm = new QName(namespaceUri, localPart);
    }
}
