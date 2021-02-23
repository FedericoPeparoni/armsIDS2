package ca.ids.abms.util;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class DateUtils {

    private DateUtils() {}

    public static int getLastDayOfMonth (final int year, final int month) {
        return LocalDate.of (year, month, 1).with (TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
    }

}
