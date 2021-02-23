package com.ubitech.aim.routeparser;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

class RouteScenario {
    private Connection connection = null;
//	private PreparedStatement psDistance = null;
//	private PreparedStatement psAzimuth = null;
    private ArrayList<TokenInterpretation> routeInterpretation;
    private int capacity;
    private boolean completionStatus = false;
    private boolean geoFeasibilityStatus = true;
    int numOfFoundPoints = 0;
    private int numOfNotFoundFeatures = 0;
    private boolean syntaxCorrectnessStatus = true;
    private double directPathLength = 0.0;
    private double routeLength = 0.0;
    private Point latestFoundPoint = null;
    private double latestFoundSegmentAzimuth = -1.0;

    ArrayList<String> listOfErrorMessages = new ArrayList<String>();

    /*
     * RouteScenario(Connection connection, int capacity) throws SQLException {
     * this.connection = connection; if (connection != null) psDistance =
     * connection.prepareStatement("SELECT ST_Distance(?::geography, ?::geography)"
     * ); this.capacity = capacity; routeInterpretation = new
     * ArrayList<TokenInterpretation>(capacity); }
     */
    RouteScenario(Connection connection, int capacity) throws SQLException {
        this.connection = connection;
//		this.psDistance = prepStDistance;
//		this.psAzimuth = prepStAzimuth;
        this.capacity = capacity;
        routeInterpretation = new ArrayList<TokenInterpretation>(capacity);
    }

    RouteScenario(Connection connection, int capacity, ArrayList<TokenInterpretation> routeInterpretation,
            int numOfFoundPoints, int numOfNotFoundFeatures, boolean syntaxCorrectnessStatus, double directPathLength,
            double routeLength, Point latestFoundPoint, double latestFoundSegmentAzimuth,
            ArrayList<String> listOfErrorMessages) {
        this.connection = connection;
//		this.psDistance = prepStDistance;
//		this.psAzimuth = prepStAzimuth;
        this.capacity = capacity;
        this.routeInterpretation = new ArrayList<TokenInterpretation>(capacity);
        this.routeInterpretation.addAll(routeInterpretation);
        this.numOfFoundPoints = numOfFoundPoints;
        this.numOfNotFoundFeatures = numOfNotFoundFeatures;
        this.syntaxCorrectnessStatus = syntaxCorrectnessStatus;
        this.directPathLength = directPathLength;
        this.routeLength = routeLength;
        this.latestFoundPoint = new Point(latestFoundPoint);
        this.latestFoundSegmentAzimuth = latestFoundSegmentAzimuth;
        this.listOfErrorMessages.addAll(listOfErrorMessages);
    }

