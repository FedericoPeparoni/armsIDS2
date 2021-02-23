package ca.ids.abms.modules.flight;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

@Entity
@SuppressWarnings("WeakerAccess")
public class FlightSchedule extends VersionedAuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Account account;

    @SearchableText
    @NotNull
    private String flightServiceNumber;

    @SearchableText
    @NotNull
    private String depAd;

    @SearchableText
    @NotNull
    private String depTime;

    @SearchableText
    @NotNull
    private String destAd;

    @SearchableText
    @NotNull
    private String destTime;

    @NotNull
    private String dailySchedule;

    @NotNull
    private Boolean selfCare;

    @SearchableText
    private String activeIndicator;
    
    @NotNull
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof FlightSchedule)) {
            return false;
        }
        final FlightSchedule that = (FlightSchedule) o;

        return id != null ? id.equals(that.getId()) : that.getId() == null;
    }

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

    public Boolean getSelfCare() {
        return selfCare;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
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

    public void setSelfCare(Boolean aSelfCare) {
        selfCare = aSelfCare;
    }

    public void setStartDate(LocalDateTime aStartDate) {
        startDate = aStartDate;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "FlightSchedule [id=" + id + ", account=" + account + ", flightServiceNumber=" + flightServiceNumber
                + ", depAd=" + depAd + ", depTime=" + depTime + ", destAd=" + destAd + ", destTime=" + destTime
                + ", dailySchedule=" + dailySchedule + ", selfCare=" + selfCare + ", activeIndicator=" + activeIndicator
                + "]";
    }
}
