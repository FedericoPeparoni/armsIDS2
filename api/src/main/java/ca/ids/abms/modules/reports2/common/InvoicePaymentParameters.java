package ca.ids.abms.modules.reports2.common;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.modules.bankaccount.BankAccount;
import org.springframework.data.annotation.Transient;

import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.transactions.TransactionPaymentMechanism;

/**
 * Invoice payement options.
 * <p>
 * These objects are filled by user interface when generating POS invoices with a payment.
 * These objects represent credit transactions. Note that the "amount" field in this
 * object is expected to be positive since the code will negate it when creating
 * a transaction.
 * <p>
 * The fields in this class are a subset of fields in TransactionViewModel class for
 * UI compatibility.
 */
public class InvoicePaymentParameters implements Cloneable {

    @NotNull
    private Double amount;

    @NotNull
    private Currency currency;

    @NotNull
    private TransactionPaymentMechanism paymentMechanism;

    @NotNull
    private String description;

    @NotNull
    private String paymentReferenceNumber;

    private Boolean exported;

    @NotNull
    private Double paymentAmount;

    @NotNull
    private Currency paymentCurrency;

    @NotNull
    private Double paymentExchangeRate;

    @Transient
    private String kraClerkName;

    @Transient
    private String kraReceiptNumber;

    @Size(min = 1, max = BankAccount.NAME_MAX_LENGTH)
    private String bankAccountName;

    @Size(min = 1, max = BankAccount.NUMBER_MAX_LENGTH)
    private String bankAccountNumber;

    @Size(min = 1, max = BankAccount.EXTERNAL_ACCOUTING_SYSTEM_ID_MAX_LENGTH)
    private String bankAccountExternalAccountingSystemId;

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

    public TransactionPaymentMechanism getPaymentMechanism() {
        return paymentMechanism;
    }

    public void setPaymentMechanism(TransactionPaymentMechanism paymentMechanism) {
        this.paymentMechanism = paymentMechanism;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPaymentReferenceNumber() {
        return paymentReferenceNumber;
    }

    public void setPaymentReferenceNumber(String paymentReferenceNumber) {
        this.paymentReferenceNumber = paymentReferenceNumber;
    }

    public Boolean getExported() {
        return exported;
    }

    public void setExported(Boolean exported) {
        this.exported = exported;
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

    @Override
    public InvoicePaymentParameters clone() {
        final InvoicePaymentParameters payment = new InvoicePaymentParameters();
        payment.setAmount(this.amount);
        payment.setCurrency(this.currency);
        payment.setPaymentMechanism(this.paymentMechanism);
        payment.setDescription(this.description);
        payment.setPaymentReferenceNumber(this.paymentReferenceNumber);
        payment.setExported(this.exported);
        payment.setPaymentAmount(this.paymentAmount);
        payment.setCurrency(this.currency);
        payment.setPaymentExchangeRate(this.paymentExchangeRate);
        payment.setKraClerkName(this.kraClerkName);
        payment.setKraReceiptNumber(this.kraReceiptNumber);
        return payment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvoicePaymentParameters that = (InvoicePaymentParameters) o;

        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;
        if (paymentMechanism != that.paymentMechanism) return false;
        if (paymentReferenceNumber != null ? !paymentReferenceNumber.equals(that.paymentReferenceNumber) : that.paymentReferenceNumber != null)
            return false;
        if (exported != null ? !exported.equals(that.exported) : that.exported != null) return false;
        if (paymentAmount != null ? !paymentAmount.equals(that.paymentAmount) : that.paymentAmount != null)
            return false;
        if (paymentCurrency != null ? !paymentCurrency.equals(that.paymentCurrency) : that.paymentCurrency != null)
            return false;
        return paymentExchangeRate != null ? paymentExchangeRate.equals(that.paymentExchangeRate) : that.paymentExchangeRate == null;
    }

    @Override
    public int hashCode() {
        int result = amount != null ? amount.hashCode() : 0;
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (paymentMechanism != null ? paymentMechanism.hashCode() : 0);
        result = 31 * result + (paymentReferenceNumber != null ? paymentReferenceNumber.hashCode() : 0);
        result = 31 * result + (paymentAmount != null ? paymentAmount.hashCode() : 0);
        result = 31 * result + (paymentCurrency != null ? paymentCurrency.hashCode() : 0);
        result = 31 * result + (paymentExchangeRate != null ? paymentExchangeRate.hashCode() : 0);
        return result;
    }
}
