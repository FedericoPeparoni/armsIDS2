package ca.ids.abms.modules.flightmovementsbuilder.utility;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ca.ids.abms.config.error.ErrorVariables;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementSource;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.mapper.RouteCacheSegmentMapper;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.vo.RouteSegmentVO;
import ca.ids.abms.modules.routesegments.RouteSegment;
import ca.ids.abms.modules.routesegments.RouteSegmentComparator;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.error.RejectedReasons;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.aircraft.AircraftType;
import ca.ids.abms.modules.airspaces.AirspaceService;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementAerodromeService;
import ca.ids.abms.modules.flightmovements.FlightMovementAircraftService;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryType;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.vo.RouteCacheVO;
import ca.ids.abms.modules.flightmovementsbuilder.vo.RPAirspaceVO;
import ca.ids.abms.modules.mtow.AverageMtowFactor;
import ca.ids.abms.modules.mtow.AverageMtowFactorService;
import ca.ids.abms.modules.radarsummary.RadarSummary;
import ca.ids.abms.modules.routesegments.SegmentType;
import ca.ids.abms.modules.unspecifiedaircraft.UnspecifiedAircraftType;
import ca.ids.abms.modules.utilities.flights.FlightUtility;
import ca.ids.abms.modules.waketurbulence.WakeTurbulenceCategory;
import ca.ids.abms.util.StringUtils;

@SuppressWarnings("squid:S2259")
@Component
public class FlightMovementBuilderUtility {

    private static final Logger LOG = LoggerFactory.getLogger(FlightMovementBuilderUtility.class);

    private static final String VALUE_NULL_NOT_ALLOWED = "value null not allowed";


    private final FlightMovementAerodromeService flightMovementAerodromeService;
    private final FlightMovementAircraftService  flightMovementAircraftService;
    private final AccountService accountService;
    private final AirspaceService airspaceService;
    private final AverageMtowFactorService averageMtowFactorService;
    private final RouteParserWrapper routeParserWrapper;
    private final RouteCacheSegmentMapper routeCacheSegmentMapper;
    private final FlightMovementRepository flightMovementRepository;
    private final SystemConfigurationService systemConfigurationService;

    @SuppressWarnings("squid:S00107")
    public FlightMovementBuilderUtility(final FlightMovementAerodromeService flightMovementAerodromeService,
                                        final FlightMovementAircraftService flightMovementAircraftService,
                                        final AccountService accountService,
                                        final AirspaceService airspaceService,
                                        final AverageMtowFactorService averageMtowFactorService,
                                        final RouteParserWrapper routeParserWrapper,
                                        final RouteCacheSegmentMapper routeCacheSegmentMapper,
                                        final FlightMovementRepository flightMovementRepository,
                                        final SystemConfigurationService systemConfigurationService) {
        this.flightMovementAerodromeService = flightMovementAerodromeService;
        this.flightMovementAircraftService=flightMovementAircraftService;
        this.accountService = accountService;
        this.airspaceService = airspaceService;
        this.averageMtowFactorService = averageMtowFactorService;
        this.routeParserWrapper = routeParserWrapper;
        this.routeCacheSegmentMapper = routeCacheSegmentMapper;
        this.flightMovementRepository = flightMovementRepository;
        this.systemConfigurationService = systemConfigurationService;
    }

    /**
     * This method return the following information:
     * <ul>
     *     <li>geographical path of the route in WKT format;</li>
     *     <li>distance in km;</li>
     *     <li>route Segments;</li>
     * </ul>
     *
     * We use this method for FPL Object from Spatia.
     */
    public RouteCacheVO getRouteInformationByRouteParser(
        final String departure, final String destination, final String route, final SegmentType segmentType,
        final String cruisingSpeedOrMachNumber, final String elapsedTime) throws FlightMovementBuilderException {
        LinkedList<RPAirspaceVO> airspaceList = airspaceService.getLinkedListAllAirspaceGeometry();
        return routeParserWrapper.getRouteInformationByRouteParser(departure, destination, route,
            cruisingSpeedOrMachNumber, elapsedTime, segmentType, airspaceList,null);
    }
    /**
     * This method return the following information:
     * <ul>
     *     <li>geographical path of the route in WKT format;</li>
     *     <li>distance in km;</li>
     *     <li>route Segments;</li>
     * </ul>
     *
     * We use this method for FPL Object from Spatia if we need to exclude TMAs.
     */
    public RouteCacheVO getRouteInformationByRouteParserExcludeTmas(
        final String departure, final String destination, final String route, final SegmentType segmentType,
        final String cruisingSpeedOrMachNumber, final String elapsedTime, Double flightLevel) throws FlightMovementBuilderException {
        
        if(flightLevel == null) {
            flightLevel = systemConfigurationService.getDouble(SystemConfigurationItemName.DEFAULT_FLIGHT_LEVEL, 0.0);
        }
        LinkedList<RPAirspaceVO> airspaceList = airspaceService.getLinkedListAllAirspaceGeometryExcludeTmas(flightLevel);
                
        return routeParserWrapper.getRouteInformationByRouteParser(departure, destination, route,
           cruisingSpeedOrMachNumber, elapsedTime, segmentType, airspaceList, flightLevel);
    }
    /**
     * This method return the following information:
     * <ul>
     *     <li>geographical path of the route in WKT format;</li>
     *     <li>distance in km;</li>
     *     <li>route Segments;</li>
     * </ul>
     *
     * It comes Not from a flight plan, But from one of the following three possible sources:
     * <ul>
     *     <li>Radar Summary;</li>
     *     <li>ATC Movement Log;</li>
     *     <li>Tower Aircraft / Passenger Movement Log;</li>
     * </ul>
     */
    public RouteCacheVO getRouteInformationByRouteParser(final String depAd, final String routeString,
            final String destAd ,final SegmentType segmentType) {

        LinkedList<String> airspaceLinkedListWKTs = new LinkedList<>(airspaceService.getAllAirspaceGeometry());
        LinkedList<RPAirspaceVO> airspace = airspaceService.getLinkedListAllAirspaceGeometry();

        return routeParserWrapper.getRouteInformationByRouteParser(depAd, routeString,destAd, segmentType,
            airspaceLinkedListWKTs, airspace);
    }
      
