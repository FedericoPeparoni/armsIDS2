package ca.ids.abms.plugins.amhs.fpl;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public class FlightMessageParserUtils {

    private FlightMessageParserUtils() {}

    public static List<String> getFplTokens(final String s, int limit) {
        return split(normalizeMessageText(s), RE_DASH, limit).map(StringUtils::strip).collect(Collectors.toList());
    }

    public static String formatTimeOfDay (final LocalTime t) {
        return TimeOfDayParser.formatTimeOfDay (t);
    }
    
    public static LocalTime parseTimeOfDay (final String s) {
        return TimeOfDayParser.parseFplTimeOfDay(s);
    }

    // --------------- private -----------------
    private static String normalizeMessageText(final String s) {
        final String s1 = StringUtils.strip(s);
        if (s1.startsWith("(")) {
            String s2;
            if (s1.endsWith(")")) {
                s2 = s1.substring(1, s1.length() - 1);
            } else {
                s2 = s1.substring(1);
            }
            return StringUtils.strip(s2);
        }
        return s1;
    }

    private static Stream<String> split(final String s, final Pattern p, int limit) {
        return Arrays.stream(p.split(s, limit));
    }

    private static final Pattern RE_DASH = Pattern.compile("(?<!:)-");
}
