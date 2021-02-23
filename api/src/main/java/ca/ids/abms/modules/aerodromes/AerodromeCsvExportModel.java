package ca.ids.abms.modules.aerodromes;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class AerodromeCsvExportModel {

    @CsvProperty(value = "Aerodrome Name")
    private String extendedAerodromeName;

    @CsvProperty(value = "ICAO Identifier")
    private String aerodromeName;

    @CsvProperty(value = "Defined in AIX/M")
    private Boolean aixmFlag;

    private String billingCenter;

    @CsvProperty(value = "Default for Billing Centre")
    private Boolean isDefaultBillingCenter;

    private String aerodromeCategory;

    @CsvProperty(latitude = true)
    private Double latitude;

    @CsvProperty(longitude = true)
    private Double longitude;

    public String getExtendedAerodromeName() {
        return extendedAerodromeName;
    }

    public void setExtendedAerodromeName(String extendedAerodromeName) {
        this.extendedAerodromeName = extendedAerodromeName;
    }

    public String getAerodromeName() {
        return aerodromeName;
    }

    public void setAerodromeName(String aerodromeName) {
        this.aerodromeName = aerodromeName;
    }

    public Boolean getAixmFlag() {
        return aixmFlag;
    }

    public void setAixmFlag(Boolean aixmFlag) {
        this.aixmFlag = aixmFlag;
    }

    public String getBillingCenter() {
        return billingCenter;
    }

    public void setBillingCenter(String billingCenter) {
        this.billingCenter = billingCenter;
    }

    public Boolean getIsDefaultBillingCenter() {
        return isDefaultBillingCenter;
    }

    public void setIsDefaultBillingCenter(Boolean isDefaultBillingCenter) {
        this.isDefaultBillingCenter = isDefaultBillingCenter;
    }

    public String getAerodromeCategory() {
        return aerodromeCategory;
    }

    public void setAerodromeCategory(String aerodromeCategory) {
        this.aerodromeCategory = aerodromeCategory;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
