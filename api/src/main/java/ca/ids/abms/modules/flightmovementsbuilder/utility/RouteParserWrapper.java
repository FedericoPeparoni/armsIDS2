package ca.ids.abms.modules.flightmovementsbuilder.utility;

import ca.ids.abms.modules.flightmovements.FlightMovementAerodromeService;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementBuilderIssue;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.RouteCache;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.RouteCacheService;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.RouteCacheUtility;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.vo.RouteCacheVO;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.vo.RouteSegmentVO;
import ca.ids.abms.modules.flightmovementsbuilder.vo.RPAirspaceVO;
import ca.ids.abms.modules.radarsummary.RadarSummary;
import ca.ids.abms.modules.routesegments.SegmentType;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.util.models.ApplicationConstants;
import ca.ids.abms.modules.util.models.Calculation;
import ca.ids.abms.modules.util.models.geometry.GeometryUtil;
import ca.ids.abms.util.GeometryUtils;
import ca.ids.abms.util.StringUtils;
import com.ubitech.aim.routeparser.*;
import com.vividsolutions.jts.geom.Geometry;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by c.talpa on 27/02/2017.
 *
 * ============================================================== WARNING!!!
 * ============================================================== This class has
 * serious problems and needs to be fixed! See TFS bug 68327.
 *
 */
@Component
public class RouteParserWrapper {

    private final DataSource navDbDataSource;
    private final RouteCacheService routeCacheService;
    private final SystemConfigurationService systemConfigurationService;
    private final FlightMovementAerodromeService flightMovementAerodromeService;
    private final RouteParserParameters routeParserParameters;

    private static final Logger LOG = LoggerFactory.getLogger(RouteParserWrapper.class);

    private static final double BUFFER_RADIUS_METERS = 399000.0;

    private static final boolean FPL_PNT_VS_DB_PNT_PRIORITY = false;

    private static final String NAVDB_ROUNDING = "NAVDB";
    private static final String NAVDB_ERROR = "Error retrieving navdbconnection";
    private static final String NAVDB_ERROR_CLOSING_CONNECTION = "Error closing navdb connection";
    

    public RouteParserWrapper(
        @Qualifier("navDBDataSource") final DataSource navDBDataSource,
        final RouteCacheService routeCacheService,
        final SystemConfigurationService systemConfigurationService,
        final FlightMovementAerodromeService flightMovementAerodromeService,
        final RouteParserParameters routeParserParameters
    ) {
        this.navDbDataSource = navDBDataSource;
        this.routeCacheService = routeCacheService;
        this.flightMovementAerodromeService = flightMovementAerodromeService;
        this.systemConfigurationService = systemConfigurationService;
        this.routeParserParameters = routeParserParameters;
    }

    public DataSource getNavDbDataSource() {
        return navDbDataSource;
    }

