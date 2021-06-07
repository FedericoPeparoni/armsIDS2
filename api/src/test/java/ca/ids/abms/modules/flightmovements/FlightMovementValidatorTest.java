package ca.ids.abms.modules.flightmovements;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.aerodromes.AerodromeCategory;
import ca.ids.abms.modules.aircraft.AircraftRegistrationService;
import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.countries.CountryService;
import ca.ids.abms.modules.countries.RegionalCountry;
import ca.ids.abms.modules.countries.RegionalCountryService;
import ca.ids.abms.modules.currencies.CurrencyExchangeRateService;
import ca.ids.abms.modules.flightmovements.category.FlightmovementCategory;
import ca.ids.abms.modules.flightmovements.category.FlightmovementCategoryService;
import ca.ids.abms.modules.flightmovements.enumerate.AdResolveType;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryScope;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryType;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementValidator;
import ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementValidatorIssue;
import ca.ids.abms.modules.flightmovementsbuilder.utility.FlightMovementBuilderUtility;
import ca.ids.abms.modules.flightmovementsbuilder.utility.KCAAFlightUtility;
import ca.ids.abms.modules.formulas.enroute.EnrouteAirNavigationChargeFormulaService;
import ca.ids.abms.modules.formulas.ldp.LdpBillingFormula;
import ca.ids.abms.modules.mtow.AverageMtowFactorService;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;

import ca.ids.abms.modules.util.models.CurrencyUtils;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.any;

public class FlightMovementValidatorTest {

    private FlightMovementAerodromeService flightMovementAerodromeService;
    private SystemConfigurationService systemConfigurationService;
    private FlightMovementBuilderUtility flightMovementBuilderUtility;
    private FlightMovementValidator flightMovementValidator;
    private FlightmovementCategoryService flightmovementCategoryService;
    private CountryService countryService;
    private RegionalCountryService regionalCountryService;

    @Before
    public void setup() {
        CurrencyExchangeRateService currencyExchangeRateService = mock(CurrencyExchangeRateService.class);
        CurrencyUtils currencyUtils = mock(CurrencyUtils.class);
        AircraftRegistrationService aircraftRegistrationService = mock(AircraftRegistrationService.class);

        KCAAFlightUtility kcaaFlightUtility = mock(KCAAFlightUtility.class);
        EnrouteAirNavigationChargeFormulaService enrouteAirNavigationChargeFormulaService = mock(EnrouteAirNavigationChargeFormulaService.class);
        AverageMtowFactorService averageMtowFactorService = mock(AverageMtowFactorService.class);

        systemConfigurationService = mock(SystemConfigurationService.class);
        flightMovementAerodromeService = mock(FlightMovementAerodromeService.class);
        flightMovementBuilderUtility = mock(FlightMovementBuilderUtility.class);
        flightmovementCategoryService = mock(FlightmovementCategoryService.class);
        countryService = mock(CountryService.class);
        regionalCountryService = mock(RegionalCountryService.class);

        flightMovementValidator = new FlightMovementValidator(systemConfigurationService, flightMovementAerodromeService,
            currencyExchangeRateService, flightMovementBuilderUtility, currencyUtils, countryService,
            regionalCountryService, aircraftRegistrationService, kcaaFlightUtility,
            enrouteAirNavigationChargeFormulaService, averageMtowFactorService, flightmovementCategoryService, null);
    }

