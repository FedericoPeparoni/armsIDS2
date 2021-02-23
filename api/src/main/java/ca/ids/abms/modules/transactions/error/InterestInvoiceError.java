package ca.ids.abms.modules.transactions.error;

import java.time.LocalDate;

public class InterestInvoiceError {

    private String invoiceNumber;

    private LocalDate missingInterestRateOnDate;

    private LocalDate missingExchangeRateOnDate;

    private String fromCurrency;

    private String toCurrency;

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDate getMissingInterestRateOnDate() {
        return missingInterestRateOnDate;
    }

    public void setMissingInterestRateOnDate(LocalDate missingInterestRateOnDate) {
        this.missingInterestRateOnDate = missingInterestRateOnDate;
    }

    public LocalDate getMissingExchangeRateOnDate() {
        return missingExchangeRateOnDate;
    }

    public void setMissingExchangeRateOnDate(LocalDate missingExchangeRateOnDate) {
        this.missingExchangeRateOnDate = missingExchangeRateOnDate;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }
}