    public RouteCacheVO getRouteInformationByLeonardoFile(RadarSummary rs) {
        
        return routeParserWrapper.getRouteInformationByLeonardoFile(rs);
    }
    
    public RouteCacheVO getRouteInformationByRouteParserExcludeTmas(final String depAd, final String routeString,
            final String destAd ,final SegmentType segmentType, Double flightLevel) {

        LinkedList<String> airspaceLinkedListWKTs = null;
        LinkedList<RPAirspaceVO> airspace = null;
        
        if(flightLevel == null) {
            flightLevel = systemConfigurationService.getDouble(SystemConfigurationItemName.DEFAULT_FLIGHT_LEVEL, 0.0);
        }
        airspaceLinkedListWKTs = new LinkedList<>(airspaceService.getAllAirspaceGeometryExcludeTmas(flightLevel));
        airspace = airspaceService.getLinkedListAllAirspaceGeometryExcludeTmas(flightLevel);
        return routeParserWrapper.getRouteInformationByRouteParser(depAd, routeString,destAd, segmentType,
           airspaceLinkedListWKTs, airspace);
    }

    public Account getAccountByAccountId(Integer accountID) {
        Account account = null;
        if (accountID != null) {
            account = accountService.getOne(accountID);
        }
        return account;
    }

    public Account resolveAccountForFlightMovement(final FlightMovement flightMovement) {
        Account account;

        // 2019-09-27 Return existing account only if the flight was entered manually
        
        Account accountById = flightMovement.getAccount() != null ? getAccountByAccountId (flightMovement.getAccount().getId()) : null;

        if (flightMovement.getAccount() != null && accountById != null && flightMovement.getSource() != null && flightMovement.getSource().equals(FlightMovementSource.MANUAL)) {
            return accountById;
        }

        // Try the operator code stored within the flight movement, from item 18 OPR/... or from a
        // similar field in tower logs etc)
        if ((account = getAccountByOprNameOrICAOcode (flightMovement.getItem18Operator())) != null) {
            LOG.debug ("Resolved account to [{} - {}] based on item 18 operator code [{}]",
                    account.getId(), account.getName(), flightMovement.getItem18Operator());
            return account;
        }

        // Try the registration number
        if ((account = getAccountIdByFLMItem18RegNum (flightMovement.getItem18RegNum(), flightMovement.getDateOfFlight())) != null) {
            LOG.debug ("Resolved account to [{} - {}] based on item 18 registration number [{}] for dateOfFlight={}",
                    account.getId(), account.getName(), flightMovement.getItem18RegNum(), flightMovement.getDateOfFlight());
            return account;
        }

        // Try to match a prefix the flight id against account's ICAO codes
        if ((account = getAccountByFlightId (flightMovement.getFlightId())) != null) {
            LOG.debug ("Resolved account to [{} - {}] based on flight_id [{}]",
                    account.getId(), account.getName(), flightMovement.getFlightId());
            return account;
        }

        // Finally, return account by id
        return account;
    }

    private Account getAccountIdByFLMItem18RegNum(final String registrationNumber, final LocalDateTime dateOfFlight) {
        Account account = null;
        if (org.apache.commons.lang.StringUtils.isNotEmpty(registrationNumber)) {
            AircraftRegistration aircraftRegistration = flightMovementAircraftService.findAircraftRegistration(registrationNumber, dateOfFlight);
            if (aircraftRegistration != null) {
                account = aircraftRegistration.getAccount();
            } else {
                LOG.warn("Aircraft Registration '{}' for date '{}' does not exist or has expired",
                    registrationNumber, dateOfFlight);
            }
        }
        return account;
    }

