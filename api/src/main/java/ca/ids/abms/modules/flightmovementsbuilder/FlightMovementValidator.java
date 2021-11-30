package ca.ids.abms.modules.flightmovementsbuilder;

import static ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementValidatorIssue.MISSING_ENROUTE_FORMULA_OR_CHARGE_SCHEDULE;
import static ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementValidatorIssue.MISSING_MTOW;
import static ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementValidatorIssue.MISSING_PARKING_TIME;
import static ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementValidatorIssue.NO_ASSOCIATED_ACCOUNT;
import static ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementValidatorIssue.UNKNOWN_DEP_AD;
import static ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementValidatorIssue.UNKNOWN_DEST_AD;
import static ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementValidatorIssue.ZERO_LENGTH_BILLABLE_TRACK;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

import ca.ids.abms.modules.formulas.enroute.EnrouteAirNavigationChargeFormula;
import ca.ids.abms.modules.formulas.enroute.EnrouteAirNavigationChargeFormulaService;
import ca.ids.abms.modules.system.BillingOrgCode;
import ca.ids.abms.modules.util.models.DateTimeUtils;
import ca.ids.abms.modules.utilities.flights.FlightUtility;

import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.vividsolutions.jts.geom.Coordinate;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.error.ErrorVariables;
import ca.ids.abms.config.error.RejectedReasons;
import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.aerodromes.AerodromeCategory;
import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.aircraft.AircraftRegistrationService;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.countries.CountryService;
import ca.ids.abms.modules.countries.RegionalCountryService;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.currencies.CurrencyExchangeRate;
import ca.ids.abms.modules.currencies.CurrencyExchangeRateService;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementAerodromeService;
import ca.ids.abms.modules.flightmovements.FlightMovementValidationViewModel;
import ca.ids.abms.modules.flightmovements.category.FlightmovementCategory;
import ca.ids.abms.modules.flightmovements.category.FlightmovementCategoryService;
import ca.ids.abms.modules.flightmovements.enumerate.AdResolveType;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementType;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryNationality;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryScope;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryType;
import ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementValidatorIssue;
import ca.ids.abms.modules.flightmovementsbuilder.utility.FlightMovementBuilderUtility;
import ca.ids.abms.modules.flightmovementsbuilder.utility.KCAAFlightUtility;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.vo.RouteCacheVO;
import ca.ids.abms.modules.formulas.ldp.LdpBillingFormula;
import ca.ids.abms.modules.formulas.ldp.ChargeTypes.LdpBillingFormulaChargeType;
import ca.ids.abms.modules.mtow.AverageMtowFactor;
import ca.ids.abms.modules.mtow.AverageMtowFactorService;
import ca.ids.abms.modules.radarsummary.RadarSummary;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.spatiareader.dto.FplObjectDto;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.unifiedtaxes.UnifiedTaxChargesService;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocation;
import ca.ids.abms.modules.util.models.ApplicationConstants;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.util.GeometryUtils;
import ca.ids.abms.util.StringUtils;

@Component
public class FlightMovementValidator {

    private static final  Logger LOG = LoggerFactory.getLogger(FlightMovementBuilderUtility.class);

    private final SystemConfigurationService systemConfigurationService;

    private final FlightMovementAerodromeService flightMovementAerodromeService;

    private final CurrencyExchangeRateService currencyExchangeRateService;

    private final FlightMovementBuilderUtility flightMovementBuilderUtility;

    private final CurrencyUtils currencyUtils;

    private final RegionalCountryService regionalCountryService;

    private final CountryService countryService;

    private final AircraftRegistrationService aircraftRegistrationService;

    private final KCAAFlightUtility kcaaFlightUtility;

    private final EnrouteAirNavigationChargeFormulaService enrouteAirNavigationChargeFormulaService;

    private final AverageMtowFactorService averageMtowFactorService;

    private final FlightmovementCategoryService flightmovementCategoryService;
    
    private final UnifiedTaxChargesService unifiedTaxChargesService;

    @SuppressWarnings("squid:S00107")
    public FlightMovementValidator(
        final SystemConfigurationService systemConfigurationService,
        final FlightMovementAerodromeService flightMovementAerodromeService,
        final CurrencyExchangeRateService currencyExchangeRateService,
        final FlightMovementBuilderUtility flightMovementBuilderUtility,
        final CurrencyUtils currencyUtils,
        final CountryService countryService,
        final RegionalCountryService regionalCountryService,
        final AircraftRegistrationService aircraftRegistrationService,
        final KCAAFlightUtility kcaaFlightUtility,
        final EnrouteAirNavigationChargeFormulaService enrouteAirNavigationChargeFormulaService,
        final AverageMtowFactorService averageMtowFactorService,
        final FlightmovementCategoryService flightmovementCategoryService,
        final UnifiedTaxChargesService unifiedTaxChargesService
    ) {
        this.systemConfigurationService = systemConfigurationService;
        this.flightMovementAerodromeService = flightMovementAerodromeService;
        this.currencyExchangeRateService = currencyExchangeRateService;
        this.flightMovementBuilderUtility = flightMovementBuilderUtility;
        this.currencyUtils = currencyUtils;
        this.countryService = countryService;
        this.regionalCountryService = regionalCountryService;
        this.aircraftRegistrationService = aircraftRegistrationService;
        this.kcaaFlightUtility = kcaaFlightUtility;
        this.enrouteAirNavigationChargeFormulaService = enrouteAirNavigationChargeFormulaService;
        this.averageMtowFactorService = averageMtowFactorService;
        this.flightmovementCategoryService = flightmovementCategoryService;
        this.unifiedTaxChargesService = unifiedTaxChargesService;
    }

