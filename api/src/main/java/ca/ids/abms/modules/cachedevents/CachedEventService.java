package ca.ids.abms.modules.cachedevents;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.util.models.ModelUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@SuppressWarnings("WeakerAccess")
public class CachedEventService {

    private CachedEventRepository cachedEventRepository;

    public CachedEventService(CachedEventRepository cachedEventRepository) {
        this.cachedEventRepository = cachedEventRepository;
    }

    public CachedEvent find(final Integer id) {
        return cachedEventRepository.findOne(id);
    }

    public List<CachedEvent> findAll() {
        return cachedEventRepository.findAll();
    }

    public Page<CachedEvent> findAll(final String search, final Pageable pageable) {
        return cachedEventRepository.findAll(new FiltersSpecification.Builder(search).build(), pageable);
    }

    public List<CachedEvent> findByRetry(Boolean retry) {
        return cachedEventRepository.findByRetryOrderByIdAsc(retry);
    }
    
    public void remove(final CachedEvent cachedEvent) {
        if (cachedEvent != null && cachedEvent.getId() != null && this.find(cachedEvent.getId()) != null)
            cachedEventRepository.delete(cachedEvent);
    }

    public void remove(final Integer id) {
        if (this.find(id) != null)
            cachedEventRepository.delete(id);
    }

    public CachedEvent save(final CachedEvent cachedEvent) {

        // return if null, nothing to save
        if (cachedEvent == null)
            return null;

        // merge with existing if id not null
        // required for properties (ie. audit values) that are not mapped to other models
        CachedEvent result;
        if (cachedEvent.getId() != null) {

            // find existing cached event by id
            result = cachedEventRepository.findOne(cachedEvent.getId());

            // merge all except results
            ModelUtils.merge(cachedEvent, result, "results");

            // bulk replace all target results that do not exists in source
            result.getResults().clear();
            result.getResults().addAll(cachedEvent.getResults());

        } else {
            result = cachedEvent;
        }

        // save and flush all changes
        return cachedEventRepository.saveAndFlush(result);
    }

    public long countAll() {
        return cachedEventRepository.count();
    }
}
