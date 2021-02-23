package ca.ids.abms.modules.towermovements;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDateTime;

public class TowerMovementLogCsvExportModel {

    @CsvProperty(date = true)
    private LocalDateTime dayOfFlight;

    @CsvProperty(value = "Dep Time")
    private String departureTime;

    @CsvProperty(date = true)
    private LocalDateTime dateOfContact;

    private String flightId;

    @CsvProperty(value = "Reg Number")
    private String registration;

    @CsvProperty(value = "A/C Type")
    private String aircraftType;

    @CsvProperty(value = "Operator Identifier")
    private String operatorName;

    @CsvProperty(value = "Dep Ad")
    private String departureAerodrome;

    @CsvProperty(value = "Dep Contact Time")
    private String departureContactTime;

    @CsvProperty(value = "Dest Ad")
    private String destinationAerodrome;

    @CsvProperty(value = "Dest Contact Time")
    private String destinationContactTime;

    private String route;

    private String flightLevel;

    private Integer flightCrew;

    private Integer passengers;

    private String flightCategory;

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

    public LocalDateTime getDateOfContact() {
        return dateOfContact;
    }

    public void setDateOfContact(LocalDateTime dateOfContact) {
        this.dateOfContact = dateOfContact;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
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

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getDepartureAerodrome() {
        return departureAerodrome;
    }

    public void setDepartureAerodrome(String departureAerodrome) {
        this.departureAerodrome = departureAerodrome;
    }

    public String getDepartureContactTime() {
        return departureContactTime;
    }

    public void setDepartureContactTime(String departureContactTime) {
        this.departureContactTime = departureContactTime;
    }

    public String getDestinationAerodrome() {
        return destinationAerodrome;
    }

    public void setDestinationAerodrome(String destinationAerodrome) {
        this.destinationAerodrome = destinationAerodrome;
    }

    public String getDestinationContactTime() {
        return destinationContactTime;
    }

    public void setDestinationContactTime(String destinationContactTime) {
        this.destinationContactTime = destinationContactTime;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getFlightLevel() {
        return flightLevel;
    }

    public void setFlightLevel(String flightLevel) {
        this.flightLevel = flightLevel;
    }

    public Integer getFlightCrew() {
        return flightCrew;
    }

    public void setFlightCrew(Integer flightCrew) {
        this.flightCrew = flightCrew;
    }

    public Integer getPassengers() {
        return passengers;
    }

    public void setPassengers(Integer passengers) {
        this.passengers = passengers;
    }

    public String getFlightCategory() {
        return flightCategory;
    }

    public void setFlightCategory(String flightCategory) {
        this.flightCategory = flightCategory;
    }
}
