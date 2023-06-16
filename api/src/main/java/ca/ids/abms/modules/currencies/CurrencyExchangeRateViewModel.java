package ca.ids.abms.modules.currencies;

import ca.ids.abms.modules.util.models.VersionedViewModel;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CurrencyExchangeRateViewModel extends VersionedViewModel {
    private Integer id;

    @NotNull
    private Currency currency;

    @NotNull
    private Double exchangeRate;

    @NotNull
    private LocalDateTime exchangeRateValidFromDate;

    @NotNull
    private LocalDateTime exchangeRateValidToDate;


    @NotNull
    private Currency targetCurrency;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public LocalDateTime getExchangeRateValidFromDate() {
        return exchangeRateValidFromDate;
    }

    public void setExchangeRateValidFromDate(LocalDateTime exchangeRateValidFromDate) {
        this.exchangeRateValidFromDate = exchangeRateValidFromDate;
    }

    public LocalDateTime getExchangeRateValidToDate() {
        return exchangeRateValidToDate;
    }

    public void setExchangeRateValidToDate(LocalDateTime exchangeRateValidToDate) {
        this.exchangeRateValidToDate = exchangeRateValidToDate;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(Currency targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    @Override
    public String toString() {
        return "CurrencyExchangeRateViewModel{" +
            "id=" + id +
            ", currency=" + currency +
            ", exchangeRate=" + exchangeRate +
            ", exchangeRateValidFromDate=" + exchangeRateValidFromDate +
            ", exchangeRateValidToDate=" + exchangeRateValidToDate +
            ", targetCurrency=" + targetCurrency +
            '}';
    }
}
