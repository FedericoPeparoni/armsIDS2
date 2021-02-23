package ca.ids.abms.modules.formulas.enroute;

import java.util.List;

import ca.ids.abms.modules.util.models.VersionedViewModel;

public class EnrouteAirNavigationChargeCategoryViewModel  extends VersionedViewModel {

    private Integer id;

    private Double mtowCategoryUpperLimit;
    
    private String wFactorFormula;
    
    private List<EnrouteAirNavigationChargeFormulaViewModel> enrouteAirNavigationChargeFormulas;
    
    public List<EnrouteAirNavigationChargeFormulaViewModel> getEnrouteAirNavigationChargeFormulas() {
        return enrouteAirNavigationChargeFormulas;
    }

    public Integer getId() {
        return id;
    }

    public Double getMtowCategoryUpperLimit() {
        return mtowCategoryUpperLimit;
    }

    public String getwFactorFormula() {
        return wFactorFormula;
    }

    public void setEnrouteAirNavigationChargeFormulas(
            List<EnrouteAirNavigationChargeFormulaViewModel> aEnrouteAirNavigationChargeFormulas) {
        enrouteAirNavigationChargeFormulas = aEnrouteAirNavigationChargeFormulas;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setMtowCategoryUpperLimit(Double aMtowCategoryUpperLimit) {
        mtowCategoryUpperLimit = aMtowCategoryUpperLimit;
    }

    public void setwFactorFormula(String aWFactorFormula) {
        wFactorFormula = aWFactorFormula;
    }
}