    /**
     * This method return the following information:
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
    RouteCacheVO getRouteInformationByRouteParser(
        final String depAd, final String routeString, final String destAd, final SegmentType segmentType,
        final LinkedList<String> airspceWKT, final LinkedList<RPAirspaceVO> airspaceRP
    ) {
        RouteCacheVO routeCacheVO = null;

        if (StringUtils.isNotBlank(routeString) && segmentType != null) {

            Connection navDBConnection = null;
            try {
                navDBConnection = navDbDataSource.getConnection();
                RouteFinder routeFinder = new RouteFinder(navDBConnection);

                routeCacheVO = getRouteCache(depAd, routeString, destAd,null);

                if (routeCacheVO == null) {
                    routeCacheVO = new RouteCacheVO();
                    String routeWKT = getSurveillanceRouteWKT(routeString, airspceWKT, routeFinder);

                    if (StringUtils.isNotBlank(routeWKT)) {
                        Geometry geometry = GeometryUtil.convertStringToGeometry(routeWKT);
                        if (geometry != null) {

                            routeCacheVO.setRoute(geometry);

                            // calculate route segments
                            List<RouteSegmentVO> routeSegments = getRouteSegmentsByRouteParser(segmentType, airspaceRP,
                                    routeFinder);
                            routeCacheVO.setRouteSegmentList(routeSegments);

                            // calculate distance from route segment collocation length
                            if (routeSegments != null && !routeSegments.isEmpty()) {
                                LOG.debug("There are {} segments", routeSegments.size());
                                Double distance = 0.0d;
                                for(RouteSegmentVO routeSegment : routeSegments) {
                                    LOG.debug("routeSegment {} length {}", routeSegment.getSegmentStartLabel(), routeSegment.getSegmentLength());
                                    distance += routeSegment.getSegmentLength();
                                }
                                //round total distance
                                routeCacheVO.setDistance(roundTotalDistance(distance));
                            }
                            updateCache(routeCacheVO, depAd, routeString, destAd, null, null,null);
                        }
                    }
                }
            } catch (SQLException se) {
                LOG.error(NAVDB_ERROR, se);
            } finally {
                if (navDBConnection != null) {
                    try {
                        navDBConnection.close();
                    } catch (SQLException e) {
                        LOG.error(NAVDB_ERROR_CLOSING_CONNECTION);
                    }
                }
            }
        } else {
            LOG.error("Error routeWKT {} or SegmentType{} are not valid ", routeString, segmentType);
        }

        return routeCacheVO;
    }
    /**
     * This method return the following information:
     * <ul>
     * <li>geographical path of the route in WKT format;</li>
     * <li>distance in km;</li>
     * <li>route Segments;</li>
     * </ul>
     * <p>
     * It comes Not from a flight plan, But from Radar Summary in Leonardo format
     */
    RouteCacheVO getRouteInformationByLeonardoFile(RadarSummary rs) {
        RouteCacheVO routeCacheVO = null;

        if (rs != null) {

            // build route string
            String routeString = getRouteForRadarLeonardo(rs.getEntryCoordinate(), rs.getExitCoordinate());
            routeCacheVO = getRouteCache(rs.getEntryCoordinate(), routeString, rs.getExitCoordinate(),null);

            if (routeCacheVO == null) {
                routeCacheVO = new RouteCacheVO();
                Geometry geometry = GeometryUtils.linestringFromPoints(rs.getEntryCoordinate(),rs.getExitCoordinate());
                if (geometry != null) {
                    routeCacheVO.setRoute(geometry);
                    RouteSegmentVO routeSegment = new RouteSegmentVO();
                    routeSegment.setSegmentType(SegmentType.RADAR);
                    routeSegment.setSegmentStartLabel(rs.getFirEntryPoint());
                    routeSegment.setSegmentEndLabel(rs.getFirExitPoint());
                    routeSegment.setLocation(geometry);
                    routeSegment.setSegmentLength(routeCacheService.getSegmentLength(geometry.toText()));
                    routeSegment.setSegmentNumber(1);
                    // calculate route segments
                    List<RouteSegmentVO> routeSegments = new ArrayList<>();
                    routeSegments.add(routeSegment);
                    routeCacheVO.setRouteSegmentList(routeSegments);
                                                        
                    // calculate distance from route segment collocation length
                    if (!routeSegments.isEmpty()) {
                        LOG.debug("There are {} segments", routeSegments.size());
                        Double distance = 0.0d;
                        for(RouteSegmentVO routeS : routeSegments) {
                             LOG.debug("routeSegment {} length {}", routeS.getSegmentStartLabel(), routeS.getSegmentLength());
                             distance += routeS.getSegmentLength();
                        }
                        //round total distance
                        routeCacheVO.setDistance(roundTotalDistance(distance));
                    }
                    updateCache(routeCacheVO, rs.getFirEntryPoint(), routeString, rs.getFirExitPoint(), null, null,null);
                }
            }
        }
        return routeCacheVO;
    }
    
