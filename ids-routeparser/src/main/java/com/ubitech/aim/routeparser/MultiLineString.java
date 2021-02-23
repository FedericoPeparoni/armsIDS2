package com.ubitech.aim.routeparser;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

class MultiLineString extends Geometry {
    LinkedList<LineString> lineStrings = null;
    boolean firstLineConnectedByStart = false;
    boolean lastLineConnectedByEnd = false;

    MultiLineString() {
        this.lineStrings = new LinkedList<LineString>();
        SRID = GeogSRID;
    }

    MultiLineString(MultiLineString multiLineString) {
        setMultiLineString(multiLineString);
    }

    MultiLineString(String multiLineWKT) {
        lineStrings = new LinkedList<LineString>();
        setWKT(multiLineWKT, GeogSRID);
    }

    MultiLineString(String multiLineWKT, Integer mltLineSRID) {
        lineStrings = new LinkedList<LineString>();
        setWKT(multiLineWKT, mltLineSRID);
    }

    MultiLineString(List<String> lineWKTs, Integer mltLineSRID) {
        lineStrings = new LinkedList<LineString>();
        setWKT(lineWKTs, mltLineSRID);
    }

    MultiLineString(Matcher mltLineMatcher, Integer mltLineSRID) {
        lineStrings = new LinkedList<LineString>();
        setWKT(mltLineMatcher, mltLineSRID);
    }

    void close() {
        super.close();
        if (lineStrings != null) {
            lineStrings.clear();
            lineStrings = null;
        }
//		clearSubLineConnections();
    }

    void clearSubLineConnections() {
        firstLineConnectedByStart = false;
        lastLineConnectedByEnd = false;

        if (lineStrings != null) {
            for (LineString line : lineStrings) {
                line.connectedByStartPoint = false;
                line.connectedByEndPoint = false;
                line.xyOffset = 0.0;
            }
        }
    }

    void reverse() {
        if (lineStrings == null || lineStrings.isEmpty())
            return;

        for (LineString subLine : lineStrings) {
            subLine.reverse();
        }
        Collections.reverse(lineStrings);
        WKT = null;

        boolean blnTemp = firstLineConnectedByStart;
        firstLineConnectedByStart = lastLineConnectedByEnd;
        lastLineConnectedByEnd = blnTemp;
    }

    int getNumOfLines() {
        if (lineStrings == null)
            return 0;
        return lineStrings.size();
    }

    String getWKT() {
        if (lineStrings == null || lineStrings.isEmpty()) {
            WKT = null;
        } else if (WKT == null) {
            String tempWKT = "MULTILINESTRING(";
            WKT = tempWKT;

            LinkedList<Point> linePoints;

            for (LineString lineString : lineStrings) {
                linePoints = lineString.points;

                if (linePoints == null || linePoints.isEmpty())
                    continue;

                tempWKT = tempWKT + "(";

                for (Point point : linePoints) {
                    tempWKT = tempWKT + point.getX().toString() + " " + point.getY().toString() + ",";
                }

                tempWKT = tempWKT.substring(0, tempWKT.length() - 1) + "),";
            }

            if (tempWKT.length() > WKT.length())
                WKT = tempWKT.substring(0, tempWKT.length() - 1) + ")";
            else
                WKT = null;
        }
        return WKT;
    }

    void addLineString(LineString lineString) {
        if (lineString == null)
            throw new NullPointerException(
                    "'LineString lineString' parameter of 'MultiLineString.addLineString' method cannot accept 'Null'.");

        if (lineString.points.isEmpty())
            return;

        if (lineString.SRID == null)
            throw new NullPointerException(
                    "'SRID' parameter of 'LineString' instance should not be 'Null' \nwhen it is transfered as a parameter to 'MultiLineString.addLineString' method.");

        if (SRID == null)
            throw new NullPointerException(
                    "'SRID' parameter of 'MultiLineString' instance should not be 'Null' \nwhen 'MultiLineString.addLineString' method is called.");

        if (!lineString.SRID.equals(SRID))
            throw new IllegalArgumentException(
                    "'SRID' parameter of 'LineString' instance is NOT equivalent to destination SRID \nwhile it is transfered as a parameter to 'MultiLineString.addLineString' method.");

        if (!lineStrings.isEmpty()) {
            lineStrings.getLast().connectedByEndPoint = false;
            lineString.connectedByStartPoint = false;
        }

        lineStrings.add(lineString);

        lastLineConnectedByEnd = lineString.connectedByEndPoint;

//		clearSubLineConnections();
        WKT = null;
    }

