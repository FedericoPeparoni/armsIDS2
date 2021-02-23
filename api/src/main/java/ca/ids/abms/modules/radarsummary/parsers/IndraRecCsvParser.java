package ca.ids.abms.modules.radarsummary.parsers;

import ca.ids.abms.modules.radarsummary.Fix;
import ca.ids.abms.modules.radarsummary.RadarSummaryCsvViewModel;
import ca.ids.abms.modules.radarsummary.RadarSummaryRejectableCsvParser;
import ca.ids.abms.modules.radarsummary.enumerators.RadarSummaryFormat;
import ca.ids.abms.util.csv.CsvFormatException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

@Component
public class IndraRecCsvParser extends RadarSummaryRejectableCsvParser {

    private static final String VALUE_DELIMITER = ",";

    private static final String LINE_INDICATOR = "^(?:(?:\\cB)|(?:^\\cC\\cB)).+$";

    /**
     * INDRA files come in 2 flavors that need to be parsed differently
     */
    enum FormatVersion {
        OLD, NEW
    }

    /**
     * Use to parse Indra csv line items from a file.
     */
    public IndraRecCsvParser() {
        super(RadarSummaryFormat.INDRA_REC, VALUE_DELIMITER, LINE_INDICATOR);
    }

    /**
     * Parse line of fields and map to RadarSummaryCsvViewModel to be added to list of parsed lines.
     * Any exceptions will be added as a rejected item.
     */
    protected RadarSummaryCsvViewModel parseFields(String[] fields) {
        Preconditions.checkNotNull (fields);
        trimToNull (fields);
        final RadarSummaryCsvViewModel model = new RadarSummaryCsvViewModel();
        final FormatVersion formatVersion = guessFormatVersion (fields);
        if (formatVersion == FormatVersion.OLD) {
            parseLineOldFormat (fields, model);
        }
        else {
            Preconditions.checkArgument (formatVersion == FormatVersion.NEW);
            parseLineNewFormat (fields, model);
        }
        model.setSegment(1);
        return model;
    }
    
    /**
     * Trim all fields in an array
     */
    private void trimToNull (final String[] fields) {
        for (int i = 0; i < fields.length; ++i) {
            fields[i] = StringUtils.trimToNull (fields[i]);
        }
    }
    
    /**
     * Determine what format version a line is.
     */
    static FormatVersion guessFormatVersion (final String[] fields) {
        // Field #12 (0-based) is a (mandatory) date/time ("contact date & time") in OLD format, but not in NEW format
        require (fields.length > 12, "unrecognized line format -- not enough fields in line");
        if (fields[12] != null && RE_GUESS_LINE_FORMAT_OLD_F12.matcher(fields[12]).matches()) {
            return FormatVersion.OLD;
        }
        // Field #48 (0-based) is a (mandatory) date in NEW format ("day of flight"), but not in OLD format
        if (fields[48] != null && RE_GUESS_LINE_FORMAT_NEW_F48.matcher (fields[48]).matches()) {
            return FormatVersion.NEW;
        }
        throw new CsvFormatException ("unrecognized line format");
    }
    private static final Pattern RE_GUESS_LINE_FORMAT_OLD_F12 = Pattern.compile ("^[0-9:/ -]{6,}$");
    private static final Pattern RE_GUESS_LINE_FORMAT_NEW_F48 = Pattern.compile ("^\\d{4}-\\d{2}-\\d{2}$");

    /**
     * Make sure the number of fields is at least the specified number
     */
    private static void requireFieldCountAtLeast (final String[] fields, final int minFields) {
        require (fields.length >= minFields, "line contains too few fields");
    }
    
    /**
     * Throw a CsvFormatException if the specified condition is false
     */
    private static void require (final boolean condition, final String message) {
        if (!condition) {
            throw new CsvFormatException (message);
        }
    }
    
