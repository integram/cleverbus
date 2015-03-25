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

package org.cleverbus.spi.alerts;

import java.util.List;


/**
 * Alerts configuration contract.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 */
public interface AlertsConfiguration {

    /**
     * Gets alert info.
     *
     * @param id the alert identifier
     * @return {@link AlertInfo}
     */
    AlertInfo getAlert(String id);

    /**
     * Gets list of all alerts.
     *
     * @param enabledOnly if {@code true} then only enabled alerts are returned
     * @return list of all alerts in order from configuration file
     */
    List<AlertInfo> getAlerts(boolean enabledOnly);

}
