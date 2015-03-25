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

/**
 * Contract for managing ESB stopping.
 * <p/>
 * It depends on service implementation if stopping is valid for one node or whole cluster.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 */
public interface StopService {

    /**
     * Is ESB stopping?
     *
     * @return {@code true} if ESB is in "stopping mode" otherwise {@code false}
     */
    boolean isStopping();


    /**
     * Stop ESB, switches to stopping mode. Can be called repeatedly.
     * <p/>
     * ESB won't to process next asynchronous messages.
     */
    void stop();


    /**
     * Cancels ESB stopping, switches back to normal mode.
     * Can be called repeatedly.
     */
    void cancelStopping();

}