    @Test
    public void test_validateFlightMovementStatus() {

        final FlightMovement fm = new FlightMovement();

        // FM is NOT NULL
        fm.setFlightCategoryType(FlightmovementCategoryType.OTHER);

        FlightMovementValidationViewModel fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        Assert.assertTrue(fvm != null && fvm.getStatus().equals(FlightMovementStatus.OTHER));

        FlightmovementCategory fmc = new FlightmovementCategory();
        fmc.setName("DOMESTIC");
        fmc.setId(1);

        // empty FM
        fm.setFlightCategoryType(FlightmovementCategoryType.DOMESTIC);
        fm.setFlightmovementCategory(fmc);

        when(flightmovementCategoryService.findCategoryByParameters(any(), any(), any())).thenReturn(fmc);
        SystemConfiguration sc = new SystemConfiguration();
        sc.setCurrentValue(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE);
        SystemConfiguration scf = new SystemConfiguration();
        scf.setCurrentValue("f");
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.PASSENGER_CHARGES_SUPPORT)).thenReturn(sc);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.IGNORE_ZERO_LENGTH_ROUTE)).thenReturn(scf);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertTrue(fvm.getIssues().contains(FlightMovementValidatorIssue.NO_ASSOCIATED_ACCOUNT));
        Assert.assertTrue(fvm.getIssues().contains(FlightMovementValidatorIssue.MISSING_MTOW));
        Assert.assertTrue(fvm.getIssues().contains(FlightMovementValidatorIssue.ZERO_LENGTH_BILLABLE_TRACK));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.INCOMPLETE));

     // FM with no issues
        fm.setAccount(new Account());
        fm.setActualMtow(1.0);
        fm.setBillableCrossingDist(1.0);
        fm.setAircraftType("TYPE");
        fm.setParkingTime(1.0);
        fm.setParkingCharges(1.0);
        fm.setPassengersChargeableDomestic(1);

        when(flightMovementBuilderUtility.checkAircraftType(fm)).thenReturn("TYPE");
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        // If the following condition fails, please fill properly the FM to pass the test and make sure
        Assert.assertTrue(fvm.getIssues() != null && fvm.getIssues().size() == 0);
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));

        // Aircraft type is unknown
        when(flightMovementBuilderUtility.checkAircraftType(fm)).thenReturn(null);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertTrue(fvm.getIssues().contains(FlightMovementValidatorIssue.UNKNOWN_AIRCRAFT_TYPE));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.INCOMPLETE));

        when(flightMovementBuilderUtility.checkAircraftType(fm)).thenReturn("TYPE");

        //Radar summary is missing
        sc = new SystemConfiguration();
        sc.setCurrentValue(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE);
        SystemConfiguration sc1 = new SystemConfiguration();
        sc1.setCurrentValue(null);
        fm.setFlightLevel("F330");
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.RADAR_SUMMARY_REQUIRED)).thenReturn(sc);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.RADAR_FLOOR_LEVEL)).thenReturn(sc1);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertTrue(fvm.getIssues().contains(FlightMovementValidatorIssue.RADAR_SUMMARY_MISSING));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.INCOMPLETE));

        sc1.setCurrentValue("10000");
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertTrue(fvm.getIssues().contains(FlightMovementValidatorIssue.RADAR_SUMMARY_MISSING));
        Assert.assertFalse(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));

        sc1.setCurrentValue("40000");
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.RADAR_SUMMARY_MISSING));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));

        //ATC log is missing for not delta flight
        sc = new SystemConfiguration();
        sc.setCurrentValue(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE);
        fm.setDeltaFlight(Boolean.FALSE);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.ATC_LOG_REQUIRED)).thenReturn(sc);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertTrue(fvm.getIssues().contains(FlightMovementValidatorIssue.ATC_LOG_MISSING));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.INCOMPLETE));

        // ATC log is missing for  delta flight
        sc = new SystemConfiguration();
        sc.setCurrentValue(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE);
        fm.setDeltaFlight(Boolean.TRUE);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.ATC_LOG_REQUIRED)).thenReturn(sc);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.ATC_LOG_MISSING));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));

        // ATC log is missing for  circular flight
        fm.setDeltaFlight(Boolean.FALSE);
        fm.setDepAd("DEP_AD");
        fm.setDestAd("DEST_AD");
        fm.setItem18Dep("DEP_AD");
        fm.setItem18Dest("DEST_AD");

        when(flightMovementAerodromeService.resolveAnyLocationToDMS("DEP_AD")).thenReturn("DEP_AD");
        when(flightMovementAerodromeService.resolveAnyLocationToDMS("DEST_AD")).thenReturn("DEST_AD");

        try{
        when(flightMovementAerodromeService.isCircularRoute("DEP_AD", "DEP_AD","DEST_AD","DEST_AD")).thenReturn(true);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.ATC_LOG_MISSING));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));
        } catch (Exception ex){
            Assert.assertTrue(fvm != null && fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.INCOMPLETE));
        }

        // check Flight plan
        sc = new SystemConfiguration();
        sc.setCurrentValue(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE);
        fm.setInitialFplData(null);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.FLIGHT_PLAN_REQUIRED)).thenReturn(sc);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertTrue(fvm.getIssues().contains(FlightMovementValidatorIssue.FLIGHT_PLAN_MISSING));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.INCOMPLETE));

        // check aircraft type
        fm.setAircraftType(null);

        fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertTrue(fvm.getIssues().contains(FlightMovementValidatorIssue.UNKNOWN_AIRCRAFT_TYPE));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.INCOMPLETE));

        // check passenger fees
        fm.setAircraftType("TEST");
        fm.setInitialFplData("TEST FPL");
        fm.setPassengersChargeableDomestic(null);
        fm.setFlightCategoryType(FlightmovementCategoryType.DOMESTIC);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertTrue(fvm.getIssues().contains(FlightMovementValidatorIssue.MISSING_PASSENGER_DOMESTIC_DATA));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.INCOMPLETE));

        fm.setPassengersChargeableDomestic(10);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.MISSING_PASSENGER_DOMESTIC_DATA));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));

        fm.setPassengersChargeableIntern(null);
        fm.setFlightCategoryType(FlightmovementCategoryType.DEPARTURE);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertTrue(fvm.getIssues().contains(FlightMovementValidatorIssue.MISSING_PASSENGER_INTERNATIONAL_DATA));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.INCOMPLETE));

        fm.setFlightCategoryType(FlightmovementCategoryType.ARRIVAL);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.MISSING_PASSENGER_INTERNATIONAL_DATA));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));

        fm.setPassengersChargeableIntern(10);
        fm.setFlightCategoryType(FlightmovementCategoryType.DEPARTURE);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.MISSING_PASSENGER_INTERNATIONAL_DATA));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));

        //check parking time
        SystemConfiguration sc2 = new SystemConfiguration();
        sc2.setCurrentValue(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.PARKING_TIME_REQUIRED)).thenReturn(sc2);
        fm.setParkingTime(null);
        fm.setFlightCategoryType(FlightmovementCategoryType.DEPARTURE);

        AerodromeCategory aerodromeCategory = new AerodromeCategory();
        Set<LdpBillingFormula> lSet = new HashSet<>();
        lSet.add(new LdpBillingFormula(aerodromeCategory, "parking_charges"));
        aerodromeCategory.setLdpBillingFormulas(lSet);
        when(flightMovementAerodromeService.resolveLocationToAdCategory( fm.getDepAd(), fm.getItem18Dep(), AdResolveType.AD_TYPE_DEPARTURE)).thenReturn(aerodromeCategory);

        fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertTrue(fvm.getIssues().contains(FlightMovementValidatorIssue.MISSING_PARKING_TIME));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.INCOMPLETE));

        fm.setParkingTime(30.0);
        fm.setFlightCategoryType(FlightmovementCategoryType.DEPARTURE);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.MISSING_PARKING_TIME));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));

        // check service charge return
        fm.setFlightCategoryType(FlightmovementCategoryType.DEPARTURE);
        sc = new SystemConfiguration();
        fm.setPassengersChargeableIntern(0);
        fm.setPassengersChargeableDomestic(0);
        sc.setCurrentValue(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.PASSENGER_SERVICE_CHARGE_REQUIRED)).thenReturn(sc);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertTrue(fvm.getIssues().contains(FlightMovementValidatorIssue.MISSING_PASSENGER_SERVICE_CHARGE_RETURN));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.INCOMPLETE));

        fm.setPassengersChargeableIntern(10);
        fm.setPassengersChargeableDomestic(10);
        sc.setCurrentValue(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.PASSENGER_SERVICE_CHARGE_REQUIRED)).thenReturn(sc);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.MISSING_PASSENGER_SERVICE_CHARGE_RETURN));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));

        // Tower log required if it is NOT overflight
        fm.setFlightCategoryType(FlightmovementCategoryType.OVERFLIGHT);
        sc = new SystemConfiguration();
        sc.setCurrentValue(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.TOWER_LOG_REQUIRED)).thenReturn(sc);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.TOWER_LOG_MISSING));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));

        fm.setFlightCategoryType(FlightmovementCategoryType.DOMESTIC);

        fvm = flightMovementValidator.validateFlightMovementStatus(fm,false);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertTrue(fvm.getIssues().contains(FlightMovementValidatorIssue.TOWER_LOG_MISSING));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.INCOMPLETE));
    }

    @Test
    public void test_validateFlightMovementType() {
        final FlightMovement fm = new FlightMovement();
        fm.setAircraftType("ABC");
        fm.setDeltaFlight(Boolean.FALSE);

        when(flightMovementAerodromeService.resolveAnyLocationToDMS("DEP_AD")).thenReturn("DEP_AD");
        when(flightMovementAerodromeService.resolveAnyLocationToDMS("DEST_AD")).thenReturn("DEST_AD");

        // FM without aerodromes and flp crossing distance has type other
        FlightMovementValidationViewModel fvm = flightMovementValidator.validateFlightMovementCategory(fm);
        Assertions.assertThat(fvm.getFlightmovementType()).isEqualTo(FlightmovementCategoryType.OTHER);

        // FM with flp crossing distance but without aerodromes has type overflight
        fm.setFplCrossingDistance(1.0);
        fvm = flightMovementValidator.validateFlightMovementCategory(fm);
        Assertions.assertThat(fvm.getFlightmovementType()).isEqualTo(FlightmovementCategoryType.OVERFLIGHT);

        // FM without flp crossing distance and without aerodromes has type other
        fm.setFplCrossingDistance(null);
        fvm = flightMovementValidator.validateFlightMovementCategory(fm);
        Assertions.assertThat(fvm.getFlightmovementType()).isEqualTo(FlightmovementCategoryType.OTHER);

     // FM with aerodromes outside the region and crossing distance 0: OTHER
        fm.setDepAd("DEP_AD");
        fm.setDestAd("DEST_AD");
        fm.setFplCrossingDistance(0.0);
        when(flightMovementAerodromeService.isAerodromeDomestic("DEP_AD","DEP_AD", true)).thenReturn(Boolean.FALSE);
        when(flightMovementAerodromeService.isAerodromeDomestic("DEST_AD","DEST_AD")).thenReturn(Boolean.FALSE);
        fvm = flightMovementValidator.validateFlightMovementCategory(fm);
        Assertions.assertThat(fvm.getFlightmovementType()).isEqualTo(FlightmovementCategoryType.OTHER);

        // FM with aerodromes outside the region: Overflight
        fm.setDepAd("DEP_AD");
        fm.setDestAd("DEST_AD");
        fm.setFplCrossingDistance(1.0);
        when(flightMovementAerodromeService.isAerodromeDomestic("DEP_AD","DEP_AD", true)).thenReturn(Boolean.FALSE);
        when(flightMovementAerodromeService.isAerodromeDomestic("DEST_AD","DEST_AD")).thenReturn(Boolean.FALSE);
        fvm = flightMovementValidator.validateFlightMovementCategory(fm);
        Assertions.assertThat(fvm.getFlightmovementType()).isEqualTo(FlightmovementCategoryType.OVERFLIGHT);


        // Departure is in the region: International departure flight

        when(flightMovementAerodromeService.isAerodromeDomestic("DEP_AD","DEP_AD", true)).thenReturn(Boolean.TRUE);
        when(flightMovementAerodromeService.isAerodromeDomestic("DEST_AD","DEP_AD")).thenReturn(Boolean.FALSE);
        fvm = flightMovementValidator.validateFlightMovementCategory(fm);
        Assertions.assertThat(fvm.getFlightmovementType()).isEqualTo(FlightmovementCategoryType.DEPARTURE);
        Assertions.assertThat(fvm.getFlightmovementScope()).isEqualTo(FlightmovementCategoryScope.INTERNATIONAL);

        // Both of aerodromes are in the region: Domestic flight
        when(flightMovementAerodromeService.isAerodromeDomestic("DEP_AD","DEP_AD", true)).thenReturn(Boolean.TRUE);
        when(flightMovementAerodromeService.isAerodromeDomestic("DEST_AD","DEST_AD")).thenReturn(Boolean.TRUE);
        fvm = flightMovementValidator.validateFlightMovementCategory(fm);
        Assertions.assertThat(fvm.getFlightmovementType()).isEqualTo(FlightmovementCategoryType.DOMESTIC);
        Assertions.assertThat(fvm.getFlightmovementScope()).isEqualTo(FlightmovementCategoryScope.DOMESTIC);

        // Departure is not in the region: International arrival flight
        when(flightMovementAerodromeService.isAerodromeDomestic("DEP_AD","DEP_AD", true)).thenReturn(Boolean.FALSE);
        when(flightMovementAerodromeService.isAerodromeDomestic("DEST_AD","DEST_AD")).thenReturn(Boolean.TRUE);
        fvm = flightMovementValidator.validateFlightMovementCategory(fm);
        Assertions.assertThat(fvm.getFlightmovementType()).isEqualTo(FlightmovementCategoryType.ARRIVAL);
        Assertions.assertThat(fvm.getFlightmovementScope()).isEqualTo(FlightmovementCategoryScope.INTERNATIONAL);

        // Departure is in the region: Regional arrival flight
        fm.setItem18RegNum("ABCDE");

        Country aircraftCountry = new Country();
        aircraftCountry.setCountryCode("ABC");

        when(flightMovementAerodromeService.isAerodromeDomestic("DEP_AD", "DEP_AD", true)).thenReturn(Boolean.FALSE);
        when(flightMovementAerodromeService.isAerodromeDomestic("DEST_AD")).thenReturn(Boolean.TRUE);
        when(flightMovementAerodromeService.isAdInsideSouthSudan("DEP_AD")).thenReturn(Boolean.TRUE);
        when(countryService.findCountryByPrefix("ABCDE")).thenReturn(aircraftCountry);
        when(countryService.findCountryByAerodromePrefix("DE")).thenReturn(Collections.singletonList(aircraftCountry));
        when(regionalCountryService.getOne(anyInt())).thenReturn(new RegionalCountry());

        fvm = flightMovementValidator.validateFlightMovementCategory(fm);
        Assertions.assertThat(fvm.getFlightmovementType()).isEqualTo(FlightmovementCategoryType.ARRIVAL);
        Assertions.assertThat(fvm.getFlightmovementScope()).isEqualTo(FlightmovementCategoryScope.REGIONAL);
    }

    @Test
    public void test_validateFlightMovementStatusForInvoice() {
        final FlightMovement fm = new FlightMovement();

        // FM is NOT NULL
        fm.setFlightCategoryType(FlightmovementCategoryType.OTHER);
        FlightMovementValidationViewModel fvm = flightMovementValidator.validateFlightMovementStatus(fm,true);
        Assert.assertTrue(fvm != null && fvm.getStatus().equals(FlightMovementStatus.OTHER));

        SystemConfiguration scf = new SystemConfiguration();
        scf.setCurrentValue("f");
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.IGNORE_ZERO_LENGTH_ROUTE)).thenReturn(scf);
        // empty FM
        fm.setFlightCategoryType(FlightmovementCategoryType.DOMESTIC);
        FlightmovementCategory fmc = new FlightmovementCategory();
        fmc.setName("DOMESTIC");
        fmc.setId(1);

        fm.setFlightmovementCategory(fmc);

        when(flightmovementCategoryService.findCategoryByParameters(any(), any(), any())).thenReturn(fmc);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,true);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertTrue(fvm.getIssues().contains(FlightMovementValidatorIssue.NO_ASSOCIATED_ACCOUNT));
        Assert.assertTrue(fvm.getIssues().contains(FlightMovementValidatorIssue.MISSING_MTOW));
        Assert.assertTrue(fvm.getIssues().contains(FlightMovementValidatorIssue.ZERO_LENGTH_BILLABLE_TRACK));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.INCOMPLETE));

     // FM with no issues
        fm.setAccount(new Account());
        fm.setActualMtow(1.0);
        fm.setBillableCrossingDist(1.0);
        fm.setAircraftType("TYPE");
        fm.setParkingTime(1.0);
        fm.setParkingCharges(1.0);
        fm.setPassengersChargeableDomestic(1);
        when(flightMovementBuilderUtility.checkAircraftType(fm)).thenReturn("TYPE");

        fvm = flightMovementValidator.validateFlightMovementStatus(fm,true);
        // If the following condition fails, please fill properly the FM to pass the test and make sure
        Assert.assertTrue(fvm.getIssues() != null && fvm.getIssues().size() == 0);
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));

        //Radar summary is missing
        SystemConfiguration sc = new SystemConfiguration();
        sc.setCurrentValue(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE);
        SystemConfiguration sc1 = new SystemConfiguration();
        sc1.setCurrentValue(null);
        fm.setFlightLevel("F330");
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.RADAR_SUMMARY_REQUIRED)).thenReturn(sc);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.RADAR_FLOOR_LEVEL)).thenReturn(sc1);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,true);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.RADAR_SUMMARY_MISSING));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));

        sc1.setCurrentValue("90000");
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,true);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.RADAR_SUMMARY_MISSING));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));

        //ATC log is missing for not delta flight
        sc = new SystemConfiguration();
        sc.setCurrentValue(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE);
        fm.setDeltaFlight(Boolean.FALSE);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.ATC_LOG_REQUIRED)).thenReturn(sc);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,true);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.ATC_LOG_MISSING));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));

        // ATC log is missing for  delta flight
        sc = new SystemConfiguration();
        sc.setCurrentValue(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE);
        fm.setDeltaFlight(Boolean.TRUE);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.ATC_LOG_REQUIRED)).thenReturn(sc);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,true);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.ATC_LOG_MISSING));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));

        // ATC log is missing for  circular flight
        fm.setDeltaFlight(Boolean.FALSE);
        fm.setDepAd("DEP_AD");
        fm.setDestAd("DEST_AD");
        fm.setItem18Dep("DEP_AD");
        fm.setItem18Dest("DEST_AD");

        when(flightMovementAerodromeService.resolveAnyLocationToDMS("DEP_AD")).thenReturn("DEP_AD");
        when(flightMovementAerodromeService.resolveAnyLocationToDMS("DEST_AD")).thenReturn("DEST_AD");

        try{
        when(flightMovementAerodromeService.isCircularRoute("DEP_AD", "DEP_AD","DEST_AD","DEST_AD")).thenReturn(true);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,true);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.ATC_LOG_MISSING));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));
        } catch (Exception ex){
            Assert.assertTrue(fvm != null && fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.INCOMPLETE));
        }

        // check Flight plan
        sc = new SystemConfiguration();
        sc.setCurrentValue(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE);
        fm.setInitialFplData(null);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.FLIGHT_PLAN_REQUIRED)).thenReturn(sc);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,true);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertTrue(fvm.getIssues().contains(FlightMovementValidatorIssue.FLIGHT_PLAN_MISSING));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.INCOMPLETE));

      // check aircraft type
        fm.setAircraftType(null);

        fvm = flightMovementValidator.validateFlightMovementStatus(fm,true);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertTrue(fvm.getIssues().contains(FlightMovementValidatorIssue.UNKNOWN_AIRCRAFT_TYPE));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.INCOMPLETE));

        // check passenger fees
        fm.setAircraftType("TEST");
        fm.setInitialFplData("TEST FPL");
        fm.setPassengersChargeableDomestic(null);
        fm.setFlightCategoryType(FlightmovementCategoryType.DOMESTIC);
        sc = new SystemConfiguration();
        sc.setCurrentValue(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.PASSENGER_CHARGES_SUPPORT)).thenReturn(sc);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.INCLUDE_PASSENGER_FEES_ON_INVOICE)).thenReturn(sc);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,true);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertTrue(fvm.getIssues().contains(FlightMovementValidatorIssue.MISSING_PASSENGER_DOMESTIC_DATA));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.INCOMPLETE));

        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.INCLUDE_PASSENGER_FEES_ON_INVOICE)).thenReturn(null);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,true);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.MISSING_PASSENGER_DOMESTIC_DATA));
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.INCLUDE_PASSENGER_FEES_ON_INVOICE)).thenReturn(sc);

        fm.setPassengersChargeableDomestic(10);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,true);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.MISSING_PASSENGER_DOMESTIC_DATA));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));

        fm.setPassengersChargeableIntern(null);
        fm.setFlightCategoryType(FlightmovementCategoryType.DEPARTURE);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,true);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertTrue(fvm.getIssues().contains(FlightMovementValidatorIssue.MISSING_PASSENGER_INTERNATIONAL_DATA));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.INCOMPLETE));

        fm.setFlightCategoryType(FlightmovementCategoryType.ARRIVAL);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,true);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.MISSING_PASSENGER_INTERNATIONAL_DATA));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));

        fm.setPassengersChargeableIntern(10);
        fm.setFlightCategoryType(FlightmovementCategoryType.DEPARTURE);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,true);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.MISSING_PASSENGER_INTERNATIONAL_DATA));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));

        //check parking time
        SystemConfiguration sc2 = new SystemConfiguration();
        sc2.setCurrentValue(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.PARKING_TIME_REQUIRED)).thenReturn(sc2);
        fm.setParkingTime(null);
        fm.setFlightCategoryType(FlightmovementCategoryType.DEPARTURE);
        AerodromeCategory aerodromeCategory = new AerodromeCategory();
        Set<LdpBillingFormula> lSet = new HashSet<>();
        lSet.add(new LdpBillingFormula(aerodromeCategory, "parking_charges"));
        aerodromeCategory.setLdpBillingFormulas(lSet);
        when(flightMovementAerodromeService.resolveLocationToAdCategory( fm.getDepAd(), fm.getItem18Dep(), AdResolveType.AD_TYPE_DEPARTURE)).thenReturn(aerodromeCategory);

        fvm = flightMovementValidator.validateFlightMovementStatus(fm,true);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertTrue(fvm.getIssues().contains(FlightMovementValidatorIssue.MISSING_PARKING_TIME));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.INCOMPLETE));

        fm.setParkingTime(30.0);
        fm.setFlightCategoryType(FlightmovementCategoryType.DEPARTURE);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,true);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.MISSING_PARKING_TIME));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));

        // check service charge return
        fm.setFlightCategoryType(FlightmovementCategoryType.DEPARTURE);
        sc = new SystemConfiguration();
        fm.setPassengersChargeableIntern(10);
        fm.setPassengersChargeableDomestic(10);
        sc.setCurrentValue(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.PASSENGER_SERVICE_CHARGE_REQUIRED)).thenReturn(sc);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,true);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.MISSING_PASSENGER_SERVICE_CHARGE_RETURN));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));

        fm.setPassengersChargeableIntern(10);
        fm.setPassengersChargeableDomestic(10);
        sc.setCurrentValue(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.PASSENGER_SERVICE_CHARGE_REQUIRED)).thenReturn(sc);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,true);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.MISSING_PASSENGER_SERVICE_CHARGE_RETURN));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));

        // Tower log required if it is NOT overflight
        fm.setFlightCategoryType(FlightmovementCategoryType.OVERFLIGHT);
        sc = new SystemConfiguration();
        sc.setCurrentValue(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.TOWER_LOG_REQUIRED)).thenReturn(sc);
        fvm = flightMovementValidator.validateFlightMovementStatus(fm,true);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.TOWER_LOG_MISSING));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));

        fm.setFlightCategoryType(FlightmovementCategoryType.DOMESTIC);

        fvm = flightMovementValidator.validateFlightMovementStatus(fm,true);
        Assert.assertTrue(fvm != null && fvm.getIssues() != null);
        Assert.assertFalse(fvm.getIssues().contains(FlightMovementValidatorIssue.TOWER_LOG_MISSING));
        Assert.assertTrue(fvm.getStatus() != null && fvm.getStatus().equals(FlightMovementStatus.PENDING));
    }
}
