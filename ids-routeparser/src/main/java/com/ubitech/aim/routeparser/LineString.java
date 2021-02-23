package com.ubitech.aim.routeparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

class LineString extends Geometry {
    LinkedList<Point> points = null;

    Boolean isClosed = null;
    Boolean crossesDateLine = null;
    Boolean crossesPrimeMeridian = null;
    Boolean sunPathOriented = null;
    Boolean northToSouthOriented = null;
    boolean connectedByStartPoint = false;
    boolean connectedByEndPoint = false;

    Double xMax = null;
    Double xMin = null;
    Double yMax = null;
    Double yMin = null;

    Integer numOfSameBeginningEndPoints = null;
    Double longitudinalSpan = null;
    Double startEndLongitudinalDiff = null;
    Point centerPoint = null;

    Double lengthInMeters = null;

//	int indexInMultiLine = -1;
    double xyOffset = 0.0;

    LineString() {
        this.points = new LinkedList<Point>();
        SRID = GeogSRID;
    }

    LineString(LineString lineString) {
        setLineString(lineString);
    }

    LineString(String lineWKT) {
        this(lineWKT, GeogSRID);
    }

    LineString(String lineWKT, Integer lineSRID) {
        setWKT(lineWKT, lineSRID);
    }

    LineString(Matcher lineMatcher, Integer lineSRID) {
        setWKT(lineMatcher, lineSRID);
    }

    LineString(List<Point> points) {
        this(points, GeogSRID);
    }

    LineString(List<Point> points, Integer lineSRID) {
        SRID = lineSRID;
        setLinePoints(points);
    }

    private void clearParameters() {

        isClosed = null;

        crossesDateLine = null;
        crossesPrimeMeridian = null;
        sunPathOriented = null;
        northToSouthOriented = null;

        connectedByStartPoint = false;
        connectedByEndPoint = false;

        xMax = null;
        xMin = null;
        yMax = null;
        yMin = null;

        numOfSameBeginningEndPoints = null;
        longitudinalSpan = null;
        startEndLongitudinalDiff = null;
        centerPoint = null;

//		indexInMultiLine = -1;
        xyOffset = 0.0;
    }

    void reverse() {
        if (points == null || points.isEmpty())
            return;
        Collections.reverse(points);
        WKT = null;

        if (sunPathOriented != null)
            sunPathOriented = !sunPathOriented;
        if (northToSouthOriented != null)
            northToSouthOriented = !sunPathOriented;

        boolean blnTemp = connectedByStartPoint;
        connectedByStartPoint = connectedByEndPoint;
        connectedByEndPoint = blnTemp;
    }

    String getWKT() {
        if (points == null || points.isEmpty()) {
            WKT = null;
        } else if (WKT == null) {
            WKT = "LINESTRING(";

            for (Point point : points) {
                WKT = WKT + point.getX().toString() + " " + point.getY().toString() + ",";
            }
            WKT = WKT.substring(0, WKT.length() - 1) + ")";
        }
        return WKT;
    }

    Point getCenterPoint() {
        if (points == null || points.isEmpty())
            return null;
        if (centerPoint == null)
            calculateCenterPoint();
        return centerPoint;
    }

    void close() {
        super.close();
        if (points != null) {
            points.clear();
            points = null;
        }
        clearParameters();
    }

    int getNumOfPoints() {
        if (points == null || points.isEmpty())
            return 0;

        return points.size();
    }

    Point getFirstPoint() {
        if (points == null)
            return null;

        return points.peekFirst();
    }

    Point getLastPoint() {
        if (points == null)
            return null;

        return points.peekLast();
    }

    Point getPoint(int index) {
        if (points == null || points.isEmpty())
            return null;

        if (index >= points.size())
            throw new IndexOutOfBoundsException("'" + index
                    + "' value, transfered to 'int index' parameter of 'LineString.getPoint' method, exceeds number of stored points.");

        return points.get(index);
    }

