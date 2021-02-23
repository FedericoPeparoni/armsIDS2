package ca.ids.spring.cache;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

public class CacheableExceptionProviderTest {

    private CacheableException cacheableException;

    private CacheableExceptionProviderImpl cacheableExceptionProvider;

    @Before
    public void setup() {

        // setup cacheable exception
        cacheableException = CacheableExceptionTest.getCacheableException();

        // setup new cacheable exception provider
        cacheableExceptionProvider = new CacheableExceptionProviderImpl();
    }

    @Test
    public void cacheableExceptionProviderTest() {

        // assert that cacheable exception provider is empty to start
        List<CacheableException> results = cacheableExceptionProvider.findAll();
        assertThat(results.size()).isEqualTo(0);

        // save a cacheable exception to provider
        cacheableExceptionProvider.create(cacheableException);

        // invoke findAll cacheable exceptions
        results = cacheableExceptionProvider.findAll();

        // assert that result size is 1 and equal to cacheable exception
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isEqualTo(cacheableException);

        // update a cacheable exception in provider
        cacheableException.setMethodName("equalsIgnoreCase");
        cacheableExceptionProvider.update(cacheableException);

        // invoke findAll cacheable exceptions
        results = cacheableExceptionProvider.findAll();

        // assert that result size is still 1 and equal update
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isEqualTo(cacheableException);

        // remove cacheable exception that was added
        cacheableExceptionProvider.remove(cacheableException);

        // assert that cacheable exceptiuon provider is empty
        results = cacheableExceptionProvider.findAll();
        assertThat(results.size()).isEqualTo(0);
    }

    @Test
    public void handleExceptionTest() {

        // assert true when exceptions is empty and exclude is false
        assertThat(cacheableExceptionProvider.handleException(null, new Throwable(), false)).isTrue();
        assertThat(cacheableExceptionProvider.handleException(new Class[]{}, new Throwable(), false)).isTrue();

        // assert false when exceptions is empty and exclude is true
        assertThat(cacheableExceptionProvider.handleException(null, new Throwable(), true)).isFalse();
        assertThat(cacheableExceptionProvider.handleException(new Class[]{}, new Throwable(), true)).isFalse();

        // assert true when exceptions is not-empty, cause included in exceptions, and exclude is false
        assertThat(cacheableExceptionProvider.handleException(new Class[]{ Throwable.class }, new Throwable(), false)).isTrue();

        // assert false when exceptions is not-empty, cause included in exceptions, and exclude is true
        assertThat(cacheableExceptionProvider.handleException(new Class[]{ Throwable.class }, new Throwable(), true)).isFalse();

        // assert that IllegalArgumentException is thrown when cause is null
        try {
            cacheableExceptionProvider.handleException(null, null, false);
            fail("Expected " + IllegalArgumentException.class + " thrown when cause is null.");
        } catch (IllegalArgumentException ignored) {}
    }
}
