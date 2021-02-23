package ca.ids.abms.modules.currencies;

import java.util.ArrayList;
import java.util.List;

public class CurrencyCsvExportModel {

    private String currencyCode;

    private String currencyName;

    private String countryName;

    private List<CurrencyMapper.ExchangeRate> exchangeRate = new ArrayList<>();

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public List<CurrencyMapper.ExchangeRate> getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(List<CurrencyMapper.ExchangeRate> exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}
