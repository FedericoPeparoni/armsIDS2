package ca.ids.abms.modules.flightmovementsbuilder;

import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.error.RejectedException;
import ca.ids.abms.config.error.RejectedReasons;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.aircraft.AircraftRegistrationRepository;
import ca.ids.abms.modules.aircraft.AircraftRegistrationService;
import ca.ids.abms.modules.atcmovements.AtcMovementLog;
import ca.ids.abms.modules.atcmovements.AtcMovementLogBillableRouteFinder;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.billingcenters.BillingCenterService;
import ca.ids.abms.modules.charges.PassengerServiceChargeReturn;
import ca.ids.abms.modules.charges.PassengerServiceChargeReturnRepository;
import ca.ids.abms.modules.dbqueries.DatabaseQueryResult;
import ca.ids.abms.modules.dbqueries.DatabaseQueryService;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementAerodromeService;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.flightmovements.FlightMovementValidationViewModel;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementSource;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryNationality;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryScope;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryType;
import ca.ids.abms.modules.flightmovementsbuilder.utility.*;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.vo.RouteCacheVO;
import ca.ids.abms.modules.flightmovementsbuilder.vo.ThruFlightPlanVO;
import ca.ids.abms.modules.radarsummary.RadarSummary;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.routesegments.RouteSegment;
import ca.ids.abms.modules.routesegments.SegmentType;
import ca.ids.abms.modules.routesegments.SegmentTypeMap;
import ca.ids.abms.modules.spatiareader.dto.FplObjectDto;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.towermovements.TowerMovementLog;
import ca.ids.abms.modules.util.models.ApplicationConstants;
import ca.ids.abms.modules.util.models.Calculation;
import ca.ids.abms.modules.util.models.DateTimeUtils;
import ca.ids.abms.modules.utilities.flights.FlightUtility;
import ca.ids.abms.util.GeometryUtils;
import ca.ids.abms.util.StringUtils;
import ca.ids.abms.util.converter.JSR310DateConverters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.operation.linemerge.LineMerger;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class FlightMovementBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(FlightMovementBuilder.class);

    private static final String FLIGHT_REASSIGNMENT_SQL_FILE = "flights/fm_reassignments";

    private static final String IDENTIFICATION_TYPE_REGISTRATION = "Registration";

    private static final String IDENTIFICATION_TYPE_FLIGHT_ID = "Flight Id";

    private static final String IDENTIFICATION_TYPE_ICAO_CODE = "ICAO code";

    private static final String IDENTIFICATION_TYPE = "identification_type";

    private static final String IDENTIFIER_TEXT = "identifier_text";

    private static final String ARRIVAL_AD_LOG = "arrivalAd --> {}";

    private static final short SIZE_PREFIX_ICAO_CODE = 3;

    private final FlightMovementBuilderUtility flightMovementBuilderUtility;
    private final FlightMovementValidator flightMovementValidator;
    private final DeltaFlightUtility deltaFlightUtility;
    private final ThruFlightPlanUtility thruFlightPlanUtility;
    private final ChargesUtility chargesUtility;
    private final FlightMovementBillable flightMovementBillable;
    private final AtcMovementLogBillableRouteFinder atcMovementLogBillableRouteFinder;
    private final FlightMovementAerodromeService flightMovementAerodromeService;
    private final BillingCenterService billingCenterService;
    private final PassengerServiceChargeReturnRepository passengerServiceChargeReturnRepository;
    private final AccountService accountService;
    private final DatabaseQueryService databaseQueryService;
    private final FlightMovementMerge flightMovementMerge;
    private final FlightMovementBillingDateEstimator flightMovementBillingDateEstimator;
    private final SystemConfigurationService systemConfigurationService;
    private final AircraftRegistrationService aircraftRegistrationService;

    @SuppressWarnings("squid:S00107")
    public FlightMovementBuilder(
        final FlightMovementBuilderUtility flightMovementBuilderUtility,
        final FlightMovementValidator flightMovementValidator,
        final DeltaFlightUtility deltaFlightUtility,
        final ThruFlightPlanUtility aThruFlightPlanUtility,
        final ChargesUtility chargesUtility,
        final FlightMovementBillable flightMovementBillable,
        final AtcMovementLogBillableRouteFinder atcMovementLogBillableRouteFinder,
        final FlightMovementAerodromeService flightMovementAerodromeService,
        final BillingCenterService billingCenterService,
        final PassengerServiceChargeReturnRepository passengerRep,
        final AccountService accountService,
        final DatabaseQueryService databaseQueryService,
        final FlightMovementMerge flightMovementMerge,
        final FlightMovementBillingDateEstimator flightMovementBillingDateEstimator,
        final SystemConfigurationService systemConfigurationService,
        final AircraftRegistrationService aircraftRegistrationService
    ) {
        this.flightMovementBuilderUtility = flightMovementBuilderUtility;
        this.flightMovementValidator = flightMovementValidator;
        this.deltaFlightUtility = deltaFlightUtility;
        this.thruFlightPlanUtility = aThruFlightPlanUtility;
        this.chargesUtility = chargesUtility;
        this.flightMovementBillable = flightMovementBillable;
        this.atcMovementLogBillableRouteFinder = atcMovementLogBillableRouteFinder;
        this.flightMovementAerodromeService = flightMovementAerodromeService;
        this.billingCenterService = billingCenterService;
        this.passengerServiceChargeReturnRepository = passengerRep;
        this.accountService = accountService;
        this.databaseQueryService = databaseQueryService;
        this.flightMovementMerge = flightMovementMerge;
        this.flightMovementBillingDateEstimator = flightMovementBillingDateEstimator;
        this.systemConfigurationService = systemConfigurationService;
        this.aircraftRegistrationService = aircraftRegistrationService;
    }

    public FlightMovementBillable getFlightMovementBillable() {
        return flightMovementBillable;
    }
       
    // Create Flight Movement from WebInterface
    public FlightMovement createUpdateFlightMovementFromUI(FlightMovement flightMovement, Boolean forInvoice)
            throws FlightMovementBuilderException {
        LOG.debug("Create or update Flight Movement from WebInterface");
        
        // Create error for invalid flight level if it is configured either for the input from Cronos,
        // or is needed for TMA routing TFS 115533
        if(flightMovementValidator.isValidateFlightLevel() || flightMovementValidator.isValidateFlightLevelAirspace()) {
                flightMovementValidator.validateFlightLevel(flightMovement);
        } else {
             if(flightMovement != null && StringUtils.isNotBlank(flightMovement.getFlightLevel())){
                  String fl = flightMovement.getFlightLevel().trim();
                  flightMovement.setFlightLevel(fl);
             }
        }

        clearRouteInfo(flightMovement);

        if (FlightMovementBuilderUtility.checkNotNullConstraint(flightMovement)) {
            calculateFlightMovement(flightMovement, forInvoice, true);
        }
        return flightMovement;
    }

    public FlightMovement calculateFlightMovement(FlightMovement flightMovement, Boolean forInvoice, Boolean checkRegNum)
        throws FlightMovementBuilderException {
        LOG.debug("Calculate Flight Movement from WebInterface");
    
        if (flightMovement.getId() == null) {
            flightMovement.setSource(FlightMovementSource.MANUAL);
        }

        SegmentType segmentType = SegmentTypeMap.mapFlightMovementSourceToSegmentType(FlightMovementSource.MANUAL);

        /*
            Check aerodrome without resolving to coordinates when not in NAVDB. This is done for billing center
            which requires aerodrome names and NOT coordinates.

            calculateRouteInformationByRouteParserDelta and calculateRouteInformationByRouteParser are able to
            handle the resolving of ABMSDB aerodrome names to coordinates which in this case makes it redundant
            to ask flightMovementBuilderUtility.checkAerodrome to resolve aerodrome not in NAVDB to coordinates.
         */
        final String dep = flightMovementBuilderUtility.checkAerodrome(flightMovement.getDepAd(),
            flightMovement.getItem18Dep(), false);

        // Actual departure is either the aerodrome code or the name from item18Dep
        flightMovement.setActualDepAd(dep);

        String dest = null;

        try {

            // thru flight plan is parsed separately from non-thru plan flights
            flightMovement.setThruFlight(thruFlightPlanUtility.isThruFlight(flightMovement));
            if (flightMovement.getThruFlight()) {
                LocalDateTime startDate = DateTimeUtils.addTimeToDate(flightMovement.getDateOfFlight(), flightMovement.getDepTime());
                List<ThruFlightPlanVO> thruLst =  thruFlightPlanUtility.parseThruPlanRoute(flightMovement.getFplRoute(),
                    flightMovement.getDepAd(), startDate);

                // ICAO thru plan rules `thruFlightPlanUtility.checkRules` are ignored as per tech spec
                // simply check that two segments were parsed from route
                if (thruLst != null && !thruLst.isEmpty()) {
                    dest = flightMovementBuilderUtility.checkAerodromeFromDelta(
                        calculateRouteInformationByRouteParserThru(flightMovement, thruLst,
                            flightMovement.getRouteSegments(), segmentType, dep), false);
                } else {
                    LOG.debug("No valid Thru plan inserted");
                    dest = flightMovementBuilderUtility.checkAerodromeFromDelta(
                        setNoRouteForThru(flightMovement), false);
                }
            } else {

                // calculate and set if flight movement is a delta flight
                flightMovement.setDeltaFlight(deltaFlightUtility.isDeltaFlight(flightMovement));

                // if delta flight with item18 DEST/ stops, use special delta flight method to determine dest ad
                // as delta flights can have multiple item18 DEST/ stops
                if (flightMovement.getDeltaFlight() && flightMovement.getItem18Dest() != null
                    && !flightMovement.getItem18Dest().isEmpty()) {
                    dest = flightMovementBuilderUtility.checkAerodromeFromDelta(
                        calculateRouteInformationByRouteParserDelta(flightMovement, flightMovement.getRouteSegments(),
                            segmentType, dep), false);
                } else {
                    dest = flightMovementBuilderUtility.checkAerodrome(flightMovement.getDestAd(),
                        flightMovement.getItem18Dest(), false);
                    calculateRouteInformationByRouteParser(flightMovement, flightMovement.getRouteSegments(),
                        segmentType, dep, dest);
                }
            }
        } catch (FlightMovementBuilderException fmbe) {
            catchAerodromeErrors(flightMovement, fmbe);
        }

        // recalculate routes fro radar/atc/tower if exist
        if (StringUtils.isNotBlank(flightMovement.getRadarRouteText())
            || StringUtils.isNotBlank(flightMovement.getRadarTowerLogRouteText())
            || StringUtils.isNotBlank(flightMovement.getAtcLogRouteText())
        ) {
            String destAd = flightMovementBuilderUtility.checkAerodrome(flightMovement.getDestAd(),
                flightMovement.getItem18Dest(), false);
            this.recalculateSurveillanceLogRoutes(flightMovement, dep, destAd);
        }

        // set actual destination
        flightMovement.setActualDestAd(dest);

        checkArrivalAerodromeTimeEet(flightMovement);
        checkFlightRules(flightMovement);

        // check and calculate the other flight movement fields
        // must be done after route segments have been defined
        checkAndResolveFlightMovement(flightMovement, forInvoice, checkRegNum);

        // resolve billing center from dep and dest aerodromes based on movement type
        this.resolveBillingCenter(flightMovement, dep, dest);
        
        return flightMovement;
    }
        
    private boolean checkUnifiedTaxInvoiced(AircraftRegistration ar, LocalDateTime flightDate) {
    	LocalDateTime coaIssueDate = ar.getCoaIssueDate();
    	LocalDateTime coaExpiryDate = ar.getCoaExpiryDate();
    	if (coaIssueDate == null || coaExpiryDate == null)
    		return false;
    	
    	if (flightDate.isBefore(coaIssueDate) || flightDate.isAfter(coaExpiryDate))
    		return false;
    	
    	return true;
    }
    
    private void setFlightStatusIfUnifiedTaxFlight2(FlightMovement flightMovement) {
        // get aircraft registration number from item18RegNum
    	String item18RegNum = flightMovementBuilderUtility.checkAircraftRegistrationNumber(flightMovement);
                
        if (item18RegNum != null) {

            Double aMtow = null;

            AircraftRegistration ar = aircraftRegistrationService.findAircraftRegistrationByRegNumber(item18RegNum);
            if (ar != null) {
            	// SMALL_AIRCRAFT_MAX_WEIGHT is expressed in KG
                Integer maxWeight = systemConfigurationService.getIntOrZero(SystemConfigurationItemName.SMALL_AIRCRAFT_MAX_WEIGHT);
                
                // MTOW stored in small tones in the DB ==> need to convert it to KG
            	aMtow = ar.getMtowOverride()* ReportHelper.TO_KG;

                if (aMtow <= maxWeight) {
                	
                	boolean isDomesticOrLocal = (ar.getIsLocal()) || 
                		(flightMovement.getFlightCategoryNationality().equals(FlightmovementCategoryNationality.NATIONAL));
                    
                	if (isDomesticOrLocal) {
                		boolean invoiced = checkUnifiedTaxInvoiced(ar, flightMovement.getDateOfFlight());
                		if (invoiced) {        
                        	// Unified Tax è pagata per l'anno in corso
                            flightMovement.setStatus(FlightMovementStatus.INVOICED);
                            flightMovement.setFlightNotes("");
                        } else {
                            // Unified Tax non è pagata per l'anno in corso
                            flightMovement.setStatus(FlightMovementStatus.INCOMPLETE);
                            flightMovement.setFlightNotes("Unified Tax not payed for the current year");
                        }
                    }
                }
            }
        }        
    }

    private void checkFlightRules(FlightMovement aFlightMovement) {
        // check and resolve flight rule
        String rule = aFlightMovement.getFlightRules();
        if (StringUtils.isNotBlank(rule)) {
            if (rule.equals("Y")) {
                aFlightMovement.setFlightRules("I");
            } else if (rule.equals("Z")) {
                aFlightMovement.setFlightRules("V");
            }
        }
    }


    /**
     *  Create new Flight Movement from FlightObject (Spatia)
     * @param fplObject
     * @return
     */
    public FlightMovement validateAndCreateFlightMovementFromFlightObject(FplObjectDto fplObject) {
        FlightMovement flightMovement = null;
        if(fplObject != null) {
            flightMovement = validateAndMapFromFplObject(fplObject);
            
            // manage the FlightMovementStatus if the flight is "unified tax"
            setFlightStatusIfUnifiedTaxFlight2(flightMovement);
        }
        return flightMovement;
    }

    /**
     * Performs calculations for the FlightMovement created or updated from CRONOS/Spatia
     * @param flightMovement
     * @throws FlightMovementBuilderException
     */
    public FlightMovement calculateFlightMovementFromFlightObject(FlightMovement flightMovement) throws FlightMovementBuilderException {

        if(flightMovement != null) {
                        
            setFlightStatusIfUnifiedTaxFlight2(flightMovement);
            
            SegmentType segmentType = SegmentTypeMap
                    .mapFlightMovementSourceToSegmentType(FlightMovementSource.NETWORK);

            /*
                Check aerodrome without resolving to coordinates when not in NAVDB. This is done for billing center
                which requires aerodrome names and NOT coordinates.

                calculateRouteInformationByRouteParserDelta and calculateRouteInformationByRouteParser are able to
                handle the resolving of ABMSDB aerodrome names to coordinates which in this case makes it redundant
                to ask flightMovementBuilderUtility.checkAerodrome to resolve aerodrome not in NAVDB to coordinates.
             */
            final String dep = flightMovementBuilderUtility.checkAerodrome(flightMovement.getDepAd(),
                flightMovement.getItem18Dep(), false);
            String dest = null;

            try {

                // thru flight plan is parsed separately from non-thru plan flights
                flightMovement.setThruFlight(thruFlightPlanUtility.isThruFlight(flightMovement));
                if (flightMovement.getThruFlight()) {
                    LocalDateTime startDate = DateTimeUtils.addTimeToDate(flightMovement.getDateOfFlight(), flightMovement.getDepTime());
                    List<ThruFlightPlanVO> thruLst = thruFlightPlanUtility.parseThruPlanRoute(flightMovement.getFplRoute(),
                        flightMovement.getDepAd(), startDate);

                    // ICAO thru plan rules `thruFlightPlanUtility.checkRules` are ignored as per tech spec
                    // simply check that two segments were parsed from route
                    if (thruLst != null && thruLst.size() > 1) {
                        dest = flightMovementBuilderUtility.checkAerodromeFromDelta(
                            calculateRouteInformationByRouteParserThru(flightMovement, thruLst,
                                flightMovement.getRouteSegments(), segmentType, dep), false);
                    } else {
                        LOG.debug("No valid Thru plan inserted");
                        dest = flightMovementBuilderUtility.checkAerodromeFromDelta(
                            setNoRouteForThru(flightMovement), false);
                    }
                } else {

                    // calculate and set if flight movement is a delta flight
                    flightMovement.setDeltaFlight(deltaFlightUtility.isDeltaFlight(flightMovement));

                    // if delta flight with item18 DEST/ stops, use special delta flight method to determine dest ad
                    // as delta flights can have multiple item18 DEST/ stops
                    if (flightMovement.getDeltaFlight() && flightMovement.getItem18Dest() != null
                        && !flightMovement.getItem18Dest().isEmpty()) {
                        dest = flightMovementBuilderUtility.checkAerodromeFromDelta(
                            calculateRouteInformationByRouteParserDelta(flightMovement,
                                flightMovement.getRouteSegments(), segmentType, dep), false);
                    } else {
                        dest = flightMovementBuilderUtility.checkAerodrome(flightMovement.getDestAd(),
                                flightMovement.getItem18Dest(), false);
                        calculateRouteInformationByRouteParser(flightMovement, flightMovement.getRouteSegments(),
                                segmentType, dep, dest);
                    }
                }
            } catch (FlightMovementBuilderException fmbe) {
                catchAerodromeErrors(flightMovement, fmbe);
            }

            // check and calculate the other flight movement fields
            // must be done after route segments have been defined
            checkAndResolveFlightMovement(flightMovement, false, true);

            // resolve billing center from dep and dest aerodromes based on movement type
            this.resolveBillingCenter(flightMovement, dep, dest);
        }
        return flightMovement;
    }


    private String calculateRouteInformationByRouteParserThru(
        FlightMovement aFlightMovement, List<ThruFlightPlanVO> aThruLst, List<RouteSegment> existingRouteSegments,
        SegmentType aSegmentType, String aDepAd
    ) {

        if (aFlightMovement == null)
            return null;

        final RouteCacheVO routeCacheVO = thruFlightPlanUtility.getThruRouteSegmentList(aFlightMovement, aThruLst, aDepAd);
        if (routeCacheVO != null && routeCacheVO.isValid()) {
            aFlightMovement.setFplCrossingDistance(routeCacheVO.getDistance());
            aFlightMovement.setFplRouteGeom(routeCacheVO.getRoute());
            aFlightMovement.setRouteSegments(flightMovementBuilderUtility.mergeTheSegmentsList(existingRouteSegments,
                routeCacheVO.getRouteSegmentList(), aSegmentType));
        }

        return routeCacheVO != null ? routeCacheVO.getDestAd() : null;
    }

    /**
     * Resolves provided flight movement's arrival aerodrome and time from delta or thru plan stops. Thru plan stops
     * will also overwrite the flight movement EET based on the last stop's arrival time minus departure time.
     *
     * Only applies to flight movements who's arrival aerodrome is not a manually changed field unless it is empty.
     */
    private void checkArrivalAerodromeTimeEet(FlightMovement aFlightMovement) {
        LOG.debug("checkArrivalAerodromeTimeEet");
        String destFromOtherInfo = Item18Parser.parse(aFlightMovement.getOtherInfo(), Item18Field.DEST);
        LOG.debug("destFromOtherInfo {}", destFromOtherInfo);
        String arrivalAd = aFlightMovement.getArrivalAd();
        LOG.debug("arrivalAd {}", arrivalAd);
        String item18Dest = aFlightMovement.getItem18Dest();
        LOG.debug("item18Dest {}", item18Dest);
        boolean isDelta = deltaFlightUtility.isDeltaFlight(aFlightMovement);
        LOG.debug("isDelta {}", isDelta);
        String destAd = aFlightMovement.getDestAd();
        String arrivalTime = aFlightMovement.getArrivalTime();
        LOG.debug("arrivalTime  {}", arrivalTime);

        if (org.apache.commons.lang.StringUtils.isEmpty(arrivalAd) || aFlightMovement.getManuallyChangedFields() == null
                || !aFlightMovement.getManuallyChangedFields().contains("arrival_ad")) {
            if (org.apache.commons.lang.StringUtils.isNotEmpty(destAd)) {
                LOG.debug("destAd {}", destAd);
                try {

                    // if delta flight with item18 DEST/ stops, use special delta flight method to determine arrival ad
                    // as delta flights can have multiple item18 DEST/ stops
                    if (isDelta && aFlightMovement.getItem18Dest() != null && !aFlightMovement.getItem18Dest().isEmpty()) {
                        String deltaDestination = deltaFlightUtility.getDeltaDestination(item18Dest, DeltaFlightUtility.AERODROME);
                        arrivalAd = flightMovementBuilderUtility.checkAerodromeFromDelta(deltaDestination, false);
                    } else if(thruFlightPlanUtility.isThruFlight(aFlightMovement)){
                    	List<ThruFlightPlanVO> list = thruFlightPlanUtility.parseThruPlanRoute(aFlightMovement.getFplRoute(), aFlightMovement.getDepAd(),
                    			DateTimeUtils.addTimeToDate(aFlightMovement.getDateOfFlight(), aFlightMovement.getDepTime()));
                    	if(list !=null && !list.isEmpty()) {
                    		ThruFlightPlanVO lastStop = list.get(list.size()-1);
                    		if(lastStop != null) {
                    			arrivalAd = flightMovementBuilderUtility.checkAerodromeFromDelta(lastStop.getDestAd(), false);
                            	arrivalTime = DateTimeUtils.convertLocalDateTimeToTimeString(lastStop.getArrivalTime());
                            	aFlightMovement.setArrivalTime(arrivalTime);
                                LOG.debug("Calculated arrivalTime THRU Flight --> '{}'", arrivalTime);
                    		}

                            // calculate estimated elapsed time from arrival time minus departure time
                            // assumes that flight cannot operate for longer then 24 hours
                            ThruFlightPlanVO firstStop =  list.get(0);
                    		if (firstStop != null && firstStop.getDepTime() != null && lastStop != null && lastStop.getArrivalTime() != null) {
                    		    LocalTime eetTime = LocalTime.ofSecondOfDay(ChronoUnit.SECONDS.between(firstStop.getDepTime(), lastStop.getArrivalTime()));
                    		    aFlightMovement.setEstimatedElapsedTime(DateTimeUtils.convertLocalTimeToString(eetTime));
                    		    LOG.debug("Calculated eet THRU Flight --> '{}'", aFlightMovement.getEstimatedElapsedTime());
                            }
                    	}
                    }else {
                        arrivalAd = flightMovementBuilderUtility.checkAerodrome(destAd, item18Dest, false);
                    }
                } catch (FlightMovementBuilderException e) {
                    LOG.debug(ARRIVAL_AD_LOG, arrivalAd);
                }
                if (org.apache.commons.lang.StringUtils.isEmpty(arrivalAd)) {
                    try {
                        if (isDelta) {
                            String deltaDestination = deltaFlightUtility.getDeltaDestination(destFromOtherInfo, DeltaFlightUtility.AERODROME);
                            arrivalAd = flightMovementBuilderUtility.checkAerodromeFromDelta(deltaDestination, false);
                        } else {
                            arrivalAd = flightMovementBuilderUtility.checkAerodrome(destAd, destFromOtherInfo, false);
                        }
                    } catch (FlightMovementBuilderException e) {
                        LOG.debug(ARRIVAL_AD_LOG, arrivalAd);
                    }
                }
            }

            aFlightMovement.setArrivalAd(arrivalAd);
            LOG.debug(ARRIVAL_AD_LOG, arrivalAd);
        }

        // 2019-10-18
        // if arrival_time field was not updated manually, recalculate arrival time
        if(aFlightMovement.getManuallyChangedFields() == null
                || !aFlightMovement.getManuallyChangedFields().contains("arrival_time")) {

            if (isDelta) {
                // calculate arrival time from delta times
                if (destAd.equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_ZZZZ)) {
                    if (org.apache.commons.lang.StringUtils.isNotEmpty(item18Dest)) {
                        arrivalTime = deltaFlightUtility.getDeltaDestination(item18Dest, DeltaFlightUtility.ARRIVEAT);
                    } else if (org.apache.commons.lang.StringUtils.isNotEmpty(destFromOtherInfo)) {
                        arrivalTime = deltaFlightUtility.getDeltaDestination(destFromOtherInfo,
                                DeltaFlightUtility.ARRIVEAT);
                    }
                }
            } else {
                // calculate arrival time from departure time and eet
                LOG.debug("depTime  {}  estimatedTime {}", aFlightMovement.getDepTime(), aFlightMovement.getEstimatedElapsedTime());
                if (org.apache.commons.lang.StringUtils.isNotEmpty(aFlightMovement.getDepTime())
                        && org.apache.commons.lang.StringUtils.isNotEmpty(aFlightMovement.getEstimatedElapsedTime())) {
                    arrivalTime = DateTimeUtils.addTime(aFlightMovement.getDepTime(), aFlightMovement.getEstimatedElapsedTime());
                }
            }
        }

        if (arrivalTime != null) {
            try {
                LocalTime.parse(arrivalTime, DateTimeFormatter.ofPattern(JSR310DateConverters.DEFAULT_PATTERN_TIME));
                aFlightMovement.setArrivalTime(arrivalTime);
                LOG.debug("Calculated arrivalTime --> '{}'", arrivalTime);
            } catch (DateTimeParseException dtpe) {
                    LOG.warn("Invalid arrivalTime --> '{}'", arrivalTime);
                    // Do nothing
            }
        }
    }

    /**
     * Mapping FPL Object to FlightMovement. Performs initial validation of the mandatory fields.
     * @param fplObject
     * @return
     */
    private FlightMovement validateAndMapFromFplObject(FplObjectDto fplObject) {
        LOG.debug("Validate Flight Movement from Spatia {}", fplObject);
        FlightMovement validFM = null;

        if(fplObject != null) {
            validFM = FlightMovementObjectMapper.fplObjectToFlightMovement(fplObject);
            if (FlightMovementBuilderUtility.checkNotNullConstraint(validFM)) {
                // 2020-0330   Create error for invalid flight level if it is configured either for the input from Cronos,
                // or is needed for TMA routing TFS 115533

                // 2019-11-19 bug 115345 - system configuration parameter controls flight level validation
                if(flightMovementValidator.isValidateFlightLevel() || flightMovementValidator.isValidateFlightLevelAirspace()) {
                    flightMovementValidator.validateFlightLevel(validFM);
                } else {
                    if(StringUtils.isNotBlank(validFM.getFlightLevel())){
                        String fl = validFM.getFlightLevel().trim();
                        validFM.setFlightLevel(fl);
                    }
                }
                checkAndParseItem18Field(validFM);
                checkArrivalAerodromeTimeEet(validFM);
                
                
            } else {
                final ErrorDTO errorDto = FlightMovementBuilderUtility.getMandatoryFieldNotValued(validFM,
                    "Cannot create/update a flight movement from Spatia", false);
                LOG.debug("Cannot create/update the flight movement {} because {}", validFM, errorDto);
                throw new RejectedException(RejectedReasons.VALIDATION_ERROR, errorDto);
            }
        }
        return validFM;
    }

    /**
     * Validates incoming FPL object and merges it with the existing FlightMovement
     */
    public FlightMovement validateAndMergeFromFplObject(FlightMovement existingFlightMovement,FplObjectDto fplObject)  {
        FlightMovement mergeFlightMovement=null;
        if (existingFlightMovement != null && fplObject != null) {
            FlightMovement updateFlightMovement = validateAndMapFromFplObject(fplObject);

            LOG.debug("Updating Flight Movement {} from Spatia {}", existingFlightMovement,updateFlightMovement);

            if (existingFlightMovement.getSource().equals(FlightMovementSource.NETWORK)
                        || existingFlightMovement.getSource().equals(FlightMovementSource.MANUAL)) {
                    mergeFlightMovement = flightMovementMerge.overwriteAllFieldsExceptUserUpdated(
                        existingFlightMovement, updateFlightMovement, true);
            }

            if (!existingFlightMovement.getSource().equals(FlightMovementSource.NETWORK)
                        && !existingFlightMovement.getSource().equals(FlightMovementSource.MANUAL)) {
                    mergeFlightMovement = flightMovementMerge.updateFlightMovementCreatedByRadarTowerAtc(
                        existingFlightMovement, updateFlightMovement);
            }
        }

        return mergeFlightMovement;
    }


    // Create Flight Movement from RadarSummary
    public FlightMovement createFlightMovementFromRadarSummary(RadarSummary radarSummary)
            throws FlightMovementBuilderException {

        FlightMovement flightMovement = null;

        if (radarSummary != null && radarSummary.isValid()) {
            // Mapping some fields from RadarSummary to FlightMovement
            flightMovement = FlightMovementObjectMapper.radarSummaryToFlightMovement(radarSummary, null, null);
         
            if (FlightMovementBuilderUtility.checkNotNullConstraint(flightMovement)) {
                flightMovementValidator.validateFlightLevel(flightMovement);
                checkAndParseItem18Field(flightMovement);
                checkArrivalAerodromeTimeEet(flightMovement);
                final SegmentType segmentType = SegmentTypeMap
                        .mapFlightMovementSourceToSegmentType(FlightMovementSource.RADAR_SUMMARY);
                
                /*
                    Check aerodrome without resolving to coordinates when not in NAVDB. This is done for billing center
                    which requires aerodrome names and NOT coordinates.

                    calculateRouteInformationByRouteParser are able to handle the resolving of ABMSDB aerodrome names
                    to coordinates which in this case makes it redundant to ask
                    flightMovementBuilderUtility.checkAerodrome to resolve aerodrome not in NAVDB to coordinates.
                 */
                    final String dep = flightMovementBuilderUtility.checkAerodrome(flightMovement.getDepAd(),
                                flightMovement.getItem18Dep(), false);

                    String dest = null;
                    RouteCacheVO routeCacheVO = null;

                    try {
                        dest = flightMovementBuilderUtility.checkAerodrome(flightMovement.getDestAd(),
                                flightMovement.getItem18Dest(), false);
                        
                        if(systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE)) {
                            routeCacheVO = this.flightMovementBuilderUtility.getRouteInformationByLeonardoFile(radarSummary);
                        } else {
                        routeCacheVO = calculateRouteInformationByRouteParser(flightMovement,
                                flightMovement.getRadarRouteText(), segmentType, dep, dest);
                        }
                    } catch (FlightMovementBuilderException fmbe) {
                        catchAerodromeErrors(flightMovement, fmbe);
                    }
                
                
                if (routeCacheVO != null) {
                    flightMovement.setRadarCrossingDistance(routeCacheVO.getDistance());
                    flightMovement.setRadarRoute(routeCacheVO.getRoute());
                    flightMovement.setRouteSegments(flightMovementBuilderUtility.mergeTheSegmentsList(
                        flightMovement.getRouteSegments(), routeCacheVO.getRouteSegmentList(), segmentType));
                }

                // check and calculate the other flight movement fields
                // must be done after route segments have been defined
                checkAndResolveFlightMovement(flightMovement, false, true);

                if ((flightMovement.getFlightCategoryType() == FlightmovementCategoryType.ARRIVAL ||
                    flightMovement.getFlightCategoryType() == FlightmovementCategoryType.DEPARTURE) &&
                    routeCacheVO != null && routeCacheVO.getDistance() != null) {
                        flightMovement.setRadarCrossingDistance(flightMovementValidator.getRadarRouteLengthValid(routeCacheVO.getDistance()));
                }

                // resolve billing center from dep and dest aerodromes based on movement type
                this.resolveBillingCenter(flightMovement, dep, dest);

                // guess EET
                if (StringUtils.isBlank(flightMovement.getEstimatedElapsedTime())) {
                    flightMovementBuilderUtility.guessEstimatedElapsedTime (flightMovement, radarSummary.getDayOfFlight());
                }
            } else {
                FlightMovementBuilderUtility.getMandatoryFieldNotValued(flightMovement, "Cannot create a flight movement from radar logs", true);
            }
        }

        return flightMovement;
    }

    // Update Flight Movement from RadarSummary
    public FlightMovement updateFlightMovementFromRadarSummary(FlightMovement existingFlightMovement,
            RadarSummary radarSummary) throws FlightMovementBuilderException {

        FlightMovement returnFlightMovement = null;
        LOG.debug("Update Flight Movement from RadarSummary");
        if (existingFlightMovement != null && radarSummary != null) {
            // Mapping some fields from RadarSummary to FlightMovement
            FlightMovement updateFlightMovement = FlightMovementObjectMapper.radarSummaryToFlightMovement(radarSummary, existingFlightMovement.getDepTime(), existingFlightMovement.getDateOfFlight());
            if (FlightMovementBuilderUtility.checkAndFillNotNullConstraint(updateFlightMovement,
                    existingFlightMovement)) {
                flightMovementValidator.validateFlightLevel(updateFlightMovement);
                checkAndParseItem18Field(updateFlightMovement);
                checkArrivalAerodromeTimeEet(updateFlightMovement);
                final SegmentType segmentType = SegmentTypeMap
                        .mapFlightMovementSourceToSegmentType(FlightMovementSource.RADAR_SUMMARY);

                /*
                    Check aerodrome without resolving to coordinates when not in NAVDB. This is done for billing center
                    which requires aerodrome names and NOT coordinates.

                    calculateRouteInformationByRouteParser are able to handle the resolving of ABMSDB aerodrome names
                    to coordinates which in this case makes it redundant to ask
                    flightMovementBuilderUtility.checkAerodrome to resolve aerodrome not in NAVDB to coordinates.
                 */
                final String dep = flightMovementBuilderUtility.checkAerodrome(updateFlightMovement.getDepAd(),
                    updateFlightMovement.getItem18Dep(), false);

                String dest = null;
                RouteCacheVO routeCacheVO = null;

                try {
                    dest = flightMovementBuilderUtility.checkAerodrome(
                        updateFlightMovement.getDestAd(), updateFlightMovement.getItem18Dest(), false
                    );
                    if(systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE)) {
                        routeCacheVO = this.flightMovementBuilderUtility.getRouteInformationByLeonardoFile(radarSummary);
                    } else {
                    routeCacheVO = calculateRouteInformationByRouteParser(
                        updateFlightMovement, updateFlightMovement.getRadarRouteText(), segmentType, dep, dest
                    );
                    }
                } catch (FlightMovementBuilderException fmbe) {
                    catchAerodromeErrors(updateFlightMovement, fmbe);
                }
                
               if (routeCacheVO != null) {
                   //if update is done from Leonardo file we don't replace the route info, but instead add the new segment
                   if(systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE)) {
                       
                       List<RouteSegment> rsList = flightMovementBuilderUtility.addTheSegmentsList(
                               existingFlightMovement.getRouteSegments(), routeCacheVO.getRouteSegmentList(), segmentType);
                       
                       updateFlightMovement.setRouteSegments(rsList);
                       
                       if (!rsList.isEmpty()) {
                           LOG.debug("There are {} segments", rsList.size());
                           Double distance = 0.0d;
                           LineMerger merger = new LineMerger();
                           //round total distance
                           for(RouteSegment routeS : rsList) {
                               LOG.debug("routeSegment {} length {}", routeS.getSegmentStartLabel(), routeS.getSegmentLength());
                               distance += routeS.getSegmentLength();
                               if(routeS != null && routeS.getLocation() != null) {
                                   merger.add(routeS.getLocation());
                               }
                           }
                           
                           updateFlightMovement.setRadarCrossingDistance(roundTotalDistance(distance));
                           Collection<LineString>  geometries = merger.getMergedLineStrings();
                           Geometry geom = GeometryUtils.lineStringToMultiLineString(geometries);
                           updateFlightMovement.setRadarRoute(geom);
                           
                       }
                       
                   } else {
     
                    updateFlightMovement.setRadarCrossingDistance(routeCacheVO.getDistance());
                    updateFlightMovement.setRadarRoute(routeCacheVO.getRoute());
                    updateFlightMovement.setRouteSegments(flightMovementBuilderUtility.mergeTheSegmentsList(
                        existingFlightMovement.getRouteSegments(), routeCacheVO.getRouteSegmentList(), segmentType));
                   }
                } else {
                    updateFlightMovement.setRouteSegments(existingFlightMovement.getRouteSegments());
                }

                if (existingFlightMovement.getSource().equals(FlightMovementSource.RADAR_SUMMARY)
                        || existingFlightMovement.getSource().equals(FlightMovementSource.MANUAL)) {
                    returnFlightMovement = flightMovementMerge.overwriteAllFieldsExceptUserUpdated(
                        existingFlightMovement, updateFlightMovement);
                }

                if (existingFlightMovement.getSource().equals(FlightMovementSource.NETWORK)) {
                    returnFlightMovement = flightMovementMerge.updateFlightMovementCreatedByFPL(
                        existingFlightMovement, updateFlightMovement);
                }

                if (existingFlightMovement.getSource().equals(FlightMovementSource.TOWER_LOG)
                        || existingFlightMovement.getSource().equals(FlightMovementSource.ATC_LOG)) {
                    returnFlightMovement = flightMovementMerge.updateFlightMovementCreatedByRadarTowerAtc(
                        existingFlightMovement, updateFlightMovement);
                }

                // check and calculate the other flight movement fields
                // must be done after route segments have been defined
                checkAndResolveFlightMovement(returnFlightMovement, false, true);

                if (returnFlightMovement != null &&
                    (returnFlightMovement.getFlightCategoryType() == FlightmovementCategoryType.ARRIVAL ||
                    returnFlightMovement.getFlightCategoryType() == FlightmovementCategoryType.DEPARTURE) &&
                    routeCacheVO != null && routeCacheVO.getDistance() != null) {
                    if(systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE)) {
                        Double distance = returnFlightMovement.getRadarCrossingDistance();
                        returnFlightMovement.setRadarCrossingDistance(flightMovementValidator.getRadarRouteLengthValid(distance));
                    } else {
                         returnFlightMovement.setRadarCrossingDistance(flightMovementValidator.getRadarRouteLengthValid(routeCacheVO.getDistance()));
                    }
                }

                // resolve billing center from dep and dest aerodromes based on movement type
                this.resolveBillingCenter(returnFlightMovement, dep, dest);
            } else {
                FlightMovementBuilderUtility.getMandatoryFieldNotValued(returnFlightMovement,"Cannot update a flight movement from radar logs", true);
            }
        }

        return returnFlightMovement;
    }

    // Create Flight Movement from TowerMovemntLog
    public FlightMovement createFlightMovementFromTowerMovementLog(TowerMovementLog towerMovementLog)
            throws FlightMovementBuilderException {

        FlightMovement flightMovement = null;

        if (towerMovementLog != null) {
            // Mapping some fields from TowerMovement to FlightMovement
            flightMovement = FlightMovementObjectMapper.towerMovementLogToFlightMovement(towerMovementLog, towerMovementLog.getDepartureTime(), towerMovementLog.getDayOfFlight());
            checkAerodromeForSurveillanceLog(flightMovement, towerMovementLog.getDepartureAerodrome(), towerMovementLog.getDestinationAerodrome());
            if (FlightMovementBuilderUtility.checkNotNullConstraint(flightMovement)) {
                flightMovementValidator.validateFlightLevel(flightMovement);
                checkAndParseItem18Field(flightMovement);
                checkArrivalAerodromeTimeEet(flightMovement);
                SegmentType segmentType = SegmentTypeMap
                        .mapFlightMovementSourceToSegmentType(FlightMovementSource.TOWER_LOG);
                /*
                    Check aerodrome without resolving to coordinates when not in NAVDB. This is done for billing center
                    which requires aerodrome names and NOT coordinates.

                    calculateRouteInformationByRouteParser are able to handle the resolving of ABMSDB aerodrome names
                    to coordinates which in this case makes it redundant to ask
                    flightMovementBuilderUtility.checkAerodrome to resolve aerodrome not in NAVDB to coordinates.
                 */
                final String dep = flightMovementBuilderUtility.checkAerodrome(flightMovement.getDepAd(),
                    flightMovement.getItem18Dep(), true);
                final String dest = flightMovementBuilderUtility.checkAerodrome(flightMovement.getDestAd(),
                    flightMovement.getItem18Dest(), true);
                final RouteCacheVO routeCacheVO = calculateRouteInformationByRouteParser(flightMovement,
                        flightMovement.getRadarTowerLogRouteText(), segmentType, dep, dest);
                if (routeCacheVO != null) {
                    flightMovement.setTowerCrossingDistance(routeCacheVO.getDistance());
                    flightMovement.setTowerLogTrack(routeCacheVO.getRoute());
                    flightMovement.setRouteSegments(flightMovementBuilderUtility.mergeTheSegmentsList(
                        flightMovement.getRouteSegments(), routeCacheVO.getRouteSegmentList(), segmentType));
                }

                // check and calculate the other flight movement fields
                // must be done after route segments have been defined
                checkAndResolveFlightMovement(flightMovement, false, true, towerMovementLog.getPassengers());

                // resolve billing center from dep and dest aerodromes based on movement type
                this.resolveBillingCenter(flightMovement, dep, dest);
            } else {
                FlightMovementBuilderUtility.getMandatoryFieldNotValued(flightMovement, "Cannot create a flight movement from tower logs", true);
            }
        }
        return flightMovement;
    }

    // Update Flight Movement from TowerMovemntLog
    public FlightMovement updateFlightMovementFromTowerMovementLog(FlightMovement existingFlightMovement,
            TowerMovementLog towerMovementLog) throws FlightMovementBuilderException {

        FlightMovement returnFlightMovement = null;
        LOG.debug("Update Flight Movement from TowerMovementLog");
        if (existingFlightMovement != null && towerMovementLog != null) {
            // Mapping some fields from TowerMovementLog to FlightMovement
            FlightMovement updateFlightMovement = FlightMovementObjectMapper
                    .towerMovementLogToFlightMovement(towerMovementLog, existingFlightMovement.getDepTime(), existingFlightMovement.getDateOfFlight());
            checkAerodromeForSurveillanceLog(updateFlightMovement, towerMovementLog.getDepartureAerodrome(), towerMovementLog.getDestinationAerodrome());
            if (FlightMovementBuilderUtility.checkAndFillNotNullConstraint(updateFlightMovement,
                    existingFlightMovement)) {
                flightMovementValidator.validateFlightLevel(updateFlightMovement);
                checkAndParseItem18Field(updateFlightMovement);
                checkArrivalAerodromeTimeEet(updateFlightMovement);
                SegmentType segmentType = SegmentTypeMap
                        .mapFlightMovementSourceToSegmentType(FlightMovementSource.TOWER_LOG);
                /*
                    Check aerodrome without resolving to coordinates when not in NAVDB. This is done for billing center
                    which requires aerodrome names and NOT coordinates.

                    calculateRouteInformationByRouteParser are able to handle the resolving of ABMSDB aerodrome names
                    to coordinates which in this case makes it redundant to ask
                    flightMovementBuilderUtility.checkAerodrome to resolve aerodrome not in NAVDB to coordinates.
                 */
                final String dep = flightMovementBuilderUtility.checkAerodrome(updateFlightMovement.getDepAd(),
                    updateFlightMovement.getItem18Dep(), true);
                final String dest = flightMovementBuilderUtility.checkAerodrome(updateFlightMovement.getDestAd(),
                    updateFlightMovement.getItem18Dest(), true);
                final RouteCacheVO routeCacheVO = calculateRouteInformationByRouteParser(updateFlightMovement,
                        updateFlightMovement.getRadarTowerLogRouteText(), segmentType, dep, dest);
                if (routeCacheVO != null) {
                    updateFlightMovement.setTowerCrossingDistance(routeCacheVO.getDistance());
                    updateFlightMovement.setTowerLogTrack(routeCacheVO.getRoute());
                    updateFlightMovement.setRouteSegments(flightMovementBuilderUtility.mergeTheSegmentsList(
                        existingFlightMovement.getRouteSegments(), routeCacheVO.getRouteSegmentList(), segmentType));
                } else {
                    updateFlightMovement.setRouteSegments(existingFlightMovement.getRouteSegments());
                }

                if (existingFlightMovement.getSource().equals(FlightMovementSource.TOWER_LOG)
                        || existingFlightMovement.getSource().equals(FlightMovementSource.MANUAL)) {
                    returnFlightMovement = flightMovementMerge.overwriteAllFieldsExceptUserUpdated(
                        existingFlightMovement, updateFlightMovement);
                }

                if (existingFlightMovement.getSource().equals(FlightMovementSource.NETWORK)) {
                    returnFlightMovement = flightMovementMerge.updateFlightMovementCreatedByFPL(
                        existingFlightMovement, updateFlightMovement);
                }

                if (existingFlightMovement.getSource().equals(FlightMovementSource.RADAR_SUMMARY)
                        || existingFlightMovement.getSource().equals(FlightMovementSource.ATC_LOG)) {
                    returnFlightMovement = flightMovementMerge.updateFlightMovementCreatedByRadarTowerAtc(
                        existingFlightMovement, updateFlightMovement);
                }

                // check and calculate the other flight movement fields
                // must be done after route segments have been defined
                
                // 2020-01-13 Bug 115200 - Passenger count is updated from Tower Log only for departing flights.
                // For Delta flights 2 Tower logs could be associated with FM, only the one departing from the initial aerodrome is changing passenger count in FM
                // If the flight is domestic - domestic passengers count is updated.
                // If the flight is international - international passengers count is updated
                Integer passengerCount;
                if(existingFlightMovement.getFlightCategoryScope() == FlightmovementCategoryScope.INTERNATIONAL) {
                    passengerCount = existingFlightMovement.getPassengersChargeableIntern();
                } else {
                    passengerCount = existingFlightMovement.getPassengersChargeableDomestic();
                }
                
                if(StringUtils.isNotBlank(towerMovementLog.getDepartureAerodrome()) && StringUtils.isNotBlank(existingFlightMovement.getDepAd()) &&
                        towerMovementLog.getDepartureAerodrome().equals(existingFlightMovement.getDepAd())){
                    passengerCount =  towerMovementLog.getPassengers();
                }
                checkAndResolveFlightMovement(returnFlightMovement, false, true, passengerCount);

                // resolve billing center from dep and dest aerodromes based on movement type
                this.resolveBillingCenter(returnFlightMovement, dep, dest);
            } else {
                FlightMovementBuilderUtility.getMandatoryFieldNotValued(returnFlightMovement,"Cannot update a flight movement from tower logs", true);
            }
        }

        return returnFlightMovement;
    }

    // Make Flight Movement from TowerMovemntLog
    public FlightMovement createFlightMovementFromAtcMovementLog(AtcMovementLog atcMovementLog)
            throws FlightMovementBuilderException {
        FlightMovement flightMovement = null;
        if (atcMovementLog != null) {
            // Mapping some fields from ATCTower to FlightMovement
            flightMovement = FlightMovementObjectMapper.atcMovementLogToFlightMovement(atcMovementLog, atcMovementLog.getDepartureTime(), atcMovementLog.getDayOfFlight());
            checkAerodromeForSurveillanceLog(flightMovement, atcMovementLog.getDepartureAerodrome(), atcMovementLog.getDestinationAerodrome());
            if (FlightMovementBuilderUtility.checkNotNullConstraint(flightMovement)) {
                flightMovementValidator.validateFlightLevel(flightMovement);
                checkAndParseItem18Field(flightMovement);
                checkArrivalAerodromeTimeEet(flightMovement);
                flightMovement.setAtcLogRouteText(this.do_getBillableAtcRouteString(flightMovement, atcMovementLog));
                SegmentType segmentType = SegmentTypeMap
                        .mapFlightMovementSourceToSegmentType(FlightMovementSource.ATC_LOG);

                /*
                    Check aerodrome without resolving to coordinates when not in NAVDB. This is done for billing center
                    which requires aerodrome names and NOT coordinates.

                    calculateRouteInformationByRouteParser are able to handle the resolving of ABMSDB aerodrome names
                    to coordinates which in this case makes it redundant to ask
                    flightMovementBuilderUtility.checkAerodrome to resolve aerodrome not in NAVDB to coordinates.
                 */
                final String dep = flightMovementBuilderUtility.checkAerodrome(flightMovement.getDepAd(),
                    flightMovement.getItem18Dep(), false);
                final String dest = flightMovementBuilderUtility.checkAerodrome(flightMovement.getDestAd(),
                    flightMovement.getItem18Dest(), false);

                RouteCacheVO routeCacheVO = calculateRouteInformationByRouteParser(flightMovement,
                        flightMovement.getAtcLogRouteText(), segmentType, dep, dest);
                if (routeCacheVO != null) {
                    flightMovement.setAtcCrossingDistance(routeCacheVO.getDistance());
                    flightMovement.setAtcLogTrack(routeCacheVO.getRoute());
                    flightMovement.setRouteSegments(flightMovementBuilderUtility.mergeTheSegmentsList(
                        flightMovement.getRouteSegments(), routeCacheVO.getRouteSegmentList(), segmentType));
                }

                // check and calculate the other flight movement fields
                // must be done after route segments have been defined
                checkAndResolveFlightMovement(flightMovement, false, true, null);

                // resolve billing center from dep and dest aerodromes based on movement type
                this.resolveBillingCenter(flightMovement, dep, dest);

            } else {
                FlightMovementBuilderUtility.getMandatoryFieldNotValued(flightMovement,"Cannot create a flight movement from ATC logs", true);

            }
        }

        return flightMovement;
    }

    // Update Flight Movement from AtcMovementLog
    public FlightMovement updateFlightMovementFromAtcMovementLog(FlightMovement existingFlightMovement,
            AtcMovementLog atcMovementLog) throws FlightMovementBuilderException {

        FlightMovement returnFlightMovement = null;
        LOG.debug("Update Flight Movement from ATCMovementLOG");
        if (existingFlightMovement != null && atcMovementLog != null) {

            // Mapping some fields from AtcMovementLog to FlightMovement
            FlightMovement updateFlightMovement = FlightMovementObjectMapper
                    .atcMovementLogToFlightMovement(atcMovementLog, existingFlightMovement.getDepTime(), existingFlightMovement.getDateOfFlight());
            checkAerodromeForSurveillanceLog(updateFlightMovement, atcMovementLog.getDepartureAerodrome(), atcMovementLog.getDestinationAerodrome());
            if (FlightMovementBuilderUtility.checkAndFillNotNullConstraint(updateFlightMovement,
                    existingFlightMovement)) {
                flightMovementValidator.validateFlightLevel(updateFlightMovement);
                checkAndParseItem18Field(updateFlightMovement);
                checkArrivalAerodromeTimeEet(updateFlightMovement);
                updateFlightMovement
                        .setAtcLogRouteText(this.do_getBillableAtcRouteString(updateFlightMovement, atcMovementLog));
                SegmentType segmentType = SegmentTypeMap
                        .mapFlightMovementSourceToSegmentType(FlightMovementSource.ATC_LOG);
                /*
                    Check aerodrome without resolving to coordinates when not in NAVDB. This is done for billing center
                    which requires aerodrome names and NOT coordinates.

                    calculateRouteInformationByRouteParser are able to handle the resolving of ABMSDB aerodrome names
                    to coordinates which in this case makes it redundant to ask
                    flightMovementBuilderUtility.checkAerodrome to resolve aerodrome not in NAVDB to coordinates.
                 */
                final String dep = flightMovementBuilderUtility.checkAerodrome(updateFlightMovement.getDepAd(),
                    updateFlightMovement.getItem18Dep(), false);
                final String dest = flightMovementBuilderUtility.checkAerodrome(updateFlightMovement.getDestAd(),
                    updateFlightMovement.getItem18Dest(), false);
                final RouteCacheVO routeCacheVO = calculateRouteInformationByRouteParser(updateFlightMovement,
                        updateFlightMovement.getAtcLogRouteText(), segmentType, dep, dest);
                if (routeCacheVO != null) {
                    updateFlightMovement.setAtcCrossingDistance(routeCacheVO.getDistance());
                    updateFlightMovement.setAtcLogTrack(routeCacheVO.getRoute());
                    updateFlightMovement.setRouteSegments(flightMovementBuilderUtility.mergeTheSegmentsList(
                        existingFlightMovement.getRouteSegments(), routeCacheVO.getRouteSegmentList(), segmentType));
                } else {
                    updateFlightMovement.setRouteSegments(existingFlightMovement.getRouteSegments());
                }

                if (existingFlightMovement.getSource().equals(FlightMovementSource.ATC_LOG)
                        || existingFlightMovement.getSource().equals(FlightMovementSource.MANUAL)) {
                    returnFlightMovement = flightMovementMerge.overwriteAllFieldsExceptUserUpdated(
                        existingFlightMovement, updateFlightMovement);
                }

                if (existingFlightMovement.getSource().equals(FlightMovementSource.NETWORK)) {
                    returnFlightMovement = flightMovementMerge.updateFlightMovementCreatedByFPL(
                        existingFlightMovement, updateFlightMovement);
                }

                if (existingFlightMovement.getSource().equals(FlightMovementSource.RADAR_SUMMARY)
                        || existingFlightMovement.getSource().equals(FlightMovementSource.TOWER_LOG)) {
                    returnFlightMovement = flightMovementMerge.updateFlightMovementCreatedByRadarTowerAtc(
                        existingFlightMovement, updateFlightMovement);
                }

                // check and calculate the other flight movement fields
                // must be done after route segments have been defined
                checkAndResolveFlightMovement(returnFlightMovement, false, true, null);

                // resolve billing center from dep and dest aerodromes based on movement type
                this.resolveBillingCenter(returnFlightMovement, dep, dest);
            } else {
                FlightMovementBuilderUtility.getMandatoryFieldNotValued(returnFlightMovement,"Cannot update a flight movement from ATC logs", true);

            }
        }

        return returnFlightMovement;
    }

    private String do_getBillableAtcRouteString(FlightMovement flightMovement, AtcMovementLog atcMovementLog) {
        final FlightMovementValidationViewModel x = this.flightMovementValidator
                .validateFlightMovementCategory(flightMovement);
        /*
         * TODO: Temporary keep FlightMovementType
         */
        flightMovement.setMovementType(x.getMovementType());

        if (x.getFlightmovementType() != null) {
        	flightMovement.setFlightCategoryType(x.getFlightmovementType());
            return atcMovementLogBillableRouteFinder.getBillableRouteString(flightMovement,atcMovementLog);

        }
        return null;
    }

    public FlightMovement updateFlightMovementFromPassengerPassengerServiceReturn(
        final FlightMovement existingFlightMovement, final PassengerServiceChargeReturn passengerServiceChargeReturn
    ) {
        LOG.debug("Update Flight Movement from PassengerPassengerServiceReturn");

        FlightMovement flightMovementResult = null;
        if (existingFlightMovement != null && passengerServiceChargeReturn != null) {

            FlightMovement updateFlightMovement = FlightMovementObjectMapper
                .passengerServiceReturnToFlightMovement(passengerServiceChargeReturn);
            flightMovementResult = flightMovementMerge.overwritePassengerFieldsExceptUserUpdated(
                existingFlightMovement, updateFlightMovement);

            // 2019-01-21 - add additional information from Passenger Serv Charge
            flightMovementResult.setPassengersChargeableDomestic(passengerServiceChargeReturn.getChargeableDomesticPassengers());
            flightMovementResult.setPassengersChargeableIntern(passengerServiceChargeReturn.getChargeableItlPassengers());
            flightMovementResult.setPassengersJoiningAdult(passengerServiceChargeReturn.getJoiningPassengers());
            flightMovementResult.setPassengersTransitAdult(passengerServiceChargeReturn.getTransitPassengers());

            flightMovementResult.setExemptArrivingPaxDomesticAirport(passengerServiceChargeReturn.getExemptArrivingPaxDomesticAirport());
            flightMovementResult.setExemptDepartingPaxDomesticAirport(passengerServiceChargeReturn.getExemptDepartingPaxDomesticAirport());

            flightMovementResult.setExemptTransferPaxDomesticAirport(passengerServiceChargeReturn.getExemptTransferPaxDomesticAirport());
            flightMovementResult.setExemptLandingPaxDomesticAirport(passengerServiceChargeReturn.getExemptLandingPaxDomesticAirport());

            flightMovementResult.setArrivingChildDomesticAirport(passengerServiceChargeReturn.getArrivingChildDomesticAirport());
            flightMovementResult.setArrivingPaxDomesticAirport(passengerServiceChargeReturn.getArrivingPaxDomesticAirport());

            flightMovementResult.setLandingPaxDomesticAirport(passengerServiceChargeReturn.getLandingPaxDomesticAirport());
            flightMovementResult.setLandingChildDomesticAirport(passengerServiceChargeReturn.getLandingChildDomesticAirport());

            flightMovementResult.setTransferPaxDomesticAirport(passengerServiceChargeReturn.getTransferPaxDomesticAirport());
            flightMovementResult.setTransferChildDomesticAirport(passengerServiceChargeReturn.getTransferChildDomesticAirport());

            flightMovementResult.setDepartingChildDomesticAirport(passengerServiceChargeReturn.getDepartingChildDomesticAirport());
            flightMovementResult.setDepartingPaxDomesticAirport(passengerServiceChargeReturn.getDepartingPaxDomesticAirport());

            flightMovementResult.setLoadedGoods(passengerServiceChargeReturn.getLoadedGoods());
            flightMovementResult.setLoadedMail(passengerServiceChargeReturn.getLoadedMail());

            flightMovementResult.setDischargedGoods(passengerServiceChargeReturn.getDischargedGoods());
            flightMovementResult.setDischargedMail(passengerServiceChargeReturn.getDischargedMail());

            // check and calculate the other flight movement fields
            // must be done after route segments have been defined
            checkAndResolveFlightMovement(flightMovementResult, false, true, null);
        }

        return flightMovementResult;
    }

    public Boolean recalculateCharges(FlightMovement flightMovement) {
        return chargesUtility.setCharges(flightMovement);
    }

    /* Helper Methods */

    private void checkAndResolveFlightMovement(final FlightMovement flightMovement, boolean forInvoice, boolean checkRegNum) {
        checkAndResolveFlightMovement(flightMovement, forInvoice,checkRegNum, null);
    }

    /**
     * In this method there are all check methods for creation a FlightMovement.
     *
     * @param flightMovement flight movement to check and resolve
     */
    private void checkAndResolveFlightMovement(final FlightMovement flightMovement, boolean forInvoice, boolean checkRegNum, Integer passengerCount) {
        if (flightMovement != null) {

            if (flightMovement.getCreatedAt() == null) {
                flightMovement.setCreatedAt(LocalDateTime.now());
            }

            //clearing flight note
            flightMovement.setFlightNotes("");

            // check and resolve aircraft type if not manually changed
            if (!FlightMovementBuilderMergeUtility.checkManuallyFieldChanged("aircraftType",flightMovement.getManuallyChangedFields())) {
                String aircraftType = flightMovementBuilderUtility.checkAircraftType(flightMovement);

                // if aircraft type is not resolved, leave it to the value from the
                // flight movement
                if (aircraftType != null && !aircraftType.isEmpty()) {
                    flightMovement.setAircraftType(aircraftType);
                }
            }

            // check and resolve item 18 aircraft type if not manually changed
            if (!FlightMovementBuilderMergeUtility.checkManuallyFieldChanged("item18AircraftType",flightMovement.getManuallyChangedFields())) {
                String item18AircraftType = flightMovementBuilderUtility.checkItem18AircraftType(flightMovement);
                flightMovement.setItem18AircraftType(item18AircraftType);
            }

            // check aircraft registration number
            if (checkRegNum) {
                String item18RegNum = flightMovementBuilderUtility.checkAircraftRegistrationNumber(flightMovement);
                flightMovement.setItem18RegNum(item18RegNum);
            }

            // check actual MTOW
            Double actualMtow = flightMovementBuilderUtility.checkMTOW(flightMovement);
            flightMovement.setActualMtow(actualMtow);

            // check Average MTOW
            Double averageMtow = flightMovementBuilderUtility.checkAverageMTOW(actualMtow);
            flightMovement.setAverageMassFactor(averageMtow);

            // check WakeTurbolence
            String wakeTurbolance = flightMovementBuilderUtility.checkWakeTurbolance(flightMovement.getAircraftType());
            flightMovement.setWakeTurb(wakeTurbolance);

            // type - check and validation the type
            FlightMovementValidationViewModel flightMovementValidationViewModel = flightMovementValidator
                .validateFlightMovementCategory(flightMovement);
            /*
             * TODO: Temporary keep FlightMovementType
             */
            flightMovement.setMovementType(flightMovementValidationViewModel.getMovementType());

            // check if flight movement is a Delta Flight
            // not required but SHOULD be done after movement type validation
            Boolean isDeltaFlight = deltaFlightUtility.isDeltaFlight(flightMovement);
            flightMovement.setDeltaFlight(isDeltaFlight);

            // check if flight movement is a Thru Flight
            Boolean isThruFlight = thruFlightPlanUtility.isThruFlight(flightMovement);
            flightMovement.setThruFlight(isThruFlight);

            //set fields
            flightMovement.setFlightCategoryNationality(flightMovementValidationViewModel.getFlightmovementNationality());
            flightMovement.setFlightCategoryScope(flightMovementValidationViewModel.getFlightmovementScope());
           	flightMovement.setFlightCategoryType(flightMovementValidationViewModel.getFlightmovementType());
           	flightMovement.setFlightmovementCategory(flightMovementValidationViewModel.getFlightmovementCategory());
           	if(flightMovementValidationViewModel.getFlightmovementCategory() != null) {
           		flightMovement.setEnrouteInvoiceCurrency(flightMovementValidationViewModel.getFlightmovementCategory().getEnrouteInvoiceCurrency());
           		flightMovement.setEnrouteResultCurrency(flightMovementValidationViewModel.getFlightmovementCategory().getEnrouteResultCurrency());
           	}

           	// Calculate billing_date for the flights defined by FPL only
           	if (flightMovement.getBillingDate() == null || (
           	    StringUtils.isBlank(flightMovement.getAtcLogRouteText()) &&
                StringUtils.isBlank(flightMovement.getRadarRouteText()) &&
                StringUtils.isBlank(flightMovement.getRadarTowerLogRouteText()))
            ) {
           		flightMovement.setBillingDate(flightMovementBillingDateEstimator
                    .resolveBillingDate(flightMovement));
           	}

           	// Look for a leasing contract to change account and nationality
           	boolean isFlightReassignment = flightReassignment(flightMovement);
           	if (!isFlightReassignment) {
           	    // 2019-09-27 If account field was manually changed to an account - keep it,
           	    // if the field was manually changed and left blanc - resolve account
                Account account;
                if (flightMovement.getManuallyChangedFields() != null && flightMovement.getManuallyChangedFields().contains("associated_account_id")) {
                    if(flightMovement.getAccount() != null && flightMovement.getAccount().getId() != null) {
                        account = accountService.getOne(flightMovement.getAccount().getId());
                    } else { // recalculate account if it was set to null by the user
                        account = flightMovementBuilderUtility.resolveAccountForFlightMovement(flightMovement);
                    }
                } else { // if no manual intervention recalculate account
                   account = flightMovementBuilderUtility.resolveAccountForFlightMovement(flightMovement);
                }

                flightMovement.setAccount(account);
           	} else {
                flightMovementValidator.recalculateFlightMovementCategory(flightMovement);
            }

            // calculate billable route and distance
            this.flightMovementBillable.calculateBillableRouteAndDistance(flightMovement);

            // find passenger service charge return and update flight movement
            checkAndResolveFlightMovementPassengerCharges(flightMovement, passengerCount);

            // calculate charges for new flight movement
            recalculateCharges(flightMovement);

            // status - check and validation the status
            handleStatus(flightMovement, forInvoice);
        }
    }

    /**
     * Check and resolve passenger counts by existing passenger service charge returns
     * or by generic passenger count.
     *
     * Does not update flight movement manually changed passenger fields.
     */
    private void checkAndResolveFlightMovementPassengerCharges(
        final FlightMovement existingFlightMovement, final Integer passengerCount
    ) {

        // find passenger service charge return and update flight movement
        PassengerServiceChargeReturn pscr = findPassengerServiceChargeReturnByFM(existingFlightMovement);

        // if no existing passenger service charge return and generic passenger count supplied
        // create a new passenger service charge return with count by movement type
        if (pscr == null && passengerCount != null) {

            pscr = new PassengerServiceChargeReturn();

            pscr.setFlightId(existingFlightMovement.getFlightId());
            pscr.setDayOfFlight(existingFlightMovement.getDateOfFlight());
            pscr.setDepartureTime(existingFlightMovement.getDepTime());

            if (existingFlightMovement.getFlightCategoryScope() == FlightmovementCategoryScope.INTERNATIONAL) {
                pscr.setChargeableItlPassengers(passengerCount);
            } else {
                pscr.setChargeableDomesticPassengers(passengerCount);
            }
        }

        // if no existing passenger service charge return and no generic passenger count supplied
        // simply return as no passenger information can be found to resolve
        if (pscr == null) return;

        // map passenger service charge return to flight movement and update existing flight movement passenger
        // fields that have not been manually changed
        FlightMovement updateFlightMovement = FlightMovementObjectMapper.passengerServiceReturnToFlightMovement(pscr);
        flightMovementMerge.overwritePassengerFieldsExceptUserUpdated(existingFlightMovement, updateFlightMovement);
    }

    private boolean flightReassignment(final FlightMovement fm) {

        boolean isFlightReassignment = false;
        LOG.debug("Looking for registration with TYPE: {}, SCOPE: {}, NATIONALITY: {} ---> CATEGORY: {} ",
                fm.getFlightCategoryType(), fm.getFlightCategoryScope(), fm.getFlightCategoryNationality(), fm.getFlightmovementCategory());
        Map<String, Object> params = createParamsForQuery(fm, IDENTIFICATION_TYPE_REGISTRATION);
        DatabaseQueryResult queryRes = databaseQueryService.query(FLIGHT_REASSIGNMENT_SQL_FILE, params);
        List<Map<String, Object>> result = null;
        if (queryRes != null) {
            result = queryRes.data();
        }
        if (queryRes == null || result == null || result.isEmpty()) {
            LOG.debug("No flight reassignment found: looking for flight id");
            params = createParamsForQuery(fm, IDENTIFICATION_TYPE_FLIGHT_ID);
            queryRes = databaseQueryService.query(FLIGHT_REASSIGNMENT_SQL_FILE, params);
            if (queryRes != null) {
                result = queryRes.data();
            }
            if (queryRes == null || result == null || result.isEmpty()) {
                LOG.debug("No flight reassignment found: looking for icao");
                params = createParamsForQuery(fm, IDENTIFICATION_TYPE_ICAO_CODE);
                queryRes = databaseQueryService.query(FLIGHT_REASSIGNMENT_SQL_FILE, params);
                if (queryRes != null) {
                    result = queryRes.data();
                }
            }
        }
        if (result != null && !result.isEmpty()) {
            LOG.debug("Flight reassignment found {} for flight {}", result, fm.getFlightId());
            Integer accountId = (Integer) result.get(0).get("account_id");
            Account account = accountService.getOne(accountId);
            fm.setFlightCategoryNationality(account.getNationality());
            fm.setAccount(account);
            isFlightReassignment = true;
        }
        return isFlightReassignment;
    }

    private static String getICAOCodePrefixBygetFlightId(String flightId) {

        String returnValue = null;

        if (StringUtils.isNotBlank(flightId) && flightId.length() >= SIZE_PREFIX_ICAO_CODE) {
            returnValue = flightId.substring(0, SIZE_PREFIX_ICAO_CODE);
        }
        return returnValue;
    }

    private Map<String, Object> createParamsForQuery(FlightMovement aFm, String aIdentificationType) {
        final Map<String, Object> params = new HashMap<>();
        if (aIdentificationType.equals(IDENTIFICATION_TYPE_REGISTRATION)) {
            params.put(IDENTIFICATION_TYPE, IDENTIFICATION_TYPE_REGISTRATION);
            params.put(IDENTIFIER_TEXT, aFm.getItem18RegNum());
        } else if (aIdentificationType.equals(IDENTIFICATION_TYPE_FLIGHT_ID)) {
            params.put(IDENTIFICATION_TYPE, IDENTIFICATION_TYPE_FLIGHT_ID);
            params.put(IDENTIFIER_TEXT, aFm.getFlightId());
        } else if (aIdentificationType.equals(IDENTIFICATION_TYPE_ICAO_CODE)) {
            params.put(IDENTIFICATION_TYPE, IDENTIFICATION_TYPE_ICAO_CODE);
            String icao = getICAOCodePrefixBygetFlightId(aFm.getFlightId());
            params.put(IDENTIFIER_TEXT, icao);
        }
        params.put("flight_date", aFm.getDateOfFlight());
        if (aFm.getFlightCategoryNationality() != null) {
            switch (aFm.getFlightCategoryNationality()) {
            case NATIONAL:
                params.put("applies_to_nationality_national", Boolean.TRUE);
                break;
            case FOREIGN:
                params.put("applies_to_nationality_foreign", Boolean.TRUE);
                break;
            default:
                break;
            }
        }
        if (aFm.getFlightCategoryScope() != null) {
            switch (aFm.getFlightCategoryScope()) {
            case DOMESTIC:
                params.put("applies_to_scope_domestic", Boolean.TRUE);
                break;
            case REGIONAL:
                params.put("applies_to_scope_regional", Boolean.TRUE);
                break;
            case INTERNATIONAL:
                params.put("applies_to_scope_international", Boolean.TRUE);
                break;
            default:
                break;
            }
        }
        if (aFm.getFlightCategoryType() != null) {
            switch (aFm.getFlightCategoryType()) {
            case DOMESTIC:
                params.put("applies_to_type_domestic", Boolean.TRUE);
                params.put("aerodrome_dest", aFm.getDestAd());
                params.put("aerodrome_dep", aFm.getDepAd());
                break;
            case ARRIVAL:
                params.put("applies_to_type_arrival", Boolean.TRUE);
                params.put("aerodrome_dest", aFm.getDestAd());
                break;
            case DEPARTURE:
                params.put("applies_to_type_departure", Boolean.TRUE);
                params.put("aerodrome_dep", aFm.getDepAd());
                break;
            case OVERFLIGHT:
                params.put("applies_to_type_overflight", Boolean.TRUE);
                break;
            default:
                break;
            }
        }
        return params;
    }

    /**
     * This method handles the status for flight movement:
    */
    public void handleStatus(FlightMovement flightMovement, boolean aForInvoice) {
        FlightMovementValidationViewModel flightMovementValidationViewModel = flightMovementValidator.validateFlightMovementStatus(flightMovement, aForInvoice);

        flightMovement.setStatus(flightMovementValidationViewModel.getStatus());

        // insert issues in resolution error
        flightMovement.setResolutionErrorsSet(flightMovementValidationViewModel.getIssues());
    }

    /* Helper methods */

    /**
     * This method set the following information:
     * <ul>
     * <li>geographical path of the route in WKT format;</li>
     * <li>distance in km;</li>
     * <li>route Segments;</li>
     * </ul>
     * We use this method for FPL Object from Spatia.
     */
    private void calculateRouteInformationByRouteParser(final FlightMovement flightMovement,
                                                        final List<RouteSegment> existingRouteSegments,
                                                        final SegmentType segmentType, final String dep,
                                                        final String dest) throws FlightMovementBuilderException {

        // Calculate Route and Crossing Distance
        if (StringUtils.isNotBlank(flightMovement.getFplRoute())) {
          
            RouteCacheVO routeCacheVO =null;
          //2020-04-02 TFS 115533 - TMA handling
            if(systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE)) {
                Double fl = Double.valueOf(systemConfigurationService.getIntOrZero(SystemConfigurationItemName.DEFAULT_FLIGHT_LEVEL));
                routeCacheVO = flightMovementBuilderUtility.getRouteInformationByRouteParserExcludeTmas(dep, dest,
                        flightMovement.getFplRoute(), segmentType, flightMovement.getCruisingSpeedOrMachNumber(),
                        flightMovement.getEstimatedElapsedTime(), FlightUtility.convertFlightLevel(flightMovement.getFlightLevel(),fl));
            } else {
                routeCacheVO = flightMovementBuilderUtility.getRouteInformationByRouteParser(dep, dest,
                    flightMovement.getFplRoute(), segmentType, flightMovement.getCruisingSpeedOrMachNumber(),
                    flightMovement.getEstimatedElapsedTime());
            }
            
            if (routeCacheVO != null && routeCacheVO.getDistance() == null) {
                // If a routeCache cannot be built, check if either dest/dep are in ABMSDB
                String depCoords = flightMovementBuilderUtility.checkAerodrome(flightMovement.getDepAd(), flightMovement.getItem18Dep(), true);
                String destCoords = flightMovementBuilderUtility.checkAerodrome(flightMovement.getDestAd(), flightMovement.getItem18Dest(), true);
                
                //2020-04-02 TFS 115533 - TMA handling
                if(systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE)) {
                    Double fl = Double.valueOf(systemConfigurationService.getIntOrZero(SystemConfigurationItemName.DEFAULT_FLIGHT_LEVEL));
                    routeCacheVO = flightMovementBuilderUtility.getRouteInformationByRouteParserExcludeTmas(depCoords, destCoords,
                            flightMovement.getFplRoute(), segmentType, flightMovement.getCruisingSpeedOrMachNumber(),
                            flightMovement.getEstimatedElapsedTime(), FlightUtility.convertFlightLevel(flightMovement.getFlightLevel(),fl));
                } else {
                routeCacheVO = flightMovementBuilderUtility.getRouteInformationByRouteParser(depCoords, destCoords,
                    flightMovement.getFplRoute(), segmentType, flightMovement.getCruisingSpeedOrMachNumber(),
                    flightMovement.getEstimatedElapsedTime());
                }
            }

            if (routeCacheVO != null) {
                flightMovement.setFplCrossingDistance(routeCacheVO.getDistance());
                if(routeCacheVO.getDistance() == null) {
                    flightMovement.setFplRouteGeom(null);
                } else
                    flightMovement.setFplRouteGeom(routeCacheVO.getRoute());

                /* Please notice: the existingRouteSegments is valued only if we are updating an existing flight movement.
                 * In this case we need to provide that list separately because the current flightMovement object could be
                 * an object not persisted yet and merged with the existing flight movement.
                 *
                 * Instead, if we're creating a new Flight Movement, the flightMovement object will contain a new list of
                 * segments created by the segments embedded into the routeCacheVO (if it is defined).
                 */
                flightMovement.setRouteSegments(flightMovementBuilderUtility.mergeTheSegmentsList(
                    existingRouteSegments, routeCacheVO.getRouteSegmentList(), segmentType));
            } else {
                flightMovement.setRouteSegments(existingRouteSegments);
            }
        } else {
            LOG.warn("I can't get Route From Route Parse because is NULL");
        }

    }

    /**
     * DELTA/Charter flights - Botswana specific functionality Destination
     * aerodrome is the last stop from Item18 DEST/ field the route is the
     * string constructed from all stops
     *
     * This method set the following information:
     * <ul>
     * <li>geographical path of the route in WKT format;</li>
     * <li>distance in km;</li>
     * <li>route Segments;</li>
     * </ul>
     * We use this method for FPL Object from Spatia.
     */
    private String calculateRouteInformationByRouteParserDelta(final FlightMovement flightMovement,
                                                               final List<RouteSegment> existingRouteSegments,
                                                               final SegmentType segmentType, final String depAd) {

        if (flightMovement == null)
            return null;

        final RouteCacheVO routeCacheVO = deltaFlightUtility.getDeltaRouteSegmentList(flightMovement, depAd);
        if (routeCacheVO.isValid()) {
            flightMovement.setFplCrossingDistance(routeCacheVO.getDistance());
            flightMovement.setFplRouteGeom(routeCacheVO.getRoute());

            /* Please notice: the existingRouteSegments is valued only if we are updating an existing flight movement.
             * In this case we need to provide that list separately because the current flightMovement object could be
             * an object not persisted yet and merged with the existing flight movement.
             *
             * Instead, if we're creating a new Flight Movement, the flightMovement object will contain a new list of
             * segments created by the segmments embedded into the routeCacheVO (if it is defined).
             */
             flightMovement.setRouteSegments(flightMovementBuilderUtility.mergeTheSegmentsList(
                    existingRouteSegments, routeCacheVO.getRouteSegmentList(), segmentType));
         } else {
                flightMovement.setRouteSegments(existingRouteSegments);
         }

        return routeCacheVO.getDestAd();
    }

    /**
     * This method set the following information:
     * <ul>
     * <li>geographical path of the route in WKT format;</li>
     * <li>distance in km;</li>
     * <li>route Segments;</li>
     * </ul>
     * <p>
     * It comes Not from a flight plan, But from one of the following three
     * possible sources:
     * <ul>
     * <li>Radar Summary;</li>
     * <li>ATC Movement Log;</li>
     * <li>Tower Aircraft / Passenger Movement Log;</li>
     * </ul>
     */
    private RouteCacheVO calculateRouteInformationByRouteParser(final FlightMovement flightMovement, final String route,
            final SegmentType segmentType, final String dep, final String dest) {

        RouteCacheVO routeCacheVO = null;
        String routeText = route;
        if (segmentType.equals(SegmentType.RADAR)) {
            
            routeText = getRouteForRadar(routeText, dep, dest, flightMovement);
        }
        // Calculate Route and Crossing Distance
        if (StringUtils.isNotBlank(routeText)) {

            // for tower log we concat route with aerodromes
            if (segmentType.equals(SegmentType.TOWER)) {
                routeText = getRouteWithAerodromes(routeText, dep, dest);
            }
          //2020-04-02 TFS 115533 - TMA handling
            if(systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE)) {
                Double fl = Double.valueOf(systemConfigurationService.getIntOrZero(SystemConfigurationItemName.DEFAULT_FLIGHT_LEVEL));
                routeCacheVO = flightMovementBuilderUtility.getRouteInformationByRouteParserExcludeTmas(dep, routeText, dest,
                        segmentType, FlightUtility.convertFlightLevel(flightMovement.getFlightLevel(),fl));
            } else {
                routeCacheVO = flightMovementBuilderUtility.getRouteInformationByRouteParser(dep, routeText, dest,
                        segmentType);
            }

        } else {
            LOG.warn("Route is NULL");
        }

        return routeCacheVO;
    }

    
    /**
     * The purpose of this method is to link a flight movement to the right billing center.
     *
     * @param flightMovement: the flight movement to update, mandatory
     * @param depAerodromeId: the ID of the departure aerodrome converted by {@link FlightMovementBuilderUtility#checkAerodrome(String, String, boolean)}
     * @param destAerodromeId: the ID of the destination aerodrome converted by {@link FlightMovementBuilderUtility#checkAerodrome(String, String, boolean)}
     */
    public void resolveBillingCenter(final FlightMovement flightMovement, final String depAerodromeId, final String destAerodromeId) {
        if (flightMovement == null) return;

        // set default value for billing center to null
        // this will allow it to be reset if billing center
        // cannot be resolved
        BillingCenter billingCenter = null;

        // set movement type from flight movement
        // if null, consider to be OTHER
        final FlightmovementCategoryType flightMovementType = flightMovement.isOTHER()
            ? FlightmovementCategoryType.OTHER : flightMovement.getFlightCategoryType();

        // attempt to resolve billing centre based on movement type
        // DOMESTIC, DEPARTURE, and ARRIVAL will default to HQ if billing centre is NOT
        // resolved by departure and/or destination aerodrome
        switch (flightMovementType) {
            case DOMESTIC:
                billingCenter = getBillingCenterByDomestic(flightMovement, depAerodromeId, destAerodromeId);
                if (billingCenter == null) billingCenter = billingCenterService.findHq();
                break;
            case DEPARTURE:
                billingCenter = getBillingCentreByAerodromeId(depAerodromeId, flightMovement);
                if (billingCenter == null) billingCenter = billingCenterService.findHq();
                break;
            case ARRIVAL:
                billingCenter = getBillingCentreByAerodromeId(destAerodromeId, flightMovement);
                if (billingCenter == null) billingCenter = billingCenterService.findHq();
                break;
            case OVERFLIGHT:
                billingCenter = billingCenterService.findHq();
                break;
            default:
                LOG.debug("The billing center non HQ cannot be determined if the movement type is {} for the FLM {}",
                    flightMovementType, flightMovement);
                break;
        }

        // set billing center to defined value whether null or not
        flightMovement.setBillingCenter(billingCenter);
    }

    /**
     * Attempt to get the Flight Movement Billing Centre based on supplied aerodrome id.
     *
     * @param aerodromeId the ID of the billable aerodrome converted by {@link FlightMovementBuilderUtility#checkAerodrome(String, String, boolean)}
     * @param flightMovement the flight movement, for logging purposes
     * @return billing centre associated to aerodrome id
     */
    private BillingCenter getBillingCentreByAerodromeId(String aerodromeId, FlightMovement flightMovement) {
        final Aerodrome aerodrome = flightMovementAerodromeService.getAeroDrome(aerodromeId);
        if (aerodrome != null) {
            final BillingCenter billingCenter = aerodrome.getBillingCenter();
            if (billingCenter != null) {
                LOG.debug("{} detected for FLM {} using the aerodrome {}", billingCenter, flightMovement, aerodromeId);
                return billingCenter;
            } else {
                LOG.warn("Cannot get the billing center for the FLM {} because the aerodrome {} has no one billing center associated",
                    flightMovement, aerodromeId);
            }
        } else {
            LOG.debug("Cannot get the billing center for the FLM {} because the aerodrome {} is not known/found in the Aerodromes table",
                flightMovement, aerodromeId);
        }
        return null;
    }

    /**
     * Attempt to get the Flight Movement Billing Centre based on DOMESTIC flight movements.
     *
     * @param flightMovement: the DOMESTIC flight movement to update, mandatory
     * @param depAerodromeId: the ID of the departure aerodrome converted by {@link FlightMovementBuilderUtility#checkAerodrome(String, String, boolean)}
     * @param destAerodromeId: the ID of the destination aerodrome converted by {@link FlightMovementBuilderUtility#checkAerodrome(String, String, boolean)}
     * @return billing centre associated to DOMESTIC flight movement
     */
    private BillingCenter getBillingCenterByDomestic(final FlightMovement flightMovement, final String depAerodromeId, final String destAerodromeId) {

        BillingCenter billingCenter;

        // resolve by dep or dest aerodrome depending on adap location configuration setting
        boolean onArrival = chargesUtility.getApplyAdapLocation().equalsIgnoreCase(ChargesUtility.ARRIVAL);
        billingCenter = this.getBillingCentreByAerodromeId(
            onArrival ? destAerodromeId : depAerodromeId, flightMovement);

        // see US 104798: BUG - CAAB - Where ZZZZ is used in the departure/destination endpoint used
        // to determine the billing centre, use the other endpoint
        if (billingCenter == null) billingCenter = this.getBillingCentreByAerodromeId(
            onArrival ? depAerodromeId : destAerodromeId, flightMovement);

        return billingCenter;
    }

    private String getRouteForRadar (String route, String dep, String dest, FlightMovement flightMovement) {
        final FlightMovementValidationViewModel fm = this.flightMovementValidator
                .validateFlightMovementCategory(flightMovement);
        if (fm == null || fm.getFlightmovementType() == null) {
            return null;
        }
        switch (fm.getFlightmovementType()) {
        case ARRIVAL:
            return getRouteWithAerodromes (route, null, dest);
        case DEPARTURE:
            return getRouteWithAerodromes (route, dep, null);
        case DOMESTIC:
        case OVERFLIGHT:
        case OTHER:
        default:
            return getRouteWithAerodromes (route, dep, dest);
        }
    }


    
    private String getRouteWithAerodromes(String route, String dep, String dest) {
        if (StringUtils.isBlank (route)) {
            if (StringUtils.isNotBlank (dep) && StringUtils.isNotBlank (dest)) {
                return dep + " DCT " + dest;
            }
            return null;
        }
        if (StringUtils.isBlank (dep) && StringUtils.isBlank(dest)) {
            return route;
        }
        if (StringUtils.isBlank (dep) && StringUtils.isNotBlank(dest)) {
            return route + " " + dest;
        }
        if (StringUtils.isNotBlank (dep) && StringUtils.isBlank(dest)) {
            return dep + " " + route;
        }
        return dep + " " + route + " " + dest;
    }
    
    public static String getFlightName (final FlightMovement x) {
        if (x != null) {
            final StringBuilder buf = new StringBuilder();
            if (x.getId() != null) {
                buf.append ("#").append (x.getId());
            }
            if (x.getDepAd() != null) {
                buf.append (" ").append (x.getDepAd());
            }
            if (x.getDestAd() != null) {
                buf.append ("->").append (x.getDestAd());
            }
            if (x.getDateOfFlight() != null) {
                buf.append (" ").append ((x.getDateOfFlight().toLocalDate()));
            }
            if (x.getDepTime() != null) {
                buf.append (" ").append (x.getDepTime());
            }
            return buf.toString();
        }
        return null;
    }

    public static void checkAndParseItem18Field (final FlightMovement flightMovement) {
        checkAndParseItem18Field (getFlightName (flightMovement), flightMovement);
    }
    
    public static void checkAndParseItem18Field (final String flightName, final FlightMovement flightMovement) {
        // check and parse the Item18Field
        if (StringUtils.isNotBlank(flightMovement.getOtherInfo())) {
            // item18_status
            String sts = Item18Parser.parse(flightMovement.getOtherInfo(), Item18Field.STS);
            if (StringUtils.isNotBlank(sts)) {
                LOG.info ("{}: setting item18Status=[{}]", flightName, sts);
                flightMovement.setItem18Status(sts);
            }
            // item18_dep
            String dep = Item18Parser.parse(flightMovement.getOtherInfo(), Item18Field.DEP);
            if (StringUtils.isNotBlank(dep)) {
                LOG.info ("{}: setting item18Dep=[{}]", flightName, dep);
                flightMovement.setItem18Dep(dep);
            }
            // item18_dest
            String dest = Item18Parser.parse(flightMovement.getOtherInfo(), Item18Field.DEST);
            if (StringUtils.isNotBlank(dest)) {
                LOG.info ("{}: setting item18Dest=[{}]", flightName, dest);
                flightMovement.setItem18Dest(dest);
            }
            // item18_reg_num
            String reg = Item18Parser.parse(flightMovement.getOtherInfo(), Item18Field.REG);
            if (StringUtils.isNotBlank(reg)) {
                LOG.info ("{}: setting item18Reg=[{}]", flightName, reg);
                flightMovement.setItem18RegNum(reg);
            }
            // item18_rmk
            String rmk = Item18Parser.parse(flightMovement.getOtherInfo(), Item18Field.RMK);
            if (StringUtils.isNotBlank(rmk)) {
                LOG.info ("{}: setting item18Rmk=[{}]", flightName, rmk);
                flightMovement.setItem18Rmk(rmk);
            }
            // item18_operator
            final String operator = Item18Parser.parse (flightMovement.getOtherInfo(), Item18Field.OPR);
            if (StringUtils.isNotBlank (operator)) {
                LOG.info ("{}: setting item18Operator=[{}]", flightName, operator);
                flightMovement.setItem18Operator (operator);
            }
            // item18_aircraft_type
            final String aircraftType = Item18Parser.parse (flightMovement.getOtherInfo(), Item18Field.TYP);
            if (StringUtils.isNotBlank (aircraftType)) {
                LOG.info ("{}: setting item18AircraftType=[{}]", flightName, aircraftType);
                flightMovement.setItem18AircraftType(aircraftType);
            }
        }
    }

    private void catchAerodromeErrors(FlightMovement flightMovement, FlightMovementBuilderException fmbe)
        throws FlightMovementBuilderException {
        switch (fmbe.getFlightMovementBuilderIssue()) {
            case ROUTE_PARSER_ERROR:
            case UNKNOWN_AERODROME:
            case UNKNOWN_AERODROME_ITEM18:
            case UNKNOWN_AERODROME_ROUTE: {
                LOG.debug(
                    "Cannot resolve the aerodromes, departure is {}, destination is {}; Other messages: {}",
                    flightMovement.getDepAd(), flightMovement.getDestAd(),
                    fmbe.getLocalizedMessage() != null ? fmbe.getLocalizedMessage() : "none");
                break;
            }
            default: {
                throw fmbe;
            }
        }
    }

    private void checkAerodromeForSurveillanceLog(FlightMovement aFlightMovement,
                                                  String aDepartureAerodrome,
                                                  String aDestinationAerodrome) {
        String dep = flightMovementBuilderUtility.checkAerodrome(aDepartureAerodrome, true);
        if (dep == null) {
            aFlightMovement.setDepAd(ApplicationConstants.PLACEHOLDER_ZZZZ);
            aFlightMovement.setItem18Dep(aDepartureAerodrome);
        }
        String dest = flightMovementBuilderUtility.checkAerodrome(aDestinationAerodrome, false);
        if (dest == null) {
            aFlightMovement.setDestAd(ApplicationConstants.PLACEHOLDER_ZZZZ);
            aFlightMovement.setItem18Dest(aDestinationAerodrome);
        }
    }

    private String setNoRouteForThru(FlightMovement aFlightMovement) {
        aFlightMovement.setFplCrossingDistance(null);
        aFlightMovement.setFplRouteGeom(null);
        return aFlightMovement.getDestAd();
    }

    private PassengerServiceChargeReturn findPassengerServiceChargeReturnByFM(FlightMovement flightMovement){
        if(flightMovement == null) {
        	LOG.debug("Flightmovement is null");
        	return null;
        }
        LOG.debug("Find PassengerServiceChargeReturn By FlightMovement: FlighId {}, dayOfFlight {}, depTime {} ",
        		flightMovement.getFlightId(), flightMovement.getDateOfFlight(), flightMovement.getDepTime());

        PassengerServiceChargeReturn passengerServiceChargeReturn=null;
        List<PassengerServiceChargeReturn> psList = null;
        if(flightMovement.getFlightId() !=null && !flightMovement.getFlightId().isEmpty() &&
        		flightMovement.getDateOfFlight()!=null){
        	if(flightMovement.getDepTime() != null && !flightMovement.getDepTime().isEmpty()) {
        		psList= passengerServiceChargeReturnRepository.findByFlightIdAndDayOfFlightAndDepartureTime(
        				flightMovement.getFlightId(), flightMovement.getDateOfFlight(), flightMovement.getDepTime());
        	}
        	if(psList == null || psList.isEmpty()) {
        		//try more wide search
        		psList= passengerServiceChargeReturnRepository.findByFlightIdAndDayOfFlight(flightMovement.getFlightId(), flightMovement.getDateOfFlight());
        	}
        	if(psList == null || psList.isEmpty() || psList.size() > 1) {
        		LOG.warn("Found none or multiple PassengerServiceChargeReturn By UniqueKey FlighId {}, dayOfFlight {}, depTime {} ",
        				flightMovement.getFlightId(), flightMovement.getDateOfFlight(), flightMovement.getDepTime());
        		return null;
        	}
        	passengerServiceChargeReturn = psList.get(0);
        }
        return passengerServiceChargeReturn;
    }

    private void recalculateSurveillanceLogRoutes(FlightMovement fm, String dep, String dest) {

        // 2020-07016 Radar routes are not recalculated if radar date is obtained from Leonardoanrdo file for radar
        if(!systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE) &&
                StringUtils.isNotBlank(fm.getRadarRouteText())) {
             RouteCacheVO  routeCacheVO = calculateRouteInformationByRouteParser(fm,
                        fm.getRadarRouteText(), SegmentType.RADAR, dep, dest);

             if (routeCacheVO != null) {
                 fm.setRadarCrossingDistance(routeCacheVO.getDistance());
                 fm.setRadarRoute(routeCacheVO.getRoute());
                 fm.setRouteSegments(flightMovementBuilderUtility.mergeTheSegmentsList(
                 fm.getRouteSegments(), routeCacheVO.getRouteSegmentList(), SegmentType.RADAR));
             }
        }
        
        if(StringUtils.isNotBlank(fm.getRadarTowerLogRouteText())) {
            RouteCacheVO  routeCacheVO = calculateRouteInformationByRouteParser(fm,
                fm.getRadarTowerLogRouteText(), SegmentType.TOWER, dep, dest);

            if (routeCacheVO != null) {
                fm.setTowerCrossingDistance(routeCacheVO.getDistance());
                fm.setTowerLogTrack(routeCacheVO.getRoute());
                fm.setRouteSegments(flightMovementBuilderUtility.mergeTheSegmentsList(
                        fm.getRouteSegments(), routeCacheVO.getRouteSegmentList(), SegmentType.TOWER));
            }
        }

        if(StringUtils.isNotBlank(fm.getAtcLogRouteText())) {
            RouteCacheVO  routeCacheVO = calculateRouteInformationByRouteParser(fm,
                fm.getAtcLogRouteText(), SegmentType.ATC, dep, dest);

            if (routeCacheVO != null) {
                fm.setAtcCrossingDistance(routeCacheVO.getDistance());
                fm.setAtcLogTrack(routeCacheVO.getRoute());
                fm.setRouteSegments(flightMovementBuilderUtility.mergeTheSegmentsList(
                        fm.getRouteSegments(), routeCacheVO.getRouteSegmentList(), SegmentType.ATC));
            }
        }
    }

    private void clearRouteInfo(FlightMovement fm) {
        if(fm != null) {
            // clear all fpl route related fields
            fm.setFplCrossingDistance(null);
            fm.setFplRouteGeom(null);
            fm.setFplCrossingCost(null);
            // clear all fpl route related fields
            // 2020-07-16 Don't clean radar route infor if radar data from Leonardo file
            if(!systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE)) {
                fm.setRadarCrossingDistance(null);
                fm.setRadarRoute(null);
                fm.setRadarCrossingCost(null);
            }

            if(fm.getRouteSegments() != null && !fm.getRouteSegments().isEmpty()) {
                Iterator<RouteSegment> iter = fm.getRouteSegments().iterator();
                while (iter.hasNext())
                {
                    RouteSegment rs = iter.next();
                    if(rs!= null && 
                           (!systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE) && rs.getSegmentType().equals(SegmentType.RADAR) ||
                            rs.getSegmentType().equals(SegmentType.SCHED))) {
                        iter.remove();
                    }
                }
            }
        }
    }
    
    /**
     * Round total flight distance
     */
    Double roundTotalDistance(Double length) {
        if (length == null) return null;

        int decimalPlaces = systemConfigurationService.getIntOrZero(SystemConfigurationItemName.FLIGHT_TOTAL_DECIMAL_PLACES);
        return Calculation.truncate(length, decimalPlaces);
    }
}
