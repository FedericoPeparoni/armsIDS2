package ca.ids.abms.plugins.kcaa.eaip.modules.transactions;

import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.transactions.TransactionPayment;
import ca.ids.abms.modules.transactions.TransactionPaymentMechanism;
import ca.ids.abms.modules.transactions.TransactionServiceProvider;
import ca.ids.abms.plugins.PluginKey;
import com.google.common.base.Preconditions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@SuppressWarnings("unused")
public class KcaaEaipTransactionServiceProvider extends TransactionServiceProvider {

    /**
     * List of transaction payment mechanisms supported by the KCAA eAIP integration. Currently, all
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

    private final KcaaEaipTransactionCacheable kcaaEaipTransactionCacheable;
    private final KcaaEaipTransactionService kcaaEaipTransactionService;

    public KcaaEaipTransactionServiceProvider(
        final KcaaEaipTransactionCacheable kcaaEaipTransactionCacheable,
        final KcaaEaipTransactionService kcaaEaipTransactionService
    ) {
        super(PluginKey.KCAA_EAIP, false, true, EXPORTABLE_MECHANISMS);
        this.kcaaEaipTransactionCacheable = kcaaEaipTransactionCacheable;
        this.kcaaEaipTransactionService = kcaaEaipTransactionService;
    }

    /**
     * Handle transaction payments on created event and update necessary KCAA eAIP entities. All exceptions will
     * be handled and retried appropriately without affecting upstream `@Transactional` methods.
     *
     * @param transactionPayment transaction payment created in ABMS
     */
    @Override
    public void creditPaymentCreated(TransactionPayment transactionPayment) {
        kcaaEaipTransactionCacheable.createCreditPayment(transactionPayment);
    }

    /**
     * Handle transaction manual export and update necessary KCAA eAIP entities without exception handling.
     * All ejections will be thrown and affect upstream `@Transactional` methods.
     *
     * @param ignored transaction to export is ignored
     * @param transactionPayments transaction payment(s) to export
     */
    @Override
    public void export(final Transaction ignored, final List<TransactionPayment> transactionPayments) {
        Preconditions.checkArgument(transactionPayments != null);

        // for each payment, export if not already
        transactionPayments.stream()
            .filter(i -> !i.getExported())
            .forEach(this::export);
    }

    /**
     * Handle transaction payment manual export and update necessary KCAA eAIP entities without exception handling.
     * All ejections will be thrown and affect upstream `@Transactional` methods.
     *
     * @param transactionPayment transaction payment to export
     */
    private void export(final TransactionPayment transactionPayment) {
        if (!transactionPayment.getExported() && kcaaEaipTransactionService.createCreditPayment(transactionPayment))
            kcaaEaipTransactionService.exported(transactionPayment);
    }
}
