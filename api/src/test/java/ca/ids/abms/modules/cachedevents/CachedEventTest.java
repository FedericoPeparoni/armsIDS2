package ca.ids.abms.modules.cachedevents;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

@SuppressWarnings("FieldCanBeLocal")
public class CachedEventTest {

    private static int ID = 1;
    private static String TARGET = "mock.target";
    private static String METHOD_NAME = "mockMethodName";
    private static List<String> PARAM_TYPES = Collections.singletonList("mockParamType");
    private static List<byte[]> ARGS = Collections.singletonList("mockArg".getBytes());
    private static List<String> EXCEPTIONS = Collections.singletonList("MockException.class");
    private static List<String> CACHES = Collections.singletonList("mock-cache");
    private static Boolean RETRY = true;
    private static Integer RETRY_COUNT = 5;

    private CachedEvent cachedEvent;

    @Before
    public void setup() {
        cachedEvent = getCachedEvent();
    }

    @Test
    public void getterTest() {
        assertThat(cachedEvent.getId()).isEqualTo(ID);
        assertThat(cachedEvent.getTarget()).isEqualTo(TARGET);
        assertThat(cachedEvent.getMethodName()).isEqualTo(METHOD_NAME);
        assertThat(cachedEvent.getParamTypes()).isEqualTo(PARAM_TYPES);
        assertThat(cachedEvent.getArgs()).isEqualTo(ARGS);
        assertThat(cachedEvent.getExceptions()).isEqualTo(EXCEPTIONS);
        assertThat(cachedEvent.getCaches()).isEqualTo(CACHES);
        assertThat(cachedEvent.getResults()).isEqualTo(getCachedEventResults());
        assertThat(cachedEvent.getMetadata()).isEqualTo(getCachedEventMetadata());
        assertThat(cachedEvent.getRetry()).isEqualTo(RETRY);
        assertThat(cachedEvent.getLastAttempt()).isEqualTo(cachedEvent.getCreatedAt());
        assertThat(cachedEvent.getRetryCount()).isEqualTo(RETRY_COUNT);
    }

    @Test
    public void addThrownResultTest() {
        cachedEvent.setResults(new ArrayList<>());
        cachedEvent.addThrownResult(new Exception("Mock Testing Exception"));
        assertThat(cachedEvent.getResults().size()).isEqualTo(1);
    }

    @Test
    public void onCreateTest() {
        cachedEvent.onCreate();
        assertThat(cachedEvent.getResults().get(0).getCreatedAt()).isEqualTo(cachedEvent.getCreatedAt());
        assertThat(cachedEvent.getResults().get(0).getCreatedBy()).isEqualTo(cachedEvent.getCreatedBy());
    }

    @Test
    public void onPersistTest() {
        cachedEvent.getResults().get(0).setCreatedAt(null);
        cachedEvent.getResults().get(0).setCreatedBy(null);
        cachedEvent.onPersist();
        assertThat(cachedEvent.getResults().get(0).getCreatedAt()).isEqualTo(cachedEvent.getUpdatedAt());
        assertThat(cachedEvent.getResults().get(0).getCreatedBy()).isEqualTo(cachedEvent.getUpdatedBy());
    }

    @Test
    public void toStringTest() {
        assertThat(cachedEvent.toString()).isEqualTo(getCachedEvent().toString());
    }

    @Test
    public void equalTest() {
        assertThat(cachedEvent).isEqualTo(getCachedEvent());
        assertThat(cachedEvent).isEqualTo(cachedEvent);
        assertThat(cachedEvent).isNotEqualTo("mock string");

        CachedEvent cachedEvent1 = getCachedEvent();
        CachedEvent cachedEvent2 = getCachedEvent();
        cachedEvent2.setId(0);

        assertThat(cachedEvent1).isNotEqualTo(cachedEvent2);

        cachedEvent1.setId(null);
        cachedEvent2.setId(null);

        assertThat(cachedEvent1).isEqualTo(cachedEvent2);
    }

    static CachedEvent getCachedEvent() {
        CachedEvent cachedEvent = new CachedEvent();
        cachedEvent.setId(ID);
        cachedEvent.setTarget(TARGET);
        cachedEvent.setMethodName(METHOD_NAME);
        cachedEvent.setParamTypes(PARAM_TYPES);
        cachedEvent.setArgs(ARGS);
        cachedEvent.setExceptions(EXCEPTIONS);
        cachedEvent.setCaches(CACHES);
        cachedEvent.setResults(getCachedEventResults());
        cachedEvent.setMetadata(getCachedEventMetadata());
        cachedEvent.setRetry(RETRY);
        cachedEvent.setRetryCount(RETRY_COUNT);
        return cachedEvent;
    }

    private static List<CachedEventResult> getCachedEventResults() {
        List<CachedEventResult> result = new ArrayList<>();
        result.add(CachedEventResultTest.getCachedEventResult());
        return result;
    }

    private static List<CachedEventMetadata> getCachedEventMetadata() {
        List<CachedEventMetadata> result = new ArrayList<>();
        result.add(CachedEventMetadataTest.getCachedEventMetadata());
        return result;
    }
}
