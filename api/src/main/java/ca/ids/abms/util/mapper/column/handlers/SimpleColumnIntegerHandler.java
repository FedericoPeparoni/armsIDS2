package ca.ids.abms.util.mapper.column.handlers;

import java.math.BigDecimal;

public class SimpleColumnIntegerHandler extends SimpleColumnNumberHandler<Integer> {

    /**
     * Convert abstract number type into decimal type to be used when setting precision and scale.
     */
    protected BigDecimal toDecimal(final Integer value) {
        return new BigDecimal(value);
    }

    /**
     * Convert decimal type into abstract number type used when returning result.
     */
    protected Integer toValue(final BigDecimal decimal) {
        return decimal.intValue();
    }
}