    private Account getAccountByOprNameOrICAOcode (final String operatorNameOrICAOcode) {
        Account account = null;
        final String ident = StringUtils.stripToNull (operatorNameOrICAOcode);
        if (ident != null) {
            account = accountService.findAccountByOprIdentifier (ident);
            if (account == null) {
                account = accountService.findAccountByIcaoCode (ident);
            }
        }
        return account;
    }

    /**
     * Will resolve aerodrome based on {@code item18} value if {@code aerodrome} cannot be resolved.
     *
     * Note, this method will return coordinates from ABMSDB Aerodromes if it does NOT exist in NAVDB. If you require
     * that the aerodrome name is returned instead, see {@link #checkAerodrome(String, String, boolean)}.
     *
     * @param aerodrome aerodrome name
     * @param item18 aerodrome item 18
     * @return resolved aerodrome identifier or coordinate
     * @throws FlightMovementBuilderException resolve aerodrome issue
     */
    private String checkAerodrome(String aerodrome, String item18) throws FlightMovementBuilderException {
        return this.checkAerodrome(aerodrome, item18, true);
    }

    /**
     * Will resolve aerodrome based on {@code aerodrome} if it doesn't exist on nadvb or arms.
     *
     * @param aerodrome aerodrome name
     * @param checkAfil check AFIL for departure aerodrome
     * @return resolved aerodrome identifier
     */
    public String checkAerodrome(final String aerodrome, final boolean checkAfil) {
        return flightMovementAerodromeService.resolveAerodrome(aerodrome, checkAfil);
    }

    /**
     * Will resolve aerodrome based on {@code item18} value if {@code aerodrome} cannot be resolved.
     *
     * @param aerodrome aerodrome name
     * @param item18 aerodrome item 18
     * @param resolveToCoordinates resolve to coordinates if not in NAVDB
     * @return resolved aerodrome identifier or coordinate
     * @throws FlightMovementBuilderException resolve aerodrome issue
     */
    public String checkAerodrome(String aerodrome, String item18, boolean resolveToCoordinates) throws FlightMovementBuilderException {
        return flightMovementAerodromeService.resolveAerodrome(aerodrome,item18, resolveToCoordinates,false);
    }

    /**
     * Will resolve aerodrome based on {@code aerodromeDelta}.
     *
     * @param aerodromeDelta aerodrome name from delta
     * @param resolveToCoordinates resolve to coordinates if not in NAVDB
     * @return resolved aerodrome identifier or coordinate
     * @throws FlightMovementBuilderException resolve aerodrome issue
     */
    public String checkAerodromeFromDelta(String aerodromeDelta, boolean resolveToCoordinates) throws FlightMovementBuilderException {
        return flightMovementAerodromeService.resolveAerodromeFromDelta(aerodromeDelta, resolveToCoordinates);
    }

    private Aerodrome findAerodrome(final String icaoCode, final String item18Name) {
        try {
            final String name = checkAerodrome (icaoCode, item18Name);
            return this.flightMovementAerodromeService.getAeroDrome(name);
        }
        catch (final FlightMovementBuilderException x) {
            return null;
        }
    }

    public String checkAircraftType(FlightMovement flightMovement) {
        LOG.info("Check and Resolve AircraftType for Flight Movement FlightID {} ", flightMovement.getFlightId());
        return flightMovementAircraftService.checkAndResolveAircraftType(flightMovement);
    }

    public String checkItem18AircraftType(FlightMovement flightMovement) {
        LOG.info("Check and Resolve Item 18 AircraftType for Flight Movement FlightID {} ", flightMovement.getFlightId());
        return flightMovementAircraftService.checkAndResolveItem18AircraftType(flightMovement.getAircraftType(), flightMovement.getOtherInfo(), flightMovement.getItem18AircraftType());
    }

    /**
     * Check MTOW for flight
     *
     * @param flightMovement to derive mtow from
     * @return mtow
     */
    public Double checkMTOW(FlightMovement flightMovement) {
        Double mtow = null;

        // 1 - check if there is an aircraft associated with flight movement    
        mtow = checkMTOWFromRegistrationNumber(flightMovement);
        
        // 2020-09-14 TFS 116125
        // MTOW from registration number always has priority over aircraft type MTOW
        // code is commented out, but not removed in case any customer would complain

     /* Double mtowFromRegNum = checkMTOWFromRegistrationNumber(flightMovement);  
       if (mtowFromRegNum != null) {
           // check and resolve mtow by Registration number if aircraft type is manually changed
            if (FlightMovementBuilderMergeUtility.checkManuallyFieldChanged("aircraftType",flightMovement.getManuallyChangedFields())) {
                AircraftRegistration ar = flightMovementAircraftService.findAircraftRegistration(flightMovement.getItem18RegNum(), flightMovement.getDateOfFlight());
                boolean theSameAC = flightMovement.getAircraftType().equals(ar.getAircraftType().getAircraftType());
                // if FlightMovement and AircraftRegistration have the same Aircraft Type, we need to use MTOW from AircraftRegistration
                mtow = theSameAC ? mtowFromRegNum : null;
            } else {
                mtow = mtowFromRegNum;
            }
        } */

        if (mtow == null) {
            // 2 - check the flight movement’s aircraft type is checked against the known aircraft types database
            mtow = checkMTOWFromAircraftType(flightMovement);
        }

        if (mtow == null) {
            // 3 - check the flight movement’s item18 aircraft type
            mtow = checkMTOWFromItem18(flightMovement);
        }

        return mtow;
    }

