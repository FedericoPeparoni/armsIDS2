package ca.ids.abms.modules.jobs.impl;

import ca.ids.abms.modules.jobs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class JobAsyncLauncher extends JobSyncLauncher {

    private static final Logger log = LoggerFactory.getLogger(JobAsyncLauncher.class);

    public JobAsyncLauncher(final JobInstanceService jobInstanceService,
                            final JobFactory jobFactory) {
        super(jobInstanceService, jobFactory);
    }

    @Override
    @Async
    public void launch(final JobType jobType, final JobParameters jobParameters) throws JobAlreadyRunningException {
        try {
            super.launch(jobType, jobParameters);
        } catch (JobAlreadyRunningException jar) {
            log.debug("The requested job cannot start: {}", jar.getMessage());
        } catch (Exception e) {
            log.error("An error has occurred", e);
        }
    }

}
