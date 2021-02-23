package ca.ids.abms.modules.formulas.enroute;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EnrouteAirNavigationChargeFormulaRepository
        extends JpaRepository<EnrouteAirNavigationChargeFormula, Integer> {


    @Query (nativeQuery = true, value =
            "select f.*\n" +
            "  from enroute_air_navigation_charge_formulas f\n" +
            "    inner join enroute_air_navigation_charge_categories c\n" +
            "    on f.enroute_charge_category_id = c.id\n" +
            " where f.flightmovement_category_id = :flightCategoryId\n" +
            "   and mtow_category_upper_limit >= :mtow\n" +
            " order by mtow_category_upper_limit asc\n" +
            " limit 1\n" +
            ""
    )
    public EnrouteAirNavigationChargeFormula findByMtowAndFlightCategory (final @Param ("mtow") double mtow, final @Param("flightCategoryId") int flightCategoryId);
}
