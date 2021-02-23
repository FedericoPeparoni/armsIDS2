package ca.ids.abms.spreadsheets.impl;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import ca.ids.abms.modules.translation.Translation;

/**
 * FLight charges basis/unit (used by parking charges).
 */
enum FlightChargeBasis {

    /**
     * Charges applied per each whole hour (rounded up).
     */
    HOUR (Translation.getLangByToken("HOUR"), "^\\s*" + Translation.getLangByToken("HOUR") + "\\s*$"),

    /**
     * Charges applied per each whole day (rounded up).
     */
    DAY (Translation.getLangByToken("DAY"), "^\\s*" + Translation.getLangByToken("DAY") + "\\s*$"),

    /**
     * Charges applied per each whole 24-hour period (rounded up).
     */
    TWENTY_FOUR_HOUR (Translation.getLangByToken("24HR"), "^\\s*" + Translation.getLangByToken("24HR") + "\\s*$");

    /** Label for this basis, as it should appear in the spreadsheet to the right of MTOW */
    public String label() {
        return label;
    }
    /** Return the constant that matches the given string, or null */
    public static FlightChargeBasis matchingValue (final String s) {
        for (final FlightChargeBasis basis: FlightChargeBasis.values()) {
            if (basis.pattern.matcher (s).matches()) {
                return basis;
            }
        }
        return null;
    }
    /** Return a string containing the comma separated of all allowed labels/values (for parse error messages, etc.) */
    public static String allDescr() {
        return allDescr;
    }

    // ----------------- private -------------------

    private String label;
    private Pattern pattern;
    private static final String allDescr = String.join (", ",
            Arrays.asList (FlightChargeBasis.values()).stream().map (x->x.label()).collect (Collectors.toList()));
    private FlightChargeBasis (final String label, final String pattern) {
        this.label = label;
        this.pattern = Pattern.compile (pattern, Pattern.CASE_INSENSITIVE);
    }
}
