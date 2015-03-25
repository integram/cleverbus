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

package org.cleverbus.core.common.asynch.msg;

import java.util.Date;

import javax.annotation.Nullable;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.asynch.model.TraceHeader;
import org.cleverbus.api.asynch.model.TraceIdentifier;
import org.cleverbus.api.entity.EntityTypeExtEnum;
import org.cleverbus.api.entity.ExternalSystemExtEnum;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.api.entity.ServiceExtEnum;
import org.cleverbus.core.common.asynch.AsynchInMessageRoute;
import org.cleverbus.core.common.asynch.TraceHeaderProcessor;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.apache.camel.component.spring.ws.SpringWebserviceMessage;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.xml.transform.StringResult;


/**
 * Processes input message and transform it to {@link Message} entity.
 * <p/>
 * Prerequisite: exchange with several header values - see input params of {@link #createMessage}
 * (call {@link AsynchInMessageRoute} before)
 * <p/>
 * Output: {@link Message} entity in the state {@link MsgStateEnum#PROCESSING} because we want to process
 * the message immediately
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public final class MessageTransformer {

    private static MessageTransformer instance;

    private MessageTransformer() {
    }

    public static MessageTransformer getInstance() {
        if (instance == null) {
            instance = new MessageTransformer();
        }

        return instance;
    }

    /**
     * Creates message entity.
     *
     * @param exchange the exchange
     * @param traceHeader the trace header
     * @param payload the message payload
     * @param service the source service
     * @param operationName the source operation name
     * @param objectId the object ID
     * @param entityType the entity type
     * @param funnelValue the funnel value
     * @return new message
     */
    @Handler
    public Message createMessage(Exchange exchange,
            @Header(value = TraceHeaderProcessor.TRACE_HEADER) TraceHeader traceHeader,
            @Body String payload,
            @Header(value = AsynchConstants.SERVICE_HEADER) ServiceExtEnum service,
            @Header(value = AsynchConstants.OPERATION_HEADER) String operationName,
            @Header(value = AsynchConstants.OBJECT_ID_HEADER) @Nullable String objectId,
            @Header(value = AsynchConstants.ENTITY_TYPE_HEADER) @Nullable EntityTypeExtEnum entityType,
            @Header(value = AsynchConstants.FUNNEL_VALUE_HEADER) @Nullable String funnelValue,
            @Header(value = AsynchConstants.GUARANTEED_ORDER_HEADER) @Nullable Boolean guaranteedOrder,
            @Header(value = AsynchConstants.EXCLUDE_FAILED_HEADER) @Nullable Boolean excludeFailedState) {

        // validate input params (trace header is validated in TraceHeaderProcessor)
        Assert.notNull(exchange, "the exchange must not be null");
        Assert.notNull(traceHeader, "the traceHeader must not be null");
        Assert.notNull(payload, "the payload must not be null");
        Assert.notNull(service, "the service must not be null");
        Assert.notNull(operationName, "the operationName must not be null");

        Date currDate = new Date();

        Message msg = new Message();
        msg.setState(MsgStateEnum.PROCESSING);
        msg.setStartProcessTimestamp(currDate);

        // params from trace header
        final TraceIdentifier traceId = traceHeader.getTraceIdentifier();
        msg.setMsgTimestamp(traceId.getTimestamp().toDate());
        msg.setReceiveTimestamp(currDate);
        msg.setSourceSystem(new ExternalSystemExtEnum() {
            @Override
            public String getSystemName() {
                return StringUtils.upperCase(traceId.getApplicationID());
            }
        });
        msg.setCorrelationId(traceId.getCorrelationID());
        msg.setProcessId(traceId.getProcessID());

        msg.setService(service);
        msg.setOperationName(operationName);
        msg.setObjectId(objectId);
        msg.setEntityType(entityType);

        msg.setFunnelValue(funnelValue);
        msg.setGuaranteedOrder(BooleanUtils.isTrue(guaranteedOrder));
        msg.setExcludeFailedState(BooleanUtils.isTrue(excludeFailedState));

        msg.setPayload(payload);
        msg.setEnvelope(getSOAPEnvelope(exchange));

        msg.setLastUpdateTimestamp(currDate);

        return msg;
    }

    /**
     * Gets original SOAP envelope.
     *
     * @param exchange the exchange
     * @return envelope as string or {@code null} if input message isn't Spring web service message
     */
    @Nullable
    public static String getSOAPEnvelope(Exchange exchange) {
        if (! (exchange.getIn() instanceof SpringWebserviceMessage)) {
            return null;
        }

        try {
            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer aTransformer = tranFactory.newTransformer();

            SpringWebserviceMessage inMsg = (SpringWebserviceMessage) exchange.getIn();
            Source source = ((SaajSoapMessage) inMsg.getWebServiceMessage()).getEnvelope().getSource();

            StringResult strRes = new StringResult();
            aTransformer.transform(source, strRes);

            return strRes.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Error occurred during conversion SOAP envelope to string", ex);
        }
    }
}
