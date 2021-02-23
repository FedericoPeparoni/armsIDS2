package ca.ids.abms.modules.reports2.invoices.overdueinvoice;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.BillingLedgerService;
import ca.ids.abms.modules.reports2.common.ReportFormat;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.utilities.invoices.InvoiceSequenceNumberHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static ca.ids.abms.util.MiscUtils.nvl;

@Service
public class OverdueInvoiceDocumentService {

    private static final ReportFormat DFLT_FORMAT = ReportFormat.pdf;
    private final ReportHelper reportHelper;
    private final OverdueInvoiceDocumentCreator overdueInvoiceDocumentCreator;
    private final InvoiceSequenceNumberHelper invoiceSequenceNumberHelper;
    private final BillingLedgerService billingLedgerService;

    public OverdueInvoiceDocumentService(
        final ReportHelper reportHelper,
        final OverdueInvoiceDocumentCreator overdueInvoiceDocumentCreator,
        final InvoiceSequenceNumberHelper invoiceSequenceNumberHelper,
        final BillingLedgerService billingLedgerService
    ){
        this.reportHelper = reportHelper;
        this.overdueInvoiceDocumentCreator = overdueInvoiceDocumentCreator;
        this.invoiceSequenceNumberHelper = invoiceSequenceNumberHelper;
        this.billingLedgerService = billingLedgerService;
    }

    @Transactional
    public void createOverdueInvoiceDocument (
        Account account,
        BillingLedger billingLedger,
        ReportFormat format
    ) {
        final ZonedDateTime zdtNow = ZonedDateTime.now (ReportHelper.UTC_ZONE_ID);
        final LocalDateTime ldtNow = zdtNow.toLocalDateTime();
        final ReportFormat reportFormat = nvl (format, DFLT_FORMAT);

        // Create the invoice
        final OverdueInvoiceCreator invoiceCreator = this.do_newCreator(ldtNow);
        invoiceCreator.createOverdueInvoiceDocument (account, billingLedger, reportFormat);

        billingLedgerService.createBillingLedgerAndTransaction(billingLedger,true,false);
    }

    // ------------------------ private -----------------------------

    /**
     * Construct a new creator object
     */
    private OverdueInvoiceCreator do_newCreator (
        final LocalDateTime ldtNow
    ) {
        return new OverdueInvoiceCreator (
            reportHelper,
            overdueInvoiceDocumentCreator,
            invoiceSequenceNumberHelper,
            ldtNow
        );
    }
}

