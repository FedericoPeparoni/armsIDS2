package ca.ids.abms.modules.jobs.impl;

import ca.ids.abms.modules.jobs.JobExecutionStatus;
import ca.ids.abms.modules.jobs.JobType;
import ca.ids.abms.modules.translation.Translation;

import java.time.LocalDateTime;

public class JobSummaryViewModel {

    private String jobName;

    private JobType jobType;

    private String jobParameters;

    private JobExecutionStatus jobExecutionStatus;

    private LocalDateTime startTime;

    private LocalDateTime stopTime;

    private Integer totalSteps;

    private Integer stepsToProcess;

    private Integer stepsCompleted;

    private Integer stepsAborted;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public String getJobParameters() {
        return jobParameters;
    }

    public void setJobParameters(String jobParameters) {
        this.jobParameters = jobParameters;
    }

    public String getJobExecutionStatus() {
        return jobExecutionStatus.toString();
    }

    public void setJobExecutionStatus(JobExecutionStatus jobExecutionStatus) {
        this.jobExecutionStatus = jobExecutionStatus;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStopTime() {
        return stopTime;
    }

    public void setStopTime(LocalDateTime stopTime) {
        this.stopTime = stopTime;
    }

    public Integer getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(Integer totalSteps) {
        this.totalSteps = totalSteps;
    }

    public Integer getStepsToProcess() {
        return stepsToProcess;
    }

    public void setStepsToProcess(Integer stepsToProcess) {
        this.stepsToProcess = stepsToProcess;
    }

    public Integer getStepsCompleted() {
        return stepsCompleted;
    }

    public void setStepsCompleted(Integer stepsCompleted) {
        this.stepsCompleted = stepsCompleted;
    }

    public Integer getStepsAborted() {
        return stepsAborted;
    }

    public void setStepsAborted(Integer stepsAborted) {
        this.stepsAborted = stepsAborted;
    }
}


