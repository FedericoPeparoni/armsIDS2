package ca.ids.abms.modules.currencies;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@UniqueKey(columnNames = {
    "currencyCode", "currencyName", "externalAccountingSystemIdentifier"
}, checkSeparately = true)
public class Currency extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 3)
    @Column(unique = true)
    @NotNull
    @SearchableText
    private String currencyCode;

    @Size(max = 50)
    @Column(unique = true)
    @NotNull
    @SearchableText
    private String currencyName;

    @ManyToOne
    @JoinColumn(name = "country_code")
    @NotNull
    private Country countryCode;

    @NotNull
    private Integer decimalPlaces;

    @NotNull
    private String symbol;

    @NotNull
    private Boolean active;

    @NotNull
    private Boolean allowUpdatedFromWeb;

    @Column(name = "external_accounting_system_identifier", length = 10, unique = true)
    @SearchableText
    @Size(max = 10)
    private String externalAccountingSystemIdentifier;

    /**
     * Default target currency for newly created exchange rates of this currency
     */
    @Column(name = "exchange_rate_target_currency")
    private Integer exchangeRateTargetCurrencyId;

    @JsonIgnore
    @OneToMany(mappedBy = "currency")
    private List<CurrencyExchangeRate> currencyExchangeRate;
    
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Currency)) {
            return false;
        }

        Currency that = (Currency) o;

        return id != null ? id.equals(that.getId()) : that.getId() == null;
    }

    public Boolean getActive() {
        return active;
    }

    public Country getCountryCode() {
        return countryCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public Integer getDecimalPlaces() {
        return decimalPlaces;
    }

    public Integer getId() {
        return this.id;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setCountryCode(Country countryCode) {
        this.countryCode = countryCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public void setDecimalPlaces(Integer decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public void setId(Integer aId) {
        this.id = aId;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Boolean getAllowUpdatedFromWeb() {
        return allowUpdatedFromWeb;
    }

    public void setAllowUpdatedFromWeb(Boolean allowUpdatedFromWeb) {
        this.allowUpdatedFromWeb = allowUpdatedFromWeb;
    }

    public String getExternalAccountingSystemIdentifier() {
        return externalAccountingSystemIdentifier;
    }

    public void setExternalAccountingSystemIdentifier(String externalAccountingSystemIdentifier) {
        this.externalAccountingSystemIdentifier = externalAccountingSystemIdentifier;
    }

    public Integer getExchangeRateTargetCurrencyId() {
        return exchangeRateTargetCurrencyId;
    }

    public void setExchangeRateTargetCurrencyId(Integer exchangeRateTargetCurrencyId) {
        this.exchangeRateTargetCurrencyId = exchangeRateTargetCurrencyId;
    }

    public List<CurrencyExchangeRate> getCurrencyExchangeRate() {
        return currencyExchangeRate;
    }

    public void setCurrencyExchangeRate(List<CurrencyExchangeRate> currencyExchangeRate) {
        this.currencyExchangeRate = currencyExchangeRate;
    }

    @Override
    public String toString() {
        return "Currency [id=" + id + ", currencyCode=" + currencyCode
                + ", currencyName=" + currencyName + ", countryCode="
                + countryCode + ", decimalPlaces=" + decimalPlaces
                + ", symbol=" + symbol + ", exchangeRate="
                + ", allowUpdatedFromWeb=" + allowUpdatedFromWeb
                + "]";
    }
}
