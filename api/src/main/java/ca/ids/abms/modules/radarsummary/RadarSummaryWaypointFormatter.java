package ca.ids.abms.modules.radarsummary;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

class RadarSummaryWaypointFormatter {

    private static final Integer WAYPOINT_VALUE_LENGTH = 3;

    private static final Integer WAYPOINT_DATETIME_INDEX = 0;
    private static final Integer WAYPOINT_POINT_INDEX = 1;
    private static final Integer WAYPOINT_LEVEL_INDEX = 2;

    private static final String WAYPONIT_VALUE_NULL = "[null]";

    static String format(final List<RadarSummaryWaypoint> waypoints) {
        if (waypoints == null || waypoints.isEmpty())
            return null;

        StringJoiner value = new StringJoiner(RadarSummaryConstants.WAYPOINTS_ENTRY_SEPARATOR);
        for (RadarSummaryWaypoint waypoint : waypoints) {
            value.add(formatWaypoint(waypoint));
        }
        return value.toString();
    }

    static List<RadarSummaryWaypoint> parse(final String value) {
        if (value == null || value.isEmpty())
            return new ArrayList<>();

        String[] items = value.split(RadarSummaryConstants.WAYPOINTS_ENTRY_SEPARATOR);

        List<RadarSummaryWaypoint> waypoints = new ArrayList<>();
        for (String item : items) {
            if (item != null && !item.isEmpty())
                waypoints.add(parseWaypoint(item));
        }

        return waypoints;
    }

    private static String formatDateTime(final LocalDateTime dateTime) {
        if (dateTime == null) return WAYPONIT_VALUE_NULL;
        return RadarSummaryConstants.WAYPOINTS_DATETIME_FORMATTER.format(dateTime);
    }

    private static String formatNullable(final String value) {
        return value == null ? WAYPONIT_VALUE_NULL : value;
    }

    private static String formatWaypoint(final RadarSummaryWaypoint waypoint) {
        StringJoiner values = new StringJoiner(RadarSummaryConstants.WAYPOINTS_VALUE_SEPARATOR);

        values.add(formatDateTime(waypoint.getDateTime()));
        values.add(formatNullable(waypoint.getPoint()));
        values.add(formatNullable(waypoint.getLevel()));

        return values.toString();
    }

    private static LocalDateTime parseDateTime(final String dateTime) {
        if (dateTime == null || dateTime.trim().equals(WAYPONIT_VALUE_NULL)) return null;
        return RadarSummaryConstants.WAYPOINTS_DATETIME_FORMATTER.parse(dateTime, LocalDateTime::from);
    }

    private static String parseNullable(final String value) {
        return value == null || value.trim().equals(WAYPONIT_VALUE_NULL) ? null : value;
    }

    private static RadarSummaryWaypoint parseWaypoint(final String value) {
        if (value == null || value.isEmpty())
            return null;

        String[] items = value.split(RadarSummaryConstants.WAYPOINTS_VALUE_SEPARATOR);
        if (items.length != WAYPOINT_VALUE_LENGTH)
            return null;

        RadarSummaryWaypoint waypoint = new RadarSummaryWaypoint();

        waypoint.setDateTime(parseDateTime(items[WAYPOINT_DATETIME_INDEX]));
        waypoint.setPoint(parseNullable(items[WAYPOINT_POINT_INDEX]));
        waypoint.setLevel(parseNullable(items[WAYPOINT_LEVEL_INDEX]));

        return waypoint;
    }

    private RadarSummaryWaypointFormatter() {
        throw new IllegalStateException("Utility class, do not instantiate a new instance.");
    }
}
