package ca.ids.abms.modules.util.models;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Created by c.talpa on 01/03/2017.
 */
public class DateTimeUtils {

    private static final Logger LOG = Logger.getLogger(DateTimeUtils.class);

    private static final String TIME_PATTERN = "HHmm";

    private static final Pattern HHmm_VALIDATION_PATTERN = Pattern.compile ("[0-9]{4}$");

    private static final Integer MINUTES_HOUR = 60;

    private static final String MSSQL_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static LocalDateTime addTimeToDate(LocalDateTime aDate, String aTimeString) {
        LocalDateTime ret = null;
        if (aDate != null && org.apache.commons.lang.StringUtils.isNotEmpty(aTimeString)) {
            LocalTime time = DateTimeUtils.convertStringToLocalTime( aTimeString);
            ret = addTimeToDate(aDate, time);
        }
        return ret;
    }

    public static LocalDateTime addTimeToDate(LocalDateTime aDate, LocalTime aTime) {
        LocalDateTime ret = null;
        if (aDate != null && aTime != null) {
            Integer minutes = getMinutes(aTime);
            ret = aDate.plusMinutes(minutes);
        }
        return ret;
    }

    public static Integer getMinutes(String time){
        int minutes= 0;

        if(StringUtils.isNotBlank(time)){
            LocalTime localTime=convertStringToLocalTime(time);
            if(localTime!=null) {
                minutes = localTime.getHour() * MINUTES_HOUR + localTime.getMinute();
            }
        }

        return minutes;
    }

    static LocalDateTime plusMinute(LocalDateTime localDateTime, String time, Integer minutes){
        LocalDateTime localDateTimeCalulated=null;

        if(localDateTime!=null && StringUtils.isNotBlank(time) && minutes!=null){
            LocalTime localTime=convertStringToLocalTime(time);
            if(localTime!=null) {
                LocalDate localDate = localDateTime.toLocalDate();
                localDateTimeCalulated = LocalDateTime.of(localDate, localTime);
                localDateTimeCalulated = localDateTimeCalulated.plusMinutes(minutes);
            }
        }

        return localDateTimeCalulated;
   }

    public static LocalDateTime minusMinute(LocalDateTime localDateTime, String time, Integer minutes){
        LocalDateTime localDateTimeCalulated=null;

        if(localDateTime!=null && StringUtils.isNotBlank(time) && minutes!=null){
            LocalTime localTime=convertStringToLocalTime(time);
            if(localTime!=null) {
                LocalDate localDate = localDateTime.toLocalDate();
                localDateTimeCalulated = LocalDateTime.of(localDate, localTime);
                localDateTimeCalulated = localDateTimeCalulated.minusMinutes(minutes);
            }
        }


        return localDateTimeCalulated;
    }

    public static String convertLocalDateTimeToTimeString(LocalDateTime localDateTime){

        String result=null;

        if(localDateTime!=null){
            result=localDateTime.toLocalTime().toString().replace(":","");
        }
        return result;
    }

    public static String convertLocalTimeToString(LocalTime time) {
        if (time == null)
            return null;
        else
            return time.format(DateTimeFormatter.ofPattern(TIME_PATTERN));
    }

    /**
     * Conversion of HHmm into total LocalTime.
     *
     * Left pads any missing digits as '0'. For example, '123' would be
     * interpreted as '0123'.
     *
     * Any invalid time provided will result in a null response.
     */
    public static LocalTime convertStringToLocalTime(String time) {

        if (time == null || StringUtils.isBlank(time))
            return null;

        // ensure time value is 4 characters long, prepend any required padding as '0'
        String value = StringUtils.leftPad(time.trim(), 4, '0');

        return HHmm_VALIDATION_PATTERN.matcher(value).matches()
            ? LocalTime.parse(value, DateTimeFormatter.ofPattern(TIME_PATTERN))
            : null;
    }

    public static LocalDateTime convertStringToLocalDateTime(String dateTime){

        if (dateTime == null || dateTime.isEmpty()) {
            return null;
        }

        LocalDateTime result = null;
        try {
            final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            result = LocalDateTime.parse(dateTime, fmt);
        } catch(DateTimeParseException e){
            LOG.debug("Error DateTimeParseException : ",e);
        }
        return result;
    }

	/**
	 * Conversion of HHmm into total number of minutes.
     *
     * Left pads any missing digits as '0'. For example, '123' would be
     * interpreted as '0123'.
     *
     * Any invalid time provided will result in a null response.
	 */
	public static Integer convertHHmmToMinutes(String time) {

		if (time == null || StringUtils.isBlank(time))
		    return null;

        // ensure time value is 4 characters long, prepend any required padding as '0'
        String value = StringUtils.leftPad(time.trim(), 4, '0');

        Integer result = null;
        if (HHmm_VALIDATION_PATTERN.matcher(value).matches()) {

            try {

                int hours = Integer.parseInt(value.substring(0, 2));
                int minutes = Integer.parseInt(value.substring(2, 4));
                result = hours * 60 + minutes;

            } catch (NumberFormatException ex) {
                LOG.error("Can't parse HHmm: " + time);
            }
        }

		return result;
	}

    /**
     * Return true if provided time value matches the format 'HHmm'. No normalization is
     * done meaning '123' is considered invalid.
     */
	public static boolean isValidTimeFormat(String time) {
	    return HHmm_VALIDATION_PATTERN.matcher(time.trim()).matches();
    }

    public static String addTime(String aDepTime, String aEstimatedTime) {
        Integer elapsedMinute = convertHHmmToMinutes(aEstimatedTime);
        return plusMinutes(aDepTime, elapsedMinute);
    }

    public static String dateToMSSqlDateTime(Object date) {
        if (date == null) {
            return null;
        }

        SimpleDateFormat mssqlFormat = new SimpleDateFormat(MSSQL_DATETIME_FORMAT);

        return mssqlFormat.format(date);
    }

    public static String dateToMSSqlDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null)
            return null;
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(MSSQL_DATETIME_FORMAT);
	    return localDateTime.format(formatter);
    }

    static String plusMinutes(String time, Integer minutes){
        String timeCalculatedStr=null;

        if(StringUtils.isNotBlank(time) && minutes!=null){
            LocalTime localTime=convertStringToLocalTime(time);
            if(localTime!=null) {
                LocalTime timeCalculated = localTime.plusMinutes(minutes);
                timeCalculatedStr = timeCalculated.toString().replace(":", "");
            }
        }

        return timeCalculatedStr;
    }

    static String minusMinutes(String time, Integer minutes){
        String timeCalculatedStr=null;

        if(StringUtils.isNotBlank(time) && minutes!=null){
            LocalTime localTime=convertStringToLocalTime(time);
            if(localTime!=null) {
                LocalTime timeCalculated = localTime.minusMinutes(minutes);
                timeCalculatedStr = timeCalculated.toString().replace(":", "");
            }
        }

        return timeCalculatedStr;
    }

    private static Integer getMinutes(LocalTime time) {
        Integer minutes = null;

        if (time != null) {
            minutes = time.getHour() * MINUTES_HOUR + time.getMinute();
        }

        return minutes;
    }

    private DateTimeUtils() {
        throw new IllegalStateException("Utility class, do not instantiate a new instance.");
    }
}
