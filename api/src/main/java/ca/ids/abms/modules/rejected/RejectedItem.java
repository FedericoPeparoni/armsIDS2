package ca.ids.abms.modules.rejected;

import java.time.LocalDateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.util.models.AuditedEntity;

@Entity
public class RejectedItem extends AuditedEntity {

    private static final long serialVersionUID = 1L;

	static final int ERR_MESSAGE_SIZE = 512;
    static final int ERR_DETAILS_SIZE = 512;
    static final int REJ_REASON_SIZE = 128;
    static final int RAW_TEXT_SIZE = 1024;
    static final int HEADER_SIZE = 1024;
    static final int FILE_NAME_SIZE = 128;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @SearchableText
    @Column(name="record_type")
    @NotNull
    @Size(max = 30)
    private String recordType;

    @NotNull
    private LocalDateTime rejectedDateTime;

    @SearchableText
    @Column(name="rejected_reason")
    @NotNull
    @Size(max = REJ_REASON_SIZE)
    private String rejectedReason;

    @SearchableText
    @Column(name="error_message")
    @Size(max = ERR_MESSAGE_SIZE)
    private String errorMessage;

    @SearchableText
    @Size(max = ERR_DETAILS_SIZE)
    private String errorDetails;

    @Size(max = RAW_TEXT_SIZE)
    private String rawText;

    @Size(max = HEADER_SIZE)
    private String header;

    private byte[] jsonText;

    @SearchableText
    @Column(name="status")
    @NotNull
    @Size(max = 15)
    private String status;

    @SearchableText
    @Column(name="originator")
    @Size(max = 128)
    private String originator;

    @SearchableText
    @Column(name="file_name")
    @Size(max = FILE_NAME_SIZE)
    private String fileName;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RejectedItem)) {
            return false;
        }
        RejectedItem that = (RejectedItem) o;

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
