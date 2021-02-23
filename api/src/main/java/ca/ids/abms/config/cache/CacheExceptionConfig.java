package ca.ids.abms.config.cache;

import ca.ids.abms.modules.cachedevents.CachedEventProvider;
import ca.ids.abms.modules.cachedevents.CachedEventTrigger;
import ca.ids.abms.modules.locking.JobLockingService;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.spring.cache.managers.CacheExceptionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheExceptionConfig {

    @Bean
    public CacheExceptionManager cacheExceptionManager(CachedEventProvider cachedEventProvider,
                                                       JobLockingService jobLockingService,
                                                       SystemConfigurationService systemConfigurationService) {
        return new CacheExceptionManager(cachedEventProvider,
            new CachedEventTrigger(jobLockingService, systemConfigurationService));
    }
}
