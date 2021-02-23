package ca.ids.abms.modules.towermovements;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import ca.ids.abms.modules.aerodromes.AerodromeService;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocation;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocationService;
import ca.ids.abms.modules.util.models.geometry.CoordinatesConversionUtility;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.db.Filter;
import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.common.dto.ItemLoaderObserver;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.util.models.ModelUtils;
import ca.ids.abms.util.StringUtils;

@Service
@Transactional
public class TowerMovementLogService {

    private static final Logger LOG = LoggerFactory.getLogger(TowerMovementLogService.class);

    private final AerodromeService aerodromeService;
    private final FlightMovementService flightMovementService;
    private final TowerMovementLogDepartureEstimator towerMovementLogDepartureEstimator;
    private final TowerMovementLogRepository towerMovementLogRepository;
    private final UnspecifiedDepartureDestinationLocationService unspecifiedDepartureDestinationLocationService;

    private static final String KEY_DATE_OF_CONTACT = "dateOfContact";

    @Autowired
    private SystemConfigurationService systemConfigurationService;

    TowerMovementLogService(
        final AerodromeService aerodromeService,
        final FlightMovementService flightMovementService,
        final TowerMovementLogDepartureEstimator towerMovementLogDepartureEstimator,
        final TowerMovementLogRepository towerMovementLogRepository,
        final UnspecifiedDepartureDestinationLocationService unspecifiedDepartureDestinationLocationService
    ) {
        this.aerodromeService = aerodromeService;
        this.flightMovementService = flightMovementService;
        this.towerMovementLogDepartureEstimator = towerMovementLogDepartureEstimator;
        this.towerMovementLogRepository = towerMovementLogRepository;
        this.unspecifiedDepartureDestinationLocationService = unspecifiedDepartureDestinationLocationService;
    }

    public SystemConfigurationService getSystemConfigurationService() {
        return systemConfigurationService;
    }

    public void setSystemConfigurationService(SystemConfigurationService systemConfigurationService) {
        this.systemConfigurationService = systemConfigurationService;
    }

    public TowerMovementLog create(TowerMovementLog towerMovementLog) throws FlightMovementBuilderException {
        return this.create(towerMovementLog, null);
    }

    public TowerMovementLog create(TowerMovementLog towerMovementLog, ItemLoaderObserver o) throws FlightMovementBuilderException {
        validateTowerMovementLog (towerMovementLog);

        towerMovementLogDepartureEstimator.resolveMissingDepartureTime(towerMovementLog);
        final FlightMovement flightMovement = flightMovementService.createUpdateFlightMovementFromTowerMovementLog(towerMovementLog, o);
        if (flightMovement != null) {
            towerMovementLog.setDayOfFlight(flightMovement.getDateOfFlight());
            towerMovementLog.setDepartureTime(flightMovement.getDepTime());
        }

        return towerMovementLogRepository.saveAndFlush(towerMovementLog);
    }

