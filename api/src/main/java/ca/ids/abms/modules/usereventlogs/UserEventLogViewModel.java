package ca.ids.abms.modules.usereventlogs;

import java.time.LocalDateTime;

public class UserEventLogViewModel {

    private Integer id;

    private String userName;

    private LocalDateTime dateTime;

    private String ipAddress;

    private String eventType;

    private String recordPrimaryKey;

    private String uniqueRecordId;

    private String modifiedColumnNamesValues;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getRecordPrimaryKey() {
        return recordPrimaryKey;
    }

    public void setRecordPrimaryKey(String recordPrimaryKey) {
        this.recordPrimaryKey = recordPrimaryKey;
    }

    public String getUniqueRecordId() {
        return uniqueRecordId;
    }

    public void setUniqueRecordId(String uniqueRecordId) {
        this.uniqueRecordId = uniqueRecordId;
    }

    public String getModifiedColumnNamesValues() {
        return modifiedColumnNamesValues;
    }

    public void setModifiedColumnNamesValues(String modifiedColumnNamesValues) {
        this.modifiedColumnNamesValues = modifiedColumnNamesValues;
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
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof UserEventLogViewModel))
            return false;

        UserEventLogViewModel that = (UserEventLogViewModel) obj;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "UserEventLog{" +
            "id=" + id +
            ", userName='" + userName + '\'' +
            ", dateTime=" + dateTime +
            ", ipAddress='" + ipAddress + '\'' +
            ", eventType='" + eventType + '\'' +
            ", recordPrimartyKey='" + recordPrimaryKey + '\'' +
            ", uniqueRecordId='" + uniqueRecordId + '\'' +
            ", modifiedColumnNamesValues='" + modifiedColumnNamesValues + '\'' +
            '}';
    }
}
