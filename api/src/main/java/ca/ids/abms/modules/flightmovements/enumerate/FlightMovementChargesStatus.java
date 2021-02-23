package ca.ids.abms.modules.flightmovements.enumerate;

/**
 * Used by {enroute,passenger,other}_charges_status in the flight movements table
 */
public enum FlightMovementChargesStatus {
    PENDING,
    INVOICED,
    PAID
}
