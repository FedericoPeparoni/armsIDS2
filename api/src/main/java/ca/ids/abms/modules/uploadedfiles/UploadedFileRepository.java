package ca.ids.abms.modules.uploadedfiles;

import ca.ids.abms.config.db.ABMSRepository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UploadedFileRepository extends ABMSRepository<UploadedFile, Integer> {
    /**
     * Find uploaded files by logical keys
     */
    @Query(nativeQuery = true, value =
        "SELECT uf.* "
            + "FROM uploaded_files uf "
            + "WHERE uf.file_name = :fileName "
            + "AND uf.file_record_type = :recordType "
            + "AND uf.manual_upload IS false "
            + "AND uf.last_modified = :lastModified\\:\\:timestamp "
            + "ORDER BY uf.last_modified DESC"
    )
    List <UploadedFile> findAutomaticUploadsByLogicalKeys (
        final @Param("fileName") String fileName,
        final @Param("recordType") String recordType,
        final @Param("lastModified") LocalDateTime lastModified
    );
}
