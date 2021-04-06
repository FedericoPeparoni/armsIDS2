package ca.ids.abms.modules.unifiedtaxes;

import ca.ids.abms.modules.formulas.unifiedtax.UnifiedTaxChargeFormulaValidationViewModel;

public class UnifiedTaxChargeFormulaException extends RuntimeException{

    private UnifiedTaxChargeFormulaValidationViewModel unifiedTaxChargeFormulaValidationViewModel;

    public UnifiedTaxChargeFormulaException(UnifiedTaxChargeFormulaValidationViewModel unifiedTaxChargeFormulaValidationViewModel){
       this.unifiedTaxChargeFormulaValidationViewModel = unifiedTaxChargeFormulaValidationViewModel;
    }

    public UnifiedTaxChargeFormulaValidationViewModel getUnifiedTaxChargeFormulaValidationViewModel() {
        return unifiedTaxChargeFormulaValidationViewModel;
    }
}
