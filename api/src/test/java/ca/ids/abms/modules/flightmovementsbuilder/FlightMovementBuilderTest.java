package ca.ids.abms.modules.flightmovementsbuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.aerodromes.cluster.RepositioningAerodromeClusterService;
import ca.ids.abms.modules.exemptions.flightroutes.ExemptFlightRouteService;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.mapper.RouteCacheSegmentMapperImpl;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.RejectedException;
import ca.ids.abms.config.error.RejectedReasons;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.atcmovements.AtcMovementLog;
import ca.ids.abms.modules.atcmovements.AtcMovementLogBillableRouteFinder;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.billingcenters.BillingCenterService;
import ca.ids.abms.modules.charges.PassengerServiceChargeReturnRepository;
import ca.ids.abms.modules.dbqueries.DatabaseQueryService;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementAerodromeService;
import ca.ids.abms.modules.flightmovements.FlightMovementValidationViewModel;
import ca.ids.abms.modules.flightmovements.category.FlightmovementCategory;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementSource;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementType;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryType;
import ca.ids.abms.modules.flightmovementsbuilder.utility.ChargesUtility;
import ca.ids.abms.modules.flightmovementsbuilder.utility.DeltaFlightUtility;
import ca.ids.abms.modules.flightmovementsbuilder.utility.FlightMovementBuilderUtility;
import ca.ids.abms.modules.flightmovementsbuilder.utility.ThruFlightPlanUtility;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.RouteCacheUtility;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.vo.RouteCacheVO;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.vo.RouteSegmentVO;
import ca.ids.abms.modules.radarsummary.FlightTravelCategory;
import ca.ids.abms.modules.radarsummary.RadarSummary;
import ca.ids.abms.modules.route.NominalRouteService;
import ca.ids.abms.modules.routesegments.SegmentType;
import ca.ids.abms.modules.spatiareader.dto.FplObjectDto;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.towermovements.TowerMovementLog;

/**
 * Created by c.talpa on 17/01/2017.
 */
public class FlightMovementBuilderTest {

    private BillingCenter billingCenterDefault;
    private ChargesUtility chargesUtility;
    private DeltaFlightUtility deltaFlightUtility;
    private FlightMovementBuilder flightMovementBuilder;
    private FlightMovementBuilderUtility flightMovementBuilderUtility;
    private FlightMovementValidator flightMovementValidator;
    private FlightMovementValidationViewModel flightMovementValidationViewModel;
    private SystemConfigurationService systemConfigurationService;
    private RouteCacheSegmentMapperImpl routeCacheSegmentMapper;
    private AccountService accountService;

    /*
    private AtcMovementLogBillableRouteFinder atcMovementLogBillableRouteFinder;

    private FlightMovementAerodromeService flightMovementAerodromeService;

    private BillingCenterService billingCenterService;

    private PassengerServiceChargeReturnRepository passengerRep;

    private DatabaseQueryService databaseQueryService;
    */


    @Before
    public void setUp() {

        final BillingCenterService billingCenterService = mock(BillingCenterService.class);
        chargesUtility = mock(ChargesUtility.class);
        deltaFlightUtility = mock(DeltaFlightUtility.class);
        flightMovementBuilderUtility = mock(FlightMovementBuilderUtility.class);
        flightMovementValidator = mock(FlightMovementValidator.class);
        systemConfigurationService = mock(SystemConfigurationService.class);
        accountService = mock(AccountService.class);

        FlightMovementBillable flightMovementBillable = new FlightMovementBillable(mock(ExemptFlightRouteService.class),
            mock(FlightMovementBuilderUtility.class), mock(NominalRouteService.class),
            mock(RepositioningAerodromeClusterService.class), systemConfigurationService);
        FlightMovementMerge flightMovementMerge = new FlightMovementMerge(
            mock(DeltaFlightUtility.class), mock(ThruFlightPlanUtility.class));
        flightMovementBuilder = new FlightMovementBuilder(
            flightMovementBuilderUtility, flightMovementValidator, deltaFlightUtility,
            mock(ThruFlightPlanUtility.class), chargesUtility, flightMovementBillable,
            mock(AtcMovementLogBillableRouteFinder.class), mock(FlightMovementAerodromeService.class),
            billingCenterService, mock(PassengerServiceChargeReturnRepository.class), accountService, mock(DatabaseQueryService.class),
            flightMovementMerge, mock(FlightMovementBillingDateEstimator.class), mock(SystemConfigurationService.class));

        // create this object for mock methods
        flightMovementValidationViewModel = new FlightMovementValidationViewModel();
        flightMovementValidationViewModel.setStatus(FlightMovementStatus.INCOMPLETE);
        flightMovementValidationViewModel.setFlightmovementType(FlightmovementCategoryType.DOMESTIC);

        // mock default adap charge location
        when(chargesUtility.getApplyAdapLocation())
            .thenReturn("");

        // mock default hq billing centre
        this.billingCenterDefault = new BillingCenter();
        this.billingCenterDefault.setId(1);
        this.billingCenterDefault.setHq(true);
        this.billingCenterDefault.setName("Head Office");
        when(billingCenterService.findHq())
            .thenReturn(this.billingCenterDefault);

        routeCacheSegmentMapper = new RouteCacheSegmentMapperImpl();
    }

