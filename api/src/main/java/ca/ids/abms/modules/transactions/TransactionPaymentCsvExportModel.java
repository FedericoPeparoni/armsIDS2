package ca.ids.abms.modules.transactions;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class TransactionPaymentCsvExportModel {

    private String invoiceNumber;

    @CsvProperty(precision = 2)
    private Double amount;

    private String currency;

    @CsvProperty(value = "Exchange Rate From USD", precision = 5)
    private Double exchangeRate;

    @CsvProperty(precision = 5)
    private Double exchangeRateToAnsp;

    @CsvProperty(value = "Account Credit")
    private String isAccountCredit;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
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

    public String getIsAccountCredit() {
        return isAccountCredit;
    }

    public void setIsAccountCredit(String isAccountCredit) {
        this.isAccountCredit = isAccountCredit;
    }
}
