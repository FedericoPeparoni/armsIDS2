package ca.ids.abms.modules.transactions;

import ca.ids.abms.modules.common.services.AbstractPluginServiceProvider;
import ca.ids.abms.plugins.PluginKey;

import java.util.Collections;
import java.util.List;

public abstract class TransactionServiceProvider extends AbstractPluginServiceProvider {

    /**
     * True to indicate that transaction credit note records are exported and the `exported`
     * column value is updated to reflect such on export. Only supports credit note adjustments.
     */
    private Boolean creditNoteExportSupport;

    /**
     * Return true to indicate that transaction payment records are exported and the `exported`
     * column value is updated to reflect such on export.
     */
    private Boolean paymentExportSupport;

    /**
     * List of supported mechanisms by implementation.
     */
    private List<TransactionPaymentMechanism> exportableMechanisms;

    public TransactionServiceProvider(PluginKey pluginKey) {
        this(pluginKey, false, false, Collections.emptyList());
    }

    public TransactionServiceProvider(
        final PluginKey pluginKey, final Boolean creditNoteExportSupport, final Boolean paymentExportSupport,
        final List<TransactionPaymentMechanism> exportableMechanisms
    ) {
        super(pluginKey);
        this.creditNoteExportSupport = creditNoteExportSupport;
        this.paymentExportSupport = paymentExportSupport;
        this.exportableMechanisms = exportableMechanisms;
    }

    /**
     * System created a transaction credit note.
     *
     * @param transaction transaction credit note created
     */
    public void creditNoteCreated(final Transaction transaction) {
        // default implementation ignored
    }

    /**
     * System created a transaction credit payment.
     *
     * @param transactionPayment transaction credit payment created
     */
    public void creditPaymentCreated(final TransactionPayment transactionPayment) {
        // default implementation ignored
    }

    /**
     * User requested Transaction to export.
     *
     * @param transaction transaction to export
     * @param transactionPayments associated transaction payments to export
     */
    public void export(final Transaction transaction, final List<TransactionPayment> transactionPayments) {
        // default implementation ignored
    }

    /**
     * Transaction was created.
     *
     * @param transaction transaction that was created
     */
    public void save(Transaction transaction) {
        // default implementation ignored
    }

    /**
     * List of supported mechanisms by implementation.
     *
     * @return list of mechanisms supported
     */
    List<TransactionPaymentMechanism> exportableMechanisms() {
        return exportableMechanisms;
    }

    /**
     * True if service provider exports Transactions and the `exported` columns values
     * will be managed by the service provider's implementation.
     *
     * @return true if export managed by implementation
     */
    Boolean isExportSupport() {
        return isExportSupportCreditNote() || isExportSupportPayment();
    }

    /**
     * True if service provider exports Transactions and the `exported` column value
     * will be managed by the service provider's implementation for given
     * transaction payment mechanism.
     *
     * @param mechanism transaction mechanism to check
     * @return true if export managed by implementation
     */
    Boolean isExportSupport(TransactionPaymentMechanism mechanism) {

        // only check if export supported
        if (!isExportSupport())
            return false;

        // default is to compare against supported mechanisms
        List<TransactionPaymentMechanism> supported = exportableMechanisms();
        return supported != null && supported.contains(mechanism);
    }

    /**
     * True if service provider exports Transaction Credit Notes and the `exported` column value
     * will be managed by the service provider's implementation.
     *
     * @return true if credit note export managed by implementation
     */
    Boolean isExportSupportCreditNote() {
        return creditNoteExportSupport;
    }

    /**
     * True if service provider exports Transaction Payments and the `exported` column value
     * will be managed by the service provider's implementation.
     *
     * @return true if payments export managed by implementation
     */
    Boolean isExportSupportPayment() {
        return paymentExportSupport;
    }
}