    static boolean isCorrectSyntax(TokenInterpretation prevToken, TokenInterpretation nextToken) {
        if (nextToken.tokenPrimaryType == TokenPrimaryType.INCORRECT_FORMAT)
            return true;
        switch (prevToken.tokenPrimaryType) {
        case INCORRECT_FORMAT:
            return true;
        case SIGNIFICANT_POINT:
        case SIGNIFICANT_POINT_NOTFOUND:
            switch (nextToken.tokenPrimaryType) {
            case SID_ROUTE:
            case SID_ROUTE_NOTFOUND:
//				SID route can follow only after an origin ADHP.
                return false;
            case KEY_WORD:
                switch (nextToken.keyWord) {
                case DCT:
//					DCT key word is OK, if follows after any kind of significant point.
                case STAR:
                    return true;
                case IFR:
                case VFR:
//					According to ICAO Doc 4444 (APPENDIX 2. FLIGHT PLAN, ITEM 15: ROUTE, (4) Change of flight rules):
//					IFR or VFR (key word) can follow after any kind of significant point except for (5) Cruise climb.
                    switch (prevToken.significantPointPrimType) {
                    case CRUISECLIMB:
                        return false;
                    default:
                        return true;
                    }
                default:
//					SID-key word as an abbreviation of 'Standard Instrument Departure' can follow only after the origin ADHP.
                    return false;
                }
            default:
//				ADHP, SIGNIFICANT_POINT, ATS_ROUTE and STAR_ROUTE are OK, if follow after any kind of significant point.
                return true;
            }
        case ATS_ROUTE:
        case ATS_ROUTE_NOTFOUND:
            switch (nextToken.tokenPrimaryType) {
            case ADHP:
            case ADHP_NOTFOUND:
//				ADHP is OK, if follows after an ATS_ROUTE.
            case SIGNIFICANT_POINT:
            case SIGNIFICANT_POINT_NOTFOUND:
//				SIGNIFICANT_POINT is OK, if follows after an ATS_ROUTE.
            case STAR_ROUTE:
            case STAR_ROUTE_NOTFOUND:
//				STAR_ROUTE is OK, if follows after an ATS_ROUTE.
                return true;
            default:
//				KEY_WORD, SID_ROUTE and ATS_ROUTE cannot follow after an ATS_ROUTE.
                return false;
            }
        case ADHP:
        case ADHP_NOTFOUND:
            switch (nextToken.tokenPrimaryType) {
            case SIGNIFICANT_POINT:
            case SIGNIFICANT_POINT_NOTFOUND:
//				SIGNIFICANT_POINT is OK, if follows after an origin ADHP.
            case ATS_ROUTE:
            case ATS_ROUTE_NOTFOUND:
//				ATS route is OK, if follows after an origin ADHP.
            case SID_ROUTE:
            case SID_ROUTE_NOTFOUND:
//				SID route is OK, if follows after an origin ADHP.
                return true;
            case KEY_WORD:
                switch (nextToken.keyWord) {
                case DCT:
                case SID:
//					DCT and SID key word are OK, if follow after an origin ADHP.
                    return true;
                default:
//					IFR or VFR-key words can follow only after a significant point.
//					STAR-key word as an abbreviation of 'Standard ARRIVAL ROUTE' cannot follow only after an origin ADHP.
                    return true;
                }
//			case ADHP:
//			case ADHP_NOTFOUND:
//				Though ADHP and STAR_ROUTE cannot follow after an origin ADHP,
//				it was requested to let the algorithm to recognize ADHPs as intermediate points as well.
//				return true;
//			case STAR_ROUTE:
//			case STAR_ROUTE_NOTFOUND:
//				Though ADHP and STAR_ROUTE cannot follow after an origin ADHP,
//				it was requested to let the algorithm to recognize ADHPs as intermediate points as well.
//				return true;
            default:
//				ADHP and STAR_ROUTE cannot follow after an origin ADHP.
                return false;
            }
        case SID_ROUTE:
        case SID_ROUTE_NOTFOUND:
            switch (nextToken.tokenPrimaryType) {
            case SIGNIFICANT_POINT:
            case SIGNIFICANT_POINT_NOTFOUND:
//				SIGNIFICANT_POINT is OK, if follows after a SID_ROUTE.
            case ATS_ROUTE:
            case ATS_ROUTE_NOTFOUND:
//				ATS route is OK, if follows after a SID_ROUTE.
            case STAR_ROUTE:
            case STAR_ROUTE_NOTFOUND:
//				STAR route is OK, if follows after a SID_ROUTE.
                return true;
            case KEY_WORD:
                switch (nextToken.keyWord) {
                case DCT:
//					DCT key word is OK, if follows after a SID_ROUTE.
                    return true;
                default:
//					IFR or VFR-key words can follow only after a significant point.
//					SID and STAR-key words cannot follow after a SID_ROUTE.
                    return false;
                }
//			case ADHP:
//			case ADHP_NOTFOUND:
//				Though ADHP cannot follow after a SID_ROUTE,
//				it was requested to let the algorithm to recognize ADHPs as intermediate points as well.
//				return true;
            default:
//				destination ADHP and SID_ROUTE cannot follow after a SID_ROUTE.
                return false;
            }
        case STAR_ROUTE:
        case STAR_ROUTE_NOTFOUND:
            switch (nextToken.tokenPrimaryType) {
            case ADHP:
            case ADHP_NOTFOUND:
//				destination ADHP is OK, if follows after a STAR_ROUTE.
                return true;
            default:
//				SIGNIFICANT_POINT, ATS_ROUTE, KEY_WORD, SID_ROUTE and STAR_ROUTE cannot follow after a STAR_ROUTE.
                return false;
            }
        case KEY_WORD:
            switch (prevToken.keyWord) {
            case DCT:
                switch (nextToken.tokenPrimaryType) {
                case ADHP:
                case ADHP_NOTFOUND:
                case SIGNIFICANT_POINT:
                case SIGNIFICANT_POINT_NOTFOUND:
                case STAR_ROUTE:
                case STAR_ROUTE_NOTFOUND:
                    return true;
                default:
//					ATS_ROUTE, KEY_WORD and SID_ROUTE cannot follow after a KEY_WORD of DCT type.
                    return false;
                }
            case IFR:
            case VFR:
                switch (nextToken.tokenPrimaryType) {
                case ADHP:
                case ADHP_NOTFOUND:
                case SIGNIFICANT_POINT:
                case SIGNIFICANT_POINT_NOTFOUND:
                case ATS_ROUTE:
                case ATS_ROUTE_NOTFOUND:
                case STAR_ROUTE:
                case STAR_ROUTE_NOTFOUND:
                    return true;
                case KEY_WORD:
                    switch (nextToken.keyWord) {
                    case DCT:
//						DCT key word is OK, if follows after a KEY_WORD of IFR or VFR type.
                        return true;
                    default:
//						Key words other than DCT cannot follow after a KEY_WORD of IFR or VFR type.
                        return false;
                    }
                default:
//					SID_ROUTE cannot follow after a KEY_WORD of IFR or VFR type.
                    return false;
                }
            case SID:
                switch (nextToken.tokenPrimaryType) {
                case SIGNIFICANT_POINT:
                case SIGNIFICANT_POINT_NOTFOUND:
                    return true;
                default:
//					ADHP, KEY_WORD, ATS_ROUTE, SID_ROUTE and STAR_ROUTE cannot follow after a KEY_WORD of SID type.
                    return false;
                }
            case STAR:
                switch (nextToken.tokenPrimaryType) {
                case ADHP:
                case ADHP_NOTFOUND:
                    return true;
                default:
//					SIGNIFICANT_POINT, KEY_WORD, ATS_ROUTE, SID_ROUTE and STAR_ROUTE cannot follow after a KEY_WORD of STAR type.
                    return false;
                }
            default:
                return false;
            }
        default:
            return false;
        }
    }

