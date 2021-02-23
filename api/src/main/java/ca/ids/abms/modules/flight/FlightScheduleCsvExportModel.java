package ca.ids.abms.modules.flight;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDateTime;

public class FlightScheduleCsvExportModel {

    private String account;

    @CsvProperty(value = "Active")
    private String activeIndicator;

    private String flightServiceNumber;

    private String depAd;

    private String depTime;

    private String destAd;

    private String destTime;

    private String dailySchedule;

    private Boolean selfCare;

    @CsvProperty(date = true)
    private LocalDateTime startDate;

    @CsvProperty(date = true)
    private LocalDateTime endDate;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getActiveIndicator() {
        return activeIndicator;
    }

    public void setActiveIndicator(String activeIndicator) {
        this.activeIndicator = activeIndicator;
    }

    public String getFlightServiceNumber() {
        return flightServiceNumber;
    }

    public void setFlightServiceNumber(String flightServiceNumber) {
        this.flightServiceNumber = flightServiceNumber;
    }

    public String getDepAd() {
        return depAd;
    }

    public void setDepAd(String depAd) {
        this.depAd = depAd;
    }

    public String getDepTime() {
        return depTime;
    }

    public void setDepTime(String depTime) {
        this.depTime = depTime;
    }

    public String getDestAd() {
        return destAd;
    }

    public void setDestAd(String destAd) {
        this.destAd = destAd;
    }

    public String getDestTime() {
        return destTime;
    }

    public void setDestTime(String destTime) {
        this.destTime = destTime;
    }

    public String getDailySchedule() {
        return dailySchedule;
    }

    public void setDailySchedule(String dailySchedule) {
        this.dailySchedule = dailySchedule;
    }

    public Boolean getSelfCare() {
        return selfCare;
    }

    public void setSelfCare(Boolean selfCare) {
        this.selfCare = selfCare;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
