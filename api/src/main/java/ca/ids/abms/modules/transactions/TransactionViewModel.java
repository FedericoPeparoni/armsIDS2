package ca.ids.abms.modules.transactions;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.bankaccount.BankAccount;
import ca.ids.abms.modules.chargesadjustment.ChargesAdjustment;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.transactionapprovals.TransactionApprovalsViewModel;
import ca.ids.abms.modules.transactions.error.InterestInvoiceError;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class TransactionViewModel {

    private Integer id;

    @NotNull
    private Account account;

    private LocalDateTime transactionDateTime;

    @NotNull
    private String description;

    @NotNull
    private TransactionType transactionType;

    private Boolean hasReceiptDocument;

    private Boolean hasApprovalDocument;

    private String approvalDocumentName;

    private String approvalDocumentType;

    private Boolean hasSupportingDocument;

    private String supportingDocumentName;

    private String supportingDocumentType;

    @NotNull
    private Double amount;

    @NotNull
    private Currency currency;

    private Double exchangeRate;

    private Currency targetCurrency;

    private Double exchangeRateToAnsp;

    private Double balance;

    @NotNull
    private Boolean exported;

    private List<Integer> billingLedgerIds;

    @NotNull
    private TransactionPaymentMechanism paymentMechanism;

    @NotNull
    private String paymentReferenceNumber;

    private Collection<ChargesAdjustment> chargesAdjustment;

    private Double paymentAmount;

    private Currency paymentCurrency;

    private Double paymentExchangeRate;

    private String receiptNumber;

    private String kraClerkName;

    private String kraReceiptNumber;

    private Boolean paymentsExported;

    private LocalDateTime paymentDate;

    @Size(min = 1, max = BankAccount.NAME_MAX_LENGTH)
    private String bankAccountName;

    @Size(min = 1, max = BankAccount.NUMBER_MAX_LENGTH)
    private String bankAccountNumber;

    @Size(min = 1, max = BankAccount.EXTERNAL_ACCOUTING_SYSTEM_ID_MAX_LENGTH)
    private String bankAccountExternalAccountingSystemId;

    private List<InterestInvoiceError> interestInvoiceError;

    private List<TransactionApprovalsViewModel> transactionApprovals;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public LocalDateTime getTransactionDateTime() {
        return transactionDateTime;
    }

    public void setTransactionDateTime(LocalDateTime transactionDateTime) {
        this.transactionDateTime = transactionDateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Boolean getHasReceiptDocument() {
        return hasReceiptDocument;
    }

    public void setHasReceiptDocument(Boolean hasReceiptDocument) {
        this.hasReceiptDocument = hasReceiptDocument;
    }

    public Boolean getHasApprovalDocument() {
        return hasApprovalDocument;
    }

    public void setHasApprovalDocument(Boolean hasApprovalDocument) {
        this.hasApprovalDocument = hasApprovalDocument;
    }

    public String getApprovalDocumentName() {
        return approvalDocumentName;
    }

    public void setApprovalDocumentName(String approvalDocumentName) {
        this.approvalDocumentName = approvalDocumentName;
    }

    public String getApprovalDocumentType() {
        return approvalDocumentType;
    }

    public void setApprovalDocumentType(String approvalDocumentType) {
        this.approvalDocumentType = approvalDocumentType;
    }

    public Boolean getHasSupportingDocument() {
        return hasSupportingDocument;
    }

    public void setHasSupportingDocument(Boolean hasSupportingDocument) {
        this.hasSupportingDocument = hasSupportingDocument;
    }

    public String getSupportingDocumentName() {
        return supportingDocumentName;
    }

    public void setSupportingDocumentName(String supportingDocumentName) {
        this.supportingDocumentName = supportingDocumentName;
    }

    public String getSupportingDocumentType() {
        return supportingDocumentType;
    }

    public void setSupportingDocumentType(String supportingDocumentType) {
        this.supportingDocumentType = supportingDocumentType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(Currency targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public Double getExchangeRateToAnsp() {
        return exchangeRateToAnsp;
    }

    public void setExchangeRateToAnsp(Double exchangeRateToAnsp) {
        this.exchangeRateToAnsp = exchangeRateToAnsp;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Boolean getExported() {
        return exported;
    }

    public void setExported(Boolean exported) {
        this.exported = exported;
    }

    public List<Integer> getBillingLedgerIds() {
        return billingLedgerIds;
    }

    public void setBillingLedgerIds(List<Integer> billingLedgerIds) {
        this.billingLedgerIds = billingLedgerIds;
    }

    public TransactionPaymentMechanism getPaymentMechanism() {
        return paymentMechanism;
    }

    public void setPaymentMechanism(TransactionPaymentMechanism paymentMechanism) {
        this.paymentMechanism = paymentMechanism;
    }

    public String getPaymentReferenceNumber() {
        return paymentReferenceNumber;
    }

    public void setPaymentReferenceNumber(String paymentReferenceNumber) {
        this.paymentReferenceNumber = paymentReferenceNumber;
    }

    public Collection<ChargesAdjustment> getChargesAdjustment() {
        return chargesAdjustment;
    }

    public void setChargesAdjustment(Collection<ChargesAdjustment> chargesAdjustment) {
        this.chargesAdjustment = chargesAdjustment;
    }

    public Double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Currency getPaymentCurrency() {
        return paymentCurrency;
    }

    public void setPaymentCurrency(Currency paymentCurrency) {
        this.paymentCurrency = paymentCurrency;
    }

    public Double getPaymentExchangeRate() {
        return paymentExchangeRate;
    }

    public void setPaymentExchangeRate(Double paymentExchangeRate) {
        this.paymentExchangeRate = paymentExchangeRate;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getKraClerkName() {
        return kraClerkName;
    }

    public void setKraClerkName(String kraClerkName) {
        this.kraClerkName = kraClerkName;
    }

    public String getKraReceiptNumber() {
        return kraReceiptNumber;
    }

    public void setKraReceiptNumber(String kraReceiptNumber) {
        this.kraReceiptNumber = kraReceiptNumber;
    }

    public Boolean getPaymentsExported() {
        return paymentsExported;
    }

    public void setPaymentsExported(Boolean paymentsExported) {
        this.paymentsExported = paymentsExported;
    }

	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getBankAccountExternalAccountingSystemId() {
        return bankAccountExternalAccountingSystemId;
    }

    public void setBankAccountExternalAccountingSystemId(String bankAccountExternalAccountingSystemId) {
        this.bankAccountExternalAccountingSystemId = bankAccountExternalAccountingSystemId;
    }

    public List<InterestInvoiceError> getInterestInvoiceError() {
        return interestInvoiceError;
    }

    public void setInterestInvoiceError(List<InterestInvoiceError> interestInvoiceError) {
        this.interestInvoiceError = interestInvoiceError;
    }

    public List<TransactionApprovalsViewModel> getTransactionApprovals() {
        return transactionApprovals;
    }

    public void setTransactionApprovals(List<TransactionApprovalsViewModel> transactionApprovals) {
        this.transactionApprovals = transactionApprovals;
    }
}