    void addMultiLineString(MultiLineString mltLineString) {
        if (mltLineString == null)
            throw new NullPointerException(
                    "'MultiLineString mltLineString' parameter of 'MultiLineString.addMultiLineString' method cannot accept 'Null'.");

        if (mltLineString.lineStrings == null || mltLineString.lineStrings.isEmpty())
            return;

        if (mltLineString.SRID == null)
            throw new NullPointerException(
                    "'SRID' parameter of 'MultiLineString' instance should not be 'Null' \nwhen it is transfered as a parameter to 'MultiLineString.addMultiLineString' method.");

        if (SRID == null)
            throw new NullPointerException(
                    "'SRID' parameter of 'MultiLineString' instance should not be 'Null' \nwhen 'MultiLineString.addMultiLineString' method is called.");

        if (!mltLineString.SRID.equals(SRID))
            throw new IllegalArgumentException(
                    "'SRID' parameter of 'MultiLineString' instance is NOT equivalent to destination SRID \nwhile it is transfered as a parameter to 'MultiLineString.addMultiLineString' method.");

        for (LineString lineString : mltLineString.lineStrings) {
            addLineString(lineString);
        }
        lastLineConnectedByEnd = mltLineString.lastLineConnectedByEnd;
    }

    void setMultiLineString(MultiLineString mltLineString) {
        if (lineStrings == null)
            lineStrings = new LinkedList<LineString>();
        else if (!lineStrings.isEmpty())
            lineStrings.clear();

        for (LineString lineString : mltLineString.lineStrings) {
            lineStrings.add(new LineString(lineString));
        }

        WKT = mltLineString.WKT;
        SRID = mltLineString.SRID;
        firstLineConnectedByStart = mltLineString.firstLineConnectedByStart;
        lastLineConnectedByEnd = mltLineString.lastLineConnectedByEnd;
    }

    void addLineGeometry(Geometry lineGeometry) {
        addLineGeometry(lineGeometry, false);
    }

    void addLineGeometry(Geometry lineGeometry, boolean combineConcatSubLines) {
        if (lineGeometry == null)
            return;

        if (lineGeometry instanceof LineString)
            addLineString((LineString) lineGeometry);

        else if (lineGeometry instanceof MultiLineString) {
            if (combineConcatSubLines)
                ((MultiLineString) lineGeometry).combineConcatenatedSubLines();
            addMultiLineString((MultiLineString) lineGeometry);
        }
    }

    void setWKT(String multiLineWKT, Integer lineSRID) {

        if (multiLineWKT == null)
            throw new NullPointerException(
                    "'String multiLineWKT' parameter of 'MultiLineString.setWKT' method cannot accept 'Null'.");

        if (lineSRID == null)
            throw new NullPointerException(
                    "'Integer lineSRID' parameter of 'MultiLineString.setWKT' method cannot accept 'Null'.");

        Matcher matcher = patternMULTILINE.matcher(multiLineWKT);

        if (matcher.matches())
            setWKT(matcher, lineSRID);

        else {
            lineStrings = null;
            SRID = null;
            WKT = null;
            clearSubLineConnections();

            String tempWKT;
            if (multiLineWKT.length() > 100)
                tempWKT = multiLineWKT.substring(0, 100);
            else
                tempWKT = multiLineWKT;

            throw new IllegalArgumentException("'" + tempWKT + "..."
                    + "' \nstring cannot be parsed as MULTILINESTRING WKT in 'MultiLineString.setWKT' method.");
        }
    }

    boolean setWKT(List<String> lineWKTs, Integer lineSRID) {

        LineString tempLineString;

        for (String lineWKT : lineWKTs) {
            tempLineString = new LineString(lineWKT, lineSRID);
            addLineString(tempLineString);
        }

        SRID = lineSRID;

        return true;
    }

