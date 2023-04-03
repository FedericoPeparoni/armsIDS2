package ca.ids.abms.modules.radarsummary;

import ca.ids.abms.config.db.Filter;
import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.config.error.RejectedException;
import ca.ids.abms.config.error.RejectedReasons;
import ca.ids.abms.modules.common.dto.ItemLoaderObserver;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.util.models.DateTimeUtils;
import ca.ids.abms.modules.util.models.ModelUtils;
import ca.ids.abms.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class RadarSummaryService {

    private static final Logger LOG = LoggerFactory.getLogger(RadarSummaryService.class);

    private final FlightMovementService flightMovementService;

    private final RadarSummaryRepository radarSummaryRepository;

    private final RadarSummaryWaypointUtility radarSummaryWaypointUtility;

    private static final String KEY_DATE_OF_FLIGHT = "dayOfFlight";

    @Autowired
    private SystemConfigurationService systemConfigurationService;

    public RadarSummaryService(
        final FlightMovementService flightMovementService,
        final RadarSummaryRepository radarSummaryRepository,
        final RadarSummaryWaypointUtility radarSummaryWaypointUtility
    ){
        this.flightMovementService = flightMovementService;
        this.radarSummaryRepository = radarSummaryRepository;
        this.radarSummaryWaypointUtility = radarSummaryWaypointUtility;
    }

    public SystemConfigurationService getSystemConfigurationService() {
        return systemConfigurationService;
    }

    public void setSystemConfigurationService(SystemConfigurationService systemConfigurationService) {
        this.systemConfigurationService = systemConfigurationService;
    }

    @Transactional(readOnly = true)
    public Page<RadarSummary> findAll(Pageable pageable) {
        LOG.debug("Request to find all RadarSummaries");
        return radarSummaryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public RadarSummary findOne(Integer id) {
        LOG.debug("Request to find RadarSummary by ID: {}",id);
        return radarSummaryRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public Page<RadarSummary> findAllRadarSummaryByFilter(Pageable pageable, String textSearch, LocalDate startDate, LocalDate endDate) {

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
            filterBuilder.restrictOn(Filter.included(KEY_DATE_OF_FLIGHT, startAt, endAt));
        }
        return radarSummaryRepository.findAll(filterBuilder.build(), pageable);
    }

    public RadarSummary create(RadarSummary radarSummary) throws FlightMovementBuilderException {

        //2020-07027 this code is called when Radar Summary is created from UI. Segment has to be set for the new RS.
        if(radarSummary != null) {
           radarSummary.setSegment(1);
       }
        return this.create(radarSummary, true, null);
    }

    public RadarSummary create(RadarSummary radarSummary, boolean ignoreWaypoints, ItemLoaderObserver o) throws FlightMovementBuilderException {
        LOG.debug("Request to create RadarSummary");

        RadarSummary existingRadarSummary;
        RadarSummary newRadarSummary;
        if(systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE)) {
            existingRadarSummary = radarSummaryRepository.findByLogicalKeySegment(radarSummary.getFlightIdentifier(),
                    radarSummary.getDepartureAeroDrome(),
                    radarSummary.getDayOfFlight(),
                    radarSummary.getDepartureTime(),
                    radarSummary.getSegment() );
        } else {
        // If existing radar summary is found that matches logical key...
        existingRadarSummary = this.findByLogicalKey(
            radarSummary.getFlightIdentifier(),
            radarSummary.getDepartureAeroDrome(),
            radarSummary.getDayOfFlight(),
            radarSummary.getDepartureTime()
        );
        }
        // exact matches should not create rejected items but instead be discarded
        if (existingRadarSummary != null && existingRadarSummary.isDuplicate(radarSummary)) {
            throw new RejectedException(RejectedReasons.ALREADY_EXISTS_SHOULD_DISCARD, null, null);
        }

        // resolve route and fir points from waypoints if not ignored
        // ignore waypoints flag is for UI so user can overwrite these fields
        if (!ignoreWaypoints) {
            radarSummaryWaypointUtility.resolveRouteAndFirPoints(radarSummary);
        }

        //2020-07-27 If multiple segments are allowed the segment number will be increased,
        // if not allowed - duplicated RS will be rejected.
        if(systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE)) {

            Integer maxSegment= this.radarSummaryRepository.getMaxSegmentNum(radarSummary.getFlightIdentifier(),
                    radarSummary.getDepartureAeroDrome(), radarSummary.getDayOfFlight(), radarSummary.getDepartureTime());
            if(maxSegment != null) {
                radarSummary.setSegment(maxSegment+1);
            } else {
                radarSummary.setSegment(1);
            }
        }
        newRadarSummary = radarSummaryRepository.save(radarSummary);

        boolean isUpdate = false;
        FlightMovement flightMovement = flightMovementService.createUpdateFlightMovementFromRadarSummary(newRadarSummary, o,isUpdate);

        // If radar summary does not contain entry and exit points/time, update with points parsed
        // during flightMovement processing
        resolveEntryExit(newRadarSummary, flightMovement);

        return newRadarSummary;
    }

    public void delete(Integer id) {
        LOG.debug("Request to delete Radarsummary : {}", id);
        try {
            radarSummaryRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    public RadarSummary update(Integer id, RadarSummary radarSummary) throws FlightMovementBuilderException {
        try {
            if (id == null || radarSummary == null) {
                LOG.debug("Request to update radarSummary failed because ID or radarSummary is null");
                return null;
            }
            return this.update(id, radarSummary, true, null,true);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
    }

    public RadarSummary update(Integer id, RadarSummary radarSummary, boolean ignoreWaypoints, ItemLoaderObserver o, boolean isUpdate) throws FlightMovementBuilderException {
        if (id == null || radarSummary == null) {
            LOG.debug("Request to update radarSummary failed because ID or radarSummary is null");
            return null;
        }

        isUpdate = true;
        if(o != null){
            isUpdate = false;
        }

        LOG.debug("Request to RadarSumnmary : {}", radarSummary);
        RadarSummary existingRadarSummary = radarSummaryRepository.getOne(id);
        if(systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE)) {
            ignoreWaypoints = false;
        }
        // resolve route and fir points from waypoints if not ignored
        // ignore waypoints flag is for UI so user can overwrite these fields
        if (ignoreWaypoints) {
            ModelUtils.merge(radarSummary, existingRadarSummary, "id");
        } else {
            ModelUtils.merge(radarSummary, existingRadarSummary, "id", "route", "firEntryPoint",
                "firEntryTime", "firExitPoint", "firExitTime", "waypoints", "firEntryCoordinate", "firExitCoordinate");
            radarSummaryWaypointUtility.resolveRadarWaypoints(radarSummary, existingRadarSummary);
        }

        FlightMovement flightMovement = flightMovementService.createUpdateFlightMovementFromRadarSummary(existingRadarSummary, o,isUpdate);

        // If radar summary does not contain entry and exit points/time, update with points parsed
        // during flightMovement processing
        resolveEntryExit(existingRadarSummary, flightMovement);

        return radarSummaryRepository.save(existingRadarSummary);
    }

    public RadarSummary createOrUpdate(final RadarSummary item) throws FlightMovementBuilderException {
        return this.createOrUpdate(item, null);
    }

    public RadarSummary createOrUpdate(final RadarSummary item, ItemLoaderObserver o) throws FlightMovementBuilderException {
        Integer radarSummaryID = null;
        if(systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE)) {
            RadarSummary radarSummary = getRadarLeonardoExist(item);
            if(radarSummary != null) {
                item.setSegment(radarSummary.getSegment());
                radarSummaryID = radarSummary.getId();
            }
        } else {
            radarSummaryID = this.checkIfExistsRadarSummary(item);
        }

        if (radarSummaryID != null) {
            return update(radarSummaryID, item, false, o,true);
        } else {
            return create(item, false, o);
        }
    }


    /**
     * Find a record that matches the specified logical key exactly.
     */
    private RadarSummary findByLogicalKeyExact (final String flightId, final String depAd, final LocalDateTime dayOfFlight, final String depTime) {
        return radarSummaryRepository.findByLogicalKeyExact (flightId, depAd, dayOfFlight, depTime);
    }

    /**
     * Find a record for the given flight and departure time within a few minutes of the specified time
     */
    private RadarSummary findByLogicalKeyFuzzy (final String flightId, final String depAd, final LocalDateTime dayOfFlight, final String depTime, final Integer plusMinusMinutes) {
        if (dayOfFlight != null) {
            final LocalDateTime fullDepartureDateTime = LocalDateTime.of (dayOfFlight.toLocalDate(), DateTimeUtils.convertStringToLocalTime (depTime));
            return radarSummaryRepository.findByLogicalKeyFuzzy (flightId, depAd, fullDepartureDateTime,
                    fullDepartureDateTime.minusMinutes (plusMinusMinutes), fullDepartureDateTime.plusMinutes (plusMinusMinutes));
        }
        return null;
    }

    /**
     * Find an radar summary using an exact match, or of that fails, an approximate one.
     * If an exact match can't be found, we look for the same flight with departure time slightly
     * off determined by a setting (+/- in minutes) in system configuration {@link SystemConfigurationItemName#DEP_TIME_RANGE_MIN}.
     */
    RadarSummary findByLogicalKey(final String flightId, final String depAd, final LocalDateTime dateOfFlight, final String depTime){
        LOG.debug("Finding RadarSummary by logical key FlighId={}, depAd={}, dateOfFlight={}, depTime={}",
                flightId, depAd, dateOfFlight, depTime);
        if(StringUtils.isBlank (flightId) || dateOfFlight == null || StringUtils.isBlank (depTime) || StringUtils.isBlank (depAd)){
            LOG.debug ("One or more arguments are NULL for finding RadarSummary by logical key FlighId={}, depAd={}, dateOfFlight={}, depTime={}",
                    flightId, depAd, dateOfFlight, depTime);
            return null;
        }

        // Find radar record that matches the given flight id/day of flight/departure time exactly
        final RadarSummary radarSummary = this.findByLogicalKeyExact (flightId, depAd, dateOfFlight, depTime);
        if (radarSummary != null) {
            return radarSummary;
        }
        // Exact match not found - find an approximate match if allowed by configuration
        final Integer plusMinusMinutes = getDepTimeRangeConfig();
        if (plusMinusMinutes != null && plusMinusMinutes > 0) {
            // find flight movement in a range
            return this.findByLogicalKeyFuzzy (flightId, depAd, dateOfFlight, depTime, plusMinusMinutes);
        }
        // nothing relevant found
        return null;
    }


    private RadarSummary getRadarLeonardoExist(RadarSummary newRS) {

        // if Leonardo file find all radar summaries
        List<RadarSummary> rsList = this.radarSummaryRepository.findByLogicalKeyAll(newRS.getFlightIdentifier(), newRS.getDepartureAeroDrome(), newRS.getDayOfFlight(), newRS.getDepartureTime());
        if(rsList != null && !rsList.isEmpty()) {
            for(RadarSummary rs : rsList) {
                if(StringUtils.isBlank(rs.getFirEntryTime()) || StringUtils.isBlank(newRS.getFirEntryTime())
                        || rs.getFirEntryTime().equalsIgnoreCase(newRS.getFirEntryTime())){
                    // duplicate found
                    return rs;
                }
            }
        }
        return null;
    }

    private Integer checkIfExistsRadarSummary(RadarSummary radarSummary) {

        if (radarSummary != null && radarSummary.getFlightIdentifier() != null &&
            radarSummary.getDayOfFlight() != null && radarSummary.getDepartureTime() != null) {

            final RadarSummary existingRadarSummary = findByLogicalKey (
                    radarSummary.getFlightIdentifier(),
                    radarSummary.getDepartureAeroDrome(),
                    radarSummary.getDayOfFlight(),
                    radarSummary.getDepartureTime());

            if (existingRadarSummary != null) {
                return existingRadarSummary.getId();
            }
        }

        return null;
    }

    /**
     * Helper method for to get departure time range values from the system configuration
     *
     * @return o or Integer value of SystemConfigurationItemName value
     */
    private Integer getDepTimeRangeConfig(){
        int itemValue = 0;
        SystemConfiguration systemConfiguration = systemConfigurationService
            .getOneByItemName(SystemConfigurationItemName.DEP_TIME_RANGE_MIN);
        if (systemConfiguration != null) {
            try {
                itemValue = Integer.parseInt(systemConfiguration.getCurrentValue());
            } catch (NumberFormatException e) {
                LOG.error("Error on parse the value for the property : {}",
                    SystemConfigurationItemName.DEP_TIME_RANGE_MIN);
            }
        }
        return itemValue;
    }

    /**
     * Attempts to set entry/exit point/time for a radar summary if not provided in radar summary upload file.
     * 1. Splits radar route and uses the first and last points of for entry/exit
     * 1.5. If radar route is null, checks corresponding flightMovement
     * 2. Checks corresponding flightMovement for entry/exit time
     *
     * @param radarSummary to resolve;
     * @param flightMovement to extract information from
     */
    private void resolveEntryExit(RadarSummary radarSummary, FlightMovement flightMovement) {
        Boolean entryPointMissing = radarSummary.getFirEntryPoint() == null || radarSummary.getFirEntryPoint().isEmpty();
        Boolean exitPointMissing = radarSummary.getFirExitPoint() == null || radarSummary.getFirExitPoint().isEmpty();
        Boolean entryTimeMissing = radarSummary.getFirEntryTime() == null || radarSummary.getFirEntryTime().isEmpty();
        Boolean exitTimeMissing = radarSummary.getFirExitTime() == null || radarSummary.getFirExitTime().isEmpty();

        // return early if attributes are present
        if (!(entryPointMissing || exitPointMissing || entryTimeMissing || exitTimeMissing)) {
            return;
        }

        if (entryPointMissing || exitPointMissing) {
            // split radar route into segments
            // -> radar routes are assumed to be space separated list
            String[] route = radarSummary.getRoute() != null ? radarSummary.getRoute().split(" ") : null;

            if (entryPointMissing) {
                String entryPoint = null;

                if (route != null && route[0] != null && !route[0].isEmpty()) {
                    // if route length is only one in length and matches the FIR exit point, assume the route only shows
                    // the exit point and thus should not be set to entry point
                    if (!(route.length == 1
                        && radarSummary.getFirExitPoint() != null
                        && radarSummary.getFirExitPoint().equalsIgnoreCase(route[0])
                    )) {
                        entryPoint = route[0];
                    }
                } else if (flightMovement != null) {
                    entryPoint = flightMovement.getBillableEntryPoint();
                }

                if (entryPoint != null && !entryPoint.isEmpty()) {
                    radarSummary.setFirEntryPoint(entryPoint);
                }
            }

            if (exitPointMissing) {
                String exitPoint = null;

                if (route != null && route[route.length - 1] != null && !route[route.length - 1].isEmpty()) {
                    // if route length is only one in length and matches the FIR entry point, assume the route only shows
                    // the entry point and thus should not be set to exit point
                    if (!(route.length == 1
                        && radarSummary.getFirEntryPoint() != null
                        && radarSummary.getFirEntryPoint().equalsIgnoreCase(route[route.length - 1])
                    )) {
                        exitPoint = route[route.length - 1];
                    } else if (flightMovement != null) {
                        exitPoint = flightMovement.getBillableExitPoint();
                    }

                    if (exitPoint != null && !exitPoint.isEmpty()) {
                        radarSummary.setFirExitPoint(exitPoint);
                    }
                }
            }

            if ((entryTimeMissing || exitTimeMissing) && flightMovement != null) {
                if (entryTimeMissing && flightMovement.getEntryTime() != null) {
                    radarSummary.setFirEntryTime(
                        DateTimeUtils.convertLocalDateTimeToTimeString(flightMovement.getEntryTime())
                    );
                }

                if (exitTimeMissing && flightMovement.getExitTime() != null) {
                    radarSummary.setFirExitTime(
                        DateTimeUtils.convertLocalDateTimeToTimeString(flightMovement.getExitTime())
                    );
                }
            }
        }
    }

    public long countAll() {
        return radarSummaryRepository.count();
    }
}
