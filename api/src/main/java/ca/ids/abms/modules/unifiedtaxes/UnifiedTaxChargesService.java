package ca.ids.abms.modules.unifiedtaxes;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.modules.common.services.AbmsCrudService;

@Service
public class UnifiedTaxChargesService extends AbmsCrudService<UnifiedTaxCharges, Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(UnifiedTaxChargesController.class);

	
    private final UnifiedTaxChargesRepository unifiedTaxChargesRepository;
    
    UnifiedTaxChargesService(final UnifiedTaxChargesRepository unifiedTaxChargesRepository)
    {
    	super(unifiedTaxChargesRepository);
    	this.unifiedTaxChargesRepository = unifiedTaxChargesRepository;
    }
    
    
    @Transactional
    @Override 
    public UnifiedTaxCharges create(final UnifiedTaxCharges entity) {
    	return super.create(entity);
    }
   		    
    
    @Transactional
    @Override 
    public UnifiedTaxCharges update(final Integer id, final UnifiedTaxCharges entity) {
    	return super.update(id, entity);
    }
    
    
    
    @Transactional(readOnly = true)
	public List<UnifiedTaxCharges> findAll() {
		return unifiedTaxChargesRepository.findAll() ;
		
	}	
}
