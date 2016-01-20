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

package org.cleverbus.spi.msg;

import org.apache.camel.Header;
import org.apache.camel.Properties;
import org.apache.camel.Property;
import org.cleverbus.api.entity.ExternalSystemExtEnum;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.api.exception.ErrorExtEnum;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.cleverbus.api.asynch.AsynchConstants.*;


/**
 * Service for manipulating with {@link Message}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface MessageService {

    public static final String BEAN = "messageService";

    /**
     * Inserts new message.
     *
     * @param message message that will be saved
     */
    void insertMessage(Message message);

    /**
     * Inserts new messages.
     *
     * @param messages the collection of message
     */
    void insertMessages(Collection<Message> messages);

    /**
     * Changes state of the message to {@link MsgStateEnum#OK}.
     * <p/>
     * If message is child message then method checks if all child messages of the parent message aren't processed.
     *
     * @param msg the message
     * @param props the exchange properties [property name; property value]
     */
    void setStateOk(@Header(MSG_HEADER) Message msg, @Properties Map<String, Object> props);

    /**
     * Changes state of the message to {@link MsgStateEnum#PROCESSING}.
     *
     * @param msg the message
     */
    void setStateProcessing(Message msg);

    /**
     * Changes state of the message to {@link MsgStateEnum#WAITING} - only if the message hasn't been already processed.
     *
     * @param msg the message
     */
    void setStateWaiting(@Header(MSG_HEADER) Message msg);

    /**
     * Changes state of the message to {@link MsgStateEnum#WAITING_FOR_RES}.
     *
     * @param msg the message
     */
    void setStateWaitingForResponse(@Header(MSG_HEADER) Message msg);

    /**
     * Changes state of the message to {@link MsgStateEnum#PARTLY_FAILED} but without increasing error count.
     *
     * @param msg the message
     */
    void setStatePartlyFailedWithoutError(@Header(MSG_HEADER) Message msg);

    /**
     * Changes state of the message to {@link MsgStateEnum#PARTLY_FAILED}.
     *
     * @param msg the message
     * @param ex the exception
     * @param errCode the error code that can be explicitly defined if needed
     * @param customData the custom data
     * @param props the exchange properties [property name; property value]
     */
    void setStatePartlyFailed(@Header(MSG_HEADER) Message msg,
                              Exception ex,
                              @Property(EXCEPTION_ERROR_CODE) @Nullable ErrorExtEnum errCode,
                              @Property(CUSTOM_DATA_PROP) @Nullable String customData,
                              @Properties Map<String, Object> props);

    /**
     * Changes state of the message to {@link MsgStateEnum#FAILED}.
     * <p/>
     * If message is child message then parent message will be marked as failed too.
     *
     * @param msg the message
     * @param ex the exception
     * @param errCode the error code that can be explicitly defined if needed
     * @param customData the custom data
     * @param props the exchange properties [property name; property value]
     */
    void setStateFailed(@Header(MSG_HEADER) Message msg,
                        Exception ex,
                        @Property(EXCEPTION_ERROR_CODE) @Nullable ErrorExtEnum errCode,
                        @Property(CUSTOM_DATA_PROP) @Nullable String customData,
                        @Properties Map<String, Object> props);

    /**
     * Changes state of the message to {@link MsgStateEnum#FAILED}.
     * <p/>
     * If message is child message then parent message will be marked as failed too.
     *
     * @param msg the message
     * @param errCode the error code
     * @param errDesc the error description
     */
    void setStateFailed(Message msg, ErrorExtEnum errCode, String errDesc);

    /**
     * Finds message by message ID.
     *
     * @param msgId the message ID
     * @return message or {@code null} if not found message with specified ID
     */
    @Nullable
    Message findMessageById(Long msgId);

    /**
     * Finds message by message ID with eager loading.
     *
     * @param msgId the message ID
     * @return message or {@code null} if not found message with specified ID
     */
    @Nullable
    Message findEagerMessageById(Long msgId);

    /**
     * Finds message by message external system and correlation Id.
     * If system parameter is null then messages are searched by correlation id only.
     *
     * @param correlationId the correlation ID
     * @param systemEnum the external system
     * @return message or {@code null} if not found message with specified correlation ID
     */
    @Nullable
    Message findMessageByCorrelationId(String correlationId, @Nullable ExternalSystemExtEnum systemEnum);

    /**
     * Finds message by substring in payload property.
     *
     * @param substring the substring of payload property
     * @return list of messages or {@code empty list} if not found messages with substring in payload property
     */
    List<Message> findMessagesByContent(String substring);

    /**
     * Get count of messages in specific state.
     *
     * @param state State of message
     * @param interval searching messages updated after this interval (in seconds)
     * @return count of messages
     */
    int getCountMessages(MsgStateEnum state, @Nullable Integer interval);

    /**
     * Get count of processing messages that contains one funnel value from parameter and funnel ID.
     * <p>
     * In {@link Message} will be return items, which has contains only one funnel value from parameter funnelValues.
     * If parameter funnelValues is empty, no {@link Message} will be returned.
     * </p>
     *
     * @param funnelValues the funnel values
     * @param idleInterval interval (in seconds) that determines how long can be message processing
     * @param funnelCompId the funnel component ID
     * @return count of processing messages
     */
    int getCountProcessingMessagesForFunnel(Collection<String> funnelValues, int idleInterval, String funnelCompId);

    /**
     * Gets list of messages that contains one funnel value from parameter for guaranteed processing order of whole
     * routes.
     * <p>
     * In {@link Message} will be return items, which has contains only one funnel value from parameter funnelValues.
     * If parameter funnelValues is empty, no {@link Message} will be returned.
     * </p>
     *
     * @param funnelValues       the funnel values
     * @param excludeFailedState {@link MsgStateEnum#FAILED FAILED} state is used by default;
     *                           use {@code true} if you want to exclude FAILED state
     * @return list of messages ordered by {@link Message#getMsgTimestamp() message timestamp}
     */
    List<Message> getMessagesForGuaranteedOrderForRoute(Collection<String> funnelValues, boolean excludeFailedState);

    /**
     * Gets list of messages that contains one funnel value from parameter for guaranteed processing order of messages
     * for specified funnel.
     * <p>
     * In {@link Message} will be return items, which has contains only one funnel value from parameter funnelValues.
     * If parameter funnelValues is empty, no {@link Message} will be returned.
     * </p>
     *
     * @param funnelValues the funnel values
     * @param idleInterval interval (in seconds) that determines how long can message be processing
     * @param excludeFailedState {@link MsgStateEnum#FAILED FAILED} state is used by default;
     *                           use {@code true} if you want to exclude FAILED state
     * @param funnelCompId the funnel component ID
     * @return list of messages ordered by {@link Message#getMsgTimestamp() message timestamp}
     */
    List<Message> getMessagesForGuaranteedOrderForFunnel(Collection<String> funnelValues, int idleInterval,
            boolean excludeFailedState, String funnelCompId);

    /**
     * Changes state of the message to {@link MsgStateEnum#POSTPONED}.
     *
     * @param msg the message
     */
    void setStatePostponed(@Header(MSG_HEADER) Message msg);

    /**
     * Sets funnel component identifier to specified message.
     *
     * @param msg the message
     * @param funnelCompId the funnel component ID
     */
    void setFunnelComponentId(Message msg, String funnelCompId);

    /**
     * Sets funnel values to specified message.
     *
     * @param msg         the message
     * @param funnelValues the funnel values
     */
    void setFunnelValue(Message msg, Collection<String> funnelValues);

    /**
     * Sets funnel component identifier and funnel values to specified message.
     *
     * @param msg          the message
     * @param funnelCompId the funnel component ID
     * @param funnelValues  the funnel values
     */
    void setFunnelComponentIdAndValue(Message msg, String funnelCompId, Collection<String> funnelValues);
}
