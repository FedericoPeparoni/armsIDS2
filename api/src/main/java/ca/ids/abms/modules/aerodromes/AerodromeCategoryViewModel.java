package ca.ids.abms.modules.aerodromes;

import java.util.Collection;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.modules.formulas.ldp.LdpBillingFormulaViewModel;
import ca.ids.abms.modules.util.models.VersionedViewModel;

public class AerodromeCategoryViewModel extends VersionedViewModel {

    private Integer id;

    @Size(max = 100)
    private String categoryName;

    @NotNull
    private Double internationalPassengerFeeAdult;

    @NotNull
    private Double internationalPassengerFeeChild;

    @NotNull
    private Double domesticPassengerFeeAdult;

    @NotNull
    private Double domesticPassengerFeeChild;

    private Set<LdpBillingFormulaViewModel> ldpBillingFormulas;

    private Collection<String> aerodromes;

    public Collection<String> getAerodromes() {
        return aerodromes;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Double getDomesticPassengerFeeAdult() {
        return domesticPassengerFeeAdult;
    }

    public Double getDomesticPassengerFeeChild() {
        return domesticPassengerFeeChild;
    }

    public Integer getId() {
        return id;
    }

    public Double getInternationalPassengerFeeAdult() {
        return internationalPassengerFeeAdult;
    }

    public Double getInternationalPassengerFeeChild() {
        return internationalPassengerFeeChild;
    }

    public Set<LdpBillingFormulaViewModel> getLdpBillingFormulas() {
        return ldpBillingFormulas;
    }

    public void setAerodromes(Collection<String> aAerodromes) {
        aerodromes = aAerodromes;
    }

    public void setCategoryName(String aCategoryName) {
        categoryName = aCategoryName;
    }

    public void setDomesticPassengerFeeAdult(Double aDomesticPassengerFeeAdult) {
        domesticPassengerFeeAdult = aDomesticPassengerFeeAdult;
    }

    public void setDomesticPassengerFeeChild(Double aDomesticPassengerFeeChild) {
        domesticPassengerFeeChild = aDomesticPassengerFeeChild;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setInternationalPassengerFeeAdult(Double aInternationalPassengerFeeAdult) {
        internationalPassengerFeeAdult = aInternationalPassengerFeeAdult;
    }

    public void setInternationalPassengerFeeChild(Double aInternationalPassengerFeeChild) {
        internationalPassengerFeeChild = aInternationalPassengerFeeChild;
    }

    public void setLdpBillingFormulas(Set<LdpBillingFormulaViewModel> ldpBillingFormulas) {
        this.ldpBillingFormulas = ldpBillingFormulas;
    }

    @Override
    public String toString() {
        return "AerodromeCategoryViewModel [id=" + id + ", categoryName=" + categoryName
                + ", internationalPassengerFeeAdult=" + internationalPassengerFeeAdult
                + ", internationalPassengerFeeChild=" + internationalPassengerFeeChild + ", domesticPassengerFeeAdult="
                + domesticPassengerFeeAdult + ", domesticPassengerFeeChild=" + domesticPassengerFeeChild
                + ", aerodromes=" + aerodromes + "]";
    }

}
