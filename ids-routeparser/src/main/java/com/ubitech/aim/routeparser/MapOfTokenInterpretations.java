package com.ubitech.aim.routeparser;

import java.sql.SQLException;
import java.util.ArrayList;

class MapOfTokenInterpretations {
    ArrayList<TokenInterpretation> listOfTokenInterpretation;
    int numOfInterpretations = 0;
    int mapOfFoundPoints[];
    double distToOriginAD[];
    double distToDestinAD[];
    int numOfFoundPoints = 0;
    int mapOfNotFoundPoints[];
    int numOfNotFoundPoints = 0;
    int mapOfATSroutes[];
    int numOfATSroutes = 0;
    int mapOfKeyWords[];
    int numOfKeyWords = 0;
    double ratio = 0;
    boolean pointConnectedToRoute = false;

    String tokenValue = "";
    int parsedPointIndex = -1;

    MapOfTokenInterpretations() {
    }

    MapOfTokenInterpretations(ArrayList<TokenInterpretation> listOfTokenInterpretation, String tokenValue,
            TokenInterpretation originADtokenInterpretation, TokenInterpretation destinADtokenInterpretation,
            double maxRatio) throws SQLException {
        this.listOfTokenInterpretation = listOfTokenInterpretation;

        numOfInterpretations = listOfTokenInterpretation.size();
        this.tokenValue = tokenValue;
        if (numOfInterpretations > 0) {
            mapOfFoundPoints = new int[numOfInterpretations];
            distToOriginAD = new double[numOfInterpretations];
            distToDestinAD = new double[numOfInterpretations];
            mapOfNotFoundPoints = new int[numOfInterpretations];
            mapOfATSroutes = new int[numOfInterpretations];
            mapOfKeyWords = new int[numOfInterpretations];
        } else {
            return;
        }

        TokenInterpretation tokenInterpretation = listOfTokenInterpretation.get(0);
        if (tokenInterpretation.tokenPrimaryType == TokenPrimaryType.INCORRECT_FORMAT) {
            numOfInterpretations = 0;
            return;
        }

        ratio = maxRatio;
//		ResultSet rs;

        for (int i = 0; i < numOfInterpretations; i++) {
            tokenInterpretation = listOfTokenInterpretation.get(i);
            tokenInterpretation.parsedPointIndex = parsedPointIndex;

            int tokenPrimaryTypeOrdinal = tokenInterpretation.tokenPrimaryType.ordinal();
            if ((tokenPrimaryTypeOrdinal >= TokenPrimaryType.SIGNIFICANT_POINT.ordinal()
                    && tokenPrimaryTypeOrdinal <= TokenPrimaryType.STAR_ROUTE.ordinal())
                    || tokenPrimaryTypeOrdinal == TokenPrimaryType.ADHP.ordinal()) {
                if (maxRatio >= 1) {
//					psDistance.setString (1, tokenInterpretation.getRoutePointWKT());
//					psDistance.setString (2, originADtokenInterpretation.getRoutePointWKT());
//					rs = psDistance.executeQuery();

//					if (rs.next()) distToOriginAD[i] = (double)rs.getFloat(1);
//					else distToOriginAD[i] = 1000000000000000.;
                    distToOriginAD[i] = Geometry
                            .distanceInMeters(tokenInterpretation.getPoint(), originADtokenInterpretation.getPoint())
                            .doubleValue();

//					psDistance.setString (2, destinADtokenInterpretation.getRoutePointWKT());
//					rs = psDistance.executeQuery();

//					if (rs.next()) distToDestinAD[i] = (double)rs.getFloat(1);
//					else distToDestinAD[i] = 1000000000000000.;
                    distToDestinAD[i] = Geometry
                            .distanceInMeters(tokenInterpretation.getPoint(), destinADtokenInterpretation.getPoint())
                            .doubleValue();
                }

                mapOfFoundPoints[numOfFoundPoints] = i;
                numOfFoundPoints = numOfFoundPoints + 1;
            } else if (tokenPrimaryTypeOrdinal >= TokenPrimaryType.SIGNIFICANT_POINT_NOTFOUND.ordinal()
                    && tokenPrimaryTypeOrdinal <= TokenPrimaryType.STAR_ROUTE_NOTFOUND.ordinal()
                    || tokenPrimaryTypeOrdinal == TokenPrimaryType.ADHP_NOTFOUND.ordinal()) {
                mapOfNotFoundPoints[numOfNotFoundPoints] = i;
                numOfNotFoundPoints = numOfNotFoundPoints + 1;
            } else if (tokenPrimaryTypeOrdinal == TokenPrimaryType.ATS_ROUTE.ordinal()
                    || tokenPrimaryTypeOrdinal == TokenPrimaryType.ATS_ROUTE_NOTFOUND.ordinal()) {
                mapOfATSroutes[numOfATSroutes] = i;
                numOfATSroutes = numOfATSroutes + 1;
            } else if (tokenPrimaryTypeOrdinal == TokenPrimaryType.KEY_WORD.ordinal()) {
                mapOfKeyWords[numOfKeyWords] = i;
                numOfKeyWords = numOfKeyWords + 1;
            }
        }
    }

