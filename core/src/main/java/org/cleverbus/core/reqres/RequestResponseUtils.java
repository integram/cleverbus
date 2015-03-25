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

package org.cleverbus.core.reqres;

import org.apache.camel.Message;
import org.apache.commons.codec.binary.Hex;
import org.cleverbus.api.common.HumanReadable;

/**
 * {@code RequestResponseUtils} is a collection of utility methods for use in request-response functionality.
 *
 * @author <a href="mailto:hanusto@gmail.com">Tomas Hanus</a>
 * @see RequestSendingEventNotifier
 * @see ResponseReceiveEventNotifier
 * @since 1.2
 */
public final class RequestResponseUtils {

    private RequestResponseUtils() {
    }

    /**
     * Transforms {@link Message#getBody() body} of message to appropriate data {@link String type}.
     *
     * @param obj the message
     * @return the string representation of message
     */
    public static String transformBody(Message obj) {
        if (obj.getBody() == null) {
            return "";
        }
        // to avoid lookup camel converters
        if (obj.getBody() instanceof String) {
            return (String) obj.getBody();
        } else if (obj.getBody() instanceof byte[]) {
            // common exchange format is XML
            String result = obj.getBody(String.class) == null ? new String((byte[]) obj.getBody())
                : obj.getBody(String.class);
            if (result != null && result.trim().length() > 0 && result.trim().startsWith("<")
                && result.trim().endsWith(">")) {
                return result.trim();
            }
            return Hex.encodeHexString((byte[]) obj.getBody());
        } else if (obj.getBody() instanceof HumanReadable) {
            return ((HumanReadable) obj.getBody()).toHumanString();
        } else {
            // otherwise return string by camel converter resolution
            return obj.getBody(String.class);
        }
    }
}