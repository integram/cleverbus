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

package org.cleverbus.core.common.dao;

import java.util.List;

import javax.annotation.Nullable;

import org.cleverbus.api.entity.ExternalSystemExtEnum;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;


/**
 * DAO for {@link Message} entity.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface MessageDao {

    /**
     * Inserts new message.
     *
     * @param msg the message
     */
    void insert(Message msg);

    /**
     * Updates message.
     *
     * @param msg the message
     */
    void update(Message msg);

    /**
     * Finds message by its ID.
     *
     * @param msgId the message ID
     * @return message or {@code null} if not available
     */
    @Nullable
    Message findMessage(Long msgId);

    /**
     * Finds message by its ID with eager loading.
     *
     * @param msgId the message ID
     * @return message or {@code null} if not available
     */
    @Nullable
    Message findEagerMessage(Long msgId);

    /**
     * Gets message by its ID.
     *
     * @param msgId the message ID
     * @return message
     */
    Message getMessage(Long msgId);

    /**
     * Finds all child messages of specified parent message.
     *
     * @param msg the message
     * @return list of child messages
     */
    List<Message> findChildMessages(Message msg);

    /**
     * Finds message by source system and correlation ID.
     *
     * @param correlationId correlation ID
     * @param sourceSystem the source system
     * @return message or {@code null} if not available
     */
    @Nullable
    Message findByCorrelationId(String correlationId, @Nullable ExternalSystemExtEnum sourceSystem);

    /**
     * Finds ONE message in state {@link MsgStateEnum#PARTLY_FAILED}.
     *
     * @param interval Interval (in seconds) between two tries of partly failed messages.
     * @return message or null if there is no any message
     */
    @Nullable
    Message findPartlyFailedMessage(int interval);

    /**
     * Finds ONE message in state {@link MsgStateEnum#POSTPONED}.
     *
     * @param interval Interval (in seconds) after that can be postponed message processed again
     * @return message or null if there is no any message
     */
    @Nullable
    Message findPostponedMessage(int interval);

    /**
     * Updates message (set start timestamp of processing) - gets lock for message.
     *
     * @param msg the message
     * @return true when update was successful otherwise false
     */
    Boolean updateMessageForLock(Message msg);

    /**
     * Finds processing messages.
     *
     * @param interval Interval (in seconds) after that processing messages are probably in dead-lock
     * @return list of messages
     */
    List<Message> findProcessingMessages(int interval);

    /**
     * Gets count of messages in specified state
     *
     * @param state the state
     * @param interval Interval (in seconds) after that messages must be updates; in other words get count
     *                 of messages updated in specified interval
     * @return count
     */
    int getCountMessages(MsgStateEnum state, @Nullable Integer interval);

    /**
     * Gets count of processing messages (PROCESSING, WAITING, WAITING_FOR_RES) with same funnel value
     * and for specified funnel ID.
     *
     * @param funnelValue the funnel value
     * @param idleInterval interval (in seconds) that determines how long can be message processing
     * @param funnelCompId the funnel component ID
     * @return count of messages
     */
    int getCountProcessingMessagesForFunnel(String funnelValue, int idleInterval, String funnelCompId);

    /**
     * Gets list of messages with specified funnel value for guaranteed processing order of whole routes.
     *
     * @param funnelValue the funnel value
     * @param excludeFailedState {@link MsgStateEnum#FAILED FAILED} state is used by default;
     *                           use {@code true} if you want to exclude FAILED state
     * @return list of messages ordered by {@link Message#getMsgTimestamp() message timestamp}
     */
    List<Message> getMessagesForGuaranteedOrderForRoute(String funnelValue, boolean excludeFailedState);

    /**
     * Gets list of messages with specified funnel value for guaranteed processing order of messages
     * for specified funnel.
     *
     * @param funnelValue the funnel value
     * @param idleInterval interval (in seconds) that determines how long can message be processing
     * @param excludeFailedState {@link MsgStateEnum#FAILED FAILED} state is used by default;
     *                           use {@code true} if you want to exclude FAILED state
     * @param funnelCompId the funnel component ID
     * @return list of messages ordered by {@link Message#getMsgTimestamp() message timestamp}
     */
    List<Message> getMessagesForGuaranteedOrderForFunnel(String funnelValue, int idleInterval,
                boolean excludeFailedState, String funnelCompId);

    /**
     * Finds message by substring in message payload.
     *
     * @param substring the substring of payload
     * @return list of message or {@code empty list} if not available
     */
    List<Message> findMessagesByContent(String substring);
}
