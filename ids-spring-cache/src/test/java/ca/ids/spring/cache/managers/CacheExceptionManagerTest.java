package ca.ids.spring.cache.managers;

import ca.ids.spring.cache.*;
import ca.ids.spring.cache.exceptions.RetryCycleLockException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CacheExceptionManagerTest {

    private ApplicationContext applicationContext;

    private CacheableException cacheableException;

    private CacheableExceptionProvider cacheableExceptionProvider;

    private CacheExceptionManager cacheExceptionManager;

    private TriggerIntervalProvider triggerIntervalProvider;

    @Before
    public void setup() {

        // define mock application context
        applicationContext = mock(ApplicationContext.class);

        // initialize new cacheable exception array
        cacheableException = CacheableExceptionTest.getCacheableException();
        cacheableException.setResults(null);

        // define mock cacheable exception provider
        cacheableExceptionProvider = mock(CacheableExceptionProviderImpl.class);

        // mock cacheable exception provider when findAll invoked
        when(cacheableExceptionProvider.findAll()).thenReturn(Collections.singletonList(cacheableException));
        when(cacheableExceptionProvider.handleException(any(), any(Throwable.class), anyBoolean())).thenReturn(true);

        // define mock trigger interval provider
        triggerIntervalProvider = mock(TriggerIntervalProvider.class);

        // mock trigger interval provider getters
        when(triggerIntervalProvider.getInterval()).thenReturn(TriggerIntervalProviderImpl.DEFAULT_INTERVAL);
        when(triggerIntervalProvider.getIntervalUnit()).thenReturn(TriggerIntervalProviderImpl.DEFAULT_INTERVAL_UNIT);

        // initialize new cache update manager
        cacheExceptionManager = new CacheExceptionManager(cacheableExceptionProvider, triggerIntervalProvider);

        // mock application context
        cacheExceptionManager.setApplicationContext(applicationContext);
    }

    @Test
    public void cacheExceptionManagerTest() {
        CacheExceptionManager result = new CacheExceptionManager();
        assertThat(result.getCacheableExceptionProvider())
            .isInstanceOf(CacheableExceptionProviderImpl.class);
        assertThat(result.getTriggerIntervalProvider())
            .isInstanceOf(TriggerIntervalProviderImpl.class);

        result = new CacheExceptionManager(mock(TriggerIntervalProvider.class));
        assertThat(result.getCacheableExceptionProvider())
            .isInstanceOf(CacheableExceptionProviderImpl.class);

        result = new CacheExceptionManager(mock(CacheableExceptionProvider.class));
        assertThat(result.getTriggerIntervalProvider())
            .isInstanceOf(TriggerIntervalProviderImpl.class);
    }

    @Test
    public void cacheableExceptionProviderTest() {
        CacheableExceptionProvider result = cacheExceptionManager.getCacheableExceptionProvider();
        assertThat(result).isEqualTo(cacheableExceptionProvider);
    }

    @Test
    public void triggerIntervalProviderTest() {
        TriggerIntervalProvider result = cacheExceptionManager.getTriggerIntervalProvider();
        assertThat(result).isEqualTo(triggerIntervalProvider);
    }

    @Test
    public void nextExecutionTimeTest() {
        ScheduledTaskRegistrar scheduledTaskRegistrar = new ScheduledTaskRegistrar();
        cacheExceptionManager.configureTasks(scheduledTaskRegistrar);
        Trigger trigger = scheduledTaskRegistrar.getTriggerTaskList().get(0).getTrigger();

        TriggerContext triggerContext = mock(TriggerContext.class);
        Date result = trigger.nextExecutionTime(triggerContext);

        assertThat(result).isEqualTo(cacheExceptionManager.getNextExecutionTime());
    }

    @Test
    public void doUpdateCache_ResultTest() throws ReflectiveOperationException, RetryCycleLockException {

        // mock necessary application context method
        mockApplicationContext(applicationContext, cacheableException);

        // call for cache update
        cacheExceptionManager.runRetryCycle();

        // verify that mock cacheable exception provider update method was NOT called
        verify(cacheableExceptionProvider, times(1)).update(eq(cacheableException));

        // verify that mock cacheable exception provider delete method was called once
        verify(cacheableExceptionProvider, times(1)).remove(eq(cacheableException));
    }

    @Test
    public void doUpdateCache_InvocationTargetExceptionTest() throws ReflectiveOperationException, RetryCycleLockException {

        // override args and methodName to force exception within target invocation
        // this will cause an IndexOutOfBoundsException when substring is invoked
        cacheableException.setMethodName("substring");
        cacheableException.setParamTypes(new Class[] { int.class, int.class });
        cacheableException.setArgs(new Integer[] { 2, 1 });
        cacheableException.setExceptions(new Class[]{ StringIndexOutOfBoundsException.class });

        // mock necessary application context method
        mockApplicationContext(applicationContext, cacheableException);

        // call for cache update
        cacheExceptionManager.runRetryCycle();

        // verify that mock cacheable exception provider update method was called
        verify(cacheableExceptionProvider, times(1)).update(eq(cacheableException));

        // verify that mock cacheable exception provider delete method was NOT called
        verify(cacheableExceptionProvider, times(0)).remove(eq(cacheableException));
    }

    @Test
    public void doUpdateCache_InvocationTargetExceptionAllTest() throws ReflectiveOperationException, RetryCycleLockException {

        // override args and methodName to force exception within target invocation
        // this will cause an IndexOutOfBoundsException when substring is invoked
        cacheableException.setMethodName("substring");
        cacheableException.setParamTypes(new Class[] { int.class, int.class });
        cacheableException.setArgs(new Integer[] { 2, 1 });
        cacheableException.setExceptions(new Class[]{ });

        // mock necessary application context method
        mockApplicationContext(applicationContext, cacheableException);

        // call for cache update
        cacheExceptionManager.runRetryCycle();

        // verify that mock cacheable exception provider update method was called
        verify(cacheableExceptionProvider, times(1)).update(eq(cacheableException));

        // verify that mock cacheable exception provider delete method was NOT called
        verify(cacheableExceptionProvider, times(0)).remove(eq(cacheableException));
    }

    @Test
    public void doUpdateCache_InvocationTargetExceptionRemoveTest() throws ReflectiveOperationException, RetryCycleLockException {

        // override args and methodName to force exception within target invocation
        // this will cause an IndexOutOfBoundsException when substring is invoked
        cacheableException.setMethodName("substring");
        cacheableException.setParamTypes(new Class[] { int.class, int.class });
        cacheableException.setArgs(new Integer[] { 2, 1 });
        cacheableException.setExceptions(new Class[]{ NullPointerException.class });

        // mock necessary application context method
        mockApplicationContext(applicationContext, cacheableException);

        // define exception handler results, false to skip and remove from retry cycle
        when(cacheableExceptionProvider.handleException(any(), any(Throwable.class), anyBoolean())).thenReturn(false);

        // call for cache update
        cacheExceptionManager.runRetryCycle();

        // verify that mock cacheable exception provider update method was called
        verify(cacheableExceptionProvider, times(1)).update(eq(cacheableException));

        // verify that mock cacheable exception provider delete method was NOT called
        verify(cacheableExceptionProvider, times(1)).remove(eq(cacheableException));
    }

    @Test
    public void doUpdateCache_ExceptionTest() throws ReflectiveOperationException, RetryCycleLockException {

        // override args to force exception with illegal parameter types
        // this will cause an IllegalArgumentException when compareTo is invoked
        cacheableException.setArgs(new Integer[] { 1 });

        // mock necessary application context method
        mockApplicationContext(applicationContext, cacheableException);

        // call for cache update
        cacheExceptionManager.runRetryCycle();

        // verify that mock cacheable exception provider update method was called
        verify(cacheableExceptionProvider, times(1)).update(eq(cacheableException));

        // verify that mock cacheable exception provider delete method was called
        verify(cacheableExceptionProvider, times(1)).remove(eq(cacheableException));
    }

    @Test
    public void doUpdateCache_RetryCycleLockErrorTest() {

        // force exception on provider to validate if try / finally is releasing lock
        when(cacheableExceptionProvider.findAll()).thenThrow(new RuntimeException());

        // attempt to run retry cycle with findAll error
        try {
            cacheExceptionManager.runRetryCycle();
        } catch (Throwable ignored) {}

        // verify that retry cycle lock was checked, set and released
        verify(triggerIntervalProvider, times(1)).isLocked();
        verify(triggerIntervalProvider, times(1)).setLock(true);
        verify(triggerIntervalProvider, times(1)).setLock(false);
    }

    @Test
    public void doUpdateCache_RetryCycleLockNullTest() {

        // force exception on provider to validate if try / finally is releasing lock
        when(cacheableExceptionProvider.findAll()).thenThrow(new RuntimeException());

        // attempt to run retry on null to force error
        try {
            cacheExceptionManager.retry(null);
        } catch (Throwable ignored) {}

        // verify that retry cycle lock was checked, set and released
        verify(triggerIntervalProvider, times(1)).isLocked();
        verify(triggerIntervalProvider, times(1)).setLock(true);
        verify(triggerIntervalProvider, times(1)).setLock(false);
    }

    @SuppressWarnings(value = {"unchecked"})
    private void mockApplicationContext(ApplicationContext applicationContext, CacheableException cacheableException)
        throws ReflectiveOperationException {
        when(applicationContext.getBean(any(Class.class)))
            .thenReturn(cacheableException.getTarget().newInstance());
    }
}
