package ca.ids.abms.modules.routesegments;

import ca.ids.abms.config.db.ABMSRepository;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by c.talpa on 07/02/2017.
 */
public interface RouteSegmentRepository extends ABMSRepository<RouteSegment, Integer> {

    Page<RouteSegment> findBySegmentType(Pageable pageable, SegmentType segmentType);

    @Query(value="SELECT rs.* FROM route_segments rs WHERE rs.segment_type = :segmentType AND rs.flight_movement = :flightId ORDER BY rs.segment_number ASC ",nativeQuery = true)
    List<RouteSegment> findBySegmentTypeAndFlightId(@Param("segmentType") String segmentType, @Param("flightId") Integer flightId);

    @Query(value="SELECT rs.* FROM route_segments rs WHERE  rs.flight_movement = :flightId ORDER BY rs.segment_number ASC ",nativeQuery = true)
    List<RouteSegment> findBySegmentTypeAndFlightId(@Param("flightId") Integer flightId);

    @Modifying
    @Query("DELETE FROM RouteSegment rs WHERE rs.flightMovement = :flightMovement")
    void removeAllByFlightMovement(@Param("flightMovement") final FlightMovement flightMovement);
}