    LinkedList<Point> getPoints() {
        if (points == null || points.isEmpty())
            return null;

        LinkedList<Point> listOfPoints = new LinkedList<Point>();
        listOfPoints.addAll(points);
        return listOfPoints;
    }

    Double getLengthInMeters() {
        return lengthInMeters;
    }

    void setWKT(String lineWKT) {
        setWKT(lineWKT, GeogSRID);
    }

    double getXYoffset(LineString baseLine) {
        xyOffset = getCenterPoint().getXYoffset(baseLine);
        return xyOffset;
    }

    void setXYoffset(LineString baseLine) {
//		indexInMultiLine = -1;
        xyOffset = getCenterPoint().getXYoffset(baseLine);
    }

    void setWKT(String lineWKT, Integer lineSRID) {

        if (lineWKT == null)
            throw new NullPointerException(
                    "'String lineWKT' parameter of 'LineString.setWKT' method cannot accept 'Null'.");

        if (lineSRID == null)
            throw new NullPointerException(
                    "'Integer lineSRID' parameter of 'LineString.setWKT' method cannot accept 'Null'.");

        Matcher matcher = patternLINESTRING.matcher(lineWKT);

        if (matcher.matches())
            setWKT(matcher, lineSRID);

        else {
            points = null;
            SRID = null;
            WKT = null;
            clearParameters();

            String tempWKT;
            if (lineWKT.length() > 100)
                tempWKT = lineWKT.substring(0, 100);
            else
                tempWKT = lineWKT;

            throw new IllegalArgumentException("'" + tempWKT + "..."
                    + "' string cannot be parsed as LINESTRING WKT in 'LineString.setWKT' method.");
        }
    }

    private void setWKT(Matcher lineMatcher, Integer lineSRID) {

        if (points == null)
            points = new LinkedList<Point>();
        else
            points.clear();

        clearParameters();

        String coordPairsArray[] = null;
        int i = 0;

        try {
            String stringOfCoordPairs = lineMatcher.group(1);
            coordPairsArray = stringOfCoordPairs.split(",");
            int numOfPoints = coordPairsArray.length;
            WKT = "LINESTRING(";
            Point tempPoint;

            for (i = 0; i < numOfPoints; i++) {
                String coordPair[] = coordPairsArray[i].trim().split("\\s+");
                tempPoint = new Point(Double.valueOf(coordPair[0]), Double.valueOf(coordPair[1]), lineSRID);
                points.add(tempPoint);
                WKT = WKT + tempPoint.getX().toString() + " " + tempPoint.getY().toString() + ",";
            }

            WKT = WKT.substring(0, WKT.length() - 1) + ")";
            SRID = lineSRID;

        } catch (NumberFormatException e) {
            SRID = null;
            WKT = null;
            throw new NumberFormatException("Coordinate pair '" + coordPairsArray[i].trim()
                    + "' extracted from LINESTRING WKT cannot be parsed as numbers in 'LineString.setWKT' method.");
        }
    }

    void setLineString(LineString lineString) {

        if (points != null) {
            points.clear();
            points = null;
        }
        points = lineString.getPoints();
        WKT = lineString.WKT;
        SRID = lineString.SRID;

        isClosed = lineString.isClosed;
        crossesDateLine = lineString.crossesDateLine;
        crossesPrimeMeridian = lineString.crossesPrimeMeridian;
        sunPathOriented = lineString.sunPathOriented;
        northToSouthOriented = lineString.northToSouthOriented;
        connectedByStartPoint = lineString.connectedByStartPoint;
        connectedByEndPoint = lineString.connectedByEndPoint;

        xMax = lineString.xMax;
        xMin = lineString.xMin;
        yMax = lineString.yMax;
        yMin = lineString.yMin;

        numOfSameBeginningEndPoints = lineString.numOfSameBeginningEndPoints;
        longitudinalSpan = lineString.longitudinalSpan;
        startEndLongitudinalDiff = lineString.startEndLongitudinalDiff;
        if (lineString.centerPoint != null)
            centerPoint = new Point(lineString.centerPoint);

        lengthInMeters = lineString.lengthInMeters;

//		indexInMultiLine = lineString.indexInMultiLine;
        xyOffset = lineString.xyOffset;
    }

