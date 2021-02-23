package com.ubitech.aim.routeparser;

import java.util.LinkedList;
import java.util.regex.Matcher;

class MultiPolygon extends Geometry {
    LinkedList<Polygon> polygons = null;

    MultiPolygon() {
        this.polygons = new LinkedList<Polygon>();
        SRID = GeogSRID;
    }

    MultiPolygon(Matcher multiPolygonMatcher, Integer multiPolygonSRID) {
        setPolygons(multiPolygonMatcher, multiPolygonSRID);
    }

    void close() {
        super.close();

        if (polygons != null) {
            if (!polygons.isEmpty()) {
                for (Polygon polygon : polygons) {
                    polygon.close();
                }
                polygons.clear();
            }
            polygons = null;
        }
    }

    String getWKT() {
        return WKT;
    }

    void setPolygons(Matcher multiPolygonMatcher, Integer multiPolygonSRID) {
        setPolygons(multiPolygonMatcher, multiPolygonSRID, maxNumOfPolygonsInMultiPoly);
    }

    void setPolygons(Matcher multiPolygonMatcher, Integer multiPolygonSRID, int numOfPolygonsToExtract) {
    	setPolygons(multiPolygonMatcher.group(1), multiPolygonSRID, numOfPolygonsToExtract);
    }
    
    private void setPolygons(String strOfPolygons, Integer multiPolygonSRID, int numOfPolygonsToExtract) {
        if (polygons == null)
            polygons = new LinkedList<Polygon>();
        else
            polygons.clear();
    	
        String polygonArray[] = null;

        WKT = "MULTIPOLYGON(";
        
        int i = 0;

        polygonArray = strOfPolygons.split("\\)\\s*\\)\\s*,\\s*\\(\\s*\\(");
        int numOfPolygons = polygonArray.length;

        for (i = 0; i < Math.min(numOfPolygons, numOfPolygonsToExtract); i++) {
        	polygons.add(new Polygon(polygonArray[i], multiPolygonSRID));
            WKT = WKT + (i > 0 ? "," : "") + polygons.get(i).toString();
        }

        WKT = WKT + ")";

        SRID = multiPolygonSRID;
    }
    
    @Override
    public String toString() {
    	if (WKT == null) return "";
    	else return WKT.replaceAll("MULTIPOLYGON", "");
    }
}
