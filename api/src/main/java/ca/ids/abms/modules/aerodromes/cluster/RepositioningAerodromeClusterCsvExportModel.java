package ca.ids.abms.modules.aerodromes.cluster;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class RepositioningAerodromeClusterCsvExportModel {

    @CsvProperty(value = "Cluster Name")
    private String repositioningAerodromeClusterName;

    @CsvProperty(value = "Enroute Fees Exempt")
    private Double enrouteFeesAreExempt;

    @CsvProperty(value = "Approach Fees Exempt")
    private Double approachFeesAreExempt;

    @CsvProperty(value = "Aerodrome Fees Exempt")
    private Double aerodromeFeesAreExempt;

    @CsvProperty(value = "Late Arrival Fees Exempt")
    private Double lateArrivalFeesAreExempt;

    @CsvProperty(value = "Late Departure Fees Exempt")
    private Double lateDepartureFeesAreExempt;

    @CsvProperty(value = "Parking Fees Exempt")
    private Double parkingFeesAreExempt;

    @CsvProperty(value = "International PAX Fees Exempt")
    private Double internationalPax;

    @CsvProperty(value = "Domestic PAX Fees Exempt")
    private Double domesticPax;

    @CsvProperty(value = "Extended Hours Fees Exempt")
    private Double extendedHours;

    private String flightNotes;

    public String getRepositioningAerodromeClusterName() {
        return repositioningAerodromeClusterName;
    }

    public void setRepositioningAerodromeClusterName(String repositioningAerodromeClusterName) {
        this.repositioningAerodromeClusterName = repositioningAerodromeClusterName;
    }

    public Double getEnrouteFeesAreExempt() {
        return enrouteFeesAreExempt;
    }

    public void setEnrouteFeesAreExempt(Double enrouteFeesAreExempt) {
        this.enrouteFeesAreExempt = enrouteFeesAreExempt;
    }

    public Double getApproachFeesAreExempt() {
        return approachFeesAreExempt;
    }

    public void setApproachFeesAreExempt(Double approachFeesAreExempt) {
        this.approachFeesAreExempt = approachFeesAreExempt;
    }

    public Double getAerodromeFeesAreExempt() {
        return aerodromeFeesAreExempt;
    }

    public void setAerodromeFeesAreExempt(Double aerodromeFeesAreExempt) {
        this.aerodromeFeesAreExempt = aerodromeFeesAreExempt;
    }

    public Double getLateArrivalFeesAreExempt() {
        return lateArrivalFeesAreExempt;
    }

    public void setLateArrivalFeesAreExempt(Double lateArrivalFeesAreExempt) {
        this.lateArrivalFeesAreExempt = lateArrivalFeesAreExempt;
    }

    public Double getLateDepartureFeesAreExempt() {
        return lateDepartureFeesAreExempt;
    }

    public void setLateDepartureFeesAreExempt(Double lateDepartureFeesAreExempt) {
        this.lateDepartureFeesAreExempt = lateDepartureFeesAreExempt;
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
