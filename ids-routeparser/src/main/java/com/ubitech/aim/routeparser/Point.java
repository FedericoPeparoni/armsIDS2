package com.ubitech.aim.routeparser;

import java.util.regex.Matcher;

class Point extends Geometry {
    private Double x = null; // should store only Longitude attribute value of a feature found in the
                             // database.
    private Double y = null; // should store only Latitude attribute value of a feature found in the
                             // database.

    Point() {
    }

    Point(Point point) {
        super(point);
        x = point.getX();
        y = point.getY();
    }

    Point(String pntWKT) {
        setWKT(pntWKT);
    }

    Point(String pntWKT, Integer pntSRID) {
        setWKT(pntWKT, pntSRID);
    }

    Point(Matcher pntMatcher, Integer pntSRID) {
        setWKT(pntMatcher, pntSRID);
    }

    Point(Double pntLong, Double pntLat) {
        setCoordinates(pntLong, pntLat, GeogSRID);
    }

    Point(Double pntX, Double pntY, Integer pntSRID) {
        setCoordinates(pntX, pntY, pntSRID);
    }

    /**
     * Returns String value, containing point location in WKT format.
     * 
     * @return String value, containing point location in WKT format.
     */
    String getWKT() {
        if (WKT == null && !(x == null || y == null))
            WKT = "POINT(" + x.toString() + " " + y.toString() + ")";

        return WKT;
    }

    Double getX() {
        return x;
    }

    Double getY() {
        return y;
    }

    void close() {
        super.close();
        x = null;
        y = null;
    }

    boolean equals(Point point) {
        if (point == null)
            throw new NullPointerException("'Point point' parameter of 'Point.equals' method cannot accept 'Null'.");

        if (x == null || y == null)
            throw new NullPointerException(
                    "'x' and 'y' parameters of 'Point' instance should not be 'Null' when 'Point.equals' method is called.");
//		if (SRID == null) throw new NullPointerException("'SRID' parameter of 'Point' instance should not be 'Null' when 'Point.equals' method is called.");

        if (point.x == null || point.y == null)
            throw new NullPointerException(
                    "'x' and 'y' parameters of 'Point' instance should not be 'Null' when it is transfered as a parameter to 'Point.equals' method.");
//		if (point.SRID == null) throw new NullPointerException("'SRID' parameter of 'Point' instance should not be 'Null' when it is transfered as a parameter to 'Point.equals' method.");

//		if (x.equals(point.x) && y.equals(point.y) && SRID.equals(point.SRID)) return true;
        if (x.equals(point.x) && y.equals(point.y))
            return true;
        else
            return false;
    }

    boolean equalsUpTo6DecPlaces(Point point) {
        if (point == null)
            throw new NullPointerException("'Point point' parameter of 'Point.equals' method cannot accept 'Null'.");

        if (x == null || y == null)
            throw new NullPointerException(
                    "'x' and 'y' parameters of 'Point' instance should not be 'Null' when 'Point.equals' method is called.");
//		if (SRID == null) throw new NullPointerException("'SRID' parameter of 'Point' instance should not be 'Null' when 'Point.equals' method is called.");

        if (point.x == null || point.y == null)
            throw new NullPointerException(
                    "'x' and 'y' parameters of 'Point' instance should not be 'Null' when it is transfered as a parameter to 'Point.equals' method.");
//		if (point.SRID == null) throw new NullPointerException("'SRID' parameter of 'Point' instance should not be 'Null' when it is transfered as a parameter to 'Point.equals' method.");

        if (Math.round(x.doubleValue() * 1000000.0) == Math.round(point.x.doubleValue() * 1000000.0)
                && Math.round(y.doubleValue() * 1000000.0) == Math.round(point.y.doubleValue() * 1000000.0))
//				&& SRID.equals(point.SRID)) 
            return true;

        else
            return false;
    }

    void setWKT(String pntWKT) {
        setWKT(pntWKT, GeogSRID);
    }

    void setWKT(String pntWKT, Integer pntSRID) {

        if (pntWKT == null)
            throw new NullPointerException("'String pntWKT' parameter of 'Point.setWKT' method cannot accept 'Null'.");

        if (pntSRID == null)
            throw new NullPointerException(
                    "'Integer pntSRID' parameter of 'Point.setWKT' method cannot accept 'Null'.");

        Matcher matcher = patternPOINT.matcher(pntWKT);

        if (matcher.matches())
            setWKT(matcher, pntSRID);

        else {
            x = null;
            y = null;
            SRID = null;
            WKT = null;

            throw new IllegalArgumentException(
                    "'" + pntWKT + "' string cannot be parsed as POINT WKT in 'Point.setWKT' method.");
        }
    }

