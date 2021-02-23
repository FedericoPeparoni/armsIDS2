package ca.ids.abms.plugins.kcaa.eaip.modules.transactions;

import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.transactions.*;
import ca.ids.abms.plugins.common.modules.AbstractPluginTransactionService;
import ca.ids.abms.plugins.kcaa.eaip.config.KcaaEaipDatabaseConfig;
import ca.ids.abms.plugins.kcaa.eaip.modules.payment.PaymentService;
import ca.ids.abms.plugins.kcaa.eaip.modules.requisition.RequisitionService;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class KcaaEaipTransactionService extends AbstractPluginTransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(KcaaEaipTransactionService.class);

    private final PaymentService paymentService;
    private final RequisitionService requisitionService;

    public KcaaEaipTransactionService(
        final TransactionPaymentRepository transactionPaymentRepository,
        final TransactionRepository transactionRepository,
        final TransactionTypeRepository transactionTypeRepository,
        final PaymentService paymentService,
        final RequisitionService requisitionService
    ) {
        super(transactionPaymentRepository, transactionRepository, transactionTypeRepository);
        this.paymentService = paymentService;
        this.requisitionService = requisitionService;
    }

    // region create credit payment

    /**
     * Perform necessary external database changes for created credit payment transaction.
     *
     * @param transactionPayment credit payment transaction to create
     * @return true if exported else false
     */
    @Override
    @Transactional(value = KcaaEaipDatabaseConfig.TRANSACTION_MANAGER)
    public Boolean createCreditPayment(final TransactionPayment transactionPayment) {
        LOG.trace("Attempt to update requisition and insert payment from transaction payment : {}",
            transactionPayment);
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
    @Transactional(propagation = Propagation.REQUIRES_NEW, value = KcaaEaipDatabaseConfig.TRANSACTION_MANAGER)
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
    protected void createEntriesByCreditPayment(final TransactionPayment transactionPayment, final Integer[] ignored) {
        LOG.trace("Insert payment(s) and update requisition number(s) from transaction payment : {}",
            transactionPayment);

        boolean isFinalPayment = isFinalPayment(transactionPayment);
        if (!isFinalPayment && LOG.isDebugEnabled()) {
            LOG.debug("Cannot update requisition number(s) from transaction payment as associated billing ledger is " +
                "not fully paid : {}", transactionPayment);
        }

        // create full/partial payment for requisition number being paid
        // only update requisition service as paid if associated billing ledger is fully paid
        transactionPayment.getBillingLedger().getKcaaEaipRequisitionNumbers().forEach(requisitionNumber -> {
            paymentService.create(transactionPayment, requisitionNumber);
            if (isFinalPayment) requisitionService.updateAsPaid(transactionPayment, requisitionNumber);
        });
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
        boolean isFinalPayment = isFinalPayment(transactionPayment);

        // create full/partial payment for requisition number being paid
        // only update requisition service as paid if associated billing ledger is fully paid
        List<PluginSqlStatement> statements = new ArrayList<>();
        transactionPayment.getBillingLedger().getKcaaEaipRequisitionNumbers().forEach(requisitionNumber -> {
            statements.add(paymentService.createStatement(transactionPayment, requisitionNumber));
            if (isFinalPayment) statements.add(requisitionService.updateAsPaidStatement(transactionPayment, requisitionNumber));
        });

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

        // validate that transaction payment should update KCAA eAIP system
        boolean result = transactionPayment != null
            && transactionPayment.getTransaction() != null
            && transactionPayment.getBillingLedger() != null
            && transactionPayment.getBillingLedger().getKcaaEaipRequisitionNumbers() != null;

        if (LOG.isTraceEnabled() && !result) {
            LOG.error("Transaction payment is missing fields that cannot be null for exporting, will be skipped : {}",
                transactionPayment);
        }

        return  result;
    }

    /**
     * Return true if provided transaction payment's associated billing ledger is PAID.
     */
    private boolean isFinalPayment(final TransactionPayment transactionPayment) {
        return InvoiceStateType.PAID.toValue().equals(
            transactionPayment.getBillingLedger().getInvoiceStateType());
    }

    // endregion validate credit payment
}
