package com.ubitech.aim.routeparser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class RoutePortion extends LineString {
    private String routeWKT = null;
    public String cruisingLevel = null;

    RoutePortion() {
    };

    RoutePortion(List<RoutePoint> listOfRoutePoints) {
        addAllRoutePoints(listOfRoutePoints);
    }

    RoutePortion(LineString lineString) {
        setLineString(lineString);
    }

    String getWKT() {
        return super.getWKT();
    }

    public String getRouteWKT() {
        if (points == null || points.isEmpty()) {
            routeWKT = null;
        } else {
            String tempWKT = "LINESTRING(";
            routeWKT = tempWKT;
            int size = points.size();
            Point tempPoint;

            for (int i = 0; i < size; i++) {
                tempPoint = points.get(i);
                if (tempPoint instanceof RoutePoint) {
                    tempWKT = tempWKT + tempPoint.getX().toString() + " " + tempPoint.getY().toString() + ",";
                }
            }
            if (tempWKT.length() > routeWKT.length())
                routeWKT = tempWKT.substring(0, tempWKT.length() - 1) + ")";
            else
                routeWKT = null;
        }
        return routeWKT;
    }

    public ArrayList<RoutePoint> getRoutePoints() {
        ArrayList<RoutePoint> listOfRoutePoints = new ArrayList<RoutePoint>(points.size());
        for (Point point : points) {
            if (point instanceof RoutePoint)
                listOfRoutePoints.add((RoutePoint) point);
        }
        return listOfRoutePoints;
    }

    public Double getLengthInKM() {
        return new Double(lengthInMeters.doubleValue() * 0.001);
    }

    public Double getLengthInNM() {
        return new Double(lengthInMeters.doubleValue() * RouteFinder.METER_TO_NM);
    }

    public int getNumOfRoutePoints() {
        if (points == null || points.isEmpty())
            return 0;

        return points.size();
    }

    public RoutePoint getRoutePoint(int index) {
        return (RoutePoint) getPoint(index);
    }

    public RoutePoint getFirstRoutePoint() {
        if (points == null || points.isEmpty())
            return null;

        for (Point point : points) {
            if (point instanceof RoutePoint) {
                return (RoutePoint) point;
            }
        }

        return null;
    }

    public RoutePoint getLastRoutePoint() {
        if (points == null || points.isEmpty())
            return null;

        Point point;
        Iterator<Point> pointsDescendingIterator = points.descendingIterator();

        while (pointsDescendingIterator.hasNext()) {
            point = pointsDescendingIterator.next();
            if (point instanceof RoutePoint) {
                return (RoutePoint) point;
            }
        }

        return null;
    }

    public LinkedList<RouteSegment> getRouteSegments() {
        if (points == null || points.isEmpty())
            return null;

        LinkedList<RouteSegment> outputList = new LinkedList<RouteSegment>();
        RoutePoint prevRoutePoint = null;
        RoutePoint currRoutePoint = null;

        for (Point point : points) {
            if (point instanceof RoutePoint) {
                if (prevRoutePoint == null) {
                    prevRoutePoint = (RoutePoint) point;
                    continue;
                }

                currRoutePoint = (RoutePoint) point;
                outputList.add(new RouteSegment(prevRoutePoint, currRoutePoint, GeogSRID));
                prevRoutePoint = currRoutePoint;
            }
        }
        return outputList;
    }

    void addRoutePoint(RoutePoint routePoint) {
        addPoint(routePoint);
    }

    boolean addAllRoutePoints(List<RoutePoint> listOfRoutePoints) {
        return addAllPoints((List) listOfRoutePoints);
    }

    void clear() {
        WKT = null;
        routeWKT = null;
        cruisingLevel = null;
        points.clear();
    }
}
