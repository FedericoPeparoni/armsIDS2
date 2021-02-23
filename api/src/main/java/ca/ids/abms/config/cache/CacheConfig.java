package ca.ids.abms.config.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport implements CachingConfigurer {

    @Bean
    public CacheManager cacheManager() {

        // predefined cache names, required ahead of time for concurrency map cache manager
        List<String> cacheNames = new ArrayList<>();
        cacheNames.add(CacheNames.CACHE_TRANSACTIONS);

        // NAV DB
        cacheNames.add(CacheNames.NAVDB_IDENTS);
        cacheNames.add(CacheNames.NAVDB_COORDINATES);
        cacheNames.add(CacheNames.NAVDB_AIRSPACES);
        cacheNames.add(CacheNames.NAVDB_AIRSPACE_BY_ID);
        cacheNames.add(CacheNames.NAVDB_AERODROME_PREFIXES);
        cacheNames.add(CacheNames.NAVDB_FIRS_BY_LOCATION);
        cacheNames.add(CacheNames.NAVDB_AD_INSIDE_SOUTH_SUDAN);

        // create and configure cache manager
        ConcurrentMapCacheManager concurrentMapCacheManager = new ConcurrentMapCacheManager();
        concurrentMapCacheManager.setCacheNames(cacheNames);

        // return cache manager
        return concurrentMapCacheManager;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> SimpleKeyGenerator.generateKey(params);
    }
}
