package ca.ids.abms.modules.usereventlogs;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDateTime;

public class UserEventLogCsvExportModel {

    private String userName;

    @CsvProperty(value = "Date / Time", dateTime = true)
    private LocalDateTime dateTime;

    @CsvProperty(value = "IP Address")
    private String ipAddress;

    private String eventType;

    private String recordPrimaryKey;

    @CsvProperty(value = "Dataset Name")
    private String uniqueRecordId;

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
}
