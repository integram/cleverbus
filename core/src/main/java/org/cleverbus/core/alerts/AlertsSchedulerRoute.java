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

import org.cleverbus.api.route.AbstractBasicRoute;
import org.cleverbus.api.route.CamelConfiguration;
import org.cleverbus.core.common.asynch.repair.RepairProcessingMsgRoute;

import org.apache.camel.spring.SpringRouteBuilder;
import org.quartz.SimpleTrigger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;


/**
 * Route definition that starts checking alerts by scheduler.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 */
@CamelConfiguration(value = AlertsSchedulerRoute.ROUTE_BEAN)
@Profile("prod")
public class AlertsSchedulerRoute extends SpringRouteBuilder {

    public static final String ROUTE_BEAN = "alertsSchedulerRoute";

    private static final String JOB_NAME = "alerts";

    /**
     * How often to run checking of alerts (in seconds).
     */
    @Value("${alerts.repeatTime}")
    private int repeatInterval;

    @Override
    public final void configure() throws Exception {
        if (repeatInterval != -1) {
            String uri = RepairProcessingMsgRoute.JOB_GROUP_NAME + "/" + JOB_NAME
                    + "?trigger.repeatInterval=" + (repeatInterval * 1000)
                    + "&trigger.repeatCount=" + SimpleTrigger.REPEAT_INDEFINITELY;

            from("quartz2://" + uri)
                    .routeId("alerts" + AbstractBasicRoute.ROUTE_SUFFIX)

                    .beanRef(AlertsCheckingService.BEAN, "checkAlerts");
        }
    }
}
