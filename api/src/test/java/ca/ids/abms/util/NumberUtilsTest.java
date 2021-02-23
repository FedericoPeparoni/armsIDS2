package ca.ids.abms.util;

import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.translation.Translation;
import ca.ids.abms.modules.translation.TranslationRequired;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.when;

public class NumberUtilsTest extends TranslationRequired {

    @Before
    public void setup() {

        // add currency conjoining and cents translation values, assume English [EN] for test
        when(Translation.getLangByToken(NumberUtils.CURRENCY_CONJOINING_VALUE, null)).thenReturn("and");
        when(Translation.getLangByToken(NumberUtils.CURRENCY_CENTS_VALUE, null)).thenReturn("cents");
    }

    @Test
    public void formatAmountInFullTextTest() {

        // validate that null value returns null
        assertThat(NumberUtils.formatAmountInFullText(null, null))
            .isNull();

        // validate that whole number returns full text without points
        assertThat(NumberUtils.formatAmountInFullText(123d, null))
            .isEqualTo("one hundred and twenty-three");

        // validate that fractional number returns full text with cents rounded to 2 decimal places
        assertThat(NumberUtils.formatAmountInFullText(123.456, null))
            .isEqualTo("one hundred and twenty-three and forty-six cents");

        // validate that negative values are handled without different formatting
        assertThat(NumberUtils.formatAmountInFullText(-123.456, null))
            .isEqualTo("minus one hundred and twenty-three and forty-six cents");

        // validate that zero decimal value displays zero
        assertThat(NumberUtils.formatAmountInFullText(0.456, null))
            .isEqualTo("zero and forty-six cents");

        // validate that conjoining translation isn't included if null
        when(Translation.getLangByToken(NumberUtils.CURRENCY_CONJOINING_VALUE, null)).thenReturn(null);
        assertThat(NumberUtils.formatAmountInFullText(0.456, null))
            .isEqualTo("zero forty-six cents");

        // validate that conjoining translation isn't included if blank
        when(Translation.getLangByToken(NumberUtils.CURRENCY_CONJOINING_VALUE, null)).thenReturn("   ");
        assertThat(NumberUtils.formatAmountInFullText(0.456, null))
            .isEqualTo("zero forty-six cents");

        // validate that cents translation isn't included if null
        when(Translation.getLangByToken(NumberUtils.CURRENCY_CENTS_VALUE, null)).thenReturn(null);
        assertThat(NumberUtils.formatAmountInFullText(0.456, null))
            .isEqualTo("zero forty-six");

        // validate that cents translation isn't included if blank
        when(Translation.getLangByToken(NumberUtils.CURRENCY_CENTS_VALUE, null)).thenReturn("   ");
        assertThat(NumberUtils.formatAmountInFullText(0.456, null))
            .isEqualTo("zero forty-six");
    }

    @Test
    public void formatAmountInFullTextWithCurrencyCodeTest() {

        // mock currency object that is used for decimal and symbol formatting
        Currency currency = NumberUtilsTest.MOCK_CURRENCY.CURRENCY();

        // validate that null value returns null
        assertThat(NumberUtils.formatAmountInFullTextWithCurrencyCode(null, currency, null))
            .isNull();

        // validate that currency code and decimal places are used
        assertThat(NumberUtils.formatAmountInFullTextWithCurrencyCode(123.456, currency, null))
            .isEqualTo("one hundred and twenty-three MCK and four hundred and fifty-six cents");

        // validate that null currency uses default decimal places of 2 and no currency code
        assertThat(NumberUtils.formatAmountInFullTextWithCurrencyCode(123.456, null, null))
            .isEqualTo("one hundred and twenty-three and forty-six cents");

        // validate that negative values are handled with sign
        assertThat(NumberUtils.formatAmountInFullTextWithCurrencyCode(-123.456, currency, null))
            .isEqualTo("minus one hundred and twenty-three MCK and four hundred and fifty-six cents");

        // validate that currency code and decimal places are used
        assertThat(NumberUtils.formatAmountInFullTextWithCurrencyCode(123.456, currency, true, null))
            .isEqualTo("123.456 one hundred and twenty-three MCK and four hundred and fifty-six cents");

        // validate that null currency uses default decimal places and no currency code
        assertThat(NumberUtils.formatAmountInFullTextWithCurrencyCode(123.456, null, true, null))
            .isEqualTo("123.46 one hundred and twenty-three and forty-six cents");

        // validate that negative values are handled with sign
        assertThat(NumberUtils.formatAmountInFullTextWithCurrencyCode(-123.456, currency, true, null))
            .isEqualTo("-123.456 minus one hundred and twenty-three MCK and four hundred and fifty-six cents");

        // validate that conjoining translation isn't included if null
        when(Translation.getLangByToken(NumberUtils.CURRENCY_CONJOINING_VALUE, null)).thenReturn(null);
        assertThat(NumberUtils.formatAmountInFullTextWithCurrencyCode(0.456, currency, null))
            .isEqualTo("zero MCK four hundred and fifty-six cents");

        // validate that conjoining translation isn't included if blank
        when(Translation.getLangByToken(NumberUtils.CURRENCY_CONJOINING_VALUE, null)).thenReturn("   ");
        assertThat(NumberUtils.formatAmountInFullTextWithCurrencyCode(0.456, currency, null))
            .isEqualTo("zero MCK four hundred and fifty-six cents");

        // validate that cents translation isn't included if null
        when(Translation.getLangByToken(NumberUtils.CURRENCY_CENTS_VALUE, null)).thenReturn(null);
        assertThat(NumberUtils.formatAmountInFullTextWithCurrencyCode(0.456, currency, null))
            .isEqualTo("zero MCK four hundred and fifty-six");

        // validate that cents translation isn't included if blank
        when(Translation.getLangByToken(NumberUtils.CURRENCY_CENTS_VALUE, null)).thenReturn("   ");
        assertThat(NumberUtils.formatAmountInFullTextWithCurrencyCode(0.456, currency, null))
            .isEqualTo("zero MCK four hundred and fifty-six");
    }

