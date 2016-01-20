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

package org.cleverbus.component.funnel;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.api.route.AbstractBasicRoute;
import org.cleverbus.component.AbstractComponentsDbTest;
import org.cleverbus.test.EntityTypeTestEnum;
import org.cleverbus.test.ExternalSystemTestEnum;
import org.cleverbus.test.ServiceTestEnum;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static org.apache.camel.component.mock.MockEndpoint.assertIsSatisfied;


/**
 * Test suite for {@link MsgFunnelComponent}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@Transactional
public class MsgFunnelComponentTest extends AbstractComponentsDbTest {

    private static final String MSG_BODY = "some body";
    private static final String FUNNEL_VALUE = "774724557";
    private static final String DIFFERENT_FUNNEL_VALUE = "DIFFERENT_FUEL_VALUE";
    private static final String FUNNEL_ID = "myFunnelId";

    @Produce(uri = "direct:start")
    private ProducerTemplate producer;

    @Produce(uri = "direct:startGuaranteed")
    private ProducerTemplate producerForGuaranteed;

    @Produce(uri = "direct:startGuaranteedWithoutFailed")
    private ProducerTemplate producerForGuaranteedWithoutFailed;

    @Produce(uri = "direct:startFunnelValue")
    private ProducerTemplate producerForFunnelValue;

    @Produce(uri = "direct:startFunnelValueGuaranteed")
    private ProducerTemplate producerForFunnelValueGuaranteed;

    @Produce(uri = "direct:startDifferentFunnelValueGuaranteed")
    private ProducerTemplate producerForDifferentFunnelValueGuaranteed;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    private Message firstMsg;

    @Before
    public void prepareMessage() throws Exception {
        firstMsg = createMessage(FUNNEL_VALUE);

        em.persist(firstMsg);
        em.flush();
    }

    protected static Message createMessage(String... funnelValue) {
        Date currDate = new Date();

        Message msg = new Message();
        msg.setState(MsgStateEnum.PROCESSING);
        msg.setMsgTimestamp(currDate);
        msg.setReceiveTimestamp(currDate);
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setCorrelationId(UUID.randomUUID().toString());
        msg.setStartProcessTimestamp(currDate);

        msg.setService(ServiceTestEnum.CUSTOMER);
        msg.setOperationName("setCustomer");
        msg.setPayload("payload");
        msg.setLastUpdateTimestamp(currDate);
        msg.setObjectId("objectID");
        msg.setEntityType(EntityTypeTestEnum.ACCOUNT);
        if (funnelValue != null && funnelValue.length != 0) {
            msg.setFunnelValues(Arrays.asList(funnelValue));
        }
        msg.setFunnelComponentId(FUNNEL_ID);

        return msg;
    }

    @Before
    public void prepareRoutes() throws Exception {
        RouteBuilder defaultRoute = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from("direct:start")
                    .to("msg-funnel:default?idleInterval=50&id=" + FUNNEL_ID)
                    .to("mock:test");
            }
        };

        getCamelContext().addRoutes(defaultRoute);

        RouteBuilder guaranteedRoute = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from("direct:startGuaranteed")
                    .to("msg-funnel:default?idleInterval=50&guaranteedOrder=true&id=" + FUNNEL_ID)
                    .to("mock:test");
            }
        };

        getCamelContext().addRoutes(guaranteedRoute);

        RouteBuilder guaranteedWithoutFailedRoute = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from("direct:startGuaranteedWithoutFailed")
                    .to("msg-funnel:default?idleInterval=50&guaranteedOrder=true&excludeFailedState=true&id=" + FUNNEL_ID)
                    .to("mock:test");
            }
        };

        getCamelContext().addRoutes(guaranteedWithoutFailedRoute);

        RouteBuilder funnelValueRoute = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from("direct:startFunnelValue")
                        .to("msg-funnel:default?idleInterval=50&id=" + FUNNEL_ID + "&funnelValue=" + FUNNEL_VALUE)
                        .to("mock:test");
            }
        };

        getCamelContext().addRoutes(funnelValueRoute);

        RouteBuilder guaranteedFunnelValueRoute = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from("direct:startFunnelValueGuaranteed")
                        .to("msg-funnel:default?idleInterval=50&guaranteedOrder=true&id=" + FUNNEL_ID
                                + "&funnelValue=" + FUNNEL_VALUE)
                        .to("mock:test");
            }
        };

        getCamelContext().addRoutes(guaranteedFunnelValueRoute);

        RouteBuilder guaranteedDifferentFunnelValueRoute = new AbstractBasicRoute() {
            @Override
            public void doConfigure() throws Exception {
                from("direct:startDifferentFunnelValueGuaranteed")
                        .to("msg-funnel:default?idleInterval=50&guaranteedOrder=true&id=" + FUNNEL_ID
                                + "&funnelValue=" + DIFFERENT_FUNNEL_VALUE)
                        .to("mock:test");
            }
        };

        getCamelContext().addRoutes(guaranteedDifferentFunnelValueRoute);
    }

    @Test
    public void testFunnel() throws Exception {
        mock.setExpectedMessageCount(0);

        Message msg = createMessage(FUNNEL_VALUE);
        em.persist(msg);
        em.flush();

        // send message with same funnel value => postpone it
        producer.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
    }

    @Test
    public void testFunnel_waitingForResponse() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = createMessage(FUNNEL_VALUE);
        msg.setStartProcessTimestamp(DateUtils.addSeconds(new Date(), -MsgFunnelEndpoint.DEFAULT_IDLE_INTERVAL - 100));
        msg.setState(MsgStateEnum.WAITING_FOR_RES);
        em.persist(msg);
        em.flush();

        producer.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(),
                CoreMatchers.is(MsgStateEnum.WAITING_FOR_RES));
    }

    @Test
    public void testFunnel_noFilter() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = createMessage("777123456");

        // send message with different funnel value
        producer.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);

        mock.assertIsSatisfied();
    }

    @Test
    public void testWithoutFunnel() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = createMessage(null);

        // input message doesn't have funnel value
        producer.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);

        mock.assertIsSatisfied();
    }

    @Test
    public void testFunnelForGuaranteedOrder_onlyCurrentMessage() throws Exception {
        mock.setExpectedMessageCount(1);

        // send one message only
        producerForGuaranteed.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, firstMsg);

        assertIsSatisfied(mock);
    }

    @Test
    public void testFunnelForGuaranteedOrder_firstMessage() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = createMessage(FUNNEL_VALUE);
        msg.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), -100)); // be before "first" message
        msg.setState(MsgStateEnum.PROCESSING);
        em.persist(msg);
        em.flush();

        // send message has "msgTimestamp" before another processing message
        producerForGuaranteed.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));
    }

    @Test
    public void testFunnelForGuaranteedOrder_postponeMessage() throws Exception {
        mock.setExpectedMessageCount(0);

        Message msg = createMessage(FUNNEL_VALUE);
        msg.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), 100)); // be after "first" message
        msg.setState(MsgStateEnum.PROCESSING);
        em.persist(msg);
        em.flush();

        // send message has "msgTimestamp" after another processing message => postpone it
        producerForGuaranteed.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
    }

    @Test
    public void testFunnelForGuaranteedOrder_excludeFailedState() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = createMessage(FUNNEL_VALUE);
        msg.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), 100)); // be after "first" message, but FAILED
        msg.setState(MsgStateEnum.FAILED);
        em.persist(msg);
        em.flush();

        // send message has "msgTimestamp" after another processing message but in FAILED state
        //  that is excluded => continue
        producerForGuaranteedWithoutFailed.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.FAILED));
    }

    @Test
    public void testFunnelForOwnFunnelValue() throws Exception {
        mock.setExpectedMessageCount(0);

        //message without funnel value
        Message msg = createMessage(null);
        em.persist(msg);
        em.flush();

        // send message with setting funnel value in route => postpone it
        producerForFunnelValue.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        msg = em.find(Message.class, msg.getMsgId());
        Assert.assertNotNull(msg);
        Assert.assertThat(msg.getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
        Assert.assertTrue(msg.getFunnelValues().contains(FUNNEL_VALUE));
    }

    @Test
    public void testFunnelForOwnFunnelValue_withValue() throws Exception {
        mock.setExpectedMessageCount(0);

        //message without different funnel value
        Message msg = createMessage(DIFFERENT_FUNNEL_VALUE);
        em.persist(msg);
        em.flush();

        // send message with setting funnel value in route => postpone it
        producerForFunnelValue.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        msg = em.find(Message.class, msg.getMsgId());
        Assert.assertNotNull(msg);
        Assert.assertThat(msg.getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
        Assert.assertEquals(2, msg.getFunnelValues().size());
        Assert.assertTrue(msg.getFunnelValues().contains(FUNNEL_VALUE));
        Assert.assertTrue(msg.getFunnelValues().contains(DIFFERENT_FUNNEL_VALUE));
    }

    @Test
    public void testFunnelForOwnFunnelValueGuaranteed_Postponed() throws Exception {
        mock.setExpectedMessageCount(0);

        Message msg = createMessage(FUNNEL_VALUE);
        msg.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), 100)); // be after "first" message
        msg.setState(MsgStateEnum.PROCESSING);
        em.persist(msg);
        em.flush();

        // input message to route with different funnel value
        producerForDifferentFunnelValueGuaranteed.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);

        mock.assertIsSatisfied();

        msg = em.find(Message.class, msg.getMsgId());
        Assert.assertNotNull(msg);
        Assert.assertThat(msg.getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
        Assert.assertTrue(CollectionUtils.isEqualCollection(msg.getFunnelValues(),
                Arrays.asList(FUNNEL_VALUE, DIFFERENT_FUNNEL_VALUE)));
    }

    @Test
    public void testFunnelForOwnFunnelValueGuaranteedNoValue_Processing() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = createMessage(null);
        msg.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), 100)); // be after "first" message
        msg.setState(MsgStateEnum.PROCESSING);
        em.persist(msg);
        em.flush();

        // input message to route with different funnel value
        producerForDifferentFunnelValueGuaranteed.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);

        mock.assertIsSatisfied();

        msg = em.find(Message.class, msg.getMsgId());
        Assert.assertNotNull(msg);
        Assert.assertThat(msg.getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));
        Assert.assertTrue(msg.getFunnelValues().contains(DIFFERENT_FUNNEL_VALUE));
    }

    @Test
    public void testFunnelForOwnFunnelValueGuaranteed_firstMessage() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = createMessage(null);
        msg.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), -100)); // be before "first" message
        msg.setState(MsgStateEnum.PROCESSING);
        em.persist(msg);
        em.flush();

        // send message has "msgTimestamp" before another processing message
        producerForFunnelValueGuaranteed.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        msg = em.find(Message.class, msg.getMsgId());
        Assert.assertNotNull(msg);
        Assert.assertThat(msg.getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));
        Assert.assertTrue(msg.getFunnelValues().contains(FUNNEL_VALUE));
    }

    @Test
    public void testFunnelForOwnFunnelValueGuaranteed_postponeMessage() throws Exception {
        mock.setExpectedMessageCount(0);

        Message msg = createMessage(null);
        msg.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), 100)); // be after "first" message
        msg.setState(MsgStateEnum.PROCESSING);
        em.persist(msg);
        em.flush();

        // send message has "msgTimestamp" after another processing message => postpone it
        producerForFunnelValueGuaranteed.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        msg = em.find(Message.class, msg.getMsgId());
        Assert.assertNotNull(msg);
        Assert.assertThat(msg.getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
        Assert.assertTrue(msg.getFunnelValues().contains(FUNNEL_VALUE));
    }
}
