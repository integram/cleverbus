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

package org.cleverbus.spi;

import org.cleverbus.api.event.CompletedMsgAsynchEvent;
import org.cleverbus.api.event.FailedMsgAsynchEvent;
import org.cleverbus.api.event.PartlyFailedMsgAsynchEvent;
import org.cleverbus.api.event.PostponedMsgAsynchEvent;
import org.cleverbus.api.event.ProcessingMsgAsynchEvent;
import org.cleverbus.api.event.WaitingMsgAsynchEvent;

import org.apache.camel.Exchange;


/**
 * Contract for sending event notifications about states of asynchronous message processing.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface AsyncEventNotifier {

    /**
     * Creates an {@link CompletedMsgAsynchEvent} for successfully completed asynch message.
     *
     * @param exchange the exchange
     */
    void notifyMsgCompleted(Exchange exchange);

    /**
     * Creates an {@link PartlyFailedMsgAsynchEvent} for partly failed asynch message.
     *
     * @param exchange the exchange
     */
    void notifyMsgPartlyFailed(Exchange exchange);

    /**
     * Creates an {@link FailedMsgAsynchEvent} for failed asynch message.
     *
     * @param exchange the exchange
     */
    void notifyMsgFailed(Exchange exchange);

    /**
     * Creates an {@link WaitingMsgAsynchEvent} for waiting asynch message.
     *
     * @param exchange the exchange
     */
    void notifyMsgWaiting(Exchange exchange);

    /**
     * Creates an {@link ProcessingMsgAsynchEvent} for processing asynch message.
     *
     * @param exchange the exchange
     */
    void notifyMsgProcessing(Exchange exchange);

    /**
     * Creates an {@link PostponedMsgAsynchEvent} for postponed asynch message.
     *
     * @param exchange the exchange
     */
    void notifyMsgPostponed(Exchange exchange);
}


