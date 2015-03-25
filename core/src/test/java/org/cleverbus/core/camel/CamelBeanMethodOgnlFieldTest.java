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

import static org.hamcrest.CoreMatchers.is;

import org.apache.camel.Handler;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Test suite for checking correct bean binding,
 * see <a href="https://issues.apache.org/jira/browse/CAMEL-6687">CAMEL-6687</a>.
 */
public class CamelBeanMethodOgnlFieldTest extends CamelTestSupport {

    @Produce
    ProducerTemplate producer;

    @Test
    public void testBothValues() {
        ExamplePojo fooBar = new ExamplePojo();
        fooBar.setFoo("foo1");
        fooBar.setBar("bar2");

        String result = producer.requestBody("direct:routeONE", fooBar, String.class);
        assertThat(result, is("foo: foo1; bar: bar2"));
    }

    @Test
    public void testNullValue() {
        ExamplePojo fooBar = new ExamplePojo();
        fooBar.setFoo(null);
        fooBar.setBar("test");

        String result = producer.requestBody("direct:routeONE", fooBar, String.class);
        assertThat(result, is("foo: null; bar: test"));
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:routeONE")
                    .bean(new ExampleBean(), "doWithFooBar(${body.foo}, ${body.bar})");
            }
        };
    }

    public static class ExampleBean {
        @Handler
        public String doWithFooBar(String foo, String bar) {
            return String.format("foo: %s; bar: %s", foo, bar);
        }
    }

    public static class ExamplePojo {
        private String foo;
        private String bar;

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public String getBar() {
            return bar;
        }

        public void setBar(String bar) {
            this.bar = bar;
        }
    }
}
