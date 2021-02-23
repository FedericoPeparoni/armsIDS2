package ca.ids.abms.modules.jobs.impl;

import java.util.HashMap;

public class JobParametersBuilder {

    private final HashMap<String, JobParameter> jobParameters;
    
    private final HashMap<String, JobParameter> jobOptionalParameters;

    public JobParametersBuilder() {
        this.jobParameters = new HashMap<>();
        this.jobOptionalParameters = new HashMap<>();
    }

    public JobParametersBuilder addParameter (final String name, final Object value, boolean isIdentifier) {
        final JobParameter jobParameter = new JobParameter(name, value, isIdentifier);
        this.jobParameters.put(name, jobParameter);
        return this;
    }
    
    public JobParametersBuilder addOptionalParameter (final String name, final Object value, boolean isIdentifier) {
        final JobParameter jobParameter = new JobParameter(name, value, isIdentifier);
        this.jobOptionalParameters.put(name, jobParameter);
        return this;
    }

    public JobParameters toJobParameters() {
        return new JobParameters(new HashMap<>(jobParameters), new HashMap<>(jobOptionalParameters));
    }
}
