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

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.commons.lang.time.DateUtils;
import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.core.AbstractCoreDbTest;
import org.cleverbus.test.ActiveRoutes;
import org.cleverbus.test.EntityTypeTestEnum;
import org.cleverbus.test.ExternalSystemTestEnum;
import org.cleverbus.test.ServiceTestEnum;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static org.apache.camel.component.mock.MockEndpoint.assertIsSatisfied;


/**
 * Test suite for {@link AsynchInMessageRoute} - specific for guaranteed order delivery.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@ActiveRoutes(classes = AsynchInMessageRoute.class)
@Transactional
public class AsynchInMessageRouteGuaranteedOrderTest extends AbstractCoreDbTest {

    @Produce(uri = AsynchInMessageRoute.URI_GUARANTEED_ORDER_ROUTE)
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    private static final String FUNNEL_VALUE = "774724557";

    private static final String FUNNEL_VALUE_TWO = "FUNNEL_VALUE_TWO";

    private static final String FUNNEL_VALUE_THREE = "FUNNEL_VALUE_THREE";

    private Message firstMsg;

    @Before
    public void prepareMessage() throws Exception {
        firstMsg = createMessage(FUNNEL_VALUE, FUNNEL_VALUE_THREE);
        firstMsg.setGuaranteedOrder(true);

        em.persist(firstMsg);
        em.flush();
    }

    @Before
    public void prepareRoutes() throws Exception {
        getCamelContext().getRouteDefinition(AsynchInMessageRoute.ROUTE_ID_GUARANTEED_ORDER)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveAddLast().to("mock:test");
                    }
                });
    }

    private Message createMessage(@Nullable String... funnelValue) {
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

        return msg;
    }

    @Test
    public void testNoGuaranteedOrder() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = createMessage(FUNNEL_VALUE);
        msg.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), 100)); // be after "first" message
        msg.setState(MsgStateEnum.PROCESSING);
        em.persist(msg);
        em.flush();

        // send message has "msgTimestamp" after another processing message => postpone it
        producer.sendBody(msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));
        Assert.assertThat(msg.getProcessingPriority(), CoreMatchers.is(AsynchInMessageRoute.NEW_MSG_PRIORITY));
    }

    @Test
    public void testGuaranteedOrder_onlyCurrentMessage() throws Exception {
        mock.setExpectedMessageCount(1);

        // send one message only
        producer.sendBodyAndHeader(firstMsg, AsynchConstants.MSG_HEADER, firstMsg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, firstMsg.getMsgId()).getState(),
                CoreMatchers.is(MsgStateEnum.PROCESSING));
    }

    @Test
    public void testGuaranteedOrder_firstMessage() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = createMessage(FUNNEL_VALUE);
        msg.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), -100)); // be before "first" message
        msg.setState(MsgStateEnum.PROCESSING);
        msg.setGuaranteedOrder(true);
        em.persist(msg);
        em.flush();

        // send message has "msgTimestamp" before another processing message
        producer.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));
    }

    @Test
    public void testGuaranteedOrder_postponeMessage() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = createMessage(FUNNEL_VALUE);
        msg.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), 100)); // be after "first" message
        msg.setState(MsgStateEnum.PROCESSING);
        msg.setGuaranteedOrder(true);
        em.persist(msg);
        em.flush();

        // send message has "msgTimestamp" after another processing message => postpone it
        producer.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
    }

    @Test
    public void testGuaranteedOrder_firstMultiFunnelMessage() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = createMessage(FUNNEL_VALUE, FUNNEL_VALUE_TWO);
        msg.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), -100)); // be before "first" message
        msg.setState(MsgStateEnum.PROCESSING);
        msg.setGuaranteedOrder(true);
        em.persist(msg);
        em.flush();

        // send message has "msgTimestamp" before another processing message
        producer.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));
    }

    @Test
    public void testGuaranteedOrder_postponeMultiFunnelMessage() throws Exception {
        Message msg = createMessage(FUNNEL_VALUE, FUNNEL_VALUE_TWO);
        msg.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), 100)); // be after "first" message
        msg.setState(MsgStateEnum.PROCESSING);
        msg.setGuaranteedOrder(true);
        em.persist(msg);
        em.flush();

        mock.setExpectedMessageCount(1);

        // send message has "msgTimestamp" after another processing message => postpone it
        producer.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));

        msg = createMessage(FUNNEL_VALUE);
        msg.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), 200)); // be after "first" message
        msg.setState(MsgStateEnum.PROCESSING);
        msg.setGuaranteedOrder(true);
        em.persist(msg);
        em.flush();

        mock.setExpectedMessageCount(2);

        // send message has "msgTimestamp" after another processing message => postpone it
        producer.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));

        msg = createMessage(FUNNEL_VALUE_THREE);
        msg.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), 300)); // be after "first" message
        msg.setState(MsgStateEnum.PROCESSING);
        msg.setGuaranteedOrder(true);
        em.persist(msg);
        em.flush();

        mock.setExpectedMessageCount(3);

        // send message has "msgTimestamp" after another processing message => postpone it
        producer.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
    }

    @Test
    public void testGuaranteedOrder_proccesingMultiFunnelMessage() throws Exception {
        mock.setExpectedMessageCount(1);

        Message msg = createMessage(FUNNEL_VALUE_TWO);
        msg.setMsgTimestamp(DateUtils.addSeconds(firstMsg.getMsgTimestamp(), 100)); // be after "first" message
        msg.setState(MsgStateEnum.PROCESSING);
        msg.setGuaranteedOrder(true);
        em.persist(msg);
        em.flush();

        // send message has "msgTimestamp" after another processing message => postpone it
        producer.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        assertIsSatisfied(mock);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));
    }
}