    /**
     * Convert cruising speed in knots to standard DOC 4444 format
     * This will, if necessary, add a missing prefix "N", and pad the number to
     * length 3 or 4, depending on prefix.
     */
    static String normalizeCruisingSpeedInKnots (final String s) {
        // Input string is null -- bail out
        if (s == null) {
            return null;
        }

        // Trim & upper-case input string; bail out if result is empty
        final String uc = StringUtils.trimToNull (s.toUpperCase (Locale.US));
        if (uc == null) {
            return null;
        }
        
        // Match the regex
        final Matcher m = RE_CRUISING_SPEED.matcher (s);
        
        // The first optional group is the prefix, e.g. the "N" in "N0010"
        if (m.matches()) {
            final String prefix = ObjectUtils.firstNonNull (m.group(1), "N");
            int width;
            if (prefix.equals ("M")) {
                width = 3;
            }
            else { // prefix == N || prefix == K
                width = 4;
            }
            return prefix + StringUtils.leftPad (m.group(2), width, '0');
        }
        return uc;
    }
    private static final Pattern RE_CRUISING_SPEED = Pattern.compile ("^([KNM])?(\\d+)$");
    
    /**
     * Convert cruising level to standard DOC 4444 format
     * This will, if necessary, add a missing prefix "F", and pad the number to
     * length 3 or 4, depending on prefix.
     */
    static String normalizeCruisingLevel (final String s) {
        // Input string is null -- bail out
        if (s == null) {
            return null;
        }

        // Trim & upper-case input string; bail out if result is empty
        final String uc = StringUtils.trimToNull (s.toUpperCase (Locale.US));
        if (uc == null) {
            return null;
        }
        
        // Match the regex
        final Matcher m = RE_CRUISING_LEVEL.matcher (s);
        
        // The first optional group is the prefix, e.g. the "N" in "N0010"
        if (m.matches()) {
            final String prefix = ObjectUtils.firstNonNull (m.group(1), "F");
            int width;
            if (prefix.equals ("F") || prefix.equals("A")) {
                width = 3;
            }
            else { // prefix == S || prefix == M
                width = 4;
            }
            return prefix + StringUtils.leftPad (m.group(2), width, '0');
        }
        return uc;
    }
    private static final Pattern RE_CRUISING_LEVEL = Pattern.compile ("^([FSAM])?(\\d+)$");

    /**
     * Extract fix information from the trailing part of the fields array.
     * 
     * In the "NEW" format the first fix column index is not fixed -- we need to scan
     * the columns until we find a triplet that looks like a fix.
     */
    @SuppressWarnings("squid:S135") // "reduce the number of break & continue statements to at most 1"
    private static List <Fix> extractFixes (final String[] fields, int startIndex) {
        final List <Fix> fixes = new ArrayList<>();
        // Scan the column array from startIndex
        for (int i = startIndex; i <= fields.length - 3; ++i) {
            // Consider only the indexes that leave a "tail" whose length is a multiple of 3
            if ((fields.length - i) % 3 != 0) {
                continue;
            }
            
            // Check whether the next 3 columns match our regular expressions for point, level and time
            String point = fields[i];
            String level = fields[i + 1];
            String time = fields[i + 2];
            if (matches (RE_FIX_POINT, point) && matches (RE_FIX_LEVEL, level) && matches (RE_FIX_TIME, time)) {
                // If they match, add this fix
                fixes.add (new Fix (time, point, level));
                // Then assume each subsequent triplet is also a fix
                for (int j = i + 3; j <= fields.length - 3; j += 3) {
                    point = fields[j];
                    level = fields[j + 1];
                    time = fields[j + 2];
                    if (point != null) {
                        fixes.add (new Fix (time, point, level));
                    }
                }
                break;
            }
        }
        return fixes;
    }
    
    /**
     * Return true if the the given string matches the given regex; handles nulls gracefully
     */
    private static boolean matches (final Pattern p, final String s) {
        return s != null && p.matcher (s).matches();
    }
    private static final Pattern RE_FIX_POINT = Pattern.compile ("^[a-zA-Z0-9]{4,}$");
    private static final Pattern RE_FIX_LEVEL = Pattern.compile ("^\\d{1,4}?$");
    private static final Pattern RE_FIX_TIME = Pattern.compile ("^\\d{2}:?\\d{2}(?::?\\d{2})?$");

