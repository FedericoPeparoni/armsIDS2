package com.ubitech.aim.routeparser;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class MultiRoutePortion extends MultiLineString {

    MultiRoutePortion(MultiLineString multiLineString) {
        lineStrings = multiLineString.lineStrings;
        firstLineConnectedByStart = multiLineString.firstLineConnectedByStart;
        lastLineConnectedByEnd = multiLineString.lastLineConnectedByEnd;
        WKT = multiLineString.WKT;
        SRID = multiLineString.SRID;
    }

    MultiRoutePortion(MultiRoutePortion multiRoutePortion) {
        this.lineStrings = new LinkedList<LineString>();
        setMultiRoutePortion(multiRoutePortion);
    }

    void setMultiRoutePortion(MultiRoutePortion multiRoutePortion) {
        if (lineStrings == null)
            lineStrings = new LinkedList<LineString>();
        else if (!lineStrings.isEmpty())
            lineStrings.clear();

        for (LineString lineString : multiRoutePortion.lineStrings) {
            lineStrings.add(new LineString(lineString));
        }

        WKT = multiRoutePortion.WKT;
        SRID = multiRoutePortion.SRID;
        firstLineConnectedByStart = multiRoutePortion.firstLineConnectedByStart;
        lastLineConnectedByEnd = multiRoutePortion.lastLineConnectedByEnd;
    }

    LinkedList<RoutePortion> getRoutePortions() {
        LinkedList<RoutePortion> outputMultiRoutePortion = new LinkedList<RoutePortion>();

        if (lineStrings == null || lineStrings.isEmpty())
            return outputMultiRoutePortion;

        for (LineString lineString : lineStrings) {
            if (lineString.points == null || lineString.points.isEmpty())
                continue;

            LineString outputLineString = new LineString();

            for (Point point : lineString.points) {
                if (point instanceof RoutePoint)
                    outputLineString.addPoint(point);
            }

            if (outputLineString.getNumOfPoints() > 0)
                outputMultiRoutePortion.add(new RoutePortion(outputLineString));
        }

        return outputMultiRoutePortion;
    }

    void instantiateRoutePoints(RoutePortion routePortion, boolean sortedSubLines, int loopStartPointIndex,
            Double intersectionAccuracyInMeters) {

        int numOfSubLines = getNumOfLines();

        ArrayList<RoutePoint> routePoints = routePortion.getRoutePoints();
        int numOfRoutePoints = routePoints.size();

        int numOfLinePoints;
        int rtePntInd;
        int lastFoundRtePntInd;
        boolean onePointFound;
        int searchToRtePntInd;
        int searchFromRtePntInd;

        Point linePoint;

        boolean linePntFound;
        boolean rtePntFlag[] = new boolean[numOfRoutePoints + 10];

//		for (int i = 0; i < (numOfRoutePoints + 10); i++) rtePntFlag[i] = false;

//		rtePntInd = 0;
        searchFromRtePntInd = 0;
        lastFoundRtePntInd = 0;

        for (int lineInd = 0; lineInd < numOfSubLines; lineInd++) {
            LineString lineString = lineStrings.get(lineInd);
            onePointFound = false;

            if (!sortedSubLines) {
                for (int i = 0; i < numOfRoutePoints; i++) {
                    if (!rtePntFlag[i]) {
                        searchFromRtePntInd = Math.max(i - 1, 0);
                        break;
                    }
                }
            }

            searchToRtePntInd = numOfRoutePoints;
            numOfLinePoints = lineString.getNumOfPoints();

            for (int linePntInd = 0; linePntInd < numOfLinePoints; linePntInd++) {
                linePoint = lineString.getPoint(linePntInd);

                rtePntInd = searchFromRtePntInd;

                linePntFound = false;

                while (rtePntInd < searchToRtePntInd) {

//					if (linePoint.equalsUpTo6DecPlaces(routePoints.get(rtePntInd)) 
//							|| Geometry.distanceInMeters(linePoint, routePoints.get(rtePntInd)) <= pntLocAccuracyInMeters) {

                    boolean pointIdentityFlag = false;
                    Double distToCurrentRtePoint;
                    Double distToNextRtePoint;

                    if (linePoint.equalsUpTo6DecPlaces(routePoints.get(rtePntInd))) {
                        if (rtePntFlag[rtePntInd] && rtePntInd < (numOfRoutePoints - 1)) {

                            distToCurrentRtePoint = Geometry.distanceInMeters(linePoint, routePoints.get(rtePntInd));
                            distToNextRtePoint = Geometry.distanceInMeters(linePoint, routePoints.get(rtePntInd + 1));

                            if (distToNextRtePoint <= distToCurrentRtePoint) {
                                rtePntInd++;
                                continue;
                            }
                        }
                        pointIdentityFlag = true;
                    } else {
                        distToCurrentRtePoint = Geometry.distanceInMeters(linePoint, routePoints.get(rtePntInd));

                        if (distToCurrentRtePoint <= intersectionAccuracyInMeters) {
                            if (rtePntFlag[rtePntInd] && rtePntInd < (numOfRoutePoints - 1)) {

                                distToNextRtePoint = Geometry.distanceInMeters(linePoint,
                                        routePoints.get(rtePntInd + 1));

                                if (distToNextRtePoint <= distToCurrentRtePoint) {
                                    rtePntInd++;
                                    continue;
                                }
                            }
                            pointIdentityFlag = true;
                        }
                    }

                    if (pointIdentityFlag) {

                        if (onePointFound) {
                            if ((rtePntInd - lastFoundRtePntInd) > 1) {
                                while (lastFoundRtePntInd < (rtePntInd - 1)) {
                                    lineString.addPoint(linePntInd, routePoints.get(++lastFoundRtePntInd));
                                    rtePntFlag[lastFoundRtePntInd] = true;
                                    linePntInd++;
                                    numOfLinePoints++;
                                }
                            }
                        }
                        onePointFound = true;

                        lastFoundRtePntInd = rtePntInd;
                        searchFromRtePntInd = lastFoundRtePntInd;
                        searchToRtePntInd = Math.min(lastFoundRtePntInd + 3, numOfRoutePoints);

                        if (rtePntFlag[rtePntInd] && !(rtePntInd == loopStartPointIndex
                                && lineInd == (numOfSubLines - 1) && linePntInd == (numOfLinePoints - 1))) {

                            Point point = lineString.removePoint(linePntInd);

                            if (numOfLinePoints == 2 && linePntInd == 1) {
                                RoutePoint rtePoint = new RoutePoint(point);
                                rtePoint.pointType = RoutePointType.OTHER;
                                rtePoint.name = Geometry.decDegToDDMMSS(rtePoint);
                                rtePoint.fillDisplayAttributes();

                                lineString.addPoint(linePntInd, rtePoint);
                            } else {
                                linePntInd--;
                                numOfLinePoints--;
                            }
                        }

                        else if (lineInd == (numOfSubLines - 1) && lastLineConnectedByEnd && numOfLinePoints == 2
                                && linePntInd == 0 && rtePntInd == (numOfRoutePoints - 1)) {

                            Point point = lineString.removePoint(linePntInd);

                            RoutePoint rtePoint = new RoutePoint(point);
                            rtePoint.pointType = RoutePointType.OTHER;
                            rtePoint.name = Geometry.decDegToDDMMSS(rtePoint);
                            rtePoint.fillDisplayAttributes();

                            lineString.addPoint(linePntInd, rtePoint);
                        }

                        else {
                            lineString.removePoint(linePntInd);
                            lineString.addPoint(linePntInd, routePoints.get(rtePntInd));

                            rtePntFlag[rtePntInd] = true;

                            if (lineInd == 0 && linePntInd == 0 && rtePntInd == 0) {

                                lineString.connectedByStartPoint = true;
                                firstLineConnectedByStart = true;
                            } else if (lineInd == (numOfSubLines - 1) && linePntInd == (numOfLinePoints - 1)
                                    && (rtePntInd == (numOfRoutePoints - 1) || rtePntInd == loopStartPointIndex)) {

                                lineString.connectedByEndPoint = true;
                                lastLineConnectedByEnd = true;
                            }
                        }

                        linePntFound = true;
                        break;
                    }

                    rtePntInd++;
                }

                if (!linePntFound) {
//					Point point = lineString.removePoint(linePntInd);

                    if (linePntInd == 0 || linePntInd == (numOfLinePoints - 1)) {
                        Point point = lineString.removePoint(linePntInd);

                        RoutePoint rtePoint = new RoutePoint(point);
                        rtePoint.pointType = RoutePointType.OTHER;
                        rtePoint.name = Geometry.decDegToDDMMSS(rtePoint);
                        rtePoint.fillDisplayAttributes();

                        lineString.addPoint(linePntInd, rtePoint);
                    }
//					else {
//						linePntInd--;
//						numOfLinePoints--;
//					}
                }
            }

            // clean-up the list of points leaving only the RoutePoints 
            // this will clean all temporary results which could contain duplicates in case of the points been too close
            // route example: FWKI -> FLKK [ DVL UN305 LABON UR782 VLS]
            // TODO
            // All the code above should be re-factored to address the issue that we now get much more points from the intersection of the route and airspace(s)
            numOfLinePoints = lineString.getNumOfPoints();

            for (int i = 0; i < numOfLinePoints; i++) {
                if (!(lineString.getPoint(i) instanceof RoutePoint)) {
                    lineString.removePoint(i);
                    i--;
                    numOfLinePoints--;
                }
            }

            if (lineInd == 0 && firstLineConnectedByStart && lineString.getFirstPoint() != routePoints.get(0))
                throw new ArithmeticException(
                        "First point of the first Sub-Line in Multi-Line should refer to Departure Route Point, whereas it is Not. Please notify the Developer.");

            if (lineInd == (numOfSubLines - 1) && lastLineConnectedByEnd
                    && !(lineString.getLastPoint() == routePoints.get(numOfRoutePoints - 1) || (loopStartPointIndex >= 0
                            && lineString.getLastPoint() == routePoints.get(loopStartPointIndex))))                 
           
                throw new ArithmeticException(
                        "End point of the last Sub-Line in Multi-Line should refer to Destination Route Point, whereas it is Not. Please notify the Developer.");

        }
    }

    void instantiateEndingRoutePoints(RoutePortion routePortion, double fplPointTolerance_InMeters,
            double dbPointTolerance_InMeters, boolean fplPntVsDbPntPriority, List<RoutePointType> pointTypes,
            DataRetriver dataRetriver, Timestamp timeStamp, boolean allowNamedEndPointsOnly) throws SQLException {

        for (LineString curLineString : lineStrings) {

            RoutePoint curFirstPoint = (RoutePoint) curLineString.getFirstPoint();
            RoutePoint curLastPoint = (RoutePoint) curLineString.getLastPoint();

            if (curFirstPoint.pointType == RoutePointType.OTHER || (curFirstPoint.designator == null && allowNamedEndPointsOnly)) {

                RoutePoint nextRtePnt = (RoutePoint) curLineString.getPoint(1);
                RoutePoint prevRtePnt = null;
                int nextRteIndex = nextRtePnt.parsedPointIndex;
                double distToPrevRtePnt;
                double distToNextRtePnt;

                if (nextRteIndex > 0) {
                    prevRtePnt = (RoutePoint) routePortion.getPoint(nextRteIndex - 1);
                    distToPrevRtePnt = Geometry.distanceInMeters(curFirstPoint, prevRtePnt);
                    distToNextRtePnt = Geometry.distanceInMeters(curFirstPoint, nextRtePnt);
                } else {
                    distToPrevRtePnt = 1000000000d;
                    distToNextRtePnt = 1000000000d;
                }

                if (fplPntVsDbPntPriority) {
                    if (distToPrevRtePnt <= fplPointTolerance_InMeters) {
                        if (distToPrevRtePnt <= distToNextRtePnt || curLineString.getNumOfPoints() == 2
                                || nextRtePnt.pointType == RoutePointType.OTHER) {

                            curLineString.removeFirst();
                            curLineString.addFirst(prevRtePnt);
                            curFirstPoint = prevRtePnt;

                            if (lineStrings.indexOf(curLineString) == 0 && curFirstPoint.parsedPointIndex == 0)
                                firstLineConnectedByStart = true;
                        } else {
                            curLineString.removeFirst();
                            curFirstPoint = nextRtePnt;
                        }
                    } else if (distToNextRtePnt <= fplPointTolerance_InMeters && curLineString.getNumOfPoints() > 2
                            && nextRtePnt.pointType != RoutePointType.OTHER) {

                        curLineString.removeFirst();
                        curFirstPoint = nextRtePnt;
                    }
                }

                if (curFirstPoint.pointType == RoutePointType.OTHER || (curFirstPoint.designator == null && allowNamedEndPointsOnly)) {
                    double dbPntTolerance;
                    if(distToPrevRtePnt <= 0.0) {
                        dbPntTolerance = dbPointTolerance_InMeters;
                    } else {
                        dbPntTolerance = Math.min(distToPrevRtePnt, distToNextRtePnt);
                        if (fplPointTolerance_InMeters < dbPntTolerance && dbPntTolerance < dbPointTolerance_InMeters)
                            dbPntTolerance = Math.min(dbPointTolerance_InMeters, (dbPntTolerance + 1000d));
                        else
                            dbPntTolerance = Math.min(dbPointTolerance_InMeters, (dbPntTolerance - 1000d));
                    }
                    List<RoutePoint> suroundingPoints = dataRetriver.getFixesAroundPoint(pointTypes,
                            curFirstPoint.getWKT(), dbPntTolerance, timeStamp);

                    if (suroundingPoints.isEmpty()) {
                        if (!fplPntVsDbPntPriority) {
                            if (distToPrevRtePnt <= fplPointTolerance_InMeters) {
                                if (distToPrevRtePnt <= distToNextRtePnt || curLineString.getNumOfPoints() == 2
                                        || nextRtePnt.pointType == RoutePointType.OTHER) {

                                    curLineString.removeFirst();
                                    curLineString.addFirst(prevRtePnt);
//									curFirstPoint = prevRtePnt;

                                    if (lineStrings.indexOf(curLineString) == 0 && curFirstPoint.parsedPointIndex == 0)
                                        firstLineConnectedByStart = true;
                                } else {
                                    curLineString.removeFirst();
//									curFirstPoint = nextRtePnt;
                                }
                            } else if (distToNextRtePnt <= fplPointTolerance_InMeters
                                    && curLineString.getNumOfPoints() > 2
                                    && nextRtePnt.pointType != RoutePointType.OTHER) {

                                curLineString.removeFirst();
//								curFirstPoint = nextRtePnt;
                            }
                        }
                    } else {
                        RoutePoint nearestPoint = null;
                        if (suroundingPoints.size() == 1)
                            nearestPoint = suroundingPoints.get(0);
                        else {
                            double shortestDistance = 1000000d;

                            for (RoutePoint point : suroundingPoints) {
                                double currentDistance = Geometry.distanceInMeters(point, curFirstPoint);

                                if (currentDistance < shortestDistance) {
                                    shortestDistance = currentDistance;
                                    nearestPoint = point;
                                }
                            }
                        }
                        if (nearestPoint.equals(nextRtePnt)) {
                            curLineString.removeFirst();
//							curFirstPoint = nextRtePnt;
                        } else {
                            nearestPoint.fillDisplayAttributes(true);
                            curLineString.removeFirst();
                            curLineString.addFirst(nearestPoint);
//							curFirstPoint = nearestPoint;
                        }
                    }
                }
            }

//			=====================================================================

            if (curLastPoint.pointType == RoutePointType.OTHER || (curFirstPoint.designator == null && allowNamedEndPointsOnly)) {

                RoutePoint prevRtePnt = (RoutePoint) curLineString.getPoint(curLineString.getNumOfPoints() - 2);
                RoutePoint nextRtePnt = null;
                int prevRteIndex = prevRtePnt.parsedPointIndex;
                double distToPrevRtePnt;
                double distToNextRtePnt;

                if (prevRteIndex >= 0 && prevRteIndex < (routePortion.getNumOfPoints() - 1)) {
                    nextRtePnt = (RoutePoint) routePortion.getPoint(prevRteIndex + 1);
                    distToPrevRtePnt = Geometry.distanceInMeters(curLastPoint, prevRtePnt);
                    distToNextRtePnt = Geometry.distanceInMeters(curLastPoint, nextRtePnt);
                } else {
                    distToPrevRtePnt = 1000000000d;
                    distToNextRtePnt = 1000000000d;
                }

                if (fplPntVsDbPntPriority) {
                    if (distToNextRtePnt <= fplPointTolerance_InMeters) {
                        if (distToNextRtePnt <= distToPrevRtePnt || curLineString.getNumOfPoints() == 2
                                || prevRtePnt.pointType == RoutePointType.OTHER) {

                            curLineString.removeLast();
                            curLineString.addLast(nextRtePnt);
                            curLastPoint = nextRtePnt;

                            if (lineStrings.indexOf(curLineString) == (lineStrings.size() - 1)
                                    && curLastPoint.parsedPointIndex == (routePortion.getNumOfPoints() - 1))
                                lastLineConnectedByEnd = true;
                        } else {
                            curLineString.removeLast();
                            curLastPoint = prevRtePnt;
                        }
                    } else if (distToPrevRtePnt <= fplPointTolerance_InMeters && curLineString.getNumOfPoints() > 2
                            && prevRtePnt.pointType != RoutePointType.OTHER) {

                        curLineString.removeLast();
                        curLastPoint = prevRtePnt;
                    }
                }

                if (curLastPoint.pointType == RoutePointType.OTHER || (curFirstPoint.designator == null && allowNamedEndPointsOnly)) {
                    double dbPntTolerance;
                    if(distToPrevRtePnt <= 0.0) {
                        dbPntTolerance = dbPointTolerance_InMeters;
                    } else {
                        dbPntTolerance = Math.min(distToPrevRtePnt, distToNextRtePnt);
                        if (fplPointTolerance_InMeters < dbPntTolerance && dbPntTolerance < dbPointTolerance_InMeters)
                            dbPntTolerance = Math.min(dbPointTolerance_InMeters, (dbPntTolerance + 1000d));
                        else
                            dbPntTolerance = Math.min(dbPointTolerance_InMeters, (dbPntTolerance - 1000d));
                    }
                    List<RoutePoint> suroundingPoints = dataRetriver.getFixesAroundPoint(pointTypes,
                            curLastPoint.getWKT(), dbPntTolerance, timeStamp);

                    if (suroundingPoints.isEmpty()) {
                        if (!fplPntVsDbPntPriority) {
                            if (distToNextRtePnt <= fplPointTolerance_InMeters) {
                                if (distToNextRtePnt <= distToPrevRtePnt || curLineString.getNumOfPoints() == 2
                                        || prevRtePnt.pointType == RoutePointType.OTHER) {

                                    curLineString.removeLast();
                                    curLineString.addLast(nextRtePnt);
//									curLastPoint = nextRtePnt;

                                    if (lineStrings.indexOf(curLineString) == (lineStrings.size() - 1)
                                            && curLastPoint.parsedPointIndex == (routePortion.getNumOfPoints() - 1))
                                        lastLineConnectedByEnd = true;
                                } else {
                                    curLineString.removeLast();
//									curLastPoint = prevRtePnt;
                                }
                            } else if (distToPrevRtePnt <= fplPointTolerance_InMeters
                                    && curLineString.getNumOfPoints() > 2
                                    && prevRtePnt.pointType != RoutePointType.OTHER) {

                                curLineString.removeLast();
//								curLastPoint = prevRtePnt;
                            }
                        }
                    } else {
                        RoutePoint nearestPoint = null;
                        if (suroundingPoints.size() == 1)
                            nearestPoint = suroundingPoints.get(0);
                        else {
                            double shortestDistance = 1000000d;

                            for (RoutePoint point : suroundingPoints) {
                                double currentDistance = Geometry.distanceInMeters(point, curLastPoint);

                                if (currentDistance < shortestDistance) {
                                    shortestDistance = currentDistance;
                                    nearestPoint = point;
                                }
                            }
                        }
                        if (nearestPoint.equals(prevRtePnt)) {
                            curLineString.removeLast();
//							curFirstPoint = prevRtePnt;
                        } else {
                            nearestPoint.fillDisplayAttributes(true);
                            curLineString.removeLast();
                            curLineString.addLast(nearestPoint);
//							curLastPoint = nearestPoint;
                        }
                    }
                }
            }
        }

        if (getNumOfLines() > 1) {
            for (int i = 0; i < (getNumOfLines() - 1); i++) {
                LineString prevLineString = lineStrings.get(i);
                LineString nextLineString = lineStrings.get(i + 1);

                if (prevLineString.getLastPoint().equals(nextLineString.getFirstPoint())) {

                    nextLineString.removeFirst();
                    prevLineString.addAllPoints(nextLineString.points);
                    prevLineString.connectedByEndPoint = nextLineString.connectedByEndPoint;

                    lineStrings.remove(i + 1);
                    nextLineString.close();
                    nextLineString = null;
                    i--;
                }

                else {
                    double distance = Geometry.distanceInMeters(prevLineString.getLastPoint(),
                            nextLineString.getFirstPoint());
                    double tolerance = Math.max(fplPointTolerance_InMeters, dbPointTolerance_InMeters);

                    if (distance <= tolerance) {
                        RoutePoint prevFplPoint = getLastFplPoint(prevLineString);
                        RoutePoint nextFplPoint = getFirstFplPoint(nextLineString);

                        if (!(prevFplPoint == null || nextFplPoint == null)) {
                            if (prevFplPoint.parsedPointIndex <= nextFplPoint.parsedPointIndex
                                    && (nextFplPoint.parsedPointIndex - prevFplPoint.parsedPointIndex) < 2) {

                                int numOfPntToBeRemoved = prevLineString.getNumOfPoints() - 1
                                        - prevLineString.points.lastIndexOf(prevFplPoint);
                                if (numOfPntToBeRemoved > 0) {
                                    for (int j = 0; j < numOfPntToBeRemoved; j++)
                                        prevLineString.removeLast();
                                }

                                numOfPntToBeRemoved = nextLineString.points.indexOf(nextFplPoint);
                                if (numOfPntToBeRemoved > 0) {
                                    for (int j = 0; j < numOfPntToBeRemoved; j++)
                                        nextLineString.removeFirst();
                                }

                                if (prevFplPoint.equals(nextFplPoint))
                                    nextLineString.removeFirst();

                                if (prevFplPoint.parsedPointIndex < nextFplPoint.parsedPointIndex) {
                                    for (int j = (prevFplPoint.parsedPointIndex
                                            + 1); j <= nextFplPoint.parsedPointIndex; j++) {
                                        prevLineString.addLast(routePortion.getPoint(j));
                                    }
                                }

                                prevLineString.addAllPoints(nextLineString.getPoints());
                                lineStrings.remove(i + 1);
                                nextLineString.close();
                                nextLineString = null;
                                i--;
                            }
                        }
                    }
                }
            }
        }
    }

    private RoutePoint getLastFplPoint(LineString lineString) {

        for (int i = (lineString.getNumOfPoints() - 1); i >= 0; i--) {
            Point point = lineString.getPoint(i);
            if (point instanceof RoutePoint && ((RoutePoint) point).parsedPointIndex >= 0)
                return (RoutePoint) point;
        }

        return null;
    }

    private RoutePoint getFirstFplPoint(LineString lineString) {

        for (Point point : lineString.points) {
            if (point instanceof RoutePoint && ((RoutePoint) point).parsedPointIndex >= 0)
                return (RoutePoint) point;
        }

        return null;
    }
    
}