    void setWKT(Matcher mltLineMatcher, Integer lineSRID) {

        if (lineStrings == null)
            lineStrings = new LinkedList<LineString>();
        else
            lineStrings.clear();

        int k = 0;
        String coordPairsArray[] = null;
        WKT = null;

        try {
            String subLineWKTs = mltLineMatcher.group(1);
            String subLineWKTArray[] = subLineWKTs.split("\\)\\s*,\\s*\\(");
            int numOfLines = subLineWKTArray.length;
            if (numOfLines == 0) {
                throw new IllegalArgumentException("'" + mltLineMatcher.group() + "..."
                        + "' \nstring cannot be parsed as MULTILINESTRING WKT in 'MultiLineString.setWKT' method \nas there is Zero number of Sublines in it.");
            }

            LinkedList<Point> extractedPoints = new LinkedList<Point>();
            Point tempPoint;

            String tempWKT = "MULTILINESTRING(";
            WKT = tempWKT;

            for (int i = 0; i < numOfLines; i++) {
                subLineWKTArray[i] = subLineWKTArray[i].replace('(', ' ');
                subLineWKTArray[i] = subLineWKTArray[i].replace(')', ' ');

                coordPairsArray = subLineWKTArray[i].split(",");
                int numOfPoints = coordPairsArray.length;

                if (numOfPoints == 0)
                    continue;

                tempWKT = tempWKT + "(";

                for (k = 0; k < numOfPoints; k++) {
                    String coordPair[] = coordPairsArray[k].trim().split("\\s+");
                    tempPoint = new Point(Double.valueOf(coordPair[0]), Double.valueOf(coordPair[1]), lineSRID);
                    extractedPoints.add(tempPoint);
                    tempWKT = tempWKT + tempPoint.getX().toString() + " " + tempPoint.getY().toString() + ",";
                }

                lineStrings.add(new LineString(extractedPoints, lineSRID));

                tempWKT = tempWKT.substring(0, tempWKT.length() - 1) + "),";

                extractedPoints.clear();
            }

            if (tempWKT.length() > WKT.length())
                WKT = tempWKT.substring(0, tempWKT.length() - 1) + ")";
            else
                WKT = null;

            SRID = lineSRID;
            clearSubLineConnections();

        } catch (NumberFormatException e) {
            SRID = null;
            WKT = null;
            throw new NumberFormatException("Coordinate pair '" + coordPairsArray[k].trim()
                    + "', extracted from MULTILINESTRING WKT, \ncannot be parsed as numbers in 'MultiLineString.setWKT' method.");
        }
    }

    void combineConcatenatedSubLines() {
        int numOfLines = lineStrings.size();
        if (numOfLines < 2)
            return;

        LineString currentLine;
        LineString nextLine;

        for (int i = 0; i < (numOfLines - 1); i++) {
            currentLine = lineStrings.get(i);
            nextLine = lineStrings.get(i + 1);

            if (currentLine.points.getLast().equalsUpTo6DecPlaces(nextLine.points.getFirst())) {
                nextLine.points.removeFirst();
                currentLine.points.addAll(nextLine.points);
                currentLine.WKT = null;

                currentLine.connectedByEndPoint = nextLine.connectedByEndPoint;

                lineStrings.remove(i + 1);
                nextLine.close();
                nextLine = null;
                numOfLines--;
                i--;
            }
        }

        WKT = null;
    }

