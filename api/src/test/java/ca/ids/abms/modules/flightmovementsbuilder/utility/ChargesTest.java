package ca.ids.abms.modules.flightmovementsbuilder.utility;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import ca.ids.abms.modules.aerodromeoperationalhours.AerodromeOperationalHoursService;
import ca.ids.abms.modules.aerodromeserviceoutages.AerodromeServiceOutageService;
import ca.ids.abms.modules.flightmovementsbuilder.utility.discount.DiscountUtility;
import ca.ids.abms.modules.localaircraftregistry.LocalAircraftRegistryService;
import ca.ids.abms.modules.plugins.PluginService;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.system.BillingOrgCode;
import org.junit.Before;
import org.junit.Test;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.accounts.AccountType;
import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.aerodromes.AerodromeService;
import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.aircraft.AircraftRegistrationService;
import ca.ids.abms.modules.aerodromes.AerodromeCategory;
import ca.ids.abms.modules.countries.CountryService;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.exemptions.ExemptionTypeService;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementAerodromeService;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.flightmovements.category.FlightmovementCategory;
import ca.ids.abms.modules.flightmovements.category.FlightmovementCategoryAttribute;
import ca.ids.abms.modules.flightmovements.enumerate.AdResolveType;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementType;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryNationality;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryScope;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryType;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBillable;
import ca.ids.abms.modules.formulas.FormulaEvaluator;
import ca.ids.abms.modules.formulas.enroute.EnrouteAirNavigationChargeFormulaService;
import ca.ids.abms.modules.mtow.AverageMtowFactor;
import ca.ids.abms.modules.mtow.AverageMtowFactorService;
import ca.ids.abms.modules.routesegments.RouteSegment;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.spreadsheets.SSService;

public class ChargesTest {

	private AerodromeService aerodromeService;
    private AverageMtowFactorService averageMtowFactorService;
    private SystemConfigurationService systemConfigurationService;
    private DeltaFlightUtility deltaFlightUtility;
    private FlightMovementAerodromeService flightMovementAerodromeService;
    private CurrencyUtils currencyUtils;
    private KCAAFlightUtility kcaaFlightUtility;
    private FlightMovementBillable flightMovementBillable;
    private ChargesUtility chargesUtility;
    private CountryService countryService;
    private AircraftRegistrationService aircraftRegistrationService;
    private AccountService accountService;
    private ReportHelper reportHelper;
    private PluginService pluginService;
    private AerodromeServiceOutageService aerodromeServiceOutageService;
    private LocalAircraftRegistryService localAircraftRegistryService;
    private AerodromeOperationalHoursService aerodromeOperationalHoursService;
    private ThruFlightPlanUtility thruFlightPlanUtility;

