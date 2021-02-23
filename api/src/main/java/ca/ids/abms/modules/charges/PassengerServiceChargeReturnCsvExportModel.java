package ca.ids.abms.modules.charges;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDateTime;

public class PassengerServiceChargeReturnCsvExportModel {

    private String flightId;

    private String account;

    @CsvProperty(date = true)
    private LocalDateTime dayOfFlight;

    @CsvProperty(value = "Dep Time")
    private String departureTime;

    @CsvProperty(value = "Join Pass")
    private Integer joiningPassengers;

    @CsvProperty(value = "Trans Pass")
    private Integer transitPassengers;

    @CsvProperty(value = "Charge Int Pass")
    private Integer chargeableItlPassengers;

    @CsvProperty(value = "Charge Dom Pass")
    private Integer chargeableDomesticPassengers;

    private Integer children;

    @CsvProperty(value = "Created By Self-Care")
    private Boolean createdBySelfCare;

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
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

    public Integer getJoiningPassengers() {
        return joiningPassengers;
    }

    public void setJoiningPassengers(Integer joiningPassengers) {
        this.joiningPassengers = joiningPassengers;
    }

    public Integer getTransitPassengers() {
        return transitPassengers;
    }

    public void setTransitPassengers(Integer transitPassengers) {
        this.transitPassengers = transitPassengers;
    }

    public Integer getChargeableItlPassengers() {
        return chargeableItlPassengers;
    }

    public void setChargeableItlPassengers(Integer chargeableItlPassengers) {
        this.chargeableItlPassengers = chargeableItlPassengers;
    }

    public Integer getChargeableDomesticPassengers() {
        return chargeableDomesticPassengers;
    }

    public void setChargeableDomesticPassengers(Integer chargeableDomesticPassengers) {
        this.chargeableDomesticPassengers = chargeableDomesticPassengers;
    }

    public Integer getChildren() {
        return children;
    }

    public void setChildren(Integer children) {
        this.children = children;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Boolean getCreatedBySelfCare() {
        return createdBySelfCare;
    }

    public void setCreatedBySelfCare(Boolean createdBySelfCare) {
        this.createdBySelfCare = createdBySelfCare;
    }
}
