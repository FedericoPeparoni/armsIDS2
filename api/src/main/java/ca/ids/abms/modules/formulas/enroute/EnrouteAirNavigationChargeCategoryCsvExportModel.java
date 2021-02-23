package ca.ids.abms.modules.formulas.enroute;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.util.ArrayList;
import java.util.List;

public class EnrouteAirNavigationChargeCategoryCsvExportModel {

    @CsvProperty(value = "MTOW Upper Limit ", mtow = true, precision = 2)
    private Double mtowCategoryUpperLimit;

    private String wFactorFormula;

    private List<EnrouteAirNavigationChargeCategoryMapper.EnrouteAirNavigationChargeFormulas> enrouteAirNavigationChargeFormulas = new ArrayList<>();

    public Double getMtowCategoryUpperLimit() {
        return mtowCategoryUpperLimit;
    }

    void setMtowCategoryUpperLimit(Double mtowCategoryUpperLimit) {
        this.mtowCategoryUpperLimit = mtowCategoryUpperLimit;
    }

    public String getwFactorFormula() {
        return wFactorFormula;
    }

    void setwFactorFormula(String wFactorFormula) {
        this.wFactorFormula = wFactorFormula;
    }

    public List<EnrouteAirNavigationChargeCategoryMapper.EnrouteAirNavigationChargeFormulas> getEnrouteAirNavigationChargeFormulas() {
        return enrouteAirNavigationChargeFormulas;
    }

    public void setEnrouteAirNavigationChargeFormulas(List<EnrouteAirNavigationChargeCategoryMapper.EnrouteAirNavigationChargeFormulas> enrouteAirNavigationChargeFormulas) {
        this.enrouteAirNavigationChargeFormulas = enrouteAirNavigationChargeFormulas;
    }
}
