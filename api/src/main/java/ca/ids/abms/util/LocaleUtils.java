package ca.ids.abms.util;

import java.util.Locale;

/**
 * Locale utility for resuable code. Originally added as a placeholder for application
 * locales such as SPANISH [ES] that are not supported as a java.util.Locale shortcut.
 */
public class LocaleUtils {

    /**
     * Useful constant for English language.
     */
    public static final Locale ENGLISH = Locale.ENGLISH;

    /**
     * Useful constant for Spanish language.
     */
    public static final Locale SPANISH = Locale.forLanguageTag("es");

    private LocaleUtils() {
        throw new IllegalStateException("Utility class, do not instantiate a new instance.");
    }
}
