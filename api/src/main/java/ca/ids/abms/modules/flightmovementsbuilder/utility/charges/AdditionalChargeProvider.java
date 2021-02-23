package ca.ids.abms.modules.flightmovementsbuilder.utility.charges;

import ca.ids.abms.modules.flightmovements.FlightMovement;

/**
 * Implement this interface to add a new charge provider. It will automatically be injected
 * into the `ChargesUtility` and be combined appropriately with arms core charges.
 */
public interface AdditionalChargeProvider {

    /**
     * Initialize charge provider specific amount(s).
     *
     * Used to clear/unset existing charge amounts before calculating any new charges.
     *
     * @param flightMovement flight movement to initialize charge
     */
    default void initialize(final FlightMovement flightMovement) {
        // default implementation ignored
    }

    /**
     * Calculate charge provider specific amount(s). Used to calculate and set any charge
     * amounts before totalling all flight movement charges.
     *
     * @param flightMovement flight movement to calculate and resolve charge amounts
     */
    default void calculate(final FlightMovement flightMovement) {
        // default implementation ignored
    }
}
