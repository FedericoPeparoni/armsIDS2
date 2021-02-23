package ca.ids.abms.modules.transactions;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDateTime;

public class TransactionCsvExportModel {

    @CsvProperty(value = "Number")
    private String receiptNumber;

    private String account;

    @CsvProperty(value = "Date/Time", dateTime = true)
    private LocalDateTime transactionDateTime;

    private String description;

    @CsvProperty(value = "Type")
    private String transactionType;

    @CsvProperty(precision = 2)
    private Double amount;

    private String currency;

    @CsvProperty(value = "Exchange Rate To USD", precision = 5)
    private Double exchangeRate;

    @CsvProperty(precision = 5)
    private Double exchangeRateToAnsp;

    @CsvProperty(precision = 2)
    private Double balance;

    private String paymentReferenceNumber;

    private String paymentMechanism;

    @CsvProperty(value = "Bank Account")
    private String bankAccountName;

    private Boolean exported;

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
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

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
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

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getPaymentReferenceNumber() {
        return paymentReferenceNumber;
    }

    public void setPaymentReferenceNumber(String paymentReferenceNumber) {
        this.paymentReferenceNumber = paymentReferenceNumber;
    }

    public String getPaymentMechanism() {
        return paymentMechanism;
    }

    public void setPaymentMechanism(String paymentMechanism) {
        this.paymentMechanism = paymentMechanism;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public Boolean getExported() {
        return exported;
    }

    public void setExported(Boolean exported) {
        this.exported = exported;
    }
}
