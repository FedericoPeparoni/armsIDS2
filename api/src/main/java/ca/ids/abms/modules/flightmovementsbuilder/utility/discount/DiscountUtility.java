package ca.ids.abms.modules.flightmovementsbuilder.utility.discount;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Use this utility for discount rates on specific charges. This utility will loop through each implemented
 * discount provider and resolve rates sequentially starting from `1.0`.
 */
@Component
public class DiscountUtility {

    private final List<DiscountProvider> discountProviders;
    private final FlightMovementRepository flightMovementRepository;

    DiscountUtility(final List<DiscountProvider> discountProviders,
                    final FlightMovementRepository flightMovementRepository) {
        this.discountProviders = discountProviders;
        this.flightMovementRepository = flightMovementRepository;
    }

    /**
     * Find combined arrival charge discount percentage from prior arrival. Used when arrival charges are
     * applied on departure.
     *
     * @param arrivalAd arrival aerodrome to evaluate
     * @param priorArrival prior flight movement arrival
     * @return applicable arrival charge discount
     */
    public Double findArrivalChargeDiscount(final String arrivalAd, final FlightMovement priorArrival) {

        // only apply arrival charge discount if arrival aerodrome to evaluate
        // and prior flight movement exists with arrival charge discounts
        if (arrivalAd == null || arrivalAd.isEmpty() || priorArrival == null ||
            priorArrival.getArrivalChargeDiscounts() == null ||
            priorArrival.getArrivalChargeDiscounts().isEmpty()) {
            return 1.0;
        }
 
        // determine previous stops from prior arrival charge discounts
        Multimap<String, Integer> previousStops = priorArrival.getArrivalChargeDiscounts();

        // ensure previous stops are not null or empty
        if (previousStops == null || previousStops.isEmpty()) {
            return 1.0;
        }

        // find previous stop discount by last arrival for arrival aerodrome
        List<FlightMovement> list =flightMovementRepository.findPriorArrivalsByRegNum(priorArrival.getItem18RegNum(), arrivalAd, priorArrival.getDateOfFlight(), priorArrival.getDepTime());
        if(list == null || list.isEmpty()) {
            return 1.0;
        }
        return findPreviousStopDiscount(previousStops, arrivalAd);
    }

    /**
     * Apply combined arrival charge discount percentage from prior arrival. Used when arrival charges are
     * applied on departure.
     *
     * Only aerodrome charges are applied arrival charge discounts.
     *
     * @param flightMovement flight movement to evaluate
     * @param priorArrival prior flight movement arrival
     */
    public void applyArrivalChargeDiscount(final FlightMovement flightMovement, final FlightMovement priorArrival) {
        // apply arrival charge discount by last stop discount from prior arrival
        applyArrivalChargeDiscount(flightMovement, findArrivalChargeDiscount(flightMovement.getActualDepAd(), priorArrival));
    }

    /**
     * Apply combine arrival charge discount if applicable.
     *
     * Only aerodrome charges are applied arrival charge discounts.
     *
     * @param flightMovement flight movement to evaluate
     * @param discount combined discount to apply
     */
    public void applyArrivalChargeDiscount(final FlightMovement flightMovement, final Double discount) {

        // only apply charge if aerodrome charge and discount exists
        if (flightMovement.getAerodromeCharges() != null && discount != null) {
            double result = flightMovement.getAerodromeCharges() * discount;
            flightMovement.setAerodromeCharges(result);
        }
    }

    /**
     * Get combined arrival charge percentage discounts, limited to two decimal places. If discount
     * applied is 1.0, no discount was found.
     *
     * @param flightMovement flight movement to evaluate
     * @param arrivalAd specific arrival aerodrome to evaluate
     * @return percentage discount rounded to two decimal places (HALF_UP)
     */
    public Double getArrivalChargeDiscount(final FlightMovement flightMovement, final String arrivalAd) {
        return getArrivalChargeDiscount(flightMovement, arrivalAd, ImmutableListMultimap.of());
    }

    /**
     * Get combined arrival charge percentage discounts, limited to two decimal places. If discount
     * applied is 1.0, no discount was found.
     *
     * @param flightMovement flight movement to evaluate
     * @param arrivalAd specific arrival aerodrome to evaluate
     * @param previousStops prior stops relative to arrival aerodrome being discounted
     * @return percentage discount rounded to two decimal places (HALF_UP)
     */
    public Double getArrivalChargeDiscount(final FlightMovement flightMovement, final String arrivalAd,
                                           final Multimap<String, Integer> previousStops) {

        Double result = DiscountConstants.FULL_RATE;
        for (DiscountProvider discountProvider : discountProviders) {
            Double discount = discountProvider.getArrivalChargeDiscount(flightMovement, arrivalAd, previousStops);
            if (discount != null)
                result *= discount;
        }

        return BigDecimal.valueOf(result)
            .setScale(DiscountConstants.ROUNDING_PRECISION, DiscountConstants.ROUNDING_MODE)
            .doubleValue();
    }

    /**
     * Find discount of previous stops, must be LinkedListMultimap to maintain sort order.
     *
     * @param previousStops map of previous stops
     * @param arrivalAd arrival aerodrome to find
     * @return discount for previous stop or 100% if none found
     */
    public Double findPreviousStopDiscount(final Multimap<String, Integer> previousStops, final String arrivalAd) {

        // loop through each previous arrival stop for the arrival aerodrome
        // use the final stop to calculate discount amount
        Integer discount = 100;
        for (Integer item : previousStops.get(arrivalAd)) {
            discount = item;
        }
        return discount / 100d;
    }
}
