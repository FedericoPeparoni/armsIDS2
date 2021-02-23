package ca.ids.abms.modules.flightmovements;

public class FlightMovementConstants {

    /**
     * Entry separator used when persisting arrival charge discounts.
     */
    static final String ARRIVAL_CHARGE_DISCOUNTS_ENTRY_SEPERATOR = ",";

    /**
     * Entry value separator used when persisting arrival charge discounts.
     */
    static final String ARRIVAL_CHARGE_DISCOUNTS_VALUE_SEPERATOR = "-";
    
    /**
     * EANA specific functionality. 
     * International flights of the same aircraft type for the same account could be charged
     * as cumulative flights if the billable distance is less then 200 km.
     * Cumulative flights have a special note MINIMUM/CUMULATIVE.
     */
    public static final Double EANA_MINIMUM_INTERNATIONAL_DISTANCE = 200.0;
    public static final String MINIMUM_CUMULATIVE = "MINIMUM/CUMULATIVE";


    private FlightMovementConstants() {
        throw new IllegalStateException("Constants class should never be instantiated!");
    }
}
