package ca.ids.abms.plugins.kcaa.aatis.modules.transactions;

import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.transactions.TransactionPayment;
import ca.ids.abms.modules.transactions.TransactionPaymentRepository;
import ca.ids.abms.modules.transactions.TransactionRepository;
import ca.ids.abms.modules.transactions.TransactionTypeRepository;
import ca.ids.abms.plugins.common.modules.AbstractPluginTransactionService;
import ca.ids.abms.plugins.kcaa.aatis.config.KcaaAatisDatabaseConfig;
import ca.ids.abms.plugins.kcaa.aatis.modules.invoicepermit.InvoicePermitService;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class KcaaAatisTransactionService extends AbstractPluginTransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(KcaaAatisTransactionService.class);

    private final InvoicePermitService invoicePermitService;

    public KcaaAatisTransactionService(
        final TransactionPaymentRepository transactionPaymentRepository,
        final TransactionRepository transactionRepository,
        final TransactionTypeRepository transactionTypeRepository,
        final InvoicePermitService invoicePermitService
    ) {
        super(transactionPaymentRepository, transactionRepository, transactionTypeRepository);
        this.invoicePermitService = invoicePermitService;
    }

    // region create credit payment

    /**
     * Perform necessary external database changes for created credit payment transaction.
     *
     * @param transactionPayment credit payment transaction to create
     * @return true if exported else false
     */
    @Override
    @Transactional(value = KcaaAatisDatabaseConfig.TRANSACTION_MANAGER)
    public Boolean createCreditPayment(final TransactionPayment transactionPayment) {
        LOG.trace("Attempt to update invoice permit from transaction payment : {}", transactionPayment);
        return super.createCreditPayment(transactionPayment);
    }

    /**
     * Cacheable wrapper for `createPayment` method. Will suspend transaction manager and create a new
     * one that will not effect the upstream transaction if exception thrown.
     *
     * @param transactionPayment credit payment transaction to create
     * @return true if exported else false
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, value = KcaaAatisDatabaseConfig.TRANSACTION_MANAGER)
    public Boolean createCreditPaymentCacheable(final TransactionPayment transactionPayment) {
        return super.createCreditPaymentCacheable(transactionPayment);
    }

    // endregion create credit payment

    // region create entries and statements

    /**
     * Update invoice permits from transaction payment billing ledger permit numbers.
     *
     * @param transactionPayment transaction payment for updating entries.
     * @param ignored line numbering not used
     */
    @Override
    protected void createEntriesByCreditPayment(
        final TransactionPayment transactionPayment, final Integer[] ignored
    ) {

        // check associated billing ledger, should only update external database once fully paid
        if (isNotFinalPayment(transactionPayment)) {
            LOG.debug("Cannot update invoice permit(s) from transaction payment as associated billing ledger is " +
                    "not fully paid : {}", transactionPayment);
            return;
        }

        LOG.trace("Update invoice permit(s) from transaction payment : {}", transactionPayment);
        transactionPayment.getBillingLedger().getKcaaAatisPermitNumbers().forEach(permitNumber ->
            invoicePermitService.updateAsPaid(transactionPayment, permitNumber));
    }

    /**
     * Use to get the SQL statements generated when updating invoice permits in KCAA AATIS.
     *
     * @param transactionPayment transaction credit payment to create entries
     * @param ignored line numbering not used
     * @return SQL statements generated
     */
    @Override
    protected List<PluginSqlStatement> createEntriesByCreditPaymentStatements(
        final TransactionPayment transactionPayment, final Integer[] ignored
    ) {

        // check associated billing ledger, should only update external database once fully paid
        if (isNotFinalPayment(transactionPayment)) {
            return new ArrayList<>();
        }

        List<PluginSqlStatement> statements = new ArrayList<>();
        transactionPayment.getBillingLedger().getKcaaAatisPermitNumbers().forEach(permitNumber ->
            statements.add(invoicePermitService.updateAsPaidStatement(transactionPayment, permitNumber)));
        return statements;
    }

    // endregion create entries and statements

    // region validate credit payment

    /**
     * Perform necessary validation for created credit payment transaction entries.
     *
     * @param transactionPayment credit payment transaction to create entries
     * @param lineNo line number of last item
     */
    @Override
    protected boolean validateEntriesByCreditPayment(final TransactionPayment transactionPayment, final Integer[] lineNo) {

        // validate that transaction payment should update KCAA AATIS system
        boolean result = transactionPayment != null
            && transactionPayment.getTransaction() != null
            && transactionPayment.getBillingLedger() != null
            && transactionPayment.getBillingLedger().getKcaaAatisPermitNumbers() != null;

        if (LOG.isTraceEnabled() && !result) {
            LOG.trace("Transaction payment is missing fields that cannot be null for exporting, will be skipped : {}",
                transactionPayment);
        }

        return  result;
    }

    /**
     * Return true if provided transaction payment's associated billing ledger is not PAID.
     */
    private boolean isNotFinalPayment(final TransactionPayment transactionPayment) {
        return !InvoiceStateType.PAID.toValue().equals(
            transactionPayment.getBillingLedger().getInvoiceStateType());
    }

    // endregion validate credit payment
}
