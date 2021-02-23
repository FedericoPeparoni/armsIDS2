package ca.ids.abms.plugins.amhs.fpl;

import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TimeOfDayParser {

    private TimeOfDayParser() {}

    public static LocalTime parseFplTimeOfDay (final String s) {
        if (s != null) {
            final Matcher m = RE_TIME.matcher(s);
            if (m.matches()) {
                final int hours = Integer.parseInt (m.group(1));
                final int minutes = Integer.parseInt (m.group(2));
                if (hours < 24 && minutes < 60) {
                    return LocalTime.of (hours, minutes);
                }
            }
        }
        return null;
    }

    public static String formatTimeOfDay (final LocalTime t) {
        if (t != null) {
            return String.format ("%02d%02d", t.getHour(), t.getMinute());
        }
        return null;
    }


    private static final Pattern RE_TIME = Pattern.compile ("^\\s*(\\d{2}):?(\\d{2})\\s*");
}
