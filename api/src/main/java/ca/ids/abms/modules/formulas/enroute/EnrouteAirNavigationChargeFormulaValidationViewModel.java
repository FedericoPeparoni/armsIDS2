package ca.ids.abms.modules.formulas.enroute;

public class EnrouteAirNavigationChargeFormulaValidationViewModel {

    private Integer idEnrouteAirNavigationChargeFormula;

    private String formula;

    private Boolean formulaValid;

    private String issue;

    public String getFormula() {
        return formula;
    }

    public Boolean getFormulaValid() {
        return formulaValid;
    }

    public Integer getIdEnrouteAirNavigationChargeFormula() {
        return idEnrouteAirNavigationChargeFormula;
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

    public void setIdEnrouteAirNavigationChargeFormula(Integer aIdEnrouteAirNavigationChargeFormula) {
        idEnrouteAirNavigationChargeFormula = aIdEnrouteAirNavigationChargeFormula;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    @Override
    public String toString() {
        return "NavigationBillingFormulaValidationViewModel{" +
            "idEnrouteAirNavigationChargeFormula=" + idEnrouteAirNavigationChargeFormula +
            ", formula='" + formula + '\'' +
            ", formulaValid=" + formulaValid +
            ", issue='" + issue + '\'' +
            '}';
    }
}
