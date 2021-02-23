package ca.ids.abms.modules.formulas.enroute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.flightmovements.category.FlightmovementCategory;
import ca.ids.abms.modules.flightmovements.category.FlightmovementCategoryService;
import ca.ids.abms.modules.flightmovements.enumerate.CostFormulaVar;
import ca.ids.abms.modules.formulas.FormulaEvaluator;
import ca.ids.abms.modules.util.models.ModelUtils;

@Service
@Transactional
public class EnrouteAirNavigationChargeCategoryService {

    private EnrouteAirNavigationChargeCategoryRepository enrouteAirNavigationChargeCategoryRepository;
    
    private FormulaEvaluator formulaEvaluator;
    
    private EnrouteAirNavigationChargeFormulaService enrouteAirNavigationChargeFormulaService;
    
    private FlightmovementCategoryService flightmovementCategoryService;
    
    private static final Logger LOG = LoggerFactory.getLogger(EnrouteAirNavigationChargeFormulaService.class);
    
    private static Map<String, Object> vars;
    
    public EnrouteAirNavigationChargeCategoryService(EnrouteAirNavigationChargeCategoryRepository anEnrouteAirNavigationChargeCategoryRepository, 
            EnrouteAirNavigationChargeFormulaService anEnrouteAirNavigationChargeFormulaService, FormulaEvaluator aFormulaEvaluator,
            FlightmovementCategoryService aFlightmovementCategoryService) {
        enrouteAirNavigationChargeCategoryRepository = anEnrouteAirNavigationChargeCategoryRepository;
        enrouteAirNavigationChargeFormulaService = anEnrouteAirNavigationChargeFormulaService;
        flightmovementCategoryService = aFlightmovementCategoryService;
        formulaEvaluator = aFormulaEvaluator;
        initVarsMap();
    }
    
