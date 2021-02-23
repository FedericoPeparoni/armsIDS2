package com.ubitech.aim.routeparser;

import java.util.LinkedList;
import java.util.regex.Matcher;

class Polygon extends Geometry {
    LinkedList<Ring> rings = null;

    Boolean isClosed = null;
    Boolean crossesDateLine = null;
    Boolean crossesPrimeMeridian = null;

    Double xMax = null;
    Double xMin = null;
    Double yMax = null;
    Double yMin = null;
    Point centerPoint = null;

    Polygon() {
        this.rings = new LinkedList<Ring>();
        SRID = GeogSRID;
    }

    Polygon(String polygonWKT) {
        this(polygonWKT, GeogSRID);
    }

    Polygon(String polygonWKT, Integer polygonSRID) {
    	setRings(polygonWKT, polygonSRID, maxNumOfRingsInPolygon);
    }

    Polygon(Matcher polygonMatcher, Integer polygonSRID) {
        setRings(polygonMatcher, polygonSRID);
    }

    Polygon(Polygon polygon) {
        setPolygon(polygon);
    }

    void setPolygon(Polygon polygon) {

        if (rings == null)
            rings = new LinkedList<Ring>();
        else
            rings.clear();

        rings.addAll(polygon.rings);
        WKT = polygon.WKT;
        SRID = polygon.SRID;

        isClosed = polygon.isClosed;
        crossesDateLine = polygon.crossesDateLine;
        crossesPrimeMeridian = polygon.crossesPrimeMeridian;

        xMax = polygon.xMax;
        xMin = polygon.xMin;
        yMax = polygon.yMax;
        yMin = polygon.yMin;
        centerPoint = polygon.centerPoint;
    }

    void setWKT(String polygonWKT) {
        setWKT(polygonWKT, GeogSRID);
    }

    void setWKT(String polygonWKT, Integer polygonSRID) {

        if (polygonWKT == null)
            throw new NullPointerException(
                    "'String polygonWKT' parameter of 'Polygon.setWKT' method cannot accept 'Null'.");

        if (polygonSRID == null)
            throw new NullPointerException(
                    "'Integer polygonSRID' parameter of 'Polygon.setWKT' method cannot accept 'Null'.");

        Matcher matcher = patternPOLYGON.matcher(polygonWKT);

        if (matcher.matches())
            setRings(matcher, polygonSRID);

        else {
            rings = null;
            SRID = null;
            WKT = null;
            clearParameters();

            String tempWKT;
            if (polygonWKT.length() > 100)
                tempWKT = polygonWKT.substring(0, 100) + " ...";
            else
                tempWKT = polygonWKT;

            throw new IllegalArgumentException(
                    "'" + tempWKT + "' \nstring cannot be parsed as POLYGON WKT in 'Polygon.setWKT' method.");
        }
    }

    void setRings(Matcher polygonMatcher, Integer polygonSRID) {
        setRings(polygonMatcher, polygonSRID, maxNumOfRingsInPolygon);
    }

    void setRings(Matcher polygonMatcher, Integer polygonSRID, int numOfRingsToExtract) {
    	
    	setRings(polygonMatcher.group(1), polygonSRID, numOfRingsToExtract);

    }
    
    private void setRings(String strOfRings, Integer polygonSRID, int numOfRingsToExtract) {

        if (rings == null)
            rings = new LinkedList<Ring>();
        else
            rings.clear();
        
        strOfRings = strOfRings.trim().replaceAll("POLYGON", "");
    	
        String ringArray[] = null;

        WKT = "POLYGON(";
        
        int i = 0;

        ringArray = strOfRings.split("\\)\\s*,\\s*\\(");
        int numOfRings = ringArray.length;

        for (i = 0; i < Math.min(numOfRings, numOfRingsToExtract); i++) {
        	rings.add(new Ring(ringArray[i], polygonSRID));
            WKT = WKT + (i > 0 ? "," : "") + rings.get(i).toString();
        }

        WKT = WKT + ")";

        SRID = polygonSRID;
    }


    public String getWKT() {
        return WKT;
    }