    @Before
    public void setup() {
        aerodromeService = mock(AerodromeService.class);
        FormulaEvaluator formulaEvaluator = mock(FormulaEvaluator.class);
        averageMtowFactorService = mock(AverageMtowFactorService.class);
        FlightMovementRepository flightMovementRepository = mock(FlightMovementRepository.class);
        SSService ssService = mock(SSService.class);
        systemConfigurationService = mock(SystemConfigurationService.class);
        ExemptionTypeService exemptionTypeService = mock(ExemptionTypeService.class);
        deltaFlightUtility = mock(DeltaFlightUtility.class);
        flightMovementAerodromeService = mock(FlightMovementAerodromeService.class);
        currencyUtils = mock(CurrencyUtils.class);
        countryService = mock(CountryService.class);
        accountService = mock(AccountService.class);
        reportHelper = mock(ReportHelper.class);
        flightMovementBillable = mock(FlightMovementBillable.class);
        pluginService = mock(PluginService.class);
        aircraftRegistrationService = mock(AircraftRegistrationService.class);
        aerodromeServiceOutageService = mock(AerodromeServiceOutageService.class);
        localAircraftRegistryService = mock(LocalAircraftRegistryService.class);
        aerodromeOperationalHoursService = mock(AerodromeOperationalHoursService.class);
        thruFlightPlanUtility = mock(ThruFlightPlanUtility.class);

        final DiscountUtility discountUtility = mock(DiscountUtility.class);

        kcaaFlightUtility = new KCAAFlightUtility(
        	aircraftRegistrationService,
            countryService,
            accountService,
            aerodromeService,
            flightMovementRepository,
            systemConfigurationService,
            pluginService,
            localAircraftRegistryService
        );
        EnrouteAirNavigationChargeFormulaService enrouteAirNavigationChargeFormulaService = mock(EnrouteAirNavigationChargeFormulaService.class);

        chargesUtility = new ChargesUtility(
            formulaEvaluator, averageMtowFactorService, flightMovementRepository,
            ssService, systemConfigurationService, exemptionTypeService, flightMovementBillable,
            deltaFlightUtility, flightMovementAerodromeService, currencyUtils, kcaaFlightUtility, reportHelper,
            enrouteAirNavigationChargeFormulaService, Collections.emptyList(), discountUtility, aerodromeServiceOutageService,
            aerodromeOperationalHoursService,thruFlightPlanUtility);

        when(systemConfigurationService.getOneByItemName(eq(SystemConfigurationItemName.APPROACH_FEE_LABEL)))
            .thenReturn(getApproachFeeConfig());
        when(systemConfigurationService.getOneByItemName(eq(SystemConfigurationItemName.ADAP_AERODROME)))
            .thenReturn(getApproachAerodomeConfig());

        when(systemConfigurationService.getIntOrZero(SystemConfigurationItemName.SMALL_AIRCRAFT_MAX_WEIGHT))
        .thenReturn(Integer.valueOf(5700));

        when(averageMtowFactorService.findAverageMtowFactorByUpperLimitAndFactorClass(any(), any()))
            .thenReturn(getAverageMtowFactor());

        when(aircraftRegistrationService.findAircraftRegistrationByRegistrationNumber(eq("5YLVP"), eq(LocalDateTime.now())))
            .thenReturn(this.getAircraftRegistration());

        when(flightMovementAerodromeService.resolveLocationToAdCategory(eq("HKJK"), any(), any(AdResolveType.class)))
            .thenReturn(getAerodromeCategory());
        when(flightMovementAerodromeService.resolveLocationToAdCategory(eq("HKMO"), any(), any(AdResolveType.class)))
            .thenReturn(getAerodromeCategory());
        when(flightMovementAerodromeService.resolveLocationToAdCategory(eq("FBMN"), any(), any(AdResolveType.class)))
            .thenReturn(getAerodromeCategory());
        when(flightMovementAerodromeService.resolveLocationToAdCategory(eq("FBKE"), any(), any(AdResolveType.class)))
            .thenReturn(getAerodromeCategory());

        when(discountUtility.getArrivalChargeDiscount(any(FlightMovement.class), anyString()))
            .thenReturn(1.0);

    }

    @Test
    public void trainingFlights() {
        when(systemConfigurationService.getBillingOrgCode()).thenReturn(BillingOrgCode.KCAA);
        when(systemConfigurationService.getBoolean("Include passenger charges on invoice")).thenReturn(true);
        final FlightMovement fm = getTrainingFlight();

        chargesUtility.setCharges(fm);
        assertThat(fm.getEnrouteCharges()).isNull();
        assertThat(fm.getDomesticPassengerCharges()).isEqualTo(100.0);
        assertThat(fm.getInternationalPassengerCharges()).isEqualTo(200.0);
        assertThat(fm.getTotalCharges()).isEqualTo(300.0);
    }

    @Test
    public void charterFlights() {
        when(systemConfigurationService.getBillingOrgCode()).thenReturn(BillingOrgCode.CAAB);
        final FlightMovement fm = getCharterFlight();

        assertThat(fm.getApproachCharges()).isEqualTo(100.0);
        assertThat(fm.getAerodromeCharges()).isEqualTo(50.0);
        assertThat(fm.getLateArrivalCharges()).isEqualTo(20.0);

        fm.setItem18RegNum("AA002");
    }

    @Test
    public void testFlightCategoryScope() {
        assertThat (FlightmovementCategoryScope.forValue (null)).isNull();
        assertThat (FlightmovementCategoryScope.forValue(FlightmovementCategoryScope.DOMESTIC.toValue())).isEqualTo(FlightmovementCategoryScope.DOMESTIC);
    }

    private AverageMtowFactor getAverageMtowFactor() {
        AverageMtowFactor amf = new AverageMtowFactor();
        amf.setUpperLimit(2.05);
        return amf;
    }

