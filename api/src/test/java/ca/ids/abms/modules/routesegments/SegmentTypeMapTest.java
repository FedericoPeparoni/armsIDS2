package ca.ids.abms.modules.routesegments;

import ca.ids.abms.modules.common.enumerators.CrossingDistanceStrategy;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.enumerate.EnrouteChargesBasis;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementSource;
import com.vividsolutions.jts.geom.Geometry;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class SegmentTypeMapTest {

    @Test
    public void mapCrossingDistanceStrategyToSegmentTypeTest() {

        // assert that all applicable crossing distance strategies map to segment types
        assertThat(SegmentTypeMap.mapCrossingDistanceStrategyToSegmentType(CrossingDistanceStrategy.ATC_LOG))
            .isEqualTo(SegmentType.ATC);
        assertThat(SegmentTypeMap.mapCrossingDistanceStrategyToSegmentType(CrossingDistanceStrategy.NOMINAL))
            .isEqualTo(SegmentType.NOMINAL);
        assertThat(SegmentTypeMap.mapCrossingDistanceStrategyToSegmentType(CrossingDistanceStrategy.RADAR))
            .isEqualTo(SegmentType.RADAR);
        assertThat(SegmentTypeMap.mapCrossingDistanceStrategyToSegmentType(CrossingDistanceStrategy.SCHEDULED))
            .isEqualTo(SegmentType.SCHED);
        assertThat(SegmentTypeMap.mapCrossingDistanceStrategyToSegmentType(CrossingDistanceStrategy.TOWER_LOG))
            .isEqualTo(SegmentType.TOWER);
        assertThat(SegmentTypeMap.mapCrossingDistanceStrategyToSegmentType(CrossingDistanceStrategy.USER))
            .isEqualTo(SegmentType.USER);

        // assert that non applicable crossing distance strategies map to null
        assertThat(SegmentTypeMap.mapCrossingDistanceStrategyToSegmentType(CrossingDistanceStrategy.LARGEST))
            .isNull();
        assertThat(SegmentTypeMap.mapCrossingDistanceStrategyToSegmentType(CrossingDistanceStrategy.SMALLEST))
            .isNull();

        // assert that null does not throw an exception and maps to null
        assertThat(SegmentTypeMap.mapCrossingDistanceStrategyToSegmentType(null))
            .isNull();
    }

    @Test
    public void mapEnrouteCrossingBasisToSegmentTypeTest() {

        // assert that all applicable enroute charge basis map to segment types
        assertThat(SegmentTypeMap.mapEnrouteCrossingBasisToSegmentType(EnrouteChargesBasis.ATC_LOG))
            .isEqualTo(SegmentType.ATC);
        assertThat(SegmentTypeMap.mapEnrouteCrossingBasisToSegmentType(EnrouteChargesBasis.MANUAL))
            .isEqualTo(SegmentType.USER);
        assertThat(SegmentTypeMap.mapEnrouteCrossingBasisToSegmentType(EnrouteChargesBasis.NOMINAL))
            .isEqualTo(SegmentType.NOMINAL);
        assertThat(SegmentTypeMap.mapEnrouteCrossingBasisToSegmentType(EnrouteChargesBasis.SCHEDULED))
            .isEqualTo(SegmentType.SCHED);
        assertThat(SegmentTypeMap.mapEnrouteCrossingBasisToSegmentType(EnrouteChargesBasis.RADAR_SUMMARY))
            .isEqualTo(SegmentType.RADAR);
        assertThat(SegmentTypeMap.mapEnrouteCrossingBasisToSegmentType(EnrouteChargesBasis.TOWER_LOG))
            .isEqualTo(SegmentType.TOWER);

        // assert that null does not throw an exception and maps to null
        assertThat(SegmentTypeMap.mapEnrouteCrossingBasisToSegmentType(null))
            .isNull();
    }

    @Test
    public void mapFlightMovementSourceToSegmentTypeTest() {

        // assert that all applicable flight movement sources map to segment types
        assertThat(SegmentTypeMap.mapFlightMovementSourceToSegmentType(FlightMovementSource.ATC_LOG))
            .isEqualTo(SegmentType.ATC);
        assertThat(SegmentTypeMap.mapFlightMovementSourceToSegmentType(FlightMovementSource.MANUAL))
            .isEqualTo(SegmentType.SCHED);
        assertThat(SegmentTypeMap.mapFlightMovementSourceToSegmentType(FlightMovementSource.NETWORK))
            .isEqualTo(SegmentType.SCHED);
        assertThat(SegmentTypeMap.mapFlightMovementSourceToSegmentType(FlightMovementSource.RADAR_SUMMARY))
            .isEqualTo(SegmentType.RADAR);
        assertThat(SegmentTypeMap.mapFlightMovementSourceToSegmentType(FlightMovementSource.TOWER_LOG))
            .isEqualTo(SegmentType.TOWER);

        // assert that null does not throw an exception and maps to null
        assertThat(SegmentTypeMap.mapFlightMovementSourceToSegmentType(null))
            .isNull();
    }

    @Test
    public void mapSegmentTypeToFlightMovementGeometryTest() {

        FlightMovement flightMovement = MockFlightMovement.NEW();

        // assert that all applicable segment types map to flight movement geometry
        assertThat(SegmentTypeMap.mapSegmentTypeToFlightMovementGeometry(SegmentType.ATC, flightMovement))
            .isEqualTo(MockFlightMovement.ATC_LOG_TRACK);
        assertThat(SegmentTypeMap.mapSegmentTypeToFlightMovementGeometry(SegmentType.SCHED, flightMovement))
            .isEqualTo(MockFlightMovement.FPL_ROUTE_GEOM);
        assertThat(SegmentTypeMap.mapSegmentTypeToFlightMovementGeometry(SegmentType.RADAR, flightMovement))
            .isEqualTo(MockFlightMovement.RADAR_ROUTE);
        assertThat(SegmentTypeMap.mapSegmentTypeToFlightMovementGeometry(SegmentType.TOWER, flightMovement))
            .isEqualTo(MockFlightMovement.TOWER_LOG_TRACK);

        // assert that non applicable segment types map to null
        assertThat(SegmentTypeMap.mapSegmentTypeToFlightMovementGeometry(SegmentType.NOMINAL, flightMovement))
            .isNull();
        assertThat(SegmentTypeMap.mapSegmentTypeToFlightMovementGeometry(SegmentType.USER, flightMovement))
            .isNull();

        // assert that null does not throw an exception and maps to null
        assertThat(SegmentTypeMap.mapSegmentTypeToFlightMovementGeometry(null, null))
            .isNull();
        assertThat(SegmentTypeMap.mapSegmentTypeToFlightMovementGeometry(SegmentType.NOMINAL, null))
            .isNull();
        assertThat(SegmentTypeMap.mapSegmentTypeToFlightMovementGeometry(null, flightMovement))
            .isNull();
    }

    private static class MockFlightMovement {

        private static final Geometry ATC_LOG_TRACK = mock(Geometry.class);

        private static final Geometry FPL_ROUTE_GEOM = mock(Geometry.class);

        private static final Geometry RADAR_ROUTE = mock(Geometry.class);

        private static final Geometry TOWER_LOG_TRACK = mock(Geometry.class);

        private static FlightMovement NEW() {

            FlightMovement flightMovement = new FlightMovement();

            flightMovement.setAtcLogTrack(ATC_LOG_TRACK);
            flightMovement.setFplRouteGeom(FPL_ROUTE_GEOM);
            flightMovement.setRadarRoute(RADAR_ROUTE);
            flightMovement.setTowerLogTrack(TOWER_LOG_TRACK);

            return flightMovement;
        }
    }
}