    public TowerMovementLog update(Integer id, TowerMovementLog towerMovementLog) throws FlightMovementBuilderException {
        TowerMovementLog tml = null;
        try {
            tml = this.update(id, towerMovementLog, null);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
        return tml;
    }

    public TowerMovementLog update(Integer id, TowerMovementLog towerMovementLog, ItemLoaderObserver o) throws FlightMovementBuilderException {
        final TowerMovementLog existingItem = towerMovementLogRepository.getOne(id);
        if (existingItem != null) {
            return update(existingItem, towerMovementLog, o);
        }
        return null;
    }

    private TowerMovementLog update(TowerMovementLog existingItem, TowerMovementLog towerMovementLog, ItemLoaderObserver o) throws FlightMovementBuilderException {
        Preconditions.checkArgument(existingItem != null && towerMovementLog != null);
        validateTowerMovementLog (towerMovementLog);

        ModelUtils.merge(towerMovementLog, existingItem, "id", "createdAt", "createdBy", "updateAt", "updatedBy");

        towerMovementLogDepartureEstimator.resolveMissingDepartureTime(existingItem);
        final FlightMovement flightMovement = flightMovementService.createUpdateFlightMovementFromTowerMovementLog(existingItem, o);
        if (flightMovement != null) {
            existingItem.setDayOfFlight(flightMovement.getDateOfFlight());
            existingItem.setDepartureTime(flightMovement.getDepTime());
        }

        return towerMovementLogRepository.saveAndFlush(existingItem);
    }

    public TowerMovementLog createOrUpdateByFlightId(final TowerMovementLog item) throws FlightMovementBuilderException {
        return this.createOrUpdateByFlightId(item, null);
    }

    public TowerMovementLog createOrUpdateByFlightId(final TowerMovementLog item, ItemLoaderObserver o) throws FlightMovementBuilderException {
        TowerMovementLog result;
        TowerMovementLog existingTowerMovementLog = checkIfExistsTowerMovementLog(item);
        if(existingTowerMovementLog != null){
            result = update(existingTowerMovementLog, item, o);
        }else{
            result = create(item, o);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public TowerMovementLog getOne(Integer id) {
        return towerMovementLogRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public Page<TowerMovementLog> findAll(Pageable pageable, LocalDate startDate, LocalDate endDate, String textSearch) {

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
            return towerMovementLogRepository.findAll(filterBuilder.build(), pageable);
        } else {
            return towerMovementLogRepository.findAll(pageable);
        }

    }

    public void delete(Integer id) {
        try {
            towerMovementLogRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    /**
     * Werner, Helen and Carmine discuss on March 24 March 2017.
     * UniqueKey is :
     * flightId  - Mandatory
     * dateOfContact - Mandatory
     * departureContactTime
     * destinationContactTime
     *
     * @param flightId
     * @param dateOfContact
     * @param departureContactTime
     * @param destinationContactTime
     * @return
     */
    @Transactional(readOnly = true)
    public TowerMovementLog findTowerMovementLogUniqueKey(String flightId, LocalDateTime dateOfContact, String departureContactTime, String destinationContactTime){

        LOG.debug("Finding TowerMovement by UniqueKey FlightId {}, dateOfContact {}, departureContactTime {}, destinationContactTime {}",  flightId,dateOfContact,departureContactTime,destinationContactTime);
        TowerMovementLog towerMovementLog=null;
        if(StringUtils.isStringIfNotNull(flightId) && dateOfContact != null && (departureContactTime != null || destinationContactTime != null)) {
            if (departureContactTime == null) {
                final List<TowerMovementLog> towerMovements = towerMovementLogRepository.findByFlightIdAndDateOfContactAndDestinationContactTime(flightId, dateOfContact, destinationContactTime);
                if (CollectionUtils.isNotEmpty(towerMovements)) {
                    towerMovementLog = towerMovements.get(0);
                }
            } else if (destinationContactTime == null) {
                final List<TowerMovementLog> towerMovements = towerMovementLogRepository.findByFlightIdAndDateOfContactAndDepartureContactTime(flightId, dateOfContact, departureContactTime);
                if (CollectionUtils.isNotEmpty(towerMovements)) {
                    towerMovementLog = towerMovements.get(0);
                }
            } else {
                towerMovementLog = towerMovementLogRepository.findByFlightIdAndDateOfContactAndDepartureContactTimeAndDestinationContactTime
                    (flightId, dateOfContact, departureContactTime, destinationContactTime);
            }
        }else{
            LOG.debug("Some parameters are NULL for finding TowerMovement by UniqueKey FlightId {}, dateOfContact {}, departureContactTime {}, destinationContactTime {}",  flightId,dateOfContact,departureContactTime,destinationContactTime);
        }
        if (LOG.isDebugEnabled()) {
            if (towerMovementLog != null) {
                LOG.debug("Found the tower movement log by FlightId {}, dateOfContact {}, departureContactTime {}, destinationContactTime {}", flightId, dateOfContact, departureContactTime, destinationContactTime);
            } else {
                LOG.debug("NOT Found any tower movement log by FlightId {}, dateOfContact {}, departureContactTime {}, destinationContactTime {}", flightId, dateOfContact, departureContactTime, destinationContactTime);
            }
        }
        return towerMovementLog;
    }

    @Transactional(readOnly = true)
    public TowerMovementLog checkIfExistsTowerMovementLog(TowerMovementLog towerMovementLog){
        TowerMovementLog towerMovementLogResult=null;

        if(towerMovementLog!=null && StringUtils.isStringIfNotNull(towerMovementLog.getFlightId()) && towerMovementLog.getDateOfContact()!=null ){
            towerMovementLogResult= findTowerMovementLogUniqueKey(
                towerMovementLog.getFlightId(), towerMovementLog.getDateOfContact(),
                towerMovementLog.getDepartureContactTime(),towerMovementLog.getDestinationContactTime());
        }
        return towerMovementLogResult;
    }

    /**
     * Excluding bulk uploads, departure and destination aerodromes must be
     * defined in the aerodromes table or unspecified locations.
     *
     * See User Story 103308
     */
    @Transactional(readOnly = true)
    public void validateAerodromeIdentifiers(final TowerMovementLog towerMovementLog) {
        Preconditions.checkArgument(towerMovementLog != null);

        boolean isDepartureNotValid = isInvalidAerodromeIdentifier(towerMovementLog.getDepartureAerodrome(), true);
        boolean isDestinationNotValid = isInvalidAerodromeIdentifier(towerMovementLog.getDestinationAerodrome(), false);

        if (isDepartureNotValid || isDestinationNotValid) {
            thrownInvalidAerodromeException(isDepartureNotValid, isDestinationNotValid,
                towerMovementLog.getDepartureAerodrome(), towerMovementLog.getDestinationAerodrome());
        }
    }

    private boolean isInvalidAerodromeIdentifier(final String aerodromeIdentifier, final boolean checkAfil) {
        String aerodrome = null;
        if (StringUtils.isNotBlank(aerodromeIdentifier)) {
            aerodrome = aerodromeService.checkAerodromeIdentifier(aerodromeIdentifier, true, checkAfil);
            if (StringUtils.isBlank(aerodrome)) {
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

    private void thrownInvalidAerodromeException(
        final boolean isDepartureNotValid, final boolean isDestinationNotValid, final String depAd, final String destAd
    ) {
        final ErrorDTO.Builder errorBuilder = new ErrorDTO.Builder(ErrorConstants.ERR_AERODROME_VALIDATION);

        if (isDepartureNotValid) {
            String value = org.apache.commons.lang.StringUtils.isBlank(depAd) ? "null" : depAd;
            errorBuilder.appendDetails("Unrecognized aerodromes; Departure is not specified. ")
                .addInvalidField(TowerMovementLog.class, "departureAerodrome", value);
        }

        if(isDestinationNotValid) {
            String value = org.apache.commons.lang.StringUtils.isBlank(destAd) ? "null" : destAd;
            errorBuilder.appendDetails("Unrecognized aerodromes; Destination is not specified. ")
                .addInvalidField(TowerMovementLog.class, "destinationAerodrome", value);
        }

        final ErrorDTO errorMessage = errorBuilder.build();
        LOG.debug(errorMessage.getErrorDescription());
        throw ExceptionFactory.getInvalidDataException(errorMessage);
    }

    private void validateTowerMovementLog (final TowerMovementLog towerMovementLog) {
        Preconditions.checkArgument(towerMovementLog != null);

        boolean isDepartureNotValid = org.apache.commons.lang.StringUtils.isBlank(towerMovementLog.getDepartureAerodrome());
        boolean isDestinationNotValid = org.apache.commons.lang.StringUtils.isBlank(towerMovementLog.getDestinationAerodrome());

        if (isDepartureNotValid || isDestinationNotValid) {
            thrownInvalidAerodromeException(isDepartureNotValid, isDestinationNotValid, null, null);
        }
    }

    public long countAll() {
        return towerMovementLogRepository.count();
    }
}