    public RouteScenario getClone() {
        RouteScenario myClone = new RouteScenario(connection, capacity, routeInterpretation, numOfFoundPoints,
                numOfNotFoundFeatures, syntaxCorrectnessStatus, directPathLength, routeLength, latestFoundPoint,
                latestFoundSegmentAzimuth, listOfErrorMessages);
        return myClone;
    }

    void setConnection(Connection connection) {
        this.connection = connection;
    }

    boolean getCompletionStatus() {
        return completionStatus;
    }

    boolean getGeoFeasibilityStatus() {
        return geoFeasibilityStatus;
    }

    int getNumOfFoundPoints() {
        return numOfFoundPoints;
    }

    int getNumOfNotFoundFeatures() {
        return numOfNotFoundFeatures;
    }

    boolean getSyntaxCorrectnessStatus() {
        return syntaxCorrectnessStatus;
    }

    double getDirectPathLength() {
        return directPathLength;
    }

    double getRouteLength() {
        return routeLength;
    }

    ArrayList<TokenInterpretation> getRouteInterpretation() {
        return routeInterpretation;
    }

    boolean addOrigin(TokenInterpretation tokenInterpretation) {
//		Completion status needs to be set to false, since a new tokenInterpretation requires verification/recalculation of the route length
        completionStatus = false;
        if (tokenInterpretation.tokenPrimaryType != TokenPrimaryType.ADHP)
            return false;
        routeInterpretation.add(0, tokenInterpretation);
        latestFoundPoint = tokenInterpretation.getPoint();
        return true;
    }

