package ca.ids.abms.modules.exemptions.flightstatus;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class ExemptFlightStatusCsvExportModel {

    private String flightItemType;

    private String flightItemValue;

    @CsvProperty(value = "Enroute Fees Exempt")
    private Double enrouteFeesAreExempt;

    private Double approachFeesExempt;

    private Double aerodromeFeesExempt;

    private Double lateArrivalFeesExempt;

    private Double lateDepartureFeesExempt;

    @CsvProperty(value = "Parking Fees Exempt")
    private Double parkingFeesAreExempt;

    @CsvProperty(value = "International PAX Fees Exempt")
    private Double internationalPax;

    @CsvProperty(value = "Domestic PAX Fees Exempt")
    private Double domesticPax;

    @CsvProperty(value = "Extended Hours Fees Exempt")
    private Double extendedHours;

    private String  flightNotes;

    public String getFlightItemType() {
        return flightItemType;
    }

    public void setFlightItemType(String flightItemType) {
        this.flightItemType = flightItemType;
    }

    public String getFlightItemValue() {
        return flightItemValue;
    }

    public void setFlightItemValue(String flightItemValue) {
        this.flightItemValue = flightItemValue;
    }

    public Double getEnrouteFeesAreExempt() {
        return enrouteFeesAreExempt;
    }

    public void setEnrouteFeesAreExempt(Double enrouteFeesAreExempt) {
        this.enrouteFeesAreExempt = enrouteFeesAreExempt;
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

    public Double getParkingFeesAreExempt() {
        return parkingFeesAreExempt;
    }

    public void setParkingFeesAreExempt(Double parkingFeesAreExempt) {
        this.parkingFeesAreExempt = parkingFeesAreExempt;
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
