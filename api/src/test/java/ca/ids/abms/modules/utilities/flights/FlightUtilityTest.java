package ca.ids.abms.modules.utilities.flights;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class FlightUtilityTest {

    @Test
    public void resolveDateOfContactTest() {

        // these test cases assume FlightConstants.DEP_TIME_BUFFER is defined as 4hrs
        LocalDateTime dateOfFlight = LocalDateTime.of(2018, 12, 31, 0, 0);

        // CASE 1: returns date of flight if departure time plus 4 hours is less then contact time
        assertThat(FlightUtility.resolveDateOfContact("1000", "1200", dateOfFlight))
            .isEqualTo(LocalDateTime.of(2018, 12, 31, 0, 0));
        assertThat(FlightUtility.resolveDateOfContact("1200", "1200", dateOfFlight))
            .isEqualTo(LocalDateTime.of(2018, 12, 31, 0, 0));
        assertThat(FlightUtility.resolveDateOfContact("1600", "1200", dateOfFlight))
            .isEqualTo(LocalDateTime.of(2018, 12, 31, 0, 0));
        assertThat(FlightUtility.resolveDateOfContact("0300", "2200", dateOfFlight))
            .isEqualTo(LocalDateTime.of(2018, 12, 31, 0, 0));
        assertThat(FlightUtility.resolveDateOfContact("0100", "2200", dateOfFlight))
            .isEqualTo(LocalDateTime.of(2018, 12, 31, 0, 0));

        // CASE 2: returns date of flight plus day if departure time plus 4 hours is greater then contact time
        assertThat(FlightUtility.resolveDateOfContact("1601", "1200", dateOfFlight))
            .isEqualTo(LocalDateTime.of(2019, 1, 1, 0, 0));
        assertThat(FlightUtility.resolveDateOfContact("1800", "1200", dateOfFlight))
            .isEqualTo(LocalDateTime.of(2019, 1, 1, 0, 0));

        // CASE 3: returns null if date of flight is null
        assertThat(FlightUtility.resolveDateOfContact("1200", "1200", null)).isNull();

        // CASE 4: returns date of flight if departure time or contact time is empty/null
        assertThat(FlightUtility.resolveDateOfContact(null, "1200", dateOfFlight))
            .isEqualTo(LocalDateTime.of(2018, 12, 31, 0, 0));
        assertThat(FlightUtility.resolveDateOfContact("    ", "1200", dateOfFlight))
            .isEqualTo(LocalDateTime.of(2018, 12, 31, 0, 0));
        assertThat(FlightUtility.resolveDateOfContact("1200", null, dateOfFlight))
            .isEqualTo(LocalDateTime.of(2018, 12, 31, 0, 0));
        assertThat(FlightUtility.resolveDateOfContact("1200", "    ", dateOfFlight))
            .isEqualTo(LocalDateTime.of(2018, 12, 31, 0, 0));

        // CASE 5: throw exception if departure time or contact time is invalid
        try {
            assertThat(FlightUtility.resolveDateOfContact("ABCD", "1200", dateOfFlight)).isNull();
            fail("Expected DateTimeParseException when departure time is invalid.");
        } catch (final DateTimeParseException ignored) {
            // ignored, expect date time parse exception
        }

        try {
            assertThat(FlightUtility.resolveDateOfContact("1200", "ABCD", dateOfFlight)).isNull();
            fail("Expected DateTimeParseException when departure time is invalid.");
        } catch (final DateTimeParseException ignored) {
            // ignored, expect date time parse exception
        }
    }

    @Test
    public void resolveDateOfFlightTest() {

        // these test cases assume FlightConstants.DEP_TIME_BUFFER is defined as 4hrs
        LocalDateTime dateOfContact = LocalDateTime.of(2019, 1, 1, 0, 0);

        // CASE 1: returns date of contact if departure time plus 4 hours is less then contact time
        assertThat(FlightUtility.resolveDateOfFlight("1000", "1200", dateOfContact))
            .isEqualTo(LocalDateTime.of(2019, 1, 1, 0, 0));
        assertThat(FlightUtility.resolveDateOfFlight("1200", "1200", dateOfContact))
            .isEqualTo(LocalDateTime.of(2019, 1, 1, 0, 0));
        assertThat(FlightUtility.resolveDateOfFlight("1600", "1200", dateOfContact))
            .isEqualTo(LocalDateTime.of(2019, 1, 1, 0, 0));
        assertThat(FlightUtility.resolveDateOfFlight("0300", "2200", dateOfContact))
            .isEqualTo(LocalDateTime.of(2019, 1, 1, 0, 0));
        assertThat(FlightUtility.resolveDateOfFlight("0100", "2200", dateOfContact))
            .isEqualTo(LocalDateTime.of(2019, 1, 1, 0, 0));

        // CASE 2: returns date of flight plus day if departure time plus 4 hours is greater then contact time
        assertThat(FlightUtility.resolveDateOfFlight("1601", "1200", dateOfContact))
            .isEqualTo(LocalDateTime.of(2018, 12, 31, 0, 0));
        assertThat(FlightUtility.resolveDateOfFlight("1800", "1200", dateOfContact))
            .isEqualTo(LocalDateTime.of(2018, 12, 31, 0, 0));

        // CASE 3: returns null if date of contact is null
        assertThat(FlightUtility.resolveDateOfFlight("1200", "1200", null)).isNull();

        // CASE 4: returns date of contact if departure time or contact time is empty/null
        assertThat(FlightUtility.resolveDateOfFlight(null, "1200", dateOfContact))
            .isEqualTo(LocalDateTime.of(2019, 1, 1, 0, 0));
        assertThat(FlightUtility.resolveDateOfFlight("    ", "1200", dateOfContact))
            .isEqualTo(LocalDateTime.of(2019, 1, 1, 0, 0));
        assertThat(FlightUtility.resolveDateOfFlight("1200", null, dateOfContact))
            .isEqualTo(LocalDateTime.of(2019, 1, 1, 0, 0));
        assertThat(FlightUtility.resolveDateOfFlight("1200", "    ", dateOfContact))
            .isEqualTo(LocalDateTime.of(2019, 1, 1, 0, 0));

        // CASE 5: throw exception if departure time or contact time is invalid
        try {
            assertThat(FlightUtility.resolveDateOfFlight("ABCD", "1200", dateOfContact)).isNull();
            fail("Expected DateTimeParseException when departure time is invalid.");
        } catch (final DateTimeParseException ignored) {
            // ignored, expect date time parse exception
        }

        try {
            assertThat(FlightUtility.resolveDateOfFlight("1200", "ABCD", dateOfContact)).isNull();
            fail("Expected DateTimeParseException when departure time is invalid.");
        } catch (final DateTimeParseException ignored) {
            // ignored, expect date time parse exception
        }
    }
}
