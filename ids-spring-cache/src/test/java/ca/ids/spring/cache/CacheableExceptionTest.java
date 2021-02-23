package ca.ids.spring.cache;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class CacheableExceptionTest {

    private static Integer ID = 1;
    private static Class TARGET = String.class;
    private static String METHOD_NAME = "compareTo";
    private static Class[] PARAM_TYPES = new Class<?>[] { String.class };
    private static Object[] ARGS = new Object[] { "testing" };
    private static Class[] EXCEPTIONS = new Class<?>[] { Exception.class };
    private static String[] CACHES = new String[] { "mock_cache_name" };
    private static Boolean EXCLUDE = false;
    private static Object[] METADATA = new Object[] { "mock metadata" };
    private static List<CacheableExceptionResult> RESULTS = Collections.singletonList(
        CacheableExceptionResultTest.getCacheableExceptionResult());

    private CacheableException cacheableException;

    @Before
    public void setup() {
        cacheableException = getCacheableException();
    }

    @Test
    public void cacheableExceptionTest() {
        CacheableException result = new CacheableException(TARGET, METHOD_NAME, PARAM_TYPES, ARGS,
            EXCEPTIONS, CACHES, EXCLUDE, METADATA, new Exception(CacheableExceptionResultTest.getCacheableExceptionResult()
            .getResult()));
        assertThat(result.getId()).isNull();
        assertThat(result.getTarget()).isEqualTo(TARGET);
        assertThat(result.getMethodName()).isEqualTo(METHOD_NAME);
        assertThat(result.getParamTypes()).isEqualTo(PARAM_TYPES);
        assertThat(result.getArgs()).isEqualTo(ARGS);
        assertThat(result.getExceptions()).isEqualTo(EXCEPTIONS);
        assertThat(result.getCaches()).isEqualTo(CACHES);
        assertThat(result.getMetadata()).isEqualTo(METADATA);
        assertThat(result.getResults()).isEqualTo(RESULTS);
    }

    @Test
    public void getterSetterTest() {
        assertThat(cacheableException.getId()).isEqualTo(ID);
        assertThat(cacheableException.getTarget()).isEqualTo(TARGET);
        assertThat(cacheableException.getMethodName()).isEqualTo(METHOD_NAME);
        assertThat(cacheableException.getParamTypes()).isEqualTo(PARAM_TYPES);
        assertThat(cacheableException.getArgs()).isEqualTo(ARGS);
        assertThat(cacheableException.getExceptions()).isEqualTo(EXCEPTIONS);
        assertThat(cacheableException.getCaches()).isEqualTo(CACHES);
        assertThat(cacheableException.getMetadata()).isEqualTo(METADATA);
        assertThat(cacheableException.getResults()).isEqualTo(RESULTS);
    }

    @Test
    public void addReturnedResultTest() {
        cacheableException.setResults(null);
        cacheableException.addReturnedResult("Mock Testing Result");
        assertThat(cacheableException.getResults().size()).isEqualTo(1);
    }

    @Test
    public void addThrownResultTest() {
        cacheableException.setResults(null);
        cacheableException.addThrownResult(new Exception("Mock Testing Exception"));
        assertThat(cacheableException.getResults().size()).isEqualTo(1);
    }

    @Test
    public void toStringTest() {
        assertThat(cacheableException.toString()).isEqualTo(getCacheableException().toString());
    }

    @Test
    public void equalTest() {
        assertThat(cacheableException).isEqualTo(getCacheableException());
        assertThat(cacheableException).isEqualTo(cacheableException);
        assertThat(cacheableException).isNotEqualTo("mock string");

        CacheableException cachedEvent1 = getCacheableException();
        CacheableException cachedEvent2 = getCacheableException();
        cachedEvent2.setId(0);

        assertThat(cachedEvent1).isNotEqualTo(cachedEvent2);

        cachedEvent1.setId(null);
        cachedEvent2.setId(null);

        assertThat(cachedEvent1).isEqualTo(cachedEvent2);
    }

    public static CacheableException getCacheableException() {
        CacheableException result = new CacheableException();
        result.setId(ID);
        result.setTarget(TARGET);
        result.setMethodName(METHOD_NAME);
        result.setParamTypes(PARAM_TYPES);
        result.setArgs(ARGS);
        result.setExceptions(EXCEPTIONS);
        result.setCaches(CACHES);
        result.setMetadata(METADATA);
        result.setResults(RESULTS);
        result.setExclude(EXCLUDE);
        return result;
    }
}
