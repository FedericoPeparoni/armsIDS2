package ca.ids.abms.modules.flightmovementsbuilder.utility;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.aerodromeoperationalhours.AerodromeOperationalHoursService;
import ca.ids.abms.modules.aerodromes.AerodromeCategory;
import ca.ids.abms.modules.aerodromeserviceoutages.AerodromeServiceOutage;
import ca.ids.abms.modules.aerodromeserviceoutages.AerodromeServiceOutageService;
import ca.ids.abms.modules.aerodromeservicetypes.DiscountType;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.exemptions.ExemptionTypeService;
import ca.ids.abms.modules.exemptions.FlightNotesUtility;
import ca.ids.abms.modules.flightmovements.*;
import ca.ids.abms.modules.flightmovements.enumerate.*;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBillable;
import ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementValidatorIssue;
import ca.ids.abms.modules.flightmovementsbuilder.enumerate.KCAAFlightChargesType;
import ca.ids.abms.modules.flightmovementsbuilder.utility.charges.AdditionalChargeProvider;
import ca.ids.abms.modules.flightmovementsbuilder.utility.discount.DiscountUtility;
import ca.ids.abms.modules.flightmovementsbuilder.vo.DeltaFlightVO;
import ca.ids.abms.modules.flightmovementsbuilder.vo.ThruFlightPlanVO;
import ca.ids.abms.modules.formulas.FormulaEvaluator;
import ca.ids.abms.modules.formulas.enroute.EnrouteAirNavigationChargeFormula;
import ca.ids.abms.modules.formulas.enroute.EnrouteAirNavigationChargeFormulaService;
import ca.ids.abms.modules.formulas.ldp.ChargeTypes.LdpBillingFormulaChargeType;
import ca.ids.abms.modules.formulas.ldp.LdpBillingFormula;
import ca.ids.abms.modules.mtow.AverageMtowFactor;
import ca.ids.abms.modules.mtow.AverageMtowFactorService;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.routesegments.RouteSegment;
import ca.ids.abms.modules.system.BillingOrgCode;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.util.models.ApplicationConstants;
import ca.ids.abms.modules.util.models.Calculation;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.modules.util.models.DateTimeUtils;
import ca.ids.abms.spreadsheets.AerodromeCharges;
import ca.ids.abms.spreadsheets.FlightChargeType;
import ca.ids.abms.spreadsheets.ParkingCharges;
import ca.ids.abms.spreadsheets.SSService;
import ca.ids.abms.util.StringUtils;
import ca.ids.abms.util.converter.JSR310DateConverters;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.LinkedListMultimap;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static ca.ids.abms.util.MiscUtils.nvl;

@Component
public class ChargesUtility {
    private static final Logger LOG = LoggerFactory.getLogger(ChargesUtility.class);
    public static final String ARRIVAL = "arrival";
    public static final String DEPARTURE  = "departure";

    private final AverageMtowFactorService averageMtowFactorService;
    private final FormulaEvaluator formulaEvaluator;
    private final SystemConfigurationService systemConfigurationService;
    private final FlightMovementRepository flightMovementRepository;
    private final SSService ssService;
    private final DeltaFlightUtility deltaFlightUtility;
    private final ExemptionTypeService exemptionTypeService;
    private final FlightMovementAerodromeService flightMovementAerodromeService;
    private final FlightMovementBillable flightMovementBillable;
    private final CurrencyUtils currencyUtils;
    private final KCAAFlightUtility kcaaFlightUtility;
    private final ReportHelper reportHelper;
    private final EnrouteAirNavigationChargeFormulaService enrouteAirNavigationChargeFormulaService;
    private final List<AdditionalChargeProvider> additionalChargeProviders;
    private final DiscountUtility discountUtility;
    private final AerodromeServiceOutageService aerodromeServiceOutageService;
    private final AerodromeOperationalHoursService aerodromeOperationalHoursService;
    private final ThruFlightPlanUtility thruFlightPlanUtility;

    @SuppressWarnings("squid:S00107")
    ChargesUtility(
        final FormulaEvaluator formulaEvaluator,
        final AverageMtowFactorService averageMtowFactorService,
        final FlightMovementRepository flightMovementRepository,
        final SSService ssService,
        final SystemConfigurationService systemConfigurationService,
        final ExemptionTypeService exemptionTypeService,
        final FlightMovementBillable flightMovementBillable,
        final DeltaFlightUtility deltaFlightUtility,
        final FlightMovementAerodromeService flightMovementAerodromeService,
        final CurrencyUtils currencyUtils,
        final KCAAFlightUtility kcaaFlightUtility,
        final ReportHelper reportHelper,
        final EnrouteAirNavigationChargeFormulaService enrouteAirNavigationChargeFormulaService,
        final List<AdditionalChargeProvider> additionalChargeProviders,
        final DiscountUtility discountUtility,
        final AerodromeServiceOutageService aerodromeServiceOutageService,
        final AerodromeOperationalHoursService aerodromeOperationalHoursService,
        final ThruFlightPlanUtility thruFlightPlanUtility
    ) {
        this.formulaEvaluator = formulaEvaluator;
        this.averageMtowFactorService = averageMtowFactorService;
        this.flightMovementRepository = flightMovementRepository;
        this.ssService = ssService;
        this.systemConfigurationService = systemConfigurationService;
        this.deltaFlightUtility = deltaFlightUtility;
        this.exemptionTypeService = exemptionTypeService;
        this.flightMovementAerodromeService = flightMovementAerodromeService;
        this.currencyUtils = currencyUtils;
        this.kcaaFlightUtility = kcaaFlightUtility;
        this.flightMovementBillable = flightMovementBillable;
        this.reportHelper = reportHelper;
        this.enrouteAirNavigationChargeFormulaService = enrouteAirNavigationChargeFormulaService;
        this.additionalChargeProviders = additionalChargeProviders;
        this.discountUtility = discountUtility;
        this.aerodromeServiceOutageService = aerodromeServiceOutageService;
        this.aerodromeOperationalHoursService = aerodromeOperationalHoursService;
        this.thruFlightPlanUtility = thruFlightPlanUtility;
    }

    /**
     * This method tests for NULL/empty flight object
     *
     * @param providedFlightMovement flight movement
     * @return Boolean
     */
    private Boolean testNullFlight(FlightMovement providedFlightMovement) {
        if (providedFlightMovement == null) {
            LOG.error("Provided FlightMovement is NULL");

            return false;
        } else {
            return true;
        }
    }

    /**
     * This method tests for NULL mtow upper limit
     *
     * @param providedFlightMovement flight movement to test
     * @return Boolean
     */
    private Boolean testMtowUpperLimit(FlightMovement providedFlightMovement) {
        AverageMtowFactor amf = averageMtowFactorService.findAverageMtowFactorByUpperLimitAndScope (providedFlightMovement.getActualMtow(), providedFlightMovement.getFlightCategoryScope());

        if (amf == null || amf.getUpperLimit() == null) {
            LOG.error("No mtow upper limit found. Default to aircraft actual MTOW");

            return false;
        } else {
            return true;
        }
    }

    /**
     * This method tests for NULL mtow value
     *
     * @param providedFlightMovement flight movement to test
     * @return Boolean
     */
    private Boolean testMtowNull(FlightMovement providedFlightMovement) {
        if (providedFlightMovement.getActualMtow() == null) {
            LOG.error("MTOW is null");

            return false;
        } else {
            return true;
        }
    }

    /**
     * This method tests for NULL Average MTOW Factor
     *
     * @param amf average mtow factor to test
     * @return Boolean
     */
    private Boolean testAverageMtowFactor(AverageMtowFactor amf) {
        if (amf == null) {
            LOG.error("No average MTOW factor is defined for MTOW ");

            return false;
        } else {
            return true;
        }
    }

    /**
     * This method tests for movement type
     *
     * @param providedFlightMovement flight movement to test
     * @return Boolean
     */
    private Boolean testFlightChargeType(FlightMovement providedFlightMovement) {
        FlightChargeType flightChargeType = getFlightChargeType(providedFlightMovement);

        if (flightChargeType == null) {
            LOG.error("FlightChargeType is NULL");

            return false;
        } else {
            return true;
        }
    }

    /**
     * This method tests if we should calculate crossing distance costs or not
     * Charges are not calculated for OTHER, OVERFLIGHT, or NULL flight types
     */
    private boolean testMovementCategoryType(FlightMovement providedFlightMovement) {

        if (providedFlightMovement.getFlightCategoryType() == null
                || providedFlightMovement.isOTHER()
                || providedFlightMovement.getFlightCategoryType() == FlightmovementCategoryType.OVERFLIGHT) {
            LOG.warn("flightMovementCategoryType is NULL, OTHER, or OVERFLIGHT");
            return false;
        } else {
            return true;
        }
    }

    /**
     * This method tests for NULL aerodrome category
     *
     * @param aerodromeCategory aerodromeCategory
     * @return Boolean
     */
    private Boolean testAerodromeCategory(AerodromeCategory aerodromeCategory) {
        if (aerodromeCategory == null) {
            LOG.warn("AerodromeCategory for provided aerodrome is unknown");
            return false;
        } else {
            return true;
        }
    }

    /**
     * This method resolve aerodrome identifier from arrival aerodrome if possible
     * @return String
     */
    public String resolveArrivalAerodrome(FlightMovement flightMovement) {
        if (StringUtils.isNotBlank(flightMovement.getArrivalAd()))
            return flightMovement.getArrivalAd();
        else if (StringUtils.isNotBlank(flightMovement.getDestAd()))
            return flightMovement.getDestAd();
        else
            return null;
    }


