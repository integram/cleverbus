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

package org.cleverbus.api.asynch;

import org.cleverbus.api.asynch.model.CallbackResponse;
import org.cleverbus.api.entity.EntityTypeExtEnum;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.ServiceExtEnum;
import org.cleverbus.api.exception.ErrorExtEnum;


/**
 * Constants regarding to asynchronous processing.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public final class AsynchConstants {

    /**
     * Header name that holds the {@link Message} for processing.
     */
    public static final String MSG_HEADER = "processingMessage";

    /**
     * Header name that holds the boolean flag, if message processing was with no effect
     * (the message can't be processed because there are no prepared data in DB so far).
     */
    public static final String NO_EFFECT_PROCESS_HEADER = "noEffectProcess";

    /**
     * Header name that holds the {@link Boolean} if processing is asynchronous (true).
     */
    public static final String ASYNCH_MSG_HEADER = "asynchMsgProcessing";

    /**
     * Property name that holds the custom data (string).
     * <p/>
     * Custom data can be used for saving arbitrary data for transferring state between more processing calls
     * of the asynchronous message.
     */
    public static final String CUSTOM_DATA_PROP = "customData";

    /**
     * Suffix of the property name that holds the business {@link Exception},
     * i.e. exception that means that target system throws expected (business) exception.
     * <p/>
     * Each target system call can generate different exceptions
     * therefore use this constant as suffix for property names.
     */
    public static final String BUSINESS_ERROR_PROP_SUFFIX = "_asynchBusinessError";

    /**
     * Header value that holds target {@link ServiceExtEnum service identification}.
     */
    public static final String SERVICE_HEADER = "asynchService";

    /**
     * Header value that holds target operation name (as string).
     */
    public static final String OPERATION_HEADER = "asynchOperation";

    /**
     * Header value that holds ID of the object in the message.
     * <p/>
     * Object ID with {@link #ENTITY_TYPE_HEADER entity type} and {@link #OPERATION_HEADER operation name}
     * are used for identification of "obsolete" messages.
     */
    public static final String OBJECT_ID_HEADER = "asynchObjectId";

    /**
     * Header value that holds only one funnel value of the message.
     * <p/>
     * Funnel value serves for filtering messages in the route where should be only one processing message
     * with same funnel value.
     */
    public static final String FUNNEL_VALUE_HEADER = "asynchFunnelValue";

    /**
     * Header value that holds all funnel values of the message.
     * <p>
     * As value it must be used {@code Collection<String>}.
     * </p>
     * <p>
     * Between all funnel values of message is OR condition.
     * </p>
     * <p>
     * Funnel value serves for filtering messages in the route where should be only one processing message
     * with same funnel value.
     * </p>
     */
    public static final String FUNNEL_VALUES_HEADER = "asynchFunnelValues";

    /**
     * Header value that holds flag (true/false) if route should be processed in guaranteed order or not.
     *
     * @see #FUNNEL_VALUE_HEADER
     * @see #FUNNEL_VALUES_HEADER
     * @see #EXCLUDE_FAILED_HEADER
     */
    public static final String GUARANTEED_ORDER_HEADER = "guaranteedOrderValue";

    /**
     * Header value that holds flag (true/false) if FAILED state should be considered when guaranteed order is involved.
     *
     * @see #GUARANTEED_ORDER_HEADER
     */
    public static final String EXCLUDE_FAILED_HEADER = "guaranteedOrderWithoutFailedValue";

    /**
     * Header value that holds {@link EntityTypeExtEnum entity type} of the object in the message.
     * <p/>
     * Entity type with {@link #OBJECT_ID_HEADER object ID} and {@link #OPERATION_HEADER operation name}
     * are used for identification of "obsolete" messages.
     * Entity type isn't mandatory, if combination object ID - operation name is enough.
     */
    public static final String ENTITY_TYPE_HEADER = "entityType";

    /**
     * Property that holds {@link CallbackResponse} with FAIL state.
     */
    public static final String ERR_CALLBACK_RES_PROP = "errorCallbackResponse";

    /**
     * Property name that holds {@link ErrorExtEnum error code} of the failure.
     */
    public static final String EXCEPTION_ERROR_CODE = "exceptionErrorCode";


    // ------------- URIs ----------------


    /**
     * URI of the route that translates exceptions. It creates uniform error responses
     * - processor translates Camel exceptions to our exception hierarchy. Calls ExceptionTranslator.
     */
    public static final String URI_EX_TRANSLATION = "direct:exceptionTranslation";

    /**
     * Endpoint URI for processing asynchronous incoming message.
     */
    public static final String URI_ASYNCH_IN_MSG = "direct:asynch_in_message_route";

    /**
     * URI of the route that solves error handling.
     */
    public static final String URI_ERROR_HANDLING = "direct:errorAsyncHandling";

    /**
     * URI of the route that sets current message as FAILED.
     */
    public static final String URI_ERROR_FATAL = "direct:errorAsyncFatal";


    /**
     * URI for asynchronous message processing (with SEDA).
     */
    public static final String URI_ASYNC_MSG = "seda:asynch_message_route"
            + "?concurrentConsumers={{asynch.concurrentConsumers}}&waitForTaskToComplete=Never"
            + "&blockWhenFull=true&queueFactory=#priorityQueueFactory";

    /**
     * URI of the route that makes post-processing after OK message.
     */
    public static final String URI_POST_PROCESS_AFTER_OK = "direct:postProcessAfterOK";

    /**
     * URI of the route that makes post-processing when message failed.
     */
    public static final String URI_POST_PROCESS_AFTER_FAILED = "direct:postProcessAfterFatal";

    /**
     * URI of for ensuring the provided message status is confirmed to the source system.
     */
    public static final String URI_CONFIRM_MESSAGE = "direct:asynch_message_confirm";

    /**
     * Header name that holds timestamp (long in ms) when message is inserted into queue for next processing.
     * Timestamp serves for measuring of waiting time in queue.
     */
    public static final String MSG_QUEUE_INSERT_HEADER = "insertMsgToQueue";


    private AsynchConstants() {
    }
}
