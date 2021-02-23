package ca.ids.abms.modules.flightmovementsbuilder;

import ca.ids.abms.modules.aerodromes.cluster.RepositioningAerodromeClusterService;
import ca.ids.abms.modules.common.enumerators.CrossingDistanceStrategy;
import ca.ids.abms.modules.exemptions.*;
import ca.ids.abms.modules.exemptions.flightroutes.ExemptFlightRouteService;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.enumerate.EnrouteChargesBasis;
import ca.ids.abms.modules.flightmovementsbuilder.utility.FlightMovementBuilderUtility;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryScope;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryType;
import ca.ids.abms.modules.flightmovementsbuilder.utility.Item18Parser;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.vo.RouteCacheVO;
import ca.ids.abms.modules.route.BiDirectionalNominalRoute;
import ca.ids.abms.modules.route.NominalRoute;
import ca.ids.abms.modules.route.NominalRouteService;
import ca.ids.abms.modules.route.NominalRouteType;
import ca.ids.abms.modules.routesegments.RouteSegment;
import ca.ids.abms.modules.routesegments.RouteSegmentComparator;
import ca.ids.abms.modules.routesegments.SegmentType;
import ca.ids.abms.modules.routesegments.SegmentTypeMap;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.util.models.ApplicationConstants;
import ca.ids.abms.modules.utilities.flights.FlightUtility;
import ca.ids.abms.util.StringUtils;
import com.google.common.base.Preconditions;
import com.vividsolutions.jts.geom.Geometry;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class FlightMovementBillable {

    private static final Logger LOG = LoggerFactory.getLogger (FlightMovementBillable.class);

    private final ExemptFlightRouteService exemptFlightRouteService;
    private final FlightMovementBuilderUtility flightMovementBuilderUtility;
    private final NominalRouteService nominalRouteService;
    private final RepositioningAerodromeClusterService repositioningAerodromeClusterService;
    private final SystemConfigurationService systemConfigurationService;

    FlightMovementBillable(
        final ExemptFlightRouteService exemptFlightRouteService,
        final FlightMovementBuilderUtility flightMovementBuilderUtility,
        final NominalRouteService nominalRouteService,
        final RepositioningAerodromeClusterService repositioningAerodromeClusterService,
        final SystemConfigurationService systemConfigurationService
    ) {
        this.exemptFlightRouteService = exemptFlightRouteService;
        this.flightMovementBuilderUtility = flightMovementBuilderUtility;
        this.nominalRouteService = nominalRouteService;
        this.repositioningAerodromeClusterService = repositioningAerodromeClusterService;
        this.systemConfigurationService = systemConfigurationService;
    }

    /**
     * Calculate billable route and crossing distance based on FPL, ATC, TOWER and RADAR components as well
     * as the strategy setting from system configuration.
     * <p>
     * This function will calculate billable route geometry, entry/exit points and crossing distance based
     * on the first available source in the order of precedence specified in system configuration. Precedence
     * is a list of constants:
     * <ul>
     *   <li>RADAR     - radar route geometry and distance
     *   <li>SCHEDULED - flight plan geometry and distance
     *   <li>ATC_LOG   - atc movement geometry and distance
     *   <li>TOWER_LOG - tower log geometry and distance
     *   <li>LARGEST   - largest distance + associated geometry among RADAR, SCHEDULED, ATC_LOG and TOWER_LOG
     *   <li>SMALLEST  - smallest distance + associated geometry among RADAR, SCHEDULED, ATC_LOG and TOWER_LOG
     *   <li>NOMINAL   - nominal entry/exit points and distance (but no geometry)
     * </ul>
     *
     * <p>
     * At the end of this function the following fields will be set (possibly to NULL values).
     * <ul>
     *   <li>billableCrossingDist
     *   <li>billableRoute
     *   <li>billableEntryPoint
     *   <li>billableExitPoint
     * </ul>
     * <p>
     * If we can't find the relevant information all of the above fields will be set to NULL, except billableCrossingDist,
     * which will be set to 0.0.
     * <p>
     * Before calling this function, some fields of the flight movement must be filled out:
     * <ul>
     *   <li>movementType -- required
     *   <li>depAd -- required
     *   <li>destAd -- required
     *   <li>item18Dest - required if destAd == ZZZZ
     *   <li>radarCrossingDistance, radarRoute -- this will be used when precedence includes RADAR
     *   <li>fplCrossingDistance, fplRouteGeom -- this will be used when precedence includes SCHEDULED
     *   <li>atcCrossingDistance, atcLogTrack -- this will be used when precedence includes ATC_LOG
     *   <li>towerCrossingDistance, towerLogTrack -- this will be used when precedence includes TOWER_LOG
     *   <li>routeSegments -- this will be used when precedence includes RADAR, SCHEDULED, ATC_LOG or TOWER_LOG
     * </ul>
     *
     */
    public void calculateBillableRouteAndDistance (final FlightMovement fm) {
        final String flightName = fm.getFlightName();

        // reset billable route fields
        fm.setBillableCrossingCost (0.0d);
        fm.setBillableRoute (null);
        fm.setBillableEntryPoint (null);
        fm.setBillableExitPoint (null);

        // skip non-billable flights and set all billable route attributes to null to handle modified flights
        if(!do_checkBillable (fm, flightName)) {
            fm.setBillableEntryPoint (null);
            fm.setBillableExitPoint (null);
            fm.setBillableCrossingDist (null);
            fm.setBillableRoute (null);
            fm.setEnrouteChargesBasis(null);


            LOG.info ("Flight {}: is not billable", flightName);
            return;
        }

        // get crossing distance precedence
        final List<CrossingDistanceStrategy> precedence = doGetCrossDistPrecedence(flightName);

        // Try different strategies in order
        for (CrossingDistanceStrategy strategy : precedence) {
            BillableRouteInfo billableRouteInfo;
            switch (strategy) {
            case RADAR:
                billableRouteInfo = this.do_createBillableRouteInfo (fm, SegmentType.RADAR);
                break;
            case SCHEDULED:
                billableRouteInfo = this.do_createBillableRouteInfo (fm, SegmentType.SCHED);
                break;
            case ATC_LOG:
                billableRouteInfo = this.do_createBillableRouteInfo (fm, SegmentType.ATC);
                break;
            case TOWER_LOG:
                billableRouteInfo = this.do_createBillableRouteInfo (fm, SegmentType.TOWER);
                break;
            case USER:
                billableRouteInfo = this.do_createBillableRouteInfo (fm, SegmentType.USER);
                break;
            case NOMINAL:
                billableRouteInfo = do_loadNominalBillableRouteInfo (fm);
                break;
            case LARGEST:
                // Find longest among RADAR, SCHEDULED, ATC and TOWER
                billableRouteInfo = do_findLargest (do_createNominalBillableRouteInfoList (fm));
                break;
            case SMALLEST:
                // Find shortest among RADAR, SCHEDULED, ATC and TOWER
                billableRouteInfo = do_findSmallest (do_createNominalBillableRouteInfoList (fm));
                break;
            default:
                throw new IllegalArgumentException (
                    String.format (
                        "invalid/unsupported crossing distance precedence '%s' found", strategy));
            }

            // reset existing enroute charges basis
            fm.setEnrouteChargesBasis(null);

            // Found a distance for current strategy
            if (billableRouteInfo != null ) {

                // update entry and exit points now and then resolve NOMINAL route details if necessary
                fm.setBillableEntryPoint (billableRouteInfo.entryPoint);
                fm.setBillableExitPoint (billableRouteInfo.exitPoint);

                // if NOMINAL has greater precedence than current strategy
                if (precedence.indexOf(strategy) > precedence.indexOf(CrossingDistanceStrategy.NOMINAL)) {
                    // check again for a nominal route using updated entry and exit points
                    BillableRouteInfo nominalBillableRoute = this.do_loadNominalBillableRouteInfo(fm);

                    if (nominalBillableRoute != null) {
                        // use the nominal route if a nominal billable route is found using the updated entry and exit points
                        billableRouteInfo = nominalBillableRoute;
                        strategy = CrossingDistanceStrategy.NOMINAL;
                    }
                }

                // set nominal route distance and segment properties on the flight movement
                // this is due to the fact that nominal distance and route segments aren't set
                // until billed unlike atc, scheduled, radar and tower sources
                if (strategy != CrossingDistanceStrategy.NOMINAL) {
                    final BillableRouteInfo nominalRouteInfo = do_loadNominalBillableRouteInfo(fm);

                    resolveNominalRouteInfo(fm, nominalRouteInfo, billableRouteInfo.basis, precedence);
                } else {
                    resolveNominalRouteInfo(fm, billableRouteInfo, billableRouteInfo.basis, precedence);
                }

                // update entry and exit points again to ensure they match any resolution in resolveNominalRouteInfo
                fm.setBillableEntryPoint(billableRouteInfo.entryPoint);
                fm.setBillableExitPoint(billableRouteInfo.exitPoint);
                fm.setBillableRoute(billableRouteInfo.routeGeom);
                fm.setEnrouteChargesBasis(billableRouteInfo.basis.toValue());

                final Double actualCrossDist = getActualCrossDist(fm, billableRouteInfo.crossDist, billableRouteInfo.basis);
                fm.setBillableCrossingDist (actualCrossDist);

                LOG.info ("Flight {}: found {} effective billable crossing distance: {}, entryPoint={}, exitPoint={}",
                    flightName, strategy, actualCrossDist, fm.getBillableEntryPoint(), fm.getBillableExitPoint());
                return;
            }
        }

        LOG.info ("Flight {}: unable to find billable distance", flightName);
    }

    // -------------------------- private ------------------------

    /**
     * Billable route information: crossDist, entryPoint, exitPoint, routeGeom, source.
     */
    private static final class BillableRouteInfo {
        public static BillableRouteInfo create (final Double crossDist, final String entryPoint, final String exitPoint) {
            return create (crossDist, entryPoint, exitPoint, null, EnrouteChargesBasis.NOMINAL);
        }
        public static BillableRouteInfo create (final Double crossDist, final String entryPoint, final String exitPoint,
        										final Geometry routeGeom, EnrouteChargesBasis basis) {
            // US 93741: skip zero length cross distance unless EnrouteChargesBasis.MANUAL (aka SegmentType.USER)
            if (crossDist != null && (crossDist > 0 || EnrouteChargesBasis.MANUAL.equals(basis))) {
                final BillableRouteInfo x = new BillableRouteInfo();
                x.crossDist = crossDist;
                x.entryPoint = entryPoint;
                x.exitPoint = exitPoint;
                x.routeGeom = routeGeom;
                x.basis = basis;
                return x;
            }
            return null;
        }
        Double crossDist;
        String entryPoint;
        String exitPoint;
        Geometry routeGeom;
        EnrouteChargesBasis basis;
    }

    /**
     * Billable route geometry: route, segments
     */
    private static class BillableRouteGeom {

        public static BillableRouteGeom create(final Geometry route, final List<RouteSegment> segments) {
            final BillableRouteGeom billableRouteGeom = new BillableRouteGeom();
            billableRouteGeom.route = route;
            billableRouteGeom.segments = segments;
            return billableRouteGeom;
        }

        Geometry route;
        List<RouteSegment> segments;
    }

    /**
     * Check whether the given flight movement can be billed
     */
    private boolean do_checkBillable (final FlightMovement fm, final String flightName) {
        if (fm != null && !fm.isOTHER()) {
            return true;
        }
        LOG.info ("Flight {} is not billable -- skipped", flightName);
        return false;
    }

    /**
     * Return the list of crossing distance strategy constants from the system configuration table
     */
    private List <CrossingDistanceStrategy> doGetCrossDistPrecedence(final String flightName) {
        // find the precedence of billable route calculation strategies
        SystemConfiguration systemConfiguration = systemConfigurationService.getOneByItemName(SystemConfigurationItemName.CROSSING_DISTANCE_PRECEDENCE);
        if (systemConfiguration == null) {
            LOG.error ("Flight {}: system config parameter '{}' not found -- can't calculate billable route",
                    flightName, SystemConfigurationItemName.CROSSING_DISTANCE_PRECEDENCE);
            return Collections.emptyList();
        }
        List <CrossingDistanceStrategy> precedence = systemConfiguration.getCurrentValueList (CrossingDistanceStrategy::parseDatabaseString);
        if (precedence == null || precedence.isEmpty()) {
            precedence = systemConfiguration.getDefaultValueList (CrossingDistanceStrategy::parseDatabaseString);
            if (precedence == null || precedence.isEmpty()) {
                LOG.error ("Flight {}: system config parameter '{}' has an empty default value -- can't calculate billable route",
                        flightName,
                        SystemConfigurationItemName.CROSSING_DISTANCE_PRECEDENCE);
                return Collections.emptyList();
            }
            LOG.info ("Flight {}: system config parameter '{}' is empty; using default value {}",
                    flightName,
                    SystemConfigurationItemName.CROSSING_DISTANCE_PRECEDENCE,
                    precedence);
        }
        else {
            LOG.info ("Flight {}: system config parameter '{}' is {}",
                    flightName,
                    SystemConfigurationItemName.CROSSING_DISTANCE_PRECEDENCE,
                    precedence);
        }
        return precedence;
    }

    /**
     * Apply crossing distance exemptions based on system configuration and flight scope. This will also remove the
     * crossing distance of each exempt thru plan flight leg and add to flight movement flight notes field.
     */
    public Double getActualCrossDist(final FlightMovement flightMovement, final Double crossDist) {
        return getActualCrossDist(flightMovement, crossDist, null);
    }

    /**
     * Apply crossing distance exemptions based on system configuration and flight scope. This will also remove the
     * crossing distance of each exempt thru plan flight leg and add to flight movement flight notes field.
     *
     * @param basis used to determine route segments to use when removing exempt thru plan flight legs (default: SCHEDULED)
     */
    private Double getActualCrossDist(final FlightMovement fm, final Double crossDist, final EnrouteChargesBasis basis) {
        // Cross dist must be greater than 0 to apply exemption
        if (fm != null && fm.getFlightCategoryType() != null && crossDist != null && crossDist > 0) {

            // if thru plan flight movement, remove exempt flight legs from actual crossing distance
            // this must be done before applying min/max exempt distances below
            Double actualCrossDist = Boolean.TRUE.equals(fm.getThruFlight())
                ? getActualCrossDistThruPlan(fm, crossDist, basis) : crossDist;

            String flightName = fm.getFlightName();
            // Distances less than this value should be exempt (i.e., 0.0)
            final Double exemptCrossDist = do_getCrossDistOption (SystemConfigurationItemName.EXEMPT_FLIGHTS_DISTANCE);
            if (exemptCrossDist == null || actualCrossDist > exemptCrossDist) {
                // If crossing distance is large enough limit it by min/max values per flight type
                String minOption = null;
                String maxOption = null;
                if(fm.getFlightCategoryType() != null) {
                	if( fm.getFlightCategoryType().equals(FlightmovementCategoryType.DOMESTIC)) {
                		minOption = SystemConfigurationItemName.MIN_DOMESTIC_CROSSING_DISTANCE;
                		maxOption = SystemConfigurationItemName.MAX_DOMESTIC_CROSSING_DISTANCE;
                	} else if(fm.getFlightCategoryType().equals(FlightmovementCategoryType.ARRIVAL) ||
                			  fm.getFlightCategoryType().equals(FlightmovementCategoryType.DEPARTURE) ||
                			  fm.getFlightCategoryType().equals(FlightmovementCategoryType.OVERFLIGHT)) {
                		if(fm.getFlightCategoryScope() !=null) {
                			if( fm.getFlightCategoryScope().equals(FlightmovementCategoryScope.REGIONAL)) {

                				minOption = SystemConfigurationItemName.MIN_REGIONAL_CROSSING_DISTANCE;
                				maxOption = SystemConfigurationItemName.MAX_REGIONAL_CROSSING_DISTANCE;
                			} else if ( fm.getFlightCategoryScope().equals(FlightmovementCategoryScope.INTERNATIONAL)){

                				minOption = SystemConfigurationItemName.MIN_INTERNATIONAL_CROSSING_DISTANCE;
                				maxOption = SystemConfigurationItemName.MAX_INTERNATIONAL_CROSSING_DISTANCE;
                			}
                		} else {
                		    LOG.info ("Flight {}: can't calculate crossing distance because category scope is null",
                                flightName);
                			return 0.0;
                		}
                	}
                } else {
                    LOG.info ("Flight {}: can't calculate crossing distance because category type is null",
                            flightName);
                	return 0.0;
                }
                // Force crossing distance to min value if necessary
                final Double minCrossDist = do_getCrossDistOption (minOption);
                if (minCrossDist != null && actualCrossDist < minCrossDist) {
                    LOG.info ("Flight {}: adjusted crossing distance {} to minimum {}", flightName, actualCrossDist, minCrossDist);
                    appendFlightNoteValue(fm, FlightMovementNoteMessage.MIN);
                    return minCrossDist;
                }

                // Force crossing distance to max value if necessary
                final Double maxCrossDist = do_getCrossDistOption (maxOption);
                if (maxCrossDist != null && actualCrossDist > maxCrossDist) {
                    LOG.info ("Flight {}: adjusted crossing distance {} to maximum {}", flightName, actualCrossDist, maxCrossDist);
                    appendFlightNoteValue(fm, FlightMovementNoteMessage.MAX);
                    return maxCrossDist;
                }
                return actualCrossDist;
            }
            else {
                LOG.info ("Flight {}: crossing distance {} is below exemption threshold {}",
                        flightName, actualCrossDist, exemptCrossDist);
                fm.setExemptFlightDistance(true);
                FlightNotesUtility.mergeFlightNotes(fm, FlightMovementNoteMessage.EXEMPT_FLIGHTS_DISTANCE);
                return 0.0d;
            }
        }
        return 0.0;
    }

    /**
     * This will remove the crossing distance of each exempt thru plan flight leg and add to flight movement notes.
     * Should only be called from within the {@link #getActualCrossDist} methods.
     */
    private Double getActualCrossDistThruPlan(final FlightMovement fm, final Double crossDist, final EnrouteChargesBasis basis) {

        // cannot perform calculations if no flight movement provided with route segments or cross distance 0 or less
        // in these cases, simply return the provided cross distance
        if (crossDist == null || crossDist <= 0.0 || fm == null || fm.getRouteSegments() == null
            || fm.getRouteSegments().isEmpty()) {
            return crossDist;
        }

        // map enroute charge basis to route segment type
        // default segment type to scheduled if no basis provided
        SegmentType segmentType = basis == null ? SegmentType.SCHED
            : SegmentTypeMap.mapEnrouteCrossingBasisToSegmentType(basis);

        // loop through each route segment that matches the basis and remove exempt flight leg lengths
        // from actual crossing distance and actual billable segments
        double actualCrossDist = crossDist;

        // keep track of the number of segments removed below to adjust segment number values
        int segmentAdjustment = 0;

        // retrieve segment collection pointer and sort as list by segment number
        // must be actual flight movement segment collection pointer to manipulate and persist
        List<RouteSegment> segments = fm.getRouteSegments();
        segments.sort(Comparator.comparingInt(RouteSegment::getSegmentNumber));

        // iterate over each segment, remove exempt from segments list for persistence later
        for (Iterator<RouteSegment> iterator = segments.iterator(); iterator.hasNext();) {
            RouteSegment segment = iterator.next();

            // only inspect segments that match provided segment type
            if (!Objects.equals(segment.getSegmentType(), segmentType)) continue;

            // remove segment from billable crossing distance if exempt
            if (isEnrouteExemptionApplied(fm, segment)) {
                segmentAdjustment++;
                actualCrossDist -= segment.getSegmentLength();
                iterator.remove();
            }

            // else if segment adjustment is greater then 0, adjust segment number
            else if (segmentAdjustment > 0) {
                segment.setSegmentNumber(segment.getSegmentNumber() - segmentAdjustment);
            }
        }

        // ensure we never return a crossing distance less then 0
        return actualCrossDist > 0.0 ? actualCrossDist : 0.0;
    }

    private void appendFlightNoteValue(FlightMovement fm, int option) {
        String value = null;
        if (fm.getFlightCategoryScope() == FlightmovementCategoryScope.DOMESTIC) {
            if (option == FlightMovementNoteMessage.MIN) {
                value = FlightMovementNoteMessage.MIN_DOMESTIC_CROSSING_DISTANCE;
            } else if (option == FlightMovementNoteMessage.MAX) {
                value = FlightMovementNoteMessage.MAX_DOMESTIC_CROSSING_DISTANCE;
            }
        } else if (fm.getFlightCategoryScope() == FlightmovementCategoryScope.REGIONAL) {
            if (option == FlightMovementNoteMessage.MIN) {
                value = FlightMovementNoteMessage.MIN_REGIONAL_CROSSING_DISTANCE;
            } else if (option == FlightMovementNoteMessage.MAX) {
                value = FlightMovementNoteMessage.MAX_REGIONAL_CROSSING_DISTANCE;
            }
        } else if (fm.getFlightCategoryScope() == FlightmovementCategoryScope.INTERNATIONAL) {
            if (option == FlightMovementNoteMessage.MIN) {
                value = FlightMovementNoteMessage.MIN_INTERNATIONAL_CROSSING_DISTANCE;
            } else if (option == FlightMovementNoteMessage.MAX) {
                value = FlightMovementNoteMessage.MAX_INTERNATIONAL_CROSSING_DISTANCE;
            }
        }
        FlightNotesUtility.mergeFlightNotes(fm, value);
    }

    /**
     * Find crossing distance option from system configuration. If the given
     * option doesn't exist, null or less than 0.0, then return null.
     */
    private Double do_getCrossDistOption (final String optionName) {
        final SystemConfiguration x = systemConfigurationService.getOneByItemName (optionName);
        if (x != null && x.getCurrentValue() != null) {
            try {
                final double value = Double.parseDouble(x.getCurrentValue());
                if (value >= 0.0d) {
                    return value;
                }
            } catch (final NumberFormatException xx) {
                // ignored, silently
            }
        }
        return null;
    }

    /**
     * Find an existing nominal route record based on the given flight movement.
     * We look for it based on departure and destination aerodromes.
     */
    private BiDirectionalNominalRoute doLoadNominalRoute(final FlightMovement fm) {
        return nominalRouteService.findNominalRouteBasedOnPrecedence(fm);
    }

    /**
     * Find first point from the list of segments of a particular type.
     */
    private String do_getEntryPoint (final List <RouteSegment> segmentList, final SegmentType segmentType) {
        if (segmentList != null && segmentType != null) {
            final RouteSegment firstSegment = segmentList.stream()
                // filter by segment type
                .filter (s->s.getSegmentType() != null && s.getSegmentType().equals(segmentType))
                // git smallest segment number
                .min (new RouteSegmentComparator())
                // or null if not found
                .orElse (null);
            if (firstSegment != null) {
                return firstSegment.getSegmentStartLabel();
            }
        }
        return null;
    }

    /**
     * Find last point from the list of segments of a particular type.
     */
    private String do_getExitPoint (final List <RouteSegment> segmentList, final SegmentType segmentType) {
        if (segmentList != null && segmentType != null) {
            final RouteSegment lastSegment = segmentList.stream()
                // filter by segment type
                .filter (s->s.getSegmentType() != null && s.getSegmentType().equals(segmentType))
                // git largest segment number
                .max (new RouteSegmentComparator())
                // or null if not found
                .orElse (null);
            if (lastSegment != null) {
                return lastSegment.getSegmentEndLabel();
            }
        }
        return null;
    }

    /**
     * Insert a nominal route for the given flight movement and crossing distance, if one doesn't exist
     */
    private void do_saveNominalRoute (final FlightMovement fm, final Double crossDist) {
        final String pointA = StringUtils.stripToNull(fm.getDepAd());
        if (pointA != null && crossDist != null && crossDist >= 0.0d) {
            String pointB = StringUtils.stripToNull(fm.getDestAd());
            if (pointB == null || pointB.equals(ApplicationConstants.PLACEHOLDER_ZZZZ)) {
                pointB = Item18Parser.getFirstAerodromeOrDMS(pointB);
            }
            if (pointB != null) {
                // Find nominal route for the given departure & destination aerodromes
                Double fl =null;
                if(systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE)) {
                    fl = Double.valueOf(systemConfigurationService.getIntOrZero(SystemConfigurationItemName.DEFAULT_FLIGHT_LEVEL)); 
                }
                BiDirectionalNominalRoute existingNominalRoute = this.nominalRouteService.findByAerodromes(pointA, pointB,
                        FlightUtility.convertFlightLevel(fm.getFlightLevel(),fl));

                // If not found, create one
                if (existingNominalRoute == null) {
                    NominalRoute nominalRoute = new NominalRoute();
                    nominalRoute.setType (NominalRouteType.AERODROME_TO_AERODROME.getValue());
                    nominalRoute.setPointa (pointA);
                    nominalRoute.setPointb (pointB);
                    nominalRoute.setNominalDistance (crossDist);
                    this.nominalRouteService.create (nominalRoute, false);
                }
            }
        }
    }

    /**
     * Create a BillableRouteInfo object based on an existing nominal route record
     */
    private BillableRouteInfo do_loadNominalBillableRouteInfo (final FlightMovement fm) {

        // find bi directional nominal route from flight movement info
        final BiDirectionalNominalRoute nominalRoute = doLoadNominalRoute(fm);
        if (nominalRoute == null || nominalRoute.getNominalDistance() == null ||
            nominalRoute.getNominalDistance() < 0.0d) {
            return null;
        }

        // normalize nominal route so that inverse points are in the correct order
        String pointA = BooleanUtils.isTrue(nominalRoute.isInverse()) ? nominalRoute.getPointb() : nominalRoute.getPointa();
        String pointB = BooleanUtils.isTrue(nominalRoute.isInverse()) ? nominalRoute.getPointa() : nominalRoute.getPointb();
        return BillableRouteInfo.create (nominalRoute.getNominalDistance(), pointA, pointB);
    }

    /**
     * Save a nominal route if necessary
     *
     * Removed from `calculateBillableRouteAndDistance(...)` as per US89078. Nominal
     * routes must be manually entered by a user until further notice.
     */
    @SuppressWarnings("unused")
    private void do_saveNominalBillableRouteInfo (final FlightMovement fm, final BillableRouteInfo billableRouteInfo) {
        this.do_saveNominalRoute (fm, billableRouteInfo.crossDist);
    }

    /**
     * Create a BillableRouteInfo of a particular segment type from bits stored in a flight movement.
     */
    private BillableRouteInfo do_createBillableRouteInfo (final FlightMovement fm, final SegmentType segmentType) {
        if (fm != null && segmentType != null) {
            final String entryPoint = do_getEntryPoint(fm.getRouteSegments(), segmentType);
            final String exitPoint = do_getExitPoint(fm.getRouteSegments(), segmentType);

            switch (segmentType) {
            case RADAR:
                return BillableRouteInfo.create (fm.getRadarCrossingDistance(),entryPoint,
                    exitPoint, fm.getRadarRoute(), EnrouteChargesBasis.RADAR_SUMMARY);
            case SCHED:
                return BillableRouteInfo.create (fm.getFplCrossingDistance(), entryPoint,
                    exitPoint, fm.getFplRouteGeom(),EnrouteChargesBasis.SCHEDULED);
            case ATC:
                return BillableRouteInfo.create (fm.getAtcCrossingDistance(), entryPoint,
                    exitPoint, fm.getAtcLogTrack(), EnrouteChargesBasis.ATC_LOG);
            case TOWER:
                return BillableRouteInfo.create (fm.getTowerCrossingDistance(), entryPoint,
                    exitPoint, fm.getTowerLogTrack(), EnrouteChargesBasis.TOWER_LOG);
            case USER:
                return BillableRouteInfo.create (fm.getUserCrossingDistance(), entryPoint,
                    exitPoint, fm.getFplRouteGeom(), EnrouteChargesBasis.MANUAL);
            default:
                break;
            }
        }
        return null;
    }

    /**
     * Create a list of billable route info objects from all possible sources stored in a flight movmenet
     * (i.e., RADAR, SCHED, ATC and TOWER). We need this to select the maximum or minimum.
     */
    private List <BillableRouteInfo> do_createNominalBillableRouteInfoList (final FlightMovement fm) {
        List <BillableRouteInfo> distInfoList = new ArrayList<>();
        distInfoList.add (do_createBillableRouteInfo (fm, SegmentType.RADAR));
        distInfoList.add (do_createBillableRouteInfo (fm, SegmentType.SCHED));
        distInfoList.add (do_createBillableRouteInfo (fm, SegmentType.ATC));
        distInfoList.add (do_createBillableRouteInfo (fm, SegmentType.TOWER));
        return distInfoList.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Find a billable route whose crossing distance is largest.
     */
    private BillableRouteInfo do_findLargest (final List <BillableRouteInfo> distInfoList) {
        if (distInfoList != null) {
            return distInfoList.stream()
                .max(Comparator.comparing(a -> a.crossDist))
                .orElse(null);
        }
        return null;
    }

    /**
     * Find a billable route whose crossing distance is smallest.
     */
    private BillableRouteInfo do_findSmallest (final List <BillableRouteInfo> distInfoList) {
        if (distInfoList != null) {
            return distInfoList.stream()
                .min(Comparator.comparing(a -> a.crossDist))
                .orElse(null);
        }
        return null;
    }

    /**
     * Resolve nominal route crossing distance and route segments based on billable route info. If
     * billable route basis is NOMINAL, find by highest precedence with route segment else find by
     * billable route basis route segments. If all else fails, calculate route segments by entry
     * and exit points.
     *
     * This method will also update the provided nominalRouteInfo entry/exit points based on resolved
     * route segment's first start label and last end label respectively.
     */
    private void resolveNominalRouteInfo(
        final FlightMovement flightMovement, final BillableRouteInfo nominalRouteInfo,
        final EnrouteChargesBasis enrouteChargesBasis, final List<CrossingDistanceStrategy> precedence) {

        // if nominal route info is null but flightmovement has nominal route values they should be cleared. Leftover from
        // nominal route that was removed
        if (nominalRouteInfo == null) {
            LOG.debug("Nominal route does not exist for flight. Clearing out nominal route values for flight if they exist.");
            flightMovement.setNominalCrossingCost(null);
            flightMovement.setNominalCrossingDistance(null);
            return;
        }

        // assert that require arguments are valid
        if (flightMovement == null || enrouteChargesBasis == null || precedence == null) {
            throw new IllegalArgumentException("'flightMovement', 'nominalRouteInfo', 'enrouteChargesBasis', and 'precedence' " +
                "arguments cannot be null");
        }

        // set flight movement's nominal crossing distance
        flightMovement.setNominalCrossingDistance(nominalRouteInfo.crossDist);

        // validate that flight movement route segments exist before resolving nominal route segments
        List<RouteSegment> routeSegments = flightMovement.getRouteSegments();
        if (routeSegments == null)
            return;

        // remove existing nominal route segments
        routeSegments.removeIf(s -> SegmentType.NOMINAL.equals(s.getSegmentType()));

        // find billable route segments and route geometry
        BillableRouteGeom billableRouteGeom = findBillableRouteGeom(routeSegments, enrouteChargesBasis, precedence, nominalRouteInfo,
            flightMovement, SegmentType.NOMINAL);

        // set nominal route info to billable route geometry
        nominalRouteInfo.routeGeom = billableRouteGeom.route;

        // map billable route segments to nominal route segments
        List<RouteSegment> nominalRouteSegments = null;
        if (billableRouteGeom.segments != null && !billableRouteGeom.segments.isEmpty()) {

            // copy billable route segments as NOMINAL route segments
            nominalRouteSegments = billableRouteGeom.segments.stream()
                .map(s -> s.copy(SegmentType.NOMINAL))
                .collect(Collectors.toList());

            LOG.debug("{} used for nominal route '{}-{}' with nominal distance of '{}' on flight movement '{}'.",
                billableRouteGeom.segments.get(0).getSegmentType(), nominalRouteInfo.entryPoint,
                nominalRouteInfo.exitPoint, nominalRouteInfo.crossDist, flightMovement);

            // update entry and exit points of nominal route info to match billable segments
            resolveNominalEntryExitPoints(nominalRouteInfo, nominalRouteSegments);
        }

        // add nominal route segments if found to route segments list
        if (nominalRouteSegments != null && !nominalRouteSegments.isEmpty())
            routeSegments.addAll(nominalRouteSegments);
        else
            LOG.warn("No route segments found for nominal route '{}-{}' with nominal distance of '{}' on " +
                "flight movement '{}'.", nominalRouteInfo.entryPoint, nominalRouteInfo.exitPoint,
                nominalRouteInfo.crossDist, flightMovement);

        // set flight movement route segment list with updated route segments
        flightMovement.setRouteSegments(routeSegments);
    }

    /**
     * Returns the billable route geometry in the following order.
     *
     * 1) Find by Enroute Charge Basis
     * 2) Find by Crossing Distance Strategy
     * 3) Find by Billable Route Info
     */
    private BillableRouteGeom findBillableRouteGeom(
        final List<RouteSegment> routeSegments, final EnrouteChargesBasis enrouteChargesBasis,
        final List<CrossingDistanceStrategy> crossingDistanceStrategies, final BillableRouteInfo billableRouteInfo,
        final FlightMovement flightMovement, final SegmentType segmentType) {

        Geometry billableRoute = null;
        List<RouteSegment> billableSegments;

        // resolve billable route segments based on enroute charge basis
        billableSegments = findRouteSegmentsByBasis(routeSegments, enrouteChargesBasis);

        // if no billable route segments found for billable route basis
        // resolve bilable route segments based on strategy precedence, highest available
        // strategy with route segments is assumed to be the billable route
        if (billableSegments == null || billableSegments.isEmpty())
            billableSegments = findRouteSegmentsByPrecedence(routeSegments, crossingDistanceStrategies);

        // if no billable route segments found by basis and precedence, resort to calculating by billable
        // route info and set billable route geometry from result
        if (billableSegments == null || billableSegments.isEmpty()) {
            RouteCacheVO routeCacheVO = findRouteByBillableInfo(billableRouteInfo, flightMovement, segmentType);
            if (routeCacheVO != null) {
                billableRoute = routeCacheVO.getRoute();
                billableSegments = flightMovementBuilderUtility.mergeTheSegmentsList(null,
                    routeCacheVO.getRouteSegmentList(), SegmentType.NOMINAL);
            }
        }

        // calculate billable route geometry from segment type if not defined above
        if (billableRoute == null && billableSegments != null && !billableSegments.isEmpty())
            billableRoute = SegmentTypeMap.mapSegmentTypeToFlightMovementGeometry(
                billableSegments.get(0).getSegmentType(), flightMovement);

        // return route geometry and segments
        return BillableRouteGeom.create(billableRoute, billableSegments);
    }

    /**
     * Find route segments in list based on enroute charge basis.
     */
    private List<RouteSegment> findRouteSegmentsByBasis(
        final List<RouteSegment> routeSegments, final EnrouteChargesBasis enrouteChargesBasis) {

        // arguments should never be null
        if (routeSegments == null || enrouteChargesBasis == null)
            throw new IllegalArgumentException("'routeSegments' and 'enrouteChargesBasis' arguments cannot be null");

        // map enroute charge basis to segment type and resolve if exists
        SegmentType segmentType = SegmentTypeMap.mapEnrouteCrossingBasisToSegmentType(enrouteChargesBasis);
        if (segmentType != null) {

            // filter out only route segments for basis segment type and return as nominal route segments
            return routeSegments.stream()
                .filter(s -> segmentType.equals(s.getSegmentType()))
                .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    /**
     * Find route segments in list based on crossing distance strategy. First available strategy
     * with route segments is returned.
     */
    private List<RouteSegment> findRouteSegmentsByPrecedence(
        final List<RouteSegment> existingRouteSegments, final List<CrossingDistanceStrategy> crossingDistanceStrategies) {

        // arguments should never be null
        if (existingRouteSegments == null || crossingDistanceStrategies == null)
            throw new IllegalArgumentException("'existingRouteSegments' and 'crossingDistanceStrategies' arguments " +
                "cannot be null");

        // loop through precedence and return first available with route segments
        for (CrossingDistanceStrategy strategy : crossingDistanceStrategies) {

            // map crossing distance strategy to segment type and resolve if exists
            SegmentType segmentType = SegmentTypeMap.mapCrossingDistanceStrategyToSegmentType(strategy);
            if (segmentType != null) {

                // filter out only route segments for strategy's segment type
                List<RouteSegment> result = existingRouteSegments.stream()
                    .filter(s -> segmentType.equals(s.getSegmentType()))
                    .collect(Collectors.toList());

                // if route segments found, return as nominal route segments
                if (!result.isEmpty())
                    return result;
            }
        }

        // return empty list if route segments could not be found
        return Collections.emptyList();
    }

    /**
     * Find route segments from route parser based on billable route info and flight movement details.
     */
    private RouteCacheVO findRouteByBillableInfo(
        final BillableRouteInfo billableRouteInfo, final FlightMovement flightMovement, final SegmentType segmentType) {

        // arguments should never be null
        if (billableRouteInfo == null || flightMovement == null)
            throw new IllegalArgumentException("'billableRouteInfo' and 'flightMovement' arguments cannot be null");

        String route = "DCT";
        String entry = billableRouteInfo.entryPoint;
        String exit = billableRouteInfo.exitPoint;

        // calculate route of entry and exit points using direct route
        RouteCacheVO routeCacheVO = null;
        try {

            // attempt to get route with en converting departure and destination values
            //2020-04-02 TFS 115533 - TMA handling
           if(systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE)) {
               Double fl = Double.valueOf(systemConfigurationService.getIntOrZero(SystemConfigurationItemName.DEFAULT_FLIGHT_LEVEL)); 
               
                routeCacheVO = flightMovementBuilderUtility.getRouteInformationByRouteParserExcludeTmas(entry, exit, route,
                        segmentType, flightMovement.getCruisingSpeedOrMachNumber(), flightMovement.getEstimatedElapsedTime(),
                        FlightUtility.convertFlightLevel(flightMovement.getFlightLevel(),fl));
            } else {
                routeCacheVO = flightMovementBuilderUtility.getRouteInformationByRouteParser(entry, exit, route,
                        segmentType, flightMovement.getCruisingSpeedOrMachNumber(), flightMovement.getEstimatedElapsedTime());
            }
            // if a routeCache cannot be built, check if either dest/dep are in ABMSDB
            if (routeCacheVO != null && routeCacheVO.getDistance() == null) {
                String entryCoords = flightMovementBuilderUtility.checkAerodrome(entry, null, true);
                String exitCoords = flightMovementBuilderUtility.checkAerodrome(exit, null, true);
                
                //2020-04-02 TFS 115533 - TMA handling
                if(systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE)) {
                    Double fl = Double.valueOf(systemConfigurationService.getIntOrZero(SystemConfigurationItemName.DEFAULT_FLIGHT_LEVEL));
                    routeCacheVO = flightMovementBuilderUtility.getRouteInformationByRouteParserExcludeTmas(entryCoords, exitCoords, route,
                            segmentType, flightMovement.getCruisingSpeedOrMachNumber(), flightMovement.getEstimatedElapsedTime(),
                            FlightUtility.convertFlightLevel(flightMovement.getFlightLevel(),fl));
                } else {
                    routeCacheVO = flightMovementBuilderUtility.getRouteInformationByRouteParser(entryCoords, exitCoords,
                            route, segmentType, flightMovement.getCruisingSpeedOrMachNumber(), flightMovement.getEstimatedElapsedTime());
                }
            }

        } catch (FlightMovementBuilderException ex) {
            LOG.warn("Could not determine {} route because: {}", segmentType, ex.getMessage());
        }

        return routeCacheVO;
    }

    /**
     * Return true if route segment has enroute exemption, used only for thru plan flight legs as segments are assumed
     * to be each flight movement thru plan stop.
     *
     * This will apply exemption notes to flight movement flight notes field.
     *
     * Limitation: Does not work with percentage, either 100% exempt or not. This needs to be redesigned if
     * it is to support percentages.
     */
    private boolean isEnrouteExemptionApplied(final FlightMovement flightMovement, final RouteSegment segment) {
        Preconditions.checkArgument(flightMovement != null && segment != null);

        // default exemption to false until enroute exemption of 100% can be found
        boolean exempt = false;

        // only look for departure and destination exemptions using stop dep and dest aerodromes
        Collection<ExemptionType> exemptions = new ArrayList<>();
        exemptions.addAll(exemptFlightRouteService.findApplicableExemptions(
            segment.getSegmentStartLabel(), segment.getSegmentEndLabel(), segment.getFlightLevel()));
        
        exemptions.addAll(repositioningAerodromeClusterService.findApplicableExemptions(
            segment.getSegmentStartLabel(), segment.getSegmentEndLabel()));

        // loop through each exemption and apply if enroute charge exemption defined as 100 percent
        Collection<String> flightNotes = new ArrayList<>();
        for (ExemptionType exemption : exemptions) {

            // skip if exemption enroute charge is not defined
            if (exemption.enrouteChargeExemption() != null && exemption.enrouteChargeExemption() == 100.0) {
                exempt = true;

                // only add flight notes if not null or blank
                if (StringUtils.isNotBlank(exemption.flightNoteChargeExemption())) {
                    flightNotes.add(exemption.flightNoteChargeExemption());
                }
            }
        }

        // merge any enroute exemption notes found into flight notes field
        // usage of FlightNotesUtility.mergeFlightNotes will prevent duplicates
        FlightNotesUtility.mergeFlightNotes(flightMovement, flightNotes);

        // return whether enroute exemption was found and flight notes applied
        return exempt;
    }

    /**
     * Since nominal routes can include departure and arrivals outside of the billable FIR. It is necessary to
     * resolve the nominal route info entry/exit points by the matching nominal route segments.
     *
     * This method will set the provided BillableRouteInfo.entryPoint and BillableRouteInfo.exitPoint values
     * from the first RouteSegment.segmentStartLabel and last RouteSegment.segmentEndLabel respectively.
     *
     * If no entry and/or exit labels are found, the billable route info entry and/or exit points will be unchanged.
     */
    private void resolveNominalEntryExitPoints(final BillableRouteInfo info, final List<RouteSegment> segments) {
        if (info == null || segments == null || segments.isEmpty()) return;

        RouteSegment entry = segments.get(0);
        RouteSegment exit = segments.get(segments.size() - 1);

        // since it is not guaranteed that the first and last segment index are to be used
        // loop through each and compare segment numbers
        for (RouteSegment segment : segments) {
            if (segment == null) continue;
            if (segment.getSegmentNumber() < entry.getSegmentNumber()) entry = segment;
            if (segment.getSegmentNumber() > exit.getSegmentNumber()) exit = segment;
        }

        // update provided billing route info entry point if entry label found
        if (entry != null && StringUtils.isNotBlank(entry.getSegmentStartLabel())) {
            info.entryPoint = entry.getSegmentStartLabel();
        }

        // update provided billing route info exit point if exit label found
        if (exit != null && StringUtils.isNotBlank(exit.getSegmentEndLabel())) {
            info.exitPoint = exit.getSegmentEndLabel();
        }
    }
}
