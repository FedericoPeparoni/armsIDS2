package ca.ids.abms.modules.formulas.ldp;

import ca.ids.abms.config.db.SearchableText;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class LdpBillingFormulaKey implements Serializable {

    private static final long serialVersionUID = 1L;

	@Column(name = "aerodrome_category_id")
    private Integer aerodromeCategoryId;

    @Column(name = "charges_type")
    @SearchableText
    private String chargesType;

    public LdpBillingFormulaKey() {
        super();
    }

    public LdpBillingFormulaKey(Integer aerodromeCategoryId, String chargesType) {
        this.aerodromeCategoryId = aerodromeCategoryId;
        this.chargesType = chargesType;
    }

    public Integer getAerodromeCategoryId() {
        return aerodromeCategoryId;
    }

    public void setAerodromeCategoryId(Integer aerodromeCategoryId) {
        this.aerodromeCategoryId = aerodromeCategoryId;
    }

    public String getChargesType() {
        return chargesType;
    }

    public void setChargesType(String chargesType) {
        this.chargesType = chargesType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LdpBillingFormulaKey that = (LdpBillingFormulaKey) o;

        if (aerodromeCategoryId != null ? !aerodromeCategoryId.equals(that.aerodromeCategoryId) : that.aerodromeCategoryId != null)
            return false;
        return chargesType != null ? chargesType.equals(that.chargesType) : that.chargesType == null;

    }

    @Override
    public int hashCode() {
        int result = aerodromeCategoryId != null ? aerodromeCategoryId.hashCode() : 0;
        result = 31 * result + (chargesType != null ? chargesType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Key{" +
            "aerodromeCategoryId=" + aerodromeCategoryId +
            ", chargesType='" + chargesType + '\'' +
            '}';
    }
}