    /**
     * This method test the creation of the FlightMovement by FlightMovemntBuilder that use a FlightMovementDTO
     */
    @Test
    public void createFlightMovementFromFlightMovement() throws FlightMovementBuilderException {
    	FlightMovement fm = new FlightMovement();

        String existingSystemConfiguration = "scheduled";
        FlightMovementValidationViewModel flightMovementValidationViewModel= new FlightMovementValidationViewModel();
        flightMovementValidationViewModel.setStatus(FlightMovementStatus.INCOMPLETE);


        // 1 Test Case:  flightMovemntDTO is Null - FlightMovementManual is Null
        when(flightMovementValidator.validateFlightMovementType(any()))
            .thenReturn(flightMovementValidationViewModel);
        when(systemConfigurationService.getCurrentValue(any()))
            .thenReturn(existingSystemConfiguration);

        FlightMovement flightMovement = flightMovementBuilder
            .createUpdateFlightMovementFromUI(null,false);
        assertThat(flightMovement)
            .isNull();


        //2 Test Case: flightMovemntDTO is not valid - Flight Movement is returned
        FlightMovement flightMovementDTO = new FlightMovement();
        flightMovementDTO.setActualDepartureTime("1125");
        flightMovementDTO.setActualMtow(2.54);
        when(flightMovementValidator.validateFlightMovementCategory(any())).thenReturn(flightMovementValidationViewModel);
        when(deltaFlightUtility.isDeltaFlight(any())).thenReturn(Boolean.FALSE);

        when(flightMovementValidator.validateFlightMovementType(any()))
            .thenReturn(flightMovementValidationViewModel);
        when(deltaFlightUtility.isDeltaFlight(any()))
            .thenReturn(Boolean.FALSE);

        flightMovement = flightMovementBuilder.createUpdateFlightMovementFromUI(flightMovementDTO,false);
        assertThat(flightMovement)
            .isEqualTo(flightMovementDTO);


        //3 Test Case: RadarSummary is valid - Flight Movement is created
        flightMovement.setFlightId("AZ123");
        flightMovementDTO.setDepAd("FAOR");
        flightMovementDTO.setDestAd("DNMM");
        flightMovementDTO.setDepTime("0438");
        flightMovementDTO.setDateOfFlight(LocalDateTime.now());

        when(flightMovementValidator.validateFlightMovementType(any()))
            .thenReturn(flightMovementValidationViewModel);
        when(flightMovementValidator.validateFlightMovementStatus(eq(fm),eq(false)))
            .thenReturn(flightMovementValidationViewModel);
        when(chargesUtility.setCharges(any()))
            .thenReturn(Boolean.FALSE);

        flightMovement = flightMovementBuilder.createUpdateFlightMovementFromUI(flightMovementDTO,false);
        assertThat(flightMovement.getFlightId())
            .isEqualTo(flightMovementDTO.getFlightId());
    }

    @Test(expected=RejectedException.class)
    public void createFlightMovementFromFlightObjectInvalid()  {

        // 1 Test Case:  fplObject is Null - FlightMovementManual is Null
        FlightMovement flightMovement = flightMovementBuilder.validateAndCreateFlightMovementFromFlightObject(null);
        assertThat(flightMovement)
            .isNull();


        //2 Test Case: flightMovemntDTO is not valid - Flight Movement is Null
        FplObjectDto fplObject = new FplObjectDto();
        fplObject.setCatalogueFplObjectId(12345L);
        fplObject.setDayOfFlight(LocalDate.now());

        when(flightMovementValidator.validateFlightMovementType(any()))
            .thenReturn(new FlightMovementValidationViewModel());
        when(flightMovementValidator.validateFlightMovementCategory(any()))
            .thenReturn(new FlightMovementValidationViewModel());
        when(systemConfigurationService.getCurrentValue(any()))
            .thenReturn("scheduled");

        flightMovement = flightMovementBuilder.validateAndCreateFlightMovementFromFlightObject(fplObject);
        assertThat(flightMovement.getFlightId())
            .isEqualTo(fplObject.getFlightId());
    }

