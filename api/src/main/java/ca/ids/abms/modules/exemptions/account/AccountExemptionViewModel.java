package ca.ids.abms.modules.exemptions.account;

import ca.ids.abms.modules.util.models.VersionedViewModel;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AccountExemptionViewModel extends VersionedViewModel {

    private Integer id;

    @NotNull
    private Integer accountId;

    private String accountName;

    @NotNull
    @Max(100)
    @Min(0)
    private Double enroute;

    @NotNull
    @Max(100)
    @Min(0)
    private Double parking;

    @NotNull
    @Max(100)
    @Min(0)
    private Double approachFeesExempt;

    @NotNull
    @Max(100)
    @Min(0)
    private Double aerodromeFeesExempt;

    @NotNull
    @Max(100)
    @Min(0)
    private Double lateArrival;

    @NotNull
    @Max(100)
    @Min(0)
    private Double lateDeparture;

    @NotNull
    @Max(100)
    @Min(0)
    private Double domesticPax;

    @NotNull
    @Max(100)
    @Min(0)
    private Double internationalPax;

    @NotNull
    @Max(100)
    @Min(0)
    private Double extendedHours;

    @Size(max = 255)
    private String flightNotes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Double getEnroute() {
        return enroute;
    }

    public void setEnroute(Double enroute) {
        this.enroute = enroute;
    }

    public Double getParking() {
        return parking;
    }

    public void setParking(Double parking) {
        this.parking = parking;
    }

    public Double getApproachFeesExempt() {
        return approachFeesExempt;
    }

    public void setApproachFeesExempt(Double approachFeesExempt) {
        this.approachFeesExempt = approachFeesExempt;
    }

    public Double getAerodromeFeesExempt() {
        return aerodromeFeesExempt;
    }

    public void setAerodromeFeesExempt(Double aerodromeFeesExempt) {
        this.aerodromeFeesExempt = aerodromeFeesExempt;
    }

    public Double getLateArrival() {
        return lateArrival;
    }

    public void setLateArrival(Double lateArrival) {
        this.lateArrival = lateArrival;
    }

    public Double getLateDeparture() {
        return lateDeparture;
    }

    public void setLateDeparture(Double lateDeparture) {
        this.lateDeparture = lateDeparture;
    }

    public Double getDomesticPax() {
        return domesticPax;
    }

    public void setDomesticPax(Double domesticPax) {
        this.domesticPax = domesticPax;
    }

    public Double getInternationalPax() {
        return internationalPax;
    }

    public void setInternationalPax(Double internationalPax) {
        this.internationalPax = internationalPax;
    }

    public Double getExtendedHours() {
        return extendedHours;
    }

    public void setExtendedHours(Double extendedHours) {
        this.extendedHours = extendedHours;
    }

    public String getFlightNotes() {
        return flightNotes;
    }

    public void setFlightNotes(String flightNotes) {
        this.flightNotes = flightNotes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountExemptionViewModel that = (AccountExemptionViewModel) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AccountExemptionViewModel{" +
                "id=" + id +
                ", accountId=" + accountId +
                '}';
    }
}
