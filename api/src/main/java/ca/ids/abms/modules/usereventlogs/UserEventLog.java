package ca.ids.abms.modules.usereventlogs;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.util.models.AuditedEntity;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Entity
public class UserEventLog extends AuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @SearchableText
    @Column(name="user_name")
    @NotNull
    private String userName;

    @NotNull
    private LocalDateTime dateTime;

    @SearchableText
    @Column(name="ip_address")
    @NotNull
    private String ipAddress;

    @SearchableText
    @Column(name="event_type")
    @NotNull
    private String eventType;

    @SearchableText
    @Column(name="record_primary_key")
    @NotNull
    private String recordPrimaryKey;

    @SearchableText
    @Column(name="unique_record_id")
    @NotNull
    private String uniqueRecordId;

    @NotNull
    private String modifiedColumnNamesValues;

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

    public Integer getId() {
        return id;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof UserEventLog))
            return false;

        UserEventLog that = (UserEventLog) obj;

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
