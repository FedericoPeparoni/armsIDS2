package ca.ids.abms.modules.recalculation;

import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class FLMJobConstant {

    /**
     * Default user name to use for flight movement jobs when no user name could be resolved.
     */
    static final String DEFAULT_USER_NAME = "system";

    /**
     * List of flight movement statuses that should be excluded from all flight movement jobs.
     */
    static final List<FlightMovementStatus> EXCLUDED_FLM_STATUS = new ArrayList<>(Arrays.asList(
        FlightMovementStatus.CANCELED,
        FlightMovementStatus.DECLINED,
        FlightMovementStatus.DELETED,
        FlightMovementStatus.INVOICED,
        FlightMovementStatus.PAID));

    /**
     * List of flight movement statuses that should be included in all flight movement jobs.
     */
    static final List<FlightMovementStatus> INCLUDED_FLM_STATUS = new ArrayList<>(Arrays.asList(
        FlightMovementStatus.ACTIVE,
        FlightMovementStatus.INCOMPLETE,
        FlightMovementStatus.PENDING,
        FlightMovementStatus.OTHER));
    
    
    private FLMJobConstant() {
        throw new IllegalStateException("Utility class, do not instantiate a new instance.");
    }
}