    /**
     * This method return the following information:
     * <ul>
     * <li>geographical path of the route in WKT format;</li>
     * <li>distance in km;</li>
     * <li>route Segments;</li>
     * </ul>
     * We use this method for FPL Object from Spatia.
     */
    RouteCacheVO getRouteInformationByRouteParser(
        final String departure, final String destination, final String route, final String cruisingSpeedOrMachNumber,
        final String elapsedTime, final SegmentType segmentType, final LinkedList<RPAirspaceVO> airspaceRP, final Double flightLevel
    ) throws FlightMovementBuilderException {
        RouteCacheVO routeCacheVO = null;

        if (StringUtils.isNotBlank(departure) && StringUtils.isNotBlank(destination)
                && StringUtils.isNotBlank(route)) {
            String[] destsAerdrome = new String[] { destination };
            Connection navDBConnection = null;
            try {
                navDBConnection = navDbDataSource.getConnection();
                RouteFinder routeFinder = new RouteFinder(navDBConnection);
                routeFinder.setNewFlightPlan(departure, route, destsAerdrome);
                Integer speed = null;
                if (org.apache.commons.lang.StringUtils.isNotBlank(cruisingSpeedOrMachNumber)) {
                    speed = RouteCacheUtility.convertSpeed(cruisingSpeedOrMachNumber);
                }
                Integer estimatedElapsed = null;
                if (org.apache.commons.lang.StringUtils.isNotBlank(elapsedTime)) {
                    estimatedElapsed = RouteCacheUtility.convertElapsedTime(elapsedTime);
                }
                boolean isCircular = routeFinder.isCircularRoute();
                if (isCircular) {
                    routeCacheVO = getRouteCache(departure, route, destination, speed, estimatedElapsed, flightLevel);
                } else {
                    routeCacheVO = getRouteCache(departure, route, destination, flightLevel);
                }

                if (routeCacheVO == null) {
                    routeCacheVO = new RouteCacheVO();
                    String routeWKT = getRouteWKTByRouteParser(departure, route, destsAerdrome,
                            cruisingSpeedOrMachNumber, elapsedTime, routeFinder);

                    if (StringUtils.isNotBlank(routeWKT)) {
                        Geometry geometry = GeometryUtil.convertStringToGeometry(routeWKT);
                        if (geometry != null) {
                            routeCacheVO.setRoute(geometry);

                            // calculate route segments
                            List<RouteSegmentVO> routeSegments = getRouteSegmentsByRouteParser(segmentType, airspaceRP,
                                    routeFinder);
                            
                            routeCacheVO.setRouteSegmentList(routeSegments);

                            // calculate distance
                            if (routeSegments != null && !routeSegments.isEmpty()) {
                                // calculate distance from route segment collocation length
                                LOG.debug("There are {} segments", routeSegments.size());
                                Double distance = 0.0d;
                                for(RouteSegmentVO routeSegment : routeSegments) {
                                    LOG.debug("routeSegment {} length {}", routeSegment.getSegmentStartLabel(), routeSegment.getSegmentLength());
                                    distance += routeSegment.getSegmentLength();
                                }
                                distance = roundTotalDistance(distance);
                                LOG.debug("Distance is {} ", distance);
                                routeCacheVO.setDistance(distance);
                            } else if (isCircular) {
                                // if the flight is circular we have to calculate the distance by speed and ett
                                // only for domestic aerodrome
                                if (estimatedElapsed != null && speed != null) {
                                    Boolean isDomestic = flightMovementAerodromeService.isAerodromeDomestic(departure, departure, true);
                                    if (isDomestic) {
                                        Double distance = RouteCacheUtility.getDistanceInKM(estimatedElapsed.doubleValue(), speed.doubleValue());
                                        distance = roundTotalDistance(distance);
                                        LOG.debug("Distance {} for speed  {} and time {}", distance, speed, estimatedElapsed);
                                        routeCacheVO.setDistance(distance);
                                    } else {
                                        LOG.debug("Circular flgiht: aerodrome is not domestic: distance will be null");
                                    }
                                } else {
                                    LOG.debug("Speed {} and/or Time {} are missing", speed, estimatedElapsed);
                                }
                            }

                            // update route cache
                            if (isCircular) {
                                updateCache(routeCacheVO, departure, route, destination, speed, estimatedElapsed,flightLevel);
                            } else {
                                updateCache(routeCacheVO, departure, route, destination, null, null,flightLevel);
                            }
                        }
                    }
                }
            } catch (SQLException se) {
                LOG.error(NAVDB_ERROR, se);
            } finally {
                if (navDBConnection != null) {
                    try {
                        navDBConnection.close();
                    } catch (SQLException e) {
                        LOG.error(NAVDB_ERROR_CLOSING_CONNECTION);
                    }
                }
            }
        } else {
            LOG.warn("Cannot parse route information due to '{}' : departure '{}', destination '{}', route '{}'",
                FlightMovementBuilderIssue.UNKNOWN_AERODROME_ROUTE, departure, destination, route);
            throw new FlightMovementBuilderException(FlightMovementBuilderIssue.UNKNOWN_AERODROME_ROUTE);
        }

        return routeCacheVO;
    }

