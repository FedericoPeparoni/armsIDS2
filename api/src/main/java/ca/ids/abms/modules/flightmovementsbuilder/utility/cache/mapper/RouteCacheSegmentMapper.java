package ca.ids.abms.modules.flightmovementsbuilder.utility.cache.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.vo.RouteSegmentVO;
import ca.ids.abms.modules.routesegments.RouteSegment;

@Mapper
public interface RouteCacheSegmentMapper {

    List<RouteSegment> toRouteSegmentLst(Iterable<RouteSegmentVO> routeSegmentVOIt);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "flightMovement", ignore = true)
    @Mapping(target = "segmentCost", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    RouteSegment toRouteSegment(RouteSegmentVO routeSegmentVO);
    
}