    public void delete(Integer id) {
        LOG.debug("Request to delete Enroute Air Navigation Charge Category : {}", id);
        try {
            final EnrouteAirNavigationChargeCategory existingEnrouteAirNavigationChargeCategory = enrouteAirNavigationChargeCategoryRepository.getOne(id);
            final List<EnrouteAirNavigationChargeFormula> enrouteAirNavigationChargeFormulas = existingEnrouteAirNavigationChargeCategory.getEnrouteAirNavigationChargeFormulas();
            for (EnrouteAirNavigationChargeFormula enrouteAirNavigationChargeFormula : enrouteAirNavigationChargeFormulas) {
                enrouteAirNavigationChargeFormulaService.delete(enrouteAirNavigationChargeFormula.getId());
            }
            enrouteAirNavigationChargeCategoryRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
        enrouteAirNavigationChargeCategoryRepository.flush();
    }

   @Transactional(readOnly = true)
    public List<EnrouteAirNavigationChargeCategory> findAll() {
       LOG.debug("Request to get all Enroute Air Navigation Charge Categories");

        return enrouteAirNavigationChargeCategoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<EnrouteAirNavigationChargeCategory> findAll(Pageable pageable) {
        LOG.debug("Request to get Enroute Air Navigation Charge Categories");
        return enrouteAirNavigationChargeCategoryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public EnrouteAirNavigationChargeCategory getOne(Integer id) {
        LOG.debug("Request to get Enroute Air Navigation Charge Category : {}", id);
        return enrouteAirNavigationChargeCategoryRepository.getOne(id);
    }

    public EnrouteAirNavigationChargeCategory save(
            EnrouteAirNavigationChargeCategory aEnrouteAirNavigationChargeCategory) {
        LOG.debug("Request to save Enroute Air Navigation Charge Category : {}", aEnrouteAirNavigationChargeCategory);
        EnrouteAirNavigationChargeCategory savedEnrouteAirNavigationChargeCategory = enrouteAirNavigationChargeCategoryRepository.save(aEnrouteAirNavigationChargeCategory);
        List<EnrouteAirNavigationChargeFormula> formulaLst = aEnrouteAirNavigationChargeCategory.getEnrouteAirNavigationChargeFormulas();
        List<EnrouteAirNavigationChargeFormula> savedFormulaLst = new ArrayList<>();
        for (EnrouteAirNavigationChargeFormula formula : formulaLst) {
            formula.setEnrouteChargeCategory(savedEnrouteAirNavigationChargeCategory);
            EnrouteAirNavigationChargeFormula savedformula = enrouteAirNavigationChargeFormulaService.save(formula);
            savedFormulaLst.add(savedformula);
        }
        aEnrouteAirNavigationChargeCategory.setEnrouteAirNavigationChargeFormulas(savedFormulaLst);
        return savedEnrouteAirNavigationChargeCategory;
    }

    public EnrouteAirNavigationChargeCategory update(Integer id,
            EnrouteAirNavigationChargeCategory enrouteAirNavigationChargeCategory) {
        EnrouteAirNavigationChargeCategory ac;
        try {
            LOG.debug("Request to update Enroute Air Navigation Charge Category : {}", enrouteAirNavigationChargeCategory);
            enrouteAirNavigationChargeCategory.setId(id);
            List<EnrouteAirNavigationChargeFormula> formulaLst = enrouteAirNavigationChargeCategory.getEnrouteAirNavigationChargeFormulas();
            List<EnrouteAirNavigationChargeFormula> savedFormulaLst = new ArrayList<>();
            for (EnrouteAirNavigationChargeFormula formula : formulaLst) {
                formula.setEnrouteChargeCategory(enrouteAirNavigationChargeCategory);
                EnrouteAirNavigationChargeFormula savedformula = enrouteAirNavigationChargeFormulaService.update(formula.getId(), formula);
                savedFormulaLst.add(savedformula);
            }
            final EnrouteAirNavigationChargeCategory existingEnrouteAirNavigationChargeCategory = enrouteAirNavigationChargeCategoryRepository.getOne(id);
            ModelUtils.checkVersionIfComparables(enrouteAirNavigationChargeCategory, existingEnrouteAirNavigationChargeCategory);
            ModelUtils.merge(enrouteAirNavigationChargeCategory, existingEnrouteAirNavigationChargeCategory);
            existingEnrouteAirNavigationChargeCategory.setEnrouteAirNavigationChargeFormulas(savedFormulaLst);
            ac = enrouteAirNavigationChargeCategoryRepository.save(existingEnrouteAirNavigationChargeCategory);
            for (EnrouteAirNavigationChargeFormula formula : savedFormulaLst) {
                formula.setEnrouteChargeCategory(ac);
                enrouteAirNavigationChargeFormulaService.save(formula);
            }
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
        return ac;
    }

    List<EnrouteAirNavigationChargeFormulaValidationViewModel> validateEnrouteAirNavigationChargeFormula(
            EnrouteAirNavigationChargeCategory aFormula) {
        List<FlightmovementCategory> categories = flightmovementCategoryService.findAllByOrderBySortOrderAsc();
        List<EnrouteAirNavigationChargeFormula> formulaLst = aFormula.getEnrouteAirNavigationChargeFormulas();
        List<EnrouteAirNavigationChargeFormulaValidationViewModel> enrouteAirNavigationChargeFormulaViewModelLst=new ArrayList<>();
        
        // evaluate wformula
        EnrouteAirNavigationChargeFormulaValidationViewModel wformulaValidation=new EnrouteAirNavigationChargeFormulaValidationViewModel();
        try {
            wformulaValidation.setIdEnrouteAirNavigationChargeFormula(aFormula.getId());
            wformulaValidation.setFormula(aFormula.getwFactorFormula());
            formulaEvaluator.evalDouble(aFormula.getwFactorFormula(),vars);

            wformulaValidation.setFormulaValid(Boolean.TRUE);
        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
            wformulaValidation.setFormulaValid(Boolean.FALSE);
            wformulaValidation.setIssue(e.getLocalizedMessage());
        }
        enrouteAirNavigationChargeFormulaViewModelLst.add(wformulaValidation);
        
        if (formulaLst.size() == categories.size()) {
            for (EnrouteAirNavigationChargeFormula formula : formulaLst) {
             
                // evaluate formula
                EnrouteAirNavigationChargeFormulaValidationViewModel formulaValidation=new EnrouteAirNavigationChargeFormulaValidationViewModel();
                try {
                    formulaValidation.setIdEnrouteAirNavigationChargeFormula(formula.getId());
                    formulaValidation.setFormula(formula.getFormula());
                    formulaEvaluator.evalDouble(formula.getFormula(),vars);
    
                    formulaValidation.setFormulaValid(Boolean.TRUE);
                } catch (Exception e) {
                    LOG.error(e.getLocalizedMessage(), e);
                    formulaValidation.setFormulaValid(Boolean.FALSE);
                    formulaValidation.setIssue(e.getLocalizedMessage());
                }
                enrouteAirNavigationChargeFormulaViewModelLst.add(formulaValidation);
                
                 // evaluate dFormula
                EnrouteAirNavigationChargeFormulaValidationViewModel dFormulaValidation=new EnrouteAirNavigationChargeFormulaValidationViewModel();
                try {
                    dFormulaValidation.setIdEnrouteAirNavigationChargeFormula(formula.getId());
                    dFormulaValidation.setFormula(formula.getdFactorFormula());
                    formulaEvaluator.evalDouble(formula.getdFactorFormula(),vars);
    
                    dFormulaValidation.setFormulaValid(Boolean.TRUE);
                } catch (Exception e) {
                    LOG.error(e.getLocalizedMessage(), e);
                    dFormulaValidation.setFormulaValid(Boolean.FALSE);
                    dFormulaValidation.setIssue(e.getLocalizedMessage());
                }
                enrouteAirNavigationChargeFormulaViewModelLst.add(dFormulaValidation);
            }
        } else {
            EnrouteAirNavigationChargeFormulaValidationViewModel formulaValidation=new EnrouteAirNavigationChargeFormulaValidationViewModel();
            formulaValidation.setFormulaValid(Boolean.FALSE);
            formulaValidation.setIssue("One or more formulas are missing");
            enrouteAirNavigationChargeFormulaViewModelLst.add(formulaValidation);
        }
        return enrouteAirNavigationChargeFormulaViewModelLst;
    }
    
    private void initVarsMap(){

        vars = new HashedMap();

        vars.put(CostFormulaVar.MTOW.varName(),12.89);
        vars.put(CostFormulaVar.SCHEDCROSSDIST.varName(),25.03);
        vars.put(CostFormulaVar.AVGMASSFACTOR.varName(),45.65);
        vars.put(CostFormulaVar.ACCOUNTDISCOUNT.varName(),3.0);
        vars.put(CostFormulaVar.ENTRIESNUMBER.varName(),22);
        vars.put(CostFormulaVar.FIRENTRYFEE.varName(),5.6);
        vars.put(CostFormulaVar.AERODROMEFEE.varName(), 5.0);
        vars.put(CostFormulaVar.APPROACHFEE.varName(), 7.25);
        vars.put(CostFormulaVar.DFACTOR.varName(),0.13);
        vars.put(CostFormulaVar.WFACTOR.varName(),0.76);
    }

    public long countAll() {
        return enrouteAirNavigationChargeCategoryRepository.count();
    }
}
