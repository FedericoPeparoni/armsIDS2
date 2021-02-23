package ca.ids.abms.modules.billings;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDateTime;

public class BillingLedgerCsvExportModel {

    private String account;

    private String invoiceNumber;

    @CsvProperty(date = true, value = "Invoice Date")
    private LocalDateTime invoicePeriodOrDate;

    private String invoiceType;

    @CsvProperty(value = "Status")
    private String invoiceStateType;

    @CsvProperty(date = true)
    private LocalDateTime paymentDueDate;

    @CsvProperty(value = "Created By")
    private String user;

    @CsvProperty(precision = 2)
    private Double invoiceAmount;

    private String invoiceCurrency;

    @CsvProperty(value = "Exchange Rate To USD", precision = 5)
    private Double invoiceExchange;

    @CsvProperty(date = true)
    private LocalDateTime invoiceDateOfIssue;

    private Boolean proforma;

    private Boolean exported;

    private String billingCentre;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDateTime getInvoicePeriodOrDate() {
        return invoicePeriodOrDate;
    }

    public void setInvoicePeriodOrDate(LocalDateTime invoicePeriodOrDate) {
        this.invoicePeriodOrDate = invoicePeriodOrDate;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceStateType() {
        return invoiceStateType;
    }

    public void setInvoiceStateType(String invoiceStateType) {
        this.invoiceStateType = invoiceStateType;
    }

    public LocalDateTime getPaymentDueDate() {
        return paymentDueDate;
    }

    public void setPaymentDueDate(LocalDateTime paymentDueDate) {
        this.paymentDueDate = paymentDueDate;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Double getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(Double invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getInvoiceCurrency() {
        return invoiceCurrency;
    }

    public void setInvoiceCurrency(String invoiceCurrency) {
        this.invoiceCurrency = invoiceCurrency;
    }

    public Double getInvoiceExchange() {
        return invoiceExchange;
    }

    public void setInvoiceExchange(Double invoiceExchange) {
        this.invoiceExchange = invoiceExchange;
    }

    public LocalDateTime getInvoiceDateOfIssue() {
        return invoiceDateOfIssue;
    }

    public void setInvoiceDateOfIssue(LocalDateTime invoiceDateOfIssue) {
        this.invoiceDateOfIssue = invoiceDateOfIssue;
    }

    public Boolean getProforma() {
        return proforma;
    }

    public void setProforma(Boolean proforma) {
        this.proforma = proforma;
    }

    public Boolean getExported() {
        return exported;
    }

    public void setExported(Boolean exported) {
        this.exported = exported;
    }

    public String getBillingCentre() {
        return billingCentre;
    }

    public void setBillingCentre(String billingCentre) {
        this.billingCentre = billingCentre;
    }
}
