package ca.ids.abms.modules.uploadedfiles;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import ca.ids.abms.modules.uploadedfiles.enumerate.UploadedFileRecordType;
import ca.ids.abms.modules.util.models.AuditedEntity;

import java.time.LocalDateTime;

@Entity
public class UploadedFile extends AuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "file_record_type", length = 20)
    private UploadedFileRecordType fileRecordType;

    @NotNull
    private String fileName;

    @NotNull
    private String fileType;

    private byte[] fileContent;

    private Long recordsCreated;

    private Long recordsInFile;

    private Long recordsRejected;

    private Long recordsUnused;

    private Long recordsUpdated;

    private Boolean manualUpload;

    private LocalDateTime lastModified;

    @Column(name = "exception_type")
    private String exceptionType;

    @Column(name = "exception_message")
    private String exceptionMessage;

    @Column(name = "exception_trace")
    private String exceptionTrace;

    public UploadedFileRecordType getFileRecordType() {
        return fileRecordType;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public Long getRecordsCreated() {
        return recordsCreated;
    }

    public Long getRecordsInFile() {
        return recordsInFile;
    }

    public Long getRecordsRejected() {
        return recordsRejected;
    }

    public Long getRecordsUnused() {
        return recordsUnused;
    }

    public Long getRecordsUpdated() {
        return recordsUpdated;
    }

    public Boolean getManualUpload() {
        return manualUpload;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public String getExceptionTrace() {
        return exceptionTrace;
    }

    public void setUploadedFileRecordType(UploadedFileRecordType aFileRecordType) {
        fileRecordType = aFileRecordType;
    }

    public void setFileContent(byte[] theFileContent) {
        this.fileContent = theFileContent;
    }

    public void setFileName(String theFileName) {
        this.fileName = theFileName;
    }

    public void setFileType(String theFileType) {
        this.fileType = theFileType;
    }

    public void setRecordsCreated(Long numberOfRecordsCreated) {
        this.recordsCreated = numberOfRecordsCreated;
    }

    public void setRecordsInFile(Long numberOfRecordsInFile) {
        this.recordsInFile = numberOfRecordsInFile;
    }

    public void setRecordsRejected(Long numberOfRecordsRejected) {
        this.recordsRejected = numberOfRecordsRejected;
    }

    public void setRecordsUnused(Long numberOfRecordsUnused) {
        this.recordsUnused = numberOfRecordsUnused;
    }

    public void setRecordsUpdated(Long numberOfRecordsUpdated) {
        this.recordsUpdated = numberOfRecordsUpdated;
    }

    public void setManualUpload(Boolean manualUpload) { this.manualUpload = manualUpload; }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public void setExceptionTrace(String exceptionTrace) {
        this.exceptionTrace = exceptionTrace;
    }

    @Override
    public String toString() {
        return "UploadedFile [id=" + id + ", fileName=" + fileName + ", fileType=" + fileType + "]";
    }

}
