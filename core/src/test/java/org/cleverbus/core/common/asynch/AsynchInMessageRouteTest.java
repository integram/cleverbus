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

import org.apache.camel.*;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.commons.lang.time.DateUtils;
import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.asynch.model.CallbackResponse;
import org.cleverbus.api.asynch.model.ConfirmationTypes;
import org.cleverbus.api.asynch.model.TraceIdentifier;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.api.exception.StoppingException;
import org.cleverbus.api.exception.ThrottlingExceededException;
import org.cleverbus.core.AbstractCoreDbTest;
import org.cleverbus.test.ActiveRoutes;
import org.cleverbus.test.ExternalSystemTestEnum;
import org.cleverbus.test.ServiceTestEnum;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


/**
 * Test suite for {@link AsynchInMessageRoute}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@ActiveRoutes(classes = AsynchInMessageRoute.class)
public class AsynchInMessageRouteTest extends AbstractCoreDbTest {

    private static final String FUNNEL_VALUE_ONE = "774724557";

    private static final String FUNNEL_VALUE_TWO = "FUNNEL_VALUE_TWO";

    @Produce(uri = AsynchConstants.URI_ASYNCH_IN_MSG)
    private ProducerTemplate producer;

    @Produce(uri = AsynchInMessageRoute.URI_GUARANTEED_ORDER_ROUTE)
    private ProducerTemplate guaranteedProducer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    @Before
    public void prepareData() {
        getHeaders().put(AsynchConstants.SERVICE_HEADER, ServiceTestEnum.CUSTOMER);
        getHeaders().put(AsynchConstants.OPERATION_HEADER, "setCustomer");
        getHeaders().put(AsynchConstants.OBJECT_ID_HEADER, "567");
    }

    @Test
    public void testResponseOK() throws Exception {
        getCamelContext().getRouteDefinition(AsynchInMessageRoute.ROUTE_ID_ASYNC)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveAddLast().to("mock:test");
                    }
                });

        mock.expectedMessageCount(1);

        producer.sendBodyAndHeaders("bodyContent", getHeaders());

        mock.assertIsSatisfied();

        // verify response
        Exchange exchange = mock.getExchanges().get(0);
        assertThat(exchange.getIn().getBody(), instanceOf(CallbackResponse.class));
        CallbackResponse callbackResponse = (CallbackResponse) exchange.getIn().getBody();
        assertThat(callbackResponse.getStatus(), is(ConfirmationTypes.OK));

        // verify DB
        int msgCount = JdbcTestUtils.countRowsInTable(getJdbcTemplate(), "message");
        assertThat(msgCount, is(1));

        final List<Message> messages = getJdbcTemplate().query("select * from message", new RowMapper<Message>() {
            @Override
            public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
                TraceIdentifier traceIdentifier = getTraceHeader().getTraceIdentifier();

                // verify row values
                assertThat(rs.getLong("msg_id"), notNullValue());
                assertThat(rs.getString("correlation_id"), is(traceIdentifier.getCorrelationID()));
                assertThat((int)rs.getShort("failed_count"), is(0));
                assertThat(rs.getString("failed_desc"), nullValue());
                assertThat(rs.getString("failed_error_code"), nullValue());
                assertThat(rs.getTimestamp("last_update_timestamp"), notNullValue());
                assertThat(rs.getTimestamp("start_process_timestamp"), notNullValue());
                assertThat(rs.getTimestamp("msg_timestamp").compareTo(traceIdentifier.getTimestamp().toDate()), is(0));
                assertThat(rs.getString("object_id"), is(getHeaders().get(AsynchConstants.OBJECT_ID_HEADER)));
                assertThat(rs.getString("operation_name"), is(getHeaders().get(AsynchConstants.OPERATION_HEADER)));
                assertThat(rs.getString("payload"), is("bodyContent"));
                assertThat(rs.getTimestamp("receive_timestamp"), notNullValue());
                assertThat(rs.getString("service"), is(ServiceTestEnum.CUSTOMER.getServiceName()));
                assertThat(rs.getString("source_system"), is(ExternalSystemTestEnum.CRM.getSystemName()));
                assertThat(MsgStateEnum.valueOf(rs.getString("state")), is(MsgStateEnum.PROCESSING));
                assertThat(rs.getString("parent_binding_type"), nullValue());
                assertThat(rs.getString("funnel_component_id"), nullValue());
                assertThat(rs.getLong("parent_msg_id"), is(0L));
                assertThat(rs.getBoolean("guaranteed_order"), is(false));
                assertThat(rs.getBoolean("exclude_failed_state"), is(false));

                return new Message();
            }
        });

        assertThat(messages.size(), is(1));
    }

    @Test
    public void testResponseFAIL_noServiceHeader() throws Exception {
        getCamelContext().getRouteDefinition(AsynchInMessageRoute.ROUTE_ID_ASYNC)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveAddLast().to("mock:test");
                    }
                });

        mock.expectedMessageCount(1);

        // clear mandatory header => FAIL response
        getHeaders().remove(AsynchConstants.SERVICE_HEADER);
        producer.sendBodyAndHeaders("bodyContent", getHeaders());

        assertErrorResponse("PredicateValidationException");
    }

    @Test
    public void testResponseFAIL_duplicateMsg() throws Exception {
        getCamelContext().getRouteDefinition(AsynchInMessageRoute.ROUTE_ID_ASYNC)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveAddLast().to("mock:test");
                    }
                });

        mock.expectedMessageCount(1);

        // add message with the same msgID (that's why we use JDBC) => FAIL response
        String sql = "INSERT INTO message "
                + " (correlation_id, failed_count, failed_desc, failed_error_code, last_update_timestamp, msg_timestamp,"
                + "     object_id, operation_name, payload, receive_timestamp, service, source_system, state, msg_id,"
                + "     guaranteed_order, exclude_failed_state)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        getJdbcTemplate().update(sql, getTraceHeader().getTraceIdentifier().getCorrelationID(), 0, "", "", null,
                new Date(), "", "opName", "payload", new Date(), ServiceTestEnum.CUSTOMER.toString(),
                ExternalSystemTestEnum.CRM.toString(), MsgStateEnum.NEW.toString(), 1, false, false);

        producer.sendBodyAndHeaders("bodyContent", getHeaders());

        assertErrorResponse("Unique index or primary key violation");
    }

    @Test
    public void testResponseFAIL_throttling() throws Exception {
        getCamelContext().getRouteDefinition(AsynchInMessageRoute.ROUTE_ID_ASYNC)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveById("throttleProcess").replace().throwException(new ThrottlingExceededException("error"));

                        weaveAddLast().to("mock:test");
                    }
                });

        mock.expectedMessageCount(0);

        try {
            producer.sendBodyAndHeaders("bodyContent", getHeaders());

            fail();
        } catch (CamelExecutionException ex) {
            assertThat(ex.getCause(), instanceOf(ThrottlingExceededException.class));
        }

        mock.assertIsSatisfied();
    }

    @Test
    public void testResponseFAIL_stopping() throws Exception {
        getCamelContext().getRouteDefinition(AsynchInMessageRoute.ROUTE_ID_ASYNC)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveById("stopChecking").replace().throwException(new StoppingException("stop"));

                        weaveAddLast().to("mock:test");
                    }
                });

        mock.expectedMessageCount(0);

        try {
            producer.sendBodyAndHeaders("bodyContent", getHeaders());

            fail();
        } catch (CamelExecutionException ex) {
            assertThat(ex.getCause(), instanceOf(StoppingException.class));
        }

        mock.assertIsSatisfied();
    }

    private void assertErrorResponse(String addInfo) throws InterruptedException {
        mock.assertIsSatisfied();

        Exchange exchange = mock.getExchanges().get(0);
        CallbackResponse callbackResponse = exchange
                .getProperty(AsynchConstants.ERR_CALLBACK_RES_PROP, CallbackResponse.class);
        assertThat(callbackResponse.getStatus(), is(ConfirmationTypes.FAIL));
        assertThat(callbackResponse.getAdditionalInfo(), containsString(addInfo));
        assertThat(exchange.getProperty(Exchange.EXCEPTION_CAUGHT), notNullValue());
    }

    @Test
    @Transactional
    public void testGuaranteedOrder_processing() throws Exception {
        getCamelContext().getRouteDefinition(AsynchInMessageRoute.ROUTE_ID_GUARANTEED_ORDER)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveById("toAsyncRoute").replace().to("mock:test");
                    }
                });

        // prepare message - only one message
        Message msg = insertNewMessage("id1", MsgStateEnum.PROCESSING, true, FUNNEL_VALUE_ONE);

        mock.expectedMessageCount(1);
        guaranteedProducer.sendBody(msg);
        mock.assertIsSatisfied();

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));

        // prepare message - second message with different funnel value
        msg = insertNewMessage("id2", MsgStateEnum.PROCESSING, true, "some value");

        mock.expectedMessageCount(2);
        guaranteedProducer.sendBody(msg);
        mock.assertIsSatisfied();

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));

        // prepare message - third message that is not guaranteed
        msg = insertNewMessage("id3", MsgStateEnum.PROCESSING, false, FUNNEL_VALUE_ONE);

        mock.expectedMessageCount(3);
        guaranteedProducer.sendBody(msg);
        mock.assertIsSatisfied();

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));
    }

    @Test
    @Transactional
    public void testGuaranteedOrder_postponedMessage() throws InterruptedException {
        // prepare message that should be postponed
        insertNewMessage("id1", MsgStateEnum.PROCESSING, true, FUNNEL_VALUE_ONE);

        Message msg = insertNewMessage("id2", MsgStateEnum.PROCESSING, true, FUNNEL_VALUE_ONE);
        msg.setReceiveTimestamp(DateUtils.addSeconds(new Date(), 10));

        // action
        guaranteedProducer.sendBody(msg);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
    }

    @Test
    @Transactional
    public void testGuaranteedOrder_postponedMultiFunnelLastMessage() throws InterruptedException {
        // prepare message that should be postponed
        insertNewMessage("id1", MsgStateEnum.PROCESSING, true, FUNNEL_VALUE_ONE);

        Message msg = insertNewMessage("id2", MsgStateEnum.PROCESSING, true, FUNNEL_VALUE_ONE, FUNNEL_VALUE_TWO);
        msg.setReceiveTimestamp(DateUtils.addSeconds(new Date(), 10));

        // action
        guaranteedProducer.sendBody(msg);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
    }

    @Test
    @Transactional
    public void testGuaranteedOrder_postponedMultiFunnelFirstMessage() throws InterruptedException {
        // prepare message that should be postponed
        insertNewMessage("id1", MsgStateEnum.PROCESSING, true, FUNNEL_VALUE_ONE, FUNNEL_VALUE_TWO);

        Message msg = insertNewMessage("id2", MsgStateEnum.PROCESSING, true, FUNNEL_VALUE_ONE);
        msg.setReceiveTimestamp(DateUtils.addSeconds(new Date(), 10));

        // action
        guaranteedProducer.sendBody(msg);

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
    }

    @Test
    @Transactional
    public void testGuaranteedOrder_multiFunnel() throws Exception {
        getCamelContext().getRouteDefinition(AsynchInMessageRoute.ROUTE_ID_GUARANTEED_ORDER)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveById("toAsyncRoute").replace().to("mock:test");
                    }
                });

        // prepare message - only one message
        Message msg = insertNewMessage("id1", MsgStateEnum.PROCESSING, true, FUNNEL_VALUE_ONE, FUNNEL_VALUE_TWO);

        mock.expectedMessageCount(1);
        guaranteedProducer.sendBody(msg);
        mock.assertIsSatisfied();

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));

        // prepare message - second message with different funnel value
        msg = insertNewMessage("id2", MsgStateEnum.PROCESSING, true, "some value");

        mock.expectedMessageCount(2);
        guaranteedProducer.sendBody(msg);
        mock.assertIsSatisfied();

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));

        // prepare message - third message that is not guaranteed
        msg = insertNewMessage("id3", MsgStateEnum.PROCESSING, false, FUNNEL_VALUE_ONE);

        mock.expectedMessageCount(3);
        guaranteedProducer.sendBody(msg);
        mock.assertIsSatisfied();

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.PROCESSING));

        msg = insertNewMessage("id4", MsgStateEnum.PROCESSING, true, FUNNEL_VALUE_TWO);
        msg.setReceiveTimestamp(DateUtils.addSeconds(new Date(), 100));

        mock.expectedMessageCount(3);
        guaranteedProducer.sendBody(msg);
        mock.assertIsSatisfied();

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));

        msg = insertNewMessage("id5", MsgStateEnum.PROCESSING, true, FUNNEL_VALUE_ONE);
        msg.setReceiveTimestamp(DateUtils.addSeconds(new Date(), 200));

        mock.expectedMessageCount(3);
        guaranteedProducer.sendBody(msg);
        mock.assertIsSatisfied();

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));

        msg = insertNewMessage("id6", MsgStateEnum.PROCESSING, true, FUNNEL_VALUE_ONE, FUNNEL_VALUE_TWO);
        msg.setReceiveTimestamp(DateUtils.addSeconds(new Date(), 300));

        mock.expectedMessageCount(3);
        guaranteedProducer.sendBody(msg);
        mock.assertIsSatisfied();

        Assert.assertThat(em.find(Message.class, msg.getMsgId()).getState(), CoreMatchers.is(MsgStateEnum.POSTPONED));
    }

    private Message insertNewMessage(String correlationId, MsgStateEnum state, boolean guaranteedOrder,
                                     @Nullable String... funnelValues) {
        Date currDate = new Date();

        Message msg = new Message();
        msg.setState(state);

        msg.setMsgTimestamp(currDate);
        msg.setReceiveTimestamp(currDate);
        msg.setLastUpdateTimestamp(currDate);
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setCorrelationId(correlationId);

        msg.setService(ServiceTestEnum.CUSTOMER);
        msg.setOperationName("setCustomer");
        msg.setObjectId(null);
        if (funnelValues != null && funnelValues.length != 0) {
            msg.setFunnelValues(Arrays.asList(funnelValues));
        }
        msg.setGuaranteedOrder(guaranteedOrder);

        msg.setPayload("xml");

        em.persist(msg);
        em.flush();

        return msg;
    }
}