    /**
     * Check MTOW from registration number
     *
     * @param flightMovement to derive mtow from
     * @return mtow
     */
    private Double checkMTOWFromRegistrationNumber(FlightMovement flightMovement) {
        String registrationNumber = flightMovement.getItem18RegNum();

        if (registrationNumber != null && !registrationNumber.isEmpty()) {
            AircraftRegistration aircraftRegistration = flightMovementAircraftService.findAircraftRegistration(
                registrationNumber, flightMovement.getDateOfFlight()
            );

            if (aircraftRegistration != null) {
                return aircraftRegistration.getMtowOverride();
            } else {
                LOG.warn(
                    "There isn't any aircraftRegistration: {} associated with the flight movement id: {} ",
                    registrationNumber,
                    flightMovement.getId()
                );
            }
        }

        return null;
    }

    /**
     * Check MTOW from aircraft type
     *
     * @param flightMovement to derive mtow from
     * @return mtow
     */
    private Double checkMTOWFromAircraftType(FlightMovement flightMovement) {
        String aircraftTypeName = flightMovement.getAircraftType();

        if (StringUtils.isNotBlank(aircraftTypeName)) {
            AircraftType aircraftType = flightMovementAircraftService.findAircaftType(aircraftTypeName);
            if (aircraftType != null) {
                return aircraftType.getMaximumTakeoffWeight();
            } else {
                // aircraft type is an unknown
                LOG.warn(
                    "AircraftType of {} is unknown for the flight movement id: {} ",
                    aircraftTypeName,
                    flightMovement.getId()
                );
            }
        } else {
            LOG.warn("AircraftType is null or empty for the flight movement id: {} ", flightMovement.getId());
        }

        return null;
    }

    /**
     * Aircraft type from item18
     * FROM ICAO: When ZZZZ is present in Item 9, then Item 18 shall contain the type(s) of aircraft preceded
     * if necessary without a space by number(s) of aircraft and separated by one space
     *
     * @param flightMovement to derive mtow from
     * @return mtow
     */
    private Double checkMTOWFromItem18(FlightMovement flightMovement) {
        String aircraftTypeName = flightMovement.getAircraftType();
        String aircraftTypeItem18;
        Double mtow = null;

        if (aircraftTypeName == null) {
            LOG.warn("AircraftType is null or empty for the flight movement id: {} ", flightMovement.getId());

            return null;
        }

        LOG.warn("Aircraft type is an unspecified aircraft type : {} ", aircraftTypeName);

        // check item18 Other Info
        if (StringUtils.isNotBlank(flightMovement.getOtherInfo())) {
            aircraftTypeItem18 = flightMovementAircraftService.findItem18AircraftType(
                flightMovement.getOtherInfo()
            );

            if (StringUtils.isNotBlank(aircraftTypeItem18)) {
                mtow = findMTOWFromItem18AircraftType(aircraftTypeItem18);
            }
        }

        // check item18 AircraftType field
        if (mtow == null && StringUtils.isNotBlank(flightMovement.getItem18AircraftType())) {
            aircraftTypeItem18 = flightMovementAircraftService.findItem18AircraftType(
                flightMovement.getItem18AircraftType()
            );

            if (StringUtils.isNotBlank(aircraftTypeItem18)) {
                mtow = findMTOWFromItem18AircraftType(aircraftTypeItem18);
            }
        }

        return mtow;
    }

    private Double findMTOWFromItem18AircraftType(String item18AircraftType) {
        AircraftType aircraftType = flightMovementAircraftService.findAircaftType(item18AircraftType);
        Double mtow = null;

        if (aircraftType != null) {
            mtow = aircraftType.getMaximumTakeoffWeight();
        }

        if (mtow == null) {
            // check unspecified aircraft table
            UnspecifiedAircraftType unspecified = flightMovementAircraftService.findUnspecifiedAircraftType(
                item18AircraftType
            );

            if (unspecified != null) {
                mtow = unspecified.getMTOW();
            }
        }

        return mtow;
    }

