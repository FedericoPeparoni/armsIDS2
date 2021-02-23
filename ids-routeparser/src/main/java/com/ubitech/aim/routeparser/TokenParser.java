package com.ubitech.aim.routeparser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TokenParser {
    Connection connection = null;
    DataRetriver dataRetriver;
    String tokenValue = null;
    Timestamp timeStamp = null;
    TokenInterpretationMode interpretationMode = null;
    boolean recognizeADHP = false;
    Point sidStarRefPoint = null;
    Double sidStarRadiusInMeters = null;

    PreparedStatement psExteriorRingWKT_GeoM = null;
    PreparedStatement psCentralPointWKT_GeoM = null;
    PreparedStatement psIntersectionWKT_GeoM = null;
//	PreparedStatement psExteriorRingWKT_GeoG = null;
//	PreparedStatement psIntersectionWKT_GeoG = null;

    private Matcher matcher = null;
    private Pattern patternPointWKT = null;
    private Pattern patternKeyWord = null;
    private Pattern patternATSroute = null;
    private Pattern patternSID_STAR = null;
    private Pattern patternNavaid = null;
    private Pattern patternADHP = null;
    private Pattern patternDesignatedPoint = null;
    private Pattern patternDegreesOnly = null;
    private Pattern patternBearingDistance = null;
    private Pattern patternDegreesMinutes = null;
    private Pattern patternDegreesMinutesSeconds = null;
    private Pattern pattern_GH_HHMM = null;
    private Pattern patternCruiseClimb = null;
    private Pattern patternCruiseClimbNavaid = null;
    private Pattern patternCruiseClimbDesignatedPoint = null;
    private Pattern patternCruiseClimbDegreesOnly = null;
    private Pattern patternCruiseClimbBearingDistance = null;
    private Pattern patternCruiseClimbDegreesMinutes = null;
    private Pattern patternChangeOfSpeedOrLevel = null;
    private Pattern patternChangeOfSpeedOrLevelNavaid = null;
    private Pattern patternChangeOfSpeedOrLevelDesignatedPoint = null;
    private Pattern patternChangeOfSpeedOrLevelDegreesOnly = null;
    private Pattern patternChangeOfSpeedOrLevelBearingDistance = null;
    private Pattern patternChangeOfSpeedOrLevelDegreesMinutes = null;

    TokenParser() {
    }

    TokenParser(Connection connection) throws SQLException {
        this.connection = connection;
        dataRetriver = new AeroDbDataRetriever(connection);
    }

    TokenParser(Connection connection, Timestamp timeStamp) throws SQLException {
        this.connection = connection;
        this.timeStamp = timeStamp;
        dataRetriver = new AeroDbDataRetriever(connection);
    }

    TokenParser(Connection connection, String tokenValue, Timestamp timeStamp,
            TokenInterpretationMode interpretationMode) throws SQLException {
        this.connection = connection;
        this.tokenValue = tokenValue;
        this.timeStamp = timeStamp;
        this.interpretationMode = interpretationMode;
        dataRetriver = new AeroDbDataRetriever(connection);
    }

    void close() throws SQLException {
        if (dataRetriver != null) {
            dataRetriver.close();
            dataRetriver = null;
        }
        if (psExteriorRingWKT_GeoM != null) {
            psExteriorRingWKT_GeoM.close();
            psExteriorRingWKT_GeoM = null;
        }
        if (psCentralPointWKT_GeoM != null) {
            psCentralPointWKT_GeoM.close();
            psCentralPointWKT_GeoM = null;
        }
        if (psIntersectionWKT_GeoM != null) {
            psIntersectionWKT_GeoM.close();
            psIntersectionWKT_GeoM = null;
        }
    }

    ArrayList<TokenInterpretation> getTokenInterpretations(String tokenValue, Timestamp timeStamp,
            TokenInterpretationMode interpretationMode, boolean recognizeADHP, Point sidstarRefPoint,
            double sidstarRadiusInMeters) throws SQLException {
        this.tokenValue = tokenValue;
        this.timeStamp = timeStamp;
        this.interpretationMode = interpretationMode;
        this.recognizeADHP = recognizeADHP;
        this.sidStarRefPoint = sidstarRefPoint;
        this.sidStarRadiusInMeters = new Double(sidstarRadiusInMeters);
        return getTokenInterpretations();
    }

    ArrayList<TokenInterpretation> getTokenInterpretations(String tokenValue, Timestamp timeStamp,
            TokenInterpretationMode interpretationMode, boolean recognizeADHP) throws SQLException {
        this.tokenValue = tokenValue;
        this.timeStamp = timeStamp;
        this.interpretationMode = interpretationMode;
        this.recognizeADHP = recognizeADHP;
        this.sidStarRefPoint = null;
        this.sidStarRadiusInMeters = null;
        return getTokenInterpretations();
    }

    ArrayList<TokenInterpretation> getTokenInterpretations(String tokenValue, Timestamp timeStamp,
            TokenInterpretationMode interpretationMode) throws SQLException {
        this.tokenValue = tokenValue;
        this.timeStamp = timeStamp;
        this.interpretationMode = interpretationMode;
        this.recognizeADHP = false;
        this.sidStarRefPoint = null;
        this.sidStarRadiusInMeters = null;
        return getTokenInterpretations();
    }

    ArrayList<TokenInterpretation> getTokenInterpretations() throws SQLException {
        ArrayList<TokenInterpretation> listOfTokenInterpretations = new ArrayList<TokenInterpretation>(15);
//		if (tokenValue == null || interpretationMode == null) {
        if (tokenValue == null || timeStamp == null || interpretationMode == null) {
//			An Exception must be thrown!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            return listOfTokenInterpretations; // This line must be deleted when the exception mechanism is specified
        }

        tokenValue = tokenValue.trim();
        int tokenLength = tokenValue.length();

        if (tokenLength < 2 || tokenLength > 29) {
            return listOfTokenInterpretations;
        }

        switch (tokenLength) {
        case 2:
        case 3:
        case 4:
//			The format of Navaid and Route (SID, STAR or ATS) designators can coincide when the number of characters is {2,4},
//			which leads to a risk of identification of a wrong feature (like a Navaid instead of a Route or vice-versa).
//			The 'getTokenInterpretations' searches all possible matches based on priorities chosen for each value of 'interpretationMode'.
//			There is a rather low probability that the format of a Key Word-type token
//			coincides with the format of any other token type (like Navaid designator).
//			We try to match regex for the Key Word first of all no matter which value 'interpretationMode' has.
//			if (tokenLength > 2 && tokenValue.matches("^(DCT|SID|STAR|IFR|VFR)$")) {
            if (tokenLength > 2) {
                if (patternKeyWord == null)
                    patternKeyWord = Pattern.compile("^(DCT|SID|STAR|IFR|VFR)$");
                matcher = patternKeyWord.matcher(tokenValue);

                if (matcher.matches()) {
                    TokenInterpretation tokenInterpretation = new TokenInterpretation();
                    tokenInterpretation.tokenPrimaryType = TokenPrimaryType.KEY_WORD;
                    tokenInterpretation.keyWord = KeyWord.valueOf(KeyWord.class, tokenValue);
//					tokenInterpretation.parsedFeatureDesignator = tokenInterpretation.keyWord.name();
                    listOfTokenInterpretations.add(tokenInterpretation);
//					From general point of view the Key Words, used in the full route string, are supposed not to coincide with existing Navaid designators.
//					However one NDB with 'IFR' designator exist in Morocco (according to aeronautical database published in the web).
//					The latter makes us assume that there is no limits on intersection of Navaid designators with the Key Words used in route expressions.
//					So we try to identify Navaid after the key word no matter which value 'interpretationMode' has:
                    if (tokenValue.equals("IFR"))
                        listOfTokenInterpretations.addAll(tryInterpretNavaid());
//					We return from 'getTokenInterpretations' since there is no other token type formats similar to the Key Word token type format.
                    return populateTokenValueAttribute(listOfTokenInterpretations);
                }
            }
//			There is zero probability that the 4-letter format of ADHP-type token
//			coincides with the format of any other token type (like SID, STAR or ATS Route designator, which must include numbers).
//			We try to match regex for the Designated Point first of all no matter which value 'interpretationMode' has.
            if (recognizeADHP && tokenLength == 4) {
                if (patternADHP == null)
                    patternADHP = Pattern.compile("^[A-Z]{4}$");
                matcher = patternADHP.matcher(tokenValue);
                if (matcher.matches()) {
                    listOfTokenInterpretations.addAll(tryInterpretADHP());
                    return listOfTokenInterpretations; // We return from 'getTokenInterpretations' since there is no
                                                       // other token types with the 5-letter format.
                }
            }
//			Otherwise (if regex for ADHP Point does not match token),
//			we do not return or break and try to identify SID, STAR and ATS Route at 'FIRST', 'LAST' and 'ANY_BUT_FIRSTORLAST' cases respectively.

            switch (interpretationMode) {
            case FIRST:
            case LAST:
//				For the First and Last tokens (as they come in the full route specification string) of {3,4} characters length,
//				we implement the following searching priority:
//				1) SID or STAR route; 2) ATS Route; 3) Navaid.

//				Here is the attempt to identify SID or STAR route in case of departure/arrival aerodrome
//				is connected to an ATS route via SID or STAR route respectively.
                if (tokenLength > 2)
                    listOfTokenInterpretations.addAll(tryInterpretSID_STAR());

//				We do not return or break and try to identify:
//				ATS Route at 'ANY_BUT_FIRSTORLAST' case (if departure/arrival aerodrome is located on the ATS route);
//				Navaid at 'POINT' case (for a pure Navaid name - without any SID/STAR postfixes).
            case ANY_BUT_FIRSTORLAST:
                // When the parsed token is not the first or last one in the full route string,
                // then it definitely cannot be SID or STAR route.
                // For that reason, when 'interpretationMode == ANY_BUT_FIRSTORLAST' and
                // 'tokenLength = 2 to 4', we search only Route and Navaid features:
                listOfTokenInterpretations.addAll(tryInterpretATSroute());

//				We do not return or break and try to identify Navaid at 'POINT' case.
            case POINT:
//				When the previously parsed token (like DCT) unambiguously prescribes us to search for point (like Navaid, WPT, etc.)
//				we can reduce chances to make wrong interpretations of the currently parsed token.
//				For that reason, when 'interpretationMode == POINT' and 'tokenLength = 2 to 3', we search only Navaid features:
                if (tokenLength < 4)
                    listOfTokenInterpretations.addAll(tryInterpretNavaid());
            }
            return listOfTokenInterpretations;

        case 5:
//			There is zero probability that the 5-letter format of a DesignatedPoint-type token
//			coincides with the format of any other token type (like SID, STAR or ATS Route designator, which must include numbers).
//			We try to match regex for the Designated Point first of all no matter which value 'interpretationMode' has.
            if (patternDesignatedPoint == null)
                patternDesignatedPoint = Pattern.compile("^[A-Z]{5}$");
            matcher = patternDesignatedPoint.matcher(tokenValue);
//			if (tokenValue.matches("^[A-Z]{5}$")) {
            if (matcher.matches()) {
                listOfTokenInterpretations.addAll(tryInterpretDesignatedPoint());
                return listOfTokenInterpretations; // We return from 'getTokenInterpretations' since there is no other
                                                   // token types with the 5-letter format.
            }
//			Otherwise (if regex for Designated Point does not match token),
//			we do not return or break and try to identify SID, STAR and ATS Route at 'FIRST', 'LAST' and 'ANY_BUT_FIRSTORLAST' cases respectively.
        case 6:
        case 7:
//			There is zero probability that the format of a 'Degrees only'-type token
//			coincides with the format of any other token type (like SID, STAR or ATS Route designator).
//			We try to match regex for the Significant point expressed in Degrees only no matter which value 'interpretationMode' has.
            if (tokenLength == 7) {
                if (patternDegreesOnly == null)
                    patternDegreesOnly = Pattern.compile("^([0-9]{2})([NnSs])([0-9]{3})([EeWw])$");
                matcher = patternDegreesOnly.matcher(tokenValue);
                if (matcher.matches()) {
                    listOfTokenInterpretations.addAll(tryInterpretDegreesOnlyPoint());
//					We return from 'getTokenInterpretations' since there is no other token types with 7-character '^[0-9]{2}[NnSs][0-9]{3}[EeWw]$' format.
                    return listOfTokenInterpretations;
                }
            }
            switch (interpretationMode) {
            case FIRST:
            case LAST:
//				The format of SID, STAR and ATS Routes' designators can coincide when the number of characters is {5,6},
//				which leads to a risk of identification of a wrong feature (like a Navaid [SID or STAR is based on] instead of a Route or vice-versa).
//				The 'getTokenInterpretations' searches all possible matches based on priorities chosen for each value of 'interpretationMode'.
//				For the First and Last tokens (as they come in the full route specification string) of {5,6} characters,
//				we implement the following searching priority:
//				1) SID or STAR route; 2) ATS Route.
//
//				Here is the attempt to identify SID or STAR route in case of departure/arrival aerodrome
//				is connected to an ATS route via SID or STAR route respectively.
                listOfTokenInterpretations.addAll(tryInterpretSID_STAR());

//				We do not return or break and try to identify ATS Route at 'ANY_BUT_FIRSTORLAST' case.
            case ANY_BUT_FIRSTORLAST:
//				We do not try to interpret SID or STAR Route for 'ANY_BUT_FIRSTORLAST' case.
//				Here is an attempt to identify ATS Route - in case of departure/arrival aerodrome is located on the ATS route.
                listOfTokenInterpretations.addAll(tryInterpretATSroute());
                return listOfTokenInterpretations;

            case POINT:
//				We do not try to interpret SID, STAR or ATS Route for 'POINT' case.
//				If either DesignatedPoint or 'Degrees only'-type token has matched to its regexp above,
//				then listOfTokenInterpretations contains at least one instance of 'TokenInterpretation' class and was already returned above (before 'switch' operator).
//				Otherwise, if neither DesignatedPoint nor 'Degrees only'-type token matched to its regexp,
//				then 'listOfTokenInterpretations' will be returned empty below (without instances of 'TokenInterpretation'),
//				which is the sign of that the token is not formated as a Point at all.
            }
            return listOfTokenInterpretations;

        case 8:
        case 9:
//			The range of 8 to 9 characters is covered by 'Bearing and Distance from a navigation aid' token type format.
//			The new unofficial 'point/[GH]HHMM' format can have from 8 to 20 character length.
            if (patternBearingDistance == null)
                patternBearingDistance = Pattern.compile("^([A-Z0-9]{2}|[A-Z]{3})([0-9]{3})([0-9]{3})$");
            matcher = patternBearingDistance.matcher(tokenValue);
            if (matcher.matches()) {
                listOfTokenInterpretations.addAll(tryInterpretBearingDistance());
                return listOfTokenInterpretations;
            }

        case 10:
        case 11:
            if (tokenLength == 11) {
                if (patternDegreesMinutes == null)
                    patternDegreesMinutes = Pattern
                            .compile("^([0-9]{2})([0-9]{2})([NnSs])([0-9]{3})([0-9]{2})([EeWw])$");
                matcher = patternDegreesMinutes.matcher(tokenValue);
                if (matcher.matches()) {
                    listOfTokenInterpretations.addAll(tryInterpretDegreesMinutesPoint());
//					We return from 'getTokenInterpretations' since there is no other token types with 11-character '^[0-9]{4}[NnSs][0-9]{5}[EeWw])$' format.
                    return listOfTokenInterpretations;
                }
            }
        case 12:
        case 13:
        case 14:
        case 15:
            if (tokenLength == 15) {
                if (patternDegreesMinutesSeconds == null)
                    patternDegreesMinutesSeconds = Pattern
                            .compile("^([0-9]{2})([0-9]{2})([0-9]{2})([NnSs])([0-9]{3})([0-9]{2})([0-9]{2})([EeWw])$");
                matcher = patternDegreesMinutesSeconds.matcher(tokenValue);
                if (matcher.matches()) {
                    listOfTokenInterpretations.addAll(tryInterpretDegreesMinutesSecondsPoint());
//					We return from 'getTokenInterpretations' since there is no other token types with 15-character '^([0-9]{2})([0-9]{2})([0-9]{2})([NnSs])([0-9]{3})([0-9]{2})([0-9]{2})([EeWw])$' format.
                    return listOfTokenInterpretations;
                }
            }
        case 16:
        case 17:
        case 18:
        case 19:
        case 20:
        case 21:
            if (tokenLength > 7) {
                if (pattern_GH_HHMM == null)
                    pattern_GH_HHMM = Pattern.compile("^([A-Z0-9]{2,15})/[GH][0-9]{4}$");
                matcher = pattern_GH_HHMM.matcher(tokenValue);
                if (matcher.matches()) {
                    listOfTokenInterpretations.addAll(tryInterpretPoint(matcher.group(1)));
                    return listOfTokenInterpretations;
                }
            }
        case 22:
//			There is zero probability that the format of a 'Change of speed or level' token type
//			coincides with the format of any other token type.
//			We try to match regex for the 'Change of speed or level' no matter which value 'interpretationMode' has.
            if (tokenLength > 9) {
                if (patternChangeOfSpeedOrLevel == null)
                    patternChangeOfSpeedOrLevel = Pattern.compile(
                            "^[A-Z0-9]{2,11}/(([KN][0-9]{4})|([M][0-9]{3}))(([AF][0-9]{3})|([MS][0-9]{4})|VFR)$");
                matcher = patternChangeOfSpeedOrLevel.matcher(tokenValue);
//				if (tokenLength > 9 && tokenValue.matches("^[A-Z0-9]{2,11}/(([KN][0-9]{4})|([M][0-9]{3}))(([AF][0-9]{3})|([MS][0-9]{4})|VFR)$")) {
                if (matcher.matches()) {
                    listOfTokenInterpretations.addAll(tryInterpretChangeOfSpeedOrLevel());
                    return listOfTokenInterpretations; // We return from 'getTokenInterpretations' since there is no
                                                       // other token types with such 10 to 22-character format.
                }
            }
        case 23:
        case 24:
        case 25:
        case 26:
        case 27:
        case 28:
        case 29:
//			There is zero probability that the format of a 'Cruise climb' token type
//			coincides with the format of any other token type.
//			We try to match regex for the 'Cruise climb' no matter which value 'interpretationMode' has.
            if (tokenLength > 15) {
                if (patternCruiseClimb == null)
                    patternCruiseClimb = Pattern.compile(
                            "^C/[A-Z0-9]{2,11}/(([KN][0-9]{4})|([M][0-9]{3}))(([AF][0-9]{3})|([MS][0-9]{4})|VFR)(([AF][0-9]{3})|([MS][0-9]{4})|PLUS)$");
                matcher = patternCruiseClimb.matcher(tokenValue);
//				if (tokenLength > 15 && tokenValue.matches("^C/[A-Z0-9]{2,11}/(([KN][0-9]{4})|([M][0-9]{3}))(([AF][0-9]{3})|([MS][0-9]{4})|VFR)(([AF][0-9]{3})|([MS][0-9]{4})|PLUS)$")) {
                if (matcher.matches()) {
                    listOfTokenInterpretations.addAll(tryInterpretCruiseClimb());
                    return listOfTokenInterpretations; // We return from 'getTokenInterpretations' since there is no
                                                       // other token types with such 16 to 29-character format.
                }
            }
        }
        return listOfTokenInterpretations;
    }

    private ArrayList<TokenInterpretation> tryInterpretCruiseClimb() throws SQLException {
        ArrayList<TokenInterpretation> listOfTokenInterpretations = new ArrayList<TokenInterpretation>(5);
        TokenInterpretation tokenInterpretation = null;

        int tokenLength = tokenValue.length();
        if (tokenLength < 16 || tokenLength > 29) {
            return listOfTokenInterpretations;
        }

        switch (tokenLength) {
        case 16:
        case 17:
        case 18:
        case 19:
        case 20:
        case 21:
            if (patternCruiseClimbNavaid == null)
                patternCruiseClimbNavaid = Pattern.compile(
                        "^C/([A-Z0-9]{2}|[A-Z]{3})/(([KN][0-9]{4})|([M][0-9]{3}))(([AF][0-9]{3})|([MS][0-9]{4})|VFR)(([AF][0-9]{3})|([MS][0-9]{4})|PLUS)$");
            matcher = patternCruiseClimbNavaid.matcher(tokenValue);
//			if (tokenValue.matches("^C/([A-Z0-9]{2}|[A-Z]{3})/(([KN][0-9]{4})|([M][0-9]{3}))(([AF][0-9]{3})|([MS][0-9]{4})|VFR)(([AF][0-9]{3})|([MS][0-9]{4})|PLUS)$")) {
            if (matcher.matches()) {
                listOfTokenInterpretations
                        .addAll(tryInterpretNavaid(tokenValue.substring(2, tokenValue.indexOf('/', 2))));

                if (listOfTokenInterpretations.isEmpty())
                    return listOfTokenInterpretations;

                Iterator<TokenInterpretation> iterator = listOfTokenInterpretations.iterator();
                while (iterator.hasNext()) {
                    tokenInterpretation = iterator.next();
                    tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.CRUISECLIMB;
                    tokenInterpretation.significantPointSecType = SignificantPointSecondaryType.NAVAID;
                }
                return listOfTokenInterpretations;
            }
        case 22:
        case 23:
            if (tokenLength > 18) {
                if (patternCruiseClimbDesignatedPoint == null)
                    patternCruiseClimbDesignatedPoint = Pattern.compile(
                            "^C/[A-Z]{5}/(([KN][0-9]{4})|([M][0-9]{3}))(([AF][0-9]{3})|([MS][0-9]{4})|VFR)(([AF][0-9]{3})|([MS][0-9]{4})|PLUS)$");
                matcher = patternCruiseClimbDesignatedPoint.matcher(tokenValue);
//				if (tokenLength > 18 && tokenValue.matches("^C/[A-Z]{5}/(([KN][0-9]{4})|([M][0-9]{3}))(([AF][0-9]{3})|([MS][0-9]{4})|VFR)(([AF][0-9]{3})|([MS][0-9]{4})|PLUS)$")) {
                if (matcher.matches()) {
                    listOfTokenInterpretations.addAll(tryInterpretDesignatedPoint(tokenValue.substring(2, 7)));

                    if (listOfTokenInterpretations.isEmpty())
                        return listOfTokenInterpretations;

                    Iterator<TokenInterpretation> iterator = listOfTokenInterpretations.iterator();
                    while (iterator.hasNext()) {
                        tokenInterpretation = iterator.next();
                        tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.CRUISECLIMB;
                        tokenInterpretation.significantPointSecType = SignificantPointSecondaryType.DESIGNATEDPOINT;
                    }
                    return listOfTokenInterpretations;
                }
            }
        case 24:
        case 25:
            if (tokenLength > 20) {
                if (patternCruiseClimbDegreesOnly == null)
                    patternCruiseClimbDegreesOnly = Pattern.compile(
                            "^C/[0-9]{2}[NnSs][0-9]{3}[EeWw]/(([KN][0-9]{4})|([M][0-9]{3}))(([AF][0-9]{3})|([MS][0-9]{4})|VFR)(([AF][0-9]{3})|([MS][0-9]{4})|PLUS)$");
                matcher = patternCruiseClimbDegreesOnly.matcher(tokenValue);
//				if (tokenLength > 20 && tokenValue.matches("^C/[0-9]{2}[NnSs][0-9]{3}[EeWw]/(([KN][0-9]{4})|([M][0-9]{3}))(([AF][0-9]{3})|([MS][0-9]{4})|VFR)(([AF][0-9]{3})|([MS][0-9]{4})|PLUS)$")) {
                if (matcher.matches()) {
                    listOfTokenInterpretations.addAll(tryInterpretDegreesOnlyPoint(tokenValue.substring(2, 9)));

                    if (listOfTokenInterpretations.isEmpty())
                        return listOfTokenInterpretations;

                    Iterator<TokenInterpretation> iterator = listOfTokenInterpretations.iterator();
                    while (iterator.hasNext()) {
                        tokenInterpretation = iterator.next();
                        tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.CRUISECLIMB;
                        tokenInterpretation.significantPointSecType = SignificantPointSecondaryType.DEGREESONLY;
                    }
                    return listOfTokenInterpretations;
                }
            }
        case 26:
        case 27:
            if (tokenLength > 21) {
                if (patternCruiseClimbBearingDistance == null)
                    patternCruiseClimbBearingDistance = Pattern.compile(
                            "^C/([A-Z0-9]{2}|[A-Z]{3})[0-9]{6}/(([KN][0-9]{4})|([M][0-9]{3}))(([AF][0-9]{3})|([MS][0-9]{4})|VFR)(([AF][0-9]{3})|([MS][0-9]{4})|PLUS)$");
                matcher = patternCruiseClimbBearingDistance.matcher(tokenValue);
//				if (tokenLength > 21 && tokenValue.matches("^C/([A-Z0-9]{2}|[A-Z]{3})[0-9]{6}/(([KN][0-9]{4})|([M][0-9]{3}))(([AF][0-9]{3})|([MS][0-9]{4})|VFR)(([AF][0-9]{3})|([MS][0-9]{4})|PLUS)$")) {
                if (matcher.matches()) {
                    listOfTokenInterpretations
                            .addAll(tryInterpretBearingDistance(tokenValue.substring(2, tokenValue.indexOf('/', 2))));

                    if (listOfTokenInterpretations.isEmpty())
                        return listOfTokenInterpretations;

                    Iterator<TokenInterpretation> iterator = listOfTokenInterpretations.iterator();
                    while (iterator.hasNext()) {
                        tokenInterpretation = iterator.next();
                        if (tokenInterpretation.significantPointPrimType == SignificantPointPrimaryType.BEARINGDISTANCE) {
                            tokenInterpretation.significantPointSecType = SignificantPointSecondaryType.BEARINGDISTANCE;
                        } else if (tokenInterpretation.significantPointPrimType == SignificantPointPrimaryType.BEARINGDISTANCE_WRONGVALUES) {
                            tokenInterpretation.significantPointSecType = SignificantPointSecondaryType.BEARINGDISTANCE_WRONGVALUES;
                        }
                        tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.CRUISECLIMB;
                    }
                    return listOfTokenInterpretations;
                }
            }
        case 28:
        case 29:
            if (tokenLength > 24) {
                if (patternCruiseClimbDegreesMinutes == null)
                    patternCruiseClimbDegreesMinutes = Pattern.compile(
                            "^C/[0-9]{4}[NnSs][0-9]{5}[EeWw]/(([KN][0-9]{4})|([M][0-9]{3}))(([AF][0-9]{3})|([MS][0-9]{4})|VFR)(([AF][0-9]{3})|([MS][0-9]{4})|PLUS)$");
                matcher = patternCruiseClimbDegreesMinutes.matcher(tokenValue);
//				if (tokenLength > 24 && tokenValue.matches("^C/[0-9]{4}[NnSs][0-9]{5}[EeWw]/(([KN][0-9]{4})|([M][0-9]{3}))(([AF][0-9]{3})|([MS][0-9]{4})|VFR)(([AF][0-9]{3})|([MS][0-9]{4})|PLUS)$")) {
                if (matcher.matches()) {
                    listOfTokenInterpretations.addAll(tryInterpretDegreesMinutesPoint(tokenValue.substring(2, 13)));

                    if (listOfTokenInterpretations.isEmpty())
                        return listOfTokenInterpretations;

                    Iterator<TokenInterpretation> iterator = listOfTokenInterpretations.iterator();
                    while (iterator.hasNext()) {
                        tokenInterpretation = iterator.next();
                        tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.CRUISECLIMB;
                        tokenInterpretation.significantPointSecType = SignificantPointSecondaryType.DEGREESANDMINUTES;
                    }
                    return listOfTokenInterpretations;
                }
            }
        }
        return listOfTokenInterpretations;
    }

    private ArrayList<TokenInterpretation> tryInterpretChangeOfSpeedOrLevel() throws SQLException {
        ArrayList<TokenInterpretation> listOfTokenInterpretations = new ArrayList<TokenInterpretation>(5);
        TokenInterpretation tokenInterpretation = null;
        String levelChange = null;

        int tokenLength = tokenValue.length();
        if (tokenLength < 10 || tokenLength > 22) {
            return listOfTokenInterpretations;
        }

        switch (tokenLength) {
        case 10:
        case 11:
        case 12:
        case 13:
        case 14:
            if (patternChangeOfSpeedOrLevelNavaid == null)
                patternChangeOfSpeedOrLevelNavaid = Pattern.compile(
                        "^([A-Z0-9]{2}|[A-Z]{3})/((?:[KN][0-9]{4})|(?:[M][0-9]{3}))((?:[AF][0-9]{3})|(?:[MS][0-9]{4})|VFR)$");
            matcher = patternChangeOfSpeedOrLevelNavaid.matcher(tokenValue);
//			if (tokenValue.matches("^([A-Z0-9]{2}|[A-Z]{3})/(([KN][0-9]{4})|([M][0-9]{3}))(([AF][0-9]{3})|([MS][0-9]{4})|VFR)$")) {
            if (matcher.matches()) {
                levelChange = matcher.group(3);
                listOfTokenInterpretations.addAll(tryInterpretNavaid(tokenValue.substring(0, tokenValue.indexOf('/'))));

                if (listOfTokenInterpretations.isEmpty())
                    return listOfTokenInterpretations;

                Iterator<TokenInterpretation> iterator = listOfTokenInterpretations.iterator();
                while (iterator.hasNext()) {
                    tokenInterpretation = iterator.next();
                    tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.SPEEDORLEVELCHANGE;
                    tokenInterpretation.significantPointSecType = SignificantPointSecondaryType.NAVAID;
                    tokenInterpretation.cruisingLevelChange = levelChange;
                }
                return listOfTokenInterpretations;
            }
        case 15:
        case 16:
            if (tokenLength > 12) {
                if (patternChangeOfSpeedOrLevelDesignatedPoint == null)
                    patternChangeOfSpeedOrLevelDesignatedPoint = Pattern.compile(
                            "^[A-Z]{5}/((?:[KN][0-9]{4})|(?:[M][0-9]{3}))((?:[AF][0-9]{3})|(?:[MS][0-9]{4})|VFR)$");
                matcher = patternChangeOfSpeedOrLevelDesignatedPoint.matcher(tokenValue);
//				if (tokenLength > 12 && tokenValue.matches("^[A-Z]{5}/(([KN][0-9]{4})|([M][0-9]{3}))(([AF][0-9]{3})|([MS][0-9]{4})|VFR)$")) {
                if (matcher.matches()) {
                    levelChange = matcher.group(2);
                    listOfTokenInterpretations.addAll(tryInterpretDesignatedPoint(tokenValue.substring(0, 5)));

                    if (listOfTokenInterpretations.isEmpty())
                        return listOfTokenInterpretations;

                    Iterator<TokenInterpretation> iterator = listOfTokenInterpretations.iterator();
                    while (iterator.hasNext()) {
                        tokenInterpretation = iterator.next();
                        tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.SPEEDORLEVELCHANGE;
                        tokenInterpretation.significantPointSecType = SignificantPointSecondaryType.DESIGNATEDPOINT;
                        tokenInterpretation.cruisingLevelChange = levelChange;
                    }
                    return listOfTokenInterpretations;
                }
            }
        case 17:
        case 18:
            if (tokenLength > 14) {
                if (patternChangeOfSpeedOrLevelDegreesOnly == null)
                    patternChangeOfSpeedOrLevelDegreesOnly = Pattern.compile(
                            "^[0-9]{2}[NnSs][0-9]{3}[EeWw]/((?:[KN][0-9]{4})|(?:[M][0-9]{3}))((?:[AF][0-9]{3})|(?:[MS][0-9]{4})|VFR)$");
                matcher = patternChangeOfSpeedOrLevelDegreesOnly.matcher(tokenValue);
//				if (tokenLength > 14 && tokenValue.matches("^[0-9]{2}[NnSs][0-9]{3}[EeWw]/(([KN][0-9]{4})|([M][0-9]{3}))(([AF][0-9]{3})|([MS][0-9]{4})|VFR)$")) {
                if (matcher.matches()) {
                    levelChange = matcher.group(2);
                    listOfTokenInterpretations.addAll(tryInterpretDegreesOnlyPoint(tokenValue.substring(0, 7)));

                    if (listOfTokenInterpretations.isEmpty())
                        return listOfTokenInterpretations;

                    Iterator<TokenInterpretation> iterator = listOfTokenInterpretations.iterator();
                    while (iterator.hasNext()) {
                        tokenInterpretation = iterator.next();
                        tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.SPEEDORLEVELCHANGE;
                        tokenInterpretation.significantPointSecType = SignificantPointSecondaryType.DEGREESONLY;
                        tokenInterpretation.cruisingLevelChange = levelChange;
                    }
                    return listOfTokenInterpretations;
                }
            }
        case 19:
        case 20:
            if (tokenLength > 15) {
                if (patternChangeOfSpeedOrLevelBearingDistance == null)
                    patternChangeOfSpeedOrLevelBearingDistance = Pattern.compile(
                            "^([A-Z0-9]{2}|[A-Z]{3})[0-9]{6}/((?:[KN][0-9]{4})|(?:[M][0-9]{3}))((?:[AF][0-9]{3})|(?:[MS][0-9]{4})|VFR)$");
                matcher = patternChangeOfSpeedOrLevelBearingDistance.matcher(tokenValue);
//				if (tokenLength > 15 && tokenValue.matches("^([A-Z0-9]{2}|[A-Z]{3})[0-9]{6}/(([KN][0-9]{4})|([M][0-9]{3}))(([AF][0-9]{3})|([MS][0-9]{4})|VFR)$")) {
                if (matcher.matches()) {
                    levelChange = matcher.group(3);
                    listOfTokenInterpretations
                            .addAll(tryInterpretBearingDistance(tokenValue.substring(0, tokenValue.indexOf('/'))));

                    if (listOfTokenInterpretations.isEmpty())
                        return listOfTokenInterpretations;

                    Iterator<TokenInterpretation> iterator = listOfTokenInterpretations.iterator();
                    while (iterator.hasNext()) {
                        tokenInterpretation = iterator.next();
                        if (tokenInterpretation.significantPointPrimType == SignificantPointPrimaryType.BEARINGDISTANCE) {
                            tokenInterpretation.significantPointSecType = SignificantPointSecondaryType.BEARINGDISTANCE;
                        } else if (tokenInterpretation.significantPointPrimType == SignificantPointPrimaryType.BEARINGDISTANCE_WRONGVALUES) {
                            tokenInterpretation.significantPointSecType = SignificantPointSecondaryType.BEARINGDISTANCE_WRONGVALUES;
                        }
                        tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.SPEEDORLEVELCHANGE;
                        tokenInterpretation.cruisingLevelChange = levelChange;
                    }
                    return listOfTokenInterpretations;
                }
            }
        case 21:
        case 22:
            if (tokenLength > 18) {
                if (patternChangeOfSpeedOrLevelDegreesMinutes == null)
                    patternChangeOfSpeedOrLevelDegreesMinutes = Pattern.compile(
                            "^[0-9]{4}[NnSs][0-9]{5}[EeWw]/((?:[KN][0-9]{4})|(?:[M][0-9]{3}))((?:[AF][0-9]{3})|(?:[MS][0-9]{4})|VFR)$");
                matcher = patternChangeOfSpeedOrLevelDegreesMinutes.matcher(tokenValue);
//				if (tokenLength > 18 && tokenValue.matches("^[0-9]{4}[NnSs][0-9]{5}[EeWw]/(([KN][0-9]{4})|([M][0-9]{3}))(([AF][0-9]{3})|([MS][0-9]{4})|VFR)$")) {
                if (matcher.matches()) {
                    levelChange = matcher.group(2);
                    listOfTokenInterpretations.addAll(tryInterpretDegreesMinutesPoint(tokenValue.substring(0, 11)));

                    if (listOfTokenInterpretations.isEmpty())
                        return listOfTokenInterpretations;

                    Iterator<TokenInterpretation> iterator = listOfTokenInterpretations.iterator();
                    while (iterator.hasNext()) {
                        tokenInterpretation = iterator.next();
                        tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.SPEEDORLEVELCHANGE;
                        tokenInterpretation.significantPointSecType = SignificantPointSecondaryType.DEGREESANDMINUTES;
                        tokenInterpretation.cruisingLevelChange = levelChange;
                    }
                    return listOfTokenInterpretations;
                }
            }
        }
        return listOfTokenInterpretations;
    }

    private ArrayList<TokenInterpretation> tryInterpretBearingDistance() throws SQLException {
        return tryInterpretBearingDistance(tokenValue);
    }

    private ArrayList<TokenInterpretation> tryInterpretBearingDistance(String token) throws SQLException {
        ArrayList<TokenInterpretation> listOfTokenInterpretations = new ArrayList<TokenInterpretation>(5);
        TokenInterpretation tokenInterpretation = null;

        if (patternBearingDistance == null)
            patternBearingDistance = Pattern.compile("^([A-Z0-9]{2}|[A-Z]{3})([0-9]{3})([0-9]{3})$");
        matcher = patternBearingDistance.matcher(token);
        if (matcher.matches()) {
            String navaidDes = matcher.group(1);
            String bearing = matcher.group(2);
            String distance = matcher.group(3);
            int intBearing = Integer.parseInt(bearing);
            int intDistance = Integer.parseInt(distance);
            if (intBearing > 360 || intDistance > 300) {
                tokenInterpretation = new TokenInterpretation();
                tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SIGNIFICANT_POINT_NOTFOUND;
                tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.BEARINGDISTANCE_WRONGVALUES;
                tokenInterpretation.significantPointSecType = SignificantPointSecondaryType.NAVAID;
                tokenInterpretation.parsedFeatureDesignator = navaidDes;
                listOfTokenInterpretations.add(tokenInterpretation);
            } else {
                List<RoutePoint> listOfNavaidPoints;
                ArrayList<RoutePointType> listOfRoutePointTypes = new ArrayList<RoutePointType>(3);
                listOfRoutePointTypes.add(RoutePointType.VOR);
                listOfRoutePointTypes.add(RoutePointType.NDB);
                listOfRoutePointTypes.add(RoutePointType.TACAN);
//				The code querying time slices of all Navaid (VOR, NDB, TACAN) features in the database
//				with 'navaidDes' designators, which are valid by 'timeStamp' time position.
                listOfNavaidPoints = dataRetriver.getFixes(navaidDes, listOfRoutePointTypes, timeStamp);

                if (listOfNavaidPoints == null || listOfNavaidPoints.isEmpty()) {
                    tokenInterpretation = new TokenInterpretation();
                    tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SIGNIFICANT_POINT_NOTFOUND;
                    tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.BEARINGDISTANCE;
                    tokenInterpretation.significantPointSecType = SignificantPointSecondaryType.NAVAID;
                    tokenInterpretation.parsedFeatureDesignator = navaidDes;
                    listOfTokenInterpretations.add(tokenInterpretation);
                } else {
//					If more than one Navaid with the same 'navaidDes' designator is found,
//					then several 'tokenInterpretation' should be initialised and added to 'listOfTokenInterpretations' in the loop.
//					The latter is possible since the only restriction on duplication of Navaid designators is that
//					they should not be duplicated within 1100 km (600 NM) - as per ICAO Annex 11, Appendix 2.
                    for (RoutePoint routePoint : listOfNavaidPoints) {
                        tokenInterpretation = new TokenInterpretation();
                        tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SIGNIFICANT_POINT;
                        tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.BEARINGDISTANCE;
                        tokenInterpretation.significantPointSecType = SignificantPointSecondaryType.NAVAID;
                        tokenInterpretation.navaidType = NavaidType.valueOf(NavaidType.class,
                                routePoint.pointType.toString());
                        tokenInterpretation.parsedFeatureDesignator = navaidDes;
                        tokenInterpretation.timeSliceRecID = routePoint.recordID;
                        tokenInterpretation.name = routePoint.name;
//						Note, that the Navaid location is not actually a route point.
                        tokenInterpretation.setRoutePointWKT(getBearDistIntersection(routePoint.getWKT(),
                                Integer.parseInt(bearing), Double.parseDouble(distance)));
                        listOfTokenInterpretations.add(tokenInterpretation);
                    }
                }
            }
        }
        return populateTokenValueAttribute(listOfTokenInterpretations);
    }

    private ArrayList<TokenInterpretation> tryInterpretDegreesOnlyPoint() {
        return tryInterpretDegreesOnlyPoint(tokenValue);
    }

    ArrayList<TokenInterpretation> tryInterpretDegreesOnlyPoint(String token) {
        ArrayList<TokenInterpretation> listOfTokenInterpretations = new ArrayList<TokenInterpretation>(2);
        TokenInterpretation tokenInterpretation = null;

        if (patternDegreesOnly == null)
            patternDegreesOnly = Pattern.compile("^([0-9]{2})([NnSs])([0-9]{3})([EeWw])$");
        matcher = patternDegreesOnly.matcher(token);
        if (matcher.matches()) {
            int latitude = Integer.parseInt(matcher.group(1));
            int longitude = Integer.parseInt(matcher.group(3));

            tokenInterpretation = new TokenInterpretation();
//			tokenInterpretation.parsedFeatureDesignator = token;

            if (latitude > 90 || longitude > 180) {
                tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SIGNIFICANT_POINT_NOTFOUND;
            } else {
                if (matcher.group(2).matches("[Ss]"))
                    latitude = -latitude;
                if (matcher.group(4).matches("[Ww]"))
                    longitude = -longitude;

                tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SIGNIFICANT_POINT;
                tokenInterpretation.setCoordinates(Double.valueOf((double) longitude),
                        Double.valueOf((double) latitude));
            }
            tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.DEGREESONLY;
            listOfTokenInterpretations.add(tokenInterpretation);
        }
        return populateTokenValueAttribute(listOfTokenInterpretations);
    }

    private ArrayList<TokenInterpretation> tryInterpretDegreesMinutesPoint() {
        return tryInterpretDegreesMinutesPoint(tokenValue);
    }

    ArrayList<TokenInterpretation> tryInterpretDegreesMinutesPoint(String token) {
        ArrayList<TokenInterpretation> listOfTokenInterpretations = new ArrayList<TokenInterpretation>(2);
        TokenInterpretation tokenInterpretation = null;

        if (patternDegreesMinutes == null)
            patternDegreesMinutes = Pattern.compile("^([0-9]{2})([0-9]{2})([NnSs])([0-9]{3})([0-9]{2})([EeWw])$");
        matcher = patternDegreesMinutes.matcher(token);
        if (matcher.matches()) {
            float latDeg = Float.valueOf(matcher.group(1));
            float latMin = Float.valueOf(matcher.group(2));
            float lonDeg = Float.valueOf(matcher.group(4));
            float lonMin = Float.valueOf(matcher.group(5));
            tokenInterpretation = new TokenInterpretation();
//			tokenInterpretation.parsedFeatureDesignator = token;

            if (latDeg > 90 || latMin > 60 || lonDeg > 180 || lonMin > 60) {
                tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SIGNIFICANT_POINT_NOTFOUND;
            } else {
                float floatLatitude = latDeg + latMin / 60;
                float floatLongitude = lonDeg + lonMin / 60;
                if (floatLatitude > 90 || floatLongitude > 180) {
                    tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SIGNIFICANT_POINT_NOTFOUND;
                } else {
                    if (matcher.group(3).matches("[Ss]"))
                        floatLatitude = -floatLatitude;
                    if (matcher.group(6).matches("[Ww]"))
                        floatLongitude = -floatLongitude;

                    tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SIGNIFICANT_POINT;
                    tokenInterpretation.setCoordinates(Double.valueOf((double) floatLongitude),
                            Double.valueOf((double) floatLatitude));
                }
            }
            tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.DEGREESANDMINUTES;
            listOfTokenInterpretations.add(tokenInterpretation);
        }
        return populateTokenValueAttribute(listOfTokenInterpretations);
    }

    private ArrayList<TokenInterpretation> tryInterpretDegreesMinutesSecondsPoint() {
        return tryInterpretDegreesMinutesSecondsPoint(tokenValue);
    }

    ArrayList<TokenInterpretation> tryInterpretDegreesMinutesSecondsPoint(String token) {
        ArrayList<TokenInterpretation> listOfTokenInterpretations = new ArrayList<TokenInterpretation>(2);
        TokenInterpretation tokenInterpretation = null;

        if (patternDegreesMinutesSeconds == null)
            patternDegreesMinutesSeconds = Pattern
                    .compile("^([0-9]{2})([0-9]{2})([0-9]{2})([NnSs])([0-9]{3})([0-9]{2})([0-9]{2})([EeWw])$");
        matcher = patternDegreesMinutesSeconds.matcher(token);
        if (matcher.matches()) {
            float latDeg = Float.valueOf(matcher.group(1));
            float latMin = Float.valueOf(matcher.group(2));
            float latSec = Float.valueOf(matcher.group(3));
            float lonDeg = Float.valueOf(matcher.group(5));
            float lonMin = Float.valueOf(matcher.group(6));
            float lonSec = Float.valueOf(matcher.group(7));
            tokenInterpretation = new TokenInterpretation();
//			tokenInterpretation.parsedFeatureDesignator = token;

            if (latDeg > 90 || latMin > 60 || latSec > 60 || lonDeg > 180 || lonMin > 60 || lonSec > 60) {
                tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SIGNIFICANT_POINT_NOTFOUND;
            } else {
                float floatLatitude = latDeg + latMin / 60 + latSec / 3600;
                float floatLongitude = lonDeg + lonMin / 60 + lonSec / 3600;
                if (floatLatitude > 90 || floatLongitude > 180) {
                    tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SIGNIFICANT_POINT_NOTFOUND;
                } else {
                    if (matcher.group(4).matches("[Ss]"))
                        floatLatitude = -floatLatitude;
                    if (matcher.group(8).matches("[Ww]"))
                        floatLongitude = -floatLongitude;

                    tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SIGNIFICANT_POINT;
                    tokenInterpretation.setCoordinates(Double.valueOf((double) floatLongitude),
                            Double.valueOf((double) floatLatitude));
                }
            }
            tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.DEGREES_MINUTES_SECONDS;
            listOfTokenInterpretations.add(tokenInterpretation);
        }
        return populateTokenValueAttribute(listOfTokenInterpretations);
    }

    private ArrayList<TokenInterpretation> tryInterpretADHP() throws SQLException {
        return tryInterpretADHP(tokenValue);
    }

    ArrayList<TokenInterpretation> tryInterpretADHP(String token) throws SQLException {
        ArrayList<TokenInterpretation> listOfTokenInterpretations = new ArrayList<TokenInterpretation>(2);
        TokenInterpretation tokenInterpretation = null;

        if (token.length() == 4) {
            if (patternADHP == null)
                patternADHP = Pattern.compile("^[A-Z]{4}$");
            matcher = patternADHP.matcher(token);

            if (matcher.matches()) {
                List<RoutePoint> listOfADHPs;
//				The code querying time slices of all ADHP features in the database
//				with 'token' designators, which are valid by 'timeStamp' time position.
                listOfADHPs = dataRetriver.getFixes(token, RoutePointType.ADHP, timeStamp);

                if (listOfADHPs == null || listOfADHPs.isEmpty()) {
                    tokenInterpretation = new TokenInterpretation();
                    tokenInterpretation.tokenPrimaryType = TokenPrimaryType.ADHP_NOTFOUND;
                    tokenInterpretation.parsedFeatureDesignator = token;
                    listOfTokenInterpretations.add(tokenInterpretation);
                } else {
                    for (RoutePoint routePoint : listOfADHPs) {
                        tokenInterpretation = new TokenInterpretation();
                        tokenInterpretation.tokenPrimaryType = TokenPrimaryType.ADHP;
                        tokenInterpretation.parsedFeatureDesignator = token;
                        tokenInterpretation.timeSliceRecID = routePoint.recordID;
                        tokenInterpretation.name = routePoint.name;
                        tokenInterpretation.setCoordinates(routePoint.getLongitude(), routePoint.getLatitude());
                        listOfTokenInterpretations.add(tokenInterpretation);
                    }
                }
            }
        }
        return populateTokenValueAttribute(listOfTokenInterpretations);
    }

    private ArrayList<TokenInterpretation> tryInterpretDesignatedPoint() throws SQLException {
        return tryInterpretDesignatedPoint(tokenValue);
    }

    private ArrayList<TokenInterpretation> tryInterpretDesignatedPoint(String token) throws SQLException {
        ArrayList<TokenInterpretation> listOfTokenInterpretations = new ArrayList<TokenInterpretation>(5);
        TokenInterpretation tokenInterpretation = null;

        if (patternDesignatedPoint == null)
            patternDesignatedPoint = Pattern.compile("^[A-Z]{5}$");
        matcher = patternDesignatedPoint.matcher(token);
//		if (token.length() == 5 && token.matches("^[A-Z]{5}$")) {
        if (token.length() == 5 && matcher.matches()) {
            List<RoutePoint> listOfDesignatedPoints;
//			The code querying time slices of all DesignatedPoint features in the database
//			with 'token' designators, which are valid by 'timeStamp' time position.
            listOfDesignatedPoints = dataRetriver.getFixes(token, RoutePointType.Waypoint, timeStamp);

            if (listOfDesignatedPoints == null || listOfDesignatedPoints.isEmpty()) {
                tokenInterpretation = new TokenInterpretation();
                tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SIGNIFICANT_POINT_NOTFOUND;
                tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.DESIGNATEDPOINT;
                tokenInterpretation.parsedFeatureDesignator = token;
                listOfTokenInterpretations.add(tokenInterpretation);
            } else {
//				According to Annex 11 Appendix 2 "the significant point at a position Not marked by the site of a radio navigation aid
//				shall be designated by a UNIQUE five-letter pronounceable <name-code>."
//				So in theory all DesignatedPoint features in the database can have only unique designators.
//				However if for some reasons a <name-code> or an entire DesignatedPoint feature was duplicated in the database,
//				then several DesignatedPoint features with the same 'token' designator will be found
//				and several 'tokenInterpretation' must be initialised and added to 'listOfTokenInterpretations' in the loop.
                for (RoutePoint routePoint : listOfDesignatedPoints) {
                    tokenInterpretation = new TokenInterpretation();
                    tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SIGNIFICANT_POINT;
                    tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.DESIGNATEDPOINT;
                    tokenInterpretation.parsedFeatureDesignator = token;
                    tokenInterpretation.timeSliceRecID = routePoint.recordID;
                    tokenInterpretation.name = routePoint.name;
                    tokenInterpretation.setCoordinates(routePoint.getLongitude(), routePoint.getLatitude());
                    listOfTokenInterpretations.add(tokenInterpretation);
                }
            }
        }
        return populateTokenValueAttribute(listOfTokenInterpretations);
    }

    private ArrayList<TokenInterpretation> tryInterpretNavaid() throws SQLException {
        return tryInterpretNavaid(tokenValue);
    }

    private ArrayList<TokenInterpretation> tryInterpretNavaid(String token) throws SQLException {
        ArrayList<TokenInterpretation> listOfTokenInterpretations = new ArrayList<TokenInterpretation>(15);
        TokenInterpretation tokenInterpretation = null;

//		Here we use originally planned regex '^[A-Z0-9]{2,4}$', based on the max number of characters,
//		specified in AIXM-5.x for 'CodeNavaidDesignatorBaseType' data type
//		if (token.matches("^[A-Z0-9]{2,4}$")) {
        if (patternNavaid == null)
            patternNavaid = Pattern.compile("^([A-Z0-9]{2}|[A-Z]{3})$");
        matcher = patternNavaid.matcher(token);
//		if (token.matches("^([A-Z0-9]{2}|[A-Z]{3})$")) {
        if (matcher.matches()) {
            List<RoutePoint> listOfNavaidPoints;
            ArrayList<RoutePointType> listOfRoutePointTypes = new ArrayList<RoutePointType>(3);
            listOfRoutePointTypes.add(RoutePointType.VOR);
            listOfRoutePointTypes.add(RoutePointType.NDB);
            listOfRoutePointTypes.add(RoutePointType.TACAN);

//			The code querying time slices of all VOR, NDB and TACAN features in the database
//			with 'token' designators, which are valid by 'timeStamp' time position.

            listOfNavaidPoints = dataRetriver.getFixes(token, listOfRoutePointTypes, timeStamp);
            /*
             * try { listOfNavaidPoints = dataRetriver.getFixes(token,
             * listOfRoutePointTypes, timeStamp); }
             * 
             * catch (SQLException e){ e.getErrorCode() PSQLException. e.getMessage(); }
             */
            if (listOfNavaidPoints == null || listOfNavaidPoints.isEmpty()) {
                tokenInterpretation = new TokenInterpretation();
                tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SIGNIFICANT_POINT_NOTFOUND;
                tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.NAVAID;
                tokenInterpretation.parsedFeatureDesignator = token;
                listOfTokenInterpretations.add(tokenInterpretation);
            } else {
//				If more than one Navaid (VOR, NDB, TACAN) with the same 'token' designator is found,
//				then several 'tokenInterpretation' are initialised and added to 'listOfTokenInterpretations' in the loop.
//				The latter is possible since the only restriction on duplication of Navaid designators is that
//				they should not be duplicated within 1100 km (600 NM) - as per ICAO Annex 11, Appendix 2.
                for (RoutePoint routePoint : listOfNavaidPoints) {
                    tokenInterpretation = new TokenInterpretation();
                    tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SIGNIFICANT_POINT;
                    tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.NAVAID;
                    tokenInterpretation.navaidType = NavaidType.valueOf(NavaidType.class,
                            routePoint.pointType.toString());
                    tokenInterpretation.parsedFeatureDesignator = token;
                    tokenInterpretation.timeSliceRecID = routePoint.recordID;
                    tokenInterpretation.name = routePoint.name;
                    tokenInterpretation.setCoordinates(routePoint.getLongitude(), routePoint.getLatitude());
                    listOfTokenInterpretations.add(tokenInterpretation);
                }
            }
        }
        return populateTokenValueAttribute(listOfTokenInterpretations);
    }

    private ArrayList<TokenInterpretation> tryInterpretATSroute() throws SQLException {
        return tryInterpretATSroute(tokenValue);
    }

    private ArrayList<TokenInterpretation> tryInterpretATSroute(String token) throws SQLException {
        ArrayList<TokenInterpretation> listOfTokenInterpretations = new ArrayList<TokenInterpretation>(5);
        TokenInterpretation tokenInterpretation = null;

//		Originally planned regex ending '[FGYZ]?' was changed to '[A-Z]?', since non-standard routes (like 'W86A' in Kazakhstan)
//		were discovered at http://www.nwva.org/nwva/plans/plans.htm
//		if (patternATSroute == null) patternATSroute = Pattern.compile("^[KUST]?[ABGHJLMNPQRTVWYZ][1-9][0-9]{0,2}[A-Z]?$");
        if (patternATSroute == null)
            patternATSroute = Pattern.compile("^[KUSABGHJLMNPQRTVWYZ]?[ABGHJLMNPQRTVWYZ][1-9][0-9]{0,2}[A-Z]?$");
        matcher = patternATSroute.matcher(token);
        if (matcher.matches()) {
            List<Long> listOfRouteRecIDs = null;
//			The code querying time slices of all Route features in the database
//			with 'atsRouteDes' designators, which are valid by 'timeStamp' time position.
            listOfRouteRecIDs = dataRetriver.getRoutesRecIDs(token, timeStamp);

            if (listOfRouteRecIDs == null || listOfRouteRecIDs.isEmpty() || listOfRouteRecIDs.get(0) == -1) {
                tokenInterpretation = new TokenInterpretation();
                tokenInterpretation.tokenPrimaryType = TokenPrimaryType.ATS_ROUTE_NOTFOUND;
                tokenInterpretation.parsedFeatureDesignator = token;
                listOfTokenInterpretations.add(tokenInterpretation);
            } else {
//				Even if the database contains more than one ATS Route with the same 'atsRouteDes' designator,
//				only one 'tokenInterpretation' is initialised and added to 'listOfTokenInterpretations'.
                tokenInterpretation = new TokenInterpretation();
                tokenInterpretation.tokenPrimaryType = TokenPrimaryType.ATS_ROUTE;
                tokenInterpretation.parsedFeatureDesignator = token;
                listOfTokenInterpretations.add(tokenInterpretation);
                tokenInterpretation.timeSliceRecID = listOfRouteRecIDs.get(0);
                tokenInterpretation.retrievedRouteRecIDs = listOfRouteRecIDs;
            }
        }
        return populateTokenValueAttribute(listOfTokenInterpretations);
    }

    private ArrayList<TokenInterpretation> tryInterpretSID_STAR() throws SQLException {
        return tryInterpretSID_STAR(tokenValue);
    }

    private ArrayList<TokenInterpretation> tryInterpretSID_STAR(String token) throws SQLException {
        ArrayList<TokenInterpretation> listOfTokenInterpretations = new ArrayList<TokenInterpretation>(5);
        TokenInterpretation tokenInterpretation = null;
//		The first group of regex '([A-Z][0-9A-Z]|[A-Z]{3}|[A-Z]{5})' is to identify significant point
//		of either
//		a) Navaid '[A-Z][0-9A-Z]|[A-Z]{3}' since no VOR/NDB/TACANs designators longer than 3 characters length were observed
//		and those ones of 3 characters length never include numbers; 
//		or
//		b) DesignatedPoint '[A-Z]{5}' type.
//		'([1-9])([A-Z&&[^IO]])?' is SID or STAR postfix
        if (patternSID_STAR == null)
            patternSID_STAR = Pattern.compile("^([A-Z][0-9A-Z]|[A-Z]{3,5})([1-9])([A-Z&&[^IO]])?$");
        matcher = patternSID_STAR.matcher(token);
        if (matcher.matches()) {
            String significantPointDes = matcher.group(1);
//			if (significantPointDes.matches("^[A-Z]{5}$")) {
            if (significantPointDes.length() > 3) {
                List<RoutePoint> listOfDesignatedPoints = null;
//				The code querying time slices of all DesignatedPoint features in the database
//				with 'significantPointDes' designators, which are valid by 'timeStamp' time position.
                if (significantPointDes.length() == 5)
                    listOfDesignatedPoints = dataRetriver.getFixes(significantPointDes, RoutePointType.Waypoint,
                            timeStamp);

                else if (significantPointDes.length() == 4) {
                    if (sidStarRefPoint != null || sidStarRadiusInMeters != null)
                        listOfDesignatedPoints = dataRetriver.getFixesAroundPoint(significantPointDes + "_",
                                RoutePointType.Waypoint, sidStarRefPoint.getWKT(), sidStarRadiusInMeters, timeStamp);
                }

                if (listOfDesignatedPoints == null || listOfDesignatedPoints.isEmpty()) {
                    tokenInterpretation = new TokenInterpretation();
                    if (interpretationMode == TokenInterpretationMode.FIRST)
                        tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SID_ROUTE_NOTFOUND;
                    else if (interpretationMode == TokenInterpretationMode.LAST)
                        tokenInterpretation.tokenPrimaryType = TokenPrimaryType.STAR_ROUTE_NOTFOUND;
                    tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.DESIGNATEDPOINT;
                    tokenInterpretation.parsedFeatureDesignator = significantPointDes;
                    listOfTokenInterpretations.add(tokenInterpretation);
                } else {
//					According to Annex 11 Appendix 2 "the significant point at a position Not marked by the site of a radio navigation aid
//					shall be designated by a UNIQUE five-letter pronounceable <name-code>."
//					So in theory all DesignatedPoint features in the database can have only unique designators.
//					However if for some reasons a <name-code> or an entire DesignatedPoint feature was duplicated in the database,
//					then several DesignatedPoint features with the same 'token' designator will be found
//					and several 'tokenInterpretation' must be initialised and added to 'listOfTokenInterpretations' in the loop.
                    for (RoutePoint routePoint : listOfDesignatedPoints) {
                        tokenInterpretation = new TokenInterpretation();
                        if (interpretationMode == TokenInterpretationMode.FIRST)
                            tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SID_ROUTE;
                        else if (interpretationMode == TokenInterpretationMode.LAST)
                            tokenInterpretation.tokenPrimaryType = TokenPrimaryType.STAR_ROUTE;
                        tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.DESIGNATEDPOINT;
                        tokenInterpretation.parsedFeatureDesignator = significantPointDes;
                        tokenInterpretation.timeSliceRecID = routePoint.recordID;
                        tokenInterpretation.name = routePoint.name;
                        tokenInterpretation.setCoordinates(routePoint.getLongitude(), routePoint.getLatitude());
                        listOfTokenInterpretations.add(tokenInterpretation);
                    }
                }
            } else {
                List<RoutePoint> listOfNavaidPoints;
                ArrayList<RoutePointType> listOfRoutePointTypes = new ArrayList<RoutePointType>(3);
                listOfRoutePointTypes.add(RoutePointType.VOR);
                listOfRoutePointTypes.add(RoutePointType.NDB);
                listOfRoutePointTypes.add(RoutePointType.TACAN);

//				The code querying time slices of all VOR, NDB and TACAN features in the database
//				with 'significantPointDes' designators, which are valid by 'timeStamp' time position.
                listOfNavaidPoints = dataRetriver.getFixes(significantPointDes, listOfRoutePointTypes, timeStamp);

                if (listOfNavaidPoints == null || listOfNavaidPoints.isEmpty()) {
                    tokenInterpretation = new TokenInterpretation();
                    if (interpretationMode == TokenInterpretationMode.FIRST)
                        tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SID_ROUTE_NOTFOUND;
                    else if (interpretationMode == TokenInterpretationMode.LAST)
                        tokenInterpretation.tokenPrimaryType = TokenPrimaryType.STAR_ROUTE_NOTFOUND;
                    tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.NAVAID;
                    tokenInterpretation.parsedFeatureDesignator = significantPointDes;
                    listOfTokenInterpretations.add(tokenInterpretation);
                } else {
//					If more than one Navaid (VOR, NDB, TACAN) with the same 'significantPointDes' designator is found,
//					then several 'tokenInterpretation' are initialised and added to 'listOfTokenInterpretations' in the loop.
//					The latter is possible since the only restriction on duplication of Navaid designators is that
//					they should not be duplicated within 1100 km (600 NM) - as per ICAO Annex 11, Appendix 2.
                    for (RoutePoint routePoint : listOfNavaidPoints) {
                        tokenInterpretation = new TokenInterpretation();
                        if (interpretationMode == TokenInterpretationMode.FIRST)
                            tokenInterpretation.tokenPrimaryType = TokenPrimaryType.SID_ROUTE;
                        else if (interpretationMode == TokenInterpretationMode.LAST)
                            tokenInterpretation.tokenPrimaryType = TokenPrimaryType.STAR_ROUTE;
                        tokenInterpretation.significantPointPrimType = SignificantPointPrimaryType.NAVAID;
                        tokenInterpretation.navaidType = NavaidType.valueOf(NavaidType.class,
                                routePoint.pointType.toString());
                        tokenInterpretation.parsedFeatureDesignator = significantPointDes;
                        tokenInterpretation.timeSliceRecID = routePoint.recordID;
                        tokenInterpretation.name = routePoint.name;
                        tokenInterpretation.setCoordinates(routePoint.getLongitude(), routePoint.getLatitude());
                        listOfTokenInterpretations.add(tokenInterpretation);
                    }
                }
            }
        }
        return populateTokenValueAttribute(listOfTokenInterpretations);
    }

    private ArrayList<TokenInterpretation> tryInterpretPoint(String token) throws SQLException {
        ArrayList<TokenInterpretation> listOfTokenInterpretations = new ArrayList<TokenInterpretation>(5);
        int tokenLength = token.length();

        if (tokenLength < 4)
            listOfTokenInterpretations.addAll(tryInterpretNavaid(token));
        else if (tokenLength == 4) {
            if (recognizeADHP)
                listOfTokenInterpretations.addAll(tryInterpretADHP(token));
        } else if (tokenLength == 5)
            listOfTokenInterpretations.addAll(tryInterpretDesignatedPoint(token));
        else if (tokenLength == 7)
            listOfTokenInterpretations.addAll(tryInterpretDegreesOnlyPoint(token));
        else if (tokenLength == 11)
            listOfTokenInterpretations.addAll(tryInterpretDegreesMinutesPoint(token));
        else if (tokenLength == 15)
            listOfTokenInterpretations.addAll(tryInterpretDegreesMinutesSecondsPoint(token));

        return populateTokenValueAttribute(listOfTokenInterpretations);
    }

    private String getBearDistIntersection(String centralPointWKT, int bearing, double distance) throws SQLException {
        if (patternPointWKT == null)
            patternPointWKT = Pattern.compile("^\\s*POINT\\s*\\(\\s*(\\S+)\\s+(\\S+)\\s*\\)\\s*$");
        matcher = patternPointWKT.matcher(centralPointWKT);
        double cntPointLon_GeoG;
        double cntPointLat_GeoG;
        if (matcher.matches()) {
            cntPointLon_GeoG = Double.parseDouble(matcher.group(1));
            cntPointLat_GeoG = Double.parseDouble(matcher.group(2));
        } else
            return null;

        int baseSrid = 0;

        if (cntPointLat_GeoG > 0) {
            baseSrid = 32601;
        } else if (cntPointLat_GeoG < 0) {
            baseSrid = 32701;
        } else if (cntPointLat_GeoG == 0) {
            if (bearing < 90 || bearing > 270)
                baseSrid = 32701;
            else
                baseSrid = 32601;
        }

        double tempDouble = (cntPointLon_GeoG + 180) / 6;
        double sridShift = Math.floor(tempDouble);

        if (sridShift == tempDouble) {
            if (bearing > 0 && bearing < 180) {
                if (sridShift > 0)
                    sridShift = sridShift - 1;
                else
                    sridShift = 59;
            }
        }

        int srid = baseSrid + (int) sridShift;

        if (psExteriorRingWKT_GeoM == null) {
            psExteriorRingWKT_GeoM = connection.prepareStatement(
                    "SELECT ST_AsText(ST_ExteriorRing(ST_Buffer(ST_Transform(?::geography::geometry, ?), ?, 90)))");
            psCentralPointWKT_GeoM = connection
                    .prepareStatement("SELECT ST_AsText(ST_Transform(?::geography::geometry, ?))");
            psIntersectionWKT_GeoM = connection.prepareStatement(
                    "SELECT ST_AsText(ST_Transform(ST_Intersection(ST_GeometryFromText(?, ?), ST_GeometryFromText(?, ?)), 4326)::geography)");
        }

        tempDouble = distance * 1852;
        psExteriorRingWKT_GeoM.setString(1, centralPointWKT);
        psExteriorRingWKT_GeoM.setInt(2, srid);
        psExteriorRingWKT_GeoM.setFloat(3, (float) (tempDouble));
        ResultSet rs = psExteriorRingWKT_GeoM.executeQuery();
        String exteriorRingWKT_Geom;
        if (rs.next())
            exteriorRingWKT_Geom = rs.getString(1);
        else
            return null;

        psCentralPointWKT_GeoM.setString(1, centralPointWKT);
        psCentralPointWKT_GeoM.setInt(2, srid);
        rs = psCentralPointWKT_GeoM.executeQuery();
        String cntPointWKT_GeoM;
        if (rs.next())
            cntPointWKT_GeoM = rs.getString(1);
        else
            return null;

        matcher = patternPointWKT.matcher(cntPointWKT_GeoM);
        double cntPointLon_GeoM;
        double cntPointLat_GeoM;
        if (matcher.matches()) {
            cntPointLon_GeoM = Double.parseDouble(matcher.group(1));
            cntPointLat_GeoM = Double.parseDouble(matcher.group(2));
        } else
            return null;

        int radial = (bearing + 180) % 360;
        double endPointLon_GeoM = cntPointLon_GeoM + Math.sin(Math.toRadians(radial)) * tempDouble * 1.05;
        double endPointLat_GeoM = cntPointLat_GeoM + Math.cos(Math.toRadians(radial)) * tempDouble * 1.05;

        String rayWKT = "LINESTRING(" + Double.toString(cntPointLon_GeoM) + " " + Double.toString(cntPointLat_GeoM)
                + "," + Double.toString(endPointLon_GeoM) + " " + Double.toString(endPointLat_GeoM) + ")";
        psIntersectionWKT_GeoM.setString(1, rayWKT);
        psIntersectionWKT_GeoM.setInt(2, srid);
        psIntersectionWKT_GeoM.setString(3, exteriorRingWKT_Geom);
        psIntersectionWKT_GeoM.setInt(4, srid);
        rs = psIntersectionWKT_GeoM.executeQuery();
        String intersectionPointWKT_GeoG;
        if (rs.next()) {
            intersectionPointWKT_GeoG = rs.getString(1);
        } else
            return null;

        return intersectionPointWKT_GeoG;
    }

    ArrayList<TokenInterpretation> populateTokenValueAttribute(
            ArrayList<TokenInterpretation> listOfTokenInterpretations) {
        if (tokenValue != null && !tokenValue.isEmpty()) {
            for (TokenInterpretation tokenInterpretation : listOfTokenInterpretations) {
                tokenInterpretation.tokenValue = tokenValue;
            }
        }
        return listOfTokenInterpretations;
    }

    /*
     * private String getBearDistIntersectionGEOGRAPHY(String centralPointWKT, int
     * bearing, double distance) throws SQLException { Pattern pattern =
     * Pattern.compile("^\\s*POINT\\s*\\(\\s*(\\S+)\\s+(\\S+)\\s*\\)\\s*$"); matcher
     * = pattern.matcher(centralPointWKT); double cntPointLon_GeoG; double
     * cntPointLat_GeoG; if (matcher.matches()) { cntPointLon_GeoG =
     * Double.parseDouble(matcher.group(1)); cntPointLat_GeoG =
     * Double.parseDouble(matcher.group(2)); } else return null;
     * 
     * int baseSrid = 0;
     * 
     * if (cntPointLat_GeoG > 0) { baseSrid = 32601; } else if (cntPointLat_GeoG <
     * 0) { baseSrid = 32701; } else if (cntPointLat_GeoG == 0) { if (bearing < 90
     * || bearing > 270) baseSrid = 32701; else baseSrid = 32601; }
     * 
     * double tempDouble = (cntPointLon_GeoG + 180) / 6; double sridShift =
     * Math.floor(tempDouble);
     * 
     * if (sridShift == tempDouble) { if (bearing > 0 && bearing < 180) { if
     * (sridShift > 0) sridShift = sridShift - 1; else sridShift = 59; } }
     * 
     * int srid = baseSrid + (int)sridShift;
     * 
     * if (psExteriorRingWKT_GeoG == null) { psExteriorRingWKT_GeoG = connection.
     * prepareStatement("SELECT ST_AsText(ST_ExteriorRing(ST_Buffer(?::geography, ?)::geometry))"
     * ); psCentralPointWKT_GeoM = connection.
     * prepareStatement("SELECT ST_AsText(ST_GeomFromText(ST_Transform(?::geography::geometry, ?)))"
     * ); psIntersectionWKT_GeoG = connection.
     * prepareStatement("SELECT ST_AsText(ST_Intersection(ST_Transform(ST_GeometryFromText(?, ?), 4326)::geography, ?::geography))"
     * ); }
     * 
     * tempDouble = distance * 1852; psExteriorRingWKT_GeoG.setString(1,
     * centralPointWKT); psExteriorRingWKT_GeoG.setFloat(2, (float)(tempDouble));
     * ResultSet rs = psExteriorRingWKT_GeoG.executeQuery(); String
     * exteriorRingWKT_Geog; if (rs.next()) exteriorRingWKT_Geog = rs.getString(1);
     * else return null;
     * 
     * psCentralPointWKT_GeoM.setString(1, centralPointWKT);
     * psCentralPointWKT_GeoM.setInt(2, srid); rs =
     * psCentralPointWKT_GeoM.executeQuery(); String cntPointWKT_GeoM; if
     * (rs.next()) cntPointWKT_GeoM = rs.getString(1); else return null;
     * 
     * matcher = pattern.matcher(cntPointWKT_GeoM); double cntPointLon_GeoM; double
     * cntPointLat_GeoM; if (matcher.matches()) { cntPointLon_GeoM =
     * Double.parseDouble(matcher.group(1)); cntPointLat_GeoM =
     * Double.parseDouble(matcher.group(2)); } else return null;
     * 
     * int radial = (bearing + 180) % 360; double endPointLon_GeoM =
     * cntPointLon_GeoM + Math.sin(Math.toRadians(radial)) * tempDouble * 1.05;
     * double endPointLat_GeoM = cntPointLat_GeoM + Math.cos(Math.toRadians(radial))
     * * tempDouble * 1.05;
     * 
     * String rayWKT = "LINESTRING(" + Double.toString(cntPointLon_GeoM) + " " +
     * Double.toString(cntPointLat_GeoM) + "," + Double.toString(endPointLon_GeoM) +
     * " " + Double.toString(endPointLat_GeoM) + ")";
     * psIntersectionWKT_GeoG.setString (1, rayWKT);
     * psIntersectionWKT_GeoG.setInt(2, srid); psIntersectionWKT_GeoG.setString (3,
     * exteriorRingWKT_Geog); rs = psIntersectionWKT_GeoG.executeQuery(); String
     * intersectionPointWKT_GeoG; if (rs.next()) { intersectionPointWKT_GeoG =
     * rs.getString(1); } else return null;
     * 
     * return intersectionPointWKT_GeoG; }
     */
}
