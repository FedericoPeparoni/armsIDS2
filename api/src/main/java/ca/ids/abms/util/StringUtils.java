package ca.ids.abms.util;

import com.google.common.base.CaseFormat;
import org.apache.tomcat.util.codec.binary.Base64;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A class containing a number of utility methods for
 * doing common String manipulation work.
 *
 * @author Derek McKinnon
 */
public final class StringUtils {

    private static final Pattern RE_WHITESPACE = Pattern.compile ("\\s+");
    public static final String WHITE_SPACE_REGEX="\\s";
    public static final String EMPTY_STRING="";
    private static final Pattern END_OF_LINE = Pattern.compile ("(?:\\r\\n)|\\r|\\n");

    // Private ctor to prevent instantiation
    private StringUtils() {
    }

    /**
     * Converts a {@code snake_case} string into a {@code camelCase} one.
     *
     * @param string The string to convert
     * @return The camelCase version of the string
     */
    public static String snakeCaseToCamelCase(String string) {
        String[] columnNameParts = string.split("_");
        if (columnNameParts.length == 1) {
            return string;
        }

        String newColumnName = columnNameParts[0];
        for (int i = 1; i < columnNameParts.length; i++) {
            newColumnName += toUpperCaseFirstLetter(columnNameParts[i]);
        }

        return newColumnName;
    }

