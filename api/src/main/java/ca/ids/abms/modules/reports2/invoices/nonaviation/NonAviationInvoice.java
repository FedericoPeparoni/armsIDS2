package ca.ids.abms.modules.reports2.invoices.nonaviation;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.transactions.Transaction;

class NonAviationInvoice {

    public Account account() {
        return account;
    }
    public NonAviationInvoiceData invoiceData() {
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
    
    public NonAviationInvoice (
            final Account account,
            final NonAviationInvoiceData invoiceData,
            final BillingLedger billingLedger,
            final ReportDocument invoiceDocument) {
        this (account, invoiceData, billingLedger, invoiceDocument, null, null);
    }
    
    public NonAviationInvoice (
            final Account account,
            final NonAviationInvoiceData invoiceData,
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
    
    public void payment (final Transaction transaction, final ReportDocument transactionDocument) {
        this.transaction = transaction;
        this.transactionDocument = transactionDocument;
    }

    // ---------------- private ---------
    private final Account account;
    
    private final NonAviationInvoiceData invoiceData;
    private final BillingLedger billingLedger;
    private final ReportDocument invoiceDocument;
    
    private Transaction transaction;
    private ReportDocument transactionDocument;
    
}
