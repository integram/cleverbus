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

package org.cleverbus.core.throttling;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.cleverbus.common.log.Log;
import org.cleverbus.spi.throttling.ThrottleCounter;
import org.cleverbus.spi.throttling.ThrottleScope;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.springframework.util.Assert;


/**
 * In-memory implementation of {@link ThrottleCounter} interface.
 * <p/>
 * Fast and enough-solution for one server solution but it's not sufficient for cluster environment.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ThrottleCounterMemoryImpl implements ThrottleCounter {

    private static final int DUMP_PERIOD = 60;

    /**
     * List of timestamps of incoming requests for specified interval per throttling scope.
     */
    private Map<ThrottleScope, List<Long>> requests = new ConcurrentHashMap<ThrottleScope, List<Long>>();

    private volatile Date lastDumpTimestamp = new Date();

    private final ReentrantLock lock = new ReentrantLock();

    private final Set<ThrottleScope> scopesInProgress = new HashSet<ThrottleScope>();

    private static final Object OBJ_LOCK = new Object();

    @Override
    public int count(ThrottleScope throttleScope, int interval) {
        Assert.notNull(throttleScope, "the throttleScope must not be null");
        Assert.isTrue(interval > 0, "the interval must be positive value");

        int counter = 0;
        boolean toLock = false;

        // is it necessary to lock thread? Only if two same throttle scopes are processed at the same time
        synchronized (OBJ_LOCK) {
            if (scopesInProgress.contains(throttleScope)) {
                toLock = true;
            } else {
                scopesInProgress.add(throttleScope);
            }
        }

        if (toLock) {
            lock.lock();
        }

        try {
            if (requests.get(throttleScope) == null) {
                requests.put(throttleScope, new Stack<Long>());
            }

            long now = DateTime.now().getMillis();
            long from = now - (interval * 1000);

            // get timestamps for specified throttling scope
            List<Long> timestamps = requests.get(throttleScope);
            timestamps.add(now);

            // count requests for specified interval
            int lastIndex = -1;
            for (int i = timestamps.size() - 1; i >= 0; i--) {
                long timestamp = timestamps.get(i);

                if (timestamp >= from) {
                    counter++;
                } else {
                    lastIndex = i;
                    break;
                }
            }

            // remove old timestamps
            if (lastIndex > 0) {
                for (int i = 0; i <= lastIndex; i++) {
                    timestamps.remove(0);
                }
            }
        } finally {
            synchronized (OBJ_LOCK) {
                scopesInProgress.remove(throttleScope);
            }

            if (toLock) {
                lock.unlock();
            }
        }


        // make dump only once in the specified interval
        if (Log.isDebugEnabled() && (DateUtils.addSeconds(new Date(), -DUMP_PERIOD).after(lastDumpTimestamp))) {
            dumpMemory();

            lastDumpTimestamp = new Date();
        }

        return counter;
    }

    /**
     * Dumps throttling memory to log on "debug" level.
     */
    void dumpMemory() {
        StringBuilder dump = new StringBuilder();
        dump.append("Throttling in-memory dump:\n");

        for (Map.Entry<ThrottleScope, List<Long>> en : requests.entrySet()) {
            dump.append("sourceSystem=");
            dump.append(en.getKey().getSourceSystem());
            dump.append(", serviceName=");
            dump.append(en.getKey().getServiceName());
            dump.append(": ");
            dump.append(en.getValue().size());
            dump.append("\n");
        }

        Log.debug(dump.toString());
    }
}
