package ca.ids.spring.cache.exceptions;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class CacheableRuntimeExceptionTest {

    private String[] metadata;
    private Throwable throwable;

    private CacheableRuntimeException cacheableRuntimeException;

    @Before
    public void setup() {
        metadata = new String[] { "mock_metadata" };
        throwable = mock(Throwable.class);
        cacheableRuntimeException = new CacheableRuntimeException(metadata, throwable);
    }

    @Test
    public void geMetadataTest() {
        assertThat(cacheableRuntimeException.getCause()).isEqualTo(throwable);

        Object[] results = cacheableRuntimeException.getMetadata();
        assertThat(results.length).isEqualTo(metadata.length);
        assertThat(results[0]).isEqualTo(metadata[0]);
    }
}
