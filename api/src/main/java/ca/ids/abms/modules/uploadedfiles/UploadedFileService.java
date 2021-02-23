package ca.ids.abms.modules.uploadedfiles;

import ca.ids.abms.modules.rejected.RejectedItemType;
import ca.ids.abms.modules.uploadedfiles.enumerate.UploadedFileRecordType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.util.List;
import java.util.Date;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.modules.common.dto.BulkLoaderSummary;
import ca.ids.abms.modules.dataimport.DataImportType;

@Service
@Transactional
public class UploadedFileService {

    private static final Logger LOG = LoggerFactory.getLogger(UploadedFileService.class);

    private UploadedFileRepository uploadedFileRepository;

    public UploadedFileService(UploadedFileRepository uploadedFileRepository) {
        this.uploadedFileRepository = uploadedFileRepository;
    }

    /**
     * This method persist a record for an uploaded file.
     *
     * @param bulkLoaderSummary results of upload
     * @param recordType upload file record type
     * @param fileName name of file uploaded
     * @param fileType type of file uploaded (contentType)
     * @param fileContent content of file as bytes
     * @param manual true if manually uploaded
     * @param lastModified last modified datetime of file
     */
    public void createUploadedFileRecordFromBulkLoaderSummary(BulkLoaderSummary bulkLoaderSummary,
                                                                      UploadedFileRecordType recordType,
                                                                      String fileName, String fileType,
                                                                      byte[] fileContent, Boolean manual,
                                                                      LocalDateTime lastModified) {
        LOG.debug("Mapping bulkLoaderSummary to uploaded file record for {}.", fileName);
        save(UploadedFileMapper.mapBulkLoaderSummaryToUploadedFile(bulkLoaderSummary, recordType, fileName,
            fileType, fileContent, manual, lastModified));
    }

    /**
     * This method persist a record for an uploaded file that failed.
     *
     * @param exception exception thrown
     * @param recordType uploaded file record type
     * @param fileName name of file uploaded
     * @param fileType type of file uploaded (contentType)
     * @param fileContent content of file as bytes
     * @param manual true if manually uploaded
     * @param lastModified last modified datetime of file
     */
    public void createUploadFileRecordFromException(Exception exception, UploadedFileRecordType recordType,
                                                            String fileName, String fileType, byte[] fileContent,
                                                            Boolean manual, LocalDateTime lastModified) {
        LOG.debug("Mapping exception to uploaded file record for {}.", fileName);
        save(UploadedFileMapper.mapExceptionToUploadedFile(exception, recordType, fileName, fileType,
            fileContent, manual, lastModified));
    }

    public LocalDateTime formatFileLastModifiedDate(Long lastModified) {
        return LocalDateTime.ofInstant(new Date(lastModified).toInstant(), ZoneId.systemDefault());
    }

    public UploadedFileRecordType getUploadedFileRecordType(DataImportType dataImportType) {
        switch (dataImportType) {
            case ATC:
                return UploadedFileRecordType.ATC_MOVEMENT_LOG;
            case PASSENGER:
                return UploadedFileRecordType.PASSENGER_SERVICE_CHARGE_LOG;
            case RADAR:
                return UploadedFileRecordType.RADAR_SUMMARY;
            case TOWER:
                return UploadedFileRecordType.TOWER_LOG;
            default:
                return UploadedFileRecordType.UNKNOWN;
        }
    }

    public UploadedFileRecordType getUploadedFileRecordType(RejectedItemType rejectedItemType) {
        switch (rejectedItemType) {
            case ATS_MOVEMENTS_LOG:
                return UploadedFileRecordType.ATC_MOVEMENT_LOG;
            case PASSENGER_SERVICE_CHARGE_RETURNS:
                return UploadedFileRecordType.PASSENGER_SERVICE_CHARGE_LOG;
            case RADAR_SUMMARIES:
                return UploadedFileRecordType.RADAR_SUMMARY;
            case TOWER_AIRCRAFT_PASSENGER_MOVEMENTS_LOG:
                return UploadedFileRecordType.TOWER_LOG;
            default:
                return UploadedFileRecordType.UNKNOWN;
        }
    }

    public Boolean shouldProcessFile(String fileName, DataImportType recordType, Long lastModified) {
        // Convert file record type to uploaded file record type
        UploadedFileRecordType type = getUploadedFileRecordType(recordType);
        LocalDateTime lastModifiedLDT = formatFileLastModifiedDate(lastModified);

        // find all uploaded files by logical keys
        List <UploadedFile> uploadedFiles = uploadedFileRepository.findAutomaticUploadsByLogicalKeys(fileName, type.toValue(), lastModifiedLDT);

        return uploadedFiles == null || uploadedFiles.isEmpty();
    }

    private UploadedFile save(final UploadedFile uploadedFile){
        LOG.debug("Saving {}", uploadedFile);
        return uploadedFileRepository.save(uploadedFile);
    }
}
