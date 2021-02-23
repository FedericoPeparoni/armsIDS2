package ca.ids.abms.modules.flightmovements;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

public class DuplicateOrMissingFlightsDetectorTest {

    private List<FlightMovementViewModel> flights;
    private int minimumWindow;
    private int percentageOfEET;

    @Before
    public void init() {
        this.flights = makeFlights();
        this.minimumWindow = 60;
        this.percentageOfEET = 100;
    }

    @Test
    public void testDuplicateOrMissingFlightsDetector() {
        DuplicateOrMissingFlightsDetector.analyze(flights, minimumWindow, percentageOfEET, true, false);

        /* Check the regular flights */

        FlightMovementViewModel flight = flights.get(0);
        assertThat(flight.isMarkedAsDuplicate()).isFalse();
        assertThat(flight.isMarkedAsFirstMissing()).isFalse();
        assertThat(flight.isMarkedAsMissingBeforeThis()).isFalse();
        assertThat(flight.isMarkedAsLastMissing()).isFalse();

        flight = flights.get(1);
        assertThat(flight.isMarkedAsDuplicate()).isFalse();
        assertThat(flight.isMarkedAsFirstMissing()).isFalse();
        assertThat(flight.isMarkedAsMissingBeforeThis()).isFalse();
        assertThat(flight.isMarkedAsLastMissing()).isFalse();

        flight = flights.get(2);
        assertThat(flight.isMarkedAsDuplicate()).isFalse();
        assertThat(flight.isMarkedAsFirstMissing()).isFalse();
        assertThat(flight.isMarkedAsMissingBeforeThis()).isFalse();
        assertThat(flight.isMarkedAsLastMissing()).isFalse();

        flight = flights.get(3);
        assertThat(flight.isMarkedAsDuplicate()).isFalse();
        assertThat(flight.isMarkedAsFirstMissing()).isFalse();
        assertThat(flight.isMarkedAsMissingBeforeThis()).isFalse();
        assertThat(flight.isMarkedAsLastMissing()).isFalse();

        flight = flights.get(9);
        assertThat(flight.isMarkedAsDuplicate()).isFalse();
        assertThat(flight.isMarkedAsFirstMissing()).isFalse();
        assertThat(flight.isMarkedAsMissingBeforeThis()).isFalse();
        assertThat(flight.isMarkedAsLastMissing()).isFalse();

        /* Check the duplicated flights */

        flight = flights.get(4);
        assertThat(flight.isMarkedAsDuplicate()).isTrue();
        assertThat(flight.isMarkedAsFirstMissing()).isFalse();
        assertThat(flight.isMarkedAsMissingBeforeThis()).isFalse();
        assertThat(flight.isMarkedAsLastMissing()).isFalse();

        flight = flights.get(5);
        assertThat(flight.isMarkedAsDuplicate()).isTrue();
        assertThat(flight.isMarkedAsFirstMissing()).isFalse();
        assertThat(flight.isMarkedAsMissingBeforeThis()).isFalse();
        assertThat(flight.isMarkedAsLastMissing()).isFalse();

        /* Check the missing flights */

        flight = flights.get(6);
        assertThat(flight.isMarkedAsDuplicate()).isFalse();
        assertThat(flight.isMarkedAsFirstMissing()).isTrue();
        assertThat(flight.isMarkedAsMissingBeforeThis()).isFalse();
        assertThat(flight.isMarkedAsLastMissing()).isFalse();

        flight = flights.get(7);
        assertThat(flight.isMarkedAsDuplicate()).isFalse();
        assertThat(flight.isMarkedAsFirstMissing()).isFalse();
        assertThat(flight.isMarkedAsMissingBeforeThis()).isTrue();
        assertThat(flight.isMarkedAsLastMissing()).isFalse();

        flight = flights.get(8);
        assertThat(flight.isMarkedAsDuplicate()).isFalse();
        assertThat(flight.isMarkedAsFirstMissing()).isFalse();
        assertThat(flight.isMarkedAsMissingBeforeThis()).isTrue();
        assertThat(flight.isMarkedAsLastMissing()).isTrue();
    }

    private static List<FlightMovementViewModel> makeFlights() {
        final List<FlightMovementViewModel> flights = new ArrayList<>();
        flights.add(makeFlight("WLD9999", LocalDateTime.of(2018,2,22,0,0,0), "1000", "HKJK", "HKNV","0030"));
        flights.add(makeFlight("WLD9999", LocalDateTime.of(2018,2,22,0,0,0), "1130", "HKNV", "HKMA","0030"));
        flights.add(makeFlight("WLD9999", LocalDateTime.of(2018,2,22,0,0,0), "1330", "HKMA", "HKMI","0030"));
        flights.add(makeFlight("WLD9999", LocalDateTime.of(2018,2,22,0,0,0), "1430", "HKMI", "HKJK","0030"));
        flights.add(makeFlight("WLD9999", LocalDateTime.of(2018,2,22,0,0,0), "1530", "HKJK", "HKMO","0030"));
        flights.add(makeFlight("WLD9999", LocalDateTime.of(2018,2,22,0,0,0), "1545", "HKJK", "HKMO","0030"));
        flights.add(makeFlight("WLD9999", LocalDateTime.of(2018,2,22,0,0,0), "1700", "HKMO", "HKMY","0030"));
        flights.add(makeFlight("WLD9999", LocalDateTime.of(2018,2,22,0,0,0), "1900", "HKMA", "HKLT","0030"));
        flights.add(makeFlight("WLD9999", LocalDateTime.of(2018,2,22,0,0,0), "2200", "HKKE", "HKJK","0030"));
        flights.add(makeFlight("WLD9999", LocalDateTime.of(2018,2,22,0,0,0), "2330", "HKJK", "HKKI","0030"));
        return flights;
    }

    private static FlightMovementViewModel makeFlight (String regNumber, LocalDateTime dateOfFlight, String depTime, String depAd, String destAd, String eet) {
        final FlightMovementViewModel flight = new FlightMovementViewModel();
        flight.setItem18RegNum(regNumber);
        flight.setDateOfFlight(dateOfFlight);
        flight.setDepTime(depTime);
        flight.setDepAd(depAd);
        flight.setDestAd(destAd);
        flight.setEstimatedElapsedTime(eet);
        flight.setMarkedAsDuplicate(false);
        flight.setMarkedAsFirstMissing(false);
        flight.setMarkedAsMissingBeforeThis(false);
        flight.setMarkedAsLastMissing(false);
        return flight;
    }
}
