package ca.ids.abms.modules.reports2.invoices.overdueinvoice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.osgi.service.component.annotations.Component;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportFormat;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.translation.Translation;
import ca.ids.abms.modules.utilities.invoices.InvoiceSequenceNumberHelper;

/**
 * Create invoice data, PDF doc, ledger and transaction; one at a time
 */
@Component
public class OverdueInvoiceCreator {

    private final ReportHelper reportHelper;
    private final OverdueInvoiceDocumentCreator overdueInvoiceDocumentCreator;
    private final LocalDateTime ldtNow;
    private final BillingCenter billingCenter;
    private final DateTimeFormatter dateFormatter;

    public OverdueInvoiceCreator (
        final ReportHelper reportHelper,
        final OverdueInvoiceDocumentCreator overdueInvoiceDocumentCreator,
        final InvoiceSequenceNumberHelper invoiceSequenceNumberHelper,
        final LocalDateTime ldtNow
    ) {
        this.reportHelper = reportHelper;
        this.overdueInvoiceDocumentCreator = overdueInvoiceDocumentCreator;
        this.ldtNow = ldtNow;
        this.billingCenter = reportHelper.getCurrentUser() != null ? reportHelper.getBillingCenterOfCurrentUser() : reportHelper.getHQBillingCenter();
        this.dateFormatter = reportHelper.getDateFormat();
    }

    public void createOverdueInvoiceDocument(final Account account, BillingLedger billingLedger, final ReportFormat reportFormat) {
        do_createOverdueInvoiceDocument(account, billingLedger, reportFormat);
    }

    // ------------------------ private ----------------------------------

    private void do_createOverdueInvoiceDocument(Account account, BillingLedger billingLedger, ReportFormat reportFormat) {
        final Currency currency = billingLedger.getInvoiceCurrency();
        final OverdueInvoiceData data = new OverdueInvoiceData();

        data.global = new OverdueInvoiceData.Global();
        data.global.realInvoiceNumber = billingLedger.getInvoiceNumber();
        data.global.invoiceNumber = reportHelper.getDisplayInvoiceNumber(data.global.realInvoiceNumber, false);
        data.global.invoiceName = String.format (Translation.getLangByToken("Overdue penalty invoice") + " %s", data.global.invoiceNumber);
        data.global.invoiceIssueLocation = billingCenter.getName();
        data.global.invoiceDateStr = reportHelper.formatDateUtc (ldtNow, dateFormatter);
        data.global.accountId = account.getId();
        data.global.accountName = account.getName();
        data.global.invoiceCurrencyCode = account.getInvoiceCurrency().getCurrencyCode();
        data.global.billingName = account.getAviationBillingContactPersonName();
        data.global.billingAddress = account.getAviationBillingMailingAddress();
        data.global.billingContactTel = account.getAviationBillingPhoneNumber();
        data.global.totalAmount = billingLedger.getAmountOwing();
        data.global.totalAmountStr = reportHelper.formatCurrency (data.global.totalAmount, currency);
        data.global.invoiceCurrencyCode = currency.getCurrencyCode();
        data.global.totalAmountStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol (data.global.totalAmount, currency);

        final ReportDocument reportDocument = this.overdueInvoiceDocumentCreator.create(data, reportFormat);

        // Save PDF file in billing ledger
        reportHelper.setReportDocument (billingLedger, reportDocument);

        // persistence error is thrown if cascaded association collection is null
        // https://hibernate.atlassian.net/browse/HHH-9940
        billingLedger.setInvoiceLineItems(new ArrayList<>());
    }
}

