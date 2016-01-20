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

package org.cleverbus.component.asynchchild;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.entity.BindingTypeEnum;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.api.route.AbstractBasicRoute;
import org.cleverbus.component.AbstractComponentsDbTest;
import org.cleverbus.test.EntityTypeTestEnum;
import org.cleverbus.test.ExternalSystemTestEnum;
import org.cleverbus.test.ServiceTestEnum;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


/**
 * Test suite for {@link AsynchChildComponent}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@Transactional
public class AsynchChildComponentTest extends AbstractComponentsDbTest {

    private static final String MSG_BODY = "some body";

    @Produce(uri = "direct:start")
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    private Message msg;

    @Before
    public void prepareMessage() throws Exception {
        Date currDate = new Date();

        msg = new Message();
        msg.setState(MsgStateEnum.PROCESSING);
        msg.setMsgTimestamp(currDate);
        msg.setReceiveTimestamp(currDate);
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setCorrelationId("123-456");

        msg.setService(ServiceTestEnum.CUSTOMER);
        msg.setOperationName("setCustomer");
        msg.setPayload("payload");
        msg.setLastUpdateTimestamp(currDate);
        msg.setObjectId("objectID");
        msg.setEntityType(EntityTypeTestEnum.ACCOUNT);

        em.persist(msg);
        em.flush();
    }

    @Test
    public void testWrongUri_noService() throws Exception {
        try {
            callComponent("asynch-child:createCustomer?bindingType=HARD&correlationId=566&sourceSystem=CRM");
        } catch (Exception ex) {
            assertThat(ExceptionUtils.getRootCause(ex).getMessage(),
                    is("Service name can't be empty for asynch-child component"));
        }
    }

    @Test
    public void testWrongUri_noOperationName() throws Exception {
        try {
            callComponent("asynch-child:service");
        } catch (Exception ex) {
            assertThat(ExceptionUtils.getRootCause(ex).getMessage(),
                    is("Service name can't be empty for asynch-child component"));
        }
    }

    @Test
    public void testWrongUri_wrongBindingType() throws Exception {
        try {
            callComponent("asynch-child:customer:createCustomer?bindingType=MEDIUM");
        } catch (Exception ex) {
            assertThat(ExceptionUtils.getRootCause(ex).getMessage(),
                    endsWith("org.cleverbus.api.entity.BindingTypeEnum.MEDIUM"));
        }
    }

    @Test
    public void testCreateChild() throws Exception {
        createAsynchRoute();

        mock.setExpectedMessageCount(1);

        callComponent("asynch-child:customer:createCustomer?bindingType=HARD&correlationId=566&sourceSystem=CRM"
                + "&objectId=111&funnelValue=val");

        mock.assertIsSatisfied();

        // verify message
        Message asynchMsg = mock.getExchanges().get(0).getIn().getBody(Message.class);
        assertThat(asynchMsg, notNullValue());
        assertThat(asynchMsg.getParentMsgId(), is(msg.getMsgId()));
        assertThat(asynchMsg.getParentBindingType(), is(BindingTypeEnum.HARD));
        assertThat(asynchMsg.getCorrelationId(), is("566"));
        assertThat(asynchMsg.getPayload(), is(MSG_BODY));
        assertThat(asynchMsg.getSourceSystem().getSystemName(), is("CRM"));
        assertThat(asynchMsg.getService().getServiceName(), is("customer"));
        assertThat(asynchMsg.getOperationName(), is("createCustomer"));
        assertThat(asynchMsg.getObjectId(), is("111"));
        assertTrue(asynchMsg.getFunnelValues().contains("val"));

        Message msgDB = em.find(Message.class, asynchMsg.getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.PROCESSING));
    }

    @Test
    public void testCreateChild_softBinding() throws Exception {
        createAsynchRoute();

        mock.setExpectedMessageCount(1);

        callComponent("asynch-child:customer:createCustomer?bindingType=SOFT&correlationId=566");

        mock.assertIsSatisfied();

        // verify message
        Message asynchMsg = mock.getExchanges().get(0).getIn().getBody(Message.class);
        assertThat(asynchMsg, notNullValue());
        assertThat(asynchMsg.getParentMsgId(), is(msg.getMsgId()));
        assertThat(asynchMsg.getParentBindingType(), is(BindingTypeEnum.SOFT));
        assertThat(asynchMsg.getCorrelationId(), is("566"));
        assertThat(asynchMsg.getPayload(), is(MSG_BODY));
        assertThat(asynchMsg.getSourceSystem().getSystemName(), is("CRM"));
        assertThat(asynchMsg.getService().getServiceName(), is("customer"));
        assertThat(asynchMsg.getOperationName(), is("createCustomer"));

        Message msgDB = em.find(Message.class, asynchMsg.getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.PROCESSING));
    }

    @Test
    public void testCreateChildFromSyncRoute() throws Exception {
        createAsynchRoute();

        mock.setExpectedMessageCount(1);

        RouteBuilder testRoute = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from("direct:start")
                    .to("asynch-child:customer:createCustomer");
            }
        };

        getCamelContext().addRoutes(testRoute);

        // send message
        producer.sendBody(MSG_BODY);

        mock.assertIsSatisfied();

        // verify message
        Message asynchMsg = mock.getExchanges().get(0).getIn().getBody(Message.class);
        assertThat(asynchMsg, notNullValue());
        assertThat(asynchMsg.getParentMsgId(), nullValue());
        assertThat(asynchMsg.getParentBindingType(), nullValue());
        assertThat(asynchMsg.getCorrelationId(), notNullValue());
        assertThat(asynchMsg.getPayload(), is(MSG_BODY));
        assertThat(asynchMsg.getSourceSystem().getSystemName(), is(AsynchChildProducer.DEFAULT_EXTERNAL_SYSTEM));
        assertThat(asynchMsg.getService().getServiceName(), is("customer"));
        assertThat(asynchMsg.getOperationName(), is("createCustomer"));

        Message msgDB = em.find(Message.class, asynchMsg.getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.PROCESSING));
    }

    private void createAsynchRoute() throws Exception {
        RouteBuilder asynchRoute = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from(AsynchConstants.URI_ASYNC_MSG)
                        .to("mock:test");
            }
        };

        mock.setExpectedMessageCount(1);

        getCamelContext().addRoutes(asynchRoute);
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
        producer.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);
    }
}
