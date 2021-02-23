package ca.ids.abms.spreadsheets;

import java.time.LocalDateTime;

/**
 * Parking charges calculator.
 * 
 * <h2>Usage</h2>
 * <pre><code>
 * SSService svc = ...;
 * byte[] excelData = ...;
 * ...
 * // Get the parking charge for an international flight of 27 tons, which arrived
 * // at 17:23 and departed at 20:05 on Jan 12 2017
 * double charge = svc.aerodromeCharges().load (excelData)
 *     .getCharge (
 *          27,
 *          LocalDateTime.parse ("2017-01-12T17:23:00"),
 *          LocalDateTime.parse ("2017-01-12T20:05:00"),
 *          FlightChargeType.INTERNATIONAL);
 * </code></pre>
 * 
 */
public interface ParkingCharges extends SSView {
    
    /**
     * Get parking charge given the flight's tonnage, arrival time, departure time and charge type.
     *
     * @param mtow              -- maximum take-off weight (metric tons); must be >= 0
     * @param arrivalTimeUtc    -- UTC arrival time, must be <= departureTimeUtc
     * @param departureTimeUtc  -- UTC departure time, must be >= arrivalTimeUtc
     * @param flightChargeType  -- charge type (international, domestic, etc).
     * 
     * @throws NullPointerException - if any arguments are null
     * @throws IllegalArgumentException - if mtow is < 0 or arrivalTimeUtc > departureTimeUtc
     */
    public double getCharge (final double mtow, final LocalDateTime arrivalTimeUtc, final LocalDateTime departureTimeUtc, final FlightChargeType flightChargeType);

}
