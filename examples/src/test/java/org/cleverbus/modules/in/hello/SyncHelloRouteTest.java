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

package org.cleverbus.modules.in.hello;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.cleverbus.common.Tools;
import org.cleverbus.modules.AbstractModulesTest;
import org.cleverbus.modules.in.hello.model.SyncHelloResponse;
import org.cleverbus.test.ActiveRoutes;
import org.cleverbus.test.route.TestWsUriBuilder;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Test suite for {@link SyncHelloRoute}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@ActiveRoutes(classes = SyncHelloRoute.class)
public class SyncHelloRouteTest extends AbstractModulesTest {

    @Produce(uri = TestWsUriBuilder.URI_WS_IN + "syncHelloRequest")
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    /**
     * Checking successful calling.
     */
    @Test
    public void testSyncHelloRoute() throws Exception {
        String xml = "<syncHelloRequest xmlns=\"http://cleverbus.org/ws/HelloService-v1\">"
                + "    <name>Mr. Parker</name>"
                + "</syncHelloRequest>";

        String output = producer.requestBody((Object)xml, String.class);
        SyncHelloResponse res = Tools.unmarshalFromXml(output, SyncHelloResponse.class);

        assertThat(res, notNullValue());
        assertThat(res.getGreeting(), is("Hello Mr. Parker"));
    }
}