    public Double checkAverageMTOW(Double mtow) {
        Double averageMtow = null;

        if (mtow != null) {
            AverageMtowFactor averageMtowFactor = averageMtowFactorService.findAverageMtowFactorByUpperLimit(mtow);
            if (averageMtowFactor != null) {
                averageMtow = averageMtowFactor.getAverageMtowFactor();
            } else {
                LOG.warn("I can't find average mtow for this value: '{}'", mtow);
            }
        } else {
            LOG.warn("MTOW is null !!! ");
        }
        return averageMtow;
    }

    public String checkWakeTurbolance(final String aircraftTypeName) {
        String wakeTurbName = null;

        if (StringUtils.isNotBlank(aircraftTypeName)) {
            AircraftType aircraftType= flightMovementAircraftService.findAircaftType(aircraftTypeName);
            if (aircraftType != null) {
                WakeTurbulenceCategory wakeTurbulenceCategory = aircraftType.getWakeTurbulenceCategory();
                wakeTurbName = wakeTurbulenceCategory.getName();
            } else {
                LOG.warn("There isn't any aircraftType object for this aircraftTypeName: '{}'", aircraftTypeName);
            }
        } else {
            LOG.warn("AircraftType is null or empty !!!");
        }

        return wakeTurbName;
    }

    private static boolean isNotValidDepTime(final String depTime) {
        boolean isNotValid = true;
        if (depTime != null) {
            try {
                LocalTime.parse(depTime, DateTimeFormatter.ofPattern("HHmm"));
                isNotValid = false;
            } catch (DateTimeParseException dtpe) {
                // Do nothing
            }
        }
        return isNotValid;
    }

    /**
     * This  method check the constraints on FlightMovements
     *
     */
    public static Boolean checkNotNullConstraint(FlightMovement flightMovement) {

        Boolean returnValue = Boolean.TRUE;

        if (flightMovement != null && flightMovement.getFlightId() != null) {

            if (flightMovement.getDateOfFlight() == null) {
                LOG.debug("DateOfFlight is null for this FlightObject ID {} ", flightMovement.getFlightId());
                returnValue = Boolean.FALSE;
            }

            if (flightMovement.getDepAd() == null) {
                LOG.debug("DepAd is null for this FlightObject ID {} ", flightMovement.getFlightId());
                returnValue = Boolean.FALSE;
            }

            if (isNotValidDepTime(flightMovement.getDepTime())) {
                LOG.debug("DepTime {} not valid for this FlightObject ID {} ", flightMovement.getDepTime(),
                    flightMovement.getFlightId());
                returnValue = Boolean.FALSE;
            }


            if (flightMovement.getDestAd() == null) {
                LOG.debug("DestAd is null for this FlightObject ID {} ", flightMovement.getFlightId());
                returnValue = Boolean.FALSE;
            }

            // MovementType and Status are calculate from the system

        } else {
            LOG.debug("FlightID is null");
            returnValue = Boolean.FALSE;
        }
        return returnValue;
    }

    /**
     * This  method returns details about the fields that don't satisfy the constraints on FlightMovements.
     * To use only if the above {checkNotNullConstraint} method returns FALSE.
     */
    public static ErrorDTO getMandatoryFieldNotValued (final FlightMovement flightMovement, final String initialMessage, final boolean throwException) {
        ErrorDTO errorDTO;
        if (flightMovement != null) {
            ErrorVariables detailVariables = new ErrorVariables();

            detailVariables.addEntry("flightId", flightMovement.getFlightId());
            detailVariables.addEntry("dateOfFlight", String.valueOf(flightMovement.getDateOfFlight()));
            detailVariables.addEntry("depTime", flightMovement.getDepTime());
            detailVariables.addEntry("depAd", flightMovement.getDepTime());

            final ErrorDTO.Builder errorBuilder = new ErrorDTO.Builder(initialMessage)
                .appendDetails("One or more fields are wrong for a flight movement with" +
                    "flightId = {{flightId}}" +
                    "dateOfFlight = {{dateOfFlight}}" +
                    "depTime = {{depTime}}" +
                    "depAd = {{depAd}}");

            if (flightMovement.getDateOfFlight() == null) {
                errorBuilder.setErrorMessage(", the date of flight is missing")
                    .addInvalidField(flightMovement.getClass(), "dateOfFlight", VALUE_NULL_NOT_ALLOWED)
                    .appendDetails("The date of flight is not specified. ");
            }
            if (flightMovement.getDepAd() == null) {
                errorBuilder.setErrorMessage(", the departure aerodrome is missing")
                    .addInvalidField(flightMovement.getClass(), "depAd", VALUE_NULL_NOT_ALLOWED)
                    .appendDetails("The departure aerodrome is not specified. ");
            }
            if (isNotValidDepTime(flightMovement.getDepTime())) {
                errorBuilder.setErrorMessage(", the departure time isn't valid")
                    .addInvalidField(flightMovement.getClass(), "depTime", "value "
                        + flightMovement.getDepTime() + " not allowed")
                    .appendDetails("The departure time is not specified. ");
            }
            if (flightMovement.getDestAd() == null) {
                errorBuilder.setErrorMessage(", the destination aerodrome is missing")
                    .addInvalidField(flightMovement.getClass(), "destAd", VALUE_NULL_NOT_ALLOWED)
                    .appendDetails("The destination aerodrome is not specified. ");
            }
            errorDTO = errorBuilder.addRejectedReason(RejectedReasons.VALIDATION_ERROR).build();
        } else {
            errorDTO = new ErrorDTO.Builder("FlightMovement is null").addRejectedReason(RejectedReasons.SYSTEM_ERROR).build();
        }
        if (throwException) {
            throw new CustomParametrizedException(errorDTO);
        }
        return errorDTO;
    }