    LinkedList<RouteScenario> addIntermediate(MapOfTokenInterpretations mapOfTokenInterps, boolean isLast,
            double maxRatio, double maxCourseChangeRad, boolean addFoundPointsOnly, AtomicInteger maxNumOfFoundPoints,
            boolean completeSearch, int presumedNumOfSegments) throws SQLException {
//		Completion status needs to be set to false, since a new tokenInterpretation requires verification/recalculation of the route length
        completionStatus = false;
        maxNumOfFoundPoints.set(numOfFoundPoints);
        LinkedList<RouteScenario> outputScenarios = new LinkedList<RouteScenario>();
        if (!syntaxCorrectnessStatus)
            return outputScenarios;
        if (routeInterpretation.isEmpty())
            return outputScenarios;
        if (routeInterpretation.get(0).tokenPrimaryType != TokenPrimaryType.ADHP)
            return outputScenarios;
        int routeListSize = routeInterpretation.size();
        if (routeListSize < 2 || routeInterpretation.get(routeListSize - 1).tokenPrimaryType != TokenPrimaryType.ADHP)
            return outputScenarios;

        ArrayList<TokenInterpretation> listOfTokenInterpretation = mapOfTokenInterps.listOfTokenInterpretation;
        TokenInterpretation tokenInterpretation;
        ArrayList<TokenInterpretation> newRouteInterpretation;
        RouteScenario newRouteScenario;

        if (mapOfTokenInterps.numOfInterpretations <= 0) {
            if (!addFoundPointsOnly) {
                newRouteInterpretation = new ArrayList<TokenInterpretation>();
                newRouteInterpretation.addAll(routeInterpretation);
                newRouteInterpretation.add(routeInterpretation.size() - 1,
                        mapOfTokenInterps.listOfTokenInterpretation.get(0));
                newRouteScenario = new RouteScenario(connection, capacity, newRouteInterpretation, numOfFoundPoints,
                        numOfNotFoundFeatures + 1, syntaxCorrectnessStatus, directPathLength, routeLength,
                        latestFoundPoint, latestFoundSegmentAzimuth, listOfErrorMessages);

                outputScenarios.add(newRouteScenario);
            }
        } else {
            if (mapOfTokenInterps.numOfFoundPoints > 0) {
//				ResultSet rs;
                boolean degreePoint = false;
                boolean noOneFeasiblePointFound = true;
                double newSegmentLength;
                double newRouteLength;
                double newSegmentAzimuth;
                double newCourseChangeRad;
                for (int i = 0; i < mapOfTokenInterps.numOfFoundPoints; i++) {
//					A token, recognized as a Significant Point and found in the database, is supposed to have a valid location.
//					At this step the location validity is checked by comparing a sum (routeLength plus distance to destination ADHP) with the longest allowed route length.
//					Incorrectly located Significant Point should be rejected (should not be used for composing a list of potentially correct route scenarios).
//					'(mapOfTokenInterps.distToDestinAD[i] + routeLength) > (directPathLength * maxRatio)' condition does not take into account the segment connecting the last found route point with the Significant Point currently being checked. 
//					The criteria specified above is applicable for normal routes only and should be ignored for ring-type routes (the routes, for which 'maxRatio' value is agreed to be invalid - less than '1').
//					'maxRatio >= 1 &&' part tigers the checking procedure for normal routes only. For ring-type routes the previous condition (involving maxRatio) will be automatically avoided. 
                    if (mapOfTokenInterps.pointConnectedToRoute)
                        tokenInterpretation = listOfTokenInterpretation.get(mapOfTokenInterps.mapOfFoundPoints[i]);
                    else {
                        if (maxRatio >= 1 && (mapOfTokenInterps.distToDestinAD[mapOfTokenInterps.mapOfFoundPoints[i]]
                                + routeLength) > (directPathLength * maxRatio))
                            continue;

                        tokenInterpretation = listOfTokenInterpretation.get(mapOfTokenInterps.mapOfFoundPoints[i]);
                        if (tokenInterpretation.tokenPrimaryType == TokenPrimaryType.ADHP
                                || tokenInterpretation.tokenPrimaryType == TokenPrimaryType.SIGNIFICANT_POINT
                                        && (tokenInterpretation.significantPointPrimType == SignificantPointPrimaryType.DEGREESONLY
                                                || tokenInterpretation.significantPointPrimType == SignificantPointPrimaryType.DEGREESANDMINUTES
                                                || tokenInterpretation.significantPointPrimType == SignificantPointPrimaryType.DEGREES_MINUTES_SECONDS
                                                || tokenInterpretation.significantPointSecType == SignificantPointSecondaryType.DEGREESONLY
                                                || tokenInterpretation.significantPointSecType == SignificantPointSecondaryType.DEGREESANDMINUTES))
                            degreePoint = true;
//						The destination AD is supposed to be added before any of the intermediate entities and will be kept the last item of the 'routeInterpretation' ArrayList.
//						To verify the syntax correctness of the currently iterated instance of 'tokenInterpretation', we need to retrieve 'routeListSize - 2' (last but one) item of the 'routeInterpretation'.
                        if (!isCorrectSyntax(routeInterpretation.get(routeListSize - 2), tokenInterpretation))
                            continue;
                    }

//					psDistance.setString(1, latestFoundPoint);
//					psDistance.setString(2, tokenInterpretation.getRoutePointWKT());
//					rs = psDistance.executeQuery();

//					if (rs.next()) {
//						newSegmentLength = (double)rs.getFloat(1);
                    newSegmentLength = Geometry.distanceInMeters(latestFoundPoint, tokenInterpretation.getPoint())
                            .doubleValue();
                    newRouteLength = routeLength + newSegmentLength;

//						'(newRouteLength + mapOfTokenInterps.distToDestinAD[i]) <= (directPathLength * maxRatio)' condition NOW takes into account the segment connecting the last found route point with the Significant Point currently being checked. 
//						'maxRatio < 1 ||' part tigers the checking procedure for normal routes only. For ring-type routes the previous condition (involving maxRatio) will be automatically avoided (the route scenario is automatically added to outputScenarios without checking). 
                    if (maxRatio < 1) {
                        newRouteInterpretation = new ArrayList<TokenInterpretation>();
                        newRouteInterpretation.addAll(routeInterpretation);
                        newRouteInterpretation.add(routeInterpretation.size() - 1, tokenInterpretation);
                        newRouteScenario = new RouteScenario(connection, capacity, newRouteInterpretation,
                                numOfFoundPoints + 1, numOfNotFoundFeatures, syntaxCorrectnessStatus, directPathLength,
                                newRouteLength, tokenInterpretation.getPoint(), 0, listOfErrorMessages);

                        maxNumOfFoundPoints.set(numOfFoundPoints + 1);
                        outputScenarios.add(newRouteScenario);

                        noOneFeasiblePointFound = false;
                    } else if (mapOfTokenInterps.pointConnectedToRoute || (newRouteLength
                            + mapOfTokenInterps.distToDestinAD[mapOfTokenInterps.mapOfFoundPoints[i]]) <= (directPathLength
                                    * maxRatio)) {
//							psAzimuth.setString(1, latestFoundPoint);
//							psAzimuth.setString(2, tokenInterpretation.getRoutePointWKT());
//							rs = psAzimuth.executeQuery();

//							if (rs.next()) {
//								newSegmentAzimuth = (double)rs.getFloat(1);
                        if (newSegmentLength > 0.0d) {
                            newSegmentAzimuth = Geometry.azimuthInRad(latestFoundPoint, tokenInterpretation.getPoint())
                                    .doubleValue();
                            newCourseChangeRad = courseChangeRad(latestFoundSegmentAzimuth, newSegmentAzimuth);
                        } else {
                            newSegmentAzimuth = latestFoundSegmentAzimuth;
                            newCourseChangeRad = 0.0d;
                        }

                        if (mapOfTokenInterps.pointConnectedToRoute || latestFoundSegmentAzimuth < 0
                                || maxCourseChangeRad <= 0 || newCourseChangeRad <= maxCourseChangeRad) {
                            newRouteInterpretation = new ArrayList<TokenInterpretation>();
                            newRouteInterpretation.addAll(routeInterpretation);
                            newRouteInterpretation.add(routeInterpretation.size() - 1, tokenInterpretation);
                            newRouteScenario = new RouteScenario(connection, capacity, newRouteInterpretation,
                                    numOfFoundPoints + 1, numOfNotFoundFeatures, syntaxCorrectnessStatus,
                                    directPathLength, newRouteLength, tokenInterpretation.getPoint(), newSegmentAzimuth,
                                    listOfErrorMessages);

                            maxNumOfFoundPoints.set(numOfFoundPoints + 1);
                            outputScenarios.add(newRouteScenario);

                            if (mapOfTokenInterps.pointConnectedToRoute
                                    || (newSegmentLength < (directPathLength / presumedNumOfSegments)
                                            && newCourseChangeRad < (maxCourseChangeRad / 3)))
                                noOneFeasiblePointFound = false;
//									if (mapOfTokenInterps.pointConnectedToRoute || (newSegmentLength < (directPathLength / presumedNumOfSegments) && newCourseChangeRad < 0.5235987756)) noOneFeasiblePointFound = false;	//	0.5235987756 rad = 30 deg
                        }
//							}
                    }
//					}
                }

//				Suppose we have correctly recognized a point-type-feature based on a token format,  
//				but the database (due to incompleteness) stores only wrong duplicates of the true feature.
//				In that case the algorithm can be disorientated and even blocked to find any route (even an incorrect alternative),
//				as the "most suitable" feature-duplicate causes exceeding of allowable total length of the route (calculated based on 'ratio' parameter and direct distance).
//				The only way to avoid such a blocking is to duplicate scenarios for each recognized, found and currently accepted (in terms of route length) features
//				with a NOTFOUND tokenInterpretation->tokenPrimaryType value.
//				This makes scenes only if either all three conditions below are true
//					1)	mapOfTokenInterps.numOfNotFoundPoints == 0
//					2)	outputScenarios.isEmpty()
//					3)	the calling procedure does Not prescribe to addFoundPointsOnly
//				Or if outputScenarios.isEmpty() is not true, but:
//					2.a)	The function is launched with 'completeSearch == true' AND
//					2.b)	Neither the current token is the last one in the route (right before the destination aerodrome)
//					2.c)	Nor the token has DEGREESONLY, DEGREESANDMINUTES or DEGREES_MINUTES_SECONDS format (it is actually a feature-based token).
//				if (mapOfTokenInterps.numOfNotFoundPoints == 0 && (!isLast || outputScenarios.isEmpty()) && !addFoundPointsOnly) {
                if (mapOfTokenInterps.numOfNotFoundPoints == 0
                        && ((completeSearch || noOneFeasiblePointFound) && !(isLast || degreePoint)
                                || outputScenarios.isEmpty())
                        && !addFoundPointsOnly) {
                    for (int i = 0; i < mapOfTokenInterps.numOfFoundPoints; i++) {
                        if (!isCorrectSyntax(routeInterpretation.get(routeListSize - 2),
                                listOfTokenInterpretation.get(mapOfTokenInterps.mapOfFoundPoints[i])))
                            continue;
                        if (isLast && !isCorrectSyntax(
                                listOfTokenInterpretation.get(mapOfTokenInterps.mapOfFoundPoints[i]),
                                routeInterpretation.get(routeListSize - 1)))
                            continue;

                        tokenInterpretation = listOfTokenInterpretation.get(mapOfTokenInterps.mapOfFoundPoints[i])
                                .getClone();
                        if (tokenInterpretation.tokenPrimaryType == TokenPrimaryType.SIGNIFICANT_POINT)
                            tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SIGNIFICANT_POINT_NOTFOUND;
                        else if (tokenInterpretation.tokenPrimaryType == TokenPrimaryType.SID_ROUTE)
                            tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SID_ROUTE_NOTFOUND;
                        else if (tokenInterpretation.tokenPrimaryType == TokenPrimaryType.STAR_ROUTE)
                            tokenInterpretation.tokenPrimaryType = TokenPrimaryType.STAR_ROUTE_NOTFOUND;
                        else if (tokenInterpretation.tokenPrimaryType == TokenPrimaryType.ADHP)
                            tokenInterpretation.tokenPrimaryType = TokenPrimaryType.ADHP_NOTFOUND;
                        tokenInterpretation.setRoutePointWKT(null);

                        newRouteInterpretation = new ArrayList<TokenInterpretation>();
                        newRouteInterpretation.addAll(routeInterpretation);
                        newRouteInterpretation.add(routeInterpretation.size() - 1, tokenInterpretation);
                        newRouteScenario = new RouteScenario(connection, capacity, newRouteInterpretation,
                                numOfFoundPoints, numOfNotFoundFeatures + 1, syntaxCorrectnessStatus, directPathLength,
                                routeLength, latestFoundPoint, latestFoundSegmentAzimuth, listOfErrorMessages);

                        outputScenarios.add(newRouteScenario);
                        break;
                    }
                }
            }

            if (mapOfTokenInterps.numOfNotFoundPoints > 0 && !addFoundPointsOnly) {
                for (int i = 0; i < mapOfTokenInterps.numOfNotFoundPoints; i++) {
                    tokenInterpretation = listOfTokenInterpretation.get(mapOfTokenInterps.mapOfNotFoundPoints[i]);
//					The destination AD is supposed to be added before any of the intermediate entities and will be kept the last item of the 'routeInterpretation' ArrayList.
//					To verify the syntax correctness of the currently iterated instance of 'tokenInterpretation', we need to retrieve 'routeListSize - 2' (last but one) item of the 'routeInterpretation'.
                    if (!isCorrectSyntax(routeInterpretation.get(routeListSize - 2), tokenInterpretation))
                        continue;

                    newRouteInterpretation = new ArrayList<TokenInterpretation>();
                    newRouteInterpretation.addAll(routeInterpretation);
                    newRouteInterpretation.add(routeInterpretation.size() - 1, tokenInterpretation);
                    newRouteScenario = new RouteScenario(connection, capacity, newRouteInterpretation, numOfFoundPoints,
                            numOfNotFoundFeatures + 1, syntaxCorrectnessStatus, directPathLength, routeLength,
                            latestFoundPoint, latestFoundSegmentAzimuth, listOfErrorMessages);

                    outputScenarios.add(newRouteScenario);
                }
            }

            if (mapOfTokenInterps.numOfATSroutes > 0 && !addFoundPointsOnly) {
                for (int i = 0; i < mapOfTokenInterps.numOfATSroutes; i++) {
                    tokenInterpretation = listOfTokenInterpretation.get(mapOfTokenInterps.mapOfATSroutes[i]);
//					The destination AD is supposed to be added before any of the intermediate entities and will be kept the last item of the 'routeInterpretation' ArrayList.
//					To verify the syntax correctness of the currently iterated instance of 'tokenInterpretation', we need to retrieve 'routeListSize - 2' (last but one) item of the 'routeInterpretation'.
                    if (!isCorrectSyntax(routeInterpretation.get(routeListSize - 2), tokenInterpretation))
                        continue;

                    newRouteInterpretation = new ArrayList<TokenInterpretation>();
                    newRouteInterpretation.addAll(routeInterpretation);
                    newRouteInterpretation.add(routeInterpretation.size() - 1, tokenInterpretation);
                    if (tokenInterpretation.tokenPrimaryType == TokenPrimaryType.ATS_ROUTE)
                        newRouteScenario = new RouteScenario(connection, capacity, newRouteInterpretation,
                                numOfFoundPoints, numOfNotFoundFeatures, syntaxCorrectnessStatus, directPathLength,
                                routeLength, latestFoundPoint, latestFoundSegmentAzimuth, listOfErrorMessages);
                    else
                        newRouteScenario = new RouteScenario(connection, capacity, newRouteInterpretation,
                                numOfFoundPoints, numOfNotFoundFeatures + 1, syntaxCorrectnessStatus, directPathLength,
                                routeLength, latestFoundPoint, latestFoundSegmentAzimuth, listOfErrorMessages);
                    outputScenarios.add(newRouteScenario);
                }
            }

            if (mapOfTokenInterps.numOfKeyWords > 0) {
                for (int i = 0; i < mapOfTokenInterps.numOfKeyWords; i++) {
                    tokenInterpretation = listOfTokenInterpretation.get(mapOfTokenInterps.mapOfKeyWords[i]);
//					The destination AD is supposed to be added before any of the intermediate entities and will be kept the last item of the 'routeInterpretation' ArrayList.
//					To verify the syntax correctness of the currently iterated instance of 'tokenInterpretation', we need to retrieve 'routeListSize - 2' (last but one) item of the 'routeInterpretation'.
                    if (!isCorrectSyntax(routeInterpretation.get(routeListSize - 2), tokenInterpretation))
                        continue;

                    newRouteInterpretation = new ArrayList<TokenInterpretation>();
                    newRouteInterpretation.addAll(routeInterpretation);
                    newRouteInterpretation.add(routeInterpretation.size() - 1, tokenInterpretation);
                    newRouteScenario = new RouteScenario(connection, capacity, newRouteInterpretation,
                            numOfFoundPoints + 1, numOfNotFoundFeatures, syntaxCorrectnessStatus, directPathLength,
                            routeLength, latestFoundPoint, latestFoundSegmentAzimuth, listOfErrorMessages);

                    maxNumOfFoundPoints.set(numOfFoundPoints + 1);
                    outputScenarios.add(newRouteScenario);
                }
            }
        }
        return outputScenarios;
    }

