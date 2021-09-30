package ca.ids.abms.modules.exemptions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ca.ids.abms.modules.aerodromes.cluster.RepositioningAerodromeCluster;
import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.exemptions.charges.methods.ExemptionChargeMethodResult;
import ca.ids.abms.modules.exemptions.charges.providers.ExemptionChargeProvider;
import ca.ids.abms.modules.exemptions.charges.providers.UnifiedTaxExemptionChargeProvider;
import ca.ids.abms.modules.exemptions.flightroutes.ExemptFlightRoute;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.util.models.CurrencyUtils;

@Service
@Transactional
public class ExemptionTypeService {

    private final CurrencyUtils currencyUtils;
    private final List<ExemptionChargeProvider> exemptionChargeProviders;
    private final UnifiedTaxExemptionChargeProvider unifiedTaxExemptionChargeProvider;
    private final List<ExemptionTypeProvider> exemptionTypeProviders;

    ExemptionTypeService(
        final CurrencyUtils currencyUtils,
        final List<ExemptionChargeProvider> exemptionChargeProviders,
        final UnifiedTaxExemptionChargeProvider unifiedTaxExemptionChargeProvider,
        final List<ExemptionTypeProvider> exemptionTypeProviders
    ) {
        this.currencyUtils = currencyUtils;
        this.exemptionChargeProviders = exemptionChargeProviders;
        this.unifiedTaxExemptionChargeProvider = unifiedTaxExemptionChargeProvider;
        this.exemptionTypeProviders = exemptionTypeProviders;
    }

    /**
     * Apply exemptions to the provided flight movement.
     */
    @Transactional(readOnly = true)
    public void resolveFlightMovementExemptions(final FlightMovement flightMovement) {
        Preconditions.checkArgument(flightMovement != null);

        // retrieve all exemption types from all exemption type providers based on provided flight movement
        Collection<ExemptionType> exemptionTypes = findFlightMovementExemptions(flightMovement);

        // loop through each exempting charge provider and apply exemptions to flight movement
        for (ExemptionChargeProvider exemptionChargeProvider : exemptionChargeProviders) {
            exemptionChargeProvider.apply(flightMovement, exemptionTypes);
        }

        // updated total charges after exemptions applied
        flightMovement.setTotalCharges(getTotalCharges(flightMovement));
    }

    /**
     * Apply exemptions to the unified tax of the given aircraft registration.
     */
    @Transactional(readOnly = true)
    public ExemptionChargeMethodResult resolveUnifiedTaxExemptions(
    		AircraftRegistration ar, 
            final LocalDateTime startDate,
            final LocalDateTime endDate,
    		double unifiedTaxChargeValue, final Currency currency) {

        // retrieve all exemption types from all exemption type providers based on provided flight movement
    	Collection<ExemptionType> exemptions = findUnifiedTaxExemptions(ar, startDate, endDate);

        ExemptionChargeMethodResult result = unifiedTaxExemptionChargeProvider.apply(unifiedTaxChargeValue, currency, exemptions);
        return result;
    }
   
    /**
     * Returns a collection of exemption types that should be applied to the provided flight movement.
     */
    private Collection<ExemptionType> findFlightMovementExemptions(final FlightMovement flightMovement) {
        Preconditions.checkArgument(flightMovement != null);

        Collection<ExemptionType> exemptionTypes = new ArrayList<>();

        // loop through each exemption type provider and collect all exemptions
        for (ExemptionTypeProvider exemptionTypeProvider : exemptionTypeProviders) {

            // return collection of exemptions from provider
            Collection<ExemptionType> exemptionType = exemptionTypeProvider.findApplicableExemptions(flightMovement);

            // only add non-null values to collection
            if (exemptionType != null) {
                exemptionTypes.addAll(exemptionType.stream()
                    .filter(e -> Objects.nonNull(e) && isNotThruFlightSegmentExemption(flightMovement, e))
                    .collect(Collectors.toList()));
            }
        }

        return exemptionTypes;
    }

    /**
     * Returns a collection of exemption types that should be applied to the provided aircraft registration.
     */
    private Collection<ExemptionType> findUnifiedTaxExemptions(
    		final AircraftRegistration aircraftRegistration,
            final LocalDateTime startDate,
            final LocalDateTime endDate) {
        Preconditions.checkArgument(aircraftRegistration != null);

        Collection<ExemptionType> exemptionTypes = new ArrayList<>();

        // loop through each exemption type provider and collect all exemptions
        for (ExemptionTypeProvider exemptionTypeProvider : exemptionTypeProviders) {

            // return collection of exemptions from provider
            Collection<ExemptionType> exemptionType = exemptionTypeProvider.findApplicableExemptions(aircraftRegistration, startDate, endDate);

            // only add non-null values to collection
            if (exemptionType != null) {
                exemptionTypes.addAll(exemptionType.stream()
                    .filter(e -> Objects.nonNull(e))
                    .collect(Collectors.toList()));
            }
        }

        return exemptionTypes;
    }
    
