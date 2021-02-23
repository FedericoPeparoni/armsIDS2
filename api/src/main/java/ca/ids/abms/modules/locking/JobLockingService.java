package ca.ids.abms.modules.locking;

import ca.ids.abms.modules.jobs.JobAlreadyRunningException;
import ca.ids.abms.modules.jobs.JobStatus;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class JobLockingService {

    private static final Logger log = LoggerFactory.getLogger(JobLockingService.class);

    private final EntityManager entityManager;

    private final JobLockingRepository jobLockingRepository;

    private final Integer timeoutInMinutes;

    public JobLockingService(final EntityManager entityManager,
                             final JobLockingRepository jobLockingRepository,
                             @Value("${app.jobLocking.timeout.minutes}") final Integer timeoutInMinutes) {
        this.entityManager = entityManager;
        this.jobLockingRepository = jobLockingRepository;
        this.timeoutInMinutes = timeoutInMinutes;
    }

    public String generateProcessId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Check if lock set for service and lock if not. Overrides own process id.
     *
     * @param serviceId service identification
     * @param processId process identification
     */
    public void check (final String serviceId, final String processId) {
        check(serviceId, processId, true);
    }

    /**
     * Check if lock set for service and lock if not. Own process id is overwritten if
     * `overrideSelf` is true.
     *
     * @param serviceId service identification
     * @param processId process identification
     * @param overrideSelf override self
     */
    public void check (final String serviceId, final String processId, final Boolean overrideSelf) {
        assert (serviceId != null && processId != null);

        final JobLocking jobLocking = getJobLockingInstance(serviceId);
        assert (jobLocking != null);

        doLock(jobLocking);

        try {
            if (overrideSelf && processId.equals(jobLocking.getProcessId())) {
                jobLocking.setStatus(JobStatus.RUNNING);
            } else {
                if (JobStatus.RUNNING.equals(jobLocking.getStatus())) {
                    if (isTimeout(jobLocking)) {
                        log.debug("A previous process was in timeout, the job will be restarted: {}", jobLocking);
                    } else {
                        log.debug("Another process is already using the job: {}", jobLocking);
                        throw new JobAlreadyRunningException("Already running " + jobLocking.toString());
                    }
                } else {
                    jobLocking.setStatus(JobStatus.RUNNING);
                }
                jobLocking.setProcessId(processId);
            }
            jobLocking.setLastCheck(LocalDateTime.now());
        } finally {
            doPersistAndRelease(jobLocking);
        }
    }

    public void complete (final String serviceId, final String processId) {
        assert (serviceId != null && processId != null);

        final JobLocking jobLocking = getJobLockingInstance(serviceId);
        assert (jobLocking != null);

        doLock(jobLocking);

        try {
            if (processId.equals(jobLocking.getProcessId())) {
                jobLocking.setStatus(JobStatus.WAITING);
            } else {
                if (JobStatus.RUNNING.equals(jobLocking.getStatus())) {
                    if (isTimeout(jobLocking)) {
                        log.debug("A previous process was in timeout, the job will be stopped: {}", jobLocking);
                    } else {
                        log.debug("Another process is already using the job: {}", jobLocking);
                        return;
                    }
                    jobLocking.setStatus(JobStatus.WAITING);
                }
                jobLocking.setProcessId(processId);
            }
            jobLocking.setLastCheck(LocalDateTime.now());
        } finally {
            doPersistAndRelease(jobLocking);
        }
    }

    private JobLocking getJobLockingInstance(final String serviceId) {
        JobLocking jobLocking = jobLockingRepository.findOne(serviceId);
        if (jobLocking == null) {
            jobLocking = new JobLocking();
            jobLocking.setServiceId(serviceId);
            jobLocking.setStatus(JobStatus.WAITING);
            jobLocking = jobLockingRepository.save(jobLocking);
        }
        return jobLocking;
    }

    private boolean isTimeout (final JobLocking jobLocking) {
        final LocalDateTime lastCheck = jobLocking.getLastCheck();
        final LocalDateTime now = LocalDateTime.now();
        return lastCheck != null && now.minusMinutes(timeoutInMinutes).isAfter(lastCheck);
    }

    private void doLock(final JobLocking jobLocking) {
        assert jobLocking != null;

        entityManager.flush();
        entityManager.refresh(jobLocking, LockModeType.PESSIMISTIC_WRITE);
        log.trace("Job locked: {}", jobLocking);
    }

    private void doPersistAndRelease(final JobLocking jobLocking) {
        assert jobLocking != null;

        entityManager.persist(jobLocking);
        log.trace("Job released: {}", jobLocking);
    }

    public void doResetLocksByProcessId(final String processId) {
        final List<JobLocking> jobs = jobLockingRepository.findAllByProcessId(processId);
        if (CollectionUtils.isNotEmpty(jobs)) {
            for (final JobLocking jobLocking : jobs) {
                if (!JobStatus.WAITING.equals(jobLocking.getStatus())) {
                    log.debug("Release job with process id {}", jobLocking.getProcessId());
                    jobLocking.setStatus(JobStatus.WAITING);
                    jobLockingRepository.save(jobLocking);
                }
            }
        }
    }
}