    /**
     * This method check and validate the status of flight movement how to descriptive on requirement document
     *
     * @param flightMovement flight movement to validate
     * @param forInvoice     is validation for invoice
     * @return the status of flight movement
     */
    public FlightMovementValidationViewModel validateFlightMovementStatus(FlightMovement flightMovement, Boolean forInvoice) {
        FlightMovementValidationViewModel flightMovementValidationViewModel = new FlightMovementValidationViewModel();

        // System configurations
        boolean isPassengerRecordRequired = getIsRequired(SystemConfigurationItemName.PASSENGER_CHARGES_SUPPORT);
        boolean includePassengerFeesOnInvoice = getIsRequired(SystemConfigurationItemName.INCLUDE_PASSENGER_FEES_ON_INVOICE);
        boolean isRadarSummaryRequired = getIsRequired(SystemConfigurationItemName.RADAR_SUMMARY_REQUIRED);
        boolean isAtcLogRequired = getIsRequired(SystemConfigurationItemName.ATC_LOG_REQUIRED);
        boolean isTowerLogRequired = getIsRequired(SystemConfigurationItemName.TOWER_LOG_REQUIRED);
        boolean isFlightPlanRequired = getIsRequired(SystemConfigurationItemName.FLIGHT_PLAN_REQUIRED);
        boolean isParkingTimeRequired = getIsRequired(SystemConfigurationItemName.PARKING_TIME_REQUIRED);
        boolean isPassengerServiceChargeRequired = getIsRequired(SystemConfigurationItemName.PASSENGER_SERVICE_CHARGE_REQUIRED);

        
        if (flightMovement == null)
            return null;
       
        flightMovementValidationViewModel.setFlightMovementID(flightMovement.getId());
        flightMovementValidationViewModel.setFlightId(flightMovement.getFlightId());
        flightMovementValidationViewModel.setDayOfFlight(flightMovement.getDateOfFlight());
        flightMovementValidationViewModel.setDepartureTime(flightMovement.getDepTime());
        flightMovementValidationViewModel.setRegistration(flightMovement.getItem18RegNum());

        EnumSet<FlightMovementValidatorIssue> issues = EnumSet.noneOf(FlightMovementValidatorIssue.class);

        if (flightMovement.isEnrouteFormulaNotValid()) {
            issues.add(MISSING_ENROUTE_FORMULA_OR_CHARGE_SCHEDULE);
        }

        // OTHER flight doesn't have to have anything
        if (flightMovement.getFlightCategoryType() != null &&
            flightMovement.isOTHER()) {
                flightMovementValidationViewModel.setStatus(FlightMovementStatus.OTHER);

                LOG.info("Movement Type is OTHER - FlightMovement {}", flightMovement.getId());
                flightMovementValidationViewModel.setIssues(issues);
                return flightMovementValidationViewModel;
        }

        // account is associated with FM
        if (flightMovement.getAccount() == null) {
            LOG.info("AssociatedAccount is Null - FlightMovement {}", flightMovement.getId());
            issues.add(NO_ASSOCIATED_ACCOUNT);
        } else {
            flightMovementValidationViewModel.setAccountName(flightMovement.getAccount().getName());
        }

        // aircraft type is null or it is not known to the system
        if (flightMovement.getAircraftType() == null ||
            flightMovementBuilderUtility.checkAircraftType(flightMovement) == null) {
            LOG.info("Aircraft type is Null or Unknown - FlightMovement {} Aircraft type: {}",
                flightMovement.getId(), flightMovement.getAircraftType());
            issues.add(FlightMovementValidatorIssue.UNKNOWN_AIRCRAFT_TYPE);
        }

        // MTOW is associated
        if (flightMovement.getActualMtow() == null) {
            LOG.info("Missing departure date time is Null - {}", flightMovement);
            issues.add(MISSING_MTOW);
        } else if (kcaaFlightUtility.isInvalidSmallAircraft(flightMovement) && systemConfigurationService.getBoolean(SystemConfigurationItemName.GENERATE_ERROR_SMALL_AC_MISSING_AOC)) {
            LOG.info("Invalid small aircraft - {}", flightMovement);
            issues.add(FlightMovementValidatorIssue.EXPIRED_OR_MISSING_COA_FOR_SMALL_AIRCRAFT);
        }

        // billable route length is 0 or null and required
        if ((flightMovement.getBillableCrossingDist() == null || flightMovement.getBillableCrossingDist() <= 0.0) &&
            isFlightRequireBillableRoute(flightMovement)
        ) {
            LOG.info("BillableRoute is Null or 0 - FlightMovement {}", flightMovement.getId());
            issues.add(ZERO_LENGTH_BILLABLE_TRACK);
        }

        // check if dep ad is unresolved, aerodromes defined by ICAO identifier (not ZZZZ or AFIL)
        if (flightMovement.getDepAd() != null && !(flightMovement.getDepAd().equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_ZZZZ)
            || flightMovement.getDepAd().equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_AFIL))
            && flightMovementAerodromeService.resolveAnyLocationToDMS(flightMovement.getDepAd()) == null) {
            issues.add(UNKNOWN_DEP_AD);
        }

        // check if dest ad is unresolved, aerodromes defined by ICAO identifier (not ZZZZ)
        if (flightMovement.getDestAd() != null && !flightMovement.getDestAd().equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_ZZZZ)
            && flightMovementAerodromeService.resolveAnyLocationToDMS(flightMovement.getDestAd()) == null) {
            issues.add(UNKNOWN_DEST_AD);
        }

        // check radar summary if flight level is above radar floor
        if (!forInvoice && isRadarSummaryRequired ) {

            int radarFloor = 0;
            if (systemConfigurationService.getOneByItemName(SystemConfigurationItemName.RADAR_FLOOR_LEVEL).getCurrentValue() != null) {
                radarFloor = Integer.parseInt(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.RADAR_FLOOR_LEVEL).getCurrentValue());
            }
            
            // 2020-09-14
            // TFS 116127 - radar floor from System Config is in feet and has to be converted to hundred of feet to compare with the flight level
            if(radarFloor > 0) {
                radarFloor = radarFloor/100;
            }
            double flightLevel = FlightUtility.convertFlightLevel(flightMovement.getFlightLevel(),0.0);
            if (flightLevel >= radarFloor &&
                (flightMovement.getRadarRouteText() == null || flightMovement.getRadarRouteText().isEmpty())) {
                // radar info is missing
                issues.add(FlightMovementValidatorIssue.RADAR_SUMMARY_MISSING);
            }
        }

        // check ATC log
        if (!forInvoice && isAtcLogRequired) {
            //ATC log is not available for Delta flights and circular flights
            try {
                Boolean circularRoute = flightMovementAerodromeService.isCircularRoute(flightMovement.getDepAd(), flightMovement.getItem18Dep(),
                    flightMovement.getDestAd(), flightMovement.getItem18Dest());

                if (!flightMovement.getDeltaFlight() && !circularRoute &&

                    (flightMovement.getAtcLogRouteText() == null || flightMovement.getAtcLogRouteText().isEmpty())) {
                        issues.add(FlightMovementValidatorIssue.ATC_LOG_MISSING);
                }

            } catch (FlightMovementBuilderException e) {
                LOG.error("Error: ", e);
            }


        }

        // check Flight plan
        if (isFlightPlanRequired &&
                (flightMovement.getInitialFplData() == null || flightMovement.getInitialFplData().isEmpty())) {
                issues.add(FlightMovementValidatorIssue.FLIGHT_PLAN_MISSING);
        }


        if (!StringUtils.isNotBlank(flightMovement.getAircraftType())) {
            LOG.info("AircraftType is Null - FlightMovement {}", flightMovement.getId());
            issues.add(FlightMovementValidatorIssue.UNKNOWN_AIRCRAFT_TYPE);
        }


        //TODO check for passenger manifest  Kenia


        // Botswana charges passenger fees on departure

        if (isPassengerRecordRequired && (!forInvoice || includePassengerFeesOnInvoice)) {
            if (flightMovement.getFlightCategoryType() != null &&
                (flightMovement.getFlightCategoryType().equals(FlightmovementCategoryType.DEPARTURE) ||
                    flightMovement.getFlightCategoryType().equals(FlightmovementCategoryType.DOMESTIC))) {

                if (flightMovement.getPassengersChargeableDomestic() == null || flightMovement.getPassengersChargeableDomestic() < 0) {
                    LOG.info("Domestic passenger data missing - FlightMovement {}", flightMovement.getId());
                    issues.add(FlightMovementValidatorIssue.MISSING_PASSENGER_DOMESTIC_DATA);
                }

            }

            if (flightMovement.getFlightCategoryType() != null && flightMovement.getFlightCategoryType().equals(FlightmovementCategoryType.DEPARTURE) &&
                    (flightMovement.getPassengersChargeableIntern() == null || flightMovement.getPassengersChargeableIntern() < 0)){
                    LOG.info("International passenger data missing - FlightMovement {}", flightMovement.getId());
                    issues.add(FlightMovementValidatorIssue.MISSING_PASSENGER_INTERNATIONAL_DATA);
            }

        }

        // check parking time
        if (flightMovement.getFlightCategoryType() != null &&
             (flightMovement.getFlightCategoryType().equals(FlightmovementCategoryType.DEPARTURE) ||
                flightMovement.getFlightCategoryType().equals(FlightmovementCategoryType.DOMESTIC))) {

            if (isParkingTimeRequired && isParkingScheduleExist(flightMovement) &&
                     ((flightMovement.getParkingTime() == null || flightMovement.getParkingTime() < 0.0)
                            || (flightMovement.getParkingTime() > 0.0 && (flightMovement.getParkingCharges() == null
                                    || flightMovement.getParkingCharges() < 0.0)))) {
                // check if there are parking charges for the aerodrome

                LOG.info("Missing parking time - FlightMovement {}", flightMovement.getId());
                issues.add(MISSING_PARKING_TIME);
            }


            //check if passenger return does exist for FM?
            if (!forInvoice && isPassengerServiceChargeRequired &&
                ((flightMovement.getPassengersChild() == null || flightMovement.getPassengersChild() < 1) &&
               		(flightMovement.getPassengersJoiningAdult() == null || flightMovement.getPassengersJoiningAdult() < 1) &&
                	(flightMovement.getPassengersTransitAdult() == null ||flightMovement.getPassengersTransitAdult() < 1) &&
               		(flightMovement.getPassengersChargeableDomestic() == null ||  flightMovement.getPassengersChargeableDomestic() <1) &&
               		(flightMovement.getPassengersChargeableIntern() == null || flightMovement.getPassengersChargeableIntern() <1))) {
                issues.add(FlightMovementValidatorIssue.MISSING_PASSENGER_SERVICE_CHARGE_RETURN);
            }
        }

        // Tower log required if it is NOT overflight
        if (!forInvoice &&
            flightMovement.getFlightCategoryType() != null &&
            !flightMovement.getFlightCategoryType().equals(FlightmovementCategoryType.OVERFLIGHT) &&
            isTowerLogRequired
           ) {
                if (flightMovement.getTowerLogTrack() == null || flightMovement.getTowerLogTrack().isEmpty()) {
                    issues.add(FlightMovementValidatorIssue.TOWER_LOG_MISSING);
             }
        }

        // used for exchange rate validation
        Currency usdCurrency = currencyUtils.getCurrencyUSD();
        Currency anspCurrency = currencyUtils.getAnspCurrency();

        // validate usd exchange rate
        if (usdCurrency != null && !usdCurrency.equals(anspCurrency) && !validateExchangeRate(usdCurrency, flightMovement.getDateOfFlight())) {
            issues.add(FlightMovementValidatorIssue.MISSING_USD_EXCHANGE_RATE);
        }

        // validate ansp exchange rate
        if (anspCurrency != null && !anspCurrency.equals(usdCurrency) && !validateExchangeRate(anspCurrency, flightMovement.getDateOfFlight())) {
            issues.add(FlightMovementValidatorIssue.MISSING_ANSP_EXCHANGE_RATE);
        }

        // validate all time values (depTime, arrivalTime, estimatedElapsedTime)
        if (
            (StringUtils.isNotBlank(flightMovement.getDepTime()) && !DateTimeUtils.isValidTimeFormat(flightMovement.getDepTime())) ||
            (StringUtils.isNotBlank(flightMovement.getArrivalTime()) && !DateTimeUtils.isValidTimeFormat(flightMovement.getArrivalTime())) ||
            (StringUtils.isNotBlank(flightMovement.getEstimatedElapsedTime()) && !DateTimeUtils.isValidTimeFormat(flightMovement.getEstimatedElapsedTime()))
        ) {
            issues.add(FlightMovementValidatorIssue.INVALID_TIME_FORMAT);
        }

        // if system configured to use airspace flight level for calculations, there must be a valid flight level
        if(isValidateFlightLevelAirspace() && 
                (StringUtils.isBlank(flightMovement.getFlightLevel()) ||
                        (!isValidLevelInFA(flightMovement.getFlightLevel()) && 
                        !isValidLevelInSM(flightMovement.getFlightLevel()) &&
                        !isValidUncontrolledVFRFlight(flightMovement.getFlightLevel())))) {
            issues.add(FlightMovementValidatorIssue.INVALID_FLIGHT_LEVEL);
        }
        
        boolean isUnifiedtaxFlightMovement = isUnifiedTaxFlightMovement(flightMovement);
        boolean isUnifiedtaxFlightMovementInvoiced = false;
        
        if (isUnifiedtaxFlightMovement) {
        	isUnifiedtaxFlightMovementInvoiced = checkUnifiedTaxInvoiced(flightMovement);
			if (isUnifiedtaxFlightMovementInvoiced) {        
	            issues.add(FlightMovementValidatorIssue.UNIFIED_TAX_NOT_PAID_FOR_CURRENT_YEAR);
	        }
        }        
                
        if (!issues.isEmpty()) {
            flightMovementValidationViewModel.setStatus(FlightMovementStatus.INCOMPLETE);
        }
        else {
        	if (isUnifiedtaxFlightMovement && isUnifiedtaxFlightMovementInvoiced) {
        		flightMovementValidationViewModel.setStatus(FlightMovementStatus.INVOICED);
        		
        		final String aircraftRegNum = flightMovement.getItem18RegNum();
      		  	if (aircraftRegNum != null) {
      		  		List<BillingLedger> billingLedgers = unifiedTaxChargesService.getBillingLedgerByRegistrationNumberAndDate(
        				aircraftRegNum, flightMovement.getDateOfFlight());
      		  		
      		  		if (!billingLedgers.isEmpty()) {
	      		  		flightMovement.setEnrouteInvoiceId(billingLedgers.get(0).getId());
	      		  		flightMovement.setPassengerInvoiceId(billingLedgers.get(0).getId());
	      		  		flightMovement.setOtherInvoiceId(billingLedgers.get(0).getId());
      		  		}
      		  	}
        	}
        	else
        		flightMovementValidationViewModel.setStatus(FlightMovementStatus.PENDING);
        }

        flightMovementValidationViewModel.setIssues(issues);

        return flightMovementValidationViewModel;
    }

    /**
    * @return the type of flight movement
    */
    public FlightMovementValidationViewModel validateFlightMovementType(FlightMovement flightMovement) {
        FlightMovementValidationViewModel flightMovementValidationViewModel = new FlightMovementValidationViewModel();
        FlightMovementType flightMovementType = FlightMovementType.OTHER;
        if (flightMovement != null) {
            flightMovementValidationViewModel.setFlightMovementID(flightMovement.getId());

            // resolve departure and destination aerodrome from flight movement fields
            String depAd = resolveDepartureAerodrome(flightMovement);
            String destAd = resolveDestinationAerodrome(flightMovement);

            // calculate departure and destination aerodrome coordinates
            Boolean isDepCoord = flightMovementAerodromeService.isAerodromeDomestic(depAd, flightMovement.getDepAd(), true);
            Boolean isDestCoord = flightMovementAerodromeService.isAerodromeDomestic(destAd, flightMovement.getDestAd());

    	  if(isDepCoord && isDestCoord){
    		  flightMovementType = FlightMovementType.DOMESTIC;
    	  } else if(isDepCoord && !isDestCoord){
    		  flightMovementType = FlightMovementType.INT_DEPARTURE;
    	  } else if(!isDepCoord && isDestCoord) {
    		  flightMovementType = FlightMovementType.INT_ARRIVAL;
    	  } else if(!isDepCoord && !isDestCoord) {
    		  flightMovementType = FlightMovementType.OTHER;
    		  if ((flightMovement.getFplCrossingDistance() != null && flightMovement.getFplCrossingDistance() > 0.0) ||
    				  (flightMovement.getAtcCrossingDistance() != null && flightMovement.getAtcCrossingDistance() > 0.0) ||
    				  (flightMovement.getTowerCrossingDistance() != null && flightMovement.getTowerCrossingDistance() > 0.0) ||
    				  (flightMovement.getRadarCrossingDistance() != null && flightMovement.getRadarCrossingDistance() > 0.0)) {
    			  flightMovementType = FlightMovementType.INT_OVERFLIGHT;
    		  }
    	  }

    	  /* It's a regional flight only if the below conditions are meet
    	   * ------------------------------------------------------------
    	   * 1. The flight is an international flight (i.e. overflight, intl. departure or intl. arrival)
    	   * 2. The aircraft is registered in a country listed in the regional countries table.
    	   *    This is determined by matching the aircraft’s registration number prefix with the registration number
    	   *    prefix of a country that is listed in the regional countries table.
    	   * 3. The flight originates from and terminates in countries listed in the regional countries table.
    	   */

    	  /* Step 1  */
    	  if (flightMovementType.equals(FlightMovementType.INT_DEPARTURE)
    			  || flightMovementType.equals(FlightMovementType.INT_ARRIVAL)
    			  || flightMovementType.equals(FlightMovementType.INT_OVERFLIGHT)) {

    		  /* Step 2 */
    		  final String aircraftRegNum = flightMovement.getItem18RegNum();
    		  if (aircraftRegNum != null) {

    			  Country aircraftCountry = null;
    			  final AircraftRegistration aircraftRegistration = aircraftRegistrationService
    					  .findAircraftRegistrationByRegistrationNumber(aircraftRegNum, flightMovement.getDateOfFlight());
    			  if (aircraftRegistration != null) {
    				  aircraftCountry = aircraftRegistration.getCountryOfRegistration();
    			  } else {
    				  LOG.debug("Not found in the DB the aircraft registration {}. We'll try to get the country by the prefix.", aircraftRegNum);
    			  }
    			  if (aircraftCountry == null) {
    				  aircraftCountry = countryService.findCountryByPrefix(aircraftRegNum);
    			  }
    			  if (aircraftCountry == null) {
    				  LOG.warn("Cannot check if the flight {} is a regional flight because the aircraft registration number {} doesn't have any country associated",
    						  flightMovement, aircraftRegNum);
    			  } else if (regionalCountryService.getOne(aircraftCountry.getId()) != null) {

    				  /* Step 3 */

    				  if (!isDepCoord && isDestCoord) {
    					  if (isRegionalLocation(flightMovement.getDepAd(), flightMovement.getItem18Dep(), true)) {
    						  flightMovementType = FlightMovementType.REG_ARRIVAL;
    						  LOG.debug("Because departure aerodrome {} is regional, the FLM {} is a REG_ARRIVAL", flightMovement.getDepAd(),flightMovement.getId());
    					  }
    				  } else if (isDepCoord && !isDestCoord) {
    					  if (isRegionalLocation(flightMovement.getDestAd(),flightMovement.getItem18Dest(), false)) {
    						  flightMovementType = FlightMovementType.REG_DEPARTURE;
    						  LOG.debug("Because the departure aerodrome {} is regional, the FLM {} is a REG_DEPARTURE", flightMovement.getDestAd(), flightMovement.getId());
    					  }
    				  } else if (!isDepCoord && !isDestCoord) {
    					  if (isRegionalLocation(flightMovement.getDepAd(), flightMovement.getItem18Dep(), true)
                              && isRegionalLocation(flightMovement.getDestAd(), flightMovement.getItem18Dest(), false)) {
    						  flightMovementType = FlightMovementType.REG_OVERFLIGHT;
    						  LOG.debug("Because the departure {} and the destination {} aerodromes are regional, the FLM {} is a REG_OVERFLIGHT", flightMovement.getDepAd(),
    								  flightMovement.getDestAd(), flightMovement.getId());
    					  }
    				  }
    			  }
    		  } else {
    			  LOG.debug("Cannot check if the flight {} is a regional flight because the aircraft registration number is null", flightMovement);
    		  }
    	  }
       }
       flightMovementValidationViewModel.setMovementType(flightMovementType);
       return flightMovementValidationViewModel;
    }

    /**
     * This method check the status of flight movement how to descriptive on requirement document
     *
     * @return the type of flight movement
     */
    public FlightMovementValidationViewModel validateFlightMovementCategory(FlightMovement flightMovement) {

        FlightMovementValidationViewModel flightMovementValidationViewModel = this.validateFlightMovementType(flightMovement);

        if (flightMovement != null) {
            flightMovementValidationViewModel.setFlightMovementID(flightMovement.getId());

            FlightmovementCategoryType type = this.validateFlightmovementCategoryType(flightMovement);
            FlightmovementCategoryNationality nationality = this.validateFlightmovementCategoryNationality(flightMovement);
            flightMovement.setFlightCategoryType(type);
            FlightmovementCategoryScope scope = this.validateFlightmovementCategoryScope(flightMovement);

            // find flight category
            FlightmovementCategory category = flightmovementCategoryService.findCategoryByParameters(type, scope, nationality);
            if(category == null) {
            	category = flightmovementCategoryService.getOne(0);
            }
           	flightMovementValidationViewModel.setFlightmovementCategory(category);

            flightMovementValidationViewModel.setFlightmovementNationality(nationality);
            flightMovementValidationViewModel.setFlightmovementScope(scope);
            flightMovementValidationViewModel.setFlightmovementType(type);


            /*
             * TODO Temporary keep FlightMovementType set to the model as well.
             */
            if(type != null && scope != null) {
            	if(type.equals(FlightmovementCategoryType.OTHER)){
            		flightMovementValidationViewModel.setMovementType(FlightMovementType.OTHER);
            	} else if(type.equals(FlightmovementCategoryType.DOMESTIC)) {
            		flightMovementValidationViewModel.setMovementType( FlightMovementType.DOMESTIC);
            	} else if(type.equals(FlightmovementCategoryType.ARRIVAL)){
            		if(scope.equals(FlightmovementCategoryScope.INTERNATIONAL)){
            			flightMovementValidationViewModel.setMovementType( FlightMovementType.INT_ARRIVAL);
            		} else if(scope.equals(FlightmovementCategoryScope.REGIONAL)) {
            			flightMovementValidationViewModel.setMovementType( FlightMovementType.REG_ARRIVAL);
            		}
            	} else if(type.equals(FlightmovementCategoryType.DEPARTURE)){
            		if(scope.equals(FlightmovementCategoryScope.INTERNATIONAL)){
            			flightMovementValidationViewModel.setMovementType( FlightMovementType.INT_DEPARTURE);
            		} else if(scope.equals(FlightmovementCategoryScope.REGIONAL)) {
            			flightMovementValidationViewModel.setMovementType( FlightMovementType.REG_DEPARTURE);
            		}
            	} else if(type.equals(FlightmovementCategoryType.OVERFLIGHT)){
            		if(scope.equals(FlightmovementCategoryScope.INTERNATIONAL)){
            			flightMovementValidationViewModel.setMovementType( FlightMovementType.INT_OVERFLIGHT);
            		} else if(scope.equals(FlightmovementCategoryScope.REGIONAL)) {
            			flightMovementValidationViewModel.setMovementType( FlightMovementType.REG_OVERFLIGHT);
            		}
            	}
            }
        }
        return flightMovementValidationViewModel;
    }

    public void recalculateFlightMovementCategory(FlightMovement flightMovement) {
        if (flightMovement != null) {
            FlightmovementCategoryType type = flightMovement.getFlightCategoryType();
            FlightmovementCategoryNationality nationality = flightMovement.getFlightCategoryNationality();
            FlightmovementCategoryScope scope = flightMovement.getFlightCategoryScope();

            // find flight category
            FlightmovementCategory category = flightmovementCategoryService.findCategoryByParameters(type, scope, nationality);
            if(category != null) {
                flightMovement.setFlightmovementCategory(category);
            }
        }
    }

    private boolean isRegionalAerodrome (final String aerodrome) {
        boolean hasRegionalAerodrome = false;
        if (aerodrome != null && aerodrome.length() > 2) {
        	if (systemConfigurationService.getBillingOrgCode() == BillingOrgCode.KCAA
                && aerodrome.substring(0,2).equalsIgnoreCase("HS")
            ) {
        		hasRegionalAerodrome =flightMovementAerodromeService.isAdInsideSouthSudan(aerodrome);
        		return hasRegionalAerodrome;
        	}

            final List<Country> countries = countryService.findCountryByAerodromePrefix(aerodrome.substring(0, 2));
            if (CollectionUtils.isNotEmpty(countries)) {
                for (final Country c : countries) {
                    hasRegionalAerodrome = regionalCountryService.getOne(c.getId()) != null;
                    if (hasRegionalAerodrome) {
                        break;
                    }
                }
            }
        }
        return hasRegionalAerodrome;
    }

    /**
     * Find country for the location defined in item18 DEP/ and/or DEST/
     * <p>
     * The token can be:
     * <ul>
     *   <li>a DMS string, such as "1122N03344E", in which case it will return default country
     *   <li>an aerodrome name that matches the aerordomes table in ABMS DB, return country by aerodrome prefix
     *   <li>an aerodrome name/code that matches the unespecified locations table in ABMS DB, return country for the unsp.location
     *   <li>an aerodrome code that matches some record in NAVDB, return country by aerodrome prefix
     * </ul>
     * In all other cases, returns the specified default value
     */
    public Boolean isRegionalUnspecifiedLocation (final String location, final String dflt) {
        Boolean result = false;
    	if (location != null) {
            final String loc = location.trim();
            if (!loc.isEmpty() && loc.length() >2) {

                // If it's a coordinate and it is not in unspecified locations table, just return default value
                Coordinate coord = GeometryUtils.parseAviationCoordinate (loc);
                if (coord != null && dflt!=null && dflt.length() >2) {

                	 // Look for it in unspecified locations, use it
                    final UnspecifiedDepartureDestinationLocation unspecLoc = this.flightMovementAerodromeService.findUnspecifiedDepartureDestinationLocation(loc);
                    if (unspecLoc != null && unspecLoc.getCountryCode() != null) {
                        return  regionalCountryService.getOne(unspecLoc.getCountryCode().getId()) != null;
                    } else { // return default
                    	Country c = countryService.findCountryByCountryCode(dflt);
                    	return  regionalCountryService.getOne(c.getId()) != null;
                    }
                }

                // If it's an airport from ABMS database, use its prefix
                final Aerodrome aerodrome = this.flightMovementAerodromeService.findAeroDromeByAeroDromeName (loc);
                if (aerodrome != null && aerodrome.getAerodromeName() != null && aerodrome.getAerodromeName().length() >2) {
                	if(this.isInSouthSudan(loc)) {
                		return true;
                	} else {
                		return isPrefixRegional(aerodrome.getAerodromeName().substring(0, 2));
                	}
                }

                // Look for it in unspecified locations, use it
                final UnspecifiedDepartureDestinationLocation unspecLoc = this.flightMovementAerodromeService.findUnspecifiedDepartureDestinationLocation(loc);
                if (unspecLoc != null && unspecLoc.getCountryCode() != null) {
                   return  regionalCountryService.getOne(unspecLoc.getCountryCode().getId()) != null;
                }

                // Otherwise, look for it in NAVDB
                if(flightMovementAerodromeService.isIcaoAerodrome(loc)) {
                	if(this.isInSouthSudan(loc)) {
                		return  true;
                	} else {
                		return  isPrefixRegional(loc.substring(0, 2));
                	}
                }

            }
        }
        LOG.debug("No coordinates for {} aerodrome found", location);
        return result;
    }

    private Boolean isPrefixRegional(String prefix) {
    	boolean result = false;

    	final List<Country> countries = countryService.findCountryByAerodromePrefix(prefix);
    	if (CollectionUtils.isNotEmpty(countries)) {
    		for (final Country c : countries) {
    			result = regionalCountryService.getOne(c.getId()) != null;
    			if (result) {
    				break;
    			}
    		}
    	}
    	return result;
    }

    private Boolean isInSouthSudan(String aerodrome) {
    	Boolean result  = false;

    	if (aerodrome != null && aerodrome.length() > 2
            && systemConfigurationService.getBillingOrgCode() == BillingOrgCode.KCAA
            && aerodrome.substring(0,2).equalsIgnoreCase("HS")
        ) {
            result = flightMovementAerodromeService.isAdInsideSouthSudan(aerodrome);
        }

    	return result;
    }

    private Boolean isRegionalLocation(String ad, String item18, boolean checkAfil) {
    	Boolean result = false;
    	if(ad != null && !ad.isEmpty()) {

    		// if ad is unspecified location defined as ZZZZ or AFIL (only for departure aerodrome) - use item18
    		if (ad.equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_ZZZZ) || (checkAfil && ad.equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_AFIL))) {
    			if (item18 != null && !item18.isEmpty()) {
    				String dflt = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.ANSP_COUNTRY_CODE);
    				result = this.isRegionalUnspecifiedLocation(item18, dflt);
    			}
    		} else {
    			result = this.isRegionalAerodrome(ad);
    		}
    	}
    	return result;
    }

    public void validateFlightLevel(final FlightMovement flightMovement) {
        if (flightMovement.getFlightLevel() != null) {
            final String fl = flightMovement.getFlightLevel().trim();
            if (!fl.isEmpty()) {
                if (!isValidLevelInFA(fl) && !isValidLevelInSM(fl) && !isValidUncontrolledVFRFlight(fl)) {
                    final ErrorVariables errorVariables = new ErrorVariables();
                    errorVariables.addEntry("flightLevel", fl);
                    errorVariables.addEntry("flightId", flightMovement.getFlightId());
                    errorVariables.addEntry("dateOfFlight", String.valueOf(flightMovement.getDateOfFlight()));
                    errorVariables.addEntry("depAd", flightMovement.getDepAd());
                    errorVariables.addEntry("destAd", flightMovement.getDestAd());
                    new ErrorDTO.Builder()
                        .setErrorMessage(ErrorConstants.ERR_INVALID_FLIGHT_LEVEL)
                        .appendDetails(
                            "Flight level: {{flightLevel}}, Flight ID: {{flightId}}, date: {{dateOfFlight}}, dep. ad.: {{depAd}}, dest. ad.: {{destAd}}"
                        )
                        .setDetailMessageVariables(errorVariables)
                        .throwInvalidDataException();
                }
                flightMovement.setFlightLevel(fl);
            }
        }
    }

    private boolean  isValidLevelInFA (final String flightLevel) {
        return flightLevel.length() == 4
            && Character.isDigit(flightLevel.charAt(1))
            && Character.isDigit(flightLevel.charAt(2))
            && Character.isDigit(flightLevel.charAt(3))
            && (flightLevel.charAt(0) == 'F' || flightLevel.charAt(0) == 'A');
    }

    private boolean isValidLevelInSM (final String flightLevel) {
        return (flightLevel.length() == 5
            && Character.isDigit(flightLevel.charAt(1))
            && Character.isDigit(flightLevel.charAt(2))
            && Character.isDigit(flightLevel.charAt(3))
            && Character.isDigit(flightLevel.charAt(4))
            && (flightLevel.charAt(0) == 'S' || flightLevel.charAt(0) == 'M'));
    }

    // ICAO Doc. 4444 exctract:
    // ...  for uncontrolled VFR flights, the letters "VFR"
    private boolean isValidUncontrolledVFRFlight(final String flightLevel) {
        String vfr = "VFR";

        return flightLevel.equalsIgnoreCase(vfr);
    }

    public static void validateFplObject (final FplObjectDto fplObject) {
        if(fplObject == null)
            throw new IllegalArgumentException("Invalid fplObject " );

        if (StringUtils.isBlank(fplObject.getFlightId()) || fplObject.getDayOfFlight() == null
            || StringUtils.isBlank(fplObject.getDepartureTime()) || StringUtils.isBlank(fplObject.getDepartureAd())) {
            final ErrorDTO.Builder errorBuilder = new ErrorDTO.Builder("Invalid FPL Object #");
            if (fplObject.getCatalogueFplObjectId() != null) {
                errorBuilder.setErrorMessage(fplObject.getCatalogueFplObjectId());
            }
            errorBuilder.addRejectedReason(RejectedReasons.VALIDATION_ERROR);

            if (StringUtils.isBlank(fplObject.getFlightId())) {
                errorBuilder.setErrorMessage(", ").setErrorMessage("missing flight ID");
            }
            if (fplObject.getDayOfFlight() == null) {
                errorBuilder.setErrorMessage(", ").setErrorMessage("missing day of flight");
            }
            if (StringUtils.isBlank(fplObject.getDepartureTime())) {
                errorBuilder.setErrorMessage(", ").setErrorMessage("missing departure time");
            }
            if (StringUtils.isBlank(fplObject.getDepartureAd())) {
                errorBuilder.setErrorMessage(", ").setErrorMessage("missing departure aerodrome");
            }
            throw errorBuilder.buildRejectedException();
        }
    }

    public static void validateRadarSummary (final RadarSummary radarSummary) {
        if(radarSummary == null)
            throw new IllegalArgumentException("Invalid radarSummary ");

        String errorMessage = null;

        if (org.apache.commons.lang.StringUtils.isBlank(radarSummary.getFlightIdentifier())) {
            errorMessage = "The flight identifier is not specified. ";
        } else if (radarSummary.getDate() == null) {
            errorMessage = "The date of flight is not specified. ";
        } else if (org.apache.commons.lang.StringUtils.isBlank(radarSummary.getDepartureAeroDrome())) {
            errorMessage = "The departure aerodrome is not specified. ";
        } else if (org.apache.commons.lang.StringUtils.isBlank(radarSummary.getDestinationAeroDrome())) {
            errorMessage = "The destination aerodrome is not specified. ";
        }

        if (errorMessage != null) {
            final ErrorVariables errorVariables = new ErrorVariables();

            errorVariables.addEntry("flightIdentifier", radarSummary.getFlightIdentifier());
            errorVariables.addEntry("date", radarSummary.getDate() != null ? radarSummary.getDate().toLocalDate().toString() : "null");
            errorVariables.addEntry("depAd", radarSummary.getDepartureAeroDrome());
            errorVariables.addEntry("destAd", radarSummary.getDestinationAeroDrome());

            throw new ErrorDTO.Builder(errorMessage)
                .appendDetails(
                    "Flight ID: {{flightIdentifier}}, date: {{date}}, dep. ad.: {{depAd}}, dest. ad.: {{destAd}}"
                )
                .setErrorMessageVariables(errorVariables)
                .addRejectedReason(RejectedReasons.VALIDATION_ERROR)
                .buildRejectedException();
        }
    }

    /**
     * Validate if currency has valid exchange rate for a given datetime.
     *
     * @param currency currency to validate
     * @param localDateTime date and time to validate currency against
     * @return is exchange rate valid
     */
    private Boolean validateExchangeRate (final Currency currency, LocalDateTime localDateTime) {
        CurrencyExchangeRate currencyExchangeRate = currencyExchangeRateService.getApplicableRateToUsd(
            currency, localDateTime);
        return currencyExchangeRate != null && currencyExchangeRate.getExchangeRate() != null &&
            currencyExchangeRate.getExchangeRate() >= 0d;
    }

    /**
     * Check if an attribute is required for a flight to be valid
     *
     * @param itemName name of the system configuration item to check
     * @return boolean representing if it is required
     */
    private boolean getIsRequired(String itemName) {
        SystemConfiguration config = systemConfigurationService.getOneByItemName(itemName);
        return config != null &&
            config.getCurrentValue() != null &&
            config.getCurrentValue().equalsIgnoreCase(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE);
    }

    /**
     * Determine if flight movement billable distance should be required. Return false if exempt,
     * system configured to ignore fixed cost flights, or valid route but does not cross FIR.
     *
     * KCAA also ignores small aircraft and training flight crossing distances.
     *
     * See user story 114729 details for ignoring if valid route.
     */
    private boolean isFlightRequireBillableRoute(FlightMovement flightMovement) {
        Preconditions.checkArgument(flightMovement != null);

        // not required if flight movement distance is flagged as exempt
        if (flightMovement.isExemptFlightDistance()) {
            return false;
        }

        // KCAA ONLY: not required if small aircraft and training flights
        boolean isKcaaOrg = systemConfigurationService.getBillingOrgCode() == BillingOrgCode.KCAA;
        if (isKcaaOrg && (
            kcaaFlightUtility.isSmallAircraft(flightMovement) || kcaaFlightUtility.isTrainingFlight(flightMovement)
        )) {
    		return false;
    	}

        // not required if flight has a flat rate for en-route charges and allowed in system configuration
        boolean allowFlatRateZeroLength = systemConfigurationService.getBoolean(SystemConfigurationItemName.IGNORE_ZERO_LENGTH_ROUTE);
    	if (allowFlatRateZeroLength && isFlatRate(flightMovement)) {
            return false;
        }
        // 2020-05-08 US 115719 - KCAA airports (HKLK) are outside of the Keniya FIR
        // If airport is domestic and it is departure or arrival, ignore zero billable track
        if (isKcaaOrg &&
               ( flightMovement.getFlightCategoryType().equals(FlightmovementCategoryType.ARRIVAL) ||
                flightMovement.getFlightCategoryType().equals(FlightmovementCategoryType.DEPARTURE) &&
                    (flightMovementAerodromeService.isAdDomesticButOutsideAirspace(flightMovement.getDepAd()) ||
                     flightMovementAerodromeService.isAdDomesticButOutsideAirspace(flightMovement.getDestAd())
                    )
               )
            ){
            return false;
        }
                    
                
                

    	
    	// 2019-11-22 US 115354 - fplRouteGeom should not be used the detrmine if the billable route is zero
    	// US 114729: only required if no valid routes were parsed from sources
        return flightMovement.getAtcLogTrack() == null && flightMovement.getBillableRoute() == null
            && flightMovement.getRadarRoute() == null && flightMovement.getTowerLogTrack() == null;
    }

    /**
     * Retrieve EnrouteFormula from DB based on FlightmovementType, FlightmovementScope and upperLimit
     *
     * @param fm FlightMovement
     */
    private Boolean isFlatRate(FlightMovement fm) {
        Preconditions.checkArgument(fm != null, "fm cannot be null");

        Double upperLimit = null;
        if (fm.getActualMtow() != null && fm.getFlightCategoryScope() != null) {
            AverageMtowFactor amf = averageMtowFactorService.findAverageMtowFactorByUpperLimitAndScope(fm.getActualMtow(), fm.getFlightCategoryScope());
            upperLimit = amf != null ? amf.getUpperLimit():fm.getActualMtow();
        }

        if (upperLimit == null) return false;

        // find enroute air navigation charge formula for upper limit and flight movement category
        final Integer categoryId = fm.getFlightmovementCategory() == null ? null
            : fm.getFlightmovementCategory().getId();
        final EnrouteAirNavigationChargeFormula formulaEntity = enrouteAirNavigationChargeFormulaService
            .findByMtowAndFlightCategory(upperLimit, categoryId);

        // validate that enroute charge formula record found for upper limit and category
        if (formulaEntity == null) {
            LOG.warn("Enroute air navigation charge formula not found for mtowUpperLimit={}, flightMovementCategoryId={}",
                upperLimit, categoryId);
            return false;
        }

        // check if enroute formula contains any variables and thus not a flat rate
        String enrouteFormula = formulaEntity.getFormula() == null ? null : formulaEntity.getFormula().trim();
        return enrouteFormula != null && enrouteFormula.matches("[^a-zA-Z\\[\\]]");
    }

    /**
     * This method check the scope of flight movement
     * Scopes are described as following:
     * DO - DOMESTIC, both aerodromes are within ANSP country
     * RE - REGIONAL, both aerodromes are within one region
     * IN - INTERNATIONAL, both aerodromes are international
     *
     * @return scope of flight movement
     */
    private FlightmovementCategoryScope validateFlightmovementCategoryScope(FlightMovement flightMovement) {
    	FlightmovementCategoryScope result = FlightmovementCategoryScope.INTERNATIONAL;

    	/*
         * If flight type is domestic, it's scope is always domestic
         * If flight type is other, it means it doesn't cross the country border and
         * the scope is international
         */
    	if(flightMovement == null || flightMovement.getFlightCategoryType() == null) {
    		return result;
    	}

        if(flightMovement.getFlightCategoryType().equals(FlightmovementCategoryType.DOMESTIC)) {
            	result = FlightmovementCategoryScope.DOMESTIC;
        }

        /* Find flight scope: RE or IN - regional or international
         * It's a regional flight only if the below conditions are meet
         * ------------------------------------------------------------
         * 1. The flight is an international flight (i.e. overflight, intl. departure or intl. arrival)
         * 2. The aircraft is registered in a country listed in the regional countries table.
         *    This is determined by matching the aircraft’s registration number prefix with the registration number
         *    prefix of a country that is listed in the regional countries table.
         * 3. The flight originates from and terminates in countries listed in the regional countries table.
         */

         /* Step 1  */
         if (flightMovement.getFlightCategoryType().equals(FlightmovementCategoryType.DEPARTURE)
             || flightMovement.getFlightCategoryType().equals(FlightmovementCategoryType.ARRIVAL)
             || flightMovement.getFlightCategoryType().equals(FlightmovementCategoryType.OVERFLIGHT)) {

             	/* Step 2 */

        	 Country aircraftCountry = findRegistrationCountry(flightMovement);
        	 if (aircraftCountry == null) {
        	 	/* aircraft registration is unknown. Nationality is decided to be foreign */
        	 	LOG.warn("Aircraft registration number {} doesn't have any country associated",flightMovement.getItem18RegNum());
        	 } else if (regionalCountryService.getOne(aircraftCountry.getId()) != null || isLocalKcaaRegistered(flightMovement)) {

        	 	/* Step 3 */
        		if (isRegionalLocation(flightMovement.getDepAd(), flightMovement.getItem18Dep(), true)
                    && isRegionalLocation(flightMovement.getDestAd(), flightMovement.getItem18Dest(), false)) {
        			result = FlightmovementCategoryScope.REGIONAL;
        			LOG.debug("Because the departure {} and the destination {} aerodromes are regional, the FLM {} is a REG_OVERFLIGHT", flightMovement.getDepAd(),
       					flightMovement.getDestAd(), flightMovement.getId());
        		} else {
        			LOG.debug("Cannot check if the flight {} is a regional flight because the aircraft registration number is null", flightMovement);
        		}
         	}
       	}
       	return result;
    }



    /**
     * If it is KCAA, check to see if the aircraft registration
     * isLocal flag is set to true. This indicates a regional flight.
     *
     * @param flightMovement
     * @return boolean
     */
    private boolean isLocalKcaaRegistered(FlightMovement flightMovement) {
        final boolean isKcaaOrg = systemConfigurationService.getBillingOrgCode() == BillingOrgCode.KCAA;
        final String aircraftRegNum = flightMovement.getItem18RegNum();

        if (isKcaaOrg && aircraftRegNum != null && !aircraftRegNum.isEmpty()) {
            AircraftRegistration registration = aircraftRegistrationService
                .findAircraftRegistrationByRegistrationNumber(aircraftRegNum, flightMovement.getDateOfFlight());
            if (registration != null && registration.getIsLocal()) {
                return true;
            }
        }
        return false;
    }

	/**
     * This method check the nationality of flight movement
     * Scopes are described as following:
     * NA - NATIONAL - aircraft registered in the ANSP country
     * FO - FOREIGN - aircraft is registered outside of ANSP country
     *
     * @return scope of flight movement
     */
    private FlightmovementCategoryNationality validateFlightmovementCategoryNationality(FlightMovement flightMovement) {
    	FlightmovementCategoryNationality result = FlightmovementCategoryNationality.FOREIGN;
    	Country aircraftCountry = findRegistrationCountry(flightMovement);
    	if(aircraftCountry != null) {
    		// compare with ANSP country from system configuration
    		String dflt = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.ANSP_COUNTRY_CODE);
    		if(aircraftCountry.getCountryCode().equalsIgnoreCase(dflt)) {
    			result = FlightmovementCategoryNationality.NATIONAL;
    		}
    	}
    	return result;
    }

    private Country findRegistrationCountry(FlightMovement flightMovement) {
    	Country aircraftCountry = null;
    	if(flightMovement != null) {
    		final String aircraftRegNum = flightMovement.getItem18RegNum();
    		if (aircraftRegNum != null && !aircraftRegNum.isEmpty()) {

    			final AircraftRegistration aircraftRegistration = aircraftRegistrationService
    					.findAircraftRegistrationByRegistrationNumber(aircraftRegNum, flightMovement.getDateOfFlight());
    			if (aircraftRegistration != null) {
             	  aircraftCountry = aircraftRegistration.getCountryOfRegistration();
    			} else {
    				LOG.debug("Not found in the DB the aircraft registration {}. We'll try to get the country by the prefix.", aircraftRegNum);
    			}
    			if (aircraftCountry == null) {
    				aircraftCountry = countryService.findCountryByPrefix(aircraftRegNum);
    			}
    		}
    	}
    	return aircraftCountry;
    }

	/**
     * This method check the type of flight movement
     * Types are described as following:
     * DO - DOMESTIC, both aerodromes are within ANSP country
     * AR - ARRIVAL, only arrival aerodrome is within ANSP country
     * DE - DEPARTURE, only departure aerodrome is within ANSP country
     * OV - OVERFLIGHT, both aerodroems are outside ANSP country, but the route crosses the country borders
     * OT - OTHER, both aerodromes are outside ANSP country and the route doesn't cross the borders
     *
     * @return the type of flight movement
     */
    public FlightmovementCategoryType validateFlightmovementCategoryType(FlightMovement flightMovement) {

    	FlightmovementCategoryType result = FlightmovementCategoryType.OTHER;

        if (flightMovement != null) {

            // resolve departure and destination aerodrome from flight movement fields
            String depAd = resolveDepartureAerodrome(flightMovement);
            String destAd = resolveDestinationAerodrome(flightMovement);

            // calculate departure and destination aerodrome coordinates
            Boolean isDepCoord = flightMovementAerodromeService.isAerodromeDomestic(depAd, flightMovement.getDepAd(), true);
            Boolean isDestCoord = flightMovementAerodromeService.isAerodromeDomestic(destAd, flightMovement.getDestAd());

            if(isDepCoord && isDestCoord){
                result = FlightmovementCategoryType.DOMESTIC;
            } else if(isDepCoord){
            	result = FlightmovementCategoryType.DEPARTURE;
            } else if(isDestCoord) {
            	result = FlightmovementCategoryType.ARRIVAL;
            } else {
                result = FlightmovementCategoryType.OTHER;
                if ((flightMovement.getFplCrossingDistance() != null && flightMovement.getFplCrossingDistance() > 0.0) ||
                        (flightMovement.getAtcCrossingDistance() != null && flightMovement.getAtcCrossingDistance() > 0.0) ||
                        (flightMovement.getTowerCrossingDistance() != null && flightMovement.getTowerCrossingDistance() > 0.0) ||
                        (flightMovement.getRadarCrossingDistance() != null && flightMovement.getRadarCrossingDistance() > 0.0)) {
                    result = FlightmovementCategoryType.OVERFLIGHT;
                }
            }
        }
        return result;
    }

    private Boolean isParkingScheduleExist(FlightMovement fm) {
        AerodromeCategory aerodromeCategory = flightMovementAerodromeService.resolveLocationToAdCategory(
                fm.getDepAd(), fm.getItem18Dep(), AdResolveType.AD_TYPE_DEPARTURE);

        boolean result = false;
        if (aerodromeCategory != null && aerodromeCategory.getLdpBillingFormulas() != null) {
            for (LdpBillingFormula ldpFormula : aerodromeCategory.getLdpBillingFormulas()) {
                if (ldpFormula.getChargesType() != null &&
                    ldpFormula.getChargesType().equals(LdpBillingFormulaChargeType.parking_charges)) {
                     result = true;
                }
            }
        }
        return result;
    }

    /**
     * Return departure aerodrome from flight movement depAd or item18Dep fields.
     */
    private String resolveDepartureAerodrome(final FlightMovement flightMovement) {
        String depAdFromFlightMovement = flightMovement.getDepAd();

        if (StringUtils.isBlank(depAdFromFlightMovement)) {
            return null;
        }

        String item18Dep = flightMovement.getItem18Dep();

        if (depAdFromFlightMovement.equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_ZZZZ)){
            return StringUtils.isNotBlank(item18Dep) ? item18Dep : ApplicationConstants.PLACEHOLDER_ZZZZ;
        } else if (depAdFromFlightMovement.equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_AFIL)) {
            return StringUtils.isNotBlank(item18Dep) ? item18Dep : ApplicationConstants.PLACEHOLDER_AFIL;
        } else {
            return depAdFromFlightMovement;
        }
    }

    /**
     * Return destination aerodrome from flight movement destAd or item18Dest fields.
     */
    private String resolveDestinationAerodrome(final FlightMovement flightMovement) {
        String destAdFromFlightMovement = flightMovement.getDestAd();

        if (StringUtils.isBlank(destAdFromFlightMovement)) {
            return null;
        }

        if (destAdFromFlightMovement.equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_ZZZZ)){
            String item18Dest = flightMovement.getItem18Dest();
            return StringUtils.isNotBlank(item18Dest) ? item18Dest : ApplicationConstants.PLACEHOLDER_ZZZZ;
        } else {
            return destAdFromFlightMovement;
        }
    }

    /**
     * If the flight is an international arrival, the radar route log should be checked for validity.
     * In some cases, specifically in Kenya, there is only one point in the radar log. This point is very
     * close to the destination aerodrome. The path that is derived is not valid for billing.
     *
     * @param radarRouteLen is the RouteCacheVO dervied from the radar route text
     */
    public Double getRadarRouteLengthValid(Double radarRouteLen) {
        // Exit early if radar length is over minimum length
        Double result = 0.0;
        Double routeLen = systemConfigurationService.getDouble(SystemConfigurationItemName.MINIMUM_RADAR_ROUTE_LENGTH, null);
        if (radarRouteLen == null || radarRouteLen > routeLen) {
            result = radarRouteLen;
        } else {
            LOG.info("Route from radar summary is invalid: DISTANCE TOO SHORT!");
        }
        return result;
    }
    
    public Boolean isValidateFlightLevel() {
        return systemConfigurationService.getBoolean(SystemConfigurationItemName.FPL_VALIDATE_CRONOS_FLIGHT_LEVEL);
    }
    
    public Boolean isValidateFlightLevelAirspace() {
        return systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE);
    }
 
    public boolean isUnifiedTaxFlightMovement(FlightMovement flightMovement) {
        // get aircraft registration number from item18RegNum
    	String item18RegNum = flightMovementBuilderUtility.checkAircraftRegistrationNumber(flightMovement);
                
        if (item18RegNum != null) {

            Double aMtow = null;

            AircraftRegistration ar = aircraftRegistrationService.findAircraftRegistrationByRegNumber(item18RegNum);
            if (ar != null) {
            	// SMALL_AIRCRAFT_MAX_WEIGHT and SMALL_AIRCRAFT_MIN_WEIGHT are expressed in KG
                Integer maxWeight = systemConfigurationService.getIntOrZero(SystemConfigurationItemName.SMALL_AIRCRAFT_MAX_WEIGHT);
                Integer minWeight = systemConfigurationService.getIntOrZero(SystemConfigurationItemName.SMALL_AIRCRAFT_MIN_WEIGHT);
                
                // MTOW stored in small tones in the DB ==> need to convert it to KG
            	aMtow = ar.getMtowOverride()* ReportHelper.TO_KG;

                if (aMtow >= minWeight && aMtow <= maxWeight) {
                	
                	// Foreign aircraft with the "Local" flag set at FALSE must be skipped
                	if  (!aircraftRegistrationService.isaForeignAircraft(ar) || ar.getIsLocal())
                		return true;
                }
            }
        }
        return false;
    }

    private AircraftRegistration getAircraftRegistration(FlightMovement flightMovement) {

    	AircraftRegistration ar = null;
    	
    	// get aircraft registration number from item18RegNum
    	String item18RegNum = flightMovementBuilderUtility.checkAircraftRegistrationNumber(flightMovement);
                
        if (item18RegNum != null) {
            ar = aircraftRegistrationService.findAircraftRegistrationByRegNumber(item18RegNum);
        }
        
        return ar;
    }
    
    private boolean checkUnifiedTaxInvoiced(FlightMovement flightMovement) {
    	
    	AircraftRegistration ar = getAircraftRegistration(flightMovement);
    	
    	if (ar != null) {
	    	LocalDateTime coaIssueDate = ar.getCoaIssueDate();
	    	LocalDateTime coaExpiryDate = ar.getCoaExpiryDate();
	    	if (coaIssueDate == null || coaExpiryDate == null)
	    		return false;
	    	
	    	LocalDateTime flightDate = flightMovement.getDateOfFlight();
	    	if (flightDate.isBefore(coaIssueDate) || flightDate.isAfter(coaExpiryDate))
	    		return false;
	    	
	    	return true;
    	}
            
    	return false;
    }
    
    private void setFlightStatusIfUnifiedTaxFlight(FlightMovement flightMovement) {
        
    	AircraftRegistration ar = getAircraftRegistration(flightMovement);    	
        if (ar != null) {
        	
        	// SMALL_AIRCRAFT_MAX_WEIGHT is expressed in KG
            Integer maxWeight = systemConfigurationService.getIntOrZero(SystemConfigurationItemName.SMALL_AIRCRAFT_MAX_WEIGHT);
            
            // MTOW stored in small tones in the DB ==> need to convert it to KG
        	Double aMtow = ar.getMtowOverride()* ReportHelper.TO_KG;

            if (aMtow <= maxWeight) {
            	
            	boolean isDomesticOrLocal = (ar.getIsLocal()) || 
            		(flightMovement.getFlightCategoryNationality().equals(FlightmovementCategoryNationality.NATIONAL));
                
            	if (isDomesticOrLocal) {
            		boolean invoiced = checkUnifiedTaxInvoiced(flightMovement);
            		
            		if (invoiced) {        
                    	// Unified Tax è pagata per l'anno in corso
                        flightMovement.setStatus(FlightMovementStatus.INVOICED);
                        flightMovement.setFlightNotes("");
                    } else {
                        // Unified Tax non è pagata per l'anno in corso
                        flightMovement.setStatus(FlightMovementStatus.INCOMPLETE);
                        flightMovement.setFlightNotes("Unified Tax not payed for the current year");
                    }
                }
            }
        }
    }
    
}