    /**
     * This method test the creation of the FlightMovement by FlightMovemntBuilder that use a FlightMovementDTO
     */
    @Test
    public void createFlightMovementFromFlightObject() throws FlightMovementBuilderException {

        // 1 Test Case:  fplObject is Null - FlightMovementManual is Null
        FlightMovement flightMovement = flightMovementBuilder.validateAndCreateFlightMovementFromFlightObject(null);
        assertThat(flightMovement)
            .isNull();


        //2 Test Case: flightMovemntDTO is not valid - Flight Movement is Null
        final RouteCacheVO routeCacheVO = buildRouteCacheVO();

        FplObjectDto fplObject = new FplObjectDto();
        fplObject.setCatalogueFplObjectId(12345L);
        fplObject.setDayOfFlight(LocalDate.now());

        when(flightMovementValidator.validateFlightMovementType(any()))
            .thenReturn(new FlightMovementValidationViewModel());
        when(flightMovementValidator.validateFlightMovementCategory(any()))
            .thenReturn(new FlightMovementValidationViewModel());
        when(systemConfigurationService.getCurrentValue(any()))
            .thenReturn("scheduled");
        when(deltaFlightUtility.getDeltaRouteSegmentList(any(), anyString()))
            .thenReturn(routeCacheVO);

        try {
            flightMovement = flightMovementBuilder.validateAndCreateFlightMovementFromFlightObject(fplObject);
            fail("Expected `VALIDATION_ERROR` for Flight Object '" + fplObject
                + "', returned Flight Movement '" + flightMovement + "' instead.");
        } catch (RejectedException e) {
            assertThat(e.getErrorDto().getRejectedReasons())
                .isEqualTo(RejectedReasons.VALIDATION_ERROR);
            assertThat(e.getReason())
                .isEqualTo(RejectedReasons.VALIDATION_ERROR.toValue());
        }


        //3 Test Case: RadarSummary is valid - Flight Movement is created
        fplObject.setDepartureTime("1245");
        fplObject.setFlightId("V5RCK");
        fplObject.setFlightType("N");
        fplObject.setDepartureAd("FYRU");
        fplObject.setDestinationAd("FYBG");
        fplObject.setRoute("DCT");
        fplObject.setAircraftType("A320");
        fplObject.setWakeTurb("M");
        fplObject.setOtherInfo("DOF/160713 EET/FBGR0032 OPR/WOA0818083918 RMK/SAR NIL");

        when(flightMovementValidator.validateFlightMovementType(any()))
            .thenReturn(flightMovementValidationViewModel);
        when(flightMovementValidator.validateFlightMovementCategory(any()))
            .thenReturn(flightMovementValidationViewModel);
        when(flightMovementValidator.validateFlightMovementStatus(any(FlightMovement.class),eq(false)))
            .thenReturn(flightMovementValidationViewModel);
        //when(flightMovementBillable.calculateBillableCrossiDistance(billableRouteVO)).thenReturn(1234.0);
        when(chargesUtility.setCharges(any()))
            .thenReturn(Boolean.FALSE);

        flightMovement = flightMovementBuilder.validateAndCreateFlightMovementFromFlightObject(fplObject);
        assertThat(flightMovement.getFlightId())
            .isEqualTo(fplObject.getFlightId());


        //4 Test Case Delta flight
        fplObject.setDepartureTime("1245");
        fplObject.setFlightId("V5RCK");
        fplObject.setFlightType("N");
        fplObject.setDepartureAd("FBKE");
        fplObject.setDestinationAd("FBSV");
        fplObject.setRoute("DCT");
        fplObject.setAircraftType("A320");
        fplObject.setWakeTurb("M");
        fplObject.setOtherInfo("DOF/160713 EET/FBGR0032 OPR/WOA0818083918 RMK/SAR NIL DEST/FBSV0800/0810 FBMN1005");

        when(flightMovementValidator.validateFlightMovementType(any()))
            .thenReturn(flightMovementValidationViewModel);
        when(flightMovementValidator.validateFlightMovementCategory(any()))
            .thenReturn(flightMovementValidationViewModel);
        when(flightMovementValidator.validateFlightMovementStatus(any(FlightMovement.class),eq(false)))
            .thenReturn(flightMovementValidationViewModel);
        //when(flightMovementBillable.calculateBillableCrossiDistance(billableRouteVO)).thenReturn(1234.0);
        when(chargesUtility.setCharges(any()))
            .thenReturn(Boolean.FALSE);
        when(deltaFlightUtility.isDeltaFlight(any()))
            .thenReturn(Boolean.TRUE);
        when(deltaFlightUtility.constructDeltaRoute(any()))
            .thenReturn("DCT FBSV");
        when(deltaFlightUtility.getDeltaDestination(any(), any()))
            .thenReturn("DCT FBMN");

        flightMovement = flightMovementBuilder.validateAndCreateFlightMovementFromFlightObject(fplObject);
        assertThat(flightMovement.getFlightId())
            .isEqualTo(fplObject.getFlightId());
        FlightMovement fm = flightMovementBuilder.calculateFlightMovementFromFlightObject(flightMovement);
        assertThat(flightMovement.getFlightId()).isEqualTo(fm.getFlightId());

        assertThat(fm.getFplCrossingDistance()).isNotNull();
        assertThat( fm.getFplCrossingDistance()).isGreaterThan(0.0);
    }

