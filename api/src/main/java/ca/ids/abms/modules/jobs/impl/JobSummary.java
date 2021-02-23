package ca.ids.abms.modules.jobs.impl;

import ca.ids.abms.modules.jobs.JobExecutionStatus;
import ca.ids.abms.modules.jobs.JobType;

import java.time.LocalDateTime;

public class JobSummary {

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

    private String message;

    private String variables;

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

    public JobExecutionStatus getJobExecutionStatus() {
        return jobExecutionStatus;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVariables() {
        return variables;
    }

    public void setVariables(String variables) {
        this.variables = variables;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobSummary that = (JobSummary) o;

        if (jobName != null ? !jobName.equals(that.jobName) : that.jobName != null) return false;
        return jobExecutionStatus == that.jobExecutionStatus;
    }

    @Override
    public int hashCode() {
        int result = jobName != null ? jobName.hashCode() : 0;
        result = 31 * result + (jobExecutionStatus != null ? jobExecutionStatus.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JobSummary{" +
            "jobName='" + jobName + '\'' +
            ", jobType=" + jobType +
            ", jobParameters=" + jobParameters +
            ", jobExecutionStatus=" + jobExecutionStatus +
            ", startTime=" + startTime +
            ", stopTime=" + stopTime +
            ", totalSteps=" + totalSteps +
            ", stepsToProcess=" + stepsToProcess +
            ", stepsCompleted=" + stepsCompleted +
            ", stepsAborted=" + stepsAborted +
            '}';
    }
}