    boolean addDestination(TokenInterpretation tokenInterpretation) throws SQLException {
//		Completion status needs to be set to false, since a new tokenInterpretation requires verification/recalculation of the route length
        completionStatus = false;
        if (!syntaxCorrectnessStatus)
            return false;
//		if (tokenInterpretation.tokenPrimaryType != TokenPrimaryType.ADHP) return false;
        int routeSize = routeInterpretation.size();
        if (routeSize > 1 && routeInterpretation.get(routeSize - 1).tokenPrimaryType == TokenPrimaryType.ADHP) {
            routeInterpretation.remove(routeSize - 1);
            routeSize = routeSize - 1;
        }
        routeInterpretation.add(tokenInterpretation);
        if (routeSize > 0 && routeInterpretation.get(0).tokenPrimaryType == TokenPrimaryType.ADHP) {
//			psDistance.setString(1, routeInterpretation.get(0).getRoutePointWKT());
//			psDistance.setString(2, tokenInterpretation.getRoutePointWKT());
//			ResultSet rs = psDistance.executeQuery();

//			if (rs.next()) directPathLength = (double)rs.getFloat(1);
//			else {
//				listOfErrorMessages.add("Could not read the result of SQL query based on ST_Distance in 'RouteScenario.addDestination'.");
//				return false;
//			}

            directPathLength = Geometry
                    .distanceInMeters(routeInterpretation.get(0).getPoint(), tokenInterpretation.getPoint())
                    .doubleValue();
        }
        return true;
    }