    void tryConnectAndMergeSubLines(Point startPoint, Point endPoint, double pntLocAccuracyInMeters,
            double maxLinearGapInMeters) {

        if (lineStrings == null || lineStrings.isEmpty())
            return;
        clearSubLineConnections();

        int i;
        double dblDist;
        int numOfSubLines = lineStrings.size();

        if (startPoint != null) {
            i = 0;
            for (LineString line : lineStrings) {
                if (line.getFirstPoint().equalsUpTo6DecPlaces(startPoint)) {
                    line.connectedByStartPoint = true;

                    firstLineConnectedByStart = true;

                    if (i > 0) {
//						lineStrings.remove(i);
//						lineStrings.addFirst(line);
                        lineStrings.addFirst(lineStrings.remove(i));
                    }
                    break;
                }
                i++;
            }
            if (!firstLineConnectedByStart && pntLocAccuracyInMeters > 0.0) {
                i = 0;
                for (LineString line : lineStrings) {
                    dblDist = Geometry.distanceInMeters(startPoint, line.getFirstPoint());
                    if (dblDist <= pntLocAccuracyInMeters) {
                        line.connectedByStartPoint = true;

                        firstLineConnectedByStart = true;

                        if (i > 0) {
//							lineStrings.remove(i);
//							lineStrings.addFirst(line);
                            lineStrings.addFirst(lineStrings.remove(i));
                        }
                        break;
                    }
                    i++;
                }
            }
        }

        if (endPoint != null) {
            i = 0;
            for (LineString line : lineStrings) {
                if (line.getLastPoint().equalsUpTo6DecPlaces(endPoint)) {
                    line.connectedByEndPoint = true;
                    if (firstLineConnectedByStart && i == 0) {
                        if (numOfSubLines > 1)
                            throw new IllegalArgumentException(
                                    "FIR Airspaces, being checked for intersection by a Route, \noverlap each other and cannot be processed.");
                    }

                    lastLineConnectedByEnd = true;

                    if (i < numOfSubLines - 1) {
//						lineStrings.remove(i);
//						lineStrings.addLast(line);
                        lineStrings.addLast(lineStrings.remove(i));
                    }
                    break;
                }
                i++;
            }
            if (!lastLineConnectedByEnd && pntLocAccuracyInMeters > 0.0) {
                i = 0;
                for (LineString line : lineStrings) {
                    dblDist = Geometry.distanceInMeters(endPoint, line.getLastPoint());
                    if (dblDist <= pntLocAccuracyInMeters) {
                        line.connectedByEndPoint = true;
                        if (firstLineConnectedByStart && i == 0) {
                            if (numOfSubLines > 1)
                                throw new IllegalArgumentException(
                                        "FIR Airspaces, being checked for intersection by a Route, \noverlap each other and cannot be processed.");
                        }

                        lastLineConnectedByEnd = true;

                        if (i < numOfSubLines - 1) {
//							lineStrings.remove(i);
//							lineStrings.addLast(line);
                            lineStrings.addLast(lineStrings.remove(i));
                        }
                        break;
                    }
                    i++;
                }
            }
        }

        int iFlag;
        boolean jFlag;

        if (numOfSubLines > 1) {
            int j;
            for (i = 0; i < (numOfSubLines - 1); i++) {
                iFlag = i;

                if (!lineStrings.get(i).connectedByEndPoint) {
                    jFlag = false;
                    for (j = i + 1; j < numOfSubLines; j++) {
                        if (lineStrings.get(j).connectedByStartPoint)
                            continue;

                        if (lineStrings.get(i).connectedByStartPoint)
                            jFlag = (tryMergeSubLines(i, j, true, maxLinearGapInMeters));
                        else if (lineStrings.get(j).connectedByEndPoint)
                            jFlag = (tryMergeSubLines(i, j, false, maxLinearGapInMeters));
                        else
                            jFlag = (tryMergeSubLines(i, j, true, maxLinearGapInMeters));

                        if (jFlag) {
                            numOfSubLines = lineStrings.size();
                            i--;
                            break;
                        }
                    }
                }

                if (i < iFlag)
                    continue;

                if (!lineStrings.get(i).connectedByStartPoint) {
                    jFlag = false;
                    for (j = i + 1; j < numOfSubLines; j++) {
                        if (lineStrings.get(j).connectedByEndPoint)
                            continue;

                        if (lineStrings.get(i).connectedByEndPoint)
                            jFlag = (tryMergeSubLines(j, i, false, maxLinearGapInMeters));
                        else if (lineStrings.get(j).connectedByStartPoint)
                            jFlag = (tryMergeSubLines(j, i, true, maxLinearGapInMeters));
                        else
                            jFlag = (tryMergeSubLines(j, i, false, maxLinearGapInMeters));

                        if (jFlag) {
                            numOfSubLines = lineStrings.size();
                            i--;
                            break;
                        }
                    }
                }
            }
        }

        /*
         * if (firstLineConnected && !lineStrings.getFirst().connectedByStartPoint)
         * throw new
         * ArithmeticException("First Sub-Line of Multi-Line should be connected by Start Point (Departure Airport), whereas it is Not. Please notify the Developer."
         * );
         * 
         * if (lastLineConnected && !lineStrings.getLast().connectedByEndPoint) throw
         * new
         * ArithmeticException("Last Sub-Line of Multi-Line should be connected by End Point (Destination Airport), whereas it is Not. Please notify the Developer."
         * );
         */
    }

