package ca.ids.abms.modules.exemptions;

/**
 * Implement to include entity in charge exemption calculations. By default, all exemption
 * amounts are ignored.
 */
public interface ExemptionType {

    /**
     * Percentage to exempt enroute charges, default null.
     */
    default Double enrouteChargeExemption() {
        return null;
    }

    /**
     * Percentage to exempt late arrival charges, default null.
     */
    default Double lateArrivalChargeExemption() {
        return null;
    }

    /**
     * Percentage to exempt late departure charges, default null.
     */
    default Double lateDepartureChargeExemption() {
        return null;
    }

    /**
     * Percentage to exempt parking charges, default null.
     */
    default Double parkingChargeExemption() {
        return null;
    }

    /**
     * Percentage to exempt approach charges, default null.
     */
    default Double approachChargeExemption() {
        return null;
    }

    /**
     * Percentage to exempt aerodrome charges, default null.
     */
    default Double aerodromeChargeExemption() {
        return null;
    }

    /**
     * Percentage to exempt domestic pax charges, default null.
     */
    default Double domesticPaxChargeExemption() {
        return null;
    }

    /**
     * Percentage to exempt international pax charges, default null.
     */
    default Double internationalPaxChargeExemption() {
        return null;
    }

    /**
     * Percentage to exempt extended hours surcharge, default null.
     */
    default Double extendedHoursSurchargeExemption() {
        return null;
    }

    /**
     * Percentage to exempt unified tax, default null.
     */
    default Double unifiedTaxExemption() {
        return null;
    }

    
    /**
     * Flight note to add when exempting charges, default null.
     */
    default String flightNoteChargeExemption() {
        return null;
    }
}
