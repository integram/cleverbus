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

import org.apache.camel.*;
import org.apache.camel.component.spring.ws.SpringWebserviceConstants;
import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.asynch.AsynchResponseProcessor;
import org.cleverbus.api.asynch.model.CallbackResponse;
import org.cleverbus.api.asynch.model.ConfirmationTypes;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.exception.IntegrationException;
import org.cleverbus.api.exception.InternalErrorEnum;
import org.cleverbus.api.exception.StoppingException;
import org.cleverbus.api.exception.ThrottlingExceededException;
import org.cleverbus.api.route.AbstractBasicRoute;
import org.cleverbus.api.route.CamelConfiguration;
import org.cleverbus.common.log.Log;
import org.cleverbus.common.log.LogContextFilter;
import org.cleverbus.core.common.asynch.msg.MessageTransformer;
import org.cleverbus.core.common.asynch.stop.StopService;
import org.cleverbus.core.common.event.AsynchEventHelper;
import org.cleverbus.core.common.exception.ExceptionTranslator;
import org.cleverbus.core.common.validator.TraceIdentifierValidator;
import org.cleverbus.spi.msg.MessageService;
import org.cleverbus.spi.throttling.ThrottleScope;
import org.cleverbus.spi.throttling.ThrottlingProcessor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.cleverbus.api.asynch.AsynchConstants.*;


/**
 * Route definition that processes incoming asynchronous message and make the following steps:
 * <ol>
 *     <li>parse trace (SOAP) header from the request
 *     <li>creates {@link Message} entity
 *     <li>check throttling
 *     <li>saves Message into db
 *     <li>creates OK/FAIL response
 * </ol>
 *
 * If everything works fine then the message is asynchronously redirected for next processing
 * without need to take it from message queue.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @see AsynchResponseProcessor
 */
@CamelConfiguration(value = AsynchInMessageRoute.ROUTE_BEAN)
public class AsynchInMessageRoute extends AbstractBasicRoute {

    public static final String ROUTE_BEAN = "inMsgRouteBean";

    /**
     * The main route for processing incoming asynchronous messages.
     */
    public static final String ROUTE_ID_ASYNC = "asyncProcessIn" + AbstractBasicRoute.ROUTE_SUFFIX;

    static final int NEW_MSG_PRIORITY = 10;

    static final String URI_GUARANTEED_ORDER_ROUTE = "direct:guaranteedOrderRoute";

    static final String ROUTE_ID_GUARANTEED_ORDER = "guaranteedOrder" + AbstractBasicRoute.ROUTE_SUFFIX;

    @Autowired
    private ThrottlingProcessor throttlingProcessor;

    @Autowired
    private MessageService messageService;

    // list of validator for trace identifier is not mandatory
    @Autowired(required = false)
    private List<TraceIdentifierValidator> validatorList;

    /**
     * Route for incoming asynchronous message input operation.
     * <p/>
     * Prerequisite: defined message headers {@link AsynchConstants#SERVICE_HEADER}, {@link AsynchConstants#OPERATION_HEADER}
     *      and optional {@link AsynchConstants#OBJECT_ID_HEADER}
     * <p/>
     * Output: {@link CallbackResponse} for OK message or fill "{@value AsynchConstants#ERR_CALLBACK_RES_PROP}" exchange property
     *      if error occurred
     */
    @Override
    @SuppressWarnings("unchecked")
    public void doConfigure() throws Exception {

        from(URI_ASYNCH_IN_MSG)
            .routeId(ROUTE_ID_ASYNC)

            .doTry()

                // check headers existence
                .validate(header(SERVICE_HEADER).isNotNull())
                .validate(header(OPERATION_HEADER).isNotNull())

                // check if ESB is not stopping?
                .beanRef(ROUTE_BEAN, "checkStopping").id("stopChecking")

                // extract trace header, trace header is mandatory
                .process(new TraceHeaderProcessor(true, validatorList))
                // remove inbound Spring WS SOAP header, so it isn't added to outbound SOAP messages
                .removeHeader(SpringWebserviceConstants.SPRING_WS_SOAP_HEADER)

                // create Message (state = PROCESSING)
                .bean(MessageTransformer.getInstance(), "createMessage")

                // throttling
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Message msg = exchange.getIn().getBody(Message.class);

                        Assert.notNull(msg, "the msg must not be null");

                        ThrottleScope throttleScope = new ThrottleScope(msg.getSourceSystem().getSystemName(),
                                msg.getOperationName());

                        throttlingProcessor.throttle(throttleScope);
                    }
                }).id("throttleProcess")

                // save it to DB
                .beanRef(ROUTE_BEAN, "insertMessage")
                //todo (cermak) in load causes a blockage, find out why and after resolving use it again