    void setLinePoints(List<Point> inputPoints) {

        if (points == null)
            this.points = new LinkedList<Point>();
        else if (!points.isEmpty())
            points.clear();

        clearParameters();

        for (Point point : inputPoints)
            if (point.SRID == null)
                point.SRID = GeogSRID;

        points.addAll(inputPoints);
    }

    void addPoint(Point point) {
        if (point == null)
            throw new NullPointerException(
                    "'Point point' parameter of 'LineString.addPoint' method cannot accept 'Null'.");
        if (point.getX() == null || point.getY() == null)
            throw new NullPointerException(
                    "'x' and 'y' parameters of 'Point' instance should not be 'Null' when it is transfered as a parameter to 'LineString.addPoint' method.");
        if (point.SRID == null)
            throw new NullPointerException(
                    "'SRID' parameter of 'Point' instance should not be 'Null' when it is transfered as a parameter to 'LineString.addPoint' method.");

        if (SRID == null)
            throw new NullPointerException(
                    "'SRID' parameter of 'LineString' instance should not be 'Null' when 'LineString.addPoint' method is called.");

        if (!point.SRID.equals(SRID))
            throw new IllegalArgumentException(
                    "'SRID' parameter of 'Point' instance is NOT equivalent to destination SRID while it is transfered as a parameter to 'LineString.addPoint' method.");

        if (points == null)
            points = new LinkedList<Point>();

        points.add(point);

        if (WKT != null) {
            WKT = WKT.substring(0, WKT.length() - 1);
            WKT = WKT + "," + point.getX().toString() + " " + point.getY().toString() + ")";
        }

//		clearParameters();

        isClosed = null;

        crossesDateLine = null;
        crossesPrimeMeridian = null;
        sunPathOriented = null;
        northToSouthOriented = null;

//		connectedByStartPoint = false;
        connectedByEndPoint = false;

        xMax = null;
        xMin = null;
        yMax = null;
        yMin = null;

        numOfSameBeginningEndPoints = null;
        longitudinalSpan = null;
        startEndLongitudinalDiff = null;
        centerPoint = null;

        xyOffset = 0.0;
    }

    void addFirst(Point point) {
        if (point == null)
            throw new NullPointerException(
                    "'Point point' parameter of 'LineString.addPoint' method cannot accept 'Null'.");
        if (point.getX() == null || point.getY() == null)
            throw new NullPointerException(
                    "'x' and 'y' parameters of 'Point' instance should not be 'Null' when it is transfered as a parameter to 'LineString.addPoint' method.");
        if (point.SRID == null)
            throw new NullPointerException(
                    "'SRID' parameter of 'Point' instance should not be 'Null' when it is transfered as a parameter to 'LineString.addPoint' method.");

        if (SRID == null)
            throw new NullPointerException(
                    "'SRID' parameter of 'LineString' instance should not be 'Null' when 'LineString.addPoint' method is called.");

        if (!point.SRID.equals(SRID))
            throw new IllegalArgumentException(
                    "'SRID' parameter of 'Point' instance is NOT equivalent to destination SRID while it is transfered as a parameter to 'LineString.addPoint' method.");

        if (points == null)
            points = new LinkedList<Point>();

        points.addFirst(point);

        if (WKT != null) {
            WKT = WKT.substring(11, WKT.length());
            WKT = "LINESTRING(" + point.getX().toString() + " " + point.getY().toString() + "," + "WKT";
        }

//		clearParameters();

        isClosed = null;

        crossesDateLine = null;
        crossesPrimeMeridian = null;
        sunPathOriented = null;
        northToSouthOriented = null;

        connectedByStartPoint = false;
//		connectedByEndPoint = false;

        xMax = null;
        xMin = null;
        yMax = null;
        yMin = null;

        numOfSameBeginningEndPoints = null;
        longitudinalSpan = null;
        startEndLongitudinalDiff = null;
        centerPoint = null;

        xyOffset = 0.0;
    }

