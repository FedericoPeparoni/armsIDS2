package ca.ids.abms.modules.cachedevents;

import ca.ids.abms.modules.jobs.JobAlreadyRunningException;
import ca.ids.abms.modules.locking.JobLockingService;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.spring.cache.TriggerIntervalProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

public class CachedEventTrigger implements TriggerIntervalProvider {

    private static final Logger LOG = LoggerFactory.getLogger(CachedEventTrigger.class);

    static final long DEFAULT_INTERVAL = 60L;

    static final TimeUnit DEFAULT_INTERVAL_UNIT = TimeUnit.MINUTES;

    private final JobLockingService jobLockingService;

    private final SystemConfigurationService systemConfigurationService;

    private final String processId;

    public CachedEventTrigger(JobLockingService jobLockingService,
                              SystemConfigurationService systemConfigurationService) {
        this.jobLockingService = jobLockingService;
        this.systemConfigurationService = systemConfigurationService;
        this.processId = jobLockingService.generateProcessId();
    }

    @Override
    public long getInterval() {

        // get interval value from system configuration table
        String config = systemConfigurationService
            .getValue(SystemConfigurationItemName.CACHED_EVENT_RETRY_INTERVAL);

        // if exists, attempt to parse as long
        if (config != null) {
            try {
                return Long.parseLong(config);
            } catch (NumberFormatException ex) {
                LOG.error("The given value {} is not a valid number, default to {} because : {}",
                    config, DEFAULT_INTERVAL, ex);
            }
        }

        // return default value if config value not found or parsed
        return DEFAULT_INTERVAL;
    }

    @Override
    public TimeUnit getIntervalUnit() {
        return DEFAULT_INTERVAL_UNIT;
    }

    @Override
    public boolean isLocked() {
        // attempt to check lock status and subsequently lock if successful
        // no need to use `setLock(true)` as done here
        // return true if lock set, else false if failed
        try {
            jobLockingService.check(this.getClass().getSimpleName(), processId, false);
            return false;
        } catch (JobAlreadyRunningException ex) {
            return true;
        }
    }

    @Override
    public void setLock(boolean isLocked) {
        // release process job lock if false
        // else ignored as set in `isLocked()`
        if (!isLocked) {
            jobLockingService.complete(this.getClass().getSimpleName(), processId);
        }
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
