package ca.ids.abms.modules.formulas.enroute;

import ca.ids.abms.modules.flightmovements.category.FlightmovementCategoryViewModel;
import ca.ids.abms.modules.util.models.VersionedViewModel;

public class EnrouteAirNavigationChargeFormulaViewModel extends VersionedViewModel {

    private Integer id;

    private FlightmovementCategoryViewModel flightmovementCategory;
    
    private String formula;
    
    private String dFactorFormula;

    public String getdFactorFormula() {
        return dFactorFormula;
    }

    public FlightmovementCategoryViewModel getFlightmovementCategory() {
        return flightmovementCategory;
    }

    public String getFormula() {
        return formula;
    }

    public Integer getId() {
        return id;
    }

    public void setdFactorFormula(String aDFactorFormula) {
        dFactorFormula = aDFactorFormula;
    }

    public void setFlightmovementCategory(FlightmovementCategoryViewModel aFlightmovementCategory) {
        flightmovementCategory = aFlightmovementCategory;
    }

    public void setFormula(String aFormula) {
        formula = aFormula;
    }

    public void setId(Integer aId) {
        id = aId;
    }
}
