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

package org.cleverbus.api.entity;

/**
 * Enumeration of possible message states.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public enum MsgStateEnum {

    /**
     * New saved message.
     */
    NEW,

    /**
     * Successfully processed message.
     */
    OK,

    /**
     * Message is just processing.
     */
    PROCESSING,

    /**
     * Last processing ended with error, there will be next try.
     */
    PARTLY_FAILED,

    /**
     * Finally failed message, no next processing.
     */
    FAILED,

    /**
     * Parent message that waits for child messages.
     */
    WAITING,

    /**
     * Message that waits for confirmation/response from external system (e.g. from VF)
     */
    WAITING_FOR_RES,

    /**
     * Message is postponed because there was another message that was processed at the same time
     * with same funnel values.
     */
    POSTPONED,

    /**
     * Message was canceled by external system or by administrator. This state isn't set by this application.
     */
    CANCEL;


    /**
     * Is specified state running?
     *
     * @param state the state
     * @return {@code true} if state is running otherwise {@code false}
     */
    public static boolean isRunning(MsgStateEnum state) {
        return state == PROCESSING || state == WAITING || state == WAITING_FOR_RES;
    }

    /**
     * Is specified state processing?
     *
     * @param state the state
     * @return {@code true} if state is processing otherwise {@code false}
     */
    public static boolean isProcessing(MsgStateEnum state) {
        return isRunning(state) || state == PARTLY_FAILED || state == POSTPONED;
    }

    /**
     * Is specified state final?
     *
     * @param state the state
     * @return {@code true} if state is final otherwise {@code false}
     */
    public static boolean isFinal(MsgStateEnum state) {
        return state == OK || state == CANCEL || state == FAILED;
    }
}