    private void setWKT(Matcher pntMatcher, Integer pntSRID) {
        try {
            setCoordinates(Double.valueOf(pntMatcher.group(1)), Double.valueOf(pntMatcher.group(2)), pntSRID);
            WKT = "POINT(" + x.toString() + " " + y.toString() + ")";
            SRID = pntSRID;

        } catch (NumberFormatException e) {
            SRID = null;
            WKT = null;
            throw new NumberFormatException("Coordinates in '" + pntMatcher.group()
                    + "' POINT WKT string cannot be parsed as numbers in 'Point.setWKT' method.");
        }
    }

    void setCoordinates(Double pntLong, Double pntLat) {
        setCoordinates(pntLong, pntLat, GeogSRID);
    }

    void setCoordinates(Double pntX, Double pntY, Integer pntSRID) {
        if (pntX == null || pntY == null)
            throw new NullPointerException(
                    "'Double pntX, Double pntY' parameters of 'Point.setCoordinates' method cannot accept 'Null'.");

        if (pntSRID == null)
            throw new NullPointerException(
                    "'Integer pntSRID' parameter of 'Point.setCoordinates' method cannot accept 'Null'.");

        if (pntSRID.equals(GeogSRID)) {
            if (pntX.doubleValue() < -180 || pntX.doubleValue() > 180)
                throw new IllegalArgumentException("'" + pntX.toString()
                        + "' value, transfered to 'Double pntX' parameter of 'Point.setCoordinates' method is not a valid Longitude.");

            if (pntY.doubleValue() < -90 || pntY.doubleValue() > 90)
                throw new IllegalArgumentException("'" + pntY.toString()
                        + "' value, transfered to 'Double pntY' parameter of 'Point.setCoordinates' method is not a valid Latitude.");
        }

        x = pntX;
        y = pntY;
        SRID = pntSRID;
        WKT = null;
    }

    double getXYoffset(LineString baseLine) {
        double xyOffset = getXoffset(baseLine);
        xyOffset = xyOffset + getYoffset(baseLine);
        return xyOffset;
    }

    double getXoffset(LineString baseLine) {
        if (baseLine == null)
            throw new NullPointerException(
                    "'LineString baseLine' parameter of 'Point.getXoffset' method cannot accept 'Null'.");
        if (baseLine.points == null)
            throw new NullPointerException(
                    "'points' parameter of 'LineString' instance should not be 'Null' when it is transfered as a parameter to 'Point.getXoffset' method.");
        if (baseLine.points.isEmpty())
            throw new IllegalArgumentException(
                    "'points' parameter of 'LineString' instance should not be Empty when it is transfered as a parameter to 'Point.getXoffset' method.");

        if (x == null || y == null)
            throw new NullPointerException(
                    "'x' and 'y' parameters of 'Point' instance should not be 'Null' when 'Point.getXoffset' method is called.");
//		if (SRID == null) throw new NullPointerException("'SRID' parameter of 'Point' instance should not be 'Null' when 'Point.getXoffset' method is called.");

        double baseCenterX = baseLine.getCenterPoint().x.doubleValue();

        double xOffset = 0.0;
        double xDiff;
        double absXdiff;

        if (baseLine.sunPathOriented != null) {

            xDiff = baseCenterX - x.doubleValue();
            absXdiff = Math.abs(xDiff);

            xOffset = absXdiff <= 180.0 ? xDiff : Math.signum(xDiff) * (-360.0) + xDiff;
            if (!baseLine.sunPathOriented)
                xOffset = -xOffset;
        }

        return xOffset;
    }

    double getYoffset(LineString baseLine) {
        if (baseLine == null)
            throw new NullPointerException(
                    "'LineString baseLine' parameter of 'Point.getYoffset' method cannot accept 'Null'.");
        if (baseLine.points == null)
            throw new NullPointerException(
                    "'points' parameter of 'LineString' instance should not be 'Null' when it is transfered as a parameter to 'Point.getYoffset' method.");
        if (baseLine.points.isEmpty())
            throw new IllegalArgumentException(
                    "'points' parameter of 'LineString' instance should not be Empty when it is transfered as a parameter to 'Point.getYoffset' method.");

        if (x == null || y == null)
            throw new NullPointerException(
                    "'x' and 'y' parameters of 'Point' instance should not be 'Null' when 'Point.getYoffset' method is called.");
//		if (SRID == null) throw new NullPointerException("'SRID' parameter of 'Point' instance should not be 'Null' when 'Point.getYoffset' method is called.");

        double baseCenterY = baseLine.getCenterPoint().y.doubleValue();

        double yOffset = 0.0;

        if (baseLine.northToSouthOriented != null) {

            yOffset = baseCenterY - y.doubleValue();

            if (!baseLine.northToSouthOriented)
                yOffset = -yOffset;
        }

        return yOffset;
    }
}
