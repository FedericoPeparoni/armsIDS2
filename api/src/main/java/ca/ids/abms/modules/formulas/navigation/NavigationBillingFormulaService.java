package ca.ids.abms.modules.formulas.navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.flightmovements.enumerate.CostFormulaVar;
import ca.ids.abms.modules.formulas.FormulaEvaluator;
import ca.ids.abms.modules.util.models.ModelUtils;

@Service
@Transactional
public class NavigationBillingFormulaService {

    private static Map<String, Object> vars;

    private NavigationBillingFormulaRepository navigationBillingFormulaRepository;

    private FormulaEvaluator formulaEvaluator;

    public NavigationBillingFormulaService (NavigationBillingFormulaRepository navigationBillingFormulaRepository,
                                            FormulaEvaluator formulaEvaluator) {
        this.navigationBillingFormulaRepository = navigationBillingFormulaRepository;
        this.formulaEvaluator=formulaEvaluator;
        vars=initVarsMap();
    }

    public NavigationBillingFormula createFormula (NavigationBillingFormula formula) {
        return navigationBillingFormulaRepository.save(formula);
    }

    @Transactional(readOnly = true)
    public NavigationBillingFormula getFormulaById(Integer id) {
        return navigationBillingFormulaRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public NavigationBillingFormula getFormulaByUpperLimit(Double upperLimit) {
        return navigationBillingFormulaRepository.getNavigationBillingFormulaByUpperLimit(upperLimit);
    }

    @Transactional(readOnly = true)
    public Page<NavigationBillingFormula> findAllFormula(Pageable pageable) {
        return navigationBillingFormulaRepository.findAll(pageable);
    }

    public NavigationBillingFormula updateFormula(Integer id, NavigationBillingFormula formula) {
        NavigationBillingFormula nbf = null;
        try {
            final NavigationBillingFormula existingFormula = navigationBillingFormulaRepository.getOne(id);
            ModelUtils.merge(formula, existingFormula, "id");
            nbf = navigationBillingFormulaRepository.save(existingFormula);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
        return nbf;
    }

    public void deleteFormula(Integer id) {
        try {
            navigationBillingFormulaRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }


    public List<NavigationBillingFormulaValidationViewModel> validateNavigationBillingFormula(NavigationBillingFormula navigationBillingFormula){
        List<NavigationBillingFormulaValidationViewModel> navigationBillingFormulaValidationViewModelList = new ArrayList<>();

        if (navigationBillingFormula != null) {
            navigationBillingFormulaValidationViewModelList.add(validateDom(navigationBillingFormula));
            navigationBillingFormulaValidationViewModelList.add(validateDomD(navigationBillingFormula));
            navigationBillingFormulaValidationViewModelList.add(validateIntArr(navigationBillingFormula));
            navigationBillingFormulaValidationViewModelList.add(validateIntArrD(navigationBillingFormula));
            navigationBillingFormulaValidationViewModelList.add(validateIntDep(navigationBillingFormula));
            navigationBillingFormulaValidationViewModelList.add(validateIntDepD(navigationBillingFormula));
            navigationBillingFormulaValidationViewModelList.add(validateIntOvf(navigationBillingFormula));
            navigationBillingFormulaValidationViewModelList.add(validateIntOvfD(navigationBillingFormula));
            navigationBillingFormulaValidationViewModelList.add(validateRegArr(navigationBillingFormula));
            navigationBillingFormulaValidationViewModelList.add(validateRegArrD(navigationBillingFormula));
            navigationBillingFormulaValidationViewModelList.add(validateRegDep(navigationBillingFormula));
            navigationBillingFormulaValidationViewModelList.add(validateRegDepD(navigationBillingFormula));
            navigationBillingFormulaValidationViewModelList.add(validateRegOvf(navigationBillingFormula));
            navigationBillingFormulaValidationViewModelList.add(validateRegOvfD(navigationBillingFormula));
            navigationBillingFormulaValidationViewModelList.add(validateW(navigationBillingFormula));
        }

        return navigationBillingFormulaValidationViewModelList;
    }

    private final Map<String, Object> initVarsMap(){
        Map<String, Object> vars = new HashedMap();

        vars.put(CostFormulaVar.MTOW.varName(),12.89);
        vars.put(CostFormulaVar.SCHEDCROSSDIST.varName(),25.03);
        vars.put(CostFormulaVar.AVGMASSFACTOR.varName(),45.65);
        vars.put(CostFormulaVar.ACCOUNTDISCOUNT.varName(),3.0);
        vars.put(CostFormulaVar.ENTRIESNUMBER.varName(),22);
        vars.put(CostFormulaVar.FIRENTRYFEE.varName(),5.6);
        vars.put(CostFormulaVar.DFACTOR.varName(),0.13);
        vars.put(CostFormulaVar.WFACTOR.varName(),0.76);

        return vars;
    }

    private NavigationBillingFormulaValidationViewModel validateDom(NavigationBillingFormula navigationBillingFormula) {
        NavigationBillingFormulaValidationViewModel formula = new NavigationBillingFormulaValidationViewModel();

        formula.setName("domestic_formula");

        try {
            formula.setIdNavigationBillingFormula(navigationBillingFormula.getId());
            formula.setFormula(navigationBillingFormula.getDomesticFormula());

            formulaEvaluator.evalDouble(navigationBillingFormula.getDomesticFormula(),vars);

            formula.setFormulaValid(Boolean.TRUE);
        } catch (Exception e) {
            formula.setFormulaValid(Boolean.FALSE);
            formula.setIssue(e.getLocalizedMessage());
        }
        return formula;
    }

    private NavigationBillingFormulaValidationViewModel validateDomD(NavigationBillingFormula navigationBillingFormula) {
        NavigationBillingFormulaValidationViewModel formula = new NavigationBillingFormulaValidationViewModel();

        formula.setName("domestic_d_factor_formula");

        try {
            formula.setIdNavigationBillingFormula(navigationBillingFormula.getId());
            formula.setFormula(navigationBillingFormula.getDomesticDFactorFormula());

            formulaEvaluator.evalDouble(navigationBillingFormula.getDomesticDFactorFormula(),vars);

            formula.setFormulaValid(Boolean.TRUE);
        } catch (Exception e) {
            formula.setFormulaValid(Boolean.FALSE);
            formula.setIssue(e.getLocalizedMessage());
        }

        return formula;
    }

    private NavigationBillingFormulaValidationViewModel validateIntArr(NavigationBillingFormula navigationBillingFormula) {
        NavigationBillingFormulaValidationViewModel formula = new NavigationBillingFormulaValidationViewModel();

        formula.setName("international_arrival_formula");

        try {
            formula.setIdNavigationBillingFormula(navigationBillingFormula.getId());
            formula.setFormula(navigationBillingFormula.getInternationalArrivalFormula());

            formulaEvaluator.evalDouble(navigationBillingFormula.getInternationalArrivalFormula(),vars);

            formula.setFormulaValid(Boolean.TRUE);
        } catch (Exception e) {
            formula.setFormulaValid(Boolean.FALSE);
            formula.setIssue(e.getLocalizedMessage());
        }

        return formula;
    }

    private NavigationBillingFormulaValidationViewModel validateIntArrD(NavigationBillingFormula navigationBillingFormula) {
        NavigationBillingFormulaValidationViewModel formula = new NavigationBillingFormulaValidationViewModel();

        formula.setName("int_arr_d_factor_formula");

        try {
            formula.setIdNavigationBillingFormula(navigationBillingFormula.getId());
            formula.setFormula(navigationBillingFormula.getIntArrDFactorFormula());

            formulaEvaluator.evalDouble(navigationBillingFormula.getIntArrDFactorFormula(),vars);

            formula.setFormulaValid(Boolean.TRUE);
        } catch (Exception e) {
            formula.setFormulaValid(Boolean.FALSE);
            formula.setIssue(e.getLocalizedMessage());
        }

        return formula;
    }

    private NavigationBillingFormulaValidationViewModel validateIntDep(NavigationBillingFormula navigationBillingFormula) {
        NavigationBillingFormulaValidationViewModel formula = new NavigationBillingFormulaValidationViewModel();

        formula.setName("international_departure_formula");

        try {
            formula.setIdNavigationBillingFormula(navigationBillingFormula.getId());
            formula.setFormula(navigationBillingFormula.getInternationalDepartureFormula());

            formulaEvaluator.evalDouble(navigationBillingFormula.getInternationalDepartureFormula(),vars);

            formula.setFormulaValid(Boolean.TRUE);
        } catch (Exception e) {
            formula.setFormulaValid(Boolean.FALSE);
            formula.setIssue(e.getLocalizedMessage());
        }

        return formula;
    }

    private NavigationBillingFormulaValidationViewModel validateIntDepD(NavigationBillingFormula navigationBillingFormula) {
        NavigationBillingFormulaValidationViewModel formula = new NavigationBillingFormulaValidationViewModel();

        formula.setName("int_dep_d_factor_formula");

        try {
            formula.setIdNavigationBillingFormula(navigationBillingFormula.getId());
            formula.setFormula(navigationBillingFormula.getIntDepDFactorFormula());

            formulaEvaluator.evalDouble(navigationBillingFormula.getIntDepDFactorFormula(),vars);

            formula.setFormulaValid(Boolean.TRUE);
        } catch (Exception e) {
            formula.setFormulaValid(Boolean.FALSE);
            formula.setIssue(e.getLocalizedMessage());
        }

        return formula;

    }

    private NavigationBillingFormulaValidationViewModel validateIntOvf(NavigationBillingFormula navigationBillingFormula) {
        NavigationBillingFormulaValidationViewModel formula = new NavigationBillingFormulaValidationViewModel();

        formula.setName("international_overflight_formula");

        try {
            formula.setIdNavigationBillingFormula(navigationBillingFormula.getId());
            formula.setFormula(navigationBillingFormula.getInternationalOverflightFormula());

            formulaEvaluator.evalDouble(navigationBillingFormula.getInternationalOverflightFormula(),vars);

            formula.setFormulaValid(Boolean.TRUE);
        } catch (Exception e) {
            formula.setFormulaValid(Boolean.FALSE);
            formula.setIssue(e.getLocalizedMessage());
        }

        return formula;
    }

    private NavigationBillingFormulaValidationViewModel validateIntOvfD(NavigationBillingFormula navigationBillingFormula) {
        NavigationBillingFormulaValidationViewModel formula = new NavigationBillingFormulaValidationViewModel();

        formula.setName("int_ovr_d_factor_formula");

        try {
            formula.setIdNavigationBillingFormula(navigationBillingFormula.getId());
            formula.setFormula(navigationBillingFormula.getIntOvrDFactorFormula());

            formulaEvaluator.evalDouble(navigationBillingFormula.getIntOvrDFactorFormula(),vars);

            formula.setFormulaValid(Boolean.TRUE);
        } catch (Exception e) {
            formula.setFormulaValid(Boolean.FALSE);
            formula.setIssue(e.getLocalizedMessage());
        }

        return formula;
    }

    private NavigationBillingFormulaValidationViewModel validateRegArr(NavigationBillingFormula navigationBillingFormula) {
        NavigationBillingFormulaValidationViewModel formula = new NavigationBillingFormulaValidationViewModel();

        formula.setName("regional_arrival_formula");

        try {
            formula.setIdNavigationBillingFormula(navigationBillingFormula.getId());
            formula.setFormula(navigationBillingFormula.getRegionalArrivalFormula());

            formulaEvaluator.evalDouble(navigationBillingFormula.getRegionalArrivalFormula(),vars);

            formula.setFormulaValid(Boolean.TRUE);
        } catch (Exception e) {
            formula.setFormulaValid(Boolean.FALSE);
            formula.setIssue(e.getLocalizedMessage());
        }

        return formula;
    }

    private NavigationBillingFormulaValidationViewModel validateRegArrD(NavigationBillingFormula navigationBillingFormula) {
        NavigationBillingFormulaValidationViewModel formula = new NavigationBillingFormulaValidationViewModel();

        formula.setName("reg_arr_d_factor_formula");

        try {
            formula.setIdNavigationBillingFormula(navigationBillingFormula.getId());
            formula.setFormula(navigationBillingFormula.getRegArrDFactorFormula());

            formulaEvaluator.evalDouble(navigationBillingFormula.getRegArrDFactorFormula(),vars);

            formula.setFormulaValid(Boolean.TRUE);
        } catch (Exception e) {
            formula.setFormulaValid(Boolean.FALSE);
            formula.setIssue(e.getLocalizedMessage());
        }

         return formula;
    }

    private NavigationBillingFormulaValidationViewModel validateRegDep(NavigationBillingFormula navigationBillingFormula) {
        NavigationBillingFormulaValidationViewModel formula = new NavigationBillingFormulaValidationViewModel();

        formula.setName("regional_departure_formula");

        try {
            formula.setIdNavigationBillingFormula(navigationBillingFormula.getId());
            formula.setFormula(navigationBillingFormula.getRegionalDepartureFormula());

            formulaEvaluator.evalDouble(navigationBillingFormula.getRegionalDepartureFormula(),vars);

            formula.setFormulaValid(Boolean.TRUE);
        } catch (Exception e) {
            formula.setFormulaValid(Boolean.FALSE);
            formula.setIssue(e.getLocalizedMessage());
        }

        return formula;
    }

    private NavigationBillingFormulaValidationViewModel validateRegDepD(NavigationBillingFormula navigationBillingFormula) {
        NavigationBillingFormulaValidationViewModel formula = new NavigationBillingFormulaValidationViewModel();

        formula.setName("reg_dep_d_factor_formula");

        try {
            formula.setIdNavigationBillingFormula(navigationBillingFormula.getId());
            formula.setFormula(navigationBillingFormula.getRegDepDFactorFormula());

            formulaEvaluator.evalDouble(navigationBillingFormula.getRegDepDFactorFormula(),vars);

            formula.setFormulaValid(Boolean.TRUE);
        } catch (Exception e) {
            formula.setFormulaValid(Boolean.FALSE);
            formula.setIssue(e.getLocalizedMessage());
        }

        return formula;
    }

    private NavigationBillingFormulaValidationViewModel validateRegOvf(NavigationBillingFormula navigationBillingFormula) {
        NavigationBillingFormulaValidationViewModel formula = new NavigationBillingFormulaValidationViewModel();

        formula.setName("regional_overflight_formula");

        try {
            formula.setIdNavigationBillingFormula(navigationBillingFormula.getId());
            formula.setFormula(navigationBillingFormula.getRegionalOverflightFormula());

            formulaEvaluator.evalDouble(navigationBillingFormula.getRegionalOverflightFormula(),vars);

            formula.setFormulaValid(Boolean.TRUE);
        } catch (Exception e) {
            formula.setFormulaValid(Boolean.FALSE);
            formula.setIssue(e.getLocalizedMessage());
        }

        return formula;
    }

    private NavigationBillingFormulaValidationViewModel validateRegOvfD(NavigationBillingFormula navigationBillingFormula) {
        NavigationBillingFormulaValidationViewModel formula = new NavigationBillingFormulaValidationViewModel();

        formula.setName("reg_ovr_d_factor_formula");

        try {
            formula.setIdNavigationBillingFormula(navigationBillingFormula.getId());
            formula.setFormula(navigationBillingFormula.getRegOvrDFactorFormula());

            formulaEvaluator.evalDouble(navigationBillingFormula.getRegOvrDFactorFormula(),vars);

            formula.setFormulaValid(Boolean.TRUE);
        } catch (Exception e) {
            formula.setFormulaValid(Boolean.FALSE);
            formula.setIssue(e.getLocalizedMessage());
        }

        return formula;
    }

    private NavigationBillingFormulaValidationViewModel validateW(NavigationBillingFormula navigationBillingFormula) {
        NavigationBillingFormulaValidationViewModel formula = new NavigationBillingFormulaValidationViewModel();

        formula.setName("w_factor_formula");

        try {
            formula.setIdNavigationBillingFormula(navigationBillingFormula.getId());
            formula.setFormula(navigationBillingFormula.getwFactorFormula());

            formulaEvaluator.evalDouble(navigationBillingFormula.getwFactorFormula(),vars);

            formula.setFormulaValid(Boolean.TRUE);
        } catch (Exception e) {
            formula.setFormulaValid(Boolean.FALSE);
            formula.setIssue(e.getLocalizedMessage());
        }

        return formula;
    }

}
