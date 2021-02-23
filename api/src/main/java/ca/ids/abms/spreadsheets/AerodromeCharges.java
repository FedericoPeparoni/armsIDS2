package ca.ids.abms.spreadsheets;

import java.time.LocalTime;

/**
 * General aerodrome charges calculator.
 * 
 * <p>This interface may be used to calculate approach,
 * late arrival, aerodrome, late departure and similar fees, which all use
 * the same spreadsheet format.'
 * 
 * <h2>Usage</h2>
 * <pre><code>
 * SSService svc = ...;
 * byte[] excelData = ...;
 * ...
 * // Get the charge for an international flight of 27 tons, at 17:23
 * double charge = svc.aerodromeCharges().load (excelData)
 *     .getCharge (27, LocalTime.of (17, 23), FlightChargeType.INTERNATIONAL);
 * </code></pre>
 * 
 */
public interface AerodromeCharges extends SSView {
    
    /**
     * Get the charge for the given MTOW, time of day and charge type.
     * 
     * @param mtow - maximum take-off weight (metric tons); must be >= 0
     * @param timeOfDay - the time of the charge
     * @param flightChargeType - international, domestic, etc
     * 
     * @throws NullPointerException - if any arguments are null
     * @throws IllegalArgumentException if mtow is < 0
     */
    public double getCharge (final double mtow, final LocalTime timeOfDay, final FlightChargeType flightChargeType) throws IllegalArgumentException, NullPointerException;
    
}