    void addLast(Point point) {
        addPoint(point);
    }

    void addPoint(int ind, Point point) {
        if (point == null)
            throw new NullPointerException(
                    "'Point point' parameter of 'LineString.addPoint' method cannot accept 'Null'.");
        if (point.getX() == null || point.getY() == null)
            throw new NullPointerException(
                    "'x' and 'y' parameters of 'Point' instance should not be 'Null' when it is transfered as a parameter to 'LineString.addPoint' method.");
        if (point.SRID == null)
            throw new NullPointerException(
                    "'SRID' parameter of 'Point' instance should not be 'Null' when it is transfered as a parameter to 'LineString.addPoint' method.");

        if (SRID == null)
            throw new NullPointerException(
                    "'SRID' parameter of 'LineString' instance should not be 'Null' when 'LineString.addPoint' method is called.");

        if (!point.SRID.equals(SRID))
            throw new IllegalArgumentException(
                    "'SRID' parameter of 'Point' instance is NOT equivalent to destination SRID while it is transfered as a parameter to 'LineString.addPoint' method.");

        if (points == null)
            points = new LinkedList<Point>();

        if (ind < 0 || ind > points.size()) {
            throw new IndexOutOfBoundsException("'" + ind
                    + "' value transfered to 'int ind' parameter of 'LineString.addPoint' method is out of bounds.");
        }

        points.add(ind, point);

//		clearParameters();

        isClosed = null;

        crossesDateLine = null;
        crossesPrimeMeridian = null;
        sunPathOriented = null;
        northToSouthOriented = null;

        if (ind == 0)
            connectedByStartPoint = false;
        if (ind == (points.size() - 1))
            connectedByEndPoint = false;

        xMax = null;
        xMin = null;
        yMax = null;
        yMin = null;

        numOfSameBeginningEndPoints = null;
        longitudinalSpan = null;
        startEndLongitudinalDiff = null;
        centerPoint = null;

        xyOffset = 0.0;

        WKT = null;
    }

    boolean addAllPoints(List<Point> listOfPoints) {
        if (SRID == null)
            throw new NullPointerException(
                    "'SRID' parameter of 'LineString' instance should not be 'Null' when 'LineString.addAllPoints' method is called.");

        if (listOfPoints == null)
            throw new NullPointerException(
                    "'List<Point> listOfPoints' parameter of 'LineString.addAllPoints' method cannot accept 'Null'.");

        if (listOfPoints.isEmpty())
            return true;

        if (points == null)
            points = new LinkedList<Point>();

        for (Point point : listOfPoints)
            addPoint(point);

//		clearParameters();
//		WKT = null;

        return true;
    }

    Point removePoint(int index) {
        return points.remove(index);
    }

    Point removeFirst() {
        return points.removeFirst();
    }

    Point removeLast() {
        return points.removeLast();
    }

    boolean addAllPointsToFront(List<Point> listOfPoints) {
        if (SRID == null)
            throw new NullPointerException(
                    "'SRID' parameter of 'LineString' instance should not be 'Null' when 'LineString.addAllPoints' method is called.");

        if (listOfPoints == null)
            throw new NullPointerException(
                    "'List<Point> listOfPoints' parameter of 'LineString.addAllPoints' method cannot accept 'Null'.");

        if (listOfPoints.isEmpty())
            return true;

        if (points == null)
            points = new LinkedList<Point>();

        if (listOfPoints instanceof LinkedList) {
            Iterator<Point> pointsDescendingIterator = ((LinkedList) listOfPoints).descendingIterator();
            while (pointsDescendingIterator.hasNext())
                points.addFirst(pointsDescendingIterator.next());
        } else if (listOfPoints instanceof ArrayList) {
            for (int i = (listOfPoints.size() - 1); i >= 0; i--) {
                points.addFirst(listOfPoints.get(i));
            }
        }

//		clearParameters();

        isClosed = null;

        crossesDateLine = null;
        crossesPrimeMeridian = null;
        sunPathOriented = null;
        northToSouthOriented = null;

        connectedByStartPoint = false;
//		connectedByEndPoint = false;

        xMax = null;
        xMin = null;
        yMax = null;
        yMin = null;

        numOfSameBeginningEndPoints = null;
        longitudinalSpan = null;
        startEndLongitudinalDiff = null;
        centerPoint = null;

        xyOffset = 0.0;

        WKT = null;

        return true;
    }

