package ca.ids.abms.modules.reports2.invoices.aviation;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.unifiedtaxes.UnifiedTaxInvoiceError;

import java.util.List;

public class AviationInvoice {

    public Account account() {
        return account;
    }
    public List <FlightMovement> flightList() {
        return flightList;
    }

    public AviationInvoiceData invoiceData() {
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

    public List <UnifiedTaxInvoiceError> unifiedTaxInvoiceErrors() {
    	return unifiedTaxInvoiceErrorList;
    }
    
    public AviationInvoice (
            final Account account,
            final List <FlightMovement> flightList,
            final AviationInvoiceData invoiceData,
            final BillingLedger billingLedger,
            final ReportDocument invoiceDocument) {
        this (account, flightList, invoiceData, billingLedger, invoiceDocument, null, null);
    }

    public AviationInvoice (
            final Account account,
            final List <FlightMovement> flightList,
            final AviationInvoiceData invoiceData,
            final BillingLedger billingLedger,
            final ReportDocument invoiceDocument,
            final Transaction transaction,
            final ReportDocument transactionDocument) {
        this.account = account;
        this.flightList = flightList;
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

    public void setUnifiedtaxErrors(List <UnifiedTaxInvoiceError> errors) {
    	 this.unifiedTaxInvoiceErrorList= errors; 
    }
    
    // ---------------- private ---------
    private final Account account;
    private final List <FlightMovement> flightList;

    private final AviationInvoiceData invoiceData;
    private final BillingLedger billingLedger;
    private final ReportDocument invoiceDocument;

    private Transaction transaction;
    private ReportDocument transactionDocument;
    private List <UnifiedTaxInvoiceError> unifiedTaxInvoiceErrorList;
}
