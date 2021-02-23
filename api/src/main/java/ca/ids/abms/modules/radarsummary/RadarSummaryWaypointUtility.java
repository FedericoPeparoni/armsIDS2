package ca.ids.abms.modules.radarsummary;

import ca.ids.abms.modules.radarsummary.enumerators.RadarSummaryFormat;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.util.billingcontext.BillingContext;
import ca.ids.abms.util.billingcontext.BillingContextKey;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
class RadarSummaryWaypointUtility {

    private final SystemConfigurationService systemConfigurationService;

    RadarSummaryWaypointUtility(
        final SystemConfigurationService systemConfigurationService
    ) {
        this.systemConfigurationService = systemConfigurationService;
    }

    /**
     * If `BillingContext.MERGE_WAYPOINTS` is TRUE, merge source radar route, entry point, entry time,
     * exit point, and exit time with target and resolve by waypoints.
     *
     * If `BillingContext.MERGE_WAYPOINTS` is FALSE, overwrite target radar route, entry point,
     * entry time, exit point, and exit time with source and resolve by waypoints.
     */
    void resolveRadarWaypoints(final RadarSummary source, final RadarSummary target) {

        RadarSummaryFormat format = RadarSummaryFormat.forName(systemConfigurationService
            .getValue(SystemConfigurationItemName.RADAR_SUMMARY_FORMAT));

        Boolean mergeWaypoints = BillingContext.get(BillingContextKey.MERGE_WAYPOINTS);

        // merge or overwrite radar waypoint data depending on billing context and radar format
        if (RadarSummaryFormat.INDRA_REC == format && (mergeWaypoints == null || mergeWaypoints)) {
            mergeRadarWaypoints(source, target);
        } else {
            overwriteRadarWaypoints(source, target);
        }
    }

    /**
     * Resolve route, entry point, entry time, exit point, and exit time by waypoints if exist and not empty.
     */
    void resolveRouteAndFirPoints(final RadarSummary radarSummary) {

        // resolve by waypoints only if waypoints are not empty
        // check for null and empty is to support legacy radar upload formats without waypoints
        if (radarSummary == null || radarSummary.getWaypoints() == null || radarSummary.getWaypoints().isEmpty())
            return;

        // resolve route and fir points by merged waypoints
        // empty waypoints will clear route and fir points
        resolveRouteAndFirPoints(radarSummary, radarSummary.getWaypoints());
    }

    /**
     * Overwrite route, entry point, entry time, exit point, and exit time by waypoints. Fields will be cleared
     * if waypoints do not exist.
     */
    private void resolveRouteAndFirPoints(final RadarSummary radarSummary, final List<RadarSummaryWaypoint> waypoints) {

        // retrieve and set route if defined, else define as null to clear
        radarSummary.setRoute(buildRouteFromWaypoints(waypoints));

        // retrieve and set FIR entry/exit points if defined, else define as null to clear
        radarSummary.setFirEntryPoint(buildPointFromWaypoints(waypoints, true));
        radarSummary.setFirEntryTime(buildTimeFromWaypoints(waypoints, true));

        // retrieve FIR entry/exit times and format if defined, else define as null to clear
        radarSummary.setFirExitPoint(buildPointFromWaypoints(waypoints, false));
        radarSummary.setFirExitTime(buildTimeFromWaypoints(waypoints, false));
    }

    /**
     * Build route text from a supplied list of radar summary waypoints. Returns null if list is not
     * defined, empty or all list of waypoints' fixed points are null/empty.
     */
    private String buildRouteFromWaypoints(final List<RadarSummaryWaypoint> waypoints) {

        // return if waypoints is not defined and empty as nothing to build
        if (waypoints == null || waypoints.isEmpty())
            return null;

        // create route with whitespace between each waypoint
        StringJoiner route = new StringJoiner(" ");

        // loop through each waypoint and add the fixed point if exists
        for (RadarSummaryWaypoint waypoint : waypoints) {
            if (waypoint != null && waypoint.getPoint() != null && !waypoint.getPoint().isEmpty())
                route.add(waypoint.getPoint());
        }

        // only return route text if not empty, else null
        return route.length() == 0 ? null
            : route.toString();
    }

    /**
     * Build point text from a supplied list of radar summary waypoints. Returns null if list is not
     * defined, empty or point is null.
     *
     * @param isEntry true if finding first waypoint, else last waypoint
     */
    private String buildPointFromWaypoints(final List<RadarSummaryWaypoint> waypoints, final boolean isEntry) {

        // return if waypoints is not defined and empty as nothing to build
        if (waypoints == null || waypoints.isEmpty())
            return null;

        // retrieve first or last waypoints, assumed to be FIR entry or exit point
        RadarSummaryWaypoint waypoint = isEntry ? waypoints.get(0)
            : waypoints.get(waypoints.size() - 1);

        // return point or null if not defined
        return waypoint == null ? null : waypoint.getPoint();
    }

