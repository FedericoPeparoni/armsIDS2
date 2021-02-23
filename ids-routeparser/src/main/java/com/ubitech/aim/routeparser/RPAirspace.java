package com.ubitech.aim.routeparser;

import java.util.LinkedList;

public class RPAirspace extends MultiPolygon {
    RPAirspaceType type = null;
    LinkedList<Polygon> bufferPolygons = null;

    public RPAirspace(String airspaceWKT, RPAirspaceType airspaceType) {

        if (airspaceWKT == null)
            throw new NullPointerException(
                    "'String airspaceWKT' parameter of 'RPAirspace' constructor cannot accept 'Null'.");

        if (airspaceType == null)
            throw new NullPointerException(
                    "'RPAirspaceType airspaceType' parameter of 'RPAirspace' constructor cannot accept 'Null'.");

        Geometry inputGeom = Geometry.geometryFromWKT(airspaceWKT, GeogSRID);

        if (inputGeom == null) {
            String tempView;
            if (airspaceWKT.length() > 100)
                tempView = airspaceWKT.substring(0, 100) + " ...";
            else
                tempView = airspaceWKT;

            throw new IllegalArgumentException("'" + tempView
                    + "' \nstring can neither be parsed as MULTIPOLYGON nor as POLYGON WKT in 'RPAirspace' constructor.");
        } else if (inputGeom instanceof MultiPolygon) {
            this.polygons = ((MultiPolygon) inputGeom).polygons;
        } else if (inputGeom instanceof Polygon) {
            this.polygons.add((Polygon) inputGeom);
        } else {
            String tempView;
            if (airspaceWKT.length() > 100)
                tempView = airspaceWKT.substring(0, 100) + " ...";
            else
                tempView = airspaceWKT;

            throw new IllegalArgumentException("'" + tempView
                    + "' \nstring can neither be parsed as MULTIPOLYGON nor as POLYGON WKT in 'RPAirspace' constructor.");
        }

        this.type = airspaceType;
        inputGeom = null;
    }

    RPAirspace(Polygon polygon, RPAirspaceType airspaceType) {

        if (polygon == null)
            throw new NullPointerException(
                    "'Polygon polygon' parameter of 'RPAirspace' constructor cannot accept 'Null'.");

        if (airspaceType == null)
            throw new NullPointerException(
                    "'RPAirspaceType airspaceType' parameter of 'RPAirspace' constructor cannot accept 'Null'.");

        this.polygons.add(new Polygon(polygon));

        this.type = airspaceType;
    }

    public void close() {
        super.close();

        type = null;

        if (bufferPolygons != null) {
            if (!bufferPolygons.isEmpty()) {
                for (Polygon polygon : bufferPolygons) {
                    polygon.close();
                }
                bufferPolygons.clear();
            }
            bufferPolygons = null;
        }
    }
}
