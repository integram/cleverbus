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

import org.apache.camel.LoggingLevel;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class CamelMulticastStopTest extends CamelTestSupport {

    @Produce
    ProducerTemplate producer;

    @Test
    public void testCamelStopMulticast() {
        String result = producer.requestBody("direct:routeONE", "any body", String.class);
        assertEquals("routeTWO", result);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:routeONE")
                    .log(LoggingLevel.WARN, "routeONE reached")
                    .transform().constant("routeONE")
                    .multicast().stopOnException()
                        .to("direct:routeTWO")
                        .to("direct:routeTHREE");

                from("direct:routeTWO")
                    .log(LoggingLevel.WARN, "routeTWO reached")
                    .transform().constant("routeTWO")
                    .stop();

                from("direct:routeTHREE")
                    .log(LoggingLevel.ERROR, "routeTHREE reached")
                    .transform().constant("routeTHREE");
            }
        };
    }
}
