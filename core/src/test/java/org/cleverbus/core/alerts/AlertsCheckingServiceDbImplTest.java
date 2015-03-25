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

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.cleverbus.spi.alerts.AlertInfo;
import org.cleverbus.spi.alerts.AlertListener;
import org.cleverbus.spi.alerts.AlertsConfiguration;
import org.cleverbus.test.AbstractTest;

import org.junit.Test;


/**
 * Test suite for {@link AlertsCheckingServiceDbImpl}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 */
public class AlertsCheckingServiceDbImplTest extends AbstractTest {

    @Test
    public void testCheckAlerts() {
        // prepare data
        AlertsCheckingServiceDbImpl checkingService = new AlertsCheckingServiceDbImpl();

        AlertInfo alert = new AlertInfo("ID", 1, "sql", true, null, null);

        AlertsConfiguration alertsConfig = mock(AlertsConfiguration.class);
        when(alertsConfig.getAlerts(true)).thenReturn(Arrays.asList(alert));

        AlertListener listener = mock(AlertListener.class);
        when(listener.supports(alert)).thenReturn(true);

        AlertsDao alertsDao = mock(AlertsDao.class);
        when(alertsDao.runQuery(anyString())).thenReturn(2L);

        setPrivateField(checkingService, "alertsConfig", alertsConfig);
        setPrivateField(checkingService, "listeners", Arrays.asList(listener));
        setPrivateField(checkingService, "alertsDao", alertsDao);

        // action
        checkingService.checkAlerts();

        // verify
        verify(alertsDao).runQuery(alert.getSql());
        verify(listener).onAlert(alert, 2L);
    }
}
