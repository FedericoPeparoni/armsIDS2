package ca.ids.abms.modules.accounts;

import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.notifications.NotificationEventType;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "account_event_map")
@UniqueKey(columnNames={"account", "notificationEventType"})
public class AccountEventMap extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "notification_event_type_id")
    private NotificationEventType notificationEventType;

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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public NotificationEventType getNotificationEventType() {
        return notificationEventType;
    }

    public void setNotificationEventType(NotificationEventType notificationEventType) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountEventMap)) return false;

        AccountEventMap that = (AccountEventMap) o;

        if (!getId().equals(that.getId())) return false;
        if (!getAccount().equals(that.getAccount())) return false;
        if (!getNotificationEventType().equals(that.getNotificationEventType())) return false;
        if (!getNotificationEmail().equals(that.getNotificationEmail())) return false;
        return getNotificationSms().equals(that.getNotificationSms());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AccountEventMap{" +
            "id=" + id +
            ", account=" + account +
            ", notificationEventType=" + notificationEventType +
            ", notificationEmail=" + notificationEmail +
            ", notificationSms=" + notificationSms +
            '}';
    }
}