    /**
     * This method return the following information:
     * <ul>
     * <li>geographical path of the route in WKT format;</li>
     *
     * </ul>
     * We use this method for FPL Object from Spatia.=
     */
    public RouteCacheVO getRouteWKTByRouteParser(final String departure, final String destination,
            final String route, final String cruisingSpeedOrMachNumber, final String elapsedTime)
            throws FlightMovementBuilderException {
        RouteCacheVO routeCacheVO = null;

        if (StringUtils.isNotBlank(departure) && StringUtils.isNotBlank(destination)
                && StringUtils.isNotBlank(route)) {
            String[] destsAerdrome = new String[] { destination };
            Connection navDBConnection = null;
            try {
                navDBConnection = navDbDataSource.getConnection();
                RouteFinder routeFinder = new RouteFinder(navDBConnection);
                routeFinder.setNewFlightPlan(departure, route, destsAerdrome);


                routeCacheVO = new RouteCacheVO();
                String routeWKT = getRouteWKTByRouteParser(departure, route, destsAerdrome,
                            cruisingSpeedOrMachNumber, elapsedTime, routeFinder);

                if (StringUtils.isNotBlank(routeWKT)) {
                    Geometry geometry = GeometryUtil.convertStringToGeometry(routeWKT);
                    if (geometry != null) {
                         routeCacheVO.setRoute(geometry);
                    }
                }
            } catch (SQLException se) {
                LOG.error(NAVDB_ERROR, se);
            } finally {
                if (navDBConnection != null) {
                    try {
                        navDBConnection.close();
                    } catch (SQLException e) {
                        LOG.error(NAVDB_ERROR_CLOSING_CONNECTION);
                    }
                }
            }
        } else {
            LOG.warn("Cannot parse route information due to '{}' : departure '{}', destination '{}', route '{}'",
                FlightMovementBuilderIssue.UNKNOWN_AERODROME_ROUTE, departure, destination, route);
            throw new FlightMovementBuilderException(FlightMovementBuilderIssue.UNKNOWN_AERODROME_ROUTE);
        }

        return routeCacheVO;
    }

    public RouteParserParameters getRouteParserParameters() {
        return routeParserParameters;
    }

    @SuppressWarnings("unused")
    private Double getDistanceByRouteParser(String lineWKT, LinkedList<RPAirspaceVO> rpAirspaceVOList,
            RouteFinder aRouteFinder) {
        Double distance = 0.0;
        if (StringUtils.isNotBlank(lineWKT) && (rpAirspaceVOList != null && !rpAirspaceVOList.isEmpty())) {
            Connection navDBConnection = null;
            try {
                navDBConnection = navDbDataSource.getConnection();
                Tools tools = new Tools(navDBConnection);
                LinkedList<RPAirspace> airspaces = getRpAirspcaeForRouteParser(rpAirspaceVOList);
                distance = tools.getLinePolygonIntersectionLengthInKM(lineWKT, airspaces);

                LinkedList<String> listOfErrorMessages = aRouteFinder.getListOfErrorMessages();
                if (!(listOfErrorMessages == null || listOfErrorMessages.isEmpty())) {
                    for (String error : listOfErrorMessages) {
                        LOG.error(error);
                    }
                }
            } catch (SQLException se) {
                LOG.error(NAVDB_ERROR, se);
            } catch (Exception e) {
                LOG.error(FlightMovementBuilderIssue.ROUTE_PARSER_ERROR
                        + ". Error on calculate distance routeString: {} ", lineWKT);
            } finally {
                if (navDBConnection != null) {
                    try {
                        navDBConnection.close();
                    } catch (SQLException e) {
                        LOG.error(NAVDB_ERROR_CLOSING_CONNECTION);
                    }
                }
            }
        }
        return distance;
    }