    @Test
    public void formatCurrencyTest() {

        // mock currency object that is used for decimal and symbol formatting
        Currency currency = NumberUtilsTest.MOCK_CURRENCY.CURRENCY();

        // validate that number is formatted as expected
        assertThat(NumberUtils.formatCurrency(12345.6789, currency))
            .isEqualTo("12,345.679");

        // validate that sign is used
        assertThat(NumberUtils.formatCurrency(-12345.6789, currency))
            .isEqualTo("-12,345.679");

        // validate that 2 decimal places used when not defined in currency
        currency.setDecimalPlaces(null);
        assertThat(NumberUtils.formatCurrency(12345.6789, currency))
            .isEqualTo("12,345.68");

        // validate that 0 decimal places used when currency defines invalid value
        currency.setDecimalPlaces(-1);
        assertThat(NumberUtils.formatCurrency(12345.6789, currency))
            .isEqualTo("12,346");

        // validate that null value returns null
        assertThat(NumberUtils.formatCurrency(null, null))
            .isNull();

        try {
            // validate that exception is thrown when null currency
            NumberUtils.formatCurrency(1.0, null);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (final IllegalArgumentException ex) {
            // ignored as exception expected
        }

    }

    @Test
    public void formatCurrencyWithSymbolTest() {

        // mock currency object that is used for decimal and symbol formatting
        Currency currency = NumberUtilsTest.MOCK_CURRENCY.CURRENCY();

        // validate that number is formatted as expected
        assertThat(NumberUtils.formatCurrencyWithSymbol(12345.6789, currency))
            .isEqualTo("$12,345.679");

        // validate that sign is used
        assertThat(NumberUtils.formatCurrencyWithSymbol(-12345.6789, currency))
            .isEqualTo("-$12,345.679");

        // validate that no symbol is used when null
        currency.setSymbol(null);
        assertThat(NumberUtils.formatCurrencyWithSymbol(12345.6789, currency))
            .isEqualTo("12,345.679");

        // validate that null value returns null
        assertThat(NumberUtils.formatCurrencyWithSymbol(null, null))
            .isNull();

        try {
            // validate that exception is thrown when null currency
            NumberUtils.formatCurrencyWithSymbol(1.0, null);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (final IllegalArgumentException ex) {
            // ignored as exception expected
        }
    }

    @Test
    public void formatDoubleTest() {

        // validate that decimal places used
        assertThat(NumberUtils.formatDouble(12345.6789, 3))
            .isEqualTo("12,345.679");

        // validate that null value returns null
        assertThat(NumberUtils.formatDouble(null, 0))
            .isNull();
    }

    private static class MOCK_CURRENCY {

        private static final Boolean ACTIVE = true;

        private static final String CURRENCY_CODE = "MCK";

        private static final String CURRENCY_NAME = "Mock Currency";

        private static final Integer DECIMAL_PLACES = 3;

        private static final Integer ID = 0;

        private static final String SYMBOL = "$";

        private static Currency CURRENCY() {
            Currency currency = new Currency();

            currency.setActive(ACTIVE);
            currency.setCurrencyCode(CURRENCY_CODE);
            currency.setCurrencyName(CURRENCY_NAME);
            currency.setDecimalPlaces(DECIMAL_PLACES);
            currency.setId(ID);
            currency.setSymbol(SYMBOL);

            return currency;
        }
    }
}
