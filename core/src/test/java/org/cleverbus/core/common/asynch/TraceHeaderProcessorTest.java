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

package org.cleverbus.core.common.asynch;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;

import org.cleverbus.api.asynch.model.TraceHeader;
import org.cleverbus.api.asynch.model.TraceIdentifier;
import org.cleverbus.api.exception.InternalErrorEnum;
import org.cleverbus.api.exception.ValidationIntegrationException;
import org.cleverbus.core.AbstractCoreTest;
import org.cleverbus.core.common.validator.TraceIdentifierValidator;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Test suite for {@link TraceHeaderProcessor}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class TraceHeaderProcessorTest extends AbstractCoreTest {

    @Produce(uri = "direct:testRoute")
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    public void prepareRoute(final TraceHeaderProcessor processor) throws Exception {

        RouteBuilder testRoute = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:testRoute")
                    .process(processor)
                    .to("mock:test");
            }
        };

        getCamelContext().addRoutes(testRoute);
    }

    @Test
    public void testParsingTraceHeaderFromBody() throws Exception {
        prepareRoute(new TraceHeaderProcessor(true, null));

        String request = "<notifyCollectionStepRequest xmlns=\"http://cleverbus.org/ws/NotificationsService-v1\">"
                + "            <traceIdentifier xmlns=\"http://cleverbus.org/ws/Common-v1\">"
                + "                <applicationID>ERP</applicationID>"
                + "                <timestamp>2013-09-27T10:23:34.6987744+02:00</timestamp>"
                + "                <correlationID>da793349-b486-489a-9180-200789b7007f</correlationID>"
                + "                <processID>process123</processID>"
                + "            </traceIdentifier>"
                + "            <externalCustomerAccountID>2065</externalCustomerAccountID>"
                + "            <eventDate>2013-09-26T00:00:00</eventDate>"
                + "            <stepType>1</stepType>"
                + "            <debtAmount>679</debtAmount>"
                + "            <invoiceNo>130000000378</invoiceNo>"
                + "            <variableSymbol>7002065001</variableSymbol>"
                + "        </notifyCollectionStepRequest>";

        mock.expectedMessageCount(1);

        // send message
        producer.sendBody(request);

        mock.assertIsSatisfied();

        Exchange exchange = mock.getExchanges().get(0);
        assertThat(exchange.getIn().getHeader(TraceHeaderProcessor.TRACE_HEADER), notNullValue());

        TraceHeader header = exchange.getIn().getHeader(TraceHeaderProcessor.TRACE_HEADER, TraceHeader.class);
        assertThat(header.getTraceIdentifier().getCorrelationID(), is("da793349-b486-489a-9180-200789b7007f"));
        assertThat(header.getTraceIdentifier().getApplicationID(), is("ERP"));
        assertThat(header.getTraceIdentifier().getProcessID(), is("process123"));
    }

    @Test
    public void testValidateTraceIdNotAllowedValues() throws Exception {
        final TraceHeaderProcessor processor = new TraceHeaderProcessor(true,
                Collections.<TraceIdentifierValidator>singletonList(
                        new TraceIdentifierValidator() {
                            @Override
                            public boolean isValid(TraceIdentifier traceIdentifier) {
                                return false;
                            }
                        }));
        prepareRoute(processor);

        String request = "<notifyCollectionStepRequest xmlns=\"http://cleverbus.org/ws/NotificationsService-v1\">"
                + "            <traceIdentifier xmlns=\"http://cleverbus.org/ws/Common-v1\">"
                + "                <applicationID>ERP</applicationID>"
                + "                <timestamp>2013-09-27T10:23:34.6987744+02:00</timestamp>"
                + "                <correlationID>da793349-b486-489a-9180-200789b7007f</correlationID>"
                + "                <processID>process123</processID>"
                + "            </traceIdentifier>"
                + "            <externalCustomerAccountID>2065</externalCustomerAccountID>"
                + "            <eventDate>2013-09-26T00:00:00</eventDate>"
                + "            <stepType>1</stepType>"
                + "            <debtAmount>679</debtAmount>"
                + "            <invoiceNo>130000000378</invoiceNo>"
                + "            <variableSymbol>7002065001</variableSymbol>"
                + "        </notifyCollectionStepRequest>";

        // send message
        try {
            producer.sendBody(request);
            fail("request must be rejected since traceId does not have the valid value");
        } catch (Exception ex) {
            Throwable origExp = ex.getCause();
            assertThat(origExp, instanceOf(ValidationIntegrationException.class));
            assertThat(((ValidationIntegrationException) origExp).getError().getErrorCode(),
                    is(InternalErrorEnum.E120.getErrorCode()));
        }
    }

    @Test
    public void testValidateTraceIdAllowedValues() throws Exception {
        final TraceHeaderProcessor processor = new TraceHeaderProcessor(true,
                Collections.<TraceIdentifierValidator>unmodifiableList(Arrays.asList(
                        // all is invalid
                        new TraceIdentifierValidator() {
                            @Override
                            public boolean isValid(TraceIdentifier traceIdentifier) {
                                return false;
                            }
                        },
                        // all is valid
                        new TraceIdentifierValidator() {
                            @Override
                            public boolean isValid(TraceIdentifier traceIdentifier) {
                                return true;
                            }
                        }
                )));
        prepareRoute(processor);

        String request = "<notifyCollectionStepRequest xmlns=\"http://cleverbus.org/ws/NotificationsService-v1\">"
                + "            <traceIdentifier xmlns=\"http://cleverbus.org/ws/Common-v1\">"
                + "                <applicationID>ERP</applicationID>"
                + "                <timestamp>2013-09-27T10:23:34.6987744+02:00</timestamp>"
                + "                <correlationID>da793349-b486-489a-9180-200789b7007f</correlationID>"
                + "                <processID>process123</processID>"
                + "            </traceIdentifier>"
                + "            <externalCustomerAccountID>2065</externalCustomerAccountID>"
                + "            <eventDate>2013-09-26T00:00:00</eventDate>"
                + "            <stepType>1</stepType>"
                + "            <debtAmount>679</debtAmount>"
                + "            <invoiceNo>130000000378</invoiceNo>"
                + "            <variableSymbol>7002065001</variableSymbol>"
                + "        </notifyCollectionStepRequest>";

        // send message
        mock.expectedMessageCount(1);

        producer.sendBody(request);

        mock.assertIsSatisfied();

        Exchange exchange = mock.getExchanges().get(0);
        assertThat(exchange.getIn().getHeader(TraceHeaderProcessor.TRACE_HEADER), notNullValue());

    }

    @Test
    public void testParsingNoTraceHeader() throws Exception {
        prepareRoute(new TraceHeaderProcessor(false, null));

        String request = "<notifyCollectionStepRequest xmlns=\"http://cleverbus.org/ws/NotificationsService-v1\">"
                + "            <externalCustomerAccountID>2065</externalCustomerAccountID>"
                + "            <eventDate>2013-09-26T00:00:00</eventDate>"
                + "            <stepType>1</stepType>"
                + "        </notifyCollectionStepRequest>";

        mock.expectedMessageCount(1);

        // send message
        producer.sendBody(request);

        mock.assertIsSatisfied();

        Exchange exchange = mock.getExchanges().get(0);
        assertThat(exchange.getIn().getHeader(TraceHeaderProcessor.TRACE_HEADER), nullValue());
    }
}