    void filterOffPointsLocatedTooFar(double directPathLength, boolean circularRoute) {
        filterOffPointsLocatedTooFar(directPathLength, circularRoute, null);
    }

    void filterOffPointsLocatedTooFar(double directPathLength, boolean circularRoute, String originDestinADHP) {
        TokenInterpretation tokenInterpretation;

        if (numOfFoundPoints > 0) {

            if (ratio >= 1) {
//				A token, which have been recognized as a Significant Point and found in the database, is supposed to have a valid location.
//				At this step the location is verified by comparing (ORIGIN->SIGNIFICANT_POINT->DESTINATION) and (ORIGIN->DESTINATION * ratio) distances.
//				Incorrectly located Significant Point should be rejected (should not be used for composing a list of potentially correct route scenarios). 
                TokenInterpretation tempTokenInterp;
                int signifPointPrimTypeOrdinal;
//				int signifPointSecTypeOrdinal;
                boolean degreePoint;

                int numOfValidPoints = 0;
                int tempNumOfFoundPoints = numOfFoundPoints;
                int tempMapOfFoundPoints[] = mapOfFoundPoints.clone();

                for (int i = 0; i < tempNumOfFoundPoints; i++) {

                    degreePoint = false;
                    tempTokenInterp = listOfTokenInterpretation.get(tempMapOfFoundPoints[i]);

                    if (tempTokenInterp.tokenPrimaryType != TokenPrimaryType.ADHP) {
                        signifPointPrimTypeOrdinal = tempTokenInterp.significantPointPrimType.ordinal();
//						signifPointSecTypeOrdinal = tempTokenInterp.significantPointSecType.ordinal();

                        if (tempTokenInterp.tokenPrimaryType == TokenPrimaryType.SIGNIFICANT_POINT
                                && ((signifPointPrimTypeOrdinal >= SignificantPointPrimaryType.DEGREESONLY.ordinal()
                                        && signifPointPrimTypeOrdinal <= SignificantPointPrimaryType.DEGREES_MINUTES_SECONDS
                                                .ordinal())))
                            degreePoint = true;
                    }

                    if (circularRoute && (degreePoint
                            || (tempTokenInterp.tokenPrimaryType == TokenPrimaryType.ADHP && originDestinADHP != null
                                    && originDestinADHP != "" && tempTokenInterp.parsedFeatureDesignator.substring(0, 1)
                                            .equals(originDestinADHP.substring(0, 1))))) {
                    }

                    else if ((distToOriginAD[tempMapOfFoundPoints[i]]
                            + distToDestinAD[tempMapOfFoundPoints[i]]) > (directPathLength * ratio)) {
                        if (numOfValidPoints < (tempNumOfFoundPoints - 1)) {
                            for (int k = numOfValidPoints; k < (tempNumOfFoundPoints - 1); k++) {
                                mapOfFoundPoints[k] = mapOfFoundPoints[k + 1];
                            }
                        }
                        numOfFoundPoints = numOfFoundPoints - 1;
                        numOfInterpretations = numOfInterpretations - 1;
                    } else {
                        numOfValidPoints = numOfValidPoints + 1;
                    }
                }
            }
//			If a token (matching to a format of a significant point or SID/STAR route) was first found in the database 
//			but later rejected as located too far from origin and destination ADHPs, 
//			the algorithm should remember the fact that the token has been recognized as a Point.
//			For that, if both numOfFoundPoints AND numOfNotFoundPoints are equal to zero,
//			we increment numOfNotFoundPoints to 1 and create mapping to a virtually NOTFOUND TokenInterpretation.
            if (numOfFoundPoints == 0 && numOfNotFoundPoints == 0) {
                mapOfNotFoundPoints[0] = mapOfFoundPoints[0];
                numOfNotFoundPoints = 1;
                numOfInterpretations = numOfInterpretations + 1;
                tokenInterpretation = listOfTokenInterpretation.get(mapOfNotFoundPoints[0]);
                if (tokenInterpretation.tokenPrimaryType == TokenPrimaryType.SIGNIFICANT_POINT)
                    tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SIGNIFICANT_POINT_NOTFOUND;
                else if (tokenInterpretation.tokenPrimaryType == TokenPrimaryType.SID_ROUTE)
                    tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SID_ROUTE_NOTFOUND;
                else if (tokenInterpretation.tokenPrimaryType == TokenPrimaryType.STAR_ROUTE)
                    tokenInterpretation.tokenPrimaryType = TokenPrimaryType.STAR_ROUTE_NOTFOUND;
                else if (tokenInterpretation.tokenPrimaryType == TokenPrimaryType.ADHP)
                    tokenInterpretation.tokenPrimaryType = TokenPrimaryType.ADHP_NOTFOUND;
                tokenInterpretation.setRoutePointWKT(null);
            }
        }
    }

