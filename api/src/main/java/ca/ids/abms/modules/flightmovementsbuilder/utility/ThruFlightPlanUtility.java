package ca.ids.abms.modules.flightmovementsbuilder.utility;

import ca.ids.abms.modules.airspaces.AirspaceService;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementAerodromeService;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.vo.RouteCacheVO;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.vo.RouteSegmentVO;
import ca.ids.abms.modules.flightmovementsbuilder.vo.RPAirspaceVO;
import ca.ids.abms.modules.flightmovementsbuilder.vo.ThruFlightPlanVO;
import ca.ids.abms.modules.routesegments.SegmentType;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocation;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocationService;
import ca.ids.abms.modules.util.models.ApplicationConstants;
import ca.ids.abms.modules.util.models.DateTimeUtils;
import ca.ids.abms.util.GeometryUtils;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.operation.linemerge.LineMerger;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ThruFlightPlanUtility {

    private static final Logger LOG = LoggerFactory.getLogger(ThruFlightPlanUtility.class);

    /**
     * THRUPLAN Item15 identifier pattern matcher.
     */
    private static final Pattern THRUPLAN_PATTERN = Pattern.compile("(?:THRU|TRU|PLAN|PLN|(?:THRU|TRU)(?:PLAN|PLN))");

    private final AirspaceService airspaceService;
    private final FlightMovementAerodromeService flightMovementAerodromeService;
    private final RouteParserWrapper routeParserWrapper;
    private final UnspecifiedDepartureDestinationLocationService unspecifiedDepartureDestinationLocationService;
    private final SystemConfigurationService systemConfigurationService;

    public ThruFlightPlanUtility(
        final AirspaceService airspaceService,
        final FlightMovementAerodromeService flightMovementAerodromeService,
        final RouteParserWrapper routeParserWrapper,
        final UnspecifiedDepartureDestinationLocationService unspecifiedDepartureDestinationLocationService,
        final SystemConfigurationService systemConfigurationService
    ) {
        this.airspaceService = airspaceService;
        this.flightMovementAerodromeService = flightMovementAerodromeService;
        this.routeParserWrapper = routeParserWrapper;
        this.unspecifiedDepartureDestinationLocationService = unspecifiedDepartureDestinationLocationService;
        this.systemConfigurationService = systemConfigurationService;
    }

    /**
     * ICAO standards have the following thru plan rules:
     *   1) Flight from maintained aerodrome thru an unmaintained aerodrome back to a maintained aerodrome.
     *   2) Flight length must not exceed 24 hours.
     *   3) Flight stop time must not exceed 60 minutes.
     *   4) Flight must not exit the boundary of the FIR.
     *
     * UNUSED: These rules are observed here but will not be used as per the technical specifications, see US 91751.
     */
    @SuppressWarnings({"squid:S3776", "unused"})
    public boolean checkRules(List<ThruFlightPlanVO> aThruLst, FlightMovement aFlightMovement) throws FlightMovementBuilderException {
        boolean isThru = true;
        final LocalDateTime depDateTime = DateTimeUtils.addTimeToDate(aFlightMovement.getDateOfFlight(),
                aFlightMovement.getDepTime());
        final ThruFlightPlanVO last = aThruLst.get(aThruLst.size() - 1);
        final LocalDateTime arrivalDateTime = last.getArrivalTime();
        if (arrivalDateTime != null && depDateTime != null) {
            long hours = ChronoUnit.HOURS.between(depDateTime, arrivalDateTime);
            if (hours < 24) {
                int i = 1;
                while (i < aThruLst.size() && isThru) {
                    final String ad = aThruLst.get(i).getDepAd();
                    if (!isMaintainedAd(ad)) {
                        LocalDateTime prevArrival = aThruLst.get(i - 1).getArrivalTime();
                        LocalDateTime currentDeparture = aThruLst.get(i).getDepTime();
                        long stopMinutes = ChronoUnit.MINUTES.between(prevArrival, currentDeparture);
                        if (stopMinutes <= 60) {
                            String destAd = aThruLst.get(i).getDestAd();
                            if (!flightMovementAerodromeService.isAerodromeDomestic(destAd)) {
                                isThru = false;
                                LOG.debug("Departure, destination and unmanned stops must all be within the FIR - {}", destAd);
                            } else if (flightMovementAerodromeService.resolveAnyLocationToDMS(destAd) == null) {
                                isThru = false;
                                LOG.debug("Aerodrome without coordinates {}", destAd);
                                flightMovementAerodromeService.checkAddAerodromeForUnspecified(destAd);
                            }
                        } else {
                            isThru = false;
                            LOG.debug(
                                "The plane should not stay on the ground more than 60 minutes at any unmanned aerodrome {}-{}",
                                aThruLst.get(i - 1).getDestAd(), aThruLst.get(i).getDepAd());
                        }
                    } else {
                        isThru = false;
                        LOG.debug(
                            "THRU plans must be from a manned aerodrome through unmanned aerodromes/airfields, ending at a manned aerodrome");
                    }
                    i++;
                }
            } else {
                LOG.debug("THRU plans extend beyond 24 hours from {} to {}", depDateTime, arrivalDateTime);
            }
        } else {
            isThru = false;
        }
        return isThru;
    }

    @SuppressWarnings("squid:S3776")
    public RouteCacheVO getThruRouteSegmentList(
        FlightMovement aFlightMovement, List<ThruFlightPlanVO> aThruLst, String aDepAd
    ) {
        final RouteCacheVO routeCacheVO = new RouteCacheVO();
        if (aFlightMovement != null) {

            String destAd = aThruLst.get(aThruLst.size()-1).getDestAd();
            if (destAd == null) {
                destAd = aFlightMovement.getDestAd();
            }

            LinkedList<RouteSegmentVO> routeSegments = null;

            LinkedList<RPAirspaceVO> rpAirspaceVOList = new LinkedList<>(airspaceService.getLinkedListAllAirspaceGeometry());

            try {
                String depDMS = aDepAd;
                Double crossingDist = 0.0;
                LineMerger merger = new LineMerger();
                if (!aThruLst.isEmpty()) {
                    routeSegments = new LinkedList<>();

                    StringJoiner routeBetweenStops = new StringJoiner(" DCT ", "DCT ", " DCT").setEmptyValue("DCT");
                    for (ThruFlightPlanVO stop : aThruLst) {
                        String destDMS = stop.getDestAd();
                        
                        if (destDMS != null) {
                            RouteCacheVO segmentRouteCacheVO = routeParserWrapper.getRouteInformationByRouteParser(depDMS,
                                destDMS, routeBetweenStops.toString(), aFlightMovement.getCruisingSpeedOrMachNumber(),
                                stop.getEet(), SegmentType.SCHED, rpAirspaceVOList, null);

                            if (segmentRouteCacheVO != null) {

                                // use aerodrome names instead of coords for thru flight map labels
                                if(segmentRouteCacheVO.getRouteSegmentList() != null
                                        && !segmentRouteCacheVO.getRouteSegmentList().isEmpty()) {
                                    for (RouteSegmentVO rs : segmentRouteCacheVO.getRouteSegmentList()) {
                                        rs.setSegmentStartLabel(stop.getDepAd());
                                        rs.setSegmentEndLabel(stop.getDestAd());
                                    }

                                    routeSegments.addAll(segmentRouteCacheVO.getRouteSegmentList());
                                }
                                
                                if (segmentRouteCacheVO.getDistance() != null) {
                                    crossingDist = crossingDist + segmentRouteCacheVO.getDistance();
                                }
                                if(segmentRouteCacheVO.getRoute() != null) {
                                   merger.add(segmentRouteCacheVO.getRoute());
                                }

                                // update dep DMS to dest DMS and reset route between stops as all stops added to this point
                                // US 114729: thru plans can fall outside FIR but still be a Kenya Aerodrome prefix
                                // and thus non-billable domestic segment
                                routeBetweenStops = new StringJoiner(" DCT ", "DCT ", " DCT").setEmptyValue("DCT");
                                depDMS = destDMS;
                            } else {
                                // add stop to route if not included in billable segments
                                routeBetweenStops.add(stop.getDestAd());
                            }
                        }
                    }

                    // Segments for the THRU flight stops are returned one by one for each stop.
                    // We have to set the correct numbers in the order of the stops for future usage
                    int segmentNumber = 1;
                    for (RouteSegmentVO rs : routeSegments) {
                        if (rs != null) {
                            rs.setSegmentNumber(segmentNumber);
                            segmentNumber = segmentNumber + 1;
                        }
                    }
                }

                @SuppressWarnings("unchecked")
                
                // If the route has loops LineMerger will return multiple LineStrings which have to be converted to a MULTISTRING
                
                Collection<LineString>  geometries = merger.getMergedLineStrings();
                Geometry geom = GeometryUtils.lineStringToMultiLineString(geometries);
                
                routeCacheVO.setDistance(routeParserWrapper.roundTotalDistance(crossingDist));
                routeCacheVO.setRoute(geom);
                routeCacheVO.setRouteSegmentList(routeSegments);
                routeCacheVO.setDestAd(destAd);
                routeCacheVO.setValid(true);
            } catch (FlightMovementBuilderException e) {
                LOG.error(e.getLocalizedMessage());
            }
        }
        return routeCacheVO;
    }

    public Boolean isThruFlight(FlightMovement flight) {
        boolean ret = false;

        Boolean isDepCoord = flightMovementAerodromeService.isAerodromeDomestic(flight.getDepAd(), flight.getDepAd(), true);
        LOG.debug("The flight {} has a {} domestic departure aerodrome {}", flight.getFlightId(), isDepCoord, flight.getDepAd());

        Boolean isDestCoord = flightMovementAerodromeService.isAerodromeDomestic(flight.getDestAd());
        LOG.debug("The flight {} has a {} domestic destination aerodrome {}", flight.getFlightId(), isDestCoord, flight.getDestAd());

        if (isDepCoord && isDestCoord) {
            String route = flight.getFplRoute();
            if (StringUtils.isNotEmpty(route)) {
                String parsed = Item15Parser.parse(route, Item15Field.THRU);
                 if (StringUtils.isNotEmpty(parsed)) {
                     LOG.debug("Fight {} could be THRU flight plans", flight.getFlightId());
                    ret = true;
                 }
            }
        }
        return ret;
    }

    private static Pattern compileRouteTextGarbageRegex() {
        return Pattern.compile (
            Stream.of(
                "D/S",
                "D/STOP",
                "DAYSTOP",
                "DAY STOP",
                "N/S",
                "N/STOP",
                "NIGHTSTOP",
                "NIGHT STOP",
                "RN/STOP",
                ";",
                "\"",
                "[*]"
           ).map (s->String.format ("(?:%s)", s)).collect(Collectors.joining("|"))
        );
    }
    private static final Pattern RE_ROUTE_TEXT_GARBAGE = compileRouteTextGarbageRegex();
    private static final Pattern RE_WS = Pattern.compile ("\\s+");
    private static String cleanRouteText (final String routeText) {
        // First replace "garbage" sequences with a single space each
        final String mostlyClean = RE_ROUTE_TEXT_GARBAGE.matcher(routeText).replaceAll(" ");
        // Next replace long space sequences with one space each
        return RE_WS.matcher (mostlyClean).replaceAll(" ");
    }

    /**
     * Parse thru plan from flight route following a predefined pattern.
     *
     * Using Item15Field definitions, the pattern is `THRUPLAN EET AD [/] [CS] [FL] EET AD`
     * where `[]` is optional.
     */
    @SuppressWarnings("squid:S3776")
    public List<ThruFlightPlanVO> parseThruPlanRoute(final String route, final String departureAd, final LocalDateTime departureDateTime) {

        List<ThruFlightPlanVO> stops = new ArrayList<>();

        // verify that required parameters are valid
        if (route == null || route.isEmpty())
            return stops;

        // remove meaningless garbage from route text
        final String cleanRoute = cleanRouteText (route);

        // determine estimated stop time between flight legs from system configurations
        int stopTime = systemConfigurationService.getIntOrZero(SystemConfigurationItemName.THRU_PLAN_ESTIMATED_STOP_TIME);

        // set initial departure aerodrome and date time from provided flight plan details
        // used to populate ThruFlightPlanVO, value will be changed as route is parsed
        String depAd = departureAd;
        LocalDateTime depTime = departureDateTime;

        // initial thru plan object, added to stops and reinitialized once eet and destAd are populated
        ThruFlightPlanVO vo = new ThruFlightPlanVO(depAd, depTime);

        // split route into separate parts using a single space OR slash as the delimiter characters
        StringTokenizer st = new StringTokenizer(cleanRoute, " ");
        
        Integer eetSum = 0;
        while (st.hasMoreTokens()) {
            String routePart = st.nextToken();
            String optionalPart = null;
            boolean optionalItem15FieldValue = routePart.contains("/");

            if (optionalItem15FieldValue) {
                StringTokenizer stWithOptional = new StringTokenizer(routePart, "/");
                routePart = parseOptionalItem15FieldValue(stWithOptional, routePart);
                optionalPart = parseOptionalItem15FieldValue(stWithOptional, routePart);
            }

            // continue if route part matches THRUPLAN Item15Field text identifier
            if (isThruPlan(routePart))
                continue;

            // attempt to resolve estimated elapsed time and arrival time values from route part
            // update depTime for next segment if successful
            String eet = Item15Parser.parse(routePart, Item15Field.EET);
            
            
            if (StringUtils.isNotEmpty(eet)) {

                int eetFinal = parseTime(eet) + eetSum;
                vo.setEet(minToHHMMFormat(eetFinal));
                                
                try {
                    // determine arrival time based on departure time plus estimated elapsed time
                    LocalDateTime arrivalTime = vo.getDepTime().plusMinutes(eetFinal);
                    vo.setArrivalTime(arrivalTime);

                    // use arrival time plus estimated stop time as next flight leg departure time
                    depTime = arrivalTime.plusMinutes(stopTime);

                // if cannot parse the time, return 0 stops. The FM will be created/updated with "ZERO_LENGTH_BILLABLE_TRACK"
                } catch (DateTimeParseException dtpe){
                    LOG.debug("The route format is not valid - cannot parse the time extracted from the route: {}", eet);
                    return new ArrayList<>();
                }

            }           

            // attempt to resolve destination aerodrome value from route part
            // update depAd for next segment if successful
            String destAd = Item15Parser.parse(routePart, Item15Field.AERODROME);
           
            if (StringUtils.isNotEmpty(destAd)) {
                vo.setDestAd(destAd);
                depAd = destAd;
            }

            // if the stop aerodrome is ZZZZ, eet time for the next stop should include eet for the previous one
            if(StringUtils.isNotEmpty(destAd) && destAd.equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_ZZZZ)
                    && StringUtils.isNotEmpty(vo.getEet())) {
                
                eetSum = eetSum + parseTime(vo.getEet());
            } else if(StringUtils.isNotEmpty(destAd) && !destAd.equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_ZZZZ)) {
                eetSum =0;
            }
            

            // once EET and AD set, add thru plan to stops and initialize new thru plan
            // it is assumed that EET and AD are always last part in a segment
            if (vo.getDestAd() != null && vo.getEet() != null && vo.getArrivalTime() != null && !vo.getDestAd().equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_ZZZZ)) {
                stops.add(vo);
                vo = new ThruFlightPlanVO(depAd, depTime);
            }

            // attempt to resolve CRUISING SPEED and FLIGHT LEVEL values
            if (optionalPart != null) {
                resolveCruisingSpeed(optionalPart, vo);
                resolveFlightLevel(optionalPart, vo);
            }
        }

        return stops;
    }

    private String parseOptionalItem15FieldValue(StringTokenizer stWithOptional, String route) {
        if (stWithOptional.hasMoreTokens()) {
           route = stWithOptional.nextToken();
        }
        return route;
    }

    private Boolean isMaintainedAd(String ident) {
        if (ident == null || ident.isEmpty())
            return false;

        // if aerodrome is in navdb - it is maintaned
        if (flightMovementAerodromeService.isAerodromeManned(ident)) {
            return true;
        } else {
            UnspecifiedDepartureDestinationLocation unad = unspecifiedDepartureDestinationLocationService
                    .findTextIdentifier(ident);
            return unad != null && unad.getMaintained();
        }
    }

    /**
     * True if route part is THRUPLAN Item15Field identifier.
     */
    private Boolean isThruPlan(final String routePart) {
        return (THRUPLAN_PATTERN.matcher(routePart).matches());
    }

    /**
     * Parse string time value as integer and throw error if parse exception occurs.
     */
    private Integer parseTime(final String eet) {
        return DateTimeUtils.getMinutes(eet);
    }

    private String minToHHMMFormat(Integer min) {
        String result = null;
        if(min != null) {
            int hours = min / 60; //since both are ints, you get an int
            int minutes = min % 60;
            result = String.format("%02d%02d", hours, minutes);
        }
        return result;
    }
    
    /**
     * Attempt to resolve optional Item15Field cruising speed value from route part. Return true if
     * successful, else false.
     */
    private void resolveCruisingSpeed(final String routePart, final ThruFlightPlanVO vo) {

        // attempt to parse cruising speed from route part, return false if null or empty
        String cruisingSpeed = Item15Parser.parse(routePart, Item15Field.CRUISING_SPEED);

        // set cruising speed from parsed route part and return true indicating match
        if (StringUtils.isNotEmpty(cruisingSpeed))
            vo.setCrusingSpeed(cruisingSpeed);
    }

    /**
     * Attempt to resolve optional Item15Field flight level value from route part. Return true if
     * successful, else false.
     */
    private void resolveFlightLevel(final String routePart, final ThruFlightPlanVO vo) {

        // attempt to parse flight level from route part, return false if null or empty
        String flightLevel = Item15Parser.parse(routePart, Item15Field.FLIGHT_LEVEL);
        // set flight level from parsed route part and return true indicating match
        if (StringUtils.isNotEmpty(flightLevel))
            vo.setFlightLevel(flightLevel);
    }
}
