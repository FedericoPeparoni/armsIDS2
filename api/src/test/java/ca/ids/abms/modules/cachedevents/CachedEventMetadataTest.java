package ca.ids.abms.modules.cachedevents;

import ca.ids.abms.modules.cachedevents.enumerators.CachedEventAction;
import ca.ids.abms.modules.cachedevents.enumerators.CachedEventType;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

@SuppressWarnings("FieldCanBeLocal")
public class CachedEventMetadataTest {

    private static CachedEventType CACHED_EVENT_TYPE = CachedEventType.SQL;
    private static CachedEventAction CACHED_EVENT_ACTION = CachedEventAction.INSERT;
    private static String RESOURCE = "mock.resource";
    private static String STATEMENT = "INSERT INTO mock.resource (value) VALUES ('value')";

    private CachedEventMetadata cachedEventMetadata;

    @Before
    public void setup() {
        cachedEventMetadata = getCachedEventMetadata();
    }

    @Test
    public void getterTest() {
        assertThat(cachedEventMetadata.getType()).isEqualTo(CACHED_EVENT_TYPE);
        assertThat(cachedEventMetadata.getAction()).isEqualTo(CACHED_EVENT_ACTION);
        assertThat(cachedEventMetadata.getResource()).isEqualTo(RESOURCE);
        assertThat(cachedEventMetadata.getStatement()).isEqualTo(STATEMENT);
    }

    @Test
    public void toStringTest() {
        assertThat(cachedEventMetadata.toString()).isEqualTo(getCachedEventMetadata().toString());
    }

    @Test
    public void equalTest() {
        assertThat(cachedEventMetadata).isEqualTo(getCachedEventMetadata());
        assertThat(cachedEventMetadata).isEqualTo(cachedEventMetadata);
        assertThat(cachedEventMetadata).isNotEqualTo("mock string");

        CachedEventMetadata cachedEventMetadata1 = getCachedEventMetadata();
        CachedEventMetadata cachedEventMetadata2 = getCachedEventMetadata();
        cachedEventMetadata2.setResource(RESOURCE + ".fail");

        assertThat(cachedEventMetadata1).isNotEqualTo(cachedEventMetadata2);

        cachedEventMetadata1.setType(null);
        cachedEventMetadata1.setAction(null);
        cachedEventMetadata1.setResource(null);
        cachedEventMetadata1.setStatement(null);

        cachedEventMetadata2.setType(null);
        cachedEventMetadata2.setAction(null);
        cachedEventMetadata2.setResource(null);
        cachedEventMetadata2.setStatement(null);

        assertThat(cachedEventMetadata1).isEqualTo(cachedEventMetadata2);
    }

    public static CachedEventMetadata getCachedEventMetadata() {
        CachedEventMetadata cachedEventMetadata = new CachedEventMetadata();
        cachedEventMetadata.setType(CACHED_EVENT_TYPE);
        cachedEventMetadata.setAction(CACHED_EVENT_ACTION);
        cachedEventMetadata.setResource(RESOURCE);
        cachedEventMetadata.setStatement(STATEMENT);
        return cachedEventMetadata;
    }
}
