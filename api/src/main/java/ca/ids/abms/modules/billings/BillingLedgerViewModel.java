package ca.ids.abms.modules.billings;

import ca.ids.abms.modules.accounts.AccountComboViewModel;
import ca.ids.abms.modules.billingcenters.BillingCenterComboViewModel;
import ca.ids.abms.modules.common.dto.EmbeddedFileDto;
import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.common.enumerators.InvoiceType;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.flightmovements.category.FlightmovementCategory;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class BillingLedgerViewModel extends EmbeddedFileDto {

    private Integer id;

    private AccountComboViewModel account;

    @NotNull
    private LocalDateTime invoicePeriodOrDate;

    @NotNull
    private InvoiceType invoiceType;

    @NotNull
    private InvoiceStateType invoiceStateType;

    @NotNull
    private LocalDateTime paymentDueDate;

    private String user;

    @NotNull
    private String invoiceNumber;

    @NotNull
    private Double invoiceAmount;

    @NotNull
    private Currency invoiceCurrency;

    @NotNull
    private Double invoiceExchange;
    
    private Currency targetCurrency;

    private Double invoiceExchangeToAnsp;

    @NotNull
    private LocalDateTime invoiceDateOfIssue;

    private Boolean exported;

    private Double amountOwing;

    private LocalDateTime finalPaymentDate;

    private String paymentMode;

    private FlightmovementCategory flightmovementCategory;

    private String transactionDescription;

    private Boolean proforma;

    private Boolean pointOfSale;

    private BillingCenterComboViewModel billingCenter;

    public AccountComboViewModel getAccount() {
        return account;
    }

    public Double getAmountOwing() {
        return amountOwing;
    }

    public Boolean getExported() {
        return exported;
    }

    public LocalDateTime getFinalPaymentDate() {
        return finalPaymentDate;
    }

    public Integer getId() {
        return id;
    }

    public Double getInvoiceAmount() {
        return invoiceAmount;
    }

    public Currency getInvoiceCurrency() {
        return invoiceCurrency;
    }

    public LocalDateTime getInvoiceDateOfIssue() {
        return invoiceDateOfIssue;
    }

    public Double getInvoiceExchange() {
        return invoiceExchange;
    }

    public Double getInvoiceExchangeToAnsp() {
        return invoiceExchangeToAnsp;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public LocalDateTime getInvoicePeriodOrDate() {
        return invoicePeriodOrDate;
    }

    public InvoiceStateType getInvoiceStateType() {
        return invoiceStateType;
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public LocalDateTime getPaymentDueDate() {
        return paymentDueDate;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public FlightmovementCategory getFlightmovementCategory() {
        return flightmovementCategory;
    }

    public void setAccount(AccountComboViewModel aAccount) {
        account = aAccount;
    }

    public void setAmountOwing(Double amountOwing) {
        this.amountOwing = amountOwing;
    }

    public void setExported(Boolean aExported) {
        exported = aExported;
    }

    public void setFinalPaymentDate(LocalDateTime finalPaymentDate) {
        this.finalPaymentDate = finalPaymentDate;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setInvoiceAmount(Double aInvoiceAmount) {
        invoiceAmount = aInvoiceAmount;
    }

    public void setInvoiceCurrency(Currency aInvoiceCurrency) {
        invoiceCurrency = aInvoiceCurrency;
    }

    public void setInvoiceDateOfIssue(LocalDateTime aInvoiceDateOfIssue) {
        invoiceDateOfIssue = aInvoiceDateOfIssue;
    }

    public void setInvoiceExchange(Double aInvoiceExchange) {
        invoiceExchange = aInvoiceExchange;
    }

    public void setInvoiceExchangeToAnsp(Double invoiceExchangeToAnsp) {
        this.invoiceExchangeToAnsp = invoiceExchangeToAnsp;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public void setInvoicePeriodOrDate(LocalDateTime aInvoicePeriodOrDate) {
        invoicePeriodOrDate = aInvoicePeriodOrDate;
    }

    public void setInvoiceStateType(InvoiceStateType aInvoiceStateType) {
        invoiceStateType = aInvoiceStateType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public void setPaymentDueDate(LocalDateTime aPaymentDueDate) {
        paymentDueDate = aPaymentDueDate;
    }

    public void setPaymentMode(String aPaymentMode) {
        paymentMode = aPaymentMode;
    }

    public void setTargetCurrency(Currency aTargetCurrency) {
        targetCurrency = aTargetCurrency;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setFlightmovementCategory(FlightmovementCategory flightmovementCategory) {
        this.flightmovementCategory = flightmovementCategory;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

    public Boolean getProforma() {
        return proforma;
    }

    public void setProforma(Boolean proforma) {
        this.proforma = proforma;
    }

    public BillingCenterComboViewModel getBillingCenter() {
        return billingCenter;
    }

    public void setBillingCenter(BillingCenterComboViewModel billingCenter) {
        this.billingCenter = billingCenter;
    }

    public Boolean getPointOfSale() {
        return pointOfSale;
    }

    public void setPointOfSale(Boolean pointOfSale) {
        this.pointOfSale = pointOfSale;
    }
}
