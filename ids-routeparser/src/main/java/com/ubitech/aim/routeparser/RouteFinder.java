package com.ubitech.aim.routeparser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements getRouteLineWKT()
 * <p>
 * getRouteLineWKT():
 * <p>
 * a) Parses route string into a list of tokens, each of which represents either
 * a Point (Point-derived entity), an Airway or a Key Word (like DCT, IFR, VFR,
 * SID, STAR).
 * <p>
 * b) Drives token parsing using TokenParser class; The list of possible
 * interpretations for each token is stored into an instance of
 * MapOfTokenInterpretations class. The list of MapOfTokenInterpretations
 * instances are stored into 'spaceOfTokenInterpretations'
 * (ArrayList<MapOfTokenInterpretations>), whose size is equal to the number of
 * tokens in the route string (excluding departure and arrival aerodromes).
 * <p>
 * c) Drives population of 'listOfRouteScenarios' (LinkedList<RouteScenario>)
 * based on the content of 'spaceOfTokenInterpretations' ArrayList.
 * <p>
 * d) Sorts 'listOfRouteScenarios' using RouteScenarioComp class.
 * <p>
 * e) Generates and returns route path in well-known text format (WKT) based on
 * the route scenario (RouteScenario class instance) sorted into the first
 * position of 'listOfRouteScenarios'.
 */
public class RouteFinder {
    static final double MAX_RATIO_FOR_ROUTE_LENGTH = 1.7d;
    static final double MAX_RATIO_FOR_NOT_FOUND_TOKENS = 1.0d;
    static final double MAX_COURSE_CHANGE_IN_RADIANS = Math.PI / 2.0d;
    static final double MAX_RTE_SEGMENT_LENGTH = 185200.0d;
    static final double NM_TO_METER = 1852.0d;
    static final double METER_TO_NM = 1.0d / NM_TO_METER;
    static final double KM_TO_NM = METER_TO_NM * 1000.0d;
    static final double MACH_TO_KMPH = 1224.0d;
    static final double BUFFER_RAD_IN_METERS = 100000.0d;
    static final double SID_STAR_PNT_TOLERANCE_IN_METERS = 100000.0d;
    static final String NAVAIDS_EXTRACTED_BYDEFAULT = "VOR TACAN NDB";

    private Connection connection = null;
    private DataRetriver dataRetriver = null;
    private TokenParser tokenParser = null;
    private Tools tools = null;
//	private PreparedStatement psDistance = null;
//	private PreparedStatement psAzimuth = null;
    private PreparedStatement psBuffer = null;
    private PreparedStatement psDifference = null;

    private Timestamp timeStamp = null;
    private String fullRoute = null;
    private String strOriginToken = null;
    private String strDestinationToken[] = null;
    private RoutePoint originRoutePoint = null;
    private RoutePoint destinationRoutePoints[] = null;

    private TokenInterpretation originTokenInterpretation = null;
    private TokenInterpretation destinTokenInterpretation = null;

//	Route parsing & interpretation process parameters ========
    private double localMaxRatioForRteLength = MAX_RATIO_FOR_ROUTE_LENGTH;
    private double localMaxRatioForNotFoundTokens = 0.5d;
    private double localMaxCourseChangeInRadians = MAX_COURSE_CHANGE_IN_RADIANS;
    private boolean localIngestIncorrectFormat = true;
    private boolean localIngestIncorrectSyntax = true;
//	==========================================================

    private LinkedList<String> listOfErrorMessages = null;

    private LinkedList<String> listOfRouteWKTs = null;
    private RouteScenario lastFoundRouteScenario = null;
    private RouteDraft lastFoundRouteDraft = null;
    private LinkedList<TokenInterpretation> listOfLastFoundAltAD_TI = null;
    private LinkedList<RoutePoint> listOfLastFoundAltAD_RP = null;
    private ArrayList<RoutePoint> lastFoundRouteStructure = null;
    private LinkedList<RoutePortion> listOfRoutePortionsByFlightLevel = null;
    private TokenInterpretation lastNotFoundADHP = null;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String[] tokenArray = null;
    private List<String> tokenList = null;
    private int tokenListSize;

    private ArrayList<MapOfTokenInterpretations> spaceOfTokenInterpretations;

    private Pattern patternLINESTRING = null;
    private Pattern patternPOLYGON = null;
    private Pattern patternPolygonParts = null;
    private Pattern patternLonLat = null;
    private Pattern patternCruisingSpeed = null;
    private Pattern patternMachNumber = null;
    private Pattern patternTimeHHMM = null;

    public RouteFinder() {
        this.timeStamp = new Timestamp(new java.util.Date().getTime());
        this.listOfErrorMessages = new LinkedList<String>();
    }

    public RouteFinder(Connection connection) {
        this.connection = connection;
        this.timeStamp = new Timestamp(new java.util.Date().getTime());
        this.listOfErrorMessages = new LinkedList<String>();
    }

    public RouteFinder(Connection connection, String originAD, String fullRoute, String destinationAD[])
            throws SQLException {
        this.connection = connection;
        this.strOriginToken = originAD;
        this.fullRoute = fullRoute;
        this.strDestinationToken = destinationAD;
        this.timeStamp = new Timestamp(new java.util.Date().getTime());
        this.listOfErrorMessages = new LinkedList<String>();
    }

    private RouteFinder(Connection connection, TokenInterpretation startInterpretation, String fullRoute,
            TokenInterpretation endInterpretation, Timestamp timeStamp) throws SQLException {
        this.connection = connection;
        this.originTokenInterpretation = startInterpretation;
        this.fullRoute = fullRoute;
        this.destinTokenInterpretation = endInterpretation;
        this.timeStamp = timeStamp;
        this.listOfErrorMessages = new LinkedList<String>();
    }

    public LinkedList<String> getListOfErrorMessages() {
        return listOfErrorMessages;
    }

    public void setConnection(Connection connection) throws SQLException {
        if (psBuffer != null) {
            psBuffer.close();
            psBuffer = null;
        }
        if (psDifference != null) {
            psDifference.close();
            psDifference = null;
        }

        if (dataRetriver != null) {
            dataRetriver.close();
            dataRetriver = null;
        }
        if (tokenParser != null) {
            tokenParser.close();
            tokenParser = null;
        }

        this.connection = connection;
    }

    Connection getConnection() {
        return this.connection;
    }

    public void setTimestamp(String dateIso) {
        timeStamp = new Timestamp(Utils.parseDateIso(dateIso).getTime());
    }

    public Timestamp getTimestamp() {
        return this.timeStamp;
    }

    private void makeMeCloneOf(RouteFinder routeFinder) {
        this.timeStamp = routeFinder.timeStamp;
        this.fullRoute = routeFinder.fullRoute;
        this.strOriginToken = routeFinder.strOriginToken;
        this.strDestinationToken = routeFinder.strDestinationToken;
        this.originRoutePoint = routeFinder.originRoutePoint;
        this.destinationRoutePoints = routeFinder.destinationRoutePoints;

        this.originTokenInterpretation = routeFinder.originTokenInterpretation;
        this.destinTokenInterpretation = routeFinder.destinTokenInterpretation;

        this.localMaxRatioForRteLength = routeFinder.localMaxRatioForRteLength;
        this.localMaxRatioForNotFoundTokens = routeFinder.localMaxRatioForNotFoundTokens;
        this.localMaxCourseChangeInRadians = routeFinder.localMaxCourseChangeInRadians;
        this.localIngestIncorrectFormat = routeFinder.localIngestIncorrectFormat;
        this.localIngestIncorrectSyntax = routeFinder.localIngestIncorrectSyntax;
        this.listOfErrorMessages = routeFinder.listOfErrorMessages;

        this.listOfRouteWKTs = routeFinder.listOfRouteWKTs;
        this.lastFoundRouteScenario = routeFinder.lastFoundRouteScenario;
        this.lastFoundRouteDraft = routeFinder.lastFoundRouteDraft;
        this.listOfLastFoundAltAD_TI = routeFinder.listOfLastFoundAltAD_TI;
        this.listOfLastFoundAltAD_RP = routeFinder.listOfLastFoundAltAD_RP;
        this.lastFoundRouteStructure = routeFinder.lastFoundRouteStructure;
        this.listOfRoutePortionsByFlightLevel = routeFinder.listOfRoutePortionsByFlightLevel;
        this.lastNotFoundADHP = routeFinder.lastNotFoundADHP;

        this.tokenArray = routeFinder.tokenArray;
        this.tokenList = routeFinder.tokenList;
        this.tokenListSize = routeFinder.tokenListSize;

        this.spaceOfTokenInterpretations = routeFinder.spaceOfTokenInterpretations;
    }

    /**
     * As oppose to initializing a separate RouteFinder instance for each flight
     * plan, use this method to reinitialize and reuse existing RouteFinder instance
     * with a new flight plan info for further parsing/interpretation. Do not forget
     * to update TimeStamp parameter (if changed) by using setTimestamp method.
     * TimeStamp determines the effective date of data extracted from the database
     * during flight plan recognition process.
     * 
     * @param originAD  can accept four (4) different formats of the Departure
     *                  Aerodrome: 1) 4-letter ICAO designator; 2) Degrees only (7
     *                  characters) in "DD[NnSs]DDD[EeWw]" format; 3) Degrees and
     *                  minutes (11 characters) in "DDMM[NnSs]DDDMM[EeWw]" format;
     *                  4) Degrees, minutes and seconds (15 characters) in
     *                  "DDMMSS[NnSs]DDDMMSS[EeWw]" format; where 'DD' - latitude
     *                  degrees, 'DDD' - longitude degrees, 'MM' - minutes, 'SS' -
     *                  seconds, [NnSs] - single letter-code representing North or
     *                  South hemisphere respectively, [EeWw] - single letter-code
     *                  representing East or West hemisphere respectively.
     * @param fullRoute string of flight plan without departure and destination
     *                  aerodromes.
     * @param           destinationAD[] array of the Main and the Alternative
     *                  Destination Aerodromes. The four (4) different formats (as
     *                  for 'originAD' parameter) are acceptable in any combination
     *                  for a number of destination aerodromes. The Main Destination
     *                  Aerodrome is mandatory and must always be the first (zero's)
     *                  element of the array.
     */
    public void setNewFlightPlan(String originAD, String fullRoute, String destinationAD[]) {
        this.strOriginToken = originAD;
        this.fullRoute = fullRoute;
        this.strDestinationToken = destinationAD;

        listOfRouteWKTs = null;
        lastFoundRouteScenario = null;
        lastFoundRouteDraft = null;
        lastFoundRouteStructure = null;
        lastNotFoundADHP = null;
        listOfRoutePortionsByFlightLevel = null;
        tokenArray = null;
        tokenList = null;
        tokenListSize = 0;

        if (listOfLastFoundAltAD_TI != null) {
            listOfLastFoundAltAD_TI.clear();
        }

        if (listOfLastFoundAltAD_RP != null) {
            listOfLastFoundAltAD_RP.clear();
        }

        if (listOfErrorMessages != null) {
            listOfErrorMessages.clear();
        }
    }

    /**
     * Sets the maximum route length ratio - route interpretation process parameter.
     * 
     * @param maxRouteLengthRatio the maximum ratio, which is the relation of total
     *                            length of all route constituting segments to
     *                            direct distance between departure and arrival
     *                            aerodromes. If maxRouteLengthRatio is set to a
     *                            smaller value, some well curved routes (with the
     *                            higher ratio) will miss a number of intermediate
     *                            points to match ratio set in this parameter.
     * @return true if maxRouteLengthRatio value has been accepted and false
     *         otherwise. If false is returned, check listOfErrorMessages for the
     *         reason. Equal to 1.6 by default.
     */
    public boolean setMaxRouteLengthRatio(double maxRouteLengthRatio) {

        if (maxRouteLengthRatio < 1 || maxRouteLengthRatio > MAX_RATIO_FOR_ROUTE_LENGTH) {
            listOfErrorMessages.add("'maxRouteLengthRatio' parameter is less than 1 or more than "
                    + MAX_RATIO_FOR_ROUTE_LENGTH + ", which is unacceptable.");
            logger.debug("'maxRouteLengthRatio' parameter is less than 1 or more than " + MAX_RATIO_FOR_ROUTE_LENGTH
                    + ", which is unacceptable.");
            return false;
        }

        this.localMaxRatioForRteLength = maxRouteLengthRatio;
        return true;
    }

    /**
     * Sets the maximum not-found-token ratio - route interpretation process
     * parameter.
     * 
     * @param maxNotFoundTokenRatio the maximum ratio, which is the relation of the
     *                              number of all unrecognized and not found tokens
     *                              to the total number of tokens in the route
     *                              (excluding aerodromes). This parameter is used
     *                              as a limit on unrecognized tokens, which if
     *                              exceeded, triggers a more precise and
     *                              time-costly search (potentially leading to
     *                              memory overflows and time outs for routes with
     *                              more than 30 tokens). When the best route
     *                              scenario, generated at the first search run,
     *                              exceeds the maximum number of not found tokens
     *                              (the product of all tokens number times
     *                              maxNotFoundTokenRatio), the function starts a
     *                              second search and tries to regenerate the best
     *                              route scenario using a more precise but more
     *                              time-costly algorithm. By this two-phase-search,
     *                              the function manages to avoid time-costly second
     *                              search phase for those routes, which are
     *                              correctly recognized at the first phase.
     * @return true if maxNotFoundTokenRatio value has been accepted and false
     *         otherwise. If false is returned, check listOfErrorMessages for the
     *         reason. Equal to 0.5 by default
     */
    public boolean setMaxNotFoundTokenRatio(double maxNotFoundTokenRatio) {

        if (maxNotFoundTokenRatio < 0 || maxNotFoundTokenRatio > MAX_RATIO_FOR_NOT_FOUND_TOKENS) {
            listOfErrorMessages.add("'maxNotFoundTokenRatio' parameter is less than 0 or more than "
                    + MAX_RATIO_FOR_NOT_FOUND_TOKENS + ", which is unacceptable.");
            logger.debug("'maxNotFoundTokenRatio' parameter is less than 0 or more than "
                    + MAX_RATIO_FOR_NOT_FOUND_TOKENS + ", which is unacceptable.");
            return false;
        }

        this.localMaxRatioForNotFoundTokens = maxNotFoundTokenRatio;
        return true;
    }

    /**
     * Sets the maximum course change in degrees - route interpretation process
     * parameter.
     * 
     * @param maxCourseChangeInDegrees this parameter prevents construction of route
     *                                 scenarios, in which course change between two
     *                                 adjacent (consecutive) route segments exceeds
     *                                 the value set in this parameter. Using bigger
     *                                 maxRouteLengthRatio values (for more
     *                                 precise/complete recognition of well curved
     *                                 routes), increases the risk of recognition of
     *                                 false navaid feature (and dis-orientation of
     *                                 the search algorithm) when the DB misses true
     *                                 (for the parsed route) navaid feature. Thus
     *                                 setting maxCourseChangeInDegrees to a
     *                                 reasonable value (may be 60 deg) enables
     *                                 filtration of not feasible navaid features
     *                                 and mitigates the negative effect of quite
     *                                 big maxRouteLengthRatio values.
     * @return true if maxCourseChangeInDegrees value has been accepted and false
     *         otherwise. If false is returned, check listOfErrorMessages for the
     *         reason. Equal to 90 deg by default.
     */

    public boolean setMaxCourseChangeInDegrees(double maxCourseChangeInDegrees) {

        if (maxCourseChangeInDegrees <= 0
                || (maxCourseChangeInDegrees / 180 * Math.PI) > MAX_COURSE_CHANGE_IN_RADIANS) {
            listOfErrorMessages.add("'maxCourseChangeInDegrees' parameter is either not positive OR more than "
                    + MAX_COURSE_CHANGE_IN_RADIANS * 180 / Math.PI + ", which is unacceptable.");
            logger.debug("'maxCourseChangeInDegrees' parameter is less or equal zero OR more than "
                    + MAX_COURSE_CHANGE_IN_RADIANS * 180 / Math.PI + ", which is unacceptable.");
            return false;
        }

        this.localMaxCourseChangeInRadians = maxCourseChangeInDegrees / 180 * Math.PI;
        return true;
    }

    /**
     * Sets the ingest incorrect format - route interpretation process parameter.
     * 
     * @param ingestIncorrectFormat If 'false' the first token with
     *                              incorrect/unrecognized format, causes function
     *                              to return NULL. If 'true' causes function not to
     *                              stop parsing/interpretation process, but just to
     *                              ignore a token with incorrect/unrecognized
     *                              format. TRUE by default.
     */

    public void setIngestIncorrectFormat(boolean ingestIncorrectFormat) {

        this.localIngestIncorrectFormat = ingestIncorrectFormat;
    }

    /**
     * Sets the ingest incorrect syntax - route interpretation process parameter.
     * 
     * @param ingestIncorrectSyntax If 'false' the first token, which cannot
     *                              syntactically follow after preceding one, causes
     *                              function to return NULL. If 'true' causes
     *                              function not to stop parsing/interpretation
     *                              process, but just to skip a token, which cannot
     *                              follow after a preceding one to meet syntax
     *                              rules. TRUE by default.
     */

    public void setIngestIncorrectSyntax(boolean ingestIncorrectSyntax) {

        this.localIngestIncorrectSyntax = ingestIncorrectSyntax;
    }

    /**
     * Calls close() method for all instances of java.sql.PreparedStatement,
     * initialised within and served to the current instance of RouteFinder class.
     * Put all RouteFinder's methods in the body of 'try' operator and use
     * RouteFinder.close() in the body of 'finally' operator to be called when
     * either an exception is thrown or the current instance of RouteFinder is not
     * used any more. At the same time if Connection is not changed, it is
     * recommended (if possible) to keep (Not to release) already initialised
     * instance of RouteFinder as long as future calls of
     * RouteFinder.getRouteLineWKT are possible. PreparedStatement.close() releases
     * all PreparedStatement objects' database and JDBC resources immediately
     * instead of waiting for this to happen when it is automatically closed. It is
     * generally good practice to release resources as soon as you are finished with
     * them to avoid tying up database resources.
     * 
     * @throws SQLException
     */
    public void close() throws SQLException {

        if (listOfLastFoundAltAD_TI != null) {
            listOfLastFoundAltAD_TI.clear();
            listOfLastFoundAltAD_TI = null;
        }

        if (listOfLastFoundAltAD_RP != null) {
            listOfLastFoundAltAD_RP.clear();
            listOfLastFoundAltAD_RP = null;
        }

        if (listOfErrorMessages != null) {
            listOfErrorMessages.clear();
            listOfErrorMessages = null;
        }

        if (psBuffer != null) {
            psBuffer.close();
            psBuffer = null;
        }
        if (psDifference != null) {
            psDifference.close();
            psDifference = null;
        }

        if (dataRetriver != null) {
            dataRetriver.close();
            dataRetriver = null;
        }
        if (tokenParser != null) {
            tokenParser.close();
            tokenParser = null;
        }
    }

    /**
     * Calculates the buffer area around input shape. The source shape must be
     * provided using WGS-84 coordinate system (SRID 4326).
     * 
     * @param shapeWKT Well-Known Text (WKT) of the source shape, which must be
     *                 provided using WGS-84 coordinate system (SRID 4326).
     * @param radius   the radius in meters, which is the desired width of the
     *                 buffer area to be built around input shape.
     * @return Well-Known Text (WKT) of the output buffer area built around the
     *         input shape.
     * @throws SQLException
     */
    public String getBuffer(String shapeWKT, double radius) throws SQLException {
        if (connection == null || shapeWKT == null || shapeWKT == "" || radius > 1000000)
            return null;
        String bufferWKT = null;
        if (radius <= 0) {
            bufferWKT = shapeWKT;
            return bufferWKT;
        }
        if (psBuffer == null)
            psBuffer = connection.prepareStatement("SELECT ST_AsText(ST_Buffer(?::geography, ?))");
        psBuffer.setString(1, shapeWKT);
        psBuffer.setFloat(2, (float) (radius));
        ResultSet rs = psBuffer.executeQuery();
        if (rs.next())
            bufferWKT = rs.getString(1);
        return bufferWKT;
    }

    public List<String> splitWKTByDateLine(String shapeWKT) throws SQLException {
        return splitWKTByDateLine(shapeWKT, false);
    }

