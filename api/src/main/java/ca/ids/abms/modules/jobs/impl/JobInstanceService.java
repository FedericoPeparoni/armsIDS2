package ca.ids.abms.modules.jobs.impl;

import ca.ids.abms.modules.jobs.*;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
@SuppressWarnings("WeakerAccess")
public class JobInstanceService {

    private static final Logger LOG = LoggerFactory.getLogger(JobInstanceService.class);

    private final EntityManager entityManager;

    private final JobInstanceRepository jobInstanceRepository;

    private final JobExecutionRepository jobExecutionRepository;

    private Integer timeoutInMinutes;

    JobInstanceService(final EntityManager entityManager,
                       final JobInstanceRepository jobInstanceRepository,
                       final JobExecutionRepository jobExecutionRepository,
                       @Value("${abms.jobs.timeout.minutes}") final Integer timeoutInMinutes) {
        this.entityManager = entityManager;
        this.jobInstanceRepository = jobInstanceRepository;
        this.jobExecutionRepository = jobExecutionRepository;
        this.timeoutInMinutes = timeoutInMinutes;
    }

    @Transactional(readOnly = true)
    public JobInstance getJobInstanceByParameters (final JobType jobType, final JobParameters jobParameters) {
        final String key = this.generateJobInstanceKey(jobType, jobParameters);
        return jobInstanceRepository.findOneByJobKey(key);
    }

    public JobInstance getOrCreateJobInstanceByParameters (final JobType jobType,
                                                          final JobParameters jobParameters) {
        final String key = this.generateJobInstanceKey(jobType, jobParameters);
        JobInstance jobInstance = jobInstanceRepository.findOneByJobKey(key);
        if (jobInstance == null) {
            final JobInstance newJobInstance = new JobInstance();
            newJobInstance.setJobKey(key);
            newJobInstance.setJobName(jobType.name());
            newJobInstance.setJobType(jobType);
            newJobInstance.setJobStatus(JobStatus.WAITING);
            try {
                jobInstance = jobInstanceRepository.save(newJobInstance);
            } catch (RuntimeException re) {
                LOG.warn("Cannot create a job {} because: {}", key, re.getLocalizedMessage());
            }
        } else if (jobInstance.getJobStatus().equals(JobStatus.RUNNING)) {
            boolean alreadyRunning = hasValidRunningExecutions(jobInstance);
            if (alreadyRunning) {
                throw new JobAlreadyRunningException("Already running the Job with ID" + " " + jobInstance.getId());
            }
        }
        return jobInstance;
    }

    @Transactional(readOnly = true)
    public JobExecution getJobExecutionByJobInstanceId(final Integer jobInstanceId) {
        return jobExecutionRepository.findTop1ByJobInstanceIdOrderByCreatedAtDesc(jobInstanceId);
    }

    public void markJobAsCompleted(final Integer jobExecutionId) {
        markJobAsCompleted(jobExecutionId, JobExecutionStatus.COMPLETED);
    }

    public void markJobAsCompleted(final Integer jobExecutionId, final JobExecutionStatus targetStatus) {
        final JobExecution jobExecution = jobExecutionRepository.findOne(jobExecutionId);
        if (jobExecution != null) {
            doLock(jobExecution);

            if (jobExecution.getJobExecutionStatus().equals(JobExecutionStatus.STARTED)) {
                jobExecution.setJobExecutionStatus(targetStatus);
                jobExecution.setStopTime(LocalDateTime.now());
            }
            doPersistAndRelease(jobExecution);

            final JobInstance jobInstance = jobExecution.getJobInstance();
            doLock(jobInstance);
            if (jobInstance.getJobStatus().equals(JobStatus.RUNNING)) {
                jobInstance.setJobStatus(JobStatus.WAITING);
            }
            doPersistAndRelease(jobInstance);

            LOG.debug("The Job ID {}, key {}, execution {}, has completed", jobInstance.getId(), jobInstance.getJobKey(),
                jobExecutionId);
        }
    }

