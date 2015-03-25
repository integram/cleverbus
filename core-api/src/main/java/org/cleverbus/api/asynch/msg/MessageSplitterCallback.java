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

package org.cleverbus.api.asynch.msg;

import java.util.List;

import org.cleverbus.api.entity.Message;


/**
 * Callback contract for getting child messages.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface MessageSplitterCallback {

    /**
     * Gets child messages for next processing.
     * Order of child messages in the list determines order of first synchronous processing.
     *
     * @param parentMsg the parent message
     * @param body the exchange body
     * @return list of child messages
     */
    List<ChildMessage> getChildMessages(Message parentMsg, Object body);
}
