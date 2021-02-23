package ca.ids.spring.cache;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class TriggerIntervalProviderTest {

    private static long INTERVAL = 5;
    private static TimeUnit INTERVAL_UNIT = TimeUnit.HOURS;

    private TriggerIntervalProvider triggerIntervalProvider;

    @Before
    public void setup() {
        triggerIntervalProvider = getTriggerIntervalProvider();
    }

    @Test
    public void triggerIntervalProviderTest() {
        TriggerIntervalProvider result = new TriggerIntervalProviderImpl();
        assertThat(result.getInterval()).isEqualTo(TriggerIntervalProviderImpl.DEFAULT_INTERVAL);
        assertThat(result.getIntervalUnit()).isEqualTo(TriggerIntervalProviderImpl.DEFAULT_INTERVAL_UNIT);
        assertThat(result.isLocked()).isEqualTo(false);

        result = new TriggerIntervalProviderImpl(INTERVAL);
        assertThat(result.getInterval()).isEqualTo(INTERVAL);
        assertThat(result.getIntervalUnit()).isEqualTo(TriggerIntervalProviderImpl.DEFAULT_INTERVAL_UNIT);
        assertThat(result.isLocked()).isEqualTo(false);

        result = new TriggerIntervalProviderImpl(INTERVAL, INTERVAL_UNIT);
        assertThat(result.getInterval()).isEqualTo(INTERVAL);
        assertThat(result.getIntervalUnit()).isEqualTo(INTERVAL_UNIT);
        assertThat(result.isLocked()).isEqualTo(false);
    }

    @Test
    public void getterSetterTest() {
        assertThat(triggerIntervalProvider.getInterval()).isEqualTo(INTERVAL);
        assertThat(triggerIntervalProvider.getIntervalUnit()).isEqualTo(INTERVAL_UNIT);
        assertThat(triggerIntervalProvider.isLocked()).isEqualTo(false);

        TriggerIntervalProviderImpl result = new TriggerIntervalProviderImpl();

        // assert that interval is being set correctly
        result.setInterval(INTERVAL);
        assertThat(result.getInterval()).isEqualTo(INTERVAL);

        // assert that lock is being set correctly
        result.setLock(true);
        assertThat(result.isLocked()).isEqualTo(true);
        result.setLock(false);
        assertThat(result.isLocked()).isEqualTo(false);
    }

    private TriggerIntervalProvider getTriggerIntervalProvider() {
        TriggerIntervalProviderImpl result = new TriggerIntervalProviderImpl();
        result.setInterval(INTERVAL, INTERVAL_UNIT);
        return result;
    }
}
