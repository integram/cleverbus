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

package org.cleverbus.core.common.asynch.confirm;

import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.entity.ExternalCall;
import org.cleverbus.api.exception.LockFailureException;
import org.cleverbus.common.log.Log;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Reads confirmations (=external calls) from DB and sends them for next processing.
 * Execution will stop when there is no further confirmation for processing.
 * <p/>
 * This executor is invoked by {@link JobStarterForConfirmationPooling}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ConfirmationPollExecutor implements Runnable {

    private static final int LOCK_FAILURE_LIMIT = 5;

    @Autowired
    private ConfirmationPool confirmationPool;

    @Autowired
    private ProducerTemplate producerTemplate;

    // note: this is because of setting different target URI for tests
    private String targetURI = AsynchConstants.URI_CONFIRM_MESSAGE;

    @Override
    public void run() {
        Log.debug("Confirmation pooling starts ...");

        // is there confirmation for processing?
        ExternalCall extCall = null;
        int lockFailureCount = 0;
        while (true) {
            try {
                extCall = confirmationPool.getNextConfirmation();
                if (extCall != null) {
                    // sends confirmation for next processing
                    producerTemplate.sendBody(targetURI, extCall);
                } else {
                    //there is no new confirmation for processing
                    //  => finish this executor and try it again after some time
                    break;
                }
            } catch (LockFailureException ex) {
                // try again to acquire next message with lock
                lockFailureCount++;

                if (lockFailureCount > LOCK_FAILURE_LIMIT) {
                    Log.warn("Probably problem with locking confirmations - count of lock failures exceeds limit ("
                            + LOCK_FAILURE_LIMIT + ").");
                    break;
                }
            } catch (Exception ex) {
                Log.error("Error occurred while getting confirmations "
                        + (extCall != null ? extCall.toHumanString() : ""), ex);
            }
        }

        Log.debug("Confirmation pooling finished.");
    }
}
