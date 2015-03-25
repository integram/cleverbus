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

package org.cleverbus.core.common.asynch.queue;

import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.api.route.AbstractBasicRoute;
import org.cleverbus.api.route.CamelConfiguration;
import org.cleverbus.common.log.Log;
import org.cleverbus.core.common.asynch.repair.RepairProcessingMsgRoute;
import org.cleverbus.core.common.asynch.stop.StopService;

import org.apache.camel.Handler;
import org.apache.camel.spring.SpringRouteBuilder;
import org.quartz.SimpleTrigger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;


/**
 * Route definition that starts job process that pools message queue (=database)
 * and takes {@link MsgStateEnum#PARTLY_FAILED} messages for further processing.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@CamelConfiguration(value = PartlyFailedMessagesPoolRoute.ROUTE_BEAN)
@Profile("prod")
public class PartlyFailedMessagesPoolRoute extends SpringRouteBuilder {

    public static final String ROUTE_BEAN = "partlyFailedMsgPoolRouteBean";

    private static final String JOB_NAME = "partlyFailedPool";

    /**
     * How often to run process for polling partly failed messages (in seconds).
     */
    @Value("${asynch.partlyFailedRepeatTime}")
    private int partlyFailedRepeatTime;

    @Override
    @SuppressWarnings("unchecked")
    public final void configure() throws Exception {
        String uri = RepairProcessingMsgRoute.JOB_GROUP_NAME + "/" + JOB_NAME
                + "?trigger.repeatInterval=" + (partlyFailedRepeatTime * 1000)
                + "&trigger.repeatCount=" + SimpleTrigger.REPEAT_INDEFINITELY;


        from("quartz2://" + uri)
                .routeId("partlyFailedMessageProcess" + AbstractBasicRoute.ROUTE_SUFFIX)

                // allow only if ESB not stopping
                .choice().when().method(ROUTE_BEAN, "isNotInStoppingMode")
                    .beanRef("jobStarterForMessagePooling", "start")
                .end();
    }

    /**
     * Checks if ESB goes down or not.
     *
     * @return {@code true} if ESB is in "stopping mode" otherwise {@code false}
     */
    @Handler
    public boolean isNotInStoppingMode() {
        StopService stopService = getApplicationContext().getBean(StopService.class);

        Log.debug("ESB stopping mode is switched on: " + stopService.isStopping());

        return !stopService.isStopping();
    }
}