    /**
     * Returns the recalculated flight movement total charges after exemptions applied. It must always respect
     * the charge currencies and be returned to the ANSP currency.
     */
    private double getTotalCharges(final FlightMovement flightMovement) {

        LocalDateTime exchageRateDate = flightMovement.getBillingDate() == null
            ? flightMovement.getDateOfFlight()
            : flightMovement.getBillingDate();

        Currency domesticPassengerChargesCurrency = currencyUtils.getSystemConfigurationCurrency(SystemConfigurationItemName.DOMESTIC_PAX_FEE_CURRENCY);
        Currency internationalPassengerChargesCurrency = currencyUtils.getSystemConfigurationCurrency(SystemConfigurationItemName.INTERNATIONAL_PAX_FEE_CURRENCY);
        Currency anspTargetCurrency = currencyUtils.getAnspCurrency();
        Currency aerodromeChargesCurrency = currencyUtils.getAerodromeCurrency(
            flightMovement.getFlightCategoryScope(), flightMovement.getFlightCategoryNationality());
        Currency approachChargesCurrency = currencyUtils.getApproachCurrency(
            flightMovement.getFlightCategoryScope(), flightMovement.getFlightCategoryNationality());
        Currency lateArrivalDepartureChargesCurrency = currencyUtils.getLateArrivalDepartureCurrency(
            flightMovement.getFlightCategoryScope(), flightMovement.getFlightCategoryNationality());
        Currency extendedHoursSurchargeCurrency = currencyUtils.getExtendedHoursSurchargeCurrency(
            flightMovement.getFlightCategoryScope(), flightMovement.getFlightCategoryNationality());

        Double enrouteChargesAnsp = currencyUtils.convertCurrency(flightMovement.getEnrouteCharges(),
            currencyUtils.getCurrencyUSD(), anspTargetCurrency, exchageRateDate);
        Double lateArrivalChargesAnsp = currencyUtils.convertCurrency(flightMovement.getLateArrivalCharges(),
            lateArrivalDepartureChargesCurrency, anspTargetCurrency, exchageRateDate);
        Double lateDepartureChargesAnsp = currencyUtils.convertCurrency(flightMovement.getLateDepartureCharges(),
            lateArrivalDepartureChargesCurrency, anspTargetCurrency, exchageRateDate);
        Double parkingChargesAnsp = flightMovement.getParkingCharges();
        Double approachChargesAnsp = currencyUtils.convertCurrency(flightMovement.getApproachCharges(),
            approachChargesCurrency, anspTargetCurrency, exchageRateDate);
        Double aerodromeChargesAnsp = currencyUtils.convertCurrency(flightMovement.getAerodromeCharges(),
            aerodromeChargesCurrency, anspTargetCurrency, exchageRateDate);
        Double domesticPassengerChargesAnsp = currencyUtils.convertCurrency(flightMovement.getDomesticPassengerCharges(),
            domesticPassengerChargesCurrency, anspTargetCurrency, exchageRateDate);
        Double intlPassengerChargesAnsp = currencyUtils.convertCurrency(flightMovement.getInternationalPassengerCharges(),
            internationalPassengerChargesCurrency, anspTargetCurrency, exchageRateDate);
        Double extendedHoursSurchargesAnsp = currencyUtils.convertCurrency(flightMovement.getExtendedHoursSurcharge(),
            extendedHoursSurchargeCurrency, anspTargetCurrency, exchageRateDate);

        return zeroIfNull(enrouteChargesAnsp)
            + zeroIfNull(lateArrivalChargesAnsp)
            + zeroIfNull(lateDepartureChargesAnsp)
            + zeroIfNull(parkingChargesAnsp)
            + zeroIfNull(approachChargesAnsp)
            + zeroIfNull(aerodromeChargesAnsp)
            + zeroIfNull(domesticPassengerChargesAnsp)
            + zeroIfNull(intlPassengerChargesAnsp)
            + zeroIfNull(extendedHoursSurchargesAnsp);
    }

    /**
     * Return false if the flight movement is a thru flight plan and the exemption type has already been applied
     * for each flight segment within {@code FlightMovementBuilder.isEnrouteExemptionApplied(FlightMovement, RouteSegment)}
     */
    private boolean isNotThruFlightSegmentExemption(final FlightMovement flightMovement, final ExemptionType exemptionType) {
        Preconditions.checkArgument(flightMovement != null && exemptionType != null);

        // only ExemptFlightRoute and RepositioningAerodromeCluster exemption types are applied to thru flight segments
        boolean isThruFlightSegmentExemption = Boolean.TRUE.equals(flightMovement.getThruFlight()) && (
            exemptionType instanceof ExemptFlightRoute || exemptionType instanceof RepositioningAerodromeCluster);

        return !isThruFlightSegmentExemption;
    }

    /**
     * Returns {@code 0.0} if value is null, else returns provided value.
     */
    private double zeroIfNull(final Double value) {
        return value == null ? 0.0 : value;
    }
}
