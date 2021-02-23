package ca.ids.abms.modules.formulas.ldp;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.modules.aerodromes.AerodromeCategory;
import ca.ids.abms.modules.formulas.ldp.ChargeTypes.LdpBillingFormulaChargeType;

import ca.ids.abms.modules.util.models.VersionedAuditedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class LdpBillingFormula extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

	@EmbeddedId
    @SearchableEntity
    private LdpBillingFormulaKey id;

    @ManyToOne
    @MapsId("id")
    @SearchableEntity
    private AerodromeCategory aerodromeCategory;

    @NotNull
    @JsonIgnore
    private byte[] chargesSpreadsheet;

    @NotNull
    @Size(max = 128)
    private String spreadsheetContentType;

    @NotNull
    @Size(max = 128)
    private String spreadsheetFileName;

    public LdpBillingFormula(AerodromeCategory aerodromeCategory, String chargesType) {
        this.id = new LdpBillingFormulaKey(aerodromeCategory.getId(), chargesType);
        this.aerodromeCategory = aerodromeCategory;
    }

    protected LdpBillingFormula() {}

    public void setId(LdpBillingFormulaKey id) {
        this.id = id;
    }

    public AerodromeCategory getAerodromeCategory() {
        return aerodromeCategory;
    }

    public void setAerodromeCategory(AerodromeCategory aerodromeCategory) {
        this.aerodromeCategory = aerodromeCategory;
    }

    @Transient
    public Integer getAerodromeCategoryId() {
        return id.getAerodromeCategoryId();
    }

    @Transient
    public String getAerodromeCategoryName() {
        return this.aerodromeCategory.getCategoryName();
    }

    public LdpBillingFormulaChargeType getChargesType() {
        return LdpBillingFormulaChargeType.valueOf(id.getChargesType());
    }

    public void setChargesType(LdpBillingFormulaChargeType chargesType) {
        id.setChargesType(chargesType.name());
    }

    public byte[] getChargesSpreadsheet() {
        return chargesSpreadsheet;
    }

    public void setChargesSpreadsheet(byte[] chargesSpreadsheet) {
        this.chargesSpreadsheet = chargesSpreadsheet;
    }

    public LdpBillingFormulaKey getId() {
        return id;
    }

    public String getSpreadsheetContentType() {
        return spreadsheetContentType;
    }

    public void setSpreadsheetContentType(String spreadsheetContentType) {
        this.spreadsheetContentType = spreadsheetContentType;
    }

    public String getSpreadsheetFileName() {
        return spreadsheetFileName;
    }

    public void setSpreadsheetFileName(String spreadsheetFileName) {
        this.spreadsheetFileName = spreadsheetFileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LdpBillingFormula that = (LdpBillingFormula) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "LdpBillingFormula{" +
            "aerodromeCategory=" + (aerodromeCategory != null ? aerodromeCategory.getId() : "null") +
            ", chargesType='" + (id != null ? id.getChargesType() : "null") + '\'' +
            '}';
    }
}
