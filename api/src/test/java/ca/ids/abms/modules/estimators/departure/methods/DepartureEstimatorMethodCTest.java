package ca.ids.abms.modules.estimators.departure.methods;

import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.aerodromes.AerodromeRepository;
import ca.ids.abms.modules.airspaces.AirspaceRepository;
import ca.ids.abms.modules.estimators.departure.DepartureEstimatorModel;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocation;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocationRepository;
import ca.ids.abms.modules.util.models.NavDBUtils;
import ca.ids.abms.modules.util.models.geometry.CoordinatesVO;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DepartureEstimatorMethodCTest {

    private final GeometryFactory geometryFactory = new GeometryFactory (new PrecisionModel(), 4326);

    private AerodromeRepository aerodromeRepository;
    private FlightMovementRepository flightMovementRepository;
    private NavDBUtils navDBUtils;
    private UnspecifiedDepartureDestinationLocationRepository unspecifiedDepartureDestinationLocationRepository;
    private DepartureEstimatorMethodC estimator;

    @Before
    public void setup() {
        AirspaceRepository airspaceRepository = mock(AirspaceRepository.class);

        aerodromeRepository = mock(AerodromeRepository.class);
        flightMovementRepository = mock(FlightMovementRepository.class);
        navDBUtils = mock(NavDBUtils.class);
        unspecifiedDepartureDestinationLocationRepository = mock(UnspecifiedDepartureDestinationLocationRepository.class);

        estimator = new DepartureEstimatorMethodC(aerodromeRepository, airspaceRepository, flightMovementRepository,
            navDBUtils, unspecifiedDepartureDestinationLocationRepository);
    }

    @Test
    public void testParseTime() {
        assertThat (DepartureEstimatorMethodC.tryParseTime (null)).isNull();
        assertThat (DepartureEstimatorMethodC.tryParseTime (" ")).isNull();
        assertThat (DepartureEstimatorMethodC.tryParseTime ("abc")).isNull();
        assertThat (DepartureEstimatorMethodC.tryParseTime ("1455")).isEqualTo(LocalTime.of (14, 55));
    }

    @Test
    public void testParseCruisingSpeed() {
        assertThat (DepartureEstimatorMethodC.tryParseCruisingSpeed (null)).isNull();
        assertThat (DepartureEstimatorMethodC.tryParseCruisingSpeed (" ")).isNull();
        assertThat (DepartureEstimatorMethodC.tryParseCruisingSpeed (" abc ")).isNull();
        assertThat (DepartureEstimatorMethodC.tryParseCruisingSpeed (" K100 ")).isCloseTo(27.7, within (0.1));
        assertThat (DepartureEstimatorMethodC.tryParseCruisingSpeed (" N100 ")).isCloseTo(51.4, within (0.1));
        assertThat (DepartureEstimatorMethodC.tryParseCruisingSpeed ("  100 ")).isCloseTo(51.4, within (0.1));
    }

    @Test
    public void testResolveAerodrome() {

        // find by aerodrome
        {
            final Aerodrome a = new Aerodrome();
            a.setGeometry(geometryFactory.createPoint (new Coordinate (11, 22)));
            when (aerodromeRepository.findByAerodromeName ("BY_AERODROME")).thenReturn(a);
            final Coordinate result = estimator.tryResolveAerodrome("BY_AERODROME");
            assertThat (result.x).isEqualTo (11d);
        }

        // find by unspec location
        {
            final UnspecifiedDepartureDestinationLocation loc = new UnspecifiedDepartureDestinationLocation();
            loc.setLatitude(33d);
            loc.setLongitude(44d);
            when(unspecifiedDepartureDestinationLocationRepository.findAllByTextIdentifierOrderById("BY_UNSPEC_LOC"))
                .thenReturn(Collections.singletonList(loc));
            when(unspecifiedDepartureDestinationLocationRepository.findAllByAerodromeIdentifierAerodromeNameOrderById("BY_UNSPEC_LOC"))
                .thenReturn(Collections.singletonList(loc));
            final Coordinate result = estimator.tryResolveAerodrome("BY_UNSPEC_LOC");
            assertThat (result.x).isEqualTo (44d);
        }

        // find by NavDB location
        {
            final CoordinatesVO coord = new CoordinatesVO (55d, 66d);
            when (navDBUtils.getCoordinatesFromAirportNAVDB ("BY_NAVDB"))
                    .thenReturn (coord);
            final Coordinate result = estimator.tryResolveAerodrome("BY_NAVDB");
            assertThat (result.x).isEqualTo (66d);
        }

        // find invalid aerodrome
        {
            final Coordinate result = estimator.tryResolveAerodrome("INVALID");
            assertThat (result).isNull();
        }
    }

    @Test
    public void testResolveCruisingSpeed() {

        when(flightMovementRepository.findLatestCruisingSpeedByRegistrationNumber("MOCK_REG_NUM"))
            .thenReturn("REG_NUM_SPEED");
        when(flightMovementRepository.findLatestCruisingSpeedByAircraftType("MOCK_AIRCRAFT_TYPE"))
            .thenReturn("AIRCRAFT_TYPE_SPEED");

        // find by provided value
        DepartureEstimatorModel model = new DepartureEstimatorModel.Builder("MOCK_MODEL")
            .cruisingSpeed("MOCK_CRUISING_SPEED").build();
        assertThat(estimator.tryResolveCruisingSpeed(model))
            .isEqualTo("MOCK_CRUISING_SPEED");

        // find by registration number
        model = new DepartureEstimatorModel.Builder("MOCK_MODEL")
            .regNum("MOCK_REG_NUM").aircraftType("MOCK_AIRCRAFT_TYPE").build();
        assertThat(estimator.tryResolveCruisingSpeed(model))
            .isEqualTo("REG_NUM_SPEED");

        // find by aircraft type
        when(flightMovementRepository.findLatestCruisingSpeedByRegistrationNumber("MOCK_REG_NUM"))
            .thenReturn(null);
        assertThat(estimator.tryResolveCruisingSpeed(model))
            .isEqualTo("AIRCRAFT_TYPE_SPEED");

        // find by all failed
        when(flightMovementRepository.findLatestCruisingSpeedByAircraftType("MOCK_AIRCRAFT_TYPE"))
            .thenReturn(null);
        assertThat(estimator.tryResolveCruisingSpeed(model))
            .isNull();
    }

    @Test
    public void testResolveEntryWaypoint() {

        // find by DMS
        {
            final Coordinate result = estimator.tryResolveEntryWaypoint("010203N0040506E", new Coordinate (0, 0));
            assertThat (result.x).isCloseTo(4.08, within (0.01));
        }

        // find by unspec location
        {
            final UnspecifiedDepartureDestinationLocation loc = new UnspecifiedDepartureDestinationLocation();
            loc.setLatitude(33d);
            loc.setLongitude(44d);
            when(unspecifiedDepartureDestinationLocationRepository.findAllByTextIdentifierOrderById("BY_UNSPEC_LOC"))
                .thenReturn(Collections.singletonList(loc));
            when(unspecifiedDepartureDestinationLocationRepository.findAllByAerodromeIdentifierAerodromeNameOrderById("BY_UNSPEC_LOC"))
                .thenReturn(Collections.singletonList(loc));
            final Coordinate result = estimator.tryResolveEntryWaypoint("BY_UNSPEC_LOC", new Coordinate (0, 0));
            assertThat (result.x).isEqualTo (44d);
        }

        // find by NavDB location
        {
            final Coordinate coord = new Coordinate (55d, 66d);
            when (navDBUtils.findClosestSignificantPoint (any(), any(), any(), any(), any()))
                    .thenReturn (coord);
            final Coordinate result = estimator.tryResolveEntryWaypoint("BY_NAVDB", new Coordinate (0, 0));
            assertThat (result.x).isEqualTo (55d);
        }
    }
}
