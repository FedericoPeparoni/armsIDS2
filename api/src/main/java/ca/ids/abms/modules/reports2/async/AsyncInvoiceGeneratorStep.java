package ca.ids.abms.modules.reports2.async;

import ca.ids.abms.modules.jobs.ItemProcessor;
import ca.ids.abms.modules.jobs.ItemWriter;
import ca.ids.abms.modules.jobs.JobInterruptedException;
import ca.ids.abms.modules.jobs.Step;
import ca.ids.abms.modules.jobs.impl.InvoiceProgressCounter;
import ca.ids.abms.modules.jobs.impl.JobParameter;
import ca.ids.abms.modules.jobs.impl.JobParameters;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.StaleStateException;
import org.hibernate.exception.LockAcquisitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Observer;

public class AsyncInvoiceGeneratorStep extends Step<AsyncInvoiceGeneratorScope> {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncInvoiceGeneratorStep.class);

    public AsyncInvoiceGeneratorStep(
        final ItemProcessor<AsyncInvoiceGeneratorScope> processor,
        final ItemWriter<AsyncInvoiceGeneratorScope> itemWriter
    ) {
        super(null, processor, itemWriter);
    }

    @Override
    @SuppressWarnings("squid:S00112")
    public void execute(JobParameters jobParameters, Observer observer) {
        LOG.debug("Executing a task with {}", jobParameters);
        final JobParameter scopeParameter = jobParameters.getParameter("scope");
        final AsyncInvoiceGeneratorScope scope = (AsyncInvoiceGeneratorScope)scopeParameter.getValue();
        scope.setInvoiceProgressCounter(new InvoiceProgressCounter(CollectionUtils.size(scope.getAccountIdList()), observer));
        if (CollectionUtils.isNotEmpty(scope.getAccountIdList())) {
            try {
                itemProcessor.processItem(scope);
                if (itemWriter != null) itemWriter.write(scope);
                scope.getInvoiceProgressCounter().setMessage("Task completed");
                scope.getInvoiceProgressCounter().sendNoItemToProcess();
            } catch (JobInterruptedException e) {
                LOG.debug("The job has been interrupted: {}", e.getLocalizedMessage());
                throw e;
            } catch(final LockAcquisitionException | ObjectOptimisticLockingFailureException | StaleStateException e) {
                LOG.debug("Invoice data was modified during invoice generation: {}", e.getMessage());
                scope.getInvoiceProgressCounter().setMessage("Invoice data was modified during invoice generation");
                throw e;
            } catch (Exception e) {
                LOG.debug("Cannot generate invoices through the job: {}", e.getLocalizedMessage());
                scope.getInvoiceProgressCounter().setMessage(e.getLocalizedMessage());
                throw new RuntimeException(e);
            } finally {
                scope.getInvoiceProgressCounter().update();
                super.closeAll();
            }
            LOG.debug("Task executed with parameters {}; {}", jobParameters, scope.getInvoiceProgressCounter());
        } else {
            LOG.debug("Task not executed because there aren't items to process");
        }
        super.closeAll();
    }
}
