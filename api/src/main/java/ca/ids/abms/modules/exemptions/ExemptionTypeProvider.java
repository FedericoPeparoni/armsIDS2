package ca.ids.abms.modules.exemptions;

import ca.ids.abms.modules.flightmovements.FlightMovement;

import java.util.Collection;
import java.util.Collections;

/**
 * Implement as bean to include in applied exemptions. By default, no exemption is applied.
 */
public interface ExemptionTypeProvider {

    /**
     * Find applicable exemption type from flight movement provided, default is an empty list.
     */
    default Collection<ExemptionType> findApplicableExemptions(final FlightMovement flightMovement) {
        return Collections.emptyList();
    }
}
