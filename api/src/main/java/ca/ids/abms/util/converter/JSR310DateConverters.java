package ca.ids.abms.util.converter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;
import ca.ids.abms.modules.translation.Translation;
import org.springframework.core.convert.converter.Converter;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class JSR310DateConverters {

    public static final String DEFAULT_PATTERN_DATE_TIME="yyyy-MM-dd HH:mm:ss";

    public static final String DEFAULT_PATTERN_DATE="yyyy-MM-dd";

    public static final String SERIALIZED_PATTERN_DATE="yyyyMMdd";

    public static final String DEFAULT_PATTERN_TIME="HHmm";

    public static final String DEFAULT_TIME_ZONE= "UTC";

    private JSR310DateConverters() {
    }

    public static class LocalDateToDateConverter implements Converter<LocalDate, Date> {

        public static final LocalDateToDateConverter INSTANCE = new LocalDateToDateConverter();

        private LocalDateToDateConverter() {
        }

        @Override
        public Date convert(LocalDate source) {
            return source == null ? null : Date.from(source.atStartOfDay(ZoneId.of(DEFAULT_TIME_ZONE)).toInstant());
        }
    }

    public static class DateToLocalDateConverter implements Converter<Date, LocalDate> {
        public static final DateToLocalDateConverter INSTANCE = new DateToLocalDateConverter();

        private DateToLocalDateConverter() {
        }

        @Override
        public LocalDate convert(Date source) {
            return source == null ? null : ZonedDateTime.ofInstant(source.toInstant(), ZoneId.of(DEFAULT_TIME_ZONE)).toLocalDate();
        }
    }

    public static class ZonedDateTimeToDateConverter implements Converter<ZonedDateTime, Date> {
        public static final ZonedDateTimeToDateConverter INSTANCE = new ZonedDateTimeToDateConverter();

        private ZonedDateTimeToDateConverter() {
        }

        @Override
        public Date convert(ZonedDateTime source) {
            return source == null ? null : Date.from(source.toInstant());
        }
    }

    public static class DateToZonedDateTimeConverter implements Converter<Date, ZonedDateTime> {
        public static final DateToZonedDateTimeConverter INSTANCE = new DateToZonedDateTimeConverter();

        private DateToZonedDateTimeConverter() {
        }

        @Override
        public ZonedDateTime convert(Date source) {
            return source == null ? null : ZonedDateTime.ofInstant(source.toInstant(), ZoneId.of(DEFAULT_TIME_ZONE));
        }
    }

    public static class LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date> {
        public static final LocalDateTimeToDateConverter INSTANCE = new LocalDateTimeToDateConverter();

        private LocalDateTimeToDateConverter() {
        }

        @Override
        public Date convert(LocalDateTime source) {
            return JSR310DateConverters.convertLocalDateTimeToDate(source);
        }
    }

    public static class DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime> {
        public static final DateToLocalDateTimeConverter INSTANCE = new DateToLocalDateTimeConverter();

        private DateToLocalDateTimeConverter() {
        }

        @Override
        public LocalDateTime convert(Date source) {
            return JSR310DateConverters.convertLocalDateTimeToDate(source);
        }

    }

    public static Date convertLocalDateTimeToDate(LocalDateTime localDateTime){
        Date result=null;
        if(localDateTime!=null){
            result = Date.from(localDateTime.atZone(ZoneId.of(DEFAULT_TIME_ZONE)).toInstant());
        }
        return result;
    }

    public static LocalDateTime convertLocalDateTimeToDate(Date source) {
        return source == null ? null : LocalDateTime.ofInstant(source.toInstant(), ZoneId.of(DEFAULT_TIME_ZONE));
    }

    public static LocalDateTime convertLocalDateToLocalDateTime(LocalDate localdate){
        LocalDateTime result=null;
        if(localdate!=null){
            Instant instant=localdate.atStartOfDay().atZone(ZoneId.of(DEFAULT_TIME_ZONE)).toInstant();
            result = LocalDateTime.ofInstant(instant, ZoneId.of(DEFAULT_TIME_ZONE));
        }
        return result;
    }

    public static LocalDateTime convertStringToLocalDateTime(String dateTime, String patternDateTime){
        LocalDateTime result=null;

        if(dateTime!=null && !dateTime.isEmpty()){

            if(patternDateTime==null || patternDateTime.isEmpty()){
                patternDateTime=DEFAULT_PATTERN_DATE_TIME;
            }

            final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(patternDateTime);
            result = LocalDateTime.parse(dateTime, DATE_FORMAT);

        }
        return result;
    }

    public static String convertLocalDateTimeToString(LocalDateTime dateTime, String patternDateTime){
        String result=null;

        if(dateTime!=null){

            if(patternDateTime==null || patternDateTime.isEmpty()){
                patternDateTime=DEFAULT_PATTERN_DATE_TIME;
            }

            final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(patternDateTime);
            result = dateTime.format(DATE_FORMAT);

        }
        return result;
    }

    public static LocalTime convertStringToLocalTime(String time, String patternTime){
        LocalTime result=null;

        if(time!=null && !time.isEmpty()){

            if(patternTime==null || patternTime.isEmpty()){
                patternTime=DEFAULT_PATTERN_TIME;
            }

            final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(patternTime);
            result = LocalTime.parse(time, DATE_FORMAT);

        }
        return result;
    }

    public static LocalDate convertStringToLocalDate(String date, String patternDate){
        LocalDate result=null;

        if(date!=null && !date.isEmpty()){

            if(patternDate==null || patternDate.isEmpty()){
                patternDate=DEFAULT_PATTERN_DATE;
            }

            final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(patternDate, Locale.ENGLISH);
            result = LocalDate.parse(date, DATE_FORMAT);

        }
        return result;
    }

    public static LocalTime convertStringToLocalTimeWithUnknownPattern(String timeString) {
        return convertStringToLocalTimeWithUnknownPattern(timeString, DateTimeParserStrategy.TIME_PATTERNS);
    }

    public static LocalTime convertStringToLocalTimeWithUnknownPattern(String timeString, DateTimeParserStrategy strategy){
        return convertStringToLocalTimeWithUnknownPattern(timeString, strategy, Locale.ENGLISH);
    }

    public static LocalTime convertStringToLocalTimeWithUnknownPattern(String timeString, DateTimeParserStrategy strategy, Locale locale){
        if (timeString != null) {
            for (String pattern : strategy.getPatterns()) {
                try {
                    return LocalTime.parse(timeString, DateTimeFormatter.ofPattern(pattern, locale));
                } catch (DateTimeParseException excep) {
                    // Nothing to do
                }
            }
            throw new IllegalArgumentException("Not able to parse the time" + "\"" + timeString + "\"" + "for all patterns allowed");
        } else {
            return null;
        }
    }

    public static LocalDate convertStringToLocalDateWithUnknownPattern(String dateString) {
        return convertStringToLocalDateWithUnknownPattern(dateString, DateTimeParserStrategy.DATE_PATTERNS_YEAR_LAST);
    }

    public static LocalDate convertStringToLocalDateWithUnknownPattern(String dateString, DateTimeParserStrategy strategy) {
        return convertStringToLocalDateWithUnknownPattern(dateString, strategy, Locale.ENGLISH);
    }

    public static LocalDate convertStringToLocalDateWithUnknownPattern(String dateString, DateTimeParserStrategy strategy, Locale locale) {
        if (dateString != null) {
            for (String pattern : strategy.getPatterns()) {
                try {
                    return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(pattern, locale));
                } catch (DateTimeParseException excep) {
                    // Nothing to do
                }
            }
            throw new IllegalArgumentException("Not able to parse the date" + "\"" + dateString + "\"" + "for all patterns allowed");
        } else {
            return null;
        }
    }

    public static String normalizeDate(final String date) {
        if (date != null) {
            final String input = date.trim();
            final StringBuilder normalizedDate = new StringBuilder();
            boolean firstChar = true;
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                if (Character.isDigit(c)) {
                    normalizedDate.append(c);
                    firstChar = true;
                } else if (Character.isAlphabetic(c)) {
                    if (firstChar) {
                        normalizedDate.append((Character.toUpperCase(c)));
                        firstChar = false;
                    } else {
                        normalizedDate.append((Character.toLowerCase(c)));
                    }
                } else {
                    normalizedDate.append('-');
                    firstChar = true;
                }
            }
            return normalizedDate.toString();
        }
        return null;
    }
}
