package org.cleverbus.core.archiving;

import org.apache.camel.Handler;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.commons.lang3.StringUtils;
import org.cleverbus.api.archiving.ProcessArchivingDataJob;
import org.cleverbus.api.route.AbstractBasicRoute;
import org.cleverbus.api.route.CamelConfiguration;
import org.cleverbus.common.log.Log;
import org.cleverbus.core.archiving.db.ArchivingDatabaseScriptJob;
import org.cleverbus.core.common.asynch.repair.RepairProcessingMsgRoute;
import org.cleverbus.core.common.asynch.stop.StopService;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;

/**
 * Route definition that starts job process that archiving data.
 * <p>
 * Class that will be called for archiving data must implement interface {@link ProcessArchivingDataJob} and
 * must be a spring bean.
 * </p>
 *
 * @author Radek Čermák [<a href="mailto:radek.cermak@cleverlance.com">radek.cermak@cleverlance.com</a>]
 * @see ArchivingDatabaseScriptJob
 * @see ProcessArchivingDataJob
 * @since 1.4.15
 */
@CamelConfiguration(value = ProcessArchivingDataRoute.ROUTE_BEAN)
@Profile("prod")
public class ProcessArchivingDataRoute extends SpringRouteBuilder {

    /**
     * Bean name.
     */
    static final String ROUTE_BEAN = "processArchivingDataBean";

    /**
     * True - archiving data is enable, false - archiving data is disabled (default value is false).
     */
    @Value("${archiving.task.enabled:false}")
    private Boolean enableArchiving;

    /**
     * Name of archiving job in quartz.
     */
    @Value("${archiving.task.type:}")
    private String archJobName;

    /**
     * Schedule cron expression, when it will be archived running.
     */
    @Value("${archiving.task.schedule:}")
    private String scheduleCronExp;

    /**
     * Bean name of archiving class (must implement interface {@link ProcessArchivingDataJob}).
     */
    @Value("${archiving.task.beanName:}")
    private String archBeanName;

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure() throws Exception {
        if (enableArchiving) {
            if (StringUtils.isBlank(archJobName)) {
                throw new IllegalStateException("Property archiving.task.type can not be empty.");
            }
            if (StringUtils.isBlank(scheduleCronExp)){
                throw new IllegalStateException("Property archiving.task.schedule are not defined.");
            }
            if (StringUtils.isBlank(archBeanName)){
                throw new IllegalStateException("Property archiving.task.beanName are not defined.");
            }

            String uri = RepairProcessingMsgRoute.JOB_GROUP_NAME + "/" + archJobName + "?cron=" + scheduleCronExp;
            //next run will be called after previous is finished
            uri += "&stateful=true";

            Object archJobBean;
            try {
                archJobBean = getApplicationContext().getBean(archBeanName);
                //is only for test if archiving bean implements right interface
                if (!ProcessArchivingDataJob.class.isAssignableFrom(archJobBean.getClass())) {
                    throw new IllegalStateException("Bean with name '" + archBeanName
                            + "' must implement interface '" + ProcessArchivingDataJob.class.getName() + "'.");
                }
            } catch (NoSuchBeanDefinitionException e) {
                throw new IllegalStateException("Bean with name '" + archBeanName
                        + "' defined in property archiving.task.beanName not found.", e);
            }

            from("quartz2://" + uri)
                    .routeId("processArchivingData" + AbstractBasicRoute.ROUTE_SUFFIX)
                    .choice().when().method(ROUTE_BEAN, "isNotInStoppingMode")
                        .bean(archJobBean, "startArchivingData")
                    .end();
        }
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
