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

package org.cleverbus.core.camel;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.Synchronization;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Test suite for checking behaviour of stop and <a href="http://camel.apache.org/oncompletion.html">onComplete</a>
 * functionality in Camel.
 * <p/>
 * Note: behaviour changed in 2.12.1 version - when the route is stopped then it's not possible to send something
 */
public class CamelStopOnCompletionTest extends CamelTestSupport {

    @Produce
    private ProducerTemplate producer;

    private boolean completed = false;

    @Test
    public void testStopOnCompletion() throws InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:test");
        mock.expectedMessageCount(2);

        String result = producer.requestBody("direct:routeONE", "any body", String.class);
        assertEquals("any body-routeONE-routeTWO", result);
        assertTrue(completed);

        mock.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                onCompletion()
                        .transform(body().append("-OnCompletion"))
                        .log(LoggingLevel.WARN, "OnCompletion")
                        .to("mock:test");

                from("direct:routeONE")
                    .onCompletion()
                        .transform(body().append("-OnCompletionInRoute"))
                        .log(LoggingLevel.WARN, "OnCompletionInRoute")
                        .to("mock:test")
                    .end()

                    .setProperty("TestProperty", constant("Test"))
                    .transform(body().append("-routeONE"))
                    .log(LoggingLevel.WARN, "routeONE")
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            exchange.getUnitOfWork().addSynchronization(new Synchronization() {
                                @Override
                                public void onComplete(Exchange exchange) {
                                    exchange.getIn().setBody(exchange.getIn().getBody(String.class) + "-Complete");
                                    log.warn("Complete");

                                    completed = true;

                                    // camel 2.12.1 - doesn't have effect
                                    producer.send(getMockEndpoint("mock:test"), exchange);
                                }

                                @Override
                                public void onFailure(Exchange exchange) {
                                    exchange.getIn().setBody(exchange.getIn().getBody(String.class) + "-Failure");
                                    log.warn("Failure");
                                    producer.send(getMockEndpoint("mock:test"), exchange);
                                }
                            });
                        }
                    })

                    .doTry()
                        .to("direct:routeTWO")
                        .transform(body().append("-continued after stop"))
                        .log(LoggingLevel.WARN, "continued after stop")
                    .doCatch(Throwable.class)
                        .transform(body().append("-caught exception"))
                        .log(LoggingLevel.ERROR, "Error: ${property." + Exchange.EXCEPTION_CAUGHT + "}")
                    .doFinally()
                        .transform(body().append("-entered finally"))
                        .log(LoggingLevel.WARN, "entered finally");

                from("direct:routeTWO")
                    .onCompletion()
                        .transform(body().append("-OnCompletionInRoute2"))
                        .log(LoggingLevel.WARN, "OnCompletionInRoute2")
                        .to("mock:test")
                    .end()
                    .transform(body().append("-routeTWO"))
                    .log(LoggingLevel.WARN, "routeTWO")
                    .stop();
            }
        };
    }
}
