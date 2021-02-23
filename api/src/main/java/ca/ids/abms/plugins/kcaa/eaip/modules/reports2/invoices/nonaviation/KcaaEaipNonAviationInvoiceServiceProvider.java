package ca.ids.abms.plugins.kcaa.eaip.modules.reports2.invoices.nonaviation;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.billings.InvoiceLineItemViewModel;
import ca.ids.abms.modules.common.enumerators.ExternalDatabaseForCharge;
import ca.ids.abms.modules.reports2.invoices.nonaviation.NonAviationInvoiceServiceProvider;
import ca.ids.abms.plugins.PluginKey;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class KcaaEaipNonAviationInvoiceServiceProvider extends NonAviationInvoiceServiceProvider {

    private static final Logger LOG = LoggerFactory.getLogger (KcaaEaipNonAviationInvoiceServiceProvider.class);

    public KcaaEaipNonAviationInvoiceServiceProvider() {
        super(PluginKey.KCAA_EAIP);
    }

    /**
     * Validate that point-of-sale invoices that include eAIP invoice line item requisition numbers
     * are able to be provided in the correct invoice currency.
     *
     * US 107384: eAIP invoice line items must be billed in the same currency found by requisition number.
     * If the account currency is different from the requisition number currency, make sure no additional
     * line items are included on the invoice.
     */
    @Override
    public void validatePosInvoice(
        final Account account, final List<InvoiceLineItemViewModel> invoiceLineItems
    ) {
        Preconditions.checkArgument(account != null && account.getInvoiceCurrency() != null);
        if (invoiceLineItems == null || invoiceLineItems.isEmpty()) return;

        // by default, all point-of-sale invoices are created using the account currency
        String invoiceCurrencyCode = account.getInvoiceCurrency().getCurrencyCode();
        List<String> lineItemCurrencies = invoiceLineItems.stream()
            .map(item -> resolveLineItemCurrency(item, invoiceCurrencyCode))
            .distinct().collect(Collectors.toList());

        // point-of-sale invoices can only contain one currency for all line items
        if (lineItemCurrencies.size() > 1) {
            LOG.warn("Cannot generate point-of-sale invoice as it contains line items with different currencies");
            throw new CustomParametrizedException("ERR_LINE_ITEMS_WITH_DIFFERENT_CURRENCIES");
        }
    }

    /**
     * Return the point-of-sale invoice currency code for the provided line items. Will return eAIP invoice currency
     * code if there is a requisition number invoice line item with a different currency then the account invoice
     * currency, default at the time of this writing.
     *
     * @param account account for point-of-sale invoice
     * @param invoiceLineItems invoice line items for point-of-sale invoice
     * @return currency to use on point-of-sale invoice for provided line items other then default
     */
    @Override
    public String preferredPosInvoiceCurrencyCode(final Account account, final List<InvoiceLineItemViewModel> invoiceLineItems) {

        // validate that necessary information is available to resolve point-of-sale invoice currency code
        if (account == null || account.getInvoiceCurrency() == null ||
            StringUtils.isBlank(account.getInvoiceCurrency().getCurrencyCode()) || invoiceLineItems == null ||
            invoiceLineItems.isEmpty()) return null;

        // find all line item invoice currencies that are different then the default account invoice currency
        String defaultCurrencyCode = account.getInvoiceCurrency().getCurrencyCode();
        List<String> lineItemCurrencies = invoiceLineItems.stream()
            .map(i -> resolveLineItemCurrency(i, defaultCurrencyCode))
            .filter(i -> !StringUtils.equalsIgnoreCase(i, defaultCurrencyCode))
            .distinct().collect(Collectors.toList());

        // should only return one non-default invoice currency if validatePosInvoice was run above
        if (lineItemCurrencies.size() > 1)
            throw new IllegalStateException("Cannot contain more then one unique currency on a point-of-sale invoice");

        // return single invoice currency that is non-default or null to indicate that the default should be used
        return lineItemCurrencies.isEmpty() || StringUtils.isBlank(lineItemCurrencies.get(0))
            ? null : lineItemCurrencies.get(0);
    }

    /**
     * Resolve the currency code for the provided line item.
     *
     * @param lineItem line item to resolve currency code
     * @param defaultCurrencyCode currency code used as the default point-of-sale currency
     * @return currency code of line item
     */
    private String resolveLineItemCurrency(final InvoiceLineItemViewModel lineItem, final String defaultCurrencyCode) {
        Preconditions.checkArgument(StringUtils.isNotBlank(defaultCurrencyCode));

        // if line item is a requisition number from external database source EAIP
        // then the line item currency for invoicing is the external requisition currency
        if (lineItem != null && lineItem.getRequisition() != null
            && ObjectUtils.equals(lineItem.getRequisition().getExternalDatabaseForCharge(), ExternalDatabaseForCharge.EAIP)
            && StringUtils.isNotBlank(lineItem.getRequisition().getReqCurrency())) {
            return lineItem.getRequisition().getReqCurrency();
        }

        // the currency for all other line items is the default currency
        // at the time of this writing, point-of-sale invoice currency is always the account currency
        return defaultCurrencyCode;
    }
}
