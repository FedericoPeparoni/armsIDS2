package ca.ids.abms.modules.unifiedtaxes;

import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.common.controllers.AbmsCrudController;

@RestController
@RequestMapping(UnifiedTaxChargesController.ENDPOINT)
@CrossOrigin
public class UnifiedTaxChargesController{

	
	static final String ENDPOINT = "/api/unified-tax-charges";
	
	private static final Logger LOG = LoggerFactory.getLogger(UnifiedTaxChargesController.class);
	
	
	private final UnifiedTaxChargesService unifiedTaxChargesService ;
	@Autowired
	public UnifiedTaxChargesController(final UnifiedTaxChargesService unifiedTaxChargesService) {
		this.unifiedTaxChargesService=unifiedTaxChargesService;
	}
	
	
	@GetMapping(path = "/list")
    @PreAuthorize("hasAuthority('unified_tax_charge_view')")
	public ResponseEntity<List<UnifiedTaxCharges>> getAllUnifiedTaxCharges(){
		return ResponseEntity.ok(unifiedTaxChargesService.findAll());
	}
	
    @GetMapping(path = "/getAircraftRegistrationsByBillingLedgerId/{billingLedgerId}")
    @PreAuthorize("hasAuthority('unified_tax_charge_view')")
    public ResponseEntity<List<AircraftRegistration>> getAircraftRegistrationsByBillingLedgerId(@PathVariable final Integer billingLedgerId,
                                                              Pageable pageable,
                                                              @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get a list of aircraft registrations by unified-tax billing ledger id : {}", billingLedgerId);

		return ResponseEntity.ok(unifiedTaxChargesService.getAircraftRegistrationsByBillingLedgerId(billingLedgerId));       
    }
    
    @GetMapping(path = "/getUnifiedTaxChargesByAircraftRegistrationIdAndBillingLedgerId/{aircraftRegistrationId}/{billingLedgerId}")
    @PreAuthorize("hasAuthority('unified_tax_charge_view')")
    public ResponseEntity<UnifiedTaxCharges> getUnifiedTaxChargesByAircraftRegistrationIdAndBillingLedgerId(@PathVariable Integer aircraftRegistrationId,@PathVariable Integer billingLedgerId) {
    	return ResponseEntity.ok(unifiedTaxChargesService.getUnifiedTaxChargesByAircraftRegistrationIdAndBillingLedgerId(aircraftRegistrationId, billingLedgerId));
    }
    
   /* @PostMapping()
    @PreAuthorize("hasAuthority('unified_tax_modify')")
    public ResponseEntity<UnifiedTaxCharges> save(final UnifiedTaxCharges charges){
             return ResponseEntity.ok().body(unifiedTaxChargesService.save(charges));
            } */
}

