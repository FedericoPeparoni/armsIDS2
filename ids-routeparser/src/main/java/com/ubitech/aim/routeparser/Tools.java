package com.ubitech.aim.routeparser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tools {
    private Connection connection = null;

    private PreparedStatement psIsValid = null;
    private PreparedStatement psIsEmpty = null;
    private PreparedStatement psIsCollection = null;
    private PreparedStatement psCollectionExtract = null;
    private PreparedStatement psGeometryType = null;
    private PreparedStatement psNumGeometries = null;
    private PreparedStatement psGeometryN = null;
    private PreparedStatement psNRings = null;
    private PreparedStatement psExteriorRing = null;
    private PreparedStatement InteriorRingN = null;
    private PreparedStatement psRemoveRepeatedPoints = null;
    private PreparedStatement psNPoints = null;
    private PreparedStatement psPointN = null;
    private PreparedStatement psX_Max = null;
    private PreparedStatement psX_Min = null;
    private PreparedStatement psY_Max = null;
    private PreparedStatement psY_Min = null;
    private PreparedStatement ps_NthPointX = null;
    private PreparedStatement ps_NthPointY = null;
    private PreparedStatement ps_X = null;
    private PreparedStatement ps_Y = null;
    private PreparedStatement psIntersect = null;
    private PreparedStatement psCovers = null;
    private PreparedStatement psPolygonCoversPoint = null;
    private PreparedStatement psIntersection = null;
    private PreparedStatement psLength = null;
    private PreparedStatement psSegmentize = null;
    private PreparedStatement psIsPointWithinPolygon = null;

    private LinkedList<String> listOfErrorMessages = new LinkedList<String>();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int SEGMENTIZE_METERS = 2000;

    public Tools(Connection connection) {
        this.connection = connection;
    }

    public void close() throws SQLException {

        if (listOfErrorMessages != null) {
            listOfErrorMessages.clear();
            listOfErrorMessages = null;
        }

        if (psIsValid != null) {
            psIsValid.close();
            psIsValid = null;
        }
        if (psIsEmpty != null) {
            psIsEmpty.close();
            psIsEmpty = null;
        }
        if (psIsCollection != null) {
            psIsCollection.close();
            psIsCollection = null;
        }
        if (psCollectionExtract != null) {
            psCollectionExtract.close();
            psCollectionExtract = null;
        }
        if (psGeometryType != null) {
            psGeometryType.close();
            psGeometryType = null;
        }
        if (psNumGeometries != null) {
            psNumGeometries.close();
            psNumGeometries = null;
        }
        if (psGeometryN != null) {
            psGeometryN.close();
            psGeometryN = null;
        }
        if (psNRings != null) {
            psNRings.close();
            psNRings = null;
        }
        if (psExteriorRing != null) {
            psExteriorRing.close();
            psExteriorRing = null;
        }
        if (InteriorRingN != null) {
            InteriorRingN.close();
            InteriorRingN = null;
        }
        if (psRemoveRepeatedPoints != null) {
            psRemoveRepeatedPoints.close();
            psRemoveRepeatedPoints = null;
        }
        if (psNPoints != null) {
            psNPoints.close();
            psNPoints = null;
        }
        if (psPointN != null) {
            psPointN.close();
            psPointN = null;
        }
        if (psX_Max != null) {
            psX_Max.close();
            psX_Max = null;
        }
        if (psX_Min != null) {
            psX_Min.close();
            psX_Min = null;
        }
        if (psY_Max != null) {
            psY_Max.close();
            psY_Max = null;
        }
        if (psY_Min != null) {
            psY_Min.close();
            psY_Min = null;
        }
        if (ps_NthPointX != null) {
            ps_NthPointX.close();
            ps_NthPointX = null;
        }
        if (ps_NthPointY != null) {
            ps_NthPointY.close();
            ps_NthPointY = null;
        }
        if (ps_X != null) {
            ps_X.close();
            ps_X = null;
        }
        if (ps_Y != null) {
            ps_Y.close();
            ps_Y = null;
        }
        if (psIntersect != null) {
            psIntersect.close();
            psIntersect = null;
        }
        if (psCovers != null) {
            psCovers.close();
            psCovers = null;
        }
        if (psPolygonCoversPoint != null) {
            psPolygonCoversPoint.close();
            psPolygonCoversPoint = null;
        }
        if (psIntersection != null) {
            psIntersection.close();
            psIntersection = null;
        }
        if (psLength != null) {
            psLength.close();
            psLength = null;
        }
        if (psSegmentize != null) {
            psSegmentize.close();
            psSegmentize = null;
        }
        if (psIsPointWithinPolygon != null) {
            psIsPointWithinPolygon.close();
            psIsPointWithinPolygon = null;
        }
    }

    /*
     * Prepare the statement that finds an intersection of 2 geometries.
     */
    private void prepareIntersectionStatement() throws SQLException {
        if (psIntersection == null) {
            // See comments in navdb's stored procedure "ids__st_intersection"
            final StringBuilder buf = new StringBuilder().append(
                    "SELECT ids__st_intersection (ST_SetSRID (?::geometry, 4326), ST_SetSRID (?::geometry, 4326))");
            psIntersection = connection.prepareStatement(buf.toString());
        }
    }

    /*
     * Prepare the statement that checks whether 2 geometries intersect.
     */
    private void prepareIntersectStatement() throws SQLException {
        if (psIntersect == null) {
            // See comments in navdb's stored procedure "ids__st_intersects"
            final StringBuilder buf = new StringBuilder().append(
                    "SELECT ids__st_intersects (ST_SetSRID (?::geometry, 4326), ST_SetSRID (?::geometry, 4326))");
            psIntersect = connection.prepareStatement(buf.toString());
        }
    }

    public LinkedList<String> getListOfErrorMessages() {
        return listOfErrorMessages;
    }

    private int getNumOfPoints(String inputWKT) throws SQLException {

        if (psNPoints == null)
            psNPoints = connection.prepareStatement("SELECT ST_NPoints(?::geometry)");

        psNPoints.setString(1, inputWKT);

        ResultSet rs = psNPoints.executeQuery();

        if (rs.next())
            return rs.getInt(1);

        else
            return 0;
    }

    /*
     * private String getPointWKT(String inputWKT, int N) throws SQLException {
     * 
     * if (psPointN == null) psPointN =
     * connection.prepareStatement("SELECT ST_AsText(ST_PointN(?::geometry,?))");
     * 
     * psPointN.setString(1, inputWKT); psPointN.setInt(2, N);
     * 
     * ResultSet rs = psPointN.executeQuery();
     * 
     * if (rs.next()) return rs.getString(1);
     * 
     * else return null; }
     */
    private Double getPointX(String inputWKT, int N) throws SQLException {

        if (ps_NthPointX == null)
            ps_NthPointX = connection.prepareStatement("SELECT ST_X(ST_PointN(?::geometry,?))");

        ps_NthPointX.setString(1, inputWKT);
        ps_NthPointX.setInt(2, N);

        ResultSet rs = ps_NthPointX.executeQuery();

        if (rs.next())
            return new Double(rs.getFloat(1));

        else
            return null;
    }

    private Double getPointY(String inputWKT, int N) throws SQLException {

        if (ps_NthPointY == null)
            ps_NthPointY = connection.prepareStatement("SELECT ST_Y(ST_PointN(?::geometry,?))");

        ps_NthPointY.setString(1, inputWKT);
        ps_NthPointY.setInt(2, N);

        ResultSet rs = ps_NthPointY.executeQuery();

        if (rs.next())
            return new Double(rs.getFloat(1));

        else
            return null;
    }

    /**
     * Builds intersection of input (line and polygon) geographies, and returns the
     * length of this intersection IN KILOMETERS. If at least one of the input
     * parameters is not a valid geography or does not include the required
     * geography type, NULL is returned. If the input geographies are valid and
     * include the required geography types, but do not share any space (are
     * disjoint), NULL value is returned. The function writes all recognized error
     * messages into Logger object and into 'Tools.listOfErrorMessages' list. Use
     * 'Tools.getListOfErrorMessages()' to get a list of generated error messages.
     * 
     * @param lineWKT     accepts as basic (LINESTRING) as complex geographies
     *                    ('GEOMETRYCOLLECTION' and 'MULTI*' collections) in WKT
     *                    format. If collection is given, the function will filter
     *                    off any geography elements other than 'LINESTRING'. Any
     *                    number of remaining 'LINESTRING' elements will be used for
     *                    building intersection and calculating the output
     *                    intersection length.
     * @param rpAirspaces accepts a list of RPAirspace instances, storing airspace
     *                    shapes in WKT format. The type of airspace (packed into
     *                    RPAirspace) is used to identify spatial UNION of input FIR
     *                    and TMA airspaces. The function packs all list-elements
     *                    into a single 'GEOMETRYCOLLECTION' and then filters off
     *                    any geography elements other than 'POLYGON'. Any number of
     *                    remaining 'POLYGON' elements will be used for building
     *                    intersection and calculating the output intersection
     *                    length.
     */

    public Double getLinePolygonIntersectionLengthInNM(String lineWKT, LinkedList<RPAirspace> rpAirspaces)
            throws SQLException {
        Double lengthInKM = getLinePolygonIntersectionLengthInKM(lineWKT, rpAirspaces);

        if (lengthInKM == null)
            return null;

        return new Double(lengthInKM.doubleValue() * RouteFinder.KM_TO_NM);
    }

    /**
     * Builds intersection of input (line and polygon) geographies, and returns the
     * length of this intersection IN KILOMETERS. If at least one of the input
     * parameters is not a valid geography or does not include the required
     * geography type, NULL is returned. If the input geographies are valid and
     * include the required geography types, but do not share any space (are
     * disjoint), NULL value is returned. The function writes all recognized error
     * messages into Logger object and into 'Tools.listOfErrorMessages' list. Use
     * 'Tools.getListOfErrorMessages()' to get a list of generated error messages.
     * 
     * @param lineWKT     accepts as basic (LINESTRING) as complex geographies
     *                    ('GEOMETRYCOLLECTION' and 'MULTI*' collections) in WKT
     *                    format. If collection is given, the function will filter
     *                    off any geography elements other than 'LINESTRING'. Any
     *                    number of remaining 'LINESTRING' elements will be used for
     *                    building intersection and calculating the output
     *                    intersection length.
     * @param rpAirspaces accepts a list of RPAirspace instances, storing airspace
     *                    shapes in WKT format. The type of airspace (packed into
     *                    RPAirspace) is used to identify spatial UNION of input FIR
     *                    and TMA airspaces. The function packs all list-elements
     *                    into a single 'GEOMETRYCOLLECTION' and then filters off
     *                    any geography elements other than 'POLYGON'. Any number of
     *                    remaining 'POLYGON' elements will be used for building
     *                    intersection and calculating the output intersection
     *                    length.
     */

    public Double getLinePolygonIntersectionLengthInKM(String lineWKT, LinkedList<RPAirspace> rpAirspaces)
            throws SQLException {
        if (listOfErrorMessages != null)
            listOfErrorMessages.clear();

        if (rpAirspaces == null || rpAirspaces.size() == 0) {
            listOfErrorMessages.add(
                    "Second parameter of 'Tools.getLinePolygonIntersectionLengthInKM' is either Null or Empty. Cannot process it.");
            logger.debug(
                    "Second parameter of 'Tools.getLinePolygonIntersectionLengthInKM' is either Null or Empty. Cannot process it.");
            return null;
        }

        String geometryCollection = new String("GEOMETRYCOLLECTION(");

        if (rpAirspaces.size() > 1) {

            LinkedList<RPAirspace> firList = new LinkedList<RPAirspace>(); // type = FIR
            LinkedList<RPAirspace> partialAirspaceList = new LinkedList<RPAirspace>(); // type = FIR_P or TMA

            for (RPAirspace rpAirspace : rpAirspaces) {

                switch (rpAirspace.type) {
                case FIR:
                    if (rpAirspace.polygons.size() > 1) {
                        for (Polygon polygon : rpAirspace.polygons) {
                            firList.add(new RPAirspace(polygon, rpAirspace.type));
                        }
                    } else {
                        firList.add(rpAirspace);
                    }
                    break;

                case FIR_P:
                    // fall through
                case TMA:
                    if (rpAirspace.polygons.size() > 1) {
                        for (Polygon polygon : rpAirspace.polygons) {
                            partialAirspaceList.add(new RPAirspace(polygon, rpAirspace.type));
                        }
                    } else {
                        partialAirspaceList.add(rpAirspace);
                    }
                    break;
                }

            }

            if (partialAirspaceList.isEmpty()) {
                for (RPAirspace firRPAirspace : firList)
                    geometryCollection = geometryCollection + firRPAirspace.polygons.getFirst().getWKT() + ",";
            } else if (firList.isEmpty()) {
                for (RPAirspace tmaRPAirspace : partialAirspaceList)
                    geometryCollection = geometryCollection + tmaRPAirspace.polygons.getFirst().getWKT() + ",";
            } else {
                for (RPAirspace firRPAirspace : firList)
                    geometryCollection = geometryCollection + firRPAirspace.polygons.getFirst().getWKT() + ",";

                for (RPAirspace tmaRPAirspace : partialAirspaceList) {
                    boolean tmaToBeExcluded = false;

                    for (RPAirspace firRPAirspace : firList) {

                        Polygon tmaPolygon = tmaRPAirspace.polygons.getFirst();
                        Polygon firPolygon = firRPAirspace.polygons.getFirst();

                        if (tmaPolygon.isCoveredByBox(firPolygon,
                                RouteAirspaceCollocation.DEFAULT_INTERSECTION_ACCURACY_IN_METERS)) {

                            if (doCover(firPolygon.getWKT(), tmaPolygon.getCenterPoint().getWKT())) {
                                tmaToBeExcluded = true;
                                break;
                            }
                        }
                    }

                    if (!tmaToBeExcluded)
                        geometryCollection = geometryCollection + tmaRPAirspace.polygons.getFirst().getWKT() + ",";
                }
            }
        }

        else if (rpAirspaces.getFirst().polygons.size() > 1) {
            for (Polygon polygon : rpAirspaces.getFirst().polygons)
                geometryCollection = geometryCollection + polygon.getWKT() + ",";
        }

        else
            geometryCollection = geometryCollection + rpAirspaces.getFirst().polygons.getFirst().getWKT() + ",";

        geometryCollection = geometryCollection.substring(0, geometryCollection.length() - 1) + ")";

        return getLinePolygonIntersectionLengthInKM(lineWKT, geometryCollection);
    }

    /**
     * Builds intersection of input (line and polygon) geographies, and returns the
     * length of this intersection IN KILOMETERS. If at least one of the input
     * parameters is not a valid geography or does not include the required
     * geography type, NULL is returned. If the input geographies are valid and
     * include the required geography types, but do not share any space (are
     * disjoint), NULL value is returned. The function writes all recognized error
     * messages into Logger object and into 'Tools.listOfErrorMessages' list. Use
     * 'Tools.getListOfErrorMessages()' to get a list of generated error messages.
     * 
     * @param lineWKT     accepts as basic (LINESTRING) as complex geographies
     *                    ('GEOMETRYCOLLECTION' and 'MULTI*' collections) in WKT
     *                    format. If collection is given, the function will filter
     *                    off any geography elements other than 'LINESTRING'. Any
     *                    number of remaining 'LINESTRING' elements will be used for
     *                    building intersection and calculating the output
     *                    intersection length.
     * @param polygonWKTs accepts a list of as basic (POLYGON) as complex
     *                    geographies ('GEOMETRYCOLLECTION' and 'MULTI*'
     *                    collections) in WKT format. The function packs all
     *                    list-elements into a single 'GEOMETRYCOLLECTION' and then
     *                    filters off any geography elements other than 'POLYGON'.
     *                    Any number of remaining 'POLYGON' elements will be used
     *                    for building intersection and calculating the output
     *                    intersection length.
     */

    public Double getLinePolygonIntersectionLengthInKM(String lineWKT, List<String> polygonWKTs) throws SQLException {
        if (listOfErrorMessages != null)
            listOfErrorMessages.clear();

        if (polygonWKTs == null || polygonWKTs.size() == 0) {
            listOfErrorMessages.add(
                    "Second parameter of 'Tools.getLinePolygonIntersectionLengthInKM' is either Null or Empty. Cannot process it.");
            logger.debug(
                    "Second parameter of 'Tools.getLinePolygonIntersectionLengthInKM' is either Null or Empty. Cannot process it.");
            return null;
        }

        String geometryCollection = new String("GEOMETRYCOLLECTION(");

        for (String polygonWKT : polygonWKTs) {
            geometryCollection = geometryCollection + polygonWKT + ",";
        }

        geometryCollection = geometryCollection.substring(0, geometryCollection.length() - 1) + ")";

        return getLinePolygonIntersectionLengthInKM(lineWKT, geometryCollection);
    }

    /**
     * Builds intersection of input (line and polygon) geographies, and returns the
     * length of this intersection IN KILOMETERS. If at least one of the input
     * parameters is not a valid geography or does not include the required
     * geography type, NULL is returned. If the input geographies are valid and
     * include the required geography types, but do not share any space (are
     * disjoint), NULL value is returned. The function writes all recognized error
     * messages into Logger object and into 'Tools.listOfErrorMessages' list. Use
     * 'Tools.getListOfErrorMessages()' to get a list of generated error messages.
     * 
     * @param lineWKT    accepts as basic (LINESTRING) as complex geographies
     *                   ('GEOMETRYCOLLECTION' and 'MULTI*' collections) in WKT
     *                   format. If collection is given, the function will filter
     *                   off any geography elements other than 'LINESTRING'. Any
     *                   number of remaining 'LINESTRING' elements will be used for
     *                   building intersection and calculating the output
     *                   intersection length.
     * @param polygonWKT accepts as basic (POLYGON) as complex geographies
     *                   ('GEOMETRYCOLLECTION' and 'MULTI*' collections) in WKT
     *                   format. If collection is given, the function filters off
     *                   any geography elements other than 'POLYGON'. Any number of
     *                   remaining 'POLYGON' elements will be used for building
     *                   intersection and calculating the output intersection
     *                   length.
     */
    public Double getLinePolygonIntersectionLengthInKM(String lineWKT, String polygonWKT) throws SQLException {

        String intersectionWKT = null;
        Double dblTotalIntersectionLength = null;

        LinkedList<String> tempWKTs;
        LinkedList<String> listOfLineWKTs = new LinkedList<String>();
        LinkedList<String> listOfPolygonWKTs = new LinkedList<String>();
        ResultSet rs;

        boolean firstParameterProcessedFlag = false;
        boolean secondParameterProcessedFlag = false;
        boolean intersectionProcessedFlag = false;

        if (listOfErrorMessages != null)
            listOfErrorMessages.clear();

        if (connection == null && (psIntersection == null || psLength == null)) {
            listOfErrorMessages.add(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statements in 'Tools.getLinePolygonIntersectionLengthInKM'.");
            logger.debug(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statements in 'Tools.getLinePolygonIntersectionLengthInKM'.");
            return null;
        }

        if (lineWKT == null || lineWKT.isEmpty()) {
            listOfErrorMessages.add(
                    "First parameter of 'Tools.getLinePolygonIntersectionLengthInKM' is either Null or Empty. Cannot process it.");
            logger.debug(
                    "First parameter of 'Tools.getLinePolygonIntersectionLengthInKM' is either Null or Empty. Cannot process it.");
            return null;
        }

        if (polygonWKT == null || polygonWKT.isEmpty()) {
            listOfErrorMessages.add(
                    "Second parameter of 'Tools.getLinePolygonIntersectionLengthInKM' is either Null or Empty. Cannot process it.");
            logger.debug(
                    "Second parameter of 'Tools.getLinePolygonIntersectionLengthInKM' is either Null or Empty. Cannot process it.");
            return null;
        }

        /*
         * if (!isValidGeometry(lineWKT)) { logger.
         * debug("First parameter of 'Tools.getLinePolygonIntersectionLengthInKM' is Not a Valid Geometry/Geography. Cannot process it."
         * );
         * 
         * SQLWarning w = psIsValid.getWarnings(); while (w != null) { if
         * (w.getMessage() != null) { logger.info ("SQLSTATE {} - {}", w.getSQLState(),
         * w.getMessage()); } w = w.getNextWarning(); }
         * 
         * return null; }
         * 
         * if (!isValidGeometry(polygonWKT)) { logger.
         * debug("Second parameter of 'Tools.getLinePolygonIntersectionLengthInKM' is Not a Valid Geometry/Geography. Cannot process it."
         * );
         * 
         * SQLWarning w = psIsValid.getWarnings(); while (w != null) { if
         * (w.getMessage() != null) { logger.info ("SQLSTATE {} - {}", w.getSQLState(),
         * w.getMessage()); } w = w.getNextWarning(); }
         * 
         * return null; }
         */
        try {
            tempWKTs = getListOfSingleNormalGeometries(lineWKT, 2);
            for (String tempWKT : tempWKTs) {
                listOfLineWKTs.add(tempWKT);
            }
            tempWKTs.clear();

            if (listOfLineWKTs == null || listOfLineWKTs.isEmpty()) {
                listOfErrorMessages.add(
                        "First parameter of 'Tools.getLinePolygonIntersectionLengthInKM' does not include correct 'LINESTRING' geography type. Cannot process it.");
                logger.debug(
                        "First parameter of 'Tools.getLinePolygonIntersectionLengthInKM' does not include correct 'LINESTRING' geography type. Cannot process it.");
                return null;
            }

            for (String tempLineWKT : listOfLineWKTs) {

                int numOfPoints = getNumOfPoints(tempLineWKT);

                if (numOfPoints < 2)
                    continue;

                if (getPointX(tempLineWKT, 1).equals(getPointX(tempLineWKT, numOfPoints))
                        && getPointY(tempLineWKT, 1).equals(getPointY(tempLineWKT, numOfPoints))) {

                    if (numOfPoints == 2)
                        continue;

                    for (int i = 1; i < numOfPoints; i++) {
                        tempWKTs.add(new String("LINESTRING(" + getPointX(tempLineWKT, i).toString() + " "
                                + getPointY(tempLineWKT, i).toString() + "," + getPointX(tempLineWKT, i + 1).toString()
                                + " " + getPointY(tempLineWKT, i + 1).toString() + ")"));
                    }
                }
            }

            if (!tempWKTs.isEmpty()) {
                listOfLineWKTs.clear();
                listOfLineWKTs.addAll(tempWKTs);
                tempWKTs.clear();
            }

            firstParameterProcessedFlag = true;

            tempWKTs = getListOfSingleNormalGeometries(polygonWKT, 3);
            for (String tempWKT : tempWKTs) {
                listOfPolygonWKTs.add(tempWKT);
            }
            tempWKTs.clear();

            if (listOfPolygonWKTs == null || listOfPolygonWKTs.isEmpty()) {
                listOfErrorMessages.add(
                        "Second parameter of 'Tools.getLinePolygonIntersectionLengthInKM' does not include correct 'POLYGON' geography type. Cannot process it.");
                logger.debug(
                        "Second parameter of 'Tools.getLinePolygonIntersectionLengthInKM' does not include correct 'POLYGON' geography type. Cannot process it.");
                return null;
            }

            secondParameterProcessedFlag = true;

            prepareIntersectionStatement();
            if (psLength == null)
                psLength = connection.prepareStatement("SELECT ST_Length(?::geography, true)");

            for (String tempLineWKT : listOfLineWKTs) {

                psIntersection.setString(1, tempLineWKT);

                for (String tempPolygonWKT : listOfPolygonWKTs) {

                    psIntersection.setString(2, tempPolygonWKT);
                    rs = psIntersection.executeQuery();

                    if (rs.next()) {
                        intersectionWKT = rs.getString(1);

                        if (isEmptyGeometry(intersectionWKT))
                            continue;

                        psLength.setString(1, intersectionWKT);
                        rs = psLength.executeQuery();

                        if (rs.next()) {
                            if (dblTotalIntersectionLength == null)
                                dblTotalIntersectionLength = new Double(0.0);
                            dblTotalIntersectionLength = dblTotalIntersectionLength + (double) rs.getFloat(1);
                        }
                    }
                }
            }

            if (dblTotalIntersectionLength != null)
                dblTotalIntersectionLength = dblTotalIntersectionLength / 1000.0;

            intersectionProcessedFlag = true;
            return dblTotalIntersectionLength;
        }

        finally {
            if (!firstParameterProcessedFlag) {
                listOfErrorMessages.add(
                        "First parameter of 'Tools.getLinePolygonIntersectionLengthInKM' could not be processed. Format verification is required.");
                logger.debug(
                        "First parameter of 'Tools.getLinePolygonIntersectionLengthInKM' could not be processed. Format verification is required.");
                return null;
            } else if (!secondParameterProcessedFlag) {
                listOfErrorMessages.add(
                        "Second parameter of 'Tools.getLinePolygonIntersectionLengthInKM' could not be processed. Format verification is required.");
                logger.debug(
                        "Second parameter of 'Tools.getLinePolygonIntersectionLengthInKM' could not be processed. Format verification is required.");
                return null;
            } else if (!intersectionProcessedFlag) {
                listOfErrorMessages.add(
                        "Both parameters of 'Tools.getLinePolygonIntersectionLengthInKM' have been processed successfully. Was not able to build intersection.");
                logger.debug(
                        "Both parameters of 'Tools.getLinePolygonIntersectionLengthInKM' have been processed successfully. Was not able to build intersection.");
                return null;
            }
        }
    }

    public Boolean isEmptyGeometry(String geoWKT) throws SQLException {

//			if (listOfErrorMessages == null) listOfErrorMessages = new LinkedList<String>();

        if (connection == null && psIsEmpty == null) {
            listOfErrorMessages.add(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.isEmptyGeometry'.");
            logger.debug(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.isEmptyGeometry'.");
            return null;
        }

        if (psIsEmpty == null)
            psIsEmpty = connection.prepareStatement("SELECT ST_IsEmpty(?::geometry)");

        psIsEmpty.setString(1, geoWKT);
        ResultSet rs = psIsEmpty.executeQuery();

        if (rs.next())
            return new Boolean(rs.getBoolean(1));

        else
            return null;
    }

    /**
     * Segmentize a geometry. This will add additional points to lines and polygon
     * borders to make sure there are no segments that are very far apart. The
     * algorithm that adds additional points uses spherical paths (great arcs). This
     * is necessary before performing any geometry operations using planar
     * algorithms, such as ST_Intersectrion (geometry, geometry);
     */
    public String segmentize(final String wkt) throws SQLException {
        if (psSegmentize == null) {
            psSegmentize = connection.prepareStatement(
                    "SELECT ST_AsText (ST_Segmentize (ST_SetSRID (?::geometry, 4326)::geography, ?)::geometry)");
        }
        psSegmentize.setString(1, wkt);
        psSegmentize.setFloat(2, (float) SEGMENTIZE_METERS);
        final ResultSet rs = psSegmentize.executeQuery();
        try {
            rs.next();
            return rs.getString(1);
        } finally {
            rs.close();
        }
    }

    public Boolean isValidGeometry(String geoWKT, AtomicReference<String> strReason) throws SQLException {
        Boolean blnIsValid;

//			if (listOfErrorMessages == null) listOfErrorMessages = new LinkedList<String>();

        if (connection == null && psIsValid == null) {
            listOfErrorMessages.add(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.isValidGeometry'.");
            logger.debug(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.isValidGeometry'.");
            return null;
        }

        if (psIsValid == null)
            psIsValid = connection.prepareStatement("SELECT ST_IsValid(?::geometry)");

        psIsValid.setString(1, geoWKT);

        ResultSet rs = psIsValid.executeQuery();

        if (rs.next()) {
            blnIsValid = rs.getBoolean(1);

            if (!blnIsValid) {
                SQLWarning w = psIsValid.getWarnings();
                while (w != null) {
                    if (w.getMessage() != null) {
                        logger.info("SQLSTATE {} - {}", w.getSQLState(), w.getMessage());
                        listOfErrorMessages
                                .add("Geometry validity check failed by the following reason: " + w.getMessage());
                        strReason.set(w.getMessage());
                    }
                    w = w.getNextWarning();
                }
            }

            return blnIsValid;
        } else
            return null;
    }

    public Boolean isGeoCollection(String geoWKT) throws SQLException {

//			if (listOfErrorMessages == null) listOfErrorMessages = new LinkedList<String>();

        if (connection == null && psIsCollection == null) {
            listOfErrorMessages.add(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.isGeoCollection'.");
            logger.debug(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.isGeoCollection'.");
            return null;
        }

        if (psIsCollection == null)
            psIsCollection = connection.prepareStatement("SELECT ST_IsCollection(?::geometry)");

        psIsCollection.setString(1, geoWKT);
        ResultSet rs = psIsCollection.executeQuery();

        if (rs.next())
            return new Boolean(rs.getBoolean(1));

        else
            return null;
    }

    public String getGeometryType(String geometryWKT) throws SQLException {

//			if (listOfErrorMessages == null) listOfErrorMessages = new LinkedList<String>();

        if (connection == null && psGeometryType == null) {
            listOfErrorMessages.add(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.getGeometryType'.");
            logger.debug(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.getGeometryType'.");
            return null;
        }

        if (psGeometryType == null)
            psGeometryType = connection.prepareStatement("SELECT ST_GeometryType(?::geometry)");

        psGeometryType.setString(1, geometryWKT);
        ResultSet rs = psGeometryType.executeQuery();

        if (rs.next())
            return rs.getString(1);

        else
            return null;
    }

    public Integer getNumOfGeometries(String inputWKT) throws SQLException {

//			if (listOfErrorMessages == null) listOfErrorMessages = new LinkedList<String>();

        if (connection == null && psNumGeometries == null) {
            listOfErrorMessages.add(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.getNumOfGeometries'.");
            logger.debug(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.getNumOfGeometries'.");
            return null;
        }

        if (psNumGeometries == null)
            psNumGeometries = connection.prepareStatement("SELECT ST_NumGeometries(?::geometry)");

        psNumGeometries.setString(1, inputWKT);
        ResultSet rs = psNumGeometries.executeQuery();

        if (rs.next())
            return rs.getInt(1);

        else
            return null;
    }

    public String getGeometryN(String inputWKT, int N) throws SQLException {

//		if (listOfErrorMessages == null) listOfErrorMessages = new LinkedList<String>();

        if (connection == null && psGeometryN == null) {
            listOfErrorMessages.add(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.getGeometryN'.");
            logger.debug(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.getGeometryN'.");
            return null;
        }

        if (psGeometryN == null)
            psGeometryN = connection.prepareStatement("SELECT ST_AsText(ST_GeometryN(?::geometry, ?))");

        psGeometryN.setString(1, inputWKT);
        psGeometryN.setInt(2, N);
        ResultSet rs = psGeometryN.executeQuery();

        if (rs.next())
            return rs.getString(1);

        else
            return null;
    }

    public String getCollectionExtract(String geoWKT, int type) throws SQLException {

//			if (listOfErrorMessages == null) listOfErrorMessages = new LinkedList<String>();

        if (connection == null && psCollectionExtract == null) {
            listOfErrorMessages.add(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.getCollectionExtract'.");
            logger.debug(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.getCollectionExtract'.");
            return null;
        }

        if (psCollectionExtract == null)
            psCollectionExtract = connection.prepareStatement(
                    "SELECT ST_AsText(ST_CollectionHomogenize(ST_CollectionExtract(?::geometry, ?)))");

        psCollectionExtract.setString(1, geoWKT);
        psCollectionExtract.setInt(2, type);
        ResultSet rs = psCollectionExtract.executeQuery();

        if (rs.next())
            return rs.getString(1);

        else
            return null;
    }

    public String getGeoIntersection(String geoWKT_A, String geoWKT_B) throws SQLException {

//			if (listOfErrorMessages == null) listOfErrorMessages = new LinkedList<String>();

        if (connection == null && psIntersection == null) {
            listOfErrorMessages.add(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.getGeoIntersection'.");
            logger.debug(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.getGeoIntersection'.");
            return null;
        }

        prepareIntersectionStatement();
        psIntersection.setString(1, geoWKT_A);
        psIntersection.setString(2, geoWKT_B);
        ResultSet rs = psIntersection.executeQuery();

        if (rs.next())
            return rs.getString(1);

        else
            return null;
    }

    public Double getLengthInMeters(MultiLineString mltLineString) throws SQLException {

        if (mltLineString.lineStrings == null || mltLineString.lineStrings.isEmpty())
            return null;

        Double outputLength = new Double(0.0d);

        for (LineString line : mltLineString.lineStrings)
            outputLength = new Double(outputLength.doubleValue() + getLengthInMeters(line).doubleValue());

        return outputLength;
    }

    public Double getLengthInMeters(LineString lineString) throws SQLException {

        if (lineString.points == null || lineString.points.isEmpty())
            return null;

        return getLengthInMeters(lineString.getWKT());
    }

    public Double getLengthInMeters(String inputWKT) throws SQLException {

        if (connection == null && psLength == null) {
            listOfErrorMessages
                    .add("'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.getLength'.");
            logger.debug("'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.getLength'.");
            return null;
        }

        if (psLength == null)
            psLength = connection.prepareStatement("SELECT ST_Length(?::geography, true)");

        psLength.setString(1, inputWKT);
        ResultSet rs = psLength.executeQuery();

        if (rs.next())
            return new Double((double) rs.getFloat(1));

        else
            return null;
    }

    public String getWKTfromRouteStructure(LinkedList<RoutePoint> inputRouteStructure) {

        if (inputRouteStructure == null || inputRouteStructure.size() < 2)
            return null;

        String outputWKT = "LINESTRING(";

        for (RoutePoint routePoint : inputRouteStructure) {
            outputWKT = outputWKT + routePoint.getLongitude().toString() + " " + routePoint.getLatitude().toString()
                    + ",";
        }

        outputWKT = outputWKT.substring(0, outputWKT.length() - 1) + ")";

        return outputWKT;
    }

    public int getNumOfSameReturnPoints(List<RoutePoint> inputRouteStructure) {
        if (inputRouteStructure == null)
            return 0;

        int routeSize = inputRouteStructure.size();
        int routeHalfSize = routeSize / 2;
        int outputValue = 0;

        for (int i = 0; i < routeHalfSize; i++) {
            if (inputRouteStructure.get(0).equals(inputRouteStructure.get(routeSize - 1 - i)))
                outputValue++;
            else
                break;
        }

        return outputValue;
    }

    public Boolean doIntersect(String geoWKT_A, String geoWKT_B) throws SQLException {

//			if (listOfErrorMessages == null) listOfErrorMessages = new LinkedList<String>();

        if (connection == null && psIntersect == null) {
            listOfErrorMessages.add(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.doIntersect'.");
            logger.debug(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.doIntersect'.");
            return null;
        }

        this.prepareIntersectStatement();

        psIntersect.setString(1, geoWKT_A);
        psIntersect.setString(2, geoWKT_B);
        ResultSet rs = psIntersect.executeQuery();

        if (rs.next())
            return new Boolean(rs.getBoolean(1));

        else
            return null;
    }

    /**
     * Return true if the given point is inside the given polygon within the given
     * tolerance in meters. The polygon must be "segmentized", i.e. "ST_Segmentize
     * (geography, geography, max_segment_length)"
     */
    public boolean isPointWithinPolygon(final String pointWkt, final String polygonWkt, final double toleranceMeters)
            throws SQLException {
        // If either geometry is null => always return false
        if (pointWkt == null && polygonWkt == null) {
            return false;
        }
        // Create the statement
        if (connection == null && psIsPointWithinPolygon == null) {
            listOfErrorMessages.add(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.isPointWithinPolygon'.");
            logger.debug(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.isPointWithinPolygon'.");
            return false;
        }
        if (psIsPointWithinPolygon == null) {
            psIsPointWithinPolygon = connection.prepareStatement("SELECT 1 FROM "
                    + "(SELECT ST_SetSRID (?::GEOMETRY, 4326) AS point, ST_SetSRID (?::GEOMETRY, 4326) AS polygon, ?::DOUBLE PRECISION AS tolerance_meters) AS x "
                    + "WHERE ST_DWithin (point::geography, polygon::geography, tolerance_meters) OR ids__st_intersects (point, polygon)");
        }
        // Set parameters
        psIsPointWithinPolygon.setString(1, pointWkt);
        psIsPointWithinPolygon.setString(2, polygonWkt);
        psIsPointWithinPolygon.setDouble(3, toleranceMeters);

        // Return true if there are any results
        try (final ResultSet rs = psIsPointWithinPolygon.executeQuery()) {
            return rs.next();
        }
    }

    public Boolean doCover(String polygonGeographyWKT, String pointGeographyWKT) throws SQLException {

        if (connection == null && psPolygonCoversPoint == null) {
            listOfErrorMessages
                    .add("'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.doCover'.");
            logger.debug("'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.doCover'.");
            return null;
        }

        if (psPolygonCoversPoint == null)
            psPolygonCoversPoint = connection.prepareStatement("SELECT ST_Covers(?::geography, ?::geography)");

        psPolygonCoversPoint.setString(1, polygonGeographyWKT);
        psPolygonCoversPoint.setString(2, pointGeographyWKT);
        ResultSet rs = psPolygonCoversPoint.executeQuery();

        if (rs.next())
            return new Boolean(rs.getBoolean(1));

        else
            return null;
    }

    public Boolean doCover(String geographyWKT_A, String geographyWKT_B, int SRID) throws SQLException {

        if (connection == null && psCovers == null) {
            listOfErrorMessages
                    .add("'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.doCover'.");
            logger.debug("'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.doCover'.");
            return null;
        }

        if (psCovers == null)
            psCovers = connection.prepareStatement(
                    "SELECT ST_Covers(ST_Transform(?::geography::geometry, ?), ST_Transform(?::geography::geometry, ?))");

        psCovers.setString(1, geographyWKT_A);
        psCovers.setInt(2, SRID);
        psCovers.setString(3, geographyWKT_B);
        psCovers.setInt(4, SRID);
        ResultSet rs = psCovers.executeQuery();

        if (rs.next())
            return new Boolean(rs.getBoolean(1));

        else
            return null;
    }

    public Integer getSRID(Point point) {

        if (point.SRID.intValue() != Geometry.GeogSRID.intValue())
            return null;

        int baseSrid;

        if (point.getY().doubleValue() >= 0) {
            baseSrid = 32601;
        } else { // if (pointLat < 0)
            baseSrid = 32701;
        }

        double tempDouble = (point.getX().doubleValue() + 180.0d) / 6.0d;
        double sridOffset = Math.floor(tempDouble);

        return new Integer(baseSrid + (int) sridOffset);
    }

    public Integer getSRID(String pointWKT) throws SQLException {

        double pointLong;
        double pointLat;

        if (connection == null && (ps_X == null || ps_Y == null)) {
            listOfErrorMessages
                    .add("'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.getSRID'.");
            logger.debug("'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.getSRID'.");
            return null;
        }

        if (ps_X == null)
            ps_X = connection.prepareStatement("SELECT ST_X(?::geometry)");
        if (ps_Y == null)
            ps_Y = connection.prepareStatement("SELECT ST_Y(?::geometry)");

        ps_X.setString(1, pointWKT);
        ResultSet rs = ps_X.executeQuery();
        if (rs.next())
            pointLong = rs.getFloat(1);
        else
            return null;

        ps_Y.setString(1, pointWKT);
        rs = ps_Y.executeQuery();
        if (rs.next())
            pointLat = rs.getFloat(1);
        else
            return null;

        rs.close();

        int baseSrid = 0;

        if (pointLat >= 0) {
            baseSrid = 32601;
        } else { // if (pointLat < 0)
            baseSrid = 32701;
        }

        double tempDouble = (pointLong + 180.0d) / 6.0d;
        double sridOffset = Math.floor(tempDouble);

        return new Integer(baseSrid + (int) sridOffset);
    }

    /*
     * public String tryCombineMultiline(String inputWKT) throws SQLException {
     * 
     * if (!(getGeometryType(inputWKT).equalsIgnoreCase("ST_MultiLinestring")))
     * return inputWKT;
     * 
     * if (connection == null && psIntersection == null) { listOfErrorMessages.
     * add("'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.getIntersection'."
     * ); logger.
     * debug("'Tools.Connection' variable is Null. Cannot create Prepared Statement in 'Tools.getIntersection'."
     * ); return null; }
     * 
     * Integer numOfGeoms = getNumOfGeometries(inputWKT); if (numOfGeoms == null)
     * return null;
     * 
     * LinkedList<String> listOfLineWKTs = new LinkedList<String>();
     * 
     * for (int i = 0; i < numOfGeoms; i++) {
     * 
     * }
     * 
     * prepareIntersectionStatement(); psIntersection.setString(1, geoWKT_A);
     * psIntersection.setString(2, geoWKT_B); ResultSet rs =
     * psIntersection.executeQuery();
     * 
     * if (rs.next()) return rs.getString(1);
     * 
     * else return null; }
     */

    public LinkedList<String> getListOfSingleNormalGeometries(String inputWKT, int typeToExtract) throws SQLException {
        String inputWKTtype;
        String tempWKT;
        String exteriorRingWKT;
        Boolean boolTemp;
        Integer numOfGeometries;
        int numOfPoints;
        LinkedList<String> listOfOutputWKTs;
        ResultSet rs;

//			if (listOfErrorMessages == null) listOfErrorMessages = new LinkedList<String>();

        if (typeToExtract < 2 || typeToExtract > 3) {
            listOfErrorMessages.add(
                    "'int typeToExtract' parameter of 'Tools.getListOfSingleNormalGeometries' is either less than 2 or greater than 3 and cannot be processed.");
            logger.debug(
                    "'int typeToExtract' parameter of 'Tools.getListOfSingleNormalGeometries' is either less than 2 or greater than 3 and cannot be processed.");
            return null;
        }

        boolTemp = isGeoCollection(inputWKT);

        if (boolTemp == null)
            return null;
        else if (boolTemp)
            inputWKT = getCollectionExtract(inputWKT, typeToExtract);

        inputWKTtype = getGeometryType(inputWKT);
        if (inputWKTtype == null || inputWKTtype.isEmpty())
            return null;

        boolTemp = isEmptyGeometry(inputWKT);

        if (boolTemp == null)
            return null;
        else if (boolTemp)
            return null;

        if (connection == null && (psExteriorRing == null || psRemoveRepeatedPoints == null || psNPoints == null)) {
            listOfErrorMessages.add(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statements in 'Tools.getListOfSingleNormalGeometries'.");
            logger.debug(
                    "'Tools.Connection' variable is Null. Cannot create Prepared Statements in 'Tools.getListOfSingleNormalGeometries'.");
            return null;
        }

        if (psExteriorRing == null)
            psExteriorRing = connection.prepareStatement("SELECT ST_AsText(ST_ExteriorRing(?::geometry))");
        if (psRemoveRepeatedPoints == null)
            psRemoveRepeatedPoints = connection
                    .prepareStatement("SELECT ST_AsText(ST_RemoveRepeatedPoints(?::geometry))");
        if (psNPoints == null)
            psNPoints = connection.prepareStatement("SELECT ST_NPoints(?::geometry)");

        if ((typeToExtract == 2 && inputWKTtype.equalsIgnoreCase("ST_Linestring"))
                || (typeToExtract == 3 && inputWKTtype.equalsIgnoreCase("ST_Polygon"))) {

            psRemoveRepeatedPoints.setString(1, inputWKT);
            rs = psRemoveRepeatedPoints.executeQuery();

            if (rs.next())
                inputWKT = rs.getString(1);
            else
                return null;

            exteriorRingWKT = inputWKT;

            if (typeToExtract == 3) {
                psExteriorRing.setString(1, inputWKT);
                rs = psExteriorRing.executeQuery();

                if (rs.next())
                    exteriorRingWKT = rs.getString(1);
                else
                    return null;
            }

            psNPoints.setString(1, exteriorRingWKT);
            rs = psNPoints.executeQuery();

            if (rs.next())
                numOfPoints = rs.getInt(1);
            else
                return null;

            if ((typeToExtract == 2 && numOfPoints > 1) || (typeToExtract == 3 && numOfPoints > 2)) {

                listOfOutputWKTs = new LinkedList<String>();
                listOfOutputWKTs.add(inputWKT);
                return listOfOutputWKTs;
            }
        }

        else if ((typeToExtract == 2 && inputWKTtype.equalsIgnoreCase("ST_MultiLinestring"))
                || (typeToExtract == 3 && inputWKTtype.equalsIgnoreCase("ST_MultiPolygon"))) {

            numOfGeometries = getNumOfGeometries(inputWKT);

            if (numOfGeometries == null || numOfGeometries < 1)
                return null;

            listOfOutputWKTs = new LinkedList<String>();

            for (int i = 1; i <= numOfGeometries; i++) {

                tempWKT = getGeometryN(inputWKT, i);

                if (tempWKT == null)
                    continue;

                psRemoveRepeatedPoints.setString(1, tempWKT);
                rs = psRemoveRepeatedPoints.executeQuery();

                if (rs.next())
                    tempWKT = rs.getString(1);
                else
                    continue;

                exteriorRingWKT = tempWKT;

                if (typeToExtract == 3) {
                    psExteriorRing.setString(1, tempWKT);
                    rs = psExteriorRing.executeQuery();

                    if (rs.next())
                        exteriorRingWKT = rs.getString(1);
                    else
                        continue;
                }

                psNPoints.setString(1, exteriorRingWKT);
                rs = psNPoints.executeQuery();

                if (rs.next())
                    numOfPoints = rs.getInt(1);
                else
                    continue;

                if ((typeToExtract == 2 && numOfPoints > 1) || (typeToExtract == 3 && numOfPoints > 2)) {

                    listOfOutputWKTs.add(tempWKT);
                }
            }

            if (listOfOutputWKTs.size() > 0)
                return listOfOutputWKTs;
        }

        return null;
    }

}
