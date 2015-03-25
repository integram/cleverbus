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

public class CamelMvelEmptyTest extends CamelTestSupport {

    @Produce
    ProducerTemplate producer;

    /**
     * Currently MVEL cannot handle numeric "== empty",
     * despite the fact that it mentions 0 value for number in language guide:
     * http://mvel.codehaus.org/Value+Emptiness
     */
    @Test(expected = RuntimeException.class)
    public void testMvelEmptyLongObject() {
        String result = producer.requestBody("direct:routeONE", new MyWrap<Long>(0L), String.class);
        assertEquals("FALSE", result);

        result = producer.requestBody("direct:routeONE", new MyWrap<Long>(1L), String.class);
        assertEquals("TRUE", result);

        result = producer.requestBody("direct:routeONE", new MyWrap<Long>(-1L), String.class);
        assertEquals("TRUE", result);

        result = producer.requestBody("direct:routeONE", new MyWrap<Long>(null), String.class);
        assertEquals("FALSE", result);
    }

    @Test(expected = RuntimeException.class)
    public void testMvelEmptyLongPrimitive() {
        String result = producer.requestBody("direct:routeONE", new MyWrapPrimitive(1L), String.class);
        assertEquals("TRUE", result);

        result = producer.requestBody("direct:routeONE", new MyWrapPrimitive(-1L), String.class);
        assertEquals("TRUE", result);

        result = producer.requestBody("direct:routeONE", new MyWrapPrimitive(0L), String.class);
        assertEquals("FALSE", result);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:routeONE")

                    .choice()
                        .when().mvel("request.body.inner != empty")
                            .log(LoggingLevel.WARN, "Body is NOT EMPTY: ${body}")
                            .transform(constant("TRUE"))
                        .otherwise()
                            .log(LoggingLevel.WARN, "Body is EMPTY: ${body}")
                            .transform(constant("FALSE"))
                    .end();
            }
        };
    }

    public static class MyWrap <T> {
        private final T inner;

        public MyWrap(T someObject) {
            this.inner = someObject;
        }

        public T getInner() {
            return inner;
        }
    }

    public static class MyWrapPrimitive {
        private final long inner;

        public MyWrapPrimitive(long somePrimitive) {
            this.inner = somePrimitive;
        }

        public long getInner() {
            return inner;
        }
    }
}
