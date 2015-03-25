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

package org.cleverbus.core.common.asynch.msg;

import java.util.Comparator;

import org.cleverbus.api.entity.Message;

import org.apache.camel.Exchange;
import org.springframework.util.Assert;


/**
 * Comparator sorts exchanges in the queue by {@link Message#getProcessingPriority() message priority of processing}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 */
public class MsgPriorityComparator implements Comparator<Exchange> {

    @Override
    public int compare(Exchange ex1, Exchange ex2) {
        Message msg1 = ex1.getIn().getBody(Message.class);
        Message msg2 = ex2.getIn().getBody(Message.class);

        Assert.notNull(msg1, "msg1 must not be null");
        Assert.notNull(msg2, "msg2 must not be null");

        return ((Integer)msg1.getProcessingPriority()).compareTo(msg2.getProcessingPriority());
    }
}
