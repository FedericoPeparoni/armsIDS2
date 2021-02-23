package ca.ids.abms.plugins.amhs.fpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

import ca.ids.abms.util.DateUtils;

class DayOfFlightParser {

    DayOfFlightParser() {}
    
    public static LocalDate parseDof (final String s, final LocalDateTime filingDateTime) {
        if (s == null || (s.length() != 6 && s.length() != 8)) {
            return null;
        }
        try {
            if (s.length() == 6) {
                final int year = Integer.parseInt (s.substring(0, 2));
                final int mon = Integer.parseInt (s.substring(2, 4));
                final int day = Integer.parseInt (s.substring(4, 6));
                if (mon < 1 || mon > 12 || day < 1 || day > 31) {
                    return null;
                }
                final LocalDate baseDate = filingDateTime == null ? LocalDate.now() : filingDateTime.toLocalDate();
                final int baseCentury = baseDate.getYear() / 100;
                long minDistanceDays = Long.MAX_VALUE;
                LocalDate dof = null;
                for (int century = baseCentury - 1; century < baseCentury + 1; ++century) {
                    final int fullYear = century * 100 + year;
                    if (DateUtils.getLastDayOfMonth (fullYear, mon) < day) {
                        continue;
                    }
                    final LocalDate d = LocalDate.of (fullYear, mon, day);
                    final long distanceDays = Math.abs (ChronoUnit.DAYS.between (d, baseDate));
                    if (distanceDays < minDistanceDays) {
                        minDistanceDays = distanceDays;
                        dof = d;
                    }
                }
                return dof;
            }
            Preconditions.checkArgument(s.length() == 8);
            final int year = Integer.parseInt (s.substring(0, 2));
            final int mon = Integer.parseInt (s.substring(2, 4));
            final int day = Integer.parseInt (s.substring(4, 6));
            if (mon < 1 || mon > 12 || day < 1 || day > 31) {
                return null;
            }
            return LocalDate.of (year, mon, day);
        }
        catch (final NumberFormatException x) {
            return null;
        }
    }

    public static LocalDate extractDof (final String s, final LocalDateTime filingDateTime) {
        if (s != null) {
            final Matcher m = RE_DOF.matcher (s);
            if (m.find()) {
                return parseDof (m.group(1), filingDateTime);
            }
        }
        return null;
    }
    
    public static String formatDof (final LocalDate dof) {
        if (dof != null) {
            return DateTimeFormatter.ISO_DATE.format (dof);
        }
        return null;
    }
    private static final Pattern RE_DOF = Pattern.compile("\\bDOF\\s*/\\s*(\\d{6}|\\d{8})\\b");

}
