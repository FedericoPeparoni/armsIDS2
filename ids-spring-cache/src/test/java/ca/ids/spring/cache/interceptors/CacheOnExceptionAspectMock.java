package ca.ids.spring.cache.interceptors;

import ca.ids.spring.cache.annotations.CacheOnExceptionConfig;
import org.springframework.cache.annotation.CacheConfig;

@CacheConfig(cacheNames = CacheOnExceptionAspectMock.CACHE_NAME)
@CacheOnExceptionConfig(exceptions = { Throwable.class })
public class CacheOnExceptionAspectMock {

    final static String ARG = "mockArg";
    final static String CACHE_NAME = "mock_cache_name";
    final static String METHOD_NAME = "mockMethod";
    final static String METHOD_VOID_NAME = "mockMethodVoid";
    final static Class PARAM_TYPE = String.class;
    final static String RESULT = "mock_result";
    final static Class RETURN_TYPE = String.class;

    public String mockMethod(String mockArg) {
        return RESULT;
    }

    public void mockMethodVoid(String mockArg) {
        // ignore
    }
}
