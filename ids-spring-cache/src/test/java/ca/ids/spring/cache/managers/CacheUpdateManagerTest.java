package ca.ids.spring.cache.managers;

import ca.ids.spring.cache.TriggerIntervalProvider;
import ca.ids.spring.cache.TriggerIntervalProviderImpl;
import ca.ids.spring.cache.exceptions.UpdateCycleLockException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.Date;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CacheUpdateManagerTest {

    private CacheUpdateManager cacheUpdateManager;

    private Runnable mockRunnable;

    private TriggerIntervalProvider triggerIntervalProvider;

    @Before
    public void setup() {

        // define mock trigger interval provider
        triggerIntervalProvider = mock(TriggerIntervalProvider.class);

        // mock trigger interval provider getters
        when(triggerIntervalProvider.getInterval()).thenReturn(TriggerIntervalProviderImpl.DEFAULT_INTERVAL);
        when(triggerIntervalProvider.getIntervalUnit()).thenReturn(TriggerIntervalProviderImpl.DEFAULT_INTERVAL_UNIT);

        // initialize new cache update manager
        cacheUpdateManager = new CacheUpdateManager(triggerIntervalProvider);

        // define a mock callable class
        mockRunnable = mockRunnable();

        // register mock callable class
        cacheUpdateManager.registerMethod(mockRunnable);
    }

    @Test
    public void CacheExceptionManagerTest() {
        CacheUpdateManager cacheUpdateManager = new CacheUpdateManager();
        assertThat(cacheUpdateManager.getTriggerIntervalProvider())
            .isInstanceOf(TriggerIntervalProviderImpl.class);
    }

    @Test
    public void nextExecutionTimeTest() {
        ScheduledTaskRegistrar scheduledTaskRegistrar = new ScheduledTaskRegistrar();
        cacheUpdateManager.configureTasks(scheduledTaskRegistrar);
        Trigger trigger = scheduledTaskRegistrar.getTriggerTaskList().get(0).getTrigger();

        TriggerContext triggerContext = mock(TriggerContext.class);
        Date result = trigger.nextExecutionTime(triggerContext);

        assertThat(result).isEqualTo(cacheUpdateManager.getNextExecutionTime());
    }

    @Test
    public void doUpdateCache_ResultTest() throws UpdateCycleLockException {

        // call for cache update
        cacheUpdateManager.runUpdateCycle();

        // verify that mock runnable run method was called once
        verify(mockRunnable, times(1)).run();
    }

    @Test
    public void doUpdateCache_ExceptionTest() throws UpdateCycleLockException {

        // set mock runnable to throw error on run invocation
        doThrow(new RuntimeException()).when(mockRunnable).run();

        // call for cache update
        cacheUpdateManager.runUpdateCycle();

        // verify that mock runnable run method was called once
        verify(mockRunnable, times(1)).run();
    }

    @Test
    public void doUpdateCache_UpdateCycleLockErrorTest() {

        // force exception on runnable to validate if try / finally is releasing lock
        doThrow(new RuntimeException()).when(mockRunnable).run();

        // attempt to run update cycle with error
        try {
            cacheUpdateManager.runUpdateCycle();
        } catch (Throwable ignored) {}

        // verify that update cycle lock was checked, set and released
        verify(triggerIntervalProvider, times(1)).isLocked();
        verify(triggerIntervalProvider, times(1)).setLock(true);
        verify(triggerIntervalProvider, times(1)).setLock(false);
    }

    @Test
    public void doUpdateCache_UpdateCycleLockNullTest() {

        // force exception on runnable to validate if try / finally is releasing lock
        doThrow(new RuntimeException()).when(mockRunnable).run();

        // attempt to run update on null to force error
        try {
            cacheUpdateManager.update(null);
        } catch (Throwable ignored) {}

        // verify that update cylce lock was checked, set and released
        verify(triggerIntervalProvider, times(1)).isLocked();
        verify(triggerIntervalProvider, times(1)).setLock(true);
        verify(triggerIntervalProvider, times(1)).setLock(false);
    }

    @SuppressWarnings("unchecked")
    private Runnable mockRunnable() {
        return mock(Runnable.class);
    }

}
