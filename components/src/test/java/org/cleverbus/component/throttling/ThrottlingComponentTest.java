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

package org.cleverbus.component.throttling;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.cleverbus.api.entity.Message;
import org.cleverbus.api.route.AbstractBasicRoute;
import org.cleverbus.component.AbstractComponentsTest;
import org.cleverbus.test.ExternalSystemTestEnum;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


/**
 * Test suite for {@link ThrottlingComponent}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@ContextConfiguration(locations = {"classpath:/org/cleverbus/component/throttling/test_throttling_conf.xml"})
public class ThrottlingComponentTest extends AbstractComponentsTest {

    @Produce(uri = "direct:start")
    private ProducerTemplate producer;

    @Test
    public void testWrongUri_wrongRequestType() throws Exception {
        try {
            callComponent("throttling:wrongReqType");
        } catch (Exception ex) {
            assertThat(ExceptionUtils.getRootCause(ex).getMessage(),
                    is("request type must have one of the following values: 'sync' or 'async'"));
        }
    }

    @Test
    public void testWrongUri_noOperationNameForSyncRequestType() throws Exception {
        try {
            callComponent("throttling:sync");
        } catch (Exception ex) {
            assertThat(ExceptionUtils.getRootCause(ex).getMessage(),
                    is("operation name is mandatory for 'sync' request type"));
        }
    }

    @Test
    public void testSuccessfulCall_sync() throws Exception {
        callComponent("throttling:sync:sendSms");
    }

    @Test
    public void testSuccessfulCall_async() throws Exception {
        RouteBuilder testRoute = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from("direct:start")
                    .to("throttling:async");
            }
        };

        getCamelContext().addRoutes(testRoute);

        // send message
        Message msg = new Message();
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setOperationName("createCustomer");

        producer.sendBody(msg);
    }

    private void callComponent(final String uri) throws Exception {
        RouteBuilder testRoute = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from("direct:start")
                    .to(uri);
            }
        };

        getCamelContext().addRoutes(testRoute);

        // send message
        producer.sendBody("someBody");
    }
}
