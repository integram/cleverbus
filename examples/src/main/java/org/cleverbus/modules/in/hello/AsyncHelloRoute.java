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

package org.cleverbus.modules.in.hello;

import static org.cleverbus.common.jaxb.JaxbDataFormatHelper.jaxb;

import javax.xml.namespace.QName;

import org.cleverbus.api.asynch.AsynchResponseProcessor;
import org.cleverbus.api.asynch.AsynchRouteBuilder;
import org.cleverbus.api.asynch.model.CallbackResponse;
import org.cleverbus.api.route.AbstractBasicRoute;
import org.cleverbus.api.route.CamelConfiguration;
import org.cleverbus.api.route.XPathValidator;
import org.cleverbus.common.log.Log;
import org.cleverbus.modules.ServiceEnum;
import org.cleverbus.modules.in.hello.model.AsyncHelloRequest;
import org.cleverbus.modules.in.hello.model.AsyncHelloResponse;

import org.apache.camel.Body;
import org.apache.camel.Expression;
import org.apache.camel.Handler;
import org.apache.camel.builder.xml.Namespaces;
import org.springframework.util.Assert;


/**
 * Route definition for "asyncHello" operation.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@CamelConfiguration(value = AsyncHelloRoute.ROUTE_BEAN)
public class AsyncHelloRoute extends AbstractBasicRoute {

    public static final String ROUTE_BEAN = "asyncHelloRouteBean";

    private static final String OPERATION_NAME = "asyncHello";

    public static final String ROUTE_ID_ASYNC_IN = getInRouteId(ServiceEnum.HELLO, OPERATION_NAME);

    public static final String ROUTE_ID_ASYNC_OUT = getOutRouteId(ServiceEnum.HELLO, OPERATION_NAME);

    public static final String URI_ASYNC_HELLO_OUT = "direct:" + ROUTE_ID_ASYNC_OUT;

    private static final String URI_PRINT_GREETING = "direct:printGreeting";

    @Override
    protected void doConfigure() throws Exception {
        // asyncHello - input asynch message
        createRouteForAsyncHelloRouteIn();

        // asyncHello - process delivery to external systems
        createRouteForAsyncHelloRouteOut();
    }

    /**
     * Route for asynchronous <strong>asyncHello</strong> input operation.
     * <p/>
     * Prerequisite: none
     * <p/>
     * Output: {@link AsyncHelloResponse}
     */
    private void createRouteForAsyncHelloRouteIn() {
        Namespaces ns = new Namespaces("h", SyncHelloRoute.HELLO_SERVICE_NS);

        // note: mandatory parameters are set already in XSD, this validation is extra
        XPathValidator validator = new XPathValidator("/h:asyncHelloRequest", ns, "h:name");

        // note: only shows using but without any influence in this case
        Expression nameExpr = xpath("/h:asyncHelloRequest/h:name").namespaces(ns).stringResult();

        AsynchRouteBuilder.newInstance(ServiceEnum.HELLO, OPERATION_NAME,
                getInWsUri(new QName(SyncHelloRoute.HELLO_SERVICE_NS, "asyncHelloRequest")),
                new AsynchResponseProcessor() {
                    @Override
                    protected Object setCallbackResponse(CallbackResponse callbackResponse) {
                        AsyncHelloResponse res = new AsyncHelloResponse();
                        res.setConfirmAsyncHello(callbackResponse);
                        return res;
                    }
                }, jaxb(AsyncHelloResponse.class))

                .withValidator(validator)
                .withObjectIdExpr(nameExpr)
                .build(this);
    }

    /**
     * Route for <strong>asyncHello</strong> operation - process delivery to external systems.
     * <p/>
     * Prerequisite: none
     */
    private void createRouteForAsyncHelloRouteOut() {
        from(URI_ASYNC_HELLO_OUT)
                .routeId(ROUTE_ID_ASYNC_OUT)

                // xml -> AsyncHelloRequest
                .unmarshal(jaxb(AsyncHelloRequest.class))

                .to("extcall:message:" + URI_PRINT_GREETING);


        from(URI_PRINT_GREETING)
                .bean(this, "printGreeting");
    }

    @Handler
    public void printGreeting(@Body AsyncHelloRequest req) {
        Assert.notNull(req, "req must not be null");

        String greeting = "Hello " + req.getName();

        Log.debug("Greeting: " + greeting);
    }
}