    Point getCenterPoint() {
        if (rings == null || rings.isEmpty())
            return null;
        if (centerPoint == null)
            calculateCenterPoint();
        return centerPoint;
    }

    void close() {
        super.close();
        clearParameters();
        rings.clear();
        rings = null;
    }

    Boolean coveresByBox(Polygon polygon) {
        return (isCoveredByBox(polygon, 0.0));
    }

    Boolean coveresByBox(Polygon polygon, double toleranceInMeters) {
        return (firstCoveredBySecondByBox(polygon, this, toleranceInMeters));
    }

    Boolean isCoveredByBox(Polygon polygon) {
        return (isCoveredByBox(polygon, 0.0));
    }

    Boolean isCoveredByBox(Polygon polygon, double toleranceInMeters) {
        return (firstCoveredBySecondByBox(this, polygon, toleranceInMeters));
    }

    static Boolean firstCoveredBySecondByBox(Polygon polygon1, Polygon polygon2, double toleranceInMeters) {
        if (polygon1.rings == null || polygon1.rings.isEmpty() || polygon1.rings.getFirst().vertexes == null || polygon1.rings.getFirst().vertexes.isEmpty() 
        		|| polygon2.rings == null || polygon2.rings.isEmpty() || polygon2.rings.getFirst().vertexes == null || polygon2.rings.getFirst().vertexes.isEmpty())
            return null;

        if (polygon1.centerPoint == null)
            polygon1.calculateCenterPoint();
        if (polygon2.centerPoint == null)
            polygon2.calculateCenterPoint();

        if (polygon1.yMax.doubleValue() > polygon2.yMax.doubleValue()) {
            if (toleranceInMeters <= 0.0)
                return new Boolean(false);

            else if (distanceInMeters(0.0, polygon1.yMax.doubleValue(), 0.0, polygon2.yMax.doubleValue())
                    .doubleValue() > toleranceInMeters)
                return new Boolean(false);
        }

        if (polygon1.yMin.doubleValue() < polygon2.yMin.doubleValue()) {
            if (toleranceInMeters <= 0.0)
                return new Boolean(false);

            else if (distanceInMeters(0.0, polygon1.yMin.doubleValue(), 0.0, polygon2.yMin.doubleValue())
                    .doubleValue() > toleranceInMeters)
                return new Boolean(false);
        }

        if (polygon2.crossesDateLine) {
            if (polygon1.crossesDateLine) {
                if (polygon1.xMax.doubleValue() > polygon2.xMax.doubleValue()) {
                    if (toleranceInMeters <= 0.0 || toleranceInMeters > 0.0
                            && (distanceInMeters(polygon1.xMax.doubleValue(), 0.0, polygon2.xMax.doubleValue(), 0.0)
                                    .doubleValue() > toleranceInMeters))
                        return new Boolean(false);
                }

                if (polygon1.xMin.doubleValue() < polygon2.xMin.doubleValue()) {
                    if (toleranceInMeters <= 0.0 || toleranceInMeters > 0.0
                            && (distanceInMeters(polygon1.xMin.doubleValue(), 0.0, polygon2.xMin.doubleValue(), 0.0)
                                    .doubleValue() > toleranceInMeters))
                        return new Boolean(false);
                }
            } else if (polygon1.xMax > polygon2.xMax && polygon1.xMin < polygon2.xMin) {
                if (toleranceInMeters <= 0.0)
                    return new Boolean(false);

                else if (distanceInMeters(polygon1.xMax.doubleValue(), 0.0, polygon2.xMax.doubleValue(), 0.0)
                        .doubleValue() > toleranceInMeters
                        && distanceInMeters(polygon1.xMin.doubleValue(), 0.0, polygon2.xMin.doubleValue(), 0.0)
                                .doubleValue() > toleranceInMeters) {
                    return new Boolean(false);
                }
            }
        } else {
            if (polygon1.crossesDateLine)
                return new Boolean(false);

            else {
                if (polygon1.xMax > polygon2.xMax) {
                    if (toleranceInMeters <= 0.0)
                        return new Boolean(false);

                    else if (distanceInMeters(polygon1.xMax.doubleValue(), 0.0, polygon2.xMax.doubleValue(), 0.0)
                            .doubleValue() > toleranceInMeters)
                        return new Boolean(false);
                }

                if (polygon1.xMin < polygon2.xMin) {
                    if (toleranceInMeters <= 0.0)
                        return new Boolean(false);

                    else if (distanceInMeters(polygon1.xMin.doubleValue(), 0.0, polygon2.xMin.doubleValue(), 0.0)
                            .doubleValue() > toleranceInMeters)
                        return new Boolean(false);
                }
            }
        }

        return new Boolean(true);
    }

