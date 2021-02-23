package ca.ids.abms.modules.flightmovementsbuilder.vo;

import java.time.LocalDateTime;

public class ThruFlightPlanVO {

    private String crusingSpeed;

    private String flightLevel;

    private String eet;

    private String destAd;

    private String depAd;

    private LocalDateTime depTime;

    private LocalDateTime arrivalTime;

    public ThruFlightPlanVO(String depAd, LocalDateTime depTime) {
        this.depAd = depAd;
        this.depTime = depTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public String getCrusingSpeed() {
        return crusingSpeed;
    }

    public String getDepAd() {
        return depAd;
    }

    public LocalDateTime getDepTime() {
        return depTime;
    }

    public String getDestAd() {
        return destAd;
    }

    public String getEet() {
        return eet;
    }

    public String getFlightLevel() {
        return flightLevel;
    }

    public void setArrivalTime(LocalDateTime aArrivalTime) {
        arrivalTime = aArrivalTime;
    }

    public void setCrusingSpeed(String aCrusingSpeed) {
        crusingSpeed = aCrusingSpeed;
    }

    public void setDepAd(String aDepAd) {
        depAd = aDepAd;
    }

    public void setDepTime(LocalDateTime aDepTime) {
        depTime = aDepTime;
    }

    public void setDestAd(String aDestAd) {
        destAd = aDestAd;
    }

    public void setEet(String aEet) {
        eet = aEet;
    }

    public void setFlightLevel(String aFlightLevel) {
        flightLevel = aFlightLevel;
    }

    @Override
    public String toString() {
        return "ThruFlightPlanVO [depAd=" + depAd + ", depTime=" + depTime + ", destAd=" + destAd + ", arrivalTime="
                + arrivalTime + ", eet=" + eet + ", crusingSpeed=" + crusingSpeed + ", flightLevel=" + flightLevel
                + "]";
    }
}
