package ca.ids.abms.modules.routesegments;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import com.vividsolutions.jts.geom.Geometry;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RouteSegmentTest {

    @Test
    public void copyTest() {

        // copied
        RouteSegment routeSegmentOrg = MockRouteSegment.NEW();
        RouteSegment routeSegmentCopy = routeSegmentOrg
            .copy(SegmentType.NOMINAL);

        // assert that copy is not same reference pointer as original
        assertThat(routeSegmentOrg == routeSegmentCopy)
            .isFalse();

        // assert that copy route segment has new segment type
        assertThat(routeSegmentCopy.getSegmentType())
            .isEqualTo(SegmentType.NOMINAL);

        // assert that the rest of the properties are the same
        assertThat(routeSegmentCopy.getFlightMovement())
            .isEqualTo(routeSegmentOrg.getFlightMovement());
        assertThat(routeSegmentCopy.getLocation())
            .isEqualTo(routeSegmentOrg.getLocation());
        assertThat(routeSegmentCopy.getSegmentCost())
            .isEqualTo(routeSegmentOrg.getSegmentCost());
        assertThat(routeSegmentCopy.getSegmentEndLabel())
            .isEqualTo(routeSegmentOrg.getSegmentEndLabel());
        assertThat(routeSegmentCopy.getSegmentLength())
            .isEqualTo(routeSegmentOrg.getSegmentLength());
        assertThat(routeSegmentCopy.getSegmentNumber())
            .isEqualTo(routeSegmentOrg.getSegmentNumber());
        assertThat(routeSegmentCopy.getSegmentStartLabel())
            .isEqualTo(routeSegmentOrg.getSegmentStartLabel());
    }

    private static class MockRouteSegment {

        private static final FlightMovement FLIGHT_MOVEMENT = new FlightMovement();

        private static final Geometry LOCATION = null;

        private static final Double SEGMENT_COST = 0d;

        private static final String SEGMENT_END_LABEL = "MOCK_END_LABEL";

        private static final Double SEGMENT_LENGTH = 100d;

        private static final Integer SEGMENT_NUMBER = 1;

        private static final String SEGMENT_START_LABEL = "MOCK_START_LABEL";

        private static final SegmentType SEGMENT_TYPE = SegmentType.SCHED;

        private static RouteSegment NEW() {
            RouteSegment routeSegment = new RouteSegment();

            routeSegment.setFlightMovement(FLIGHT_MOVEMENT);
            routeSegment.setLocation(LOCATION);
            routeSegment.setSegmentCost(SEGMENT_COST);
            routeSegment.setSegmentEndLabel(SEGMENT_END_LABEL);
            routeSegment.setSegmentLength(SEGMENT_LENGTH);
            routeSegment.setSegmentNumber(SEGMENT_NUMBER);
            routeSegment.setSegmentStartLabel(SEGMENT_START_LABEL);
            routeSegment.setSegmentType(SEGMENT_TYPE);

            return routeSegment;
        }
    }
}
