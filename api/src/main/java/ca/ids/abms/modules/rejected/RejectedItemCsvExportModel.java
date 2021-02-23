package ca.ids.abms.modules.rejected;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDateTime;

public class RejectedItemCsvExportModel {

    private String recordType;

    @CsvProperty(dateTime = true, value = "Rejection Date / Time")
    private LocalDateTime rejectedDateTime;

    private String rejectedReason;

    private String errorMessage;

    private String status;

    private String originator;

    private String fileName;

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public LocalDateTime getRejectedDateTime() {
        return rejectedDateTime;
    }

    public void setRejectedDateTime(LocalDateTime rejectedDateTime) {
        this.rejectedDateTime = rejectedDateTime;
    }

    public String getRejectedReason() {
        return rejectedReason;
    }

    public void setRejectedReason(String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
