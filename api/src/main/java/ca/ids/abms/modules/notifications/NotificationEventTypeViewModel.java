package ca.ids.abms.modules.notifications;

import java.time.LocalDateTime;

public class NotificationEventTypeViewModel {

    private Integer id;
    private String eventType;
    private Boolean userNotificationIndicator;
    private Boolean customerNotificationIndicator;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "NotificationEventTypeViewModel{" +
            "id=" + id +
            ", eventType='" + eventType + '\'' +
            ", userNotificationIndicator=" + userNotificationIndicator +
            ", customerNotificationIndicator=" + customerNotificationIndicator +
            ", createdBy='" + createdBy + '\'' +
            ", createdAt=" + createdAt +
            ", updatedBy='" + updatedBy + '\'' +
            ", updatedAt=" + updatedAt +
            '}';
    }
}