    /**
     * This  method check the constraints on FlightMovements
     */
    public static Boolean checkAndFillNotNullConstraint(FlightMovement flightMovement, FlightMovement existFlightMovement) {

        if (flightMovement != null && existFlightMovement != null) {

            if (flightMovement.getDateOfFlight() == null && existFlightMovement.getDateOfFlight()!=null) {
                flightMovement.setDateOfFlight(existFlightMovement.getDateOfFlight());
            }

            if (flightMovement.getDepAd() == null && existFlightMovement.getDepAd()!=null) {
                flightMovement.setDepAd(existFlightMovement.getDepAd());
            }

            if (flightMovement.getDepTime() == null && existFlightMovement.getDepTime()!=null) {
                flightMovement.setDepTime(existFlightMovement.getDepTime());
            }


            if (flightMovement.getDestAd() == null && existFlightMovement.getDestAd()!=null) {
                flightMovement.setDestAd(existFlightMovement.getDestAd());
            }

            if (flightMovement.getFlightId() == null && existFlightMovement.getFlightId()!=null) {
                flightMovement.setFlightId(existFlightMovement.getFlightId());
            }

            // MovementType and Status are calculate from the system
        }

        return checkNotNullConstraint(flightMovement);
    }

    public Account getAccountByFlightId(final String flightId) {

        Account account = null;
        boolean nonStandardFlightId = systemConfigurationService.getBoolean(SystemConfigurationItemName.ALLOW_NON_STANDARD_FLIGHT_ID);
        String icaoCode = FlightUtility.getICAOCodePrefixBygetFlightId(flightId, nonStandardFlightId);
        if (StringUtils.isNotBlank(icaoCode) && accountService != null) {
            account = accountService.findAccountByIcaoCode(icaoCode);
            if (account == null) {
                LOG.warn("ICAO Code '{}' does not match any accounts", icaoCode);
            }
        }
        return account;
    }

