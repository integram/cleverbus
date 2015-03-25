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

package org.cleverbus.core.alerts;

import java.util.Collection;
import java.util.List;

import org.cleverbus.common.log.Log;
import org.cleverbus.spi.alerts.AlertInfo;
import org.cleverbus.spi.alerts.AlertListener;
import org.cleverbus.spi.alerts.AlertsConfiguration;

import org.springframework.beans.factory.annotation.Autowired;


/**
 * Default implementation of {@link AlertsCheckingService} interface.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 */
public abstract class AbstractAlertsCheckingService implements AlertsCheckingService {

    @Autowired
    private AlertsConfiguration alertsConfig;

    @Autowired
    private Collection<AlertListener> listeners;

    @Override
    public final void checkAlerts() {
        Log.debug("Alerts checking starts ...");

        if (listeners.isEmpty()) {
            Log.debug("There is no listeners => not reason for alerts checking.");

            return;
        }

        List<AlertInfo> alerts = alertsConfig.getAlerts(true);

        for (AlertInfo alert : alerts) {
            long count = getCount(alert);

            if (count > alert.getLimit()) {
                Log.debug("Actual count=" + count + " exceeded limit (" + alert.getLimit()
                        + ") of alert (" + alert.toHumanString() + ")");

                // notify all listeners
                for (AlertListener listener : listeners) {
                    try {
                        if (listener.supports(alert)) {
                            listener.onAlert(alert, count);
                        }
                    } catch (Exception ex) {
                        Log.error("Listener (" + listener.getClass().getName() + ") for alert (" + alert
                                + ") ends with exception.", ex);
                    }
                }
            }
        }

        Log.debug("Alerts checking ends.");
    }

    /**
     * Gets actual count of specified alert.
     *
     * @param alert the alert info
     * @return count of items from the alert query
     */
    protected abstract long getCount(AlertInfo alert);
}
