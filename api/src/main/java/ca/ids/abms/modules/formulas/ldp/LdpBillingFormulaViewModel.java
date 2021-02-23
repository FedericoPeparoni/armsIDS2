package ca.ids.abms.modules.formulas.ldp;

import ca.ids.abms.modules.common.dto.EmbeddedFileDto;

public class LdpBillingFormulaViewModel extends EmbeddedFileDto {

    private String chargesType;

    private Integer id;

    private Integer aerodromeCategoryId;

    private String aerodromeCategoryName;

    public String getChargesType() {
        return chargesType;
    }

    public void setChargesType(String chargesType) {
        this.chargesType = chargesType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAerodromeCategoryId() {
        return aerodromeCategoryId;
    }

    public void setAerodromeCategoryId(Integer aerodromeCategoryId) {
        this.aerodromeCategoryId = aerodromeCategoryId;
    }

    public String getAerodromeCategoryName() {
        return aerodromeCategoryName;
    }

    public void setAerodromeCategoryName(String aerodromeCategoryName) {
        this.aerodromeCategoryName = aerodromeCategoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LdpBillingFormulaViewModel that = (LdpBillingFormulaViewModel) o;

        if (chargesType != null ? !chargesType.equals(that.chargesType) : that.chargesType != null) return false;
        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        int result = chargesType != null ? chargesType.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LdpBillingFormulaViewModel{" +
            "chargesType='" + chargesType + '\'' +
            ", id='" + id + '\'' +
            ", aerodromeCategoryName='" + aerodromeCategoryName + '\'' +
            ", spreadsheetContentType='" + getDocumentMimeType() + '\'' +
            ", spreadsheetFileName='" + getDocumentFilename() + '\'' +
            '}';
    }
}
