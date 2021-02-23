package ca.ids.abms.modules.util.models;

import java.math.BigDecimal;
import java.math.RoundingMode;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.currencies.Currency;

public class Calculation {

    /* From Java docs:
     * The RoundingMode.HALF_EVEN "is the rounding mode that statistically minimizes cumulative error when applied
     *  repeatedly over a sequence of calculations. It is sometimes known as Banker's Rounding, and is chiefly used
     *  in the USA. This rounding mode is analogous to the rounding policy used for float and double arithmetic
     *  in Java".
     */
    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;
    private static final int DEFAULT_DECIMALS = 2;

    private Calculation (){}

    public static double truncate (double value, int decimalPlaces) {
        return BigDecimal.valueOf(value).setScale(decimalPlaces, ROUNDING).doubleValue();
    }

    public static double truncate (final BigDecimal value, int decimalPlaces) {
        return value.setScale(decimalPlaces, ROUNDING).doubleValue();
    }

    public static Double roundByCurrencyDecimal (final Double value, final Currency currency) {
        int decimalPlaces = DEFAULT_DECIMALS;

        if (value != null) {
            if (currency != null && currency.getDecimalPlaces() != null) {
                decimalPlaces = currency.getDecimalPlaces();
            }
            return BigDecimal.valueOf(value).setScale(decimalPlaces, ROUNDING).doubleValue();
        }
        return value;
    }

    public static double divide (final BigDecimal dividend, final BigDecimal divisor, int decimalPlaces) {
        return dividend.divide(divisor, decimalPlaces, ROUNDING).doubleValue();
    }

    public static double roundUp(final double value) {
        return Math.ceil(value);
    }

    public static double roundDown(final double value) {
        return Math.floor(value);
    }

    public static double roundNormal(final double value) {
        return Math.round(value);
    }

    public enum MathOperator {
        ADD, SUBTRACT, MULTIPLY, DIVIDE
    }

    /** Math operations using BigDecimal for accuracy:
     * x-y
     * x+y
     * x*y
     * x/y
     */
    public static Double operation(Double x, Double y, MathOperator operator, Currency currency) {

        if (currency == null || currency.getDecimalPlaces() == null) {
            throw new CustomParametrizedException("Can't perform operation due to invalid input data");
        }

        return operation(x, y, operator, currency.getDecimalPlaces());
    }

    /** Math operations using BigDecimal for accuracy:
     * x-y
     * x+y
     * x*y
     * x/y
     */
    public static Double operation(Double x, Double y, MathOperator operator, Integer decimalPlaces) {

        if (x == null || y == null || operator == null || decimalPlaces == null) {
            throw new CustomParametrizedException("Can't perform operation due to invalid input data");
        }

        final BigDecimal xBigDecimal = BigDecimal.valueOf(x);
        final BigDecimal yBigDecimal = BigDecimal.valueOf(y);

        BigDecimal resultBigDecimal;
        switch(operator) {
            case ADD:
                resultBigDecimal = xBigDecimal.add(yBigDecimal);
                break;
            case SUBTRACT:
                resultBigDecimal = xBigDecimal.subtract(yBigDecimal);
                break;
            case MULTIPLY:
                resultBigDecimal = xBigDecimal.multiply(yBigDecimal);
                break;
            case DIVIDE:
                resultBigDecimal = xBigDecimal.divide(yBigDecimal, ROUNDING);
                break;
            default:
                throw new IllegalStateException("Operator is not supported");
        }

        return Calculation.truncate(resultBigDecimal, decimalPlaces);
    }
}
