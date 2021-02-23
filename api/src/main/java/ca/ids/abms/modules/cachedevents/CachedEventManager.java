package ca.ids.abms.modules.cachedevents;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.hibernate.StaleStateException;
import org.springframework.stereotype.Component;

import ca.ids.abms.modules.util.models.ModelUtils;
import ca.ids.spring.cache.CacheableException;
import ca.ids.spring.cache.exceptions.RetryCycleLockException;
import ca.ids.spring.cache.managers.CacheExceptionManager;

/**
 * Wrapper for the cache exception manager.
 */
@SuppressWarnings("WeakerAccess")
@Component
public class CachedEventManager {

    private final CachedEventMapper cachedEventMapper;
    private final CachedEventService cachedEventService;
    private final CacheExceptionManager cacheExceptionManager;

    public CachedEventManager(CachedEventMapper cachedEventMapper,
                              CachedEventService cachedEventService,
                              CacheExceptionManager cacheExceptionManager) {
        this.cachedEventMapper = cachedEventMapper;
        this.cachedEventService = cachedEventService;
        this.cacheExceptionManager = cacheExceptionManager;
    }

    /**
     * Get next execution time of the retry cycle.
     *
     * @return next retry cycle execution time
     */
    public Date nextRetryCycle() {
        return cacheExceptionManager.getNextExecutionTime();
    }

    /**
     * Attempt to find and run a cached event. Response is appended to found cached event
     * results list and returned.
     *
     * @param id cached event id to retry
     * @return cached event retried
     * @throws StaleStateException when no cached event found for id supplied
     */
    public CachedEvent retry(final Integer id) throws RetryCycleLockException {

        // find cached event by id
        CachedEvent cachedEvent = cachedEventService.find(id);

        // if cached event is null, throw new exception
        if (cachedEvent == null)
            throw new StaleStateException("Cached Event could not be found. It was either processed successfully or deleted by a user.");

        // find cached event and map to cacheable exception for retry
        // if class not found, add to results and return
        CacheableException cacheableException;
        try {
            cacheableException = cachedEventMapper
                .toCacheableException(cachedEvent);
        } catch (ClassNotFoundException | IOException ex) {
            cachedEvent.setRetry(false);
            cachedEvent.addThrownResult(ex);
            cachedEvent.onPersist(); // force update of audit properties
            cachedEventService.save(cachedEvent);
            return cachedEvent;
        }

        // retry cacheable exception, doRetry adds result
        cacheExceptionManager.retry(cacheableException);

        // map cacheable exception back to cached event object
        CachedEvent retryResult;
        try {
            retryResult = cachedEventMapper.toCachedEvent(cacheableException);
        } catch (IOException ex) {
            cachedEvent.setRetry(false);
            cachedEvent.addThrownResult(ex);
            cachedEvent.onPersist(); // force update of audit properties
            cachedEventService.save(cachedEvent);
            return cachedEvent;
        }

        // merge all except results, this preserves un mappable properties such as audit values
        ModelUtils.merge(retryResult, cachedEvent, "results");

        // bulk add all source results that do not exists in target
        List<CachedEventResult> source = retryResult.getResults();
        List<CachedEventResult> target = cachedEvent.getResults();
        target.addAll(source.subList(target.size(), source.size()));

        // return cached event with result appended to results list
        return cachedEvent;
    }
}
