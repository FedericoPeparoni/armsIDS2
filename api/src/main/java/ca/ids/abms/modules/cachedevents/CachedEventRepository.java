package ca.ids.abms.modules.cachedevents;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CachedEventRepository extends ABMSRepository<CachedEvent, Integer> {

    List<CachedEvent> findByRetryOrderByIdAsc(Boolean retry);
}
