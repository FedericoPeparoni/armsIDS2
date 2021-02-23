/**
 * 
 */
package ca.ids.abms.modules.flightmovements.category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


/**
 * @author heskina
 *
 */
public interface FlightmovementCategoryRepository extends JpaRepository<FlightmovementCategory,Integer>{

	List<FlightmovementCategory> findAllByOrderBySortOrderAsc();
	FlightmovementCategory getOne(Integer id);
	
	@Query(value = "select distinct c.* from flightmovement_categories c join flightmovement_category_attributes a on a.flightmovement_category = c.id " + 
			"where ((a.flight_type = :type or a.flight_type is null) and " +
			"(a.flight_nationality = :nationality or a.flight_nationality is null )  and " +
			"(a.flight_scope = :scope or a.flight_scope is null))", nativeQuery = true)
	List<FlightmovementCategory> findCategoryByParams(
			@Param("type") String type,
            @Param("scope") String scope,
            @Param("nationality") String nationality
            );
	
	@Query(value = "select c.* from flightmovement_categories c where c.name!='OTHER' order by c.name", nativeQuery = true)
	List<FlightmovementCategory> getFlightmovementCategoriesForAviationInvoice();
}
