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

package org.cleverbus.core.throttling;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.cleverbus.api.entity.Message;
import org.cleverbus.api.exception.IntegrationException;
import org.cleverbus.api.exception.InternalErrorEnum;
import org.cleverbus.core.AbstractCoreTest;
import org.cleverbus.spi.throttling.ThrottleScope;
import org.cleverbus.spi.throttling.ThrottlingProcessor;
import org.cleverbus.test.ExternalSystemTestEnum;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;


/**
 * Test suite for {@link ThrottleProcessorImpl}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ThrottleMsgProcessorTest extends AbstractCoreTest {

    @Produce(uri = "direct:start")
    private ProducerTemplate producer;

    @Autowired
    private ThrottlingProcessor throttlingProcessor;


    @Before
    public void prepareConfiguration() {
        // prepare properties
        String prefix = ThrottlingPropertiesConfiguration.PROPERTY_PREFIX;
        Properties props = new Properties();
        props.put(prefix + "*.sendSms", "2/10");
        props.put(prefix + "crm.createCustomer", "2/10");

        // create configuration
        ThrottlingPropertiesConfiguration conf = new ThrottlingPropertiesConfiguration(props);

        setPrivateField(throttlingProcessor, "configuration", conf);
    }

    @Test
    public void testSyncProcessor() throws Exception {
        RouteBuilder route = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            ThrottleScope throttleScope = new ThrottleScope(ThrottleScope.ANY_SOURCE_SYSTEM, "sendSms");
                            throttlingProcessor.throttle(throttleScope);
                        }
                    });
            }
        };

        getCamelContext().addRoutes(route);

        producer.sendBody("something");
        producer.sendBody("something");

        try {
            producer.sendBody("something");
            fail();
        } catch (CamelExecutionException ex) {
            assertThat(ex.getCause(), instanceOf(IntegrationException.class));
            assertErrorCode(((IntegrationException)ex.getCause()).getError(), InternalErrorEnum.E114);
        }
    }

    @Test
    public void testSyncProcessorWithDefaults() throws Exception {
        // create configuration
        ThrottlingPropertiesConfiguration confDefaults = new ThrottlingPropertiesConfiguration(new Properties());

        setPrivateField(throttlingProcessor, "configuration", confDefaults);

        RouteBuilder route = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            ThrottleScope throttleScope = new ThrottleScope(ThrottleScope.ANY_SOURCE_SYSTEM, "sendSms");
                            throttlingProcessor.throttle(throttleScope);
                        }
                    });
            }
        };

        getCamelContext().addRoutes(route);

        for (int i = 0; i < AbstractThrottlingConfiguration.DEFAULT_LIMIT; i++) {
            producer.sendBody("something");
        }

        try {
            producer.sendBody("something");
            fail();
        } catch (CamelExecutionException ex) {
            assertThat(ex.getCause(), instanceOf(IntegrationException.class));
            assertErrorCode(((IntegrationException)ex.getCause()).getError(), InternalErrorEnum.E114);
        }
    }

    @Test
    public void testAsyncProcessor() throws Exception {
        RouteBuilder route = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            Message msg = exchange.getIn().getBody(Message.class);

                            Assert.notNull(msg, "the msg must not be null");

                            ThrottleScope throttleScope = new ThrottleScope(msg.getSourceSystem().getSystemName(),
                                    msg.getOperationName());
                            throttlingProcessor.throttle(throttleScope);
                        }
                    });
            }
        };

        getCamelContext().addRoutes(route);

        Message msg = new Message();
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setOperationName("createCustomer");

        producer.sendBody(msg);
        producer.sendBody(msg);

        try {
            producer.sendBody(msg);
            fail();
        } catch (CamelExecutionException ex) {
            assertThat(ex.getCause(), instanceOf(IntegrationException.class));
            assertErrorCode(((IntegrationException)ex.getCause()).getError(), InternalErrorEnum.E114);
        }
    }
}