    public static String camelCaseToSnakeCase(String string) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, string);
    }

    private static String toUpperCaseFirstLetter(String s) {
        char[] parts = s.toCharArray();
        parts[0] = Character.toUpperCase(parts[0]);

        return String.valueOf(parts);
    }

    /**
     * Joins a {@link List<T>} of elements together using the specified delimiter.
     *
     * @param list      The list of elements
     * @param delimiter A string used to separate the elements
     * @see StringUtils#listToString(List)
     */
    public static <T> String listToString(List<T> list, String delimiter) {
        return list.stream().map(Object::toString).collect(Collectors.joining(delimiter));
    }

    /**
     * Joins a {@link List<T>} of elements together using a comma ({@code ,})
     *
     * @param list The list of elements
     */
    public static <T> String listToString(List<T> list) {
        return listToString(list, ",");
    }

    /**
     * Joins a {@link List<T>} of elements together using a comma ({@code ,})
     * and surrounds it by braces to make it into an array literal.
     *
     * @param list the list of elements
     */
    public static <T> String listToArrayLiteral(List<T> list) {
        return "'{" + listToString(list) + "}'";
    }

    public static String dateToTimestampLiteral(LocalDateTime date) {
        String format = date.format(DateTimeFormatter.ISO_DATE_TIME);

        return "'" + format + "'::timestamp";
    }

    /**
     * Convert a raw array of blob bytes into a String encoded by Base64 to use into dto beans
     *
     * @param rawBlobData byte array
     * @param contentType content type of the raw data
     * @return a string Base64-encoded, built with the UTF-8 characters set
     */
    public static String encodeBlobIntoBase64String (final byte[] rawBlobData, final String contentType) {
        StringBuilder sb = new StringBuilder();
        sb.append("data:").append(contentType).append(";base64,")
            .append(org.apache.commons.codec.binary.StringUtils.newStringUtf8(Base64.encodeBase64(rawBlobData, false)));
        return sb.toString();
    }

    /**
     * Replace each white space sequence of any length (including newlines) with a single space.
     */
    public static String normalizeWhiteSpace (final String s) {
        if (s != null) {
            return RE_WHITESPACE.matcher(s).replaceAll(" ");
        }
        return null;
    }
    
    /**
     * Check if the string is null or empty, if not to do the trim method on string.
     *
     * @param value Sting to check
     * @return null if the value is  null or return trimmed string.
     * 
     * @deprecated Use {@link #strip} instead, but note that unlike this function, "strip" will only remove
     *                 true whitespace, rather than all control characters
     */
    @Deprecated
    @SuppressWarnings ("squid:S1133")
    public static String checkAndTrimString(String value){

        String result=null;
        if(value!=null && !value.isEmpty()){
            result=value.trim();
        }
        return result;
    }

    /**
     * Check if the string is not null or not empty.
     *
     * @param value Sting to check
     * @return true if the value is not null or not empty, false if it's.
     * 
     * @deprecated Use {@link #isNotBlank} instead, but note that unlike this function,
     *             "isNotBlank" only looks for true whitespace, rather than all control characters
     */

    @Deprecated
    @SuppressWarnings ("squid:S1133")
    public static Boolean isStringIfNotNull(String value){
        Boolean result=Boolean.FALSE;

        String stringTrimmed=checkAndTrimString(value);

        if(stringTrimmed!=null && !stringTrimmed.isEmpty()){
            result=Boolean.TRUE;
        }
        return result;
    }


    /**
     * Trim a string; convert empty strings to NULL
     * 
     * @deprecated Use {@link #stripToNull} instead, but note that "stripToNull" will only remove
     *             true whitespace, rather than all control characters
     */
    @Deprecated
    @SuppressWarnings("squid:S1133")
    public static String trimStringEmptyToNull (final String s) {
        if (s != null) {
            final String t = s.trim();
            if (!t.isEmpty()) {
                return t;
            }
        }
        return null;
    }
    
    /**
     * Convert end-of-line sequences to UNIX style (\n).
     * 
     * It's called dos2unix because there's a popular command-line tool with that name.
     */
    public static String dos2unix (final CharSequence s) {
    	if (s != null) {
    		return END_OF_LINE.matcher (s).replaceAll("\n");
    	}
    	return null;
    }
    /**
     * Convert end-of-line sequences to DOS style (\r\n).
     * 
     * It's called unix2dos because there's a popular command-line tool with that name.
     */
    public static String unix2dos (final CharSequence s) {
    	if (s != null) {
    		return END_OF_LINE.matcher (s).replaceAll("\r\n");
    	}
    	return null;
    }

    /**
     * Format a messy string for including in log files and such. This function will replace
     * any non-printable characters and line breaks with Java-style backslash codes. It will
     * also abbreviate the result to the given maximum length by replacing the middle part
     * with "...". You should use this function in log messages and in toString() methods,
     * because otherwise log files tend to get filled with tons of unreadable garbage.
     * 
     * @param s - the input string
     * @param maxLength - maximum length for the result; if is is shorter it will remain unchanged (minus the special character escaping).
     */
    public static String abbrev (final String s, int maxLength) {
    	if (s != null) {
    		final String escapedString = org.apache.commons.lang3.StringEscapeUtils.escapeJava(s);
    		return org.apache.commons.lang3.StringUtils.abbreviateMiddle (escapedString, "...", maxLength);
    	}
    	return null;
    }
    
    /**
     * Same as <code>{@link #abbrev(String,int) abbrev}(s, 40)</code>
     */
    public static String abbrev (final String s) {
    	return abbrev (s, 40);
    }
    
    /**
     * Checks if a CharSequence is whitespace, empty ("") or null.
     * 
     * @see {@link org.apache.commons.lang3.StringUtils#isBlank}
     */
    public static boolean isBlank (final CharSequence s) {
        return org.apache.commons.lang3.StringUtils.isBlank (s);
    }
    
    /**
     * Checks if a CharSequence is not empty (""), not null and not whitespace only.
     * 
     * @see {@link org.apache.commons.lang3.StringUtils#isNotBlank}
     */
    public static boolean isNotBlank (final CharSequence s) {
        return org.apache.commons.lang3.StringUtils.isNotBlank (s);
    }
    
    /**
     * Strips whitespace from the start and end of a String returning null if the String is empty ("") after the strip.
     * 
     * @see {@link org.apache.commons.lang3.StringUtils#stripToNull}
     */
    public static String stripToNull (final String s) {
        return org.apache.commons.lang3.StringUtils.stripToNull (s);
    }
    
    /**
     * Strips whitespace from the start and end of a String.
     * 
     * @see {@link org.apache.commons.lang3.StringUtils#strip}
     */
    public static String strip (final String s) {
        return org.apache.commons.lang3.StringUtils.strip (s);
    }
    
}
