package ca.ids.abms.modules.common.mappers;

import ca.ids.abms.util.converter.DateTimeParserStrategy;
import ca.ids.abms.util.converter.JSR310DateConverters;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.Locale;

/**
 * This is a base mapper helpful to centralize the converters of the date/time parsers
 */
public final class DateTimeMapperUtils {

    private static DateTimeFormatter ISO_DATE_OPTIONAL_TIME = new DateTimeFormatterBuilder()
        .append(DateTimeFormatter.ISO_LOCAL_DATE)
        .optionalStart()
        .appendLiteral('T')
        .append(DateTimeFormatter.ISO_LOCAL_TIME)
        .appendLiteral('Z')
        .toFormatter();

    private DateTimeMapperUtils(){}

    public static LocalDateTime mapLocalDateTimeISO (final String date) {
        return parseISODateTime(date);
    }

    public static String mapLocalDateTimeISO (final LocalDateTime date) {
        return parseISODateTime(date);
    }

    public static LocalDateTime mapJavaDateToLocalDateTime(final Date date) {
        LocalDateTime result = null;
        if (date != null) {
            result = LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"));
        }
        return result;
    }

    public static LocalDate mapJavaDateToLocalDate(final Date date) {
        LocalDate result = null;
        if (date != null) {
            result = LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC")).toLocalDate();
        }
        return result;
    }

    public static LocalDateTime parseCustomLocalDateToLocalDateTime (final String date, final String pattern, final Locale locale) {
        if (date != null && pattern != null) {
            final LocalDate dateObj = LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern, locale != null ? locale : Locale.ENGLISH));
            return JSR310DateConverters.convertLocalDateToLocalDateTime(dateObj);
        } else {
            return null;
        }
    }

    public static LocalDate parseISODate (final String date) {
        if (date != null) {
            return LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        } else {
            return null;
        }
    }

    public static String parseISODate (final LocalDate date) {
        if (date != null) {
            return date.format(DateTimeFormatter.ISO_DATE);
        } else {
            return null;
        }
    }

    public static LocalDateTime parseISODateTime (final String dateTime) {
        if (dateTime != null) {
            return LocalDateTime.parse(dateTime, ISO_DATE_OPTIONAL_TIME);
        } else {
            return null;
        }
    }

    public static String parseISODateTime (final LocalDateTime dateTime) {
        if (dateTime != null) {
            return dateTime.format(ISO_DATE_OPTIONAL_TIME);
        } else {
            return null;
        }
    }

    public static String normalizeTime(final String time) {
        if (StringUtils.isNotBlank(time)) {
            final LocalTime timeObject = JSR310DateConverters.convertStringToLocalTimeWithUnknownPattern(time);
            return timeObject.format(DateTimeFormatter.ofPattern(JSR310DateConverters.DEFAULT_PATTERN_TIME));
        } else {
            return null;
        }
    }

    public static String normalizeDate(final String date) {
        return normalizeDate(date, DateTimeParserStrategy.DATE_PATTERNS_YEAR_LAST);
    }

    public static String normalizeDate(final String date, DateTimeParserStrategy strategy) {
        if (StringUtils.isNotBlank(date)) {
            final LocalDate dateObject = JSR310DateConverters.convertStringToLocalDateWithUnknownPattern(JSR310DateConverters.normalizeDate(date), strategy);
            return dateObject.format(DateTimeFormatter.ofPattern(JSR310DateConverters.DEFAULT_PATTERN_DATE));
        } else {
            return null;
        }
    }

    public static LocalDate parseCustomLocalDate (final String date, final String pattern, final Locale locale) {
        if (date != null && pattern != null) {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern, locale != null ? locale : Locale.ENGLISH));
        } else {
            return null;
        }
    }

    public static LocalDateTime parseCustomLocalDateToLocalDateTime (final String date, final DateTimeFormatter formatter) {
        if (date != null && formatter != null) {
            final LocalDate dateObj = LocalDate.parse(date, formatter);
            return JSR310DateConverters.convertLocalDateToLocalDateTime(dateObj);
        } else {
            return null;
        }
    }

    public String parseCustomLocalDate (final LocalDate date, final String pattern, final Locale locale) {
        if (date != null) {
            return date.format(DateTimeFormatter.ofPattern(pattern, locale != null ? locale : Locale.ENGLISH));
        } else {
            return null;
        }
    }

    public static LocalTime parseSystemTime (final String time) {
        if (time != null) {
            return JSR310DateConverters.convertStringToLocalTime(time, JSR310DateConverters.DEFAULT_PATTERN_TIME);
        } else {
            return null;
        }
    }

    public static String parseSystemTime (final LocalTime time) {
        if (time != null) {
            return time.format(DateTimeFormatter.ofPattern(JSR310DateConverters.DEFAULT_PATTERN_TIME));
        } else {
            return null;
        }
    }
}
