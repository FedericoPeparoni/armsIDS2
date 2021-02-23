/**
 * 
 */
package ca.ids.abms.modules.flightmovements.category;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author heskina
 *
 */
@RestController
@RequestMapping("/api/flightmovement-categories")
public class FlightmovementCategoryController {
	private final Logger log = LoggerFactory.getLogger(FlightmovementCategoryController.class);
	private FlightmovementCategoryService flightmovementCategoryService;
	
	public FlightmovementCategoryController(FlightmovementCategoryService flightmovementCategoryService) {
		this.flightmovementCategoryService = flightmovementCategoryService;
	}
	
	@RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<FlightmovementCategory>> getAllFlightmovementCategories(){
        log.debug("REST request to get all FlightmovementCategories");
        final List<FlightmovementCategory> list = flightmovementCategoryService.findAllByOrderBySortOrderAsc();

        return ResponseEntity.ok().body(list);
    }
	
	/**
	 * Return all except OTHER
	 * @return
	 */
	@RequestMapping(value = "/aviation-invoice", method = RequestMethod.GET)
    public ResponseEntity<List<FlightmovementCategory>> getFlightmovementCategoriesForAviationInvoice(){
        log.debug("REST request to get FlightmovementCategories for aviation invoice");
        final List<FlightmovementCategory> list = flightmovementCategoryService.getFlightmovementCategoriesForAviationInvoice();

        return ResponseEntity.ok().body(list);
    }	

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<FlightmovementCategory> getFlightmovementCategory(@PathVariable Integer id) {
        log.debug("REST request to get FlightmovementCategory : {}", id);

        FlightmovementCategory flightmovementCategory = flightmovementCategoryService.getOne(id);

        return ResponseEntity.ok().body(flightmovementCategory);
    }

}
