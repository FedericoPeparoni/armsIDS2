package ca.ids.abms.modules.routesegments;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by c.talpa on 05/01/2017.
 */
public class RouteSegmentServiceTest {

    private RouteSegmentRepository routeSegmentRepository;
    private RouteSegmentService routeSegmentService;


    @Before
    public void setup() {
        routeSegmentRepository = mock(RouteSegmentRepository.class);
        routeSegmentService = new RouteSegmentService(routeSegmentRepository);
    }

    @Test
    public void createRouteSegment() throws Exception {

        RouteSegment routeSegment= new RouteSegment();
        routeSegment.setSegmentCost(14.5);
        routeSegment.setSegmentLength(4554.2);
        routeSegment.setSegmentNumber(456);
        routeSegment.setSegmentType(SegmentType.ATC);

        when(routeSegmentRepository.save(any(RouteSegment.class))).thenReturn(routeSegment);

        RouteSegment result = routeSegmentService.create(routeSegment);
        assertThat(routeSegment.getSegmentNumber()).isEqualTo(result.getSegmentNumber());
    }

    @Test
    public void getAllRadarSummaries() throws Exception {

        List<RouteSegment> routeSegmentList = Collections.singletonList(new RouteSegment());

        when(routeSegmentRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(routeSegmentList));

        Page<RouteSegment> results = routeSegmentService.findAll(mock(Pageable.class));

        assertThat(results.getTotalElements()).isEqualTo(routeSegmentList.size());
    }

    @Test
    public void getRadarSummaryById() throws Exception {

        RouteSegment routeSegment = new RouteSegment();
        routeSegment.setId(1);

        when(routeSegmentRepository.findOne(1)).thenReturn(routeSegment);

        RouteSegment result = routeSegmentService.findOne(1);
        assertThat(result).isEqualTo(routeSegment);
    }


    @Test
    public void updateRadarSummary() throws Exception {
        RouteSegment routeSegment= new RouteSegment();
        routeSegment.setId(1);
        routeSegment.setSegmentCost(14.5);
        routeSegment.setSegmentLength(4554.2);
        routeSegment.setSegmentNumber(456);
        routeSegment.setSegmentType(SegmentType.ATC);

        RouteSegment updateRouteSegment= new RouteSegment();
        routeSegment.setSegmentCost(48957.5);

        when(routeSegmentService.findOne(1)).thenReturn(routeSegment);

        when(routeSegmentRepository.save(any(RouteSegment.class))).thenReturn(routeSegment);

        RouteSegment result = routeSegmentService.update(1, updateRouteSegment);

        assertThat(result.getSegmentCost()==updateRouteSegment.getSegmentCost());
    }

    @Test
    public void deleteRadarSummary() throws Exception {
        routeSegmentService.delete(1);
        verify(routeSegmentRepository).delete(any(Integer.class));
    }

}
