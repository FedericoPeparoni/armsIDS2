package com.ubitech.aim.routeparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//

abstract class Geometry {
    static final Integer GeogSRID = 4326;
    static final double EarthRadiusInMeters = 6371000.0d;
    static final Pattern patternPOINT = Pattern
            .compile("^\\s*POINT\\s*\\(\\s*([\\S&&[^\\)\\(]]+)\\s+([\\S&&[^\\)\\(]]+)\\s*\\)\\s*$");
    static final Pattern patternLINESTRING = Pattern.compile("^\\s*LINESTRING\\s*\\(([^\\)\\(]{3,})\\)\\s*$");
    static final Pattern patternMULTILINE = Pattern.compile(
            "^\\s*MULTILINESTRING\\s*\\(\\s*(\\([^\\)\\(]{3,}\\)(?:\\s*,\\s*\\([^\\)\\(]{3,}\\))*?)\\s*\\)\\s*$");
    static final Pattern patternPOLYGON = Pattern
            .compile("^\\s*POLYGON\\s*+\\(\\s*+(.*)\\s*\\)\\s*$");
    static final Pattern patternMULTIPOLYGON = Pattern.compile(
            "^\\s*+MULTIPOLYGON\\s*+\\(\\s*+(.*)\\s*\\)\\s*$");
    
    static final int maxNumOfPolygonsInMultiPoly = 1;
    static final int maxNumOfRingsInPolygon = 10;

    String WKT = null;
    Integer SRID = null;

    abstract String getWKT();

    Geometry() {
    }

    Geometry(Geometry geometry) {
        this.WKT = geometry.getWKT();
        this.SRID = geometry.SRID;
    }

    static Geometry geometryFromWKT(String geoWKT, Integer geoSRID) {
        if (geoWKT == null || geoWKT.isEmpty())
            return null;

        Matcher matcher = patternLINESTRING.matcher(geoWKT);
        if (matcher.matches())
            return new LineString(matcher, geoSRID);

        matcher = patternMULTILINE.matcher(geoWKT);
        if (matcher.matches())
            return new MultiLineString(matcher, geoSRID);

        matcher = patternPOLYGON.matcher(geoWKT);
        if (matcher.matches())
            return new Polygon(matcher, geoSRID);

        matcher = patternMULTIPOLYGON.matcher(geoWKT);
        if (matcher.matches())
            return new MultiPolygon(matcher, geoSRID);

        matcher = patternPOINT.matcher(geoWKT);
        if (matcher.matches())
            return new Point(matcher, geoSRID);

        return null;
    }

    void close() {
        WKT = null;
        SRID = null;
    }

    static int getSRID(double longitude, double latitude) {

        int baseSrid;

        if (latitude >= 0) {
            baseSrid = 32601;
        } else { // if (pointLat < 0)
            baseSrid = 32701;
        }

        double tempDouble = (longitude + 180.0) / 6;
        double sridOffset = Math.floor(tempDouble);

        return baseSrid + (int) sridOffset;
    }

    static Double distanceInMeters(Point pnt1, Point pnt2) {
        if (pnt1.getX() == null || pnt1.getY() == null || pnt1.SRID == null || pnt2.getX() == null
                || pnt2.getY() == null || pnt2.SRID == null)
            return null;

        if (!(pnt1.SRID.equals(GeogSRID) && pnt1.SRID.equals(GeogSRID)))
            return null;

        return distanceInMeters(pnt1.getX().doubleValue(), pnt1.getY().doubleValue(), pnt2.getX().doubleValue(),
                pnt2.getY().doubleValue());
    }

    static Double distanceInMeters(double x1, double y1, double x2, double y2) {

        if (x1 == x2 && y1 == y2)
            return new Double(0.0);

        double dX;

        dX = x2 - x1;

        x1 = x1 / 180.0d * Math.PI;
        x2 = x2 / 180.0d * Math.PI;
        y1 = y1 / 180.0d * Math.PI;
        y2 = y2 / 180.0d * Math.PI;

        dX = dX / 180.0d * Math.PI;

        return new Double(Math.acos(Math.sin(y1) * Math.sin(y2) + Math.cos(y1) * Math.cos(y2) * Math.cos(dX))
                * EarthRadiusInMeters);
    }

