/**
 *
 */
package ca.ids.abms.modules.formulas.navigation;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NavigationBillingFormulaRepository extends ABMSRepository<NavigationBillingFormula, Integer> {

    @Query(value="select nbf.* from navigation_billing_formulas nbf " +
    "where nbf.upper_limit>=:upperLimit " +
    "order by nbf.upper_limit asc " +
    "limit 1",
    nativeQuery = true)
    NavigationBillingFormula getNavigationBillingFormulaByUpperLimit(@Param("upperLimit") Double upperLimit);
}
