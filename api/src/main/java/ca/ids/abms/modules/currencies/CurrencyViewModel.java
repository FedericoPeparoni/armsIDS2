package ca.ids.abms.modules.currencies;

import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

import java.util.Objects;

public class CurrencyViewModel extends VersionedAuditedEntity {

    private Integer id;

    private String currencyCode;

    private String currencyName;

    private Country countryCode;

    private Integer decimalPlaces;

    private String symbol;

    private Boolean active;

    private Boolean allowUpdatedFromWeb;

    private String externalAccountingSystemIdentifier;

    private Integer exchangeRateTargetCurrencyId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Country getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(Country countryCode) {
        this.countryCode = countryCode;
    }

    public Integer getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(Integer decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyViewModel that = (CurrencyViewModel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CurrencyViewModel{" +
            "id=" + id +
            ", currencyCode='" + currencyCode + '\'' +
            ", currencyName='" + currencyName + '\'' +
            ", countryCode=" + countryCode +
            ", decimalPlaces=" + decimalPlaces +
            ", symbol='" + symbol + '\'' +
            ", active=" + active +
            ", allowUpdatedFromWeb=" + allowUpdatedFromWeb +
            ", externalAccountingSystemIdentifier='" + externalAccountingSystemIdentifier + '\'' +
            ", exchangeRateTargetCurrencyId=" + exchangeRateTargetCurrencyId +
            '}';
    }
}