    /**
     * Generates a representative set of polylines or polygons for visualisation of
     * a route on the geographical map projection. If input route line/polygon
     * crosses Date Line it is split into pieces by Date Line. The input route
     * line/polygon crosses Date Line if there is at least one pair of adjacent
     * nodes, which are located in different hemispheres and the sum of their
     * locations' absolute longitude values exceeds 180 degrees.
     * 
     * @param shapeWKT    text representations (WKT) of the input spatial polyline
     *                    object like 'POLYLINE(...)' or polygon object like
     *                    'POLYGON((...),(...))'.
     * @param cutOffHoles works only if the 'shapeWKT' parameter gets a polygon. The
     *                    'POLYGON((...),(...))' can contain one or more inner
     *                    brackets. The first pair of inner brackets is supposed to
     *                    contain the exterior border of the polygon itself. The
     *                    pairs of inner brackets after the first one are considered
     *                    as holes within the polygon exterior border. If the input
     *                    polygon contains at least one hole, it will be processed
     *                    only if 'cutOffHoles' parameter gets 'true'; Otherwise
     *                    only the first pair of inner brackets (exterior border)
     *                    will be processed and returned.
     * @return List of text representations (WKTs) of the calculated representative
     *         set of polylines or polygons for visualisation of a route on the
     *         geographical map projection.
     * @throws SQLException
     */
    public List<String> splitWKTByDateLine(String shapeWKT, boolean cutOffHoles) throws SQLException {
        String coordinatesString;
        String coordinatePairsArray[];
        int numOfPoints;
        double longLatArray[][];
        double longitudePrev;
        double longitudeNext;
        double splitLatitude;
        LinkedList<String> listOfOutputPartsWKT;
        if (shapeWKT == null)
            return null;
        DecimalFormat decimalFormat = new DecimalFormat("0.####################");

        if (patternLINESTRING == null)
            patternLINESTRING = Pattern.compile("^\\s*LINESTRING\\s*\\((.+)\\)\\s*$");
        Matcher matcher = patternLINESTRING.matcher(shapeWKT);

        if (matcher.matches()) {
            listOfOutputPartsWKT = new LinkedList<String>();
            coordinatesString = matcher.group(1);
            coordinatePairsArray = coordinatesString.split(",");
            numOfPoints = coordinatePairsArray.length;
            longLatArray = new double[numOfPoints][2];

            if (patternLonLat == null)
                patternLonLat = Pattern.compile("^\\s*(\\S+)\\s+(\\S+)\\s*$");
            for (int i = 0; i < numOfPoints; i++) {
                matcher = patternLonLat.matcher(coordinatePairsArray[i]);
                if (matcher.matches()) {
                    longLatArray[i][0] = Double.parseDouble(matcher.group(1));
                    if (Math.abs(longLatArray[i][0]) == 180 && i > 0) {
                        if (Math.signum(longLatArray[i][0]) == -Math.signum(longLatArray[i - 1][0]))
                            longLatArray[i][0] = -longLatArray[i][0];
                    }
                    longLatArray[i][1] = Double.parseDouble(matcher.group(2));
                }
            }
            if (Math.abs(longLatArray[0][0]) == 180
                    && Math.signum(longLatArray[0][0]) == -Math.signum(longLatArray[1][0]))
                longLatArray[0][0] = -longLatArray[0][0];

            int k = 0;
            String routePartWKT = "LINESTRING(";
            for (int i = 0; i < numOfPoints - 1; i++) {
                routePartWKT = routePartWKT + decimalFormat.format(longLatArray[i][0]) + " "
                        + decimalFormat.format(longLatArray[i][1]) + ",";
//				if (Math.signum(longLatArray[i][0]) != Math.signum(longLatArray[i + 1][0])
//						&& Math.abs(longLatArray[i][0]) > 90 && Math.abs(longLatArray[i][0]) < 180
//						&& Math.abs(longLatArray[i + 1][0]) > 90 && Math.abs(longLatArray[i + 1][0]) < 180) {
                if (Math.signum(longLatArray[i][0]) == -Math.signum(longLatArray[i + 1][0])
                        && (Math.abs(longLatArray[i][0]) + Math.abs(longLatArray[i + 1][0])) > 180
                        && Math.abs(longLatArray[i][0]) < 180 && Math.abs(longLatArray[i + 1][0]) < 180) {

                    k = i + 1;
                    longitudePrev = Math.signum(longLatArray[i + 1][0]) * (180 - Math.abs(longLatArray[i][0]));
                    longitudeNext = Math.signum(longLatArray[i][0]) * (180 - Math.abs(longLatArray[i + 1][0]));
                    splitLatitude = longLatArray[i][1] - longitudePrev * (longLatArray[i + 1][1] - longLatArray[i][1])
                            / (longitudeNext - longitudePrev);
                    routePartWKT = routePartWKT + Math.signum(longLatArray[i][0]) * 180 + " "
                            + decimalFormat.format(splitLatitude) + ")";
                    listOfOutputPartsWKT.add(routePartWKT);
                    routePartWKT = "LINESTRING(" + Math.signum(longLatArray[i + 1][0]) * 180 + " "
                            + decimalFormat.format(splitLatitude) + ",";
                } else if (Math.abs(longLatArray[i][0]) == 180) {
                    if (i == 0)
                        continue;
//					if (Math.signum(longLatArray[k][0]) != Math.signum(longLatArray[i + 1][0])
//							&& Math.abs(longLatArray[i + 1][0]) > 90 && Math.abs(longLatArray[i + 1][0]) < 180) {
                    if (Math.signum(longLatArray[k][0]) == -Math.signum(longLatArray[i + 1][0])
                            && Math.abs(longLatArray[i + 1][0]) < 180) {

                        k = i + 1;
                        routePartWKT = routePartWKT + ")";
                        listOfOutputPartsWKT.add(routePartWKT);
//						routePartWKT = "LINESTRING(" + Math.signum(longLatArray[i + 1][0]) * Math.abs(longLatArray[i][0]) + " " + decimalFormat.format(longLatArray[i][1]) + ",";
                        routePartWKT = "LINESTRING(" + Math.signum(longLatArray[i + 1][0]) * 180 + " "
                                + decimalFormat.format(longLatArray[i][1]) + ",";
                    }
                }
            }
            routePartWKT = routePartWKT + decimalFormat.format(longLatArray[numOfPoints - 1][0]) + " "
                    + decimalFormat.format(longLatArray[numOfPoints - 1][1]) + ")";
            listOfOutputPartsWKT.add(routePartWKT);
            return listOfOutputPartsWKT;
        } else {
            if (patternPOLYGON == null)
                patternPOLYGON = Pattern.compile("^\\s*POLYGON\\s*\\(\\s*\\((.+?)\\),?(.*)\\)\\s*$");
            matcher = patternPOLYGON.matcher(shapeWKT);
            if (matcher.matches()) {
                listOfOutputPartsWKT = new LinkedList<String>();
                coordinatesString = matcher.group(1);
                coordinatePairsArray = coordinatesString.split(",");
                numOfPoints = coordinatePairsArray.length;
                int maxNumOfPoints = numOfPoints + 2;
//				This array will store numeric representation of the original/source polygon's border.
//				The first dimension is the border node sequential number.
//				The second dimension is to differentiate between longitudes (zeroth element) and latitude (first element).
//				The size of the first dimension is of 1 bigger than the number of coordinate pairs read from source polygon WKT.
//				this an extra element is reserved in case the border passes through a pole and an extra coordinate pair with 90 latitude is required.
                longLatArray = new double[maxNumOfPoints][2];

                if (patternLonLat == null)
                    patternLonLat = Pattern.compile("^\\s*(\\S+)\\s+(\\S+)\\s*$");
                Matcher matcher2;
                for (int i = 0, j = 0; i < numOfPoints; i++, j++) {
                    matcher2 = patternLonLat.matcher(coordinatePairsArray[j]);
                    if (matcher2.matches()) {
                        longLatArray[i][0] = Double.parseDouble(matcher2.group(1));
                        if (Math.abs(longLatArray[i][0]) == 180 && i > 0) {
//							When a polygon's border-node is located on the date line, 
//							its longitude (180 absolute value) must have the same sign as the longitude of the preceding node.
//							The rule above is required for correct visualisation of airspaces in geographical projection.
                            if (Math.signum(longLatArray[i][0]) == -Math.signum(longLatArray[i - 1][0]))
                                longLatArray[i][0] = -longLatArray[i][0];
                        }

                        longLatArray[i][1] = Double.parseDouble(matcher2.group(2));

//						When the polygon's border passes through a pole, it must have two sequential nodes with the absolute latitude value of 90 degrees.
//						The first of those two nodes must have longitude equal to the longitude of the preceding node.
//						The second of those two nodes must have longitude equal to the longitude of the subsequent node.
//						The rule above is required for correct visualisation of airspaces in geographical projection.
                        if (Math.abs(longLatArray[i][1]) == 90 && i > 0) {
                            if (Math.abs(longLatArray[i - 1][1]) != 90) {
                                if (longLatArray[i][0] != longLatArray[i - 1][0]) {
                                    if (i == numOfPoints - 1) {
                                        if (numOfPoints < maxNumOfPoints) {
                                            longLatArray[i + 1][0] = longLatArray[i][0];
                                            longLatArray[i + 1][1] = longLatArray[i][1];

                                            longLatArray[i][0] = longLatArray[i - 1][0];
                                            numOfPoints++;
                                            i++;
                                        }
                                    } else
                                        longLatArray[i][0] = longLatArray[i - 1][0];
                                }
                            }
                        } else if (i > 1 && Math.abs(longLatArray[i - 1][1]) == 90 && numOfPoints < maxNumOfPoints) {
                            if (Math.abs(longLatArray[i - 2][1]) != 90) {
                                longLatArray[i + 1][0] = longLatArray[i][0];
                                longLatArray[i + 1][1] = longLatArray[i][1];

                                longLatArray[i][1] = longLatArray[i - 1][1];
                                numOfPoints++;
                                i++;
                            }
                        }
                    }
                }
                if (Math.abs(longLatArray[0][0]) == 180
                        && Math.signum(longLatArray[0][0]) == -Math.signum(longLatArray[1][0]))
                    longLatArray[0][0] = -longLatArray[0][0];

                if (Math.abs(longLatArray[0][1]) == 90) {
                    if (longLatArray[0][0] != longLatArray[1][0] && numOfPoints < maxNumOfPoints) {
                        longLatArray[0][0] = longLatArray[1][0];
                        longLatArray[numOfPoints][0] = longLatArray[0][0];
                        longLatArray[numOfPoints][1] = longLatArray[0][1];
                        numOfPoints++;
                    }
                }

                int k = 0;
                boolean firstPolygon = true;
                String polygonPartWKT = "POLYGON((";
                String firstCoordPair = "";
                for (int i = 0; i < numOfPoints - 1; i++) {
                    polygonPartWKT = polygonPartWKT + decimalFormat.format(longLatArray[i][0]) + " "
                            + decimalFormat.format(longLatArray[i][1]) + ",";
//					if (Math.signum(longLatArray[i][0]) != Math.signum(longLatArray[i + 1][0])
//							&& Math.abs(longLatArray[i][0]) > 90 && Math.abs(longLatArray[i][0]) < 180
//							&& Math.abs(longLatArray[i + 1][0]) > 90 && Math.abs(longLatArray[i + 1][0]) < 180) {
                    if (Math.signum(longLatArray[i][0]) == -Math.signum(longLatArray[i + 1][0])
                            && (Math.abs(longLatArray[i][0]) + Math.abs(longLatArray[i + 1][0])) > 180
                            && Math.abs(longLatArray[i][0]) < 180 && Math.abs(longLatArray[i + 1][0]) < 180) {

                        k = i + 1;
                        longitudePrev = Math.signum(longLatArray[i + 1][0]) * (180 - Math.abs(longLatArray[i][0]));
                        longitudeNext = Math.signum(longLatArray[i][0]) * (180 - Math.abs(longLatArray[i + 1][0]));
                        splitLatitude = longLatArray[i][1] - longitudePrev
                                * (longLatArray[i + 1][1] - longLatArray[i][1]) / (longitudeNext - longitudePrev);
                        if (firstPolygon) {
                            polygonPartWKT = polygonPartWKT + Math.signum(longLatArray[i][0]) * 180 + " "
                                    + decimalFormat.format(splitLatitude) + ",";
                            if (listOfOutputPartsWKT.size() == 0)
                                listOfOutputPartsWKT.add(polygonPartWKT);
                            else
                                listOfOutputPartsWKT.set(0, polygonPartWKT);
                            firstCoordPair = Math.signum(longLatArray[i + 1][0]) * 180 + " "
                                    + decimalFormat.format(splitLatitude);
                            polygonPartWKT = "POLYGON((" + firstCoordPair + ",";
                            firstPolygon = false;
                        } else {
                            polygonPartWKT = polygonPartWKT + Math.signum(longLatArray[i][0]) * 180 + " "
                                    + decimalFormat.format(splitLatitude) + "," + firstCoordPair + "))";
                            listOfOutputPartsWKT.add(polygonPartWKT);
                            polygonPartWKT = listOfOutputPartsWKT.getFirst();
                            polygonPartWKT = polygonPartWKT + Math.signum(longLatArray[i + 1][0]) * 180 + " "
                                    + decimalFormat.format(splitLatitude) + ",";
                            firstPolygon = true;
                        }
                    } else if (Math.abs(longLatArray[i][0]) == 180) {
                        if (i == 0)
                            continue;
//						if (Math.signum(longLatArray[k][0]) != Math.signum(longLatArray[i + 1][0])
//								&& Math.abs(longLatArray[i + 1][0]) > 90 && Math.abs(longLatArray[i + 1][0]) < 180) {
                        if (Math.signum(longLatArray[k][0]) == -Math.signum(longLatArray[i + 1][0])
                                && Math.abs(longLatArray[i + 1][0]) < 180) {

                            k = i + 1;
                            if (firstPolygon) {
//								polygonPartWKT = polygonPartWKT + ",";
                                if (listOfOutputPartsWKT.size() == 0)
                                    listOfOutputPartsWKT.add(polygonPartWKT);
                                else
                                    listOfOutputPartsWKT.set(0, polygonPartWKT);
                                firstCoordPair = Math.signum(longLatArray[i + 1][0]) * 180 + " "
                                        + decimalFormat.format(longLatArray[i][1]);
                                polygonPartWKT = "POLYGON((" + firstCoordPair + ",";
                                firstPolygon = false;
                            } else {
                                polygonPartWKT = polygonPartWKT + firstCoordPair + "))";
                                listOfOutputPartsWKT.add(polygonPartWKT);
                                polygonPartWKT = listOfOutputPartsWKT.getFirst();
                                polygonPartWKT = polygonPartWKT + Math.signum(longLatArray[i + 1][0]) * 180 + " "
                                        + decimalFormat.format(longLatArray[i][1]) + ",";
                                firstPolygon = true;
                            }
                        }
                    }
                }

                polygonPartWKT = polygonPartWKT + decimalFormat.format(longLatArray[numOfPoints - 1][0]) + " "
                        + decimalFormat.format(longLatArray[numOfPoints - 1][1]) + "))";
                if (listOfOutputPartsWKT.size() == 0)
                    listOfOutputPartsWKT.add(polygonPartWKT);
                else
                    listOfOutputPartsWKT.set(0, polygonPartWKT);

                if (cutOffHoles && matcher.group(2).trim().length() > 0) {
                    /*
                     * if (listOfOutputPartsWKT.size() == 1) { listOfOutputPartsWKT.set(0,
                     * shapeWKT); return listOfOutputPartsWKT; }
                     */
                    if (connection == null) {
                        listOfErrorMessages.add(
                                "The connection with the database is not set. The result returned by 'splitWKTByDateLine' may not be correct.");
                        logger.debug(
                                "The connection with the database is not set. The result returned by 'splitWKTByDateLine' may not be correct.");
                        return listOfOutputPartsWKT;
                    }

                    String holeWKTs = matcher.group(2).trim();
                    LinkedList<String> listOfHoleWKT = new LinkedList<String>();

                    if (patternPolygonParts == null)
                        patternPolygonParts = Pattern.compile("^\\((.+?)\\),?(.*)$");
                    matcher2 = patternPolygonParts.matcher(holeWKTs);

                    while (matcher2.matches()) {
                        listOfHoleWKT.addAll(splitWKTByDateLine("POLYGON((" + matcher2.group(1) + "))", false));
                        holeWKTs = matcher2.group(2).trim();
                        if (holeWKTs.length() > 0)
                            matcher2 = patternPolygonParts.matcher(holeWKTs);
                        else
                            break;
                    }

                    if (psDifference == null)
                        psDifference = connection.prepareStatement(
                                "SELECT ST_AsText(ST_Difference(?::geometry, ?::geometry)::geography)");

                    ResultSet rs;
                    int i = 0;
                    for (String outputPart : listOfOutputPartsWKT) {
                        for (String holePart : listOfHoleWKT) {
                            psDifference.setString(1, outputPart);
                            psDifference.setString(2, holePart);
                            rs = psDifference.executeQuery();
                            if (rs.next()) {
                                outputPart = rs.getString(1);
                                listOfOutputPartsWKT.set(i, outputPart);
                            } else {
                                listOfErrorMessages.add(
                                        "SQL query using 'ST_Difference' did not return any result. The result returned by 'splitWKTByDateLine' may not be correct.");
                                logger.debug(
                                        "SQL query using 'ST_Difference' did not return any result. The result returned by 'splitWKTByDateLine' may not be correct.");
//								return listOfOutputPartsWKT;
                            }
                        }
                        i = i + 1;
                    }
                    return listOfOutputPartsWKT;
                }
                return listOfOutputPartsWKT;
            }
        }
        return null;
    }

