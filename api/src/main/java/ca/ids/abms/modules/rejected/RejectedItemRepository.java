package ca.ids.abms.modules.rejected;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RejectedItemRepository extends ABMSRepository<RejectedItem, Integer> {

    long countByRecordTypeAndRawTextAndJsonTextAndHeaderAndStatus(String recordType, String rawText, byte[] jsonText,
                                                                     String header, String status);
}
