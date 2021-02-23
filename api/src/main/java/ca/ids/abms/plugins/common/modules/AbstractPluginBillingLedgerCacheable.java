package ca.ids.abms.plugins.common.modules;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.InvoiceOverduePenalty;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.plugins.common.utilities.PluginExceptionHelper;
import ca.ids.spring.cache.annotations.CacheableOnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractPluginBillingLedgerCacheable {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPluginBillingLedgerCacheable.class);

    private final AbstractPluginBillingLedgerService pluginBillingLedgerService;

    public AbstractPluginBillingLedgerCacheable(
        final AbstractPluginBillingLedgerService pluginBillingLedgerService
    ) {
        this.pluginBillingLedgerService = pluginBillingLedgerService;
    }

    /**
     * Cachable Billing Ledger wrapper for `create` service method. This catches
     * all exceptions and throws as CacheableMetadataException with necessary sql
     * statements if possible.
     *
     * @param billingLedger billing ledger to create
     * @param flightMovements flight movements associated to billing ledger
     * @param invoiceOverduePenalties invoice overdue penalties associated to billing ledger
     */
    @CacheableOnException(retry = true, exceptions = { CustomParametrizedException.class }, exclude = true)
    public void create(
        final BillingLedger billingLedger,
        final List<FlightMovement> flightMovements,
        final List<InvoiceOverduePenalty>invoiceOverduePenalties
    ) {
        try {
            // create necessary external records and throw exception if failed
            // else update billing ledger export status if successful
            if (pluginBillingLedgerService.createCacheable(billingLedger, flightMovements, invoiceOverduePenalties))
                pluginBillingLedgerService.exported(billingLedger);
        } catch (CustomParametrizedException ex) {
            throw ex; // throw custom parametrized exceptions back up the stack
        } catch (Exception ex) {
            // handle all other exceptions as cacheable runtime exception
            PluginExceptionHelper.logExportException(LOG, ex, billingLedger);
            throw PluginExceptionHelper.handleExportException(ex, pluginBillingLedgerService
                .createStatement(billingLedger, flightMovements, invoiceOverduePenalties));
        }
    }
}
