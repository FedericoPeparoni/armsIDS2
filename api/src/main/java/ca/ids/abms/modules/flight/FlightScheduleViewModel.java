package ca.ids.abms.modules.flight;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.util.models.VersionedViewModel;
import org.hibernate.validator.constraints.NotBlank;

@SuppressWarnings({"WeakerAccess", "unused"})
public class FlightScheduleViewModel extends VersionedViewModel {

    @Id
    private Integer id;

    @NotNull
    private Account account;

    @NotNull
    private String flightServiceNumber;

    @NotNull
    private String depAd;

    @NotNull
    private String depTime;

    @NotNull
    private String destAd;

    @NotNull
    private String destTime;

    @NotBlank
    private String dailySchedule;

    @NotNull
    private Boolean selfCare;

    private String activeIndicator;

    @NotNull
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private List<FlightScheduleMovementViewModel> missingFlightMovements;

    private List<FlightScheduleMovementViewModel> unexpectedFlights;

    // these fields don't exist in the table because we use them
    // only `on the` front-end to show `the` status of the record,
    // if `an` approval request `exists` for this record
    private Integer scRequestId;

    private String scRequestType;

    public Account getAccount() {
        return account;
    }

    public String getActiveIndicator() {
        return activeIndicator;
    }

    public String getDailySchedule() {
        return dailySchedule;
    }

    public String getDepAd() {
        return depAd;
    }

    public String getDepTime() {
        return depTime;
    }

    public String getDestAd() {
        return destAd;
    }

    public String getDestTime() {
        return destTime;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public String getFlightServiceNumber() {
        return flightServiceNumber;
    }

    public Integer getId() {
        return id;
    }

    public List<FlightScheduleMovementViewModel> getMissingFlightMovements() {
        return missingFlightMovements;
    }

    public List<FlightScheduleMovementViewModel> getUnexpectedFlights() {
        return unexpectedFlights;
    }

    public Boolean getSelfCare() {
        return selfCare;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public Integer getScRequestId() {
        return scRequestId;
    }

    public String getScRequestType() {
        return scRequestType;
    }

    public void setAccount(Account aAccount) {
        account = aAccount;
    }

    public void setActiveIndicator(String aActiveIndicator) {
        activeIndicator = aActiveIndicator;
    }

    public void setDailySchedule(String aDailySchedule) {
        dailySchedule = aDailySchedule;
    }

    public void setDepAd(String aDepAd) {
        depAd = aDepAd;
    }

    public void setDepTime(String aDepTime) {
        depTime = aDepTime;
    }

    public void setDestAd(String aDestAd) {
        destAd = aDestAd;
    }

    public void setDestTime(String aDestTime) {
        destTime = aDestTime;
    }

    public void setEndDate(LocalDateTime aEndDate) {
        endDate = aEndDate;
    }

    public void setFlightServiceNumber(String aFlightServiceNumber) {
        flightServiceNumber = aFlightServiceNumber;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setMissingFlightMovements(List<FlightScheduleMovementViewModel> missingFlightMovements) {
        this.missingFlightMovements = missingFlightMovements;
    }

    public void setUnexpectedFlights(List<FlightScheduleMovementViewModel> unexpectedFlights) {
        this.unexpectedFlights = unexpectedFlights;
    }

    public void setSelfCare(Boolean aSelfCare) {
        selfCare = aSelfCare;
    }

    public void setStartDate(LocalDateTime aStartDate) {
        startDate = aStartDate;
    }

    public void setScRequestId(Integer scRequestId) {
        this.scRequestId = scRequestId;
    }

    public void setScRequestType(String scRequestType) {
        this.scRequestType = scRequestType;
    }
}