    private void clearParameters() {

        WKT = null;
        isClosed = null;

        crossesDateLine = null;
        crossesPrimeMeridian = null;

        xMax = null;
        xMin = null;
        yMax = null;
        yMin = null;

        centerPoint = null;
    }

    private void calculateCenterPoint() {
        if (rings == null || rings.isEmpty() || rings.getFirst().vertexes == null
        		|| rings.getFirst().vertexes.isEmpty() || rings.getFirst().vertexes.size() < 3) {
        	
            throw new IllegalArgumentException(
                    "'Polygon.calculateCenterPoint()' private method has been called with illeagal 'Polygon.points' private variable. \nPlease notify the Developer.");
        }

        double startX = rings.getFirst().vertexes.get(0).getX();
        double startY = rings.getFirst().vertexes.get(0).getY();

        double prevX = startX;
        double prevXabs = Math.abs(prevX);
        double prevXsign = Math.signum(prevX);

        double tempX;
        double tempXabs;
        double tempBackX;
        double tempXsign;
        double tempY;

        double maxX = startX;
        double minX = startX;

        double maxY = startY;
        double minY = startY;

//		clearParameters();

        boolean crossesDateLineFlag = false;
        boolean crossesPrimeMeridianFlag = false;
        boolean searchForMaxExtentLongitude = false;
        xMax = null;
        xMin = null;
        yMax = null;
        yMin = null;
        centerPoint = null;

        int i = 0;

        for (Point point : rings.getFirst().vertexes) {
            if (i == 0) {
                i++;
                continue;
            }

            tempX = point.getX().doubleValue();
            tempY = point.getY().doubleValue();
            tempXsign = Math.signum(tempX);

            tempXabs = Math.abs(tempX);

            if (tempXabs == 180.0) {
                tempX = 180.0 * prevXsign;
                tempXsign = prevXsign;
            } else if (prevXabs == 180.0) {
                prevX = 180.0 * tempXsign;
                prevXsign = tempXsign;
            }

            if (crossesDateLineFlag) {

                if (tempXsign == prevXsign) {

                    if (searchForMaxExtentLongitude)
                        maxX = Double.max(maxX, tempX);

                    else
                        minX = Double.min(minX, tempX);

                } else {
                    if ((Double.max(prevX, tempX) - Double.min(prevX, tempX)) < 180) {

                        if (searchForMaxExtentLongitude)
                            maxX = Double.max(maxX, tempX);
                        else
                            minX = Double.min(minX, tempX);

                        if (!crossesPrimeMeridianFlag && (tempXsign == 0.0 || prevXsign == 0.0)) {
                            if (searchForMaxExtentLongitude) {
                                if (maxX > 0.0)
                                    crossesPrimeMeridianFlag = true;
                            } else if (minX < 0.0)
                                crossesPrimeMeridianFlag = true;
                        } else
                            crossesPrimeMeridianFlag = true;
                    } else if (tempXabs < 180.0) {
                        if (searchForMaxExtentLongitude) {
                            if (tempX > 0.0)
                                searchForMaxExtentLongitude = false;
                        } else if (tempX < 0.0)
                            searchForMaxExtentLongitude = true;
                    }

                    if (tempXabs != 180.0) {

                        if (searchForMaxExtentLongitude)
                            maxX = Double.max(maxX, tempX);

                        else
                            minX = Double.min(minX, tempX);
                    }
                }

                if (maxX >= minX) {
                    String tempWKT;
                    if (WKT.length() > 100)
                        tempWKT = WKT.substring(0, 100) + " ...";
                    else
                        tempWKT = WKT;

                    throw new IllegalArgumentException("Bounding box of the following Airspace '" + tempWKT
                            + "' \ncontains one of the Earth poles. \nThis type of Airspaces are currently not processed. \nPlease notify the Developer if there is a need to process such Airspaces.");
                }
            } else {
                if (tempXsign == prevXsign || tempXsign == 0.0 || prevXsign == 0.0) {
                    maxX = Double.max(maxX, tempX);
                    minX = Double.min(minX, tempX);
                } else if (tempXsign == -prevXsign) {
                    if ((Double.max(prevX, tempX) - Double.min(prevX, tempX)) <= 180) {
                        maxX = Double.max(maxX, tempX);
                        minX = Double.min(minX, tempX);
                        crossesPrimeMeridianFlag = true;
                    } else {
                        if (tempXabs == 180.0) {
                            if (prevXabs == 180.0) {
                            } else if (prevX > 0.0)
                                maxX = 180;
                            else if (prevX < 0.0)
                                minX = -180.0;
                            else {
                                if (maxX <= 0.0)
                                    minX = -180.0;
                                else if (minX >= 0.0)
                                    maxX = 180.0;
                                else if (Math.abs(maxX) < Math.abs(minX))
                                    minX = -180.0;
                                else
                                    maxX = 180.0;
                            }
                        } else if (prevXabs == 180.0) {
                            if (i > 1) {
                                for (int j = (i - 2); j >= 0; j--) {
                                    tempBackX = rings.getFirst().vertexes.get(j).getX().doubleValue();
                                    if (Math.abs(tempBackX) < 180.0) {
                                        if (tempXsign == -Math.signum(tempBackX)) {
                                            crossesDateLineFlag = true;
                                            if (tempXsign == 1)
                                                searchForMaxExtentLongitude = false;
                                            else if (tempXsign == -1)
                                                searchForMaxExtentLongitude = true;
                                        }
                                        break;
                                    }
                                }
                            }
                            if (crossesDateLineFlag) {
                                if (searchForMaxExtentLongitude)
                                    maxX = tempX;
                                else
                                    minX = tempX;

                                if (maxX >= minX) {
                                    String tempWKT;
                                    if (WKT.length() > 100)
                                        tempWKT = WKT.substring(0, 100) + " ...";
                                    else
                                        tempWKT = WKT;

                                    throw new IllegalArgumentException("Bounding box of the following Airspace '"
                                            + tempWKT
                                            + "' \ncontains one of the Earth poles. \nThis type of Airspaces are currently not processed. \nPlease notify the Developer if there is a need to process such Airspaces.");
                                }
                            } else {
                                if (tempXsign == 1) {
                                    if (Math.abs(minX) == 180.0)
                                        minX = 180.0;
                                    minX = Double.min(minX, tempX);
                                } else if (tempXsign == -1) {
                                    if (Math.abs(maxX) == 180.0)
                                        maxX = -180.0;
                                    maxX = Double.max(maxX, tempX);
                                }
                            }
                        }
                    }
                }
            }

            maxY = Double.max(maxY, tempY);
            minY = Double.min(minY, tempY);

            prevX = tempX;
            prevXsign = tempXsign;
            prevXabs = tempXabs;
            i++;
        }

        xMax = new Double(maxX);
        xMin = new Double(minX);
        yMax = new Double(maxY);
        yMin = new Double(minY);

        crossesDateLine = new Boolean(crossesDateLineFlag);
        crossesPrimeMeridian = new Boolean(crossesPrimeMeridianFlag);

        if (crossesDateLineFlag) {

            double dblTemp = (360.0 - minX + maxX) / 2;

            if (((minX + maxX) / 2) > 0.0)
                tempX = maxX - dblTemp;

            else
                tempX = minX + dblTemp;
        } else {
            tempX = (minX + maxX) / 2;
        }

        tempY = (minY + maxY) / 2;

        this.centerPoint = new Point(tempX, tempY, SRID);
    }

    @Override
    public String toString() {
    	if (WKT == null) return "";
    	else return WKT.replaceAll("POLYGON", "");
    }
}
