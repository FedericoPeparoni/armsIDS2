package ca.ids.abms.modules.jobs.impl;

import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.jobs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class JobSyncLauncher implements JobLauncher {

    private static final Logger LOG = LoggerFactory.getLogger(JobSyncLauncher.class);

    private static final String LOG_MSG_STATUS = "Retrieving the status of the Job ID: {}, key: {}";

    private final JobInstanceService jobInstanceService;

    private final JobFactory jobFactory;

    public JobSyncLauncher(final JobInstanceService jobInstanceService,
                           final JobFactory jobFactory) {
        this.jobInstanceService = jobInstanceService;
        this.jobFactory = jobFactory;
    }

    @Override
    public JobSummary getStatus(final JobType jobType, final JobParameters jobParameters) {
        JobExecution jobExecution = null;
        final JobInstance jobInstance = jobInstanceService.getJobInstanceByParameters(jobType, jobParameters);
        if (jobInstance != null && jobInstance.getId() != null) {
            LOG.debug(LOG_MSG_STATUS, jobInstance.getId(), jobInstance.getJobKey());
            jobInstanceService.cancelBlockedExecutionsByJobId(jobInstance.getId());
            jobExecution = jobInstanceService.getJobExecutionByJobInstanceId(jobInstance.getId());
        }
        return buildSummary(jobType, jobInstance, jobExecution, jobParameters, null);
    }

    public JobSummary getStatus(final JobType jobType, final JobParameters jobParameters, final JobSummary jobSummary) {
        JobExecution jobExecution = null;
        final JobInstance jobInstance = jobInstanceService.getJobInstanceByParameters(jobType, jobParameters);
        if (jobInstance != null && jobInstance.getId() != null) {
            LOG.debug(LOG_MSG_STATUS, jobInstance.getId(), jobInstance.getJobKey());
            jobInstanceService.cancelBlockedExecutionsByJobId(jobInstance.getId());
            jobExecution = jobInstanceService.getJobExecutionByJobInstanceId(jobInstance.getId());
        }
        return buildSummary(jobType, jobInstance, jobExecution, jobParameters, jobSummary);
    }

    @Override
    public void create(final JobType jobType, final JobParameters jobParameters) {
        try {
            jobInstanceService.markJobAsQueued(jobType, jobParameters);
        } catch (JobAlreadyRunningException jare) {
            LOG.warn("Job creation failed, job already running for type {} with parameters {}",
                jobType.name(), jobParameters.toQueryString());
            throw jare;
        }
    }

    @Override
    public void launch(final JobType jobType, final JobParameters jobParameters) {
        final JobExecution jobExecution = jobInstanceService.markJobAsStarted(jobType, jobParameters);
        if (jobExecution != null) {
            final Job job = jobFactory.buildJob(jobType, jobExecution.getId(), jobParameters);
            try {
                job.executeSteps(jobParameters);
                jobInstanceService.markJobAsCompleted(jobExecution.getId());
            } catch (JobInterruptedException jie) {
                LOG.debug(jie.getLocalizedMessage());
                jobInstanceService.markJobAsCompleted(jobExecution.getId(), JobExecutionStatus.BLOCKED);
            } catch (Exception e) {
                LOG.error("Job execution {} has failed: {}", jobExecution.getId(), ExceptionFactory.resolveManagedErrors(e));
                jobInstanceService.markJobAsCompleted(jobExecution.getId(), JobExecutionStatus.FAILED);
            }
        }
    }

    @Override
    public void abort(final JobType jobType, final JobParameters jobParameters) {
        jobInstanceService.markJobAsCanceled(jobType, jobParameters);
    }

    @Override
    public boolean isAlreadyRunning(JobType jobType, JobParameters jobParameters) {
        boolean alreadyRunning = false;
        final JobInstance jobInstance = jobInstanceService.getJobInstanceByParameters(jobType, jobParameters);
        if (jobInstance != null && jobInstance.getJobStatus().equals(JobStatus.RUNNING)) {
            LOG.debug(LOG_MSG_STATUS, jobInstance.getId(), jobInstance.getJobKey());
            alreadyRunning = jobInstanceService.hasValidRunningExecutions(jobInstance);
        }
        return (alreadyRunning);
    }

    private JobSummary buildSummary(final JobType jobType, final JobInstance jobInstance,
                                    final JobExecution jobExecution, final JobParameters jobParameters,
                                    final JobSummary jobSummaryToCompile) {
        final JobSummary jobSummary = jobSummaryToCompile != null ? jobSummaryToCompile : new JobSummary();

        if (jobInstance != null) {
            jobSummary.setJobType(jobInstance.getJobType());
            jobSummary.setJobName(jobInstance.getJobName());
        } else {
            jobSummary.setJobType(jobType);
            jobSummary.setJobName(jobType.name());
        }
        if (jobExecution != null) {
            jobSummary.setJobExecutionStatus(jobExecution.getJobExecutionStatus());
            jobSummary.setStartTime(jobExecution.getStartTime());
            jobSummary.setStepsAborted(jobExecution.getStepsAborted());
            jobSummary.setStepsCompleted(jobExecution.getStepsCompleted());
            jobSummary.setStepsToProcess(jobExecution.getStepsToProcess());
            jobSummary.setTotalSteps(jobExecution.getTotalSteps());
            jobSummary.setStopTime(jobExecution.getStopTime());
            jobSummary.setJobParameters(jobExecution.getParameters());
            jobSummary.setMessage(jobExecution.getMessage());
            jobSummary.setVariables(jobExecution.getVariables());
        } else {
            jobSummary.setJobExecutionStatus(JobExecutionStatus.READY);
            jobSummary.setStartTime(LocalDateTime.now());
            jobSummary.setStepsAborted(0);
            jobSummary.setStepsCompleted(0);
            jobSummary.setStepsToProcess(0);
            jobSummary.setTotalSteps(0);
            if (jobParameters != null) {
                jobSummary.setJobParameters(jobParameters.getParameters().toString());
            }
        }
        return jobSummary;
    }
}
