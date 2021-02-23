package ca.ids.abms.plugins.kcaa.erp.modules.transactions;

import ca.ids.abms.modules.jobs.JobAlreadyRunningException;
import ca.ids.abms.modules.locking.JobLockingService;
import ca.ids.abms.modules.plugins.PluginService;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.transactions.TransactionService;
import ca.ids.abms.plugins.PluginKey;
import ca.ids.abms.plugins.kcaa.erp.modules.receiptheader.ReceiptHeader;
import ca.ids.abms.plugins.kcaa.erp.modules.receiptheader.ReceiptHeaderMapper;
import ca.ids.abms.plugins.kcaa.erp.modules.receiptheader.ReceiptHeaderService;
import ca.ids.abms.plugins.kcaa.erp.modules.receiptline.ReceiptLine;
import ca.ids.abms.plugins.kcaa.erp.modules.receiptline.ReceiptLineService;
import ca.ids.abms.plugins.kcaa.erp.modules.system.KcaaErpConfigurationItemName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.xml.bind.DatatypeConverter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@SuppressWarnings("unused")
public class KcaaErpTransactionProcessor implements SchedulingConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(KcaaErpTransactionProcessor.class);

    private final JobLockingService jobLockingService;

    private final PluginService pluginService;

    private final ReceiptHeaderMapper receiptHeaderMapper;

    private final ReceiptHeaderService receiptHeaderService;

    private final ReceiptLineService receiptLineService;

    private final SystemConfigurationService systemConfigurationService;

    private final TransactionService transactionService;

    private final String processId;

    @Value("${app.plugins.kcaa.erp.receipt-retrieval-interval}")
    @SuppressWarnings("FieldCanBeLocal")
    private Integer interval = 5;

    public KcaaErpTransactionProcessor(final JobLockingService jobLockingService,
                                       final PluginService pluginService,
                                       final ReceiptHeaderMapper receiptHeaderMapper,
                                       final ReceiptHeaderService receiptHeaderService,
                                       final ReceiptLineService receiptLineService,
                                       final SystemConfigurationService systemConfigurationService,
                                       final TransactionService transactionService) {
        this.jobLockingService = jobLockingService;
        this.pluginService = pluginService;
        this.receiptHeaderMapper = receiptHeaderMapper;
        this.receiptHeaderService = receiptHeaderService;
        this.receiptLineService = receiptLineService;
        this.systemConfigurationService = systemConfigurationService;
        this.transactionService = transactionService;

        this.processId = jobLockingService.generateProcessId();
    }

    /**
     * Get last import timestamp from system configuration.
     *
     * @return last import timestamp
     */
    private byte[] getPreviousTimestamp() {
        String timestamp = systemConfigurationService.getValue(KcaaErpConfigurationItemName.RECEIPT_PROCESSOR_STARTING_TIMESTAMP);
        if (timestamp == null || timestamp.isEmpty())
            return DatatypeConverter.parseHexBinary("0000000000000000");
        else
            return DatatypeConverter.parseHexBinary(timestamp);
    }

    /**
     * Set last import timestamp in system configuration.
     *
     * @param timestamp last import timestamp
     */
    private void setLastImport(byte[] timestamp) {
        systemConfigurationService.update(KcaaErpConfigurationItemName.RECEIPT_PROCESSOR_STARTING_TIMESTAMP,
            DatatypeConverter.printHexBinary(timestamp));
    }

    /**
     * Create transaction and catch all exceptions.
     *
     * @param transaction transaction to create
     */
    private void doCreate(Transaction transaction) {
        try {
            transactionService.createCreditTransactionByPayments(transaction);
        } catch (Exception ex) {
            LOG.error("Could not process transaction because : {}", ex.getMessage());
        }
    }

    /**
     * Find all missing payments to process and create transactions
     * if possible.
     */
    private void doImport() {
        // set previous and latest timestamp for next import date
        byte[] previousTimestamp = getPreviousTimestamp();
        byte[] latestTimestamp = receiptHeaderService.findLatestTimestamp();

        // return if latest timestamp is not null and does not equal previous
        if (latestTimestamp == null || Arrays.equals(previousTimestamp, latestTimestamp)) {
            LOG.trace("Nothing to import for {}, up-to-dates", this.getClass().getSimpleName());
            return;
        }

        // retrieve a list of new receipt headers
        List<ReceiptHeader> receiptHeaders = receiptHeaderService.findByTimestamp(previousTimestamp, latestTimestamp);

        // get related receipt lines for each receipt header
        for (ReceiptHeader receiptHeader : receiptHeaders) {
            receiptHeader.setReceiptLines(receiptLineService.findByReceiptHeaderNo(receiptHeader.getNo()));
        }

        // restrict to valid receipt headers
        receiptHeaders.removeIf(this::isInvalid);

        // map to transaction and
        List<Transaction> transactions = receiptHeaderMapper.toTransaction(receiptHeaders);

        // restrict to valid transactions
        transactions.removeIf(this::isInvalid);

        // create new transactions and payments for each
        for (Transaction transaction : transactions) {
            doCreate(transaction);
        }

        // update last import date time
        setLastImport(latestTimestamp);
    }

    /**
     * Run necessary services to get updated payment receipts from Kcaa Erp
     * Account System.
     *
     * This should not run two instances concurrently, thus do NOT use fixedRate
     * for @Scheduled value.
     */
    private void doProcess() {

        // if disabled, quietly return
        if (!pluginService.isEnabled(PluginKey.KCAA_ERP))
            return;

        // verify that the job isn't in use by another instance
        try {
            jobLockingService.check (this.getClass().getSimpleName(), processId);
            LOG.trace("Attempting to import {}.", this.getClass().getSimpleName());
        } catch (JobAlreadyRunningException jare) {
            LOG.trace("Another instance are importing {}.", this.getClass().getSimpleName());
            return;
        }

        try {
            doImport();
        } catch (Exception ex) {
            LOG.error("Could not process transactions because : {}", ex.getMessage());
        }

        // release job locking for this process id
        jobLockingService.complete (this.getClass().getSimpleName(), processId);
    }

    /**
     * Validate if receipt header is invalid and cannot be processed.
     *
     * @param receiptHeader receipt header to validated
     * @return true if invalid
     */
    private boolean isInvalid(ReceiptHeader receiptHeader) {
        if (receiptHeader.getReceiptLines() == null)
            return true;

        for (ReceiptLine receiptLine : receiptHeader.getReceiptLines()) {
            if (receiptLine.getInvoiceNo() != null && !receiptLine.getInvoiceNo().isEmpty())
                return false;
        }

        LOG.warn("Cannot process receipt header as no receipt lines with [Invoice No] : {}", receiptHeader);

        return true;
    }

    /**
     * Validate if transaction is invalid and cannot be processed.
     *
     * @param transaction transaction to validate
     * @return true if invalid
     */
    private boolean isInvalid(Transaction transaction) {

        boolean isInvalid = transaction.getAccount() == null
            || transaction.getDescription() == null
            || transaction.getTransactionDateTime() == null
            || transaction.getTransactionType() == null
            || transaction.getAmount() == null
            || transaction.getCurrency() == null
            || transaction.getExported() == null
            || transaction.getBillingLedgerIds() == null
            || transaction.getPaymentMechanism() == null
            || transaction.getPaymentReferenceNumber() == null
            || transaction.getPaymentAmount() == null
            || transaction.getPaymentCurrency() == null
            || transaction.getPaymentExchangeRate() == null;

        if (LOG.isDebugEnabled() && isInvalid) {
            LOG.warn("Cannot process invalid transaction : {}", transaction);
        }

        return isInvalid;
    }

    /**
     * This will get the trigger for from system configuration if needed. Defaults
     * to 5 minutes if value not found or could not be parsed.
     *
     * @param triggerContext current trigger context
     * @return date for next trigger interval
     */
    private Date trigger(TriggerContext triggerContext) {
        return new PeriodicTrigger(interval, TimeUnit.MINUTES)
            .nextExecutionTime(triggerContext);
    }

    /**
     * Used to manage the scheduling using crontab expressions.
     *
     * @param scheduledTaskRegistrar registrar for scheduling tasks
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.addTriggerTask(this::doProcess, this::trigger);
    }

    /**
     * This method is used on destroy to release any locks. It should NOT
     * be called directly and is handled by the Spring Framework. This method
     * isn't guaranteed to be called, therefore, a timeout is used as a fallback if necessary within JobLockingServices.
     *
     * Note: All exceptions are caught, logged and squashed. The is required
     * to allow the shutdown process to continue running even on exception to
     * prevent memory leaks.
     */
    @PreDestroy
    @SuppressWarnings("unused")
    public void destroy() {
        try {
            jobLockingService.doResetLocksByProcessId(processId);
        } catch (Exception ex) {
            LOG.error("Could not release scheduler on shutdown: {}", ex.getLocalizedMessage());
        }
    }
}
