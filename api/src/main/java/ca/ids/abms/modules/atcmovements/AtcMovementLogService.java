package ca.ids.abms.modules.atcmovements;

import ca.ids.abms.config.db.Filter;
import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.aerodromes.AerodromeService;
import ca.ids.abms.modules.common.dto.ItemLoaderObserver;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocation;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocationService;
import ca.ids.abms.modules.util.models.ModelUtils;
import ca.ids.abms.modules.util.models.geometry.CoordinatesConversionUtility;
import ca.ids.abms.util.StringUtils;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class AtcMovementLogService {

    private static final Logger LOG = LoggerFactory.getLogger(AtcMovementLogService.class);

    private final AerodromeService aerodromeService;
    private final AtcMovementLogDepartureEstimator atcMovementLogDepartureEstimator;
    private final AtcMovementLogRepository atcMovementLogRepository;
    private final FlightMovementService flightMovementService;
    private final UnspecifiedDepartureDestinationLocationService unspecifiedDepartureDestinationLocationService;

    private static final String KEY_DATE_OF_CONTACT = "dateOfContact";

    public AtcMovementLogService(final AerodromeService aerodromeService,
                                 final AtcMovementLogDepartureEstimator atcMovementLogDepartureEstimator,
                                 final AtcMovementLogRepository atcMovementLogRepository,
                                 final FlightMovementService flightMovementService,
                                 final UnspecifiedDepartureDestinationLocationService unspecifiedDepartureDestinationLocationService) {

        this.aerodromeService = aerodromeService;
        this.atcMovementLogDepartureEstimator = atcMovementLogDepartureEstimator;
        this.atcMovementLogRepository = atcMovementLogRepository;
        this.flightMovementService = flightMovementService;
        this.unspecifiedDepartureDestinationLocationService = unspecifiedDepartureDestinationLocationService;
    }

    public AtcMovementLog create(AtcMovementLog atcMovementLog) throws FlightMovementBuilderException {
        return this.create(atcMovementLog, null, false);
    }

    public AtcMovementLog create(AtcMovementLog atcMovementLog, ItemLoaderObserver observer, boolean createFromImport) throws FlightMovementBuilderException {
        validateAtcMovementLog (atcMovementLog);

        atcMovementLogDepartureEstimator.resolveMissingDepartureTime(atcMovementLog);
        final FlightMovement flightMovement = flightMovementService.createUpdateFlightMovementFromAtcMovementLog(atcMovementLog, observer);
        if (flightMovement != null) {
            atcMovementLog.setDayOfFlight(flightMovement.getDateOfFlight());
            atcMovementLog.setDepartureTime(flightMovement.getDepTime());
        }

        // we need to check again that there is no atc with the same unique key after a flight movement
        // is created/updated and date of flight and departure time are set to the ATC log
        if (createFromImport) {
            return atcMovementLog;
        }
        return atcMovementLogRepository.saveAndFlush(atcMovementLog);
    }

    public AtcMovementLog update(Integer id, AtcMovementLog atcMovementLog) throws FlightMovementBuilderException {
        AtcMovementLog aml;
        try {
            aml = this.update(id, atcMovementLog, null, true);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
        return aml;
    }

    public AtcMovementLog update(Integer id, AtcMovementLog atcMovementLog, ItemLoaderObserver o, boolean createFlightMovement) throws FlightMovementBuilderException {
        validateAtcMovementLog (atcMovementLog);

        final AtcMovementLog existingItem = atcMovementLogRepository.getOne(id);
        ModelUtils.merge(atcMovementLog, existingItem, "id", "createdAt", "createdBy", "updateAt", "updatedBy");

        if (createFlightMovement) {
            atcMovementLogDepartureEstimator.resolveMissingDepartureTime(atcMovementLog);
            final FlightMovement flightMovement = flightMovementService.createUpdateFlightMovementFromAtcMovementLog(existingItem, o);
            if (flightMovement != null) {
                existingItem.setDayOfFlight(flightMovement.getDateOfFlight());
                existingItem.setDepartureTime(flightMovement.getDepTime());
            }
        }
        return atcMovementLogRepository.saveAndFlush(existingItem);
    }

    public AtcMovementLog createOrUpdate(final AtcMovementLog item) throws FlightMovementBuilderException {
        return this.createOrUpdate(item, null);
    }

    public AtcMovementLog createOrUpdate(final AtcMovementLog item, ItemLoaderObserver o) throws FlightMovementBuilderException {
        Integer atcMovementLOGId = this.checkIfExistsAtcMovementLOG(item);
        if (atcMovementLOGId != null) {
            return update(atcMovementLOGId, item, o, true);
        } else {
            AtcMovementLog createdAtcMovementLog = create(item, o, true);
            atcMovementLOGId = this.checkIfExistsAtcMovementLOG(item);
            if (atcMovementLOGId == null) {
                return atcMovementLogRepository.saveAndFlush(createdAtcMovementLog);
            } else {
                return update(atcMovementLOGId, createdAtcMovementLog, o, false);
            }

        }
    }

    @Transactional(readOnly = true)
    public AtcMovementLog getOne(Integer id) {
        return atcMovementLogRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public Page<AtcMovementLog> findAll(Pageable pageable, String textSearch, LocalDate startDate, LocalDate endDate) {

        if (textSearch != null || startDate != null || endDate != null) {
            final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);
            if (startDate != null || endDate != null) {
                LocalDateTime startAt;
                LocalDateTime endAt;
                if (startDate == null) {
                    startAt = (LocalDateTime.now()).minusYears(1000);
                } else {
                    startAt = startDate.atStartOfDay();
                }
                if (endDate == null) {
                    endAt = (LocalDateTime.now()).plusYears(1000);
                } else {
                    endAt = endDate.atTime(LocalTime.MAX);
                }
                filterBuilder.restrictOn(Filter.included(KEY_DATE_OF_CONTACT, startAt, endAt));
            }
            return atcMovementLogRepository.findAll(filterBuilder.build(), pageable);
        } else {
            return atcMovementLogRepository.findAll(pageable);
        }
    }

    public void delete(final Integer id) {
        try {
            atcMovementLogRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    private Integer checkIfExistsAtcMovementLOG(AtcMovementLog atcMovementLog){
        Integer returnValue = null;

        if (atcMovementLog != null && atcMovementLog.getFlightId() != null &&
            atcMovementLog.getDayOfFlight() != null && atcMovementLog.getDepartureTime() != null) {

            AtcMovementLog atcMovementLogByUniqueKey = findAtcMovementLogByUniqueKey(atcMovementLog.getFlightId(), atcMovementLog.getDateOfContact(),
                atcMovementLog.getFirEntryTime(), atcMovementLog.getFirMidTime(), atcMovementLog.getFirExitTime());

            if (atcMovementLogByUniqueKey != null){
                returnValue = atcMovementLogByUniqueKey.getId();
            }
        }
        return returnValue;
    }

    /**
     * Werner, Helen and Carmine discuss on March 24 March 2017.
     * UniqueKey is :
     * flightId  - Mandatory
     * dateOfContact - Mandatory
     * firEntryTime
     * firMidTime
     * firExitTime
     *
     * @param flightId String
     * @param dateOfContact LocalDateTime
     * @param firEntryTime String
     * @param firMidTime String
     * @param firExitTime String
     * @return AtcMovementLog
     */
    @Transactional(readOnly = true)
    public AtcMovementLog findAtcMovementLogByUniqueKey(String flightId, LocalDateTime dateOfContact, String firEntryTime, String firMidTime, String firExitTime ){

        LOG.debug("Finding ATCMovementLOG by UniqueKey flightId: {}, dateOfContact: {}, firEntryTime: {}, firMidTime: {}, firExitTime: {}  ",
            flightId, dateOfContact, firEntryTime, firMidTime, firExitTime);

        AtcMovementLog atcMovementLog = null;
        if (StringUtils.isNotBlank(flightId) && dateOfContact != null) {
            List<AtcMovementLog> atcMovementLogs =
                atcMovementLogRepository.findByFlightIdAndDateOfContactAndFirEntryTimeAndFirMidTimeAndFirExitTimeOrderByDateOfContactDescFirEntryTimeDesc(flightId,
                    dateOfContact, firEntryTime,firMidTime,firExitTime);
            if (atcMovementLogs != null && !atcMovementLogs.isEmpty()) {
                atcMovementLog = atcMovementLogs.get(0);
            }
        } else {
            LOG.debug("Some parameters are NULL for finding ATCMovementLOG from UniqueKey flightId: {}, dateOfContact: {},",  flightId,dateOfContact);
        }
        return atcMovementLog;
    }

    /**
     * Excluding bulk uploads, departure and destination aerodromes must be
     * defined in the aerodromes table or unspecified locations.
     *
     * See User Story 103308
     */
    @Transactional(readOnly = true)
    public void validateAerodromeIdentifiers(final AtcMovementLog atcMovementLog) {
        Preconditions.checkArgument(atcMovementLog != null);

        boolean isDepartureNotValid = isInvalidAerodromeIdentifier(atcMovementLog.getDepartureAerodrome(), true);
        boolean isDestinationNotValid = isInvalidAerodromeIdentifier(atcMovementLog.getDestinationAerodrome(), false);

        if (isDepartureNotValid || isDestinationNotValid) {
            throwInvalidDepDestException(isDepartureNotValid, isDestinationNotValid,
                atcMovementLog.getDepartureAerodrome(), atcMovementLog.getDestinationAerodrome());
        }
    }

    private boolean isInvalidAerodromeIdentifier(final String aerodromeIdentifier, final boolean checkAfil) {
        String aerodrome = null;
        if (org.apache.commons.lang.StringUtils.isNotBlank(aerodromeIdentifier)) {
            aerodrome = aerodromeService.checkAerodromeIdentifier(aerodromeIdentifier, true, checkAfil);
            if (org.apache.commons.lang.StringUtils.isBlank(aerodrome)) {
                UnspecifiedDepartureDestinationLocation unspecifiedLocation = unspecifiedDepartureDestinationLocationService
                    .findTextIdentifier(aerodromeIdentifier);
                if (unspecifiedLocation != null) {
                    if (unspecifiedLocation.getAerodromeIdentifier() != null) {
                        // resolve unspecified by Aerodrome Identifier
                        aerodrome = unspecifiedLocation.getAerodromeIdentifier().getAerodromeName();
                    } else {
                        // resolve unspecified by Aerdrome Coordinates
                        aerodrome = CoordinatesConversionUtility.convertLatitudeLongitudeFromDecimalToDMS(
                            unspecifiedLocation.getLatitude(), unspecifiedLocation.getLongitude());
                    }
                }
            }
        }
        return aerodrome == null;
    }

    private void throwInvalidDepDestException(final boolean isDepartureNotValid,
                                              final boolean isDestinationNotValid,
                                              final String depAd,
                                              final String destAd) {
        final ErrorDTO.Builder errorBuilder = new ErrorDTO.Builder(ErrorConstants.ERR_AERODROME_VALIDATION);

        if (isDepartureNotValid) {
            String value = org.apache.commons.lang.StringUtils.isBlank(depAd) ? "null" : depAd;
            errorBuilder.appendDetails("Unrecognized aerodromes; Departure is not specified. ")
                .addInvalidField(AtcMovementLog.class, "departureAerodrome", value);
        }

        if (isDestinationNotValid) {
            String value = org.apache.commons.lang.StringUtils.isBlank(destAd) ? "null" : destAd;
            errorBuilder.appendDetails("Unrecognized aerodromes; Destination is not specified. ")
                .addInvalidField(AtcMovementLog.class, "destinationAerodrome", value);
        }

        final ErrorDTO errorMessage = errorBuilder.build();
        LOG.debug(errorMessage.getErrorDescription());
        throw ExceptionFactory.getInvalidDataException(errorMessage);
    }

    private void validateAtcMovementLog (final AtcMovementLog atcMovementLog) {
        Preconditions.checkArgument(atcMovementLog != null);

        boolean isDepartureNotValid = org.apache.commons.lang.StringUtils.isBlank(atcMovementLog.getDepartureAerodrome());
        boolean isDestinationNotValid = org.apache.commons.lang.StringUtils.isBlank(atcMovementLog.getDestinationAerodrome());

        if (isDepartureNotValid || isDestinationNotValid) {
            throwInvalidDepDestException(isDepartureNotValid, isDestinationNotValid, null, null);
        }
    }

    public long countAll() {
        return atcMovementLogRepository.count();
    }
}
