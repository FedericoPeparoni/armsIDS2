package ca.ids.spring.cache;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class CacheableExceptionResultTest {

    private static Class CLAZZ = Exception.class;
    private static String RESULT = "mock exception";
    private static Boolean THROWN = true;

    private CacheableExceptionResult cacheableExceptionResult;

    @Before
    public void setup() {
        cacheableExceptionResult = getCacheableExceptionResult();
    }

    @Test
    public void cacheableExceptionResultTest() {
        CacheableExceptionResult result = new CacheableExceptionResult(CLAZZ, RESULT, THROWN);
        assertThat(result.getClazz()).isEqualTo(CLAZZ);
        assertThat(result.getResult()).isEqualTo(RESULT);
        assertThat(result.getThrown()).isEqualTo(THROWN);
    }

    @Test
    public void getterSetterTest() {
        assertThat(cacheableExceptionResult.getClazz()).isEqualTo(CLAZZ);
        assertThat(cacheableExceptionResult.getResult()).isEqualTo(RESULT);
        assertThat(cacheableExceptionResult.getThrown()).isEqualTo(THROWN);
    }

    @Test
    public void toStringTest() {
        assertThat(cacheableExceptionResult.toString()).isEqualTo(getCacheableExceptionResult().toString());
    }

    @Test
    public void equalTest() {
        assertThat(cacheableExceptionResult).isEqualTo(getCacheableExceptionResult());
        assertThat(cacheableExceptionResult).isEqualTo(cacheableExceptionResult);
        assertThat(cacheableExceptionResult).isNotEqualTo("mock string");

        CacheableExceptionResult cacheableExceptionResult1 = getCacheableExceptionResult();
        CacheableExceptionResult cacheableExceptionResulta2 = getCacheableExceptionResult();
        cacheableExceptionResulta2.setResult(RESULT + ".fail");

        assertThat(cacheableExceptionResult1).isNotEqualTo(cacheableExceptionResulta2);

        cacheableExceptionResult1.setClazz(null);
        cacheableExceptionResult1.setResult(null);
        cacheableExceptionResult1.setThrown(null);

        cacheableExceptionResulta2.setClazz(null);
        cacheableExceptionResulta2.setResult(null);
        cacheableExceptionResulta2.setThrown(null);

        assertThat(cacheableExceptionResult1).isEqualTo(cacheableExceptionResulta2);
    }

    static CacheableExceptionResult getCacheableExceptionResult() {
        CacheableExceptionResult result = new CacheableExceptionResult();
        result.setClazz(CLAZZ);
        result.setResult(RESULT);
        result.setThrown(THROWN);
        return result;
    }
}
