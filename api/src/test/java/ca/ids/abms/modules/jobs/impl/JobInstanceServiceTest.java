package ca.ids.abms.modules.jobs.impl;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.jobs.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unused")
public class JobInstanceServiceTest {

    @Mock
    private ItemReader<FlightMovement> itemReader;

    @Mock
    private  ItemProcessor<FlightMovement> itemProcessor;

    @Mock
    private  ItemWriter<FlightMovement> itemWriter;

    private JobInstanceService jobInstanceService;

    @Mock
    private EntityManager entityManager;

    @Mock
    private JobInstanceRepository jobInstanceRepository;

    @Mock
    private JobExecutionRepository jobExecutionRepository;

    private JobParameters jobParameters = new JobParametersBuilder()
        .addParameter("user", "usr001", true)
        .addParameter("year", "2017", false)
        .addParameter("month", "03", false)
        .toJobParameters();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        doNothing().when(entityManager).flush();

        jobInstanceService = new JobInstanceService(entityManager, jobInstanceRepository, jobExecutionRepository, 1);

        when(jobInstanceRepository.save(any(JobInstance.class)))
            .thenAnswer((Answer<JobInstance>) invocation -> (JobInstance) invocation.getArguments()[0]);

        when(jobExecutionRepository.saveAndFlush(any(JobExecution.class)))
            .thenAnswer((Answer<JobExecution>) invocation -> {
                final JobExecution jobExecution = (JobExecution) invocation.getArguments()[0];
                jobExecution.setId(4);
                return jobExecution;
            });

        final JobExecution jobExecution = giveMeAnExecution();

