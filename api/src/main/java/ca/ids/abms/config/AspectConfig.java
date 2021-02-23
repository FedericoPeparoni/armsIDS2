package ca.ids.abms.config;

import ca.ids.abms.config.aop.LoggingAspect;
import ca.ids.spring.cache.interceptors.CacheOnExceptionAspect;
import ca.ids.spring.cache.managers.CacheExceptionManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class AspectConfig {

    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }

    @Bean
    @DependsOn({"cacheManager", "keyGenerator", "cacheExceptionManager"})
    public CacheOnExceptionAspect cacheOnExceptionAspect(CacheManager cacheManager,
                                                         KeyGenerator keyGenerator,
                                                         CacheExceptionManager cacheExceptionManager) {
        return new CacheOnExceptionAspect(cacheManager, keyGenerator,
            cacheExceptionManager.getCacheableExceptionProvider());
    }
}
