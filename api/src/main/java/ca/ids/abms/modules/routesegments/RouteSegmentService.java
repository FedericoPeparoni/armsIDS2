package ca.ids.abms.modules.routesegments;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.modules.util.models.ModelUtils;

/**
 * Created by c.talpa on 07/02/2017.
 */
@Service
@Transactional
public class RouteSegmentService {

    private final Logger LOG = LoggerFactory.getLogger(RouteSegmentService.class);

    private RouteSegmentRepository routeSegmentRepository;

    public RouteSegmentService(RouteSegmentRepository routeSegmentRepository){
        this.routeSegmentRepository=routeSegmentRepository;
    }

    @Transactional(readOnly = true)
    public Page<RouteSegment> findAll(Pageable pageable) {
        LOG.debug("Request to find all RouteSegments");
        return routeSegmentRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<RouteSegment> findAllBySegmentType(Pageable pageable,SegmentType segmentType) {
        LOG.debug("Request to find all RouteSegments By SegmentType: {}",segmentType);
        return routeSegmentRepository.findBySegmentType(pageable,segmentType);
    }

    @Transactional(readOnly = true)
    public List<RouteSegment> findAllBySegmentTypeAndFlightId(SegmentType segmentType, Integer flightId) {
        LOG.debug("Request to find all RouteSegments By SegmentType: {} and flightId {}",segmentType,flightId);
        return routeSegmentRepository.findBySegmentTypeAndFlightId(segmentType.getValue(),flightId);
    }

    @Transactional(readOnly = true)
    public List<RouteSegment> findAllByFlightId(Integer flightId) {
        LOG.debug("Request to find all RouteSegments By flightId {}",flightId);
        return routeSegmentRepository.findBySegmentTypeAndFlightId(flightId);
    }

    @Transactional(readOnly = true)
    public RouteSegment findOne(Integer id) {
        LOG.debug("Request to find RouteSegment by ID: {}",id);
        return routeSegmentRepository.findOne(id);
    }



    public RouteSegment create(RouteSegment routeSegment) {
        LOG.debug("Request to create RouteSegment ");
        return routeSegmentRepository.save(routeSegment);

    }

    public void delete(Integer id) {
        LOG.debug("Request to delete RouteSegment : {}", id);
        routeSegmentRepository.delete(id);
    }

    public RouteSegment update(Integer id, RouteSegment routeSegment) {
        LOG.debug("Request to RouteSegment : {}", routeSegment);
        RouteSegment existingRouteSegment = routeSegmentRepository.findOne(id);
        ModelUtils.merge(routeSegment, existingRouteSegment, "id");
        return routeSegmentRepository.save(existingRouteSegment);
    }

}
