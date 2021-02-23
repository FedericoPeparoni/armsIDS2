package ca.ids.abms.modules.locking;

import ca.ids.abms.modules.jobs.JobStatus;
import ca.ids.abms.modules.util.models.AuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_locking")
public class JobLocking extends AuditedEntity {

    private static final long serialVersionUID = 1L;

    @Id
    private String serviceId;

    @Enumerated(EnumType.STRING)
    @NotNull
    private JobStatus status;

    private String processId;

    private LocalDateTime lastCheck;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public LocalDateTime getLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(LocalDateTime lastCheck) {
        this.lastCheck = lastCheck;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        JobLocking that = (JobLocking) o;

        if (serviceId != null ? !serviceId.equals(that.serviceId) : that.serviceId != null) return false;
        if (processId != null ? !processId.equals(that.processId) : that.processId != null) return false;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        int result = processId != null ? processId.hashCode() : 0;
        result = 31 * result + (serviceId != null ? serviceId.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JobLocking{" +
            "processId=" + processId +
            ", serviceId=" + serviceId +
            ", status=" + status +
            ", lastCheck=" + lastCheck +
            '}';
    }
}

