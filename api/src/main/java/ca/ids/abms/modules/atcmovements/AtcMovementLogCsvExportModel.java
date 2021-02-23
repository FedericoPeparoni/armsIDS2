package ca.ids.abms.modules.atcmovements;

import ca.ids.abms.util.csv.annotations.CsvProperty;
import ca.ids.abms.util.csv.annotations.CsvPropertyOrder;

import java.time.LocalDateTime;

@CsvPropertyOrder(value = {"dayOfFlight", "departureTime", "dateOfContact", "flightId", "registration", "operatorIdentifier",
    "aircraftType", "flightType", "departureAerodrome", "destinationAerodrome", "firEntryPoint", "firEntryTime", "firMidPoint",
    "firMidTime", "firExitPoint", "firExitTime", "flightLevel", "wakeTurbulence", "route", "flightCategory"})
public class AtcMovementLogCsvExportModel {

    @CsvProperty(date = true)
    private LocalDateTime dateOfContact;

    @CsvProperty(value = "Reg Number")
    private String registration;

    private String operatorIdentifier;

    private String route;

    private String flightId;

    @CsvProperty(value = "A/C Type")
    private String aircraftType;

    @CsvProperty(value = "Dep Ad")
    private String departureAerodrome;

    @CsvProperty(value = "Dest Ad")
    private String destinationAerodrome;

    @CsvProperty(value = "Entry Point")
    private String firEntryPoint;

    @CsvProperty(value = "Entry Time")
    private String firEntryTime;

    @CsvProperty(value = "Mid Point")
    private String firMidPoint;

    @CsvProperty(value = "Mid Time")
    private String firMidTime;

    @CsvProperty(value = "Exit Point")
    private String firExitPoint;

    @CsvProperty(value = "Exit Time")
    private String firExitTime;

    private String flightLevel;

    @CsvProperty(value = "WTC")
    private String wakeTurbulence;

    private String flightCategory;

    private String flightType;

    @CsvProperty(date = true)
    private LocalDateTime dayOfFlight;

    @CsvProperty(value = "Dep Time")
    private String departureTime;

    public LocalDateTime getDateOfContact() {
        return dateOfContact;
    }

    public void setDateOfContact(LocalDateTime dateOfContact) {
        this.dateOfContact = dateOfContact;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getOperatorIdentifier() {
        return operatorIdentifier;
    }

    public void setOperatorIdentifier(String operatorIdentifier) {
        this.operatorIdentifier = operatorIdentifier;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }

    public String getDepartureAerodrome() {
        return departureAerodrome;
    }

    public void setDepartureAerodrome(String departureAerodrome) {
        this.departureAerodrome = departureAerodrome;
    }

    public String getDestinationAerodrome() {
        return destinationAerodrome;
    }

    public void setDestinationAerodrome(String destinationAerodrome) {
        this.destinationAerodrome = destinationAerodrome;
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

    public String getFirMidPoint() {
        return firMidPoint;
    }

    public void setFirMidPoint(String firMidPoint) {
        this.firMidPoint = firMidPoint;
    }

    public String getFirMidTime() {
        return firMidTime;
    }

    public void setFirMidTime(String firMidTime) {
        this.firMidTime = firMidTime;
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

    public String getFlightLevel() {
        return flightLevel;
    }

    public void setFlightLevel(String flightLevel) {
        this.flightLevel = flightLevel;
    }

    public String getWakeTurbulence() {
        return wakeTurbulence;
    }

    public void setWakeTurbulence(String wakeTurbulence) {
        this.wakeTurbulence = wakeTurbulence;
    }

    public String getFlightCategory() {
        return flightCategory;
    }

    public void setFlightCategory(String flightCategory) {
        this.flightCategory = flightCategory;
    }

    public String getFlightType() {
        return flightType;
    }

    public void setFlightType(String flightType) {
        this.flightType = flightType;
    }

    public LocalDateTime getDayOfFlight() {
        return dayOfFlight;
    }

    public void setDayOfFlight(LocalDateTime dayOfFlight) {
        this.dayOfFlight = dayOfFlight;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }
}
