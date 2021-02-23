package ca.ids.abms.modules.flight;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

@Entity
public class FlightReassignment extends VersionedAuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @SearchableEntity
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
    @SearchableText
    private String identificationType;

    @NotNull
    @SearchableText
    private String identifierText;

    @NotNull
    @Column(name = "reassignment_start_date")
    private LocalDateTime startDate;

    @NotNull
    @Column(name = "reassignment_end_date")
    private LocalDateTime endDate;
    
    @OneToMany(mappedBy = "flightReassignment")
    private Set<FlightReassignmentAerodrome> aerodromeIdentifiers = new HashSet<>();

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FlightReassignment other = (FlightReassignment) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public Account getAccount() {
        return account;
    }

    public Set<FlightReassignmentAerodrome> getAerodromeIdentifiers() {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    public void setAccount(Account aAccount) {
        account = aAccount;
    }

    public void setAerodromeIdentifiers(Set<FlightReassignmentAerodrome> aAerodromeIdentifiers) {
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

    @Override
    public String toString() {
        return "FlightReassignment [id=" + id + ", account=" + account + ", appliesToTypeArrival="
                + appliesToTypeArrival + ", appliesToTypeDeparture=" + appliesToTypeDeparture
                + ", appliesToTypeDomestic=" + appliesToTypeDomestic + ", appliesToTypeOverflight="
                + appliesToTypeOverflight + ", appliesToScopeDomestic=" + appliesToScopeDomestic
                + ", appliesToScopeRegional=" + appliesToScopeRegional + ", appliesToScopeInternational="
                + appliesToScopeInternational + ", appliesToNationalityNational=" + appliesToNationalityNational
                + ", appliesToNationalityForeign=" + appliesToNationalityForeign + ", identificationType="
                + identificationType + ", identifierText=" + identifierText + ", startDate=" + startDate + ", endDate=" + endDate + "]";
    }
}
