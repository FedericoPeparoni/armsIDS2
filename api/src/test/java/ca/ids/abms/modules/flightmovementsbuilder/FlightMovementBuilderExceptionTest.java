package ca.ids.abms.modules.flightmovementsbuilder;

import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementBuilderIssue;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by s.craymer on 25/08/2017.
 */
public class FlightMovementBuilderExceptionTest {

    FlightMovementBuilderException flightMovementBuilderException;

    /**
     * This method test the creation of the FlightMovementBuilderException by FlightMovementStatus
     */
    @Test
    public void flightMovementBuilderExceptionByStatusTest() {

        // Test 1: assert that canceled status maps correctly to canceled issue
    	FlightMovementBuilderException flightMovementBuilderException = new FlightMovementBuilderException(FlightMovementStatus.CANCELED);
        assertThat(flightMovementBuilderException.getFlightMovementBuilderIssue()).isEqualTo(FlightMovementBuilderIssue.FLIGHT_MOVEMENT_CANCELLED);

        // Test 1: assert that invoiced status maps correctly to invoiced issue
        flightMovementBuilderException = new FlightMovementBuilderException(FlightMovementStatus.INVOICED);
        assertThat(flightMovementBuilderException.getFlightMovementBuilderIssue()).isEqualTo(FlightMovementBuilderIssue.FLIGHT_MOVEMENT_INVOICED);

        // Test 1: assert that any other status maps correctly to default issue
        flightMovementBuilderException = new FlightMovementBuilderException(FlightMovementStatus.PENDING);
        assertThat(flightMovementBuilderException.getFlightMovementBuilderIssue()).isEqualTo(FlightMovementBuilderIssue.FLIGHT_MOVEMENT_NOT_EDITABLE);
    }
}
