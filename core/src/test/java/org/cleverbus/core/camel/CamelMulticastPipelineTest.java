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

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class CamelMulticastPipelineTest extends CamelTestSupport {

    @Produce
    ProducerTemplate producer;

    @Test
    public void testCamelMulticastPipeline() {
        String result = producer.requestBody("direct:routeONE", "start", String.class);
        assertEquals("start->routeONE->routeTWO + start->routeONE->routeTHREE + start->routeONE->routeFOUR->routeFIVE", result);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:routeONE")
                    .log(LoggingLevel.WARN, "routeONE reached")
                    .transform(body().append("->routeONE"))
                    .multicast(
                            new AggregationStrategy() {
                                @Override
                                public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                                    if (oldExchange == null) {
                                        return newExchange;
                                    } else {
                                        oldExchange.getIn().setBody(oldExchange.getIn().getBody(String.class)
                                                + " + "
                                                + newExchange.getIn().getBody(String.class));
                                        return oldExchange;
                                    }
                                }
                            })
                        .to("direct:routeTWO", "direct:routeTHREE")
                        .pipeline()
                            .to("direct:routeFOUR")
                            .to("direct:routeFIVE")
                        .end()
                    .end();

                from("direct:routeTWO")
                    .log(LoggingLevel.WARN, "routeTWO reached with body ${body}")
                    .transform(body().append("->routeTWO"));

                from("direct:routeTHREE")
                    .log(LoggingLevel.WARN, "routeTHREE reached with body ${body}")
                    .transform(body().append("->routeTHREE"));

                from("direct:routeFOUR")
                    .log(LoggingLevel.WARN, "routeFOUR reached with body ${body}")
                    .transform(body().append("->routeFOUR"));

                from("direct:routeFIVE")
                    .log(LoggingLevel.WARN, "routeFIVE reached with body ${body}")
                    .transform(body().append("->routeFIVE"));
            }
        };
    }
}
