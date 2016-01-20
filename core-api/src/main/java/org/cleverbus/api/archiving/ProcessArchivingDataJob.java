package org.cleverbus.api.archiving;

/**
 * Interface for archiving data job, that can be schedule with cron expression.
 * <p>
 * Class that implement this interface must be a spring bean.
 * </p>
 *
 * @author Radek Čermák [<a href="mailto:radek.cermak@cleverlance.com">radek.cermak@cleverlance.com</a>]
 * @since 1.4.15
 */
public interface ProcessArchivingDataJob {

    /**
     * Method will be called when task scheduling for data archiving.
     */
    void startArchivingData();
}
