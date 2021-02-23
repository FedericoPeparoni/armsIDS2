package ca.ids.abms.modules.cachedevents;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Java6Assertions.assertThat;

@SuppressWarnings("FieldCanBeLocal")
public class CachedEventResultTest {

    private static String CLAZZ = "MockException.class";
    private static String RESULT = "My Mock Exception Result";
    private static LocalDateTime CREATED_AT = LocalDateTime.now();
    private static String CREATED_BY = "system";

    private CachedEventResult cachedEventResult;

    @Before
    public void setup() {
        cachedEventResult = getCachedEventResult();
    }

    @Test
    public void getterTest() {
        assertThat(cachedEventResult.getClazz()).isEqualTo(CLAZZ);
        assertThat(cachedEventResult.getResult()).isEqualTo(RESULT);
        assertThat(cachedEventResult.getCreatedAt()).isEqualTo(CREATED_AT);
        assertThat(cachedEventResult.getCreatedBy()).isEqualTo(CREATED_BY);
    }

    @Test
    public void toStringTest() {
        assertThat(cachedEventResult.toString()).isEqualTo(getCachedEventResult().toString());
    }

    @Test
    public void equalTest() {
        assertThat(cachedEventResult).isEqualTo(getCachedEventResult());
        assertThat(cachedEventResult).isEqualTo(cachedEventResult);
        assertThat(cachedEventResult).isNotEqualTo("mock string");

        CachedEventResult cachedEventResult1 = getCachedEventResult();
        CachedEventResult cachedEventResult2 = getCachedEventResult();
        cachedEventResult2.setClazz(CLAZZ + ".fail");

        assertThat(cachedEventResult1).isNotEqualTo(cachedEventResult2);

        cachedEventResult1.setClazz(null);
        cachedEventResult1.setResult(null);
        cachedEventResult1.setCreatedAt(null);
        cachedEventResult1.setCreatedBy(null);

        cachedEventResult2.setClazz(null);
        cachedEventResult2.setResult(null);
        cachedEventResult2.setCreatedAt(null);
        cachedEventResult2.setCreatedBy(null);

        assertThat(cachedEventResult1).isEqualTo(cachedEventResult2);
    }

    public static CachedEventResult getCachedEventResult() {
        CachedEventResult cachedEventResult = new CachedEventResult();
        cachedEventResult.setClazz(CLAZZ);
        cachedEventResult.setResult(RESULT);
        cachedEventResult.setCreatedAt(CREATED_AT);
        cachedEventResult.setCreatedBy(CREATED_BY);
        return cachedEventResult;
    }
}