    LinkedList<RouteScenario> addIntermediatePointInterp(ArrayList<TokenInterpretation> tokenInterpretations,
            LinkedList<String> airspaceWKTs, double toleranceMeters) throws SQLException {

        LinkedList<RouteScenario> outputScenarios = new LinkedList<RouteScenario>();
        Tools tools = new Tools(connection);
//		ResultSet rs;

        if (tokenInterpretations == null || tokenInterpretations.isEmpty())
            return outputScenarios;

        for (TokenInterpretation tokenInterpretation : tokenInterpretations) {
            if (!(tokenInterpretation.tokenPrimaryType == TokenPrimaryType.SIGNIFICANT_POINT
                    || tokenInterpretation.tokenPrimaryType == TokenPrimaryType.ADHP))
                continue;

            boolean checkIfPointIsInAirspaceFlag = true;

            if (tokenInterpretation.tokenPrimaryType == TokenPrimaryType.SIGNIFICANT_POINT
                    && tokenInterpretation.significantPointPrimType.ordinal() >= SignificantPointPrimaryType.DEGREESONLY
                            .ordinal()) {

                if (tokenInterpretation.significantPointPrimType
                        .ordinal() <= SignificantPointPrimaryType.DEGREES_MINUTES_SECONDS.ordinal()
                        || (tokenInterpretation.significantPointSecType
                                .ordinal() >= SignificantPointSecondaryType.DEGREESONLY.ordinal()
                                && tokenInterpretation.significantPointSecType
                                        .ordinal() <= SignificantPointSecondaryType.DEGREESANDMINUTES.ordinal())) {

                    checkIfPointIsInAirspaceFlag = false;
                }
            }

            else if (tokenInterpretation.tokenPrimaryType == TokenPrimaryType.ADHP)
                checkIfPointIsInAirspaceFlag = false;

            if (checkIfPointIsInAirspaceFlag) {
                if (airspaceWKTs == null || airspaceWKTs.isEmpty())
                    continue;

                boolean pointIsInAirspaceFlag = false;

                for (String airspaceWKT : airspaceWKTs) {
                    if (tools.isPointWithinPolygon(tokenInterpretation.getRoutePointWKT(), airspaceWKT,
                            toleranceMeters)) {
                        pointIsInAirspaceFlag = true;
                        break;
                    }
                }

                if (!pointIsInAirspaceFlag)
                    continue;
            }

            double newSegmentLength;
            double newRouteLength;

//			psDistance.setString(1, latestFoundPointWKT);
//			psDistance.setString(2, tokenInterpretation.getRoutePointWKT());
//			rs = psDistance.executeQuery();

//			if (rs.next()) newSegmentLength = (double)rs.getFloat(1);
            newSegmentLength = Geometry
                    .distanceInMeters(routeInterpretation.get(routeInterpretation.size() - 2).longitude,
                            routeInterpretation.get(routeInterpretation.size() - 2).latitude,
                            tokenInterpretation.longitude, tokenInterpretation.latitude)
                    .doubleValue();
            newRouteLength = routeLength + newSegmentLength;

            ArrayList<TokenInterpretation> newRouteInterpretation = new ArrayList<TokenInterpretation>();
            newRouteInterpretation.addAll(routeInterpretation);
            newRouteInterpretation.add(routeInterpretation.size() - 1, tokenInterpretation);
            RouteScenario newRouteScenario = new RouteScenario(connection, capacity, newRouteInterpretation,
                    numOfFoundPoints + 1, numOfNotFoundFeatures, syntaxCorrectnessStatus, directPathLength,
                    newRouteLength, tokenInterpretation.getPoint(), 0, listOfErrorMessages);

            outputScenarios.add(newRouteScenario);
        }

        return outputScenarios;
    }

