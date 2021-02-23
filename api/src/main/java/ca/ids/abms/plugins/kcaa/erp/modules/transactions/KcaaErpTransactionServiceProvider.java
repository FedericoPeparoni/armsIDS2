package ca.ids.abms.plugins.kcaa.erp.modules.transactions;

import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.transactions.TransactionPayment;
import ca.ids.abms.modules.transactions.TransactionPaymentMechanism;
import ca.ids.abms.modules.transactions.TransactionServiceProvider;
import ca.ids.abms.plugins.PluginKey;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@SuppressWarnings("unused")
public class KcaaErpTransactionServiceProvider extends TransactionServiceProvider {

    /**
     * List of transaction payment mechanisms supported by the KCAA ERP integration. Currently, only
     * adjustment payment mechanisms is supported by this provider.
     */
    private static final List<TransactionPaymentMechanism> EXPORTABLE_MECHANISMS = new ArrayList<>();
    static {
        EXPORTABLE_MECHANISMS.add(TransactionPaymentMechanism.adjustment);
    }

    private final KcaaErpTransactionCacheable kcaaErpTransactionCacheable;
    private final KcaaErpTransactionService kcaaErpTransactionService;

    public KcaaErpTransactionServiceProvider(
        final KcaaErpTransactionCacheable kcaaErpTransactionCacheable,
        final KcaaErpTransactionService kcaaErpTransactionService
    ) {
        super(PluginKey.KCAA_ERP, true, false, EXPORTABLE_MECHANISMS);
        this.kcaaErpTransactionCacheable = kcaaErpTransactionCacheable;
        this.kcaaErpTransactionService = kcaaErpTransactionService;
    }

    /**
     * Handle credit note transaction on creation and update necessary ERP entities. All exceptions will
     * be handled and retried appropriately without affecting upstream `@Transactional` methods.
     *
     * @param transaction credit note transaction created in ABMS
     */
    @Override
    public void creditNoteCreated(Transaction transaction) {
        kcaaErpTransactionCacheable.createCreditNote(transaction);
    }

    /**
     * Handle transaction manual export and update necessary ERP entities without exception handling.
     * All ejections will be thrown and affect upstream `@Transactional` methods.
     *
     * @param transaction transaction to export
     * @param transactionPayments unused, KCAA ERP does not export payments
     */
    @Override
    public void export(final Transaction transaction, @SuppressWarnings("unused") final List<TransactionPayment> transactionPayments) {
        if (!transaction.getExported() && kcaaErpTransactionService.createCreditNote(transaction))
            kcaaErpTransactionService.exported(transaction);
    }
}
