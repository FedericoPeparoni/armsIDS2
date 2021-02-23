package ca.ids.abms.modules.releasenotes;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReleaseNoteRepository extends ABMSRepository<ReleaseNote, Integer> {
}
