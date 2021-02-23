package ca.ids.abms.modules.cachedevents;

import ca.ids.spring.cache.CacheableException;
import ca.ids.spring.cache.exceptions.RetryCycleLockException;
import ca.ids.spring.cache.managers.CacheExceptionManager;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class CachedEventManagerTest {

    private CachedEventManager cachedEventManager;
    private CachedEventMapper cachedEventMapper;
    private CachedEventService cachedEventService;
    private CacheExceptionManager cacheExceptionManager;

    private Date nextRetryCycle;

    @Before
    public void setup() throws ClassNotFoundException, IOException {
        cachedEventMapper = mock(CachedEventMapper.class);
        cachedEventService = mock(CachedEventService.class);
        cacheExceptionManager = mock(CacheExceptionManager.class);

        cachedEventManager = new CachedEventManager(cachedEventMapper,
            cachedEventService, cacheExceptionManager);

        when(cachedEventMapper.toCacheableException(any(CachedEvent.class))).thenReturn(new CacheableException());
        when(cachedEventMapper.toCachedEvent(any(CacheableException.class))).thenReturn(CachedEventTest.getCachedEvent());

        when(cachedEventService.find(anyInt())).thenReturn(CachedEventTest.getCachedEvent());
        when(cachedEventService.save(any(CachedEvent.class))).thenReturn(CachedEventTest.getCachedEvent());

        nextRetryCycle = new Date();
        when(cacheExceptionManager.getNextExecutionTime()).thenReturn(nextRetryCycle);
    }

    @Test
    public void nextRetryCycleTest() {
        Date result = cachedEventManager.nextRetryCycle();

        assertThat(result).isEqualTo(nextRetryCycle);

        verify(cacheExceptionManager, times(1)).getNextExecutionTime();
    }

    @Test
    public void retryTest() throws ClassNotFoundException, IOException, RetryCycleLockException {
        CachedEvent result = cachedEventManager.retry(CachedEventTest.getCachedEvent().getId());

        assertThat(result).isEqualTo(CachedEventTest.getCachedEvent());

        verify(cachedEventMapper, times(1)).toCacheableException(any(CachedEvent.class));
        verify(cachedEventMapper, times(1)).toCachedEvent(any(CacheableException.class));

        verify(cachedEventService, times(1)).find(anyInt());
        verify(cachedEventService, times(0)).save(any(CachedEvent.class));

        verify(cacheExceptionManager, times(1)).retry(any(CacheableException.class));
    }
}
