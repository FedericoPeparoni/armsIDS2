package ca.ids.abms.modules.radarsummary;

import ca.ids.abms.util.converter.JSR310DateConverters;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

class RadarSummaryConstants {

    /**
     * DateTimeFormatter used when formatting time values to string data types. Should always be configured to use
     * the same pattern as FlightMovement time format, currently defined from JSR310DateConverters default time pattern.
     */
    static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(JSR310DateConverters.DEFAULT_PATTERN_TIME);

    /**
     * DateTimeFormatter used when persisting and parsing waypoint datatime data.
     */
    static final DateTimeFormatter WAYPOINTS_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd:HHmm");

    /**
     * Entry separator used when persisting waypoint data.
     */
    static final String WAYPOINTS_ENTRY_SEPARATOR = ",";

    /**
     * Threshold units used to determine duplicate waypoints, value is defined by WAYPOINT_TIME_THRESHOLD_VALUE.
     */
    static final ChronoUnit WAYPOINTS_TIME_THRESHOLD_UNIT = ChronoUnit.MINUTES;

    /**
     * Threshold value used to determine duplicate waypoints, unit is defined by WAYPOINT_TIME_THRESHOLD_UNIT.
     */
    static final Integer WAYPOINTS_TIME_THRESHOLD_VALUE = 15;

    /**
     * Entry value separator used when persisting waypoint data.
     */
    static final String WAYPOINTS_VALUE_SEPARATOR = "-";

    private RadarSummaryConstants() {
        throw new IllegalStateException("Constants class should never be instantiated!");
    }
}
