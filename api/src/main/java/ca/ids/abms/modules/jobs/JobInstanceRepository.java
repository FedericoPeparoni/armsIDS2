package ca.ids.abms.modules.jobs;


import ca.ids.abms.modules.jobs.impl.JobInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobInstanceRepository extends JpaRepository<JobInstance, Integer> {

    JobInstance findOneByJobKey(final String jobKey);


}
