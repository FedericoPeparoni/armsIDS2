package ca.ids.abms.modules.billings;

import ca.ids.abms.modules.common.enumerators.InvoiceType;
import ca.ids.abms.modules.common.services.AbstractPluginServiceProvider;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.plugins.PluginKey;

import java.util.Collections;
import java.util.List;

public abstract class BillingLedgerServiceProvider extends AbstractPluginServiceProvider {

    public BillingLedgerServiceProvider(PluginKey pluginKey) {
        super(pluginKey);
    }

    /**
     * Billing Ledger was approved.
     *
     * @param billingLedger billing ledger that was approved
     */
    public void approve(final BillingLedger billingLedger) {
        // default implementation ignored
    }

    /**
     * Billing Ledger was created.
     *
     * @param billingledger billing ledger that was created
     */
    public void created(final BillingLedger billingledger) {
        // default implementation ignored
    }

    /**
     * Billing Ledger was created with associated flight movements.
     *
     * @param billingLedger billing ledger that was created
     * @param flightMovements associated flight movements
     */
    public void created(final BillingLedger billingLedger, final List<FlightMovement> flightMovements) {
        // default implementation ignored
    }

    /**
     * User requested Billing Ledger to export.
     *
     * @param billingLedger billing ledger to export
     */
    public void export(final BillingLedger billingLedger) {
        // default implementation ignored
    }

    /**
     * True if service provider exports Billing Ledgers and the `exported` column value
     * will be managed by the service provider's implementation.
     *
     * @return true if export managed by implementation
     */
    public Boolean isExportSupport() {
        // default implementation is false
        return false;
    }

    /**
     * True if service provider exports Billing Ledgers of type and the `exported` column value
     * will be managed by the service provider's implementation.
     *
     * @param type invoice type to check
     * @return true if export managed by implementation
     */
    public Boolean isExportSupport(InvoiceType type) {

        // only check if export supported
        if (!isExportSupport())
            return false;

        // default is to compare against supported mechanisms
        List<InvoiceType> supported = exportableTypes();
        return supported != null && supported.contains(type);
    }

    /**
     * List of supported invoice types by implementation.
     *
     * @return list of invoice types supported
     */
    public List<InvoiceType> exportableTypes() {
        // default implementation is none
        return Collections.emptyList();
    }

    /**
     * Billing Ledger was saved.
     *
     * @param billingLedger billing ledger that was saved
     */
    public void save(final BillingLedger billingLedger) {
        // default implementation ignored
    }
}
