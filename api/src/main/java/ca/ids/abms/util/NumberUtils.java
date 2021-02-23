package ca.ids.abms.util;

import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.translation.Translation;
import com.ibm.icu.text.RuleBasedNumberFormat;
import com.ibm.icu.util.ULocale;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.StringJoiner;

public class NumberUtils {

    /**
     * Translation key for currency conjoining word value between dollar and cent amounts.
     */
    static final String CURRENCY_CONJOINING_VALUE = "CURRENCY_CONJOINING_VALUE";

    /**
     * Translation key for currency cent word value appending cent amounts.
     */
    static final String CURRENCY_CENTS_VALUE = "CURRENCY_CENTS_VALUE";

    /**
     * Format double with precision (fractional digits) of 2 as full text value.
     *
     * @param value number value to format
     * @return full text value
     */
    public static String formatAmountInFullText(final Double value, final Locale locale) {
        return formatAmountInFullText(value, 2, null, locale);
    }

    /**
     * Format double using currency code and currency decimal places as full text value.
     *
     * @param value number value to format
     * @param currency currency to format number value as
     * @return full text value with currency code
     */
    public static String formatAmountInFullTextWithCurrencyCode(
        final Double value, final Currency currency, final Locale locale
    ) {
        return formatAmountInFullTextWithCurrencyCode(value, currency, false, locale);
    }

    /**
     * Format double using currency code and currency decimal places as full text value.
     *
     * @param value number value to format
     * @param currency currency to format number value as
     * @param includePrefix include number value and symbol as prefix
     * @return full text value with currency code
     */
    public static String formatAmountInFullTextWithCurrencyCode(
        final Double value, final Currency currency, final Boolean includePrefix, final Locale locale
    ) {
        if (value == null)
            return null;

        final String currencyCode = currency != null && currency.getCurrencyCode() != null
            ? currency.getCurrencyCode() : "";
        final int precision = Math.max (0, currency != null && currency.getDecimalPlaces() != null
            ? currency.getDecimalPlaces() : 2);
        final String sign = value.compareTo(0.0d) >= 0 ? "" : "-";

        return includePrefix
            ? String.format ("%s%s %s", sign, NumberUtils.formatDouble(Math.abs(value), precision),
            NumberUtils.formatAmountInFullText(value, precision, currencyCode, locale))
            : NumberUtils.formatAmountInFullText(value, precision, currencyCode, locale);
    }

    /**
     * Format currency without the currency symbol.
     *
     * @param value number value to format
     * @param currency currency to format number value as
     * @return formatted value
     */
    public static String formatCurrency(final Double value, final Currency currency) {

        if (value == null) return null;
        if (currency == null) throw new IllegalArgumentException("currency argument cannot be null");

        final int fractionalDigits = Math.max(0, currency.getDecimalPlaces() == null
            ? 2 : currency.getDecimalPlaces());

        return formatDouble(value, fractionalDigits);
    }

    /**
     * Format currency, including the currency symbol.
     *
     * @param value number value to format
     * @param currency currency to format number value as
     * @return formatted value with currency symbol
     */
    public static String formatCurrencyWithSymbol(final Double value, final Currency currency) {

        if (value == null) return null;
        if (currency == null) throw new IllegalArgumentException("currency argument cannot be null");

        final String symbol = currency.getSymbol() == null
            ? "" : currency.getSymbol();

        final String sign = value.compareTo(0.0d) >= 0
            ? "" : "-";

        return String.format ("%s%s%s", sign, symbol, formatCurrency(Math.abs(value), currency));
    }

    /**
     * Format double with the given number of fractional digits.
     *
     * @param value number value to format
     * @param fractionalDigits decimal precision to use
     * @return formatted value
     */
    public static String formatDouble(final Double value, int fractionalDigits) {

        if (value == null) return null;

        final DecimalFormat formatter = new DecimalFormat();

        formatter.setMinimumFractionDigits(fractionalDigits);
        formatter.setMaximumFractionDigits(fractionalDigits);

        return formatter.format(value);
    }

    /**
     * Format double with given precision (fractional digits) and currency code as full text value.
     */
    private static String formatAmountInFullText(
        final Double value, final Integer precision, final String currencyCode, final Locale locale
    ) {
        if (value == null) return null;

        // fractional (aka decimal) part of the double value
        double fractionalPart = value % 1;

        // integral (whole) part of the double value
        double integralPart = value - fractionalPart;

        // multiplier used to calculate cents by 10 to the power of precision
        // shifting the decimal place to allow rounding the remainder
        int multiplier = (int) Math.pow(10, precision);

        // integer representation of dollar and cent amounts
        // use HALF_EVEN rounding to match formatDouble method
        int dollars = (int) integralPart;
        int cents = BigDecimal.valueOf(fractionalPart * multiplier)
            .setScale(0, RoundingMode.HALF_EVEN).intValue();

        // account for rounding up to next dollar and add to dollar amount
        // resulting in cents being equal to zero (mathematically absolute
        // value of cents should never be higher then the multiplier)
        if (Math.abs(cents) == multiplier) {
            dollars += cents / multiplier;
            cents = 0;
        }

        // build full text representation of value
        StringJoiner result = new StringJoiner(" ");

        // format dollar value in words using provided locale
        // locale is defaulted to ENGLISH if not defined
        result.add(numberFormatter(locale).format(dollars));

        // add currency code if exists in provided locale
        // locale is defaulted to ENGLISH if not defined
        if (currencyCode != null && !currencyCode.isEmpty()) {
            result.add(currencyCode);
        }

        // include cents with conjoining and cent translations only if cents is greater then 0
        if (Math.abs(cents) > 0) {

            // add currency conjoining translated value if defined
            String conjoiningTranslation = Translation.getLangByToken(CURRENCY_CONJOINING_VALUE, locale);
            if (StringUtils.isNotBlank(conjoiningTranslation)) result.add(conjoiningTranslation);

            // format cents value in words using provided locale
            // locale is defaulted to ENGLISH if not defined
            result.add(numberFormatter(locale).format(Math.abs(cents)));

            // add currency cents translated value if defined
            String centsTranslation = Translation.getLangByToken(CURRENCY_CENTS_VALUE, locale);
            if (StringUtils.isNotBlank(centsTranslation)) result.add(centsTranslation);
        }

        return result.toString();
    }

    /**
     * Returns spellout number formatter for provided locale. Attempts to use verbose numbering rule sets
     * where applicable.
     *
     * Defaults locale to {@link Locale#ENGLISH } if not provided.
     */
    private static RuleBasedNumberFormat numberFormatter(final Locale locale) {

        ULocale uLocale = ULocale.forLocale(locale != null ? locale : Locale.ENGLISH);
        RuleBasedNumberFormat numberFormatter = new RuleBasedNumberFormat(uLocale, RuleBasedNumberFormat.SPELLOUT);

        // set default rule set depending on locale language
        // we want to try to use verbose formats so conjoining words are used
        switch (uLocale.getLanguage()) {
            case "en":
                numberFormatter.setDefaultRuleSet("%spellout-numbering-verbose");
                break;
            case "es":
            case "fr":
            case "it":
                numberFormatter.setDefaultRuleSet("%spellout-numbering");
                break;
            default:
                // ignored, use locale specific default rule set
        }

        return numberFormatter;
    }

    private NumberUtils() {
        throw new IllegalStateException("Utility class, do not instantiate a new instance.");
    }
}
