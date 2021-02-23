package ca.ids.abms.modules.locking;

import ca.ids.abms.modules.jobs.JobStatus;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class JobLockingTest {

    private JobLockingRepository jobLockingRepository;

    private JobLockingService jobLockingService;

    private final String processId = "test";

    @Before
    public void setup() {
        EntityManager entityManager = mock(EntityManager.class);
        jobLockingRepository = mock(JobLockingRepository.class);

        when(jobLockingRepository.findOne(eq(JobLockingTest.class.getName()))).thenReturn(getMockJob());

        jobLockingService = new JobLockingService(entityManager, jobLockingRepository, 10);
    }

    @Test
    public void destroyWaitingTest() {

        final JobLocking jobLocking = getMockJob();
        jobLocking.setStatus(JobStatus.WAITING);
        when(jobLockingRepository.findOne(eq(this.getClass().getName()))).thenReturn(jobLocking);

        jobLockingService.doResetLocksByProcessId(processId);

        verify(jobLockingRepository, times(0)).save(any(JobLocking.class));
    }

    @Test
    public void destoryRunningTest() {

        final JobLocking jobLocking = getMockJob();
        jobLocking.setStatus(JobStatus.RUNNING);

        final List<JobLocking> jobs = new ArrayList<>();
        jobs.add(jobLocking);
        when(jobLockingRepository.findAllByProcessId(eq(processId))).thenReturn(jobs);

        jobLockingService.doResetLocksByProcessId(processId);

        verify(jobLockingRepository, times(1)).save(jobLocking);
    }

    @Test
    public void destroyNoneTest() {
        when(jobLockingRepository.findAll()).thenReturn(null);

        jobLockingService.doResetLocksByProcessId(processId);

        verify(jobLockingRepository, times(0)).save(any(JobLocking.class));
    }

    @Test
    public void destroyExceptionTest() {
        when(jobLockingRepository.save(any(JobLocking.class)))
            .thenThrow(new RuntimeException("Save on destroy failed..."));

        try {
            jobLockingService.doResetLocksByProcessId(processId);
        } catch (Exception ex) {
            fail("FPLSchedulerService.destroy() should NOT return any exceptions " +
                "but the following exception was thrown: {}", ex);
        }
    }

    private JobLocking getMockJob() {
        JobLocking jobLocking = new JobLocking();
        jobLocking.setServiceId(JobLockingTest.class.getName());
        jobLocking.setProcessId(processId);
        jobLocking.setStatus(JobStatus.WAITING);
        return jobLocking;
    }
}