    public void markJobAsCanceled(final JobType jobType, final JobParameters jobParameters) {
        final JobInstance jobInstance = getJobInstanceByParameters(jobType, jobParameters);
        if (jobInstance != null) {
            jobExecutionRepository.cancelAllCurrentExecutionsByJobId(jobInstance.getId());
            doLock(jobInstance);
            if (jobInstance.getJobStatus().equals(JobStatus.RUNNING)) {
                jobInstance.setJobStatus(JobStatus.WAITING);
            }
            doPersistAndRelease(jobInstance);
            LOG.debug("The Job ID {}, key {}, has been canceled", jobInstance.getId(), jobInstance.getJobKey());
        }
    }

    public JobExecution markJobAsQueued(final JobType jobType, final JobParameters jobParameters) {

        // create job instance if one does not already exist and clear all existing executions
        // JobAlreadyRunningException will be thrown if job is already running with another service
        final JobInstance jobInstance = getOrCreateJobInstanceByParameters(jobType, jobParameters);
        if (jobInstance == null) return null;

        jobExecutionRepository.cancelAllCurrentExecutionsByJobId(jobInstance.getId());

        doLock(jobInstance);
        JobExecution jobExecution = null;
        try {
            if (jobInstance.getJobStatus().equals(JobStatus.WAITING)) {
                final JobExecution newExecution = new JobExecution();
                newExecution.setJobExecutionStatus(JobExecutionStatus.QUEUED);
                newExecution.setStartGuid(UUID.randomUUID().toString());
                newExecution.setJobInstance(jobInstance);
                newExecution.setParameters(jobParameters.toQueryString());
                jobExecution = jobExecutionRepository.saveAndFlush(newExecution);
                jobInstance.setJobStatus(JobStatus.RUNNING);
                LOG.debug("The Job ID {}, key {}, has started right now with the execution ID {}", jobInstance.getId(),
                    jobInstance.getJobKey(), jobExecution.getId());

            } else {
                /* In case the same job has started after the call to the getOrCreateJobInstanceByParameters() method
                 * by another process and before to lock the job instance with the current process
                 */
                throw new JobAlreadyRunningException("Already running the Job with ID " + jobInstance.getId());
            }
        } catch (JobAlreadyRunningException jare) {
            throw jare;
        } catch (RuntimeException re) {
            LOG.warn("Cannot execute the job {} because: {}", jobInstance.getJobKey(), re.getLocalizedMessage());
        } finally {
            doPersistAndRelease(jobInstance);
        }

        return jobExecution;
    }

    public JobExecution markJobAsStarted(final JobType jobType, final JobParameters jobParameters) {

        // do NOT start job if job instance has status of WAITING, this means it was cancel between
        // the time it was QUEUED and STARTED
        final JobInstance jobInstance = getJobInstanceByParameters(jobType, jobParameters);
        if (jobInstance == null || JobStatus.WAITING.equals(jobInstance.getJobStatus()))
            return null;

        JobExecution jobExecution = null;
        try {
            final JobExecution existingJobExecution = jobExecutionRepository.findTop1ByJobInstanceIdOrderByCreatedAtDesc(
                jobInstance.getId());

            existingJobExecution.setJobExecutionStatus(JobExecutionStatus.STARTED);
            existingJobExecution.setStartTime(LocalDateTime.now());

            jobExecution = jobExecutionRepository.saveAndFlush(existingJobExecution);
            LOG.debug("The Job ID {}, key {}, has started right now with the execution ID {}", jobInstance.getId(),
                jobInstance.getJobKey(), jobExecution.getId());

        } catch (RuntimeException re) {
            LOG.warn("Cannot execute the job {} because: {}", jobInstance.getJobKey(), re.getLocalizedMessage());
        }

        return jobExecution;
    }