    /**
     * Assign fix-related fields in the model object
     */
    private static void assignFixData (final RadarSummaryCsvViewModel x, final String[] fields, int firstFixIndex) {
        // Parse out all fixes
        final List <Fix> fixes = extractFixes (fields, firstFixIndex);
        x.setFixes (Fix.formatList (fixes));
        
        //
        // Construct entry/exit points and route from individual fixes
        //
        
        // Entry/exit points and route text
        if (!fixes.isEmpty()) {
            
            final Fix entry = fixes.get(0);
            x.setFirEntryPoint(entry.point);
            x.setFirEntryTime (entry.time);
            x.setFirEntryFlightLevel (entry.level);

            final Fix exit = fixes.get (fixes.size() - 1);
            x.setFirExitPoint (exit.point);
            x.setFirExitTime (exit.time);
            x.setFirExitFlightLevel (exit.level);

            x.setRoute (Fix.formatFlightPlanRoute (fixes));
        }
    }

    /**
     * Parse a line of CSV input in the "OLD" format
     */
    void parseLineOldFormat (final String[] fields, final RadarSummaryCsvViewModel x) {
        
        // 20 fields are fixed, followed by fixes/3 fields each, first 20 are required
        requireFieldCountAtLeast (fields, 20);

        // date -- parse it, then re-format to ISO date-only format for storing in CSV model object
        if (fields[12] != null) {
            x.setDate (
                    DateTimeFormatter.ISO_DATE.format (
                            LocalDateTime.parse (fields[12], OLD_F12)
                    )
            );
        }

        // flightIdentifier
        x.setFlightId (fields[1]);

        // dayOfFlight -- N/A

        // departureTime -- N/A

        // registration
        x.setRegistration(fields[2]);

        // aircraft type
        x.setAircraftType(fields[6]);

        // departure aerodrome
        x.setDepartureAeroDrome(fields[4]);

        // destination aerodrome
        x.setDestinationAeroDrome(fields[8]);

        // flightRule -- N/A

        // flightTravelCategory -- N/A

        // flightType
        x.setFlightType (fields[3]);

        // destination time
        x.setDestTime(fields[10]);

        // operatorName
        x.setOperatorName(fields[19]);

        //  operatorIcaoCode -- N/A

        // speed
        x.setCruisingSpeed (normalizeCruisingSpeedInKnots (fields[7]));

        // flightLevel
        x.setFlightLevel (normalizeCruisingLevel (fields[11]));

        // wakeTurbulenceCategory
        x.setWakeTurb(fields[17]);

        // fixes
        // firEntryPoint
        // firEntryTime
        // firEntryLevel
        // firExitPoint
        // firExitTime
        // firExitLevel
        // route
        assignFixData (x, fields, 20);

    }
    private static final DateTimeFormatter OLD_F12 = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss");

    /**
     * Parse a line of CSV input in the "NEW" format
     * 
     * WARNING: the original documentation for this format contains a mistake -- there's
     * actually an extra field after arrivalTime that shifts all subsequent indeces by 1.
     */
    void parseLineNewFormat (final String[] fields, final RadarSummaryCsvViewModel x) {
        // The last field we need is Registration number (index 49)
        requireFieldCountAtLeast (fields, 50);
        
        // date: N/A
        
        // flightIdentifier
        x.setFlightId (fields[2]);
        
        // dayOfFlight -- it's already in ISO format
        x.setDayOfFlight (fields[48]);
        
        // departureTime
        x.setDepartureTime (fields[16]);
        
        // registration
        x.setRegistration (fields[49]);
        
        // aircraftType
        x.setAircraftType (fields[8]);
        
        // departureAeroDrome
        x.setDepartureAeroDrome (fields[15]);
        
        // destinationAerodrome
        x.setDestinationAeroDrome (fields[18]);
        
        // fixes
        // firEntryPoint
        // firEntryTime
        // firEntryLevel
        // firExitPoint
        // firExitTime
        // firExitLevel
        // route
        assignFixData (x, fields, 79);

        // flightRule
        x.setFlightRule (fields[7]);
        
        // flightTravelCategory -- N/A
        
        // flightType
        x.setFlightType (fields[12]);
        
        // arrivalTime
        x.setDestTime (fields[19]);
        
        // operatorName
        x.setOperatorName (fields[47]);
        
        // operatorIcaoCode -- N/A

        // speed
        x.setCruisingSpeed (normalizeCruisingSpeedInKnots (fields[21]));
        
        // flightLevel
        x.setFlightLevel (normalizeCruisingLevel (fields[22]));
        
        // wakeTurbulenceCategory
        x.setWakeTurb(fields[10]);
        
    }
}
