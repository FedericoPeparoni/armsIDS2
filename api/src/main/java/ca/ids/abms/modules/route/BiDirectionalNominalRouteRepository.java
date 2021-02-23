package ca.ids.abms.modules.route;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
interface BiDirectionalNominalRouteRepository
    extends org.springframework.data.repository.Repository<BiDirectionalNominalRoute, Integer> {

    @Query(
        value = "SELECT *, pointa = :pointB AND pointb = :pointA AND bi_directional = true AS is_inverse " +
            "FROM nominal_routes nr " +
            "WHERE type = :type " +
            "AND pointa = :pointA AND pointb = :pointB " +
            "OR (pointa = :pointB AND pointb = :pointA AND bi_directional = true)" +
            "LIMIT 1",
        nativeQuery = true
    )
    BiDirectionalNominalRoute findByPointsAndType (
        @Param("pointA") String pointA,
        @Param("pointB") String pointB,
        @Param("type") String type
    );

    @Query(
            value = "SELECT *, pointa = :pointB AND pointb = :pointA AND bi_directional = true AS is_inverse " +
                "FROM nominal_routes nr " +
                "WHERE type = :type " +
                "AND (nominal_route_floor <= :flightLevel AND nominal_route_ceiling > :flightLevel)" +
                "AND (pointa = :pointA AND pointb = :pointB " +
                "OR (pointa = :pointB AND pointb = :pointA AND bi_directional = true))" +
                "LIMIT 1",
            nativeQuery = true
        )
        BiDirectionalNominalRoute findByPointsAndTypeAndFlightLevel (
            @Param("pointA") String pointA,
            @Param("pointB") String pointB,
            @Param("type") String type,
            @Param("flightLevel") Double flightLevel
        );
    
    @Query(
        value = "SELECT *, pointa = :exitFir AND pointb = :entryFir AND bi_directional = true AS is_inverse " +
            "FROM nominal_routes nr " +
            "WHERE type = :type " +
            "AND pointa = :entryFir AND pointb = :exitFir " +
            "OR (pointa = :exitFir AND pointb = :entryFir AND bi_directional = true)" +
            "LIMIT 1",
        nativeQuery = true
    )
    BiDirectionalNominalRoute findByFirToFir (
        @Param("entryFir") String entryFir,
        @Param("exitFir") String exitFir,
        @Param("type") String type
    );
    
    @Query(
            value = "SELECT *, pointa = :exitFir AND pointb = :entryFir AND bi_directional = true AS is_inverse " +
                "FROM nominal_routes nr " +
                "WHERE type = :type " +
                "AND (nominal_route_floor <= :flightLevel AND nominal_route_ceiling > :flightLevel)" +
                "AND pointa = :entryFir AND pointb = :exitFir " +
                "OR (pointa = :exitFir AND pointb = :entryFir AND bi_directional = true)" +
                "LIMIT 1",
            nativeQuery = true
        )
        BiDirectionalNominalRoute findByFirToFirAndFlightLevel (
            @Param("entryFir") String entryFir,
            @Param("exitFir") String exitFir,
            @Param("type") String type,
            @Param("flightLevel") Double flightLevel
        );
    
    @Query(
        value = "SELECT *, (pointa = :destAd OR pointa = :exitFir) AND (pointb = :depAd OR pointb = :entryFir) AND bi_directional = true AS is_inverse " +
            "FROM nominal_routes nr " +
            "WHERE type = :type " +
            "AND ((pointa = :depAd OR pointa = :entryFir) AND (pointb = :destAd OR pointb = :exitFir)) " +
            "OR  ((pointa = :destAd OR pointa = :exitFir) AND (pointb = :depAd OR pointb = :entryFir) AND bi_directional = true) " +
            "LIMIT 1",
        nativeQuery = true
    )
    BiDirectionalNominalRoute findByFirToAerodromeOrInverse (
        @Param("entryFir") String entryFir,
        @Param("exitFir") String exitFir,
        @Param("destAd") String destAd,
        @Param("depAd") String depAd,
        @Param("type") String type
    );
    
    @Query(
            value = "SELECT *, (pointa = :destAd OR pointa = :exitFir) AND (pointb = :depAd OR pointb = :entryFir) AND bi_directional = true AS is_inverse " +
                "FROM nominal_routes nr " +
                "WHERE type = :type " +
                "AND (nominal_route_floor <= :flightLevel AND nominal_route_ceiling > :flightLevel)" +
                "AND (((pointa = :depAd OR pointa = :entryFir) AND (pointb = :destAd OR pointb = :exitFir)) " +
                "OR  ((pointa = :destAd OR pointa = :exitFir) AND (pointb = :depAd OR pointb = :entryFir) AND bi_directional = true)) " +
                "LIMIT 1",
            nativeQuery = true
        )
        BiDirectionalNominalRoute findByFirAndFlightLevelToAerodromeOrInverse (
            @Param("entryFir") String entryFir,
            @Param("exitFir") String exitFir,
            @Param("destAd") String destAd,
            @Param("depAd") String depAd,
            @Param("type") String type,
            @Param("flightLevel") Double flightLevel
        );
}
