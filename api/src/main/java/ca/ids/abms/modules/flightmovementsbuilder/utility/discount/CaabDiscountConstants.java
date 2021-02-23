package ca.ids.abms.modules.flightmovementsbuilder.utility.discount;

class CaabDiscountConstants {

    /**
     * CAAB discount rate to apply on aerodrome charge when applicable.
     */
    static final Double ARRIVAL_DISCOUNT_RATE = 0.5;

    private CaabDiscountConstants() {
        throw new IllegalStateException("Constants class should never be instantiated!");
    }
}
