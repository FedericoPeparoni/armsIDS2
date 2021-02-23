package ca.ids.abms.util.mapper.column.handlers;

import ca.ids.abms.util.mapper.column.SimpleColumnHandler;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public abstract class SimpleColumnNumberHandler<T extends Number> implements SimpleColumnHandler<T> {

    /**
     * Rounding mode used when required for precision and scale.
     */
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * Simple rounding of object field with BigDecimal rounding from Column.precision and
     * Column.scale values.
     */
    public T handle(final Column column, final T value) {

        // round value using column precision and scale, null is left untouched
        if (value != null)
            return round(value, column.precision(), column.scale());
        else
            return null;
    }

    /**
     * Round integer value based on field precision and scale using defined rounding mode.
     */
    private T round(final T value, final Integer precision, final Integer scale) {

        if (value == null || precision == null || scale == null)
            throw new IllegalArgumentException("Arguments cannot be null.");

        // forcing math context precision and scale using simple math rounding
        BigDecimal result = toDecimal(value)
            .setScale(scale, ROUNDING_MODE)
            .round(new MathContext(precision, ROUNDING_MODE));

        // return and abstract number value type
        return toValue(result);
    }

    /**
     * Convert abstract number type into decimal type to be used when setting precision and scale.
     */
    protected abstract BigDecimal toDecimal(final T value);

    /**
     * Convert decimal type into abstract number type used when returning result.
     */
    protected abstract T toValue(final BigDecimal decimal);
}
