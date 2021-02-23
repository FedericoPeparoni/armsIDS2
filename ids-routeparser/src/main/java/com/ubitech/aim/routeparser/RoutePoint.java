package com.ubitech.aim.routeparser;

import java.sql.Timestamp;
import java.util.ArrayList;

public class RoutePoint extends Point {
    public String designator = null; // should store only Designator attribute value of a feature found in the
                                     // database.
    String name = null; // should store only Name attribute value of a feature found in the database.
    public RoutePointType pointType = null;
    Timestamp timestamp = null; // should store effective time for all other feature attribute values, stored in
                                // the variables above.
    Long recordID = null; // should store only database record ID of a feature found in the database.

    boolean connectedToRoute = false;
    boolean isCredible = false;
    boolean prevConnected = false;
    boolean nextConnected = false;
    int parsedPointIndex = -1;
    double prevDist = -1;
    double nextDist = -1;

//	The following two variables are used only for displaying on the map.=================
//	Please DO NOT use them for other purposes.===========================================
    String displayName = null;
    ArrayList<DisplayAttribute> displayAttributes = null;
//	=====================================================================================

    RoutePoint() {
    }

    RoutePoint(int capacity) {
        displayAttributes = new ArrayList<DisplayAttribute>(capacity);
    }

    RoutePoint(Point point) {
        super(point);
    }

