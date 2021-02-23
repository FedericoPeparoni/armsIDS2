package ca.ids.abms.modules.reports2.common;

import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryNationality;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryScope;
import ca.ids.abms.modules.system.BillingOrgCode;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import com.google.common.base.Preconditions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Convert currency values while caching results for performance.
 *
 * <p>This class allows one to convert currencies for the same date. It caches
 * exchange rates internally, so it should be used whenever we need
 * to do many currency conversions in one transaction.
 *
 */
public class CachedCurrencyConverter {

    private final LocalDateTime exchangeRateDate;
    private final CurrencyUtils currencyUtils;
    private final BillingOrgCode billingOrgCode;

    private final Map<Currency, Double> exchangeRateToUsdMap = new HashMap <>();
    private final Map<CachedCurrencyKey, Double> exchangeRateMap = new HashMap<>();
    private final Map<String, Currency> currencyCodeMap = new HashMap<>();

    private Currency anspCurrency = null;
    private Currency usdCurrency = null;
    private Currency eurCurrency = null;
    private Currency paxDomCurrency = null;
    private Currency paxIntlCurrency = null;
    private Currency aerodromeDomCurrency = null;
    private Currency aerodromeIntlCurrency = null;
    private Currency aerodromeRegCurrency = null;
    private Currency approachDomCurrency = null;
    private Currency approachIntlCurrency = null;
    private Currency approachRegCurrency = null;
    private Currency lateArrDepDomCurrency = null;
    private Currency lateArrDepIntlCurrency = null;
    private Currency lateArrDepRegCurrency = null;

    private final Map<CachedScopeNationalityKey, Currency> aerodromeCurrencyMap = new HashMap<>();
    private final Map<CachedScopeNationalityKey, Currency> approachCurrencyMap = new HashMap<>();
    private final Map<CachedScopeNationalityKey, Currency> lateArrivalDepartureCurrencyMap = new HashMap<>();
    private final Map<CachedScopeNationalityKey, Currency> extendedHoursSurchargeCurrencyMap = new HashMap<>();

    /**
     * Create a new instance.
     *
     * The exchangeRateDate parameter will be used for all exhange rate lookups in
     * subsequent method calls.
     */
    public CachedCurrencyConverter (final CurrencyUtils currencyUtils, final LocalDateTime exchangeRateDate) {
        Preconditions.checkNotNull (currencyUtils);
        Preconditions.checkNotNull (exchangeRateDate);
        this.currencyUtils = currencyUtils;
        this.exchangeRateDate = exchangeRateDate;
        this.billingOrgCode = currencyUtils.getBillingOrgCode();
    }

    /**
     * Convert the specified value to the ANSP currency. Uses exchange rate date provided to the constructor.
     * KCAA is always converted to USD
     *
     * @deprecated should use toANSPCurrency
     */
    @Deprecated
    public Double convertToANSP (final Double value, final Currency currency) {
        // KCAA should always be converted to USD
        Currency ansp = billingOrgCode == BillingOrgCode.KCAA
            ? getUsdCurrency()
            : getAnspCurrency();

        if (value != null) {
            return convertCurrency(value, currency, ansp);
        }
        return null;
    }

    /**
     * Convert to ANSP currency.
     *
     * @param value the value
     * @param currency the currency
     * @return the double
     */
    public Double toANSPCurrency (final Double value, final Currency currency) {
       if (value != null) {
            return convertCurrency(value, currency, getAnspCurrency());
        }
        return null;
    }

    /**
     * Convert to USD currency.
     *
     * @param value the value
     * @param currency the currency
     * @return the double
     */
    public Double toUSDCurrency (final Double value, final Currency currency) {
        if (value != null) {
             return convertCurrency(value, currency, getUsdCurrency());
         }
         return null;
     }

    /**
     * Return the ANSP currency defined in system settings.
     */
    public Currency getAnspCurrency() {
        if (anspCurrency == null)
            anspCurrency = currencyUtils.getAnspCurrency();
        return anspCurrency;
    }

    /**
     * Return the USD currency defined in system settings.
     */
    public Currency getUsdCurrency() {
        if (usdCurrency == null)
            usdCurrency = currencyUtils.getCurrencyUSD();
        return usdCurrency;
    }

