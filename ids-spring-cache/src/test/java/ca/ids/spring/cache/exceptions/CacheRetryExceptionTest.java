package ca.ids.spring.cache.exceptions;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class CacheRetryExceptionTest {

    @Test
    public void cacheRetryExceptionTest() {
        CacheRetryException result = new CacheRetryException();
        assertThat(result.getMessage()).isEqualTo(CacheRetryException.MESSAGE);
    }

    @Test
    public void cacheRetryExceptionThrowableTest() {
        Throwable throwable = mock(Throwable.class);
        CacheRetryException result = new CacheRetryException(throwable);
        assertThat(result.getCause()).isEqualTo(throwable);
        assertThat(result.getMessage()).isEqualTo(CacheRetryException.MESSAGE);
    }
}