    RoutePoint(TokenInterpretation tokenInterpretation, Timestamp timeStamp) {
        switch (tokenInterpretation.tokenPrimaryType) {
        case ADHP:
        case SIGNIFICANT_POINT:
        case SID_ROUTE:
        case STAR_ROUTE:
            break;
        default:
            return;
        }

        this.designator = tokenInterpretation.parsedFeatureDesignator;
        this.name = tokenInterpretation.name;
        this.setCoordinates(tokenInterpretation.getLongitude(), tokenInterpretation.getLatitude());
        this.timestamp = (Timestamp) timeStamp.clone();
        this.recordID = tokenInterpretation.timeSliceRecID;
        this.parsedPointIndex = tokenInterpretation.parsedPointIndex;
        this.connectedToRoute = tokenInterpretation.connectedToRoute;

        switch (tokenInterpretation.tokenPrimaryType) {
        case ADHP:
            this.pointType = RoutePointType.ADHP;
            if (this.name != null && !this.name.equalsIgnoreCase("UNKNOWN")) {
                this.displayName = this.name;
                if (this.designator == null)
                    this.designator = "ZZZZ";
                this.addDisplayAttribute("Designator", this.designator);
            } else if (this.designator != null) {
                this.displayName = this.designator;
                this.addDisplayAttribute("Designator", this.designator);
            } else if (tokenInterpretation.tokenValue != null) {
                this.designator = "ZZZZ";
                this.displayName = tokenInterpretation.tokenValue;
                this.addDisplayAttribute("Designator", this.designator);
            }
            this.addDisplayAttribute("Point type", tokenInterpretation.tokenPrimaryType.toString());
            break;

        case SIGNIFICANT_POINT:
        case SID_ROUTE:
        case STAR_ROUTE:
            switch (tokenInterpretation.significantPointPrimType) {
            case NAVAID:
//				this.pointType = RoutePointType.valueOf(tokenInterpretation.navaidType.toString());
                if (this.name != null && !this.name.equalsIgnoreCase("UNKNOWN")) {
                    this.displayName = this.name;
                    if (this.designator != null)
                        this.addDisplayAttribute("Designator", this.designator);
                } else if (this.designator != null) {
                    this.displayName = this.designator;
                    this.addDisplayAttribute("Designator", this.designator);
                }
                if (tokenInterpretation.navaidType != null) {
                    this.pointType = RoutePointType.valueOf(tokenInterpretation.navaidType.toString());
                    this.addDisplayAttribute("Navaid type", this.pointType.toString());
                } else {
                    this.pointType = RoutePointType.OTHER;
                }
                break;
            case BEARINGDISTANCE:
                if (tokenInterpretation.tokenValue != null) {
                    this.displayName = tokenInterpretation.tokenValue;
                    if (this.name != null)
                        this.addDisplayAttribute("Name", this.name);
                    if (this.designator != null)
                        this.addDisplayAttribute("Designator", this.designator);
                } else if (this.name != null && !this.name.equalsIgnoreCase("UNKNOWN")) {
                    this.displayName = this.name;
                    if (this.designator != null)
                        this.addDisplayAttribute("Designator", this.designator);
                } else if (this.designator != null) {
                    this.displayName = this.designator;
                    this.addDisplayAttribute("Designator", this.designator);
                }
                if (tokenInterpretation.navaidType != null) {
                    this.pointType = RoutePointType.valueOf(tokenInterpretation.navaidType.toString());
                    this.addDisplayAttribute("Navaid type", this.pointType.toString());
                } else {
                    this.pointType = RoutePointType.OTHER;
                }
                break;
            case DESIGNATEDPOINT:
                this.pointType = RoutePointType.Waypoint;
                if (this.name != null && !this.name.equalsIgnoreCase("UNKNOWN")) {
                    this.displayName = this.name;
                    if (this.designator != null)
                        this.addDisplayAttribute("Designator", this.designator);
                } else if (this.designator != null) {
                    this.displayName = this.designator;
                    this.addDisplayAttribute("Designator", this.designator);
                }
                this.addDisplayAttribute("Point type", "Waypoint");
                break;
            case DEGREESONLY:
                this.pointType = RoutePointType.Waypoint;
                this.displayName = tokenInterpretation.tokenValue;
                this.addDisplayAttribute("Point type", "Degrees only");
                break;
            case DEGREESANDMINUTES:
                this.pointType = RoutePointType.Waypoint;
                this.displayName = tokenInterpretation.tokenValue;
                this.addDisplayAttribute("Point type", "Degrees and minutes");
                break;
            case DEGREES_MINUTES_SECONDS:
                this.pointType = RoutePointType.Waypoint;
                this.displayName = tokenInterpretation.tokenValue;
                this.addDisplayAttribute("Point type", "Degrees Minutes Seconds");
                break;
            case SPEEDORLEVELCHANGE:
            case CRUISECLIMB:
                switch (tokenInterpretation.significantPointSecType) {
                case NAVAID:
                    if (this.name != null && !this.name.equalsIgnoreCase("UNKNOWN")) {
                        this.displayName = this.name;
                        if (this.designator != null)
                            this.addDisplayAttribute("Designator", this.designator);
                    } else if (this.designator != null) {
                        this.displayName = this.designator;
                        this.addDisplayAttribute("Designator", this.designator);
                    }
                    if (tokenInterpretation.navaidType != null) {
                        this.pointType = RoutePointType.valueOf(tokenInterpretation.navaidType.toString());
                        this.addDisplayAttribute("Navaid type", this.pointType.toString());
                    } else {
                        this.pointType = RoutePointType.OTHER;
                    }
                    break;
                case BEARINGDISTANCE:
                    if (tokenInterpretation.tokenValue != null) {
                        this.displayName = tokenInterpretation.tokenValue;
                        if (this.name != null)
                            this.addDisplayAttribute("Name", this.name);
                        if (this.designator != null)
                            this.addDisplayAttribute("Designator", this.designator);
                    } else if (this.name != null && !this.name.equalsIgnoreCase("UNKNOWN")) {
                        this.displayName = this.name;
                        if (this.designator != null)
                            this.addDisplayAttribute("Designator", this.designator);
                    } else if (this.designator != null) {
                        this.displayName = this.designator;
                        this.addDisplayAttribute("Designator", this.designator);
                    }
                    if (tokenInterpretation.navaidType != null) {
                        this.pointType = RoutePointType.valueOf(tokenInterpretation.navaidType.toString());
                        this.addDisplayAttribute("Navaid type", this.pointType.toString());
                    } else {
                        this.pointType = RoutePointType.OTHER;
                    }
                    break;
                case DESIGNATEDPOINT:
                    this.pointType = RoutePointType.Waypoint;
                    if (this.name != null && !this.name.equalsIgnoreCase("UNKNOWN")) {
                        this.displayName = this.name;
                        if (this.designator != null)
                            this.addDisplayAttribute("Designator", this.designator);
                    } else if (this.designator != null) {
                        this.displayName = this.designator;
                        this.addDisplayAttribute("Designator", this.designator);
                    }
                    this.addDisplayAttribute("Point type", "Waypoint");
                    break;
                case DEGREESONLY:
                    this.pointType = RoutePointType.Waypoint;
                    this.displayName = tokenInterpretation.tokenValue;
                    this.addDisplayAttribute("Point type", "Degrees only");
                    break;
                case DEGREESANDMINUTES:
                    this.pointType = RoutePointType.Waypoint;
                    this.displayName = tokenInterpretation.tokenValue;
                    this.addDisplayAttribute("Point type", "Degrees and minutes");
                    break;
                default:
                    return;
                }
                break;
            default:
                return;
            }
            break;
        default:
        }
    }

