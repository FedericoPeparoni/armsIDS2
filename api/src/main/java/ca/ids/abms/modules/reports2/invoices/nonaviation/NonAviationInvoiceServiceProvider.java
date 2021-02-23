package ca.ids.abms.modules.reports2.invoices.nonaviation;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.billings.InvoiceLineItemViewModel;
import ca.ids.abms.modules.common.services.AbstractPluginServiceProvider;
import ca.ids.abms.plugins.PluginKey;

import java.util.List;

/**
 * Used within {@link NonAviationInvoiceService} to preform additional plugin specific logic.
 */
public abstract class NonAviationInvoiceServiceProvider extends AbstractPluginServiceProvider {

    public NonAviationInvoiceServiceProvider(PluginKey pluginKey) {
        super(pluginKey);
    }

    /**
     * Validate point-of-sale invoice parameters. Throw necessary validation messages
     * using {@link ca.ids.abms.config.error.CustomParametrizedException}.
     *
     * @param account account pont-of-sale invoice will be applied against
     * @param invoiceLineItems list of all invoice line items included on point-of-sale invoice
     */
    public void validatePosInvoice(final Account account, final List<InvoiceLineItemViewModel> invoiceLineItems) {
        // default implementation ignored
    }

    /**
     * Return point-of-sale invoice currency for account and provided line items. Default implementation returns null
     * to indicate that no preferred currency is implemented.
     *
     * @param account account for point-of-sale invoice
     * @param invoiceLineItems invoice line items for point-of-sale invoice
     * @return preferred currency to use on point-of-sale invoices
     */
    public String preferredPosInvoiceCurrencyCode(final Account account, final List<InvoiceLineItemViewModel> invoiceLineItems) {
        return null;
    }
}
