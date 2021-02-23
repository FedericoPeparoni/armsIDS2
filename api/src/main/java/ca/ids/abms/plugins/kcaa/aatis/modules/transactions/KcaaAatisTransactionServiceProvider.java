package ca.ids.abms.plugins.kcaa.aatis.modules.transactions;

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
public class KcaaAatisTransactionServiceProvider extends TransactionServiceProvider {

    /**
     * List of transaction payment mechanisms supported by the KCAA AATIS integration. Currently, all
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

    private final KcaaAatisTransactionCachable kcaaAatisTransactionCachable;
    private final KcaaAatisTransactionService kcaaAatisTransactionService;

    public KcaaAatisTransactionServiceProvider(
        final KcaaAatisTransactionCachable kcaaAatisTransactionCachable,
        final KcaaAatisTransactionService kcaaAatisTransactionService
    ) {
        super(PluginKey.KCAA_AATIS, false, true, EXPORTABLE_MECHANISMS);
        this.kcaaAatisTransactionCachable = kcaaAatisTransactionCachable;
        this.kcaaAatisTransactionService = kcaaAatisTransactionService;
    }

    /**
     * Handle transaction payments on created event and update necessary KCAA AATIS entities. All exceptions will
     * be handled and retried appropriately without affecting upstream `@Transactional` methods.
     *
     * @param transactionPayment transaction payment created in ABMS
     */
    @Override
    public void creditPaymentCreated(TransactionPayment transactionPayment) {
        kcaaAatisTransactionCachable.createCreditPayment(transactionPayment);
    }

    /**
     * Handle transaction manual export and update necessary KCAA AATIS entities without exception handling.
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
     * Handle transaction payment manual export and update necessary KCAA AATIS entities without exception handling.
     * All ejections will be thrown and affect upstream `@Transactional` methods.
     *
     * @param transactionPayment transaction payment to export
     */
    private void export(final TransactionPayment transactionPayment) {
        if (!transactionPayment.getExported() && kcaaAatisTransactionService.createCreditPayment(transactionPayment))
            kcaaAatisTransactionService.exported(transactionPayment);
    }
}
