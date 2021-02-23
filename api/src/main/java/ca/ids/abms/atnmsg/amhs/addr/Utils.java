package ca.ids.abms.atnmsg.amhs.addr;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Utilities for AMHS address parsers.
 */
abstract class Utils {

    public static final int MAX_AMHS_ADDR_LEN = 200;

    /**
     * Trim leading and trailing white space from a string.
     *
     * Java's built-in String.trim method removes all control characters, not just
     * white space -- that's not what we want.
     */
    public static String trim(final String src) {
        if (src != null) {
            int begin = 0;
            while (begin < src.length() && isWhiteSpace(src.charAt(begin)))
                ++begin;
            int end = src.length();
            while (end > begin && isWhiteSpace(src.charAt(end - 1)))
                --end;
            final int length = end - begin;
            if (length > 0)
                return src.substring(begin, end);
            return "";
        }
        return null;
    }

    private static boolean isWhiteSpace(char c) {
        return c == ' ' || c == '\t' || c == '\r' || c == '\n';
    }

    /**
     * Check whether the given string contains characters outside of the character
     * set allowed in AMHS addresses.
     *
     * @return the first substring that contains prohibited characters, or null
     */
    public static String checkCharSet(final String src) {
        if (src != null) {
            final Matcher m = Utils.RE_PROHIBITED_CHARSET.matcher(src);
            if (m.find()) {
                return m.group(1);
            }
        }
        return null;
    }

    /**
     * Check whether the given string contains characters sequences prohibited in
     * AMHS addresses.
     *
     * @return the first substring that contains prohibited characters, or null
     */
    public static String checkBadChars(final String src) {
        if (src != null) {
            final Matcher m = Utils.RE_PROHIBITED_SEQ.matcher(src);
            if (m.find()) {
                return m.group(1);
            }
        }
        return null;
    }

    /**
     * Check whether the given string contains white space characters
     *
     * @return true if there's white space, false otherwise
     */
    public static boolean containsWhiteSpace(final String src) {
        if (src != null) {
            final Matcher m = Utils.RE_WHITE_SPACE.matcher(src);
            if (m.find())
                return true;
        }
        return false;
    }

    /**
     * Check whether the given AMHS address key is known.
     *
     * Returns the normalized spelling of that key, or null if the key is unkn own.
     */
    public static String checkKey(final String src) {
        if (src != null) {
            final String uc = src.toUpperCase(Locale.US);
            return do_checkKey(uc);
        }
        return null;
    }

    /**
     * Check whether the given string looks like an AFTN address.
     */
    public static boolean isAftnAddr(final String src) {
        if (src != null) {
            final String uc = src.toUpperCase(Locale.US);
            return do_isAftnAddr(uc);
        }
        return false;
    }

    /**
     * Keys required in an AMHS address
     */
    static final String[] REQ_KEYS = new String[] { "C", "A", "P", "O", "OU1" };

    /**
     * All keys allowed in an AMHS address
     */
    static final String[] ALL_KEYS = new String[] { "C", "A", "P", "O", "OU1", "CN" };

    public static int countChar(final CharSequence s, final char c) {
        int count = 0;
        if (s != null) {
            for (int i = 0; i < s.length(); ++i) {
                if (c == s.charAt(i)) {
                    ++count;
                }
            }
        }
        return count;
    }

    // ---------------------- private ---------------------------

    private static String do_checkKey(final String src) {
        if (src.equals("C"))
            return "C";
        if (src.equals("A") || src.equals("ADMD"))
            return "A";
        if (src.equals("P") || src.equals("PRMD"))
            return "P";
        if (src.equals("O"))
            return "O";
        if (src.equals("OU") || src.equals("OU1"))
            return "OU1";
        if (src.equals("CN"))
            return "CN";
        return null;
    }

    private static boolean do_isAftnAddr(final String src) {
        return RE_AFTN_ADDR.matcher(src).matches();
    }

    private static final Pattern RE_PROHIBITED_CHARSET = Pattern
            .compile("((?:(?:(?![\\x20\\x0a\\x0d\\x09])[^\\x20-\\x7e])+))");
    private static final Pattern RE_PROHIBITED_SEQ = Pattern
            .compile("((?:,,)|(?://)|(?:==)|(?:\\$)|(?:\")|(?:\\\\)|(?:#)|(?::)|(?:[&<]))");

    private static final Pattern RE_AFTN_ADDR = Pattern.compile("^[A-Z]{8}$");

    private static final Pattern RE_WHITE_SPACE = Pattern.compile("([\\x20\\x0d\\x0a\\x09]+)");

    static String abbrev(final String src) {
        return StringUtils.abbreviate(src, 10);
    }

    private Utils() {
    }
}
