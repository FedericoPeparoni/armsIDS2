package ca.ids.abms.modules.exemptions.aircraftflights;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDateTime;

public class AircraftFlightsExemptionCsvExportModel {

    private String aircraftRegistration;

    private String flightId;

    private Double enrouteFeesExempt;

    private Double approachFeesExempt;

    private Double aerodromeFeesExempt;

    private Double lateArrivalFeesExempt;

    private Double lateDepartureFeesExempt;

    private Double parkingFeesExempt;

    @CsvProperty(value = "International PAX Fees Exempt")
    private Double internationalPax;

    @CsvProperty(value = "Domestic PAX Fees Exempt ")
    private Double domesticPax;

    @CsvProperty(value = "Extended Hours Fees Exempt")
    private Double extendedHours;

    private String flightNotes;

    @CsvProperty(date = true)
    private LocalDateTime exemptionStartDate;

    @CsvProperty(date = true)
    private LocalDateTime exemptionEndDate;

    public String getAircraftRegistration() {
        return aircraftRegistration;
    }

    public void setAircraftRegistration(String aircraftRegistration) {
        this.aircraftRegistration = aircraftRegistration;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public Double getEnrouteFeesExempt() {
        return enrouteFeesExempt;
    }

    public void setEnrouteFeesExempt(Double enrouteFeesExempt) {
        this.enrouteFeesExempt = enrouteFeesExempt;
    }

    public Double getApproachFeesExempt() {
        return approachFeesExempt;
    }

    public void setApproachFeesExempt(Double approachFeesExempt) {
        this.approachFeesExempt = approachFeesExempt;
    }

    public Double getAerodromeFeesExempt() {
        return aerodromeFeesExempt;
    }

    public void setAerodromeFeesExempt(Double aerodromeFeesExempt) {
        this.aerodromeFeesExempt = aerodromeFeesExempt;
    }

    public Double getLateArrivalFeesExempt() {
        return lateArrivalFeesExempt;
    }

    public void setLateArrivalFeesExempt(Double lateArrivalFeesExempt) {
        this.lateArrivalFeesExempt = lateArrivalFeesExempt;
    }

    public Double getLateDepartureFeesExempt() {
        return lateDepartureFeesExempt;
    }

    public void setLateDepartureFeesExempt(Double lateDepartureFeesExempt) {
        this.lateDepartureFeesExempt = lateDepartureFeesExempt;
    }

    public Double getParkingFeesExempt() {
        return parkingFeesExempt;
    }

    public void setParkingFeesExempt(Double parkingFeesExempt) {
        this.parkingFeesExempt = parkingFeesExempt;
    }

    public Double getInternationalPax() {
        return internationalPax;
    }

    public void setInternationalPax(Double internationalPax) {
        this.internationalPax = internationalPax;
    }

    public Double getDomesticPax() {
        return domesticPax;
    }

    public void setDomesticPax(Double domesticPax) {
        this.domesticPax = domesticPax;
    }

    public Double getExtendedHours() {
        return extendedHours;
    }

    public void setExtendedHours(Double extendedHours) {
        this.extendedHours = extendedHours;
    }

    public String getFlightNotes() {
        return flightNotes;
    }

    public void setFlightNotes(String flightNotes) {
        this.flightNotes = flightNotes;
    }

    public LocalDateTime getExemptionStartDate() {
        return exemptionStartDate;
    }

    public void setExemptionStartDate(LocalDateTime exemptionStartDate) {
        this.exemptionStartDate = exemptionStartDate;
    }

    public LocalDateTime getExemptionEndDate() {
        return exemptionEndDate;
    }

    public void setExemptionEndDate(LocalDateTime exemptionEndDate) {
        this.exemptionEndDate = exemptionEndDate;
    }
}
