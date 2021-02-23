package ca.ids.abms.modules.exemptions.account;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class AccountExemptionCsvExportModel {

    private String account;

    @CsvProperty(value = "Enroute Fees Exempt")
    private Double enroute;

    @CsvProperty(value = "Approach Fees Exempt")
    private Double approachFeesExempt;

    @CsvProperty(value = "Aerodrome Fees Exempt")
    private Double aerodromeFeesExempt;

    @CsvProperty(value = "Late Arrival Fees Exempt")
    private Double lateArrival;

    @CsvProperty(value = "Late Departure Fees Exempt")
    private Double lateDeparture;

    @CsvProperty(value = "Parking Fees Exempt")
    private Double parking;

    @CsvProperty(value = "International PAX Fees Exempt")
    private Double internationalPax;

    @CsvProperty(value = "Domestic PAX Fees Exempt")
    private Double domesticPax;

    @CsvProperty(value = "Extended Hours Fees Exempt")
    private Double extendedHours;

    private String flightNotes;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Double getEnroute() {
        return enroute;
    }

    public void setEnroute(Double enroute) {
        this.enroute = enroute;
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

    public Double getLateArrival() {
        return lateArrival;
    }

    public void setLateArrival(Double lateArrival) {
        this.lateArrival = lateArrival;
    }

    public Double getLateDeparture() {
        return lateDeparture;
    }

    public void setLateDeparture(Double lateDeparture) {
        this.lateDeparture = lateDeparture;
    }

    public Double getParking() {
        return parking;
    }

    public void setParking(Double parking) {
        this.parking = parking;
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