    private FlightMovement getTrainingFlight() {
        final Account account = new Account();
        account.setId(1);
        account.setApprovedFlightSchoolIndicator(true);
        final FlightmovementCategory cat = new FlightmovementCategory();
        {
            final Currency usd = new Currency();
            usd.setActive(true);
            usd.setCurrencyCode("USD");
            usd.setDecimalPlaces(2);
            usd.setSymbol("$");

            final Set <FlightmovementCategoryAttribute> attrSet = new HashSet<> ();
            FlightmovementCategoryAttribute a = new FlightmovementCategoryAttribute();
            a.setFlightmovementCategory(cat);
            a.setFlightNationality("NA");
            a.setFlightScope("DO");
            a.setFlightType("DO");

            cat.setId(100);
            cat.setEnrouteInvoiceCurrency (usd);
            cat.setEnrouteResultCurrency (usd);
            cat.setFlightmovementCategoryAttributes (attrSet);
            cat.setName("DOMESTIC");
            cat.setShortName("DO");
            cat.setSortOrder(1);
        }
        final FlightMovement fm = new FlightMovement();
        fm.setId(1);
        fm.setAccount(account);
        fm.setDepAd("HKJK");
        fm.setDestAd("HKMO");
        fm.setArrivalAd("HKMO");
        fm.setFlightId("TST001");
        fm.setItem18RegNum("5YLVP");
        fm.setAircraftType("C320");
        fm.setDateOfFlight(LocalDateTime.now());
        fm.setDepTime("1000");
        fm.setActualMtow(2.05);
        fm.setAverageMassFactor(1.0);
        fm.setFplCrossingDistance(1000.0);
        fm.setId(1);
        fm.setStatus(FlightMovementStatus.PENDING);
        fm.setMovementType(FlightMovementType.DOMESTIC);
        fm.setFlightCategoryScope(FlightmovementCategoryScope.DOMESTIC);
        fm.setFlightCategoryType(FlightmovementCategoryType.DOMESTIC);
        fm.setFlightCategoryNationality(FlightmovementCategoryNationality.NATIONAL);
        fm.setFlightmovementCategory(cat);
        fm.setFlightType("DOMESTIC");
        fm.setDeltaFlight(false);
        fm.setItem18Rmk("TRAINING");
        fm.setBillingDate(LocalDateTime.of(2017,12,1,0,0));
        fm.setDateOfFlight(LocalDateTime.of(2017,12,1,0,0));
        fm.setDomesticPassengerCharges(20.0);
        fm.setInternationalPassengerCharges(40.0);
        fm.setEnrouteCharges(50.0);
        fm.setPassengersChargeableDomestic(100);
        fm.setPassengersChargeableIntern(100);
        return fm;
    }

    private FlightMovement getCharterFlight() {
        final AccountType charter = new AccountType();
        charter.setId(6);
        charter.setName("Charter");
        final Account account = new Account();
        account.setId(2);
        account.setAccountType(charter);
        final FlightMovement fm = new FlightMovement();
        fm.setId(1);
        fm.setAccount(account);
        fm.setDepAd("FBMN");
        fm.setDestAd("FBKE");
        fm.setArrivalAd("FBKE");
        fm.setFlightId("TST002");
        fm.setItem18RegNum("AA002");
        fm.setAircraftType("C320");
        fm.setFlightType("N");
        fm.setDateOfFlight(LocalDateTime.now());
        fm.setDepTime("1000");
        fm.setArrivalTime("1200");
        fm.setActualMtow(2.05);
        fm.setAverageMassFactor(1.0);
        fm.setFplCrossingDistance(1000.0);
        fm.setStatus(FlightMovementStatus.PENDING);
        fm.setMovementType(FlightMovementType.DOMESTIC);
        fm.setFlightType("N");
        fm.setDeltaFlight(false);
        fm.setBillingDate(LocalDateTime.of(2017,12,1,0,0));
        fm.setDateOfFlight(LocalDateTime.of(2017,12,1,0,0));
        fm.setDomesticPassengerCharges(20.0);
        fm.setInternationalPassengerCharges(40.0);
        fm.setEnrouteCharges(50.0);
        fm.setPassengersChargeableDomestic(100);
        fm.setPassengersChargeableIntern(100);
        fm.setApproachCharges(100.0);
        fm.setAerodromeCharges(50.0);
        fm.setLateArrivalCharges(20.0);
        return fm;
    }

    private AircraftRegistration getAircraftRegistration() {
    	AircraftRegistration lar = new AircraftRegistration();
        lar.setCoaExpiryDate(LocalDateTime.of(2017,12,31,0,0));
        lar.setMtowOverride(2.0);
        lar.setCoaIssueDate(LocalDateTime.of(2017,10,31,0,0));
        return lar;
    }

