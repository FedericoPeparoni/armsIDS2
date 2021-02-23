package ca.ids.abms.modules.flightmovements;

import ca.ids.abms.config.error.RejectedException;
import ca.ids.abms.config.error.RejectedReasons;
import ca.ids.abms.modules.aerodromeoperationalhours.AerodromeOperationalHoursService;
import ca.ids.abms.modules.aircraft.AircraftTypeService;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilder;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementMerge;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementValidator;
import ca.ids.abms.modules.flightmovementsbuilder.utility.FlightMovementBuilderUtility;
import ca.ids.abms.modules.flightmovementsbuilder.utility.ThruFlightPlanUtility;
import ca.ids.abms.modules.plugins.PluginService;
import ca.ids.abms.modules.spatiareader.dto.FplObjectDto;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.transactions.TransactionService;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.modules.util.models.WhitelistingUtils;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FlightMovementServiceFromSpatiaTest {

    private static final int DEP_TIME_RANGE_EET_PERCENTAGE = 50;
    private static final int DEP_TIME_RANGE_MIN = 30;

    private FlightMovementBuilder flightMovementBuilder;
    private FlightMovementRepository flightMovementRepository;
    private FlightMovementService flightMovementService;

    @Before
    public void setup() {

        SystemConfigurationService systemConfigurationService = mock(SystemConfigurationService.class);
        FlightMovementRepositoryUtility flightMovementRepositoryUtility = mock(FlightMovementRepositoryUtility.class);

        flightMovementBuilder = mock(FlightMovementBuilder.class);
        flightMovementRepository = mock(FlightMovementRepository.class);

        flightMovementService = new FlightMovementService(flightMovementRepository,
            mock(FlightMovementAerodromeService.class), mock(AircraftTypeService.class), flightMovementBuilder,
            mock(FlightMovementValidator.class), mock(FlightMovementBuilderUtility.class),
            mock(ThruFlightPlanUtility.class), systemConfigurationService, mock(FlightMovementMerge.class),
            flightMovementRepositoryUtility, mock(AerodromeOperationalHoursService.class),
            mock(CurrencyUtils.class), mock(TransactionService.class), mock(WhitelistingUtils.class), mock(PluginService.class));

        // mock system configuration item departure time range eet percentage matching setting
        when(systemConfigurationService
            .getOneByItemName(SystemConfigurationItemName.DEP_TIME_RANGE_EET_PERCENTAGE))
            .thenReturn(new MOCK_SYSTEM_CONFIGURATION.BUILDER(
                SystemConfigurationItemName.DEP_TIME_RANGE_EET_PERCENTAGE,
                String.valueOf(DEP_TIME_RANGE_EET_PERCENTAGE)).build());

        // mock system configuration item departure time range minimum matching setting
        when(systemConfigurationService
            .getOneByItemName(SystemConfigurationItemName.DEP_TIME_RANGE_MIN))
            .thenReturn(new MOCK_SYSTEM_CONFIGURATION.BUILDER(
                SystemConfigurationItemName.DEP_TIME_RANGE_MIN,
                String.valueOf(DEP_TIME_RANGE_MIN)).build());

        // mock flight movement persistence and overwrite methods
        when(flightMovementRepositoryUtility.persist(any(FlightMovement.class)))
            .thenAnswer(i -> i.getArguments()[0]);
        when(flightMovementRepositoryUtility.overwrite(any(FlightMovement.class)))
            .thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    public void createUpdateFlightMovementFromSpatiaTestCase1() {

        FplObjectDto flightObject = new MOCK_FLIGHT_PLAN.BUILDER().build();
        FlightMovement existingFlightMovement = new MOCK_FLIGHT_MOVEMENT.BUILDER().build();

        when(flightMovementRepository
            .findBySpatiaFplObjectIdOrderBySpatiaFplObjectIdDesc(MOCK_FLIGHT_PLAN.CATALOGUE_FPL_OBJECT_ID))
            .thenReturn(Collections.singletonList(existingFlightMovement));

        when(flightMovementRepository
            .findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(
                MOCK_FLIGHT_PLAN.FLIGHT_ID, MOCK_FLIGHT_PLAN.DEPARTURE_AD, MOCK_FLIGHT_PLAN.DAY_OF_FLIGHT,
                MOCK_FLIGHT_PLAN.DEPARTURE_TIME, MOCK_FLIGHT_PLAN.TOTAL_EET, DEP_TIME_RANGE_EET_PERCENTAGE,
                DEP_TIME_RANGE_MIN))
            .thenReturn(Collections.singletonList(existingFlightMovement));

        when(flightMovementBuilder
            .validateAndMergeFromFplObject(existingFlightMovement, flightObject))
            .thenReturn(existingFlightMovement);

        FlightMovement resultFlightMovement = flightMovementService.createUpdateFlightMovementFromSpatia(
            flightObject, false);
        assertThat(resultFlightMovement).isEqualTo(existingFlightMovement);
    }

    @Test
    public void createUpdateFlightMovementFromSpatiaTestCase2()  {

        FplObjectDto fplObjectDto = new MOCK_FLIGHT_PLAN.BUILDER().build();
        FlightMovement flightMovement = new MOCK_FLIGHT_MOVEMENT.BUILDER().build();

        // set invalid departure aerodrome
        fplObjectDto.setDepartureAd(null);
        try {
            flightMovement = flightMovementService.createUpdateFlightMovementFromSpatia(fplObjectDto, false);
            fail("Expected `VALIDATION_ERROR` for Flight Object '" + fplObjectDto
                    + "', returned Flight Movement '" + flightMovement + "' instead.");
        } catch (RejectedException e) {
            assertThat(e.getErrorDto().getRejectedReasons())
            .isEqualTo(RejectedReasons.VALIDATION_ERROR);
            assertThat(e.getReason())
            .isEqualTo(RejectedReasons.VALIDATION_ERROR.toValue());
        }

        when(flightMovementService.doPersistFlightMovement(flightMovement)).thenThrow(new RuntimeException("Generic error"));

        try {
            flightMovementService.calculateFlightMovementFromFplObject(flightMovement);
            fail("Expected error");
        } catch (Exception e) {
            // Suppress all exceptions because the user should not see them
            assertThat(e.getMessage()).isEqualTo("Generic error");
        }

    }

    static class MOCK_FLIGHT_MOVEMENT {

        static final Integer ID = 12345;
        static final String FLIGHT_ID = "ABC001";
        static final LocalDateTime DATE_OF_FLIGHT = LocalDate.of(2019, 2, 1).atStartOfDay();
        static final String DEP_AD = "ABCD";
        static final String DEST_AD = "WXYZ";
        static final String DEP_TIME = "0120";
        static final String ACTUAL_DEPARTURE_TIME = "0125";
        static final String ARRIVAL_AD = "WXYZ";
        static final String ARRIVAL_TIME = "0255";
        static final String AIRCRAFT_TYPE = "A200";
        static final String FPL_ROUTE = "LMNOP";

        static class BUILDER {

            private Integer id = ID;
            private String flightId = FLIGHT_ID;
            private LocalDateTime dateOfFlight = DATE_OF_FLIGHT;
            private String depAd = DEP_AD;
            private String destAd = DEST_AD;
            private String depTime = DEP_TIME;
            private String actualDepartureTime = ACTUAL_DEPARTURE_TIME;
            private String arrivalAd = ARRIVAL_AD;
            private String arrivalTime = ARRIVAL_TIME;
            private String aircraftType = AIRCRAFT_TYPE;
            private String fplRoute = FPL_ROUTE;

            BUILDER id(final Integer id) {
                this.id = id;
                return this;
            }

            BUILDER flightId(final String flightId) {
                this.flightId = flightId;
                return this;
            }

            BUILDER dateOfFlight(final LocalDateTime dateOfFlight) {
                this.dateOfFlight = dateOfFlight;
                return this;
            }

            BUILDER depAd(final String depAd) {
                this.depAd = depAd;
                return this;
            }

            BUILDER destAd(final String destAd) {
                this.destAd = destAd;
                return this;
            }

            BUILDER depTime(final String depTime) {
                this.depTime = depTime;
                return this;
            }

            BUILDER actualDepartureTime(final String actualDepartureTime) {
                this.actualDepartureTime = actualDepartureTime;
                return this;
            }

            BUILDER arrivalAd(final String arrivalAd) {
                this.arrivalAd = arrivalAd;
                return this;
            }

            BUILDER arrivalTime(final String arrivalTime) {
                this.arrivalTime = arrivalTime;
                return this;
            }

            BUILDER aircraftType(final String aircraftType) {
                this.aircraftType = aircraftType;
                return this;
            }

            BUILDER fplRoute(final String fplRoute) {
                this.fplRoute = fplRoute;
                return this;
            }

            FlightMovement build() {
                FlightMovement result = new FlightMovement();

                result.setId(id);
                result.setFlightId(flightId);
                result.setDateOfFlight(dateOfFlight);
                result.setDepAd(depAd);
                result.setDestAd(destAd);
                result.setDepTime(depTime);
                result.setActualDepartureTime(actualDepartureTime);
                result.setArrivalAd(arrivalAd);
                result.setArrivalTime(arrivalTime);
                result.setAircraftType(aircraftType);
                result.setFplRoute(fplRoute);

                return result;
            }
        }
    }

    static class MOCK_FLIGHT_PLAN {

        static final long CATALOGUE_FPL_OBJECT_ID = 123456L;
        static final String CATALOGUE_PRC_STATUS = "P";
        static final LocalDate DAY_OF_FLIGHT = LocalDate.of(2019, 2, 1);
        static final String DEPARTURE_TIME = "0120";
        static final String FLIGHT_ID = "ABC001";
        static final String DEPARTURE_AD = "ABCD";
        static final String DESTINATION_AD = "WXYZ";
        static final String FLIGHT_RULES = "I";
        static final String FLIGHT_TYPE = "S";
        static final String AIRCRAFT_TYPE = "A200";
        static final String WAKE_TURB = "M";
        static final String MSG_DEPARTURE_TIME = "";
        static final String SPEED = "N0240";
        static final String FLIGHT_LEVEL = "F180";
        static final String ROUTE = "LMNOP";
        static final String TOTAL_EET = "0135";
        static final String ARRIVAL_AD = "";
        static final String ARRIVAL_TIME = "";
        static final String OTHER_INFO = "NAV/GPS DOF/190201 REG/ABCDE EET/LMNOP0031 OPR/MOCK OPERATOR" +
            "TEL0987654321 RMK/SARNML CLR/WBS312DEC18";
        static final String RAW_FPL = "(FPL-ABC001-IS" +
            "-A200/M-SDFGZY/C" +
            "-ABCD0120" +
            "-N0240F180 LMNOP" +
            "" +
            "-WXYZ0135 ABCD WXYZ" +
            "-NAV/GPS DOF/190201 REG/ABCDE EET/LMNOP0031 OPR/MOCK OPERATOR" +
            "TEL0987654321 RMK/SARNML CLR/WBS312DEC18)";

        static class BUILDER {

            private long catalogueFplObjectId = CATALOGUE_FPL_OBJECT_ID;
            private String cataloguePrcStatus = CATALOGUE_PRC_STATUS;
            private LocalDate dayOfFlight = DAY_OF_FLIGHT;
            private String departureTime = DEPARTURE_TIME;
            private String flightId = FLIGHT_ID;
            private String departureAd = DEPARTURE_AD;
            private String destinationAd = DESTINATION_AD;
            private String flightRules = FLIGHT_RULES;
            private String flightType = FLIGHT_TYPE;
            private String aircraftType = AIRCRAFT_TYPE;
            private String wakeTurb = WAKE_TURB;
            private String msgDepartureTime = MSG_DEPARTURE_TIME;
            private String speed = SPEED;
            private String flightLevel = FLIGHT_LEVEL;
            private String route = ROUTE;
            private String totalEet = TOTAL_EET;
            private String arrivalAd = ARRIVAL_AD;
            private String arrivalTime = ARRIVAL_TIME;
            private String otherInfo = OTHER_INFO;
            private String rawFpl = RAW_FPL;

            BUILDER catalogueFplObjectId(final long catalogueFplObjectId) {
                this.catalogueFplObjectId = catalogueFplObjectId;
                return this;
            }

            BUILDER cataloguePrcStatus(final String cataloguePrcStatus) {
                this.cataloguePrcStatus = cataloguePrcStatus;
                return this;
            }

            BUILDER dayOfFlight(final LocalDate dayOfFlight) {
                this.dayOfFlight = dayOfFlight;
                return this;
            }

            BUILDER departureTime(final String departureTime) {
                this.departureTime = departureTime;
                return this;
            }

            BUILDER flightId(final String flightId) {
                this.flightId = flightId;
                return this;
            }

            BUILDER departureAd(final String departureAd) {
                this.departureAd = departureAd;
                return this;
            }

            BUILDER destinationAd(final String destinationAd) {
                this.destinationAd = destinationAd;
                return this;
            }

            BUILDER flightRules(final String flightRules) {
                this.flightRules = flightRules;
                return this;
            }

            BUILDER flightType(final String flightType) {
                this.flightType = flightType;
                return this;
            }

            BUILDER aircraftType(final String aircraftType) {
                this.aircraftType = aircraftType;
                return this;
            }

            BUILDER wakeTurb(final String wakeTurb) {
                this.wakeTurb = wakeTurb;
                return this;
            }

            BUILDER msgDepartureTime(final String msgDepartureTime) {
                this.msgDepartureTime = msgDepartureTime;
                return this;
            }

            BUILDER speed(final String speed) {
                this.speed = speed;
                return this;
            }

            BUILDER flightLevel(final String flightLevel) {
                this.flightLevel = flightLevel;
                return this;
            }

            BUILDER route(final String route) {
                this.route = route;
                return this;
            }

            BUILDER totalEet(final String totalEet) {
                this.totalEet = totalEet;
                return this;
            }

            BUILDER arrivalAd(final String arrivalAd) {
                this.arrivalAd = arrivalAd;
                return this;
            }

            BUILDER arrivalTime(final String arrivalTime) {
                this.arrivalTime = arrivalTime;
                return this;
            }

            BUILDER otherInfo(final String otherInfo) {
                this.otherInfo = otherInfo;
                return this;
            }

            BUILDER rawFpl(final String rawFpl) {
                this.rawFpl = rawFpl;
                return this;
            }

            FplObjectDto build() {
                FplObjectDto result = new FplObjectDto();

                result.setCatalogueFplObjectId(catalogueFplObjectId);
                result.setCataloguePrcStatus(cataloguePrcStatus);
                result.setDayOfFlight(dayOfFlight);
                result.setDepartureTime(departureTime);
                result.setFlightId(flightId);
                result.setDepartureAd(departureAd);
                result.setDestinationAd(destinationAd);
                result.setFlightRules(flightRules);
                result.setFlightType(flightType);
                result.setAircraftType(aircraftType);
                result.setWakeTurb(wakeTurb);
                result.setMsgDepartureTime(msgDepartureTime);
                result.setSpeed(speed);
                result.setFlightLevel(flightLevel);
                result.setRoute(route);
                result.setTotalEet(totalEet);
                result.setArrivalAd(arrivalAd);
                result.setArrivalTime(arrivalTime);
                result.setOtherInfo(otherInfo);
                result.setRawFpl(rawFpl);

                return result;
            }
        }
    }

    static class MOCK_SYSTEM_CONFIGURATION {

        static final Integer ID = 0;
        static final String ITEM_NAME = "MOCK_ITEM";
        static final String ITEM_VALUE = "MOCK_VALUE";

        static class BUILDER {

            private String itemName = ITEM_NAME;
            private String itemValue = ITEM_VALUE;

            BUILDER (final String itemName, final String itemValue) {
                this.itemName = itemName;
                this.itemValue = itemValue;
            }

            SystemConfiguration build() {
                SystemConfiguration result = new SystemConfiguration();

                result.setId(ID);
                result.setItemName(itemName);
                result.setDefaultValue(itemValue);

                result.setCurrentValue(itemValue);
                return result;
            }
        }
    }
}