    /**
     * Return the EUR currency defined in system settings.
     */
    public Currency getEurCurrency() {
        if (eurCurrency == null)
            eurCurrency = currencyUtils.getCurrency("EUR");
        return eurCurrency;
    }

    /**
     * Return the Domestic PAX fee currency defined in system settings.
     */
    public Currency getPaxDomCurrency() {
        if (paxDomCurrency == null)
            paxDomCurrency = currencyUtils.getSystemConfigurationCurrency(SystemConfigurationItemName
                .DOMESTIC_PAX_FEE_CURRENCY);
        return paxDomCurrency;
    }

    /**
     * Return the International PAX fee currency defined in system settings.
     */
    public Currency getPaxIntlCurrency() {
        if (paxIntlCurrency == null)
            paxIntlCurrency = currencyUtils.getSystemConfigurationCurrency(SystemConfigurationItemName
                .INTERNATIONAL_PAX_FEE_CURRENCY);
        return paxIntlCurrency;
    }

    /**
     * Return the Domestic aerodrome fee currency defined in system settings.
     */
    public Currency getAerodromeDomCurrency() {
        if (aerodromeDomCurrency == null)
            aerodromeDomCurrency = currencyUtils.getSystemConfigurationCurrency(SystemConfigurationItemName
                .DOMESTIC_AERODROME_CHARGES_CURRENCY);
        return aerodromeDomCurrency;
    }

    /**
     * Return the International aerodrome fee currency defined in system settings.
     */
    public Currency getAerodromeIntlCurrency() {
        if (aerodromeIntlCurrency == null)
            aerodromeIntlCurrency = currencyUtils.getSystemConfigurationCurrency(SystemConfigurationItemName
                .INTERNATIONAL_AERODROME_CHARGES_CURRENCY);
        return aerodromeIntlCurrency;
    }

    /**
     * Return the Regional aerodrome fee currency defined in system settings.
     */
    public Currency getAerodromeRegCurrency() {
        if (aerodromeRegCurrency == null)
            aerodromeRegCurrency = currencyUtils.getSystemConfigurationCurrency(SystemConfigurationItemName
                .REGIONAL_AERODROME_CHARGES_CURRENCY);
        return aerodromeRegCurrency;
    }

    /**
     * Return the Domestic approach fee currency defined in system settings.
     */
    public Currency getApproachDomCurrency() {
        if (approachDomCurrency == null)
            approachDomCurrency = currencyUtils.getSystemConfigurationCurrency(SystemConfigurationItemName
                .DOMESTIC_APPROACH_CHARGES_CURRENCY);
        return approachDomCurrency;
    }

    /**
     * Return the International approach fee currency defined in system settings.
     */
    public Currency getApproachIntlCurrency() {
        if (approachIntlCurrency == null)
            approachIntlCurrency = currencyUtils.getSystemConfigurationCurrency(SystemConfigurationItemName
                .INTERNATIONAL_APPROACH_CHARGES_CURRENCY);
        return approachIntlCurrency;
    }

    /**
     * Return the Regional approach fee currency defined in system settings.
     */
    public Currency getApproachRegCurrency() {
        if (approachRegCurrency == null)
            approachRegCurrency = currencyUtils.getSystemConfigurationCurrency(SystemConfigurationItemName
                .REGIONAL_APPROACH_CHARGES_CURRENCY);
        return approachRegCurrency;
    }

    /**
     * Return the Domestic late arrival/departure fee currency defined in system settings.
     */
    public Currency getLateArrDepDomCurrency() {
        if (lateArrDepDomCurrency == null)
            lateArrDepDomCurrency = currencyUtils.getSystemConfigurationCurrency(SystemConfigurationItemName
                .DOMESTIC_LATE_ARRIVAL_DEPARTURE_CHARGES_CURRENCY);
        return lateArrDepDomCurrency;
    }

    /**
     * Return the International late arrival/departure fee currency defined in system settings.
     */
    public Currency getLateArrDepIntlCurrency() {
        if (lateArrDepIntlCurrency == null)
            lateArrDepIntlCurrency = currencyUtils.getSystemConfigurationCurrency(SystemConfigurationItemName
                .INTERNATIONAL_LATE_ARRIVAL_DEPARTURE_CHARGES_CURRENCY);
        return lateArrDepIntlCurrency;
    }

