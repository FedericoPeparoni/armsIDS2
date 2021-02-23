package ca.ids.abms.modules.cachedevents;

import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class CachedEventServiceTest {

    private CachedEventRepository cachedEventRepository;
    private CachedEventService cachedEventService;

    private Page<CachedEvent> page;

    @Before
    public void setup() {
        cachedEventRepository = mock(CachedEventRepository.class);
        cachedEventService = new CachedEventService(cachedEventRepository);

        page = mock(Page.class);

        when(cachedEventRepository.findOne(anyInt())).thenReturn(CachedEventTest.getCachedEvent());
        when(cachedEventRepository.findAll()).thenReturn(Collections.singletonList(CachedEventTest.getCachedEvent()));
        when(cachedEventRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(cachedEventRepository.findByRetryOrderByIdAsc(anyBoolean())).thenReturn(Collections.singletonList(CachedEventTest.getCachedEvent()));
        when(cachedEventRepository.saveAndFlush(any(CachedEvent.class))).thenReturn(CachedEventTest.getCachedEvent());
    }

    @Test
    public void findTest() {
        CachedEvent cachedEvent = cachedEventService.find(CachedEventTest.getCachedEvent().getId());
        assertThat(cachedEvent).isEqualTo(CachedEventTest.getCachedEvent());
        verify(cachedEventRepository, times(1)).findOne(anyInt());
    }

    @Test
    public void findAllTest() {
        List<CachedEvent> results = cachedEventService.findAll();
        assertThat(results.size()).isEqualTo(1);
        verify(cachedEventRepository, times(1)).findAll();
    }

    @Test
    public void findAllPageableTest() {
        Page<CachedEvent> results = cachedEventService.findAll("", null);
        assertThat(results).isEqualTo(page);
        verify(cachedEventRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void findByRetryTest() {
        List<CachedEvent> results = cachedEventService.findByRetry(true);
        assertThat(results.size()).isEqualTo(1);
        verify(cachedEventRepository, times(1)).findByRetryOrderByIdAsc(anyBoolean());
    }

    @Test
    public void removeTest() {
        cachedEventService.remove(1);
        cachedEventService.remove(CachedEventTest.getCachedEvent());
        verify(cachedEventRepository, times(2)).findOne(anyInt());
        verify(cachedEventRepository, times(1)).delete(anyInt());
        verify(cachedEventRepository, times(1)).delete(any(CachedEvent.class));
    }

    @Test
    public void saveTest() {
        CachedEvent result = cachedEventService.save(CachedEventTest.getCachedEvent());
        assertThat(result).isEqualTo(CachedEventTest.getCachedEvent());
    }
}
