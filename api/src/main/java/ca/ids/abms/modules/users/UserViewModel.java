package ca.ids.abms.modules.users;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import javax.validation.constraints.Size;

import ca.ids.abms.modules.util.models.VersionedViewModel;
import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonProperty;

import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.roles.RoleViewModel;

public class UserViewModel extends VersionedViewModel {

    private Integer id;

    @Size(min = 4, max = 50)
    private String login;

    @Email
    @Size(max = 255)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Size(max = 100)
    private String name;

    @Size(max = 255)
    private String contactInformation;

    @Size(max = 100)
    private String smsNumber;

    private Collection<RoleViewModel> roles;

    private Collection<String> permissions;

    private BillingCenter billingCenter;

    private String language;

    @Size(max = 100)
    private String jobTitle;

    private Boolean isSelfcareUser;

    private Boolean forcePasswordChange;

    private Boolean registrationStatus;

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public BillingCenter getBillingCenter() {
        return billingCenter;
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

    public Collection<String> getPermissions() {
        return permissions;
    }

    public Collection<RoleViewModel> getRoles() {
        return roles;
    }

    public String getSmsNumber() {
        return smsNumber;
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

    public Boolean getRegistrationStatus() {
        return registrationStatus;
    }

    public void setBillingCenter(BillingCenter aBillingCenter) {
        billingCenter = aBillingCenter;
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
        this.login = login;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPermissions(Collection<String> permissions) {
        this.permissions = new TreeSet<>(permissions);
    }

    public void setRoles(Collection<RoleViewModel> roles) {
        this.roles = new ArrayList<>(roles);
    }

    public void setSmsNumber(String aSmsNumber) {
        smsNumber = aSmsNumber;
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

    public void setRegistrationStatus(Boolean registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    @Override
    public String toString() {
        return "UserViewModel{" + "id=" + id + ", login='" + login + '\'' + ", name='" + name + '\'' + ", email='"
                + email + '\'' + ", job title='" + jobTitle + '}';
    }
}
