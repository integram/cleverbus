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

import org.cleverbus.api.route.AbstractBasicRoute;
import org.cleverbus.api.route.CamelConfiguration;
import org.cleverbus.core.common.asynch.repair.RepairProcessingMsgRoute;

import org.apache.camel.spring.SpringRouteBuilder;
import org.quartz.SimpleTrigger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;


/**
 * Route definition that starts job process that pools failed confirmations for next processing.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@CamelConfiguration(value = ConfirmationsPoolRoute.ROUTE_BEAN)
@Profile("prod")
public class ConfirmationsPoolRoute extends SpringRouteBuilder {

    public static final String ROUTE_BEAN = "confirmationsPoolRouteBean";

    private static final String JOB_NAME = "confirmationPool";

    /**
     * How often to run process for pooling failed confirmations (in seconds).
     */
    @Value("${asynch.confirmation.repeatTime}")
    private int repeatTime;

    @Override
    @SuppressWarnings("unchecked")
    public final void configure() throws Exception {
        String uri = RepairProcessingMsgRoute.JOB_GROUP_NAME + "/" + JOB_NAME
                + "?trigger.repeatInterval=" + (repeatTime * 1000)
                + "&trigger.repeatCount=" + SimpleTrigger.REPEAT_INDEFINITELY;

        from("quartz2://" + uri)
                .routeId("confirmationsPool" + AbstractBasicRoute.ROUTE_SUFFIX)

                .beanRef("jobStarterForConfirmationPooling", "start");
    }
}
