package ca.ids.abms.modules.unspecified;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class UnspecifiedDepartureDestinationLocationCsvExportModel {

    private String textIdentifier;

    private String name;

    private Boolean maintained;

    @CsvProperty(value = "Ad Name")
    private String aerodromeIdentifier;

    private String countryCode;

    private String status;

    @CsvProperty(latitude = true)
    private Double latitude;

    @CsvProperty(longitude = true)
    private Double longitude;

    public String getTextIdentifier() {
        return textIdentifier;
    }

    public void setTextIdentifier(String textIdentifier) {
        this.textIdentifier = textIdentifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getMaintained() {
        return maintained;
    }

    public void setMaintained(Boolean maintained) {
        this.maintained = maintained;
    }

    public String getAerodromeIdentifier() {
        return aerodromeIdentifier;
    }

    public void setAerodromeIdentifier(String aerodromeIdentifier) {
        this.aerodromeIdentifier = aerodromeIdentifier;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