    @SuppressWarnings("unused")
    private Double getIntersectionLengthInKM(RouteFinder routeFinder) {
        Double distance = 0.0;
        Connection navDBConnection = null;
        try {
            navDBConnection = navDbDataSource.getConnection();
            RouteAirspaceCollocation routeAirspaceCollocation = new RouteAirspaceCollocation(navDBConnection);
            distance = routeAirspaceCollocation.getIntersectionLengthInKM();

            LinkedList<String> listOfErrorMessages = routeFinder.getListOfErrorMessages();
            if (!(listOfErrorMessages == null || listOfErrorMessages.isEmpty())) {
                for (String error : listOfErrorMessages) {
                    LOG.error(error);
                }
            }
        } catch (SQLException se) {
            LOG.error(NAVDB_ERROR, se);
        } catch (Exception e) {
            LOG.error(FlightMovementBuilderIssue.ROUTE_PARSER_ERROR.toValue());
        } finally {
            if (navDBConnection != null) {
                try {
                    navDBConnection.close();
                } catch (SQLException e) {
                    LOG.error(NAVDB_ERROR_CLOSING_CONNECTION);
                }
            }
        }

        return distance;
    }

    private RouteCacheVO getRouteCache(String depAd, String route, String destAd, Double flightLevel) {
        RouteCacheVO routeCacheVO = null;
        String normalizeRoute = RouteCacheUtility.normalize(route);
        RouteCache routeCache = routeCacheService.findByKey(depAd, normalizeRoute, destAd, flightLevel);

        if (routeCache != null) {
            LOG.debug("RouteCache is found with ID {} for keystring {}, {}, {}, {}", routeCache.getId(), depAd,
                    normalizeRoute, destAd, flightLevel);
            byte[] obj = routeCache.getParsedRoute();
            routeCacheVO = (RouteCacheVO) SerializationUtils.deserialize(obj);
        }
        return routeCacheVO;
    }

    private RouteCacheVO getRouteCache(String depAd, String route, String destAd, Integer speed, Integer elaspedTime, Double flightLevel) {
        RouteCacheVO routeCacheVO = null;
        String normalizeRoute = RouteCacheUtility.normalize(route);
        RouteCache routeCache;

        try {
            routeCache = routeCacheService.findByKey(depAd, normalizeRoute, destAd, speed, elaspedTime, flightLevel);
        } catch (NullPointerException e) {
            routeCache = null;
        }

        if (routeCache != null) {
            LOG.debug("RouteCache is found with ID {} for keystring {}, {}, {}, {}, {}, {}", routeCache.getId(), depAd,
                    normalizeRoute, destAd, speed, elaspedTime, flightLevel);
            byte[] obj = routeCache.getParsedRoute();
            routeCacheVO = (RouteCacheVO) SerializationUtils.deserialize(obj);
        }
        return routeCacheVO;
    }

