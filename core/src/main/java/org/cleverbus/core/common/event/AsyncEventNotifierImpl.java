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

package org.cleverbus.core.common.event;

import org.cleverbus.spi.AsyncEventNotifier;

import org.apache.camel.Exchange;


/**
 * Implementation of {@link AsyncEventNotifier}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class AsyncEventNotifierImpl implements AsyncEventNotifier {

    @Override
    public void notifyMsgCompleted(Exchange exchange) {
        AsynchEventHelper.notifyMsgCompleted(exchange);
    }

    @Override
    public void notifyMsgPartlyFailed(Exchange exchange) {
        AsynchEventHelper.notifyMsgPartlyFailed(exchange);
    }

    @Override
    public void notifyMsgFailed(Exchange exchange) {
        AsynchEventHelper.notifyMsgFailed(exchange);
    }

    @Override
    public void notifyMsgWaiting(Exchange exchange) {
        AsynchEventHelper.notifyMsgWaiting(exchange);
    }

    @Override
    public void notifyMsgProcessing(Exchange exchange) {
        AsynchEventHelper.notifyMsgProcessing(exchange);
    }

    @Override
    public void notifyMsgPostponed(Exchange exchange) {
        AsynchEventHelper.notifyMsgPostponed(exchange);
    }
}
