package ca.ids.abms.modules.locking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobLockingRepository extends JpaRepository<JobLocking, String> {

    List<JobLocking> findAllByProcessId(final String processId);
}
