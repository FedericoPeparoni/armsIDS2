package ca.ids.abms.modules.util.models;

import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.error.ErrorVariables;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.currencies.CurrencyExchangeRate;
import ca.ids.abms.modules.currencies.CurrencyExchangeRateService;
import ca.ids.abms.modules.currencies.CurrencyService;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryNationality;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryScope;
import ca.ids.abms.modules.system.BillingOrgCode;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CurrencyUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CurrencyUtils.class);

    /** USD Current code */
    private static final String CURRENCY_USD = "USD";

    private CurrencyService currencyService;
    private CurrencyExchangeRateService currencyExchangeRateService;
    private SystemConfigurationService systemConfigurationService;

    public CurrencyUtils(CurrencyService currencyService, CurrencyExchangeRateService currencyExchangeRateService,
                         SystemConfigurationService systemConfigurationService) {
    	this.currencyService=currencyService;
    	this.currencyExchangeRateService=currencyExchangeRateService;
    	this.systemConfigurationService = systemConfigurationService;
    }

    public CurrencyExchangeRateService getCurrencyExchangeRateService() {
        return this.currencyExchangeRateService;
    }

    /**
     * Get USD currency
     */
    public Currency getCurrencyUSD() {
        return currencyService.findByCurrencyCode (CURRENCY_USD);
    }

    /** Get the ANSP currency */
    public Currency getAnspCurrency() {
        final Currency anspCurrency = this.currencyService.getANSPCurrency();
        if (anspCurrency == null) {
            throw new RuntimeException ("ANSP currency is not defined");
        }
        return anspCurrency;
    }

    /** Convert a currency value to another currency */
    public Double convertCurrency (final Double value, final Currency currency, final Currency targetCurrency, final LocalDateTime date) {
        if (value != null) {
            Double val = Calculation.truncate(value, currency.getDecimalPlaces());
            if (!currency.equals(targetCurrency)) {
                return this.convertCurrency(val, this.getExchangeRate(currency, targetCurrency, date),
                    targetCurrency.getDecimalPlaces());
            }
            return val;
        }
        return null;
    }

    /**
     * Convert currency value using exchange rate and decimal places provided.
     *
     * @param value source currency value
     * @param exchangeRate source to target currency exchange rate
     * @param decimalPlaces target decimal places to round
     * @return target currency value
     */
    public Double convertCurrency (final Double value, final double exchangeRate, final int decimalPlaces) {
        if (value != null) {
            return Calculation.truncate(value * exchangeRate, decimalPlaces);
        }
        return null;
    }

    /**
     * Convert US dollars to another currency using the given exchange rate and target currency precision
     */
    public Double convertUsdToCurrency (final Double value, final double exchangeRateToUsd, final int decimalPlaces) {
        if (value != null) {
            return Calculation.truncate (value / exchangeRateToUsd, decimalPlaces);
        }
        return null;
    }

    /**
     * Get exchange rate from/to the given currencies.
     */
    public double getExchangeRate (final Currency from, final Currency to, final LocalDateTime date) {
        return currencyExchangeRateService.getExchangeRate(from, to, date);
    }

    /**
     * Return exchange rate to target currency for the given date. Throws ErrorDTO if an applicable exchange
     * rate could not be found.
     *
     * NOTE: Update `CurrencyExchangeRateService.getExchangeRate(Currency, Currency, LocalDateTime)` if this
     * method is modified.
     */
    public double getApplicableRate(final Currency source, final Currency target, final LocalDateTime date) {

        // return 1 if currencies are the same
        if (source != null && source.equals(target))
            return 1d;

        // attempt to get exchange rate from target currency
        final CurrencyExchangeRate x = currencyExchangeRateService.getApplicableRate(source, target, date);
        if (x != null && x.getExchangeRate() != null)
            return x.getExchangeRate();

        // attempt to get exchange rate from target currency exchange conversion
        // inverse exchange rate value MUST be great then zero to divide by
        final CurrencyExchangeRate xInverse = currencyExchangeRateService.getApplicableRate(target, source, date);
        if (xInverse != null && xInverse.getExchangeRate() != null && xInverse.getExchangeRate() > 0d)
            return 1 / xInverse.getExchangeRate();

        // attempt to get exchange rate from usd exchange conversion
        // target currency usd exchange rate must be greater then zero to divide by
        final CurrencyExchangeRate sourceToUsd = currencyExchangeRateService.getApplicableRateToUsd(source, date);
        final CurrencyExchangeRate targetToUsd = currencyExchangeRateService.getApplicableRateToUsd(target, date);
        if (sourceToUsd != null && sourceToUsd.getExchangeRate() != null && sourceToUsd.getExchangeRate() >= 0d &&
            targetToUsd != null && targetToUsd.getExchangeRate() != null && targetToUsd.getExchangeRate() > 0d)
            return sourceToUsd.getExchangeRate() / targetToUsd.getExchangeRate();

        // throw exception as no exchange rate could be determined
        ErrorVariables errorVariables = new ErrorVariables();

        errorVariables.addEntry("fromCurrencyCode", source != null ? source.getCurrencyCode() : "n/a");
        errorVariables.addEntry("toCurrencyCode", target != null ? target.getCurrencyCode() : "n/a");
        errorVariables.addEntry("date", date.toLocalDate().toString());

        throw new ErrorDTO.Builder()
            .setErrorMessage("Missing applicable exchange rates for currency {{fromCurrencyCode}} to {{toCurrencyCode}} on {{date}}")
            .setErrorMessageVariables(errorVariables)
            .appendDetails("Please update the exchange rates before continuing")
            .buildInvalidDataException();
    }

    /**
     * Return exchange rate to USD for the given date. Throws ErrorDTO if an applicable exchange rate could
     * not be found.
     */
    public double getApplicableRateToUsd(final Currency currency, final LocalDateTime date) {
        final CurrencyExchangeRate x = currencyExchangeRateService.getApplicableRateToUsd(currency, date);
        if (currency != null && currency.getCurrencyCode() != null && currency.getCurrencyCode().equals("USD")) {
            return 1.0d;
        } else if (x == null || x.getExchangeRate() == null) {
            ErrorVariables errorVariables = new ErrorVariables();

            errorVariables.addEntry("currencyCode", currency != null ? currency.getCurrencyCode() : "n/a");
            errorVariables.addEntry("date", date.toLocalDate().toString());

            throw new ErrorDTO.Builder()
                    .setErrorMessage("Missing applicable exchange rates for currency {{currencyCode}} on {{date}}")
                    .setErrorMessageVariables(errorVariables)
                    .appendDetails("Please update the exchange rates before continuing")
                    .buildInvalidDataException();
        } else {
            return x.getExchangeRate();
        }
    }

    public Currency getCurrency(String code) {
        return currencyService.findByCurrencyCode(code);
    }

    /**
     * Gets the currency or ANSP currency for the given system configuration item name. ANSP currency
     * is returned if system configuration item name value is "ANSP".
     *
     * @param itemName system configuration item name
     * @return item currency or ANSP currency
     */
    public Currency getSystemConfigurationCurrency(final String itemName) {
        if (itemName == null)
            throw new IllegalArgumentException("ItemName must be defined.");
        LOG.debug("Get currency for configuration item: {}", itemName);
        Currency currency = null;
        final SystemConfiguration item = systemConfigurationService.getOneByItemName(itemName);
        if (item != null) {
            String currencyCode;
            if (org.apache.commons.lang.StringUtils.isNotEmpty(item.getCurrentValue())) {
                currencyCode = item.getCurrentValue();
            } else {
                currencyCode = item.getDefaultValue();
            }
            if ("ANSP".equals(currencyCode)) {
                currency = getAnspCurrency();
            } else if (currencyCode != null) {
                currency = getCurrency(currencyCode);
            }
        }
        return currency;
    }

    public int getDecimalPlaces(Currency currency) {
        return Math.max (0, currency.getDecimalPlaces() == null ? 2 : currency.getDecimalPlaces());
    }

    public BillingOrgCode getBillingOrgCode() {
        return systemConfigurationService.getBillingOrgCode();
    }

    public Currency getAerodromeCurrency(
        final FlightmovementCategoryScope scope, final FlightmovementCategoryNationality nationality
    ) {
        // special case made for EANA to support air navigation charge schedules by nationality
        if (getBillingOrgCode() == BillingOrgCode.EANA)
            return getAerodromeCurrencyFromNationalityAndScope(scope, nationality);
        else
            return getAerodromeCurrencyFromScope(scope);
    }

    private Currency getAerodromeCurrencyFromNationalityAndScope(
        final FlightmovementCategoryScope scope, final FlightmovementCategoryNationality nationality
    ) {
        LOG.debug("Get aerodrome currency for nationality: {}", nationality);

        Currency currency;
        if (nationality == FlightmovementCategoryNationality.NATIONAL && scope == FlightmovementCategoryScope.DOMESTIC)
            currency = getSystemConfigurationCurrency(SystemConfigurationItemName.DOMESTIC_AERODROME_CHARGES_CURRENCY);
        else
            currency = getSystemConfigurationCurrency(SystemConfigurationItemName.INTERNATIONAL_AERODROME_CHARGES_CURRENCY);

        if (currency == null)
            throw new IllegalArgumentException(
                String.format("Aerodrome Charge Currency for Flight movement nationality %s must be defined.",
                    nationality.name()));

        return currency;
    }

    private Currency getAerodromeCurrencyFromScope(final FlightmovementCategoryScope scope) {
        LOG.debug("Get aerodrome currency for scope: {}", scope);

        Currency currency;
        if (scope == FlightmovementCategoryScope.DOMESTIC)
            currency = getSystemConfigurationCurrency(SystemConfigurationItemName.DOMESTIC_AERODROME_CHARGES_CURRENCY);
        else if (scope == FlightmovementCategoryScope.REGIONAL)
            currency = getSystemConfigurationCurrency(SystemConfigurationItemName.REGIONAL_AERODROME_CHARGES_CURRENCY);
        else
            currency = getSystemConfigurationCurrency(SystemConfigurationItemName.INTERNATIONAL_AERODROME_CHARGES_CURRENCY);

        if (currency == null)
            throw new IllegalArgumentException(
                String.format("Aerodrome Charge Currency for Flight movement scope %s must be defined.", scope.name()));

        return currency;
     }

    public Currency getApproachCurrency(
        final FlightmovementCategoryScope scope, final FlightmovementCategoryNationality nationality
    ) {
        // special case made for EANA to support air navigation charge schedules by nationality
        if (getBillingOrgCode() == BillingOrgCode.EANA)
            return getApproachCurrencyFromNationalityAndScope(scope, nationality);
        else
            return getApproachCurrencyFromScope(scope);
    }

    private Currency getApproachCurrencyFromNationalityAndScope(
        final FlightmovementCategoryScope scope, final FlightmovementCategoryNationality nationality
    ) {
        LOG.debug("Get approach currency for nationality: {}", nationality);

        Currency currency;
        if (nationality == FlightmovementCategoryNationality.NATIONAL && scope == FlightmovementCategoryScope.DOMESTIC)
            currency = getSystemConfigurationCurrency(SystemConfigurationItemName.DOMESTIC_APPROACH_CHARGES_CURRENCY);
        else
            currency = getSystemConfigurationCurrency(SystemConfigurationItemName.INTERNATIONAL_APPROACH_CHARGES_CURRENCY);

        if (currency == null)
            throw new IllegalArgumentException(
                String.format("Approach Charge Currency for Flight movement nationality %s must be defined.", nationality.name()));

        return currency;
    }

    private Currency getApproachCurrencyFromScope(final FlightmovementCategoryScope scope) {
        LOG.debug("Get approach currency for scope: {}", scope);

        Currency currency;
        if (scope == FlightmovementCategoryScope.DOMESTIC)
            currency = getSystemConfigurationCurrency(SystemConfigurationItemName.DOMESTIC_APPROACH_CHARGES_CURRENCY);
        else if (scope == FlightmovementCategoryScope.REGIONAL)
            currency = getSystemConfigurationCurrency(SystemConfigurationItemName.REGIONAL_APPROACH_CHARGES_CURRENCY);
        else
            currency = getSystemConfigurationCurrency(SystemConfigurationItemName.INTERNATIONAL_APPROACH_CHARGES_CURRENCY);

        if (currency == null)
            throw new IllegalArgumentException(
                String.format("Approach Charge Currency for Flight movement scope %s must be defined.", scope.name()));

        return currency;
     }

    public Currency getLateArrivalDepartureCurrency(
        final FlightmovementCategoryScope scope, final FlightmovementCategoryNationality nationality
    ) {
        // special case made for EANA to support air navigation charge schedules by nationality
        if (getBillingOrgCode() == BillingOrgCode.EANA)
            return getLateArrivalDepartureCurrencyFromNationalityAndScope(scope, nationality);
        else
            return getLateArrivalDepartureCurrencyFromScope(scope);
    }

    private Currency getLateArrivalDepartureCurrencyFromNationalityAndScope(
        final FlightmovementCategoryScope scope, final FlightmovementCategoryNationality nationality
    ) {
        LOG.debug("Get late arrival/departure currency for nationality: {}", nationality);

        Currency currency;
        if (nationality == FlightmovementCategoryNationality.NATIONAL && scope == FlightmovementCategoryScope.DOMESTIC)
            currency = getSystemConfigurationCurrency(SystemConfigurationItemName.DOMESTIC_LATE_ARRIVAL_DEPARTURE_CHARGES_CURRENCY);
        else
            currency = getSystemConfigurationCurrency(SystemConfigurationItemName.INTERNATIONAL_LATE_ARRIVAL_DEPARTURE_CHARGES_CURRENCY);

        if (currency == null)
            throw new IllegalArgumentException(
                String.format("Late arrival/departure Charge Currency for Flight movement nationality %s must be defined.", nationality.name()));

        return currency;
    }

    private Currency getLateArrivalDepartureCurrencyFromScope(final FlightmovementCategoryScope scope) {
        LOG.debug("Get late arrival/departure currency for scope: {}", scope);

        Currency currency;
        if (scope == FlightmovementCategoryScope.DOMESTIC)
            currency = getSystemConfigurationCurrency(SystemConfigurationItemName.DOMESTIC_LATE_ARRIVAL_DEPARTURE_CHARGES_CURRENCY);
        else if (scope == FlightmovementCategoryScope.REGIONAL)
            currency = getSystemConfigurationCurrency(SystemConfigurationItemName.REGIONAL_LATE_ARRIVAL_DEPARTURE_CHARGES_CURRENCY);
        else
            currency = getSystemConfigurationCurrency(SystemConfigurationItemName.INTERNATIONAL_LATE_ARRIVAL_DEPARTURE_CHARGES_CURRENCY);

        if (currency == null)
        	throw new IllegalArgumentException(
        	    String.format("Late arrival/departure Charge Currency for Flight movement scope %s must be defined.", scope.name()));

        return currency;
     }

    public Currency getExtendedHoursSurchargeCurrency(
        final FlightmovementCategoryScope scope, final FlightmovementCategoryNationality nationality
    ) {
        // special case made for EANA to support air navigation charge schedules by nationality
        if (getBillingOrgCode() == BillingOrgCode.EANA)
            return getExtendedHoursSurchargeCurrencyFromNationalityAndScope(scope, nationality);
        else
            return getExtendedHoursSurchargeCurrencyFromScope(scope);
    }

    private Currency getExtendedHoursSurchargeCurrencyFromNationalityAndScope(
        final FlightmovementCategoryScope scope, final FlightmovementCategoryNationality nationality
    ) {
        LOG.debug("Get extended hours surcharge currency for nationality: {}", nationality);

        Currency currency;
        if (nationality == FlightmovementCategoryNationality.NATIONAL && scope == FlightmovementCategoryScope.DOMESTIC)
            currency = getSystemConfigurationCurrency(SystemConfigurationItemName.DOMESTIC_EXTENDED_HOURS_SURCHARGE_CURRENCY);
        else
            currency = getSystemConfigurationCurrency(SystemConfigurationItemName.INTERNATIONAL_EXTENDED_HOURS_SURCHARGE_CURRENCY);

        if (currency == null)
            throw new IllegalArgumentException(
                String.format("Extended Hours Surcharge Currency for Flight movement nationality %s must be defined.", nationality.name()));

        return currency;
    }

    private Currency getExtendedHoursSurchargeCurrencyFromScope(final FlightmovementCategoryScope scope) {
        LOG.debug("Get extended hours surcharge currency for scope: {}", scope);

        Currency currency;
        if (scope == FlightmovementCategoryScope.DOMESTIC)
            currency = getSystemConfigurationCurrency(SystemConfigurationItemName.DOMESTIC_EXTENDED_HOURS_SURCHARGE_CURRENCY);
        else if (scope == FlightmovementCategoryScope.REGIONAL)
            currency = getSystemConfigurationCurrency(SystemConfigurationItemName.REGIONAL_EXTENDED_HOURS_SURCHARGE_CURRENCY);
        else
            currency = getSystemConfigurationCurrency(SystemConfigurationItemName.INTERNATIONAL_EXTENDED_HOURS_SURCHARGE_CURRENCY);

        if (currency == null)
            throw new IllegalArgumentException(
                String.format("Extended Hours Surcharge Currency for Flight movement scope %s must be defined.", scope.name()));

        return currency;
    }
}
