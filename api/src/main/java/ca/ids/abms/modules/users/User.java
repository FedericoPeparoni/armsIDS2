package ca.ids.abms.modules.users;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.selfcareportal.approvalrequests.SelfCarePortalApprovalRequest;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;
import ca.ids.abms.modules.util.models.annotations.MergeOnNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.permissions.Permission;
import ca.ids.abms.modules.roles.Role;

@Entity
@UniqueKey(columnNames = {"login", "email", "smsNumber", "name"}, checkSeparately = true)
public class User extends VersionedAuditedEntity {

    @SuppressWarnings("squid:S2068")
    static final String PASSWORD_FIELD_NAME = "password";
    static final String IS_SELFCARE_USER_FIELD_NAME = "isSelfcareUser";

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(min = 4, max = 50)
    @SearchableText
    private String login;

    @NotNull
    @Size(max = 255)
    @SearchableText
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotNull
    @Size(max = 100)
    @SearchableText
    private String name;

    @NotNull
    @Size(max = 255)
    @SearchableText
    private String contactInformation;

    @Size(max = 100)
    @SearchableText
    @MergeOnNull
    private String smsNumber;

    @JsonIgnore
    @SearchableEntity
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<UserRole> userRoles = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<BillingLedger> billingLedgers = new HashSet<>();

    @ManyToOne
    private BillingCenter billingCenter;

    @Size(max = 100)
    @SearchableText
    private String jobTitle;

    @Size(max = 2)
    private String language;

    @NotNull
    private Boolean isSelfcareUser;

    @NotNull
    private Boolean forcePasswordChange;

    private String passwordHistory;

    private String emailActivationKey;

    private LocalDateTime activationKeyExpiration;

    private String temporaryPassword;

    private LocalDateTime temporaryPasswordExpiration;

    @NotNull
    private Boolean registrationStatus;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<SelfCarePortalApprovalRequest> selfCarePortalApprovalRequest = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        User user = (User) o;

        return id != null ? id.equals(user.id) : user.id == null;
    }

    public BillingCenter getBillingCenter() {
        return billingCenter;
    }

    public Set<BillingLedger> getBillingLedgers() {
        return billingLedgers;
    }

    @Transient
    public Collection<Integer> getBillings() {
        return getBillingLedgers().stream().map(BillingLedger::getId).collect(Collectors.toList());
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public String getEmail() {
        return email;
    }

    public Integer getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    @Transient
    public Collection<String> getPermissions() {
        return getRoles().stream().flatMap(role -> role.getPermissions().stream()).map(Permission::getName).distinct()
                .collect(Collectors.toSet());
    }

    @Transient
    public Collection<Role> getRoles() {
        return getUserRoles().stream().map(UserRole::getRole).collect(Collectors.toList());
    }

    public String getSmsNumber() {
        return smsNumber;
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public String getLanguage() {
        return language;
    }

    public Boolean getIsSelfcareUser() {
        return isSelfcareUser;
    }

    public Boolean getForcePasswordChange() {
        return forcePasswordChange;
    }

    public String getPasswordHistory() {
        return passwordHistory;
    }

    public String getEmailActivationKey() {
        return emailActivationKey;
    }

    public LocalDateTime getActivationKeyExpiration() {
        return activationKeyExpiration;
    }

    public Boolean getRegistrationStatus() {
        return registrationStatus;
    }

    public String getTemporaryPassword() {
        return temporaryPassword;
    }

    public LocalDateTime getTemporaryPasswordExpiration() {
        return temporaryPasswordExpiration;
    }

    public Set<SelfCarePortalApprovalRequest> getSelfCarePortalApprovalRequest() {
        return selfCarePortalApprovalRequest;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setBillingCenter(BillingCenter aBillingCenter) {
        billingCenter = aBillingCenter;
    }

    public void setBillingLedgers(Set<BillingLedger> aBillingLedgers) {
        billingLedgers = aBillingLedgers;
    }

    public void setContactInformation(String aContactInformation) {
        contactInformation = aContactInformation;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login.toLowerCase(); // normalize logins
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSmsNumber(String aSmsNumber) {
        smsNumber = aSmsNumber;
    }

    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setIsSelfcareUser(Boolean isSelfcareUser) {
        this.isSelfcareUser = isSelfcareUser;
    }

    public void setForcePasswordChange(Boolean forcePasswordChange) {
        this.forcePasswordChange = forcePasswordChange;
    }

    public void setPasswordHistory(String passwordHistory) {
        this.passwordHistory = passwordHistory;
    }

    public void setEmailActivationKey(String emailActivationKey) {
        this.emailActivationKey = emailActivationKey;
    }

    public void setActivationKeyExpiration(LocalDateTime activationKeyExpiration) {
        this.activationKeyExpiration = activationKeyExpiration;
    }

    public void setRegistrationStatus(Boolean registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public void setTemporaryPassword(String temporaryPassword) {
        this.temporaryPassword = temporaryPassword;
    }

    public void setTemporaryPasswordExpiration(LocalDateTime temporaryPasswordExpiration) {
        this.temporaryPasswordExpiration = temporaryPasswordExpiration;
    }

    public void setSelfCarePortalApprovalRequest(Set<SelfCarePortalApprovalRequest> selfCarePortalApprovalRequest) {
        this.selfCarePortalApprovalRequest = selfCarePortalApprovalRequest;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", login=" + login + ", email=" + email + ", name=" + name + ", contactInformation="
                + contactInformation + ", smsNumber=" + smsNumber + ", jobTitle=" + jobTitle + "]";
    }
}
