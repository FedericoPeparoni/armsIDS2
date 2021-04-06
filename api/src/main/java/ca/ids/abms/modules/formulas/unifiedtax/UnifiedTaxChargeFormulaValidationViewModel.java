package ca.ids.abms.modules.formulas.unifiedtax;

public class UnifiedTaxChargeFormulaValidationViewModel {

    private String formula;

    private Boolean formulaValid;

    private String issue;

    public String getFormula() {
        return formula;
    }

    public Boolean getFormulaValid() {
        return formulaValid;
    }

    public String getIssue() {
        return issue;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public void setFormulaValid(Boolean formulaValid) {
        this.formulaValid = formulaValid;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    @Override
    public String toString() {
        return "NavigationBillingFormulaValidationViewModel{" +
            ", formula='" + formula + '\'' +
            ", formulaValid=" + formulaValid +
            ", issue='" + issue + '\'' +
            '}';
    }
}