//              .to("jpa:" + Message.class.getName() + "?usePersist=true&persistenceUnit=" + DbConst.UNIT_NAME)

                // check guaranteed order
//                .to(ExchangePattern.InOnly, URI_GUARANTEED_ORDER_ROUTE)
                //TODO (juza) finish in 1.1 version
                .to(URI_GUARANTEED_ORDER_ROUTE)

                // create OK response
                .beanRef(ROUTE_BEAN, "createOkResponse")

            .endDoTry()

            .doCatch(ThrottlingExceededException.class)
                // we want to throw exception, not return fail response
                .log(LoggingLevel.ERROR, "Incoming route - throttling rules were exceeded: ${property."
                        + Exchange.EXCEPTION_CAUGHT + ".message}.")

                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        throw (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
                    }
                })

            .doCatch(StoppingException.class)
                // we want to throw exception, not return fail response
                .log(LoggingLevel.INFO, "Incoming route - asynchronous message was rejected because ESB was stopping.")

                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        throw (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
                    }
                })

            .doCatch(SQLException.class, Exception.class)
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Exception ex = (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
                        Log.error("Incoming route - error during saving incoming message: ", ex);
                    }
                })

                // create FAIL response
                .bean(AsynchInMessageRoute.class, "createFailResponse")

            .end()

            .process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    // nothing to do - it's because correct running unit tests
                }
            });


        // check guaranteed order
        from(URI_GUARANTEED_ORDER_ROUTE)
                .routeId(ROUTE_ID_GUARANTEED_ORDER)
                .errorHandler(noErrorHandler())

                .validate(body().isInstanceOf(Message.class))

                // for case when exception is thrown - message has been already saved into DB
                //  => mark it as PARTLY_FAILED and process it later in standard way
