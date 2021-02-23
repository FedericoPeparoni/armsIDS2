package ca.ids.abms.modules.aircraft;

import ca.ids.abms.modules.accounts.AccountViewModel;
import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.util.models.VersionedViewModel;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class AircraftRegistrationViewModel extends VersionedViewModel {

    private Integer id;

    @Size(max = 10)
    @NotNull
    private String registrationNumber;

    @NotNull
    private LocalDateTime registrationStartDate;

    @NotNull
    private LocalDateTime registrationExpiryDate;

    private AircraftType aircraftType;

    private AccountViewModel account;

    @NotNull
    private Double mtowOverride;

    @NotNull
    private Country countryOfRegistration;

    private Boolean countryOverride;

    private Boolean createdBySelfCare;

    // these fields don't exist in the table because we use them
    // only `on the` front-end to show `the` status of the record,
    // if `an` approval request `exists` for this record
    private Integer scRequestId;

    private String scRequestType;

    @NotNull
    private Boolean isLocal;
    
    private LocalDateTime aircraftServiceDate;
    
    private LocalDateTime coaExpiryDate;
    
    private LocalDateTime coaIssueDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public LocalDateTime getRegistrationStartDate() {
        return registrationStartDate;
    }

    public void setRegistrationStartDate(LocalDateTime registrationStartDate) {
        this.registrationStartDate = registrationStartDate;
    }

    public LocalDateTime getRegistrationExpiryDate() {
        return registrationExpiryDate;
    }

    public void setRegistrationExpiryDate(LocalDateTime registrationExpiryDate) {
        this.registrationExpiryDate = registrationExpiryDate;
    }

    public AircraftType getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(AircraftType aircraftType) {
        this.aircraftType = aircraftType;
    }

    public AccountViewModel getAccount() {
        return account;
    }

    public void setAccount(AccountViewModel account) {
        this.account = account;
    }

    public Double getMtowOverride() {
        return mtowOverride;
    }

    public void setMtowOverride(Double mtowOverride) {
        this.mtowOverride = mtowOverride;
    }

    public Country getCountryOfRegistration() {
        return countryOfRegistration;
    }

    public void setCountryOfRegistration(Country countryOfRegistration) {
        this.countryOfRegistration = countryOfRegistration;
    }

    public Boolean getCountryOverride() {
        return countryOverride;
    }

    public void setCountryOverride(Boolean countryOverride) {
        this.countryOverride = countryOverride;
    }

    public Boolean getCreatedBySelfCare() {
        return createdBySelfCare;
    }

    public void setCreatedBySelfCare(Boolean createdBySelfCare) {
        this.createdBySelfCare = createdBySelfCare;
    }

    public Integer getScRequestId() {
        return scRequestId;
    }

    public void setScRequestId(Integer scRequestId) {
        this.scRequestId = scRequestId;
    }

    public String getScRequestType() {
        return scRequestType;
    }

    public void setScRequestType(String scRequestType) {
        this.scRequestType = scRequestType;
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
    
}
