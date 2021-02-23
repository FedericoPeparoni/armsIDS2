package ca.ids.abms.modules.jobs.impl;

import ca.ids.abms.modules.jobs.JobStatus;
import ca.ids.abms.modules.jobs.JobType;
import ca.ids.abms.modules.util.models.AuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class JobInstance extends AuditedEntity {

    private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.STRING)
    @NotNull
    private JobType jobType;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    @Size(max = 128)
    @NotNull
    private String jobKey;

    @NotNull
    @Size(max = 128)
    private String jobName;

    @Enumerated(EnumType.STRING)
    @NotNull
    private JobStatus jobStatus;

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    @Override
    public String toString() {
        return "Job{" +
            "jobType=" + jobType +
            ", id=" + id +
            ", jobKey='" + jobKey + '\'' +
            ", jobName='" + jobName + '\'' +
            ", jobStatus=" + jobStatus +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        JobInstance job = (JobInstance) o;

        if (id != null ? !id.equals(job.id) : job.id != null) return false;
        return jobKey != null ? jobKey.equals(job.jobKey) : job.jobKey == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (jobKey != null ? jobKey.hashCode() : 0);
        return result;
    }
}