    private List<RouteSegmentVO> getRouteSegmentsByRouteParser (SegmentType segmentType,
            LinkedList<RPAirspaceVO> rpAirspaceVOList, RouteFinder aRouteFinder) {
        List<RouteSegmentVO> routeSegmentList = new LinkedList<>();

        if (segmentType != null && rpAirspaceVOList != null && !rpAirspaceVOList.isEmpty()) {
            Connection navDBConnection = null;
            try {
                navDBConnection = navDbDataSource.getConnection();
                final ArrayList <RoutePoint> routePointList = aRouteFinder.getLastFoundRouteStructure();
                RouteAirspaceCollocation routeAirspaceCollocation = new RouteAirspaceCollocation(navDBConnection);
                LinkedList<RPAirspace> airspaces = getRpAirspcaeForRouteParser(rpAirspaceVOList);
                routeAirspaceCollocation.setAirspaces(airspaces);
                routeAirspaceCollocation.setRouteStructure(routePointList);

                if (LOG.isDebugEnabled() && routePointList != null && !routePointList.isEmpty()) {
                    LOG.debug ("Found {} route point(s):", routePointList.size());
                    int index = 1;
                    for (final RoutePoint routePoint: routePointList) {
                        LOG.debug ("  {}) {} ({}) type={} lat={} long={}", index, routePoint.designator, routePoint.getDisplayName(), routePoint.pointType, routePoint.getLatitude(), routePoint.getLongitude());
                        ++index;
                    }
                }

                final SystemConfiguration configuration = systemConfigurationService.getOneByItemName(SystemConfigurationItemName.ENTRY_EXIT_POINT_ROUNDING_DISTANCE);
                Double dbPointToleranceInMeters = null;
                if (configuration != null) {
                    if (configuration.getCurrentValue() != null) {
                        /* Convert the value from km in meters */
                        dbPointToleranceInMeters = Double.parseDouble(configuration.getCurrentValue()) * 1000.0D;
                    } else if (configuration.getDefaultValue() != null) {
                        /* Convert the value from km in meters */
                        dbPointToleranceInMeters = Double.parseDouble(configuration.getDefaultValue()) * 1000.0D;
                    }
                }
                Double minSegmentGap = systemConfigurationService.getDouble(SystemConfigurationItemName.MINIMUM_SEGMENT_GAP, null);
                boolean allowNamedEndPointsOnly = systemConfigurationService.getString(SystemConfigurationItemName.ENTRY_EXIT_POINT_ROUNDING_LOCATIONS).equals(NAVDB_ROUNDING);

                // minimum segment gap is entered in km, but route parser is using the value in meters
                if(minSegmentGap != null)
                    minSegmentGap = minSegmentGap * 1000.0;

                RouteAirspaceCollocationType collocationType = routeAirspaceCollocation
                    .resolveCollocation(dbPointToleranceInMeters, dbPointToleranceInMeters, FPL_PNT_VS_DB_PNT_PRIORITY, null,minSegmentGap,allowNamedEndPointsOnly);

                if (collocationType.ordinal() > RouteAirspaceCollocationType.DISJOINT.ordinal()) {
                    LinkedList<com.ubitech.aim.routeparser.RouteSegment> routeSegments = routeAirspaceCollocation
                            .getRouteSegments();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug ("Found {} route segment(s):", routeSegments.size());
                        int index = 1;
                        for (final com.ubitech.aim.routeparser.RouteSegment routeSegment: routeSegments) {
                            LOG.debug ("  {}) {} - {} - {} km", index, routeSegment.getStartRoutePoint().getDisplayName(), routeSegment.getEndRoutePoint().getDisplayName(), routeSegment.getLengthInKM());
                            ++index;
                        }
                    }

                    int i = 0;
                    for (com.ubitech.aim.routeparser.RouteSegment routeSegment : routeSegments) {
                        i++;
                        RouteSegmentVO routeSegmentABMS = new RouteSegmentVO();
                       // round segment length
                        routeSegmentABMS.setSegmentLength(roundSegmentLength(routeSegment.getLengthInKM()));

                        routeSegmentABMS.setSegmentNumber(i);
                        if (routeSegment.getStartRoutePoint() != null) {

                            String startLabel = routeSegment.getStartRoutePoint().designator;

                            if (startLabel == null || startLabel.equals(ApplicationConstants.PLACEHOLDER_ZZZZ)) {
                                startLabel = routeSegment.getStartRoutePoint().getDisplayName();
                            }

                            routeSegmentABMS.setSegmentStartLabel(startLabel);
                        }
                        if (routeSegment.getEndRoutePoint() != null) {

                            String endLabel = routeSegment.getEndRoutePoint().designator;

                            if (endLabel == null || endLabel.equals(ApplicationConstants.PLACEHOLDER_ZZZZ)) {
                                endLabel = routeSegment.getEndRoutePoint().getDisplayName();
                            }

                            routeSegmentABMS.setSegmentEndLabel(endLabel);
                        }
                        routeSegmentABMS.setSegmentType(segmentType);
                        routeSegmentABMS.setSegmentType(segmentType);
                        Geometry geometry = GeometryUtil.convertStringToGeometry(routeSegment.getWKT());
                        routeSegmentABMS.setLocation(geometry);
                        routeSegmentList.add(routeSegmentABMS);
                    }

                } else {
                    LOG.info("Route and Airspace(s) do Not intersect");
                }

                LinkedList<String> listOfErrorMessages = aRouteFinder.getListOfErrorMessages();
                if (!(listOfErrorMessages == null || listOfErrorMessages.isEmpty())) {
                    for (String error : listOfErrorMessages) {
                        LOG.error(error);
                    }
                }
            } catch (SQLException se) {
                LOG.error(NAVDB_ERROR, se);
            } catch (Exception e) {
                LOG.error(FlightMovementBuilderIssue.ROUTE_PARSER_ERROR
                        + ", Error on calculate distance:  route segment - SegmentType: {} ", segmentType);
            } finally {
                if (navDBConnection != null) {
                    try {
                        navDBConnection.close();
                    } catch (SQLException e) {
                        LOG.error(NAVDB_ERROR_CLOSING_CONNECTION);
                    }
                }
            }
        } else {
            LOG.info("Airspace is NULL ");
        }

