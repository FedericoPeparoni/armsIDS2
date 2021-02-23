package ca.ids.abms.config.cache;

import ca.ids.spring.cache.TriggerIntervalProviderImpl;
import ca.ids.spring.cache.managers.CacheUpdateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

// TODO: remove, cache update manager only here for prototype plugin exception mocking
@Configuration
public class CacheUpdateConfig {

    final private Logger LOG = LoggerFactory.getLogger(ca.ids.abms.plugins.prototype.config.CacheUpdateConfig.class);

    @Bean
    public CacheUpdateManager cacheUpdateManager() {
        LOG.info("Initialize Cache Update Manager with 1 minute cycle interval.");
        return new CacheUpdateManager(new TriggerIntervalProviderImpl(1, TimeUnit.MINUTES));
    }
}
