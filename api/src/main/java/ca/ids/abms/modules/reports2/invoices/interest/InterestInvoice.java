package ca.ids.abms.modules.reports2.invoices.interest;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.transactions.Transaction;

public class InterestInvoice {

    private final Account account;
    private final InterestInvoiceData invoiceData;
    private final BillingLedger billingLedger;
    private final ReportDocument invoiceDocument;
    private Transaction transaction;
    private ReportDocument transactionDocument;

    public Account account() {
        return account;
    }

    public InterestInvoiceData invoiceData() {
        return invoiceData;
    }

    public BillingLedger billingLedger() {
        return billingLedger;
    }

    public ReportDocument invoiceDocument() {
        return invoiceDocument;
    }

    public Transaction transaction() {
        return transaction;
    }

    public ReportDocument transactionDocument() {
        return transactionDocument;
    }

    public InterestInvoice (final Account account,
                            final InterestInvoiceData invoiceData,
                            final BillingLedger billingLedger,
                            final ReportDocument invoiceDocument) {
        this (account, invoiceData, billingLedger, invoiceDocument, null, null);
    }

    public InterestInvoice (final Account account,
                            final InterestInvoiceData invoiceData,
                            final BillingLedger billingLedger,
                            final ReportDocument invoiceDocument,
                            final Transaction transaction,
                            final ReportDocument transactionDocument) {

        this.account = account;
        this.invoiceData = invoiceData;
        this.billingLedger = billingLedger;
        this.invoiceDocument = invoiceDocument;
        this.transaction = transaction;
        this.transactionDocument = transactionDocument;
    }
}
