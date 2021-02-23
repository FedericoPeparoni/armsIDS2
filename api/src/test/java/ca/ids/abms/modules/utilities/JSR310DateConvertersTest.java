package ca.ids.abms.modules.utilities;

import static ca.ids.abms.util.converter.JSR310DateConverters.convertStringToLocalTime;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.junit.Test;

/**
 * Created by c.talpa on 17/01/2017.
 */
public class JSR310DateConvertersTest {

    @Test
    public void convertStringToLocalTimeTest(){
        LocalTime trueValueLocalTime=LocalTime.of(3,24);
        LocalTime localTime=convertStringToLocalTime("0324","HHmm");
        assertThat(trueValueLocalTime.equals(localTime));
    }

    @Test
    public void normalizeTimeTest(){
        final String time[] = {
            "04:41", "HH:mm",
            "4 42", "H mm",
            "0443", "HHmm",
            "04:44", "HH:mm",
            "16:45", "HH:mm",
            "1646", "HHmm",
            "04:47 AM", "hh:mm a",
            "4:48 AM", "h:mm a"
        };

        assertThat(parseTime(time[0], time[1])).isNotNull();
        assertThat(parseTime(time[2], time[3])).isNotNull();
        assertThat(parseTime(time[4], time[5])).isNotNull();
        assertThat(parseTime(time[6], time[7])).isNotNull();
        assertThat(parseTime(time[8], time[9])).isNotNull();
        assertThat(parseTime(time[10], time[11])).isNotNull();
        assertThat(parseTime(time[12], time[13])).isNotNull();
        assertThat(parseTime(time[14], time[15])).isNotNull();


    }


    private LocalDate parseDate (final String date, final String pattern) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH));
    }

    private LocalTime parseTime (final String time, final String pattern) {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH));
    }
}
