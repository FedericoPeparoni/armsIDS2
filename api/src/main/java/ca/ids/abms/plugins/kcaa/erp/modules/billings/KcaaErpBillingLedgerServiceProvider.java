package ca.ids.abms.plugins.kcaa.erp.modules.billings;

import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.BillingLedgerServiceProvider;
import ca.ids.abms.modules.common.enumerators.InvoiceType;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.plugins.PluginKey;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class KcaaErpBillingLedgerServiceProvider extends BillingLedgerServiceProvider {

    private final KcaaErpBillingLedgerCacheable kcaaErpBillingLedgerCacheable;

    private final KcaaErpBillingLedgerService kcaaErpBillingLedgerService;

    public KcaaErpBillingLedgerServiceProvider(
        final KcaaErpBillingLedgerCacheable kcaaErpBillingLedgerCacheable,
        final KcaaErpBillingLedgerService kcaaErpBillingLedgerService
    ) {
        super(PluginKey.KCAA_ERP);
        this.kcaaErpBillingLedgerCacheable = kcaaErpBillingLedgerCacheable;
        this.kcaaErpBillingLedgerService = kcaaErpBillingLedgerService;
    }

    /**
     * Handle billing ledger on approve event and update necessary ERP entities. All exceptions will
     * be handled and retried appropriately without affecting upstream `@Transactional` methods.
     *
     * @param billingLedger billing ledger approved in ABMS
     */
    @Override
    public void approve(final BillingLedger billingLedger) {
        created(billingLedger);
    }

    /**
     * Handle billing ledger on creation and update necessary ERP entities. All exceptions will
     * be handled and retried appropriately without affecting upstream `@Transactional` methods.
     *
     * @param billingLedger billing ledger created in ABMS
     */
    @Override
    public void created(final BillingLedger billingLedger) {
        created(billingLedger, Collections.emptyList());
    }

    /**
     * Handle billing ledger and related flight movements on creation and update necessary ERP entities.
     * All exceptions will be handled and retried appropriately without affecting upstream `@Transactional`
     * methods.
     *
     * @param billingLedger billing ledger created in ABMS
     * @param flightMovements list of flight movements associated to billing ledger in ABMS
     */
    @Override
    public void created(final BillingLedger billingLedger, final List<FlightMovement> flightMovements) {
        kcaaErpBillingLedgerCacheable.create(billingLedger, flightMovements,
            kcaaErpBillingLedgerService.overduePenalties(billingLedger));
    }

    /**
     * Handle billing ledger manual export and update necessary ERP entities without exception handling.
     * All ejections will be thrown and affect upstream `@Transactional` methods.
     *
     * @param billingLedger billing ledger to export
     */
    @Override
    public void export(final BillingLedger billingLedger) {
        if (!billingLedger.getExported() && kcaaErpBillingLedgerService.create(billingLedger, Collections.emptyList(),
            kcaaErpBillingLedgerService.overduePenalties(billingLedger)))
            kcaaErpBillingLedgerService.exported(billingLedger);
    }

    /**
     * Return true to indicate that billing ledger records are exported to KCAA ERP and the `exported`
     * column value is updated to reflect such on export.
     *
     * @return true
     */
    @Override
    public Boolean isExportSupport() {
        return true;
    }

    /**
     * Return list of invoice types supported by the CAAB SAGE integration. Currently, all
     * types are supported by this provider.
     *
     * @return list of supported types
     */
    @Override
    public List<InvoiceType> exportableTypes() {
        List<InvoiceType> result = new ArrayList<>();
        result.add(InvoiceType.AVIATION_IATA);
        result.add(InvoiceType.AVIATION_NONIATA);
        result.add(InvoiceType.NON_AVIATION);
        result.add(InvoiceType.PASSENGER);
        result.add(InvoiceType.DEBIT_NOTE);
        result.add(InvoiceType.OVERDUE);
        return result;
    }
}