    /**
     * This method tests that updating a flight movement from a flight object does not modify billing date if previously
     * set by radar summary/tower/atc
     */
    @Test
    public void updateFlightMovementFlightObject() throws FlightMovementBuilderException {

        // Setup flight movement from tower log
        TowerMovementLog  towerMovementLog = new TowerMovementLog();
        towerMovementLog.setRegistration("TST REG");
        towerMovementLog.setOperatorName("OPRI");
        towerMovementLog.setDepartureContactTime("1234");
        towerMovementLog.setDepartureAerodrome("DEP");
        towerMovementLog.setDestinationAerodrome("DEST");
        towerMovementLog.setRoute("DCT");
        towerMovementLog.setDayOfFlight(LocalDateTime.now());
        towerMovementLog.setDateOfContact(LocalDateTime.now());
        towerMovementLog.setFlightId("FLIGHTID");

        when(flightMovementValidator.validateFlightMovementType(any()))
            .thenReturn(flightMovementValidationViewModel);
        when(flightMovementValidator.validateFlightMovementCategory(any()))
            .thenReturn(flightMovementValidationViewModel);
        when(flightMovementValidator.validateFlightMovementStatus(any(FlightMovement.class), eq(false)))
            .thenReturn(flightMovementValidationViewModel);
        when(systemConfigurationService.getCurrentValue(any()))
            .thenReturn("scheduled");

        FlightMovement fmFromTower = flightMovementBuilder.createFlightMovementFromTowerMovementLog(towerMovementLog);

        // Setup flight movement from FPL
        final RouteCacheVO routeCacheVO = buildRouteCacheVO();

        FplObjectDto fplObject = new FplObjectDto();
        fplObject.setCatalogueFplObjectId(12345L);
        fplObject.setDayOfFlight(LocalDate.now().plusDays(1));
        fplObject.setDepartureTime("1245");
        fplObject.setFlightId("V5RCK");
        fplObject.setFlightType("N");
        fplObject.setDepartureAd("FYRU");
        fplObject.setDestinationAd("FYBG");
        fplObject.setRoute("DCT");
        fplObject.setAircraftType("A320");
        fplObject.setWakeTurb("M");
        fplObject.setOtherInfo("DOF/160713 EET/FBGR0032 OPR/WOA0818083918 RMK/SAR NIL");

        when(flightMovementValidator.validateFlightMovementCategory(any()))
            .thenReturn(flightMovementValidationViewModel);
        when(chargesUtility.setCharges(any()))
            .thenReturn(Boolean.FALSE);
        when(flightMovementValidator.validateFlightMovementStatus(any(),eq(false)))
            .thenReturn(flightMovementValidationViewModel);
        when(deltaFlightUtility.getDeltaRouteSegmentList(any(), anyString()))
            .thenReturn(routeCacheVO);
        when(systemConfigurationService.getCurrentValue(any()))
            .thenReturn("scheduled");

        // Billing date set by ATS takes precedence and should not be modified by FPL object
        FlightMovement updatedFromFpl = flightMovementBuilder.validateAndMergeFromFplObject(fmFromTower, fplObject);
        flightMovementBuilder.calculateFlightMovementFromFlightObject(updatedFromFpl);
        assertThat(updatedFromFpl.getBillingDate())
            .isEqualTo(fmFromTower.getBillingDate());

        FlightMovement fmFromFpl = flightMovementBuilder.validateAndCreateFlightMovementFromFlightObject(fplObject);
        flightMovementBuilder.calculateFlightMovementFromFlightObject(fmFromFpl);
        FlightMovement updatedFromTower = flightMovementBuilder.updateFlightMovementFromTowerMovementLog(fmFromFpl, towerMovementLog);
        assertThat(updatedFromTower.getBillingDate())
            .isEqualTo(fmFromTower.getBillingDate());
    }

