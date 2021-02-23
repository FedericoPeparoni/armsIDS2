package ca.ids.abms.modules.flight;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDateTime;

public class FlightReassignmentCsvExportModel {

    @CsvProperty(value = "Reassigned Company")
    private String account;

    @CsvProperty(date = true)
    private LocalDateTime startDate;

    @CsvProperty(date = true)
    private LocalDateTime endDate;

    private String identificationType;

    private String identifierText;

    private String aerodromes;

    private String flightType;

    private String flightScope;

    private String aircraftNationality;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(String identificationType) {
        this.identificationType = identificationType;
    }

    public String getIdentifierText() {
        return identifierText;
    }

    public void setIdentifierText(String identifierText) {
        this.identifierText = identifierText;
    }

    public String getAerodromes() {
        return aerodromes;
    }

    public void setAerodromes(String aerodromes) {
        this.aerodromes = aerodromes;
    }

    public String getFlightType() {
        return flightType;
    }

    public void setFlightType(String flightType) {
        this.flightType = flightType;
    }

    public String getFlightScope() {
        return flightScope;
    }

    public void setFlightScope(String flightScope) {
        this.flightScope = flightScope;
    }

    public String getAircraftNationality() {
        return aircraftNationality;
    }

    public void setAircraftNationality(String aircraftNationality) {
        this.aircraftNationality = aircraftNationality;
    }
}
