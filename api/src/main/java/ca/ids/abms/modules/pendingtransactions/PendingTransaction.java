package ca.ids.abms.modules.pendingtransactions;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.pendingtransactionapprovals.PendingTransactionApprovals;
import ca.ids.abms.modules.transactions.TransactionPaymentMechanism;
import ca.ids.abms.modules.transactions.TransactionType;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;
import ca.ids.abms.modules.workflows.ApprovalWorkflow;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@SuppressWarnings("WeakerAccess")
public class PendingTransaction extends VersionedAuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "account_id")
    @SearchableEntity
    private Account account;

    @NotNull
    private LocalDateTime transactionDateTime;

    @NotNull
    @Column(unique = true)
    @Size(max = 100)
    @SearchableText
    private String description;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "transaction_type_id")
    @SearchableEntity
    private TransactionType transactionType;

    @NotNull
    private Double localAmount;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "local_currency_id")
    private Currency localCurrency;

    @NotNull
    private Double exchangeRateToUsd;

    @NotNull
    private Double exchangeRateToAnsp;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @SearchableText
    private TransactionPaymentMechanism paymentMechanism;

    @NotNull
    @SearchableText
    private String paymentReferenceNumber;

    @NotNull
    private Double paymentAmount;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "payment_currency_id")
    private Currency paymentCurrency;

    @NotNull
    private Double paymentExchangeRate;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "current_approval_level")
    @SearchableEntity
    private ApprovalWorkflow currentApprovalLevel;

    @JoinColumn(name = "previous_approval_level")
    @ManyToOne
    @SearchableEntity
    private ApprovalWorkflow previousApprovalLevel;

    @NotNull
    private String relatedInvoices;

    @Transient
    private List<BillingLedger> detailedInvoices;

    @Transient
    private Boolean canApprove;

    @Transient
    private Boolean canReject;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pendingTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PendingChargeAdjustment> pendingChargeAdjustments;

    private byte[] approvalDocument;

    private String approvalDocumentName;

    private String approvalDocumentType;

    private byte[] supportingDocument;

    private String supportingDocumentName;

    private String supportingDocumentType;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pendingTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("approvalDateTime ASC")
    private List<PendingTransactionApprovals> pendingTransactionApprovals;

    public byte[] getApprovalDocument() {
        return approvalDocument;
    }

    public void setApprovalDocument(byte[] approvalDocument) {
        this.approvalDocument = approvalDocument;
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

    public ApprovalWorkflow getCurrentApprovalLevel() {
        return currentApprovalLevel;
    }

    public void setCurrentApprovalLevel(ApprovalWorkflow currentApprovalLevel) {
        this.currentApprovalLevel = currentApprovalLevel;
    }

    public ApprovalWorkflow getPreviousApprovalLevel() {
        return previousApprovalLevel;
    }

    public void setPreviousApprovalLevel(ApprovalWorkflow previousApprovalLevel) {
        this.previousApprovalLevel = previousApprovalLevel;
    }

    public String getRelatedInvoices() {
        return relatedInvoices;
    }

    public void setRelatedInvoices(String relatedInvoices) {
        this.relatedInvoices = relatedInvoices;
    }

    public List<BillingLedger> getDetailedInvoices() {
        return detailedInvoices;
    }

    public void setDetailedInvoices(List<BillingLedger> detailedInvoices) {
        this.detailedInvoices = detailedInvoices;
    }

    public List<PendingChargeAdjustment> getPendingChargeAdjustments() {
        return pendingChargeAdjustments;
    }

    public void setPendingChargeAdjustments(List<PendingChargeAdjustment> pendingChargeAdjustments) {
        this.pendingChargeAdjustments = pendingChargeAdjustments;
    }

    public byte[] getSupportingDocument() {
        return supportingDocument;
    }

    public void setSupportingDocument(byte[] supportingDocument) {
        this.supportingDocument = supportingDocument;
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

    public List<PendingTransactionApprovals> getPendingTransactionApprovals() {
        return pendingTransactionApprovals;
    }

    public void setPendingTransactionApprovals(List<PendingTransactionApprovals> pendingTransactionApprovals) {
        this.pendingTransactionApprovals = pendingTransactionApprovals;
    }

    /**
     * Return to UI a flag to indicate if approval document exists
     */
    @JsonInclude
    public Boolean getHasApprovalDocument() {
        return approvalDocument != null;
    }

    /**
     * Return to UI a flag to indicate if supporting document exists
     */
    @JsonInclude
    public Boolean getHasSupportingDocument() {
        return supportingDocument != null;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof PendingTransaction)) {
            return false;
        }

        PendingTransaction that = (PendingTransaction) o;

        return id != null ? id.equals(that.getId()) : that.getId() == null;

    }
    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (transactionDateTime != null ? transactionDateTime.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (transactionType != null ? transactionType.hashCode() : 0);
        result = 31 * result + (localAmount != null ? localAmount.hashCode() : 0);
        result = 31 * result + (localCurrency != null ? localCurrency.hashCode() : 0);
        result = 31 * result + (paymentMechanism != null ? paymentMechanism.hashCode() : 0);
        result = 31 * result + (paymentReferenceNumber != null ? paymentReferenceNumber.hashCode() : 0);
        result = 31 * result + (paymentAmount != null ? paymentAmount.hashCode() : 0);
        result = 31 * result + (paymentCurrency != null ? paymentCurrency.hashCode() : 0);
        return result;
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
            ", approvalDocumentName='" + approvalDocumentName + '\'' +
            ", approvalDocumentType='" + approvalDocumentType + '\'' +
            ", supportingDocumentName='" + supportingDocumentName + '\'' +
            ", supportingDocumentType='" + supportingDocumentType + '\'' +
            '}';
    }
}
