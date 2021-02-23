package ca.ids.abms.modules.fiscalyear;

import ca.ids.abms.modules.jobs.JobAlreadyRunningException;
import ca.ids.abms.modules.locking.JobLockingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Date;

@Component
public class FiscalYearProcessor implements SchedulingConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(FiscalYearProcessor.class);

    private static final String CRON_TRIGGER_EXPRESSION = "0 0 * * * *";

    private final FiscalYearService fiscalYearService;

    private final JobLockingService jobLockingService;

    private final String processId;

    public FiscalYearProcessor(final FiscalYearService fiscalYearService,
                               final JobLockingService jobLockingService) {
        this.fiscalYearService = fiscalYearService;
        this.jobLockingService = jobLockingService;
        this.processId = jobLockingService.generateProcessId();
    }

    /**
     * Process fiscal year related tasks.
     */
    private void doProcess() {

        // verify that the job isn't in use by another instance
        try {
            jobLockingService.check(this.getClass().getSimpleName(), processId);
            LOG.trace("Attempting to verify fiscal year : {}.", this.getClass().getSimpleName());
        } catch (JobAlreadyRunningException jare) {
            LOG.trace("Another instance is verifying fiscal year : {}.", this.getClass().getSimpleName());
            return;
        }

        try {
            fiscalYearService.checkFiscalYear();
        } catch (Exception ex) {
            LOG.error("Could not process verify fiscal year because : {}", ex);
        }

        // release job locking for this process id
        jobLockingService.complete(this.getClass().getSimpleName(), processId);
    }

    /**
     * Gets next trigger execution time at the top of every hour of every day.
     *
     * @param triggerContext current trigger context
     * @return date for next trigger interval
     */
    private Date trigger(TriggerContext triggerContext) {
        return new CronTrigger(CRON_TRIGGER_EXPRESSION)
            .nextExecutionTime(triggerContext);
    }

    /**
     * Used to manage the scheduling using crontab expressions.
     *
     * @param scheduledTaskRegistrar registrar for scheduling tasks
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.addTriggerTask(this::doProcess, this::trigger);
    }

    /**
     * This method is used on destroy to release any locks. It should NOT
     * be called directly and is handled by the Spring Framework. This method
     * isn't guaranteed to be called, therefore, a timeout is used as a fallback if necessary within JobLockingServices.
     *
     * Note: All exceptions are caught, logged and squashed. The is required
     * to allow the shutdown process to continue running even on exception to
     * prevent memory leaks.
     */
    @PreDestroy
    @SuppressWarnings("unused")
    public void destroy() {
        try {
            jobLockingService.doResetLocksByProcessId(processId);
        } catch (Exception ex) {
            LOG.error("Could not release scheduler on shutdown: {}", ex.getLocalizedMessage());
        }
    }
}
