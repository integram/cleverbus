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

import org.apache.camel.ExchangePattern;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class CamelInOnlyPatternTest extends CamelTestSupport{
    @Produce
    ProducerTemplate producer;

    @Test
    public void testInOnlyPattern() {
        assertEquals("route ONE", producer.requestBody("direct:route1A", "original body", String.class));
        assertEquals("route TWO", producer.requestBody("direct:route2A", "original body", String.class));
        assertEquals("route THREE", producer.requestBody("direct:route3A", "original body", String.class));
        assertEquals("route FOUR", producer.requestBody("direct:route4A", "original body", String.class));
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:route1A")
                        .to("direct:route1B");
                from("direct:route1B")
                        .transform(constant("route ONE"));

                from("direct:route2A")
                        .setExchangePattern(ExchangePattern.InOnly)
                        .to("direct:route2B?exchangePattern=InOnly");
                from("direct:route2B?exchangePattern=InOnly")
                        .transform(constant("route TWO"));

                from("direct:route3A")
                        .setExchangePattern(ExchangePattern.InOnly)
                        .to("direct:route3B");
                from("direct:route3B")
                        .setExchangePattern(ExchangePattern.InOnly)
                        .transform(constant("route THREE"));

                from("direct:route4A")
                        .inOnly("direct:route4B?exchangePattern=InOnly");
                from("direct:route4B?exchangePattern=InOnly")
                        .transform(constant("route FOUR"));
            }
        };
    }
}
