package ca.ids.abms.modules.exemptions.account;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.exemptions.ExemptionType;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@UniqueKey(columnNames = "account")
public class AccountExemption extends VersionedAuditedEntity implements ExemptionType {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @SearchableEntity(searchableField = "name")
    private Account account;

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
    
    @NotNull
    @Max(100)
    @Min(0)
    private Double unifiedTax;

 

	@Size(max = 255)
    @SearchableText
    @NotNull
    private String flightNotes;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
    
    public Double getUnifiedTax() {
 		return unifiedTax;
 	}

 	public void setUnifiedTax(Double unifiedTax) {
 		this.unifiedTax = unifiedTax;
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

        AccountExemption that = (AccountExemption) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AccountExemption{" +
                "id=" + id +
                ", account=" + account +
                '}';
    }

    @Override
    public Double enrouteChargeExemption() {
        return enroute;
    }

    @Override
    public Double lateArrivalChargeExemption() {
        return lateArrival;
    }

    @Override
    public Double lateDepartureChargeExemption() {
        return lateDeparture;
    }

    @Override
    public Double parkingChargeExemption() {
        return parking;
    }

    @Override
    public Double approachChargeExemption() {
        return approachFeesExempt;
    }

    @Override
    public Double aerodromeChargeExemption() {
        return aerodromeFeesExempt;
    }

    @Override
    public Double domesticPaxChargeExemption() {
        return domesticPax;
    }

    @Override
    public Double internationalPaxChargeExemption() {
        return internationalPax;
    }

    @Override
    public Double extendedHoursSurchargeExemption() {
        return extendedHours;
    }

    @Override
    public String flightNoteChargeExemption() {
        return flightNotes;
    }
}
