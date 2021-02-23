package ca.ids.abms.modules.spatiareader.processor;

import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.config.error.RejectedException;
import ca.ids.abms.modules.common.services.RejectableItemConsumer;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.jobs.JobAlreadyRunningException;
import ca.ids.abms.modules.locking.JobLockingService;
import ca.ids.abms.modules.spatiareader.FlightPlansService;
import ca.ids.abms.modules.spatiareader.dto.FplObjectDto;
import ca.ids.abms.modules.spatiareader.entity.FplObject;
import ca.ids.abms.modules.spatiareader.mapper.CplFplMapper;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.util.converter.JSR310DateConverters;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
public class FPLObjectProcessor extends RejectableItemConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(FPLObjectProcessor.class);

    static final String LAST_TIMESTAMP_ITEM_NAME = "Flight plan processor starting time";

    private final SystemConfigurationService systemConfigurationService;
    private final FlightMovementService flightMovementService;
    private final CplFplMapper cplFplMapper;
    private final FlightPlansService flightPlansService;
    private final JobLockingService jobLockingService;
    private final boolean enabled;
    private final String processId;

    FPLObjectProcessor(
        final FlightMovementService flightMovementService, final SystemConfigurationService systemConfigurationService,
        final CplFplMapper cplFplMapper, final FlightPlansService flightPlansService,
        @Value("${abms.fplObjectProcessor.enabled}") boolean enabled, final JobLockingService jobLockingService
    ) {
        if (!enabled) LOG.warn("The Flight Object processor is disabled");
        this.systemConfigurationService = systemConfigurationService;
        this.flightMovementService = flightMovementService;
        this.cplFplMapper = cplFplMapper;
        this.flightPlansService = flightPlansService;
        this.enabled = enabled;
        this.jobLockingService = jobLockingService;
        this.processId = jobLockingService.generateProcessId();
    }

    /**
     * This method read a set of Flight Object from Spatia DB and process it with the Flight Movement Service
     */
    @Scheduled(cron = "${abms.fplObjectProcessor.schedule}")
    @SuppressWarnings("squid:S3776")
    void doProcess() {
        if (enabled && flightPlansService.thereIsACronosPluginEnabled()) {

            /* Verify that the scheduler isn't in use by another instance */
            try {
                jobLockingService.check (this.getClass().getSimpleName(), processId);
            } catch (JobAlreadyRunningException jare) {
                return;
            }

            LOG.trace("The Flight Object processor is verifying if there are Flight Object to process...");
            LocalDateTime lastTimestamp;

            /* Get the starting date */
            SystemConfiguration item = systemConfigurationService.getOneByItemName(LAST_TIMESTAMP_ITEM_NAME);
            lastTimestamp = getCurrentTimestamp(item);

            if (lastTimestamp != null) {
                long millis = System.currentTimeMillis();

                /* Initialize the most recent date with the current starting date */
                LocalDateTime recentDate = lastTimestamp;

                /* Get a set of FplObject (the size is a parameter injected into the query) */
                final List<FplObject> listQueryResult = flightPlansService.getFplObjectsStartingFromDate(lastTimestamp);
                if (!listQueryResult.isEmpty()) {
                    LOG.info("Processing {} Flight Objects starting from {}", listQueryResult.size(), lastTimestamp);
                }

                for (final FplObject fplObject : listQueryResult) {
                    LOG.debug("Processing flight object with fpl_object_id={}", fplObject.getCatalogueFplObjectId());

                    final FplObjectDto fplObjectDto = cplFplMapper.toFplDto(fplObject);
                    fixRouteText(fplObjectDto);

                    /* Verify, in case of timeout, that in meanwhile another instance hasn't got the scheduler */
                    try {
                        jobLockingService.check (this.getClass().getSimpleName(), processId);
                    } catch (JobAlreadyRunningException jare) {
                        LOG.warn("Job locking timed out.. cancelling processing of flight object with fpl_object_id={}",
                            fplObjectDto.getCatalogueFplObjectId());
                        return;
                    }

                    /* Process each FPL Object and update the most recent date */
                    try {
                        processFplObjectDto (fplObjectDto);
                    } finally {
                        if (recentDate.isBefore(fplObjectDto.getChildMessageMaxCatalogueDate())) {
                            recentDate = fplObjectDto.getChildMessageMaxCatalogueDate();
                            item = updateStatingTime(recentDate, item);
                        }
                    }
                }
                if (LOG.isInfoEnabled() && !listQueryResult.isEmpty()) {
                    LOG.info("Processed {} flight objects in {}ms; the next Flight Objects to process are after {}",
                        listQueryResult.size(), System.currentTimeMillis() - millis, recentDate);
                }
            } else {
                LOG.trace("The Flight Object processor can't retrieve Flight Objects from DB because the staring time is not configured");
            }
            try {
                jobLockingService.complete (this.getClass().getSimpleName(), processId);
            } catch (JobAlreadyRunningException jare) {
                // Nothing to do
            }
        }
    }
    
    public void processFplObjectDto (final FplObjectDto fplObjectDto) {
        try {
            updateRejectableFplObject(fplObjectDto);
            LOG.trace("Flight object for flight ID {} with date {} processed", fplObjectDto.getFlightId(),
                fplObjectDto.getChildMessageMaxCatalogueDate());
        } catch (final RejectedException e) {
            rejectItem(fplObjectDto, e);
            LOG.debug("Rejected the Flight Object with flight ID {} because: {}; {}", fplObjectDto.getFlightId(),
                e.getLocalizedMessage(), e.getReason());
        }
    }

    public void updateRejectableFplObject(final FplObjectDto fplObjectDto) {
        assert fplObjectDto != null;
        FlightMovement fm;
        try {
            fm = flightMovementService.createUpdateFlightMovementFromSpatia(fplObjectDto, false);
        } catch (RejectedException e) {
            LOG.debug("Rejected {} because {}", fplObjectDto, e.getMessage());
            throw e;
        } catch (Exception e) {
            LOG.debug("Rejected {} because {}", fplObjectDto,e);
            throw ExceptionFactory.resolveRejectedException(e, null);
        }

        try {
            flightMovementService.calculateFlightMovementFromFplObject(fm);
            flightMovementService.checkWhitelisting(fm);
        } catch (Exception e) {
            // Suppress all exceptions because the user should not see them
            LOG.error("Calculations for {} failed because {}", fplObjectDto,e);
        }
    }

    private LocalDateTime getCurrentTimestamp (final SystemConfiguration item) {
        LocalDateTime currentTimestamp = null;
        if (item != null) {
            if (StringUtils.isNotBlank(item.getCurrentValue())) {
                try {
                    currentTimestamp = JSR310DateConverters.convertStringToLocalDateTime(item.getCurrentValue(), JSR310DateConverters.DEFAULT_PATTERN_DATE_TIME);
                } catch (IllegalArgumentException | DateTimeParseException e) {
                    LOG.error("Cannot use the configuration item \"{}\" because the configured date \"{}\" does not match this format \"{}\"",
                        LAST_TIMESTAMP_ITEM_NAME, item.getCurrentValue(), JSR310DateConverters.DEFAULT_PATTERN_DATE_TIME);
                }
            } else {
                try {
                    currentTimestamp = JSR310DateConverters.convertStringToLocalDateTime(item.getDefaultValue(), JSR310DateConverters.DEFAULT_PATTERN_DATE_TIME);
                } catch (IllegalArgumentException | DateTimeParseException e) {
                    LOG.error("Cannot use the configuration item \"{}\" because the default date \"{}\" does not match this format \"{}\"",
                        LAST_TIMESTAMP_ITEM_NAME, item.getDefaultValue(), JSR310DateConverters.DEFAULT_PATTERN_DATE_TIME);
                }
            }
        } else {
            LOG.warn("Configuration item not found!");
        }
        return currentTimestamp;
    }

    private SystemConfiguration updateStatingTime (final LocalDateTime updatedDate, final SystemConfiguration item) {
        if (item != null) {
            final String currentValue = JSR310DateConverters.convertLocalDateTimeToString(updatedDate, JSR310DateConverters.DEFAULT_PATTERN_DATE_TIME);
            item.setCurrentValue(currentValue);
            return systemConfigurationService.update(item);
        } else {
            return null;
        }
    }

    /**
     * BUG 74063: Rejected items flight movement from spatia route contains trailing "\r\n"
     * Flight movement processing from spatia.  Routes in rejected items have a trailing newline.  “WBV UN181 PEDIL DCT AVAGO DCT\r\n”.  This should be trimmed.
     */
    private void fixRouteText(final FplObjectDto fplObjectDto) {
		if (fplObjectDto!=null && fplObjectDto.getRoute()!=null && !fplObjectDto.getRoute().isEmpty()) {

			String fixedRouteText = fplObjectDto.getRoute().replaceAll("\\r\\n|\\r|\\n", " ").trim();

			fplObjectDto.setRoute(fixedRouteText);
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
