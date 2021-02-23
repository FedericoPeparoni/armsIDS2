package ca.ids.abms.modules.uploadedfiles;

import java.time.LocalDateTime;
import java.util.Arrays;

import ca.ids.abms.modules.common.dto.BulkLoaderSummary;
import ca.ids.abms.modules.uploadedfiles.enumerate.UploadedFileRecordType;

public class UploadedFileMapper {

    public static UploadedFile mapBulkLoaderSummaryToUploadedFile(BulkLoaderSummary summary,
                                                                  UploadedFileRecordType recordType, String fileName,
                                                                  String fileType, byte[] fileContent, Boolean manual,
                                                                  LocalDateTime lastModified) {

        UploadedFile uploadedFile = new UploadedFile();

        uploadedFile.setFileName(fileName);
        uploadedFile.setFileType(fileType);
        uploadedFile.setUploadedFileRecordType(recordType);

        uploadedFile.setFileContent(fileContent);

        uploadedFile.setRecordsInFile(summary.getTotal());
        uploadedFile.setRecordsCreated(summary.getFplAdded());
        uploadedFile.setRecordsUpdated(summary.getFplUpdated());
        uploadedFile.setRecordsRejected(summary.getRejected());
        uploadedFile.setRecordsUnused(
            summary.getTotal() - (summary.getFplAdded() + summary.getFplUpdated() + summary.getRejected())
        );

        uploadedFile.setManualUpload(manual);
        uploadedFile.setLastModified(lastModified);

        return uploadedFile;
    }

    public static UploadedFile mapExceptionToUploadedFile(Exception exception, UploadedFileRecordType recordType, String fileName,
                                                          String fileType, byte[] fileContent, Boolean manual,
                                                          LocalDateTime lastModified) {
        UploadedFile uploadedFile = new UploadedFile();

        uploadedFile.setFileName(fileName);
        uploadedFile.setFileType(fileType);
        uploadedFile.setUploadedFileRecordType(recordType);

        uploadedFile.setFileContent(fileContent);

        uploadedFile.setExceptionType(exception != null && exception.getClass() != null ? exception.getClass().getSimpleName() : null);
        uploadedFile.setExceptionMessage(exception != null ? exception.getMessage() : null);
        uploadedFile.setExceptionTrace(exception != null && exception.getStackTrace().length > 0 ? Arrays.toString(exception.getStackTrace()) : null);

        uploadedFile.setManualUpload(manual);
        uploadedFile.setLastModified(lastModified);

        return uploadedFile;
    }

    private UploadedFileMapper() {
        throw new IllegalStateException("Utility class");
    }
}
