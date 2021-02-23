package ca.ids.abms.modules.jobs;

import ca.ids.abms.modules.jobs.impl.JobParameters;

import java.util.Observer;

public abstract class Step<T> {

    protected final ItemReader<T> itemReader;

    protected final ItemProcessor<T> itemProcessor;

    protected final ItemWriter<T> itemWriter;

    public Step(final ItemReader<T> itemReader, final  ItemProcessor<T> itemProcessor)
    {
        this.itemReader = itemReader;
        this.itemProcessor = itemProcessor;
        this.itemWriter = null;
    }

    public Step(final ItemReader<T> itemReader, final  ItemProcessor<T> itemProcessor,
                final ItemWriter<T> itemWriter) {
        this.itemReader = itemReader;
        this.itemProcessor = itemProcessor;
        this.itemWriter = itemWriter;
    }

    public abstract void execute(final JobParameters jobParameters, final Observer observer);

    protected void closeAll() {
        if (itemWriter != null) {
            try {
                itemWriter.close();
            } catch (Exception e) {
                // Nothing to do
            }
        }
        if (itemReader != null) {
            try {
                this.itemReader.close();
            } catch (Exception e) {
                // Nothing to do
            }
        }
    }
}
