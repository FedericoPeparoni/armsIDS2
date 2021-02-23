package ca.ids.abms.modules.flightmovements;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.aerodromeoperationalhours.AerodromeOperationalHoursService;
import ca.ids.abms.modules.aerodromes.AerodromeService;
import ca.ids.abms.modules.aircraft.AircraftType;
import ca.ids.abms.modules.aircraft.AircraftTypeService;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.common.mappers.DateTimeMapperUtils;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementType;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilder;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementMerge;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementValidator;
import ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementValidatorIssue;
import ca.ids.abms.modules.flightmovementsbuilder.utility.DeltaFlightUtility;
import ca.ids.abms.modules.flightmovementsbuilder.utility.FlightMovementBuilderUtility;
import ca.ids.abms.modules.flightmovementsbuilder.utility.ThruFlightPlanUtility;
import ca.ids.abms.modules.plugins.PluginService;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.transactions.TransactionService;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.modules.util.models.WhitelistingUtils;
import ca.ids.abms.util.EnumUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class FlightMovementServiceTest {

    private FlightMovementService flightMovementService;

    private FlightMovementRepository flightMovementRepository;
    private FlightMovementAerodromeService flightMovementAerodromeService;
    private FlightMovementBuilder flightMovementBuilder;
    private AircraftTypeService aircraftTypeService;
    private FlightMovementValidator flightMovementValidator;
    private SystemConfigurationService systemConfigurationService;
    private AerodromeService aerodromeService;

    @Before
    public void setup() {

        flightMovementRepository = mock(FlightMovementRepository.class);
        flightMovementAerodromeService =mock(FlightMovementAerodromeService.class);
        aircraftTypeService = mock(AircraftTypeService.class);
        flightMovementBuilder = mock(FlightMovementBuilder.class);
        flightMovementValidator = mock(FlightMovementValidator.class);
        systemConfigurationService = mock(SystemConfigurationService.class);
        aerodromeService = mock(AerodromeService.class);

        FlightMovementBuilderUtility flightMovementBuilderUtility = mock(FlightMovementBuilderUtility.class);
        ThruFlightPlanUtility thruFlightPlanUtility = mock(ThruFlightPlanUtility.class);
        FlightMovementRepositoryUtility flightMovementRepositoryUtility = mock(FlightMovementRepositoryUtility.class);

        FlightMovementMerge flightMovementMerge = new FlightMovementMerge(
            mock(DeltaFlightUtility.class), mock(ThruFlightPlanUtility.class));

        flightMovementService = new FlightMovementService(flightMovementRepository, flightMovementAerodromeService,
            aircraftTypeService, flightMovementBuilder, flightMovementValidator, flightMovementBuilderUtility,
            thruFlightPlanUtility, systemConfigurationService, flightMovementMerge, flightMovementRepositoryUtility,
            mock(AerodromeOperationalHoursService.class), mock(CurrencyUtils.class),
            mock(TransactionService.class), mock(WhitelistingUtils.class), mock(PluginService.class));

        when(flightMovementRepositoryUtility.persist(any(FlightMovement.class)))
            .thenAnswer(i -> i.getArguments()[0]);
        when(flightMovementRepositoryUtility.overwrite(any(FlightMovement.class)))
            .thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    public void createFlightMovement() throws Exception {
        FlightMovement flightMovement = new FlightMovement();

        flightMovement.setFlightId("TEST1FM");
        flightMovement.setDateOfFlight(LocalDateTime.now());
        flightMovement.setDepTime("1012");
        flightMovement.setDepAd("FYRU");
        flightMovement.setDestAd("FYBG");
        flightMovement.setAircraftType("C210");
        flightMovement.setActualDepartureTime("1234");
        flightMovement.setArrivalTime("1414");
        flightMovement.setArrivalAd("1614");
        flightMovement.setFlightType("L");
        flightMovement.setMovementType(FlightMovementType.OTHER);
        flightMovement.setStatus(FlightMovementStatus.INCOMPLETE);

        when(flightMovementAerodromeService.checkAerodromeIdentifier("FYBG")).thenReturn(flightMovement.getDestAd());

        when(flightMovementAerodromeService.checkAerodromeIdentifier("FYRU", true, true))
            .thenReturn(flightMovement.getDepAd());
        when(aerodromeService.checkAerodromeIdentifier("FYRU", true, true)).thenReturn(flightMovement.getDepAd());

        when(flightMovementRepository.save(any(FlightMovement.class))).thenReturn(flightMovement);
        when(flightMovementBuilder.createUpdateFlightMovementFromUI(any(),any())).thenReturn(flightMovement);

        FlightMovement flightMovementResult = flightMovementService.createFlightMovementFromUI(flightMovement,false);
        assertThat(flightMovementResult.getFlightId()).isEqualTo(flightMovement.getFlightId());
    }


    @Test
    public void updateFlightMovement() throws Exception {
        FlightMovement flightMovement = new FlightMovement();

        flightMovement.setId(1);
        flightMovement.setFlightId("TEST1FM");
        flightMovement.setDateOfFlight(LocalDateTime.now());
        flightMovement.setDepAd("depADTest");
        flightMovement.setDestAd("destAdTest");
        flightMovement.setFlightType("E");
        flightMovement.setMovementType(FlightMovementType.OTHER);
        flightMovement.setStatus(FlightMovementStatus.INCOMPLETE);

        when(flightMovementAerodromeService.checkAerodromeIdentifier("depADTest", true, true))
            .thenReturn(flightMovement.getDepAd());
        when(aerodromeService.checkAerodromeIdentifier("depADTest", true, true)).thenReturn(flightMovement.getDepAd());

        when(flightMovementAerodromeService.checkAerodromeIdentifier("destAdTest")).thenReturn(flightMovement.getDestAd());

        when(flightMovementRepository.findOne(1)).thenReturn(flightMovement);
        when(flightMovementRepository.save(any(FlightMovement.class))).thenReturn(flightMovement);
        when(flightMovementAerodromeService.isAerodromeDomestic("destAdTest")).thenReturn(false);

        FlightMovement flightMovementUpdate = new FlightMovement();
        flightMovementUpdate.setId(1);
        flightMovementUpdate.setStatus(FlightMovementStatus.PENDING);
        flightMovementUpdate.setDepAd("depADTest");
        flightMovementUpdate.setDestAd("destAdTest");
        when(flightMovementBuilder.createUpdateFlightMovementFromUI(any(),any())).thenReturn(flightMovementUpdate);
        when(flightMovementAerodromeService.isCircularRoute(flightMovementUpdate.getDepAd(),"",
                flightMovementUpdate.getDestAd(),"")).thenReturn(false);
        FlightMovement flightMovementResult = flightMovementService.updateFlightMovementFromUI(1,flightMovementUpdate);
        assertThat(flightMovementResult.getStatus()).isEqualTo(flightMovementUpdate.getStatus());
    }


    @Test
    public void updateFlightMovementType() throws Exception {
        FlightMovement flightMovement = new FlightMovement();

        flightMovement.setId(1);
        flightMovement.setFlightId("TEST1FM");
        flightMovement.setDateOfFlight(LocalDateTime.now());
        flightMovement.setDepAd("depADTest");
        flightMovement.setDestAd("destAdTest");
        flightMovement.setFlightType("E");
        flightMovement.setMovementType(FlightMovementType.OTHER);
        flightMovement.setStatus(FlightMovementStatus.INCOMPLETE);

        when(flightMovementAerodromeService.checkAerodromeIdentifier("depADTest", true, true))
            .thenReturn(flightMovement.getDepAd());
        when(aerodromeService.checkAerodromeIdentifier("depADTest", true, true)).thenReturn(flightMovement.getDepAd());

        when(flightMovementAerodromeService.checkAerodromeIdentifier("destAdTest")).thenReturn(flightMovement.getDestAd());

        when(flightMovementRepository.findOne(1)).thenReturn(flightMovement);
        when(flightMovementRepository.save(any(FlightMovement.class))).thenReturn(flightMovement);
        when(flightMovementBuilder.createUpdateFlightMovementFromUI(any(),any())).thenReturn(flightMovement);

        flightMovement.setMovementType(FlightMovementType.INT_ARRIVAL);
        flightMovement.setEstimatedElapsedTime("1000");
        FlightMovement flightMovementResult = flightMovementService.updateFlightMovementFromUI(1, flightMovement);

        assertThat(flightMovementResult.getMovementType()).isEqualTo(FlightMovementType.INT_ARRIVAL);
    }

    @Test
    public void updateFlightMovementStatus() throws FlightMovementBuilderException {
        FlightMovement flightMovement = new FlightMovement();

        flightMovement.setId(1);
        flightMovement.setFlightId("TEST1FM");
        flightMovement.setDateOfFlight(LocalDateTime.now());
        flightMovement.setDepAd("depADTest");
        flightMovement.setDestAd("destAdTest");
        flightMovement.setFlightType("E");
        flightMovement.setMovementType(FlightMovementType.OTHER);
        flightMovement.setStatus(FlightMovementStatus.INCOMPLETE);

        when(flightMovementAerodromeService.checkAerodromeIdentifier("depADTest", true, true))
            .thenReturn(flightMovement.getDepAd());
        when(aerodromeService.checkAerodromeIdentifier("depADTest", true, true)).thenReturn(flightMovement.getDepAd());

        when(flightMovementAerodromeService.checkAerodromeIdentifier("destAdTest")).thenReturn(flightMovement.getDestAd());
        when(flightMovementRepository.findOne(1)).thenReturn(flightMovement);
        when(flightMovementRepository.save(any(FlightMovement.class))).thenReturn(flightMovement);

        flightMovement.setStatus(FlightMovementStatus.PENDING);
        when(flightMovementBuilder.createUpdateFlightMovementFromUI(any(),any())).thenReturn(flightMovement);


        FlightMovement flightMovementResult = flightMovementService.updateFlightMovementFromUI(1, flightMovement);
        assertThat(flightMovementResult.getStatus()).isEqualTo(FlightMovementStatus.PENDING);
    }

    @Test
    public void findFlightMovementById() {

        FlightMovement flightMovement = new FlightMovement();
        flightMovement.setFlightId("TEST1FM");
        flightMovement.setDateOfFlight(LocalDateTime.now());
        flightMovement.setDepAd("depADTest");
        flightMovement.setDestAd("destAdTest");
        flightMovement.setFlightType("E");
        flightMovement.setMovementType(FlightMovementType.OTHER);
        flightMovement.setStatus(FlightMovementStatus.INCOMPLETE);

        when(flightMovementRepository.findOne(1))
            .thenReturn(flightMovement);

        FlightMovement flightMovementResult = flightMovementService.findFlightMovementById(1);
        assertThat(flightMovementResult).isEqualTo(flightMovement);
    }


    @Test
    public void findFlightMovementByFlightId() {

        FlightMovement flightMovement = new FlightMovement();
        flightMovement.setFlightId("TEST1FM");
        flightMovement.setDateOfFlight(LocalDateTime.now());
        flightMovement.setDepAd("depADTest");
        flightMovement.setDestAd("destAdTest");
        flightMovement.setFlightType("E");
        flightMovement.setMovementType(FlightMovementType.OTHER);
        flightMovement.setStatus(FlightMovementStatus.INCOMPLETE);

        when(flightMovementRepository.findByFlightId("TEST1FM"))
            .thenReturn(flightMovement);

        FlightMovement flightMovementResult = flightMovementService.findFlightMovementByFlightId("TEST1FM");
        assertThat(flightMovementResult).isEqualTo(flightMovement);
    }

    @Test
    public void getFlightMovementStatusById() {

        FlightMovement flightMovement = new FlightMovement();
        flightMovement.setFlightId("TEST1FM");
        flightMovement.setDateOfFlight(LocalDateTime.now());
        flightMovement.setDepAd("depADTest");
        flightMovement.setDestAd("destAdTest");
        flightMovement.setFlightType("E");
        flightMovement.setMovementType(FlightMovementType.OTHER);
        flightMovement.setStatus(FlightMovementStatus.INCOMPLETE);

        when(flightMovementRepository.findOne(1))
            .thenReturn(flightMovement);

        FlightMovementStatus status  = flightMovementService.getFlightMovementStatusById(1);
        assertThat(status).isEqualTo(FlightMovementStatus.INCOMPLETE);
    }

    @Test
    public void getAircraftTypeByLatestRegistrationNumber() {

        FlightMovement flightMovement = new FlightMovement();
        flightMovement.setItem18RegNum("F123456");
        flightMovement.setAircraftType("SR71-BLACKBIRD");

        AircraftType aircraftType = new AircraftType();
        aircraftType.setAircraftType("SR71-BLACKBIRD");

        when(flightMovementRepository.findTop1ByItem18RegNumOrderByIdDesc("F123456"))
            .thenReturn(flightMovement);

        when(aircraftTypeService.findByAircraftType("SR71-BLACKBIRD"))
            .thenReturn(aircraftType);

        AircraftType aircraftTypeResult = flightMovementService.getAircraftTypeByLatestRegistrationNumber("F123456");
        assertThat(aircraftTypeResult.getAircraftType()).isEqualTo("SR71-BLACKBIRD");
    }

    @Test
    public void getFlightMovementsByInvoiceId() {

        List<FlightMovement> flightMovements = new ArrayList<>(1);

        FlightMovement flightMovementEnroute = new FlightMovement();
        flightMovementEnroute.setEnrouteInvoiceId(1);

        FlightMovement flightMovementPassenger = new FlightMovement();
        flightMovementPassenger.setPassengerInvoiceId(1);

        FlightMovement flightMovementOther = new FlightMovement();
        flightMovementOther.setOtherInvoiceId(1);

        flightMovements.add(flightMovementEnroute);
        flightMovements.add(flightMovementPassenger);
        flightMovements.add(flightMovementOther);

        when(flightMovementRepository.findAllByAssociatedBillingLedgerId(1)).thenReturn(flightMovements);
        assertThat(flightMovementService.findAllByAssociatedBillingLedgerId(1)).isEqualTo(flightMovements);
    }

    @Test
    public void getFlightMovementsByAccountId() {

        List<FlightMovement> flightMovements = new ArrayList<>(1);

        Account account = new Account();
        account.setId(1);

        FlightMovement flightMovement = new FlightMovement();
        flightMovement.setAccount(account);

        flightMovements.add(flightMovement);

        when(flightMovementRepository.findAllByAccount(eq(account.getId()))).thenReturn(flightMovements);

        List<FlightMovement> results= flightMovementService.findAllFlightMovementByAccount(1);
        assertThat(results.size()).isEqualTo(1);

    }

    @Test
    public void deleteRole() {
        FlightMovement flightMovement = new FlightMovement();
        flightMovement.setFlightId("TEST1FM");
        flightMovement.setDateOfFlight(LocalDateTime.now());
        flightMovement.setDepAd("depADTest");
        flightMovement.setDestAd("destAdTest");
        flightMovement.setFlightType("E");
        flightMovement.setMovementType(FlightMovementType.OTHER);
        flightMovement.setStatus(FlightMovementStatus.INCOMPLETE);

        when(flightMovementRepository.findOne(1)).thenReturn(flightMovement);
        flightMovementService.deleteFlightMovement(1);
        verify(flightMovementRepository).delete(any(Integer.class));
    }

    @Test
    public void testResolutionErrorsConversionFromStringToEnumSet() {
    	// case 1 - multiple values
		EnumSet<FlightMovementValidatorIssue> expected1 = EnumSet.of(
				FlightMovementValidatorIssue.MISSING_MTOW,
				FlightMovementValidatorIssue.UNKNOWN_STATUS,
				FlightMovementValidatorIssue.UNKNOWN_AIRCRAFT_TYPE);

		String resolutionErrorsStr1 = "MISSING_MTOW,UNKNOWN_STATUS, UNKNOWN_AIRCRAFT_TYPE";
		EnumSet<FlightMovementValidatorIssue> actual1 = EnumUtils
				.convertStringToEnumSet(FlightMovementValidatorIssue.class,
						resolutionErrorsStr1);

		assertThat(actual1).isEqualTo(expected1);

		// case 2 - single value
		EnumSet<FlightMovementValidatorIssue> expected2 = EnumSet.of(
				FlightMovementValidatorIssue.UNKNOWN_AIRCRAFT_TYPE);

		String resolutionErrorsStr2 = "UNKNOWN_AIRCRAFT_TYPE";
		EnumSet<FlightMovementValidatorIssue> actual2 = EnumUtils
				.convertStringToEnumSet(FlightMovementValidatorIssue.class,
						resolutionErrorsStr2);

		assertThat(actual2).isEqualTo(expected2);

		// case 3 - fake values, duplicates
		EnumSet<FlightMovementValidatorIssue> expected3 = EnumSet.of(
				FlightMovementValidatorIssue.UNKNOWN_AIRCRAFT_TYPE);

		String resolutionErrorsStr3 = "UNKNOWN_AIRCRAFT_TYPE, BLAHBLAH, UNKNOWN_AIRCRAFT_TYPE";
		EnumSet<FlightMovementValidatorIssue> actual3 = EnumUtils
				.convertStringToEnumSet(FlightMovementValidatorIssue.class,
						resolutionErrorsStr3);

		assertThat(actual3).isEqualTo(expected3);
    }

    @Test
    public void testResolutionErrorsConversionFromEnumSetToString() {

		EnumSet<FlightMovementValidatorIssue> resolutionErrorsSet = EnumSet.of(
				FlightMovementValidatorIssue.MISSING_MTOW,
				FlightMovementValidatorIssue.TOWER_AIRCRAFT_PASSENGER);

    	FlightMovement flightMovement = new FlightMovement();
    	flightMovement.setResolutionErrorsSet(resolutionErrorsSet);

    	String expectedResolutionErrorsStr = "MISSING_MTOW,TOWER_AIRCRAFT_PASSENGER";
    	String actualResolutionErrorsStr = flightMovement.getResolutionErrors();

    	assertThat(actualResolutionErrorsStr).isEqualTo(expectedResolutionErrorsStr);
    }

    @Test
    public void validateFlightMovementByMonthYear() {

        FlightMovement flightMovement = new FlightMovement();
        flightMovement.setId(99990);
        flightMovement.setFlightId("TEST1FM");
        flightMovement.setDateOfFlight(LocalDateTime.of(2017, 3, 31, 0, 0));
        flightMovement.setDepAd("depADTest");
        flightMovement.setDestAd("destAdTest");
        flightMovement.setFlightType("E");
        flightMovement.setMovementType(FlightMovementType.DOMESTIC);
        flightMovement.setStatus(FlightMovementStatus.INCOMPLETE);

        FlightMovement flightMovement1 = new FlightMovement();
        flightMovement1.setFlightId("TEST2FM");
        flightMovement1.setId(99991);
        flightMovement1.setDateOfFlight(LocalDateTime.of(2017, 3, 27, 0, 0));
        flightMovement1.setDepAd("depADTest");
        flightMovement1.setDestAd("destAdTest");
        flightMovement1.setFlightType("E");
        flightMovement1.setMovementType(FlightMovementType.DOMESTIC);
        flightMovement1.setStatus(FlightMovementStatus.PENDING);

        ArrayList<FlightMovement> list = new ArrayList<>();
        FlightMovementValidationViewModel vm = new FlightMovementValidationViewModel();
        EnumSet<FlightMovementValidatorIssue> issues = EnumSet.noneOf(FlightMovementValidatorIssue.class);
        issues.add(FlightMovementValidatorIssue.ALL);
        vm.setIssues(issues);
        vm.setStatus(FlightMovementStatus.INCOMPLETE);
        list.add(flightMovement1);
        list.add(flightMovement);

        // noinspection unchecked
        when(flightMovementRepository.findAll(any(Specification.class))).thenReturn(list);
        when(flightMovementRepository.findOne(99990)).thenReturn(flightMovement);
        when(flightMovementRepository.findOne(99991)).thenReturn(flightMovement1);
        when(systemConfigurationService.getBoolean(eq(SystemConfigurationItemName.INVOICE_BY_DAY_OF_FLIGHT)))
            .thenReturn(true);

        when(flightMovementValidator.validateFlightMovementStatus(flightMovement,false)).thenReturn(vm);
        when(flightMovementValidator.validateFlightMovementStatus(flightMovement1,false)).thenReturn(vm);
        List<FlightMovementValidationViewModel> result = flightMovementService.validateAllFlightMovementByParams(
            DateTimeMapperUtils.parseISODateTime("2017-03-01T00:00:00.000Z"),
            DateTimeMapperUtils.parseISODateTime("2017-03-31T00:00:00.000Z"), true, null,
            true, buildABillingCenter(), null);
        assertThat(result.size()).isEqualTo(1);
    }

    private static BillingCenter buildABillingCenter() {
        final BillingCenter bc = new BillingCenter();
        bc.setId(1);
        bc.setHq(true);
        return bc;
    }

    @Test
    public void reconcileFlightMovementByMonthYear() throws FlightMovementBuilderException {

        FlightMovement flightMovement = new FlightMovement();
        flightMovement.setId(99990);
        flightMovement.setFlightId("TEST1FM");
        flightMovement.setDateOfFlight(LocalDateTime.of(2017, 3, 31,0,0));
        flightMovement.setDepAd("depADTest");
        flightMovement.setDestAd("destAdTest");
        flightMovement.setFlightType("E");
        flightMovement.setMovementType(FlightMovementType.DOMESTIC);
        flightMovement.setEstimatedElapsedTime("0500");
        flightMovement.setStatus(FlightMovementStatus.INCOMPLETE);

        FlightMovement flightMovement1 = new FlightMovement();
        flightMovement1.setFlightId("TEST2FM");
        flightMovement1.setId(99991);
        flightMovement1.setDateOfFlight(LocalDateTime.of(2017, 3, 27,0,0));
        flightMovement1.setDepAd("depADTest");
        flightMovement1.setDestAd("destAdTest");
        flightMovement1.setFlightType("E");
        flightMovement1.setMovementType(FlightMovementType.DOMESTIC);
        flightMovement1.setEstimatedElapsedTime("0600");
        flightMovement1.setStatus(FlightMovementStatus.PENDING);

        ArrayList<FlightMovement> list = new ArrayList<>();
        FlightMovementValidationViewModel vm = new FlightMovementValidationViewModel();

        list.add(flightMovement1);
        list.add(flightMovement);

        when(flightMovementRepository.findForAviationBillingByMonthYearIatastatus(any(Date.class), any(Date.class),
            anyBoolean(), anyInt())).thenReturn(list);
        when(flightMovementRepository.findOne(99990)).thenReturn(flightMovement);
        when(flightMovementRepository.findOne(99991)).thenReturn(flightMovement1);
        when(flightMovementBuilder.createUpdateFlightMovementFromUI(flightMovement,false)).thenReturn(flightMovement);
        when(flightMovementBuilder.createUpdateFlightMovementFromUI(flightMovement1,false)).thenReturn(flightMovement1);
        when(flightMovementAerodromeService.checkAerodromeIdentifier(any())).thenReturn("FBSK");
        when(flightMovementRepository.save(flightMovement1)).thenReturn(flightMovement1);
        when(flightMovementRepository.save(flightMovement)).thenReturn(flightMovement);

        when(flightMovementAerodromeService.checkAerodromeIdentifier("depADTest", true, true))
            .thenReturn(flightMovement.getDepAd());
        when(aerodromeService.checkAerodromeIdentifier("depADTest", true, true)).thenReturn(flightMovement.getDepAd());

        ResultRecords result = flightMovementService.reconcileAllFlightMovementByParams(DateTimeMapperUtils
            .parseISODateTime("2017-03-01T00:00:00.000Z"), DateTimeMapperUtils.parseISODateTime("2017-03-31T00:00:00.000Z"),
            true,null,true, buildABillingCenter());
        assertThat(result.getTotalRecords()).isEqualTo(2);
    }

    @Test
    public void recalculateFlightMovementByMonthYear() {

        FlightMovement flightMovement = new FlightMovement();
        flightMovement.setId(99990);
        flightMovement.setFlightId("TEST1FM");
        flightMovement.setDateOfFlight(LocalDateTime.of(2017, 3, 31, 0, 0));
        flightMovement.setDepAd("depADTest");
        flightMovement.setDestAd("destAdTest");
        flightMovement.setFlightType("E");
        flightMovement.setMovementType(FlightMovementType.DOMESTIC);
        flightMovement.setStatus(FlightMovementStatus.INCOMPLETE);

        FlightMovement flightMovement1 = new FlightMovement();
        flightMovement1.setFlightId("TEST2FM");
        flightMovement1.setId(99991);
        flightMovement1.setDateOfFlight(LocalDateTime.of(2017, 3, 27, 0, 0));
        flightMovement1.setDepAd("depADTest");
        flightMovement1.setDestAd("destAdTest");
        flightMovement1.setFlightType("E");
        flightMovement1.setMovementType(FlightMovementType.DOMESTIC);
        flightMovement1.setStatus(FlightMovementStatus.PENDING);

        ArrayList<FlightMovement> list = new ArrayList<>();

        list.add(flightMovement1);
        list.add(flightMovement);

        when(flightMovementRepository.findForAviationBillingByMonthYearIatastatus(any(Date.class), any(Date.class),
            anyBoolean(), anyInt())).thenReturn(list);
        when(flightMovementRepository.findOne(99990)).thenReturn(flightMovement);
        when(flightMovementRepository.findOne(99991)).thenReturn(flightMovement1);
        when(flightMovementBuilder.recalculateCharges(any(FlightMovement.class))).thenReturn(true);
        when(flightMovementRepository.save(flightMovement1)).thenReturn(flightMovement1);
        when(flightMovementRepository.save(flightMovement)).thenReturn(flightMovement);

        ResultRecords result = flightMovementService.recalculateAllFlightMovementByParams(
            DateTimeMapperUtils.parseISODateTime("2017-03-01T00:00:00.000Z"),
            DateTimeMapperUtils.parseISODateTime("2017-03-31T00:00:00.000Z"), true, null,
            true,buildABillingCenter());
        assertThat(result.getTotalRecords()).isEqualTo(2);
    }
    
    @Test
    public void isDuplicate() {
        FlightMovementViewModel flightMovement = new FlightMovementViewModel();
        flightMovement.setFlightId("TEST1");
        flightMovement.setDateOfFlight(LocalDateTime.of(2019, 12, 23, 0, 0));
        flightMovement.setDepAd("DEPT");
        flightMovement.setDestAd("DEST");
        flightMovement.setEstimatedElapsedTime("0100");

        FlightMovementViewModel flightMovement1 = new FlightMovementViewModel();
        flightMovement1.setFlightId("TEST1");
        flightMovement1.setDateOfFlight(LocalDateTime.of(2019, 12, 23, 0, 0));
        flightMovement1.setDepAd("DEPT");
        flightMovement1.setDestAd("DEST");
        flightMovement1.setEstimatedElapsedTime("0100");
        
        when(systemConfigurationService.getInteger(SystemConfigurationItemName.DEP_TIME_RANGE_EET_PERCENTAGE, 0)).thenReturn(50);
        when(systemConfigurationService.getInteger(SystemConfigurationItemName.DEP_TIME_RANGE_MIN,  0)).thenReturn(30);

        Boolean result;
        
        //Test 1 same dep time
        flightMovement.setDepTime("1230");
        flightMovement1.setDepTime("1230");  
        
        result = flightMovementService.isDuplicate(flightMovement, flightMovement1);
        assertThat(result).isTrue();
        
        flightMovement.setDepTime("1230");
        flightMovement1.setDepTime("1530");  
        
        result = flightMovementService.isDuplicate(flightMovement, flightMovement1);
        assertThat(result).isFalse();      
        
        // accept time line of 2 digits
        flightMovement.setDepTime("0030");
        flightMovement1.setDepTime("30");  
        
        result = flightMovementService.isDuplicate(flightMovement, flightMovement1);
        assertThat(result).isTrue();
        
        flightMovement.setDepTime("30");
        flightMovement1.setDepTime("0050");  
        
        result = flightMovementService.isDuplicate(flightMovement, flightMovement1);
        assertThat(result).isTrue();
        
        // Test 2 with eet
        flightMovement.setDepTime("1230");
        flightMovement1.setDepTime("1300");  
        
        result = flightMovementService.isDuplicate(flightMovement, flightMovement1);
        assertThat(result).isTrue();
        
        flightMovement.setDepTime("1230");
        flightMovement1.setDepTime("1301");  
        
        result = flightMovementService.isDuplicate(flightMovement, flightMovement1);
        assertThat(result).isFalse();
        
        flightMovement.setDepTime("1230");
        flightMovement1.setDepTime("1200");  
        
        result = flightMovementService.isDuplicate(flightMovement, flightMovement1);
        assertThat(result).isTrue();
        
        flightMovement.setDepTime("1230");
        flightMovement1.setDepTime("1159");  
        
        result = flightMovementService.isDuplicate(flightMovement, flightMovement1);
        assertThat(result).isFalse();
        
        flightMovement1.setEstimatedElapsedTime("100");
        flightMovement.setDepTime("1230");
        flightMovement1.setDepTime("1159");  
        
        result = flightMovementService.isDuplicate(flightMovement, flightMovement1);
        assertThat(result).isFalse();
        // Test overnight
        flightMovement.setDateOfFlight(LocalDateTime.of(2019, 12, 23, 0, 0));
        flightMovement1.setDateOfFlight(LocalDateTime.of(2019, 12, 24, 0, 0));
        flightMovement.setDepTime("2330");
        flightMovement1.setDepTime("0000");
        result = flightMovementService.isDuplicate(flightMovement, flightMovement1);
        assertThat(result).isTrue();
        
        flightMovement1.setDepTime("0001");
        result = flightMovementService.isDuplicate(flightMovement, flightMovement1);
        assertThat(result).isFalse();
        
        flightMovement.setDateOfFlight(LocalDateTime.of(2019, 12, 23, 0, 0));
        flightMovement1.setDateOfFlight(LocalDateTime.of(2019, 12, 22, 0, 0));
        flightMovement.setDepTime("0015");
        flightMovement1.setDepTime("2345");
        result = flightMovementService.isDuplicate(flightMovement, flightMovement1);
        assertThat(result).isTrue();
        
        flightMovement.setDateOfFlight(LocalDateTime.of(2019, 12, 23, 0, 0));
        flightMovement1.setDateOfFlight(LocalDateTime.of(2019, 12, 22, 0, 0));
        flightMovement.setDepTime("0015");
        flightMovement1.setDepTime("2345");
        result = flightMovementService.isDuplicate(flightMovement, flightMovement1);
        assertThat(result).isTrue();
         
        flightMovement1.setDepTime("2344");
        result = flightMovementService.isDuplicate(flightMovement, flightMovement1);
        assertThat(result).isFalse();
        
        flightMovement1.setDepTime("2300");
        result = flightMovementService.isDuplicate(flightMovement, flightMovement1);
        assertThat(result).isFalse();
        
        flightMovement1.setDepTime(null);
        result = flightMovementService.isDuplicate(flightMovement, flightMovement1);
        assertThat(result).isFalse();
    }
}
