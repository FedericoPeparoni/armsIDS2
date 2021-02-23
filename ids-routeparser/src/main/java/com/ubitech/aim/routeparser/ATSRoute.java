package com.ubitech.aim.routeparser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

class ATSRoute {
    ArrayList<RoutePoint> atsRoutePath = null;
    Timestamp timestamp = null;
    Long routeRecID = null;
    Double atsRouteLength = null;

    ATSRoute() {
    }

    ATSRoute(ArrayList<RoutePoint> atsRoutePath, Timestamp timestamp) {
        this.atsRoutePath = atsRoutePath;
        this.timestamp = timestamp;
    }

    ATSRoute(ArrayList<RoutePoint> atsRoutePath, Timestamp timestamp, Long recordID) {
        this.atsRoutePath = atsRoutePath;
        this.timestamp = timestamp;
        this.routeRecID = recordID;
    }

    ATSRoute(ArrayList<RoutePoint> atsRoutePath, Timestamp timestamp, Long recordID, Double atsRouteLength) {
        this.atsRoutePath = atsRoutePath;
        this.timestamp = timestamp;
        this.routeRecID = recordID;
        this.atsRouteLength = atsRouteLength;
    }

    RoutePoint getFirstPoint() {
        if (atsRoutePath == null || atsRoutePath.isEmpty())
            return null;
        return atsRoutePath.get(0);
    }

    RoutePoint getLastPoint() {
        if (atsRoutePath == null || atsRoutePath.isEmpty())
            return null;
        return atsRoutePath.get(atsRoutePath.size() - 1);
    }

    RoutePoint getPoint(int index) {
        if (index < 0 || atsRoutePath == null || atsRoutePath.size() <= index)
            return null;
        return atsRoutePath.get(0);
    }

    int numOfPoints() {
        if (atsRoutePath == null)
            return -1;
        if (atsRoutePath.isEmpty())
            return 0;
        return atsRoutePath.size();
    }

    ArrayList<RoutePoint> getInnerRoutePointsForMap() {
        return getInnerRoutePointsForMap(false);
    }

    ArrayList<RoutePoint> getInnerRoutePointsForMap(boolean fillAttributes) {
        if (this.atsRoutePath == null || this.atsRoutePath.size() <= 2)
            return new ArrayList<RoutePoint>(2);

        int routePathSize = this.atsRoutePath.size();
        ArrayList<RoutePoint> outputListOfPoints = new ArrayList<RoutePoint>(routePathSize - 2);
        int i = 0;

        if (fillAttributes) {
            for (RoutePoint routePoint : this.atsRoutePath) {
                i++;
                if (i == 1 || i == routePathSize)
                    continue;

                routePoint.fillDisplayAttributes(true);

                outputListOfPoints.add(routePoint);
            }
        } else {
            for (RoutePoint routePoint : this.atsRoutePath) {
                i++;
                if (i == 1 || i == routePathSize)
                    continue;
                outputListOfPoints.add(routePoint);
            }
        }

        return outputListOfPoints;
    }

    Double length() {
        return atsRouteLength;
    }

    Double length(PreparedStatement psDistance) throws SQLException {
        if (atsRoutePath == null) {
            atsRouteLength = null;
            return atsRouteLength;
        } else if (atsRoutePath.size() < 2) {
            atsRouteLength = 0.;
            return atsRouteLength;
        }

        if (psDistance != null) {
            ResultSet rs;
            atsRouteLength = 0.;

            for (int i = 0; i < (atsRoutePath.size() - 2); i++) {
                psDistance.setString(1, atsRoutePath.get(i).getWKT());
                psDistance.setString(2, atsRoutePath.get(i + 1).getWKT());
                rs = psDistance.executeQuery();
                if (rs.next())
                    atsRouteLength = atsRouteLength + (double) rs.getFloat(1);
            }
        }

        return atsRouteLength;
    }
}