    boolean completeScenario(double maxRatio) throws SQLException {
        if (!syntaxCorrectnessStatus)
            return false;
        int listSize = routeInterpretation.size();
        if (listSize < 3)
            return false;
        if (!(routeInterpretation.get(0).tokenPrimaryType == TokenPrimaryType.ADHP
                && routeInterpretation.get(listSize - 1).tokenPrimaryType == TokenPrimaryType.ADHP))
            return false;

        if (!isCorrectSyntax(routeInterpretation.get(listSize - 2), routeInterpretation.get(listSize - 1))) {
            syntaxCorrectnessStatus = false;
            return false;
        }

//		psDistance.setString(1, latestFoundPoint);
//		psDistance.setString(2, routeInterpretation.get(listSize - 1).getRoutePointWKT());
//		ResultSet rs = psDistance.executeQuery();

        double lastSegmentLength = Geometry
                .distanceInMeters(latestFoundPoint, routeInterpretation.get(listSize - 1).getPoint()).doubleValue();

//		if (rs.next()) lastSegmentLength = (double)rs.getFloat(1);
//		else {
//			listOfErrorMessages.add("Could not read the result of SQL query based on ST_Distance in 'completeScenario'.");
//			lastSegmentLength = 1000000000000000.0;
//			return false;
//		}

        routeLength = routeLength + lastSegmentLength;

//		geoFeasibilityStatus can be checked for normal routes only.
//		For ring-type routes (when maxRatio is less than 1) geoFeasibilityStatus always gets true by default.
        if (maxRatio < 1 || routeLength <= (directPathLength * maxRatio))
            geoFeasibilityStatus = true;
        else
            geoFeasibilityStatus = false;

        completionStatus = true;
        return true;
    }

