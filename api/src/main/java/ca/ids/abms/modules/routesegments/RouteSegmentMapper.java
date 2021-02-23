package ca.ids.abms.modules.routesegments;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Created by c.talpa on 23/02/2017.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RouteSegmentMapper {

    List<RouteSegmentViewModel> toViewModel(Iterable<RouteSegment> routeSegments);


    @Mapping(target = "flightRecordId", source = "flightMovement.id")
    RouteSegmentViewModel toViewModel(RouteSegment routeSegment);


    @Mapping(target = "flightMovement", ignore = true)
    RouteSegment toModel(RouteSegmentViewModel dto);
}
