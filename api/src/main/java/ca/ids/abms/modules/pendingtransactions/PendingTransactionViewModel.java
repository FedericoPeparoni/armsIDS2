package ca.ids.abms.modules.pendingtransactions;

import ca.ids.abms.modules.accounts.AccountViewModel;
import ca.ids.abms.modules.billings.BillingLedgerViewModel;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.pendingtransactionapprovals.PendingTransactionApprovalsViewModel;
import ca.ids.abms.modules.transactions.TransactionPaymentMechanism;
import ca.ids.abms.modules.transactions.TransactionType;
import ca.ids.abms.modules.util.models.VersionedViewModel;
import ca.ids.abms.modules.workflows.ApprovalWorkflowViewModel;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class PendingTransactionViewModel extends VersionedViewModel {

    private Integer id;

    @NotNull
    private AccountViewModel account;

    @NotNull
    private LocalDateTime transactionDateTime;

    @NotNull
    @Size(max = 100)
    private String description;

    @NotNull
    private TransactionType transactionType;

    @NotNull
    private Double localAmount;

    @NotNull
    private Currency localCurrency;

    @NotNull
    private Double exchangeRateToUsd;

    @NotNull
    private Double exchangeRateToAnsp;

    @NotNull
    private TransactionPaymentMechanism paymentMechanism;

    @NotNull
    private String paymentReferenceNumber;

    @NotNull
    private Double paymentAmount;

    @NotNull
    private Currency paymentCurrency;

    @NotNull
    private Double paymentExchangeRate;

    @NotNull
    private ApprovalWorkflowViewModel currentApprovalLevel;

    private ApprovalWorkflowViewModel previousApprovalLevel;

    @NotNull
    private String relatedInvoices;

    private List<PendingChargeAdjustmentViewModel> pendingChargeAdjustments;

    private List<BillingLedgerViewModel> detailedInvoices;

    private Boolean canApprove;

    private Boolean canReject;

    private Boolean hasApprovalDocument;

    private String approvalDocumentName;

    private String approvalDocumentType;

    private Boolean hasSupportingDocument;

    private String supportingDocumentName;

    private String supportingDocumentType;

    private Set<PendingTransactionApprovalsViewModel> pendingTransactionApprovals;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AccountViewModel getAccount() {
        return account;
    }

    public void setAccount(AccountViewModel account) {
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

    public Double getLocalAmount() {
        return localAmount;
    }

    public void setLocalAmount(Double localAmount) {
        this.localAmount = localAmount;
    }

    public Currency getLocalCurrency() {
        return localCurrency;
    }

    public void setLocalCurrency(Currency localCurrency) {
        this.localCurrency = localCurrency;
    }

    public Double getExchangeRateToUsd() {
        return exchangeRateToUsd;
    }

    public void setExchangeRateToUsd(Double exchangeRateToUsd) {
        this.exchangeRateToUsd = exchangeRateToUsd;
    }

    public Double getExchangeRateToAnsp() {
        return exchangeRateToAnsp;
    }

    public void setExchangeRateToAnsp(Double exchangeRateToAnsp) {
        this.exchangeRateToAnsp = exchangeRateToAnsp;
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

    public ApprovalWorkflowViewModel getCurrentApprovalLevel() {
        return currentApprovalLevel;
    }

    public void setCurrentApprovalLevel(ApprovalWorkflowViewModel currentApprovalLevel) {
        this.currentApprovalLevel = currentApprovalLevel;
    }

    public ApprovalWorkflowViewModel getPreviousApprovalLevel() {
        return previousApprovalLevel;
    }

    public void setPreviousApprovalLevel(ApprovalWorkflowViewModel previousApprovalLevel) {
        this.previousApprovalLevel = previousApprovalLevel;
    }

    public String getRelatedInvoices() {
        return relatedInvoices;
    }

    public void setRelatedInvoices(String relatedInvoices) {
        this.relatedInvoices = relatedInvoices;
    }

    public List<BillingLedgerViewModel> getDetailedInvoices() {
        return detailedInvoices;
    }

    public void setDetailedInvoices(List<BillingLedgerViewModel> detailedInvoices) {
        this.detailedInvoices = detailedInvoices;
    }

    public Boolean getCanApprove() {
        return canApprove;
    }

    public void setCanApprove(Boolean canApprove) {
        this.canApprove = canApprove;
    }

    public Boolean getCanReject() {
        return canReject;
    }

    public void setCanReject(Boolean canReject) {
        this.canReject = canReject;
    }

    public List<PendingChargeAdjustmentViewModel> getPendingChargeAdjustments() {
        return pendingChargeAdjustments;
    }

    public void setPendingChargeAdjustments(List<PendingChargeAdjustmentViewModel> pendingChargeAdjustments) {
        this.pendingChargeAdjustments = pendingChargeAdjustments;
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

    public Set<PendingTransactionApprovalsViewModel> getPendingTransactionApprovals() {
        return pendingTransactionApprovals;
    }

    public void setPendingTransactionApprovals(Set<PendingTransactionApprovalsViewModel> pendingTransactionApprovals) {
        this.pendingTransactionApprovals = pendingTransactionApprovals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PendingTransactionViewModel that = (PendingTransactionViewModel) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PendingTransaction{" +
            "id=" + id +
            ", transactionDateTime=" + transactionDateTime +
            ", description='" + description + '\'' +
            ", transactionType=" + transactionType +
            ", localAmount=" + localAmount +
            ", localCurrency=" + localCurrency +
            ", exchangeRateToUsd=" + exchangeRateToUsd +
            ", exchangeRateToAnsp=" + exchangeRateToAnsp +
            ", paymentMechanism=" + paymentMechanism +
            ", paymentReferenceNumber='" + paymentReferenceNumber + '\'' +
            ", paymentAmount=" + paymentAmount +
            ", paymentCurrency=" + paymentCurrency +
            ", paymentExchangeRate=" + paymentExchangeRate +
            ", currentApprovalLevel=" + currentApprovalLevel +
            ", previousApprovalLevel=" + previousApprovalLevel +
            ", relatedInvoices='" + relatedInvoices + '\'' +
            '}';
    }
}
