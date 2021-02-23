package ca.ids.abms.plugins.caab.sage.modules.transactions;

import ca.ids.abms.modules.transactions.*;
import ca.ids.abms.plugins.PluginKey;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@SuppressWarnings("unused")
public class CaabSageTransactionServiceProvider extends TransactionServiceProvider {

    /**
     * List of transaction payment mechanisms supported by the CAAB SAGE integration. Currently, all
     * mechanisms except invoice are supported by this provider.
     */
    private static final List<TransactionPaymentMechanism> EXPORTABLE_MECHANISMS = new ArrayList<>();
    static {
        EXPORTABLE_MECHANISMS.add(TransactionPaymentMechanism.cash);
        EXPORTABLE_MECHANISMS.add(TransactionPaymentMechanism.credit);
        EXPORTABLE_MECHANISMS.add(TransactionPaymentMechanism.debit);
        EXPORTABLE_MECHANISMS.add(TransactionPaymentMechanism.cheque);
        EXPORTABLE_MECHANISMS.add(TransactionPaymentMechanism.wire);
        EXPORTABLE_MECHANISMS.add(TransactionPaymentMechanism.adjustment);
    }

    private final CaabSageTransactionCacheable caabSageTransactionCacheable;
    private final CaabSageTransactionService caabSageTransactionService;

    public CaabSageTransactionServiceProvider(
        final CaabSageTransactionCacheable caabSageTransactionCacheable,
        final CaabSageTransactionService caabSageTransactionService
    ) {
        super(PluginKey.CAAB_SAGE, true, true, EXPORTABLE_MECHANISMS);
        this.caabSageTransactionCacheable = caabSageTransactionCacheable;
        this.caabSageTransactionService = caabSageTransactionService;
    }

    /**
     * Handle credit note transaction on creation and update necessary Sage entities. All exceptions will
     * be handled and retried appropriately without affecting upstream `@Transactional` methods.
     *
     * @param transaction credit note transaction created in ABMS
     */
    @Override
    public void creditNoteCreated(Transaction transaction) {
        caabSageTransactionCacheable.createCreditNote(transaction);
    }

    /**
     * Handle credit payment transaction on creation and update necessary Sage entities. All exceptions will
     * be handled and retried appropriately without affecting upstream `@Transactional` methods.
     *
     * @param transactionPayment credit payment transaction created in ABMS
     */
    @Override
    public void creditPaymentCreated(TransactionPayment transactionPayment) {
        caabSageTransactionCacheable.createCreditPayment(transactionPayment);
    }

    /**
     * Handle transaction manual export and update necessary SAGE entities without exception handling.
     * All ejections will be thrown and affect upstream `@Transactional` methods.
     *
     * @param transaction transaction to export
     */
    @Override
    public void export(final Transaction transaction, final List<TransactionPayment> transactionPayments) {

        // export credit note if valid
        if (!transaction.getExported() && caabSageTransactionService.createCreditNote(transaction))
            caabSageTransactionService.exported(transaction);

        // for each payment, export if not already
        transactionPayments.stream()
            .filter(i -> !i.getExported())
            .forEach(this::export);
    }

    /**
     * Handle transaction payment manual export and update necessary SAGE entities without exception handling.
     * All ejections will be thrown and affect upstream `@Transactional` methods.
     *
     * @param transactionPayment transaction payment to export
     */
    private void export(final TransactionPayment transactionPayment) {
        if (!transactionPayment.getExported() && caabSageTransactionService.createCreditPayment(transactionPayment))
            caabSageTransactionService.exported(transactionPayment);
    }
}
