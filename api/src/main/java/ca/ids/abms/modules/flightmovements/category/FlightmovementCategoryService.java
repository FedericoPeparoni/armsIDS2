/**
 * 
 */
package ca.ids.abms.modules.flightmovements.category;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryNationality;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryScope;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryType;

/**
 * @author heskina
 *
 */
@Service
@Transactional
public class FlightmovementCategoryService {
	 private final Logger log = LoggerFactory.getLogger(FlightmovementCategoryService.class);
	 private FlightmovementCategoryRepository flightmovementCategoryRepository;
	 
	 public FlightmovementCategoryService(FlightmovementCategoryRepository flightmovementCategoryRepository) {
		 this.flightmovementCategoryRepository = flightmovementCategoryRepository;
	 }
	 
	 @Transactional(readOnly = true)
	 public List<FlightmovementCategory> findAllByOrderBySortOrderAsc() {
		 log.debug("Request to get all Flightmovement Categories sorted by sort order");

	     return flightmovementCategoryRepository.findAllByOrderBySortOrderAsc();
	 }
	 
	 @Transactional(readOnly = true)
	 public List<FlightmovementCategory> getFlightmovementCategoriesForAviationInvoice() {
		 log.debug("Request to get FlightmovementCategories for aviation invoice");

	     return flightmovementCategoryRepository.getFlightmovementCategoriesForAviationInvoice();
	 }	 
	 
	 @Transactional(readOnly = true)
	 public FlightmovementCategory getOne(Integer id) {
	     log.debug("Request to get one Flightmovement Category by id");

	     return flightmovementCategoryRepository.getOne(id);
	 }	 
	 
	 @Transactional(readOnly = true)
	 public FlightmovementCategory findCategoryByParameters(FlightmovementCategoryType type,
			 										FlightmovementCategoryScope scope,FlightmovementCategoryNationality nationality){
		 List<FlightmovementCategory> list = null;
		 FlightmovementCategory result = null;
		 String typeString = null;
		 if(type != null) {
			 typeString =type.toValue();
		 }
		 String scopeString =null;
		 String nationalityString =null;
		 if(scope != null) {
			 scopeString =scope.toValue();
		 }
		 if(nationality != null) {
			 nationalityString =nationality.toValue();
		 }
		 list = this.flightmovementCategoryRepository.findCategoryByParams(typeString, scopeString, nationalityString);
		 if(list !=null && !list.isEmpty()) {
	
			 int scoreC = 0;
			 for(FlightmovementCategory fc:list) {
				 int scoreA=0;
				 for(FlightmovementCategoryAttribute fa:fc.getFlightmovementCategoryAttributes()) {
					 int score = 0;
					 
					 if (fa.getFlightType() == null) {
						 ;
					 }
					 else if (fa.getFlightType().equals(typeString)) {
						 ++score;
					 }
					 else {
						 continue;
					 }
					 if(fa.getFlightScope() == null) {
						 ;
					 } else if(fa.getFlightScope().equals(scopeString)) {
						 ++score;
					 } else {
						 continue;
					 }
					 
					 if(fa.getFlightNationality() == null) {
						 ;
					 }else if( fa.getFlightNationality().equals(nationalityString)) {
					 
						 ++score;
					 } else {
						 continue;
					 }
					 if(score > scoreA) {
						 scoreA = score;
					 }
				 }
				 if(scoreA > scoreC) {
					 scoreC = scoreA;
					 result = fc;
				 }
			 }
		 }
		 return result;
	 }
	 
}
