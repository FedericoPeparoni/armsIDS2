package ca.ids.abms.modules.formulas.enroute;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.util.models.ModelUtils;

@Service
@Transactional
public class EnrouteAirNavigationChargeFormulaService {

    private EnrouteAirNavigationChargeFormulaRepository enrouteAirNavigationChargeFormulaRepository; 
    
    private static final Logger LOG = LoggerFactory.getLogger(EnrouteAirNavigationChargeFormulaService.class);

    public EnrouteAirNavigationChargeFormulaService(
            EnrouteAirNavigationChargeFormulaRepository anEnrouteAirNavigationChargeFormulaRepository) {
        enrouteAirNavigationChargeFormulaRepository = anEnrouteAirNavigationChargeFormulaRepository;
    }

    public void delete(Integer id) {
        LOG.debug("Request to delete EnrouteAirNavigationChargeFormula : {}", id);
        try {
            enrouteAirNavigationChargeFormulaRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);

        }
    }

    @Transactional(readOnly = true)
    public List<EnrouteAirNavigationChargeFormula> findAll() {
        LOG.debug("Request to get all EnrouteAirNavigationChargeFormulas");
        return enrouteAirNavigationChargeFormulaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<EnrouteAirNavigationChargeFormula> findAll(Pageable pageable) {
        LOG.debug("Request to get enrouteAirNavigationChargeFormulas");
        return enrouteAirNavigationChargeFormulaRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public EnrouteAirNavigationChargeFormula getOne(Integer id) {
        LOG.debug("Request to get EnrouteAirNavigationChargeFormula : {}", id);
        return enrouteAirNavigationChargeFormulaRepository.getOne(id);
    }

    public EnrouteAirNavigationChargeFormula save(EnrouteAirNavigationChargeFormula enrouteAirNavigationChargeFormula) {
        return enrouteAirNavigationChargeFormulaRepository.save(enrouteAirNavigationChargeFormula);

    }

    public EnrouteAirNavigationChargeFormula update(Integer id,
            EnrouteAirNavigationChargeFormula enrouteAirNavigationChargeFormula) {
        EnrouteAirNavigationChargeFormula a = null;
        try {
            LOG.debug("Request to update EnrouteAirNavigationChargeFormula : {}", enrouteAirNavigationChargeFormula);
            enrouteAirNavigationChargeFormula.setId(id);
            EnrouteAirNavigationChargeFormula existingEnrouteAirNavigationChargeFormula = enrouteAirNavigationChargeFormulaRepository
                    .getOne(id);

            ModelUtils.checkVersionIfComparables(enrouteAirNavigationChargeFormula,
                    existingEnrouteAirNavigationChargeFormula);
            ModelUtils.merge(enrouteAirNavigationChargeFormula, existingEnrouteAirNavigationChargeFormula,
                    new String[0]);

            a = enrouteAirNavigationChargeFormulaRepository.save(existingEnrouteAirNavigationChargeFormula);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
        return a;
    }
    
    public EnrouteAirNavigationChargeFormula findByMtowAndFlightCategory (final Double mtow, final Integer flightCategoryId) {
        if (mtow != null && flightCategoryId != null) {
            return enrouteAirNavigationChargeFormulaRepository.findByMtowAndFlightCategory (mtow, flightCategoryId);
        }
        return null;
    }
    
}
