package ca.ids.abms.modules.aircraft;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.modules.flightmovementsbuilder.vo.SmallAircraftVO;
import ca.ids.abms.modules.unifiedtaxes.UnifiedTaxCharges;

import org.springframework.data.annotation.Transient;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;
import ca.ids.abms.modules.util.models.annotations.MergeOnNull;

@Entity
public class AircraftRegistration extends VersionedAuditedEntity implements SmallAircraftVO {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 10)
    @NotNull
    @SearchableText
    private String registrationNumber;

    @NotNull
    private LocalDateTime registrationStartDate;

    @NotNull
    private LocalDateTime registrationExpiryDate;
    
    //add 
    
    
    private String aircraftScope;
    
    

    @ManyToOne
    @JoinColumn(name = "aircraft_registration_id")
    private UnifiedTaxCharges unifiedTaxCharges;
    
    

    public String getAircraftScope() {
		return aircraftScope;
	}

	public void setAircraftScope(String aircraftScope) {
		this.aircraftScope = aircraftScope;
	}

	@ManyToOne
    @SearchableEntity
    private AircraftType aircraftType;

    @ManyToOne
    @SearchableEntity
    private Account account;

    @NotNull
    private Double mtowOverride;

    @ManyToOne
    @JoinColumn(name = "country_of_registration")
    @NotNull
    @SearchableEntity
    private Country countryOfRegistration;

    private Boolean countryOverride;

    private Boolean createdBySelfCare;

    @NotNull
    private Boolean isLocal;

    @MergeOnNull(true)
    private LocalDateTime aircraftServiceDate;

    @MergeOnNull(true)
    private LocalDateTime coaExpiryDate;

    @MergeOnNull(true)
    private LocalDateTime coaIssueDate;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (! (o instanceof AircraftRegistration))
            return false;

        AircraftRegistration that = (AircraftRegistration) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    public Account getAccount() {
        return account;
    }

    public AircraftType getAircraftType() {
        return aircraftType;
    }

    public Country getCountryOfRegistration() {
        return countryOfRegistration;
    }

    public Integer getId() {
        return id;
    }

    public Boolean getCountryOverride() {
        return countryOverride;
    }

    public Double getMtowOverride() {
        return mtowOverride;
    }

    public LocalDateTime getRegistrationExpiryDate() {
        return registrationExpiryDate;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public LocalDateTime getRegistrationStartDate() {
        return registrationStartDate;
    }

    @Override
    public int hashCode() {
        return registrationNumber != null ? registrationNumber.hashCode() : 0;
    }

    public void setAccount(Account aAccount) {
        account = aAccount;
    }

    public void setAircraftType(AircraftType aAircraftType) {
        aircraftType = aAircraftType;
    }

    public void setCountryOfRegistration(Country aCountryOfRegistration) {
        countryOfRegistration = aCountryOfRegistration;
    }

    public void setCountryOverride(Boolean aCountryOverride) {
        countryOverride = aCountryOverride;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setMtowOverride(Double aMtowOverride) {
        mtowOverride = aMtowOverride;
    }

    public void setRegistrationExpiryDate(LocalDateTime aRegistrationExpiryDate) {
        registrationExpiryDate = aRegistrationExpiryDate;
    }

    public void setRegistrationNumber(String aRegistrationNumber) {
        registrationNumber = aRegistrationNumber.toUpperCase();
    }

    public void setRegistrationStartDate(LocalDateTime aRegistrationStartDate) {
        registrationStartDate = aRegistrationStartDate;
    }

    public Boolean getCreatedBySelfCare() {
        return createdBySelfCare;
    }

    public void setCreatedBySelfCare(Boolean createdBySelfCare) {
        this.createdBySelfCare = createdBySelfCare;
    }

    public Boolean getIsLocal() {
        return isLocal;
    }

    public void setIsLocal(Boolean isLocal) {
        this.isLocal = isLocal;
    }

    public LocalDateTime getAircraftServiceDate() {
        return aircraftServiceDate;
    }

    public void setAircraftServiceDate(LocalDateTime aircraftServiceDate) {
        this.aircraftServiceDate = aircraftServiceDate;
    }

    public LocalDateTime getCoaExpiryDate() {
        return coaExpiryDate;
    }

    public void setCoaExpiryDate(LocalDateTime coaExpiryDate) {
        this.coaExpiryDate = coaExpiryDate;
    }

    public LocalDateTime getCoaIssueDate() {
        return coaIssueDate;
    }

    public void setCoaIssueDate(LocalDateTime coaIssueDate) {
        this.coaIssueDate = coaIssueDate;
    }

    @Override
    @Transient
    public boolean isSmallAircraftCoaValid(LocalDateTime date) {
        return date != null && coaExpiryDate != null && coaIssueDate != null &&
            (date.isBefore(coaExpiryDate) || date.isEqual(coaExpiryDate)) &&
            (date.isAfter(coaIssueDate) || date.isEqual(coaIssueDate));
    }
}
