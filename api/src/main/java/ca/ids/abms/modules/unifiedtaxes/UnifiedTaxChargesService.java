package ca.ids.abms.modules.unifiedtaxes;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.common.services.AbmsCrudService;

@Service
@Transactional
public class UnifiedTaxChargesService  {

    private static final Logger LOG = LoggerFactory.getLogger(UnifiedTaxChargesController.class);

	
    private final UnifiedTaxChargesRepository unifiedTaxChargesRepository;
    @Autowired
    UnifiedTaxChargesService(final UnifiedTaxChargesRepository unifiedTaxChargesRepository) {
    	this.unifiedTaxChargesRepository = unifiedTaxChargesRepository;
    }
    
    
    @Transactional
    public UnifiedTaxCharges save(final UnifiedTaxCharges charge) {
        LOG.debug("Save unified tax charge {}", charge);
        return unifiedTaxChargesRepository.save(charge);
    	
    }
   		        
    @Transactional(readOnly = true)
	public List<UnifiedTaxCharges> findAll() {
		return unifiedTaxChargesRepository.findAll() ;
		
	}

    @Transactional(readOnly = true)
	public List<AircraftRegistration> getAircraftRegistrationsByBillingLedgerId(Integer billingLedgerId) {
		return unifiedTaxChargesRepository.getAircraftRegistrationsByBillingLedgerId(billingLedgerId);
	}	
    
    
    @Transactional(readOnly = true)
	public List<BillingLedger> getBillingLedgerByRegistrationNumberAndDate(String registrationNumber, LocalDateTime date) {
		return unifiedTaxChargesRepository.getBillingLedgerByRegistrationNumberAndDate(registrationNumber, date);
	}	
   
}