    /**
     * This method test the creation of the FlightMovement by FlightMovemntBuilder that use a RadarSummary
     */
    @Test
    public void createFlightMovementFromRadarSummary() throws FlightMovementBuilderException {

        // 1 Test Case:  RadarSummary is Null - Flight Movement is Null
        FlightMovement flightMovement = flightMovementBuilder.createFlightMovementFromRadarSummary(null);
        assertThat(flightMovement)
            .isNull();


        //2 Test Case: RadarSummary is not valid - Flight Movement is Null
        RadarSummary radarSummary = new RadarSummary();
        radarSummary.setFlightTravelCategory(FlightTravelCategory.mapFromValue("OVR"));
        radarSummary.setFlightRule("IFR");
        radarSummary.setFlightType("S");
        radarSummary.setFirExitTime("0018");
        radarSummary.setFirExitPoint("AVOGU");
        radarSummary.setFirEntryTime("2321");
        radarSummary.setFirEntryPoint("NESEK");
        radarSummary.setAircraftType("B738");
        radarSummary.setRegistration("5NMJO EET");

        flightMovement = flightMovementBuilder.createFlightMovementFromRadarSummary(radarSummary);
        assertThat(flightMovement)
            .isNull();


        //3 Test Case: RadarSummary is valid - Flight Movement is created
        radarSummary.setDepartureAeroDrome("FAOR");
        radarSummary.setDestinationAeroDrome("DNMM");
        radarSummary.setDepartureTime("0438");
        radarSummary.setDestinationAeroDrome("0711");
        radarSummary.setFlightIdentifier("ARA104");
        radarSummary.setDate(LocalDateTime.now());
        radarSummary.setDayOfFlight(LocalDateTime.now());
        radarSummary.setSegment(1);

        when(flightMovementValidator.validateFlightMovementType(any()))
            .thenReturn(flightMovementValidationViewModel);
        when(flightMovementValidator.validateFlightMovementCategory(any()))
            .thenReturn(flightMovementValidationViewModel);
        when(flightMovementValidator.validateFlightMovementStatus(any(FlightMovement.class), eq(false)))
            .thenReturn(flightMovementValidationViewModel);
        when(chargesUtility.setCharges(any()))
            .thenReturn(Boolean.FALSE);
        when(systemConfigurationService.getCurrentValue(any()))
            .thenReturn("scheduled");

        flightMovement = flightMovementBuilder.createFlightMovementFromRadarSummary(radarSummary);
        assertThat(flightMovement.getFlightId())
            .isEqualTo(radarSummary.getFlightIdentifier());
    }

    @Test
    public void mapFlightRules() throws FlightMovementBuilderException {

        RadarSummary radarSummary = new RadarSummary();
        radarSummary.setFirExitTime("0018");
        radarSummary.setFirExitPoint("AVOGU");
        radarSummary.setFirEntryTime("2321");
        radarSummary.setFirEntryPoint("NESEK");
        radarSummary.setAircraftType("B738");
        radarSummary.setRegistration("5NMJO EET");
        radarSummary.setDepartureAeroDrome("FAOR");
        radarSummary.setDestinationAeroDrome("DNMM");
        radarSummary.setDepartureTime("0438");
        radarSummary.setDestinationAeroDrome("0711");
        radarSummary.setFlightIdentifier("ARA104");
        radarSummary.setDate(LocalDateTime.now());
        radarSummary.setDayOfFlight(LocalDateTime.now());
        radarSummary.setSegment(1);

        when(flightMovementValidator.validateFlightMovementType(any()))
            .thenReturn(flightMovementValidationViewModel);
        when(flightMovementValidator.validateFlightMovementCategory(any()))
            .thenReturn(flightMovementValidationViewModel);
        when(flightMovementValidator.validateFlightMovementStatus(any(FlightMovement.class), eq(false)))
            .thenReturn(flightMovementValidationViewModel);
        when(systemConfigurationService.getCurrentValue(any()))
            .thenReturn("scheduled");

        // test for IFR
        radarSummary.setFlightRule("IFR");
        FlightMovement flightMovement = flightMovementBuilder.createFlightMovementFromRadarSummary(radarSummary);
        assertThat(flightMovement.getFlightRules())
            .isEqualTo("I");

        // test for VFR
        radarSummary.setFlightRule("VFR");
        flightMovement = flightMovementBuilder.createFlightMovementFromRadarSummary(radarSummary);
        assertThat(flightMovement.getFlightRules())
            .isEqualTo("V");

        // test for "other" case
        radarSummary.setFlightRule("BAD FLIGHT RULE");
        flightMovement = flightMovementBuilder.createFlightMovementFromRadarSummary(radarSummary);
        assertThat(flightMovement.getFlightRules())
            .isNull();
    }

