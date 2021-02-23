package ca.ids.abms.modules.flightmovements.enumerate;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

/**
 * Flight types (regional, domestic, etc)
 *
 * @deprecated - use FM categories instead
 */
@Deprecated
public enum FlightMovementType {

    DOMESTIC (true, true),
    REG_OVERFLIGHT (false, false),
    REG_ARRIVAL (false, true),
    REG_DEPARTURE (true, false),
    INT_OVERFLIGHT (false, false),
    INT_ARRIVAL (false, true),
    INT_DEPARTURE (true, false),
    OTHER (false, false),
    OVERFLIGHT (false, false);

    /**
     * Returns true if flights of this type depart from a domestic airport
     */
    @JsonIgnore
    public boolean isDepartureDomestic() {
        return this.isDepartureDomestic;
    }
    
    /**
     * Returns true if flights of this type arrive to a domestic airport
     */
    @JsonIgnore
    public boolean isDestinationDomestic() {
        return this.isDestinationDomestic;
    }
    
    /**
     * Format to a string suitable for storing in the database, etc.
     */
    @JsonValue
    public String toValue() {
        return this.name();
    }
    
    /**
     * Parse an external representation of this enum; returns null on errors
     */
    @JsonCreator
    public static FlightMovementType forValue (final String externalId) {
        if (externalId != null) {
            try {
                return FlightMovementType.valueOf (externalId.toUpperCase (Locale.US));
            }
            catch (final IllegalArgumentException x) {
            }
        }
        return null;
    }
    
    /**
     * Parse an external representation of this enum; throws exceptions on errors
     */
    public static FlightMovementType parseExternal (final String externalId) {
        return FlightMovementType.valueOf (externalId);
    }
    
    // ------------------- private --------------------

    private FlightMovementType (final boolean isDepartureDomestic, final boolean isDestinationDomestic) {
        this.isDepartureDomestic = isDepartureDomestic;
        this.isDestinationDomestic = isDestinationDomestic;
        
    }
    
    private final boolean isDepartureDomestic;
    private final boolean isDestinationDomestic;
    
}
