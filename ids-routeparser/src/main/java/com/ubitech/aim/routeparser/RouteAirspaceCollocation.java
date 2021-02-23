package com.ubitech.aim.routeparser;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RouteAirspaceCollocation {

    private static final double MAX_INTERSECTION_ACCURACY_IN_METERS = 2500.0d;
    static final double DEFAULT_INTERSECTION_ACCURACY_IN_METERS = MAX_INTERSECTION_ACCURACY_IN_METERS;

    private static final double MAX_LINEAR_GAP_IN_METERS = 7000.0d;
    private static final double DEFAULT_LINEAR_GAP_IN_METERS = MAX_LINEAR_GAP_IN_METERS;

    private static final double MAX_FPLPOINT_TOLERANCE_IN_METERS = 100000.0d;
    private static final double DEFAULT_FPLPOINT_TOLERANCE_IN_METERS = 30000.0d;

    private static final double MAX_DBPOINT_TOLERANCE_IN_METERS = 100000.0d;
    private static final double DEFAULT_DBPOINT_TOLERANCE_IN_METERS = 30000.0d;

    private static final LinkedList<RoutePointType> DEFAULT_DBPOINT_TYPES = new LinkedList<RoutePointType>(
            Arrays.asList(RoutePointType.Waypoint, RoutePointType.VOR, RoutePointType.TACAN, RoutePointType.NDB));

    private double intersectionPntAccuracyInMeters = DEFAULT_INTERSECTION_ACCURACY_IN_METERS;
    private double mergedLinearGapInMeters = DEFAULT_LINEAR_GAP_IN_METERS;

    private double fplPointToleranceInMeters = DEFAULT_FPLPOINT_TOLERANCE_IN_METERS;
    private double dbPointToleranceInMeters = DEFAULT_DBPOINT_TOLERANCE_IN_METERS;
    private LinkedList<RoutePointType> dbPointTypes = DEFAULT_DBPOINT_TYPES;
    
    private boolean allowNamedEndPointsOnly = false;

    private Connection connection = null;
    private Timestamp timeStamp = null;

    private RouteAirspaceCollocationType collocationType = RouteAirspaceCollocationType.UNDETERMINED;

    private boolean fplPntVsDbPntPriority = true;

    private ArrayList<RoutePoint> routeStructure = null;
    private String routeWKT = null;
    private String intersectedRoutePortionWKT = null;
    private LineString routeLineString = null;
    private LineString tempRteLineString_1 = null;
    private LineString tempRteLineString_2 = null;

    private Boolean isCircularRoute = null;
    private Boolean toMiddlePointAndBackSameWay = null;

    private LinkedList<RPAirspace> inputRPAirspaces = new LinkedList<RPAirspace>();
    private LinkedList<RPAirspace> inputNonOverlappingRPAirspaces = new LinkedList<RPAirspace>();

    private LinkedList<String> intersectedAirspaceWKTs = null;

    private LinkedList<RoutePortion> intersectedRoutePortions = new LinkedList<RoutePortion>();
    private LinkedList<RouteSegment> intersectedRouteSegments = new LinkedList<RouteSegment>();
    private Double intersectionTotalLengthInKM = null;
    private DataRetriver dataRetriver = null;
    private Tools tools;

    private int numOfRoutePoints = 0;
    private int numOfIntersectedAirspaces = 0;
    private int numOfIntersectedRoutePortions = 0;

    public RouteAirspaceCollocation(Connection connection) throws SQLException {
        this.connection = connection;
        this.timeStamp = new Timestamp(new java.util.Date().getTime());
        this.tools = new Tools(connection);
        this.dataRetriver = new AeroDbDataRetriever(connection);
    }

    private void clearParameters() {
        if (this.intersectedAirspaceWKTs != null)
            this.intersectedAirspaceWKTs.clear();
        this.numOfIntersectedAirspaces = 0;
        this.numOfIntersectedRoutePortions = 0;
        this.intersectedRoutePortions.clear();
        this.intersectedRouteSegments.clear();
        this.intersectedRoutePortionWKT = null;
        this.intersectionTotalLengthInKM = null;
        this.collocationType = RouteAirspaceCollocationType.UNDETERMINED;
    }

    public RouteAirspaceCollocationType resolveCollocation(Double fplPointTolerance_InMeters,
            Double dbPointTolerance_InMeters, Boolean fplPntVsDbPntPriority, List<RoutePointType> usedDbPointTypes, Double mergedLinearGapInMeters,
            boolean allowNamedEndPointsOnly)
            throws SQLException {
        
        if (mergedLinearGapInMeters != null) {
            if(mergedLinearGapInMeters < 0) 
                throw new IllegalArgumentException("Merged Linear Gap parameter cannot be Negative.");

            if (mergedLinearGapInMeters > MAX_LINEAR_GAP_IN_METERS)
                throw new IllegalArgumentException("Merged Linear Gap parameter cannot be greater than "
                        + String.valueOf(MAX_LINEAR_GAP_IN_METERS) + " meters.");

            this. mergedLinearGapInMeters = mergedLinearGapInMeters;
            intersectionPntAccuracyInMeters = mergedLinearGapInMeters/2.8;
            this.allowNamedEndPointsOnly = allowNamedEndPointsOnly;
        }
        
        return resolveCollocation(fplPointTolerance_InMeters,
                 dbPointTolerance_InMeters, fplPntVsDbPntPriority,  usedDbPointTypes);
    }

    public RouteAirspaceCollocationType resolveCollocation(Double fplPointTolerance_InMeters,
            Double dbPointTolerance_InMeters, Boolean fplPntVsDbPntPriority, List<RoutePointType> usedDbPointTypes)
            throws SQLException {

        if (fplPointTolerance_InMeters != null) {
            if (fplPointTolerance_InMeters < 0)
                throw new IllegalArgumentException("FPL Point Tolerance parameter cannot be Negative.");

            if (fplPointTolerance_InMeters > MAX_FPLPOINT_TOLERANCE_IN_METERS)
                throw new IllegalArgumentException("FPL Point Tolerance parameter cannot be greater than "
                        + String.valueOf(MAX_FPLPOINT_TOLERANCE_IN_METERS) + " meters.");

            fplPointToleranceInMeters = fplPointTolerance_InMeters.doubleValue();
        }

        if (dbPointTolerance_InMeters != null) {
            if (dbPointTolerance_InMeters < 0)
                throw new IllegalArgumentException("Database Point Tolerance parameter cannot be Negative.");

            if (dbPointTolerance_InMeters > MAX_DBPOINT_TOLERANCE_IN_METERS)
                throw new IllegalArgumentException("Database Point Tolerance parameter cannot be be greater than "
                        + String.valueOf(MAX_DBPOINT_TOLERANCE_IN_METERS) + " meters.");

            dbPointToleranceInMeters = dbPointTolerance_InMeters.doubleValue();
        }

        if (fplPntVsDbPntPriority != null)
            this.fplPntVsDbPntPriority = fplPntVsDbPntPriority.booleanValue();

        if (usedDbPointTypes != null) {
            dbPointTypes.clear();
            dbPointTypes.addAll(usedDbPointTypes);
        }

        return resolveCollocation();
    }

    public RouteAirspaceCollocationType resolveCollocation() throws SQLException {

        this.collocationType = RouteAirspaceCollocationType.UNDETERMINED;
        if (intersectedRoutePortions != null)
            intersectedRoutePortions.clear();
        if (intersectedRouteSegments != null)
            intersectedRouteSegments.clear();
        if (intersectedAirspaceWKTs != null)
            intersectedAirspaceWKTs.clear();

        if (routeStructure == null || routeStructure.isEmpty() || inputRPAirspaces == null
                || inputRPAirspaces.isEmpty()) {

            intersectionTotalLengthInKM = null;
            return this.collocationType;
        }

        isCircularRoute = null;
        toMiddlePointAndBackSameWay = null;

        int numOfSameReturnPoints = routeLineString.getNumOfSameBeginningEndPoints();
        int rteLine1EndPointIndex = routeLineString.getNumOfPoints() - 1;
        int loopStartPointIndex = -1;

        if (numOfSameReturnPoints == 0) {

            isCircularRoute = new Boolean(false);
            tempRteLineString_1 = routeLineString;
        } else {
            isCircularRoute = new Boolean(true);

            toMiddlePointAndBackSameWay = ((numOfSameReturnPoints * 2 + 1) == numOfRoutePoints) ? new Boolean(true)
                    : new Boolean(false);

            if (numOfSameReturnPoints == 1) {
                if (numOfRoutePoints == 2)
                    throw new IllegalArgumentException(
                            "Route consists of only two points having the same location/coordinates. Since Route has Zero Length, it cannot be processed.");
                rteLine1EndPointIndex = numOfRoutePoints - 1;
                loopStartPointIndex = 0;
                tempRteLineString_1 = routeLineString;
            } else if (toMiddlePointAndBackSameWay) {
                rteLine1EndPointIndex = numOfSameReturnPoints;
                ArrayList<LineString> tempListOfLines = routeLineString.getPartsSplitedByIndex(rteLine1EndPointIndex);
                tempRteLineString_1 = tempListOfLines.get(0);
            } else {
                if ((numOfSameReturnPoints * 2) == numOfRoutePoints)
                    throw new IllegalArgumentException(
                            "Route consists of even number of points (uneven number of segments) and the midmost segment has Zero Length. Such a route cannot be processed.");

                rteLine1EndPointIndex = numOfRoutePoints - numOfSameReturnPoints;
                loopStartPointIndex = numOfSameReturnPoints - 1;
                ArrayList<LineString> tempListOfLines = routeLineString.getPartsSplitedByIndex(rteLine1EndPointIndex);
                tempRteLineString_1 = tempListOfLines.get(0);
                tempRteLineString_2 = tempListOfLines.get(1);
            }
        }

        Point routeBox2dCenterPoint = tempRteLineString_1.getCenterPoint();

        ArrayList<LinkedList<String>> arrayList = getIntersectedWKTs(tempRteLineString_1.getWKT());
        intersectedAirspaceWKTs = arrayList.get(0);
        LinkedList<String> intersectedRoutePortionWKTs = arrayList.get(1);
        arrayList = null;
        numOfIntersectedAirspaces = intersectedAirspaceWKTs.size();
        numOfIntersectedRoutePortions = intersectedRoutePortionWKTs.size();

        if (numOfIntersectedAirspaces == 0) {
            this.collocationType = RouteAirspaceCollocationType.DISJOINT;

            intersectionTotalLengthInKM = null;
            return this.collocationType;
        }

        boolean startPointInAirspaceFlag = false;
        boolean firstLineEndPointInAirspaceFlag = false;
        boolean endPointInAirspaceFlag = false;

        for (String airspaceWKT : intersectedAirspaceWKTs) {

            if (!startPointInAirspaceFlag && tools.doIntersect(routeStructure.get(0).getWKT(), airspaceWKT))
                startPointInAirspaceFlag = true;

            if (!firstLineEndPointInAirspaceFlag
                    && tools.doIntersect(routeStructure.get(rteLine1EndPointIndex).getWKT(), airspaceWKT))
                firstLineEndPointInAirspaceFlag = true;
        }

        if (isCircularRoute)
            endPointInAirspaceFlag = startPointInAirspaceFlag;
        else
            endPointInAirspaceFlag = firstLineEndPointInAirspaceFlag;

        if (startPointInAirspaceFlag) {
            if (endPointInAirspaceFlag)
                this.collocationType = RouteAirspaceCollocationType.DOMESTIC;

            else
                this.collocationType = RouteAirspaceCollocationType.DEPARTURE;
        } else {
            if (endPointInAirspaceFlag)
                this.collocationType = RouteAirspaceCollocationType.ARRIVAL;

            else
                this.collocationType = RouteAirspaceCollocationType.OVERFLIGHT;
        }

        if (numOfIntersectedRoutePortions == 0) {
            intersectionTotalLengthInKM = new Double(0.0);
            return this.collocationType;
        }

        MultiLineString intersectionMultiline = new MultiLineString();

        for (String subRouteWKT : intersectedRoutePortionWKTs) {
            Geometry tempGeom = Geometry.geometryFromWKT(subRouteWKT, Geometry.GeogSRID);
            intersectionMultiline.addLineGeometry(tempGeom, true);
        }
        intersectedRoutePortionWKTs.clear();
        intersectedRoutePortionWKTs = null;

        numOfIntersectedRoutePortions = intersectionMultiline.getNumOfLines();

        if (numOfIntersectedRoutePortions == 0) {
            intersectionTotalLengthInKM = new Double(0.0);
            return this.collocationType;
        }

        if (this.collocationType.equals(RouteAirspaceCollocationType.DOMESTIC) && numOfIntersectedAirspaces == 1
                && numOfIntersectedRoutePortions == 1) {

            if (tools.doCover(intersectedAirspaceWKTs.getFirst(), this.routeWKT,
                    tools.getSRID(routeBox2dCenterPoint))) {

                RoutePortion tempRoutePortion = new RoutePortion(routeStructure);

                intersectedRouteSegments.addAll(tempRoutePortion.getRouteSegments());

                double lengthInMeters = 0.0;
                for (RouteSegment routeSegment : intersectedRouteSegments) {
                    routeSegment.lengthInMeters = tools.getLengthInMeters(routeSegment.getWKT());
                    lengthInMeters = lengthInMeters + routeSegment.lengthInMeters.doubleValue();
                }

                tempRoutePortion.lengthInMeters = new Double(lengthInMeters);

                intersectionTotalLengthInKM = new Double(lengthInMeters * 0.001d);

                intersectedRoutePortions.add(tempRoutePortion);
                intersectedRoutePortionWKT = tempRoutePortion.getRouteWKT();

                return this.collocationType;
            }
        }

        Point startPoint = null;
        Point endPoint = null;

        startPoint = tempRteLineString_1.getFirstPoint();
        endPoint = tempRteLineString_1.getLastPoint();

        intersectionMultiline.tryConnectAndMergeSubLines(startPoint, endPoint, intersectionPntAccuracyInMeters,
                mergedLinearGapInMeters);

        /*
         * if (intersectionMultiline.firstLineConnectedByStart) { if (isCircularRoute ||
         * intersectionMultiline.lastLineConnectedByEnd) this.collocationType =
         * RouteAirspaceCollocationType.DOMESTIC; else this.collocationType =
         * RouteAirspaceCollocationType.DEPARTURE; } else { if (isCircularRoute ||
         * !intersectionMultiline.lastLineConnectedByEnd) this.collocationType =
         * RouteAirspaceCollocationType.OVERFLIGHT; else this.collocationType =
         * RouteAirspaceCollocationType.ARRIVAL; }
         */
        if (!(isCircularRoute && loopStartPointIndex == 0)
                || (toMiddlePointAndBackSameWay != null && toMiddlePointAndBackSameWay))
            intersectionMultiline.sortSubLinesGeographically(tempRteLineString_1);

        MultiRoutePortion intersectionMultiRoutePortion = new MultiRoutePortion(intersectionMultiline);

        intersectionMultiRoutePortion.instantiateRoutePoints(new RoutePortion(tempRteLineString_1),
                (!isCircularRoute || (toMiddlePointAndBackSameWay != null && toMiddlePointAndBackSameWay)),
                loopStartPointIndex, intersectionPntAccuracyInMeters);

        if (fplPointToleranceInMeters > intersectionPntAccuracyInMeters || dbPointToleranceInMeters > 0) {
            if (dataRetriver == null)
                dataRetriver = new AeroDbDataRetriever(connection);

            intersectionMultiRoutePortion.instantiateEndingRoutePoints(new RoutePortion(tempRteLineString_1),
                    fplPointToleranceInMeters, dbPointToleranceInMeters, fplPntVsDbPntPriority, dbPointTypes,
                    dataRetriver, timeStamp,allowNamedEndPointsOnly);
        }

        if (this.isCircularRoute) {
            MultiRoutePortion intersectionMultiRoutePortion_2;

            if (toMiddlePointAndBackSameWay != null && toMiddlePointAndBackSameWay) {
                intersectionMultiRoutePortion_2 = new MultiRoutePortion(intersectionMultiRoutePortion);
                intersectionMultiRoutePortion_2.reverse();

                boolean connectionFlag = intersectionMultiRoutePortion.lastLineConnectedByEnd;

                intersectionMultiRoutePortion.addMultiLineString(intersectionMultiRoutePortion_2);

                if (connectionFlag)
                    intersectionMultiRoutePortion.combineConcatenatedSubLines();

                intersectionTotalLengthInKM = new Double(0.0);
                for (LineString lineString : intersectionMultiRoutePortion.lineStrings) {
                    lineString.lengthInMeters = tools.getLengthInMeters(lineString.getWKT());
                    intersectionTotalLengthInKM = new Double(
                            intersectionTotalLengthInKM.doubleValue() + lineString.lengthInMeters.doubleValue());
                }
                intersectionTotalLengthInKM = new Double(intersectionTotalLengthInKM.doubleValue() * 0.001);

                intersectionMultiRoutePortion_2.close();
                intersectionMultiRoutePortion_2 = null;
            }

            else if (loopStartPointIndex > 0) {
                LinkedList<String> intersectedAirspaceWKTs_2 = null;

                arrayList = getIntersectedWKTs(tempRteLineString_2.getWKT());
                intersectedAirspaceWKTs_2 = arrayList.get(0);
                intersectedRoutePortionWKTs = arrayList.get(1);
                arrayList = null;
                int numOfIntersectedAirspaces_2 = intersectedAirspaceWKTs_2.size();
                int numOfIntersectedRoutePortions_2 = intersectedRoutePortionWKTs.size();

                if (numOfIntersectedAirspaces_2 == 0) {
                    intersectedRoutePortions = intersectionMultiRoutePortion.getRoutePortions();
                    intersectedRoutePortionWKT = intersectionMultiRoutePortion.getWKT();

                    LinkedList<RouteSegment> routeSegments;
                    double totalLengthInMeters = 0.0;
                    double routePortionLengthInMeters;

                    for (RoutePortion routePortion : intersectedRoutePortions) {
                        routePortionLengthInMeters = 0.0;
                        routeSegments = routePortion.getRouteSegments();
                        intersectedRouteSegments.addAll(routeSegments);

                        for (RouteSegment routeSegment : routeSegments) {
                            routeSegment.lengthInMeters = tools.getLengthInMeters(routeSegment.getWKT());
                            routePortionLengthInMeters = routePortionLengthInMeters
                                    + routeSegment.lengthInMeters.doubleValue();
                        }

                        routePortion.lengthInMeters = new Double(routePortionLengthInMeters);

                        totalLengthInMeters = totalLengthInMeters + routePortionLengthInMeters;
                    }

                    intersectionTotalLengthInKM = new Double(totalLengthInMeters * 0.001);

                    return this.collocationType;
                }

                MultiLineString intersectionMultiline_2 = new MultiLineString();

                for (String subRouteWKT : intersectedRoutePortionWKTs) {
                    Geometry tempGeom = Geometry.geometryFromWKT(subRouteWKT, Geometry.GeogSRID);
                    intersectionMultiline_2.addLineGeometry(tempGeom, true);
                }

                numOfIntersectedRoutePortions_2 = intersectionMultiline_2.getNumOfLines();

                if (numOfIntersectedRoutePortions_2 == 0) {
                    intersectedRoutePortions = intersectionMultiRoutePortion.getRoutePortions();
                    intersectedRoutePortionWKT = intersectionMultiRoutePortion.getWKT();

                    LinkedList<RouteSegment> routeSegments;
                    double totalLengthInMeters = 0.0;
                    double routePortionLengthInMeters;

                    for (RoutePortion routePortion : intersectedRoutePortions) {
                        routePortionLengthInMeters = 0.0;
                        routeSegments = routePortion.getRouteSegments();
                        intersectedRouteSegments.addAll(routeSegments);

                        for (RouteSegment routeSegment : routeSegments) {
                            routeSegment.lengthInMeters = tools.getLengthInMeters(routeSegment.getWKT());
                            routePortionLengthInMeters = routePortionLengthInMeters
                                    + routeSegment.lengthInMeters.doubleValue();
                        }

                        routePortion.lengthInMeters = new Double(routePortionLengthInMeters);

                        totalLengthInMeters = totalLengthInMeters + routePortionLengthInMeters;
                    }

                    intersectionTotalLengthInKM = new Double(totalLengthInMeters * 0.001);

                    return this.collocationType;
                }

                if (intersectionMultiline.lastLineConnectedByEnd)
                    startPoint = tempRteLineString_2.getFirstPoint();
                else
                    startPoint = null;
                if (intersectionMultiline.firstLineConnectedByStart)
                    endPoint = tempRteLineString_2.getLastPoint();
                else
                    endPoint = null;

                intersectionMultiline_2.tryConnectAndMergeSubLines(startPoint, endPoint,
                        intersectionPntAccuracyInMeters, mergedLinearGapInMeters);

                intersectionMultiline_2.sortSubLinesGeographically(tempRteLineString_2);

                intersectionMultiRoutePortion_2 = new MultiRoutePortion(intersectionMultiline_2);

                intersectionMultiRoutePortion_2.instantiateRoutePoints(new RoutePortion(tempRteLineString_2), true, -1,
                        intersectionPntAccuracyInMeters);

                if (fplPointToleranceInMeters > intersectionPntAccuracyInMeters || dbPointToleranceInMeters > 0) {
                    if (dataRetriver == null)
                        dataRetriver = new AeroDbDataRetriever(connection);

                    intersectionMultiRoutePortion_2.instantiateEndingRoutePoints(new RoutePortion(tempRteLineString_2),
                            fplPointToleranceInMeters, dbPointToleranceInMeters, fplPntVsDbPntPriority, dbPointTypes,
                            dataRetriver, timeStamp,allowNamedEndPointsOnly);
                }

                boolean connectionFlag = intersectionMultiRoutePortion.lastLineConnectedByEnd;

                intersectionMultiRoutePortion.addMultiLineString(intersectionMultiRoutePortion_2);

                if (connectionFlag)
                    intersectionMultiRoutePortion.combineConcatenatedSubLines();

                intersectionMultiRoutePortion_2.close();
                intersectionMultiRoutePortion_2 = null;
            }
        }

        intersectedRoutePortions = intersectionMultiRoutePortion.getRoutePortions();
        intersectedRoutePortionWKT = intersectionMultiRoutePortion.getWKT();

        LinkedList<RouteSegment> routeSegments;
        double totalLengthInMeters = 0.0;
        double routePortionLengthInMeters;

        for (RoutePortion routePortion : intersectedRoutePortions) {
            routePortionLengthInMeters = 0.0;
            routeSegments = routePortion.getRouteSegments();
            intersectedRouteSegments.addAll(routeSegments);

            for (RouteSegment routeSegment : routeSegments) {
                routeSegment.lengthInMeters = tools.getLengthInMeters(routeSegment.getWKT());
                routePortionLengthInMeters = routePortionLengthInMeters + routeSegment.lengthInMeters.doubleValue();
            }

            routePortion.lengthInMeters = new Double(routePortionLengthInMeters);

            totalLengthInMeters = totalLengthInMeters + routePortionLengthInMeters;
        }

        intersectionTotalLengthInKM = new Double(totalLengthInMeters * 0.001);

        return this.collocationType;
    }

    public void setRouteStructure(ArrayList<RoutePoint> routeStructure) {
        try {
            if (routeStructure == null || routeStructure.isEmpty() || routeStructure.size() < 2) {
                this.routeStructure = null;
                this.routeWKT = null;
                return;
            }

            this.numOfRoutePoints = routeStructure.size();

            this.routeStructure = new ArrayList<RoutePoint>(numOfRoutePoints);
            this.routeStructure.addAll(routeStructure);

            for (int i = 0; i < numOfRoutePoints; i++)
                this.routeStructure.get(i).parsedPointIndex = i;

            this.routeLineString = new LineString((List) this.routeStructure);
            this.routeWKT = this.routeLineString.getWKT();
            this.isCircularRoute = null;
            this.toMiddlePointAndBackSameWay = null;
        }

        finally {
            clearParameters();
        }
    }

    public void setAirspaces(RPAirspace rpAirspace) throws SQLException {

        if (rpAirspace == null)
            throw new NullPointerException(
                    "'RPAirspace rpAirspace' parameter of 'RouteAirspaceCollocation.setAirspaces' method cannot accept 'Null'.");

        LinkedList<RPAirspace> rpAirspaces = new LinkedList<RPAirspace>();
        rpAirspaces.add(rpAirspace);

        setAirspaces(rpAirspaces);
    }

    public void setAirspaces(LinkedList<RPAirspace> rpAirspaces) throws SQLException {

        try {
            this.inputRPAirspaces.clear();
            this.inputNonOverlappingRPAirspaces.clear();

            this.inputRPAirspaces.addAll(rpAirspaces);

            if (rpAirspaces.size() > 1) {

                LinkedList<RPAirspace> firList = new LinkedList<RPAirspace>(); // type = FIR
                LinkedList<RPAirspace> partialAirspaceList = new LinkedList<RPAirspace>(); // type = FIR_P or TMA

                for (RPAirspace rpAirspace : rpAirspaces) {
                    switch (rpAirspace.type) {
                    case FIR:
                        if (rpAirspace.polygons.size() > 1) {
                            for (Polygon polygon : rpAirspace.polygons) {
                                firList.add(new RPAirspace(polygon, rpAirspace.type));
                            }
                        } else {
                            firList.add(rpAirspace);
                        }
                        break;

                    case FIR_P:
                        // fall through
                    case TMA:
                        if (rpAirspace.polygons.size() > 1) {
                            for (Polygon polygon : rpAirspace.polygons) {
                                partialAirspaceList.add(new RPAirspace(polygon, rpAirspace.type));
                            }
                        } else {
                            partialAirspaceList.add(rpAirspace);
                        }
                        break;
                    }

                }

                if (partialAirspaceList.isEmpty()) {
                    this.inputNonOverlappingRPAirspaces.addAll(firList);
                } else if (firList.isEmpty()) {
                    this.inputNonOverlappingRPAirspaces.addAll(partialAirspaceList);
                } else {
                    this.inputNonOverlappingRPAirspaces.addAll(firList);

                    for (RPAirspace tmaRPAirspace : partialAirspaceList) {
                        boolean tmaToBeExcluded = false;

                        for (RPAirspace firRPAirspace : firList) {

                            Polygon tmaPolygon = tmaRPAirspace.polygons.getFirst();
                            Polygon firPolygon = firRPAirspace.polygons.getFirst();

                            if (tmaPolygon.isCoveredByBox(firPolygon, DEFAULT_INTERSECTION_ACCURACY_IN_METERS)) {

                                if (tools.doCover(firPolygon.getWKT(), tmaPolygon.getCenterPoint().getWKT())) {
                                    tmaToBeExcluded = true;
                                    break;
                                }
                            }
                        }

                        if (!tmaToBeExcluded)
                            this.inputNonOverlappingRPAirspaces.add(tmaRPAirspace);
                    }
                }
            }

            else if (rpAirspaces.getFirst().polygons.size() > 1) {
                for (Polygon polygon : rpAirspaces.getFirst().polygons) {
                    this.inputNonOverlappingRPAirspaces.add(new RPAirspace(polygon, rpAirspaces.getFirst().type));
                }
            }

            else
                this.inputNonOverlappingRPAirspaces.add(rpAirspaces.getFirst());
        }

        finally {
            clearParameters();
        }
    }

    public String getInputRouteWKT() {
        return this.routeWKT;
    }

    public String getIntersectedRoutePortionWKT() {
        return this.intersectedRoutePortionWKT;
    }

    public Double getIntersectionLengthInKM() {
        return this.intersectionTotalLengthInKM;
    }

    public Double getIntersectionLengthInNM() {
        return new Double(this.intersectionTotalLengthInKM.doubleValue() * 1000.0 * RouteFinder.METER_TO_NM);
    }

    public LinkedList<String> getIntersectedAirspaceWKTs() {
        return intersectedAirspaceWKTs;
    }

    public LinkedList<RoutePortion> getRoutePortions() {
        return intersectedRoutePortions;
    }

    public int getNumOfIntersections() {
        if (intersectedRoutePortions == null)
            return 0;
        return intersectedRoutePortions.size();
    }

    public LinkedList<RouteSegment> getRouteSegments() {
        return intersectedRouteSegments;
    }

    private ArrayList<LinkedList<String>> getIntersectedWKTs(String rteWKT) throws SQLException {

        ArrayList<LinkedList<String>> outputArrayList = new ArrayList<LinkedList<String>>(2);
        LinkedList<String> outputAirspaceWKTs = new LinkedList<String>();
        LinkedList<String> outputRoutePortionWKTs = new LinkedList<String>();

        LinkedList<String> nonOverlappingAirspaceWKTs = new LinkedList<String>();

        for (RPAirspace rpAirspace : inputNonOverlappingRPAirspaces)
            nonOverlappingAirspaceWKTs.add(rpAirspace.polygons.getFirst().getWKT());

        String intersectionWKT;

        final String highPrecRouteWkt = tools.segmentize(rteWKT);

        for (String airspaceWKT : nonOverlappingAirspaceWKTs) {
            final String highPrecAirspaceWkt = tools.segmentize(airspaceWKT);
            if (tools.doIntersect(highPrecRouteWkt, highPrecAirspaceWkt)) {
                outputAirspaceWKTs.add(airspaceWKT);

                intersectionWKT = tools.getGeoIntersection(highPrecRouteWkt, highPrecAirspaceWkt);
                intersectionWKT = tools.getCollectionExtract(intersectionWKT, 2);

                if (tools.isEmptyGeometry(intersectionWKT))
                    continue;

                else {
                    outputRoutePortionWKTs.add(intersectionWKT);
                }
            }
        }

        outputArrayList.add(outputAirspaceWKTs);
        outputArrayList.add(outputRoutePortionWKTs);
        return outputArrayList;
    }
}
