package ca.ids.abms.modules.utilities.flights;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.error.ErrorVariables;
import ca.ids.abms.modules.util.models.DateTimeUtils;
import ca.ids.abms.util.converter.JSR310DateConverters;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlightUtility {

    private static final short SIZE_PREFIX_ICAO_CODE = 3;
    
    /**
     * Use to resolve date of contact from date of flight, departure time, and contact time.
     *
     * If the departure time is more than {@code FlightConstants.DEP_TIME_BUFFER} hours later than contact time,
     * use date of flight plus one day as date of contact.
     */
    public static LocalDateTime resolveDateOfContact(final String departureTime, final String contactTime, final LocalDateTime dateOfFlight) {
        if (dateOfFlight == null)
            return null;

        // shift departure time as we are comparing against date of flight
        return doAdjustDate(departureTime, contactTime, dateOfFlight)
            ? dateOfFlight.plusDays(1)
            : dateOfFlight;
    }

    /**
     * Use to resolve date of flight from date of contact, departure time, and contact time.
     *
     * If the departure time is more than {@code FlightConstants.DEP_TIME_BUFFER} hours later than contact time,
     * use date of contact minus one day as date of flight.
     */
    public static LocalDateTime resolveDateOfFlight(final String departureTime, final String contactTime, final LocalDateTime dateOfContact) {
        if (dateOfContact == null)
            return null;

        // shift contact time as we are comparing against date of contact
        return doAdjustDate(departureTime, contactTime, dateOfContact)
            ? dateOfContact.minusDays(1)
            : dateOfContact;
    }

    /**
     * Use to resolve date of arrival from date of flight, departure time, and eet.
     *
     * If the departure time is more than {@code FlightConstants.DEP_TIME_BUFFER} hours later than departure time + eet,
     * use date of flight plus one day as date of contact.
     */
    public static LocalDateTime resolveDateOfArrival(final String departureTime, final String eet, final LocalDateTime dateOfFlight) {
        if (dateOfFlight == null)
            return null;

        String  arrivalTime = DateTimeUtils.addTime(departureTime, eet);

         // shift departure time as we are comparing against date of flight
        return doAdjustDate(departureTime, arrivalTime, dateOfFlight)
            ? dateOfFlight.plusDays(1)
            : dateOfFlight;
    }

    private static boolean doAdjustDate(final String departureTime, final String contactTime, final LocalDateTime date) {
        if (date == null || StringUtils.isBlank(departureTime) || StringUtils.isBlank(contactTime))
            return false;

        final LocalDateTime contactDateTime = LocalDateTime.of(date.toLocalDate(),
            LocalTime.parse(contactTime, DateTimeFormatter.ofPattern(JSR310DateConverters.DEFAULT_PATTERN_TIME)));
        final LocalDateTime departureDateTime = LocalDateTime.of(date.toLocalDate(),
            LocalTime.parse(departureTime, DateTimeFormatter.ofPattern(JSR310DateConverters.DEFAULT_PATTERN_TIME)));

        return contactDateTime.isBefore(departureDateTime.minusHours(FlightConstants.DEP_TIME_BUFFER));
    }

    private FlightUtility() {
        throw new IllegalStateException("Utility class, do not instantiate a new instance.");
    }
    
    public static String getICAOCodePrefixBygetFlightId(final String flightId, final boolean nonStandardFlightId) {
        String result = null;
        if (StringUtils.isNotBlank(flightId) && flightId.length() >= SIZE_PREFIX_ICAO_CODE) {
            // 2019-05-21
            // check if the flight id qualifies for ICAO format:
            // ICAO code(3 letters) must be followed by numbers only
            // Example: EAG123 matches
            //          EAGLE does not match
            //          EAGLE111 does not match
            //          EAG11X does not match
            //
            // 2019-12-09 (user story 115366)
            // If this system configuration "Allow non-standard flight id" (nonStandardFlightId) is set to true,
            // if the first 3 characters of the flight id are alphabetic and the fourth is a number,
            // extract the first three characters and compare them with the account icao codes
            // Example: EAG11X matches
            //          EAG123 matches
            //          EAGLE does not match
            //          EAGLE111 does not match
            String pattern;
            if (nonStandardFlightId) {
                pattern = "^([A-Za-z]{3})[0-9]";
            }
            else {
                pattern = "^([A-Za-z]{3})[0-9]+$";
            }

            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(flightId);
            if(m.find()) {
                result = m.group(1);
            }      
        }
        return result;
    }
    
    /*Utilities Method*/

    /**
     * F330 flight level + 3 chars
     * S1130 tens of meters + 4 chars
     * A045 hundreds of feet
     * M0840 tens of meters
     * <p>
     * Calculate flight level in feet
     * M,S - tens of meters
     * A - hundreds of feet
     * FT -feet
     * FL - hundreds of feet
     * Conversion from meters to feet: ft = m*3.2808
     *
     * @return flight level in hundred of feet
     */
    public static Double convertFlightLevel(final String flightLevel, final Double defaultLevel) {
        
        Double result = -1.0d;
        
        if (StringUtils.isBlank(flightLevel)) {
            return defaultLevel;
        }
        
        final String flightLevelCleaned = flightLevel.trim();
        
        // VFR flights are considered to be very low
        if(flightLevelCleaned.equalsIgnoreCase("VFR")) {
            return 1.0;
        }

        if (!flightLevelCleaned.isEmpty()) {

            long level;
            String str;
            try {
                 if (flightLevelCleaned.startsWith("F") || flightLevelCleaned.startsWith("A")) {

                     str = flightLevelCleaned.substring(1);
                     level = Long.parseLong(str);
                     result = level * 100.0;
                 } else if (flightLevelCleaned.startsWith("S") || flightLevelCleaned.startsWith("M")) {

                     str = flightLevelCleaned.substring(1);
                     level = Long.parseLong(str);
                     result = level * 10 * 3.2808;
                 }
            } catch (NumberFormatException nfe) {
                 final ErrorVariables errorVariables = new ErrorVariables();
                 errorVariables.addEntry("flightLevel", flightLevelCleaned);
                 new ErrorDTO.Builder()
                    .setErrorMessage(ErrorConstants.ERR_INVALID_FLIGHT_LEVEL)
                    .appendDetails(
                        "Flight level: {{flightLevel}}, Flight ID: {{flightId}}, date: {{dateOfFlight}}, dep. ad.: {{depAd}}, dest. ad.: {{destAd}}"
                    )
                    .setDetailMessageVariables(errorVariables)
                    .throwInvalidDataException();
            }
            if (result <= 0) {
                result = -1.0d;
            }
        }
        return (result <= 0) ? defaultLevel : result/100;
    }
}
