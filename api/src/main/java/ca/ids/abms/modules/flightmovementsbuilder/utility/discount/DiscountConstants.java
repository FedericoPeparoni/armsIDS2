package ca.ids.abms.modules.flightmovementsbuilder.utility.discount;

import java.math.RoundingMode;

class DiscountConstants {

    /**
     * Full rate at which to multiple non-discounted charges. Also used as base rate for applying
     * discounts.
     */
    static final Double FULL_RATE = 1.0;

    /**
     * Rounding mode to use when combining multiple discount rates together.
     */
    static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * Rounding precision to use when combining multiple discounts rates together.
     */
    static final Integer ROUNDING_PRECISION = 2;

    private DiscountConstants() {
        throw new IllegalStateException("Constants class should never be instantiated!");
    }
}
