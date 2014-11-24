/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
            return Hex.encodeHexString((byte[]) obj.getBody());
        } else if (obj.getBody() instanceof HumanReadable) {
            return ((HumanReadable) obj.getBody()).toHumanString();
        } else {
            // otherwise return string by camel converter resolution
            return obj.getBody(String.class);
        }
    }
}