    /**
     * Return true if the given aerodrome belongs to the given billing center, for inclusion in an aviation invoice
     */
    @Deprecated
    public boolean flightBelongsToBillingCenter (final FlightMovement flightMovement, final BillingCenter billingCenter) {
        final FlightmovementCategoryType flightMovementType = flightMovement.getFlightCategoryType() == null ? FlightmovementCategoryType.OTHER : flightMovement.getFlightCategoryType();
    	final Set <Aerodrome> billingCenterAerodromeSet = billingCenter.getAerodromes();

        switch (flightMovementType) {

            // Domestic: match only arrival airport
            case DOMESTIC:
                {
                    final Aerodrome arrivalAerodrome = findAerodrome(flightMovement.getDestAd(), flightMovement.getItem18Dest());
                    if (arrivalAerodrome != null && (billingCenter.getHq() || billingCenterAerodromeSet.contains (arrivalAerodrome))) {
                        LOG.debug ("> PASS - FLM {} id:{} flightId:{} regNum:{} aircraftType:{} - aerodrome id:{} name:{} belongs to billing center id:{} name:{}",
                            flightMovementType, flightMovement.getId(), flightMovement.getFlightId(), flightMovement.getItem18RegNum(), flightMovement.getAircraftType(),
                            arrivalAerodrome.getId(), arrivalAerodrome.getAerodromeName(),
                            billingCenter.getId(), billingCenter.getName());
                        return true;
                    }
                    LOG.debug ("> FAIL - FLM {} id:{} flightId:{} regNum:{} aircraftType:{} - aerodrome {}/{} does not belong to billing center id:{} name:{}",
                        flightMovementType, flightMovement.getId(), flightMovement.getFlightId(), flightMovement.getItem18RegNum(), flightMovement.getAircraftType(),
                        flightMovement.getDestAd(), flightMovement.getItem18Dest(),
                        billingCenter.getId(), billingCenter.getName());
                    return false;
                }

            // International overflight: match only if billing center is HQ
            case OVERFLIGHT:
                if (billingCenter.getHq()) {
                    LOG.debug ("> PASS - FLM {} id:{} flightId:{} regNum:{} aircraftType:{} - overflights are managed by HQ billing center id:{} name:{}",
                        flightMovementType, flightMovement.getId(), flightMovement.getFlightId(), flightMovement.getItem18RegNum(), flightMovement.getAircraftType(),
                        billingCenter.getId(), billingCenter.getName());
                    return true;
                }
                LOG.debug ("> FAIL - FLM {} id:{} flightId:{} regNum:{} aircraftType:{} - overflights are not managed non-HQ billing center id:{} name:{}",
                    flightMovementType, flightMovement.getId(), flightMovement.getFlightId(), flightMovement.getItem18RegNum(), flightMovement.getAircraftType(),
                    billingCenter.getId(), billingCenter.getName());
                return false;

            case ARRIVAL:
            case DEPARTURE:
            // International arrival/departure: match if either departure or arrival aerodromes belong to this billing center
                {
                    final Aerodrome departureAerodrome = findAerodrome(flightMovement.getDepAd(), flightMovement.getItem18Dep());
                    if (departureAerodrome != null && (billingCenter.getHq() || billingCenterAerodromeSet.contains (departureAerodrome))) {
                        LOG.debug ("> PASS - FLM {} id:{} flightId:{} regNum:{} aircraftType:{} - departure aerodrome id:{} name:{} belongs to billing center id:{} name:{}",
                            flightMovementType, flightMovement.getId(), flightMovement.getFlightId(), flightMovement.getItem18RegNum(), flightMovement.getAircraftType(),
                            departureAerodrome.getId(), departureAerodrome.getAerodromeName(), billingCenter.getId(), billingCenter.getName());
                        return true;
                    }

                    final Aerodrome arrivalAerodrome = findAerodrome(flightMovement.getDestAd(), flightMovement.getItem18Dest());
                    if (arrivalAerodrome != null && (billingCenter.getHq() || billingCenterAerodromeSet.contains (arrivalAerodrome))) {
                        LOG.debug ("> PASS - FLM {} id:{} flightId:{} regNum:{} aircraftType:{} - arrival aerodrome id:{} name:{} belongs to billing center id:{} name:{}",
                            flightMovementType, flightMovement.getId(), flightMovement.getFlightId(), flightMovement.getItem18RegNum(), flightMovement.getAircraftType(),
                            arrivalAerodrome.getId(), arrivalAerodrome.getAerodromeName(), billingCenter.getId(), billingCenter.getName());
                        return true;
                    }
                    LOG.debug ("> FAIL - FLM {} id:{} flightId:{} regNum:{} aircraftType:{} - neither departure nor destination aerodromes belong to billing center id:{} name:{}",
                        flightMovementType, flightMovement.getId(), flightMovement.getFlightId(), flightMovement.getItem18RegNum(), flightMovement.getAircraftType(),
                        billingCenter.getId(), billingCenter.getName());
                    return false;
                }

            // OTHER flights are not related to our country at all, skip
            case OTHER:
                LOG.debug ("> FAIL - FLM {} id:{} flightId:{} regNum:{} aircraftType:{} - flights of type OTHER are not billable",
                    flightMovementType, flightMovement.getId(), flightMovement.getFlightId(), flightMovement.getItem18RegNum(),
                    flightMovement.getAircraftType());
                return false;
        }
        return true;
    }

    public boolean checkAircraftRegistration(String registrationNumber, LocalDateTime date) {
        boolean ret = false;
        AircraftRegistration ar = flightMovementAircraftService.findAircraftRegistration(registrationNumber, date);
        if (ar != null) {
            ret = true;
        }
        return ret;
    }
    
    public boolean checkAircraftRegistrationNumberPrefix(String registrationNumber) {
        return flightMovementAircraftService.startsWithAircraftRegistrationPrefix(registrationNumber);
    }

    public String checkAircraftRegistrationNumber(FlightMovement flightMovement) {
        String item18RegNum = flightMovement.getItem18RegNum();
        if (item18RegNum == null) {
            String flightId = flightMovement.getFlightId();
            if (checkAircraftRegistration(flightId, flightMovement.getDateOfFlight()) ||
                    checkAircraftRegistrationNumberPrefix(flightId)) {
                item18RegNum = flightId;
            }
        }
        return item18RegNum;
    }
    
    public List<RouteSegment> mergeTheSegmentsList (final List<RouteSegment> existingSegmentsList,
                                                     final List<RouteSegmentVO> newSegments, final SegmentType type) {

        List<RouteSegment> segments = existingSegmentsList;
        if (segments == null) {
            segments = new ArrayList<>();
        }
        segments.removeIf(segment -> segment.getSegmentType().equals(type));
        if (newSegments != null) {
            for (final RouteSegmentVO routeSegmentVO : newSegments) {
                final RouteSegment segment = routeCacheSegmentMapper.toRouteSegment(routeSegmentVO);
                segment.setSegmentType(type);
                segments.add(segment);
            }
        }
        return segments;
    }

