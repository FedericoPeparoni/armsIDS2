package com.ubitech.aim.routeparser;

import java.util.ArrayList;

class Ring extends Geometry {

	ArrayList<Point> vertexes = null;

	public Ring(String coordPairs, Integer ringSRID) {
		setPoints(coordPairs, ringSRID);
	}

	public Ring(Geometry geometry) {
		super(geometry);
		// TODO Auto-generated constructor stub
	}

	@Override
	String getWKT() {
		// TODO Auto-generated method stub
		return WKT;
	}
	
    private void setPoints(String coordPairs, Integer ringSRID) {

        if (vertexes == null)
            vertexes = new ArrayList<Point>();
        else
            vertexes.clear();

        String coordPairsArray[] = null;

        WKT = "RING(";
        
        int i = 0;

        try {
        	coordPairs = coordPairs.trim().replaceAll("[\\)\\(]", "");

            coordPairsArray = coordPairs.split(",");
            int numOfPoints = coordPairsArray.length;

            if (numOfPoints < 4) {
                String tempView;
                if (coordPairs.length() > 100)
                    tempView = "(" + coordPairs.substring(0, 100) + " ...)";
                else
                    tempView = "(" + coordPairs + ")";

                throw new IllegalArgumentException("'" + tempView
                        + "' \nstring cannot be parsed as RING WKT in 'Ring.setWKT' method \nas there are less than 4 vertexes (coordinate pairs) in it.");
            }
            
            String coordPair[];
            Point tempPoint;

            for (i = 0; i < numOfPoints; i++) {
            	coordPair = coordPairsArray[i].trim().split("\\s+");
                tempPoint = new Point(Double.valueOf(coordPair[0]), Double.valueOf(coordPair[1]), ringSRID);
                vertexes.add(tempPoint);
                WKT = WKT + tempPoint.getX().toString() + " " + tempPoint.getY().toString() + ",";
            }

            WKT = WKT.substring(0, WKT.length() - 1) + ")";

            SRID = ringSRID;

        } catch (NumberFormatException e) {
            SRID = null;
            WKT = null;
            vertexes.clear();
            throw new NumberFormatException("Coordinate pair '" + coordPairsArray[i].trim()
                    + "', extracted from POLYGON WKT, cannot be parsed as numbers in 'Polygon.setWKT' method.");
        }
    }

    @Override
    public String toString() {
    	if (WKT == null) return "";
    	else return WKT.replaceAll("RING", "");
    }
}