    void setPointIndex(int index) {
        this.parsedPointIndex = index;

        for (int i = 0; i < numOfFoundPoints; i++) {
            listOfTokenInterpretation.get(mapOfFoundPoints[i]).parsedPointIndex = index;
        }
        for (int i = 0; i < numOfNotFoundPoints; i++) {
            listOfTokenInterpretation.get(mapOfNotFoundPoints[i]).parsedPointIndex = index;
        }
    }

    MapOfTokenInterpretations getCopy() {
        MapOfTokenInterpretations myCopy = new MapOfTokenInterpretations();

        myCopy.listOfTokenInterpretation = new ArrayList<TokenInterpretation>(this.listOfTokenInterpretation.size());
        for (TokenInterpretation tokenInterpretation : this.listOfTokenInterpretation) {
            myCopy.listOfTokenInterpretation.add(tokenInterpretation.getClone2());
        }
        myCopy.numOfInterpretations = this.numOfInterpretations;
        myCopy.mapOfFoundPoints = (int[]) this.mapOfFoundPoints.clone();
        myCopy.distToOriginAD = (double[]) this.distToOriginAD.clone();
        myCopy.distToDestinAD = (double[]) this.distToDestinAD.clone();
        myCopy.numOfFoundPoints = this.numOfFoundPoints;
        myCopy.mapOfNotFoundPoints = (int[]) this.mapOfNotFoundPoints.clone();
        myCopy.numOfNotFoundPoints = this.numOfNotFoundPoints;
        myCopy.mapOfATSroutes = (int[]) this.mapOfATSroutes.clone();
        myCopy.numOfATSroutes = this.numOfATSroutes;
        myCopy.mapOfKeyWords = (int[]) this.mapOfKeyWords.clone();
        myCopy.numOfKeyWords = this.numOfKeyWords;
        myCopy.ratio = this.ratio;
        myCopy.parsedPointIndex = this.parsedPointIndex;
        myCopy.tokenValue = this.tokenValue;

        return myCopy;
    }
}
