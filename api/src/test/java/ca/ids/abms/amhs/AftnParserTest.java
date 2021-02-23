package ca.ids.abms.amhs;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ca.ids.abms.amhs.AftnParser.createFilingDateTime;

public class AftnParserTest {

    @Test
    public void testCreateFilingDateTime() {
        assertThat (createFilingDateTime ("010000", localDateTime ("2020-01-01T00:00:00")))
            .isEqualTo(localDateTime ("2020-01-01T00:00:00"));
        assertThat (createFilingDateTime ("020000", localDateTime ("2020-01-03T00:00:00")))
            .isEqualTo(localDateTime ("2020-01-02T00:00:00"));
        assertThat (createFilingDateTime ("031856", localDateTime ("2021-08-21T21:49:59")))
            .isEqualTo(localDateTime ("2021-08-03T18:56:00"));
        assertThat (createFilingDateTime ("301425", localDateTime ("2021-02-20T21:49:59")))
            .isEqualTo(localDateTime ("2021-01-30T14:25:00"));
    }

    private static LocalDateTime localDateTime (final String isoStr) {
        return LocalDateTime.parse (isoStr, DateTimeFormatter.ISO_DATE_TIME);
    }



}
