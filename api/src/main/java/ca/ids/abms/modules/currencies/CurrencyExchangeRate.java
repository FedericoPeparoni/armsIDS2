package ca.ids.abms.modules.currencies;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

@Entity
@UniqueKey(columnNames={"currency", "targetCurrency", "exchangeRateValidFromDate", "exchangeRateValidToDate"})
public class CurrencyExchangeRate extends VersionedAuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

	@ManyToOne
    @JoinColumn(name = "currency")
    @NotNull
    private Currency currency;

    @NotNull
    private Double exchangeRate;

    @NotNull
    private LocalDateTime exchangeRateValidFromDate;

    @NotNull
    private LocalDateTime exchangeRateValidToDate;

    @ManyToOne
    @JoinColumn(name = "target_currency")
    @NotNull
    private Currency targetCurrency;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrencyExchangeRate that = (CurrencyExchangeRate) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public LocalDateTime getExchangeRateValidFromDate() {
        return exchangeRateValidFromDate;
    }

    public LocalDateTime getExchangeRateValidToDate() {
        return exchangeRateValidToDate;
    }

    public Integer getId() {
        return id;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public void setExchangeRateValidFromDate(LocalDateTime exchangeRateValidFromDate) {
        this.exchangeRateValidFromDate = exchangeRateValidFromDate;
    }

    public void setExchangeRateValidToDate(LocalDateTime exchangeRateValidToDate) {
        this.exchangeRateValidToDate = exchangeRateValidToDate;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTargetCurrency(Currency aTargetCurrency) {
        targetCurrency = aTargetCurrency;
    }

    @Override
    public String toString() {
        return "CurrencyExchangeRate{" +
                "id=" + id +
                ", currency=" + currency +
                ", targetCurrency=" + targetCurrency +
                ", exchangeRate=" + exchangeRate +
                ", exchangeRateValidFromDate=" + exchangeRateValidFromDate +
                ", exchangeRateValidToDate=" + exchangeRateValidToDate +
                '}';
    }
}