        return routeSegmentList;
    }

    private String getRouteWKTByRouteParser(String depAd, String route, String[] destAd,
            String cruisingSpeedOrMachNumber, String elapsedTime, RouteFinder aRouteFinder) {
        LOG.debug("Start: Get getRouteWKT By RouteFinder depAd {}, route {}, destAd{} ", depAd, route, destAd);
        String routeWKT = null;
        List<String> listOfRoutesForEachDestination = null;

        if (StringUtils.isNotBlank(depAd) && StringUtils.isNotBlank(route)
                && (destAd != null && destAd.length > 0)) {

            try {
                if (aRouteFinder.isCircularRoute()) {
                    if (elapsedTime != null && cruisingSpeedOrMachNumber != null) {
                        listOfRoutesForEachDestination = aRouteFinder.getCircularRouteLineWKT(cruisingSpeedOrMachNumber,
                                elapsedTime, routeParserParameters.getRouteLengthMaxToleranceCoeff(),
                                routeParserParameters.getMaxRouteSegmentLength());
                    } else {
                        LOG.debug("One or more value are null - elapsedTime {} cruisingSpeedOrMachNumber {}", elapsedTime, cruisingSpeedOrMachNumber);
                    }
                } else {
                    listOfRoutesForEachDestination = aRouteFinder.getRouteLineWKT();
                }

                LinkedList<String> listOfErrorMessages = aRouteFinder.getListOfErrorMessages();
                if (!(listOfErrorMessages == null || listOfErrorMessages.isEmpty())) {
                    for (String error : listOfErrorMessages) {
                        LOG.error(error);
                    }
                }
            } catch (Exception e) {
                LOG.error("'{}' : depAd '{}', route '{}', destAd '{}' ", FlightMovementBuilderIssue.ROUTE_PARSER_ERROR,
                        depAd, route, destAd[0]);
                LOG.debug("", e);
            }

            if (listOfRoutesForEachDestination != null && !listOfRoutesForEachDestination.isEmpty()) {
                routeWKT = listOfRoutesForEachDestination.get(0);
            }
        }
        LOG.debug("End : Get getRouteWKT By RouteFinder");
        return routeWKT;
    }

    /* Helper Method */
    private LinkedList<RPAirspace> getRpAirspcaeForRouteParser(LinkedList<RPAirspaceVO> rpAirspaceVOS) {
        LinkedList<RPAirspace> rpAirspaces = null;
        if (rpAirspaceVOS != null && !rpAirspaceVOS.isEmpty()) {
            rpAirspaces = new LinkedList<>();
            for (RPAirspaceVO rpAirspaceVO : rpAirspaceVOS) {
                RPAirspace rpAirspace = new RPAirspace(rpAirspaceVO.getAirspceWkt(), rpAirspaceVO.getRPAirspaceType());
                rpAirspaces.add(rpAirspace);
            }
        } else {
            LOG.error("Billable airspace has NOT been configured");
        }
        return rpAirspaces;
    }

    private String getSurveillanceRouteWKT(String routeString, LinkedList<String> airspacesWKTs,
            RouteFinder aRouteFinder) {
        LOG.debug("Start: GetSurveillanceRouteWKT By Route Parser");
        String routeWKT = null;

        if (StringUtils.isNotBlank(routeString) && airspacesWKTs != null && !airspacesWKTs.isEmpty()) {

            try {
                routeWKT = aRouteFinder.getSurveillanceRouteWKT(routeString, airspacesWKTs, BUFFER_RADIUS_METERS);

                LinkedList<String> listOfErrorMessages = aRouteFinder.getListOfErrorMessages();
                if (!(listOfErrorMessages == null || listOfErrorMessages.isEmpty())) {
                    for (String error : listOfErrorMessages) {
                        LOG.error(error);
                    }
                }
            } catch (Exception e) {
                LOG.error("{} : GetSurveillanceRouteWKT routeString '{}'",
                    FlightMovementBuilderIssue.ROUTE_PARSER_ERROR, routeString, e);
            }

        }
        LOG.debug("End : Get RouteGeometry By RouteFinder");
        return routeWKT;
    }

    private void updateCache(RouteCacheVO aRouteCacheVO, String depAd, String route, String destAd,
            Integer cruisingSpeedOrMachNumber, Integer elapsedTime, Double flightLevel) {
        if (depAd != null && destAd != null && route != null && aRouteCacheVO != null) {
            final RouteCache routeCache = new RouteCache();
            routeCache.setDepartureAerodrome(depAd);
            String normalizeRoute = RouteCacheUtility.normalize(route);
            routeCache.setRouteText(normalizeRoute);
            routeCache.setDestinationAerodrome(destAd);
            routeCache.setSpeed(cruisingSpeedOrMachNumber);
            routeCache.setEstimatedElapsed(elapsedTime);
            routeCache.setFlightLevel(flightLevel);
            byte[] obj = SerializationUtils.serialize(aRouteCacheVO);
            routeCache.setParsedRoute(obj);
            try {
                routeCacheService.save(routeCache);
            } catch (Exception e) {
                LOG.error("Error saving routeCache because {}: depAd '{}', route '{}', destAd '{}', " +
                        "cruisingSpeedOrMachNumber '{}', elapsedTime '{}', flightLevel '{}'",
                    e.getLocalizedMessage(), depAd, normalizeRoute, destAd, cruisingSpeedOrMachNumber, elapsedTime, flightLevel);
                LOG.debug("", e);
            }
        } else {
            LOG.warn("Cannot save the routeCache object as necessary information is missing : depAd '{}', route '{}', " +
                    "destAd '{}', cruisingSpeedOrMachNumber '{}', elapsedTime '{}', flightLevel '{}'",
                depAd, route, destAd, cruisingSpeedOrMachNumber, elapsedTime, flightLevel);
        }
    }

    /**
     * Round flight leg length
     */
    private Double roundSegmentLength(Double length) {
    	if (length == null) return null;

    	int decimalPlaces = systemConfigurationService.getIntOrZero(SystemConfigurationItemName.FLIGHT_LEG_DECIMAL_PLACES);
        return Calculation.truncate(length, decimalPlaces);
    }

    /**
     * Round total flight distance
     */
    Double roundTotalDistance(Double length) {
    	if (length == null) return null;

    	int decimalPlaces = systemConfigurationService.getIntOrZero(SystemConfigurationItemName.FLIGHT_TOTAL_DECIMAL_PLACES);
        return Calculation.truncate(length, decimalPlaces);
    }
    
    private String getRouteForRadarLeonardo(String entry, String exit) {
        if (StringUtils.isNotBlank (entry) && StringUtils.isNotBlank (exit)) {
            return entry + " DCT " + exit;
        }
        return null;
    }
}
