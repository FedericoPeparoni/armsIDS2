package ca.ids.abms.modules.flightmovements;

import ca.ids.abms.modules.aerodromeoperationalhours.AerodromeOperationalHoursService;
import ca.ids.abms.modules.aerodromes.AerodromeService;
import ca.ids.abms.modules.aircraft.AircraftTypeService;
import ca.ids.abms.modules.atcmovements.AtcMovementLog;
import ca.ids.abms.modules.charges.PassengerServiceChargeReturn;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilder;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementMerge;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementValidator;
import ca.ids.abms.modules.flightmovementsbuilder.utility.DeltaFlightUtility;
import ca.ids.abms.modules.flightmovementsbuilder.utility.FlightMovementBuilderUtility;
import ca.ids.abms.modules.flightmovementsbuilder.utility.ThruFlightPlanUtility;
import ca.ids.abms.modules.plugins.PluginService;
import ca.ids.abms.modules.radarsummary.RadarSummary;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.towermovements.TowerMovementLog;
import ca.ids.abms.modules.transactions.TransactionService;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.modules.util.models.WhitelistingUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FlightMovementServiceFindFromOtherSourceTest {

    private FlightMovementService flightMovementService;

    private FlightMovementRepository flightMovementRepository;
    private SystemConfigurationService systemConfigurationService;

    private SystemConfiguration systemConfigurationMin;
    private SystemConfiguration systemConfigurationEet;
    private List<FlightMovement> flightMovementList;
    private FlightMovementBuilderUtility flightMovementBuilderUtility;
    private FlightMovementAerodromeService flightMovementAerodromeService;
    private AerodromeService aerodromeService;

    private Sort sort;

    @Before
    public void setup() {
        AircraftTypeService aircraftTypeService = mock(AircraftTypeService.class);
        FlightMovementBuilder flightMovementBuilder = mock(FlightMovementBuilder.class);
        FlightMovementValidator flightMovementValidator = mock(FlightMovementValidator.class);
        ThruFlightPlanUtility thruFlightPlanUtility = mock(ThruFlightPlanUtility.class);
        flightMovementAerodromeService = mock(FlightMovementAerodromeService.class);
        aerodromeService = mock(AerodromeService.class);

        flightMovementRepository = mock(FlightMovementRepository.class);
        systemConfigurationService= mock(SystemConfigurationService.class);
        flightMovementBuilderUtility = mock(FlightMovementBuilderUtility.class);

        FlightMovementMerge flightMovementMerge = new FlightMovementMerge(
            mock(DeltaFlightUtility.class), mock(ThruFlightPlanUtility.class));

        flightMovementService = new FlightMovementService(flightMovementRepository, flightMovementAerodromeService,
            aircraftTypeService, flightMovementBuilder, flightMovementValidator, flightMovementBuilderUtility,
            thruFlightPlanUtility, systemConfigurationService, flightMovementMerge,
            mock(FlightMovementRepositoryUtility.class), mock(AerodromeOperationalHoursService.class),
            mock(CurrencyUtils.class), mock(TransactionService.class), mock(WhitelistingUtils.class), mock(PluginService.class), null);


        // Create some utility object
        systemConfigurationMin = new SystemConfiguration();
        systemConfigurationMin.setItemName(SystemConfigurationItemName.DEP_TIME_RANGE_MIN);
        systemConfigurationEet = new SystemConfiguration();
        systemConfigurationEet.setItemName(SystemConfigurationItemName.DEP_TIME_RANGE_EET_PERCENTAGE);

        LocalDateTime dateOfFlight=LocalDateTime.of(2017,3,20,0,0);

        flightMovementList = new ArrayList<>();
        FlightMovement flightMovement = new FlightMovement();
        flightMovement.setFlightId("TAL001");
        flightMovement.setDateOfFlight(dateOfFlight);

        flightMovementList.add(flightMovement);

        sort = new Sort(Sort.Direction.DESC, "dateOfFlight", "depTime");
    }

    @Test
    public void testFindFlightMovementByLogicalKeyWithoutTimeRange() {

        // Test Case 1
        List<FlightMovement> flightMovementListReturn = flightMovementService.findAllByLogicalKey(null, null, null, null);
        Assert.assertTrue(flightMovementListReturn==null || flightMovementListReturn.isEmpty());

        // Test Case 2
        String flightId = "TAL001";
        LocalDateTime dateOfFlight = LocalDate.now().atStartOfDay();
        flightMovementListReturn = flightMovementService.findAllByLogicalKey(flightId, dateOfFlight, null, null);
        Assert.assertTrue(flightMovementListReturn == null || flightMovementListReturn.isEmpty());

        // Test Case 3 : We have all information but we don't have any FlightMovement
        String depTime = "1234";
        String depAd = "FBSK";
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.DEP_TIME_RANGE_MIN)).thenReturn(null);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.DEP_TIME_RANGE_EET_PERCENTAGE)).thenReturn(null);
        when(flightMovementRepository.findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(any(),any(),any(),any(),any())).thenReturn(null);
        flightMovementListReturn = flightMovementService.findAllByLogicalKey(flightId, dateOfFlight, depTime, depAd);
        Assert.assertNull(flightMovementListReturn);

        // Test Case 4 : We have all information but we don't have any FlightMovement
        systemConfigurationMin.setCurrentValue("0");
        systemConfigurationEet.setCurrentValue("0");
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.DEP_TIME_RANGE_MIN)).thenReturn(systemConfigurationMin);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.DEP_TIME_RANGE_EET_PERCENTAGE)).thenReturn(systemConfigurationEet);
        when(flightMovementRepository.findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(any(),any(),any(),any(),any())).thenReturn(null);
        flightMovementListReturn = flightMovementService.findAllByLogicalKey(flightId, dateOfFlight, depTime, depAd);
        Assert.assertNull(flightMovementListReturn);

        // Test Case 5 : We have all information but we have a FlightMovements
        systemConfigurationMin.setCurrentValue("0");
        systemConfigurationEet.setCurrentValue("0");
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.DEP_TIME_RANGE_MIN)).thenReturn(systemConfigurationMin);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.DEP_TIME_RANGE_EET_PERCENTAGE)).thenReturn(systemConfigurationEet);
        when(flightMovementRepository.findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(any(), eq(flightId), any(), eq(depTime), eq(depAd))).thenReturn(flightMovementList);
        flightMovementListReturn = flightMovementService.findAllByLogicalKey(flightId,dateOfFlight,depTime,depAd);
        Assert.assertNotNull(flightMovementListReturn);
        Assert.assertTrue(flightMovementListReturn.get(0).getFlightId().equalsIgnoreCase(flightId));
    }

    @Test
    public void testFindFlightMovementByLogicalKeyWithTimeRange() {

        // Test Case 1
        String flightId = "TAL001";
        LocalDateTime dateOfFlight = LocalDateTime.of(2017,3,20,12,20);
        String depTime = "1234";
        String depAd = "FBSK";
        systemConfigurationMin.setCurrentValue("20"); // min time range 20 minutes
        systemConfigurationEet.setCurrentValue("50"); // 50 percent eet time range
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.DEP_TIME_RANGE_MIN)).thenReturn(systemConfigurationMin);
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.DEP_TIME_RANGE_EET_PERCENTAGE)).thenReturn(systemConfigurationEet);
        List<FlightMovement> flightMovementListReturn = flightMovementService.findAllByLogicalKey(flightId,dateOfFlight,depTime,depAd);
        Assert.assertNotNull(flightMovementListReturn);
        Assert.assertTrue(flightMovementListReturn.isEmpty());
    }

    @Test
    public void testFindFlightMovementFromRadarSummary() {

        // Test Case 1 - throw exception radar summary is null
        try {
            flightMovementService.findFlightMovementFromRadarSummary(null);
        } catch (IllegalArgumentException ignored) {}

        //Test Case 2 - throw exception we don't have all information
        LocalDateTime dateOfFlight=LocalDateTime.of(2017,3,20,0,0);
        RadarSummary radarSummary = new RadarSummary();
        radarSummary.setFlightIdentifier("TAL001");
        radarSummary.setDate(dateOfFlight);
        flightMovementService.findFlightMovementFromRadarSummary(radarSummary);

        // Test Case 3 - we have all information but we don't have any flight movemnt
        dateOfFlight = LocalDateTime.of(2017,3,20,12,20);
        radarSummary = new RadarSummary();
        radarSummary.setFlightIdentifier("TAL001");
        radarSummary.setDayOfFlight(dateOfFlight);
        radarSummary.setDepartureTime("1234");
        radarSummary.setDepartureAeroDrome("FBSK");

        when(flightMovementRepository.findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(any(),eq("TAL001"),eq(dateOfFlight), eq("1234"),eq("FBSK"))).thenReturn(null);
        FlightMovement flightMovementReturn = flightMovementService.findFlightMovementFromRadarSummary(radarSummary);
        Assert.assertNull(flightMovementReturn);

        // Test Case 4 - we have all information and we have any flight movement
        dateOfFlight = LocalDateTime.of(2017,3,20,12,20);
        radarSummary = new RadarSummary();
        radarSummary.setFlightIdentifier("TAL001");
        radarSummary.setDayOfFlight(dateOfFlight);
        radarSummary.setDepartureTime("1234");
        radarSummary.setDepartureAeroDrome("FBSK");

        when(flightMovementRepository.findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(any(),eq("TAL001"),eq(dateOfFlight), eq("1234"),eq("FBSK"))).thenReturn(flightMovementList);
        flightMovementReturn = flightMovementService.findFlightMovementFromRadarSummary(radarSummary);
        Assert.assertTrue(flightMovementReturn.getFlightId().equalsIgnoreCase(radarSummary.getFlightIdentifier()));

        // Test Case 4 - we have all information and we have any flight movement on day before
        dateOfFlight = LocalDateTime.of(2017,3,20,12,20);
        radarSummary=new RadarSummary();
        radarSummary.setFlightIdentifier("TAL001");
        radarSummary.setDayOfFlight(dateOfFlight);
        radarSummary.setDepartureTime("1234");
        radarSummary.setDepartureAeroDrome("FBSK");

        when(flightMovementRepository.findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(any(),eq("TAL001"),eq(dateOfFlight), eq("1234"),eq("FBSK"))).thenReturn(null);
        when(flightMovementRepository.findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(any(),eq("TAL001"),eq(dateOfFlight), eq("1234"),eq("FBSK"))).thenReturn(flightMovementList);
        flightMovementReturn = flightMovementService.findFlightMovementFromRadarSummary(radarSummary);
        Assert.assertTrue(flightMovementReturn.getFlightId().equalsIgnoreCase(radarSummary.getFlightIdentifier()));
    }

    @Test
    public void testFindFlightMovementFromTowerMovementLog() throws FlightMovementBuilderException {

        // Test Case 1 - throw exception TowerMovemntLog is null
        try {
            flightMovementService.findFlightMovementFromTowerMovementLog(null);
        } catch (IllegalArgumentException ignored) {}

        // Test Case 2 - throw exception TowerMovementLog we don't have some information
        TowerMovementLog towerMovementLog = new TowerMovementLog();
        LocalDateTime dateOfFlight = LocalDateTime.of(2017,3,20,0,0);
        towerMovementLog.setFlightId("TAL001");
        towerMovementLog.setDateOfContact(dateOfFlight);
        try {
            flightMovementService.findFlightMovementFromTowerMovementLog(towerMovementLog);
        } catch (FlightMovementBuilderException ignored) {}

        // Test Case 3 -  we have depTime but we don't have flight
        towerMovementLog = new TowerMovementLog();
        dateOfFlight = LocalDateTime.of(2017,3,20,0,0);
        towerMovementLog.setFlightId("TAL001");
        towerMovementLog.setDateOfContact(dateOfFlight);
        towerMovementLog.setDepartureAerodrome("FBSK");
        towerMovementLog.setDestinationAerodrome("FBMN");
        towerMovementLog.setDepartureContactTime("1234");

        when(flightMovementRepository.findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(any(),eq("TAL001"),eq(dateOfFlight), eq("1234"),eq("FBSK"))).thenReturn(null);
        FlightMovement flightMovementReturn = flightMovementService.findFlightMovementFromTowerMovementLog(towerMovementLog);
        Assert.assertNull(flightMovementReturn);

        // Test Case 4 - we have depTime and flight
        towerMovementLog = new TowerMovementLog();
        dateOfFlight = LocalDateTime.of(2017,3,20,0,0);
        towerMovementLog.setFlightId("TAL001");
        towerMovementLog.setDateOfContact(dateOfFlight);
        towerMovementLog.setDepartureAerodrome("FBSK");
        towerMovementLog.setDestinationAerodrome("FBMN");
        towerMovementLog.setDepartureContactTime("1234");

        when(flightMovementBuilderUtility.checkAerodrome("FBSK", true)).thenReturn("FBSK");
        when(flightMovementAerodromeService.resolveAerodrome("FBSK", true)).thenReturn("FBSK");
        when(aerodromeService.checkAerodromeIdentifier("FBSK", true, false)).thenReturn("FBSK");
        when(flightMovementRepository.findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(any(),eq("TAL001"),eq(dateOfFlight), eq("1234"),eq("FBSK"))).thenReturn(flightMovementList);
        flightMovementReturn = flightMovementService.findFlightMovementFromTowerMovementLog(towerMovementLog);
        Assert.assertTrue(flightMovementReturn.getFlightId().equalsIgnoreCase(towerMovementLog.getFlightId()));

        // Test Case 5 - we have depTime
        towerMovementLog = new TowerMovementLog();
        dateOfFlight = LocalDateTime.of(2017,3,20,0,0);
        towerMovementLog.setFlightId("TAL001");
        towerMovementLog.setDateOfContact(dateOfFlight);
        towerMovementLog.setDepartureAerodrome("FBSK");
        towerMovementLog.setDestinationAerodrome("FBMN");
        towerMovementLog.setDepartureContactTime("1234");

        when(flightMovementRepository.findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(any(),eq("TAL001"),eq(dateOfFlight), eq("1234"),eq("FBSK"))).thenReturn(null);
        when(flightMovementRepository.findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(any(),eq("TAL001"),eq(dateOfFlight), eq("1234"),eq("FBSK"))).thenReturn(flightMovementList);
        flightMovementReturn = flightMovementService.findFlightMovementFromTowerMovementLog(towerMovementLog);
        Assert.assertTrue(flightMovementReturn.getFlightId().equalsIgnoreCase(towerMovementLog.getFlightId()));
    }

    @Test
    public void testFindFlightMovementFromTowerMovementLogTowerArrival() throws FlightMovementBuilderException {

        // Test Case 1 - we don't have depTime and we don't have flight
        TowerMovementLog towerMovementLog = new TowerMovementLog();
        LocalDateTime dateOfFlight=LocalDateTime.of(2017,3,20,0,0);
        towerMovementLog.setFlightId("TAL001");
        towerMovementLog.setDateOfContact(dateOfFlight);
        towerMovementLog.setDepartureAerodrome("FBSK");
        towerMovementLog.setDestinationAerodrome("FBMN");
        towerMovementLog.setDestinationContactTime("1634");
        LocalDateTime dayOfContactBefore=dateOfFlight.minusDays(1);

        when(flightMovementRepository.findAllByFlightIdAndDepAdAndDateOfFlight(any(),eq("TAL001"), eq("FBSK"), eq(dayOfContactBefore), eq(dateOfFlight))).thenReturn(null);
        FlightMovement flightMovementReturn = flightMovementService.findFlightMovementFromTowerMovementLog(towerMovementLog);
        Assert.assertNull(flightMovementReturn);

        // Test Case 2 - we don't have depTime and we have flight on day before. the flights don't have EET
        towerMovementLog = new TowerMovementLog();
        dateOfFlight = LocalDateTime.of(2017,3,20,0,0);
        towerMovementLog.setFlightId("TAL001");
        towerMovementLog.setDateOfContact(dateOfFlight);
        towerMovementLog.setDepartureAerodrome("FBSK");
        towerMovementLog.setDestinationAerodrome("FBMN");
        towerMovementLog.setDestinationContactTime("1634");
        dayOfContactBefore=dateOfFlight.minusDays(1);

        when(flightMovementRepository.findAllByFlightIdAndDepAdAndDateOfFlight(any(),eq("TAL001"), eq("FBSK"), eq(dayOfContactBefore), eq(dateOfFlight))).thenReturn(flightMovementList);
        flightMovementReturn = flightMovementService.findFlightMovementFromTowerMovementLog(towerMovementLog);
        Assert.assertNull(flightMovementReturn);

        // Test Case 3 - we don't have depTime. The flights on day before don't meet the search criteria
        towerMovementLog = new TowerMovementLog();
        dateOfFlight=LocalDateTime.of(2017,3,20,0,0);
        towerMovementLog.setFlightId("TAL001");
        towerMovementLog.setDateOfContact(dateOfFlight);
        towerMovementLog.setDepartureAerodrome("FBSK");
        towerMovementLog.setDestinationAerodrome("FBMN");
        towerMovementLog.setDestinationContactTime("1634");
        dayOfContactBefore=dateOfFlight.minusDays(1);

        FlightMovement flightMovement = new FlightMovement();
        flightMovement.setFlightId("TAL001");
        flightMovement.setEstimatedElapsedTime("0120");
        flightMovement.setDateOfFlight(dayOfContactBefore);
        flightMovementList.add(flightMovement);

        when(flightMovementRepository.findAllByFlightIdAndDepAdAndDateOfFlight(any(),eq("TAL001"), eq("FBSK"), eq(dayOfContactBefore), eq(dateOfFlight))).thenReturn(flightMovementList);
        when(flightMovementRepository.findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(any(),eq("TAL001"),eq(dayOfContactBefore), eq("1514"),eq("FBSK"))).thenReturn(null);
        flightMovementReturn = flightMovementService.findFlightMovementFromTowerMovementLog(towerMovementLog);
        Assert.assertNull(flightMovementReturn);


        // Test Case 4 - we don't have depTime and we have flight on day before. the flights have EET and is
        // (ContacTime - EET) is before depTime
        towerMovementLog = new TowerMovementLog();
        dateOfFlight=LocalDateTime.of(2017,3,20,0,0);
        towerMovementLog.setFlightId("TAL001");
        towerMovementLog.setDateOfContact(dateOfFlight);
        towerMovementLog.setDepartureAerodrome("FBSK");
        towerMovementLog.setDestinationAerodrome("FBMN");
        towerMovementLog.setDestinationContactTime("1014");
        dayOfContactBefore=dateOfFlight.minusDays(1);

        when(flightMovementRepository.findAllByFlightIdAndDepAdAndDateOfFlight(any(),eq("TAL001"), eq("FBSK"), eq(dayOfContactBefore), eq(dateOfFlight))).thenReturn(flightMovementList);
        when(flightMovementRepository.findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(any(),eq("TAL001"),eq(dayOfContactBefore), eq("1514"),eq("FBSK"))).thenReturn(flightMovementList);
        flightMovementReturn = flightMovementService.findFlightMovementFromTowerMovementLog(towerMovementLog);
        Assert.assertNull(flightMovementReturn);

        // Test Case 5 - we don't have depTime and we have flight on day before.
        towerMovementLog = new TowerMovementLog();
        dateOfFlight=LocalDateTime.of(2017,3,20,0,0);
        towerMovementLog.setFlightId("TAL001");
        towerMovementLog.setDateOfContact(dateOfFlight);
        towerMovementLog.setDepartureAerodrome("FBSK");
        towerMovementLog.setDestinationAerodrome("FBMN");
        towerMovementLog.setDestinationContactTime("1634");
        dayOfContactBefore=dateOfFlight.minusDays(1);

        when(flightMovementBuilderUtility.checkAerodrome("FBSK", true)).thenReturn("FBSK");
        when(flightMovementBuilderUtility.checkAerodrome("FBMN", false)).thenReturn("FBMN");

        when(flightMovementAerodromeService.resolveAerodrome("FBSK", true)).thenReturn("FBSK");
        when(aerodromeService.checkAerodromeIdentifier("FBSK", true, false)).thenReturn("FBSK");

        when(flightMovementRepository.findAllByFlightIdAndDepAdAndDateOfFlight(any(),eq("TAL001"), eq("FBSK"), eq(dayOfContactBefore), eq(dateOfFlight))).thenReturn(flightMovementList);
        when(flightMovementRepository.findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(eq(sort),eq("TAL001"),eq(dateOfFlight), eq("1514"),eq("FBSK"))).thenReturn(flightMovementList);
        flightMovementReturn = flightMovementService.findFlightMovementFromTowerMovementLog(towerMovementLog);
        assertThat(flightMovementReturn).isNotNull();
        assertThat(flightMovementReturn.getFlightId().toUpperCase()).isEqualTo(towerMovementLog.getFlightId().toUpperCase());
    }

    @Test
    public void testFindFlightMovementFromAtcMovementLog() throws FlightMovementBuilderException {

        // Test Case 1 - throw IllegalArgumentException when AtcMovementLog is null
        try {
            flightMovementService.findFlightMovementFromAtcMovementLog(null);
        } catch (IllegalArgumentException ignored) {}

        // Test Case 2 - throw FlightMovementBuilderException when AtcMovementLog missing mandatory information
        AtcMovementLog atcMovementLog = new AtcMovementLog();
        LocalDateTime dateOfFlight = LocalDateTime.of(2017,3,20,0,0);
        atcMovementLog.setFlightId("TAL001");
        atcMovementLog.setDateOfContact(dateOfFlight);
        try {
            flightMovementService.findFlightMovementFromAtcMovementLog(atcMovementLog);
        } catch (FlightMovementBuilderException ignored) {}

        // Test Case 3 - throw exception TowerMovementLog we have depTime but we don't have flight
        atcMovementLog = new AtcMovementLog();
        dateOfFlight = LocalDateTime.of(2017,3,20,0,0);
        atcMovementLog.setFlightId("TAL001");
        atcMovementLog.setDateOfContact(dateOfFlight);
        atcMovementLog.setDepartureAerodrome("FBSK");
        atcMovementLog.setDestinationAerodrome("FBMN");
        atcMovementLog.setDepartureTime("1234");

        when(flightMovementRepository.findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(any(),eq("TAL001"),eq(dateOfFlight), eq("1234"),eq("FBSK"))).thenReturn(null);
        FlightMovement flightMovementReturn = flightMovementService.findFlightMovementFromAtcMovementLog(atcMovementLog);
        Assert.assertNull(flightMovementReturn);

        // Test Case 4 - throw exception TowerMovementLog we have depTime
        atcMovementLog = new AtcMovementLog();
        dateOfFlight = LocalDateTime.of(2017,3,20,0,0);
        atcMovementLog.setFlightId("TAL001");
        atcMovementLog.setDateOfContact(dateOfFlight);
        atcMovementLog.setDepartureAerodrome("FBSK");
        atcMovementLog.setDestinationAerodrome("FBMN");
        atcMovementLog.setDepartureTime("1234");

        when(flightMovementBuilderUtility.checkAerodrome("FBSK", true)).thenReturn("FBSK");
        when(flightMovementAerodromeService.resolveAerodrome("FBSK", true)).thenReturn("FBSK");
        when(aerodromeService.checkAerodromeIdentifier("FBSK", true, true)).thenReturn("FBSK");

        when(flightMovementRepository.findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(any(),eq("TAL001"),eq(dateOfFlight), eq("1234"),eq("FBSK"))).thenReturn(flightMovementList);
        flightMovementReturn = flightMovementService.findFlightMovementFromAtcMovementLog(atcMovementLog);
        Assert.assertTrue(flightMovementReturn.getFlightId().equalsIgnoreCase(atcMovementLog.getFlightId()));

        // Test Case 5 - throw exception TowerMovementLog we have depTime
        atcMovementLog = new AtcMovementLog();
        dateOfFlight = LocalDateTime.of(2017,3,20,0,0);
        atcMovementLog.setFlightId("TAL001");
        atcMovementLog.setDateOfContact(dateOfFlight);
        atcMovementLog.setDepartureAerodrome("FBSK");
        atcMovementLog.setDestinationAerodrome("FBMN");
        atcMovementLog.setDepartureTime("1234");
        try {
            when(flightMovementRepository.findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(any(),eq("TAL001"),eq(dateOfFlight), eq("1234"),eq("FBSK"))).thenReturn(null);
            when(flightMovementRepository.findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(any(),eq("TAL001"),eq(dateOfFlight), eq("1234"),eq("FBSK"))).thenReturn(flightMovementList);
            flightMovementReturn = flightMovementService.findFlightMovementFromAtcMovementLog(atcMovementLog);
            Assert.assertTrue(flightMovementReturn.getFlightId().equalsIgnoreCase(atcMovementLog.getFlightId()));
        } catch (FlightMovementBuilderException e) {
            Assert.assertTrue(Boolean.FALSE);
        }

        // Test Case 6 - throw exception TowerMovementLog we have don't depTime
        atcMovementLog = new AtcMovementLog();
        dateOfFlight = LocalDateTime.of(2017,3,20,0,0);
        atcMovementLog.setFlightId("TAL001");
        atcMovementLog.setDateOfContact(dateOfFlight);
        atcMovementLog.setDepartureAerodrome("FBSK");
        atcMovementLog.setDestinationAerodrome("FBMN");
        atcMovementLog.setFirEntryTime("1634");

        flightMovementReturn = flightMovementService.findFlightMovementFromAtcMovementLog(atcMovementLog);
        Assert.assertNull(flightMovementReturn);
    }

    @Test
    public void testFindFlightMovementByPassengerServiceChargeReturn() throws FlightMovementBuilderException {

        // Test Case 1 - throw exception PassengerServiceChargeReturn is null
        try {
            flightMovementService.findFlightMovementByPassengerServiceChargeReturn(null, false);
        } catch (IllegalArgumentException ignored) {}

        // Test Case 2 - throw exception PassengerServiceChargeReturn don't has day-of-flight
        PassengerServiceChargeReturn passengerServiceChargeReturn = new PassengerServiceChargeReturn();
        passengerServiceChargeReturn.setFlightId("TAL001");

        flightMovementService.findFlightMovementByPassengerServiceChargeReturn(passengerServiceChargeReturn, false);

        // Test Case 3 - there aren't any flight
        LocalDateTime dateOfFlight=LocalDateTime.now();
        passengerServiceChargeReturn=new PassengerServiceChargeReturn();
        passengerServiceChargeReturn.setFlightId("TAL001");
        passengerServiceChargeReturn.setDayOfFlight(dateOfFlight);

        when(flightMovementRepository.findAllByFlightIdAndDateOfFlight(eq("TAL001"),eq(dateOfFlight))).thenReturn(null);
        List<FlightMovement> flightMovementListReturn = flightMovementService.findFlightMovementByPassengerServiceChargeReturn(passengerServiceChargeReturn, false);
        Assert.assertNull(flightMovementListReturn);

        // Test Case 4
        dateOfFlight=LocalDateTime.now();
        passengerServiceChargeReturn=new PassengerServiceChargeReturn();
        passengerServiceChargeReturn.setFlightId("TAL001");
        passengerServiceChargeReturn.setDayOfFlight(dateOfFlight);

        when(flightMovementRepository.findAllByFlightIdAndDateOfFlight(eq("TAL001"),eq(dateOfFlight))).thenReturn(flightMovementList);
        flightMovementListReturn = flightMovementService.findFlightMovementByPassengerServiceChargeReturn(passengerServiceChargeReturn, false);
        Assert.assertNotNull(flightMovementListReturn);
        Assert.assertEquals(flightMovementListReturn.size(), flightMovementList.size());
    }
}
