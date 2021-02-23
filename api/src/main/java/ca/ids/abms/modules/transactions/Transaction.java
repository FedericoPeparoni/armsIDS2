package ca.ids.abms.modules.transactions;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.bankaccount.BankAccount;
import ca.ids.abms.modules.chargesadjustment.ChargesAdjustment;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.flightmovements.category.FlightmovementCategory;
import ca.ids.abms.modules.transactionapprovals.TransactionApprovals;
import ca.ids.abms.modules.util.models.AuditedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Transaction extends AuditedEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

	@ManyToOne
    @NotNull
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
    @SearchableEntity
    private TransactionType transactionType;

    @NotNull
    private Double amount;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "currency_id")
    @SearchableEntity
    private Currency currency;

    @NotNull
    private Double exchangeRate;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "target_currency_id")
    private Currency targetCurrency;

    @NotNull
    private Double exchangeRateToAnsp;
    
    @NotNull
    private Double balance;

    @NotNull
    private Boolean exported;

    @Transient
    private List<Integer> billingLedgerIds;

    @JsonIgnore
    private byte[] receiptDocument;

    @JsonIgnore
    private String receiptDocumentType;

    @JsonIgnore
    private String receiptDocumentFileName;

    @SearchableText
    private String receiptNumber;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @SearchableText
    private TransactionPaymentMechanism paymentMechanism;

    @NotNull
    @SearchableText
    private String paymentReferenceNumber;

    @JsonIgnore
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL,
        fetch = FetchType.LAZY)
    private Set<ChargesAdjustment> chargesAdjustment = new HashSet<>();

    @NotNull
    private Double paymentAmount;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "payment_currency_id")
    private Currency paymentCurrency;

    @NotNull
    private Double paymentExchangeRate;

    @Transient
    private String debitNoteNumber;

    @Transient
    private String kraClerkName;

    @Transient
    private String kraReceiptNumber;

    @JsonIgnore
    @OneToMany(mappedBy = "transaction")
    private Set<TransactionPayment> transactionPayments;

    private byte[] approvalDocument;

    private String approvalDocumentName;

    private String approvalDocumentType;

    private byte[] supportingDocument;

    private String supportingDocumentName;

    private String supportingDocumentType;

    //this property is used for aviation invoice generation to determine if currency should be taken from flight movement category
    @Transient
    private FlightmovementCategory flightmovementCategory;
    
    private LocalDateTime paymentDate;

    @Column(name = "bank_account_name", length = BankAccount.NAME_MAX_LENGTH)
    private String bankAccountName;

    @Column(name = "bank_account_number", length = BankAccount.NUMBER_MAX_LENGTH)
    private String bankAccountNumber;

    @Column(name = "bank_account_external_accounting_system_id", length = BankAccount.EXTERNAL_ACCOUTING_SYSTEM_ID_MAX_LENGTH)
    private String bankAccountExternalAccountingSystemId;

    @NotNull
    @SearchableText
    @Column(name = "receipt_seq_number_type")
    private String receiptSequenceNumberType;
    
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "transaction", cascade = CascadeType.ALL)
    @OrderBy("approvalDateTime ASC")
    private Set<TransactionApprovals> transactionApprovals;

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

    public Account getAccount() {
        return account;
    }    

    public Double getAmount() {
        return amount;
    }

    public Double getBalance() {
        return balance;
    }

    public List<Integer> getBillingLedgerIds() {
        return billingLedgerIds;
    }

    public Set<ChargesAdjustment> getChargesAdjustment() {
        return chargesAdjustment;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String getDebitNoteNumber() {
        return debitNoteNumber;
    }

    public String getDescription() {
        return description;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public Double getExchangeRateToAnsp() {
        return exchangeRateToAnsp;
    }

    public Boolean getExported() {
        return exported;
    }

    public FlightmovementCategory getFlightmovementCategory() {
		return flightmovementCategory;
	}

    public Integer getId() {
        return id;
    }

    public Double getPaymentAmount() {
        return paymentAmount;
    }

    public Currency getPaymentCurrency() {
        return paymentCurrency;
    }

    public Double getPaymentExchangeRate() {
        return paymentExchangeRate;
    }

    public TransactionPaymentMechanism getPaymentMechanism() {
        return paymentMechanism;
    }

    public String getPaymentReferenceNumber() {
        return paymentReferenceNumber;
    }

    public byte[] getReceiptDocument() {
        return receiptDocument;
    }

    public String getReceiptDocumentFileName() {
        return receiptDocumentFileName;
    }

    public String getReceiptDocumentType() {
        return receiptDocumentType;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public LocalDateTime getTransactionDateTime() {
        return transactionDateTime;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public Set<TransactionApprovals> getTransactionApprovals() {
        return transactionApprovals;
    }

    public void setTransactionApprovals(Set<TransactionApprovals> transactionApprovals) {
        this.transactionApprovals = transactionApprovals;
    }

    /**
     * Return to UI a flag to indicate if receipt document exists
     */
    @JsonInclude(JsonInclude.Include.ALWAYS)
	public Boolean getHasReceiptDocument() {
		return receiptDocument != null;
	}

    /**
     * Return to UI a flag to indicate if approval document exists
     */
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public Boolean getHasApprovalDocument() {
        return approvalDocument != null;
    }

    /**
     * Return to UI a flag to indicate if supporting document exists
     */
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public Boolean getHasSupportingDocument() {
        return supportingDocument != null;
    }

    public void setAccount(Account aAccount) {
        account = aAccount;
    }

    public void setAmount(Double aAmount) {
        amount = aAmount;
    }

    public void setBalance(Double aBalance) {
        balance = aBalance;
    }

    public void setBillingLedgerIds(List<Integer> billingLedgerIds) {
        this.billingLedgerIds = billingLedgerIds;
    }

    public void setChargesAdjustment(Set<ChargesAdjustment> chargesAdjustment) {
        this.chargesAdjustment = chargesAdjustment;
    }

    public void setCurrency(Currency aCurrency) {
        currency = aCurrency;
    }

    public void setDebitNoteNumber(String debitNoteNumber) {
        this.debitNoteNumber = debitNoteNumber;
    }

    public void setDescription(String aDescription) {
        description = aDescription;
    }

    public void setExchangeRate(Double aExchangeRate) {
        exchangeRate = aExchangeRate;
    }

    public void setExchangeRateToAnsp(Double exchangeRateToAnsp) {
        this.exchangeRateToAnsp = exchangeRateToAnsp;
    }

    public void setExported(Boolean aExported) {
        exported = aExported;
    }

    public void setFlightmovementCategory(FlightmovementCategory flightmovementCategory) {
		this.flightmovementCategory = flightmovementCategory;
	}

    public void setId(Integer aId) {
        id = aId;
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

    public Set<TransactionPayment> getTransactionPayments() {
        return transactionPayments;
    }

    public void setTransactionPayments(Set<TransactionPayment> transactionPayments) {
        this.transactionPayments = transactionPayments;
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
    
    public void setPaymentAmount(Double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public void setPaymentCurrency(Currency paymentCurrency) {
        this.paymentCurrency = paymentCurrency;
    }

    public void setPaymentExchangeRate(Double paymentExchangeRate) {
        this.paymentExchangeRate = paymentExchangeRate;
    }

    public void setPaymentMechanism(TransactionPaymentMechanism paymentMechanism) {
        this.paymentMechanism = paymentMechanism;
    }

    public void setPaymentReferenceNumber(String paymentReferenceNumber) {
        this.paymentReferenceNumber = paymentReferenceNumber;
    }

    public void setReceiptDocument(byte[] receiptDocument) {
        this.receiptDocument = receiptDocument;
    }

    public void setReceiptDocumentFileName(String receiptDocumentFileName) {
        this.receiptDocumentFileName = receiptDocumentFileName;
    }

    public void setReceiptDocumentType(String receiptDocumentType) {
        this.receiptDocumentType = receiptDocumentType;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }
    
    public void setTargetCurrency(Currency aTargetCurrency) {
        targetCurrency = aTargetCurrency;
    }

	public void setTransactionDateTime(LocalDateTime aTransactionDateTime) {
        transactionDateTime = aTransactionDateTime;
    }

	public void setTransactionType(TransactionType aTransactionType) {
        transactionType = aTransactionType;
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


    public String getReceiptSequenceNumberType() {
        return receiptSequenceNumberType;
    }

    public void setReceiptSequenceNumberType(String receiptSeqNumberType) {
        this.receiptSequenceNumberType = receiptSeqNumberType;
    }
    

    @Override
    public String toString() {
        return "Transaction [id=" + id + ", account=" + account + ", transactionDateTime=" + transactionDateTime +
                ", description=" + description + ", transactionType=" + transactionType + ", amount=" + amount +
                ", currency=" + currency + ", exchangeRate=" + exchangeRate + ", balance=" + balance +
                ", exported=" + exported + ", receiptNumber=" + receiptNumber + ", debitNoteNumber=" + debitNoteNumber +
                ", kraReceiptNumber=" + kraReceiptNumber + ", kraClerkName=" + kraClerkName +
                ", approvalDocument=" + approvalDocument +
                ", approvalDocumentName='" + approvalDocumentName + '\'' +
                ", approvalDocumentType='" + approvalDocumentType + '\'' +
            "]";
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Transaction that = (Transaction) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

}
