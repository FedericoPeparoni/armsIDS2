package ca.ids.abms.modules.selfcareportal.flightsearch;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDateTime;

public class SCFlightMovementCsvExportModel {

    @CsvProperty(value = "Account")
    private String accountName;

    @CsvProperty(value = "Registration Number")
    private String item18RegNum;

    private String flightId;

    @CsvProperty(date = true)
    private LocalDateTime dateOfFlight;

    private String status;

    @CsvProperty(value = "Departure Time")
    private String depTime;

    @CsvProperty(value = "Departure Aerodrome")
    private String depAd;

    @CsvProperty(value = "Destination Aerodrome")
    private String destAd;

    @CsvProperty(value = "Flight Movement Cost (USD)", precision = 2)
    private Double totalChargesUsd;

    private String flightNotes;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getItem18RegNum() {
        return item18RegNum;
    }

    public void setItem18RegNum(String item18RegNum) {
        this.item18RegNum = item18RegNum;
    }

    public LocalDateTime getDateOfFlight() {
        return dateOfFlight;
    }

    public void setDateOfFlight(LocalDateTime dateOfFlight) {
        this.dateOfFlight = dateOfFlight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDepTime() {
        return depTime;
    }

    public void setDepTime(String depTime) {
        this.depTime = depTime;
    }

    public String getDepAd() {
        return depAd;
    }

    public void setDepAd(String depAd) {
        this.depAd = depAd;
    }

    public String getDestAd() {
        return destAd;
    }

    public void setDestAd(String destAd) {
        this.destAd = destAd;
    }

    public Double getTotalChargesUsd() {
        return totalChargesUsd;
    }

    public void setTotalChargesUsd(Double totalChargesUsd) {
        this.totalChargesUsd = totalChargesUsd;
    }

    public String getFlightNotes() {
        return flightNotes;
    }

    public void setFlightNotes(String flightNotes) {
        this.flightNotes = flightNotes;
    }
}
