package ca.ids.abms.plugins.amhs.fpl;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DayOfFlightParserTest {

    @Test
    public void testExtractDof() {
        assertThat (extractDof("x", "2000-01-01T00:00:00")).isNull();
        assertThat (extractDof("DOF 000102", "2000-01-01T00:00:00")).isNull();
        assertThat (extractDof("DOF 000102x", "2000-01-01T00:00:00")).isNull();
        assertThat (extractDof("DOF 00010203", "2000-01-01T00:00:00")).isNull();
        assertThat (extractDof("DOF 000132", "2000-01-01T00:00:00")).isNull();
        assertThat (extractDof("xxx DOF/000102 yyy", "2000-01-01T00:00:00")).isEqualTo("2000-01-02");
        assertThat (extractDof("xxx DOF/990102 yyy", "2020-01-01T00:00:00")).isEqualTo("1999-01-02");
        assertThat (extractDof("xxx DOF/990102 yyy", "2080-01-01T00:00:00")).isEqualTo("2099-01-02");
    }

    private static String extractDof (final String dofStr, final String filingDateTimeStr) {
        final LocalDate res = DayOfFlightParser.extractDof (dofStr, LocalDateTime.parse (filingDateTimeStr, DateTimeFormatter.ISO_DATE_TIME));
        return res == null ? null : res.toString();
    }

}
