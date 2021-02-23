package ca.ids.abms.modules.jobs.impl;

import ca.ids.abms.modules.jobs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Slice;

import java.util.Collection;
import java.util.Observer;

public class SimpleStep<T> extends Step<T> {

    private static final Logger log = LoggerFactory.getLogger(SimpleStep.class);

    public SimpleStep(final ItemReader itemReader, final ItemProcessor itemProcessor) {
        super (itemReader, itemProcessor);
    }

    public SimpleStep(final ItemReader itemReader, final ItemProcessor itemProcessor,
                      final ItemWriter itemWriter) {
        super (itemReader, itemProcessor, itemWriter);
    }

    public void execute(final JobParameters jobParameters, final Observer observer) throws JobInterruptedException {
        log.debug("Executing a task with {}", jobParameters);

        final Iterable<T> items;
        try {
            this.itemReader.open(jobParameters);
            items = itemReader.read();
        } finally {
            this.itemReader.close();
        }

        int totalItems = -1;
        if (items instanceof Slice) {
            totalItems = ((Slice) items).getSize();
        } else if (items instanceof Collection) {
            totalItems = ((Collection) items).size();
        }
        final StepSummary stepSummary = new StepSummary(totalItems, observer);
        if (totalItems > 0) {
            log.debug("Read {} total items to process", totalItems);
            if (itemWriter != null) {
                itemWriter.open();
            }
            for (final T item : items) {
                try {
                    T result = itemProcessor.processItem(item);

                    if (result != null) {
                        if (itemWriter != null) {
                            itemWriter.write(result);
                        }
                        stepSummary.increaseProcessed();
                    } else {
                        stepSummary.increaseDiscarded();
                    }

                } catch (JobInterruptedException e) {
                    log.debug("The job has been interrupted: {}", e.getLocalizedMessage());
                    super.closeAll();
                    throw e;
                } catch (Exception e) {
                    log.debug("An item has been discarded because an error: {}; {}", e.getLocalizedMessage(), stepSummary);
                    stepSummary.increaseDiscarded();
                }
            }
        } else {
            log.debug("Not found any item to process");
        }

        log.debug("Task executed with parameters {}; {}", jobParameters, stepSummary);
        super.closeAll();
    }
}
