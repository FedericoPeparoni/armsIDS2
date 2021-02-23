package ca.ids.abms.modules.jobs.impl;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class JobParameters {

    private final Map<String, JobParameter> jobParameters;
    
    private final Map<String, JobParameter> jobOptionalParameters;

    JobParameters(Map<String, JobParameter> jobParameters, Map<String, JobParameter> jobOptionalParameters) {
        assert jobParameters != null;
        assert jobOptionalParameters != null;
        this.jobParameters = jobParameters;
        this.jobOptionalParameters = jobOptionalParameters;
    }

    public JobParameter getParameter(final String name) {
        return this.jobParameters.get(name);
    }
    
    public JobParameter getOptionalParameter(final String name) {
        return this.jobOptionalParameters.get(name);
    }

    public Collection<JobParameter> getParameters () {
        return this.jobParameters.values();
    }
    
    public Collection<JobParameter> getOptionalParameters () {
        return this.jobOptionalParameters.values();
    }
    
    public boolean hasOptionalParameters() {
        boolean hasOptionalParameters = false;
        if (jobOptionalParameters.size() > 0) {
            hasOptionalParameters = true;
        }
        return hasOptionalParameters;
    }

    public String toQueryString() {
        StringBuilder sb = new StringBuilder();
        if (!jobParameters.isEmpty()) {
            sb.append(jobParameters.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue().getValue())
                .collect(Collectors.joining(";")));
        }
        if (!jobOptionalParameters.isEmpty()) {
            sb.append(jobOptionalParameters.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue().getValue())
                .collect(Collectors.joining(";")));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "JobParameters{" + getParameters().toString() + "}, JobOptionalParameters{" + getOptionalParameters().toString() + "}";
    }
}
