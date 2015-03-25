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

package org.cleverbus.core.common.asynch.stop;

import org.cleverbus.common.log.Log;


/**
 * Implementation of {@link StopService} for one ESB node/instance.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 */
public class StopServiceNodeImpl implements StopService {

    private boolean stopping = false;

    @Override
    public boolean isStopping() {
        return stopping;
    }

    @Override
    public synchronized void stop() {
        this.stopping = true;

        Log.info("ESB starts stopping ...");
    }

    @Override
    public synchronized void cancelStopping() {
        this.stopping = false;

        Log.info("ESB stopping was canceled.");
    }
}
