package ca.ids.abms.modules.flight;

import java.time.LocalDateTime;
import java.util.Collection;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.util.models.VersionedViewModel;

public class FlightReassignmentViewModel extends VersionedViewModel {

    @Id
    private Integer id;
    
    private Account account;
    
    @NotNull
    private Boolean appliesToTypeArrival;
    
    @NotNull
    private Boolean appliesToTypeDeparture;
    
    @NotNull
    private Boolean appliesToTypeDomestic;
    
    @NotNull
    private Boolean appliesToTypeOverflight;
    
    @NotNull
    private Boolean appliesToScopeDomestic;
    
    @NotNull
    private Boolean appliesToScopeRegional;
    
    @NotNull
    private Boolean appliesToScopeInternational;
    
    @NotNull
    private Boolean appliesToNationalityNational;
    
    @NotNull
    private Boolean appliesToNationalityForeign;
    
    @NotNull
    private String identificationType;
    
    @NotNull
    private String identifierText;
    
    private Collection<String> aerodromeIdentifiers;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    public Account getAccount() {
        return account;
    }

    public Collection<String> getAerodromeIdentifiers() {
        return aerodromeIdentifiers;
    }

    public Boolean getAppliesToNationalityForeign() {
        return appliesToNationalityForeign;
    }

    public Boolean getAppliesToNationalityNational() {
        return appliesToNationalityNational;
    }

    public Boolean getAppliesToScopeDomestic() {
        return appliesToScopeDomestic;
    }

    public Boolean getAppliesToScopeInternational() {
        return appliesToScopeInternational;
    }

    public Boolean getAppliesToScopeRegional() {
        return appliesToScopeRegional;
    }

    public Boolean getAppliesToTypeArrival() {
        return appliesToTypeArrival;
    }

    public Boolean getAppliesToTypeDeparture() {
        return appliesToTypeDeparture;
    }

    public Boolean getAppliesToTypeDomestic() {
        return appliesToTypeDomestic;
    }

    public Boolean getAppliesToTypeOverflight() {
        return appliesToTypeOverflight;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public Integer getId() {
        return id;
    }

    public String getIdentificationType() {
        return identificationType;
    }

    public String getIdentifierText() {
        return identifierText;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setAccount(Account aAccount) {
        account = aAccount;
    }

    public void setAerodromeIdentifiers(Collection<String> aAerodromeIdentifiers) {
        aerodromeIdentifiers = aAerodromeIdentifiers;
    }

    public void setAppliesToNationalityForeign(Boolean aAppliesToNationalityForeign) {
        appliesToNationalityForeign = aAppliesToNationalityForeign;
    }

    public void setAppliesToNationalityNational(Boolean aAppliesToNationalityNational) {
        appliesToNationalityNational = aAppliesToNationalityNational;
    }

    public void setAppliesToScopeDomestic(Boolean aAppliesToScopeDomestic) {
        appliesToScopeDomestic = aAppliesToScopeDomestic;
    }

    public void setAppliesToScopeInternational(Boolean aAppliesToScopeInternational) {
        appliesToScopeInternational = aAppliesToScopeInternational;
    }

    public void setAppliesToScopeRegional(Boolean aAppliesToScopeRegional) {
        appliesToScopeRegional = aAppliesToScopeRegional;
    }

    public void setAppliesToTypeArrival(Boolean aAppliesToTypeArrival) {
        appliesToTypeArrival = aAppliesToTypeArrival;
    }

    public void setAppliesToTypeDeparture(Boolean aAppliesToTypeDeparture) {
        appliesToTypeDeparture = aAppliesToTypeDeparture;
    }

    public void setAppliesToTypeDomestic(Boolean aAppliesToTypeDomestic) {
        appliesToTypeDomestic = aAppliesToTypeDomestic;
    }

    public void setAppliesToTypeOverflight(Boolean aAppliesToTypeOverflight) {
        appliesToTypeOverflight = aAppliesToTypeOverflight;
    }

    public void setEndDate(LocalDateTime aEndDate) {
        endDate = aEndDate;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setIdentificationType(String aIdentificationType) {
        identificationType = aIdentificationType;
    }
    
    public void setIdentifierText(String aIdentifierText) {
        identifierText = aIdentifierText;
    }
    
    public void setStartDate(LocalDateTime aStartDate) {
        startDate = aStartDate;
    }
}
