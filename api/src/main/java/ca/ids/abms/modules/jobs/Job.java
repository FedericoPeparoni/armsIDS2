package ca.ids.abms.modules.jobs;

import ca.ids.abms.modules.jobs.impl.JobInstanceService;
import ca.ids.abms.modules.jobs.impl.JobParameters;
import ca.ids.abms.modules.jobs.impl.StepSummary;

import java.util.*;

public abstract class Job implements Observer {

    protected JobInstanceService jobInstanceService;

    private final Integer jobExecutionId;

    public Job (final JobInstanceService jobInstanceService, final Integer jobExecutionId) {
        assert jobInstanceService != null;
        assert jobExecutionId != null && jobExecutionId > 0;

        this.jobInstanceService = jobInstanceService;
        this.jobExecutionId = jobExecutionId;
    }

    public abstract void executeSteps(final JobParameters jobParameters);

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof StepSummary && jobExecutionId > 0) {
            final StepSummary stepSummary = (StepSummary)o;
            jobInstanceService.updateJobExecution(jobExecutionId, stepSummary);
        }
    }
}