    /**
     * This method checks the system config for applying adap, tests for NULL in case of error
     *
     * @return Boolean
     */
    private boolean getApplyAdapConfig() {
        String value = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.APPROACH_FEE_LABEL);
        return value != null && value.equalsIgnoreCase("ADAP");
    }

    /**
     * This method checks the system config for adap location setting, tests for NULL in case of error
     *
     * @return String
     */
    public String getApplyAdapLocation() {
        return ObjectUtils.firstNonNull(systemConfigurationService
            .getCurrentValue(SystemConfigurationItemName.ADAP_AERODROME), "");
    }

    /**
     * This method calculates and set all possible charges for FlightMovement.
     *
     * @param providedFlightMovement flight movement
     * @return Boolean
     */
    public boolean setCharges(FlightMovement providedFlightMovement) {
        Boolean applyAdap = getApplyAdapConfig();
        String aerodromeAdap = getApplyAdapLocation();

        if (!testNullFlight(providedFlightMovement)) {
            return false;
        }

        doInitializeAllAmounts(providedFlightMovement);

        doCalculateAdapCharges(providedFlightMovement, applyAdap, aerodromeAdap);

        doCalculateAndSetCrossingCosts(providedFlightMovement);

        // resolve aerodrome identifier from arrival aerodrome if possible
        String arrivalAerodromeIdentifier = resolveArrivalAerodrome(providedFlightMovement);

        // in order to calculate related charges, we need to make sure the aerodromes has aerodrome category associated with it
        AerodromeCategory arrivalAerodromeCategory = this.flightMovementAerodromeService
            .resolveLocationToAdCategory(arrivalAerodromeIdentifier, providedFlightMovement.getItem18Dest(), AdResolveType.AD_TYPE_DESTINATION);

        AerodromeCategory departureAerodromeCategory = this.flightMovementAerodromeService
            .resolveLocationToAdCategory(providedFlightMovement.getDepAd(), providedFlightMovement.getItem18Dep(), AdResolveType.AD_TYPE_DEPARTURE);

        LocalTime departureTimeOfDayForCharges = deriveDepartureTimeOfDayForCharge(providedFlightMovement);

        doArrivalRelatedCharges(providedFlightMovement, applyAdap, aerodromeAdap, arrivalAerodromeIdentifier, arrivalAerodromeCategory);

        doDepartureRelatedCharges(providedFlightMovement, departureAerodromeCategory, departureTimeOfDayForCharges);

        doCalculateAndSetExtendedHoursSurcharge(providedFlightMovement, arrivalAerodromeIdentifier, arrivalAerodromeCategory, departureAerodromeCategory, departureTimeOfDayForCharges);

        if (systemConfigurationService.getBillingOrgCode() == BillingOrgCode.KCAA) {
            this.handleKCAACalculations(providedFlightMovement, applyAdap);
        }

        // 2019-05-28
        // ZAMBIA specific requirement:
        // International arrival flights with an enroute distance of 0 will be charged 15% of the landing fee for that flight

        if (systemConfigurationService.getBillingOrgCode() == BillingOrgCode.ZACL &&
                providedFlightMovement.getFlightCategoryScope().equals(FlightmovementCategoryScope.INTERNATIONAL) &&
                providedFlightMovement.getFlightCategoryType().equals(FlightmovementCategoryType.ARRIVAL) &&
                providedFlightMovement.getBillableCrossingDist() != null &&
                providedFlightMovement.getBillableCrossingDist() >= 0.0 &&
                (providedFlightMovement.getEnrouteCharges() == null ||
                    providedFlightMovement.getEnrouteCharges() <= 0.0)) {

            // calculate landing charges for the arrival aerodrome category
            Double charges = calculateLandingFeesAd(providedFlightMovement);

            // set en-route cost to 15% of landing fee

            Double cost = charges*15.0/100.0;
            providedFlightMovement.setEnrouteCharges(cost);
        }

        // calculate all additional charge amounts, not included in flight movement total
        for (AdditionalChargeProvider additionalChargeProvider : additionalChargeProviders) {
            additionalChargeProvider.calculate(providedFlightMovement);
        }

        exemptionTypeService.resolveFlightMovementExemptions(providedFlightMovement);

        applySmallAircraftExemptions(providedFlightMovement);

        calculateAndSetTotalChargesAmount(providedFlightMovement);
        calculateAndSetTotalChargesAmountInUSD(providedFlightMovement);

        return providedFlightMovement.getTotalCharges() != null && providedFlightMovement.getTotalCharges() != 0;
    }

    private static Map<String, Object> emptyFormulaMap() {
        Map<String, Object> map = new HashMap<>();

        map.put(CostFormulaVar.ACCOUNTDISCOUNT.varName(), 0d);
        map.put(CostFormulaVar.AVGMASSFACTOR.varName(), 0d);
        map.put(CostFormulaVar.ENTRIESNUMBER.varName(), 0d);
        map.put(CostFormulaVar.FIRENTRYFEE.varName(), 0d);
        map.put(CostFormulaVar.MTOW.varName(), 0d);
        map.put(CostFormulaVar.SCHEDCROSSDIST.varName(), 0d);
        map.put(CostFormulaVar.APPROACHFEE.varName(), 0d);
        map.put(CostFormulaVar.AERODROMEFEE.varName(), 0d);

        return map;
    }

    private String getMtowFactorClass (final FlightMovement flightMovement) {

        // if MTOW factor class IS used, we must get the AMF which corresponds with the fm movement scope
        if (averageMtowFactorService.isMtowFactorClassUsed() && flightMovement.getFlightCategoryScope() != null) {
            return flightMovement.getFlightCategoryScope().getMtowFactorClass();
        }

        // if MTOW factor class is NOT used or scope is missing, default to DOMESTIC value
        return FlightmovementCategoryScope.DOMESTIC.getMtowFactorClass();
    }

    /**
     * Wrapper for ForumlaEvaluator.evalDouble(String, Map<String, Object>) to force zero or greater results only.
     *
     * @param formula passed as first parameter
     * @param vars passed as second parameter
     * @return result 0.0d or greater
     * @throws Exception evaluation exception
     */
    private double evalDoublePositive(final String formula, final Map<String, Object> vars) throws Exception {
        double result = formulaEvaluator.evalDouble(formula, vars);

        return result > 0.0d ? result : 0.0d;
    }

    /**
     * Get flight charge type based on flight movement category.
     */
    private FlightChargeType getFlightChargeType(final FlightMovement flightMovement) {
        if (flightMovement == null)
            return null;

        // special case made for EANA to support air navigation charge schedules by nationality
        if (systemConfigurationService.getBillingOrgCode() == BillingOrgCode.EANA)
            return getFlightChargeTypeFromNationalityAndScope(flightMovement.getFlightCategoryNationality(),
                flightMovement.getFlightCategoryScope());
        else
            return getFlightChargeTypeFromScope(flightMovement.getFlightCategoryScope());
    }

    /**
     * Get flight charge type based on flight movement category nationality.
     */
    private FlightChargeType getFlightChargeTypeFromNationalityAndScope(
        final FlightmovementCategoryNationality nationality, final FlightmovementCategoryScope scope
    ) {
        if (nationality == FlightmovementCategoryNationality.NATIONAL && scope == FlightmovementCategoryScope.DOMESTIC)
            return FlightChargeType.DOMESTIC;
        else if (nationality == FlightmovementCategoryNationality.NATIONAL || nationality == FlightmovementCategoryNationality.FOREIGN)
            return FlightChargeType.INTERNATIONAL;
        else
            return null;
    }

    /**
     * Get flight charge type based on flight movement category scope.
     */
    private FlightChargeType getFlightChargeTypeFromScope (final FlightmovementCategoryScope scope) {
        if (scope != null) {
            switch (scope) {
            case INTERNATIONAL:
                return FlightChargeType.INTERNATIONAL;
            case DOMESTIC:
                return FlightChargeType.DOMESTIC;
            case REGIONAL:
                return FlightChargeType.REGIONAL;
            }
        }
        return null;
    }

    /**
     * Return LdpBillingFormula from AerodromeCategory for provided
     * LdpBillingFormulaChargeType
     *
     * @param providedAerodromeCategory AerodromeCategory
     * @param providedLdpBillingFormulaChargeType LdpBillingFormulaChargeType
     * @return LdpBillingFormula
     */
    private LdpBillingFormula getLdpBillingFormulaFromAerodromeCategory(AerodromeCategory providedAerodromeCategory, LdpBillingFormulaChargeType providedLdpBillingFormulaChargeType) {
        if (providedLdpBillingFormulaChargeType == null || providedAerodromeCategory == null) {
            return null;
        }

        Set<LdpBillingFormula> ldpBillingFormulas = providedAerodromeCategory.getLdpBillingFormulas();
        if (ldpBillingFormulas != null && !ldpBillingFormulas.isEmpty()) {
            for (LdpBillingFormula ldpFormula : ldpBillingFormulas) {
                if (ldpFormula.getChargesType() != null &&
                    providedLdpBillingFormulaChargeType.equals(ldpFormula.getChargesType())) {
                    return ldpFormula;
                }
            }
        }

        return null;
    }

    /**
     * Return the aerodrome type charge
     *
     * @param ldpBillingFormula LdpBillingFormula
     * @param mtow Double
     * @param timeOfDay LocalTime
     * @param flightChargeType FlightChargeType
     * @return double
     */
    private double calculateAerodromeTypeCharge(LdpBillingFormula ldpBillingFormula, Double mtow, LocalTime timeOfDay, FlightChargeType flightChargeType) {
        double result = 0d;

        if (ldpBillingFormula != null && mtow != null && timeOfDay != null && flightChargeType != null) {
            byte[] lateDepartureChargesSpreadsheetData = ldpBillingFormula.getChargesSpreadsheet();
            AerodromeCharges aerodromeChargescharges = ssService.aerodromeCharges().load(lateDepartureChargesSpreadsheetData);
            Double mtowInUnitOfMeasure;

            // MTOW is stored as tons in database
            // If system configuration is set to KG, MTOW is converted from tons to KG
            // Charge schdules and their MTOW bands are assumed to be in KG when system configuration is set to KG
            mtowInUnitOfMeasure = reportHelper.mtowToUnitOfMeasure(mtow);


            result = aerodromeChargescharges.getCharge(mtowInUnitOfMeasure, timeOfDay, flightChargeType);
        } else {
            LOG.error("Cannot calculate aerodrome type charge due to null value: ldpBillingFormula={} , mtow={}, timeOfDay={}, flightChargeType={}",
                ldpBillingFormula, mtow, timeOfDay, flightChargeType);
        }

        return result;
    }

    /**
     * EstimatedArrivalTime = EstimatedDepartureTime + EstimatedElapsedTime
     */
    private LocalTime calculateEstimatedArrivalTime(FlightMovement providedFlightMovement) {
        LocalTime estimatedArrival = null;
        String totalEet = providedFlightMovement.getEstimatedElapsedTime();
        LocalDateTime departureLocalDateTime = calculateDepartureLocalDateTime(providedFlightMovement);

        if (departureLocalDateTime != null && totalEet != null && !totalEet.isEmpty()) {
            Integer totalMinutesFromEstimatedElapsedTime = DateTimeUtils.convertHHmmToMinutes(totalEet);
            if (totalMinutesFromEstimatedElapsedTime != null) {
                LocalDateTime estimatedArrivalLocalDateTime = departureLocalDateTime.plusMinutes(totalMinutesFromEstimatedElapsedTime);
                estimatedArrival = estimatedArrivalLocalDateTime.toLocalTime();
            }
        }

        return estimatedArrival;
    }

    /**
     * Calculate Estimated Departure LocalDateTime as dateOfFlight +
     * departureTime ActualDepartureTime from FlightMovement is not taken into
     * consideration. Only DepTime is used.
     */
    private LocalDateTime calculateDepartureLocalDateTime(FlightMovement providedFlightMovement) {
        LocalDateTime dateOfFlight = providedFlightMovement.getDateOfFlight();
        String estimatedDepartureTime = providedFlightMovement.getDepTime();

        /*
         * TODO: verify if we need to use actualDepartureTime if it exists
         * String actualDepartureTime =
         * providedFlightMovement.getActualDepartureTime();
         */

        LocalDateTime estimatedDepartureLocalDateTime = null;

        if (dateOfFlight != null && estimatedDepartureTime != null && !estimatedDepartureTime.isEmpty()) {
            Integer totalMinutesFromDepartureTime = DateTimeUtils.convertHHmmToMinutes(estimatedDepartureTime);
            if (totalMinutesFromDepartureTime != null) {
                estimatedDepartureLocalDateTime = dateOfFlight.plusMinutes(totalMinutesFromDepartureTime);
            }
        }

        return estimatedDepartureLocalDateTime;
    }

    /**
     * Calculate aerodrome and approach charges from flight movement based on arrival or departure aerodrome and time.
     *
     * @param flightMovement flight movement to calculate
     * @param applyAdap true if aerodrome and approach are applied as single charge
     * @param aerodromeAdap 'arrival' or 'departure' aerodrome used
     */
    private void doCalculateAdapCharges(final FlightMovement flightMovement, final Boolean applyAdap, final String aerodromeAdap) {
        if (flightMovement == null) return;

        // determine if thru flight by property or default to the expensive calculation
        // this is required as the thru flight property was a recent addition and may not be populated
        boolean isThruFlight = flightMovement.getThruFlight() != null ? flightMovement.getThruFlight()
            : thruFlightPlanUtility.isThruFlight(flightMovement);

        // apply separate adap charges for thru flight plans
        if (isThruFlight) {
            calculateAdapChargesThruFlight(flightMovement, aerodromeAdap.equalsIgnoreCase(ARRIVAL), applyAdap);
        } else {
            calculateAdapCharges(flightMovement, aerodromeAdap.equalsIgnoreCase(ARRIVAL), applyAdap);
        }
    }

    /**
     * Calculate aerodrome and approach charges from flight movement based on arrival or departure aerodrome and time.
     *
     * @param flightMovement flight movement to calculate
     * @param onArrival true if charges applied on arrival, else on departure
     * @param isCombined true if charges are combined under approach
     */
    private void calculateAdapCharges(final FlightMovement flightMovement, final Boolean onArrival, final Boolean isCombined) {

        AerodromeCategory aerodromeCategory = getAerodomeCategoryForAerodromeAndApproachCharges(flightMovement, onArrival);
        LocalTime arrivalTimeOfDayForCharges = deriveTimeOfDayForCharge(flightMovement, onArrival);
        FlightChargeType flightChargeType = getFlightChargeType(flightMovement);

        LOG.debug("Calculated arrival time for ADAP calculations: {}", arrivalTimeOfDayForCharges);

        // calculate aerodrome charges
        LdpBillingFormula aerodromeChargesFormula = getLdpBillingFormulaFromAerodromeCategory(aerodromeCategory,
            LdpBillingFormulaChargeType.aerodrome_charges);
        double aerodromeCharges = aerodromeChargesFormula == null ? 0d : calculateAerodromeTypeCharge(
            aerodromeChargesFormula, flightMovement.getActualMtow(), arrivalTimeOfDayForCharges, flightChargeType);

        // calculate approach charges
        LdpBillingFormula approachChargesFormula = getLdpBillingFormulaFromAerodromeCategory(aerodromeCategory,
            LdpBillingFormulaChargeType.approach_charges);
        double approachCharges = approachChargesFormula == null ? 0d : calculateAerodromeTypeCharge(
            approachChargesFormula, flightMovement.getActualMtow(), arrivalTimeOfDayForCharges, flightChargeType);

        // used within enroute charge formula and is not persisted to the database layer
        // must be done before applying any discounts and is the reason it is separated
        flightMovement.setAerodromeChargesWithoutDiscount(aerodromeCharges);
        flightMovement.setApproachChargesWithoutDiscount(approachCharges);

        // apply aerodrome service outage discount, set aerodrome and approach values for this method to work
        flightMovement.setAerodromeCharges(aerodromeCharges);
        flightMovement.setApproachCharges(approachCharges);
        applyAerodromeServiceOutagesDiscount(flightMovement, arrivalTimeOfDayForCharges);

        // reset aerodrome and approach value as single ADAP charge to approach if combined
        if (isCombined) {
            flightMovement.setApproachCharges(Double.sum(flightMovement.getAerodromeCharges(), flightMovement.getApproachCharges()));
            flightMovement.setAerodromeCharges(0d);
        }

        LOG.debug("Formulas for ADAP Arrival Charges: Approach={}, Aerodrome={}",
            approachChargesFormula, aerodromeChargesFormula);
        LOG.debug("Calculated ADAP Arrival charges: Approach={}, Aerodrome={}",
            flightMovement.getApproachCharges(), flightMovement.getAerodromeCharges());
    }

    /**
     * Find prior arrival for a specific flight movement registration number by departure time only.
     *
     * This method does not take into account actual arrival times.
     *
     * @param flightMovement flight mvoement to find prior arrivals from
     * @return prior flight movement arrival or null if none found
     */
    private FlightMovement findPriorArrivalForRegistration(final FlightMovement flightMovement) {

        String depAd = flightMovement.getDepAd();
        if (depAd != null && (depAd.equals(ApplicationConstants.PLACEHOLDER_ZZZZ) || depAd.equals(ApplicationConstants.PLACEHOLDER_AFIL))) {
            depAd = flightMovement.getItem18Dep();
        }

        return findPriorArrivalForRegistration(flightMovement, depAd);
    }

    /**
     * Find prior arrival for a specific flight movement registration number by departure time only.
     *
     * This method does not take into account actual arrival times.
     *
     * @param flightMovement flight mvoement to find prior arrivals from
     * @param depAd departure aerodrome to find prior arrivals from, used for delta calculations
     * @return prior flight movement arrival or null if none found
     */
    private FlightMovement findPriorArrivalForRegistration(final FlightMovement flightMovement, final String depAd) {
        if (flightMovement == null || flightMovement.getItem18RegNum() == null ||
            flightMovement.getDateOfFlight() == null || flightMovement.getDepTime() == null || depAd == null) {
            return null;
        }

        return flightMovementRepository.findPriorArrivalByRegNum(flightMovement.getItem18RegNum(), depAd,
            flightMovement.getDateOfFlight(), flightMovement.getDepTime());
    }

    private void applyAerodromeServiceOutagesDiscount(FlightMovement providedFlightMovement, LocalTime arrivalTimeOfDayForCharges){
        LOG.debug("Checking Aerodrome Service Outages for the flight");
        String aerodromeName = providedFlightMovement.getDestAd();
        LocalTime depTime = DateTimeUtils.convertStringToLocalTime(providedFlightMovement.getDepTime());
        LocalDate dateOfFlight = providedFlightMovement.getDateOfFlight().toLocalDate();
        LocalDateTime dateTime;

        if (arrivalTimeOfDayForCharges.isBefore(depTime)) {
            dateOfFlight = dateOfFlight.plusDays(1);
        }
        dateTime = dateOfFlight.atTime(arrivalTimeOfDayForCharges);

        List <AerodromeServiceOutage> outages = aerodromeServiceOutageService.getAerodromeServiceOutagesByAerodromeAndDateOfFlight(aerodromeName, dateTime);
        if (outages.isEmpty()) {
            LOG.debug("There are no AerodromeServiceOutages for the flight with arrival aerodrome: {} and arrival date/time: {}", aerodromeName, dateTime);
            return;
        }

        // default values for aerodrome and approach discount amounts
        double aerodromePercentageDiscount = 0;
        double aerodromeFixedDiscount = 0;
        double approachPercentageDiscount = 0;
        double approachFixedDiscount = 0;

        // loop through each discount and define flight notes and discount amounts
        StringBuilder flightNotes = new StringBuilder();
        for (AerodromeServiceOutage outage: outages) {
            if (outage.getAerodromeDiscountType().equals(DiscountType.percentage)) {
                aerodromePercentageDiscount += outage.getAerodromeDiscountAmount();
            } else {
                aerodromeFixedDiscount += outage.getAerodromeDiscountAmount();
            }

            if (outage.getApproachDiscountType().equals(DiscountType.percentage)){
                approachPercentageDiscount += outage.getApproachDiscountAmount();
            } else {
                approachFixedDiscount += outage.getApproachDiscountAmount();
            }
            flightNotes.append(outage.getFlightNotes()).append("; ");
        }

        // ensure discount is never great than 100 percent
        aerodromePercentageDiscount = aerodromePercentageDiscount < 100 ? aerodromePercentageDiscount : 100;
        approachPercentageDiscount = approachPercentageDiscount < 100 ? approachPercentageDiscount : 100;

        // apply discount to aerodrome charges
        LOG.debug("Applying AerodromeServiceOutages discount to aerodrome charges");
        providedFlightMovement.setAerodromeCharges(getDiscountAmount(providedFlightMovement.getAerodromeCharges(),
            aerodromePercentageDiscount, aerodromeFixedDiscount));

        // apply discount to approach charges
        LOG.debug("Applying AerodromeServiceOutages discount to approach charges");
        providedFlightMovement.setApproachCharges(getDiscountAmount(providedFlightMovement.getApproachCharges(),
            approachPercentageDiscount, approachFixedDiscount));

        // append discount notes
        FlightNotesUtility.mergeFlightNotes(providedFlightMovement, flightNotes.toString());
    }

    private double getDiscountAmount(final double charge, final double percentage, final double fixed) {

        // return charge less then 0 as nothing to discount
        if (charge <= 0) return charge;

        // apply percentage and fixed discount to charge amount
        double amount = charge - (percentage / 100 * charge) - fixed;

        // return discount amount or 0 if less then 0
        return amount > 0 ? amount : 0;
    }


    /**
     * Find prior arrival for provided FlightMovement. This will be used for
     * parking charges calculation.
     */
    private FlightMovement findPriorArrivalForParking(FlightMovement providedFlightMovement) {

        FlightMovement priorArrival = null;

        String depAd = providedFlightMovement.getDepAd();

        if (depAd != null && (depAd.equals(ApplicationConstants.PLACEHOLDER_ZZZZ) || depAd.equals(ApplicationConstants.PLACEHOLDER_AFIL))) {
            depAd = providedFlightMovement.getItem18Dep();
        }

        /*
         * prior arrival flight_movement record must have : - actual arrival
         * time(arrival record) - The same registration number - Movement type
         * as ARRIVAL - Destination airport of prior FlightMovement should be -
         * Time of arrival less than departure time of the
         * providedFlightMovement
         */
        if (providedFlightMovement.getDateOfFlight() != null
                && providedFlightMovement.getItem18RegNum() != null && depAd != null
                && providedFlightMovement.getDepTime() != null && !providedFlightMovement.getDepTime().isEmpty()) {

            List<FlightMovement> priorArrivals = flightMovementRepository.findPriorArrivals(
                    providedFlightMovement.getItem18RegNum(), depAd, providedFlightMovement.getDateOfFlight());

            if (priorArrivals != null) {
                LOG.debug("There are {} prior arrivals", priorArrivals.size() );
                LocalDateTime depDateTime = DateTimeUtils.addTimeToDate(providedFlightMovement.getDateOfFlight(),
                        providedFlightMovement.getDepTime());
                LOG.debug("depDateTime {}", depDateTime);
                for (FlightMovement fm : priorArrivals) {
                    if (org.apache.commons.lang.StringUtils.isNotBlank(fm.getArrivalTime())) {
                        LocalDateTime actualArrivalLocalDateTime = DateTimeUtils.addTimeToDate(fm.getDateOfFlight(),
                                fm.getArrivalTime());
                        LOG.debug("actualArrivalLocalDateTime {}", actualArrivalLocalDateTime);
                        if (actualArrivalLocalDateTime.isBefore(depDateTime)) {
                            priorArrival = fm;
                            break;
                        } else {
                            LocalDateTime actualDepLocalDateTime = DateTimeUtils.addTimeToDate(fm.getDateOfFlight(),
                                    fm.getDepTime());
                            if (actualDepLocalDateTime.isBefore(depDateTime)) {
                                LOG.debug("Estimated arrival time {} is after of departure time {}: set parking time at 0", actualArrivalLocalDateTime, depDateTime);
                                providedFlightMovement.setParkingTime(0d);
                                break;
                            }
                        }
                    } else if (org.apache.commons.lang.StringUtils.isNotBlank(fm.getEstimatedElapsedTime())) {
                        Integer minutesEstimatedElapsedTime = DateTimeUtils.getMinutes(fm.getEstimatedElapsedTime());
                        LocalTime depTime = DateTimeUtils.convertStringToLocalTime(fm.getDepTime());
                        LocalTime esimatedArrivalTime = depTime.plusMinutes(minutesEstimatedElapsedTime);
                        LOG.debug("esimatedArrivalTime {}", esimatedArrivalTime);
                        LocalDateTime actualArrivalLocalDateTime = DateTimeUtils.addTimeToDate(fm.getDateOfFlight(),
                                esimatedArrivalTime);
                        LOG.debug("actualArrivalLocalDateTime {}", actualArrivalLocalDateTime);
                        if (actualArrivalLocalDateTime.isBefore(depDateTime)) {
                            priorArrival = fm;
                            break;
                        } else {
                            LocalDateTime actualDepLocalDateTime = DateTimeUtils.addTimeToDate(fm.getDateOfFlight(),
                                    fm.getDepTime());
                            if (actualDepLocalDateTime.isBefore(depDateTime)) {
                                LOG.debug("Estimated arrival time {} is after of departure time {}: set parking time at 0", actualArrivalLocalDateTime, depDateTime);
                                providedFlightMovement.setParkingTime(0d);
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            LOG.debug("Unable to find priorArrivals");
        }

        return priorArrival;
    }

    /**
     * Determine actual arrival LocalDateTime if actual arrival time exists.
     *
     * @param providedFlightMovement flight movement
     * @return LocalDateTime
     */
    private LocalDateTime getActualArrivalLocalDateTime(FlightMovement providedFlightMovement) {

        LocalDateTime arrivalLocalDateTime = null;
        if (providedFlightMovement != null && providedFlightMovement.getDateOfFlight() != null) {
            if (StringUtils.isNotBlank(providedFlightMovement.getArrivalTime())) {
                try {
                    arrivalLocalDateTime = DateTimeUtils.addTimeToDate(providedFlightMovement.getDateOfFlight(),
                            providedFlightMovement.getArrivalTime());
                } catch (Exception ex) {
                    LOG.error(ex.getMessage());
                }
            } else if (StringUtils.isNotBlank(providedFlightMovement.getEstimatedElapsedTime())) {
                Integer minutesEstimatedElapsedTime = DateTimeUtils
                        .getMinutes(providedFlightMovement.getEstimatedElapsedTime());
                LocalTime depTime = DateTimeUtils.convertStringToLocalTime(providedFlightMovement.getDepTime());
                LocalTime esimatedArrivalTime = depTime.plusMinutes(minutesEstimatedElapsedTime.longValue());
                arrivalLocalDateTime = DateTimeUtils.addTimeToDate(providedFlightMovement.getDateOfFlight(),
                        esimatedArrivalTime);
            }
        }
        return arrivalLocalDateTime;
    }

    /**
     * Verify that no other departure records between the prior arrival and the
     * departure record of the current FlightMovement
     *
     * @param priorArrivalFlightMovement prior flight movement
     * @param currentFlightMovement current flight movement
     * @return boolean
     */
    private boolean checkIfAnyDepartureRecordsBetweenPriorArrivalAndCurrentDeparture(
            FlightMovement priorArrivalFlightMovement, FlightMovement currentFlightMovement) {

        // return false if no prior and current flight movements set
        if (priorArrivalFlightMovement == null || currentFlightMovement == null)
            return false;

        // determine departure aerodrome
        String depAd = currentFlightMovement.getDepAd();
        if (depAd != null && (depAd.equals(ApplicationConstants.PLACEHOLDER_ZZZZ) || depAd.equals(ApplicationConstants.PLACEHOLDER_AFIL)))
            depAd = currentFlightMovement.getItem18Dep();

        // confirm necessary values are not null
        if (depAd == null || currentFlightMovement.getItem18RegNum() == null
            || currentFlightMovement.getDateOfFlight() == null || priorArrivalFlightMovement.getDateOfFlight() == null
            || priorArrivalFlightMovement.getDepTime() == null || priorArrivalFlightMovement.getDepTime().isEmpty()
            || currentFlightMovement.getDepTime() == null || currentFlightMovement.getDepTime().isEmpty())
            return false;

        // convert LocalDateTime to Date  for use as parameters
        Date convertedDateOfFlightCurrentFlightMovement = JSR310DateConverters
                .convertLocalDateTimeToDate(currentFlightMovement.getDateOfFlight());
        Date convertedDateOfFlightPriorArrivalFlightMovement = JSR310DateConverters
                .convertLocalDateTimeToDate(priorArrivalFlightMovement.getDateOfFlight());

        // find any existing records between prior and current flight movement
        List<FlightMovement> exisitingFlightMovements = flightMovementRepository
                .findAnyDepartureRecordsBetweenPriorArrivalAndCurrentDeparture(
                        currentFlightMovement.getItem18RegNum(), depAd,
                        convertedDateOfFlightCurrentFlightMovement, currentFlightMovement.getDepTime(),
                        convertedDateOfFlightPriorArrivalFlightMovement, priorArrivalFlightMovement.getDepTime());

        // return result if no records found
        if (exisitingFlightMovements == null || exisitingFlightMovements.isEmpty())
            return false;

        /*
         * Find first existing departure that matches depAd.
         *
         * This is accomplished by looping through each existing flight movement found and determining if either
         * depAd or item18Dep match current flight movement depAd or item18Dep field.
         *
         * This is required as ZZZZ depAd need to parse item18Dep field in order to validate.
         */
        for (FlightMovement fm : exisitingFlightMovements) {

            // continue if no fm movement or dep ad
            if (fm == null || fm.getDepAd() == null)
                continue;

            // return true if depAd matches
            if (fm.getDepAd().equalsIgnoreCase(depAd))
                return true;

            // continue if depAd does not equal ZZZZ or AFIL, or item18Dep does not exists
            if (!(fm.getDepAd().equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_ZZZZ) || fm.getDepAd().equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_AFIL))
                || fm.getItem18Dep() == null)
                continue;

            // get item18Dep aerodrome identifier if possible
            String item18Dep = Item18Parser.getFirstAerodrome(fm.getItem18Dep());

            // return true if item18Dep matches
            if (item18Dep != null && item18Dep.equalsIgnoreCase(depAd))
                return true;
        }

        // return false to indicate no matches
        return false;
    }

    private Double getAmountFromCharge(Double chargeAmount) {
        return chargeAmount != null ? chargeAmount : 0;
    }

    /**
     * Get charge amount as 0 or greater and convert to another currency.
     *
     * @param chargeAmount charge amount
     * @param fromCurrency charge amount currency
     * @param toCurrency currency to convert into
     * @param dateTime date time for currency conversion
     * @return charge amount 0 or greater
     */
    public Double getAmountFromCharge(Double chargeAmount, Currency fromCurrency, Currency toCurrency, LocalDateTime dateTime) {
        if (chargeAmount != null && chargeAmount > 0.0 && fromCurrency != null && toCurrency != null && dateTime != null)
            return this.getAmountFromCharge(currencyUtils.convertCurrency(chargeAmount, fromCurrency, toCurrency, dateTime));
        else
            return this.getAmountFromCharge(chargeAmount);
    }

    /**
     * Before calculating or recalculation of any charges,
     * we need to set all of them to NULL
     *
     * @param providedFlightMovement FlightMovement
     */
    private void doInitializeAllAmounts(final FlightMovement providedFlightMovement) {
        providedFlightMovement.setEnrouteCharges(null);
        providedFlightMovement.setApproachCharges(null);
        providedFlightMovement.setApproachChargesWithoutDiscount(null);
        providedFlightMovement.setAerodromeCharges(null);
        providedFlightMovement.setAerodromeChargesWithoutDiscount(null);
        providedFlightMovement.setLateArrivalCharges(null);
        providedFlightMovement.setLateDepartureCharges(null);
        providedFlightMovement.setParkingCharges(null);
        providedFlightMovement.setDomesticPassengerCharges(null);
        providedFlightMovement.setInternationalPassengerCharges(null);
        providedFlightMovement.setTotalCharges(null);
        providedFlightMovement.setExtendedHoursSurcharge(null);

        // initialize parking time only if not manually entered
        if (providedFlightMovement.getManuallyChangedFields() != null && !providedFlightMovement.getManuallyChangedFields().contains("parking_time")) {
            providedFlightMovement.setParkingTime(null);
        }

        // initialize all additional charge amounts
        for (AdditionalChargeProvider additionalChargeProvider : additionalChargeProviders) {
            additionalChargeProvider.initialize(providedFlightMovement);
        }
    }


    private void doCalculateAndSetCrossingCosts(final FlightMovement providedFlightMovement) {
        Double mtowUpperLimit;
        Double billableCrossingDistance = 0d;
        Double enrouteCharges;
        Map<String, Object> map = emptyFormulaMap();

        String enrouteFormula;
        String dFactorFormula;
        String wFactorFormula;

        // without MTOW we can't find any formulas => bail out
        final Double mtow = providedFlightMovement.getActualMtow();
        if (mtow == null) {
            LOG.debug ("can't calculate crossing costs - MTOW is null");
            return;
        }

        // calculate MTOW-related values
        // if scope is unknown we can't find avergae_mtow_factor => bail out
        final FlightmovementCategoryScope scope = providedFlightMovement.getFlightCategoryScope();
        if (scope == null) {
            LOG.debug ("can't calculate crossing costs - scope is null");
            return;
        }

        // add actual MTOW value to formula map as configured unit of measure
        if (mtow > 0d) {
            // MTOW is stored as tons in database
            // if system configuration is set to KG, MTOW is converted from tons to KG
            // enroute formulas and their [MTOW] variables are assumed to be in KG when system configuration is set to KG
            map.put(CostFormulaVar.MTOW.varName(), reportHelper.mtowToUnitOfMeasure(mtow));
        }

        // find average MTOW factor
        final AverageMtowFactor amf = averageMtowFactorService.findAverageMtowFactorByUpperLimitAndFactorClass(
            mtow, getMtowFactorClass(providedFlightMovement));

        // add average MTOW factor to formula map and set mtowUpperLimit value accordingly
        if (amf != null) {
            mtowUpperLimit = averageMtowFactorService.isMtowFactorClassUsed()
                ? amf.getUpperLimit() : mtow;

            // add average MTOW factor to formula map
            map.put(CostFormulaVar.AVGMASSFACTOR.varName(), amf.getAverageMtowFactor());

        } else {
            mtowUpperLimit = mtow;
        }

        // Find the formula record
        final Integer categoryId = providedFlightMovement.getFlightmovementCategory() == null ? null : providedFlightMovement.getFlightmovementCategory().getId();
        final EnrouteAirNavigationChargeFormula formulaEntity = enrouteAirNavigationChargeFormulaService.findByMtowAndFlightCategory (mtowUpperLimit, categoryId);
        if (formulaEntity == null) {
            LOG.debug ("can't calculate crossing costs - enroute air navigation charge formula not found for mtowUpperLimit={}, flightMovementCategoryId={}",
                    mtowUpperLimit, categoryId);
            providedFlightMovement.setEnrouteFormulaNotValid(true);
            return;
        }

        // Enroute formula
        enrouteFormula = formulaEntity.getFormula() == null ? null : formulaEntity.getFormula().trim();
        if (enrouteFormula == null || enrouteFormula.isEmpty()) {
            LOG.debug ("can't calculate crossing costs - enroute forumla is null or empty");
            providedFlightMovement.setEnrouteFormulaNotValid(true);
            return;
        }

        // D-factor formula
        dFactorFormula = formulaEntity.getdFactorFormula();
        if (dFactorFormula == null || dFactorFormula.isEmpty()) {
            LOG.debug ("can't calculate crossing costs - d-factor forumla is null or empty");
            providedFlightMovement.setEnrouteFormulaNotValid(true);
            return;
        }

        // W-Factor formula
        wFactorFormula = formulaEntity.getEnrouteChargeCategory().getwFactorFormula() == null ? null : formulaEntity.getEnrouteChargeCategory().getwFactorFormula().trim();
        if (wFactorFormula == null || wFactorFormula.isEmpty()) {
            LOG.debug ("can't calculate crossing costs - w-factor forumla is null or empty");
            providedFlightMovement.setEnrouteFormulaNotValid(true);
            return;
        }

        // W-factor
        try {
            Double wfactor = this.evalDoublePositive(wFactorFormula, map);

            providedFlightMovement.setWFactor(wfactor);
            map.put(CostFormulaVar.WFACTOR.varName(), wfactor);
        } catch (Exception e) {
            LOG.error("W factor calculation failed", e);
        }

        // find Approach charges (without discounts) for the formula if needed
        if (enrouteFormula.contains(CostFormulaVar.APPROACHFEE.varName()) &&
            providedFlightMovement.getApproachChargesWithoutDiscount() != null &&
            providedFlightMovement.getApproachChargesWithoutDiscount() > 0
        ) {
            double approachCharges = providedFlightMovement.getApproachChargesWithoutDiscount();
            Currency approachChargesCurrency = currencyUtils.getApproachCurrency(
                providedFlightMovement.getFlightCategoryScope(), providedFlightMovement.getFlightCategoryNationality());
            Currency enrouteResultCurrency = providedFlightMovement.getEnrouteResultCurrency();

            if (!approachChargesCurrency.equals(enrouteResultCurrency)) {
                approachCharges = getAmountFromCharge(approachCharges, approachChargesCurrency, enrouteResultCurrency,
                    providedFlightMovement.getBillingDate() != null ? providedFlightMovement.getBillingDate() : providedFlightMovement.getDateOfFlight());
            }
            map.put(CostFormulaVar.APPROACHFEE.varName(), approachCharges);
        }

        // find Aerodrome charges (without discounts) for the formula if needed
        if (enrouteFormula.contains(CostFormulaVar.AERODROMEFEE.varName()) &&
            providedFlightMovement.getAerodromeChargesWithoutDiscount() != null &&
            providedFlightMovement.getAerodromeChargesWithoutDiscount() > 0
        ) {
            double aerodromeCharges = providedFlightMovement.getAerodromeChargesWithoutDiscount();
            Currency aerodromeChargesCurrency = currencyUtils.getAerodromeCurrency(
                providedFlightMovement.getFlightCategoryScope(), providedFlightMovement.getFlightCategoryNationality());
            Currency enrouteResultCurrency = providedFlightMovement.getEnrouteResultCurrency();

            if (!aerodromeChargesCurrency.equals(enrouteResultCurrency)) {
                aerodromeCharges = getAmountFromCharge(aerodromeCharges, aerodromeChargesCurrency, enrouteResultCurrency,
                    providedFlightMovement.getBillingDate() != null ? providedFlightMovement.getBillingDate() : providedFlightMovement.getDateOfFlight());
            }

            map.put(CostFormulaVar.AERODROMEFEE.varName(), aerodromeCharges);
        }

        // find discount if exist
        if (providedFlightMovement.getAccount() != null) {
            Account acc = providedFlightMovement.getAccount();
            double discount = acc.getDiscountStructure()== null ? 0 : acc.getDiscountStructure();

            map.put(CostFormulaVar.ACCOUNTDISCOUNT.varName(), discount);
        }

        // if delta flight with item18 DEST/ stops, use special delta flight method to calculate and set billable
        // crossing distance as delta flights can have multiple item18 DEST/ stops
        //
        // DELTA flights are billed on the first leg to or from the manned aerodrome,
        // if it is an overnight flight it is billed on the first leg on the first day and the first leg from the manned aerodrome next day
        if (providedFlightMovement.getDeltaFlight() && providedFlightMovement.getItem18Dest() != null
            && !providedFlightMovement.getItem18Dest().isEmpty()) {

            double deltaCrossDist = 0d;

            // get first segment
            RouteSegment billSeg = deltaFlightUtility.getBillableSegment(providedFlightMovement);
            if(billSeg != null && billSeg.getSegmentLength() != null) {
                deltaCrossDist = deltaCrossDist + billSeg.getSegmentLength();
            }

            // get overnight segment
            RouteSegment billOvernightSeg = deltaFlightUtility.getBillableOvernightSegment(providedFlightMovement);
            if (billOvernightSeg != null && billOvernightSeg.getSegmentLength() != null) {
                    deltaCrossDist = deltaCrossDist + billOvernightSeg.getSegmentLength();
            }

            // recalculate actual crossing distance following exempt status and reset billing crossing distance
            // this is normally defined in FlightMovementBillable.calculateBillableRouteAndDistance(...) but delta
            // only uses first legs from each day when calculating crossing distances
            billableCrossingDistance = flightMovementBillable.getActualCrossDist(providedFlightMovement, deltaCrossDist);
            providedFlightMovement.setBillableCrossingDist(deltaCrossDist);


        } else if (providedFlightMovement.getFplCrossingDistance() != null) {
            // For non-delta flights use crossing distance from the flight movement
            billableCrossingDistance = providedFlightMovement.getFplCrossingDistance();
        }

        /*
         * billable crossing distance can be ignored for the flat rate en-route charges
         */

        if ((billableCrossingDistance != null && billableCrossingDistance > 0.0) ||
                !isFlightRequireBillableRoute(providedFlightMovement, enrouteFormula) ) {
            map.put(CostFormulaVar.SCHEDCROSSDIST.varName(), billableCrossingDistance);

            // find dfactor
            try {
                Double dfactor = this.evalDoublePositive(dFactorFormula, map);
                dfactor = (double) Math.round(dfactor);
                map.put(CostFormulaVar.DFACTOR.varName(), dfactor);
            } catch (Exception e) {
                LOG.error("D factor calculation failed", e);
            }
            // calculate formula result

            try {
                enrouteCharges = this.evalDoublePositive(enrouteFormula, map);
                providedFlightMovement.setFplCrossingCost(enrouteCharges);
            } catch (Exception ex) {
                LOG.error("FPL En-route Cost calculation failed", ex);
            }
        }

        // ATC log
        if (providedFlightMovement.getAtcCrossingDistance() != null && providedFlightMovement.getAtcCrossingDistance() > 0.0) {
            map.put(CostFormulaVar.SCHEDCROSSDIST.varName(), providedFlightMovement.getAtcCrossingDistance());

            // find dfactor
            try {
                Double dfactor = this.evalDoublePositive(dFactorFormula, map);
                dfactor = (double) Math.round(dfactor);
                map.put(CostFormulaVar.DFACTOR.varName(), dfactor);
            } catch (Exception e) {
                LOG.error("D factor calculation failed", e);
            }
            // calculate formula result
            try {
                enrouteCharges = this.evalDoublePositive(enrouteFormula, map);
                providedFlightMovement.setAtcCrossingDistanceCost(enrouteCharges);
            } catch (Exception ex) {
                LOG.error("ATC Log En-route Cost calculation failed", ex);
            }
        }

        // Tower log
        if (providedFlightMovement.getTowerCrossingDistance() != null && providedFlightMovement.getTowerCrossingDistance() > 0.0) {
            map.put(CostFormulaVar.SCHEDCROSSDIST.varName(), providedFlightMovement.getTowerCrossingDistance());

            // find dfactor
            try {
                Double dfactor = this.evalDoublePositive(dFactorFormula, map);
                dfactor = (double) Math.round(dfactor);
                map.put(CostFormulaVar.DFACTOR.varName(), dfactor);
            } catch (Exception e) {
                LOG.error("D factor calculation failed", e);
            }
            // calculate formula result
            try {
                enrouteCharges = this.evalDoublePositive(enrouteFormula, map);
                providedFlightMovement.setTowerCrossingDistanceCost(enrouteCharges);
            } catch (Exception ex) {
                LOG.error("Tower Log En-route Cost calculation failed", ex);
            }
        }

        // Radar log
        if (providedFlightMovement.getRadarCrossingDistance() != null && providedFlightMovement.getRadarCrossingDistance() > 0.0) {
            map.put(CostFormulaVar.SCHEDCROSSDIST.varName(), providedFlightMovement.getRadarCrossingDistance());

            // find dfactor
            try {
                Double dfactor = this.evalDoublePositive(dFactorFormula, map);
                dfactor = (double) Math.round(dfactor);
                map.put(CostFormulaVar.DFACTOR.varName(), dfactor);
            } catch (Exception e) {
                LOG.error("D factor calculation failed", e);
            }
            // calculate formula result
            try {
                enrouteCharges = this.evalDoublePositive(enrouteFormula, map);
                providedFlightMovement.setRadarCrossingCost(enrouteCharges);
            } catch (Exception ex) {
                LOG.error("Radar Log En-route Cost calculation failed", ex);
            }
        }

        // Billable distance cost
        if (providedFlightMovement.getBillableCrossingDist() != null && providedFlightMovement.getBillableCrossingDist() > 0.0) {

            map.put(CostFormulaVar.SCHEDCROSSDIST.varName(), providedFlightMovement.getBillableCrossingDist());

            // find dfactor
            try {
                Double dfactor = this.evalDoublePositive(dFactorFormula, map);
                dfactor = (double) Math.round(dfactor);
                providedFlightMovement.setDFactor(dfactor);
                map.put(CostFormulaVar.DFACTOR.varName(), dfactor);
            } catch (Exception e) {
                LOG.error("D factor calculation failed", e);
            }

            // calculate formula result
            try {
                enrouteCharges = this.evalDoublePositive(enrouteFormula, map);
                providedFlightMovement.setBillableCrossingCost(enrouteCharges);

                // TODO decide which one we will use finally, for now fill both
                if (!providedFlightMovement.isExemptFlightDistance()) {
                    providedFlightMovement.setEnrouteCharges(enrouteCharges);
                }

            } catch (Exception ex) {
                LOG.error("Billable En-route Cost calculation failed", ex);
            }

            // Calculate cumulative flights for EANA
            //------------------------------------------
            // 2019-04-30 - new requirements from EANA
            // International flights only, if crossing distance < 200 km
            // Every flight for the same regnum or account+aircraft type, and same DOF is charged according to the crossing distance
            // If the sum of the distances for the all cumulative flights for the day is less then 200 km,
            // the difference between this sum and 200 is saved in the coulmn crossing_distance_to_minimum
            // and the cost for this difference is saved in the column enroute_cost_to minimum.
            // Otherwise both columns are 0.0
            //-------------------------------------------------------------------------------------------------------------------------
            // Old requirements for EANA - obsolete. The comment is left for reference only
            // EANA specific functionality
            // International flights only, if crossing distance < 200 km
            // First flight for the same regnum or account+aircraft type, and same DOF is charged for 200 km
            // Next is charged for 0 km and so on until 200 km is reached for all flights
            // The last is charged for the difference between its crossing distance and 200 km
            // After that each is charged for real distance
            if (systemConfigurationService.getBillingOrgCode() == BillingOrgCode.EANA
                && providedFlightMovement.getFlightCategoryScope() != null
                && providedFlightMovement.getFlightCategoryScope().equals(FlightmovementCategoryScope.INTERNATIONAL)) {

                Double crossingDistanceToMinimum = 0.0;

                if (providedFlightMovement.getAccount() != null && providedFlightMovement.getAccount().getId() != null
                        && StringUtils.isNotBlank(providedFlightMovement.getWakeTurb())) {

                    // System configuration item INVOICE_BY_DAY_OF_FLIGHT is used to determine if the flights are grouped
                    // by billing_date or by date_of_flight.
                    // if INVOICE_BY_DAY_OF_FLIGHT == true, group by date_of_flight
                    // else group by billing_date.
                    List<FlightMovement> list;
                    if (systemConfigurationService.getBoolean(SystemConfigurationItemName.INVOICE_BY_DAY_OF_FLIGHT)) {
                        list = flightMovementRepository.findAllCumulativeByAccountAndWakeTurbTypeDOF(
                            providedFlightMovement.getAccount().getId(), providedFlightMovement.getWakeTurb(),
                            providedFlightMovement.getDateOfFlight());
                    } else {
                        list = providedFlightMovement.getBillingDate() == null ? null : flightMovementRepository
                            .findAllCumulativeByAccountAndWakeTurbTypeBD(providedFlightMovement.getAccount().getId(),
                                providedFlightMovement.getWakeTurb(), providedFlightMovement.getBillingDate());
                    }

                    if (list != null && !list.isEmpty()) {

                        double result = list.stream()
                            .filter(fm -> fm.getBillableCrossingDist() != null)
                            .mapToDouble(FlightMovement::getBillableCrossingDist)
                            .sum();

                        FlightMovement last = list.get(0);
                        if(result < FlightMovementConstants.EANA_MINIMUM_INTERNATIONAL_DISTANCE &&
                                last != null && last.getId().equals(providedFlightMovement.getId())) {

                            //check if the current flight is the last flight for the day
                            crossingDistanceToMinimum = FlightMovementConstants.EANA_MINIMUM_INTERNATIONAL_DISTANCE - result;
                            providedFlightMovement.setCrossingDistanceToMinimum(crossingDistanceToMinimum);
                        } else {
                            // if the flight is not the last one the following fields should be cleared
                            // in case if the values were set when this flight was the last for the day
                            providedFlightMovement.setCrossingDistanceToMinimum(0.0);
                            providedFlightMovement.setEnrouteCostToMinimum(0.0);
                        }
                    }
                }
                if(crossingDistanceToMinimum > 0.0) {

                    // calculate formula result
                    try {
                        map.put(CostFormulaVar.SCHEDCROSSDIST.varName(), crossingDistanceToMinimum);
                        Double dfactor = this.evalDoublePositive(dFactorFormula, map);
                        dfactor = (double) Math.round(dfactor);
                        map.put(CostFormulaVar.DFACTOR.varName(), dfactor);
                        enrouteCharges = this.evalDoublePositive(enrouteFormula, map);
                        providedFlightMovement.setEnrouteCostToMinimum(enrouteCharges);
                    } catch (Exception ex) {
                    LOG.error("Cumulative flights En-route Cost calculation failed", ex);
                    }
                }
            }

        } else {
            providedFlightMovement.setEnrouteCharges(null);
        }


        // Nominal route cost
        if (providedFlightMovement.getNominalCrossingDistance() != null && providedFlightMovement.getNominalCrossingDistance() > 0.0) {
            map.put(CostFormulaVar.SCHEDCROSSDIST.varName(), providedFlightMovement.getNominalCrossingDistance());

            // find dfactor
            try {
                Double dfactor = this.evalDoublePositive(dFactorFormula, map);
                dfactor = (double) Math.round(dfactor);
                map.put(CostFormulaVar.DFACTOR.varName(), dfactor);
            } catch (Exception e) {
                LOG.error("D factor calculation failed", e);
            }

            // calculate formula result
            try {
                enrouteCharges = this.evalDoublePositive(enrouteFormula, map);
                providedFlightMovement.setNominalCrossingCost(enrouteCharges);
            } catch (Exception ex) {
                LOG.error("Nominal route En-route Cost calculation failed", ex);
            }
        }

        // User distance cost
        if (providedFlightMovement.getUserCrossingDistance() != null && providedFlightMovement.getUserCrossingDistance() > 0.0) {
            map.put(CostFormulaVar.SCHEDCROSSDIST.varName(), providedFlightMovement.getUserCrossingDistance());

            // find dfactor
            try {
                Double dfactor = this.evalDoublePositive(dFactorFormula, map);
                dfactor = (double) Math.round(dfactor);
                map.put(CostFormulaVar.DFACTOR.varName(), dfactor);
            } catch (Exception e) {
                LOG.error("D factor calculation failed", e);
            }

            // calculate formula result
            try {
                enrouteCharges = this.evalDoublePositive(enrouteFormula, map);
                providedFlightMovement.setUserCrossingDistanceCost(enrouteCharges);
            } catch (Exception ex) {
                LOG.error("Nominal route En-route Cost calculation failed", ex);
            }
        }
    }

    /**
     * Calculate charges based on arrival aerodrome
     *
     * @param providedFlightMovement flight movement
     */
    private void doArrivalRelatedCharges(FlightMovement providedFlightMovement, Boolean applyAdap, String aerodromeAdap,
                                         String arrivalAerodrome, AerodromeCategory aerodromeCategory) {
        LOG.debug("Calculating Arrival Related Charges. Delta={}", providedFlightMovement.getDeltaFlight());

        // apply ADAP charges on arrival when aerodrome adap setting is "arrival"
        Boolean adapOnArrival = aerodromeAdap.equalsIgnoreCase(ARRIVAL);

        // if delta flight with item18 DEST/ stops, use special delta flight method to calculate and set arrival
        // related charges as delta flights can have multiple item18 DEST/ stops
        if (providedFlightMovement.getDeltaFlight() && providedFlightMovement.getItem18Dest() != null
            && !providedFlightMovement.getItem18Dest().isEmpty()) {
            this.calculateAndSetAllArrivalRelatedChargesDelta(providedFlightMovement, applyAdap, adapOnArrival);
        } else {
            this.calculateAndSetAllArrivalRelatedCharges(providedFlightMovement, adapOnArrival, arrivalAerodrome, aerodromeCategory);
        }
    }

    private void doDepartureRelatedCharges(FlightMovement providedFlightMovement, AerodromeCategory aerodromeCategory, LocalTime departureTimeForCharges) {
        if (!testMovementCategoryType(providedFlightMovement) || !testMtowNull(providedFlightMovement) || !testFlightChargeType(providedFlightMovement)) {
            return;
        }

        Double actualMtow = providedFlightMovement.getActualMtow();
        testAerodromeCategory(aerodromeCategory);
        FlightChargeType flightChargeType = getFlightChargeType(providedFlightMovement);

        // calculate and set late departure charges
        this.calculateAndSetLateDepartureCharges(providedFlightMovement, aerodromeCategory, actualMtow, flightChargeType, departureTimeForCharges);

        if (systemConfigurationService.shouldCalculateParkingCharges()) {
            // calculate and set parking time and charges
            this.calculateAndSetParkingTimeAndCharges(providedFlightMovement, aerodromeCategory, actualMtow, flightChargeType);
        }

        // calculate and set passenger charges
        this.calculateAndSetPassengerCharges(providedFlightMovement, aerodromeCategory);
    }

    /**
     * Calculates adn sets all arrival related charges for DELTA flights: approach, aerodrome, late arrival.
     *
     * Assumes delta flights are domestic, at least one stop, and do not span multiple calender days.
     */
    private void calculateAndSetAllArrivalRelatedChargesDelta(
        final FlightMovement providedFlightMovement, final Boolean isAdapCombined, final Boolean adapOnArrival) {

        // MTOW is required for calculation
        if (!testMtowNull(providedFlightMovement) && !testMtowUpperLimit(providedFlightMovement)) {
            LOG.error("Could not calculate DELTA arrival charges: MTOW missing");
            return;
        }

        // Initialise charges
        double totalAerodromeCharges = 0d;
        double totalApproachCharges = 0d;
        double totalLateArrivalCharges = 0d;

        // Parse item18 dest field for DELTA stop list
        List<DeltaFlightVO> deltaList = Item18Parser.destFieldToMap(providedFlightMovement.getItem18Dest());
        LinkedList<DeltaFlightVO> deltaStopList = deltaList != null && !deltaList.isEmpty()
            ? new LinkedList<>(deltaList)
            : null;

        if (deltaStopList != null && !deltaStopList.isEmpty()) {

            // used to hold each applied delta stop's charge discount
            final LinkedListMultimap<String, Integer> arrivalChargeDiscounts = LinkedListMultimap.create();

            // loop through each delta stop and calculate late arrival, approach, and aerodrome charges
            // if formulas exist for delta stop's associated aerodrome category
            for (int i = 0; i < deltaStopList.size(); i++) {

                // define reference to current iterating stop
                DeltaFlightVO stop = deltaStopList.get(i);

                // define initial previous stop from prior arrival
                // previous stop used when ADAP applied on departure
                DeltaFlightVO prevStop = i == 0 ? priorArrivalAsDeltaStop(providedFlightMovement)
                    : deltaStopList.get(i - 1);

                // identify is used to find aerodrome category for charges
                // time of day is used within charge tables to determine fixed charge along with mtow factor
                String identForCharge = adapOnArrival ? stop.getIdent() : prevStop.getIdent();
                String timeForCharge = adapOnArrival ? stop.getArrivaAt() : prevStop.getArrivaAt();
                Double mtowForCharge = providedFlightMovement.getActualMtow();

                // calculate late arrival
                totalLateArrivalCharges += calculateArrivalRelatedChargeDelta(identForCharge, timeForCharge, mtowForCharge,
                    LdpBillingFormulaChargeType.late_arrival_charges);

                // calculate approach charges
                totalApproachCharges += calculateArrivalRelatedChargeDelta(identForCharge, timeForCharge, mtowForCharge,
                    LdpBillingFormulaChargeType.approach_charges);

                // calculate aerodrome charge
                double aerodromeCharge = calculateArrivalRelatedChargeDelta(identForCharge, timeForCharge, mtowForCharge,
                    LdpBillingFormulaChargeType.aerodrome_charges);

                // calculate arrival charge discount for delta stop
                double aerodromeDiscount = discountUtility.getArrivalChargeDiscount(providedFlightMovement, stop.getIdent(),
                    arrivalChargeDiscounts);

                // calculate aerodrome charges with applied arrival discount if adap applied on arrival
                // else if no previous stops on delta route, find discount by prior arrival
                // else, find discount by prior delta stop
                if (adapOnArrival) {
                    totalAerodromeCharges += aerodromeCharge * aerodromeDiscount;
                } else if (arrivalChargeDiscounts.get(prevStop.getIdent()).isEmpty()) {
                    totalAerodromeCharges += aerodromeCharge * discountUtility.findArrivalChargeDiscount(
                        prevStop.getIdent(), findPriorArrivalForRegistration(providedFlightMovement, prevStop.getIdent()));
                } else {
                    totalAerodromeCharges += aerodromeCharge * discountUtility.findPreviousStopDiscount(
                        arrivalChargeDiscounts, prevStop.getIdent());
                }

                // add stop identity to arrival charge discount multimap
                // must be down after discount if finding discount by prior delta stop
                // this will be used in future departures if applying adap on departure
                arrivalChargeDiscounts.put(stop.getIdent(), (int) (aerodromeDiscount * 100));
            }

            // set arrival charge discounts for all arrival aerodrome identifiers
            providedFlightMovement.setArrivalChargeDiscounts(arrivalChargeDiscounts);
        }

        // override provided flight movement aerodrome and approach charges using delta logic
        providedFlightMovement.setLateArrivalCharges(totalLateArrivalCharges);
        if (isAdapCombined) {
            providedFlightMovement.setAerodromeCharges(0d);
            providedFlightMovement.setApproachCharges(totalAerodromeCharges + totalApproachCharges);
        } else {
            providedFlightMovement.setAerodromeCharges(totalAerodromeCharges);
            providedFlightMovement.setApproachCharges(totalApproachCharges);
        }
    }

    /**
     * Used to calculate an arrival related charge for delta stops.
     */
    private double calculateArrivalRelatedChargeDelta(
        final String ident, final String time, final Double mtow, final LdpBillingFormulaChargeType chargeType
    ) {

        // in order to calculate arrival related charges, we need to make sure the aerodrome
        // has aerodrome category associated  with it
        AerodromeCategory aerodromeCategory = flightMovementAerodromeService.resolveLocationToAdCategory(
            ident, null, AdResolveType.AD_TYPE_DELTA_DESTINATION);

        // must have a aerodrome category in order to calculate any charges
        if (aerodromeCategory == null) {
            LOG.error("AerodromeCategory for provided departure aerodrome {} is unknown", ident);
            return 0d;
        }

        // use aerodrome category to find charge formula
        LdpBillingFormula formula = getLdpBillingFormulaFromAerodromeCategory(aerodromeCategory, chargeType);

        // time of day is used within charge tables to determine fixed charge along with mtow factor
        LocalTime timeOfdayForCharge = DateTimeUtils.convertStringToLocalTime(time);

        // calculate arrival charge and return
        return formula == null ? 0d : calculateAerodromeTypeCharge(
            formula, mtow, timeOfdayForCharge, FlightChargeType.DOMESTIC);
    }

    private AerodromeCategory getAerodomeCategoryForAerodromeAndApproachCharges(FlightMovement flightMovement, Boolean onArrival) {
        String adIdentifier = onArrival ? flightMovement.getDestAd() : flightMovement.getDepAd();
        String item18Ad = onArrival ? flightMovement.getItem18Dest() : flightMovement.getItem18Dep();
        AdResolveType locationType = onArrival ? AdResolveType.AD_TYPE_DESTINATION : AdResolveType.AD_TYPE_DEPARTURE;

        AerodromeCategory aerodromeCategory = this.flightMovementAerodromeService
            .resolveLocationToAdCategory(adIdentifier, item18Ad, locationType);

        if (testAerodromeCategory(aerodromeCategory)) {
            return aerodromeCategory;
        } else {
            return null;
        }
    }

    private void calculateAndSetAllArrivalRelatedCharges(FlightMovement providedFlightMovement, Boolean adapOnArrival,
                                                         String adIdentifier, AerodromeCategory aerodromeCategory) {

        // validate that the provided flight movement can have arrival related charges applied
        if (!testMovementCategoryType(providedFlightMovement) || !testFlightChargeType(providedFlightMovement) || !testMtowNull(providedFlightMovement))
            return;

        FlightChargeType flightChargeType = getFlightChargeType(providedFlightMovement);

        // calculate late arrival charges only if aerodrome category found
        if (testAerodromeCategory(aerodromeCategory)) {
            LOG.debug("Calculating Normal Flight Arrival Charges: Identifier={}, AerodromeCategory={}", adIdentifier, aerodromeCategory);

            LocalTime arrivalTimeOfDayForCharges = deriveTimeOfDayForCharge(providedFlightMovement, adapOnArrival);

            // calculate late arrival
            LdpBillingFormula lateArrivalChargesFormula = getLdpBillingFormulaFromAerodromeCategory(aerodromeCategory,
                LdpBillingFormulaChargeType.late_arrival_charges);
            if (lateArrivalChargesFormula != null) {
                providedFlightMovement.setLateArrivalCharges(calculateAerodromeTypeCharge(lateArrivalChargesFormula,
                    providedFlightMovement.getActualMtow(), arrivalTimeOfDayForCharges, flightChargeType));
            }
        }

        // determine arrival discount rate if identifier exist and it is the real aerodrome with 4-letter ICAO code, else clear
        // if applying on departure, discount will be saved for later use when this flight departs again
        Double discount;
        if (StringUtils.isNotBlank(adIdentifier) && adIdentifier.length() == 4 ) {
            discount = discountUtility.getArrivalChargeDiscount(providedFlightMovement, adIdentifier);
            providedFlightMovement.setArrivalChargeDiscounts(ImmutableListMultimap.of(adIdentifier, (int)(discount * 100)));
        } else {
            discount = 1.0;
            providedFlightMovement.setArrivalChargeDiscounts(null);
        }

        // apply arrival charge discount if ADAP charged on arrival else from prior arrival
        if (adapOnArrival) {
            discountUtility.applyArrivalChargeDiscount(providedFlightMovement, discount);
        } else {
            discountUtility.applyArrivalChargeDiscount(providedFlightMovement,
                findPriorArrivalForRegistration(providedFlightMovement));
        }
    }

    /**
     * Calculate and set provided flight movement late departure charges.
     *
     * @param flightMovement flight movement
     * @param aerodromeCategory aerodrome category
     * @param actualMtow actual mtow
     * @param flightChargeType flight charge type
     */
    private void calculateAndSetLateDepartureCharges(FlightMovement flightMovement, AerodromeCategory aerodromeCategory,
                                                     Double actualMtow, FlightChargeType flightChargeType, LocalTime departureTimeOfDayForCharges) {

        if (flightMovement == null || aerodromeCategory == null || actualMtow == null || flightChargeType == null) {
            return;
        }

        // calculate late departure
        LdpBillingFormula lateDepartureChargesFormula = getLdpBillingFormulaFromAerodromeCategory(aerodromeCategory,
            LdpBillingFormulaChargeType.late_departure_charges);
        if (lateDepartureChargesFormula != null) {
            flightMovement.setLateDepartureCharges(calculateAerodromeTypeCharge(lateDepartureChargesFormula,
                actualMtow, departureTimeOfDayForCharges, flightChargeType));
        }
    }

    private void doCalculateAndSetExtendedHoursSurcharge(FlightMovement flightMovement,
                                                         String arrivalAerodromeIdentifier,
                                                         AerodromeCategory arrivalAerodromeCategory,
                                                         AerodromeCategory departureAerodromeCategory,
                                                         LocalTime departureTimeOfDayForCharges) {

        // validate that the provided flight movement can have related charges applied
        if (flightMovement == null || !testMtowNull(flightMovement) || !testFlightChargeType(flightMovement) ||
            Boolean.FALSE.equals(systemConfigurationService.getBoolean(SystemConfigurationItemName.EXTENDED_HOURS_SURCHARGE_SUPPORT))) {
            return;
        }

        FlightChargeType flightChargeType = getFlightChargeType(flightMovement);
        String depAerodrome = flightMovement.getDepAd();

        double extendedHoursSurchargeForDeparture = 0d;
        double extendedHoursSurchargeForArrival = 0d;

        // calculate extended hours surcharge for departure aerodrome only if aerodrome category found
        if (testAerodromeCategory(departureAerodromeCategory)) {
            LOG.debug("Calculating Extended Hours Surcharge for Departure Aerodrome: {}, AerodromeCategory: {}", depAerodrome, departureAerodromeCategory);

            extendedHoursSurchargeForDeparture = calculateExtendedHoursSurcharge(flightMovement, depAerodrome, departureAerodromeCategory,
                departureTimeOfDayForCharges, flightChargeType, DEPARTURE);
        }

        // calculate extended hours surcharge for arrival aerodrome only if aerodrome category found
        if (testAerodromeCategory(arrivalAerodromeCategory)) {
            LOG.debug("Calculating Extended Hours Surcharge for Arrival Aerodrome: {}, AerodromeCategory: {}", arrivalAerodromeIdentifier, arrivalAerodromeCategory);
            LocalTime arrivalTimeOfDayForCharges = deriveTimeOfDayForCharge(flightMovement, true);

            extendedHoursSurchargeForArrival = calculateExtendedHoursSurcharge(flightMovement, arrivalAerodromeIdentifier, arrivalAerodromeCategory,
                arrivalTimeOfDayForCharges, flightChargeType, ARRIVAL);
        }

        flightMovement.setExtendedHoursSurcharge(extendedHoursSurchargeForArrival + extendedHoursSurchargeForDeparture);
    }

    private double calculateExtendedHoursSurcharge(FlightMovement flightMovement,
                                                   String aerodrome,
                                                   AerodromeCategory aerodromeCategory,
                                                   LocalTime flightTimeOfDayForCharges,
                                                   FlightChargeType flightChargeType,
                                                   String aerodromeType) {
        LdpBillingFormula extendedHoursServiceChargesFormula = getLdpBillingFormulaFromAerodromeCategory(aerodromeCategory, LdpBillingFormulaChargeType.extended_hours_service_charge);
        if (extendedHoursServiceChargesFormula != null) {
            double base = calculateAerodromeTypeCharge(extendedHoursServiceChargesFormula, flightMovement.getActualMtow(),
                flightTimeOfDayForCharges, flightChargeType);
            int extendedHours = aerodromeOperationalHoursService.getExtendedAerodromeOperationalHours(flightMovement, aerodrome, aerodromeType, flightTimeOfDayForCharges);
            if (extendedHours > 0) {
                String extendedHoursSurchargeBasis = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.EXTENDED_HOURS_SURCHARGE_BASIS);
                return extendedHoursSurchargeBasis.equals("fixed") ? base : base * extendedHours;
            }
        }
        return 0;
    }

    /**
     * Calculate and set provided flight movement parking time and charges.
     *
     * Parking charges for a flight are calculated between its prior arrival
     * and its departure.
     *
     * However if the user entered parking time manually - this value is
     * used to calculate parking charges.
     *
     * @param flightMovement flight movement
     * @param aerodromeCategory aerodrome category
     * @param actualMtow actual mtow
     * @param flightChargeType fligth charge type
     */
    private void calculateAndSetParkingTimeAndCharges(FlightMovement flightMovement,
            AerodromeCategory aerodromeCategory, Double actualMtow, FlightChargeType flightChargeType) {

        if (flightMovement == null || actualMtow == null || flightChargeType == null) {
            return;
        }

        boolean doParkingCharge = true;
        SystemConfiguration systemConfiguration = systemConfigurationService
                .getOneByItemName(SystemConfigurationItemName.PARKING_TIME_REQUIRED);
        if (aerodromeCategory == null
                || (systemConfiguration == null || systemConfiguration.getCurrentValue() == null || !systemConfiguration
                        .getCurrentValue().equalsIgnoreCase(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE))) {
            doParkingCharge = false;
            flightMovement.setParkingCharges(0.0);
        }

        LdpBillingFormula parkingChargesFormula = getLdpBillingFormulaFromAerodromeCategory(aerodromeCategory,
                LdpBillingFormulaChargeType.parking_charges);
        ParkingCharges parkingChargesSss = null;
        if (parkingChargesFormula != null) {
            byte[] parkingSpreadsheetData = parkingChargesFormula.getChargesSpreadsheet();
            parkingChargesSss = ssService.parkingCharges().load(parkingSpreadsheetData);
        }
        LocalDateTime departureLocalDateTime = calculateDepartureLocalDateTime(flightMovement);
        LocalDateTime priorArrivalLocalDateTime = null; // arrivalTimeUtc

        // calculate parking time if it wasn't manually changed OR manually
        // changed value is null
        if (flightMovement.getParkingTime() == null || flightMovement.getManuallyChangedFields() == null
                || !flightMovement.getManuallyChangedFields().contains("parking_time")) {

            // calculate parking time
            Double parkingTimeHours = null;

            FlightMovement priorArrival = null;
            if (departureLocalDateTime != null) {

                // calculate correct arrivalTimeUtc
                priorArrival = findPriorArrivalForParking(flightMovement);
                priorArrivalLocalDateTime = getActualArrivalLocalDateTime(priorArrival);
                LOG.debug("priorArrivalLocalDateTime {}", priorArrivalLocalDateTime);

                if (priorArrivalLocalDateTime != null  && departureLocalDateTime.isAfter(priorArrivalLocalDateTime)) {
                    Long parkingTimeMinutes = ChronoUnit.MINUTES.between(priorArrivalLocalDateTime,
                            departureLocalDateTime);
                    parkingTimeHours = parkingTimeMinutes.doubleValue() / 60.00; // convert
                                                                                 // minutes
                                                                                 // to
                                                                                 // hours
                    LOG.debug("parkingTimeHours {}", parkingTimeHours);
                }
            }

            // verify that no other departure records between this arrival
            // record and the departure record of providedFlightMovement
            boolean checkIfOtherDepartureRecordExists = checkIfAnyDepartureRecordsBetweenPriorArrivalAndCurrentDeparture(
                    priorArrival, flightMovement);

            if (!checkIfOtherDepartureRecordExists) {
                // set parking time
                if (parkingTimeHours != null) {
                    flightMovement.setParkingTime(parkingTimeHours);
                }
            } else {
                LOG.debug("There are other departure records: no parking charge to calculate");
                doParkingCharge = false;
            }
        } else if (departureLocalDateTime != null) {
            LOG.debug("Parking time has been typed manually");
            priorArrivalLocalDateTime = departureLocalDateTime.minusMinutes((long) (flightMovement.getParkingTime() * 60));
        }

        // calculate parking charge if true
        if (doParkingCharge && parkingChargesSss != null) {

            // apply possible exemptions from account or system configuration
            // default
            LocalDateTime startParkingExempt = null;
            if (priorArrivalLocalDateTime != null) {
                if (flightMovement.getAccount() != null
                        && flightMovement.getAccount().getAircraftParkingExemption() != null) {
                    startParkingExempt = priorArrivalLocalDateTime
                            .minusHours(flightMovement.getAccount().getAircraftParkingExemption());
                } else if (systemConfigurationService.getCurrentValue("Default account parking exemption") != null) {
                    final double parkingExemption = Double.parseDouble(systemConfigurationService
                        .getCurrentValue("Default account parking exemption"));
                    startParkingExempt = priorArrivalLocalDateTime
                        .minusHours((int) parkingExemption)
                        .minusMinutes((int) (parkingExemption * 60) % 60)
                        .minusSeconds((int) (parkingExemption * (60*60)) % 60);

                } else {
                    startParkingExempt = priorArrivalLocalDateTime;
                }
            }
            LOG.debug("startParkingExempt: {}", startParkingExempt);

            // MTOW is stored as tons in database
            // If system configuration is set to KG, MTOW is converted from tons to KG
            // Charge schdules and their MTOW bands are assumed to be in KG when system configuration is set to KG
            Double mtowInUnitOfMeasure = reportHelper.mtowToUnitOfMeasure(actualMtow);

            // calculate parking charge amount minus any parking exemptions
            if (startParkingExempt != null  && !departureLocalDateTime.isBefore(startParkingExempt)) {
                flightMovement.setParkingCharges(parkingChargesSss.getCharge(mtowInUnitOfMeasure, startParkingExempt,
                        departureLocalDateTime, flightChargeType));
            }
        }
    }

    /**
     * Calculate and set provided flight movement domestic and international passenger charges.
     *
     * @param flightMovement flight movement
     * @param aerodromeCategory aerodrome category
     */
    private void calculateAndSetPassengerCharges(FlightMovement flightMovement,
            AerodromeCategory aerodromeCategory) {

        if (aerodromeCategory == null || flightMovement == null) {
            return;
        }

        if (flightMovement.getAccount() == null || flightMovement.getAccount().getId() == null) {
            LOG.warn("Cannot calculate passenger charges as the account is unknown");
            return;
        }

        final Double domesticPassengerFeeAdult = aerodromeCategory.getDomesticPassengerFeeAdult();
        final Double internationalPassengerFeeAdult = aerodromeCategory.getInternationalPassengerFeeAdult();

        final Integer passengersChargeableDomesticNumber = flightMovement.getPassengersChargeableDomestic();
        final Integer passengersChargeableInternationalNumber = flightMovement.getPassengersChargeableIntern();

        Double domesticPassengerFeePercentage = 100.00; // 100% by default
        Double internationalPassengerFeePercentage = 100.00; // 100% by default

        if (systemConfigurationService
                .getOneByItemName(SystemConfigurationItemName.DOMESTIC_PASSENGER_FEE_PERCENTAGE) != null) {
            domesticPassengerFeePercentage = Double.valueOf(systemConfigurationService
                    .getOneByItemName(SystemConfigurationItemName.DOMESTIC_PASSENGER_FEE_PERCENTAGE).getCurrentValue());
        }

        if (systemConfigurationService
                .getOneByItemName(SystemConfigurationItemName.INTERNATIONAL_PASSENGER_FEE_PERCENTAGE) != null) {
            internationalPassengerFeePercentage = Double.valueOf(systemConfigurationService
                    .getOneByItemName(SystemConfigurationItemName.INTERNATIONAL_PASSENGER_FEE_PERCENTAGE)
                    .getCurrentValue());
        }

        /*
         * Passenger charges calculated on departure AS:
         *
         * DomesticPassengerCharges =
         * (domesticPassengerFeeAdult*PassengersChargeableDomestic) * (Domestic
         * passenger_fee_percentage * 0.01)
         *
         * InternationalPassengerCharges =
         * (internationalPassengerFeeAdult*PassengersChargeableIntern) *
         * (International passenger_fee_percentage * 0.01)
         */

        if (domesticPassengerFeeAdult != null && passengersChargeableDomesticNumber != null) {
            flightMovement.setDomesticPassengerCharges(domesticPassengerFeeAdult * passengersChargeableDomesticNumber
                    * (domesticPassengerFeePercentage * 0.01));
        }

        if (internationalPassengerFeeAdult != null && passengersChargeableInternationalNumber != null) {
            flightMovement.setInternationalPassengerCharges(internationalPassengerFeeAdult
                    * passengersChargeableInternationalNumber * (internationalPassengerFeePercentage * 0.01));
        }
    }

    /**
     * Calculate total charges amount.
     *
     * @param providedFlightMovement flight movement
     */
    private void calculateAndSetTotalChargesAmount(FlightMovement providedFlightMovement) {
        if (providedFlightMovement == null) {
            return;
        }

        // used for necessary currency conversion
        final Currency anspCurrency = currencyUtils.getAnspCurrency();
        final Currency usdCurrency = currencyUtils.getCurrencyUSD();
        final Currency domesticPassengerChargesCurrency = currencyUtils.getSystemConfigurationCurrency(SystemConfigurationItemName.DOMESTIC_PAX_FEE_CURRENCY);
        final Currency internationalPassengerChargesCurrency = currencyUtils.getSystemConfigurationCurrency(SystemConfigurationItemName.INTERNATIONAL_PAX_FEE_CURRENCY);

        Currency approachChargesCurrency = currencyUtils.getApproachCurrency(
            providedFlightMovement.getFlightCategoryScope(), providedFlightMovement.getFlightCategoryNationality());
        providedFlightMovement.setApproachChargesCurrency(approachChargesCurrency);

        Currency aerodromeChargesCurrency = currencyUtils.getAerodromeCurrency(
            providedFlightMovement.getFlightCategoryScope(), providedFlightMovement.getFlightCategoryNationality());
        providedFlightMovement.setAerodromeChargesCurrency(aerodromeChargesCurrency);

        Currency lateArrivalDepartureChargesCurrency = currencyUtils.getLateArrivalDepartureCurrency(
            providedFlightMovement.getFlightCategoryScope(), providedFlightMovement.getFlightCategoryNationality());
        providedFlightMovement.setLateArrivalDepartureChargesCurrency(lateArrivalDepartureChargesCurrency);

        Currency extendedHoursSurchargeCurrency = currencyUtils.getExtendedHoursSurchargeCurrency(
            providedFlightMovement.getFlightCategoryScope(), providedFlightMovement.getFlightCategoryNationality());
        providedFlightMovement.setExtendedHoursSurchargeCurrency(extendedHoursSurchargeCurrency);

        // default USD
        if (providedFlightMovement.getTaspChargeCurrency() == null) {
            providedFlightMovement.setTaspChargeCurrency(usdCurrency);
        }

        final LocalDateTime now = LocalDateTime.now();

        // Round charges according to currency decimals
        roundChargesToCurrencyDecimals(providedFlightMovement);

        /*
         * For all flights the total charge should be the sum of the following:
         *
         * Enroute charges - ANSP || USD
         * Approach charges - ANSP || USD
         * Aerodrome charges - ANSP || USD
         * Late charges - ANSP || USD
         * Parking charges - ANSP
         * Domestic passenger charges - ANSP || USD
         * International passenger charges -- ANSP || USD
         * TASP charges fro arival - ANSP || USD
         *
         * Total charges are in ANSP currency
         */
        Double totalAmount = getAmountFromCharge(providedFlightMovement.getEnrouteCharges(), usdCurrency, anspCurrency, now)
            + getAmountFromCharge(providedFlightMovement.getApproachCharges(), approachChargesCurrency, anspCurrency, now)
            + getAmountFromCharge(providedFlightMovement.getAerodromeCharges(), aerodromeChargesCurrency, anspCurrency, now)
            + getAmountFromCharge(providedFlightMovement.getLateArrivalCharges(), lateArrivalDepartureChargesCurrency, anspCurrency, now)
            + getAmountFromCharge(providedFlightMovement.getLateDepartureCharges(), lateArrivalDepartureChargesCurrency, anspCurrency, now)
            + getAmountFromCharge(providedFlightMovement.getParkingCharges())
            + getAmountFromCharge(providedFlightMovement.getExtendedHoursSurcharge(), extendedHoursSurchargeCurrency, anspCurrency, now);

        if (systemConfigurationService.getBoolean(SystemConfigurationItemName.INCLUDE_PASSENGER_FEES_ON_INVOICE)) {
            totalAmount += getAmountFromCharge(providedFlightMovement.getDomesticPassengerCharges(), domesticPassengerChargesCurrency, anspCurrency, now)
                + getAmountFromCharge(providedFlightMovement.getInternationalPassengerCharges(), internationalPassengerChargesCurrency, anspCurrency, now);
        }

        // TASP charges are added to the total amount for DOMESTIC, Regional
        // Arrival and International arrival
        // TASP charges are in ANSP || USD (default USD)
        if ((providedFlightMovement.getFlightCategoryScope() == FlightmovementCategoryScope.DOMESTIC
                || providedFlightMovement.getFlightCategoryType() == FlightmovementCategoryType.ARRIVAL) &&
             providedFlightMovement.getTaspCharge() != null) {
                totalAmount += getAmountFromCharge(providedFlightMovement.getTaspCharge(), providedFlightMovement.getTaspChargeCurrency(), anspCurrency, now);
        }

        // set total charges
        providedFlightMovement.setTotalCharges(totalAmount);
    }

    /**
     * Calculate total charges amount in USD.
     *
     * @param flightMovement calculated flight movement
     */
    private void calculateAndSetTotalChargesAmountInUSD(final FlightMovement flightMovement) {

        if (flightMovement == null) {
            return;
        }

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

        // convert charged amounts to USD
        double totalChargesAmountUsd = getAmountFromCharge(enrouteCharges, enrouteChargesCurrency, usdCurrency, dateTimeCurrencyConversion)
            + getAmountFromCharge(aerodromeCharges, aerodromeChargesCurrency, usdCurrency, dateTimeCurrencyConversion)
            + getAmountFromCharge(approachCharges, approachChargesCurrency, usdCurrency, dateTimeCurrencyConversion)
            + getAmountFromCharge(taspCharges, taspChargeCurrency, usdCurrency, dateTimeCurrencyConversion)
            + getAmountFromCharge(lateArrivalCharges, lateArrivalDepartureChargesCurrency, usdCurrency, dateTimeCurrencyConversion)
            + getAmountFromCharge(lateDepartureCharges, lateArrivalDepartureChargesCurrency, usdCurrency, dateTimeCurrencyConversion)
            + getAmountFromCharge(extendedHoursSurcharges, extendedHoursSurchargeCurrency, usdCurrency, dateTimeCurrencyConversion)
            + getAmountFromCharge(parkingCharges, anspCurrency, usdCurrency, dateTimeCurrencyConversion);

        if (systemConfigurationService.getBoolean(SystemConfigurationItemName.INCLUDE_PASSENGER_FEES_ON_INVOICE)) {
            totalChargesAmountUsd += getAmountFromCharge(domesticPassengerCharges, domesticPassengerChargesCurrency, usdCurrency, dateTimeCurrencyConversion)
                + getAmountFromCharge(internationalPassengerCharges, internationalPassengerChargesCurrency, usdCurrency, dateTimeCurrencyConversion);
        }

        // set total charges in USD
        flightMovement.setTotalChargesUsd(totalChargesAmountUsd);
    }

    private void roundChargesToCurrencyDecimals(FlightMovement fm) {
        // extract rounded values
        Double roundedApprochCharges = Calculation.roundByCurrencyDecimal(fm.getApproachCharges(), fm.getApproachChargesCurrency());
        Double roundedAerodromeCharges = Calculation.roundByCurrencyDecimal(fm.getAerodromeCharges(), fm.getAerodromeChargesCurrency());
        Double roundedLateArrivalCharges = Calculation.roundByCurrencyDecimal(fm.getLateArrivalCharges(), fm.getLateArrivalDepartureChargesCurrency());
        Double roundedLateDepartureCharges = Calculation.roundByCurrencyDecimal(fm.getLateDepartureCharges(), fm.getLateArrivalDepartureChargesCurrency());
        Double roundedExtendedHoursSurcharge = Calculation.roundByCurrencyDecimal(fm.getExtendedHoursSurcharge(), fm.getExtendedHoursSurchargeCurrency());
        Double roundedTaspCharges = Calculation.roundByCurrencyDecimal(fm.getTaspCharge(), fm.getTaspChargeCurrency());
        Double roundedEnrouteCharges = Calculation.roundByCurrencyDecimal(fm.getEnrouteCharges(), fm.getEnrouteInvoiceCurrency());
        Double roundedBillableCrossingCost = Calculation.roundByCurrencyDecimal(fm.getBillableCrossingCost(), fm.getEnrouteInvoiceCurrency());
        Double roundedNominalCrossingCost = Calculation.roundByCurrencyDecimal(fm.getNominalCrossingCost(), fm.getEnrouteInvoiceCurrency());
        Double roundedFplCrossingCost = Calculation.roundByCurrencyDecimal(fm.getFplCrossingCost(), fm.getEnrouteInvoiceCurrency());
        Double roundedRadarCrossingCost = Calculation.roundByCurrencyDecimal(fm.getRadarCrossingCost(), fm.getEnrouteInvoiceCurrency());
        Double roundedTowerCrossingCost = Calculation.roundByCurrencyDecimal(fm.getTowerCrossingDistanceCost(), fm.getEnrouteInvoiceCurrency());
        Double roundedAtcCrossingCost = Calculation.roundByCurrencyDecimal(fm.getAtcCrossingDistanceCost(), fm.getEnrouteInvoiceCurrency());
        Double roundedUserCrossingCost = Calculation.roundByCurrencyDecimal(fm.getUserCrossingDistanceCost(), fm.getEnrouteInvoiceCurrency());

        // set rounded values
        fm.setApproachCharges(roundedApprochCharges);
        fm.setAerodromeCharges(roundedAerodromeCharges);
        fm.setLateArrivalCharges(roundedLateArrivalCharges);
        fm.setLateDepartureCharges(roundedLateDepartureCharges);
        fm.setExtendedHoursSurcharge(roundedExtendedHoursSurcharge);
        fm.setTaspCharge(roundedTaspCharges);
        fm.setEnrouteCharges(roundedEnrouteCharges);
        fm.setBillableCrossingCost(roundedBillableCrossingCost);
        fm.setNominalCrossingCost(roundedNominalCrossingCost);
        fm.setFplCrossingCost(roundedFplCrossingCost);
        fm.setRadarCrossingCost(roundedRadarCrossingCost);
        fm.setTowerCrossingDistanceCost(roundedTowerCrossingCost);
        fm.setAtcCrossingDistanceCost(roundedAtcCrossingCost);
        fm.setUserCrossingDistanceCost(roundedUserCrossingCost);
    }

    private void handleKCAACalculations (final FlightMovement flightMovement, Boolean applyAdap) {
        final KCAAFlightChargesType chargeType = kcaaFlightUtility.getKCAAFlightChargesType(flightMovement);
        FlightMovementPair flightMovementPair = kcaaFlightUtility.findKCAASomaliFlightPair(flightMovement);

        LOG.debug("{} to apply for the Kenyan flight {}", chargeType, flightMovement);

        switch (chargeType) {
            case KCAA_SMALL_AIRCRAFT_CHARGES:
                applyChargesForSmallKenyanAircraft(flightMovement);
                break;
            case TRAINING_CONFIGURED_CHARGES:
                applyChargesForSmallKenyanAircraft(flightMovement);
                applyChargesForTrainingFlights(flightMovement);
                break;
            case TRAINING_NO_CHARGES:
                applyChargesForSmallKenyanAircraft(flightMovement);
                flightMovement.setEnrouteCharges(0.0d);
                break;
            default:
        }

        // check if flight is part of a Somali Flight Pair, if it is, return new FlightMovementPair
        if (flightMovementPair != null) {
            FlightMovement firstSegment = flightMovementPair.getSegmentOne();
            FlightMovement secondSegment = flightMovementPair.getSegmentTwo();

            calculateAndSetFlightPairChargesSegmentOne(firstSegment, secondSegment);
            calculateAndSetFlightPairChargesSegmentTwo(secondSegment, applyAdap);
        }
    }

    private void applyChargesForSmallKenyanAircraft (final FlightMovement flightMovement) {
        final Double domesticPAXCharges = flightMovement.getDomesticPassengerCharges() != null
            ? flightMovement.getDomesticPassengerCharges()
            : 0;
        final Double internationalPAXCharges = flightMovement.getInternationalPassengerCharges() != null
            ? flightMovement.getInternationalPassengerCharges()
            : 0;

        // reset charges
        doInitializeAllAmounts(flightMovement);

        // add charges appropriate for KCAA small aircraft
        flightMovement.setDomesticPassengerCharges(domesticPAXCharges);
        flightMovement.setInternationalPassengerCharges(internationalPAXCharges);
    }

    private void applyChargesForTrainingFlights(final FlightMovement flightMovement) {
        final Currency trainingCostsCurrency = currencyUtils.getSystemConfigurationCurrency(SystemConfigurationItemName.TRAINING_COST_CURRENCY);
        final Currency usdCurrency = currencyUtils.getCurrencyUSD();
        final SystemConfiguration trainingCostPerDayConfig = systemConfigurationService
            .getOneByItemName(SystemConfigurationItemName.TRAINING_COST_PER_DAY);


        if (trainingCostsCurrency != null && trainingCostPerDayConfig != null) {
             String amountStr = trainingCostPerDayConfig.getCurrentValue();
            if (amountStr == null) {
                amountStr = trainingCostPerDayConfig.getDefaultValue();
            }
            final Double amount = Double.parseDouble(amountStr);
            final Double amountUSD = getAmountFromCharge(amount, trainingCostsCurrency, usdCurrency, LocalDateTime.now());
            flightMovement.setEnrouteCharges(amountUSD);
        } else {
            LOG.error("Misconfiguration detected: unable to find the training cost for KCAA Training Flight {}", flightMovement);
        }
    }

    /**
     * SEgment one is charged for the full length of both segments
     * @param segmentOne
     * @param segmentTwo
     */
    private void calculateAndSetFlightPairChargesSegmentOne(FlightMovement segmentOne, FlightMovement segmentTwo) {
        final Double crossDistForPairFlightSegmentOne = getCrossDistForPairFlight(segmentOne);
        final Double crossDistForPairFlightSegmentTwo = getCrossDistForPairFlight(segmentTwo);
        Double crossDistForPairFlight = null;

        flightMovementBillable.calculateBillableRouteAndDistance(segmentOne);
        flightMovementBillable.calculateBillableRouteAndDistance(segmentTwo);

        if (crossDistForPairFlightSegmentOne != null && crossDistForPairFlightSegmentTwo != null) {
            crossDistForPairFlight = flightMovementBillable.getActualCrossDist(segmentOne, crossDistForPairFlightSegmentOne + crossDistForPairFlightSegmentTwo);
            segmentOne.setBillableCrossingDist(crossDistForPairFlight);
        }

        doCalculateAndSetCrossingCosts(segmentOne);
        calculateFlightPairSharedCharges(segmentOne);

        segmentOne.setAerodromeCharges(0d);
        segmentOne.setApproachCharges(0d);
    }

    private Double getCrossDistForPairFlight (FlightMovement flight) {
        Double crossDistForPairFlight = null;
        if (flight.getEnrouteChargesBasis() != null) {
            EnrouteChargesBasis enrouteChargesBasis = EnrouteChargesBasis.forValue(flight.getEnrouteChargesBasis());
            switch (enrouteChargesBasis) {
                case RADAR_SUMMARY:
                    crossDistForPairFlight = flight.getRadarCrossingDistance();
                    break;
                case ATC_LOG:
                    crossDistForPairFlight = flight.getAtcCrossingDistance();
                    break;
                case TOWER_LOG:
                    crossDistForPairFlight = flight.getTowerCrossingDistance();
                    break;
                case SCHEDULED:
                    crossDistForPairFlight = flight.getFplCrossingDistance();
                    break;
                case NOMINAL:
                    crossDistForPairFlight = flight.getNominalCrossingDistance();
                    break;
                case MANUAL:
                    crossDistForPairFlight = flight.getUserCrossingDistance();
                    break;
            }
        }
        return crossDistForPairFlight;
    }

    private void calculateAndSetFlightPairChargesSegmentTwo(FlightMovement segmentTwo, Boolean applyAdap) {
        segmentTwo.setEnrouteCharges(0.0);
        segmentTwo.setBillableCrossingDist(0.0);

        calculateFlightPairSharedCharges(segmentTwo);

        if (applyAdap) {
            Double aerodromeCharges = segmentTwo.getAerodromeCharges() != null ? segmentTwo.getAerodromeCharges() : 0d;
            Double approachCharges = segmentTwo.getApproachCharges() != null ? segmentTwo.getApproachCharges() : 0d;
            segmentTwo.setApproachCharges(aerodromeCharges + approachCharges);
            segmentTwo.setAerodromeCharges(0d);
        }
    }

    private void calculateFlightPairSharedCharges(FlightMovement flight) {
        if (!testMtowNull(flight) || !testFlightChargeType(flight)) {
            return;
        }

        FlightChargeType flightChargeType = getFlightChargeType(flight);
        LocalTime arrivalTimeOfDayForCharges = deriveTimeOfDayForCharge(flight, true);
        Double actualMtow = flight.getActualMtow();

        AerodromeCategory aerodromeCategory = this.flightMovementAerodromeService.resolveLocationToAdCategory(
            flight.getDepAd(), flight.getItem18Dep(), AdResolveType.AD_TYPE_DEPARTURE
        );

        // approach
        LdpBillingFormula approachChargesFormula = getLdpBillingFormulaFromAerodromeCategory(
            aerodromeCategory, LdpBillingFormulaChargeType.approach_charges
        );

        if (approachChargesFormula != null) {
            flight.setApproachCharges(calculateAerodromeTypeCharge(approachChargesFormula, actualMtow, arrivalTimeOfDayForCharges, flightChargeType));
        }

        // calculate aerodrome
        LdpBillingFormula aerodromeChargesFormula = getLdpBillingFormulaFromAerodromeCategory(aerodromeCategory, LdpBillingFormulaChargeType.aerodrome_charges);

        if (aerodromeChargesFormula != null) {
            flight.setAerodromeCharges(calculateAerodromeTypeCharge(aerodromeChargesFormula, actualMtow, arrivalTimeOfDayForCharges, flightChargeType));
        }
    }

    public LocalTime deriveDepartureTimeOfDayForCharge(FlightMovement flightMovement) {
        LocalTime departureTimeOfDayForCharges;

        LocalTime actualDepartureLocalTime = DateTimeUtils
            .convertStringToLocalTime(flightMovement.getActualDepartureTime());

        if (actualDepartureLocalTime != null) {
            departureTimeOfDayForCharges = actualDepartureLocalTime;
        } else {
            departureTimeOfDayForCharges = DateTimeUtils
                .convertStringToLocalTime(flightMovement.getDepTime());
        }
        return departureTimeOfDayForCharges;
    }

    public LocalTime deriveTimeOfDayForCharge(FlightMovement flight, Boolean arrival) {
        List<FlightMovement> priorArrivals = null;
        if (flight.getItem18RegNum() != null) {
            priorArrivals = flightMovementRepository.findPriorArrivals(flight.getItem18RegNum(), flight.getDepAd(), flight.getDateOfFlight());
        }
        LocalTime estimatedArrivalLocalTime = calculateEstimatedArrivalTime(flight);
        LocalTime actualArrivalLocalTime = DateTimeUtils.convertStringToLocalTime(flight.getArrivalTime());
        LocalTime arrivalTimeOfDayForCharges;

        if (arrival) {
            if (actualArrivalLocalTime != null) {
                arrivalTimeOfDayForCharges = actualArrivalLocalTime;
            } else if (estimatedArrivalLocalTime != null) {
                arrivalTimeOfDayForCharges = estimatedArrivalLocalTime;
            } else if (flight.getExitTime() != null) {
                arrivalTimeOfDayForCharges = flight.getExitTime().toLocalTime();
            } else {
                arrivalTimeOfDayForCharges = LocalTime.NOON;
            }
        } else {
            if (priorArrivals == null || priorArrivals.isEmpty()) {
                if (actualArrivalLocalTime != null) {
                    arrivalTimeOfDayForCharges = actualArrivalLocalTime;
                } else if (estimatedArrivalLocalTime != null) {
                    arrivalTimeOfDayForCharges = estimatedArrivalLocalTime;
                } else if (flight.getEntryTime() != null) {
                    arrivalTimeOfDayForCharges = flight.getEntryTime().toLocalTime();
                } else {
                    arrivalTimeOfDayForCharges = LocalTime.NOON;
                }
            } else {
                arrivalTimeOfDayForCharges = DateTimeUtils.convertStringToLocalTime(priorArrivals.get(0).getArrivalTime());

                if (arrivalTimeOfDayForCharges == null) {
                    arrivalTimeOfDayForCharges = LocalTime.NOON;
                }
            }
        }

        return arrivalTimeOfDayForCharges;
    }

    @SuppressWarnings ("squid:S1126")
    private boolean isFlightRequireBillableRoute(FlightMovement fm, final String enrouteFormula) {
        SystemConfiguration config = systemConfigurationService.getOneByItemName(SystemConfigurationItemName.IGNORE_ZERO_LENGTH_ROUTE);
        boolean isIgnoreZeroLengthRoute = config != null &&
            config.getCurrentValue() != null &&
            config.getCurrentValue().equalsIgnoreCase(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE);

        if(!isIgnoreZeroLengthRoute) {
            return false;
        }
        if(kcaaFlightUtility.isValidSmallAircraft(fm) || kcaaFlightUtility.isTrainingFlight(fm)){
            return false;
        }
        if(enrouteFormula != null && !enrouteFormula.matches("[^a-zA-Z\\[\\]]")) {
            return false;
        }
        return true;
    }

    private void applySmallAircraftExemptions(FlightMovement flight) {

        // Check exemption for a small local aircraft
        if( flight != null && kcaaFlightUtility.isValidSmallAircraft(flight)){
            if(systemConfigurationService.getBoolean(SystemConfigurationItemName.EXEMPT_SMALL_AC_WITH_VALID_AOC_ENROUTE)) {
                LOG.debug ("en-route charges are exempt for small local aircraft");
                flight.setEnrouteCharges(0.0);
            }
            if(systemConfigurationService.getBoolean(SystemConfigurationItemName.EXEMPT_SMALL_AC_WITH_VALID_AOC_APPROACH)) {
                LOG.debug ("approach charges are exempt for small local aircraft");
                flight.setApproachCharges(0.0);
            }
            if(systemConfigurationService.getBoolean(SystemConfigurationItemName.EXEMPT_SMALL_AC_WITH_VALID_AOC_AERODROME)) {
                LOG.debug ("aerodrome charges are exempt for small local aircraft");
                flight.setAerodromeCharges(0.0);
            }
            if(systemConfigurationService.getBoolean(SystemConfigurationItemName.EXEMPT_SMALL_AC_WITH_VALID_AOC_PASSENGER)) {
                LOG.debug ("passenger charges are exempt for small local aircraft");
                flight.setInternationalPassengerCharges(0.0);
                flight.setDomesticPassengerCharges(0.0);
            }
        }
    }

    /**
     * Find prior flight movement arrival and return as a delta stop object.
     */
    private DeltaFlightVO priorArrivalAsDeltaStop(final FlightMovement flightMovement) {
        if (flightMovement == null) return null;

        DeltaFlightVO result = new DeltaFlightVO();

        // define stop identity as the provided flight movement departure aerodrome
        if (flightMovement.getDepAd() == null || flightMovement.getDepAd().equals(ApplicationConstants.PLACEHOLDER_ZZZZ)) {
            result.setIdent(flightMovement.getItem18Dep());
        } else {
            result.setIdent(flightMovement.getDepAd());
        }

        // define stop departure time as provided flight movement departure time
        result.setDepartAt(flightMovement.getDepTime());

        // define stop arrival time as prior flight movement arrival time
        LocalTime arrivalTime = deriveTimeOfDayForCharge(flightMovement, false);
        if (arrivalTime != null) {
            result.setArrivaAt(DateTimeUtils.convertLocalTimeToString(arrivalTime));
        }

        return result;
    }

    /**
     * Calculate ADAP charges for intermediate aerodromes included in the route for TRU PLAN flights
     * Calculate aerodrome and approach charges from flight movement based on arrival or departure aerodrome and time.
     *
     * @param flightMovement flight movement to calculate
     * @param onArrival true if charges applied on arrival, else on departure
     * @param isCombined true if charges are combined under approach
     */
    private void calculateAdapChargesThruFlight(
        final FlightMovement flightMovement, final Boolean onArrival, final Boolean isCombined
    ) {

        FlightChargeType flightChargeType = getFlightChargeType(flightMovement);

        List<ThruFlightPlanVO> stops = this.thruFlightPlanUtility.parseThruPlanRoute(flightMovement.getFplRoute(), flightMovement.getDepAd(),
                DateTimeUtils.addTimeToDate(flightMovement.getDateOfFlight(), flightMovement.getDepTime()));

        if (stops == null || stops.isEmpty()) {
            return;
        }

        double aerodromeChargesSum = 0d;
        double approachChargesSum = 0d;

        for (ThruFlightPlanVO s : stops) {

            // get aerodrome category for stop
            AerodromeCategory aerodromeCategory;
            if(onArrival) {
                 aerodromeCategory = flightMovementAerodromeService.resolveLocationToAdCategory(
                     s.getDestAd(), null, AdResolveType.AD_TYPE_DELTA_DESTINATION);
            } else {
                aerodromeCategory = flightMovementAerodromeService.resolveLocationToAdCategory(
                    s.getDepAd(), null, AdResolveType.AD_TYPE_DELTA_DESTINATION);
            }

            // calculate aerodrome charges
            LdpBillingFormula aerodromeChargesFormula = getLdpBillingFormulaFromAerodromeCategory(aerodromeCategory,
                LdpBillingFormulaChargeType.aerodrome_charges);
            LocalTime arrivalTimeOfDayForCharges = s.getArrivalTime().toLocalTime();
            double aerodromeCharges = aerodromeChargesFormula == null ? 0d : calculateAerodromeTypeCharge(
                aerodromeChargesFormula, flightMovement.getActualMtow(), arrivalTimeOfDayForCharges, flightChargeType);

            // calculate approach charges
            LdpBillingFormula approachChargesFormula = getLdpBillingFormulaFromAerodromeCategory(aerodromeCategory,
                LdpBillingFormulaChargeType.approach_charges);
            double approachCharges = approachChargesFormula == null ? 0d : calculateAerodromeTypeCharge(
                approachChargesFormula, flightMovement.getActualMtow(), arrivalTimeOfDayForCharges, flightChargeType);

            aerodromeChargesSum = Double.sum(aerodromeChargesSum, aerodromeCharges);
            approachChargesSum = Double.sum(approachChargesSum, approachCharges);
            LOG.debug("Formulas for ADAP Arrival Charges: Approach={}, Aerodrome={}",
                approachChargesFormula, aerodromeChargesFormula);
            LOG.debug("Calculated ADAP Arrival charges: Approach={}, Aerodrome={}",
                approachChargesSum, aerodromeChargesSum);
        }

        // used within enroute charge formula and is not persisted to the database layer
        // must be done before applying any discounts and is the reason it is separated
        flightMovement.setAerodromeChargesWithoutDiscount(aerodromeChargesSum);
        flightMovement.setApproachChargesWithoutDiscount(approachChargesSum);

        // overwrite aerodrome and approach charges
        if (isCombined) {
            flightMovement.setApproachCharges(Double.sum(aerodromeChargesSum, approachChargesSum));
            flightMovement.setAerodromeCharges(0d);
        } else {
            flightMovement.setApproachCharges(approachChargesSum);
            flightMovement.setAerodromeCharges(aerodromeChargesSum);
        }
    }

    private Double calculateLandingFeesAd(FlightMovement fm) {
        Double result = null;
        if(fm == null)
            return result;

        String adIdentifier = null;
        if (fm.getArrivalAd() != null && !fm.getArrivalAd().isEmpty())
            adIdentifier = fm.getArrivalAd();
        else if (fm.getDestAd() != null && !fm.getDestAd().isEmpty())
            adIdentifier = fm.getDestAd();

        // in order to calculate arrival related charges, we need to
        // make sure the aerodrome has aerodrome category associated
        // with it. find ad in local aerodromes table
        AerodromeCategory aerodromeCategory = this.flightMovementAerodromeService
            .resolveLocationToAdCategory(adIdentifier, fm.getItem18Dest(), AdResolveType.AD_TYPE_DESTINATION);
        LocalTime arrivalTimeOfDayForCharges = deriveTimeOfDayForCharge(fm, true);
        FlightChargeType flightChargeType = getFlightChargeType(fm);

        // calculate aerodrome charges
        LdpBillingFormula aerodromeChargesFormula = getLdpBillingFormulaFromAerodromeCategory(aerodromeCategory,
            LdpBillingFormulaChargeType.aerodrome_charges);
        double aerodromeCharges = aerodromeChargesFormula == null ? 0d : calculateAerodromeTypeCharge(
            aerodromeChargesFormula, fm.getActualMtow(), arrivalTimeOfDayForCharges, flightChargeType);

        // calculate approach charges
        LdpBillingFormula approachChargesFormula = getLdpBillingFormulaFromAerodromeCategory(aerodromeCategory,
            LdpBillingFormulaChargeType.approach_charges);
        double approachCharges = approachChargesFormula == null ? 0d : calculateAerodromeTypeCharge(
            approachChargesFormula, fm.getActualMtow(), arrivalTimeOfDayForCharges, flightChargeType);

        result = aerodromeCharges + approachCharges;

        return result;
    }
}
