package ca.ids.abms.modules.cachedevents;

import ca.ids.abms.modules.jobs.JobAlreadyRunningException;
import ca.ids.abms.modules.locking.JobLockingService;
import ca.ids.abms.modules.system.SystemConfigurationService;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class CachedEventTriggerTest {

    private CachedEventTrigger cachedEventTrigger;

    private JobLockingService jobLockingService;

    private SystemConfigurationService systemConfigurationService;

    @Before
    public void setup() {
        jobLockingService = mock(JobLockingService.class);

        systemConfigurationService = mock(SystemConfigurationService.class);

        cachedEventTrigger = new CachedEventTrigger(jobLockingService, systemConfigurationService);

        when(systemConfigurationService.getValue(anyString())).thenReturn(String.valueOf(CachedEventTrigger.DEFAULT_INTERVAL));
    }

    @Test
    public void getIntervalTest() {
        long result = cachedEventTrigger.getInterval();

        assertThat(result).isEqualTo(CachedEventTrigger.DEFAULT_INTERVAL);

        verify(systemConfigurationService, times(1)).getValue(anyString());
    }

    @Test
    public void getIntervalDefaultTest() {
        // mock bad system configuration data
        when(systemConfigurationService.getValue(anyString())).thenReturn("a");

        long result = cachedEventTrigger.getInterval();

        assertThat(result).isEqualTo(CachedEventTrigger.DEFAULT_INTERVAL);

        verify(systemConfigurationService, times(1)).getValue(anyString());
    }

    @Test
    public void getIntervalUnit() {
        TimeUnit result = cachedEventTrigger.getIntervalUnit();

        assertThat(result).isEqualTo(CachedEventTrigger.DEFAULT_INTERVAL_UNIT);
    }

    @Test
    public void isLockedTrueTest() {
        // assert that true is returned when no exception
        assertThat(cachedEventTrigger.isLocked()).isEqualTo(false);
    }

    @Test
    public void isLockedFalseTest() {
        // assert that false is returned when JobAlreadyRunningException
        doThrow(JobAlreadyRunningException.class).when(jobLockingService)
            .check(anyString(), anyString(), anyBoolean());
        assertThat(cachedEventTrigger.isLocked()).isEqualTo(true);
    }

    @Test
    public void isLockedExceptionTest() {
        // assert that exception is thrown when not JobAlreadyRunningException
        doThrow(RuntimeException.class).when(jobLockingService)
            .check(anyString(), anyString());
        try {
            cachedEventTrigger.isLocked();
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (Throwable ignored) {
            // ignored as exception expected
        }
    }

    @Test
    public void setLockFalseTest() {
        // verify that complete method was run only once when passing false
        // and check was not run at all when passing false
        cachedEventTrigger.setLock(false);
        verify(jobLockingService, times(0))
            .check(anyString(), anyString());
        verify(jobLockingService, times(1))
            .complete(anyString(), anyString());
    }

    @Test
    public void setLockTrueTest() {
        // verify that complete and check aren't called at all when passing true
        // check is handled inside the isLocked method
        cachedEventTrigger.setLock(true);
        verify(jobLockingService, times(0))
            .check(anyString(), anyString());
        verify(jobLockingService, times(0))
            .complete(anyString(), anyString());
    }
}
