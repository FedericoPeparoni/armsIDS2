package ca.ids.abms.modules.radarsummary;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import ca.ids.abms.util.StringUtils;

/**
 * Fix (route point) information used by Radar summaries.
 * It contains raw/unparsed fields and is used as a helper class between CSV models
 * and entity objects.
 *
 */
public final class Fix {
    
    public final String time;
    public final String point;
    public final String level;
    
    public Fix (final String time, final String point, final String level) {
        Preconditions.checkNotNull (point);
        this.time = time;
        this.point = point;
        this.level = level;
    }
    
    @Override
    public boolean equals (final Object o) {
        if (o instanceof Fix) {
            final Fix other = (Fix)o;
            return Objects.equals(this.time,  other.time) &&
                    Objects.equals(this.point,  other.point) &&
                    Objects.equals(this.level, other.level);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return point.hashCode();
    }

    /**
     * Format this fix as "TIME-NAME-LEVEL"
     */
    @Override
    public String toString() {
        return String.format ("%s-%s-%s",
                time == null ? NULL_STR : time,
                point == null ? NULL_STR : point,
                level == null ? NULL_STR : level);
    }
    private static final String NULL_STR = "[null]";
    
    /**
     * Parse a string of the form "TIME-POINT-LEVEL"
     * 
     * @throws IllegalArgumentException if input string doesn't match the expected format
     */
    public static Fix valueOf (final String s) {
        final Matcher m = RE_FIX.matcher (s);
        Preconditions.checkArgument (m.matches(), "invalid fix \"%s\", expecting \"TIME-POINT-LEVEL\"", StringUtils.abbrev(s));
        return new Fix (nonNullGroup (m, 1), nonNullGroup (m, 2), nonNullGroup (m, 3));
    }
    private static String nonNullGroup (final Matcher matcher, final int groupIndex) {
        final String group = StringUtils.stripToNull (matcher.group(groupIndex));
        if (group == null || group.equalsIgnoreCase (NULL_STR)) {
            return null;
        }
        return group;
    }
    private static final Pattern RE_FIX = Pattern.compile ("^(.*?)-(.*?)-(.*)$");
    
    /**
     * Format a list of fixes as "TIME-POINT-LEVEL, TIME-POINT-LEVEL, ..."
     */
    public static String formatList (final List <Fix> list) {
        if (list != null) {
            return StringUtils.stripToNull (
                    list.stream()
                        .filter (Objects::nonNull)
                        .map(Fix::toString)
                        .collect(Collectors.joining(", "))
            );
        }
        return null;
    }
    
    /**
     * Format a list of fixes as a space-separated list of points (ignoring times and levels)
     */
    public static String formatFlightPlanRoute (final List <Fix> list) {
        if (list != null) {
            return StringUtils.stripToNull (
                    list.stream()
                        .filter (Objects::nonNull)
                        .map(fix -> fix.point)
                        .collect(Collectors.joining(" "))
            );
        }
        return null;
    }
    
    /**
     * Parse a list of fixes from a string similar to "TIME-NAME-LEVEL, TIME-NAME-LEVEL, ...".
     * 
     * @throws IllegalArgumentException if input string doesn't match the expected format
     */
    @SuppressWarnings("squid:S1168")
    public static List <Fix> parseList (final String s) {
        if (s != null) {
            final String[] parts = RE_LIST_SEP.split (s);
            if (parts != null) {
                final List<Fix> result = Arrays.stream(parts)
                        .map (StringUtils::stripToNull)
                        .filter (Objects::nonNull)
                        .map (Fix::valueOf)
                        .collect (Collectors.toList());
                if (!result.isEmpty()) {
                    return result;
                }
                
            }
        }
        return null;
    }
    private static final Pattern RE_LIST_SEP = Pattern.compile ("\\s*,+\\s*");
    
}

