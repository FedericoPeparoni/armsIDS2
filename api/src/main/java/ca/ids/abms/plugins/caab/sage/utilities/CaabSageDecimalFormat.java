package ca.ids.abms.plugins.caab.sage.utilities;

import java.text.DecimalFormat;

/**
 * CAAB Sage INTEGRATION database expects decimals stored in VARCHAR.
 *
 * For example, Double -123456.789 would be formatted as '-123456.79' when
 * converted to VARCHAR.
 */
public class CaabSageDecimalFormat {

    /**
     * Decimal format minimum and maximum fractional digits.
     */
    private static final Integer FRACTIONAL_DIGITS = 2;

    /**
     * Decimal formatter that allows minimum of zero fractional digits and
     * maximum of Integer.MAX_VALUE fractional digits without and grouping
     * separation used.
     */
    private static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat();
    static {
        DECIMAL_FORMATTER.setMinimumFractionDigits(FRACTIONAL_DIGITS);
        DECIMAL_FORMATTER.setMaximumFractionDigits(FRACTIONAL_DIGITS);
        DECIMAL_FORMATTER.setGroupingUsed(false);
    }

    /**
     * Format Double as String using the pattern '-####.##'.
     */
    public static String format(final Double value) {
        return DECIMAL_FORMATTER.format(value);
    }

    private CaabSageDecimalFormat() {
        throw new IllegalStateException("Utility class, do not instantiate a new instance.");
    }
}