    public void updateJobExecution(final Integer jobExecutionId, final StepSummary stepSummary) {
        final JobExecution jobExecution = jobExecutionRepository.findOne(jobExecutionId);
        if (jobExecution != null) {
            LOG.trace("Progress updating for job execution with ID {} and status {}...", jobExecution.getId(), jobExecution.getJobExecutionStatus());
            doLock(jobExecution);
            try {
                if (!jobExecution.getJobExecutionStatus().equals(JobExecutionStatus.STARTED) &&
                    !jobExecution.getJobExecutionStatus().equals(JobExecutionStatus.COMPLETED)) {
                    LOG.warn("The job execution with ID {} has been interrupted", jobExecutionId);
                    throw new JobInterruptedException("The job execution with ID" + " " + jobExecutionId + " " + "has been interrupted");
                }
                jobExecution.setTotalSteps(stepSummary.getTotalItems());
                jobExecution.setStepsToProcess(stepSummary.getItemsToProcess());
                jobExecution.setStepsCompleted(stepSummary.getProcessed());
                jobExecution.setStepsAborted(stepSummary.getDiscarded());
                if (stepSummary.getMessage() != null) {
                    jobExecution.setMessage(stepSummary.getMessage().getMessage());
                    jobExecution.setVariables(stepSummary.getMessage().getVariables());
                }
                jobExecution.setUpdatedAt(LocalDateTime.now());
                if (stepSummary.getTotalItems() > 0 && stepSummary.getItemsToProcess() < 1) {
                    jobExecution.setStopTime(LocalDateTime.now());
                }
            } finally {
                doPersistAndRelease(jobExecution);
            }
        } else {
            LOG.debug("The job execution with ID {} has been canceled", jobExecutionId);
            throw new JobInterruptedException("The job execution with ID" + " " + jobExecutionId + " " + "has been canceled");
        }
    }

    public Integer cancelBlockedExecutionsByJobId(final Integer jobId) {
        Preconditions.checkArgument(jobId != null);
        final LocalDateTime expiration = LocalDateTime.now().minusMinutes(timeoutInMinutes);
        return jobExecutionRepository.cancelAllBlockedExecutionsByJobId(jobId, expiration);
    }

    public boolean hasValidRunningExecutions(final JobInstance jobInstance) {
        Preconditions.checkArgument(jobInstance != null);
        int runningExecutions;

        final Integer blockedExecutions = cancelBlockedExecutionsByJobId(jobInstance.getId());
        runningExecutions = jobExecutionRepository.getRunningExecutions(jobInstance.getId());

        if (LOG.isDebugEnabled() && blockedExecutions > 0) {
            LOG.warn("The job ID {}, key {}, has {} blocked executions that have been terminated because a timeout occurs, and {} valid running executions",
                jobInstance.getId(), jobInstance.getJobKey(), blockedExecutions, runningExecutions);
        }

        doLock(jobInstance);

        try {
            if (runningExecutions == 0) {
                jobInstance.setJobStatus(JobStatus.WAITING);
            }
        } finally {
            doPersistAndRelease(jobInstance);
        }

        return (runningExecutions > 0);
    }

    private String generateJobInstanceKey(final JobType jobType, final JobParameters jobParameters) {
        Preconditions.checkArgument(jobType != null && jobParameters != null);
        final StringBuilder strBuilder = new StringBuilder(jobType.name());
        for (final JobParameter jobParameter : jobParameters.getParameters()) {
            if (jobParameter.getIdentifier()) {
                strBuilder.append(':').append(jobParameter.getValue());
            }
        }
        return strBuilder.toString();
    }

    private void doLock(final JobInstance jobInstance) {
        assert jobInstance != null;

        entityManager.flush();
        entityManager.refresh(jobInstance, LockModeType.PESSIMISTIC_WRITE);
        LOG.trace("Job locked: ID {}, key {}", jobInstance.getId(), jobInstance.getJobKey());
    }

    private void doLock(final JobExecution jobExecution) {
        assert jobExecution != null;

        entityManager.flush();
        entityManager.refresh(jobExecution, LockModeType.PESSIMISTIC_WRITE);
        LOG.trace("Execution locked: ID {}", jobExecution.getId());

    }

    private void doPersistAndRelease(final JobInstance jobInstance) {
        assert jobInstance != null;

        entityManager.persist(jobInstance);
        LOG.trace("Job released: ID {}, key {}", jobInstance.getId(), jobInstance.getJobKey());
    }

    private void doPersistAndRelease(final JobExecution jobExecution) {
        assert jobExecution != null;

        entityManager.persist(jobExecution);
        LOG.trace("Execution released: ID {}", jobExecution.getId());
    }
}
