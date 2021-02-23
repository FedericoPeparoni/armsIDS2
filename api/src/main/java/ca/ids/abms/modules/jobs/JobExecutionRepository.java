package ca.ids.abms.modules.jobs;

import ca.ids.abms.modules.jobs.impl.JobExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface JobExecutionRepository extends JpaRepository<JobExecution, Integer> {

    /**
     * Retrieve the current job execution
     */
    JobExecution findTop1ByJobInstanceIdOrderByCreatedAtDesc(final Integer jobInstance);

    @Modifying
    @Query("UPDATE JobExecution SET jobExecutionStatus = \'CANCELED\' WHERE jobExecutionStatus IN (\'QUEUED\', \'STARTED\') AND jobInstance.id = :jobId")
    void cancelAllCurrentExecutionsByJobId(@Param("jobId") final Integer jobId);

    @Modifying
    @Query("UPDATE JobExecution SET jobExecutionStatus = \'BLOCKED\' WHERE jobInstance.id = :jobId AND "
        + "jobExecutionStatus IN (\'QUEUED\', \'STARTED\') AND updatedAt < :date")
    Integer cancelAllBlockedExecutionsByJobId(@Param("jobId") final Integer id, @Param("date") final LocalDateTime expiration);

    @Query("SELECT count(e.id) FROM JobExecution e WHERE e.jobExecutionStatus IN (\'QUEUED\', \'STARTED\') AND e.jobInstance.id = :jobId")
    Integer getRunningExecutions(@Param("jobId") final Integer id);

}