//                .setHeader(AsynchConstants.ASYNCH_MSG_HEADER, constant(true))
                //TODO (juza) finish in 1.1 version + delete errorHandler

                .choice()
                    .when().method(ROUTE_BEAN, "isMsgInGuaranteedOrder")
                        // no guaranteed order or message in the right order => continue

                        .beanRef(ROUTE_BEAN, "saveLogContextParams")

                        .beanRef(ROUTE_BEAN, "setInsertQueueTimestamp")

                        .beanRef(ROUTE_BEAN, "setMsgPriority")

                        // redirect message asynchronously for next processing
                        .to(ExchangePattern.RobustInOnly, AsynchConstants.URI_ASYNC_MSG).id("toAsyncRoute")

                    .otherwise()

                        // message isn't in right guaranteed order => postpone
                        .beanRef(ROUTE_BEAN, "postponeMessage")
                .end()

                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        // nothing to do - it's because correct running unit tests
                    }
                });
    }

    /**
     * Insert new message into database.
     *
     * @param msg message that will be saved
     * @return saved message
     */
    @Handler
    public Message insertMessage(@Body final Message msg) {
        Assert.notNull(msg, "msg can not be null");

        Log.debug("Insert new asynch message '" + msg.toHumanString() + "'.");

        messageService.insertMessage(msg);
        return msg;
    }

    /**
     * Checks if specified message should be processed in guaranteed order and if yes
     * then checks if the message is in the right order.
     *
     * @param msg the asynchronous message
     * @return {@code true} if message's order is ok otherwise {@code false}
     */
    @Handler
    public boolean isMsgInGuaranteedOrder(@Body Message msg) {
        if (!msg.isGuaranteedOrder()) {
            // no guaranteed order => continue
            return true;
        } else {
            // guaranteed order => is the message in the right order?
            List<Message> messages = getBean(MessageService.class)
                    .getMessagesForGuaranteedOrderForRoute(msg.getFunnelValues(), msg.isExcludeFailedState());

            if (messages.size() == 1) {
                Log.debug("There is only one processing message with funnel values: " + msg.getFunnelValues()
                        + " => continue");

                return true;

            // is specified message first one for processing?
            } else if (messages.get(0).equals(msg)) {
                Log.debug("Processing message (msg_id = {}, funnel values = '{}') is the first one"
                        + " => continue", msg.getMsgId(), msg.getFunnelValues());

                return true;

            } else {
                Log.debug("There is at least one processing message with funnel values '{}'"
                        + " before current message (msg_id = {}); message {} will be postponed.",
                        msg.getFunnelValues(), msg.getMsgId(), msg.toHumanString());

                return false;
            }
        }
    }

    @Handler
    public void postponeMessage(Exchange exchange, @Body Message msg) {
        // set Message to header because of event notification
        exchange.getIn().setHeader(AsynchConstants.MSG_HEADER, msg);

        // change state
        getBean(MessageService.class).setStatePostponed(msg);

        // generates event
        AsynchEventHelper.notifyMsgPostponed(exchange);
    }

    /**
     * Checks if ESB goes down or not. If yes then {@link StopService} is thrown.
     */
    @Handler
    public void checkStopping() {
        StopService stopService = getApplicationContext().getBean(StopService.class);

        if (stopService.isStopping()) {
            throw new StoppingException("ESB is stopping ...");
        }
    }

    /**
     * Saves log request ID into header {@link LogContextFilter#CTX_REQUEST_ID}.
     * It's because child threads don't inherits this information from parent thread automatically.
     *
     * @param msg the message
     * @param headers the incoming message headers
     */
    @Handler
    public void saveLogContextParams(@Body Message msg, @Headers Map<String, Object> headers) {
        // request ID should be set from LogContextFilter#initContext
        Map contextMap = MDC.getCopyOfContextMap();

        String requestId = null;
        if (contextMap != null && contextMap.get(LogContextFilter.CTX_REQUEST_ID) != null) {
            requestId = (String) contextMap.get(LogContextFilter.CTX_REQUEST_ID);
            headers.put(LogContextFilter.CTX_REQUEST_ID, requestId);
        }

        LogContextHelper.setLogContextParams(msg, requestId);
    }

    @Handler
    public void setInsertQueueTimestamp(@Headers Map<String, Object> headers) {
        headers.put(AsynchConstants.MSG_QUEUE_INSERT_HEADER, System.currentTimeMillis());
    }

    @Handler
    public void setMsgPriority(@Body Message msg) {
        // new messages will be processed earlier then PARTLY_FAILED or POSTPONED messages
        msg.setProcessingPriority(NEW_MSG_PRIORITY);
    }

    /**
     * Creates OK response.
     *
     * @param exchange the exchange
     * @return CallbackResponse
     */
    @Handler
    public CallbackResponse createOkResponse(Exchange exchange) {
        CallbackResponse callbackResponse = new CallbackResponse();
        callbackResponse.setStatus(ConfirmationTypes.OK);

        return callbackResponse;
    }

    /**
     * Creates FAIL response {@link CallbackResponse}
     * and saves it into "{@value AsynchConstants#ERR_CALLBACK_RES_PROP}" exchange property.
     *
     * @param exchange the exchange
     */
    @Handler
    public void createFailResponse(Exchange exchange) {
        // can be more errors during processing
        if (exchange.getProperty(ERR_CALLBACK_RES_PROP) != null) {
            return;
        }

        CallbackResponse callbackResponse = new CallbackResponse();
        callbackResponse.setStatus(ConfirmationTypes.FAIL);

        // creates error message
        Exception ex = (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
        String additionalInfo;
        if (ex instanceof IntegrationException) {
            additionalInfo = ((IntegrationException) ex).getError() + ": " + ex.getMessage();
        } else {
            additionalInfo = ExceptionTranslator.composeErrorMessage(InternalErrorEnum.E106, ex);
        }
        callbackResponse.setAdditionalInfo(additionalInfo);

        exchange.setProperty(ERR_CALLBACK_RES_PROP, callbackResponse);
    }
}
