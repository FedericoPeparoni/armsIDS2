package com.ubitech.aim.routeparser;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenInterpretation {
    TokenPrimaryType tokenPrimaryType = null;
    KeyWord keyWord = null;
    SignificantPointPrimaryType significantPointPrimType = null;
    SignificantPointSecondaryType significantPointSecType = null;
    NavaidType navaidType = null;
    String parsedFeatureDesignator = null;
    Long timeSliceRecID = null;
    List<Long> retrievedRouteRecIDs = null;
    List<List<RoutePoint>> retrievedRouteStructures = null;
    String name = null;
    String cruisingLevelChange = null;
    private String routePointWKT = null;
//	String basePointWKT = null;
//	String parsedBearing = null;
//	String parsedDistance = null;
    String tokenValue = null;
    Double latitude = null;
    Double longitude = null;
    ATSRoute atsRoutePortion = null;

    boolean connectedToRoute = false;

    int parsedPointIndex = -1;

    TokenInterpretation() {
    }

    TokenInterpretation(TokenPrimaryType tokenPrimaryType, KeyWord keyWord,
            SignificantPointPrimaryType significantPointPrimType, SignificantPointSecondaryType significantPointSecType,
            NavaidType navaidType, String parsedFeatureDesignator, Long timeSliceRecID, List<Long> retrievedRouteRecIDs,
            List<List<RoutePoint>> retrievedRouteStructures, String name, String routePointWKT, String tokenValue,
            Double latitude, Double longitude, ATSRoute atsRoutePortion) {
//			String routePointWKT, String basePointWKT, String parsedBearing, String parsedDistance) {
        this.tokenPrimaryType = tokenPrimaryType;
        this.keyWord = keyWord;
        this.significantPointPrimType = significantPointPrimType;
        this.significantPointSecType = significantPointSecType;
        this.navaidType = navaidType;
        this.parsedFeatureDesignator = parsedFeatureDesignator;
        this.timeSliceRecID = timeSliceRecID;
        this.retrievedRouteRecIDs = retrievedRouteRecIDs;
        this.retrievedRouteStructures = retrievedRouteStructures;
        this.name = name;
        this.routePointWKT = routePointWKT;
        this.tokenValue = tokenValue;
        this.latitude = latitude;
        this.longitude = longitude;
        this.atsRoutePortion = atsRoutePortion;
//		this.basePointWKT = basePointWKT;
//		this.parsedBearing = parsedBearing;
//		this.parsedDistance = parsedDistance;
    }

    TokenInterpretation(RoutePoint routePoint) {
        if (routePoint == null)
            return;

        this.parsedFeatureDesignator = routePoint.designator;
        this.tokenValue = routePoint.designator;
        this.name = routePoint.name;
        this.setCoordinates(routePoint.getLongitude(), routePoint.getLatitude());
        this.timeSliceRecID = routePoint.recordID;

        switch (routePoint.pointType) {
        case OTHER:
            this.tokenPrimaryType = TokenPrimaryType.INCORRECT_FORMAT;
            break;
        case ADHP:
            this.tokenPrimaryType = TokenPrimaryType.ADHP;
            break;
        case Waypoint:
            this.tokenPrimaryType = TokenPrimaryType.SIGNIFICANT_POINT;
            this.significantPointPrimType = SignificantPointPrimaryType.DESIGNATEDPOINT;
            break;
        default:
            this.tokenPrimaryType = TokenPrimaryType.SIGNIFICANT_POINT;
            this.significantPointPrimType = SignificantPointPrimaryType.NAVAID;
            this.navaidType = NavaidType.valueOf(routePoint.pointType.toString());
        }
    }

    TokenInterpretation getClone() {
        TokenInterpretation myClone = new TokenInterpretation(tokenPrimaryType, keyWord, significantPointPrimType,
                significantPointSecType, navaidType, parsedFeatureDesignator, timeSliceRecID, retrievedRouteRecIDs,
                retrievedRouteStructures, name, routePointWKT, tokenValue, latitude, longitude, atsRoutePortion);
//				routePointWKT, basePointWKT, parsedBearing, parsedDistance);
        return myClone;
    }

    TokenInterpretation getClone2() {
        TokenInterpretation myClone = new TokenInterpretation(tokenPrimaryType, keyWord, significantPointPrimType,
                significantPointSecType, navaidType, parsedFeatureDesignator, timeSliceRecID, retrievedRouteRecIDs,
                retrievedRouteStructures, name, routePointWKT, tokenValue, latitude, longitude, null);
//				routePointWKT, basePointWKT, parsedBearing, parsedDistance);
        return myClone;
    }

    /**
     * Returns true only if the token is based on a designator of found point-type
     * (AirportHeliport, DesignatedPoint, VOR, NDB or TACAN) feature occurrence OR
     * if the token is based on DEGREESONLY or DEGREESANDMINUTES (like 46N078W or
     * 4602N07805W).
     * 
     * @return true only if the token is based on a designator of found point-type
     *         (AirportHeliport, DesignatedPoint, VOR, NDB or TACAN) feature
     *         occurrence OR if the token is based on DEGREESONLY or
     *         DEGREESANDMINUTES (like 46N078W or 4602N07805W).
     */
    public boolean isValidRoutePoint() {
        if (tokenPrimaryType == TokenPrimaryType.SIGNIFICANT_POINT || tokenPrimaryType == TokenPrimaryType.ADHP
                || tokenPrimaryType == TokenPrimaryType.SID_ROUTE || tokenPrimaryType == TokenPrimaryType.STAR_ROUTE) {
            return true;
        }
        return false;
    }

    public Long getTimeSliceRecID() {
        return timeSliceRecID;
    }

    /**
     * Returns name-field value if the token is based on a designator of found
     * feature occurrence. Otherwise null is returned.
     * 
     * @return name-field value if the token is based on a designator of found
     *         feature occurrence. Otherwise null is returned.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns parsed feature designator if the token is based on AIXM-feature.
     * 
     * @return parsed feature designator if the token is based on AIXM-feature. For
     *         example if token is HADDY/N0420F330 or DUB180040/N0350M0840,
     *         getParsedFeatureDesignator() will return HADDY or DUB respectively.
     *         Otherwise, if the token does not represent a feature, null is
     *         returned.
     */
    public String getParsedFeatureDesignator() {
        return parsedFeatureDesignator;
    }

    /**
     * Returns original value of the token parsed/extracted from the full route
     * expression.
     * 
     * @return original value of the token parsed/extracted from the full route
     *         expression.
     */
    public String getTokenValue() {
        return tokenValue;
    }

//	Note:	If the token represented entity is based on intersection of Bearing and Distance from Navaid,
//			getRoutePointWKT() will return coordinates of intersection (Not Navaid location).
    public String getRoutePointWKT() {
        return routePointWKT;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Point getPoint() {
        return new Point(longitude, latitude);
    }

    void setCoordinates(Double longDbl, Double latDbl) {
        if (longDbl == null || latDbl == null || longDbl < -180 || longDbl > 180 || latDbl < -90 || latDbl > 90) {
            this.longitude = null;
            this.latitude = null;
            this.routePointWKT = null;
            return;
        }

        this.longitude = longDbl;
        this.latitude = latDbl;
        this.routePointWKT = "POINT(" + this.longitude.toString() + " " + this.latitude.toString() + ")";
    }

    void setRoutePointWKT(String pointWKT) {
        if (pointWKT == null) {
            this.longitude = null;
            this.latitude = null;
            this.routePointWKT = null;
            return;
        }

        Pattern patternPointWKT = Pattern.compile("^\\s*POINT\\s*\\(\\s*(\\S+)\\s+(\\S+)\\s*\\)\\s*$");
        Matcher matcher = patternPointWKT.matcher(pointWKT);
        if (matcher.matches()) {
            double dblLong = Double.parseDouble(matcher.group(1));
            ;
            double dblLat = Double.parseDouble(matcher.group(2));
            ;
            if (dblLat < -90 || dblLat > 90 || dblLong < -180 || dblLong > 180)
                return;
            this.longitude = dblLong;
            this.latitude = dblLat;
            this.routePointWKT = "POINT(" + this.longitude.toString() + " " + this.latitude.toString() + ")";
        }
    }

    boolean isBasedOnRoutePoint(RoutePoint routePoint) {
        if (tokenPrimaryType == null || significantPointPrimType == null || timeSliceRecID == null
                || parsedFeatureDesignator == null)
            return false;

        if (routePoint.pointType == null || routePoint.recordID == null || routePoint.designator == null)
            return false;

        if (!timeSliceRecID.equals(routePoint.recordID))
            return false;

        if (!parsedFeatureDesignator.equals(routePoint.designator))
            return false;

        if (tokenPrimaryType.ordinal() < TokenPrimaryType.SIGNIFICANT_POINT.ordinal()
                || tokenPrimaryType.ordinal() > TokenPrimaryType.STAR_ROUTE.ordinal())
            return false;

        if (routePoint.pointType == RoutePointType.ADHP)
            return false;

        if (routePoint.pointType == RoutePointType.Waypoint) {
            switch (significantPointPrimType) {
            case DESIGNATEDPOINT:
                return true;
            case SPEEDORLEVELCHANGE:
            case CRUISECLIMB:
                if (significantPointSecType == null)
                    return false;
                switch (significantPointSecType) {
                case DESIGNATEDPOINT:
                    return true;
                default:
                    return false;
                }
            default:
                return false;
            }
        } else if (routePoint.pointType.ordinal() > RoutePointType.Waypoint.ordinal()) {
            switch (significantPointPrimType) {
            case NAVAID:
                if (navaidType == null)
                    return false;
                else if (navaidType.toString().toUpperCase().equals(routePoint.pointType.toString().toUpperCase()))
                    return true;
                else
                    return false;
            case SPEEDORLEVELCHANGE:
            case CRUISECLIMB:
                switch (significantPointSecType) {
                case NAVAID:
                    if (navaidType == null)
                        return false;
                    else if (navaidType.toString().toUpperCase().equals(routePoint.pointType.toString().toUpperCase()))
                        return true;
                    else
                        return false;
                default:
                    return false;
                }
            default:
                return false;
            }
        }
        return false;
    }
}