    static Double azimuthInRad(Point pnt1, Point pnt2) {
        if (pnt1.getX() == null || pnt1.getY() == null || pnt1.SRID == null || pnt2.getX() == null
                || pnt2.getY() == null || pnt2.SRID == null)
            return null;

        if (!(pnt1.SRID.equals(GeogSRID) && pnt1.SRID.equals(GeogSRID)))
            return null;

        return azimuthInRad(pnt1.getX().doubleValue(), pnt1.getY().doubleValue(), pnt2.getX().doubleValue(),
                pnt2.getY().doubleValue());
    }

    static Double azimuthInRad(double x1, double y1, double x2, double y2) {

        if (x1 == x2 && y1 == y2)
            return new Double(0.0);

        double dX;

        dX = x2 - x1;

        x1 = x1 / 180.0d * Math.PI;
        x2 = x2 / 180.0d * Math.PI;
        y1 = y1 / 180.0d * Math.PI;
        y2 = y2 / 180.0d * Math.PI;

        dX = dX / 180.0d * Math.PI;

        double y = Math.sin(dX) * Math.cos(y2);
        double x = Math.cos(y1) * Math.sin(y2) - Math.sin(y1) * Math.cos(y2) * Math.cos(dX);

        return new Double(Math.atan2(y, x));
    }

    static Double distanceInMeters2(Point pnt1, Point pnt2) {
        if (pnt1.getX() == null || pnt1.getY() == null || pnt1.SRID == null || pnt2.getX() == null
                || pnt2.getY() == null || pnt2.SRID == null)
            return null;

        if (pnt1.equals(pnt2))
            return new Double(0.0);

        if (!(pnt1.SRID.equals(GeogSRID) && pnt1.SRID.equals(GeogSRID)))
            return null;

        double x1, y1, x2, y2, dX, dY, a, c;

        x1 = pnt1.getX();
        y1 = pnt1.getY();
        x2 = pnt2.getX();
        y2 = pnt2.getY();

        dX = x2 - x1;
        dY = y2 - y1;

        x1 = x1 / 180.0d * Math.PI;
        x2 = x2 / 180.0d * Math.PI;
        y1 = y1 / 180.0d * Math.PI;
        y2 = y2 / 180.0d * Math.PI;

        dX = dX / 180.0d * Math.PI;
        dY = dY / 180.0d * Math.PI;

        a = Math.sin(dY / 2) * Math.sin(dY / 2) + Math.cos(y1) * Math.cos(y2) * Math.sin(dX / 2) * Math.sin(dX / 2);
        c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return new Double(c * EarthRadiusInMeters);
    }

    static String decDegToDDMMSS(Point point) {
        return decDegToDDMMSS(point.getX().doubleValue(), point.getY().doubleValue());
    }

    static String decDegToDDMMSS(double longitude, double latitude) {
        double latAbs = Math.abs(latitude);
        if (latAbs > 90.0d)
            latAbs = latAbs % 90.0d;
        final String latH = latitude >= 0.0d ? "N" : "S";
        int latDeg = (int) Math.floor(latAbs);
        double latFrac = (latAbs - latDeg) * 60.0d;
        int latMin = (int) Math.floor(latFrac);
        latFrac = (latFrac - latMin) * 60.0d;
        int latSec = (int) Math.round(latFrac);
        if (latSec == 60) {
            latMin++;
            latSec = 0;
            if (latMin == 60) {
                latDeg++;
                latMin = 0;
            }
        }

        double lngAbs = Math.abs(longitude);
        if (lngAbs > 180.0d)
            lngAbs = lngAbs % 180.0d;
        final String lngH = longitude >= 0.0d ? "E" : "W";
        int lngDeg = (int) Math.floor(lngAbs);
        double lngFrac = (lngAbs - lngDeg) * 60.0d;
        int lngMin = (int) Math.floor(lngFrac);
        lngFrac = (lngFrac - lngMin) * 60.0d;
        int lngSec = (int) Math.round(lngFrac);
        if (lngSec == 60) {
            lngMin++;
            lngSec = 0;
            if (lngMin == 60) {
                lngDeg++;
                lngMin = 0;
            }
        }

        final String coordStr = String.format("%02d %02d %02d %s %03d %02d %02d %s", latDeg, latMin, latSec, latH,
                lngDeg, lngMin, lngSec, lngH);

        return coordStr;
    }

    @Override
    public String toString() {
        return getWKT();
    }

}