        when(jobInstanceRepository.findOneByJobKey(anyString())).thenReturn(jobExecution.getJobInstance());
        when(jobExecutionRepository.findTop1ByJobInstanceIdOrderByCreatedAtDesc(eq(2))).thenReturn(jobExecution);
        when(jobExecutionRepository.findOne(eq(2))).thenReturn(jobExecution);
    }

    @Test
    public void testCreateJobInstanceByParameters() {
        when(jobInstanceRepository.findOneByJobKey(anyString())).thenReturn(null);

        final JobInstance newJobInstance = jobInstanceService.getOrCreateJobInstanceByParameters(JobType.BULK_RECALCULATION, jobParameters);
        assertThat(newJobInstance).isNotNull();
        assertThat(newJobInstance.getJobType()).isEqualTo(JobType.BULK_RECALCULATION);
        assertThat(newJobInstance.getJobStatus()).isEqualTo(JobStatus.WAITING);
        assertThat(newJobInstance.getJobKey()).isEqualTo(JobType.BULK_RECALCULATION.name()+':'+"usr001");

        verify(entityManager, never()).persist(any(JobInstance.class));
        verify(entityManager, never()).refresh(any(JobInstance.class), any(LockModeType.class));
        verify(entityManager, never()).persist(any(JobExecution.class));
        verify(entityManager, never()).refresh(any(JobExecution.class), any(LockModeType.class));
    }

    @Test
    public void testGetJobInstanceByParameters() {
        when(jobExecutionRepository.cancelAllBlockedExecutionsByJobId(eq(2), any(LocalDateTime.class))).thenReturn(0);
        when(jobExecutionRepository.getRunningExecutions(2)).thenReturn(0);

        final JobInstance jobInstance = jobInstanceService.getOrCreateJobInstanceByParameters(JobType.BULK_RECALCULATION, jobParameters);
        assertThat(jobInstance).isNotNull();
        assertThat(jobInstance.getId()).isEqualTo(2);
        assertThat(jobInstance.getJobType()).isEqualTo(JobType.BULK_RECALCULATION);
        assertThat(jobInstance.getJobStatus()).isEqualTo(JobStatus.WAITING);
        assertThat(jobInstance.getJobKey()).isEqualTo(JobType.BULK_RECALCULATION.name());

        verify(entityManager, times(1)).persist(any(JobInstance.class));
        verify(entityManager, times(1)).refresh(any(JobInstance.class), any(LockModeType.class));

        jobInstance.setJobStatus(JobStatus.RUNNING);
        when(jobInstanceRepository.findOneByJobKey(anyString())).thenReturn(jobInstance);
        when(jobExecutionRepository.getRunningExecutions(2)).thenReturn(1);
        try {
            jobInstanceService.getOrCreateJobInstanceByParameters(JobType.BULK_RECALCULATION, jobParameters);
            fail("Expected `JobAlreadyRunningException`.");
        } catch (JobAlreadyRunningException ignored) {
            // ignored
        }
    }

    @Test
    public void markJobAsCompleted() {
        final JobExecution jobExecution = giveMeAnExecution();

        when(jobExecutionRepository.findOne(eq(3))).thenReturn(jobExecution);

        jobInstanceService.markJobAsCompleted(3);

        assertThat(jobExecution.getJobExecutionStatus()).isEqualTo(JobExecutionStatus.COMPLETED);
        assertThat(jobExecution.getJobInstance().getJobStatus()).isEqualTo(JobStatus.WAITING);

        verify(entityManager, atLeastOnce()).persist(any(JobInstance.class));
        verify(entityManager, atLeastOnce()).refresh(any(JobInstance.class), any(LockModeType.class));
        verify(entityManager, atLeastOnce()).persist(any(JobExecution.class));
        verify(entityManager, atLeastOnce()).refresh(any(JobExecution.class), any(LockModeType.class));

    }

    @Test
    public void markJobAsQueued() {
        JobExecution jobExecution = jobInstanceService.markJobAsQueued(JobType.BULK_RECALCULATION, jobParameters);
        assertThat(jobExecution.getJobExecutionStatus()).isEqualTo(JobExecutionStatus.QUEUED);

        verify(jobExecutionRepository, times(1)).cancelAllCurrentExecutionsByJobId(2);
        verify(jobExecutionRepository, times(1)).saveAndFlush(any(JobExecution.class));
        verify(entityManager, atLeastOnce()).persist(any(JobInstance.class));
        verify(entityManager, atLeastOnce()).refresh(any(JobInstance.class), any(LockModeType.class));
    }

    @Test
    public void markJobAsStarted() {

        final JobExecution jobExecution = jobInstanceService.markJobAsStarted(JobType.BULK_RECALCULATION, jobParameters);
        assertThat(jobExecution.getJobExecutionStatus()).isEqualTo(JobExecutionStatus.STARTED);

        verify(jobExecutionRepository, times(1)).saveAndFlush(any(JobExecution.class));
    }

    @Test
    public void markJobAsCanceled() {
        jobInstanceService.markJobAsCanceled(JobType.BULK_RECALCULATION, jobParameters);

        verify(jobExecutionRepository, times(1)).cancelAllCurrentExecutionsByJobId(2);
        verify(entityManager, atLeastOnce()).persist(any(JobInstance.class));
        verify(entityManager, atLeastOnce()).refresh(any(JobInstance.class), any(LockModeType.class));
    }

    @Test
    public void updateJobExecution() {
        final StepSummary stepSummary = new StepSummary(5);
        stepSummary.increaseProcessed();
        stepSummary.increaseProcessed();
        stepSummary.increaseDiscarded();

        final JobExecution jobExecution = giveMeAnExecution();
        when(jobExecutionRepository.findOne(3)).thenReturn(jobExecution);

        jobInstanceService.updateJobExecution(3, stepSummary);

        assertThat(jobExecution.getTotalSteps()).isEqualTo(5);
        assertThat(jobExecution.getStepsCompleted()).isEqualTo(2);
        assertThat(jobExecution.getStepsAborted()).isEqualTo(1);
        assertThat(jobExecution.getStepsToProcess()).isEqualTo(2);
        assertThat(jobExecution.getJobExecutionStatus()).isEqualTo(JobExecutionStatus.STARTED);

        verify(entityManager, atLeastOnce()).persist(any(JobInstance.class));
        verify(entityManager, atLeastOnce()).refresh(any(JobInstance.class), any(LockModeType.class));

        stepSummary.increaseProcessed();
        stepSummary.increaseProcessed();

        jobInstanceService.updateJobExecution(3, stepSummary);

        assertThat(jobExecution.getStepsCompleted()).isEqualTo(4);
        assertThat(jobExecution.getStepsToProcess()).isEqualTo(0);
    }

    private JobExecution giveMeAnExecution () {
        final JobInstance existingInstance = new JobInstance();
        existingInstance.setId(2);
        existingInstance.setJobStatus(JobStatus.RUNNING);
        existingInstance.setJobType(JobType.BULK_RECALCULATION);
        existingInstance.setJobKey(JobType.BULK_RECALCULATION.name());

        final JobExecution jobExecution = new JobExecution();
        jobExecution.setJobExecutionStatus(JobExecutionStatus.STARTED);
        jobExecution.setId(3);
        jobExecution.setJobInstance(existingInstance);

        return jobExecution;
    }
}
