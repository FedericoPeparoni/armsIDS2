package ca.ids.abms.plugins.kcaa.erp.modules.aircraftregistrations;

import ca.ids.abms.modules.jobs.JobAlreadyRunningException;
import ca.ids.abms.modules.locking.JobLockingService;
import ca.ids.abms.modules.plugins.PluginService;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.plugins.PluginKey;
import ca.ids.abms.plugins.kcaa.erp.modules.system.KcaaErpConfigurationItemName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.xml.bind.DatatypeConverter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class KcaaErpAircraftRegistrationProcessor implements SchedulingConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(KcaaErpAircraftRegistrationProcessor.class);

    private final JobLockingService jobLockingService;

    private final KcaaErpAircraftRegistrationService kcaaErpAircraftRegistrationService;

    private final PluginService pluginService;

    private final SystemConfigurationService systemConfigurationService;

    private final String processId;

    @Value("${app.plugins.kcaa.erp.aircraft-registration-retrieval-interval}")
    @SuppressWarnings("FieldCanBeLocal")
    private Integer interval = 5;

    public KcaaErpAircraftRegistrationProcessor(
        final JobLockingService jobLockingService,
        final KcaaErpAircraftRegistrationService kcaaErpAircraftRegistrationService,
        final PluginService pluginService,
        final SystemConfigurationService systemConfigurationService
    ) {
        this.jobLockingService = jobLockingService;
        this.kcaaErpAircraftRegistrationService = kcaaErpAircraftRegistrationService;
        this.pluginService = pluginService;
        this.systemConfigurationService = systemConfigurationService;

        this.processId = jobLockingService.generateProcessId();
    }

    /**
     * Get last import timestamp from system configuration.
     *
     * @return last import timestamp
     */
    private byte[] getPreviousTimestamp() {
        String timestamp = systemConfigurationService
            .getValue(KcaaErpConfigurationItemName.AIRCRAFT_REGISTRATION_PROCESSOR_STARTING_TIMESTAMP);
        if (timestamp == null || timestamp.isEmpty())
            return DatatypeConverter.parseHexBinary("0000000000000000");
        else
            return DatatypeConverter.parseHexBinary(timestamp);
    }

    /**
     * Set last import timestamp in system configuration.
     *
     * @param timestamp last import timestamp
     */
    private void setLastImport(byte[] timestamp) {
        systemConfigurationService.update(KcaaErpConfigurationItemName.AIRCRAFT_REGISTRATION_PROCESSOR_STARTING_TIMESTAMP,
            DatatypeConverter.printHexBinary(timestamp));
    }

    /**
     * Create aircraft registration and catch all exceptions.
     *
     * @param registration aircraft registration to create
     */
    private void doCreate(KcaaErpAircraftRegistration registration) {
        try {
            kcaaErpAircraftRegistrationService.createLocalAircraftRegistry(registration);
        } catch (Exception ex) {
            LOG.error("Could not process aircraft registration because : {}", ex.getMessage());
        }
    }

    /**
     * Find all missing aircraft registrations to process and create aircraft
     * registrations if possible.
     */
    private void doImport() {

        // set previous and latest timestamp for next import date
        byte[] previousTimestamp = getPreviousTimestamp();
        byte[] latestTimestamp = kcaaErpAircraftRegistrationService.findLatestTimestamp();

        // return if latest timestamp is not null and does not equal previous
        if (latestTimestamp == null || Arrays.equals(previousTimestamp, latestTimestamp)) {
            LOG.trace("Nothing to import for {}, up-to-dates", this.getClass().getSimpleName());
            return;
        }

        // retrieve a list of new aircraft registrations
        List<KcaaErpAircraftRegistration> registrations = kcaaErpAircraftRegistrationService
            .findByTimestampBetween(previousTimestamp, latestTimestamp);

        // create new aircraft registration for each
        for(KcaaErpAircraftRegistration registration : registrations) {
            doCreate(registration);
        }

        // update last import date time
        setLastImport(latestTimestamp);
    }

    /**
     * Run necessary services to get updated aircraft registrations from Kcaa Erp
     * Accounting System.
     *
     * This should not run two instances concurrently, thus do NOT use fixedRate
     * for @Scheduled value.
     */
    private void doProcess() {

        // if disabled, quietly return
        if (!pluginService.isEnabled(PluginKey.KCAA_ERP))
            return;

        // verify that the job isn't in use by another instance
        try {
            jobLockingService.check (this.getClass().getSimpleName(), processId);
            LOG.trace("Attempting to import {}.", this.getClass().getSimpleName());
        } catch (JobAlreadyRunningException jare) {
            LOG.trace("Another instance are importing {}.", this.getClass().getSimpleName());
            return;
        }

        try {
            doImport();
        } catch (Exception ex) {
            LOG.error("Could not process aircraft registration because : {}", ex.getMessage());
        }

        // release job locking for this process id
        jobLockingService.complete (this.getClass().getSimpleName(), processId);
    }

    /**
     * This will get the trigger for from system configuration if needed. Defaults
     * to 5 minutes if value not found or could not be parsed.
     *
     * @param triggerContext current trigger context
     * @return date for next trigger interval
     */
    private Date trigger(TriggerContext triggerContext) {
        return new PeriodicTrigger(interval, TimeUnit.MINUTES)
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
