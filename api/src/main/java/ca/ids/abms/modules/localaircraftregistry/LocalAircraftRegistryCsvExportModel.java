package ca.ids.abms.modules.localaircraftregistry;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDateTime;

public class LocalAircraftRegistryCsvExportModel {

    private String registrationNumber;

    private String ownerName;

    private String analysisType;

    @CsvProperty(value = "MTOW", mtow = true, precision = 2)
    private Double mtowWeight;

    @CsvProperty(date = true, value = "CoA Date of Renewal")
    private LocalDateTime coaDateOfRenewal;

    @CsvProperty(date = true, value = "CoA Date of Expiry")
    private LocalDateTime coaDateOfExpiry;

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    public Double getMtowWeight() {
        return mtowWeight;
    }

    public void setMtowWeight(Double mtowWeight) {
        this.mtowWeight = mtowWeight;
    }

    public LocalDateTime getCoaDateOfRenewal() {
        return coaDateOfRenewal;
    }

    public void setCoaDateOfRenewal(LocalDateTime coaDateOfRenewal) {
        this.coaDateOfRenewal = coaDateOfRenewal;
    }

    public LocalDateTime getCoaDateOfExpiry() {
        return coaDateOfExpiry;
    }

    public void setCoaDateOfExpiry(LocalDateTime coaDateOfExpiry) {
        this.coaDateOfExpiry = coaDateOfExpiry;
    }
}
