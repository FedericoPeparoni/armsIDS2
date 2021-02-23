package ca.ids.abms.modules.jobs.impl;

import ca.ids.abms.modules.jobs.*;

public class SimpleJob<T> extends Job {

    private Step<T> singleStep;

    public SimpleJob(final JobInstanceService jobInstanceService, final Integer jobExecutionId,
                     final Step<T> singleStep) {
        super (jobInstanceService, jobExecutionId);
        this.singleStep = singleStep;
    }

    public void executeSteps(final JobParameters jobParameters) {
        singleStep.execute(jobParameters, this);
    }
}
