package ca.ids.abms.modules.reports2.invoices.interest;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.billings.BillingLedgerService;
import ca.ids.abms.modules.reports2.common.ReportFormat;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.reports2.common.RoundingUtils;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.modules.utilities.invoices.InvoiceSequenceNumberHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Service
public class InterestInvoiceService {

    private static final Logger LOG = LoggerFactory.getLogger (InterestInvoiceService.class);
    private static final ReportFormat DEFAULT_FORMAT = ReportFormat.pdf;

    private final ReportHelper reportHelper;
    private final BillingLedgerService billingLedgerService;
    private final InterestInvoiceDocumentCreator interestInvoiceDocumentCreator;
    private final InvoiceSequenceNumberHelper invoiceSequenceNumberHelper;
    private final CurrencyUtils currencyUtils;
    private final RoundingUtils roundingUtils;
    private final SystemConfigurationService systemConfigurationService;

    public InterestInvoiceService(final ReportHelper reportHelper,
                                  final BillingLedgerService billingLedgerService,
                                  final InterestInvoiceDocumentCreator interestInvoiceDocumentCreator,
                                  final InvoiceSequenceNumberHelper invoiceSequenceNumberHelper,
                                  final CurrencyUtils currencyUtils,
                                  final RoundingUtils roundingUtils,
                                  final SystemConfigurationService systemConfigurationService) {

        this.reportHelper = reportHelper;
        this.billingLedgerService = billingLedgerService;
        this.interestInvoiceDocumentCreator = interestInvoiceDocumentCreator;
        this.invoiceSequenceNumberHelper = invoiceSequenceNumberHelper;
        this.currencyUtils = currencyUtils;
        this.roundingUtils = roundingUtils;
        this.systemConfigurationService = systemConfigurationService;
    }

    /**
     * Create an interest invoice
     */
    public void createInterestInvoice (final Integer accountId, final Double total, final Double defaultPenalty,
                                       final Double punitivePenalty, final LocalDateTime exchangeRateDate, final long overdueDays,
                                       final double overdueInvoiceAmount, final String overdueInvoiceNumber) {

        final ZonedDateTime zdtNow = ZonedDateTime.now (ReportHelper.UTC_ZONE_ID);
        final LocalDateTime ldtNow = zdtNow.toLocalDateTime();
        final BillingCenter billingCenter = reportHelper.getBillingCenterOfCurrentUser();
        final Account account = reportHelper.getAccount (accountId);

        LOG.info("Creating interest invoice for billingCenter={}/{}, account={}/{}",
            billingCenter.getId(),
            billingCenter.getName(),
            account.getId(),
            account.getName());

        // Create the invoice
        final InterestInvoiceCreator invoiceCreator = this.interestInvoiceCreator(ldtNow, exchangeRateDate);
        final InterestInvoice invoice = invoiceCreator.createInterestInvoice(account, DEFAULT_FORMAT, total, defaultPenalty, punitivePenalty,
            overdueDays, overdueInvoiceAmount, overdueInvoiceNumber);

        invoice.invoiceDocument();
    }

    // ------------------------ private -----------------------------

    /**
     * Construct a new creator object
     */
    private InterestInvoiceCreator interestInvoiceCreator(final LocalDateTime ldtNow, final LocalDateTime exchangeRateDate) {
        return new InterestInvoiceCreator (
               reportHelper,
               billingLedgerService,
               interestInvoiceDocumentCreator,
               invoiceSequenceNumberHelper,
               ldtNow,
               currencyUtils,
               true,
               ldtNow,
               roundingUtils,
               systemConfigurationService,
               exchangeRateDate);
    }
}
