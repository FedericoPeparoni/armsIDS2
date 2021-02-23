package ca.ids.abms.modules.flightmovementsbuilder.utility;

import ca.ids.abms.modules.airspaces.AirspaceService;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementAerodromeService;
import ca.ids.abms.modules.flightmovementsbuilder.vo.ThruFlightPlanVO;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocationService;
import ca.ids.abms.modules.util.models.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ThruFlightPlanUtilityTest {

    private FlightMovementAerodromeService flightMovementAerodromeService;

    private ThruFlightPlanUtility thruFlightPlanUtility;

    @Before
    public void setup() {
        AirspaceService airspaceService = mock(AirspaceService.class);
        RouteParserWrapper routeParserWrapper = mock(RouteParserWrapper.class);
        UnspecifiedDepartureDestinationLocationService unspecifiedDepartureDestinationLocationService =
            mock(UnspecifiedDepartureDestinationLocationService.class);
        SystemConfigurationService systemConfigurationService = mock(SystemConfigurationService.class);

        flightMovementAerodromeService = mock(FlightMovementAerodromeService.class);
        thruFlightPlanUtility = new ThruFlightPlanUtility(airspaceService, flightMovementAerodromeService,
            routeParserWrapper, unspecifiedDepartureDestinationLocationService, systemConfigurationService);

        when(flightMovementAerodromeService.isAerodromeDomestic(anyString()))
            .thenReturn(true);
        when(flightMovementAerodromeService.resolveAnyLocationToDMS(anyString()))
            .thenReturn("mock_coordinate");
        when(systemConfigurationService.getIntOrZero(SystemConfigurationItemName.THRU_PLAN_ESTIMATED_STOP_TIME))
            .thenReturn(MockFlightMovement.THRU_FLIGHT_STOP_TIME);
    }

    @Test
    public void isThruFlightTest() {

        FlightMovement flightMovement = MockFlightMovement.FLIGHT_MOVEMENT();

        // validate when thru flight passed, true is returned
        when(flightMovementAerodromeService.isAerodromeDomestic(flightMovement.getDepAd(), flightMovement.getDepAd(), true))
            .thenReturn(true);
        assertThat(thruFlightPlanUtility.isThruFlight(flightMovement))
            .isTrue();

        // validate when route is empty, false is returned
        flightMovement.setFplRoute("");
        assertThat(thruFlightPlanUtility.isThruFlight(flightMovement))
            .isFalse();

        // validate when route is null, false is returned
        flightMovement.setFplRoute(null);
        assertThat(thruFlightPlanUtility.isThruFlight(flightMovement))
            .isFalse();

        // validate when coordinates are not domestic, false is returned
        when(flightMovementAerodromeService.isAerodromeDomestic(flightMovement.getDepAd()))
            .thenReturn(false);
        assertThat(thruFlightPlanUtility.isThruFlight(flightMovement))
            .isFalse();
    }

    @Test
    public void parseThruPlanRouteTest() {

        // validate that empty thru flight plan list returned when invalid
        assertThat(thruFlightPlanUtility.parseThruPlanRoute(null, null, null))
            .isNotNull();

        // parse thru plan route and validate results below
        List<ThruFlightPlanVO> results = thruFlightPlanUtility.parseThruPlanRoute(MockFlightMovement.ROUTE(),
            MockFlightMovement.DEP_AD, MockFlightMovement.DEP_TIME());

        // validate that thru flight plan list of size two return when valid route
        assertThat(results.size())
            .isEqualTo(2);

        // validate that result item 1 values are valid
        assertThat(results.get(0).getDepAd())
            .isEqualTo(MockFlightMovement.DEP_AD);
        assertThat(results.get(0).getDepTime())
            .isEqualTo(MockFlightMovement.DEP_TIME());
        assertThat(results.get(0).getCrusingSpeed())
            .isEqualTo(null);
        assertThat(results.get(0).getFlightLevel())
            .isEqualTo(null);
        assertThat(results.get(0).getEet())
            .isEqualTo(MockFlightMovement.EET);
        assertThat(results.get(0).getArrivalTime())
            .isEqualTo(MockFlightMovement.ARRIVAL_TIME());
        assertThat(results.get(0).getDestAd())
            .isEqualTo(MockFlightMovement.DEST_AD);

        // validate that result item 2 values are valid
        assertThat(results.get(1).getDepAd())
            .isEqualTo(MockFlightMovement.THRU_DEP_AD);
        assertThat(results.get(1).getDepTime())
            .isEqualTo(MockFlightMovement.THRU_DEP_TIME());
        assertThat(results.get(1).getCrusingSpeed())
            .isEqualTo(null);
        assertThat(results.get(1).getFlightLevel())
            .isEqualTo(null);
        assertThat(results.get(1).getEet())
            .isEqualTo(MockFlightMovement.THRU_EET);
        assertThat(results.get(1).getArrivalTime())
            .isEqualTo(MockFlightMovement.THRU_ARRIVAL_TIME());
        assertThat(results.get(1).getDestAd())
            .isEqualTo(MockFlightMovement.THRU_DEST_AD);
    }

    @Test
    public void parseThruPlanRouteAltTest() {

        // build alternative route without space between EET AD and no optional values
        StringJoiner altRoute = new StringJoiner(" ");
        altRoute.add("TRU PLN");
        altRoute.add(MockFlightMovement.EET + MockFlightMovement.DEST_AD);
        altRoute.add(MockFlightMovement.THRU_EET + MockFlightMovement.THRU_DEST_AD);

        // parse alternative thru plan route and validate results below
        List<ThruFlightPlanVO> results = thruFlightPlanUtility.parseThruPlanRoute(altRoute.toString(),
            MockFlightMovement.DEP_AD, MockFlightMovement.DEP_TIME());

        // validate that result item 1 values are valid
        assertThat(results.get(0).getDepAd())
            .isEqualTo(MockFlightMovement.DEP_AD);
        assertThat(results.get(0).getDepTime())
            .isEqualTo(MockFlightMovement.DEP_TIME());
        assertThat(results.get(0).getCrusingSpeed())
            .isEqualTo(null);
        assertThat(results.get(0).getFlightLevel())
            .isEqualTo(null);
        assertThat(results.get(0).getEet())
            .isEqualTo(MockFlightMovement.EET);
        assertThat(results.get(0).getArrivalTime())
            .isEqualTo(MockFlightMovement.ARRIVAL_TIME());
        assertThat(results.get(0).getDestAd())
            .isEqualTo(MockFlightMovement.DEST_AD);

        // validate that result item 2 values are valid
        assertThat(results.get(1).getDepAd())
            .isEqualTo(MockFlightMovement.THRU_DEP_AD);
        assertThat(results.get(1).getDepTime())
            .isEqualTo(MockFlightMovement.THRU_DEP_TIME());
        assertThat(results.get(1).getCrusingSpeed())
            .isEqualTo(null);
        assertThat(results.get(1).getFlightLevel())
            .isEqualTo(null);
        assertThat(results.get(1).getEet())
            .isEqualTo(MockFlightMovement.THRU_EET);
        assertThat(results.get(1).getArrivalTime())
            .isEqualTo(MockFlightMovement.THRU_ARRIVAL_TIME());
        assertThat(results.get(1).getDestAd())
            .isEqualTo(MockFlightMovement.THRU_DEST_AD);
    }

    private static class MockFlightMovement {

        private static final LocalDateTime DATE_OF_FLIGHT = LocalDateTime.now();

        private static final String DEP_AD = "WXYZ";

        private static final String DEP_TIME = "0930";

        private static final String DEST_AD = "ABCD";

        private static final String EET = "0050";

        private static final String THRU_CRUISING_SPEED = "N0230";

        private static final String THRU_DEP_AD = DEST_AD;

        private static final String THRU_DEST_AD = "LMNO";

        private static final String THRU_EET = "0110";

        private static final String THRU_FLIGHT_LEVEL = "F190";

        private static final Integer THRU_FLIGHT_STOP_TIME = 20;

        private static LocalDateTime ARRIVAL_TIME() {
            return DateTimeUtils.addTimeToDate(DEP_TIME(), EET);
        }

        private static LocalDateTime DEP_TIME() {
            return DateTimeUtils.addTimeToDate(DATE_OF_FLIGHT, DEP_TIME);
        }

        private static FlightMovement FLIGHT_MOVEMENT() {
            FlightMovement flightMovement = new FlightMovement();

            flightMovement.setDepAd(DEP_AD);
            flightMovement.setDestAd(DEST_AD);
            flightMovement.setFplRoute(ROUTE());

            return flightMovement;
        }

        private static String ROUTE() {
            StringJoiner route = new StringJoiner(" ");

            route.add("THRU PLAN");
            route.add(EET);
            route.add(DEST_AD);
            route.add("/");
            route.add(THRU_CRUISING_SPEED);
            route.add(THRU_FLIGHT_LEVEL);
            route.add(THRU_EET);
            route.add(THRU_DEST_AD);

            return route.toString();
        }

        private static LocalDateTime THRU_ARRIVAL_TIME() {
            return DateTimeUtils.addTimeToDate(THRU_DEP_TIME(), THRU_EET);
        }

        private static LocalDateTime THRU_DEP_TIME() {
            return ARRIVAL_TIME().plusMinutes(THRU_FLIGHT_STOP_TIME);
        }
    }
}
