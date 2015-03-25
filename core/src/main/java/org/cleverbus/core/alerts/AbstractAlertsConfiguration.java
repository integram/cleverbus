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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nullable;

import org.cleverbus.common.log.Log;
import org.cleverbus.spi.alerts.AlertInfo;
import org.cleverbus.spi.alerts.AlertsConfiguration;

import org.springframework.util.Assert;


/**
 * Parent class for alerts configuration.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 */
public abstract class AbstractAlertsConfiguration implements AlertsConfiguration {

    private List<AlertInfo> alerts = new CopyOnWriteArrayList<AlertInfo>();

    protected final void addAlert(AlertInfo alertInfo) {
        Assert.notNull(alertInfo, "alertInfo must not be null");

        // check uniqueness
        if (alerts.contains(alertInfo)) {
            throw new IllegalStateException("Wrong alert's configuration - alert (id = '"
                    + alertInfo.getId() + "') already exist.");
        } else {
            Log.debug("New alert info added: " + alertInfo);
            alerts.add(alertInfo);
        }
    }

    @Override
    public final AlertInfo getAlert(String id) {
        AlertInfo alert = findAlert(id);

        if (alert == null) {
            throw new IllegalArgumentException("There is no alert with id = '" + id + "'");
        }

        return alert;
    }

    @Nullable
    AlertInfo findAlert(String id) {
        for (AlertInfo alert : alerts) {
            if (alert.getId().equals(id)) {
                return alert;
            }
        }

        return null;
    }

    @Override
    public final List<AlertInfo> getAlerts(boolean enabledOnly) {
        if (!enabledOnly) {
            // return all
            return Collections.unmodifiableList(alerts);

        } else {
            // note: wouldn't be better to keep duplicated list of enabled alerts?
            List<AlertInfo> retAlerts = new ArrayList<AlertInfo>();
            for (AlertInfo alert : alerts) {
                if (alert.isEnabled()) {
                    retAlerts.add(alert);
                }
            }

            return retAlerts;
        }
    }
}
