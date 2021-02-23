package ca.ids.abms.modules.flightmovementsbuilder.utility.cache;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouteCacheUtility {

    private final static String DEPARTURE_AERODROME = "dep_ad";

    private final static String DESTINATION_AERODROME = "dest_ad";

    private final static String ROUTE = "route";

    private final static String ELAPSED_TIME = "elapsed_time";

    private final static String CRUISING_SPEED = "cruising_speed";

    private final static String EQUALS = "=";

    private final static String SEPARATOR = "|";

    private final static String COMMA = ",";

    private static Pattern patternMachNumber = null;

    private static Pattern patternCruisingSpeed = null;

    private static Pattern patternTimeHHMM = null;

    private static final double MACH_TO_KMPH = 1224.0;

    private static Logger LOG = LoggerFactory.getLogger(RouteCacheUtility.class);

    public static String combine(boolean isCircularRoute, String aDeparture, String aRoute, String[] aDestsAerdrome,
            String aCruisingSpeedOrMachNumber, String aElapsedTime) {
        String keyString = "";
        if (isCircularRoute) {
            keyString = combineKey(aDeparture, aRoute, aDestsAerdrome, aCruisingSpeedOrMachNumber, aElapsedTime);
        } else {
            keyString = combineKey(aDeparture, aRoute, aDestsAerdrome);
        }
        LOG.debug("KEYSTRING {}", keyString);
        return keyString;
    }

    public static String combine(String aRoute) {
        String keyString = combineKey(aRoute);
        LOG.debug("KEYSTRING {}", keyString);
        return keyString;
    }

    public static Integer convertElapsedTime(String elapsedTime) {
        Matcher matcher;
        int elapsedHours = 0;
        int elapsedMinutes = 0;
        Integer calculatedTime = null;
        if (patternTimeHHMM == null)
            patternTimeHHMM = Pattern.compile("^([0-9]{2})([0-5][0-9])$");
        matcher = patternTimeHHMM.matcher(elapsedTime);
        if (matcher.matches()) {
            elapsedHours = Integer.parseInt(matcher.group(1));
            elapsedMinutes = Integer.parseInt(matcher.group(2));
            elapsedMinutes = (elapsedHours * 60) + elapsedMinutes;
            calculatedTime = Integer.valueOf(elapsedMinutes);
        } else {
            LOG.debug(
                    "'elapsedTime' parameter string does not match 4-digit 'HHMM' format. Please note that 'MM' cannot be greater than 59 minutes.");
        }
        return calculatedTime;
    }

    public static Integer convertSpeed(String speed) {
        Matcher matcher;
        double speedMPH = 0;
        Integer calculatedSpeed = null;
        if (speed.length() == 4) {
            if (patternMachNumber == null)
                patternMachNumber = Pattern.compile("^[Mm]([0-9]{3})$");
            matcher = patternMachNumber.matcher(speed);
            if (matcher.matches()) {
                speedMPH = Double.parseDouble(matcher.group(1)) * MACH_TO_KMPH * 10; // to convert to meters per hour we multiply to 10 (not 1000) cause Mach number is given in the nearest hundredth of unit Mach.
                Double d = Double.valueOf(speedMPH);
                calculatedSpeed = Integer.valueOf(d.intValue());
            } else {
                LOG.debug("4-character 'cruisingSpeedOrMachNumber' parameter string does not match 'M###' format.");
            }
        } else if (speed.length() == 5) {
            if (patternCruisingSpeed == null)
                patternCruisingSpeed = Pattern.compile("^([KkNn])([0-9]{4})$");
            matcher = patternCruisingSpeed.matcher(speed);
            if (matcher.matches()) {
                speedMPH = Double.parseDouble(matcher.group(2));

                if (matcher.group(1).equalsIgnoreCase("K")) {
                    speedMPH = speedMPH * 1000;
                } else if (matcher.group(1).equalsIgnoreCase("N")) {
                    speedMPH = speedMPH * 1852;
                }
                Double d = Double.valueOf(speedMPH);
                calculatedSpeed = Integer.valueOf(d.intValue());
            } else {
                LOG.debug(
                        "5-character 'cruisingSpeedOrMachNumber' parameter string matches neither 'K####' nor 'N####' format.");
            }
        }
        return calculatedSpeed;
    }

    public static String normalize(String aString) {
        String s = aString.replaceAll("\\s+", " ").trim();
        return s;
    }

    public static String removeFlightLevelsAndNormalize (final String route) {
        if (route != null) {
            final StringBuilder result = new StringBuilder();
            int size = route.length();
            for (int i = 0; i < size; i++) {
                char c = route.charAt(i);
                if (c == '/'
                    && i + 4 <= size
                    && Character.isDigit(route.charAt(i + 1))
                    && Character.isDigit(route.charAt(i + 2))
                    && Character.isDigit(route.charAt(i + 3))
                    && (i + 4 == size || Character.isWhitespace(route.charAt(i + 4)))) {
                    i += 3;
                } else {
                    result.append(c);
                }
            }
            return result.toString().replaceAll("\\s+", " ").trim();
        }
        return null;
    }

    private static String combineKey(String aRoute) {
        StringBuilder sb = new StringBuilder();
        if (aRoute != null) {
            sb.append(ROUTE);
            sb.append(EQUALS);
            sb.append(normalize(aRoute));
        }
        return sb.toString();
    }

    private static String combineKey(String aDeparture, String aRoute, String[] aDestsAerdrome) {
        StringBuilder sb = new StringBuilder();
        if (aDeparture != null) {
            sb.append(DEPARTURE_AERODROME);
            sb.append(EQUALS);
            sb.append(normalize(aDeparture));
            sb.append(SEPARATOR);
        }
        if (aRoute != null) {
            sb.append(ROUTE);
            sb.append(EQUALS);
            sb.append(normalize(aRoute));
            sb.append(SEPARATOR);
        }
        if (aDestsAerdrome != null && aDestsAerdrome.length > 0) {
            sb.append(DESTINATION_AERODROME);
            sb.append(EQUALS);
            for (int i = 0; i < aDestsAerdrome.length; i++) {
                sb.append(normalize(aDestsAerdrome[i]));
                sb.append(COMMA);
            }
            sb.setLength(sb.length() - 1);
            sb.append(SEPARATOR);
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    private static String combineKey(String aDeparture, String aRoute, String[] aDestsAerdrome,
            String aCruisingSpeedOrMachNumber, String aElapsedTime) {
        StringBuilder sb = new StringBuilder();
        String firstPart = combineKey(aDeparture, aRoute, aDestsAerdrome);
        sb.append(firstPart);
        if (aCruisingSpeedOrMachNumber != null) {
            sb.append(CRUISING_SPEED);
            sb.append(EQUALS);
            sb.append(normalize(aCruisingSpeedOrMachNumber));
            sb.append(SEPARATOR);
        }
        if (aElapsedTime != null) {
            sb.append(ELAPSED_TIME);
            sb.append(EQUALS);
            sb.append(normalize(aElapsedTime));
            sb.append(SEPARATOR);
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public static Double getDistanceInKM(Double aTimeInMin, Double aSpeedInMh) {
        Double inMeters = getDistanceInM(aTimeInMin, aSpeedInMh);
        Double inKm = inMeters / 1000;
        return inKm;
    }

    public static Double getDistanceInM(Double aTimeInMin, Double aSpeedInMh) {
        return (aSpeedInMh * (aTimeInMin / 60));
    }
}