    /**
     * Generates a representative set of polylines and polygons for visualisation of
     * a route line on a geographical map. First if input route line crosses Date
     * Line it is split into pieces by Date Line. For each piece of input route line
     * a Buffer polygon is then created in the way that it does not intersect Date
     * Line (or is limited by Date Line). The input route line crosses Date Line if
     * there is at least one pair of adjacent route nodes, which are located in
     * different hemispheres and the sum of their locations' absolute longitude
     * values exceeds 180 degrees.
     * 
     * @param routeLineWKT text representations (WKT) of the input spatial polyline
     *                     object like 'POLYLINE(...)'.
     * @param radius       the radius in meters for the buffer to be constructed
     *                     around input polyline object.
     * @return the first dimension of the output two-dimensional array represents
     *         the number of pieces the input route line was split into by Date
     *         Line. The second dimension of the output array is always equal to 2;
     *         In which zeroth elements contain pieces of input polyline-WKT, and
     *         the first elements contain polygon-WKTs of buffers constructed for
     *         the respective polyline-WKTs.
     * @throws SQLException
     */
    public String[][] getLinePolygonPairsForMap(String routeLineWKT, double radius) throws SQLException {
        if (routeLineWKT == null)
            return null;

        List<String> routeLineParts = splitWKTByDateLine(routeLineWKT);
        List<String> routeBufferParts;
        String routeBufferWKT;
        String outputArray[][] = new String[routeLineParts.size()][2];

        Comparator<String> longestFirst = new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                if (a.length() > b.length())
                    return -1;
                else if (a.length() == b.length())
                    return 0;
                else
                    return 1;
            }
        };

        int i = 0;
        for (String routePartWKT : routeLineParts) {
            routeBufferWKT = getBuffer(routePartWKT, radius);
            if (routeBufferWKT == null)
                return null;
            routeBufferParts = splitWKTByDateLine(routeBufferWKT, true);
            Collections.sort(routeBufferParts, longestFirst);
            outputArray[i][0] = routePartWKT;
            outputArray[i][1] = routeBufferParts.get(0);
            i = i + 1;
        }

        return outputArray;
    }

    /**
     * Parses text representations (WKT) of the input spatial polygon object like
     * 'POLYGON((...),(...))', in which each inner pair of brackets represents a
     * polygon curve. Spatial representation (sequence of node coordinates) of each
     * polygon curve is extracted and then stored into output list of strings as a
     * separate element.
     * 
     * @param polygonWKT text representations (WKT) of the input spatial polygon
     *                   object.
     * @return List of String objects containing spatial representation (sequence of
     *         node coordinates) of input polygon curves. If the format of input
     *         String object does not match expected WKT for polygon, the returned
     *         List will not contain any elements.
     */
    public List<String> unpackPolygonPartsFromWKT(String polygonWKT) {
        LinkedList<String> outputList = new LinkedList<String>();
        if (patternPOLYGON == null)
            patternPOLYGON = Pattern.compile("^\\s*POLYGON\\s*\\(\\s*\\((.+?)\\),?(.*)\\)\\s*$");

        Matcher matcher = patternPOLYGON.matcher(polygonWKT);
        if (matcher.matches()) {
            outputList.add(matcher.group(1).trim());
            String holeWKTs = matcher.group(2).trim();
            if (holeWKTs.length() > 0) {
                if (patternPolygonParts == null)
                    patternPolygonParts = Pattern.compile("^\\((.+?)\\),?(.*)$");
                matcher = patternPolygonParts.matcher(holeWKTs);

                while (matcher.matches()) {
                    outputList.add(matcher.group(1).trim());
                    holeWKTs = matcher.group(2).trim();
                    if (holeWKTs.length() > 0)
                        matcher = patternPolygonParts.matcher(holeWKTs);
                    else
                        break;
                }
            }
        }
        return outputList;
    }

    /**
     * Returns a properly ordered list of all route points constituting the route
     * path (geometry), which was generated at the last call of getRouteLineWKT().
     * 
     * @return ArrayList of RoutePoint class instances, generated based on the last
     *         call of getRouteLineWKT(). Each RoutePoint element of the returned
     *         list stores corresponding route point attributes. RoutePoint elements
     *         in the output list can represent fixes found by two different ways.
     *         First of all there are fixes, which have been parsed (recognized as
     *         route tokens) in the input route string. Such fixes can be of the
     *         following different types: a) designated point, b) navaid, c) pair of
     *         geographical coordinates, d) point calculated based bearing and
     *         distance values from a navaid. Secondary, if at least one ATS route
     *         token (surrounded by designated point and/or navaid tokens) has been
     *         recognized and found in the database, there can be additional fixes
     *         (and respective RoutePoint elements) located in-between of the fixes
     *         surrounding the ATS route token. Such fixes can be of only two
     *         (designated point and navaid) types. To extract attributes to be
     *         displayed on the map, see 'get...' methods of RoutePoint class. Is
     *         supposed to be called after a respective call of getRouteLineWKT()
     *         function.
     */
    public ArrayList<RoutePoint> getLastFoundRouteStructure() {
        return lastFoundRouteStructure;
    }

    public LinkedList<RoutePortion> getListOfRoutePortions(String defaultCruisingLevel) {
        if (listOfRoutePortionsByFlightLevel == null || listOfRoutePortionsByFlightLevel.isEmpty()) {
            return null;
        } else {
            listOfRoutePortionsByFlightLevel.getFirst().cruisingLevel = defaultCruisingLevel;
            return listOfRoutePortionsByFlightLevel;
        }
    }

    public RoutePoint getStartPoint() {
        if (lastFoundRouteStructure == null || lastFoundRouteStructure.isEmpty())
            return null;
        return lastFoundRouteStructure.get(0);
    }

    public RoutePoint getEndPoint() {
        if (lastFoundRouteStructure == null || lastFoundRouteStructure.isEmpty())
            return null;
        return lastFoundRouteStructure.get(lastFoundRouteStructure.size() - 1);
    }

    /**
     * Returns coded interpretation of the route, generated at the last call of
     * getRouteLineWKT().
     * 
     * @return ArrayList generated at the last call of getRouteLineWKT(). Each
     *         element of the returned list (TokenInterpretation instance)
     *         represents a coded interpretation of the corresponding route token
     *         from the input full route string (sequence of tokens from departure
     *         aerodrome to arrival aerodrome inclusively). See methods of
     *         TokenInterpretation class, which return valuable information.
     */
    ArrayList<TokenInterpretation> getLastFoundRouteInterpretation() {
        if (lastFoundRouteScenario == null)
            return null;
        return lastFoundRouteScenario.getRouteInterpretation();
    }

    /**
     * Returns List of TokenInterpretation class instances, for alternative
     * aerodrome designators input in the last call of getRouteLineWKT().
     * 
     * @return List of TokenInterpretation class instances, for alternative
     *         aerodrome designators input in the last call of getRouteLineWKT(). If
     *         an only arrival/destination aerodrome was transferred at the last
     *         call of getRouteLineWKT(), getLastFoundAltAD_WKT() will return null
     *         (not initialised List). Otherwise the size of the returned
     *         List<TokenInterpretation> is always equal to the number of
     *         alternative aerodromes (number of all destination aerodromes minus
     *         1). If one of the alternative aerodromes is not found in the
     *         database, then the following functions of the respective
     *         TokenInterpretation instance will return Null: getTimeSliceRecID(),
     *         getRoutePointWKT() , getName(), getAIXMClassName(); whereas
     *         isValidRoutePoint() will return false.
     */
    List<TokenInterpretation> getLastFoundAltAD() {
        return listOfLastFoundAltAD_TI;
    }

    /**
     * Returns List of RoutePoint class instances, for alternative aerodrome
     * designators input in the last call of getRouteLineWKT().
     * 
     * @return List of RoutePoint class instances, for alternative aerodrome
     *         designators input in the last call of getRouteLineWKT(). If an only
     *         arrival/destination aerodrome was transferred at the last call of
     *         getRouteLineWKT(), getLastFoundAltAD_WKT() will return null.
     *         Otherwise the size of the returned List<RoutePoint> is equal to the
     *         number of alternative aerodromes found in the database (as maximum
     *         the number of all destination aerodromes minus 1). If one of the
     *         alternative aerodromes is not found in the database, it will not be
     *         added to the output List<RoutePoint>.
     */
    List<RoutePoint> getLastFoundAltADPoints() {
        return listOfLastFoundAltAD_RP;
    }

    /**
     * Parses full route string, searches respective features in the database and
     * reproduces the route geographic path based on the found point nodes and
     * recognized en-route portions.
     * 
     * @param maxRouteLengthRatio        the maximum ratio, which is the relation of
     *                                   total length of all route constituting
     *                                   segments to direct distance between
     *                                   departure and arrival aerodromes. If
     *                                   maxRouteLengthRatio is set to a smaller
     *                                   value, some well curved routes (with the
     *                                   higher ratio) will miss a number of
     *                                   intermediate points to match ratio set in
     *                                   this parameter.
     * @param completeSearch             if 'true' makes route recognition/search
     *                                   algorithm to generate extra route
     *                                   scenarios, in which the current token
     *                                   recognized as Point, is additionally
     *                                   interpreted as SIGNIFICANT_POINT_NOTFOUND
     *                                   (even if that Point has related feature(s)
     *                                   matching by designator in the database and
     *                                   do not lead to exceeding of maximum
     *                                   'maxRouteLengthRatio' parameter at the
     *                                   current step). This option makes
     *                                   recognition/search process slower, but
     *                                   guarantees complete avoidance of
     *                                   wrong/incorrect features matching by
     *                                   designator, which is potentially able to
     *                                   direct the algorithm in wrong way/path.
     * @param localIngestIncorrectFormat If 'false' the first token with
     *                                   incorrect/unrecognized format, causes
     *                                   function to return NULL. If 'true' causes
     *                                   function not to stop parsing/interpretation
     *                                   process, but just to ignore a token with
     *                                   incorrect/unrecognized format. TRUE by
     *                                   default.
     * @return List of route geographical paths for the respective primary and
     *         alternative aerodromes in well-known text (WKT) format. Zero's item
     *         of the list always contains the geographical path to the main
     *         destination aerodrome. If alternative aerodrome(s) were input, the
     *         respective 2nd and others output route geographical paths will differ
     *         only by last pair of coordinates.
     * @throws SQLException
     */
    public LinkedList<String> getRouteLineWKT() throws SQLException {
        boolean circularRoute = false;
        boolean completeSearch = false;
        TokenPrimaryType startPointType = null;
        TokenPrimaryType endPointType = null;
        listOfRouteWKTs = null;
        lastFoundRouteScenario = null;
        lastFoundRouteDraft = null;
        lastFoundRouteStructure = null;
        lastNotFoundADHP = null;
        listOfRoutePortionsByFlightLevel = null;
        tokenArray = null;
        tokenList = null;
        tokenListSize = 0;

        ArrayList<TokenInterpretation> listOfTokenInterpretations;

        if (listOfLastFoundAltAD_TI != null) {
            listOfLastFoundAltAD_TI.clear();
        }

        if (listOfLastFoundAltAD_RP != null) {
            listOfLastFoundAltAD_RP.clear();
        }

        if (listOfErrorMessages != null) {
            listOfErrorMessages.clear();
        }

//		if (listOfErrorMessages == null) listOfErrorMessages = new LinkedList<String>();

        if (fullRoute == null || (strOriginToken == null && originTokenInterpretation == null)
                || ((strDestinationToken == null || strDestinationToken.length == 0)
                        && destinTokenInterpretation == null)
                || connection == null || timeStamp == null) {
//			An Exception must be thrown!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            listOfErrorMessages.add("Null value is found among parameters used by 'getRouteLineWKT' function.");
            logger.debug("Null value is found among parameters used by 'getRouteLineWKT' function.");
            return null; // This line must be deleted when the exception mechanism is specified
        }

        fullRoute = fullRoute.trim();
        int tokenLength;

        if (tokenParser == null)
            tokenParser = new TokenParser(connection, timeStamp);

        if (strOriginToken == null) {
            strOriginToken = originTokenInterpretation.tokenValue.trim();
            originRoutePoint = new RoutePoint(originTokenInterpretation, timeStamp);

            startPointType = originTokenInterpretation.tokenPrimaryType;
            originTokenInterpretation.tokenPrimaryType = TokenPrimaryType.ADHP;
        }

        else {
            strOriginToken = strOriginToken.trim();
            tokenLength = strOriginToken.length();

            if (tokenLength == 4) {
                listOfTokenInterpretations = tokenParser.tryInterpretADHP(strOriginToken);
            } else {
                if (tokenLength == 7) {
                    listOfTokenInterpretations = tokenParser.tryInterpretDegreesOnlyPoint(strOriginToken);
                } else if (tokenLength == 11) {
                    listOfTokenInterpretations = tokenParser.tryInterpretDegreesMinutesPoint(strOriginToken);
                } else if (tokenLength == 15) {
                    listOfTokenInterpretations = tokenParser.tryInterpretDegreesMinutesSecondsPoint(strOriginToken);
                } else {
                    listOfErrorMessages
                            .add("Incorrect format of '" + strOriginToken + "' for departure aerodrome/coordinates.");
                    logger.debug("Incorrect format of '" + strOriginToken + "' for departure aerodrome/coordinates.");
                    return null;
                }
            }

            if (listOfTokenInterpretations.isEmpty()) {
                listOfErrorMessages.add("'" + strOriginToken + "' token has invalid format for route departure point.");
                logger.debug("'" + strOriginToken + "' token has invalid format for route departure point.");
                return null;
            } else {
                originTokenInterpretation = listOfTokenInterpretations.get(0);
                originTokenInterpretation.tokenValue = strOriginToken;
                if (originTokenInterpretation.tokenPrimaryType == TokenPrimaryType.ADHP_NOTFOUND) {
                    lastNotFoundADHP = originTokenInterpretation;
                    listOfErrorMessages.add("Departure AirportHeliport feature with '" + strOriginToken
                            + "' designator is not found in the database.");
                    logger.debug("Departure AirportHeliport feature with '" + strOriginToken
                            + "' designator is not found in the database.");
                    return null;
                }

                if (originTokenInterpretation.tokenPrimaryType != TokenPrimaryType.ADHP) {
                    originTokenInterpretation.tokenPrimaryType = TokenPrimaryType.ADHP;
                    originTokenInterpretation.name = strOriginToken;
                }
            }
            listOfTokenInterpretations.clear();

            originRoutePoint = new RoutePoint(originTokenInterpretation, timeStamp);
        }

        if (originRoutePoint.getLongitude() == null || originRoutePoint.getLatitude() == null) {
            listOfErrorMessages
                    .add("Was not able to retrieve latitude and/or longitude of departure AirportHeliport feature.");
            logger.debug("Was not able to retrieve latitude and/or longitude of departure AirportHeliport feature.");
            return null;
        }

        TokenInterpretation tokenInterpretation;

        if (strDestinationToken == null || strDestinationToken.length == 0) {
            strDestinationToken = new String[1];
            strDestinationToken[0] = destinTokenInterpretation.getTokenValue().trim();

            destinationRoutePoints = new RoutePoint[1];
            destinationRoutePoints[0] = new RoutePoint(destinTokenInterpretation, timeStamp);

            endPointType = destinTokenInterpretation.tokenPrimaryType;
            destinTokenInterpretation.tokenPrimaryType = TokenPrimaryType.ADHP;
        } else {
            destinationRoutePoints = new RoutePoint[strDestinationToken.length];

            if (strDestinationToken.length > 1) {
                listOfLastFoundAltAD_RP = new LinkedList<RoutePoint>();
                listOfLastFoundAltAD_TI = new LinkedList<TokenInterpretation>();
            }

            for (int k = 0; k < strDestinationToken.length; k++) {
                strDestinationToken[k] = strDestinationToken[k].trim();
                tokenLength = strDestinationToken[k].length();

                if (tokenLength == 4) {
                    listOfTokenInterpretations = tokenParser.tryInterpretADHP(strDestinationToken[k]);
                } else {
                    if (tokenLength == 7) {
                        listOfTokenInterpretations = tokenParser.tryInterpretDegreesOnlyPoint(strDestinationToken[k]);
                    } else if (tokenLength == 11) {
                        listOfTokenInterpretations = tokenParser
                                .tryInterpretDegreesMinutesPoint(strDestinationToken[k]);
                    } else if (tokenLength == 15) {
                        listOfTokenInterpretations = tokenParser
                                .tryInterpretDegreesMinutesSecondsPoint(strDestinationToken[k]);
                    } else {
                        listOfErrorMessages.add("Incorrect format of '" + strDestinationToken[k]
                                + "' for destination aerodrome/coordinates.");
                        logger.debug("Incorrect format of '" + strDestinationToken[k]
                                + "' for destination aerodrome/coordinates.");
                        if (k == 0)
                            return null;
                        else
                            continue;
                    }
                }

                if (listOfTokenInterpretations.isEmpty()) {
                    listOfErrorMessages.add(
                            "'" + strDestinationToken[k] + "' token has invalid format for route destination point.");
                    logger.debug(
                            "'" + strDestinationToken[k] + "' token has invalid format for route destination point.");
                    if (k == 0)
                        return null;
                    else
                        continue;
                } else {
                    tokenInterpretation = listOfTokenInterpretations.get(0);
                    tokenInterpretation.tokenValue = strDestinationToken[k];

                    if (tokenInterpretation.tokenPrimaryType == TokenPrimaryType.ADHP_NOTFOUND) {
                        lastNotFoundADHP = tokenInterpretation;
                        listOfErrorMessages.add("Destination AirportHeliport feature with '" + strDestinationToken[k]
                                + "' designator is not found in the database.");
                        logger.debug("Destination AirportHeliport feature with '" + strDestinationToken[k]
                                + "' designator is not found in the database.");
                        if (k == 0)
                            return null;
                        else
                            continue;
                    } else if (tokenInterpretation.getLongitude() == null
                            || tokenInterpretation.getLatitude() == null) {
                        listOfErrorMessages.add("Was not able to retrieve latitude and/or longitude of '"
                                + strDestinationToken[k] + "' destination AirportHeliport feature.");
                        logger.debug("Was not able to retrieve latitude and/or longitude of '" + strDestinationToken[k]
                                + "' destination AirportHeliport feature.");
                        // the function continues only if both departure and main destination aerodromes
                        // are retrieved with coordinates of their locations
                        if (k == 0)
                            return null;
                        else
                            continue;
                    }

                    if (tokenInterpretation.tokenPrimaryType != TokenPrimaryType.ADHP) {
                        tokenInterpretation.tokenPrimaryType = TokenPrimaryType.ADHP;
                        tokenInterpretation.name = strDestinationToken[k];
                    }
                    destinationRoutePoints[k] = new RoutePoint(tokenInterpretation, timeStamp);
                }

                if (k == 0)
                    destinTokenInterpretation = tokenInterpretation;
                else {
                    listOfLastFoundAltAD_RP.add(destinationRoutePoints[k]);
                    listOfLastFoundAltAD_TI.add(tokenInterpretation);
                }

                listOfTokenInterpretations.clear();
                tokenInterpretation = null;
            }
        }

        if (strOriginToken != null && strDestinationToken != null && strDestinationToken.length > 0
                && strOriginToken.equalsIgnoreCase(strDestinationToken[0])
                || originRoutePoint.isAtSameLocation(destinationRoutePoints[0])) {

            circularRoute = true;
            destinTokenInterpretation = originTokenInterpretation.getClone();
            destinationRoutePoints[0] = originRoutePoint;
//			For the ring-type routes (when ICAO-codes of departure and arrival aerodromes are the same), 
//			we need to avoid rejecting those route scenarios, whose ratio (relation of total length of all segments to direct distance between departure and arrival aerodromes) exceed the 'localMaxRatioForRteLength' value (see maxRouteLengthRatio parameter of getRouteLineWKT),
//			as the direct distance between departure and arrival aerodromes is equal to zero for routes starting and ending at the same aerodrome.
//			To avoid rejecting such route scenarios, we change localMaxRatioForRteLength to an invalid (-1) value, which later serves as a mark of a ring-type route.
            localMaxRatioForRteLength = -1;
        }

//		fullRoute string is split into String-array of tokens 'tokenArray' and then saved into List<String> 'tokenList'
        tokenArray = fullRoute.split("\\s+");
        tokenList = new LinkedList<String>(Arrays.asList(tokenArray));
        tokenListSize = tokenList.size();
        if (tokenListSize == 0) {
            listOfErrorMessages.add("No tokens have been recognized in the route string.");
            logger.debug("No tokens have been recognized in the route string.");
            return null;
        }

//		The first token of the fullRoute string (in 'tokenList') is compared to the originAD parameter and is removed if equal to departure Aerodrome 
        while (tokenListSize > 0 && tokenList.get(0).equals(strOriginToken)) {
            tokenList.remove(0);
            tokenListSize = tokenList.size();
        }
        if (tokenListSize == 0) {
            listOfErrorMessages.add(
                    "No tokens except for departure aerodrome designator/coordinates were recognized in the route string.");
            logger.debug(
                    "No tokens except for departure aerodrome designator/coordinates were recognized in the route string.");
            if (circularRoute)
                return null;
        } else {
//			The last token of the fullRoute string (in 'tokenList') is compared to the each element of 'destinationAD' array-parameter and is removed if equal to arrival Aerodrome 
            while (tokenListSize > 0 && tokenList.get(tokenListSize - 1).equals(strDestinationToken[0])) {
                tokenList.remove(tokenListSize - 1);
                tokenListSize = tokenList.size();
            }
            if (tokenListSize == 0) {
                listOfErrorMessages.add(
                        "No tokens except for departure/destination aerodrome designator/coordinates were recognized in the route string.");
                logger.debug(
                        "No tokens except for departure/destination aerodrome designator/coordinates were recognized in the route string.");
                if (circularRoute)
                    return null;
            }
        }

//		PreparedStatement 'psDistance' to calculate distance between two geographical points in WKT format is initialised
//		if (psDistance == null) psDistance = connection.prepareStatement("SELECT ST_Distance(?::geography, ?::geography)");
//		if (psAzimuth == null) psAzimuth = connection.prepareStatement("SELECT ST_Azimuth(?::geography, ?::geography)");

//		The root 'baseRouteScenario' is initialised by TokenInterpretation instances of departure and arrival aerodromes
//		later the root 'baseRouteScenario' will iteratively evolve into a series of more complex instances of RouteScenario class
        RouteScenario baseRouteScenario = new RouteScenario(connection, tokenListSize + 2); // The number of tokens in
                                                                                            // the route plus 2
                                                                                            // (departure and arrival
                                                                                            // aerodromes)
        baseRouteScenario.addOrigin(originTokenInterpretation);
        baseRouteScenario.addDestination(destinTokenInterpretation);
//		LinkedList<RouteScenario> 'listOfRouteScenarios' is initialised by the first simplest (root) instance of RouteScenario class
        LinkedList<RouteScenario> listOfRouteScenarios = new LinkedList<RouteScenario>();
        listOfRouteScenarios.add(baseRouteScenario);

//		ArrayList<MapOfTokenInterpretations> 'spaceOfTokenInterpretations' is a structure for storing all possible interpretations for all route tokens
//		for each token there is an instance of MapOfTokenInterpretations, which stores mapping references onto typical alternative TokenInterpretation-s of the same token 
        spaceOfTokenInterpretations = new ArrayList<MapOfTokenInterpretations>(50);
//		ArrayList<TokenInterpretation> listOfTokenInterpretations;

        logger.debug("#) DES.- NUM OF INTERP.- LIST OF FOUND LOCATIONS");
        tokenInterpretation = null;
        String listOfWKT;
        int numOfParsedAndFoundPointsInRoute = 0;

//		The elements of List<String> tokenList are transfered to 'tokenParser.getTokenInterpretations' function in the following loop (below)
//		to get the list of all possible instances of TokenInterpretation: One list of TokenInterpretation instances for each token of the route at one iteration.
//		The retrieved 'listOfTokenInterpretations' list is used to initialise MapOfTokenInterpretations 'mapOfTokenInterpretations',
//		which is then added to 'spaceOfTokenInterpretations'.
//		Calls of 'getTokenInterpretations' vary in iterations by 'interpretationMode' parameter,
//		which helps to avoid surely wrong token interpretations based on interpretation of previous token and known semantic rules.
        for (int i = 0; i < tokenListSize; i++) {
            if (i == 0) {
                if (originTokenInterpretation.tokenPrimaryType == TokenPrimaryType.ADHP) {
//					TokenInterpretationMode.FIRST is used for searching only those interpretations of a token, which can follow after the departure aerodrome
                    listOfTokenInterpretations = tokenParser.getTokenInterpretations(tokenList.get(i), timeStamp,
                            TokenInterpretationMode.FIRST, true, originRoutePoint, SID_STAR_PNT_TOLERANCE_IN_METERS);
                    if (listOfTokenInterpretations.isEmpty()) {
                        listOfErrorMessages.add("The format of '" + tokenList.get(i)
                                + "' token is not recognized in the position right after departure aerodrome.");
                        logger.debug("The format of '" + tokenList.get(i)
                                + "' token is not recognized in the position right after departure aerodrome.");
//						'getTokenInterpretations' returns empty list if the format of the token string does not match any known/allowed format.
//						If the token format is not recognized and ingestIncorrectFormat == false, a typing error is recognized, which must be corrected first.
//						So, there is no reason to continue parsing the route string and null is returned as a sign of mistype.
                        if (!localIngestIncorrectFormat)
                            return null;
                    }
                } else {
                    listOfTokenInterpretations = tokenParser.getTokenInterpretations(tokenList.get(i), timeStamp,
                            TokenInterpretationMode.POINT, true);
                    if (listOfTokenInterpretations.isEmpty()) {
                        listOfErrorMessages
                                .add("The format of '" + tokenList.get(i) + "' token is not recognized as a Point.");
                        logger.debug("The format of '" + tokenList.get(i) + "' token is not recognized as a Point.");
                        if (!localIngestIncorrectFormat)
                            return null;
                    }
                }
            } else if (i == tokenListSize - 1) {
                if (destinTokenInterpretation.tokenPrimaryType == TokenPrimaryType.ADHP) {
//					TokenInterpretationMode.LAST is used for searching only those interpretations of a token, which can precede the arrival aerodrome
                    listOfTokenInterpretations = tokenParser.getTokenInterpretations(tokenList.get(i), timeStamp,
                            TokenInterpretationMode.LAST, true, destinationRoutePoints[0],
                            SID_STAR_PNT_TOLERANCE_IN_METERS);
                    if (listOfTokenInterpretations.isEmpty()) {
                        listOfErrorMessages.add("The format of '" + tokenList.get(i)
                                + "' token is not recognized in the position right before arrival aerodrome.");
                        logger.debug("The format of '" + tokenList.get(i)
                                + "' token is not recognized in the position right before arrival aerodrome.");
//						If the token format is not recognized and ingestIncorrectFormat == false, it is understood as typing error, which must be corrected first.
//						So, there is no reason to continue parsing the route string and null is returned as a sign of mistype.
                        if (!localIngestIncorrectFormat)
                            return null;
                    }
                } else {
                    listOfTokenInterpretations = tokenParser.getTokenInterpretations(tokenList.get(i), timeStamp,
                            TokenInterpretationMode.POINT, true);
                    if (listOfTokenInterpretations.isEmpty()) {
                        listOfErrorMessages
                                .add("The format of '" + tokenList.get(i) + "' token is not recognized as a Point.");
                        logger.debug("The format of '" + tokenList.get(i) + "' token is not recognized as a Point.");
                        if (!localIngestIncorrectFormat)
                            return null;
                    }
                }
            } else {
                ArrayList<TokenInterpretation> prevListOfTokenInterpretations = spaceOfTokenInterpretations
                        .get(i - 1).listOfTokenInterpretation;
                tokenInterpretation = prevListOfTokenInterpretations.get(0);
                if (prevListOfTokenInterpretations.size() == 1
                        && tokenInterpretation.tokenPrimaryType == TokenPrimaryType.ATS_ROUTE) {
//					TokenInterpretationMode.POINT is used for searching only POINT interpretations of a token, which can follow after ATS route
                    listOfTokenInterpretations = tokenParser.getTokenInterpretations(tokenList.get(i), timeStamp,
                            TokenInterpretationMode.POINT, true);
                    if (listOfTokenInterpretations.isEmpty()) {
                        listOfErrorMessages.add("The format of '" + tokenList.get(i)
                                + "' token is not recognized as a Point following after ATS_ROUTE.");
                        logger.debug("The format of '" + tokenList.get(i)
                                + "' token is not recognized as a Point following after ATS_ROUTE.");
//						If the token format is not recognized and ingestIncorrectFormat == false, it is understood as typing error, which must be corrected first.
//						So, there is no reason to continue parsing the route string and null is returned as a sign of mistype.
                        if (!localIngestIncorrectFormat)
                            return null;
                    }
                } else if (tokenInterpretation.tokenPrimaryType == TokenPrimaryType.KEY_WORD
                        && tokenInterpretation.keyWord == KeyWord.DCT) {
//					TokenInterpretationMode.POINT is used for searching only POINT interpretations of a token, which can follow after DCT keyword
                    listOfTokenInterpretations = tokenParser.getTokenInterpretations(tokenList.get(i), timeStamp,
                            TokenInterpretationMode.POINT, true);
                    if (listOfTokenInterpretations.isEmpty()) {
                        listOfErrorMessages.add("The format of '" + tokenList.get(i)
                                + "' token is not recognized as a Point following after 'DCT'.");
                        logger.debug("The format of '" + tokenList.get(i)
                                + "' token is not recognized as a Point following after 'DCT'.");
//						If the token format is not recognized and ingestIncorrectFormat == false, it is understood as typing error, which must be corrected first.
//						So, there is no reason to continue parsing the route string and null is returned as a sign of mistype.
                        if (!localIngestIncorrectFormat)
                            return null;
                    }
                } else {
                    int intPrevDuplTokenInd = prevDuplicatedTokenIndex(i);
                    if (intPrevDuplTokenInd >= 0) {
                        MapOfTokenInterpretations duplicatedMap = spaceOfTokenInterpretations.get(intPrevDuplTokenInd);
                        if (duplicatedMap.numOfATSroutes > 0 && (duplicatedMap.listOfTokenInterpretation
                                .get(duplicatedMap.mapOfATSroutes[0]).tokenPrimaryType == TokenPrimaryType.ATS_ROUTE
                                || duplicatedMap.listOfTokenInterpretation.get(
                                        duplicatedMap.mapOfATSroutes[0]).tokenPrimaryType == TokenPrimaryType.ATS_ROUTE_NOTFOUND)) {
                            spaceOfTokenInterpretations.add(duplicatedMap.getCopy());
                            listOfWKT = "";
                            if (duplicatedMap.numOfFoundPoints > 0) {
                                for (int j = 0; j < duplicatedMap.numOfFoundPoints; j++) {
                                    listOfWKT = listOfWKT + " - " + duplicatedMap.listOfTokenInterpretation
                                            .get(duplicatedMap.mapOfFoundPoints[j]).getRoutePointWKT();
                                }
                            }
                            logger.debug(i + 1 + ") " + tokenList.get(i) + " - " + duplicatedMap.numOfInterpretations
                                    + listOfWKT);
                            continue;
                        }
                    }
//					TokenInterpretationMode.ANY_BUT_FIRSTORLAST is used for searching ANY_BUT_FIRSTORLAST interpretations of a token,
//					whose interpretation can be POINT, ATS_ROUTE or a key word like DCT, IFR or VFR.
                    listOfTokenInterpretations = tokenParser.getTokenInterpretations(tokenList.get(i), timeStamp,
                            TokenInterpretationMode.ANY_BUT_FIRSTORLAST, true);
                    if (listOfTokenInterpretations.isEmpty()) {
                        listOfErrorMessages.add("The format of '" + tokenList.get(i)
                                + "' token is not recognized as any intermediate entity.");
                        logger.debug("The format of '" + tokenList.get(i)
                                + "' token is not recognized as any intermediate entity.");
//						If the token format is not recognized and ingestIncorrectFormat == false, it is understood as typing error, which must be corrected first.
//						So, there is no reason to continue parsing the route string and null is returned as a sign of mistype.
                        if (!localIngestIncorrectFormat)
                            return null;
                    }
                }
            }
            if (listOfTokenInterpretations.isEmpty()) {
                tokenInterpretation = new TokenInterpretation();
                tokenInterpretation.tokenPrimaryType = TokenPrimaryType.INCORRECT_FORMAT;
                tokenInterpretation.tokenValue = tokenList.get(i);
//				tokenInterpretation.parsedFeatureDesignator = tokenList.get(i);
                listOfTokenInterpretations.add(tokenInterpretation);
            } else if (!isTokenRecognizedAndFound(listOfTokenInterpretations)) {
//				If the currently iterated token format is recognized as the one, which is based on the feature from database (not a key word),
//				but is actually either not found in the database or is found, but is too far from the route, the corresponding notification is stored into the 'listOfErrorMessages'.
                listOfErrorMessages.add("'" + tokenList.get(i)
                        + "' has a format of a feature-based token, but the feature with the respective designator is not found in the database.");
                logger.debug("'" + tokenList.get(i)
                        + "' has a format of a feature-based token, but the feature with the respective designator is not found in the database.");
            }
            MapOfTokenInterpretations mapOfTokenInterpretations = new MapOfTokenInterpretations(
                    listOfTokenInterpretations, tokenList.get(i), originTokenInterpretation, destinTokenInterpretation,
                    localMaxRatioForRteLength);

            if (circularRoute) {
                mapOfTokenInterpretations.filterOffPointsLocatedTooFar(MAX_RTE_SEGMENT_LENGTH * 10, circularRoute);
            } else
                mapOfTokenInterpretations.filterOffPointsLocatedTooFar(baseRouteScenario.getDirectPathLength(), false);

            if (mapOfTokenInterpretations.numOfFoundPoints > 0)
                numOfParsedAndFoundPointsInRoute = numOfParsedAndFoundPointsInRoute + 1;

            spaceOfTokenInterpretations.add(mapOfTokenInterpretations);
            listOfWKT = "";
            if (mapOfTokenInterpretations.numOfFoundPoints > 0) {
                for (int j = 0; j < mapOfTokenInterpretations.numOfFoundPoints; j++) {
                    listOfWKT = listOfWKT + " - " + mapOfTokenInterpretations.listOfTokenInterpretation
                            .get(mapOfTokenInterpretations.mapOfFoundPoints[j]).getRoutePointWKT();
                }
            }
            logger.debug(i + 1 + ") " + tokenList.get(i) + " - " + mapOfTokenInterpretations.numOfInterpretations
                    + listOfWKT);
        }

        // now we try to decrease the number of token interpretation for ATS routes if
        // we can find it in the data provider
        int mapInd = -1;
        for (MapOfTokenInterpretations currentMap : spaceOfTokenInterpretations) {
            mapInd++;

            if (currentMap.numOfATSroutes == 0)
                continue;
            assert currentMap.numOfATSroutes == 1;

            TokenInterpretation atsRouteTokenInterpretation = currentMap.listOfTokenInterpretation
                    .get(currentMap.mapOfATSroutes[0]);

            if (atsRouteTokenInterpretation.tokenPrimaryType != TokenPrimaryType.ATS_ROUTE)
                continue;

            boolean startRoutePointIsFound = false;
            boolean endRoutePointIsFound = false;

            if (mapInd > 0 && mapInd < (spaceOfTokenInterpretations.size() - 1)) {

                MapOfTokenInterpretations prevMap = spaceOfTokenInterpretations.get(mapInd - 1);
                MapOfTokenInterpretations nextMap = spaceOfTokenInterpretations.get(mapInd + 1);

                if (prevMap.numOfFoundPoints == 0 || nextMap.numOfFoundPoints == 0)
                    continue;

                List<String> prevPointUniqueDesignators = getUniquePointDesignators(prevMap);

                List<String> nextPointUniqueDesignators = getUniquePointDesignators(nextMap);

                retrieveRoutePortion(atsRouteTokenInterpretation, prevPointUniqueDesignators,
                        nextPointUniqueDesignators, timeStamp);

                if (atsRouteTokenInterpretation.atsRoutePortion == null
                        || (atsRouteTokenInterpretation.atsRoutePortion.numOfPoints() < 2)) {
                    atsRouteTokenInterpretation.atsRoutePortion = null;
                    continue;
                }

//				make ATS Route an only valid/trusted interpretation
                currentMap.numOfInterpretations = 1;
                currentMap.numOfFoundPoints = 0;
                currentMap.numOfNotFoundPoints = 0;
                currentMap.numOfKeyWords = 0;

                RoutePoint firstRoutePoint = atsRouteTokenInterpretation.atsRoutePortion.getFirstPoint();

                startRoutePointIsFound = false;
                for (int i = 0; i < prevMap.numOfFoundPoints; i++) {
                    if (prevMap.listOfTokenInterpretation.get(prevMap.mapOfFoundPoints[i])
                            .isBasedOnRoutePoint(firstRoutePoint)) {
//						make the first ATS Route point an only valid/trusted interpretation in the previous map
                        prevMap.numOfInterpretations = 1;
                        prevMap.numOfFoundPoints = 1;
                        prevMap.mapOfFoundPoints[0] = prevMap.mapOfFoundPoints[i];
                        prevMap.numOfNotFoundPoints = 0;
                        prevMap.numOfATSroutes = 0;
                        prevMap.numOfKeyWords = 0;
                        startRoutePointIsFound = true;
                        break;
                    }
                }

                if (!startRoutePointIsFound) {
                    prevMap.pointConnectedToRoute = false;
                    listOfErrorMessages.add("The first '" + firstRoutePoint.designator
                            + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                    logger.debug("The first '" + firstRoutePoint.designator
                            + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                }

                RoutePoint lastRoutePoint = atsRouteTokenInterpretation.atsRoutePortion.getLastPoint();

                endRoutePointIsFound = false;
                for (int i = 0; i < nextMap.numOfFoundPoints; i++) {
                    if (nextMap.listOfTokenInterpretation.get(nextMap.mapOfFoundPoints[i])
                            .isBasedOnRoutePoint(lastRoutePoint)) {
//						make the last ATS Route point an only valid/trusted interpretation in the next map
                        nextMap.numOfInterpretations = 1;
                        nextMap.numOfFoundPoints = 1;
                        nextMap.mapOfFoundPoints[0] = nextMap.mapOfFoundPoints[i];
                        nextMap.numOfNotFoundPoints = 0;
                        nextMap.numOfATSroutes = 0;
                        nextMap.numOfKeyWords = 0;
                        endRoutePointIsFound = true;
                        break;
                    }
                }

                if (!endRoutePointIsFound) {
                    listOfErrorMessages.add("The last '" + lastRoutePoint.designator
                            + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                    logger.debug("The last '" + lastRoutePoint.designator
                            + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                }

                if (startRoutePointIsFound && endRoutePointIsFound) {
                    nextMap.pointConnectedToRoute = true;
                }
            } else {
                if (mapInd == 0 && spaceOfTokenInterpretations.size() > 1) {
                    MapOfTokenInterpretations nextMap = spaceOfTokenInterpretations.get(mapInd + 1);
                    if (nextMap.numOfFoundPoints == 0)
                        continue;

                    List<String> nextPointUniqueDesignators = getUniquePointDesignators(nextMap);

                    if (originRoutePoint.pointType.ordinal() > RoutePointType.ADHP.ordinal()) {
                        LinkedList<String> prevPointUniqueDesignators = new LinkedList<String>();
                        prevPointUniqueDesignators.add(originRoutePoint.designator);

                        retrieveRoutePortion(atsRouteTokenInterpretation, prevPointUniqueDesignators,
                                nextPointUniqueDesignators, timeStamp);

                        if (atsRouteTokenInterpretation.atsRoutePortion == null
                                || (atsRouteTokenInterpretation.atsRoutePortion.numOfPoints() < 2)) {
                            atsRouteTokenInterpretation.atsRoutePortion = null;
                            continue;
                        }

                        RoutePoint firstRoutePoint = atsRouteTokenInterpretation.atsRoutePortion.getFirstPoint();

                        if (!originRoutePoint.equalsUpTo6DecPlaces(firstRoutePoint)) {
                            listOfErrorMessages.add("The first '" + firstRoutePoint.designator
                                    + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                            logger.debug("The first '" + firstRoutePoint.designator
                                    + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                        }
                    } else {
                        retrieveRoutePortionConnectedToADHP(atsRouteTokenInterpretation, originRoutePoint, true,
                                nextPointUniqueDesignators, timeStamp);

                        if (atsRouteTokenInterpretation.atsRoutePortion == null
                                || atsRouteTokenInterpretation.atsRoutePortion.numOfPoints() < 2) {
                            atsRouteTokenInterpretation.atsRoutePortion = null;
                            continue;
                        }
                    }

//					make ATS Route an only valid/trusted interpretation
                    currentMap.numOfInterpretations = 1;
                    currentMap.numOfFoundPoints = 0;
                    currentMap.numOfNotFoundPoints = 0;
                    currentMap.numOfKeyWords = 0;

                    RoutePoint lastRoutePoint = atsRouteTokenInterpretation.atsRoutePortion.getLastPoint();

                    endRoutePointIsFound = false;
                    for (int i = 0; i < nextMap.numOfFoundPoints; i++) {
                        if (nextMap.listOfTokenInterpretation.get(nextMap.mapOfFoundPoints[i])
                                .isBasedOnRoutePoint(lastRoutePoint)) {
                            nextMap.numOfInterpretations = 1;
                            nextMap.numOfFoundPoints = 1;
                            nextMap.mapOfFoundPoints[0] = nextMap.mapOfFoundPoints[i];
                            nextMap.numOfNotFoundPoints = 0;
                            nextMap.numOfATSroutes = 0;
                            nextMap.numOfKeyWords = 0;
                            endRoutePointIsFound = true;
                            break;
                        }
                    }

                    if (endRoutePointIsFound)
                        nextMap.pointConnectedToRoute = true;
                    else {
                        listOfErrorMessages.add("The last '" + lastRoutePoint.designator
                                + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                        logger.debug("The last '" + lastRoutePoint.designator
                                + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                    }
                } else if (mapInd == (spaceOfTokenInterpretations.size() - 1)
                        && spaceOfTokenInterpretations.size() > 1) {
                    MapOfTokenInterpretations prevMap = spaceOfTokenInterpretations.get(mapInd - 1);
                    if (prevMap.numOfFoundPoints == 0)
                        continue;

                    List<String> prevPointUniqueDesignators = getUniquePointDesignators(prevMap);

                    if (destinationRoutePoints[0].pointType.ordinal() > RoutePointType.ADHP.ordinal()) {
                        LinkedList<String> nextPointUniqueDesignators = new LinkedList<String>();
                        nextPointUniqueDesignators.add(destinationRoutePoints[0].designator);

                        retrieveRoutePortion(atsRouteTokenInterpretation, prevPointUniqueDesignators,
                                nextPointUniqueDesignators, timeStamp);

                        if (atsRouteTokenInterpretation.atsRoutePortion == null
                                || (atsRouteTokenInterpretation.atsRoutePortion.numOfPoints() < 2)) {
                            atsRouteTokenInterpretation.atsRoutePortion = null;
                            continue;
                        }

                        RoutePoint lastRoutePoint = atsRouteTokenInterpretation.atsRoutePortion.getLastPoint();

                        if (!destinationRoutePoints[0].equalsUpTo6DecPlaces(lastRoutePoint)) {
                            listOfErrorMessages.add("The last '" + lastRoutePoint.designator
                                    + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                            logger.debug("The last '" + lastRoutePoint.designator
                                    + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                        }
                    } else {
                        retrieveRoutePortionConnectedToADHP(atsRouteTokenInterpretation, destinationRoutePoints[0],
                                false, prevPointUniqueDesignators, timeStamp);

                        if (atsRouteTokenInterpretation.atsRoutePortion == null
                                || atsRouteTokenInterpretation.atsRoutePortion.numOfPoints() < 2) {
                            atsRouteTokenInterpretation.atsRoutePortion = null;
                            continue;
                        }
                    }

                    currentMap.numOfInterpretations = 1;
                    currentMap.numOfFoundPoints = 0;
                    currentMap.numOfNotFoundPoints = 0;
                    currentMap.numOfKeyWords = 0;

                    RoutePoint firstRoutePoint = atsRouteTokenInterpretation.atsRoutePortion.getFirstPoint();

                    startRoutePointIsFound = false;
                    for (int i = 0; i < prevMap.numOfFoundPoints; i++) {
                        if (prevMap.listOfTokenInterpretation.get(prevMap.mapOfFoundPoints[i])
                                .isBasedOnRoutePoint(firstRoutePoint)) {
                            prevMap.numOfInterpretations = 1;
                            prevMap.numOfFoundPoints = 1;
                            prevMap.mapOfFoundPoints[0] = prevMap.mapOfFoundPoints[i];
                            prevMap.numOfNotFoundPoints = 0;
                            prevMap.numOfATSroutes = 0;
                            prevMap.numOfKeyWords = 0;
                            startRoutePointIsFound = true;
                            break;
                        }
                    }

                    if (!startRoutePointIsFound) {
                        prevMap.pointConnectedToRoute = false;
                        listOfErrorMessages.add("The first '" + firstRoutePoint.designator
                                + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                        logger.debug("The first '" + firstRoutePoint.designator
                                + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                    }
                } else if (mapInd == 0 && spaceOfTokenInterpretations.size() == 1) {

                    if (originRoutePoint.pointType.ordinal() > RoutePointType.ADHP.ordinal()
                            && destinationRoutePoints[0].pointType.ordinal() > RoutePointType.ADHP.ordinal()) {

                        LinkedList<String> prevPointUniqueDesignators = new LinkedList<String>();
                        prevPointUniqueDesignators.add(originRoutePoint.designator);

                        LinkedList<String> nextPointUniqueDesignators = new LinkedList<String>();
                        nextPointUniqueDesignators.add(destinationRoutePoints[0].designator);

                        retrieveRoutePortion(atsRouteTokenInterpretation, prevPointUniqueDesignators,
                                nextPointUniqueDesignators, timeStamp);

                        if (atsRouteTokenInterpretation.atsRoutePortion == null
                                || (atsRouteTokenInterpretation.atsRoutePortion.numOfPoints() < 2)) {
                            atsRouteTokenInterpretation.atsRoutePortion = null;
                            continue;
                        }

                        RoutePoint firstRoutePoint = atsRouteTokenInterpretation.atsRoutePortion.getFirstPoint();

                        if (!originRoutePoint.equalsUpTo6DecPlaces(firstRoutePoint)) {
                            listOfErrorMessages.add("The first '" + firstRoutePoint.designator
                                    + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                            logger.debug("The first '" + firstRoutePoint.designator
                                    + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                        }

                        RoutePoint lastRoutePoint = atsRouteTokenInterpretation.atsRoutePortion.getLastPoint();

                        if (!destinationRoutePoints[0].equalsUpTo6DecPlaces(lastRoutePoint)) {
                            listOfErrorMessages.add("The last '" + lastRoutePoint.designator
                                    + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                            logger.debug("The last '" + lastRoutePoint.designator
                                    + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                        }
                    } else {
                        retrieveRoutePortionFromADHPtoADHP(atsRouteTokenInterpretation, originRoutePoint,
                                destinationRoutePoints[0], timeStamp);

                        if (atsRouteTokenInterpretation.atsRoutePortion == null
                                || atsRouteTokenInterpretation.atsRoutePortion.numOfPoints() < 2) {
                            atsRouteTokenInterpretation.atsRoutePortion = null;
                            continue;
                        }
                    }

                    currentMap.numOfInterpretations = 1;
                    currentMap.numOfFoundPoints = 0;
                    currentMap.numOfNotFoundPoints = 0;
                    currentMap.numOfKeyWords = 0;
                }
            }
        }

        generateListOfRouteScenarios(listOfRouteScenarios, completeSearch, numOfParsedAndFoundPointsInRoute);
//		If the final 'listOfRouteScenarios' list is empty, then null is returned.  
        if (listOfRouteScenarios.isEmpty())
            return null;

//		If the final 'listOfRouteScenarios' list contains more than 1 possible scenarios they are sorted in priority order, specified by RouteScenarioComp Comparator class.  
        if (listOfRouteScenarios.size() > 1)
            Collections.sort(listOfRouteScenarios, new RouteScenarioComp());

//		The first item of 'listOfRouteScenarios' list is chosen to be an output/resultant route interpretation scenario.  
        RouteScenario bestOutputScenario = listOfRouteScenarios.getFirst();
        ArrayList<TokenInterpretation> routeInterpretation = bestOutputScenario.getRouteInterpretation();

        if (!completeSearch) {
            boolean regenerateListOfScenarios = false;

            double notFoundTokensRatio = ((double) bestOutputScenario.getNumOfNotFoundFeatures()) / tokenListSize;

            if (notFoundTokensRatio > localMaxRatioForNotFoundTokens)
                regenerateListOfScenarios = true;
//			else {
//				int nextInd = 0;
//				int ordinalOfPrevInterpretation = 0;
//				for (TokenInterpretation tempTokenInterp: routeInterpretation) {
//					nextInd++;
//					if (tempTokenInterp.tokenPrimaryType.ordinal() != TokenPrimaryType.ATS_ROUTE.ordinal()
//						|| tempTokenInterp.atsRoutePortion == null) {
//						ordinalOfPrevInterpretation = tempTokenInterp.tokenPrimaryType.ordinal();
//						continue;
//					}

//					if	((nextInd > 1 && ordinalOfPrevInterpretation > TokenPrimaryType.STAR_ROUTE.ordinal()) 
//						|| (nextInd < routeInterpretation.size() && routeInterpretation.get(nextInd).tokenPrimaryType.ordinal() > TokenPrimaryType.STAR_ROUTE.ordinal())) {

//						regenerateListOfScenarios = true;
//						break;
//					}
//				}
//			}
            if (regenerateListOfScenarios) {
                completeSearch = true;
//				LinkedList<RouteScenario> 'listOfRouteScenarios' is initialised by the first simplest (root) instance of RouteScenario class
                listOfRouteScenarios.clear();
                listOfRouteScenarios.add(baseRouteScenario);
                logger.debug(
                        "The list of scenarious will be regenerated with 'completeSearch' parameter set to 'true':");
                generateListOfRouteScenarios(listOfRouteScenarios, completeSearch, numOfParsedAndFoundPointsInRoute);
                if (listOfRouteScenarios.isEmpty())
                    return null;
                if (listOfRouteScenarios.size() > 1)
                    Collections.sort(listOfRouteScenarios, new RouteScenarioComp());
                bestOutputScenario = listOfRouteScenarios.getFirst();
                routeInterpretation = bestOutputScenario.getRouteInterpretation();
            }
        }

        if (startPointType != null)
            routeInterpretation.get(0).tokenPrimaryType = startPointType;
        if (endPointType != null)
            routeInterpretation.get(routeInterpretation.size() - 1).tokenPrimaryType = endPointType;

        lastFoundRouteScenario = bestOutputScenario;

        /*
         * routeInterpretation.get(0).tokenValue = strOriginAD; for (int j = 1; j <=
         * tokenListSize; j++) { routeInterpretation.get(j).tokenValue = tokenList.get(j
         * - 1); } routeInterpretation.get(tokenListSize + 1).tokenValue =
         * strDestinationAD[0];
         */
        lastFoundRouteStructure = new ArrayList<RoutePoint>(50);
        listOfRoutePortionsByFlightLevel = new LinkedList<RoutePortion>();
        RoutePortion tempRoutePortion = new RoutePortion();

        originRoutePoint.fillDisplayAttributes(true);
        lastFoundRouteStructure.add(originRoutePoint);
        tempRoutePortion.addRoutePoint(originRoutePoint);

        int nextInd = 0;
        int numOfRoutePoints;
        int ordinalOfPrevInterpretation = 0;
        RoutePoint tempRoutePoint = null;
        for (TokenInterpretation tempTokenInterp : routeInterpretation) {
            nextInd++;
            if (tempTokenInterp.tokenPrimaryType.ordinal() < TokenPrimaryType.ADHP.ordinal()
                    || tempTokenInterp.tokenPrimaryType.ordinal() > TokenPrimaryType.STAR_ROUTE.ordinal()
                    || nextInd == 1 || nextInd == routeInterpretation.size()) {
                ordinalOfPrevInterpretation = tempTokenInterp.tokenPrimaryType.ordinal();
                continue;
            }

            switch (tempTokenInterp.tokenPrimaryType) {
            case ATS_ROUTE:
                if (tempTokenInterp.atsRoutePortion == null
                        || (nextInd > 1 && ordinalOfPrevInterpretation > TokenPrimaryType.STAR_ROUTE.ordinal())
                        || (nextInd < routeInterpretation.size() && routeInterpretation.get(nextInd).tokenPrimaryType
                                .ordinal() > TokenPrimaryType.STAR_ROUTE.ordinal())) {

                    ordinalOfPrevInterpretation = tempTokenInterp.tokenPrimaryType.ordinal();
                    continue;
                }
                lastFoundRouteStructure.addAll(tempTokenInterp.atsRoutePortion.getInnerRoutePointsForMap(true));
                tempRoutePortion.addAllRoutePoints(tempTokenInterp.atsRoutePortion.getInnerRoutePointsForMap());
                break;
            case SIGNIFICANT_POINT:
            case SID_ROUTE:
            case STAR_ROUTE:
            case ADHP:
                numOfRoutePoints = lastFoundRouteStructure.size();
                tempRoutePoint = lastFoundRouteStructure.get(numOfRoutePoints - 1);

                if (tempRoutePoint.getY().equals(tempTokenInterp.latitude)
                        && tempRoutePoint.getX().equals(tempTokenInterp.longitude)) {

                    lastFoundRouteStructure.remove(numOfRoutePoints - 1);
                } else {
                    tempRoutePortion.addRoutePoint(new RoutePoint(tempTokenInterp, this.timeStamp));
                }

                lastFoundRouteStructure.add(new RoutePoint(tempTokenInterp, this.timeStamp));

                if (tempTokenInterp.significantPointPrimType == SignificantPointPrimaryType.SPEEDORLEVELCHANGE) {
                    listOfRoutePortionsByFlightLevel.add(tempRoutePortion);
                    tempRoutePortion = new RoutePortion();
                    tempRoutePortion.cruisingLevel = tempTokenInterp.cruisingLevelChange;
                    tempRoutePortion.addRoutePoint(new RoutePoint(tempTokenInterp, this.timeStamp));
                }
            default:
            }
            ordinalOfPrevInterpretation = tempTokenInterp.tokenPrimaryType.ordinal();
        }
        destinationRoutePoints[0].fillDisplayAttributes(true);
        lastFoundRouteStructure.add(destinationRoutePoints[0]);
        tempRoutePortion.addRoutePoint(destinationRoutePoints[0]);
        listOfRoutePortionsByFlightLevel.add(tempRoutePortion);

//		listOfErrorMessages.add("NUMBER OF FOUND SCENARIOS: " + listOfRouteScenarios.size());
        logger.debug("NUMBER OF FOUND SCENARIOS: " + listOfRouteScenarios.size());

        String report = "";
        if (bestOutputScenario.getDirectPathLength() > 0)
            report = report + String.format("1) RATIO: %.6f; ",
                    bestOutputScenario.getRouteLength() / bestOutputScenario.getDirectPathLength());
        report = report + String.format("LENGTH: %.0f; ", bestOutputScenario.getRouteLength()) + "; ";
        report = report + "FOUND PNTS: " + Integer.toString(bestOutputScenario.getNumOfFoundPoints()) + "; ";
        report = report + "NOT FOUND FEATS: " + Integer.toString(bestOutputScenario.getNumOfNotFoundFeatures()) + "; ";

        String routeWKT = "LINESTRING(";
        boolean insertFlag = false;
        String notFoundFeats = "NOT FOUND FEATS:";

        String tokenLabel;
        int i = 0;
        for (TokenInterpretation tokenInterp : routeInterpretation) {
            if (insertFlag)
                report = report.concat("; ");
            if (tokenInterp.parsedFeatureDesignator != null)
                tokenLabel = tokenInterp.parsedFeatureDesignator;
            else
                tokenLabel = tokenInterp.tokenValue;
            report = report.concat(Integer.toString(i + 1) + ")" + tokenLabel + " - ");
            if (tokenInterp.tokenPrimaryType == TokenPrimaryType.SIGNIFICANT_POINT
                    || tokenInterp.tokenPrimaryType == TokenPrimaryType.SIGNIFICANT_POINT_NOTFOUND) {
                report = report.concat(tokenInterp.significantPointPrimType.name());
                if (tokenInterp.significantPointPrimType == SignificantPointPrimaryType.NAVAID
                        || tokenInterp.significantPointPrimType == SignificantPointPrimaryType.BEARINGDISTANCE) {
                    if (tokenInterp.tokenPrimaryType == TokenPrimaryType.SIGNIFICANT_POINT)
                        report = report.concat("/" + tokenInterp.navaidType.name());
                } else if (tokenInterp.significantPointPrimType == SignificantPointPrimaryType.SPEEDORLEVELCHANGE
                        || tokenInterp.significantPointPrimType == SignificantPointPrimaryType.CRUISECLIMB) {
                    report = report.concat("/" + tokenInterp.significantPointSecType.name());
                    if (tokenInterp.significantPointSecType == SignificantPointSecondaryType.NAVAID
                            || tokenInterp.significantPointSecType == SignificantPointSecondaryType.BEARINGDISTANCE) {
                        if (tokenInterp.tokenPrimaryType == TokenPrimaryType.SIGNIFICANT_POINT)
                            report = report.concat("/" + tokenInterp.navaidType.name());
                    }
                }
            } else {
                report = report.concat(tokenInterp.tokenPrimaryType.name());
            }
            if (i > tokenListSize) {
                report = report.concat(
                        "[" + tokenInterp.getLongitude().toString() + " " + tokenInterp.getLatitude().toString() + "]");
                break;
            }
            i++;
            if (tokenInterp.getRoutePointWKT() == null) {
                if (tokenInterp.tokenPrimaryType.ordinal() >= TokenPrimaryType.INCORRECT_FORMAT.ordinal())
                    notFoundFeats = notFoundFeats.concat(" " + tokenLabel);
                continue;
            }
            report = report.concat(
                    "[" + tokenInterp.getLongitude().toString() + " " + tokenInterp.getLatitude().toString() + "]");
            insertFlag = true;
        }

        i = 0;
        int lastInd = lastFoundRouteStructure.size();
        for (RoutePoint tempPoint : lastFoundRouteStructure) {
            i++;
            if (i == lastInd)
                break;
            routeWKT = routeWKT
                    .concat(tempPoint.getLongitude().toString() + " " + tempPoint.getLatitude().toString() + ",");
        }

//		The returned list is actually a list of WKTes of the route alternatives, which differ by the arrival Aerodrome and its respective location coordinates. 
        listOfRouteWKTs = new LinkedList<String>();
        for (int k = 0; k < strDestinationToken.length; k++) {
            if (destinationRoutePoints[k].getX() != null && destinationRoutePoints[k].getY() != null) {
                listOfRouteWKTs.add(routeWKT.concat(destinationRoutePoints[k].getLongitude().toString() + " "
                        + destinationRoutePoints[k].getLatitude().toString() + ")"));
            }
        }
//		listOfErrorMessages.add(report);
        logger.debug(report);
        insertFlag = false;
        i = 0;
        for (RouteScenario tempScenario : listOfRouteScenarios) {
            i = i + 1;
            if (insertFlag) {
                if (tempScenario.getDirectPathLength() > 0)
                    report = String.format(i + ") RATIO: %.6f; ",
                            tempScenario.getRouteLength() / tempScenario.getDirectPathLength());
                report = report + String.format("LENGTH: %.0f; ", tempScenario.getRouteLength()) + "; ";
                report = report + "FOUND PNTS: " + Integer.toString(tempScenario.getNumOfFoundPoints()) + "; ";
                report = report + "NOT FOUND FEATS: " + Integer.toString(tempScenario.getNumOfNotFoundFeatures());
//				listOfErrorMessages.add(report);
                logger.debug(report);
            }
            insertFlag = true;
        }
//		listOfErrorMessages.add(notFoundFeats);
        logger.debug(notFoundFeats);
        return listOfRouteWKTs;
    }

    /**
     * Parses circular route string, searches respective features in the database
     * and reproduces the route geographic path based on the found point nodes and
     * recognized en-route portions. Circular route is a route returning back to the
     * departure aerodrome (departure and arrival ADHP-designators are identical).
     * 
     * @param cruisingSpeedOrMachNumber    can accept one of the following 3
     *                                     formats: - K followed by 4 NUMERICS
     *                                     giving the true airspeed in kilometres
     *                                     per hour; - N followed by 4 NUMERICS
     *                                     giving the true airspeed in knots; - M
     *                                     followed by 3 NUMERICS giving the true
     *                                     Mach number to the nearest hundredth of
     *                                     unit Mach.
     * @param elapsedTime                  can accept 4 NUMERICS, giving the total
     *                                     estimated elapsed time in HHMM format.
     * @param routeLengthMaxToleranceCoeff The function calculates estimated route
     *                                     length based on the first two parameter
     *                                     values. Acceptable route length is
     *                                     calculated by multiplication of the
     *                                     estimated route length and the
     *                                     routeLengthMaxToleranceCoeff value. Any
     *                                     recognized point-type token, located from
     *                                     the departure/destination airport at a
     *                                     distance longer than half of acceptable
     *                                     route length, will automatically be
     *                                     rejected.
     * @param maxRouteSegmentLengthInNM    specifies maximum segment length in NM
     *                                     between consequitive point-type tokens in
     *                                     the route. The function tries to verify
     *                                     credibility of all recognized and
     *                                     retrieved (from the database) points by
     *                                     measuring distances between consequitive
     *                                     ones. If some of the retrieved points
     *                                     generate route segments longer than the
     *                                     specified value, a point with a lower
     *                                     credibility at one of the segment ends
     *                                     will be rejected.
     * @return List of route geographical paths for the respective primary and
     *         alternative aerodromes in well-known text (WKT) format. Zero's item
     *         of the list always contains the geographical path to the main
     *         destination aerodrome. If alternative aerodrome(s) were input, the
     *         respective 2nd and others output route geographical paths will differ
     *         only by last pair of coordinates.
     * @throws SQLException
     */
    public LinkedList<String> getCircularRouteLineWKT(String cruisingSpeedOrMachNumber, String elapsedTime,
            double routeLengthMaxToleranceCoeff, double maxRouteSegmentLengthInNM) throws SQLException {
        listOfRouteWKTs = null;
        lastFoundRouteScenario = null;
        lastFoundRouteDraft = null;
        lastFoundRouteStructure = null;
        lastNotFoundADHP = null;
        listOfRoutePortionsByFlightLevel = null;
        tokenArray = null;
        tokenList = null;
        tokenListSize = 0;
        double maxRouteSegmentLengthInMeters = maxRouteSegmentLengthInNM * NM_TO_METER;

        Double estimatedRouteLengthInMeters;
        Double acceptableMaxRouteLengthInMeters;

        TokenInterpretation originADtokenInterpretation = null;
        ArrayList<TokenInterpretation> listOfTokenInterpretations;

        if (listOfLastFoundAltAD_TI != null) {
            listOfLastFoundAltAD_TI.clear();
        }

        if (listOfLastFoundAltAD_RP != null) {
            listOfLastFoundAltAD_RP.clear();
        }

        if (listOfErrorMessages != null) {
            listOfErrorMessages.clear();
        }

//		if (listOfErrorMessages == null) listOfErrorMessages = new LinkedList<String>();

        if (cruisingSpeedOrMachNumber == null) {
            listOfErrorMessages.add(
                    "'cruisingSpeedOrMachNumber' parameter of 'RouteFinder.getCircularRouteLineWKT' method cannot accept 'Null'.");
            logger.debug(
                    "'cruisingSpeedOrMachNumber' parameter of 'RouteFinder.getCircularRouteLineWKT' method cannot accept 'Null'.");
            return null;
        } else if (cruisingSpeedOrMachNumber.length() < 4 || cruisingSpeedOrMachNumber.length() > 5) {
            listOfErrorMessages.add(
                    "'cruisingSpeedOrMachNumber' parameter string contains either less than 4 or more than 5 characters, which is unacceptable.");
            logger.debug(
                    "'cruisingSpeedOrMachNumber' parameter string contains either less than 4 or more than 5 characters, which is unacceptable.");
            return null;
        } else if (elapsedTime == null) {
            listOfErrorMessages.add(
                    "'elapsedTime' parameter of 'RouteFinder.getCircularRouteLineWKT' method cannot accept 'Null'.");
            logger.debug(
                    "'elapsedTime' parameter of 'RouteFinder.getCircularRouteLineWKT' method cannot accept 'Null'.");
            return null;
        } else if (elapsedTime.length() != 4) {
            listOfErrorMessages.add("'elapsedTime' parameter string does not match 4-digit 'HHMM' format.");
            logger.debug("'elapsedTime' parameter string does not match 4-digit 'HHMM' format.");
            return null;
        }

        Matcher matcher;
        double speedMPH = 0;
        double elapsedHours;
        double elapsedMinutes;

        if (cruisingSpeedOrMachNumber.length() == 4) {
            if (patternMachNumber == null)
                patternMachNumber = Pattern.compile("^[Mm]([0-9]{3})$");
            matcher = patternMachNumber.matcher(cruisingSpeedOrMachNumber);
            if (matcher.matches()) {
                speedMPH = Double.parseDouble(matcher.group(1)) * MACH_TO_KMPH * 10; // to convert to meters per hour we
                                                                                     // multiply to 10 (not 1000) cause
                                                                                     // Mach number is given in the
                                                                                     // nearest hundredth of unit Mach.
            } else {
                listOfErrorMessages
                        .add("4-character 'cruisingSpeedOrMachNumber' parameter string does not match 'M###' format.");
                logger.debug("4-character 'cruisingSpeedOrMachNumber' parameter string does not match 'M###' format.");
                return null;
            }
        } else if (cruisingSpeedOrMachNumber.length() == 5) {
            if (patternCruisingSpeed == null)
                patternCruisingSpeed = Pattern.compile("^([KkNn])([0-9]{4})$");
            matcher = patternCruisingSpeed.matcher(cruisingSpeedOrMachNumber);
            if (matcher.matches()) {
                speedMPH = Double.parseDouble(matcher.group(2));

                if (matcher.group(1).equalsIgnoreCase("K")) {
                    speedMPH = speedMPH * 1000;
                } else if (matcher.group(1).equalsIgnoreCase("N")) {
                    speedMPH = speedMPH * 1852;
                }
            } else {
                listOfErrorMessages.add(
                        "5-character 'cruisingSpeedOrMachNumber' parameter string matches neither 'K####' nor 'N####' format.");
                logger.debug(
                        "5-character 'cruisingSpeedOrMachNumber' parameter string matches neither 'K####' nor 'N####' format.");
                return null;
            }
        }

        if (patternTimeHHMM == null)
            patternTimeHHMM = Pattern.compile("^([0-9]{2})([0-5][0-9])$");
        matcher = patternTimeHHMM.matcher(elapsedTime);
        if (matcher.matches()) {
            elapsedHours = Integer.parseInt(matcher.group(1));
            elapsedMinutes = Integer.parseInt(matcher.group(2));
        } else {
            listOfErrorMessages.add(
                    "'elapsedTime' parameter string does not match 4-digit 'HHMM' format. Please note that 'MM' cannot be greater than 59 minutes.");
            logger.debug(
                    "'elapsedTime' parameter string does not match 4-digit 'HHMM' format. Please note that 'MM' cannot be greater than 59 minutes.");
            return null;
        }

        estimatedRouteLengthInMeters = speedMPH * (elapsedHours + elapsedMinutes / 60);
        acceptableMaxRouteLengthInMeters = estimatedRouteLengthInMeters * routeLengthMaxToleranceCoeff;

        if (fullRoute == null || strOriginToken == null || connection == null || timeStamp == null) {
            listOfErrorMessages.add("Null value is found among parameters used by 'getCircularRouteLineWKT' function.");
            logger.debug("Null value is found among parameters used by 'getCircularRouteLineWKT' function.");
            return null; // This line must be deleted when the exception mechanism is specified
        }
        fullRoute = fullRoute.trim();

        strOriginToken = strOriginToken.trim();
        int tokenLength = strOriginToken.length();

        if (tokenParser == null)
            tokenParser = new TokenParser(connection, timeStamp);

        if (tokenLength == 4) {
            listOfTokenInterpretations = tokenParser.tryInterpretADHP(strOriginToken);
        } else {
            if (tokenLength == 7) {
                listOfTokenInterpretations = tokenParser.tryInterpretDegreesOnlyPoint(strOriginToken);
            } else if (tokenLength == 11) {
                listOfTokenInterpretations = tokenParser.tryInterpretDegreesMinutesPoint(strOriginToken);
            } else if (tokenLength == 15) {
                listOfTokenInterpretations = tokenParser.tryInterpretDegreesMinutesSecondsPoint(strOriginToken);
            } else {
                listOfErrorMessages
                        .add("Incorrect format of '" + strOriginToken + "' for departure aerodrome/coordinates.");
                logger.debug("Incorrect format of '" + strOriginToken + "' for departure aerodrome/coordinates.");
                return null;
            }
        }

        if (listOfTokenInterpretations.isEmpty()) {
            listOfErrorMessages.add("'" + strOriginToken + "' token has invalid format for route departure point.");
            logger.debug("'" + strOriginToken + "' token has invalid format for route departure point.");
            return null;
        } else {
            originADtokenInterpretation = listOfTokenInterpretations.get(0);
            if (originADtokenInterpretation.tokenPrimaryType == TokenPrimaryType.ADHP_NOTFOUND) {
                listOfErrorMessages.add("Departure AirportHeliport feature with '" + strOriginToken
                        + "' designator is not found in the database.");
                logger.debug("Departure AirportHeliport feature with '" + strOriginToken
                        + "' designator is not found in the database.");
                return null;
            }

            if (originADtokenInterpretation.tokenPrimaryType != TokenPrimaryType.ADHP) {
                originADtokenInterpretation.tokenPrimaryType = TokenPrimaryType.ADHP;
                originADtokenInterpretation.name = strOriginToken;
            }
            originRoutePoint = new RoutePoint(originADtokenInterpretation, timeStamp);
        }
        listOfTokenInterpretations.clear();

        if (originRoutePoint.getLongitude() == null || originRoutePoint.getLatitude() == null) {
            listOfErrorMessages
                    .add("Was not able to retrieve latitude and/or longitude of departure AirportHeliport feature.");
            logger.debug("Was not able to retrieve latitude and/or longitude of departure AirportHeliport feature.");
            return null;
        }

        destinationRoutePoints = new RoutePoint[strDestinationToken.length];

        strDestinationToken[0] = strDestinationToken[0].trim();

        if (strDestinationToken[0].equalsIgnoreCase(strOriginToken)) {
            destinationRoutePoints[0] = originRoutePoint.getClone();
        } else {
            listOfErrorMessages
                    .add("'getCircularRouteLineWKT' function call is incorrect, since '" + strDestinationToken[0]
                            + "' - destination token is not identical to '" + strOriginToken + "' - departure token.");
            logger.debug("'getCircularRouteLineWKT' function call is incorrect, since '" + strDestinationToken[0]
                    + "' - destination token is not identical to '" + strOriginToken + "' - departure token.");
            return null;
        }

//		fullRoute string is split into String-array of tokens 'tokenArray' and then saved into List<String> 'tokenList'
        tokenArray = fullRoute.split("\\s+");
        tokenList = new LinkedList<String>(Arrays.asList(tokenArray));
        tokenListSize = tokenList.size();
        if (tokenListSize == 0) {
            listOfErrorMessages.add("No tokens in the route string were recognized.");
            logger.debug("No tokens in the route string were recognized.");
            return null;
        }

//		The first token of the fullRoute string (in 'tokenList') is compared to the originAD parameter and is removed if equal to departure Aerodrome 
        while (tokenListSize > 0 && tokenList.get(0).equals(strOriginToken)) {
            tokenList.remove(0);
            tokenListSize = tokenList.size();
        }
        if (tokenListSize == 0) {
            listOfErrorMessages
                    .add("No tokens except for departure aerodrome designator were recognized in the route string.");
            logger.debug("No tokens except for departure aerodrome designator were recognized in the route string.");
            return null;
        }
//		The last token of the fullRoute string (in 'tokenList') is compared to the strDestinationAD[0] and is removed if equal to destination Aerodrome 
        while (tokenListSize > 0 && tokenList.get(tokenListSize - 1).equals(strDestinationToken[0])) {
            tokenList.remove(tokenListSize - 1);
            tokenListSize = tokenList.size();
        }
        if (tokenListSize == 0) {
            listOfErrorMessages.add(
                    "No tokens except for departure/destination aerodrome designator were recognized in the route string.");
            logger.debug(
                    "No tokens except for departure/destination aerodrome designator were recognized in the route string.");
            return null;
        }

//		PreparedStatement 'psDistance' to calculate distance between two geographical points in WKT format is initialised
//		if (psDistance == null) psDistance = connection.prepareStatement("SELECT ST_Distance(?::geography, ?::geography)");

//		ArrayList<MapOfTokenInterpretations> 'spaceOfTokenInterpretations' is a structure for storing all possible interpretations for all route tokens
//		for each token there is an instance of MapOfTokenInterpretations, which stores mapping references onto typical alternative TokenInterpretation-s of the same token 
        spaceOfTokenInterpretations = new ArrayList<MapOfTokenInterpretations>(30);
//		ArrayList<TokenInterpretation> listOfTokenInterpretations;

        logger.debug("#) DES.- NUM OF INTERP.- LIST OF FOUND LOCATIONS");
        TokenInterpretation tokenInterpretation;
        String listOfWKT;
        int numOfParsedAndFoundPointsInRoute = 0;

//		The elements of List<String> tokenList are transfered to 'tokenParser.getTokenInterpretations' function in the following loop (below)
//		to get the list of all possible instances of TokenInterpretation: One list of TokenInterpretation instances for each token of the route at one iteration.
//		The retrieved 'listOfTokenInterpretations' list is used to initialise MapOfTokenInterpretations 'mapOfTokenInterpretations',
//		which is then added to 'spaceOfTokenInterpretations'.
//		Calls of 'getTokenInterpretations' vary in iterations by 'interpretationMode' parameter,
//		which helps to avoid surely wrong token interpretations based on interpretation of previous token and known semantic rules.
        for (int i = 0; i < tokenListSize; i++) {
            if (i == 0) {
//				TokenInterpretationMode.FIRST is used for searching only those interpretations of a token, which can follow after the departure aerodrome
                listOfTokenInterpretations = tokenParser.getTokenInterpretations(tokenList.get(i), timeStamp,
                        TokenInterpretationMode.FIRST, true);
                if (listOfTokenInterpretations.isEmpty()) {
                    listOfErrorMessages.add("The format of '" + tokenList.get(i)
                            + "' token is not recognized in the position right after departure aerodrome.");
                    logger.debug("The format of '" + tokenList.get(i)
                            + "' token is not recognized in the position right after departure aerodrome.");
//					'getTokenInterpretations' returns empty list if the format of the token string does not match any known/allowed format.
//					If the token format is not recognized and ingestIncorrectFormat == false, a typing error is recognized, which must be corrected first.
//					So, there is no reason to continue parsing the route string and null is returned as a sign of mistype.
                    if (!localIngestIncorrectFormat)
                        return null;
                }
            } else if (i == tokenListSize - 1) {
//				TokenInterpretationMode.LAST is used for searching only those interpretations of a token, which can precede the arrival aerodrome
                listOfTokenInterpretations = tokenParser.getTokenInterpretations(tokenList.get(i), timeStamp,
                        TokenInterpretationMode.LAST, true);
                if (listOfTokenInterpretations.isEmpty()) {
                    listOfErrorMessages.add("The format of '" + tokenList.get(i)
                            + "' token is not recognized in the position right before arrival aerodrome.");
                    logger.debug("The format of '" + tokenList.get(i)
                            + "' token is not recognized in the position right before arrival aerodrome.");
//					If the token format is not recognized and ingestIncorrectFormat == false, it is understood as typing error, which must be corrected first.
//					So, there is no reason to continue parsing the route string and null is returned as a sign of mistype.
                    if (!localIngestIncorrectFormat)
                        return null;
                }
            } else {
                ArrayList<TokenInterpretation> prevListOfTokenInterpretations = spaceOfTokenInterpretations
                        .get(i - 1).listOfTokenInterpretation;
                tokenInterpretation = prevListOfTokenInterpretations.get(0);
                if (prevListOfTokenInterpretations.size() == 1
                        && tokenInterpretation.tokenPrimaryType == TokenPrimaryType.ATS_ROUTE) {
//					TokenInterpretationMode.POINT is used for searching only POINT interpretations of a token, which can follow after ATS route
                    listOfTokenInterpretations = tokenParser.getTokenInterpretations(tokenList.get(i), timeStamp,
                            TokenInterpretationMode.POINT, true);
                    if (listOfTokenInterpretations.isEmpty()) {
                        listOfErrorMessages.add("The format of '" + tokenList.get(i)
                                + "' token is not recognized as a Point following after ATS_ROUTE.");
                        logger.debug("The format of '" + tokenList.get(i)
                                + "' token is not recognized as a Point following after ATS_ROUTE.");
//						If the token format is not recognized and ingestIncorrectFormat == false, it is understood as typing error, which must be corrected first.
//						So, there is no reason to continue parsing the route string and null is returned as a sign of mistype.
                        if (!localIngestIncorrectFormat)
                            return null;
                    }
                } else if (tokenInterpretation.tokenPrimaryType == TokenPrimaryType.KEY_WORD
                        && tokenInterpretation.keyWord == KeyWord.DCT) {
//					TokenInterpretationMode.POINT is used for searching only POINT interpretations of a token, which can follow after DCT keyword
                    listOfTokenInterpretations = tokenParser.getTokenInterpretations(tokenList.get(i), timeStamp,
                            TokenInterpretationMode.POINT, true);
                    if (listOfTokenInterpretations.isEmpty()) {
                        listOfErrorMessages.add("The format of '" + tokenList.get(i)
                                + "' token is not recognized as a Point following after 'DCT'.");
                        logger.debug("The format of '" + tokenList.get(i)
                                + "' token is not recognized as a Point following after 'DCT'.");
//						If the token format is not recognized and ingestIncorrectFormat == false, it is understood as typing error, which must be corrected first.
//						So, there is no reason to continue parsing the route string and null is returned as a sign of mistype.
                        if (!localIngestIncorrectFormat)
                            return null;
                    }
                } else {
                    int intPrevDuplTokenInd = prevDuplicatedTokenIndex(i);
                    if (intPrevDuplTokenInd >= 0) {
                        MapOfTokenInterpretations duplicatedMap = spaceOfTokenInterpretations.get(intPrevDuplTokenInd);
                        if (duplicatedMap.numOfATSroutes > 0 && (duplicatedMap.listOfTokenInterpretation
                                .get(duplicatedMap.mapOfATSroutes[0]).tokenPrimaryType == TokenPrimaryType.ATS_ROUTE
                                || duplicatedMap.listOfTokenInterpretation.get(
                                        duplicatedMap.mapOfATSroutes[0]).tokenPrimaryType == TokenPrimaryType.ATS_ROUTE_NOTFOUND)) {
                            spaceOfTokenInterpretations.add(duplicatedMap.getCopy());
                            listOfWKT = "";
                            if (duplicatedMap.numOfFoundPoints > 0) {
                                for (int j = 0; j < duplicatedMap.numOfFoundPoints; j++) {
                                    listOfWKT = listOfWKT + " - " + duplicatedMap.listOfTokenInterpretation
                                            .get(duplicatedMap.mapOfFoundPoints[j]).getRoutePointWKT();
                                }
                            }
                            logger.debug(i + 1 + ") " + tokenList.get(i) + " - " + duplicatedMap.numOfInterpretations
                                    + listOfWKT);
                            continue;
                        }
                    }
//					TokenInterpretationMode.ANY_BUT_FIRSTORLAST is used for searching ANY_BUT_FIRSTORLAST interpretations of a token,
//					whose interpretation can be POINT, ATS_ROUTE or a key word like DCT, IFR or VFR.
                    listOfTokenInterpretations = tokenParser.getTokenInterpretations(tokenList.get(i), timeStamp,
                            TokenInterpretationMode.ANY_BUT_FIRSTORLAST, true);
                    if (listOfTokenInterpretations.isEmpty()) {
                        listOfErrorMessages.add("The format of '" + tokenList.get(i)
                                + "' token is not recognized as any intermediate entity.");
                        logger.debug("The format of '" + tokenList.get(i)
                                + "' token is not recognized as any intermediate entity.");
//						If the token format is not recognized and ingestIncorrectFormat == false, it is understood as typing error, which must be corrected first.
//						So, there is no reason to continue parsing the route string and null is returned as a sign of mistype.
                        if (!localIngestIncorrectFormat)
                            return null;
                    }
                }
            }
            if (listOfTokenInterpretations.isEmpty()) {
                tokenInterpretation = new TokenInterpretation();
                tokenInterpretation.tokenPrimaryType = TokenPrimaryType.INCORRECT_FORMAT;
//				tokenInterpretation.parsedFeatureDesignator = tokenList.get(i);
                listOfTokenInterpretations.add(tokenInterpretation);
            } else if (!isTokenRecognizedAndFound(listOfTokenInterpretations)) {
//				If the currently iterated token format is recognized as the one, which is based on the feature from database (not a key word),
//				but is actually either not found in the database or is found, but is too far from the route, the corresponding notification is stored into the 'listOfErrorMessages'.
                listOfErrorMessages.add("'" + tokenList.get(i)
                        + "' has a format of a feature-based token, but the feature with the respective designator is not found in the database.");
                logger.debug("'" + tokenList.get(i)
                        + "' has a format of a feature-based token, but the feature with the respective designator is not found in the database.");
            }
            MapOfTokenInterpretations mapOfTokenInterpretations = new MapOfTokenInterpretations(
                    listOfTokenInterpretations, tokenList.get(i), originADtokenInterpretation,
                    originADtokenInterpretation, 1);
            if (mapOfTokenInterpretations.numOfFoundPoints > 0)
                numOfParsedAndFoundPointsInRoute = numOfParsedAndFoundPointsInRoute + 1;
            spaceOfTokenInterpretations.add(mapOfTokenInterpretations);
            listOfWKT = "";
            if (mapOfTokenInterpretations.numOfFoundPoints > 0) {
                for (int j = 0; j < mapOfTokenInterpretations.numOfFoundPoints; j++) {
                    listOfWKT = listOfWKT + " - " + mapOfTokenInterpretations.listOfTokenInterpretation
                            .get(mapOfTokenInterpretations.mapOfFoundPoints[j]).getRoutePointWKT();
                }
            }
            logger.debug(i + 1 + ") " + tokenList.get(i) + " - " + mapOfTokenInterpretations.numOfInterpretations
                    + listOfWKT);
        }

        LinkedList<MapOfTokenInterpretations> mapsToBeExcluded = new LinkedList<MapOfTokenInterpretations>();
        int parsedPointCounter = 0;
//		boolean countablePointsFlag = true;

        // now we try to decrease the number of token interpretation for ATS routes if
        // we can find it in the data provider
        MapOfTokenInterpretations previousMap;
        int mapInd = -1;
        for (MapOfTokenInterpretations currentMap : spaceOfTokenInterpretations) {
            mapInd++;

            if (!localIngestIncorrectSyntax) {
//			Syntax verification of the flight plan string 
                if (mapInd == 0) {
                    if (!areSyntacticallyConnectable(originADtokenInterpretation, currentMap)) {
                        listOfErrorMessages.add("Was not able to connect departure '"
                                + originADtokenInterpretation.tokenValue
                                + "' aerodrome token to any interpretation of '" + currentMap.tokenValue + "' token.");
                        logger.debug("Was not able to connect departure '" + originADtokenInterpretation.tokenValue
                                + "' aerodrome token to any interpretation of '" + currentMap.tokenValue + "' token.");
                        return null;
                    }
                } else {
                    previousMap = spaceOfTokenInterpretations.get(mapInd - 1);

                    if (!areSyntacticallyConnectable(previousMap, currentMap)) {
                        listOfErrorMessages
                                .add("Was not able to connect any interpretation of '" + previousMap.tokenValue
                                        + "' token to any interpretation of '" + currentMap.tokenValue + "' token.");
                        logger.debug("Was not able to connect any interpretation of '" + previousMap.tokenValue
                                + "' token to any interpretation of '" + currentMap.tokenValue + "' token.");
                        return null;
                    }

                    if (mapInd == (spaceOfTokenInterpretations.size() - 1)
                            && !areSyntacticallyConnectable(currentMap, originADtokenInterpretation)) {
                        listOfErrorMessages.add("Was not able to connect any interpretation of '"
                                + currentMap.tokenValue + "' token to destination '"
                                + originADtokenInterpretation.tokenValue + "' aerodrome token.");
                        logger.debug("Was not able to connect any interpretation of '" + currentMap.tokenValue
                                + "' token to destination '" + originADtokenInterpretation.tokenValue
                                + "' aerodrome token.");
                        return null;
                    }
                }
            }

//			If a token has been recognized as a point, the parsedPointCounter need to be incremented.
//			parsedPointCounter allows correct identification of maximum possible distance between found route points.
            if (currentMap.numOfFoundPoints > 0 || currentMap.numOfNotFoundPoints > 0) {
                parsedPointCounter++;
                currentMap.setPointIndex(parsedPointCounter);
            }

//			If a token has a point-type format but was not found in DB and it does not have ATS-route format, 
//			it is excluded from the route path identification.
            if (currentMap.numOfFoundPoints <= 0 && currentMap.numOfATSroutes <= 0)
                mapsToBeExcluded.add(currentMap);

//			If a token does not have ATS-route format, skip to the next iteration.
            if (currentMap.numOfATSroutes == 0)
                continue;
            assert currentMap.numOfATSroutes == 1;

            TokenInterpretation atsRouteTokenInterpretation = currentMap.listOfTokenInterpretation
                    .get(currentMap.mapOfATSroutes[0]);

//			If a token having ATS-route format was not found in DB, skip to the next iteration.
//			If moreover that token was not found in DB as a point, it is excluded from the route path identification.
            if (atsRouteTokenInterpretation.tokenPrimaryType != TokenPrimaryType.ATS_ROUTE) {
                if (currentMap.numOfFoundPoints <= 0) {
                    mapsToBeExcluded.add(currentMap);
//					if (currentMap.numOfNotFoundPoints <= 0) countablePointsFlag = false;
                    listOfErrorMessages.add("'" + currentMap.tokenValue
                            + "' has a format of ATS Route token, but the Route feature with the respective designator is not found in the database.");
                    logger.debug("'" + currentMap.tokenValue
                            + "' has a format of ATS Route token, but the Route feature with the respective designator is not found in the database.");
                }
                continue;
            }

            boolean startRoutePointIsFound = false;
            boolean endRoutePointIsFound = false;

            if (mapInd > 0 && mapInd < (spaceOfTokenInterpretations.size() - 1)) {

                MapOfTokenInterpretations prevMap = spaceOfTokenInterpretations.get(mapInd - 1);
                MapOfTokenInterpretations nextMap = spaceOfTokenInterpretations.get(mapInd + 1);

//				If a token having ATS-route format was found in DB, but surrounding point-type tokens were not found in DB,
//				it is excluded from the route path identification and we skip to the next iteration.
                if (prevMap.numOfFoundPoints == 0 || nextMap.numOfFoundPoints == 0) {
                    if (currentMap.numOfFoundPoints <= 0) {
                        mapsToBeExcluded.add(currentMap);
//						if (currentMap.numOfNotFoundPoints <= 0) countablePointsFlag = false;
                        listOfErrorMessages.add("'" + currentMap.tokenValue
                                + "' has a format of ATS Route token and was found in the database, but one or both surrounding tokens were not found in the database as Point-type features.");
                        logger.debug("'" + currentMap.tokenValue
                                + "' has a format of ATS Route token and was found in the database, but one or both surrounding tokens were not found in the database as Point-type features.");
                    }
                    continue;
                }

                List<String> prevPointUniqueDesignators = getUniquePointDesignators(prevMap);

                List<String> nextPointUniqueDesignators = getUniquePointDesignators(nextMap);

                retrieveRoutePortion(atsRouteTokenInterpretation, prevPointUniqueDesignators,
                        nextPointUniqueDesignators, timeStamp);

                if (atsRouteTokenInterpretation.atsRoutePortion == null
                        || (atsRouteTokenInterpretation.atsRoutePortion.numOfPoints() < 2)) {
                    atsRouteTokenInterpretation.atsRoutePortion = null;
                    if (currentMap.numOfFoundPoints <= 0) {
                        mapsToBeExcluded.add(currentMap);
//						if (currentMap.numOfNotFoundPoints <= 0) countablePointsFlag = false;
                        listOfErrorMessages.add("'" + currentMap.tokenValue
                                + "' has a format of ATS Route token and was found in the database, but one or both surrounding Point-type tokens are not found among the points belonged to this route.");
                        logger.debug("'" + currentMap.tokenValue
                                + "' has a format of ATS Route token and was found in the database, but one or both surrounding Point-type tokens are not found among the points belonged to this route.");
                    }
                    continue;
                }

//				make ATS Route an only valid/trusted interpretation
                currentMap.numOfInterpretations = 1;
                currentMap.numOfFoundPoints = 0;
                currentMap.numOfNotFoundPoints = 0;
                currentMap.numOfKeyWords = 0;

                RoutePoint firstRoutePoint = atsRouteTokenInterpretation.atsRoutePortion.getFirstPoint();

                startRoutePointIsFound = false;
                for (int i = 0; i < prevMap.numOfFoundPoints; i++) {
                    if (prevMap.listOfTokenInterpretation.get(prevMap.mapOfFoundPoints[i])
                            .isBasedOnRoutePoint(firstRoutePoint)) {
//						make the first ATS Route point an only valid/trusted interpretation in the previous map
                        prevMap.numOfInterpretations = 1;
                        prevMap.numOfFoundPoints = 1;
                        prevMap.mapOfFoundPoints[0] = prevMap.mapOfFoundPoints[i];
                        prevMap.numOfNotFoundPoints = 0;
                        prevMap.numOfATSroutes = 0;
                        prevMap.numOfKeyWords = 0;
                        prevMap.listOfTokenInterpretation.get(prevMap.mapOfFoundPoints[i]).connectedToRoute = true;
                        startRoutePointIsFound = true;
                        break;
                    }
                }

                if (!startRoutePointIsFound) {
//					prevMap.pointConnectedToRoute = false;
                    listOfErrorMessages.add("The first '" + firstRoutePoint.designator
                            + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                    logger.debug("The first '" + firstRoutePoint.designator
                            + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                }

                RoutePoint lastRoutePoint = atsRouteTokenInterpretation.atsRoutePortion.getLastPoint();

                endRoutePointIsFound = false;
                for (int i = 0; i < nextMap.numOfFoundPoints; i++) {
                    if (nextMap.listOfTokenInterpretation.get(nextMap.mapOfFoundPoints[i])
                            .isBasedOnRoutePoint(lastRoutePoint)) {
//						make the last ATS Route point an only valid/trusted interpretation in the next map
                        nextMap.numOfInterpretations = 1;
                        nextMap.numOfFoundPoints = 1;
                        nextMap.mapOfFoundPoints[0] = nextMap.mapOfFoundPoints[i];
                        nextMap.numOfNotFoundPoints = 0;
                        nextMap.numOfATSroutes = 0;
                        nextMap.numOfKeyWords = 0;
                        nextMap.listOfTokenInterpretation.get(nextMap.mapOfFoundPoints[i]).connectedToRoute = true;
                        endRoutePointIsFound = true;
                        break;
                    }
                }

                if (!endRoutePointIsFound) {
                    listOfErrorMessages.add("The last '" + lastRoutePoint.designator
                            + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                    logger.debug("The last '" + lastRoutePoint.designator
                            + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                }

//				Only if both previous and next tokens have been recognized as a start and an end of the current ATS-route token,
//				they get 'true' value for 'pointConnectedToRoute' flag.
                if (startRoutePointIsFound && endRoutePointIsFound) {
                    prevMap.pointConnectedToRoute = true;
                    nextMap.pointConnectedToRoute = true;
                    if (currentMap.numOfFoundPoints > 0 || currentMap.numOfNotFoundPoints > 0) {
                        parsedPointCounter--;
                    }
                    currentMap.setPointIndex(-1);
                } else if (currentMap.numOfFoundPoints <= 0) {
                    mapsToBeExcluded.add(currentMap);
//					countablePointsFlag = false;
                    listOfErrorMessages.add("'" + currentMap.tokenValue
                            + "' has a format of ATS Route token and was found in the database, but one or both surrounding Point-type tokens are not found among the points belonged to this route.");
                    logger.debug("'" + currentMap.tokenValue
                            + "' has a format of ATS Route token and was found in the database, but one or both surrounding Point-type tokens are not found among the points belonged to this route.");
                }
            } else {
                if (mapInd == 0 && spaceOfTokenInterpretations.size() > 1) {
                    MapOfTokenInterpretations nextMap = spaceOfTokenInterpretations.get(mapInd + 1);
//					If a token having ATS-route format was found in DB, but the next point-type tokens was not found in DB,
//					it is excluded from the route path identification and we skip to the next iteration.
                    if (nextMap.numOfFoundPoints == 0) {
                        if (currentMap.numOfFoundPoints <= 0) {
                            mapsToBeExcluded.add(currentMap);
//							if (currentMap.numOfNotFoundPoints <= 0) countablePointsFlag = false;
                            listOfErrorMessages.add("'" + currentMap.tokenValue
                                    + "' has a format of ATS Route token and was found in the database, but the next '"
                                    + nextMap.tokenValue
                                    + "' token was not found in the database as a Point-type feature.");
                            logger.debug("'" + currentMap.tokenValue
                                    + "' has a format of ATS Route token and was found in the database, but the next '"
                                    + nextMap.tokenValue
                                    + "' token was not found in the database as a Point-type feature.");
                        }
                        continue;
                    }

                    List<String> nextPointUniqueDesignators = getUniquePointDesignators(nextMap);

                    retrieveRoutePortionConnectedToADHP(atsRouteTokenInterpretation, originRoutePoint, true,
                            nextPointUniqueDesignators, timeStamp);

                    if (atsRouteTokenInterpretation.atsRoutePortion == null
                            || atsRouteTokenInterpretation.atsRoutePortion.numOfPoints() < 2) {
                        atsRouteTokenInterpretation.atsRoutePortion = null;
                        if (currentMap.numOfFoundPoints <= 0) {
                            mapsToBeExcluded.add(currentMap);
//							if (currentMap.numOfNotFoundPoints <= 0) countablePointsFlag = false;
                            listOfErrorMessages.add("'" + currentMap.tokenValue
                                    + "' has a format of ATS Route token and was found in the database, but one or both surrounding Point-type tokens do not relate to this route.");
                            logger.debug("'" + currentMap.tokenValue
                                    + "' has a format of ATS Route token and was found in the database, but one or both surrounding Point-type tokens do not relate to this route.");
                        }
                        continue;
                    }

                    currentMap.numOfInterpretations = 1;
                    currentMap.numOfFoundPoints = 0;
                    currentMap.numOfNotFoundPoints = 0;
                    currentMap.numOfKeyWords = 0;

                    RoutePoint lastRoutePoint = atsRouteTokenInterpretation.atsRoutePortion.getLastPoint();

                    endRoutePointIsFound = false;
                    for (int i = 0; i < nextMap.numOfFoundPoints; i++) {
                        if (nextMap.listOfTokenInterpretation.get(nextMap.mapOfFoundPoints[i])
                                .isBasedOnRoutePoint(lastRoutePoint)) {
                            nextMap.numOfInterpretations = 1;
                            nextMap.numOfFoundPoints = 1;
                            nextMap.mapOfFoundPoints[0] = nextMap.mapOfFoundPoints[i];
                            nextMap.numOfNotFoundPoints = 0;
                            nextMap.numOfATSroutes = 0;
                            nextMap.numOfKeyWords = 0;
                            nextMap.listOfTokenInterpretation.get(nextMap.mapOfFoundPoints[i]).connectedToRoute = true;
                            endRoutePointIsFound = true;
                            break;
                        }
                    }

                    if (endRoutePointIsFound) {
                        nextMap.pointConnectedToRoute = true;
                        if (currentMap.numOfFoundPoints > 0 || currentMap.numOfNotFoundPoints > 0) {
                            parsedPointCounter--;
                        }
                        currentMap.setPointIndex(-1);
                    } else {
                        listOfErrorMessages.add("The last '" + lastRoutePoint.designator
                                + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                        logger.debug("The last '" + lastRoutePoint.designator
                                + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                        if (currentMap.numOfFoundPoints <= 0) {
                            mapsToBeExcluded.add(currentMap);
//							countablePointsFlag = false;
                        }
                    }
                } else if (mapInd == (spaceOfTokenInterpretations.size() - 1)
                        && spaceOfTokenInterpretations.size() > 1) {
                    MapOfTokenInterpretations prevMap = spaceOfTokenInterpretations.get(mapInd - 1);
//					If a token having ATS-route format was found in DB, but the previous point-type tokens was not found in DB,
//					it is excluded from the route path identification and we skip to the next iteration.
                    if (prevMap.numOfFoundPoints == 0) {
                        if (currentMap.numOfFoundPoints <= 0) {
                            mapsToBeExcluded.add(currentMap);
                            listOfErrorMessages.add("'" + currentMap.tokenValue
                                    + "' has a format of ATS Route token and was found in the database, but the previous '"
                                    + prevMap.tokenValue
                                    + "' token was not found in the database as a Point-type feature.");
                            logger.debug("'" + currentMap.tokenValue
                                    + "' has a format of ATS Route token and was found in the database, but the previous '"
                                    + prevMap.tokenValue
                                    + "' token was not found in the database as a Point-type feature.");
                        }
                        continue;
                    }

                    List<String> prevPointUniqueDesignators = getUniquePointDesignators(prevMap);

                    retrieveRoutePortionConnectedToADHP(atsRouteTokenInterpretation, originRoutePoint, false,
                            prevPointUniqueDesignators, timeStamp);

                    if (atsRouteTokenInterpretation.atsRoutePortion == null
                            || atsRouteTokenInterpretation.atsRoutePortion.numOfPoints() < 2) {
                        atsRouteTokenInterpretation.atsRoutePortion = null;
                        if (currentMap.numOfFoundPoints <= 0) {
                            mapsToBeExcluded.add(currentMap);
//							if (currentMap.numOfNotFoundPoints <= 0) countablePointsFlag = false;
                            listOfErrorMessages.add("'" + currentMap.tokenValue
                                    + "' has a format of ATS Route token and was found in the database, but one or both surrounding Point-type tokens do not relate to this route.");
                            logger.debug("'" + currentMap.tokenValue
                                    + "' has a format of ATS Route token and was found in the database, but one or both surrounding Point-type tokens do not relate to this route.");
                        }
                        continue;
                    }

                    currentMap.numOfInterpretations = 1;
                    currentMap.numOfFoundPoints = 0;
                    currentMap.numOfNotFoundPoints = 0;
                    currentMap.numOfKeyWords = 0;

                    RoutePoint firstRoutePoint = atsRouteTokenInterpretation.atsRoutePortion.getFirstPoint();

                    startRoutePointIsFound = false;
                    for (int i = 0; i < prevMap.numOfFoundPoints; i++) {
                        if (prevMap.listOfTokenInterpretation.get(prevMap.mapOfFoundPoints[i])
                                .isBasedOnRoutePoint(firstRoutePoint)) {
                            prevMap.numOfInterpretations = 1;
                            prevMap.numOfFoundPoints = 1;
                            prevMap.mapOfFoundPoints[0] = prevMap.mapOfFoundPoints[i];
                            prevMap.numOfNotFoundPoints = 0;
                            prevMap.numOfATSroutes = 0;
                            prevMap.numOfKeyWords = 0;
                            prevMap.listOfTokenInterpretation.get(prevMap.mapOfFoundPoints[i]).connectedToRoute = true;
                            startRoutePointIsFound = true;
                            break;
                        }
                    }

                    if (startRoutePointIsFound) {
                        prevMap.pointConnectedToRoute = true;
                        if (currentMap.numOfFoundPoints > 0 || currentMap.numOfNotFoundPoints > 0) {
                            parsedPointCounter--;
                        }
                        currentMap.setPointIndex(-1);
                    } else {
                        listOfErrorMessages.add("The first '" + firstRoutePoint.designator
                                + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                        logger.debug("The first '" + firstRoutePoint.designator
                                + "' route point returned by 'dataRetriver.getRoutePortion()' cannot be found among those ones found by 'dataRetriver.getFixFeatures()'.");
                        if (currentMap.numOfFoundPoints <= 0) {
                            mapsToBeExcluded.add(currentMap);
//							countablePointsFlag = false;
                        }
                    }
                } else if (mapInd == 0 && spaceOfTokenInterpretations.size() == 1) {

                    retrieveRoutePortionFromADHPtoADHP(atsRouteTokenInterpretation, originRoutePoint, originRoutePoint,
                            timeStamp);

                    if (atsRouteTokenInterpretation.atsRoutePortion == null
                            || atsRouteTokenInterpretation.atsRoutePortion.numOfPoints() < 2) {
                        atsRouteTokenInterpretation.atsRoutePortion = null;
                        if (currentMap.numOfFoundPoints <= 0) {
                            mapsToBeExcluded.add(currentMap);
//							if (currentMap.numOfNotFoundPoints <= 0) countablePointsFlag = false;
                            listOfErrorMessages.add("'" + currentMap.tokenValue
                                    + "' has a format of ATS Route token and was found in the database, but one or both surrounding Point-type tokens do not relate to this route.");
                            logger.debug("'" + currentMap.tokenValue
                                    + "' has a format of ATS Route token and was found in the database, but one or both surrounding Point-type tokens do not relate to this route.");
                        }
                        continue;
                    }

                    currentMap.numOfInterpretations = 1;
                    currentMap.numOfFoundPoints = 0;
                    currentMap.numOfNotFoundPoints = 0;
                    currentMap.numOfKeyWords = 0;
                    currentMap.setPointIndex(-1);
                }
            }
        }

        if (mapsToBeExcluded.size() > 0) {
            spaceOfTokenInterpretations.removeAll(mapsToBeExcluded);
            mapsToBeExcluded.clear();
        }

        if (spaceOfTokenInterpretations.size() > 0) {
            for (MapOfTokenInterpretations currentMap : spaceOfTokenInterpretations) {
                if ((currentMap.numOfFoundPoints > 0) && !currentMap.pointConnectedToRoute) {
                    currentMap.filterOffPointsLocatedTooFar(acceptableMaxRouteLengthInMeters, true, strOriginToken);
                    if (currentMap.numOfFoundPoints == 0) {
                        mapsToBeExcluded.add(currentMap);
                        listOfErrorMessages.add("'" + currentMap.tokenValue
                                + "' has a format of a feature-based token, but the respective feature(s) found in the database are located too far relatively to the departure/destination airport and the estimated route length.");
                        logger.debug("'" + currentMap.tokenValue
                                + "' has a format of a feature-based token, but the respective feature(s) found in the database are located too far relatively to the departure/destination airport and the estimated route length.");
                    }
                }
            }

            if (mapsToBeExcluded.size() > 0) {
                spaceOfTokenInterpretations.removeAll(mapsToBeExcluded);
                mapsToBeExcluded.clear();
            }
        }

        if (spaceOfTokenInterpretations.size() > 1) {
            for (int i = 1; i < spaceOfTokenInterpretations.size(); i++) {
                if (spaceOfTokenInterpretations.get(i).tokenValue
                        .equals(spaceOfTokenInterpretations.get(i - 1).tokenValue))
                    mapsToBeExcluded.add(spaceOfTokenInterpretations.get(i));
            }
        }

        if (mapsToBeExcluded.size() > 0) {
            spaceOfTokenInterpretations.removeAll(mapsToBeExcluded);
            mapsToBeExcluded.clear();
        }

        mapsToBeExcluded = null;

        originRoutePoint.parsedPointIndex = 0;
        destinationRoutePoints[0].parsedPointIndex = parsedPointCounter + 1;

//		The root 'baseRouteScenario' is initialised by TokenInterpretation instances of departure and arrival aerodromes
//		later the root 'baseRouteScenario' will iteratively evolve into a series of more complex instances of RouteScenario class
        RouteDraft baseRouteDraft = new RouteDraft(estimatedRouteLengthInMeters, acceptableMaxRouteLengthInMeters,
                maxRouteSegmentLengthInMeters);
        baseRouteDraft.addOrigin(originRoutePoint);
        baseRouteDraft.addDestination(destinationRoutePoints[0]);

//		LinkedList<RouteDraft> listOfRouteDrafts is initialised by the first simplest (root) instance of RouteDraft class
        LinkedList<RouteDraft> listOfRouteDrafts = new LinkedList<RouteDraft>();
        listOfRouteDrafts.add(baseRouteDraft);

        if (spaceOfTokenInterpretations.size() == 0) {
            lastFoundRouteDraft = baseRouteDraft;
        } else {
            RouteDraft tempDraft;
            LinkedList<RouteDraft> tempDraftList = new LinkedList<RouteDraft>();

            for (MapOfTokenInterpretations map : spaceOfTokenInterpretations) {
                for (RouteDraft routeDraft : listOfRouteDrafts) {
                    if (map.numOfFoundPoints > 0) {
                        if (map.numOfFoundPoints == 1) {
                            routeDraft.addRoutePoint(new RoutePoint(
                                    map.listOfTokenInterpretation.get(map.mapOfFoundPoints[0]), timeStamp));
                        } else {
                            for (int i = 0; i < map.numOfFoundPoints; i++) {
                                tempDraft = routeDraft.getClone();
                                tempDraft.addRoutePoint(new RoutePoint(
                                        map.listOfTokenInterpretation.get(map.mapOfFoundPoints[i]), timeStamp));
                                tempDraftList.add(tempDraft);
                            }
                        }
                    } else if (map.numOfATSroutes > 0
                            && map.listOfTokenInterpretation.get(map.mapOfATSroutes[0]).atsRoutePortion != null) {
                        tempDraft = routeDraft.getClone();
                        tempDraft.addAllRoutePoints(
                                map.listOfTokenInterpretation.get(map.mapOfATSroutes[0]).atsRoutePortion
                                        .getInnerRoutePointsForMap());
                        tempDraftList.add(tempDraft);
                    }
                }
                if (tempDraftList.size() > 0) {
                    listOfRouteDrafts.clear();
                    listOfRouteDrafts.addAll(tempDraftList);
                    tempDraftList.clear();
                }
            }
            tempDraftList.clear();

            for (RouteDraft routeDraft : listOfRouteDrafts) {
                if (routeDraft.numOfIncrediblePoints > 0)
                    tempDraftList.addAll(routeDraft.getShortenedDrafts());
            }

            listOfRouteDrafts.addAll(tempDraftList);
            tempDraftList.clear();

            if (listOfRouteDrafts.size() > 1)
                Collections.sort(listOfRouteDrafts, new RouteDraftComp());

            lastFoundRouteDraft = listOfRouteDrafts.getFirst();
        }

        lastFoundRouteStructure = lastFoundRouteDraft.getAllRoutePoints();

        lastFoundRouteScenario = new RouteScenario(connection, lastFoundRouteStructure.size());
        ArrayList<TokenInterpretation> routeInterpretation = new ArrayList<TokenInterpretation>(
                lastFoundRouteStructure.size());

        for (RoutePoint routePoint : lastFoundRouteStructure) {
            routeInterpretation.add(new TokenInterpretation(routePoint));
        }

        lastFoundRouteScenario.setRouteInterpretation(routeInterpretation);

        listOfRoutePortionsByFlightLevel = new LinkedList<RoutePortion>();
        RoutePortion tempRoutePortion = new RoutePortion();

        originRoutePoint.fillDisplayAttributes(true);
        destinationRoutePoints[0].fillDisplayAttributes(true);

        tempRoutePortion.addAllRoutePoints(lastFoundRouteStructure);
        listOfRoutePortionsByFlightLevel.add(tempRoutePortion);

        String report = String.format("ESTIMATED ROUTE LENGTH (NM): %.0f; ",
                estimatedRouteLengthInMeters / NM_TO_METER);
        report = report + String.format(
                "\n                                                       IDENTIFIED PATH LENGTH (NM): %.0f; ",
                lastFoundRouteDraft.length() / NM_TO_METER);
        report = report + String.format(
                "\n                                           RATIO OF IDENTIFIED TO ESTIMATED LENGTH: %.6f; ",
                lastFoundRouteDraft.length() / estimatedRouteLengthInMeters);
        report = report + "\n                                               NUMBER OF FOUND INTERMEDIATE POINTS: "
                + Integer.toString(lastFoundRouteDraft.numOfRoutePoints() - 2) + "\n";

        String routeWKT = "LINESTRING(";

        String tokenLabel;
        int i = 0;
        for (RoutePoint routePoint : lastFoundRouteStructure) {
            i++;
            if (i > 1)
                report = report.concat("; ");

            tokenLabel = routePoint.designator;
            report = report.concat("\n" + Integer.toString(i) + ")" + tokenLabel + " - ");
            report = report.concat(routePoint.pointType.name());
            report = report.concat(
                    " [" + routePoint.getLongitude().toString() + " " + routePoint.getLatitude().toString() + "]");

            if (i < lastFoundRouteStructure.size())
                routeWKT = routeWKT + routePoint.getLongitude() + " " + routePoint.getLatitude() + ",";
        }

//		The returned list is actually a list of WKTes of the route alternatives, which differ by the arrival Aerodrome and its respective location coordinates. 
        listOfRouteWKTs = new LinkedList<String>();

        if (strDestinationToken.length > 1) {
            listOfLastFoundAltAD_RP = new LinkedList<RoutePoint>();
            listOfLastFoundAltAD_TI = new LinkedList<TokenInterpretation>();
        }

        tokenInterpretation = null;

        for (int k = 0; k < strDestinationToken.length; k++) {
            if (k == 0) {
                listOfRouteWKTs.add(routeWKT.concat(lastFoundRouteDraft.getDepartureADHP().getLongitude().toString()
                        + " " + lastFoundRouteDraft.getDepartureADHP().getLatitude().toString() + ")"));
            } else {
                strDestinationToken[k] = strDestinationToken[k].trim();
                tokenLength = strDestinationToken[k].length();

                if (tokenLength == 4) {
                    listOfTokenInterpretations = tokenParser.tryInterpretADHP(strDestinationToken[k]);
                } else {
                    if (tokenLength == 7) {
                        listOfTokenInterpretations = tokenParser.tryInterpretDegreesOnlyPoint(strDestinationToken[k]);
                    } else if (tokenLength == 11) {
                        listOfTokenInterpretations = tokenParser
                                .tryInterpretDegreesMinutesPoint(strDestinationToken[k]);
                    } else if (tokenLength == 15) {
                        listOfTokenInterpretations = tokenParser
                                .tryInterpretDegreesMinutesSecondsPoint(strDestinationToken[k]);
                    } else {
                        listOfErrorMessages.add("Incorrect format of '" + strDestinationToken[k]
                                + "' for alternative destination aerodrome/coordinates.");
                        logger.debug("Incorrect format of '" + strDestinationToken[k]
                                + "' for alternative destination aerodrome/coordinates.");
                        continue;
                    }
                }

                if (listOfTokenInterpretations.isEmpty()) {
                    listOfErrorMessages.add("'" + strDestinationToken[k]
                            + "' token has invalid format for route alternative destination point.");
                    logger.debug("'" + strDestinationToken[k]
                            + "' token has invalid format for route alternative destination point.");
                    continue;
                } else {
                    tokenInterpretation = listOfTokenInterpretations.get(0);

                    if (tokenInterpretation.tokenPrimaryType == TokenPrimaryType.ADHP_NOTFOUND) {
                        listOfErrorMessages.add("Alternative destination AirportHeliport feature with '"
                                + strDestinationToken[k] + "' designator is not found in the database.");
                        logger.debug("Alternative destination AirportHeliport feature with '" + strDestinationToken[k]
                                + "' designator is not found in the database.");
                        continue;
                    } else if (tokenInterpretation.getLongitude() == null
                            || tokenInterpretation.getLatitude() == null) {
                        listOfErrorMessages.add("Was not able to retrieve latitude and/or longitude of '"
                                + strDestinationToken[k] + "' alternative destination AirportHeliport feature.");
                        logger.debug("Was not able to retrieve latitude and/or longitude of '" + strDestinationToken[k]
                                + "' alternative destination AirportHeliport feature.");
//						the function continues only if both departure and main destination aerodromes are retrieved with coordinates of their locations
                        continue;
                    }

                    if (tokenInterpretation.tokenPrimaryType != TokenPrimaryType.ADHP) {
                        tokenInterpretation.tokenPrimaryType = TokenPrimaryType.ADHP;
                        tokenInterpretation.name = strDestinationToken[k];
                    }
                    destinationRoutePoints[k] = new RoutePoint(tokenInterpretation, timeStamp);
                }

                listOfLastFoundAltAD_RP.add(destinationRoutePoints[k]);
                listOfLastFoundAltAD_TI.add(tokenInterpretation);

                listOfTokenInterpretations.clear();
                tokenInterpretation = null;

                listOfRouteWKTs.add(routeWKT.concat(destinationRoutePoints[k].getLongitude() + " "
                        + destinationRoutePoints[k].getLatitude() + ")"));
            }
        }

        logger.debug(report);

        return listOfRouteWKTs;
    }

    private boolean isTokenRecognizedAndFound(ArrayList<TokenInterpretation> listOfTokenInterpretations) {
        if (listOfTokenInterpretations.isEmpty())
            return false;

        for (TokenInterpretation tokenInterpretation : listOfTokenInterpretations) {
            if (tokenInterpretation.tokenPrimaryType.ordinal() <= TokenPrimaryType.STAR_ROUTE.ordinal())
                return true;
        }

        return false;
    }

    private int prevDuplicatedTokenIndex(int currentIndex) {
        String currentToken = tokenList.get(currentIndex);

        for (int i = currentIndex - 2; i >= 0 & i > (currentIndex - 10); i--) {
            if (currentToken.equals(tokenList.get(i)))
                return i;
        }

        return -1;
    }

    private List<String> getUniquePointDesignators(MapOfTokenInterpretations inputMap) {
        int numOfPoints = inputMap.numOfFoundPoints;
        if (numOfPoints <= 0)
            return null;

        List<String> outputDesignators = new ArrayList<String>(numOfPoints);

        for (int i = 0; i < numOfPoints; i++) {
            if (outputDesignators.isEmpty())
                outputDesignators.add(
                        inputMap.listOfTokenInterpretation.get(inputMap.mapOfFoundPoints[i]).parsedFeatureDesignator);
            else {
                boolean foundPointDesFlag = false;
                for (String pointDesignator : outputDesignators) {
                    if (pointDesignator.equals(inputMap.listOfTokenInterpretation
                            .get(inputMap.mapOfFoundPoints[i]).parsedFeatureDesignator)) {
                        foundPointDesFlag = true;
                        break;
                    }
                }
                if (!foundPointDesFlag)
                    outputDesignators.add(inputMap.listOfTokenInterpretation
                            .get(inputMap.mapOfFoundPoints[i]).parsedFeatureDesignator);
            }
        }
        return outputDesignators;
    }

//	The purpose of the function is to find and return a sub-path of a route, which connects
//	a fix from firstPointDesignatorList with a fix from secondPointDesignatorList.
//	The sub-path is supposed to be found based on relations among routes and fixes established through segment features.
//	If such a sub-path is found, both ending points (from firstPointDesignatorList and secondPointDesignatorList) should be included into 
//	the returned ATSRoute.atsRoutePath list of sub-path constituting points.
//	Must return null if searched sub-path is not found in the database.
    void retrieveRoutePortion(TokenInterpretation atsRteTokenInterp, List<String> firstPointDesignatorList,
            List<String> secondPointDesignatorList, Timestamp timeStamp) throws SQLException {
        // TODO: PAVEL
        if (atsRteTokenInterp.retrievedRouteStructures == null
                || atsRteTokenInterp.retrievedRouteStructures.isEmpty()) {
            if (atsRteTokenInterp.retrievedRouteRecIDs == null || atsRteTokenInterp.retrievedRouteRecIDs.isEmpty())
                return;
            if (dataRetriver == null)
                dataRetriver = new AeroDbDataRetriever(connection);
            atsRteTokenInterp.retrievedRouteStructures = dataRetriver
                    .getRoutePoints(atsRteTokenInterp.retrievedRouteRecIDs, this.timeStamp);
            if (atsRteTokenInterp.retrievedRouteStructures == null
                    || atsRteTokenInterp.retrievedRouteStructures.isEmpty())
                return;
        }

        ArrayList<RoutePoint> atsRoutePath = new ArrayList<RoutePoint>(10);
        boolean firstPointFlag = false;
        boolean secondPointFlag = false;
        int routeInd = -1;

        for (List<RoutePoint> routePoints : atsRteTokenInterp.retrievedRouteStructures) {
            routeInd++;
            if (routePoints == null || routePoints.isEmpty())
                continue;

            firstPointFlag = false;
            secondPointFlag = false;
            if (!atsRoutePath.isEmpty())
                atsRoutePath.clear();

            for (RoutePoint rtePnt : routePoints) {
                if (!firstPointFlag) {
                    for (String firstPointDes : firstPointDesignatorList) {
                        if (rtePnt.designator.equals(firstPointDes)) {
                            rtePnt.fillDisplayAttributes(true);
                            if (secondPointFlag)
                                atsRoutePath.add(0, rtePnt);
                            firstPointFlag = true;
                            break;
                        }
                    }
                }
                if (!secondPointFlag) {
                    for (String secondPointDes : secondPointDesignatorList) {
                        if (rtePnt.designator.equals(secondPointDes)) {
                            rtePnt.fillDisplayAttributes(true);
                            if (firstPointFlag)
                                atsRoutePath.add(rtePnt);
                            secondPointFlag = true;
                            break;
                        }
                    }
                }
                if (!firstPointFlag && !secondPointFlag)
                    continue;
                else if (firstPointFlag && !secondPointFlag) {
                    rtePnt.fillDisplayAttributes(true);
                    atsRoutePath.add(rtePnt);
                } else if (!firstPointFlag && secondPointFlag) {
                    rtePnt.fillDisplayAttributes(true);
                    atsRoutePath.add(0, rtePnt);
                } else if (firstPointFlag && secondPointFlag)
                    break;
            }

            if (firstPointFlag && secondPointFlag)
                break;
        }

        if (firstPointFlag && secondPointFlag) {
            ATSRoute atsRoute = new ATSRoute();
            atsRoute.atsRoutePath = atsRoutePath;
            atsRoute.routeRecID = atsRteTokenInterp.retrievedRouteRecIDs.get(routeInd);
            atsRoute.timestamp = (Timestamp) timeStamp.clone();
            atsRteTokenInterp.atsRoutePortion = atsRoute;

            if (atsRteTokenInterp.retrievedRouteStructures.size() > 1) {
                List<RoutePoint> tempRoutePoints = atsRteTokenInterp.retrievedRouteStructures.get(routeInd);
                atsRteTokenInterp.retrievedRouteStructures.clear();
                atsRteTokenInterp.retrievedRouteStructures.add(tempRoutePoints);
//				atsRteTokenInterp.retrievedRouteStructures.retainAll(atsRteTokenInterp.retrievedRouteStructures.get(routeInd));

                Long tempRouteRecID = atsRteTokenInterp.retrievedRouteRecIDs.get(routeInd);
                atsRteTokenInterp.retrievedRouteRecIDs.clear();
                atsRteTokenInterp.retrievedRouteRecIDs.add(tempRouteRecID);
            }
        } else
            atsRteTokenInterp.atsRoutePortion = null;
    }

//	The function should not necessarily be implemented. If it returns null the route-finding algorithm will continue working without errors.
//	The purpose of the function is to find and return a sub-path of a route, which could in the best way connect adhpRoutePoint and one of the points from pointDesignatorList.
//	The sub-path is supposed to be found based on relations among routes and fixes established through segment features.
//	A fix adjacent to adhpRoutePoint should be found as the nearest to that adhpRoutePoint.
//	If such a sub-path is found, both adhpRoutePoint and an item from pointDesignatorList should be included into 
//	the returned ATSRoute.atsRoutePath list of sub-path constituting points.
//	If 'isDepartureADHP' is true ATSRoute.atsRoutePath should start with adhpRoutePoint.
//	Otherwise if 'isDepartureADHP' is false ATSRoute.atsRoutePath should end with adhpRoutePoint.
//	Must return null if no one fix from pointDesignatorList list is found in the database as a part of atsRouteDesignator route.
    void retrieveRoutePortionConnectedToADHP(TokenInterpretation atsRteTokenInterp, RoutePoint adhpRoutePoint,
            boolean isDepartureADHP, List<String> pointDesignatorList, Timestamp timeStamp) throws SQLException {
        // TODO: Pavel

        if (atsRteTokenInterp.retrievedRouteStructures == null
                || atsRteTokenInterp.retrievedRouteStructures.isEmpty()) {
            if (atsRteTokenInterp.retrievedRouteRecIDs == null || atsRteTokenInterp.retrievedRouteRecIDs.isEmpty())
                return;
            if (dataRetriver == null)
                dataRetriver = new AeroDbDataRetriever(connection);
            atsRteTokenInterp.retrievedRouteStructures = dataRetriver
                    .getRoutePoints(atsRteTokenInterp.retrievedRouteRecIDs, this.timeStamp);
            if (atsRteTokenInterp.retrievedRouteStructures == null
                    || atsRteTokenInterp.retrievedRouteStructures.isEmpty())
                return;
        }

        double adhpToRtePointCurrentDistance; // temporary variable to store current ADHP to Route Point distance in the
                                              // loop
        double adhpToNearestRtePointDistance[]; // an array of found shortest ADHP to Route Point distances for all
                                                // retrieved Route Structures
        int rtePointNearestToADHPIndex[]; // an array of found Route Point indexes (for all retrieved Route Structures),
                                          // which are nearest to adhpRoutePoint
        int rtePointFoundIndex[]; // an array of found Route Point indexes (for all retrieved Route Structures),
                                  // which match at least one searched designator among pointDesignatorList

        rtePointFoundIndex = new int[atsRteTokenInterp.retrievedRouteStructures.size()];
        rtePointNearestToADHPIndex = new int[atsRteTokenInterp.retrievedRouteStructures.size()];
        adhpToNearestRtePointDistance = new double[atsRteTokenInterp.retrievedRouteStructures.size()];

        int routeInd = -1; // route index in the loop of retrieved Route Structures
        int i;
//		String adhpLocationWKT = adhpRoutePoint.getWKT();
//		if (psDistance == null) psDistance = connection.prepareStatement("SELECT ST_Distance(?::geography, ?::geography)");
//		ResultSet rs;

        for (List<RoutePoint> routePoints : atsRteTokenInterp.retrievedRouteStructures) {
            routeInd++;
            rtePointFoundIndex[routeInd] = -1;
            rtePointNearestToADHPIndex[routeInd] = -1;
            adhpToNearestRtePointDistance[routeInd] = 1000000000000000.;

            if (routePoints == null || routePoints.isEmpty())
                continue;

            i = -1; // route-point index in the inner loop of points of a Route Structure from an
                    // outer loop
            for (RoutePoint rtePnt : routePoints) {
                i++;
//				search for the nearest to ADHP route-point from a Route Structure from an outer loop
//				psDistance.setString (1, adhpLocationWKT);
//				psDistance.setString (2, rtePnt.getWKT());
//				rs = psDistance.executeQuery();	// measure distance from ADHP to the current route-point
//				if (rs.next()) {
//					adhpToRtePointCurrentDistance = (double)rs.getFloat(1);	//	distance from ADHP to the current route-point
                adhpToRtePointCurrentDistance = Geometry.distanceInMeters(adhpRoutePoint, rtePnt).doubleValue(); // distance
                                                                                                                 // from
                                                                                                                 // ADHP
                                                                                                                 // to
                                                                                                                 // the
                                                                                                                 // current
                                                                                                                 // route-point
                if (adhpToRtePointCurrentDistance < adhpToNearestRtePointDistance[routeInd]) {
                    adhpToNearestRtePointDistance[routeInd] = adhpToRtePointCurrentDistance; // save the shortest
                                                                                             // distance from ADHP to a
                                                                                             // route-point of a Route
                                                                                             // Structure from an outer
                                                                                             // loop
                    rtePointNearestToADHPIndex[routeInd] = i; // save index of a route-point nearest to ADHP
                }
//				}
//				else adhpToRtePointCurrentDistance = 1000000000000000.;

                if (rtePointFoundIndex[routeInd] < 0) {
                    for (String rtePointDes : pointDesignatorList) {
                        if (rtePnt.designator.equals(rtePointDes) && adhpToRtePointCurrentDistance < 1000000) { // 1000,000
                                                                                                                // meters
                                                                                                                // = 540
                                                                                                                // NM
                            rtePointFoundIndex[routeInd] = i;
                            if (rtePointNearestToADHPIndex[routeInd] < rtePointFoundIndex[routeInd])
                                break;
                        }
                    }
                }
            }
        }

//		search for a Route Structure, which passess at a shortest distance from ADHP
        int matchingRouteInd = -1;
        adhpToRtePointCurrentDistance = 1000000000000000.; // will be used to find the Route Structure passing at the
                                                           // shortest distance from ADHP

        if (routeInd >= 0) {
            for (i = 0; i <= routeInd; i++) {
                if (rtePointFoundIndex[i] >= 0 && adhpToNearestRtePointDistance[i] < adhpToRtePointCurrentDistance) {
                    adhpToRtePointCurrentDistance = adhpToNearestRtePointDistance[i];
                    matchingRouteInd = i;
                }
            }
        }

        if (matchingRouteInd >= 0 && adhpToNearestRtePointDistance[matchingRouteInd] < 100000) { // 100,000 meters = 54
                                                                                                 // NM
            ArrayList<RoutePoint> atsRoutePath = new ArrayList<RoutePoint>(10);

            if (isDepartureADHP) {
                atsRoutePath.add(0, adhpRoutePoint);
                if (rtePointNearestToADHPIndex[matchingRouteInd] < rtePointFoundIndex[matchingRouteInd]) {
                    for (i = rtePointNearestToADHPIndex[matchingRouteInd]; i <= rtePointFoundIndex[matchingRouteInd]; i++) {
                        atsRteTokenInterp.retrievedRouteStructures.get(matchingRouteInd).get(i)
                                .fillDisplayAttributes(true);
                        atsRoutePath.add(atsRteTokenInterp.retrievedRouteStructures.get(matchingRouteInd).get(i));
                    }
                } else if (rtePointNearestToADHPIndex[matchingRouteInd] > rtePointFoundIndex[matchingRouteInd]) {
                    for (i = rtePointNearestToADHPIndex[matchingRouteInd]; i >= rtePointFoundIndex[matchingRouteInd]; i--) {
                        atsRteTokenInterp.retrievedRouteStructures.get(matchingRouteInd).get(i)
                                .fillDisplayAttributes(true);
                        atsRoutePath.add(atsRteTokenInterp.retrievedRouteStructures.get(matchingRouteInd).get(i));
                    }
                }
            } else {
                if (rtePointFoundIndex[matchingRouteInd] < rtePointNearestToADHPIndex[matchingRouteInd]) {
                    for (i = rtePointFoundIndex[matchingRouteInd]; i <= rtePointNearestToADHPIndex[matchingRouteInd]; i++) {
                        atsRteTokenInterp.retrievedRouteStructures.get(matchingRouteInd).get(i)
                                .fillDisplayAttributes(true);
                        atsRoutePath.add(atsRteTokenInterp.retrievedRouteStructures.get(matchingRouteInd).get(i));
                    }
                } else if (rtePointFoundIndex[matchingRouteInd] > rtePointNearestToADHPIndex[matchingRouteInd]) {
                    for (i = rtePointFoundIndex[matchingRouteInd]; i >= rtePointNearestToADHPIndex[matchingRouteInd]; i--) {
                        atsRteTokenInterp.retrievedRouteStructures.get(matchingRouteInd).get(i)
                                .fillDisplayAttributes(true);
                        atsRoutePath.add(atsRteTokenInterp.retrievedRouteStructures.get(matchingRouteInd).get(i));
                    }
                }
                atsRoutePath.add(adhpRoutePoint);
            }

            ATSRoute atsRoute = new ATSRoute();
            atsRoute.atsRoutePath = atsRoutePath;
            atsRoute.routeRecID = atsRteTokenInterp.retrievedRouteRecIDs.get(matchingRouteInd);
            atsRoute.timestamp = (Timestamp) timeStamp.clone();
            atsRteTokenInterp.atsRoutePortion = atsRoute;

            if (atsRteTokenInterp.retrievedRouteStructures.size() > 1) {
                List<RoutePoint> tempRoutePoints = atsRteTokenInterp.retrievedRouteStructures.get(matchingRouteInd);
                atsRteTokenInterp.retrievedRouteStructures.clear();
                atsRteTokenInterp.retrievedRouteStructures.add(tempRoutePoints);
//				atsRteTokenInterp.retrievedRouteStructures.retainAll(atsRteTokenInterp.retrievedRouteStructures.get(matchingRouteInd));

                Long tempRouteRecID = atsRteTokenInterp.retrievedRouteRecIDs.get(matchingRouteInd);
                atsRteTokenInterp.retrievedRouteRecIDs.clear();
                atsRteTokenInterp.retrievedRouteRecIDs.add(tempRouteRecID);
            }
        }

        return;
    }

    void retrieveRoutePortionFromADHPtoADHP(TokenInterpretation atsRteTokenInterp, RoutePoint depADHPRoutePoint,
            RoutePoint arrADHPRoutePoint, Timestamp timeStamp) throws SQLException {

        if (atsRteTokenInterp.retrievedRouteStructures == null
                || atsRteTokenInterp.retrievedRouteStructures.isEmpty()) {
            if (atsRteTokenInterp.retrievedRouteRecIDs == null || atsRteTokenInterp.retrievedRouteRecIDs.isEmpty())
                return;
            if (dataRetriver == null)
                dataRetriver = new AeroDbDataRetriever(connection);
            atsRteTokenInterp.retrievedRouteStructures = dataRetriver
                    .getRoutePoints(atsRteTokenInterp.retrievedRouteRecIDs, this.timeStamp);
            if (atsRteTokenInterp.retrievedRouteStructures == null
                    || atsRteTokenInterp.retrievedRouteStructures.isEmpty())
                return;
        }

        double departureADHPtoRtePointCurDist; // temporary variable to store current departure ADHP to Route Point
                                               // distance in the loop
        double arrivalADHPtoRtePointCurDist; // temporary variable to store current arrival ADHP to Route Point distance
                                             // in the loop
        double departureADHPtoNearestRtePointDistance[];// an array of found shortest departure ADHP to Route Point
                                                        // distances for all retrieved Route Structures
        double arrivalADHPtoNearestRtePointDistance[]; // an array of found shortest arrival ADHP to Route Point
                                                       // distances for all retrieved Route Structures
        int rtePointNearestToDepartureADHPindex[]; // an array of found Route Point indexes (for all retrieved Route
                                                   // Structures), which are nearest to departure ADHP
        int rtePointNearestToArrivalADHPindex[]; // an array of found Route Point indexes (for all retrieved Route
                                                 // Structures), which are nearest to arrival ADHP

        departureADHPtoNearestRtePointDistance = new double[atsRteTokenInterp.retrievedRouteStructures.size()];
        arrivalADHPtoNearestRtePointDistance = new double[atsRteTokenInterp.retrievedRouteStructures.size()];
        rtePointNearestToDepartureADHPindex = new int[atsRteTokenInterp.retrievedRouteStructures.size()];
        rtePointNearestToArrivalADHPindex = new int[atsRteTokenInterp.retrievedRouteStructures.size()];

        int routeInd = -1; // route index in the loop of retrieved Route Structures
        int i;
//		String departureADHPlocationWKT = depADHPRoutePoint.getWKT();
//		String arrivalADHPlocationWKT = arrADHPRoutePoint.getWKT();

//		if (psDistance == null) psDistance = connection.prepareStatement("SELECT ST_Distance(?::geography, ?::geography)");
//		ResultSet rs;

        for (List<RoutePoint> routePoints : atsRteTokenInterp.retrievedRouteStructures) {

            routeInd++;
            rtePointNearestToDepartureADHPindex[routeInd] = -1;
            rtePointNearestToArrivalADHPindex[routeInd] = -1;
            departureADHPtoNearestRtePointDistance[routeInd] = 1000000000000000.;
            arrivalADHPtoNearestRtePointDistance[routeInd] = 1000000000000000.;

            if (routePoints == null || routePoints.isEmpty())
                continue;

            i = -1; // route-point index in the inner loop of points of a Route Structure from an
                    // outer loop
            for (RoutePoint rtePnt : routePoints) {
                i++;
//				search for the nearest to departure ADHP route-point from a Route Structure from an outer loop
//				psDistance.setString (1, departureADHPlocationWKT);
//				psDistance.setString (2, rtePnt.getWKT());
//				rs = psDistance.executeQuery();	// measure distance from departure ADHP to the current route-point

//				if (rs.next()) {
//					departureADHPtoRtePointCurDist = (double)rs.getFloat(1);	//	distance from departure ADHP to the current route-point
                departureADHPtoRtePointCurDist = Geometry.distanceInMeters(depADHPRoutePoint, rtePnt).doubleValue(); // distance
                                                                                                                     // from
                                                                                                                     // departure
                                                                                                                     // ADHP
                                                                                                                     // to
                                                                                                                     // the
                                                                                                                     // current
                                                                                                                     // route-point
                if (departureADHPtoRtePointCurDist < departureADHPtoNearestRtePointDistance[routeInd]) {
                    departureADHPtoNearestRtePointDistance[routeInd] = departureADHPtoRtePointCurDist; // save the
                                                                                                       // shortest
                                                                                                       // distance from
                                                                                                       // departure ADHP
                                                                                                       // to a
                                                                                                       // route-point of
                                                                                                       // a Route
                                                                                                       // Structure from
                                                                                                       // an outer loop
                    rtePointNearestToDepartureADHPindex[routeInd] = i; // save index of a route-point nearest to
                                                                       // departure ADHP
                }
//				}

//				search for the nearest to arrival ADHP route-point from a Route Structure from an outer loop
//				psDistance.setString (1, arrivalADHPlocationWKT);
//				psDistance.setString (2, rtePnt.getWKT());
//				rs = psDistance.executeQuery();	// measure distance from arrival ADHP to the current route-point

//				if (rs.next()) {
//					arrivalADHPtoRtePointCurDist = (double)rs.getFloat(1);	//	distance from arrival ADHP to the current route-point
                arrivalADHPtoRtePointCurDist = Geometry.distanceInMeters(arrADHPRoutePoint, rtePnt).doubleValue(); // distance
                                                                                                                   // from
                                                                                                                   // arrival
                                                                                                                   // ADHP
                                                                                                                   // to
                                                                                                                   // the
                                                                                                                   // current
                                                                                                                   // route-point
                if (arrivalADHPtoRtePointCurDist < arrivalADHPtoNearestRtePointDistance[routeInd]) {
                    arrivalADHPtoNearestRtePointDistance[routeInd] = arrivalADHPtoRtePointCurDist; // save the shortest
                                                                                                   // distance from
                                                                                                   // arrival ADHP to a
                                                                                                   // route-point of a
                                                                                                   // Route Structure
                                                                                                   // from an outer loop
                    rtePointNearestToArrivalADHPindex[routeInd] = i; // save index of a route-point nearest to arrival
                                                                     // ADHP
                }
//				}
            }
        }

//		search for a Route Structure, which passess at a shortest distance from both departure and arrival ADHPs
        int matchingRouteInd = -1;
        double totalADHPsToRouteCurDist = 1000000000000000.; // will be used to find the Route Structure passing at the
                                                             // shortest distance from both departure and arrival ADHPs

        if (routeInd >= 0) {
            for (i = 0; i <= routeInd; i++) {
                if ((departureADHPtoNearestRtePointDistance[i]
                        + arrivalADHPtoNearestRtePointDistance[i]) < totalADHPsToRouteCurDist) {
                    totalADHPsToRouteCurDist = departureADHPtoNearestRtePointDistance[i]
                            + arrivalADHPtoNearestRtePointDistance[i];
                    matchingRouteInd = i;
                }
            }
        }

        if (matchingRouteInd >= 0 && totalADHPsToRouteCurDist < 200000) { // 200,000 meters = 108 NM
            ArrayList<RoutePoint> atsRoutePath = new ArrayList<RoutePoint>(20);

            atsRoutePath.add(depADHPRoutePoint);
            if (rtePointNearestToDepartureADHPindex[matchingRouteInd] < rtePointNearestToArrivalADHPindex[matchingRouteInd]) {
                for (i = rtePointNearestToDepartureADHPindex[matchingRouteInd]; i <= rtePointNearestToArrivalADHPindex[matchingRouteInd]; i++) {
                    atsRteTokenInterp.retrievedRouteStructures.get(matchingRouteInd).get(i).fillDisplayAttributes(true);
                    atsRoutePath.add(atsRteTokenInterp.retrievedRouteStructures.get(matchingRouteInd).get(i));
                }
            } else if (rtePointNearestToDepartureADHPindex[matchingRouteInd] > rtePointNearestToArrivalADHPindex[matchingRouteInd]) {
                for (i = rtePointNearestToDepartureADHPindex[matchingRouteInd]; i >= rtePointNearestToArrivalADHPindex[matchingRouteInd]; i--) {
                    atsRteTokenInterp.retrievedRouteStructures.get(matchingRouteInd).get(i).fillDisplayAttributes(true);
                    atsRoutePath.add(atsRteTokenInterp.retrievedRouteStructures.get(matchingRouteInd).get(i));
                }
            }
            atsRoutePath.add(arrADHPRoutePoint);

            ATSRoute atsRoute = new ATSRoute();
            atsRoute.atsRoutePath = atsRoutePath;
            atsRoute.routeRecID = atsRteTokenInterp.retrievedRouteRecIDs.get(matchingRouteInd);
            atsRoute.timestamp = (Timestamp) timeStamp.clone();
            atsRteTokenInterp.atsRoutePortion = atsRoute;

            if (atsRteTokenInterp.retrievedRouteStructures.size() > 1) {
                List<RoutePoint> tempRoutePoints = atsRteTokenInterp.retrievedRouteStructures.get(matchingRouteInd);
                atsRteTokenInterp.retrievedRouteStructures.clear();
                atsRteTokenInterp.retrievedRouteStructures.add(tempRoutePoints);
//				atsRteTokenInterp.retrievedRouteStructures.retainAll(atsRteTokenInterp.retrievedRouteStructures.get(matchingRouteInd));

                Long tempRouteRecID = atsRteTokenInterp.retrievedRouteRecIDs.get(matchingRouteInd);
                atsRteTokenInterp.retrievedRouteRecIDs.clear();
                atsRteTokenInterp.retrievedRouteRecIDs.add(tempRouteRecID);
            }
        }

        return;
    }

    private void generateListOfRouteScenarios(LinkedList<RouteScenario> listOfRouteScenarios, boolean completeSearch,
            int numOfParsedAndFoundPointsInRoute) throws SQLException {
        logger.debug("NUM OF SCENARIOS AFTER EACH STEP");
//		The following loop is to evolve and populate the list of scenarios LinkedList<RouteScenario> 'listOfRouteScenarios'.
//		The list of scenarios 'listOfRouteScenarios' is evolved by adding token interpretations (for the next token) at each step of iteration.
//		Ideally each intermediate scenario (currently iterated in the loop) evolves into the number of branches, which corresponds to the number of interpretations of the next route token. 
//		Generation of the scenario branches is encapsulated in 'addIntermediate' method of the RouteScenario class.
        int maxNumOfFoundPoints = 0;
        int tempNumOfFoundPoints;
        int numOfRejectedTokens = 0;
        AtomicInteger returnedNumOfPoints = new AtomicInteger();
        int i = 0;
        int pointTypeCounter = 0;
        double verifyCourseChange = 0;
        int minPassLimit;
        int listSize = spaceOfTokenInterpretations.size();
        int numOfSegmentsToBeVerified = (int) Math.round(numOfParsedAndFoundPointsInRoute / 3);
        if (numOfSegmentsToBeVerified <= 0)
            numOfSegmentsToBeVerified = 1;

        LinkedList<RouteScenario> tempScenarioList = new LinkedList<RouteScenario>();
        for (MapOfTokenInterpretations mapOfTokenInterpretations : spaceOfTokenInterpretations) {
            i++;
            if (mapOfTokenInterpretations.numOfFoundPoints > 0)
                pointTypeCounter = pointTypeCounter + 1;
            minPassLimit = maxNumOfFoundPoints - listSize + i - 1;
//			In the inner loop (see below) 'tempScenario.addIntermediate' receives an instance of MapOfTokenInterpretations
//			from 'spaceOfTokenInterpretations' (which is iterated in the outer loop - see above),
//			and returns LinkedList<RouteScenario> of evolved scenario branches, which are added to LinkedList<RouteScenario> tempScenarioList.
            tempNumOfFoundPoints = 0;
            for (RouteScenario tempScenario : listOfRouteScenarios) {
//				The logic of 'addIntermediate' function needs to know whether the transfered  'mapOfTokenInterpretations' 
//				represents the last token before the destination ADHP.
//				The second 'boolean isLast' parameter gets true if i == listSize

//				The fourth 'boolean addFoundPointsOnly' parameter is the way to block generating branches of routeScenario, 
//				which do not have chances to be a first in the 'LinkedList<RouteScenario> listOfRouteScenarios' due to a small number of found points.
                if (pointTypeCounter <= 2)
                    verifyCourseChange = -1;
                else
                    verifyCourseChange = 1;

                if (tempScenario.numOfFoundPoints > minPassLimit) {
                    tempScenarioList.addAll(tempScenario.addIntermediate(mapOfTokenInterpretations, i == listSize,
                            localMaxRatioForRteLength, verifyCourseChange * localMaxCourseChangeInRadians, false,
                            returnedNumOfPoints, completeSearch, numOfSegmentsToBeVerified));
                    tempNumOfFoundPoints = Math.max(tempNumOfFoundPoints, returnedNumOfPoints.get());
                } else if (tempScenario.numOfFoundPoints == minPassLimit && tempScenario.numOfFoundPoints > 0) {
                    tempScenarioList.addAll(tempScenario.addIntermediate(mapOfTokenInterpretations, i == listSize,
                            localMaxRatioForRteLength, verifyCourseChange * localMaxCourseChangeInRadians, true,
                            returnedNumOfPoints, completeSearch, numOfSegmentsToBeVerified));
                    tempNumOfFoundPoints = Math.max(tempNumOfFoundPoints, returnedNumOfPoints.get());
                }
            }
            maxNumOfFoundPoints = Math.max(maxNumOfFoundPoints, tempNumOfFoundPoints);
//			At the end of each (outer loop) iteration 'tempScenarioList' accumulates branches evolved from branches generated for the previous route token;
//			The previous list of branches 'listOfRouteScenarios' is cleared and gets the new list of evolved scenario branches by 'addAll' method.
//			listOfRouteScenarios.clear();
            if (tempScenarioList.isEmpty()) {
                if (i == listSize) {
                    if ((i - numOfRejectedTokens) > 1) {
                        listOfErrorMessages.add(
                                "Was not able to connect any interpretation of '" + mapOfTokenInterpretations.tokenValue
                                        + "' token either to '" + tokenList.get(i - 2 - numOfRejectedTokens)
                                        + "' or to the destination aerodrome '" + strDestinationToken[0] + "'.");
                        logger.debug(
                                "Was not able to connect any interpretation of '" + mapOfTokenInterpretations.tokenValue
                                        + "' token either to '" + tokenList.get(i - 2 - numOfRejectedTokens)
                                        + "' or to the destination aerodrome '" + strDestinationToken[0] + "'.");
                    } else {
                        listOfErrorMessages.add(
                                "Was not able to connect any interpretation of '" + mapOfTokenInterpretations.tokenValue
                                        + "' token either to the departure aerodrome '" + strOriginToken
                                        + "' or to the destination aerodrome '" + strDestinationToken[0] + "'.");
                        logger.debug(
                                "Was not able to connect any interpretation of '" + mapOfTokenInterpretations.tokenValue
                                        + "' token either to the departure aerodrome '" + strOriginToken
                                        + "' or to the destination aerodrome '" + strDestinationToken[0] + "'.");
                    }
                } else if (i > (1 + numOfRejectedTokens)) {
                    listOfErrorMessages.add("Was not able to connect any interpretation of '"
                            + mapOfTokenInterpretations.tokenValue + "' token to any interpretation of '"
                            + tokenList.get(i - 1 - numOfRejectedTokens) + "'.");
                    logger.debug("Was not able to connect any interpretation of '"
                            + mapOfTokenInterpretations.tokenValue + "' token to any interpretation of '"
                            + tokenList.get(i - 1 - numOfRejectedTokens) + "'.");
                } else {
                    listOfErrorMessages.add(
                            "Was not able to connect any interpretation of '" + mapOfTokenInterpretations.tokenValue
                                    + "' token to the departure aerodrome '" + strOriginToken + "'.");
                    logger.debug(
                            "Was not able to connect any interpretation of '" + mapOfTokenInterpretations.tokenValue
                                    + "' token to the departure aerodrome '" + strOriginToken + "'.");
                }
                if (!localIngestIncorrectSyntax) {
// If localIngestIncorrectSyntax == false, the procedure returns empty list of route scenarios.
// Since a couple of tokens could not be syntactically connected the entire search process will be finally unsuccessful. 
                    listOfRouteScenarios.clear();
                    return;
                }
// If localIngestIncorrectSyntax == true, the procedure avoids/removes the current token as it cannot be syntactically connected to the previous one.
// The search is continued without the current token.
// At the same time if 'generateListOfRouteScenarios' has been called second time, this token has already been removed from the 'tokenList'
// ...which is why we first check if tokenList-item equals to mapOfTokenInterpretations-item.
                if (tokenList.size() > i - 1 - numOfRejectedTokens
                        && mapOfTokenInterpretations.tokenValue.equals(tokenList.get(i - 1 - numOfRejectedTokens))) {
                    tokenList.remove(i - 1 - numOfRejectedTokens);
                    tokenListSize = tokenList.size();
                }
                numOfRejectedTokens = numOfRejectedTokens + 1;
            } else {
                listOfRouteScenarios.clear();
                listOfRouteScenarios.addAll(tempScenarioList);
//				tempScenarioList is now cleared to accumulate a new list of scenario branches at the next (outer loop) iteration.
                logger.debug(i + ") " + tempScenarioList.size());
                tempScenarioList.clear();
            }
        }

        if (listOfRouteScenarios.isEmpty()) {
            listOfRouteScenarios.clear();
            return;
        }

        Iterator<RouteScenario> listOfRouteScenariosIterator = listOfRouteScenarios.iterator();
        RouteScenario tempScenario;
        while (listOfRouteScenariosIterator.hasNext()) {
            tempScenario = listOfRouteScenariosIterator.next();
//			If for some reason the final 'listOfRouteScenarios' list includes incorrect (from semantic point of view) scenarios, they are removed.  
            if (!tempScenario.getSyntaxCorrectnessStatus())
                listOfRouteScenariosIterator.remove();
//			else if (!tempScenario.getGeoFeasibilityStatus()) listOfRouteScenariosIterator.remove();
            else {
                tempScenario.completeScenario(localMaxRatioForRteLength);
                if (!tempScenario.getSyntaxCorrectnessStatus())
                    listOfRouteScenariosIterator.remove();
            }
        }
    }

    public boolean isCircularRoute() {
        if (this.strOriginToken == null || this.strOriginToken.isEmpty() || this.strDestinationToken == null
                || this.strDestinationToken.length == 0 || this.strDestinationToken[0].isEmpty())
            throw new NullPointerException("Flight Plan String parameters have not been initialized.");

        if (this.strOriginToken.trim().equalsIgnoreCase(this.strDestinationToken[0].trim()))
            return true;
        else
            return false;
    }

    public String getSurveillanceRouteWKT(String routeString, LinkedList<String> airspaceWKTs) throws SQLException {
        return getSurveillanceRouteWKT(routeString, airspaceWKTs, BUFFER_RAD_IN_METERS);
    }

    public String getSurveillanceRouteWKT(String routeString, LinkedList<String> airspaceWKTs,
            double bufferRadiusInMeters) throws SQLException {

        if (routeString == null || airspaceWKTs == null || airspaceWKTs.size() == 0 || connection == null
                || timeStamp == null) {
//			An Exception must be thrown!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            listOfErrorMessages.add("Null value is found among parameters used by 'getSurveillanceRouteWKT' function.");
            logger.debug("Null value is found among parameters used by 'getSurveillanceRouteWKT' function.");
            return null;
        } else if (bufferRadiusInMeters > 500000.0) {
            listOfErrorMessages.add(
                    "Airspace buffer radius value, transfered to 'getSurveillanceRouteWKT' function, exceeds 500,000 meters, which is inaceptable.");
            logger.debug(
                    "Airspace buffer radius value, transfered to 'getSurveillanceRouteWKT' function, exceeds 500,000 meters, which is inaceptable.");
            return null;
        }

        listOfRouteWKTs = null;
        lastFoundRouteScenario = null;
        lastFoundRouteDraft = null;
        lastFoundRouteStructure = null;
        lastNotFoundADHP = null;
        listOfRoutePortionsByFlightLevel = null;
        tokenArray = null;
        tokenList = null;
        tokenListSize = 0;

//		timeStamp = null;
        fullRoute = null;
        strOriginToken = null;
        strDestinationToken = null;
        originTokenInterpretation = null;
        destinTokenInterpretation = null;

        if (listOfLastFoundAltAD_TI != null) {
            listOfLastFoundAltAD_TI.clear();
        }

        if (listOfLastFoundAltAD_RP != null) {
            listOfLastFoundAltAD_RP.clear();
        }

        if (listOfErrorMessages != null) {
            listOfErrorMessages.clear();
        }

        tokenArray = routeString.split("\\s+");
        tokenList = new LinkedList<String>(Arrays.asList(tokenArray));
        tokenListSize = tokenList.size();

        if (tokenListSize == 0) {
            listOfErrorMessages.add("No tokens have been recognized in the route string.");
            logger.debug("No tokens have been recognized in the route string.");
            return null;
        } else if (tokenListSize == 1) {
            listOfErrorMessages.add("An only token has been recognized in the route string.");
            logger.debug("An only token has been recognized in the route string.");
            return null;
        }

        for (String token : tokenList)
            token = token.trim();

        for (int i = 1; i < tokenListSize; i++) {
            if (tokenList.get(i - 1).equals(tokenList.get(i))) {
                tokenList.remove(i);
                tokenListSize = tokenList.size();
                i--;
            }
        }

        if (tokenListSize == 1) {
            listOfErrorMessages.add("An only token has been recognized in the route string.");
            logger.debug("An only token has been recognized in the route string.");
            return null;
        }

        String startIdent = null;
        String endIdent = null;
        fullRoute = "";

        int j = 0;

        for (String token : tokenList) {
            if (j == 0)
                startIdent = token;
            else if (j == (tokenListSize - 1))
                endIdent = token;
            else
                fullRoute = fullRoute + token + " ";
            j++;
        }

        fullRoute = fullRoute.trim();

        boolean circularRouteFlag = false;

        if (startIdent.equals(endIdent)) {
            circularRouteFlag = true;
//			listOfErrorMessages.add("Start and End fix identifiers, transfered to 'getSurveillanceRouteWKT' function, are identical. Such a Surveillance Route cannot be processed.");
//			logger.debug("Start and End fix identifiers, transfered to 'getSurveillanceRouteWKT' function, are identical. Such a Surveillance Route cannot be processed.");
//			return null;
        }

        if (tokenParser == null)
            tokenParser = new TokenParser(connection, timeStamp);

        ArrayList<TokenInterpretation> dbFoundStartTokenInterpretations = tokenParser
                .getTokenInterpretations(startIdent, timeStamp, TokenInterpretationMode.POINT, true);

        ArrayList<TokenInterpretation> dbFoundEndTokenInterpretations;
        if (circularRouteFlag) {
            dbFoundEndTokenInterpretations = dbFoundStartTokenInterpretations;
        } else
            dbFoundEndTokenInterpretations = tokenParser.getTokenInterpretations(endIdent, timeStamp,
                    TokenInterpretationMode.POINT, true);

        if (!(dbFoundStartTokenInterpretations == null || dbFoundStartTokenInterpretations.isEmpty())) {
            for (int i = 0; i < dbFoundStartTokenInterpretations.size(); i++) {
                if (!(dbFoundStartTokenInterpretations.get(i).tokenPrimaryType == TokenPrimaryType.SIGNIFICANT_POINT
                        || dbFoundStartTokenInterpretations.get(i).tokenPrimaryType == TokenPrimaryType.ADHP)) {
                    dbFoundStartTokenInterpretations.remove(i);
                    i--;
                }
            }
        }

        ArrayList<TokenInterpretation> airspaceMatchingStartTokenInterpretations = new ArrayList<TokenInterpretation>(
                dbFoundStartTokenInterpretations.size());
        ArrayList<TokenInterpretation> airspaceMatchingEndTokenInterpretations = new ArrayList<TokenInterpretation>(
                dbFoundEndTokenInterpretations.size());

        boolean checkIfStartPointInAirspaceFlag = true;

        if (dbFoundStartTokenInterpretations == null || dbFoundStartTokenInterpretations.isEmpty()) {
            listOfErrorMessages.add("Starting '" + startIdent
                    + "' route fix, given to 'getSurveillanceRouteWKT' method, is either of incorrect format OR is not found in the database.");
            logger.debug("Starting '" + startIdent
                    + "' route fix, given to 'getSurveillanceRouteWKT' method, is either of incorrect format OR is not found in the database.");
            return null;
        }

        else {
            for (int i = 0; i < dbFoundStartTokenInterpretations.size(); i++) {
                if (dbFoundStartTokenInterpretations.get(i).tokenPrimaryType == TokenPrimaryType.SIGNIFICANT_POINT
                        && dbFoundStartTokenInterpretations.get(i).significantPointPrimType
                                .ordinal() >= SignificantPointPrimaryType.DEGREESONLY.ordinal()) {

                    if (dbFoundStartTokenInterpretations.get(i).significantPointPrimType
                            .ordinal() <= SignificantPointPrimaryType.DEGREES_MINUTES_SECONDS.ordinal()
                            || (dbFoundStartTokenInterpretations.get(i).significantPointSecType
                                    .ordinal() >= SignificantPointSecondaryType.DEGREESONLY.ordinal()
                                    && dbFoundStartTokenInterpretations.get(i).significantPointSecType
                                            .ordinal() <= SignificantPointSecondaryType.DEGREESANDMINUTES.ordinal())) {

                        checkIfStartPointInAirspaceFlag = false;
                        airspaceMatchingStartTokenInterpretations.add(dbFoundStartTokenInterpretations.get(i));
                    }
                }

                else if (dbFoundStartTokenInterpretations.get(i).tokenPrimaryType == TokenPrimaryType.ADHP) {
                    checkIfStartPointInAirspaceFlag = false;
                    airspaceMatchingStartTokenInterpretations.add(dbFoundStartTokenInterpretations.get(i));
                }
            }
        }

        boolean checkIfEndPointInAirspaceFlag = true;

        if (circularRouteFlag) {
            checkIfEndPointInAirspaceFlag = checkIfStartPointInAirspaceFlag;
            if (!checkIfEndPointInAirspaceFlag)
                airspaceMatchingEndTokenInterpretations.addAll(airspaceMatchingStartTokenInterpretations);
        } else {
            if (!(dbFoundEndTokenInterpretations == null || dbFoundEndTokenInterpretations.isEmpty())) {
                for (int i = 0; i < dbFoundEndTokenInterpretations.size(); i++) {
                    if (!(dbFoundEndTokenInterpretations.get(i).tokenPrimaryType == TokenPrimaryType.SIGNIFICANT_POINT
                            || dbFoundEndTokenInterpretations.get(i).tokenPrimaryType == TokenPrimaryType.ADHP)) {
                        dbFoundEndTokenInterpretations.remove(i);
                        i--;
                    }
                }
            }

            if (dbFoundEndTokenInterpretations == null || dbFoundEndTokenInterpretations.isEmpty()) {
                listOfErrorMessages.add("Ending '" + endIdent
                        + "' route fix, given to 'getSurveillanceRouteWKT' method, is either of incorrect format OR is not found in the database.");
                logger.debug("Ending '" + endIdent
                        + "' route fix, given to 'getSurveillanceRouteWKT' method, is either of incorrect format OR is not found in the database.");
                return null;
            }

            else {
                for (int i = 0; i < dbFoundEndTokenInterpretations.size(); i++) {
                    if (dbFoundEndTokenInterpretations.get(i).tokenPrimaryType == TokenPrimaryType.SIGNIFICANT_POINT
                            && dbFoundEndTokenInterpretations.get(i).significantPointPrimType
                                    .ordinal() >= SignificantPointPrimaryType.DEGREESONLY.ordinal()) {

                        if (dbFoundEndTokenInterpretations.get(i).significantPointPrimType
                                .ordinal() <= SignificantPointPrimaryType.DEGREES_MINUTES_SECONDS.ordinal()
                                || (dbFoundEndTokenInterpretations.get(i).significantPointSecType
                                        .ordinal() >= SignificantPointSecondaryType.DEGREESONLY.ordinal()
                                        && dbFoundEndTokenInterpretations.get(i).significantPointSecType
                                                .ordinal() <= SignificantPointSecondaryType.DEGREESANDMINUTES
                                                        .ordinal())) {

                            checkIfEndPointInAirspaceFlag = false;
                            airspaceMatchingEndTokenInterpretations.add(dbFoundEndTokenInterpretations.get(i));
                        }
                    }

                    else if (dbFoundEndTokenInterpretations.get(i).tokenPrimaryType == TokenPrimaryType.ADHP) {
                        checkIfEndPointInAirspaceFlag = false;
                        airspaceMatchingEndTokenInterpretations.add(dbFoundEndTokenInterpretations.get(i));
                    }
                }
            }
        }

        if (tools == null)
            tools = new Tools(connection);

        LinkedList<String> tempWKTs;
        LinkedList<String> normalisedAirspaceWKTs = new LinkedList<String>();

        if (checkIfStartPointInAirspaceFlag || checkIfEndPointInAirspaceFlag
                || (circularRouteFlag && !fullRoute.isEmpty())) {
            for (String airspaceWKT : airspaceWKTs) {
                tempWKTs = tools.getListOfSingleNormalGeometries(airspaceWKT, 3);

                for (String tempWKT : tempWKTs) {
                    normalisedAirspaceWKTs.add(tempWKT);
                }

                tempWKTs.clear();
                tempWKTs = null;
            }

            if (normalisedAirspaceWKTs.size() == 0) {
                listOfErrorMessages.add(
                        "No Airspaces [POLYGON-imbedding WKTs] were transfered to 'getSurveillanceRouteWKT' function.");
                logger.debug(
                        "No Airspaces [POLYGON-imbedding WKTs] were transfered to 'getSurveillanceRouteWKT' function.");
                return null;
            }
        }

        if (checkIfStartPointInAirspaceFlag) {
            for (int i = 0; i < dbFoundStartTokenInterpretations.size(); i++) {
                for (String airspaceWKT : normalisedAirspaceWKTs) {
                    if (tools.doIntersect(dbFoundStartTokenInterpretations.get(i).getRoutePointWKT(), airspaceWKT)) {
                        airspaceMatchingStartTokenInterpretations.add(dbFoundStartTokenInterpretations.remove(i));
                        i--;
                        break;
                    }
                }
            }

            if (!dbFoundStartTokenInterpretations.isEmpty() && bufferRadiusInMeters > 0.0) {
                for (int i = 0; i < dbFoundStartTokenInterpretations.size(); i++) {
                    for (String airspaceWKT : normalisedAirspaceWKTs) {
                        if (tools.isPointWithinPolygon(dbFoundStartTokenInterpretations.get(i).getRoutePointWKT(),
                                airspaceWKT, bufferRadiusInMeters)) {
                            airspaceMatchingStartTokenInterpretations.add(dbFoundStartTokenInterpretations.remove(i));
                            i--;
                            break;
                        }
                    }
                }
            }

            if (airspaceMatchingStartTokenInterpretations.isEmpty()) {
                if (bufferRadiusInMeters <= 0.0) {
                    listOfErrorMessages.add(
                            "None of Starting route fixes, found in DB based on ident [transfered to 'getSurveillanceRouteWKT'], intersects target Airspace(s). Try to set airspace buffer value to expand searched area.");
                    logger.debug(
                            "None of Starting route fixes, found in DB based on ident [transfered to 'getSurveillanceRouteWKT'], intersects target Airspace(s). Try to set airspace buffer value to expand searched area.");
                } else {
                    listOfErrorMessages.add(
                            "None of Starting route fixes, found in DB based on ident [transfered to 'getSurveillanceRouteWKT'], intersects target Airspace(s). Try to increase airspace buffer value to expand searched area.");
                    logger.debug(
                            "None of Starting route fixes, found in DB based on ident [transfered to 'getSurveillanceRouteWKT'], intersects target Airspace(s). Try to increase airspace buffer value to expand searched area.");
                }
                return null;
            }
        }
        dbFoundStartTokenInterpretations.clear();
        dbFoundStartTokenInterpretations = null;

        if (checkIfEndPointInAirspaceFlag) {
            if (circularRouteFlag) {
                airspaceMatchingEndTokenInterpretations.addAll(airspaceMatchingStartTokenInterpretations);
            } else {
                for (int i = 0; i < dbFoundEndTokenInterpretations.size(); i++) {
                    for (String airspaceWKT : normalisedAirspaceWKTs) {
                        if (tools.doIntersect(dbFoundEndTokenInterpretations.get(i).getRoutePointWKT(), airspaceWKT)) {
                            airspaceMatchingEndTokenInterpretations.add(dbFoundEndTokenInterpretations.remove(i));
                            i--;
                            break;
                        }
                    }
                }

                if (!dbFoundEndTokenInterpretations.isEmpty() && bufferRadiusInMeters > 0.0) {
                    for (int i = 0; i < dbFoundEndTokenInterpretations.size(); i++) {
                        for (String airspaceWKT : normalisedAirspaceWKTs) {
                            if (tools.isPointWithinPolygon(dbFoundEndTokenInterpretations.get(i).getRoutePointWKT(),
                                    airspaceWKT, bufferRadiusInMeters)) {
                                airspaceMatchingEndTokenInterpretations.add(dbFoundEndTokenInterpretations.remove(i));
                                i--;
                                break;
                            }
                        }
                    }
                }

                if (airspaceMatchingEndTokenInterpretations.isEmpty()) {
                    if (bufferRadiusInMeters <= 0.0) {
                        listOfErrorMessages.add(
                                "None of Ending route fixes, found in DB based on ident [transfered to 'getSurveillanceRouteWKT'], intersects target Airspace(s). Try to set airspace buffer value to expand searched area.");
                        logger.debug(
                                "None of Ending route fixes, found in DB based on ident [transfered to 'getSurveillanceRouteWKT'], intersects target Airspace(s). Try to set airspace buffer value to expand searched area.");
                    } else {
                        listOfErrorMessages.add(
                                "None of Ending route fixes, found in DB based on ident [transfered to 'getSurveillanceRouteWKT'], intersects target Airspace(s). Try to increase airspace buffer value to expand searched area.");
                        logger.debug(
                                "None of Ending route fixes, found in DB based on ident [transfered to 'getSurveillanceRouteWKT'], intersects target Airspace(s). Try to increase airspace buffer value to expand searched area.");
                    }
                    return null;
                }
            }
        }
        dbFoundEndTokenInterpretations.clear();
        dbFoundEndTokenInterpretations = null;

        if (fullRoute.isEmpty()) {
            LinkedList<RouteScenario> listOfRouteScenarios = new LinkedList<RouteScenario>();

//			if (psDistance == null) psDistance = connection.prepareStatement("SELECT ST_Distance(?::geography, ?::geography)");

            for (TokenInterpretation startTokenInterpretation : airspaceMatchingStartTokenInterpretations) {
                TokenPrimaryType startPointType = startTokenInterpretation.tokenPrimaryType;
                startTokenInterpretation.tokenPrimaryType = TokenPrimaryType.ADHP;

                for (TokenInterpretation endTokenInterpretation : airspaceMatchingEndTokenInterpretations) {
                    RouteScenario tempRouteScenario = new RouteScenario(connection, 2);

                    TokenPrimaryType endPointType = endTokenInterpretation.tokenPrimaryType;

                    endTokenInterpretation.tokenPrimaryType = TokenPrimaryType.ADHP;

                    tempRouteScenario.addOrigin(startTokenInterpretation);
                    tempRouteScenario.addDestination(endTokenInterpretation);

                    endTokenInterpretation.tokenPrimaryType = endPointType;

                    listOfRouteScenarios.add(tempRouteScenario);
                }

                startTokenInterpretation.tokenPrimaryType = startPointType;
            }

            for (RouteScenario routeScenario : listOfRouteScenarios)
                routeScenario.completeScenario();

            Collections.sort(listOfRouteScenarios, new RouteScenarioComp());

            lastFoundRouteScenario = listOfRouteScenarios.getFirst();
            listOfRouteScenarios.clear();
            listOfRouteScenarios = null;

            ArrayList<TokenInterpretation> listOfTokenInterpretations = lastFoundRouteScenario.getRouteInterpretation();
            lastFoundRouteStructure = new ArrayList<RoutePoint>(2);
            String routeWKT = "LINESTRING(";

            for (TokenInterpretation tokenInterpretation : listOfTokenInterpretations) {
                lastFoundRouteStructure.add(new RoutePoint(tokenInterpretation, timeStamp));
                routeWKT = routeWKT.concat(tokenInterpretation.getLongitude().toString() + " "
                        + tokenInterpretation.getLatitude().toString() + ",");
            }
            routeWKT = routeWKT.substring(0, routeWKT.length() - 1) + ")";

            return routeWKT;
        } else if (circularRouteFlag) {
            tokenArray = fullRoute.split("\\s+");
            tokenList = new LinkedList<String>(Arrays.asList(tokenArray));
            tokenListSize = tokenList.size();

            LinkedList<RouteScenario> listOfRouteScenarios = new LinkedList<RouteScenario>();
            LinkedList<RouteScenario> tempRouteScenarios = new LinkedList<RouteScenario>();
            ArrayList<TokenPrimaryType> startPointTypes = new ArrayList<TokenPrimaryType>();

//			if (psDistance == null) psDistance = connection.prepareStatement("SELECT ST_Distance(?::geography, ?::geography)");

            for (TokenInterpretation startTokenInterpretation : airspaceMatchingStartTokenInterpretations) {
                startPointTypes.add(startTokenInterpretation.tokenPrimaryType);
                startTokenInterpretation.tokenPrimaryType = TokenPrimaryType.ADHP;

                RouteScenario baseRouteScenario = new RouteScenario(connection, tokenListSize + 2);

                baseRouteScenario.addOrigin(startTokenInterpretation);
                baseRouteScenario.addDestination(startTokenInterpretation);

                listOfRouteScenarios.add(baseRouteScenario);
            }

            for (String token : tokenList) {
                ArrayList<TokenInterpretation> tokenInterpretations = tokenParser.getTokenInterpretations(token,
                        timeStamp, TokenInterpretationMode.POINT, true);

                if (tokenInterpretations == null || tokenInterpretations.isEmpty())
                    continue;

                for (RouteScenario tempScenario : listOfRouteScenarios) {
                    tempRouteScenarios.addAll(tempScenario.addIntermediatePointInterp(tokenInterpretations,
                            normalisedAirspaceWKTs, bufferRadiusInMeters));
                }

                if (tempRouteScenarios.isEmpty())
                    continue;

                listOfRouteScenarios.clear();
                listOfRouteScenarios.addAll(tempRouteScenarios);
                tempRouteScenarios.clear();
            }

            for (RouteScenario routeScenario : listOfRouteScenarios)
                routeScenario.completeScenario();

            Collections.sort(listOfRouteScenarios, new RouteScenarioComp());

            lastFoundRouteScenario = listOfRouteScenarios.getFirst();
            listOfRouteScenarios.clear();
            listOfRouteScenarios = null;
            tempRouteScenarios = null;

            ArrayList<TokenInterpretation> listOfTokenInterpretations = lastFoundRouteScenario.getRouteInterpretation();

            if (startPointTypes.size() == 1)
                listOfTokenInterpretations.get(0).tokenPrimaryType = startPointTypes.get(0);

            else {
                for (int i = 0; i < airspaceMatchingStartTokenInterpretations.size(); i++) {
                    if (airspaceMatchingStartTokenInterpretations.get(i) == listOfTokenInterpretations.get(0)) {
                        listOfTokenInterpretations.get(0).tokenPrimaryType = startPointTypes.get(i);
//						listOfTokenInterpretations.get(listOfTokenInterpretations.size() - 1).tokenPrimaryType = startPointTypes.get(i);
                        break;
                    }
                }
            }

            lastFoundRouteStructure = new ArrayList<RoutePoint>(listOfTokenInterpretations.size());
            String routeWKT = "LINESTRING(";

            for (TokenInterpretation tokenInterpretation : listOfTokenInterpretations) {
                lastFoundRouteStructure.add(new RoutePoint(tokenInterpretation, timeStamp));
                routeWKT = routeWKT.concat(tokenInterpretation.getLongitude().toString() + " "
                        + tokenInterpretation.getLatitude().toString() + ",");
            }
            routeWKT = routeWKT.substring(0, routeWKT.length() - 1) + ")";

            return routeWKT;
        } else {
            LinkedList<RouteFinder> listOfRouteFinders = new LinkedList<RouteFinder>();

            for (TokenInterpretation startTokenInterpretation : airspaceMatchingStartTokenInterpretations) {
                for (TokenInterpretation endTokenInterpretation : airspaceMatchingEndTokenInterpretations) {
                    RouteFinder tempRouteFinder = new RouteFinder(connection, startTokenInterpretation, fullRoute,
                            endTokenInterpretation, timeStamp);
                    tempRouteFinder.getRouteLineWKT();
                    listOfRouteFinders.add(tempRouteFinder);
                }
            }

            if (listOfRouteFinders.size() == 1) {
                makeMeCloneOf(listOfRouteFinders.getFirst());
                listOfRouteFinders = null;
                return listOfRouteWKTs.getFirst();
            } else {
                LinkedList<RouteScenario> listOfRouteScenarios = new LinkedList<RouteScenario>();

                for (RouteFinder tempRouteFinder : listOfRouteFinders)
                    listOfRouteScenarios.add(tempRouteFinder.lastFoundRouteScenario);

                Collections.sort(listOfRouteScenarios, new RouteScenarioComp());

                RouteScenario bestRouteScenario = listOfRouteScenarios.getFirst();

                for (RouteFinder tempRouteFinder : listOfRouteFinders) {
                    if (tempRouteFinder.lastFoundRouteScenario == bestRouteScenario)
                        makeMeCloneOf(tempRouteFinder);
                    else
                        tempRouteFinder.close();
                }

                listOfRouteFinders = null;
                return listOfRouteWKTs.getFirst();
            }
        }
    }

    private boolean areSyntacticallyConnectable(TokenInterpretation firstInterpretation,
            MapOfTokenInterpretations secondMap) {
        for (int j = 0; j < secondMap.numOfFoundPoints; j++) {
            if (RouteScenario.isCorrectSyntax(firstInterpretation,
                    secondMap.listOfTokenInterpretation.get(secondMap.mapOfFoundPoints[j])))
                return true;
        }
        for (int j = 0; j < secondMap.numOfNotFoundPoints; j++) {
            if (RouteScenario.isCorrectSyntax(firstInterpretation,
                    secondMap.listOfTokenInterpretation.get(secondMap.mapOfNotFoundPoints[j])))
                return true;
        }
        for (int j = 0; j < secondMap.numOfATSroutes; j++) {
            if (RouteScenario.isCorrectSyntax(firstInterpretation,
                    secondMap.listOfTokenInterpretation.get(secondMap.mapOfATSroutes[j])))
                return true;
        }
        for (int j = 0; j < secondMap.numOfKeyWords; j++) {
            if (RouteScenario.isCorrectSyntax(firstInterpretation,
                    secondMap.listOfTokenInterpretation.get(secondMap.mapOfKeyWords[j])))
                return true;
        }

        return false;
    }

    private boolean areSyntacticallyConnectable(MapOfTokenInterpretations firstMap,
            TokenInterpretation secondInterpretation) {
        for (int i = 0; i < firstMap.numOfFoundPoints; i++) {
            if (RouteScenario.isCorrectSyntax(firstMap.listOfTokenInterpretation.get(firstMap.mapOfFoundPoints[i]),
                    secondInterpretation))
                return true;
        }
        for (int i = 0; i < firstMap.numOfNotFoundPoints; i++) {
            if (RouteScenario.isCorrectSyntax(firstMap.listOfTokenInterpretation.get(firstMap.mapOfNotFoundPoints[i]),
                    secondInterpretation))
                return true;
        }
        for (int i = 0; i < firstMap.numOfATSroutes; i++) {
            if (RouteScenario.isCorrectSyntax(firstMap.listOfTokenInterpretation.get(firstMap.mapOfATSroutes[i]),
                    secondInterpretation))
                return true;
        }
        for (int i = 0; i < firstMap.numOfKeyWords; i++) {
            if (RouteScenario.isCorrectSyntax(firstMap.listOfTokenInterpretation.get(firstMap.mapOfKeyWords[i]),
                    secondInterpretation))
                return true;
        }

        return false;
    }

    private boolean areSyntacticallyConnectable(MapOfTokenInterpretations firstMap,
            MapOfTokenInterpretations secondMap) {
        for (int i = 0; i < firstMap.numOfFoundPoints; i++) {
            for (int j = 0; j < secondMap.numOfFoundPoints; j++) {
                if (RouteScenario.isCorrectSyntax(firstMap.listOfTokenInterpretation.get(firstMap.mapOfFoundPoints[i]),
                        secondMap.listOfTokenInterpretation.get(secondMap.mapOfFoundPoints[j])))
                    return true;
            }
            for (int j = 0; j < secondMap.numOfNotFoundPoints; j++) {
                if (RouteScenario.isCorrectSyntax(firstMap.listOfTokenInterpretation.get(firstMap.mapOfFoundPoints[i]),
                        secondMap.listOfTokenInterpretation.get(secondMap.mapOfNotFoundPoints[j])))
                    return true;
            }
            for (int j = 0; j < secondMap.numOfATSroutes; j++) {
                if (RouteScenario.isCorrectSyntax(firstMap.listOfTokenInterpretation.get(firstMap.mapOfFoundPoints[i]),
                        secondMap.listOfTokenInterpretation.get(secondMap.mapOfATSroutes[j])))
                    return true;
            }
            for (int j = 0; j < secondMap.numOfKeyWords; j++) {
                if (RouteScenario.isCorrectSyntax(firstMap.listOfTokenInterpretation.get(firstMap.mapOfFoundPoints[i]),
                        secondMap.listOfTokenInterpretation.get(secondMap.mapOfKeyWords[j])))
                    return true;
            }
        }
        for (int i = 0; i < firstMap.numOfNotFoundPoints; i++) {
            for (int j = 0; j < secondMap.numOfFoundPoints; j++) {
                if (RouteScenario.isCorrectSyntax(
                        firstMap.listOfTokenInterpretation.get(firstMap.mapOfNotFoundPoints[i]),
                        secondMap.listOfTokenInterpretation.get(secondMap.mapOfFoundPoints[j])))
                    return true;
            }
            for (int j = 0; j < secondMap.numOfNotFoundPoints; j++) {
                if (RouteScenario.isCorrectSyntax(
                        firstMap.listOfTokenInterpretation.get(firstMap.mapOfNotFoundPoints[i]),
                        secondMap.listOfTokenInterpretation.get(secondMap.mapOfNotFoundPoints[j])))
                    return true;
            }
            for (int j = 0; j < secondMap.numOfATSroutes; j++) {
                if (RouteScenario.isCorrectSyntax(
                        firstMap.listOfTokenInterpretation.get(firstMap.mapOfNotFoundPoints[i]),
                        secondMap.listOfTokenInterpretation.get(secondMap.mapOfATSroutes[j])))
                    return true;
            }
            for (int j = 0; j < secondMap.numOfKeyWords; j++) {
                if (RouteScenario.isCorrectSyntax(
                        firstMap.listOfTokenInterpretation.get(firstMap.mapOfNotFoundPoints[i]),
                        secondMap.listOfTokenInterpretation.get(secondMap.mapOfKeyWords[j])))
                    return true;
            }
        }
        for (int i = 0; i < firstMap.numOfATSroutes; i++) {
            for (int j = 0; j < secondMap.numOfFoundPoints; j++) {
                if (RouteScenario.isCorrectSyntax(firstMap.listOfTokenInterpretation.get(firstMap.mapOfATSroutes[i]),
                        secondMap.listOfTokenInterpretation.get(secondMap.mapOfFoundPoints[j])))
                    return true;
            }
            for (int j = 0; j < secondMap.numOfNotFoundPoints; j++) {
                if (RouteScenario.isCorrectSyntax(firstMap.listOfTokenInterpretation.get(firstMap.mapOfATSroutes[i]),
                        secondMap.listOfTokenInterpretation.get(secondMap.mapOfNotFoundPoints[j])))
                    return true;
            }
            for (int j = 0; j < secondMap.numOfATSroutes; j++) {
                if (RouteScenario.isCorrectSyntax(firstMap.listOfTokenInterpretation.get(firstMap.mapOfATSroutes[i]),
                        secondMap.listOfTokenInterpretation.get(secondMap.mapOfATSroutes[j])))
                    return true;
            }
            for (int j = 0; j < secondMap.numOfKeyWords; j++) {
                if (RouteScenario.isCorrectSyntax(firstMap.listOfTokenInterpretation.get(firstMap.mapOfATSroutes[i]),
                        secondMap.listOfTokenInterpretation.get(secondMap.mapOfKeyWords[j])))
                    return true;
            }
        }
        for (int i = 0; i < firstMap.numOfKeyWords; i++) {
            for (int j = 0; j < secondMap.numOfFoundPoints; j++) {
                if (RouteScenario.isCorrectSyntax(firstMap.listOfTokenInterpretation.get(firstMap.mapOfKeyWords[i]),
                        secondMap.listOfTokenInterpretation.get(secondMap.mapOfFoundPoints[j])))
                    return true;
            }
            for (int j = 0; j < secondMap.numOfNotFoundPoints; j++) {
                if (RouteScenario.isCorrectSyntax(firstMap.listOfTokenInterpretation.get(firstMap.mapOfKeyWords[i]),
                        secondMap.listOfTokenInterpretation.get(secondMap.mapOfNotFoundPoints[j])))
                    return true;
            }
            for (int j = 0; j < secondMap.numOfATSroutes; j++) {
                if (RouteScenario.isCorrectSyntax(firstMap.listOfTokenInterpretation.get(firstMap.mapOfKeyWords[i]),
                        secondMap.listOfTokenInterpretation.get(secondMap.mapOfATSroutes[j])))
                    return true;
            }
            for (int j = 0; j < secondMap.numOfKeyWords; j++) {
                if (RouteScenario.isCorrectSyntax(firstMap.listOfTokenInterpretation.get(firstMap.mapOfKeyWords[i]),
                        secondMap.listOfTokenInterpretation.get(secondMap.mapOfKeyWords[j])))
                    return true;
            }
        }

        return false;
    }

    LinkedList<String> getNotFoundNavaids() {

        LinkedList<String> outputList = new LinkedList<String>();

        if (lastFoundRouteScenario != null) {
            ArrayList<TokenInterpretation> tokenInterpretations = lastFoundRouteScenario.getRouteInterpretation();

            for (TokenInterpretation tokenInterpretation : tokenInterpretations) {

                if (tokenInterpretation.tokenPrimaryType.ordinal() >= TokenPrimaryType.SIGNIFICANT_POINT_NOTFOUND
                        .ordinal()
                        && !(tokenInterpretation.parsedFeatureDesignator == null
                                || tokenInterpretation.parsedFeatureDesignator.isEmpty())) {

                    if (tokenInterpretation.significantPointPrimType
                            .ordinal() <= SignificantPointPrimaryType.BEARINGDISTANCE.ordinal())
                        outputList.add(tokenInterpretation.parsedFeatureDesignator);

                    else if (tokenInterpretation.significantPointPrimType
                            .ordinal() >= SignificantPointPrimaryType.SPEEDORLEVELCHANGE.ordinal()) {

                        if (tokenInterpretation.significantPointSecType
                                .ordinal() <= SignificantPointSecondaryType.BEARINGDISTANCE.ordinal())
                            outputList.add(tokenInterpretation.parsedFeatureDesignator);

                    }
                }
            }
        }

        return outputList;
    }

    LinkedList<String> getNotFoundWaypoints() {

        LinkedList<String> outputList = new LinkedList<String>();

        if (lastFoundRouteScenario != null) {
            ArrayList<TokenInterpretation> tokenInterpretations = lastFoundRouteScenario.getRouteInterpretation();

            for (TokenInterpretation tokenInterpretation : tokenInterpretations) {

                if (tokenInterpretation.tokenPrimaryType.ordinal() >= TokenPrimaryType.SIGNIFICANT_POINT_NOTFOUND
                        .ordinal()
                        && !(tokenInterpretation.parsedFeatureDesignator == null
                                || tokenInterpretation.parsedFeatureDesignator.isEmpty())) {

                    if (tokenInterpretation.significantPointPrimType
                            .ordinal() == SignificantPointPrimaryType.DESIGNATEDPOINT.ordinal())
                        outputList.add(tokenInterpretation.parsedFeatureDesignator);

                    else if (tokenInterpretation.significantPointPrimType
                            .ordinal() >= SignificantPointPrimaryType.SPEEDORLEVELCHANGE.ordinal()) {

                        if (tokenInterpretation.significantPointSecType
                                .ordinal() == SignificantPointSecondaryType.DESIGNATEDPOINT.ordinal())
                            outputList.add(tokenInterpretation.parsedFeatureDesignator);

                    }
                }
            }
        }

        return outputList;
    }

    LinkedList<String> getNotFoundATSroutes() {

        LinkedList<String> outputList = new LinkedList<String>();

        if (lastFoundRouteScenario != null) {
            ArrayList<TokenInterpretation> tokenInterpretations = lastFoundRouteScenario.getRouteInterpretation();

            for (TokenInterpretation tokenInterpretation : tokenInterpretations) {

                if (tokenInterpretation.tokenPrimaryType.ordinal() == TokenPrimaryType.ATS_ROUTE_NOTFOUND.ordinal()
                        && !(tokenInterpretation.parsedFeatureDesignator == null
                                || tokenInterpretation.parsedFeatureDesignator.isEmpty()))

                    outputList.add(tokenInterpretation.parsedFeatureDesignator);
            }
        }

        return outputList;
    }

    LinkedList<String> getDisconnectedATSroutes() {

        LinkedList<String> outputList = new LinkedList<String>();

        if (lastFoundRouteScenario != null) {
            ArrayList<TokenInterpretation> tokenInterpretations = lastFoundRouteScenario.getRouteInterpretation();
            TokenInterpretation tokenInterpretation;

            for (int i = 1; i < (tokenInterpretations.size() - 1); i++) {

                tokenInterpretation = tokenInterpretations.get(i);

                if (tokenInterpretation.tokenPrimaryType.ordinal() == TokenPrimaryType.ATS_ROUTE.ordinal()
                        && tokenInterpretation.atsRoutePortion == null) {

                    if (tokenInterpretations.get(i - 1).tokenPrimaryType.ordinal() < TokenPrimaryType.INCORRECT_FORMAT
                            .ordinal()
                            && tokenInterpretations.get(i - 1).tokenPrimaryType.ordinal() > TokenPrimaryType.KEY_WORD
                                    .ordinal()
                            && tokenInterpretations.get(i + 1).tokenPrimaryType
                                    .ordinal() < TokenPrimaryType.INCORRECT_FORMAT.ordinal()
                            && tokenInterpretations.get(i + 1).tokenPrimaryType.ordinal() > TokenPrimaryType.KEY_WORD
                                    .ordinal()) {

                        outputList.add(tokenInterpretation.tokenValue + " - Cannot connect '"
                                + tokenInterpretations.get(i - 1).tokenValue + "' and '"
                                + tokenInterpretations.get(i + 1).tokenValue + "'");
                    }
                }
            }
        }

        return outputList;
    }

    LinkedList<String> getNotFoundADHPs() {

        LinkedList<String> outputList = new LinkedList<String>();

        if (lastFoundRouteScenario != null) {
            ArrayList<TokenInterpretation> tokenInterpretations = lastFoundRouteScenario.getRouteInterpretation();

            for (TokenInterpretation tokenInterpretation : tokenInterpretations) {

                if (tokenInterpretation.tokenPrimaryType.ordinal() == TokenPrimaryType.ADHP_NOTFOUND.ordinal()
                        && !(tokenInterpretation.parsedFeatureDesignator == null
                                || tokenInterpretation.parsedFeatureDesignator.isEmpty()))

                    outputList.add(tokenInterpretation.parsedFeatureDesignator);
            }
        }

        if (lastNotFoundADHP != null) {
            if (!(lastNotFoundADHP.parsedFeatureDesignator == null
                    || lastNotFoundADHP.parsedFeatureDesignator.isEmpty()))
                outputList.add(lastNotFoundADHP.parsedFeatureDesignator);
            else if (!(lastNotFoundADHP.tokenValue == null || lastNotFoundADHP.tokenValue.isEmpty()))
                outputList.add(lastNotFoundADHP.tokenValue);
        }

        return outputList;
    }

    LinkedList<String> getIncorrectTokens() {

        LinkedList<String> outputList = new LinkedList<String>();

        if (lastFoundRouteScenario != null) {
            ArrayList<TokenInterpretation> tokenInterpretations = lastFoundRouteScenario.getRouteInterpretation();

            for (TokenInterpretation tokenInterpretation : tokenInterpretations) {

                if (tokenInterpretation.tokenPrimaryType.ordinal() == TokenPrimaryType.INCORRECT_FORMAT.ordinal()
                        && !(tokenInterpretation.tokenValue == null || tokenInterpretation.tokenValue.isEmpty()))

                    outputList.add(tokenInterpretation.tokenValue);
            }
        }

        return outputList;
    }
}
