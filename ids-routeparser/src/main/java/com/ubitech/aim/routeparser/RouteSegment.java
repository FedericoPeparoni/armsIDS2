package com.ubitech.aim.routeparser;

public class RouteSegment extends Segment {

    RouteSegment() {
        SRID = GeogSRID;
    }

    RouteSegment(RoutePoint startRoutePoint, RoutePoint endRoutePoint) {
        super(startRoutePoint, endRoutePoint);
    }

    RouteSegment(RoutePoint startRoutePoint, RoutePoint endRoutePoint, Integer rteSegSRID) {
        super(startRoutePoint, endRoutePoint, rteSegSRID);
    }

    public String getWKT() {
        return super.getWKT();
    }

    public RoutePoint getStartRoutePoint() {
        if (startPoint != null && startPoint instanceof RoutePoint)
            return (RoutePoint) startPoint;
        return null;
    }

    public RoutePoint getEndRoutePoint() {
        if (endPoint != null && endPoint instanceof RoutePoint)
            return (RoutePoint) endPoint;
        return null;
    }

    public Double getLengthInKM() {
        if (startPoint == null || endPoint == null || lengthInMeters == null)
            return null;

        return new Double(lengthInMeters.doubleValue() * 0.001);
    }

    public Double getLengthInNM() {
        if (startPoint == null || endPoint == null || lengthInMeters == null)
            return null;

        return new Double(lengthInMeters.doubleValue() * RouteFinder.METER_TO_NM);
    }
}
