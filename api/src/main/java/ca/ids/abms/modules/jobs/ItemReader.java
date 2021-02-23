package ca.ids.abms.modules.jobs;

import ca.ids.abms.modules.jobs.impl.JobParameters;

public interface ItemReader<T> {

    void open(final JobParameters jobParameters);

    Iterable<T> read();

    void close();

}
