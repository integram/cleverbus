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

package org.cleverbus.api.event;

import org.cleverbus.api.entity.Message;

import org.apache.camel.Exchange;
import org.apache.camel.management.event.AbstractExchangeEvent;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.util.Assert;


/**
 * Base class for asynchronous {@link Message message} events.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public abstract class AbstractAsynchEvent extends AbstractExchangeEvent {

    private final Message message;

    /**
     * Creates new event.
     *
     * @param exchange the exchange
     * @param message the message
     */
    public AbstractAsynchEvent(Exchange exchange, Message message) {
        super(exchange);

        Assert.notNull(message, "message must not be null");

        this.message = message;
    }

    /**
     * Gets asynchronous message.
     *
     * @return message
     */
    public Message getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("exchange", getExchange())
            .append("message", getMessage())
            .toString();
    }
}
