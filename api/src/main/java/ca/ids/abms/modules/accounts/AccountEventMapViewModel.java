package ca.ids.abms.modules.accounts;

import ca.ids.abms.modules.util.models.VersionedViewModel;

import javax.validation.constraints.NotNull;

public class AccountEventMapViewModel extends VersionedViewModel {

    private Integer id;

    @NotNull
    private Integer account;

    @NotNull
    private Integer notificationEventType;

    @NotNull
    private Boolean notificationEmail;

    @NotNull
    private Boolean notificationSms;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAccount() {
        return account;
    }

    public void setAccount(Integer account) {
        this.account = account;
    }

    public Integer getNotificationEventType() {
        return notificationEventType;
    }

    public void setNotificationEventType(Integer notificationEventType) {
        this.notificationEventType = notificationEventType;
    }

    public Boolean getNotificationEmail() {
        return notificationEmail;
    }

    public void setNotificationEmail(Boolean notificationEmail) {
        this.notificationEmail = notificationEmail;
    }

    public Boolean getNotificationSms() {
        return notificationSms;
    }

    public void setNotificationSms(Boolean notificationSms) {
        this.notificationSms = notificationSms;
    }

    @Override
    public String toString() {
        return "AccountEventMapViewModel{" +
            "id=" + id +
            ", account=" + account +
            ", notificationEventType=" + notificationEventType +
            ", notificationEmail=" + notificationEmail +
            ", notificationSms=" + notificationSms +
            '}';
    }
}
