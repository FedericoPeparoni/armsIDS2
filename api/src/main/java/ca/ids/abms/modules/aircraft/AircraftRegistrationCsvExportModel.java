package ca.ids.abms.modules.aircraft;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDateTime;

public class AircraftRegistrationCsvExportModel {

    private String registrationNumber;

    @CsvProperty(date = true, value = "Start Date")
    private LocalDateTime registrationStartDate;

    @CsvProperty(date = true, value = "Expiry Date")
    private LocalDateTime registrationExpiryDate;

    private String account;

    private String aircraftType;

    @CsvProperty(value = "MTOW Override", mtow = true, precision = 2)
    private Double mtowOverride;

    @CsvProperty(value = "Created By Self-Care")
    private Boolean createdBySelfCare;

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public LocalDateTime getRegistrationStartDate() {
        return registrationStartDate;
    }

    public void setRegistrationStartDate(LocalDateTime registrationStartDate) {
        this.registrationStartDate = registrationStartDate;
    }

    public LocalDateTime getRegistrationExpiryDate() {
        return registrationExpiryDate;
    }

    public void setRegistrationExpiryDate(LocalDateTime registrationExpiryDate) {
        this.registrationExpiryDate = registrationExpiryDate;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Double getMtowOverride() {
        return mtowOverride;
    }

    public void setMtowOverride(Double mtowOverride) {
        this.mtowOverride = mtowOverride;
    }

    public Boolean getCreatedBySelfCare() {
        return createdBySelfCare;
    }

    public void setCreatedBySelfCare(Boolean createdBySelfCare) {
        this.createdBySelfCare = createdBySelfCare;
    }
}
