package ca.ids.abms.modules.notifications;

import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.accounts.AccountEventMap;
import ca.ids.abms.modules.util.models.AuditedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "notification_event_types")
@UniqueKey(columnNames = "eventType")
public class NotificationEventType extends AuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 60)
    private String eventType;

    @NotNull
    private Boolean userNotificationIndicator;

    @NotNull
    private Boolean customerNotificationIndicator;

    @JsonIgnore
    @OneToMany(mappedBy = "notificationEventType")
    private Set<AccountEventMap> accountEventMaps = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Boolean getUserNotificationIndicator() {
        return userNotificationIndicator;
    }

    public void setUserNotificationIndicator(Boolean userNotificationIndicator) {
        this.userNotificationIndicator = userNotificationIndicator;
    }

    public Boolean getCustomerNotificationIndicator() {
        return customerNotificationIndicator;
    }

    public void setCustomerNotificationIndicator(Boolean customerNotificationIndicator) {
        this.customerNotificationIndicator = customerNotificationIndicator;
    }

    public Set<AccountEventMap> getAccountEventMaps() {
        return accountEventMaps;
    }

    public void setAccountEventMaps(Set<AccountEventMap> accountEventMaps) {
        this.accountEventMaps = accountEventMaps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationEventType)) return false;

        NotificationEventType that = (NotificationEventType) o;

        if (!getId().equals(that.getId())) return false;
        if (!getEventType().equals(that.getEventType())) return false;
        if (!getUserNotificationIndicator().equals(that.getUserNotificationIndicator())) return false;
        if (!getCustomerNotificationIndicator().equals(that.getCustomerNotificationIndicator())) return false;
        return getAccountEventMaps().equals(that.getAccountEventMaps());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "NotificationEventType{" +
            "id=" + id +
            ", eventType='" + eventType + '\'' +
            ", userNotificationIndicator=" + userNotificationIndicator +
            ", customerNotificationIndicator=" + customerNotificationIndicator +
            '}';
    }
}
