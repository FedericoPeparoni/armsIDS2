package ca.ids.abms.modules.jobs.impl;

import ca.ids.abms.modules.jobs.JobExecutionStatus;
import ca.ids.abms.modules.util.models.AuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
public class JobExecution extends AuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 128)
    private String startGuid;

    private LocalDateTime startTime;

    private LocalDateTime stopTime;

    private Integer totalSteps;

    private Integer stepsToProcess;

    private Integer stepsCompleted;

    private Integer stepsAborted;

    @NotNull
    private String parameters;

    private String message;

    private String variables;

    @Enumerated(EnumType.STRING)
    @NotNull
    private JobExecutionStatus jobExecutionStatus;

    @ManyToOne
    @JoinColumn(name = "job_instance_id")
    @NotNull
    private JobInstance jobInstance;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStartGuid() {
        return startGuid;
    }

    public void setStartGuid(String startGuid) {
        this.startGuid = startGuid;
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

    public JobExecutionStatus getJobExecutionStatus() {
        return jobExecutionStatus;
    }

    public void setJobExecutionStatus(JobExecutionStatus jobExecutionStatus) {
        this.jobExecutionStatus = jobExecutionStatus;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public JobInstance getJobInstance() {
        return jobInstance;
    }

    public void setJobInstance(JobInstance jobInstance) {
        this.jobInstance = jobInstance;
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
    public String toString() {
        return "JobExecution{" +
            "id=" + id +
            ", startGuid='" + startGuid + '\'' +
            ", startTime=" + startTime +
            ", stopTime=" + stopTime +
            ", totalSteps=" + totalSteps +
            ", stepsToProcess=" + stepsToProcess +
            ", stepsCompleted=" + stepsCompleted +
            ", stepsAborted=" + stepsAborted +
            ", parameters='" + parameters + '\'' +
            ", message='" + message + '\'' +
            ", variables='" + variables + '\'' +
            ", jobExecutionStatus=" + jobExecutionStatus +
            ", jobInstance=" + jobInstance +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        JobExecution that = (JobExecution) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
