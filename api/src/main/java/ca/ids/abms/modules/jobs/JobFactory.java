package ca.ids.abms.modules.jobs;

import ca.ids.abms.modules.jobs.impl.JobParameters;

public interface JobFactory {

    Job buildJob(final JobType jobType, final Integer jobExecutionId, final JobParameters jobParameters);

}
