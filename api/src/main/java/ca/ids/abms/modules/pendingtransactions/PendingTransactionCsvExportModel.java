package ca.ids.abms.modules.pendingtransactions;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDateTime;

public class PendingTransactionCsvExportModel {

    private String account;

    private String description;

    @CsvProperty(value = "Date/Time", dateTime = true)
    private LocalDateTime transactionDateTime;

    @CsvProperty(value = "Type")
    private String transactionType;

    @CsvProperty(value = "Amount", precision = 2)
    private Double paymentAmount;

    @CsvProperty(precision = 5)
    private Double exchangeRateToUsd;

    @CsvProperty(value = "Payment Ref. Number")
    private String paymentReferenceNumber;

    private String paymentMechanism;

    @CsvProperty(value = "Approval Level")
    private String currentApprovalLevel;

    @CsvProperty(value = "Previous Action")
    private String previousApprovalLevel;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTransactionDateTime() {
        return transactionDateTime;
    }

    public void setTransactionDateTime(LocalDateTime transactionDateTime) {
        this.transactionDateTime = transactionDateTime;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Double getExchangeRateToUsd() {
        return exchangeRateToUsd;
    }

    public void setExchangeRateToUsd(Double exchangeRateToUsd) {
        this.exchangeRateToUsd = exchangeRateToUsd;
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

    public String getCurrentApprovalLevel() {
        return currentApprovalLevel;
    }

    public void setCurrentApprovalLevel(String currentApprovalLevel) {
        this.currentApprovalLevel = currentApprovalLevel;
    }

    public String getPreviousApprovalLevel() {
        return previousApprovalLevel;
    }

    public void setPreviousApprovalLevel(String previousApprovalLevel) {
        this.previousApprovalLevel = previousApprovalLevel;
    }
}
