package ca.ids.abms.modules.reports2.invoices.aviation.charges;

import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.reports2.common.CachedCurrencyConverter;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.reports2.common.RoundingUtils;
import ca.ids.abms.modules.system.BillingOrgCode;

public class AviationInvoiceChargeHelper {

    private final CachedCurrencyConverter cachedCurrencyConverter;
    private final Currency invoiceCurrency;
    private final BillingOrgCode billingOrgCode;
    private final ReportHelper reportHelper;
    private final RoundingUtils roundingUtils;

    /**
     * Create helper with required utility classes for invoice data generation.
     *
     * @param cachedCurrencyConverter cached currency convert class for invoice
     * @param invoiceCurrency invoice currency
     * @param billingOrgCode organization name in system configuration
     * @param reportHelper report helper utility class
     * @param roundingUtils rounding utility class
     */
    public AviationInvoiceChargeHelper(
        final CachedCurrencyConverter cachedCurrencyConverter,
        final Currency invoiceCurrency,
        final BillingOrgCode billingOrgCode,
        final ReportHelper reportHelper,
        final RoundingUtils roundingUtils
    ) {
        this.cachedCurrencyConverter = cachedCurrencyConverter;
        this.invoiceCurrency = invoiceCurrency;
        this.billingOrgCode = billingOrgCode;
        this.reportHelper = reportHelper;
        this.roundingUtils = roundingUtils;
    }

    public CachedCurrencyConverter getCachedCurrencyConverter() {
        return cachedCurrencyConverter;
    }

    public Currency getInvoiceCurrency() {
        return invoiceCurrency;
    }

    public BillingOrgCode getBillingOrgCode() {
        return billingOrgCode;
    }

    public ReportHelper getReportHelper() {
        return reportHelper;
    }

    public RoundingUtils getRoundingUtils() {
        return roundingUtils;
    }
}
