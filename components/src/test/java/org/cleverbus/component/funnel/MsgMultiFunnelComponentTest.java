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
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.apache.camel.component.mock.MockEndpoint.assertIsSatisfied;

/**
 * Test suite for {@link MsgFunnelComponent} with multi funnel value in {@link Message}.
 *
 * @author Radek Čermák [<a href="mailto:radek.cermak@cleverlance.com">radek.cermak@cleverlance.com</a>]
 * @since 2.0.4
 */
@Transactional
public class MsgMultiFunnelComponentTest extends AbstractComponentsDbTest {

    private static final String MSG_BODY = "some body";
    private static final String FUNNEL_VALUE = "774724557";
    private static final String DIFFERENT_FUNNEL_VALUE = "DIFFERENT_FUEL_VALUE";
    private static final String MULTI_FUNNEL_VALUE = "MULTI_FUNNEL_VALUE";
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
        firstMsg = MsgFunnelComponentTest.createMessage(FUNNEL_VALUE, MULTI_FUNNEL_VALUE);

        em.persist(firstMsg);
        em.flush();
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
    public void testMultiFunnelValue() throws Exception {
        mock.setExpectedMessageCount(0);

        Message msg = MsgFunnelComponentTest.createMessage(FUNNEL_VALUE, MULTI_FUNNEL_VALUE, DIFFERENT_FUNNEL_VALUE);
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

        Message msg = MsgFunnelComponentTest.createMessage(MULTI_FUNNEL_VALUE);
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

        Message msg = MsgFunnelComponentTest.createMessage("777123456", "777999111");

        // send message with different funnel value
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

        Message msg = MsgFunnelComponentTest.createMessage(FUNNEL_VALUE, DIFFERENT_FUNNEL_VALUE);
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

        Message msg = MsgFunnelComponentTest.createMessage(FUNNEL_VALUE, DIFFERENT_FUNNEL_VALUE);
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
    public void testFunnelForOwnFunnelValue() throws Exception {
        mock.setExpectedMessageCount(0);

        //message without funnel value
        Message msg = MsgFunnelComponentTest.createMessage(DIFFERENT_FUNNEL_VALUE);
        em.persist(msg);
        em.flush();

        // send message with setting funnel value in route => postpone it
        producerForFunnelValue.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        msg = em.find(Message.class, msg.getMsgId());
        Assert.assertNotNull(msg);
        Assert.assertThat(msg.getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
        Assert.assertEquals(2, msg.getFunnelValues().size());
        Assert.assertTrue(CollectionUtils.isEqualCollection(msg.getFunnelValues(),
                Arrays.asList(FUNNEL_VALUE, DIFFERENT_FUNNEL_VALUE)));
    }

    @Test
    public void testFunnelForOwnFunnelValueGuaranteed_Postponed() throws Exception {
        mock.setExpectedMessageCount(0);

        Message msg = MsgFunnelComponentTest.createMessage(FUNNEL_VALUE);
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
    public void testFunnelForOwnFunnelValueGuaranteed_Processing() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = MsgFunnelComponentTest.createMessage("777999888");
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
        Assert.assertEquals(2, msg.getFunnelValues().size());
        Assert.assertTrue(CollectionUtils.isEqualCollection(msg.getFunnelValues(),
                Arrays.asList("777999888", DIFFERENT_FUNNEL_VALUE)));
    }

    @Test
    public void testFunnelForOwnFunnelValueGuaranteed_firstMessage() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = MsgFunnelComponentTest.createMessage(DIFFERENT_FUNNEL_VALUE);
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
        Assert.assertEquals(2, msg.getFunnelValues().size());
        Assert.assertTrue(CollectionUtils.isEqualCollection(msg.getFunnelValues(),
                Arrays.asList(FUNNEL_VALUE, DIFFERENT_FUNNEL_VALUE)));
    }

    @Test
    public void testFunnelForOwnFunnelValueGuaranteed_postponeMessage() throws Exception {
        mock.setExpectedMessageCount(0);

        Message msg = MsgFunnelComponentTest.createMessage(DIFFERENT_FUNNEL_VALUE);
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
        Assert.assertEquals(2, msg.getFunnelValues().size());
        Assert.assertTrue(CollectionUtils.isEqualCollection(msg.getFunnelValues(),
                Arrays.asList(FUNNEL_VALUE, DIFFERENT_FUNNEL_VALUE)));
    }

    @Test
    public void testFunnelValueGuaranteed_multiFunnelMessage() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msgFirst = MsgFunnelComponentTest.createMessage(MULTI_FUNNEL_VALUE);
        msgFirst.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), 100)); // be after "first" message
        em.persist(msgFirst);
        em.flush();

        //send first message
        producerForGuaranteed.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msgFirst);

        Message msgSecond = MsgFunnelComponentTest.createMessage(FUNNEL_VALUE, DIFFERENT_FUNNEL_VALUE);
        msgSecond.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), 150)); // be after "first" message
        em.persist(msgSecond);
        em.flush();

        //send second message
        producerForGuaranteed.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msgSecond);

        Message msgThird = MsgFunnelComponentTest.createMessage(DIFFERENT_FUNNEL_VALUE);
        msgThird.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), 200)); // be after "first" message
        em.persist(msgThird);
        em.flush();

        //send third message
        producerForGuaranteed.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msgThird);

        Message msgFourth = MsgFunnelComponentTest.createMessage("777111222");
        msgFourth.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), 250)); // be after "first" message
        em.persist(msgFourth);
        em.flush();

        //send fourth message
        producerForGuaranteed.sendBodyAndHeader(MSG_BODY, AsynchConstants.MSG_HEADER, msgFourth);

        assertIsSatisfied(mock);

        msgFirst = em.find(Message.class, msgFirst.getMsgId());
        Assert.assertNotNull(msgFirst);
        Assert.assertThat(msgFirst.getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
        Assert.assertEquals(1, msgFirst.getFunnelValues().size());
        Assert.assertTrue(CollectionUtils.isEqualCollection(msgFirst.getFunnelValues(),
                Collections.singletonList(MULTI_FUNNEL_VALUE)));

        msgSecond = em.find(Message.class, msgSecond.getMsgId());
        Assert.assertNotNull(msgSecond);
        Assert.assertThat(msgSecond.getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
        Assert.assertEquals(2, msgSecond.getFunnelValues().size());
        Assert.assertTrue(CollectionUtils.isEqualCollection(msgSecond.getFunnelValues(),
                Arrays.asList(FUNNEL_VALUE, DIFFERENT_FUNNEL_VALUE)));

        msgThird = em.find(Message.class, msgThird.getMsgId());
        Assert.assertNotNull(msgThird);
        Assert.assertThat(msgThird.getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
        Assert.assertEquals(1, msgThird.getFunnelValues().size());
        Assert.assertTrue(CollectionUtils.isEqualCollection(msgThird.getFunnelValues(),
                Collections.singletonList(DIFFERENT_FUNNEL_VALUE)));

        msgFourth = em.find(Message.class, msgFourth.getMsgId());
        Assert.assertNotNull(msgFourth);
        Assert.assertThat(msgFourth.getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));
        Assert.assertEquals(1, msgFourth.getFunnelValues().size());
        Assert.assertTrue(CollectionUtils.isEqualCollection(msgFourth.getFunnelValues(),
                Collections.singletonList("777111222")));
    }
}
