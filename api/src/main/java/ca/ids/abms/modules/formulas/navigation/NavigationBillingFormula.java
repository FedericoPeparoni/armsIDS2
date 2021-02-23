package ca.ids.abms.modules.formulas.navigation;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

@Entity
public class NavigationBillingFormula extends VersionedAuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name="upper_limit")
    private Double upperLimit;

    @NotNull
    @Column(name="domestic_formula")
    private String domesticFormula;

    @NotNull
    @Column(name="regional_departure_formula")
    private String regionalDepartureFormula;

    @NotNull
    @Column(name="regional_arrival_formula")
    private String regionalArrivalFormula;

    @NotNull
    @Column(name="regional_overflight_formula")
    private String regionalOverflightFormula;

    @NotNull
    @Column(name="international_departure_formula")
    private String internationalDepartureFormula;

    @NotNull
    @Column(name="international_arrival_formula")
    private String internationalArrivalFormula;

    @NotNull
    @Column(name="international_overflight_formula")
    private String internationalOverflightFormula;

    @NotNull
    @Column(name="domestic_d_factor_formula")
    private String domesticDFactorFormula;

    @NotNull
    @Column(name="reg_dep_d_factor_formula")
    private String regDepDFactorFormula;

    @NotNull
    @Column(name="reg_arr_d_factor_formula")
    private String  regArrDFactorFormula;

    @NotNull
    @Column(name="reg_ovr_d_factor_formula")
    private String regOvrDFactorFormula;

    @NotNull
    @Column(name="int_dep_d_factor_formula")
    private String intDepDFactorFormula;

    @NotNull
    @Column(name="int_arr_d_factor_formula")
    private String intArrDFactorFormula;

    @NotNull
    @Column(name="int_ovr_d_factor_formula")
    private String intOvrDFactorFormula;

    @NotNull
    @Column(name="w_factor_formula")
    private String wFactorFormula;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NavigationBillingFormula other = (NavigationBillingFormula) obj;
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

    public Integer getId() {
        return id;
    }

    public String getDomesticFormula() {
        return domesticFormula;
    }

    public String getDomesticDFactorFormula() {
        return domesticDFactorFormula;
    }

    public String getIntArrDFactorFormula() {
        return intArrDFactorFormula;
    }

    public String getIntDepDFactorFormula() {
        return intDepDFactorFormula;
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

    public String getRegOvrDFactorFormula() {
        return regOvrDFactorFormula;
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

    public String getRegionalArrivalFormula() {
        return regionalArrivalFormula;
    }

    public String getRegionalDepartureFormula() {
        return regionalDepartureFormula;
    }

    public String getRegionalOverflightFormula() {
        return regionalOverflightFormula;
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

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDomesticDFactorFormula(String aDomesticDFactorFormula) {
        domesticDFactorFormula = aDomesticDFactorFormula;
    }

    public void setDomesticFormula(String domesticFormula) {
        this.domesticFormula = domesticFormula;
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

}
