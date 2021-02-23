package ca.ids.abms.modules.estimators.departure.methods;

import ca.ids.abms.modules.estimators.departure.DepartureEstimatorModel;
import ca.ids.abms.modules.estimators.departure.DepartureEstimatorResult;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DepartureEstimatorMethodATest {

    private FlightMovementRepository repository;
    private DepartureEstimatorMethodA methodA;

    @Before
    public void setup() {
        this.repository = mock(FlightMovementRepository.class);
        this.methodA = new DepartureEstimatorMethodA(repository);
    }

    @Test
    public void estimateDepartureTimeTest() {

        // returns result with date of flight and departure time successful
        FlightMovement flightMovement = MOCK.FLIGHT();
        DepartureEstimatorModel model = MOCK.MODEL();

        when(repository.findAllByFlightIdAndDepAdAndDateOfContactAndTimeOfContact(
            model.getFlightId(), model.getDepAd(), model.getDateOfContact(), model.getDateOfContact().minusDays(1),
            model.getTimeOfContact()))
            .thenReturn(Collections.singletonList(flightMovement));

        DepartureEstimatorResult result = methodA.estimateDepartureTime(model);
        assertThat(result).isNotNull();
        assertThat(result.getDayOfFlight()).isEqualTo(flightMovement.getDateOfFlight());
        assertThat(result.getDepTime()).isEqualTo(flightMovement.getDepTime());
    }

    @Test
    public void findExistingFlightMovementTest() {

        // no existing flight movements must not return day of flight or departure time
        DepartureEstimatorModel model = MOCK.MODEL();

        assertThat(methodA.estimateDepartureTime(model)).isNull();


        // assert that flight movement from previous week is used to guess date of flight and dep time
        FlightMovement flightMovementWeek = MOCK.FLIGHT();
        flightMovementWeek.setDateOfFlight(flightMovementWeek.getDateOfFlight().minusWeeks(1));
        model = MOCK.MODEL();

        when(repository.findAllByFlightIdAndDepAdAndDateOfContactAndTimeOfContact(
            model.getFlightId(), model.getDepAd(),model.getDateOfContact().minusWeeks(1),
            model.getDateOfContact().minusWeeks(1).minusDays(1), model.getTimeOfContact()))
            .thenReturn(Collections.singletonList(flightMovementWeek));

        DepartureEstimatorResult result = methodA.estimateDepartureTime(model);
        assertThat(result).isNotNull();
        assertThat(result.getDayOfFlight()).isEqualTo(flightMovementWeek.getDateOfFlight().plusWeeks(1));
        assertThat(result.getDepTime()).isEqualTo(flightMovementWeek.getDepTime());


        // assert that flight movement from current day is used to guess date of flight and dep time
        FlightMovement flightMovementDay = MOCK.FLIGHT();
        model = MOCK.MODEL();

        when(repository.findAllByFlightIdAndDepAdAndDateOfContactAndTimeOfContact(
            model.getFlightId(), model.getDepAd(), model.getDateOfContact(), model.getDateOfContact().minusDays(1),
            model.getTimeOfContact()))
            .thenReturn(Collections.singletonList(flightMovementDay));

        result = methodA.estimateDepartureTime(model);
        assertThat(result).isNotNull();
        assertThat(result.getDayOfFlight()).isEqualTo(flightMovementDay.getDateOfFlight());
        assertThat(result.getDepTime()).isEqualTo(flightMovementDay.getDepTime());


        // assert that two or more flights returned does not set day of flight or departure time
        List<FlightMovement> flightMovements = new ArrayList<>();
        flightMovements.add(new FlightMovement());
        flightMovements.add(new FlightMovement());
        model = MOCK.MODEL();

        when(repository.findAllByFlightIdAndDepAdAndDateOfContactAndTimeOfContact(
            anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class), anyString()))
            .thenReturn(flightMovements);

        assertThat(methodA.estimateDepartureTime(model)).isNull();
    }

    @Test
    public void hasInvalidFieldsTest() {

        // model flight identifier must not be null
        assertThat(methodA.estimateDepartureTime(MOCK.MODEL(
            null, MOCK.DATE_OF_CONTACT, MOCK.TIME_OF_CONTACT, MOCK.DEP_AD)))
            .isNull();


        // model flight identifier must must match ICAO format
        assertThat(methodA.estimateDepartureTime(MOCK.MODEL(
            "123456", MOCK.DATE_OF_CONTACT, MOCK.TIME_OF_CONTACT, MOCK.DEP_AD)))
            .isNull();


        // model departure aerodrome must not be null
        assertThat(methodA.estimateDepartureTime(MOCK.MODEL(
            MOCK.FLIGHT_ID, MOCK.DATE_OF_CONTACT, MOCK.TIME_OF_CONTACT, null)))
            .isNull();


        // model departure aerodrome must not be empty
        assertThat(methodA.estimateDepartureTime(MOCK.MODEL(
            MOCK.FLIGHT_ID, MOCK.DATE_OF_CONTACT, MOCK.TIME_OF_CONTACT, "")))
            .isNull();


        // model date of contact must not be null
        assertThat(methodA.estimateDepartureTime(MOCK.MODEL(
            MOCK.FLIGHT_ID, null, MOCK.TIME_OF_CONTACT, MOCK.DEP_AD)))
            .isNull();


        // model time of contact must not be null
        assertThat(methodA.estimateDepartureTime(MOCK.MODEL(
            MOCK.FLIGHT_ID, MOCK.DATE_OF_CONTACT, null, MOCK.DEP_AD)))
            .isNull();


        // flight movement date of flight must not be null
        FlightMovement flightMovement = MOCK.FLIGHT();
        flightMovement.setDateOfFlight(null);

        when(repository.findAllByFlightIdAndDepAdAndDateOfContactAndTimeOfContact(
            anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class), anyString()))
            .thenReturn(Collections.singletonList(flightMovement));

        assertThat(methodA.estimateDepartureTime(MOCK.MODEL())).isNull();


        // flight movement departure time must not be null
        flightMovement = MOCK.FLIGHT();
        flightMovement.setDepTime(null);

        when(repository.findAllByFlightIdAndDepAdAndDateOfContactAndTimeOfContact(
            anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class), anyString()))
            .thenReturn(Collections.singletonList(flightMovement));

        assertThat(methodA.estimateDepartureTime(MOCK.MODEL())).isNull();


        // flight movement departure time must not be empty
        flightMovement = MOCK.FLIGHT();
        flightMovement.setDepTime("");

        when(repository.findAllByFlightIdAndDepAdAndDateOfContactAndTimeOfContact(
            anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class), anyString()))
            .thenReturn(Collections.singletonList(flightMovement));

        assertThat(methodA.estimateDepartureTime(MOCK.MODEL())).isNull();


        // returns true, set date of flight, and departure time if all fields valid
        flightMovement = MOCK.FLIGHT();

        when(repository.findAllByFlightIdAndDepAdAndDateOfContactAndTimeOfContact(
            anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class), anyString()))
            .thenReturn(Collections.singletonList(flightMovement));

        DepartureEstimatorResult result = methodA.estimateDepartureTime(MOCK.MODEL());
        assertThat(result).isNotNull();
        assertThat(result.getDayOfFlight()).isEqualTo(flightMovement.getDateOfFlight());
        assertThat(result.getDepTime()).isEqualTo(flightMovement.getDepTime());
    }

    static class MOCK {

        static final Integer ID = 0;
        static final String FLIGHT_ID = "ABC123";
        static final LocalDateTime DATE_OF_FLIGHT = LocalDate.of(2018, 12, 30).atStartOfDay();
        static final String DEP_TIME = "0600";
        static final String DEP_AD = "ABCD";
        static final String DEST_AD = "WXYZ";

        static final String DISPLAY_NAME = "mockDepartureEstimatorModel";
        static final LocalDateTime DATE_OF_CONTACT = LocalDate.of(2018, 12, 30).atStartOfDay();
        static final String TIME_OF_CONTACT = "0700";

        static FlightMovement FLIGHT() {

            FlightMovement model = new FlightMovement();

            model.setId(ID);
            model.setFlightId(FLIGHT_ID);
            model.setDateOfFlight(DATE_OF_FLIGHT);
            model.setDepAd(DEP_AD);
            model.setDestAd(DEST_AD);
            model.setDepTime(DEP_TIME);

            return model;
        }

        static DepartureEstimatorModel MODEL() {
            return MODEL(FLIGHT_ID, DATE_OF_CONTACT, TIME_OF_CONTACT, DEP_AD);
        }

        static DepartureEstimatorModel MODEL(final String flightId, final LocalDateTime dateOfContact, final String timeOfContact, final String depAd) {
            return new DepartureEstimatorModel.Builder(DISPLAY_NAME)
                .flightId(flightId)
                .dateOfContact(dateOfContact)
                .timeOfContact(timeOfContact)
                .depAd(depAd)
                .build();
        }
    }
}
