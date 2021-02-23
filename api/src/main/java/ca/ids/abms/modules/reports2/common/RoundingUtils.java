package ca.ids.abms.modules.reports2.common;

import org.springframework.stereotype.Component;

import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.util.models.Calculation;
import ca.ids.abms.modules.util.models.CurrencyUtils;

@Component
public class RoundingUtils {

    private final CurrencyUtils currencyUtils;

    private final SystemConfigurationService systemConfigurationService;

    private String roundingConfigAviation = null;
    private String roundingConfigNonAviation = null;

    public RoundingUtils(
        final SystemConfigurationService systemConfigurationService,
        final CurrencyUtils currencyUtils) {
        this.systemConfigurationService = systemConfigurationService;
        this.currencyUtils = currencyUtils;
    }

    /**
     * Calculate a single value based on rounding and exchange rates as needed
     *
     * @return double
     */
    public double calculateSingleRoundedValue(Double value, Currency currency, boolean aviation) {
        return calcRoundedValue(value, currency, aviation);
    }

    /**
     * Use to update the cached rounding config value whenever the system configuration
     * settings are updated.
     */
    public void updateRoundingConfig() {
        roundingConfigAviation = systemConfigurationService.getValue(SystemConfigurationItemName.AVIATION_ROUNDING);
        roundingConfigNonAviation = systemConfigurationService.getValue(SystemConfigurationItemName.NONAVIATION_ROUNDING);
    }

    /**
     * Get system config for the number of decimal places to display
     *
     * @return int
     */
    private int getDecimalPlace(Currency currency) {
        return currencyUtils.getDecimalPlaces(currency);
    }

    /**
     * Calculate the return value based on the rounding system settings
     *
     * @return java.util.Currencyouble
     */
    private double calcRoundedValue(Double value, Currency currency, boolean aviation) {
        if (value == null || value.equals(0.0d) || currency == null) {
            return 0d;
        }

        final String rounding = getRoundingConfig(aviation);
        final Integer decimalPlaces = getDecimalPlace(currency);

        if (rounding == null || rounding.equalsIgnoreCase("None")) {
            return Calculation.truncate(value, decimalPlaces);
        } else if (rounding.equalsIgnoreCase("Up")) {
            return Calculation.roundUp(value);
        } else if (rounding.equalsIgnoreCase("Down")) {
            return Calculation.roundDown(value);
        } else {
            return Calculation.roundNormal(value);
        }
    }

    /**
     * Get system config for rounding (caches value so that extra calls to database are not made)
     *
     * @return system config string for rounding
     */
    private String getRoundingConfig(boolean aviation) {
        if (aviation) {
            roundingConfigAviation = resolveRoundingConfig(roundingConfigAviation, SystemConfigurationItemName.AVIATION_ROUNDING);
            return roundingConfigAviation;
        } else {
            roundingConfigNonAviation = resolveRoundingConfig(roundingConfigNonAviation, SystemConfigurationItemName.NONAVIATION_ROUNDING);
            return roundingConfigNonAviation;
        }
    }

    /**
     * Check if value set, set if not, then return
     *
     * @return system config string for rounding
     */
    private String resolveRoundingConfig(String value, String roundingName) {
        if (value == null)
            value = systemConfigurationService.getValue(roundingName);
        return value;
    }

}