    private void calculateCenterPoint() {
        if (points == null || points.isEmpty())
            return;

        int numOfPoints = points.size();
        this.numOfSameBeginningEndPoints = new Integer(getNumOfSameBeginningEndPoints());

        if (numOfSameBeginningEndPoints.intValue() > 0) {
            isClosed = new Boolean(true);
            if (numOfSameBeginningEndPoints.intValue() > 1)
                numOfPoints = numOfPoints - numOfSameBeginningEndPoints.intValue();
        } else
            isClosed = new Boolean(false);

        double startX = points.getFirst().getX();
        double startY = points.getFirst().getY();
        double endX = points.get(numOfPoints - 1).getX();
        double endY = points.get(numOfPoints - 1).getY();

        double prevX = startX;
        double prevXsign = Math.signum(startX);

        double tempX = 0;
        double tempXsign;
        double tempY;

        double maxX = startX;
        double minX = startX;

        double maxY = startY;
        double minY = startY;

//		clearParameters();

        crossesDateLine = new Boolean(false);
        crossesPrimeMeridian = new Boolean(false);
        sunPathOriented = null;
        northToSouthOriented = null;
        xMax = null;
        xMin = null;
        yMax = null;
        yMin = null;
        longitudinalSpan = null;
        startEndLongitudinalDiff = null;
        centerPoint = null;

        for (int i = 1; i < numOfPoints; i++) {
            tempX = points.get(i).getX().doubleValue();
            tempY = points.get(i).getY().doubleValue();
            tempXsign = Math.signum(tempX);

            if (Math.abs(tempX) == 180.0) {
                tempX = 180.0 * prevXsign;
                tempXsign = prevXsign;
            } else if (Math.abs(prevX) == 180.0) {
                prevX = 180.0 * tempXsign;
                prevXsign = tempXsign;
            }

            if (tempXsign == prevXsign || tempXsign == 0.0 || prevXsign == 0.0) {
                if (crossesDateLine) {
                    if (tempXsign == 0.0 || prevXsign == 0.0) {
                        throw new IllegalArgumentException("'" + this.getWKT()
                                + "' Route WKT crosses both Prime Meridian and Date Line. Cannot process it. Please notify the Developer.");
                    }
                    if (tempXsign == 1.0)
                        minX = Double.min(minX, tempX);
                    else if (tempXsign == -1.0)
                        maxX = Double.max(maxX, tempX);
                } else {
                    maxX = Double.max(maxX, tempX);
                    minX = Double.min(minX, tempX);
                }
            } else {
                if ((Math.abs(prevX) + Math.abs(tempX)) <= 180.0) {
                    crossesPrimeMeridian = new Boolean(true);
                    if (crossesDateLine) {
                        throw new IllegalArgumentException("'" + this.getWKT()
                                + "' Route WKT crosses both Prime Meridian and Date Line. Cannot process it. Please notify the Developer.");
                    }
                    maxX = Double.max(maxX, tempX);
                    minX = Double.min(minX, tempX);
                } else {
                    if (crossesPrimeMeridian) {
                        throw new IllegalArgumentException("'" + this.getWKT()
                                + "' Route WKT crosses both Prime Meridian and Date Line. Cannot process it. Please notify the Developer.");
                    }

                    if (crossesDateLine) {
                        if (tempXsign == 1.0)
                            minX = Double.min(minX, tempX);
                        else if (tempXsign == -1.0)
                            maxX = Double.max(maxX, tempX);
                    } else {
                        if (tempXsign == 1.0)
                            minX = tempX;
                        else if (tempXsign == -1.0)
                            maxX = tempX;

                        crossesDateLine = new Boolean(true);
                    }
                }
            }

            maxY = Double.max(maxY, tempY);
            minY = Double.min(minY, tempY);

            prevX = tempX;
            prevXsign = tempXsign;
        }

        if (crossesPrimeMeridian && (maxX == 0.0 || minX == 0.0))
            crossesPrimeMeridian = new Boolean(false);

        if (crossesDateLine && (Math.abs(maxX) == 180.0 || Math.abs(minX) == 180.0)) {

            crossesDateLine = new Boolean(false);

            if (Math.abs(maxX) == 180.0)
                maxX = 180.0 * Math.signum(minX);
            else if (Math.abs(minX) == 180.0)
                minX = 180.0 * Math.signum(maxX);
        }

        xMax = new Double(maxX);
        xMin = new Double(minX);
        yMax = new Double(maxY);
        yMin = new Double(minY);

        if (crossesDateLine) {

            double dblTemp = (360.0 - Math.abs(maxX) - Math.abs(minX)) / 2;

            if (Math.abs(minX) > Math.abs(maxX))
                tempX = maxX - dblTemp;

            else
                tempX = minX + dblTemp;

            longitudinalSpan = new Double(360.0 + maxX - minX);

            if (startX < 0) {
                if (endX > 0) {
                    sunPathOriented = new Boolean(true);
                    startEndLongitudinalDiff = new Double(360.0 + startX - endX);
                } else {
                    startEndLongitudinalDiff = new Double(Math.abs(startX - endX));

                    if (startX > endX)
                        sunPathOriented = new Boolean(true);
                    else if (startX < endX)
                        sunPathOriented = new Boolean(false);
                }
            } else if (startX > 0) {
                if (endX < 0) {
                    sunPathOriented = new Boolean(false);
                    startEndLongitudinalDiff = new Double(360.0 - startX + endX);
                } else {
                    startEndLongitudinalDiff = new Double(Math.abs(startX - endX));

                    if (startX > endX)
                        sunPathOriented = new Boolean(true);
                    else if (startX < endX)
                        sunPathOriented = new Boolean(false);
                }
            }
        } else {
            tempX = (minX + maxX) / 2;

            longitudinalSpan = new Double(maxX - minX);
            startEndLongitudinalDiff = new Double(Math.abs(startX - endX));

            if (startX > endX)
                sunPathOriented = new Boolean(true);
            else if (startX < endX)
                sunPathOriented = new Boolean(false);
        }

        tempY = (minY + maxY) / 2;

        if (startY > endY)
            northToSouthOriented = new Boolean(true);
        else if (startY < endY)
            northToSouthOriented = new Boolean(false);

        this.centerPoint = new Point(tempX, tempY, SRID);
    }

