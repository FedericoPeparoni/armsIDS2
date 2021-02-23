package ca.ids.abms.modules.exemptions.charges.providers;

import ca.ids.abms.modules.exemptions.ExemptionType;
import ca.ids.abms.modules.flightmovements.FlightMovement;

import java.util.Collection;

public interface ExemptionChargeProvider {

    /**
     * Apply flight movement charge exemptions based on the exemptions provided.
     */
    void apply(final FlightMovement flightMovement, final Collection<ExemptionType> exemptions);
}
