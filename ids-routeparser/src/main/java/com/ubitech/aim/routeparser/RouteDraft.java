package com.ubitech.aim.routeparser;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class RouteDraft {

    private double estimatedRouteLengthInMeters;
    private double acceptableMaxRouteLengthInMeters;
    private double maxRouteSegmentLength;
    private double length = 0;
    private boolean isLengthCalculated = false;
    int numOfIncrediblePoints = 0;

    private boolean originADHPadded = false;
    private boolean destinationADHPadded = false;

    private ArrayList<RoutePoint> routePoints = null;

//	private PreparedStatement psDistance = null;

    RouteDraft(double estimatedRouteLengthInMeters, double acceptableMaxRouteLengthInMeters,
            double maxRouteSegmentLength) {
//		this.psDistance = psDistance;
        this.estimatedRouteLengthInMeters = estimatedRouteLengthInMeters;
        this.acceptableMaxRouteLengthInMeters = acceptableMaxRouteLengthInMeters;
        this.maxRouteSegmentLength = maxRouteSegmentLength;

        if (routePoints == null)
            routePoints = new ArrayList<RoutePoint>(40);
    }

    RouteDraft getClone() {
        RouteDraft clonedRouteDraft = new RouteDraft(this.estimatedRouteLengthInMeters,
                this.acceptableMaxRouteLengthInMeters, this.maxRouteSegmentLength);
        if (routePoints != null && !routePoints.isEmpty())
            clonedRouteDraft.routePoints.addAll(routePoints);
        clonedRouteDraft.length = this.length;
        clonedRouteDraft.isLengthCalculated = this.isLengthCalculated;
        clonedRouteDraft.numOfIncrediblePoints = this.numOfIncrediblePoints;
        clonedRouteDraft.originADHPadded = this.originADHPadded;
        clonedRouteDraft.destinationADHPadded = this.destinationADHPadded;
        return clonedRouteDraft;
    }

    public String getRouteWKT() {
        if (routePoints.size() == 0)
            return null;

        String routeWKT = routePoints.get(0).getLongitude() + " " + routePoints.get(0).getLatitude();

        if (routePoints.size() > 1) {
            for (int i = 1; i < routePoints.size(); i++) {
                routeWKT = routeWKT + "," + routePoints.get(i).getLongitude() + " " + routePoints.get(i).getLatitude();
            }
        }

        return "LINESTRING(" + routeWKT + ")";
    }

    public int numOfRoutePoints() {
        return routePoints.size();
    }

    public int numOfCrediblePoints() {
        return (routePoints.size() - numOfIncrediblePoints);
    }

    public double length() throws SQLException {
        if (isLengthCalculated)
            return length;
//		ResultSet rs;

        length = 0;
        double segmentLength = 0;

        for (int i = 0; i < (routePoints.size() - 1); i++) {
//			psDistance.setString (1, routePoints.get(i).getWKT());
//			psDistance.setString (2, routePoints.get(i + 1).getWKT());
//			rs = psDistance.executeQuery();
//			if (rs.next()) {
//				segmentLength = (double)rs.getFloat(1);
            segmentLength = Geometry.distanceInMeters(routePoints.get(i), routePoints.get(i + 1)).doubleValue();
            length = length + segmentLength;
//			}

            routePoints.get(i).nextDist = segmentLength;
            routePoints.get(i + 1).prevDist = segmentLength;
        }
        isLengthCalculated = true;

        numOfIncrediblePoints = 0;
        for (RoutePoint routePoint : routePoints) {
            if (routePoint.parsedPointIndex < 0 || routePoint.pointType.ordinal() <= RoutePointType.Waypoint.ordinal()
                    || routePoint.connectedToRoute) {
                routePoint.isCredible = true;
            } else {
                routePoint.isCredible = false;
                numOfIncrediblePoints++;
            }
        }

        if (numOfIncrediblePoints > 0) {
            for (int i = 1; i < (routePoints.size()); i++) {
                if (routePoints.get(i).isCredible)
                    continue;

                if (routePoints.get(i - 1).parsedPointIndex >= 0) {
                    if (routePoints.get(i).prevDist <= (maxRouteSegmentLength
                            * (routePoints.get(i).parsedPointIndex - routePoints.get(i - 1).parsedPointIndex))) {
                        routePoints.get(i).prevConnected = true;
                        routePoints.get(i - 1).nextConnected = true;
                    } else
                        continue;
                }
            }

            for (int i = 1; i < (routePoints.size() - 1); i++) {
                if (routePoints.get(i).isCredible)
                    continue;

                if (routePoints.get(i - 1).isCredible && routePoints.get(i).prevConnected) {
                    routePoints.get(i).isCredible = true;
                    numOfIncrediblePoints--;
                }
            }

            if (numOfIncrediblePoints > 0) {
                for (int i = routePoints.size() - 2; i > 0; i--) {
                    if (routePoints.get(i).isCredible)
                        continue;

                    if (routePoints.get(i + 1).isCredible && routePoints.get(i).nextConnected) {
                        routePoints.get(i).isCredible = true;
                        numOfIncrediblePoints--;
                    }
                }
            }
        }

        return length;
    }

    LinkedList<RouteDraft> getShortenedDrafts() throws SQLException {

        LinkedList<RouteDraft> listOfDrafts = new LinkedList<RouteDraft>();

        LinkedList<RoutePoint> listOfIncrediblePoints = new LinkedList<RoutePoint>();

//		if (numOfIncrediblePoints <= 0 || routePoints.size() < 4 || length <= acceptableMaxRouteLengthInMeters) return listOfDrafts;
        if (numOfIncrediblePoints <= 0 || routePoints.size() < 4)
            return listOfDrafts;

        RouteDraft tempDraft;

        for (int i = 0; i < numOfIncrediblePoints; i++) {
            tempDraft = this.getClone();
            tempDraft.removePoint(listOfIncrediblePoints.get(i));
            listOfDrafts.add(tempDraft);
        }

        if (numOfIncrediblePoints < 2 || routePoints.size() < 5)
            return listOfDrafts;

        for (int i = 0; i < (numOfIncrediblePoints - 1); i++) {
            for (int j = i + 1; j < numOfIncrediblePoints; j++) {
                tempDraft = this.getClone();
                tempDraft.removePoint(listOfIncrediblePoints.get(i));
                tempDraft.removePoint(listOfIncrediblePoints.get(j));
                listOfDrafts.add(tempDraft);
            }
        }

        if (numOfIncrediblePoints < 3 || routePoints.size() < 6)
            return listOfDrafts;

        for (int i = 0; i < (numOfIncrediblePoints - 2); i++) {
            for (int j = i + 1; j < (numOfIncrediblePoints - 1); j++) {
                for (int k = j + 1; k < numOfIncrediblePoints; k++) {
                    tempDraft = this.getClone();
                    tempDraft.removePoint(listOfIncrediblePoints.get(i));
                    tempDraft.removePoint(listOfIncrediblePoints.get(j));
                    tempDraft.removePoint(listOfIncrediblePoints.get(k));
                    listOfDrafts.add(tempDraft);
                }
            }
        }

        if (numOfIncrediblePoints < 4 || routePoints.size() < 7)
            return listOfDrafts;

        for (int i = 0; i < (numOfIncrediblePoints - 3); i++) {
            for (int j = i + 1; j < (numOfIncrediblePoints - 2); j++) {
                for (int k = j + 1; k < (numOfIncrediblePoints - 1); k++) {
                    for (int l = k + 1; l < numOfIncrediblePoints; l++) {
                        tempDraft = this.getClone();
                        tempDraft.removePoint(listOfIncrediblePoints.get(i));
                        tempDraft.removePoint(listOfIncrediblePoints.get(j));
                        tempDraft.removePoint(listOfIncrediblePoints.get(k));
                        tempDraft.removePoint(listOfIncrediblePoints.get(l));
                        listOfDrafts.add(tempDraft);
                    }
                }
            }
        }

        if (numOfIncrediblePoints < 5 || routePoints.size() < 8)
            return listOfDrafts;

        for (int i = 0; i < (numOfIncrediblePoints - 4); i++) {
            for (int j = i + 1; j < (numOfIncrediblePoints - 3); j++) {
                for (int k = j + 1; k < (numOfIncrediblePoints - 2); k++) {
                    for (int l = k + 1; l < (numOfIncrediblePoints - 1); l++) {
                        for (int m = l + 1; m < numOfIncrediblePoints; m++) {
                            tempDraft = this.getClone();
                            tempDraft.removePoint(listOfIncrediblePoints.get(i));
                            tempDraft.removePoint(listOfIncrediblePoints.get(j));
                            tempDraft.removePoint(listOfIncrediblePoints.get(k));
                            tempDraft.removePoint(listOfIncrediblePoints.get(l));
                            tempDraft.removePoint(listOfIncrediblePoints.get(m));
                            listOfDrafts.add(tempDraft);
                        }
                    }
                }
            }
        }

        return listOfDrafts;
    }

    boolean isLengthWithinTheLimit() throws SQLException {
        if (length() <= acceptableMaxRouteLengthInMeters)
            return true;
        else
            return false;
    }

    double longerThanEstimatedLength() throws SQLException {
        return (length() - estimatedRouteLengthInMeters);
    }

    void addOrigin(RoutePoint routePoint) throws SQLException {

        if (originADHPadded) {
            routePoints.remove(0);
            routePoints.add(0, routePoint);
        } else {
            routePoints.add(0, routePoint);
            originADHPadded = true;
        }
        isLengthCalculated = false;
    }

    void addDestination(RoutePoint routePoint) throws SQLException {

        if (destinationADHPadded) {
            routePoints.remove(routePoints.size() - 1);
            routePoints.add(routePoint);
        } else {
            routePoints.add(routePoint);
            destinationADHPadded = true;
        }
        isLengthCalculated = false;
    }

    int addFirstRoutePoint(RoutePoint routePoint) throws SQLException {

        isLengthCalculated = false;
        if (originADHPadded) {
            routePoints.add(1, routePoint);
            return 1;
        } else {
            routePoints.add(0, routePoint);
            return 0;
        }

    }

    int addRoutePoint(RoutePoint routePoint) throws SQLException {

        isLengthCalculated = false;
        if (destinationADHPadded) {
            routePoints.add(routePoints.size() - 1, routePoint);
            return routePoints.size() - 2;
        } else {
            routePoints.add(routePoint);
            return routePoints.size() - 1;
        }
    }

    int addRoutePoint(int index, RoutePoint routePoint) throws SQLException {

        isLengthCalculated = false;
        if (originADHPadded) {
            routePoints.add(index + 1, routePoint);
            return 1;
        } else {
            routePoints.add(index, routePoint);
            return 0;
        }
    }

    void removePoint(int index) throws SQLException {
        if (!routePoints.get(index).isCredible)
            numOfIncrediblePoints--;
        routePoints.remove(index);
        isLengthCalculated = false;
    }

    void removePoint(RoutePoint point) throws SQLException {
        if (!routePoints.contains(point))
            return;
        if (!point.isCredible)
            numOfIncrediblePoints--;
        routePoints.remove(point);
        isLengthCalculated = false;
    }

    void addAllRoutePoints(List<RoutePoint> listOfRoutePoints) throws SQLException {

        if (listOfRoutePoints == null || listOfRoutePoints.isEmpty())
            return;

        for (RoutePoint routePoint : listOfRoutePoints) {
            addRoutePoint(routePoint);
        }
    }

    void addAllRoutePoints(int index, List<RoutePoint> listOfRoutePoints)
            throws java.lang.IndexOutOfBoundsException, SQLException {

        if (index < 0 || index > listOfRoutePoints.size())
            throw new java.lang.IndexOutOfBoundsException();

        if (listOfRoutePoints == null || listOfRoutePoints.isEmpty())
            return;

        if (index == listOfRoutePoints.size()) {
            addAllRoutePoints(listOfRoutePoints);
        } else {
            int i = index;
            for (RoutePoint routePoint : listOfRoutePoints) {
                addRoutePoint(i, routePoint);
                i++;
            }
        }
    }

    RoutePoint getFirstRoutePoint() throws java.util.NoSuchElementException {
        if (originADHPadded)
            return routePoints.get(1);
        else
            return routePoints.get(0);
    }

    RoutePoint getDepartureADHP() throws java.util.NoSuchElementException {
        if (originADHPadded)
            return routePoints.get(0);
        else
            return null;
    }

    RoutePoint getRoutePoint(int index) throws java.lang.IndexOutOfBoundsException {
        if (originADHPadded)
            return routePoints.get(index + 1);
        else
            return routePoints.get(index);
    }

    RoutePoint getLastRoutePoint() throws java.util.NoSuchElementException {
        if (destinationADHPadded)
            return routePoints.get(routePoints.size() - 2);
        else
            return routePoints.get(routePoints.size() - 1);
    }

    RoutePoint getDestinationADHP() throws java.util.NoSuchElementException {
        if (destinationADHPadded)
            return routePoints.get(routePoints.size() - 1);
        else
            return null;
    }

    ArrayList<RoutePoint> getAllRoutePoints() {
        ArrayList<RoutePoint> toReturn = new ArrayList<RoutePoint>(routePoints.size());
        toReturn.addAll(routePoints);
        return toReturn;
    }

}
