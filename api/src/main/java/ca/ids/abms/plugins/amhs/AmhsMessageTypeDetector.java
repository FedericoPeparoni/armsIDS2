package ca.ids.abms.plugins.amhs;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ca.ids.abms.plugins.amhs.AmhsMessageType.*;

public class AmhsMessageTypeDetector {
    
    public static AmhsMessageType guessType (final String text) {
        if (text != null) {
            for (final TypePattern p: PATTERNS) {
                if (p.pattern.matcher(text).find()) {
                    return p.type;
                }
            }
        }
        return UNKNOWN;
    }
    
    private static class TypePattern {
        final AmhsMessageType type;
        final Pattern pattern;
        public TypePattern (final AmhsMessageType type) {
            this (type, "^(?:[(]\\s*)?" + type.name() + "\\b");
        }
        public TypePattern (final AmhsMessageType type, final String pattern) {
            this.type = type;
            this.pattern = Pattern.compile (pattern);
        }
    }
    
    private static final List <TypePattern> PATTERNS = Arrays.stream (AmhsMessageType.values())
            .map (TypePattern::new)
            .collect(Collectors.toList());

}
