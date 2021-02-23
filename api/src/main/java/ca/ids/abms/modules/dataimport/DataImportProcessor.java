package ca.ids.abms.modules.dataimport;

import ca.ids.abms.modules.atcmovements.AtcMovementLogImporter;
import ca.ids.abms.modules.charges.PassengerServiceChargeReturnImporter;
import ca.ids.abms.modules.common.dto.BulkLoaderSummary;
import ca.ids.abms.modules.jobs.JobAlreadyRunningException;
import ca.ids.abms.modules.locking.JobLockingService;
import ca.ids.abms.modules.radarsummary.RadarSummaryImporter;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.towermovements.TowerMovementLogImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class DataImportProcessor implements SchedulingConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(DataImportProcessor.class);

    private final SystemConfigurationService systemConfigurationService;

    @Qualifier(DataImportType.Values.ATC)
    private final AtcMovementLogImporter atcMovementLogImporter;

    @Qualifier(DataImportType.Values.PASSENGER)
    private final PassengerServiceChargeReturnImporter passengerServiceChargeReturnImporter;

    @Qualifier(DataImportType.Values.RADAR)
    private final RadarSummaryImporter radarSummaryImporter;

    @Qualifier(DataImportType.Values.TOWER)
    private final TowerMovementLogImporter towerMovementLogImporter;

    private final JobLockingService jobLockingService;

    private final String processId;

    DataImportProcessor(final SystemConfigurationService systemConfigurationService,
                        final AtcMovementLogImporter atcMovementLogImporter,
                        final PassengerServiceChargeReturnImporter passengerServiceChargeReturnImporter,
                        final RadarSummaryImporter radarSummaryImporter,
                        final TowerMovementLogImporter towerMovementLogImporter,
                        final JobLockingService jobLockingService) {
        this.systemConfigurationService = systemConfigurationService;
        this.atcMovementLogImporter = atcMovementLogImporter;
        this.passengerServiceChargeReturnImporter = passengerServiceChargeReturnImporter;
        this.radarSummaryImporter = radarSummaryImporter;
        this.towerMovementLogImporter = towerMovementLogImporter;
        this.jobLockingService = jobLockingService;
        this.processId = jobLockingService.generateProcessId();
    }

    /**
     * This method reads a set of files from a system configuration location and process it with
     * the Bulk Upload service. This should not run two instances concurrently, thus do NOT use fixedRate
     * for @Scheduled value.
     */
    void doProcess() {

        // if disabled, quietly return
        if (!enabled())
            return;

        // loop through each bulk uploader
        for(DataImportType dataImportType : DataImportType.values()) {
            /* Verify that the job isn't in use by another instance */
            try {
                jobLockingService.check (dataImportType.getValue(), processId);
                LOG.trace("Attempting to import {}.", dataImportType.getValue());
            } catch (JobAlreadyRunningException jare) {
                LOG.trace("Another instance are importing {}.", dataImportType.getValue());
                return;
            }

            // catch ANY exceptions, log, and continue to next data import type
            try {

                // use to hold the results of import
                List<BulkLoaderSummary> results;

                // run appropriate data importer based on data import type
                switch(dataImportType) {
                    case ATC:
                        results = this.atcMovementLogImporter.doImport();
                        break;
                    case PASSENGER:
                        results = this.passengerServiceChargeReturnImporter.doImport();
                        break;
                    case RADAR:
                        results = this.radarSummaryImporter.doImport();
                        break;
                    case TOWER:
                        results = this.towerMovementLogImporter.doImport();
                        break;
                    default:
                        LOG.trace("Could NOT find appropriate importer for data import type {}.",
                            dataImportType.getValue());
                        jobLockingService.complete (dataImportType.getValue(), processId);
                        continue;
                }
                jobLockingService.complete (dataImportType.getValue(), processId);

                // log warning if no results returned
                // else log result size
                if (results == null || results.isEmpty())
                    LOG.trace("Finished import for {}. Nothing was imported.", dataImportType.getValue());
                else
                    LOG.trace("Finished import for {}. {} file(s) imported.",
                        dataImportType.getValue(), results.size());

            } catch (Exception ex) {
                LOG.error("Could not complete the data import of {} because: {}",
                    dataImportType.getValue(), ex.getMessage());
            }
        }
    }

    /**
     * Return true or false if automated upload system configuration is enabled or disabled.
     *
     * @return true for enabled, false disabled
     */
    private boolean enabled() {

        // get data import enabled setting from system configuration
        SystemConfiguration systemConfiguration = systemConfigurationService
            .getOneByItemName(SystemConfigurationItemName.AUTOMATED_UPLOAD_ENABLED);

        // if automated upload enabled setting does not exits, return false
        if (systemConfiguration == null)
            return false;

        // get enabled value as current value or default value if current is null or empty
        String enabled = systemConfiguration.getCurrentValue() == null || systemConfiguration.getCurrentValue().isEmpty()
            ? systemConfiguration.getDefaultValue()
            : systemConfiguration.getCurrentValue();

        // return true or false depending on enabled value
        return enabled != null && enabled.equalsIgnoreCase(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE);
    }

    /**
     * This will get the trigger for automated upload schedule from system configuration. Defaults
     * to 60 minutes if value not found or could not be parsed.
     *
     * @param triggerContext current trigger context
     * @return date for next trigger interval
     */
    private Date trigger(TriggerContext triggerContext) {

        // default to 60, representing minutes
        long interval = 60;

        // get cron value from system configuration table
        SystemConfiguration systemConfiguration = systemConfigurationService
            .getOneByItemName(SystemConfigurationItemName.AUTOMATED_UPLOAD_SCHEDULE);

        // if exists, attempt to get value from current or default system value
        // else return default from above
        if (systemConfiguration != null) {

            // attempt to get system configuration value
            String config = null;
            if(systemConfiguration.getCurrentValue() != null) {
                config = systemConfiguration.getCurrentValue();
            } else if (systemConfiguration.getDefaultValue() != null) {
                config = systemConfiguration.getDefaultValue();
            }

            // if exists, attempt to parse as long
            if (config != null) {
                try {
                    interval = Long.parseLong(config);
                } catch (NumberFormatException ex) {
                    LOG.error("The given value {} is not a valid number, default to {}.",
                        config, interval);
                }
            }
        }

        // return next trigger date from trigger context
        return new PeriodicTrigger(interval, TimeUnit.MINUTES).nextExecutionTime(triggerContext);
    }

    /**
     * Used to manage the scheduling of automated uploads using crontab expressions.
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
