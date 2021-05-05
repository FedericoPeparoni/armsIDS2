package ca.ids.abms.modules.unifiedtaxes;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ca.ids.abms.config.db.ABMSRepository;
import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.billings.BillingLedger;

@Repository
public interface UnifiedTaxChargesRepository extends ABMSRepository<UnifiedTaxCharges, Integer> {
	
    @Query("SELECT ar FROM UnifiedTaxCharges utc INNER JOIN utc.aircraftRegistration ar INNER JOIN utc.billingLedger bl WHERE bl.id = :billingLedgerId")
    List<AircraftRegistration> getAircraftRegistrationsByBillingLedgerId(@Param("billingLedgerId") Integer billingLedgerId); 
}




