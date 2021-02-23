package ca.ids.abms.spreadsheets.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

import ca.ids.abms.spreadsheets.InvalidSpreadsheetLayoutException;
import ca.ids.abms.modules.translation.Translation;

/**
 * A time period used in charges spreadsheets, defined by [startSecond, endSecond).
 * These objects may be constructed by parsing strings of the form "14:00-15:00" or similar.
 * The times are converted to seconds (of the day) and stored as such. The start time/second
 * is optional.
 *
 * @author dpanech
 *
 */
class SimpleTimeRange {

    /** Start second of day (inclusive, optional) */
    public final Integer startSecond;

    /** End second of day (exclusive, mandatory */
    public final Integer endSecond;

    /** Parse a string representing a time range */
    public static SimpleTimeRange parseRange (final String s) {
        return parseRange (null, s);
    }

    /** Parse a string representing a time range; errPrefix is used in exception messages */
    public static SimpleTimeRange parseRange (final String errPrefix, final String s) {
        final Matcher m = RE_RANGE.matcher (s);
        check (m.matches(), errPrefix, s);
        final Integer startSecond = parseTime (errPrefix, m.group(1));
        final Integer endSecond = parseTime2400 (errPrefix, m.group(2));
        check (startSecond == null || startSecond < endSecond, errPrefix, s);
        return new SimpleTimeRange (startSecond, endSecond);
    }

    /** Parse a time of day ("14:00" etc) into seconds of day. */
    public static Integer parseTime (final String s) {
        return parseTime (null, s);
    }

    /** Parse a time of day ("14:00" etc) into seconds of day; errPrefix is used in exception messages */
    public static Integer parseTime (final String errPrefix, final String s) {
        return do_parseTime (errPrefix, s, false);
    }

    /** Parse a time of day ("14:00" etc) into seconds of day; allows the value of "24:00" */
    public static Integer parseTime2400 (final String s) {
        return parseTime2400 (null, s);
    }

    /** Parse a time of day ("14:00" etc) into seconds of day; allows the value of "24:00"; errPrefix is used in exception messages */
    public static Integer parseTime2400 (final String errPrefix, final String s) {
        return do_parseTime (errPrefix, s, true);
    }

    /** Convert hours and minutes to seconds */
    public static int secondOfDay (int hour, int minute) {
        Preconditions.checkArgument(hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59);
        return hour * 60 * 60 + minute * 60;
    }

    /** Convert hours and minutes to seconds; allows 24:00 */
    public static int secondOfDay2400 (int hour, int minute) {
        Preconditions.checkArgument((hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59) || (hour == 24 && minute == 0));
        return hour * 60 * 60 + minute * 60;
    }

    /** Format second of day as "HH:MM" */
    public static String formatSecondsOfDay (final int secondOfDay) {
        Preconditions.checkArgument(secondOfDay >= 0 && secondOfDay <= secondOfDay (23, 59));
        final int hour = secondOfDay / 60 / 60;
        final int minute = (secondOfDay - hour * 60 * 60) / 60;
        return String.format ("%02d:%02d", hour, minute);
    }

    /** Format second of day as "HH:MM"; allows 24:00 */
    public static String formatSecondsOfDay2400 (final int secondOfDay) {
        return formatSecondsOfDay2400(secondOfDay, "%02d:%02d");
    }

    /** Format second of day; allows 24:00 */
    public static String formatSecondsOfDay2400 (final int secondOfDay, final String stringFormat) {
        Preconditions.checkArgument(secondOfDay >= 0 && secondOfDay <= secondOfDay2400 (24, 00));
        final int hour = secondOfDay / 60 / 60;
        final int minute = (secondOfDay - hour * 60 * 60) / 60;
        return String.format (stringFormat, hour, minute);
    }

    // ------------- private ------------
    private static final Pattern RE_RANGE = Pattern.compile("^(?:(.+)(?:(?:to)|[-]))?(.+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern RE_TIME_OF_DAY = Pattern.compile ("^\\s*(\\d{1,2})(?::?(\\d{2}))?\\s*$");

    private SimpleTimeRange (final Integer startSecond, final Integer endSecond) {
        this.startSecond = startSecond;
        this.endSecond = endSecond;
    }

    private static void check (boolean ok, final String locPrefix, final String s) {
        if (!ok) {
            throw new InvalidSpreadsheetLayoutException (SSUtils.formatMessageWithPrefix(locPrefix, Translation.getLangByToken("invalid time") + " \"{}\"", s));
        }
    }

    private static Integer do_parseTime (final String locPrefix, final String s, boolean allow2400) {
        if (s != null) {
            final Matcher m = RE_TIME_OF_DAY.matcher (s);
            check (m.matches(), locPrefix, s);
            int hour, minute;
            hour = Integer.parseInt (m.group(1));
            minute = m.group(2) == null ? 0 : Integer.parseInt (m.group(2));
            check ((hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59) || (allow2400 && hour == 24 && minute == 0),
                    locPrefix, s);
            return allow2400 ? secondOfDay2400 (hour, minute) : secondOfDay (hour, minute);
        }
        return null;
    }

}
