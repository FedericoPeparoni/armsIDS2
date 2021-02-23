package ca.ids.abms.modules.jobs;

import ca.ids.abms.modules.jobs.impl.JobParameters;
import ca.ids.abms.modules.jobs.impl.JobSummary;

public interface JobLauncher {

    JobSummary getStatus(final JobType jobType, final JobParameters jobParameters);

    void create(final JobType jobType, final JobParameters jobParameters);

    void launch(final JobType jobType, final JobParameters jobParameters);

    void abort(final JobType jobType, final JobParameters jobParameters);

    boolean isAlreadyRunning(final JobType jobType, final JobParameters jobParameters);
}
