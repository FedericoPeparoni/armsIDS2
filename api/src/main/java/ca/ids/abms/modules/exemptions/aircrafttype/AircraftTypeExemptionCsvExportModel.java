package ca.ids.abms.modules.exemptions.aircrafttype;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class AircraftTypeExemptionCsvExportModel {

    @CsvProperty(value = "A/C Type")
    private String aircraftType;

    private Double enrouteFeesExempt;

    private Double approachFeesExempt;

    private Double aerodromeFeesExempt;

    private Double lateArrivalFeesExempt;

    private Double lateDepartureFeesExempt;

    private Double parkingFeesExempt;

    @CsvProperty(value = "International PAX Fees Exempt")
    private Double internationalPax;

    @CsvProperty(value = "Domestic PAX Fees Exempt")
    private Double domesticPax;

    @CsvProperty(value = "Extended Hours Fees Exempt")
    private Double extendedHours;

    private String flightNotes;

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
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
}