    RoutePoint getClone() {
        RoutePoint toReturn = new RoutePoint();

        toReturn.designator = this.designator;
        toReturn.name = this.name;
        toReturn.pointType = this.pointType;
        toReturn.setCoordinates(this.getX(), this.getY());
        toReturn.timestamp = (Timestamp) this.timestamp.clone();
        toReturn.recordID = this.recordID;
        toReturn.parsedPointIndex = this.parsedPointIndex;

        toReturn.displayName = this.displayName;

        if (this.displayAttributes != null) {
            ArrayList<DisplayAttribute> displayAttributes = new ArrayList<DisplayAttribute>(
                    this.displayAttributes.size());
            for (int i = 0; i < this.displayAttributes.size(); i++) {
                displayAttributes.add(new DisplayAttribute(this.displayAttributes.get(i).getAttrName(),
                        this.displayAttributes.get(i).getAttrValue()));
            }
        }

        return toReturn;
    }

    public Boolean isAtSameLocation(Point point) {
        return equals(point);
    }

    void addDisplayAttribute(String attrName, String attrValue) {
        if (displayAttributes == null)
            displayAttributes = new ArrayList<DisplayAttribute>(3);
        displayAttributes.add(new DisplayAttribute(attrName, attrValue));
    }

    /**
     * Returns String value, which contains the most representative name of a route
     * node (fix) for visualization on a map.
     * 
     * @return String value of a fix's display name attribute. The returned value
     *         can be based on different attributes of a route fix. If the fix is a
     *         feature, found in the database, either a name or (if name is empty in
     *         the database) a designator attribute value will be returned. If the
     *         fix is based on explicitly printed geographical coordinates, an
     *         entire token value will be returned.
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Returns Double Latitude value of the route fix's geographical location.
     * 
     * @return Double Latitude value of the route fix's geographical location.
     */
    public Double getLatitude() {
        return getY();
    }

    /**
     * Returns Double Longitude value of the route fix's geographical location.
     * 
     * @return Double Longitude value of the route fix's geographical location.
     */
    public Double getLongitude() {
        return getX();
    }

    /**
     * Returns ArrayList of DisplayAttribute class instances for visualization of
     * additional attributes of a route fix on a map.
     * 
     * @return ArrayList<DisplayAttribute> of additional attributes of a route fix
     *         for visualization on a map.
     */
    public ArrayList<DisplayAttribute> getDisplayAttributes() {
        return this.displayAttributes;
    }

    void fillDisplayAttributes() {
        fillDisplayAttributes(false);
    }

    void fillDisplayAttributes(boolean typeInclusively) {
        if (displayAttributes != null && !displayAttributes.isEmpty())
            displayAttributes.clear();

        if (this.name != null && !this.name.equalsIgnoreCase("UNKNOWN")) {
            this.displayName = this.name;
            if (this.designator != null)
                this.addDisplayAttribute("Designator", this.designator);
        }

        else if (this.designator != null) {
            this.displayName = this.designator;
            this.addDisplayAttribute("Designator", this.designator);
        }

        if (typeInclusively && this.pointType != null && this.pointType.ordinal() > RoutePointType.OTHER.ordinal()) {
            if (this.pointType.ordinal() > RoutePointType.Waypoint.ordinal())
                this.addDisplayAttribute("Navaid type", this.pointType.toString());
            else if (this.pointType.ordinal() > RoutePointType.OTHER.ordinal())
                this.addDisplayAttribute("Point type", this.pointType.toString());
        }
    }
    
    @Override
    public String toString() {
        return String.format ("designator=%s; %s", this.designator, super.toString());
    }
}