    /**
     * This method test the creation of the FlightMovement by FlightMovemntBuilder that use a TowerMovementLog
     */
    @Test
    public void createFlightMovementFromTowerMovementLog() throws FlightMovementBuilderException {

        // 1 Test Case:  TowerMovement is Null - Flight Movement is Null
        FlightMovement flightMovement = flightMovementBuilder.createFlightMovementFromTowerMovementLog(null);
        assertThat(flightMovement)
            .isNull();


        //2 Test Case: TowerMovement is not valid - Flight Movement is Null
        TowerMovementLog towerMovementLog = new TowerMovementLog();
        towerMovementLog.setRegistration("TST REG");
        towerMovementLog.setOperatorName("OPRI");
        towerMovementLog.setDepartureContactTime("1234");

        try {
            flightMovement = flightMovementBuilder.createFlightMovementFromTowerMovementLog(towerMovementLog);
            fail("Expected `VALIDATION_ERROR` for Tower Movement Log '" + towerMovementLog
                + "', returned Flight Movement '" + flightMovement + "' instead.");
        }catch(CustomParametrizedException e){
            assertThat(e.getErrorDTO().getRejectedReasons())
                .isEqualTo(RejectedReasons.VALIDATION_ERROR);
        }


        //3 Test Case: RadarSummary is valid - Flight Movement is created
        towerMovementLog.setDepartureAerodrome("DEP");
        towerMovementLog.setDestinationAerodrome("DEST");
        towerMovementLog.setRoute("DCT");
        towerMovementLog.setDayOfFlight(LocalDateTime.now());
        towerMovementLog.setDateOfContact(LocalDateTime.now());
        towerMovementLog.setFlightId("FLIGHTID");

        when(flightMovementValidator.validateFlightMovementType(any()))
            .thenReturn(flightMovementValidationViewModel);
        when(flightMovementValidator.validateFlightMovementCategory(any()))
            .thenReturn(flightMovementValidationViewModel);
        when(flightMovementValidator.validateFlightMovementStatus(any(FlightMovement.class), eq(false)))
            .thenReturn(flightMovementValidationViewModel);
        when(systemConfigurationService.getCurrentValue(any()))
            .thenReturn("scheduled");

        flightMovement = flightMovementBuilder.createFlightMovementFromTowerMovementLog(towerMovementLog);
        assertThat(flightMovement.getFlightId())
            .isEqualTo(towerMovementLog.getFlightId());

    }

    /**
     * This method test the creation of the FlightMovement by FlightMovemntBuilder that use a TowerMovementLog
     */
    @Test
    public void createFlightMovementFromAtcLog() throws FlightMovementBuilderException {

        // 1 Test Case:  TowerMovement is Null - Flight Movement is Null
        FlightMovement flightMovement = flightMovementBuilder.createFlightMovementFromAtcMovementLog(null);
        assertThat(flightMovement)
            .isNull();


        //2 Test Case: TowerMovement is not valid - Flight Movement is Null
        AtcMovementLog atcMovementLog = new AtcMovementLog();
        atcMovementLog.setRegistration("TST REG");
        atcMovementLog.setOperatorIdentifier("OPRI");
        atcMovementLog.setDepartureTime("1234");

        try {
            flightMovement = flightMovementBuilder.createFlightMovementFromAtcMovementLog(atcMovementLog);
            fail("Expected `VALIDATION_ERROR` for Atc Movement Log '" + atcMovementLog
                + "', returned Flight Movement '" + flightMovement + "' instead.");
        }catch(CustomParametrizedException e){
            assertThat(e.getErrorDTO().getRejectedReasons())
                .isEqualTo(RejectedReasons.VALIDATION_ERROR);
        }


        //3 Test Case: RadarSummary is valid - Flight Movement is created
        atcMovementLog.setDepartureAerodrome("DEP");
        atcMovementLog.setDestinationAerodrome("DEST");
        atcMovementLog.setRoute("DCT");
        atcMovementLog.setDayOfFlight(LocalDateTime.now());
        atcMovementLog.setDateOfContact(LocalDateTime.now());
        atcMovementLog.setFlightId("FLIGHTID");

        when(flightMovementValidator.validateFlightMovementType(any()))
            .thenReturn(flightMovementValidationViewModel);
        when(flightMovementValidator.validateFlightMovementCategory(any()))
            .thenReturn(flightMovementValidationViewModel);
        when(flightMovementValidator.validateFlightMovementStatus(any(FlightMovement.class), eq(false)))
            .thenReturn(flightMovementValidationViewModel);
        when(systemConfigurationService.getCurrentValue(any()))
            .thenReturn("scheduled");

        flightMovement = flightMovementBuilder.createFlightMovementFromAtcMovementLog(atcMovementLog);
        assertThat(flightMovement.getFlightId())
            .isEqualTo(atcMovementLog.getFlightId());
    }

