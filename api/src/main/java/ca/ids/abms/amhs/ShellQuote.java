package ca.ids.abms.amhs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

class ShellQuote {
    
    private ShellQuote() {}

    /** Quote a list of strings using Bash quoting rules */
    public static String quote(final String... args) {
        return quote(Arrays.asList(args));
    }

    /** Quote a list of strings using Bash quoting rules */
    public static String quote(final List<String> args) {
        final StringBuilder buf = new StringBuilder();
        for (final String arg : args) {
            if (arg.length() == 0 || RE_SPEC.matcher(arg).find()) {
                buf.append('"');
                for (int i = 0; i < arg.length(); ++i) {
                    final char c = arg.charAt(i);
                    if (c == '\"' || c == '\\') {
                        buf.append('\\');
                    }
                    buf.append(c);
                }
                buf.append('"');
                buf.append(' ');
                continue;
            }
            buf.append(arg);
            buf.append(' ');
        }
        if (buf.length() > 0) {
            buf.delete(buf.length() - 1, buf.length());
        }
        return buf.toString();
    }

    private static final Pattern RE_SPEC = Pattern.compile("[ \\t\\r\\n\\\\\"']");

    /** The reverse of {@link #quote(List)} */
    @SuppressWarnings ({ "squid:S135", "squid:S3776" })
    public static List<String> unquote(final String s) {
        final List<String> args = new ArrayList<>();
        int i = 0;
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inBackslash = false;

        while (i < s.length()) {
            // Skip leading spaces
            for (; i < s.length() && isSpace(s.charAt(i)); ++i)
                ;
            // done
            if (i >= s.length()) {
                break;
            }

            // Parse a token and add it to the list
            final StringBuilder token = new StringBuilder();
            inSingleQuote = false;
            inDoubleQuote = false;
            inBackslash = false;
            for (; i < s.length(); ++i) {
                final char c = s.charAt(i);
                if (inSingleQuote) {
                    if (c == '\'') {
                        inSingleQuote = false;
                        continue;
                    }
                    token.append(c);
                    continue;
                }
                if (inDoubleQuote) {
                    if (c == '"') {
                        if (!inBackslash) {
                            inDoubleQuote = false;
                            continue;
                        }
                        token.append(c);
                        continue;
                    }
                    if (c == '\\') {
                        if (!inBackslash) {
                            inBackslash = true;
                            continue;
                        }
                        token.append(c);
                        inBackslash = false;
                        continue;
                    }
                    if (inBackslash && c != '"' && c != '\\') {
                        token.append('\\');
                        inBackslash = false;
                    }
                    token.append(c);
                    continue;
                }
                if (!inBackslash) {
                    if (c == '\'') {
                        inSingleQuote = true;
                        continue;
                    }
                    if (c == '"') {
                        inDoubleQuote = true;
                        continue;
                    }
                    if (c == '\\') {
                        inBackslash = true;
                        continue;
                    }
                    if (isSpace(c)) {
                        break;
                    }
                    token.append(c);
                    continue;
                }
                token.append(c);
                inBackslash = false;
            } // for

            args.add(token.toString());
        }

        if (inSingleQuote || inDoubleQuote) {
            throw new ShellException ("unterminated quote");
        }
        if (inBackslash) {
            final int lastIndex = args.size() - 1;
            args.set(lastIndex, args.get(lastIndex) + '\\');
        }
        return args;
    }

    private static boolean isSpace(final char c) {
        return c == ' ' || c == '\t' || c == '\r' || c == '\n';
    }

}
