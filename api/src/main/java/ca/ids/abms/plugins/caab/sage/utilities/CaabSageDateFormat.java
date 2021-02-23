package ca.ids.abms.plugins.caab.sage.utilities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * CAAB Sage INTEGRATION database expects dates stored in VARCHAR to be in the format of 'ddMMyyyy'
 * and all time values are ignored.
 *
 * For example, LocalDateTime January 31, 2018 at 00:00:00 would be formatted as '31012018' when
 * converted to VARCHAR.
 */
public class CaabSageDateFormat {

    /**
     * Date time formatter that follows the pattern 'ddMMyyyy'.
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");

    /**
     * Format LocalDateTime as String using the pattern 'ddMMyyyy'.
     */
    public static String format(LocalDateTime date) {
        return date.format(DATE_TIME_FORMATTER);
    }

    /**
     * Format LocalDate as String using the pattern 'ddMMyyyy'.
     */
    public static String format(LocalDate date) {
        return date.format(DATE_TIME_FORMATTER);
    }

    private CaabSageDateFormat() {
        throw new IllegalStateException("Utility class, do not instantiate a new instance.");
    }
}
