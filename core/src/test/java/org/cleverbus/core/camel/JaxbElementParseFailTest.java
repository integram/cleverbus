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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import org.cleverbus.core.camel.jaxb.TestXmlObject;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.Test;


/**
 * Test suite for checking JAXB unmarshaling.
 */
public class JaxbElementParseFailTest extends CamelTestSupport {

    private String requestXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<ns1:testXmlObject xmlns:ns1=\"urn:testXmlObject\">\n" +
            "    <ns1:testDate>2013-07-22+02:00</ns1:testDate>\n" +
            "</ns1:testXmlObject>";

    @Produce
    private ProducerTemplate producer;

    @Test
    public void testJaxbElementParseFailOne() {
        try {
            producer.requestBody("direct:routeONE", requestXml, TestXmlObject.class);
            fail("Either the adapter didn't throw an exception (it should for this test) or it was ignored");
        } catch (CamelExecutionException exc) {
            Throwable rootCause = ExceptionUtils.getRootCause(exc);
            assertNotNull(rootCause);
            assertThat(rootCause, is(instanceOf(NumberFormatException.class)));
        }
    }

    @Test
    public void testJaxbElementParseFailTwo() {
        try {
            producer.requestBody("direct:routeTWO", requestXml, TestXmlObject.class);
        } catch (CamelExecutionException exc) {
            fail("Exception was thrown but it's not expected for this case");
        }
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                from("direct:routeONE")
                        .log(LoggingLevel.INFO, "Before unmarshaling: ${body}")
                        .unmarshal(getDataFormat(TestXmlObject.class))
                        .log(LoggingLevel.INFO, "After unmarshaling: ${body}");

                from("direct:routeTWO")
                        .log(LoggingLevel.INFO, "Before unmarshaling: ${body}")
                        .unmarshal().jaxb(TestXmlObject.class.getPackage().getName())
                        .log(LoggingLevel.INFO, "After unmarshaling: ${body}");
            }
        };
    }

    protected JaxbDataFormat getDataFormat(Class<?> xmlClass) {
        JaxbDataFormat dataFormat = new JaxbDataFormat(false);
        dataFormat.setContextPath(xmlClass.getPackage().getName());
        dataFormat.setSchema("classpath:org/cleverbus/core/camel/jaxb/mock.xsd");
        return dataFormat;
    }
}
