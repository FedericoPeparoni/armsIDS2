package ca.ids.abms.modules.formulas.navigation;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import ca.ids.abms.modules.util.models.VersionedViewModel;

public class NavigationBillingFormulaViewModel extends VersionedViewModel {

    private Integer id;

    @NotNull
    @JsonProperty("upper_limit")
    private Double upperLimit;

    @JsonProperty("domestic_d_factor_formula")
    private String domesticDFactorFormula;

    @JsonProperty("reg_dep_d_factor_formula")
    private String regDepDFactorFormula;

    @JsonProperty("reg_arr_d_factor_formula")
    private String regArrDFactorFormula;

    @JsonProperty("reg_ovr_d_factor_formula")
    private String regOvrDFactorFormula;

    @JsonProperty("int_dep_d_factor_formula")
    private String intDepDFactorFormula;

    @JsonProperty("int_arr_d_factor_formula")
    private String intArrDFactorFormula;

    @JsonProperty("int_ovr_d_factor_formula")
    private String intOvrDFactorFormula;

    @JsonProperty("domestic_formula")
    private String domesticFormula;

    @JsonProperty("regional_departure_formula")
    private String regionalDepartureFormula;

    @JsonProperty("regional_arrival_formula")
    private String regionalArrivalFormula;

    @JsonProperty("regional_overflight_formula")
    private String regionalOverflightFormula;

    @JsonProperty("international_departure_formula")
    private String internationalDepartureFormula;

    @JsonProperty("international_arrival_formula")
    private String internationalArrivalFormula;

    @JsonProperty("international_overflight_formula")
    private String internationalOverflightFormula;

    @JsonProperty("w_factor_formula")
    private String wFactorFormula;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NavigationBillingFormulaViewModel other = (NavigationBillingFormulaViewModel) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (upperLimit == null) {
            if (other.upperLimit != null)
                return false;
        } else if (!upperLimit.equals(other.upperLimit))
            return false;
        return true;
    }

    public String getDomesticDFactorFormula() {
        return domesticDFactorFormula;
    }

    public String getDomesticFormula() {
        return domesticFormula;
    }

    public Integer getId() {
        return id;
    }

    public String getIntArrDFactorFormula() {
        return intArrDFactorFormula;
    }

    public String getIntDepDFactorFormula() {
        return intDepDFactorFormula;
    }

    public String getInternationalArrivalFormula() {
        return internationalArrivalFormula;
    }

    public String getInternationalDepartureFormula() {
        return internationalDepartureFormula;
    }

    public String getInternationalOverflightFormula() {
        return internationalOverflightFormula;
    }

    public String getIntOvrDFactorFormula() {
        return intOvrDFactorFormula;
    }

    public String getRegArrDFactorFormula() {
        return regArrDFactorFormula;
    }

    public String getRegDepDFactorFormula() {
        return regDepDFactorFormula;
    }

    public String getRegionalArrivalFormula() {
        return regionalArrivalFormula;
    }

    public String getRegionalDepartureFormula() {
        return regionalDepartureFormula;
    }

    public String getRegionalOverflightFormula() {
        return regionalOverflightFormula;
    }

    public String getRegOvrDFactorFormula() {
        return regOvrDFactorFormula;
    }

    public Double getUpperLimit() {
        return upperLimit;
    }

    public String getwFactorFormula() {
        return wFactorFormula;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((upperLimit == null) ? 0 : upperLimit.hashCode());
        return result;
    }

    public void setDomesticDFactorFormula(String aDomesticDFactorFormula) {
        domesticDFactorFormula = aDomesticDFactorFormula;
    }

    public void setDomesticFormula(String domesticFormula) {
        this.domesticFormula = domesticFormula;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setIntArrDFactorFormula(String aIntArrDFactorFormula) {
        intArrDFactorFormula = aIntArrDFactorFormula;
    }

    public void setIntDepDFactorFormula(String aIntDepDFactorFormula) {
        intDepDFactorFormula = aIntDepDFactorFormula;
    }

    public void setInternationalArrivalFormula(String internationalArrivalFormula) {
        this.internationalArrivalFormula = internationalArrivalFormula;
    }

    public void setInternationalDepartureFormula(String internationalDepartureFormula) {
        this.internationalDepartureFormula = internationalDepartureFormula;
    }

    public void setInternationalOverflightFormula(String internationalOverflightFormula) {
        this.internationalOverflightFormula = internationalOverflightFormula;
    }

    public void setIntOvrDFactorFormula(String aIntOvrDFactorFormula) {
        intOvrDFactorFormula = aIntOvrDFactorFormula;
    }

    public void setRegArrDFactorFormula(String aRegArrDFactorFormula) {
        regArrDFactorFormula = aRegArrDFactorFormula;
    }

    public void setRegDepDFactorFormula(String aRegDepDFactorFormula) {
        regDepDFactorFormula = aRegDepDFactorFormula;
    }

    public void setRegionalArrivalFormula(String regionalArrivalFormula) {
        this.regionalArrivalFormula = regionalArrivalFormula;
    }

    public void setRegionalDepartureFormula(String regionalDepartureFormula) {
        this.regionalDepartureFormula = regionalDepartureFormula;
    }

    public void setRegionalOverflightFormula(String regionalOverflightFormula) {
        this.regionalOverflightFormula = regionalOverflightFormula;
    }

    public void setRegOvrDFactorFormula(String aRegOvrDFactorFormula) {
        regOvrDFactorFormula = aRegOvrDFactorFormula;
    }

    public void setUpperLimit(Double upperLimit) {
        this.upperLimit = upperLimit;
    }

    public void setwFactorFormula(String aWFactorFormula) {
        wFactorFormula = aWFactorFormula;
    }

    @Override
    public String toString() {
        return "NavigationBillingFormulaViewModel [id=" + id + ", upperLimit=" + upperLimit + "]";
    }

}
