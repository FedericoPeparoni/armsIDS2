package ca.ids.abms.modules.transactions;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.util.models.AuditedEntity;

@Entity
public class TransactionPayment extends AuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    @NotNull
    private Transaction transaction;

    @ManyToOne
    @JoinColumn(name = "billing_ledger_id")
    @NotNull
    private BillingLedger billingLedger;

    @NotNull
    private Double amount;

    @ManyToOne
    @NotNull
    private Currency currency;

    @NotNull
    private Double exchangeRate;
    
    @ManyToOne
    @JoinColumn(name = "target_currency_id")
    @NotNull
    private Currency targetCurrency;

    private Double exchangeRateToAnsp;

    @NotNull
    private Boolean isAccountCredit;

    @NotNull
    private Boolean exported = false;
    
    @Transient
    private String kraClerkName;
    
    @Transient
    private String kraReceiptNumber;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public BillingLedger getBillingLedger() {
        return billingLedger;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(Currency aTargetCurrency) {
        targetCurrency = aTargetCurrency;
    }

    public void setBillingLedger(BillingLedger billingLedger) {
        this.billingLedger = billingLedger;
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

    public Double getExchangeRateToAnsp() {
        return exchangeRateToAnsp;
    }

    public void setExchangeRateToAnsp(Double exchangeRateToAnsp) {
        this.exchangeRateToAnsp = exchangeRateToAnsp;
    }

    public Boolean getIsAccountCredit() {
		return isAccountCredit;
	}

	public void setIsAccountCredit(Boolean isAccountCredit) {
		this.isAccountCredit = isAccountCredit;
	}

    public Boolean getExported() {
        return exported;
    }

    public void setExported(Boolean exported) {
        this.exported = exported;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TransactionPayment that = (TransactionPayment) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TransactionPayment [id=" + id + ", transaction=" + transaction + ", billingLedger=" + billingLedger + ", amount="
                + amount + ", currency=" + currency + ", exchangeRate=" + exchangeRate
                + ", exchangeRateToAnsp=" + exchangeRateToAnsp + "]";
    }
}
