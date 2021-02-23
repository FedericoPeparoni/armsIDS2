package ca.ids.abms.modules.formulas.navigation;

/**
 * Created by c.talpa on 07/12/2016.
 */
public class NavigationBillingFormulaValidationViewModel {

    private Integer idNavigationBillingFormula;

    private String formula;

    private Boolean formulaValid;

    private String issue;

    private String name;

    public Integer getIdNavigationBillingFormula() {
        return idNavigationBillingFormula;
    }

    public void setIdNavigationBillingFormula(Integer idNavigationBillingFormula) {
        this.idNavigationBillingFormula = idNavigationBillingFormula;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Boolean getFormulaValid() {
        return formulaValid;
    }

    public void setFormulaValid(Boolean formulaValid) {
        this.formulaValid = formulaValid;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "NavigationBillingFormulaValidationViewModel{" +
            "idNavigationBillingFormula=" + idNavigationBillingFormula +
            ", formula='" + formula + '\'' +
            ", formulaValid=" + formulaValid +
            ", issue='" + issue + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}