    int getNumOfSameBeginningEndPoints() {
        if (points == null || points.size() < 2)
            return 0;

        int numOfPoints = points.size();
        int halfNumOfPoints = numOfPoints / 2;
        int outputValue = 0;

        for (int i = 0; i < halfNumOfPoints; i++) {
            if (points.get(i).equals(points.get(numOfPoints - 1 - i)))
                outputValue++;
            else
                break;
        }

        return outputValue;
    }

    ArrayList<LineString> getPartsSplitedByIndex(int index) {
        if (points == null || points.size() < 2)
            return null;
        if (index < 1)
            return null;

        ArrayList<LineString> outputList = new ArrayList<LineString>(2);

        if (index > (points.size() - 2)) {
            outputList.add(new LineString(this));
            return outputList;
        }

        LinkedList<Point> tempPointList1 = new LinkedList<Point>();
        LinkedList<Point> tempPointList2 = new LinkedList<Point>();

        int i = 0;

        for (Point point : points) {

            if (i < index)
                tempPointList1.add(point);

            else if (i == index) {
                tempPointList1.add(point);
                tempPointList2.add(point);
            } else
                tempPointList2.add(point);

            i++;
        }

        outputList.add(new LineString(tempPointList1));
        outputList.add(new LineString(tempPointList2));

        return outputList;
    }
}
