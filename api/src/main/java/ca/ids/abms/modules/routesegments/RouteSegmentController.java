package ca.ids.abms.modules.routesegments;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementSource;

/**
 * Created by c.talpa on 23/02/2017.
 */
@RestController
@RequestMapping("/api/route-segments")
public class RouteSegmentController {

    private final Logger LOG = LoggerFactory.getLogger(RouteSegmentController.class);

    private RouteSegmentService routeSegmentService;

    private RouteSegmentMapper routeSegmentMapper;

    public RouteSegmentController(RouteSegmentService routeSegmentService, RouteSegmentMapper routeSegmentMapper){
        this.routeSegmentMapper=routeSegmentMapper;
        this.routeSegmentService=routeSegmentService;
    }



    @GetMapping
    public ResponseEntity<Page<RouteSegmentViewModel>> getAllRouteSegment(@SortDefault(sort = {"dayOfFlight"}, direction = Sort.Direction.DESC) Pageable pageable) {
        LOG.debug("REST request to get all RouteSegment");
        final Page<RouteSegment> page=routeSegmentService.findAll(pageable);
        final Page<RouteSegmentViewModel> resultPage= new PageImpl<>(routeSegmentMapper.toViewModel(page),pageable,page.getTotalElements());
        return ResponseEntity.ok().body(resultPage);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<RouteSegmentViewModel> getRouteSegment(@PathVariable Integer id) {
        LOG.debug("REST request to get RouteSegment : {}", id);

        RouteSegment radarSummary = routeSegmentService.findOne(id);

        return Optional.ofNullable(radarSummary)
            .map(result -> new ResponseEntity<>(routeSegmentMapper.toViewModel(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }



    @RequestMapping(value = "/find-by-type/{segmentType}", method = RequestMethod.GET)
    public ResponseEntity<Page<RouteSegmentViewModel>> finAllRouteSegmentBySegmentType(Pageable pageable, @PathVariable SegmentType segmentType) {
        LOG.debug("REST request to get All RouteSegment By SegmentType : " + segmentType);
        final Page<RouteSegment> page = routeSegmentService.findAllBySegmentType(pageable,segmentType);
        final Page<RouteSegmentViewModel> resultPage = new PageImpl<>(routeSegmentMapper.toViewModel(page), pageable, page.getTotalElements());
        return ResponseEntity.ok().body(resultPage);
    }

    @RequestMapping(value = "/find-by-segmenttype-and-flightid/{segmentType}/{flightId}", method = RequestMethod.GET)
    public ResponseEntity<Page<RouteSegmentViewModel>> findRouteSegmentBySegmentTypeAndFlightID(Pageable pageable, @PathVariable SegmentType segmentType, @PathVariable Integer flightId) {
        LOG.debug("REST request to get All RouteSegment By SegmentType : {} and flightMovementId: {}", segmentType, flightId);
        final List<RouteSegment> routeSegmentList = routeSegmentService.findAllBySegmentTypeAndFlightId(segmentType,flightId);
        final Page<RouteSegmentViewModel> resultPage = new PageImpl<>(routeSegmentMapper.toViewModel(routeSegmentList), pageable, routeSegmentList.size());
        return ResponseEntity.ok().body(resultPage);
    }

    @RequestMapping(value = "/find-by-source-and-flightid/{flightMovementSource}/{flightId}", method = RequestMethod.GET)
    public ResponseEntity<Page<RouteSegmentViewModel>> findRouteSegmentBySegmentTypeAndFlightID(Pageable pageable, @PathVariable FlightMovementSource flightMovementSource, @PathVariable Integer flightId) {
        LOG.debug("REST request to get All RouteSegment By FlightMovementSource : {} and flightMovementId: {}", flightMovementSource, flightId);
        SegmentType segmentType=SegmentTypeMap.mapFlightMovementSourceToSegmentType(flightMovementSource);
        final List<RouteSegment> routeSegmentList = routeSegmentService.findAllBySegmentTypeAndFlightId(segmentType,flightId);
        final Page<RouteSegmentViewModel> resultPage = new PageImpl<>(routeSegmentMapper.toViewModel(routeSegmentList), pageable, routeSegmentList.size());
        return ResponseEntity.ok().body(resultPage);
    }

    @RequestMapping(value = "/find-all/{flightId}", method = RequestMethod.GET)
    public ResponseEntity<Page<RouteSegmentViewModel>> finAllRouteSegmentByFlightID(Pageable pageable, @PathVariable Integer flightId) {
        LOG.debug("REST request to get All RouteSegment By flightMovementId: {}", flightId);
        final List<RouteSegment> routeSegmentList = routeSegmentService.findAllByFlightId(flightId);
        final Page<RouteSegmentViewModel> resultPage = new PageImpl<>(routeSegmentMapper.toViewModel(routeSegmentList), pageable, routeSegmentList.size());
        return ResponseEntity.ok().body(resultPage);
    }

}
