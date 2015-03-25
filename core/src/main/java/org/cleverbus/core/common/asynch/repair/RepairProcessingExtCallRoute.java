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

package org.cleverbus.core.common.asynch.repair;

import org.cleverbus.api.entity.ExternalCallStateEnum;
import org.cleverbus.api.route.AbstractBasicRoute;
import org.cleverbus.api.route.CamelConfiguration;

import org.apache.camel.spring.SpringRouteBuilder;
import org.quartz.SimpleTrigger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;


/**
 * Route definition that repairs external calls hooked in the state {@link ExternalCallStateEnum#PROCESSING}.
 * After a specified time these external calls are changed to {@link ExternalCallStateEnum#FAILED} state
 * without increasing failed count.
 */
@CamelConfiguration(value = RepairProcessingExtCallRoute.ROUTE_BEAN)
@Profile("prod")
public class RepairProcessingExtCallRoute extends SpringRouteBuilder {

    public static final String ROUTE_BEAN = "repairProcessingExtCallRoute";

    public static final String JOB_GROUP_NAME = "esbCleverBSS";

    private static final String JOB_NAME = "extCallRepair";

    /**
     * How often to run repair process (in seconds).
     */
    @Value("${asynch.repairRepeatTime}")
    private int repeatInterval;

    @Override
    public final void configure() throws Exception {
        // repair processing messages
        String uri = JOB_GROUP_NAME + "/" + JOB_NAME
                + "?trigger.repeatInterval=" + (repeatInterval * 1000)
                + "&trigger.repeatCount=" + SimpleTrigger.REPEAT_INDEFINITELY;

        from("quartz2://" + uri)
                .routeId("repairExternalCallProcess" + AbstractBasicRoute.ROUTE_SUFFIX)

                .beanRef(RepairExternalCallService.BEAN, "repairProcessingExternalCalls");
    }
}