    /**
     * The purpose of this test case is to test the method checkAndResolveFlightMovement() of the
     * {FlightMovementBuilder} class. Because that method is private, we'll call the createUpdateFlightMovementFromUI.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void checkAndResolveFlightMovement() throws FlightMovementBuilderException {
        String existingSystemConfiguration = "scheduled";

        final FlightMovement flightMovement = new FlightMovement();
        flightMovement.setFlightId("FID");
        flightMovement.setDateOfFlight(LocalDateTime.of(2017,3,29,16,4));
        flightMovement.setDepAd("A");
        flightMovement.setDepTime("1604");
        flightMovement.setDestAd("B");
        flightMovement.setFplRoute("FPL_ROUTE");
        flightMovement.setManuallyChangedFields("");

        final SystemConfiguration existingSystemConfigurationEntity = new SystemConfiguration();
        existingSystemConfigurationEntity.setCurrentValue(existingSystemConfiguration);

        final FlightMovementValidationViewModel fmDto = new FlightMovementValidationViewModel();
        fmDto.setFlightmovementType(FlightmovementCategoryType.DOMESTIC);
        FlightmovementCategory fmc = new FlightmovementCategory();
    	fmc.setName("DOMESTIC");
    	fmc.setId(1);
    	fmDto.setFlightmovementCategory(fmc);
    	fmDto.setMovementType(FlightMovementType.DOMESTIC);
        final RouteCacheVO routeCacheVO = buildRouteCacheVO();

        final FlightMovementValidationViewModel status = new FlightMovementValidationViewModel();
        status.setStatus(FlightMovementStatus.PENDING);

        when(flightMovementBuilderUtility.resolveAccountForFlightMovement(any(FlightMovement.class)))
            .thenReturn(buildAccount());
        when(flightMovementBuilderUtility.getAccountByAccountId(anyInt()))
            .thenReturn(buildAccount());
        when(flightMovementBuilderUtility.checkAircraftType(any(FlightMovement.class)))
            .thenReturn("ACTYPE");
        when(flightMovementBuilderUtility.checkMTOW(any(FlightMovement.class)))
            .thenReturn(1.0);
        when(flightMovementBuilderUtility.checkAverageMTOW(Matchers.eq(1.0)))
            .thenReturn(1.1);
        when(flightMovementBuilderUtility.checkWakeTurbolance(Matchers.eq("ACTYPE")))
            .thenReturn("WAKET");
        when(flightMovementBuilderUtility.mergeTheSegmentsList(any(), any(), any()))
            .thenReturn(routeCacheSegmentMapper.toRouteSegmentLst(routeCacheVO.getRouteSegmentList()));
        when(deltaFlightUtility.isDeltaFlight(any(FlightMovement.class)))
            .thenReturn(true);
        when(flightMovementValidator.validateFlightMovementCategory(any(FlightMovement.class)))
            .thenReturn(fmDto);
        when(flightMovementValidator.validateFlightMovementStatus(any(FlightMovement.class),any(Boolean.class)))
            .thenReturn(status);
        when(chargesUtility.setCharges(any()))
            .thenReturn(Boolean.TRUE);
        when(deltaFlightUtility.constructDeltaRoute(any(FlightMovement.class)))
            .thenReturn("route");
        when(flightMovementBuilderUtility.checkAerodrome(anyString(), anyString(), anyBoolean()))
            .thenReturn("A");
        when(flightMovementBuilderUtility.getRouteInformationByRouteParser(anyString(), anyString(), anyString(),
            any(SegmentType.class), anyString(), anyString()))
            .thenReturn(routeCacheVO);
        when(deltaFlightUtility.getDeltaRouteSegmentList(any(), anyString()))
            .thenReturn(routeCacheVO);
        when(systemConfigurationService.getCurrentValue(any()))
            .thenReturn(existingSystemConfiguration);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.CROSSING_DISTANCE_PRECEDENCE))
            .thenReturn (existingSystemConfigurationEntity);
        when(accountService.getOne(any()))
            .thenReturn(buildAccount());

        flightMovementBuilder.createUpdateFlightMovementFromUI(flightMovement,false);

        // assert that billing centre is hq since aerodrome A is unknown
        assertThat(flightMovement.getBillingCenter())
            .isEqualTo(this.billingCenterDefault);

        assertThat(flightMovement.getSource())
            .isEqualTo(FlightMovementSource.MANUAL);
        assertThat(flightMovement.getCreatedAt())
            .isNotNull();
        assertThat(flightMovement.getAccount().getId())
            .isEqualTo(1);
        assertThat(flightMovement.getAircraftType())
            .isEqualTo("ACTYPE");
        assertThat(flightMovement.getActualMtow())
            .isEqualTo(1.0);
        assertThat(flightMovement.getAverageMassFactor())
            .isEqualTo(1.1);
        assertThat(flightMovement.getWakeTurb())
            .isEqualTo("WAKET");
        assertThat(flightMovement.getDeltaFlight())
            .isEqualTo(true);
        assertThat(flightMovement.getMovementType())
            .isEqualTo(FlightMovementType.DOMESTIC);
        assertThat(flightMovement.getFlightCategoryType())
            .isEqualTo(FlightmovementCategoryType.DOMESTIC);
        assertThat(flightMovement.getBillableRoute())
            .isEqualTo(routeCacheVO.getRoute());
        assertThat(flightMovement.getBillableEntryPoint())
            .isEqualTo("START_SEGMENT");
        assertThat(flightMovement.getBillableExitPoint())
            .isEqualTo("END_SEGMENT");
        assertThat(flightMovement.getBillableCrossingDist())
            .isEqualTo(7.0);
        assertThat(flightMovement.getStatus())
            .isEqualTo(status.getStatus());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testRemovingFLfromRoute() {

        String actual = RouteCacheUtility.removeFlightLevelsAndNormalize(null);
        assertThat(actual)
            .isNull();

        actual = RouteCacheUtility.removeFlightLevelsAndNormalize("");
        assertThat(actual)
            .isEqualTo("");

        actual = RouteCacheUtility.removeFlightLevelsAndNormalize("ABCD/250 /250 /250 /250 EFGH/250");
        assertThat(actual)
            .isEqualTo("ABCD EFGH");

        actual = RouteCacheUtility.removeFlightLevelsAndNormalize("/123/456 TEST/123 TEST /123 /12A TEST /1234/123 ");
        assertThat(actual)
            .isEqualTo("/123 TEST TEST /12A TEST /1234");
    }

    private Account buildAccount() {
        final Account account = new Account();
        account.setId(1);
        account.setName("Account name");
        account.setIcaoCode("ICAO");
        account.setOprIdentifier("OPR");
        return account;
    }

    private RouteCacheVO buildRouteCacheVO() {

        final List<RouteSegmentVO> routeSegments = new ArrayList<>();
        routeSegments.add(buildRouteSegmentVO(1));
        routeSegments.add(buildRouteSegmentVO(2));

        final RouteCacheVO routeCacheVO = new RouteCacheVO();
        routeCacheVO.setDistance(7.0);
        routeCacheVO.setRoute(new GeometryFactory()
            .createPoint(new Coordinate(1, 1)));
        routeCacheVO.setRouteSegmentList(routeSegments);
        routeCacheVO.setValid(true);
        return routeCacheVO;
    }

    private RouteSegmentVO buildRouteSegmentVO(final Integer number) {
        final RouteSegmentVO routeSegmentVO = new RouteSegmentVO();
        routeSegmentVO.setSegmentType(SegmentType.SCHED);
        routeSegmentVO.setSegmentNumber(number);
        routeSegmentVO.setSegmentStartLabel("START_SEGMENT");
        routeSegmentVO.setSegmentEndLabel("END_SEGMENT");
        return routeSegmentVO;
    }
}
