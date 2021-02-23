package ca.ids.abms.modules.cachedevents;

import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.spring.cache.CacheableException;
import ca.ids.spring.cache.CacheableExceptionProvider;
import ca.ids.spring.cache.CacheableExceptionResult;
import com.google.common.base.Preconditions;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CachedEventProvider implements CacheableExceptionProvider {

    private static final Logger LOG = LoggerFactory.getLogger(CachedEventProvider.class);

    private CachedEventService cachedEventService;
    private CachedEventMapper cachedEventMapper;
    private SystemConfigurationService systemConfigurationService;

    @SuppressWarnings("WeakerAccess")
    public CachedEventProvider(CachedEventService cachedEventService, CachedEventMapper cachedEventMapper,
            SystemConfigurationService systemConfigurationService) {
        this.cachedEventService = cachedEventService;
        this.cachedEventMapper = cachedEventMapper;
        this.systemConfigurationService = systemConfigurationService;
    }

    @Override
    public List<CacheableException> findAll() {
        
        // find all CachedEvents
        List<CachedEvent> cachedEvents = cachedEventService.findByRetry(true);

        // attempt to map each returned CachedEvent to a CacheableException
        // if ClassNotFoundException, add to results, save, and skip over
        List<CacheableException> cacheableExceptions = new ArrayList<>();
        for (CachedEvent cachedEvent : cachedEvents) {
            try {
                cacheableExceptions.add(cachedEventMapper.toCacheableException(cachedEvent));
            } catch (ClassNotFoundException | IOException ex) {
                LOG.warn("Removing CachedEvent {} from retry cycle because : {}",
                    cachedEvent, ex.getLocalizedMessage());
                cachedEvent.setRetry(false);
                cachedEvent.addThrownResult(ex);
                cachedEventService.save(cachedEvent);
            }
        }

        LOG.trace("Begin retry cycle with {} cacheable exceptions from all cached events flagged to retry.",
            cacheableExceptions.size());

        // return all mapped CacheableExceptions
        return cacheableExceptions;
    }

    @Override
    public void create(CacheableException cacheableException) {
        LOG.debug("Creating new cached event from cacheable exception {}.", cacheableException);
        this.save(cacheableException);
    }

    @Override
    public void update(CacheableException cacheableException) {
        LOG.debug("Updating cached event from cacheable exception {}.", cacheableException);
        this.save(cacheableException);
    }

    @Override
    public void remove(CacheableException cacheableException) {
        LOG.debug("Removing cached event from cacheable exception {}.", cacheableException);
        if (cacheableException != null) {

            // get last item to validate if last result threw an exception
            List<CacheableExceptionResult> results = cacheableException.getResults();

            // if thrown, soft delete as last result was an unwanted exception
            // else, hard delete as last result was successful
            if (results != null && !results.isEmpty() && results.get(results.size() - 1).getThrown())
                this.save(cacheableException, false);
            else
                cachedEventService.remove(cacheableException.getId());
        }
    }

    /**
     * Handle if list of exceptions is empty or a list item is an instance of the cause.
     */
    @Override
    public boolean handleException(final Class[] exceptions, final Throwable cause, final boolean exclude) {
        Preconditions.checkNotNull(cause, "Cause cannot be null");

        if (exceptions == null || exceptions.length == 0)
            return !exclude;

        for (Class<?> exception : exceptions) {
            if (exception.isInstance(cause))
                return !exclude;
        }

        return exclude;
    }

    private void save(CacheableException cacheableException) {
        this.save(cacheableException, true);
    }
    
    private Integer getMaxRetries() {
        return this.systemConfigurationService.getInteger (SystemConfigurationItemName.CACHED_EVENT_MAX_RETRIES, null);
    }

    @SuppressWarnings({"squid:S00112", "squid:S2139"})
    private void save(CacheableException cacheableException, boolean retry) {
        try {
            CachedEvent cachedEvent = cachedEventMapper.toCachedEvent(cacheableException);
            // Disable future retry attempts if we've generated too many results by this point
            if (retry) {
                final Integer maxRetries = getMaxRetries();
                if (maxRetries != null && cachedEvent.getRetryCount() > maxRetries) {
                    LOG.debug ("Setting \"retry\" to false because this event exceeded retry limit \"{}\": {}", maxRetries, cachedEvent);
                    retry = false;
                }
            }
            cachedEvent.setRetry(retry);

            // Save only the last element in result list
            final List <CachedEventResult> resultList = cachedEvent.getResults();
            if (!CollectionUtils.isEmpty (resultList)) {
                cachedEvent.setResults (Arrays.asList (resultList.get (resultList.size() - 1)));
            }

            // Save it
            cachedEventService.save(cachedEvent);
        } catch (IOException ex) {
            LOG.error("Failed to map CacheableException {} to CachedEvent before saving because : {}",
                cacheableException, ex);
            throw new RuntimeException(ex);
        }
    }
    
}
