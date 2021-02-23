package ca.ids.abms.modules.aerodromes;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.formulas.ldp.LdpBillingFormula;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@UniqueKey(columnNames = "categoryName")
public class AerodromeCategory extends VersionedAuditedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 100)
    @Column(unique = true)
    @SearchableText
    private String categoryName;

    @NotNull
    private Double internationalPassengerFeeAdult;

    @NotNull
    private Double internationalPassengerFeeChild;

    @NotNull
    private Double domesticPassengerFeeAdult;

    @NotNull
    private Double domesticPassengerFeeChild;

    @JsonIgnore
    @OneToMany(mappedBy = "aerodromeCategory")
    private Set<Aerodrome> aerodromes = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "aerodromeCategory")
    private Set<LdpBillingFormula> ldpBillingFormulas = new HashSet<>();

    private static final long serialVersionUID = 1L;

    public Set<Aerodrome> getAerodromes() {
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

    public Set<LdpBillingFormula> getLdpBillingFormulas() {
        return ldpBillingFormulas;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setAerodromes(Set<Aerodrome> aAerodromes) {
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

    public void setLdpBillingFormulas(Set<LdpBillingFormula> ldpBillingFormulas) {
        this.ldpBillingFormulas = ldpBillingFormulas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AerodromeCategory aerodromeCategory = (AerodromeCategory) o;

        return id != null ? id.equals(aerodromeCategory.id) : aerodromeCategory.id == null;
    }

	@Override
	public String toString() {
		return "AerodromeCategory [id=" + id + ", categoryName=" + categoryName
				+ ", internationalPassengerFeeAdult="
				+ internationalPassengerFeeAdult
				+ ", internationalPassengerFeeChild="
				+ internationalPassengerFeeChild
				+ ", domesticPassengerFeeAdult=" + domesticPassengerFeeAdult
				+ ", domesticPassengerFeeChild=" + domesticPassengerFeeChild
				+ ", aerodromes=" + aerodromes + "]";
	}
}