    /**
     * Return the Regional late arrival/departure fee currency defined in system settings.
     */
    public Currency getLateArrDepRegCurrency() {
        if (lateArrDepRegCurrency == null)
            lateArrDepRegCurrency = currencyUtils.getSystemConfigurationCurrency(SystemConfigurationItemName
                .REGIONAL_LATE_ARRIVAL_DEPARTURE_CHARGES_CURRENCY);
        return lateArrDepRegCurrency;
    }

    /**
     * Return cached currency defined by currency code.
     */
    public Currency getCurrencyByCode(final String currencyCode) {
        return currencyCodeMap.computeIfAbsent(currencyCode, currencyUtils::getCurrency);
    }

    /**
     * Returns the exchange rate for converting sourceCurrency to targetCurrency. Throws ErrorDTO exception if no
     * exchange rate can be determined. Uses exchange rate date passed to the constructor on class instantiation.
     */
    public double getExchangeRate (Currency sourceCurrency, Currency targetCurrency) {
        return exchangeRateMap.computeIfAbsent(new CachedCurrencyKey(sourceCurrency, targetCurrency),
            key -> currencyUtils.getApplicableRate(key.getSource(), key.getTarget(), exchangeRateDate));
    }

    /**
     * Return the exchange rate of the given currency to USD. Throws ErrorDTO exception if no exchange rate
     * can be determined. Uses exchange rate date passed to the constructor on class instantiation.
     */
    public double getExchangeRateToUsd (final Currency currency) {
        return exchangeRateToUsdMap.computeIfAbsent(currency,
            key -> currencyUtils.getApplicableRateToUsd(currency, exchangeRateDate));
    }

    /**
     * Convert the specified value in the given currency to another currency and precision
     */
    public Double convertCurrency (Double value, Currency currency, Currency targetCurrency) {
        if (value != null) {
            if (!currency.equals(targetCurrency)) {
                return currencyUtils.convertCurrency(value, this.getExchangeRate(currency, targetCurrency), targetCurrency.getDecimalPlaces());
            }
            return value;
        }
        return null;
    }

    /**
     * Gets the currency or ANSP currency for the given system configuration item name. ANSP currency
     * is returned if system configuration item name value is "ANSP".
     *
     * @param itemName system configuration item name
     * @return item currency or ANSP currency
     */
    public Currency getSystemConfigurationCurrency(final String itemName) {
        return this.currencyUtils.getSystemConfigurationCurrency(itemName);
    }

    /**
     * Returns the aerodrome currency for provided scope and nationality.
     */
    public Currency getAerodromeCurrency(final FlightmovementCategoryScope scope, final FlightmovementCategoryNationality nationality) {
        return aerodromeCurrencyMap.computeIfAbsent(new CachedScopeNationalityKey(scope, nationality),
            key -> currencyUtils.getAerodromeCurrency(key.getScope(), key.getNationality()));
    }

    /**
     * Returns the approach currency for provided scope and nationality.
     */
    public Currency getApproachCurrency(final FlightmovementCategoryScope scope, final FlightmovementCategoryNationality nationality) {
        return approachCurrencyMap.computeIfAbsent(new CachedScopeNationalityKey(scope, nationality),
            key -> currencyUtils.getApproachCurrency(key.getScope(), key.getNationality()));
    }

    /**
     * Returns the late arrival and departure currency for provided scope and nationality.
     */
    public Currency getLateArrivalDepartureCurrency(final FlightmovementCategoryScope scope, final FlightmovementCategoryNationality nationality) {
        return lateArrivalDepartureCurrencyMap.computeIfAbsent(new CachedScopeNationalityKey(scope, nationality),
            key -> currencyUtils.getLateArrivalDepartureCurrency(key.getScope(), key.getNationality()));
    }

    /**
     * Returns the extend hours surcharge currency for provided scope and nationality.
     */
    public Currency getExtendedHoursSurchargeCurrency(final FlightmovementCategoryScope scope, final FlightmovementCategoryNationality nationality) {
        return extendedHoursSurchargeCurrencyMap.computeIfAbsent(new CachedScopeNationalityKey(scope, nationality),
            key -> currencyUtils.getExtendedHoursSurchargeCurrency(key.getScope(), key.getNationality()));
    }
}
