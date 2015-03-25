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

package org.cleverbus.core.common.directcall;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.cleverbus.core.AbstractCoreTest;
import org.cleverbus.test.ActiveRoutes;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Test suite for {@link DirectCallWsRoute}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@ActiveRoutes(classes = DirectCallWsRoute.class)
public class DirectCallWsRouteTest extends AbstractCoreTest {

    private static final String CALL_ID = "callId";

    @Produce(uri = "direct:test")
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    @Autowired
    private DirectCallRegistry callRegistry;

    @Before
    public void prepareRoutes() throws Exception {
        getCamelContext().getRouteDefinition(DirectCallWsRoute.ROUTE_ID_DIRECT_CALL)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        replaceFromWith("direct:test");

                        weaveById(DirectCallWsRoute.ROUTING_SLIP_ID).replace().to("mock:test");
                    }
                });
    }

    @Before
    public void prepareCallRegistry() {
        callRegistry.addParams(CALL_ID, new DirectCallParams("body", "uri", "senderRef", "soapAction", "header"));
    }

    @Test
    public void testExternalCall() throws Exception {
        mock.expectedMessageCount(1);

        producer.sendBodyAndHeader("empty", DirectCallWsRoute.CALL_ID_HEADER, CALL_ID);

        mock.assertIsSatisfied();

        // verify response
        Exchange exchange = mock.getExchanges().get(0);
        assertThat((String)exchange.getIn().getBody(), is("body"));

        try {
            callRegistry.getParams(CALL_ID);
            fail("There should not be params anymore.");
        } catch (IllegalStateException ex) {}
    }

    @Test
    public void testExternalCallWithWrongCallId() throws Exception {
        try {
            producer.sendBodyAndHeader("empty", DirectCallWsRoute.CALL_ID_HEADER, "23");
            fail("Call ID is wrong");
        } catch (CamelExecutionException ex) {
            assertThat(ex.getCause().getCause(), instanceOf(IllegalStateException.class));
        }
    }
}
