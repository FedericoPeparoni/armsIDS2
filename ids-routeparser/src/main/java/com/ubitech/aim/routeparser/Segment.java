package com.ubitech.aim.routeparser;

class Segment extends Geometry {
    Point startPoint = null;
    Point endPoint = null;
    Double lengthInMeters = null;

    Segment() {
        SRID = GeogSRID;
    }

    Segment(Point startPoint, Point endPoint) {
        this(startPoint, endPoint, GeogSRID);
    }

    Segment(Point startPoint, Point endPoint, Integer segmentSRID) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.SRID = segmentSRID;
    }

    String getWKT() {
        if (startPoint == null || endPoint == null)
            return null;

        return "LINESTRING(" + startPoint.getX().toString() + " " + startPoint.getY().toString() + ","
                + endPoint.getX().toString() + " " + endPoint.getY().toString() + ")";
    }
}
