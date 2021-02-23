package com.ubitech.aim.routeparser;

import java.util.Locale;
import java.util.Calendar;
import java.util.TimeZone;

class Utils {
    /** UTC timezone */
    public static TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");

    /** A non-specific locale (en_US) */
    public static Locale NEUTRAL_LOCALE = new Locale("en", "US");

    private static java.util.regex.Pattern isoDatePattern = java.util.regex.Pattern
            .compile("^\\s*(\\d{4})-(\\d{2})-(\\d{2})(?:\\s+(\\d{2}):(\\d{2}):(\\d{2}))?\\s*$");

    /** Parse a date in the "yyyy-MM-dd HH:mm:ss" format */
    public static java.util.Date parseDateIso(String str) {
        if (str == null)
            return null;
        java.util.regex.Matcher m = isoDatePattern.matcher(str);
        if (!m.matches())
            return null;
        int year, month, day;
        int hour = 0, minute = 0, second = 0;
        year = Integer.parseInt(m.group(1));
        month = Integer.parseInt(m.group(2));
        if (month < 1 || month > 12)
            return null;
        day = Integer.parseInt(m.group(3));
        if (day < 1 || day > 31)
            return null;
        if (m.group(4) != null) {
            hour = Integer.parseInt(m.group(4));
            if (hour > 23)
                return null;
            minute = Integer.parseInt(m.group(5));
            if (minute > 59)
                return null;
            second = Integer.parseInt(m.group(6));
            if (second > 59)
                return null;
        }
        Calendar calendar = Calendar.getInstance(UTC_TIMEZONE, NEUTRAL_LOCALE);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
