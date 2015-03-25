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

package org.cleverbus.spi.extcall;

import org.cleverbus.api.entity.ExternalCall;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.exception.LockFailureException;

/**
 * Contract for checking duplicate and obsolete calls.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface ExternalCallService {

    /**
     * Prepares a new external call for the specified unique operation invocation.
     * Operation URI and operation key (such as object ID) together
     * uniquely identify such an operation invocation.
     * <p/>
     * The attempted external call might not be allowed.
     * This could happen if the external call is either duplicate or outdated.
     * To verify it's a new call, the provided {@link Message#getMsgTimestamp()}
     * will be compared with an existing known one (if any) for this invocation:
     * if the provided timestamp is newer, the call is considered new,
     * otherwise the call is considered outdated or duplicate.
     *
     * @param operationUri the operation URI that the external call is done via
     * @param operationKey the operation key that is to be unique for this operation URI
     *                     (object ID for edits, correlation ID for single-entity creation messages, etc.)
     * @param message      the message with the correct msg timestamp
     * @return an instance of {@link ExternalCall}, if the attempted call is new and is prepared to be made;
     * null if the call is duplicate or outdated and should NOT be made
     * @throws LockFailureException if the requested call is currently in the processing state
     *                              and therefore is temporarily not allowed to be repeated
     */
    ExternalCall prepare(String operationUri, String operationKey, Message message);

    /**
     * Marks the external call as a successfully executed call.
     *
     * @param externalCall the external call to be finalized.
     */
    void complete(ExternalCall externalCall);

    /**
     * Marks the external call as an unsuccessfully executed call.
     *
     * @param externalCall the external call to be finalized.
     */
    void failed(ExternalCall externalCall);

}
