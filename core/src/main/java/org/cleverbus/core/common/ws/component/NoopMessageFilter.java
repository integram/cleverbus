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

package org.cleverbus.core.common.ws.component;

import org.apache.camel.Exchange;
import org.apache.camel.component.spring.ws.filter.MessageFilter;
import org.apache.camel.component.spring.ws.filter.impl.BasicMessageFilter;
import org.springframework.ws.WebServiceMessage;

/**
 * Does no message filtering (post-processing).
 * <p/>
 * Used instead of the default {@link BasicMessageFilter}, which adds Camel Message headers to Soap Message headers,
 * and Camel Message attachments to Soap Message attachments.
 */
public class NoopMessageFilter implements MessageFilter {

    @Override
    public void filterProducer(Exchange exchange, WebServiceMessage producerResponse) {
    }

    @Override
    public void filterConsumer(Exchange exchange, WebServiceMessage consumerResponse) {
    }
}
