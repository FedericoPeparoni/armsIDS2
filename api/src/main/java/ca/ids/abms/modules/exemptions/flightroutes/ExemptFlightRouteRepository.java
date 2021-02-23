package ca.ids.abms.modules.exemptions.flightroutes;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
interface ExemptFlightRouteRepository extends ABMSRepository<ExemptFlightRoute, Integer> {

    @Query("SELECT efr FROM ExemptFlightRoute efr WHERE ( " +
            "efr.departureAerodrome = :depAd AND efr.destinationAerodrome = :destAd " +
        ") OR ( efr.exemptionInEitherDirection = TRUE AND " +
            "efr.departureAerodrome = :destAd AND efr.destinationAerodrome = :depAd " +
        ")")
    Collection<ExemptFlightRoute> findAllByDepAndDest(@Param("depAd") String depAd, @Param("destAd") String destAd);
    
    @Query("SELECT efr FROM ExemptFlightRoute efr WHERE (( " +
            "efr.departureAerodrome = :depAd AND efr.destinationAerodrome = :destAd " +
        ") OR ( efr.exemptionInEitherDirection = TRUE AND " +
            "efr.departureAerodrome = :destAd AND efr.destinationAerodrome = :depAd " +
        "))" +
        "AND (efr.exemptRouteFloor <= :flightLevel AND efr.exemptRouteCeiling > :flightLevel)")
    Collection<ExemptFlightRoute> findAllByDepAndDestAndFlightLevel(@Param("depAd") String depAd, @Param("destAd") String destAd, @Param("flightLevel") Double flightLevel);

}
