package ca.ids.abms.modules.billings;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.plugins.kcaa.aatis.modules.permitnumber.KcaaAatisPermitNumber;
import ca.ids.abms.plugins.kcaa.eaip.modules.requisitionnumbers.KcaaEaipRequisitionNumber;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.chargesadjustment.ChargesAdjustment;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.category.FlightmovementCategory;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

@Entity
public class BillingLedger extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @SearchableEntity(searchableField = "name")
    private Account account;

    /*
     * NotFound is used to prevent unhandled errors when retrieving billing ledgers
     * that belong to a billing center that has been deleted.
     */
    @ManyToOne
    @JoinColumn(name = "billing_center_id")
    @NotFound(action= NotFoundAction.IGNORE)
    @SearchableEntity(searchableField = "name")
    private BillingCenter billingCenter;

    @NotNull
    private LocalDateTime invoicePeriodOrDate;

    @NotNull
    @SearchableText
    private String invoiceType;

    //in Kenya: invoice states NEW, APPROVED, PUBLISHED, PAID are used. In Botswana: only PUBLISHED, PAID states are used.
    @NotNull
    @SearchableText
    private String invoiceStateType;

    @NotNull
    private LocalDateTime paymentDueDate;

    /*
     * NotFound is used to prevent unhandled errors when retrieving billing ledgers
     * that belong to a user that has been deleted.
     */
    @ManyToOne
    @NotFound(action= NotFoundAction.IGNORE)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @SearchableText
    private String invoiceNumber;

    @Column(name = "invoice_filename")
    @SearchableText
    private String invoiceFileName;

    @JsonIgnore
    private byte[] invoiceDocument;

    @SearchableText
    private String invoiceDocumentType;

    @NotNull
    private Double invoiceAmount;

    @ManyToOne
    @JoinColumn(name = "invoice_currency")
    @NotNull
    @SearchableEntity(searchableField = "currencyCode")
    private Currency invoiceCurrency;

    @NotNull
    private Double invoiceExchange;

    @ManyToOne
    @JoinColumn(name = "target_currency")
    @NotNull
    private Currency targetCurrency;

    @NotNull
    private Double invoiceExchangeToAnsp;

    @NotNull
    private LocalDateTime invoiceDateOfIssue;

    private Boolean exported;

    @NotNull
    private Double amountOwing;

    private LocalDateTime finalPaymentDate;

    @SearchableText
    private String paymentMode;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "billingLedger", cascade = CascadeType.ALL, orphanRemoval = true)
    private List <InvoiceLineItem> invoiceLineItems;

    //this property is used for aviation invoice generation to determine if currency should be taken from flight movement category
    @ManyToOne
    @JoinColumn(name = "flight_category_id")
    private FlightmovementCategory flightmovementCategory;

    @JsonIgnore
    @OneToMany(mappedBy = "billingLedger", cascade = CascadeType.ALL,
        fetch = FetchType.LAZY)
    private Set<ChargesAdjustment> chargesAdjustment = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "billingLedger", cascade = CascadeType.ALL)
    private Set<KcaaAatisPermitNumber> kcaaAatisPermitNumbers;

    @JsonIgnore
    @OneToMany(mappedBy = "billingLedger", cascade = CascadeType.ALL)
    private Set<KcaaEaipRequisitionNumber> kcaaEaipRequisitionNumbers;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "enroute_invoice_id", insertable = false, updatable = false)
    private Set<FlightMovement> enrouteFlightMovements = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_invoice_id", insertable = false, updatable = false)
    private Set<FlightMovement> passengerFlightMovements = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "other_invoice_id", insertable = false, updatable = false)
    private Set<FlightMovement> otherFlightMovements = new HashSet<>();

    /**
     * Used for Debit Notes to hold description from transaction form until the billing ledger is published
     * and the debit transaction is created.
     */
    @Column(name = "transaction_description", length = 100)
    @Size(max = 100)
    private String transactionDescription;

    @NotNull
    private Boolean proforma = false;

    @NotNull
    private Boolean pointOfSale = false;

    private String clerkName;
    
    private String receiptNumber;
    
    @NotNull
    @SearchableText
    @Column(name = "invoice_seq_number_type")
    private String invoiceSequenceNumberType;

    @Column(name = "invoice_reference_number")
    @SearchableText
    private String invoiceReferenceNumber;
   
    /**
     * Used for account credit when a billing ledger is created but not saved during invoice preview
     */
    @Transient
    private Double accountCredit;
    
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        BillingLedger that = (BillingLedger) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    public Account getAccount() {
        return account;
    }

    public Double getAmountOwing() {
        return amountOwing;
    }

    public BillingCenter getBillingCenter() {
        return billingCenter;
    }

    public Set<ChargesAdjustment> getChargesAdjustment() {
        return chargesAdjustment;
    }

    public Boolean getExported() {
        return exported;
    }

    public LocalDateTime getFinalPaymentDate() {
        return finalPaymentDate;
    }

    public FlightmovementCategory getFlightmovementCategory() {
		return flightmovementCategory;
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

    public byte[] getInvoiceDocument() {
        return invoiceDocument;
    }

    public String getInvoiceDocumentType() {
        return invoiceDocumentType;
    }

    public Double getInvoiceExchange() {
        return invoiceExchange;
    }

    public Double getInvoiceExchangeToAnsp() {
        return invoiceExchangeToAnsp;
    }

    public String getInvoiceFileName() {
        return invoiceFileName;
    }

    public List<InvoiceLineItem> getInvoiceLineItems() {
        return invoiceLineItems;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public LocalDateTime getInvoicePeriodOrDate() {
        return invoicePeriodOrDate;
    }

    public String getInvoiceReferenceNumber() {
        return invoiceReferenceNumber;
    }
    
    public String getInvoiceStateType() {
        return invoiceStateType;
    }

    public String getInvoiceType() {
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

    public User getUser() {
        return user;
    }

    public Set<KcaaAatisPermitNumber> getKcaaAatisPermitNumbers() {
        return kcaaAatisPermitNumbers;
    }

    public Set<KcaaEaipRequisitionNumber> getKcaaEaipRequisitionNumbers() {
        return kcaaEaipRequisitionNumbers;
    }

    public void setAccount(Account aAccount) {
        account = aAccount;
    }

    public void setAmountOwing(Double amountOwing) {
        this.amountOwing = amountOwing;
    }

    public void setBillingCenter(BillingCenter billingCenter) {
        this.billingCenter = billingCenter;
    }

    public void setChargesAdjustment(Set<ChargesAdjustment> chargesAdjustment) {
        this.chargesAdjustment = chargesAdjustment;
    }

    public void setExported(Boolean aExported) {
        exported = aExported;
    }

    public void setFinalPaymentDate(LocalDateTime finalPaymentDate) {
        this.finalPaymentDate = finalPaymentDate;
    }

    public void setFlightmovementCategory(FlightmovementCategory flightmovementCategory) {
		this.flightmovementCategory = flightmovementCategory;
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

    public void setInvoiceDocument(byte[] aInvoiceDocument) {
        invoiceDocument = aInvoiceDocument;
    }

    public void setInvoiceDocumentType(String invoiceDocumentType) {
        this.invoiceDocumentType = invoiceDocumentType;
    }

    public void setInvoiceExchange(Double aInvoiceExchange) {
        invoiceExchange = aInvoiceExchange;
    }

    public void setInvoiceExchangeToAnsp(Double invoiceExchangeToAnsp) {
        this.invoiceExchangeToAnsp = invoiceExchangeToAnsp;
    }

    public void setInvoiceFileName(String invoiceFileName) {
        this.invoiceFileName = invoiceFileName;
    }

    public void setInvoiceLineItems(List<InvoiceLineItem> invoiceLineItems) {
        this.invoiceLineItems = invoiceLineItems;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public void setInvoicePeriodOrDate(LocalDateTime aInvoicePeriodOrDate) {
        invoicePeriodOrDate = aInvoicePeriodOrDate;
    }

    public void setInvoiceReferenceNumber(String invoiceReferenceNumber) {
        this.invoiceReferenceNumber = invoiceReferenceNumber;
    }
    
    public void setInvoiceStateType(String aInvoiceStateType) {
        invoiceStateType = aInvoiceStateType;
    }

    public void setInvoiceType(String invoiceType) {
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

	public void setUser(User aUser) {
        user = aUser;
    }

    public void setKcaaAatisPermitNumbers(Set<KcaaAatisPermitNumber> kcaaAatisPermitNumbers) {
        this.kcaaAatisPermitNumbers = kcaaAatisPermitNumbers;
    }

    public void setKcaaEaipRequisitionNumbers(Set<KcaaEaipRequisitionNumber> kcaaEaipRequisitionNumbers) {
        this.kcaaEaipRequisitionNumbers = kcaaEaipRequisitionNumbers;
    }

    public Set<FlightMovement> getEnrouteFlightMovements() {
		return enrouteFlightMovements;
	}

	public void setEnrouteFlightMovements(Set<FlightMovement> enrouteFlightMovements) {
		this.enrouteFlightMovements = enrouteFlightMovements;
	}

	public Set<FlightMovement> getPassengerFlightMovements() {
		return passengerFlightMovements;
	}

	public void setPassengerFlightMovements(Set<FlightMovement> passengerFlightMovements) {
		this.passengerFlightMovements = passengerFlightMovements;
	}

	public Set<FlightMovement> getOtherFlightMovements() {
		return otherFlightMovements;
	}

	public void setOtherFlightMovements(Set<FlightMovement> otherFlightMovements) {
		this.otherFlightMovements = otherFlightMovements;
	}

	@Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Boolean getPointOfSale() {
        return pointOfSale;
    }

    public void setPointOfSale(Boolean pointOfSale) {
        this.pointOfSale = pointOfSale;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getClerkName() {
        return clerkName;
    }

    public void setClerkName(String clerkName) {
        this.clerkName = clerkName;
    }

    public Double getAccountCredit() {
        return accountCredit;
    }

    public void setAccountCredit(Double accountCredit) {
        this.accountCredit = accountCredit;
    }


    public String getInvoiceSequenceNumberType() {
        return invoiceSequenceNumberType;
    }

    public void setInvoiceSequenceNumberType(String invoiceSeqNumberType) {
        this.invoiceSequenceNumberType = invoiceSeqNumberType;
    }
    
    @Override
    public String toString() {
        return "BillingLedger [id=" + id + ", account=" + account + ", invoicePeriodOrDate=" + invoicePeriodOrDate
                + ", invoiceType=" + invoiceType + ", invoiceStateType=" + invoiceStateType + ", paymentDueDate=" + paymentDueDate
                + ", user=" + user + ", invoiceDocument=byteArray[length=" + (invoiceDocument == null ? null : invoiceDocument.length) + "]"
                + ", invoiceDocumentType=" + invoiceDocumentType + ", invoiceAmount=" + invoiceAmount
                + ", invoiceCurrency=" + invoiceCurrency + ", invoiceExchange=" + invoiceExchange
                + ", invoiceExchangeToAnsp=" + invoiceExchangeToAnsp + ", invoiceDateOfIssue=" + invoiceDateOfIssue
                + ", exported=" + exported + ", amountOwing=" + amountOwing + ", finalPaymentDate=" + finalPaymentDate
                + ", paymentMode=" + paymentMode
                + "]";
    }
}