    @SuppressWarnings("squid:S3776")
    public List<RouteSegment> addTheSegmentsList (final List<RouteSegment> existingSegmentsList,
            final List<RouteSegmentVO> newSegments, final SegmentType type) {

        List<RouteSegment> segments = existingSegmentsList;
        if (segments == null) {
            segments = new ArrayList<>();
        }
        final RouteSegment lastSegment = segments.stream()
                // filter by segment type
                .filter (s->s.getSegmentType() != null && s.getSegmentType().equals(type))
                // git largest segment number
                .max (new RouteSegmentComparator())
                // or null if not found
                .orElse (null);
        int segmentNum = (lastSegment != null) ? lastSegment.getSegmentNumber() : 0;
        if (newSegments != null) {
            for (final RouteSegmentVO routeSegmentVO : newSegments) {
                final RouteSegment segment = routeCacheSegmentMapper.toRouteSegment(routeSegmentVO);
                segment.setSegmentType(type);
                
                boolean segmentExist = false;
                if(!segments.isEmpty()) {
                    for(RouteSegment rs:segments) {
                        if(segment.getLocation() != null && rs.getLocation() != null && segment.getLocation().equals(rs.getLocation())) {
                            segmentExist = true;
                        }
                    }
                }
                if(!segmentExist) {
                    segment.setSegmentNumber(segmentNum+1);
                    segments.add(segment);
                }
            }
        }
        return segments;
    }
    
    /**
     * Fill out a missing EET using older similar flights in the database.
     *
     * We look for flights with the same call sign, registration, AC type and airports
     * within 7 days before the baseDateTime.
     *
     * @param fm - the flight movement whose EET should be updated
     * @param baseDateTime - look for similar DB flights around this date; defaults to current
     *
     */
    public void guessEstimatedElapsedTime (final FlightMovement fm, final LocalDateTime baseDateTime) {
        this.guessEstimatedElapsedTime (fm, baseDateTime == null ? null : baseDateTime.toLocalDate());
    }

    /**
     * Fill out a missing EET using older similar flights in the database.
     *
     * We look for flights with the same call sign, registration, AC type and airports
     * within 7 days before the baseDate.
     *
     * @param fm - the flight movement whose EET should be updated
     * @param baseDate - look for similar DB flights around this date; defaults to current
     *
     */
    private void guessEstimatedElapsedTime (final FlightMovement fm, final LocalDate baseDate) {
        if (StringUtils.isBlank(fm.getEstimatedElapsedTime()) &&
                !StringUtils.isBlank (fm.getDepAd()) && !StringUtils.isBlank (fm.getDestAd())) {
            final String aircraftType = ObjectUtils.firstNonNull(
                    StringUtils.stripToNull (fm.getAircraftType()),
                    StringUtils.stripToNull (fm.getItem18AircraftType()));
            final LocalDate dofBase = (baseDate == null ? LocalDate.now() : baseDate);
            final LocalDate fromDateInclusive = dofBase.minusDays (7);
            final LocalDate toDateExclusive = dofBase.plusDays (1);
            final String eet = this.flightMovementRepository.findHistoricalEstimatedElapsedTime (
                    fm.getDepAd(), fm.getDestAd(), fm.getFlightId(), fm.getItem18RegNum(),
                    aircraftType, fromDateInclusive, toDateExclusive);
            LOG.debug ("Found historical EET={} using these parameters: depAd={} destAd={} flightId={} regNum={} acType={} fromDateInclusive={}, toDateExclusive={}",
                    eet, fm.getDepAd(), fm.getDestAd(), fm.getFlightId(), fm.getItem18RegNum(), aircraftType, fromDateInclusive, toDateExclusive);
            fm.setEstimatedElapsedTime (eet);
        }
    }

    public String guessEstimatedElapsedTime (final String departureAd,
                                             final String destinationAd,
                                             final String flightId,
                                             final String item18RegNum,
                                             final String aircraftType,
                                             final LocalDate baseDate) {
        if (StringUtils.isBlank(departureAd) && StringUtils.isBlank(destinationAd)) {
            LOG.debug ("Cannot find historical EET because departure or destination aerodrome is null");
            return null;
        }

        final LocalDate dofBase = (baseDate == null ? LocalDate.now() : baseDate);
        final LocalDate fromDateInclusive = dofBase.minusDays (7);
        final LocalDate toDateExclusive = dofBase.plusDays (1);
        final String eet = this.flightMovementRepository.findHistoricalEstimatedElapsedTime (departureAd, destinationAd,
            flightId, item18RegNum, aircraftType, fromDateInclusive, toDateExclusive);

        LOG.debug ("Found historical EET = {} using these parameters: depAd = {} destAd = {} flightId = {} regNum = {} " +
                "acType = {} fromDateInclusive = {}, toDateExclusive = {}",
                eet, departureAd, destinationAd, flightId, item18RegNum, aircraftType, fromDateInclusive, toDateExclusive);

        return eet;
    }
    
}
