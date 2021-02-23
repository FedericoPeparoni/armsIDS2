package ca.ids.abms.modules.radarsummary;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDateTime;

public class RadarSummaryCsvExportModel {

    @CsvProperty(value = "Flight Id")
    private String flightIdentifier;

    @CsvProperty(date = true)
    private LocalDateTime date;

    @CsvProperty(value = "Dep Time")
    private String departureTime;

    @CsvProperty(date = true)
    private LocalDateTime dayOfFlight;

    @CsvProperty(value = "Reg Number")
    private String registration;

    @CsvProperty(value = "A/C Type")
    private String  aircraftType;

    @CsvProperty(value = "Dep Ad")
    private String departureAeroDrome;

    @CsvProperty(value = "Dest Ad")
    private String destinationAeroDrome;

    @CsvProperty(value = "Entry Point")
    private String firEntryPoint;

    @CsvProperty(value = "Entry Time")
    private String firEntryTime;

    @CsvProperty(value = "Exit Point")
    private String firExitPoint;

    @CsvProperty(value = "Exit Time")
    private String firExitTime;

    private String flightRule;

    @CsvProperty(value = "Flight Category")
    private String flightTravelCategory;

    private String route;

    public String getFlightIdentifier() {
        return flightIdentifier;
    }

    public void setFlightIdentifier(String flightIdentifier) {
        this.flightIdentifier = flightIdentifier;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getDayOfFlight() {
        return dayOfFlight;
    }

    public void setDayOfFlight(LocalDateTime dayOfFlight) {
        this.dayOfFlight = dayOfFlight;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }

    public String getDepartureAeroDrome() {
        return departureAeroDrome;
    }

    public void setDepartureAeroDrome(String departureAeroDrome) {
        this.departureAeroDrome = departureAeroDrome;
    }

    public String getDestinationAeroDrome() {
        return destinationAeroDrome;
    }

    public void setDestinationAeroDrome(String destinationAeroDrome) {
        this.destinationAeroDrome = destinationAeroDrome;
    }

    public String getFirEntryPoint() {
        return firEntryPoint;
    }

    public void setFirEntryPoint(String firEntryPoint) {
        this.firEntryPoint = firEntryPoint;
    }

    public String getFirEntryTime() {
        return firEntryTime;
    }

    public void setFirEntryTime(String firEntryTime) {
        this.firEntryTime = firEntryTime;
    }

    public String getFirExitPoint() {
        return firExitPoint;
    }

    public void setFirExitPoint(String firExitPoint) {
        this.firExitPoint = firExitPoint;
    }

    public String getFirExitTime() {
        return firExitTime;
    }

    public void setFirExitTime(String firExitTime) {
        this.firExitTime = firExitTime;
    }

    public String getFlightRule() {
        return flightRule;
    }

    public void setFlightRule(String flightRule) {
        this.flightRule = flightRule;
    }

    public String getFlightTravelCategory() {
        return flightTravelCategory;
    }

    public void setFlightTravelCategory(String flightTravelCategory) {
        this.flightTravelCategory = flightTravelCategory;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }
}
