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

package org.cleverbus.core.common.contextcall;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.cleverbus.api.exception.NoDataFoundException;
import org.cleverbus.core.AbstractCoreTest;
import org.cleverbus.test.ActiveRoutes;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;


/**
 * Test suite for {@link ContextCallRoute}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@ActiveRoutes(classes = ContextCallRoute.class)
@ContextConfiguration(locations = {"classpath:/org/cleverbus/core/camel/common/contextcall/test-context.xml"})
public class ContextCallRouteTest extends AbstractCoreTest {

    private static final String CALL_ID = "callId";

    @Produce(uri = "direct:test")
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    @Autowired
    private ContextCallRegistry callRegistry;

    @Before
    public void prepareRoutes() throws Exception {
        getCamelContext().getRouteDefinition(ContextCallRoute.ROUTE_ID_CONTEXT_CALL)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        replaceFromWith("direct:test");

                        weaveAddLast().to("mock:test");
                    }
                });
    }

    @Before
    public void prepareCallRegistry() {
        callRegistry.addParams(CALL_ID, new ContextCallParams(TestService.class, "getGreeting", "Petr"));
    }

    @Test
    public void testCallWithParams() throws Exception {
        mock.expectedMessageCount(1);

        producer.sendBodyAndHeader("empty", ContextCallRoute.CALL_ID_HEADER, CALL_ID);

        mock.assertIsSatisfied();

        // verify response
        assertThat(callRegistry.getResponse(CALL_ID, String.class), is("Hello Petr"));
    }

    @Test
    public void testCallWithNoParams() throws Exception {
        String callId = "callId2";
        callRegistry.addParams(callId, new ContextCallParams(TestService.class, "getDefaultGreeting"));

        mock.expectedMessageCount(1);

        producer.sendBodyAndHeader("empty", ContextCallRoute.CALL_ID_HEADER, callId);

        mock.assertIsSatisfied();

        // verify response
        assertThat(callRegistry.getResponse(callId, String.class), is("Hello CleverBus"));
    }

    @Test
    public void testCallWithWrongCallId() throws Exception {
        try {
            producer.sendBodyAndHeader("empty", ContextCallRoute.CALL_ID_HEADER, "23");
            fail("Call ID is wrong");
        } catch (CamelExecutionException ex) {
            assertThat(ex.getCause().getCause(), instanceOf(NoDataFoundException.class));
        }
    }
}
