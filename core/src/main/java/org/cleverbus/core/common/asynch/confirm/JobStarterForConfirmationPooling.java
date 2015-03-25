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

import org.cleverbus.common.log.Log;

import org.springframework.beans.factory.annotation.Autowired;


/**
 * Starts new job for pooling confirmations from the queue.
 * <p/>
 * If previous job has still been running then skips this execution and try it next time.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class JobStarterForConfirmationPooling {

    private Boolean isRunning = Boolean.FALSE;

    private final Object lock = new Object();

    @Autowired
    private ConfirmationPollExecutor pollExecutor;

    public void start() throws Exception {
        synchronized (lock) {
            if (isRunning) {
                Log.debug("Job hasn't been started because previous job has still been running.");
                return;
            }

            isRunning = Boolean.TRUE;
        }

        try {
            pollExecutor.run();
        } catch (Exception ex) {
            Log.error("Error occurred during polling confirmations.", ex);
        } finally {
            isRunning = Boolean.FALSE;
        }
    }
}