    /**
     * Build time text from a supplied list of radar summary waypoints. Returns null if list is not
     * defined, empty or datetime is null.
     *
     * @param isEntry true if finding first waypoint, else last waypoint
     */
    private String buildTimeFromWaypoints(final List<RadarSummaryWaypoint> waypoints, final boolean isEntry) {

        // return if waypoints is not defined and empty as nothing to build
        if (waypoints == null || waypoints.isEmpty())
            return null;

        // retrieve first or last waypoints, assumed to be FIR entry or exit time
        RadarSummaryWaypoint waypoint = isEntry ? waypoints.get(0)
            : waypoints.get(waypoints.size() - 1);

        // return as formatted time or null if not defined
        return waypoint.getDateTime() == null ? null
            : waypoint.getDateTime().format(RadarSummaryConstants.TIME_FORMATTER);
    }

    /**
     * Concatenate and sort radar waypoints. Duplicates by radar summery waypoint threshold or sequential points
     * are removed from the concatenated list. When duplicates found, first by datetime is retained.
     */
    private List<RadarSummaryWaypoint> concatAndSortRadarWaypoints(final List<RadarSummaryWaypoint> a, final List<RadarSummaryWaypoint> b) {

        // concatenate and sort collections in natural order
        List<RadarSummaryWaypoint> waypoints = Stream
            .concat(a.stream().filter(Objects::nonNull), b.stream().filter(Objects::nonNull))
            .sorted(Comparator.comparing(RadarSummaryWaypoint::getDateTime, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList());

        // collect only waypoints that are defined, do not have the same point as the previous and not duplicate
        // based on the radar summery waypoint threshold, first by datetime are always kept
        List<RadarSummaryWaypoint> result = new ArrayList<>();
        for (RadarSummaryWaypoint waypoint : waypoints) {

            if (waypoint == null)
                continue;

            if (result.isEmpty() || (!Objects.equals(result.get(result.size() - 1).getPoint(), waypoint.getPoint()) &&
                result.stream().noneMatch(o -> matchRadarWaypoint(o, waypoint))))
                result.add(waypoint);
        }

        return result;
    }

    /**
     * Returns true if the provided waypoints match by point and within radar summary waypoint threshold.
     */
    private boolean matchRadarWaypoint(final RadarSummaryWaypoint a, final RadarSummaryWaypoint b) {

        // return false if objects not defined, datetimes not defined, or points not equal
        if (a == null || b == null || a.getDateTime() == null || b.getDateTime() == null || !Objects.equals(a.getPoint(), b.getPoint()))
            return false;

        // get the difference between datetime values and return true if within threshold
        long diff = Math.abs(RadarSummaryConstants.WAYPOINTS_TIME_THRESHOLD_UNIT
            .between(b.getDateTime(), a.getDateTime()));
        return diff <= RadarSummaryConstants.WAYPOINTS_TIME_THRESHOLD_VALUE;
    }

    /**
     * Merge source radar route, entry point, entry time, exit point, and exit time with target.
     *
     * Duplicate waypoints and waypoints within a specified tolerance are overwritten by the
     * source waypoint data.
     */
    private void mergeRadarWaypoints(final RadarSummary source, final RadarSummary target) {

        // merge source and target waypoints, remove duplicates, and sort by datetime
        List<RadarSummaryWaypoint> waypoints = concatAndSortRadarWaypoints(target.getWaypoints(), source.getWaypoints());
        target.setWaypoints(waypoints);

        // resolve route and fir points by merged waypoints
        // empty waypoints will clear route and fir points
        resolveRouteAndFirPoints(target, waypoints);
    }

    /**
     * Overwrite target radar route, entry point, entry time, exit point, and exit time with source.
     */
    private void overwriteRadarWaypoints(final RadarSummary source, final RadarSummary target) {

        // overwrite target data with source
        target.setRoute(source.getRoute());
        target.setFirEntryPoint(source.getFirEntryPoint());
        target.setFirEntryTime(source.getFirEntryTime());
        target.setFirExitPoint(source.getFirExitPoint());
        target.setFirExitTime(source.getFirExitTime());
        target.setWaypoints(source.getWaypoints());

        // resolve by waypoints only if waypoints are not empty
        resolveRouteAndFirPoints(target);
    }
}