    boolean completeScenario() throws SQLException {
        if (!syntaxCorrectnessStatus)
            return false;
        int listSize = routeInterpretation.size();
        if (listSize < 3)
            return false;

        double lastSegmentLength = Geometry
                .distanceInMeters(routeInterpretation.get(routeInterpretation.size() - 2).longitude,
                        routeInterpretation.get(routeInterpretation.size() - 2).latitude,
                        routeInterpretation.get(routeInterpretation.size() - 1).longitude,
                        routeInterpretation.get(routeInterpretation.size() - 1).latitude)
                .doubleValue();

        routeLength = routeLength + lastSegmentLength;

        geoFeasibilityStatus = true;
        completionStatus = true;

        return true;
    }

    double courseChangeRad(double firstCourseRad, double secondCourseRad) {
        double courseChange = -1;

        if (firstCourseRad < 0)
            firstCourseRad = firstCourseRad + 2 * Math.PI;
        if (secondCourseRad < 0)
            secondCourseRad = secondCourseRad + 2 * Math.PI;

        courseChange = Math.max(firstCourseRad, secondCourseRad) - Math.min(firstCourseRad, secondCourseRad);

        if (courseChange > Math.PI)
            courseChange = 2 * Math.PI - courseChange;

        return courseChange;
    }

    void setRouteInterpretation(ArrayList<TokenInterpretation> routeInterpretation) {
        if (this.capacity >= routeInterpretation.size())
            this.routeInterpretation.addAll(routeInterpretation);
        else
            this.routeInterpretation = routeInterpretation;
    }
}
