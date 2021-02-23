package ca.ids.abms.modules.flightmovementsbuilder.utility.discount;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import com.google.common.collect.Multimap;

/**
 * Implement this interface to add a new discount provider. It will automatically be injected
 * into the `DiscountUtility` and be combined appropriately with any other discounts applicable.
 */
interface DiscountProvider {

    /**
     * Implement this method to determine appropriate aerodrome charge discount to apply for the provided
     * flight movement and arrival aerodrome.
     *
     * @param flightMovement flight movement to discount
     * @param arrivalAd arrival aerodrome to discount
     * @param previousStops prior stops relative to arrival aerodrome being discounted
     * @return discount rate (default 1.0 and null will be ignored)
     */
    default Double getArrivalChargeDiscount(final FlightMovement flightMovement, final String arrivalAd,
                                            final Multimap<String, Integer> previousStops) {
        return DiscountConstants.FULL_RATE;
    }
}