    private AerodromeCategory getAerodromeCategory() {
        AerodromeCategory ac = new AerodromeCategory();
        ac.setId(1);
        ac.setDomesticPassengerFeeAdult(1.0);
        ac.setInternationalPassengerFeeAdult(2.0);
        ac.setDomesticPassengerFeeChild(0.0);
        ac.setInternationalPassengerFeeChild(0.0);
        ac.setCategoryName("Class I");
        return ac;
    }

    private SystemConfiguration getApproachFeeConfig() {
        SystemConfiguration sc = new SystemConfiguration();
        sc.setCurrentValue("t");
        return sc;
    }

    private SystemConfiguration getApproachAerodomeConfig() {
        SystemConfiguration sc = new SystemConfiguration();
        sc.setCurrentValue("Arrival");
        return sc;
    }

	@Test
	public void test() {

		FlightMovement fm = baseFlight();
        chargesUtility.setCharges(fm);
        AverageMtowFactor amf= new AverageMtowFactor();
        amf.setUpperLimit(200.0);
        when(averageMtowFactorService.findAverageMtowFactorByUpperLimit(any())).thenReturn(amf);
        Aerodrome ad = new Aerodrome();
		ad.setAerodromeName("FBMN");
		AerodromeCategory ac = new AerodromeCategory();
		ad.setAerodromeCategory(ac);
		when(deltaFlightUtility.getOvernightFirstSegment(any())).thenReturn(fm.getRouteSegments().get(3));
        when(aerodromeService.findAeroDromeByAeroDromeName(any())).thenReturn(ad);
	}

	private FlightMovement baseFlight(){
		FlightMovement fm = new FlightMovement();
		fm.setDepAd("FBMN");
		fm.setArrivalAd("FBSK");
		fm.setFlightId("EXY761");
		fm.setAircraftType("DH8D");
		fm.setDateOfFlight(LocalDateTime.now());
		fm.setDepTime("1830");
		fm.setActualMtow(200.0);
		fm.setAverageMassFactor(1.0);
		fm.setFplCrossingDistance(1000.0);
		fm.setFlightType("INT_DEPARTURE");
		fm.setId(999999);
		fm.setStatus(FlightMovementStatus.PENDING);
		fm.setFlightType("DOMESTIC");
		fm.setDeltaFlight(true);

		fm.setItem18Dest("FBSV0800/0810 SELINDA0830/0840 XARANNA0920/0830 STANLEYS0940/0955 FBMN1005");
		 LinkedList<RouteSegment> rsList = new LinkedList<>();
		 RouteSegment rs = new RouteSegment();
		 rs.setSegmentStartLabel("FBKE");
         rs.setSegmentEndLabel("FBSV");
		 rs.setSegmentNumber(0);
		 rsList.add(rs);
		 rs.setSegmentStartLabel("FBSV");
         rs.setSegmentEndLabel("SELINDA");
		 rs.setSegmentNumber(1);
		 rsList.add(rs);
		 rs.setSegmentStartLabel("SELINDA");
         rs.setSegmentEndLabel("XARANNA");
		 rs.setSegmentNumber(2);
		 rsList.add(rs);
		 rs.setSegmentStartLabel("XARANNA");
         rs.setSegmentEndLabel("STANLEYS");
		 rs.setSegmentNumber(3);
		 rsList.add(rs);
		 rs.setSegmentStartLabel("STANLEYS");
         rs.setSegmentEndLabel("FBMN");
		 rs.setSegmentNumber(4);
		 rsList.add(rs);
		 rs.setSegmentStartLabel("FBMN");
         rs.setSegmentEndLabel("FBKE");
		 rs.setSegmentNumber(5);
		 rsList.add(rs);

		 fm.setRouteSegments(rsList);
		 fm.setId(99999);

		return fm;
	}

    /* private FlightMovement getKCAAFlight() {
        FlightMovement fm = new FlightMovement();
        fm.setDepAd("HKJK");
        fm.setDestAd("HKMO");
        fm.setArrivalAd("HKMO");
        fm.setFlightId("TST001");
        fm.setItem18RegNum("5YLVP");
        fm.setAircraftType("C320");
        fm.setDateOfFlight(LocalDateTime.now());
        fm.setDepTime("1000");
        fm.setActualMtow(2.05);
        fm.setAverageMassFactor(1.0);
        fm.setFplCrossingDistance(1000.0);
        fm.setId(1);
        fm.setStatus(FlightMovementStatus.PENDING);
        fm.setFlightType("DOMESTIC");
        fm.setDeltaFlight(false);
        fm.setItem18Rmk("TRAINING");
        return fm;
    }*/
}
