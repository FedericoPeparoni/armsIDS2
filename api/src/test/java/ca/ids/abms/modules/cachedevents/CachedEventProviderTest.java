package ca.ids.abms.modules.cachedevents;

import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.spring.cache.CacheableException;
import ca.ids.spring.cache.CacheableExceptionResult;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class CachedEventProviderTest {

    private CachedEventMapper cachedEventMapper;
    private CachedEventProvider cachedEventProvider;
    private CachedEventService cachedEventService;
    private SystemConfigurationService systemConfigurationService;

    @Before
    public void setup() throws ClassNotFoundException, IOException {
        cachedEventMapper = mock(CachedEventMapper.class);
        cachedEventService = mock(CachedEventService.class);
        systemConfigurationService = mock(SystemConfigurationService.class);

        cachedEventProvider = new CachedEventProvider(cachedEventService, cachedEventMapper, systemConfigurationService);

        when(cachedEventMapper.toCacheableException(any(CachedEvent.class))).thenReturn(getCacheableException());
        when(cachedEventMapper.toCachedEvent(any(CacheableException.class))).thenReturn(CachedEventTest.getCachedEvent());

        when(cachedEventService.findByRetry(anyBoolean())).thenReturn(Collections.singletonList(CachedEventTest.getCachedEvent()));
        when(cachedEventService.save(any(CachedEvent.class))).thenReturn(CachedEventTest.getCachedEvent());
    }

    @Test
    public void findAllTest() throws ClassNotFoundException, IOException {
        List<CacheableException> result = cachedEventProvider.findAll();

        assertThat(result.size()).isEqualTo(1);

        verify(cachedEventMapper, times(1)).toCacheableException(any(CachedEvent.class));

        verify(cachedEventService, times(1)).findByRetry(anyBoolean());
        verify(cachedEventService, times(0)).save(any(CachedEvent.class));
    }

    @Test
    public void createUpdateTest() throws IOException {
        cachedEventProvider.create(getCacheableException());
        cachedEventProvider.update(getCacheableException());

        verify(cachedEventMapper, times(2)).toCachedEvent(any(CacheableException.class));

        verify(cachedEventService, times(2)).save(any(CachedEvent.class));
    }

    @Test
    public void removeSuccessTest() throws IOException {
        cachedEventProvider.remove(getCacheableException(false));

        verify(cachedEventMapper, times(0)).toCachedEvent(any(CacheableException.class));

        verify(cachedEventService, times(0)).save(any(CachedEvent.class));
        verify(cachedEventService, times(1)).remove(anyInt());
    }

    @Test
    public void removeThrowntTest() throws IOException  {
        cachedEventProvider.remove(getCacheableException());

        verify(cachedEventMapper, times(1)).toCachedEvent(any(CacheableException.class));

        verify(cachedEventService, times(1)).save(any(CachedEvent.class));
        verify(cachedEventService, times(0)).remove(anyInt());
    }

    private CacheableException getCacheableException() {
        return this.getCacheableException(true);
    }

    private CacheableException getCacheableException(Boolean thrown) {
        CacheableExceptionResult cacheableExceptionResult = new CacheableExceptionResult();
        cacheableExceptionResult.setThrown(thrown);

        CacheableException cacheableException = new CacheableException();
        cacheableException.setId(1);
        cacheableException.setResults(Collections.singletonList(cacheableExceptionResult));

        return cacheableException;
    }
}
