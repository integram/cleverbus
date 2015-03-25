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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Date;

import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.asynch.confirm.ConfirmationCallback;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.api.exception.IntegrationException;
import org.cleverbus.api.exception.InternalErrorEnum;
import org.cleverbus.api.exception.ValidationIntegrationException;
import org.cleverbus.api.route.AbstractBasicRoute;
import org.cleverbus.core.AbstractCoreDbTest;
import org.cleverbus.core.common.asynch.confirm.ConfirmationPollExecutor;
import org.cleverbus.test.ActiveRoutes;
import org.cleverbus.test.ExternalSystemTestEnum;
import org.cleverbus.test.ServiceTestEnum;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;


/**
 * Test suite for {@link AsynchMessageRoute}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@ActiveRoutes(classes = AsynchMessageRoute.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class)
@Transactional
public class AsynchMessageRouteConfirmTest extends AbstractCoreDbTest {

    private static final String REQUEST_XML =
            "  <cus:setCustomerRequest xmlns=\"http://cleverbus.org/ws/Customer-v1\""
                    + "         xmlns:cus=\"http://cleverbus.org/ws/CustomerService-v1\">"
                    + "         <cus:customer>"
                    + "            <externalCustomerID>12</externalCustomerID>"
                    + "            <customerNo>23</customerNo>"
                    + "            <customerTypeID>2</customerTypeID>"
                    + "            <lastName>Juza</lastName>"
                    + "            <firstName>Petr</firstName>"
                    + "         </cus:customer>"
                    + "  </cus:setCustomerRequest>";

    @Produce(uri = AsynchMessageRoute.URI_SYNC_MSG)
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    private Message msg;

    private String subRouteUri;

    @Autowired
    @ReplaceWithMock
    private ConfirmationCallback confirmationCallback;

    @Autowired
    private ConfirmationPollExecutor confirmationExecutor;

    /**
     * Interval (in seconds) between two tries of failed confirmations.
     */
    @Value("${asynch.confirmation.interval}")
    private int interval;

    @Before
    public void prepareData() throws Exception {
        // message
        Date currDate = new Date();

        msg = new Message();
        msg.setState(MsgStateEnum.PROCESSING);
        msg.setMsgTimestamp(currDate);
        msg.setReceiveTimestamp(currDate);
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setCorrelationId("123-456");

        msg.setService(ServiceTestEnum.CUSTOMER);
        msg.setOperationName("setCustomer");
        msg.setPayload(REQUEST_XML);
        msg.setLastUpdateTimestamp(currDate);

        subRouteUri = "direct:" + msg.getService().getServiceName() + "_" + msg.getOperationName()
                + AbstractBasicRoute.OUT_ROUTE_SUFFIX;
        getCamelContext().addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                onException(Exception.class)
                    .handled(true)
                    .to(AsynchConstants.URI_ERROR_FATAL);

                from(subRouteUri)
                        .routeId("TestRoute")
                        .to(mock);
            }
        });
    }

    @Test
    public void testConfirmationStatusOK() throws Exception {
        // save message into DB
        em.persist(msg);
        em.flush();

        // send message
        producer.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        // verify message
        Message msgDB = em.find(Message.class, msg.getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.OK));

        // verify confirmation sent OK
        verify(confirmationCallback).confirm(msg);
        verifyNoMoreInteractions(confirmationCallback);
    }

    @Test
    public void testConfirmationStatusFailed() throws Exception {
        // simulate fatal failure:
        mock.whenAnyExchangeReceived(throwException(new ValidationIntegrationException(InternalErrorEnum.E102)));

        // save message into DB
        em.persist(msg);
        em.flush();

        // send message
        producer.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);

        // verify message
        Message msgDB = em.find(Message.class, msg.getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.FAILED));

        // verify confirmation sent OK
        verify(confirmationCallback).confirm(msg);
        verifyNoMoreInteractions(confirmationCallback);
    }

    @Test
    public void testConfirmationFailedRepeat() throws Exception {
        if (interval > 0) {
            fail("This test requires interval to be set to 0, otherwise the polling won't happen fast enough");
        }

        // force confirmation callback to fail:
        doThrow(new IntegrationException(InternalErrorEnum.E100, "Simulated Failure ONE")) // fail once
                .doThrow(new IntegrationException(InternalErrorEnum.E100, "Simulated Failure TWO")) // fail twice
                .doNothing() // succeed on the 3rd time
                .when(confirmationCallback).confirm(msg);

        // save message into DB
        em.persist(msg);
        em.flush();

        // send message
        producer.sendBodyAndHeader(msg, AsynchConstants.MSG_HEADER, msg);
        Thread.sleep(1000);
        confirmationExecutor.run(); // push failed confirms from DB to confirm queue
        Thread.sleep(1000);
        confirmationExecutor.run(); // push failed confirms from DB to confirm queue

        // verify message
        Message msgDB = em.find(Message.class, msg.getMsgId());
        assertThat(msgDB, notNullValue());
        assertThat(msgDB.getState(), is(MsgStateEnum.OK)); // message should stay OK

        // verify 2 failures + 1 success and nothing else:
        verify(confirmationCallback, never()).confirm(null);
        verify(confirmationCallback, times(3)).confirm(eq(msg));
        verifyNoMoreInteractions(confirmationCallback);
    }
}
