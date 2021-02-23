package ca.ids.abms.modules.util.models;

import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementValidatorIssue;
import ca.ids.abms.modules.flightmovementsbuilder.utility.ChargesUtility;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ca.ids.abms.util.MiscUtils.nvl;

@Component
public class WhitelistingUtils {
    private static final Logger LOG = LoggerFactory.getLogger(WhitelistingUtils.class);

    private final SystemConfigurationService systemConfigurationService;
    private final ChargesUtility chargesUtility;
    private final CurrencyUtils currencyUtils;

    public WhitelistingUtils(final SystemConfigurationService systemConfigurationService,
                             final ChargesUtility chargesUtility,
                             final CurrencyUtils currencyUtils) {
        this.systemConfigurationService = systemConfigurationService;
        this.chargesUtility = chargesUtility;
        this.currencyUtils = currencyUtils;
    }

    public boolean isWhitelistingEnabled() {
        return systemConfigurationService.getBoolean(SystemConfigurationItemName.WL_ENABLED);
    }

    public boolean isWhitelistingStartDateSet() {
        String whitelistingStartDate = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.WL_START_DATE);
        return !StringUtils.isBlank(whitelistingStartDate);
    }

    public LocalDate getWhitelistingStartDate() {
        LOG.debug("Check if whitelisting is applied");

        // Whitelisting logic is only applied when whitelisting is enabled in the system configuration
        final boolean isWhitelistingEnabled = systemConfigurationService.getBoolean(SystemConfigurationItemName.WL_ENABLED);
        if (!isWhitelistingEnabled) {
            LOG.debug("Whitelisting is disabled");
            return null;
        }

        // The day of flight is equal to or later than the whitelisting start date
        String whitelistingStartDate = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.WL_START_DATE);
        if (StringUtils.isBlank(whitelistingStartDate)) {
            LOG.debug("Can't applied Whitelisting because start date is not set");
            return null;
        }

        whitelistingStartDate = whitelistingStartDate.substring(0, 10);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(whitelistingStartDate, formatter);
    }

    /**
     * Payments must be made in advance and flights which are not prepaid must be declined.  The only exceptions to this are:
     * - 	accounts which have a credit facility with DC-ANSP;
     * - 	state aircraft and military flights identified with type (M) and item 18 with “SYS/STATE AIRCRAFT”
     *      (these flights are not declined, and are not billed);
     * - 	ambulance flights identified with type (G,N,S)  and item 18 with STS/HOSP or STS/MEDEVAC
     *      (these flights are not declined, but are billed)
     * @param flightMovement calculated flight movement
     * @return boolean
     */
    public boolean ifWhitelistingFlightIsBillable(FlightMovement flightMovement) {
        LOG.debug("Check if the flight is whitelisting billable");
        int flightId = flightMovement.getId();
        if (!flightMovement.getAccount().getCashAccount()) {
            LOG.debug("The flight with id {} is not whitelisting billable flight - Account has a credit facility with DC-ANSP", flightId);
            return false;
        }

        String flightType = nvl(flightMovement.getFlightType(), "");
        String item18Status = nvl(flightMovement.getItem18Status(), "");

        //noinspection ConstantConditions
        if (flightType.equalsIgnoreCase("M") && item18Status.contains("STATE")) {
            LOG.debug("The flight with id {} is not whitelisting billable flight - this id a STATE flight", flightId);
            return false;
        }

        LOG.debug("The flight with id {} is whitelisting billable flight", flightId);
        return true;
    }

    /**
     * All charges for whitelisting flights should be in USD
     * @param flightMovement calculated flight movement
     * @return total charges amount
     */
    public double getTotalChargesAmountInUSD(final FlightMovement flightMovement) {

        // used for necessary currency conversion
        final Currency anspCurrency = currencyUtils.getAnspCurrency();
        final Currency usdCurrency = currencyUtils.getCurrencyUSD();

        LocalDateTime dateTimeCurrencyConversion = flightMovement.getDateOfFlight();
        if (flightMovement.getResolutionErrorsSet() != null &&
            flightMovement.getResolutionErrorsSet().contains(FlightMovementValidatorIssue.MISSING_ANSP_EXCHANGE_RATE)) {
            dateTimeCurrencyConversion = LocalDateTime.now();
        }

        final Currency domesticPassengerChargesCurrency = currencyUtils.getSystemConfigurationCurrency(SystemConfigurationItemName.DOMESTIC_PAX_FEE_CURRENCY);
        final Currency internationalPassengerChargesCurrency = currencyUtils.getSystemConfigurationCurrency(SystemConfigurationItemName.INTERNATIONAL_PAX_FEE_CURRENCY);
        final Currency enrouteChargesCurrency = flightMovement.getEnrouteResultCurrency();
        final Currency aerodromeChargesCurrency = flightMovement.getAerodromeChargesCurrency();
        final Currency approachChargesCurrency = flightMovement.getApproachChargesCurrency();
        final Currency lateArrivalDepartureChargesCurrency = flightMovement.getLateArrivalDepartureChargesCurrency();
        final Currency extendedHoursSurchargeCurrency = flightMovement.getExtendedHoursSurchargeCurrency();
        final Currency taspChargeCurrency = flightMovement.getTaspChargeCurrency();

        // these doubles can never be null. Suppression due to java limitation of the return value
        @SuppressWarnings("ConstantConditions") double enrouteCharges = nvl(flightMovement.getEnrouteCharges(), 0d);
        @SuppressWarnings("ConstantConditions") double aerodromeCharges = nvl(flightMovement.getAerodromeCharges(), 0d);
        @SuppressWarnings("ConstantConditions") double approachCharges = nvl(flightMovement.getApproachCharges(), 0d);
        @SuppressWarnings("ConstantConditions") double domesticPassengerCharges = nvl(flightMovement.getDomesticPassengerCharges(), 0d);
        @SuppressWarnings("ConstantConditions") double internationalPassengerCharges = nvl(flightMovement.getInternationalPassengerCharges(), 0d);
        @SuppressWarnings("ConstantConditions") double taspCharges = nvl(flightMovement.getTaspCharge(), 0d);
        @SuppressWarnings("ConstantConditions") double lateArrivalCharges = nvl(flightMovement.getLateArrivalCharges(), 0d);
        @SuppressWarnings("ConstantConditions") double lateDepartureCharges = nvl(flightMovement.getLateDepartureCharges(), 0d);
        @SuppressWarnings("ConstantConditions") double extendedHoursSurcharges = nvl(flightMovement.getExtendedHoursSurcharge(), 0d);
        @SuppressWarnings("ConstantConditions") double parkingCharges = nvl(flightMovement.getParkingCharges(), 0d);

        // all pre-payment and charged amounts are in USD
        double totalChargesAmount = chargesUtility.getAmountFromCharge(enrouteCharges, enrouteChargesCurrency, usdCurrency, dateTimeCurrencyConversion)
            + chargesUtility.getAmountFromCharge(aerodromeCharges, aerodromeChargesCurrency, usdCurrency, dateTimeCurrencyConversion)
            + chargesUtility.getAmountFromCharge(approachCharges, approachChargesCurrency, usdCurrency, dateTimeCurrencyConversion)
            + chargesUtility.getAmountFromCharge(taspCharges, taspChargeCurrency, usdCurrency, dateTimeCurrencyConversion)
            + chargesUtility.getAmountFromCharge(lateArrivalCharges, lateArrivalDepartureChargesCurrency, usdCurrency, dateTimeCurrencyConversion)
            + chargesUtility.getAmountFromCharge(lateDepartureCharges, lateArrivalDepartureChargesCurrency, usdCurrency, dateTimeCurrencyConversion)
            + chargesUtility.getAmountFromCharge(extendedHoursSurcharges, extendedHoursSurchargeCurrency, usdCurrency, dateTimeCurrencyConversion)
            + chargesUtility.getAmountFromCharge(parkingCharges, anspCurrency, usdCurrency, dateTimeCurrencyConversion);

        if (systemConfigurationService.getBoolean(SystemConfigurationItemName.INCLUDE_PASSENGER_FEES_ON_INVOICE)) {
            totalChargesAmount += chargesUtility.getAmountFromCharge(domesticPassengerCharges, domesticPassengerChargesCurrency, usdCurrency, dateTimeCurrencyConversion)
                + chargesUtility.getAmountFromCharge(internationalPassengerCharges, internationalPassengerChargesCurrency, usdCurrency, dateTimeCurrencyConversion);
        }

        return totalChargesAmount;
    }
}
