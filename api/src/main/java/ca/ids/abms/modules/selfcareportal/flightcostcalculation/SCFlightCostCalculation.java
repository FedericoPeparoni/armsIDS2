package ca.ids.abms.modules.selfcareportal.flightcostcalculation;

import javax.validation.constraints.NotNull;

public class SCFlightCostCalculation {

    @NotNull
    private String aircraftType;

    private String registrationNumber;

    private String speed;

    private String estimatedElapsedTime;

    @NotNull
    private String depAerodrome;

    @NotNull
    private String destAerodrome;

    @NotNull
    private String route;

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getEstimatedElapsedTime() {
        return estimatedElapsedTime;
    }

    public void setEstimatedElapsedTime(String estimatedElapsedTime) {
        this.estimatedElapsedTime = estimatedElapsedTime;
    }

    public String getDepAerodrome() {
        return depAerodrome;
    }

    public void setDepAerodrome(String depAerodrome) {
        this.depAerodrome = depAerodrome;
    }

    public String getDestAerodrome() {
        return destAerodrome;
    }

    public void setDestAerodrome(String destAerodrome) {
        this.destAerodrome = destAerodrome;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }
}
