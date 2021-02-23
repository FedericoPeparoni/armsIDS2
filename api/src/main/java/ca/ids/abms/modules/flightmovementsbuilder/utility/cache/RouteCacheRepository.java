package ca.ids.abms.modules.flightmovementsbuilder.utility.cache;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RouteCacheRepository extends JpaRepository<RouteCache, Integer> {

    public RouteCache findTop1ByDepartureAerodromeAndRouteTextAndDestinationAerodrome(String depAd, String route, String destAd);

    public RouteCache findByDepartureAerodromeAndRouteTextAndDestinationAerodromeAndSpeedAndEstimatedElapsed(String depAd, String route, String destAd, Integer speed, Integer estimatedElapsed);

    public RouteCache findTop1ByDepartureAerodromeAndRouteTextAndDestinationAerodromeAndFlightLevel(String depAd, String route, String destAd, Double flightLevel);

    public RouteCache findByDepartureAerodromeAndRouteTextAndDestinationAerodromeAndSpeedAndEstimatedElapsedAndFlightLevel(String depAd, String route, String destAd,
            Integer speed, Integer estimatedElapsed, Double flightLevel);

    @Modifying
    @Query(value="delete from route_cache where id in (select id from route_cache order by created_at limit :limit)", nativeQuery = true)
    public void deleteOldestRouteCache(@Param("limit") Long limit);
    
    @Query(nativeQuery = true, value = "SELECT St_Length(ST_GeogFromText(:geom))")
    public double getSegmentLength(@Param ("geom") final String geom);
}
