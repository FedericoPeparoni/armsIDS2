package ca.ids.abms.modules.rejected;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.modules.translation.Translation;
import ca.ids.abms.modules.util.models.AuditedEntity;

public class RejectedItemViewModel extends AuditedEntity {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @NotNull
    @Size(max = 30)
    private RejectedItemType recordType;

    @NotNull
    private LocalDateTime rejectedDateTime;

    @Size(max = 512)
    private String errorMessage;

    @Size(max = 512)
    private String errorDetails;

    @NotNull
    @Size(max = 128)
    private String rejectedReason;

    @Size(max = 1024)
    private String rawText;

    @Size(max = 1024)
    private String header;

    private byte[] jsonText;

    @NotNull
    @Size(max = 15)
    private RejectedItemStatus status;

    @Size(max = 128)
    private String originator;

    @Size(max = 128)
    private String fileName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RejectedItemType getRecordType() {
        return recordType;
    }

    public void setRecordType(RejectedItemType recordType) {
        this.recordType = recordType;
    }

    public LocalDateTime getRejectedDateTime() {
        return rejectedDateTime;
    }

    public void setRejectedDateTime(LocalDateTime rejectedDateTime) {
        this.rejectedDateTime = rejectedDateTime;
    }

    public String getRejectedReason() {
        if (rejectedReason == null) {
            return null;
        }

        return Translation.getLangByToken(rejectedReason);
    }

    public void setRejectedReason(String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }

    public RejectedItemStatus getStatus() {
        return status;
    }

    public void setStatus(RejectedItemStatus status) {
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

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public byte[] getJsonText() {
        return jsonText;
    }

    public void setJsonText(byte[] jsonText) {
        this.jsonText = jsonText;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RejectedItemViewModel)) {
            return false;
        }
        RejectedItemViewModel that = (RejectedItemViewModel) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return rejectedDateTime != null ? rejectedDateTime.equals(that.rejectedDateTime) : that.rejectedDateTime == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (rejectedDateTime != null ? rejectedDateTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RejectedItem{" +
            "id=" + id +
            ", recordType='" + recordType + '\'' +
            ", rejectedDateTime=" + rejectedDateTime +
            ", rejectedReason='" + rejectedReason + '\'' +
            ", rawText='" + rawText + '\'' +
            ", status='" + status + '\'' +
            ", originator='" + originator + '\'' +
            ", fileName='" + fileName + '\'' +
            '}';
    }
}
