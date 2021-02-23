package ca.ids.abms.modules.flightmovementsbuilder.utility;

import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.airspaces.AirspaceService;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.flightmovements.FlightMovementAerodromeService;
import ca.ids.abms.modules.flightmovements.FlightMovementAircraftService;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.mapper.RouteCacheSegmentMapperImpl;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.vo.RouteSegmentVO;
import ca.ids.abms.modules.mtow.AverageMtowFactorService;
import ca.ids.abms.modules.routesegments.RouteSegment;
import ca.ids.abms.modules.routesegments.SegmentType;
import ca.ids.abms.modules.system.SystemConfigurationService;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class FlightMovementBuilderUtilityTest {

    private FlightMovementBuilderUtility flightMovementBuilderUtility;

    @Before
    public void setup() {
        this.flightMovementBuilderUtility = new FlightMovementBuilderUtility(mock(FlightMovementAerodromeService.class),
            mock(FlightMovementAircraftService.class), mock(AccountService.class), mock(AirspaceService.class),
            mock(AverageMtowFactorService.class), mock(RouteParserWrapper.class), new RouteCacheSegmentMapperImpl(),
            mock(FlightMovementRepository.class), mock(SystemConfigurationService.class));
    }

    @Test
    public void mergeTheSegmentListTest() {

        List<RouteSegmentVO> newSegments = new ArrayList<>();
        newSegments.add(mock(RouteSegmentVO.class));

        // assert that new segments are mapped and added to existing list
        assertThat(flightMovementBuilderUtility
            .mergeTheSegmentsList(MockRouteSegment.LIST(), newSegments, SegmentType.RADAR).size())
            .isEqualTo(2);

        // assert that existing segment types are overwritten
        assertThat(flightMovementBuilderUtility
            .mergeTheSegmentsList(MockRouteSegment.LIST(), newSegments, MockRouteSegment.SEGMENT_TYPE).size())
            .isEqualTo(1);

        // assert that null existing segment list is handled
        assertThat(flightMovementBuilderUtility
            .mergeTheSegmentsList(null, newSegments, SegmentType.RADAR).size())
            .isEqualTo(1);
    }

    private static class MockRouteSegment {

        private static final SegmentType SEGMENT_TYPE = SegmentType.SCHED;

        private static RouteSegment NEW() {
            RouteSegment routeSegment = new RouteSegment();
            routeSegment.setSegmentType(SEGMENT_TYPE);
            return routeSegment;
        }

        private static List<RouteSegment> LIST() {
            List<RouteSegment> list = new ArrayList<>();
            list.add(NEW());
            return list;
        }
    }
}