    boolean tryMergeSubLines(int firstLineIndex, int secondLineIndex, boolean retainFirst, double accuracyInMeters) {
        int numOfSubLines = lineStrings.size();

        if (firstLineIndex < 0 || secondLineIndex < 0)
            throw new IllegalArgumentException(
                    "'int firstLineIndex' and 'int secondLineIndex' parameter values cannot be less than zero.");
        if (firstLineIndex >= numOfSubLines || secondLineIndex >= numOfSubLines)
            throw new IllegalArgumentException(
                    "'int firstLineIndex' and 'int secondLineIndex' parameter values \ncaanot be equal or greater than the number of Sublines in Multiline.");
        if (firstLineIndex == secondLineIndex)
            throw new IllegalArgumentException(
                    "'int firstLineIndex' and 'int secondLineIndex' parameter values cannot be equal.");

        LineString firstLine = lineStrings.get(firstLineIndex);
        LineString secondLine = lineStrings.get(secondLineIndex);

        if (firstLine.getLastPoint().equalsUpTo6DecPlaces(secondLine.getFirstPoint())) {
            if (retainFirst) {
                secondLine.points.removeFirst();
                firstLine.addAllPoints(secondLine.points);
                firstLine.connectedByEndPoint = secondLine.connectedByEndPoint;
                lineStrings.remove(secondLineIndex);
            } else {
                firstLine.points.removeLast();
                secondLine.addAllPointsToFront(firstLine.points);
                secondLine.connectedByStartPoint = firstLine.connectedByStartPoint;
                lineStrings.remove(firstLineIndex);
            }
            return true;
        } else if (accuracyInMeters > 0.0) {
            double dblDist = Geometry.distanceInMeters(firstLine.getLastPoint(), secondLine.getFirstPoint())
                    .doubleValue();

            if (dblDist <= accuracyInMeters) {
                if (retainFirst) {
//					firstLine.points.removeLast();
//					secondLine.points.removeFirst();
                    firstLine.addAllPoints(secondLine.points);
                    firstLine.connectedByEndPoint = secondLine.connectedByEndPoint;
                    lineStrings.remove(secondLineIndex);
                } else {
//					firstLine.points.removeLast();
//					secondLine.points.removeFirst();
                    secondLine.addAllPointsToFront(firstLine.points);
                    secondLine.connectedByStartPoint = firstLine.connectedByStartPoint;
                    lineStrings.remove(firstLineIndex);
                }
                return true;
            }
        }
        return false;
    }

    void sortSubLinesGeographically(LineString baseLine) {
        int numOfSubLines = lineStrings.size();
        int i = 0;

        for (LineString subLine : lineStrings) {

            subLine.setXYoffset(baseLine);

            if (i == 0 && firstLineConnectedByStart) {
                if (!subLine.connectedByStartPoint)
                    throw new ArithmeticException(
                            "First Sub-Line of Multi-Line should be connected by Start Point (Departure Airport), \nwhereas it is Not. Please notify the Developer.");
            } else {
                if (subLine.connectedByStartPoint)
                    throw new ArithmeticException("'" + i
                            + "' Sub-Line of Multi-Line should Not be connected by Start Point (Departure Airport), \nwhereas it Is connected. Please notify the Developer.");
            }

            if (i == (numOfSubLines - 1) && lastLineConnectedByEnd) {
                if (!subLine.connectedByEndPoint)
                    throw new ArithmeticException(
                            "Last Sub-Line of Multi-Line should be connected by End Point (Destination Airport), \nwhereas it is Not. Please notify the Developer.");
            } else {
                if (subLine.connectedByEndPoint)
                    throw new ArithmeticException("'" + i
                            + "' Sub-Line of Multi-Line should Not be connected by End Point (Destination Airport), \nwhereas it Is connected. Please notify the Developer.");
            }

            i++;
        }

        if (numOfSubLines < 2)
            return;
        else if ((firstLineConnectedByStart || lastLineConnectedByEnd) && numOfSubLines < 3)
            return;
        else if (firstLineConnectedByStart && lastLineConnectedByEnd && numOfSubLines < 4)
            return;

        if (!baseLine.isClosed) {
            Collections.sort(lineStrings, new LineStringComp());
        }
    }
}
