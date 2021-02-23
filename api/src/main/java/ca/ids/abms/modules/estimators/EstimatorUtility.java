package ca.ids.abms.modules.estimators;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.vividsolutions.jts.geom.Coordinate;

import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.aerodromes.AerodromeRepository;
import ca.ids.abms.modules.airspaces.AirspaceRepository;
import ca.ids.abms.modules.common.mappers.DateTimeMapperUtils;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocation;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocationRepository;
import ca.ids.abms.modules.util.models.NavDBUtils;
import ca.ids.abms.modules.util.models.geometry.CoordinatesVO;
import ca.ids.abms.util.GeometryUtils;

@Component
public class EstimatorUtility {

    private static final Logger LOG = LoggerFactory.getLogger(EstimatorUtility.class);

    /**
     * Used by NavDBUtils.findClosestSignificantPoint to "segmentize" local airspace repository.
     */
    private static final int SEGMENT_LENGTH_METERS = 2000;

    /**
     * Look for waypoints within 400 kilometers of airspace borders.
     */
    private static final int WAYPOINT_TO_AIRSPACE_BORDER_DISTANCE_METERS = 400000;

    private final AerodromeRepository aerodromeRepository;
    private final AirspaceRepository airspaceRepository;
    private final NavDBUtils navdbUtils;
    private final UnspecifiedDepartureDestinationLocationRepository unspecifiedDepartureDestinationLocationRepository;

    EstimatorUtility(
        final AerodromeRepository aerodromeRepository,
        final AirspaceRepository airspaceRepository,
        final NavDBUtils navdbUtils,
        final UnspecifiedDepartureDestinationLocationRepository unspecifiedDepartureDestinationLocationRepository
    ) {
        this.aerodromeRepository = aerodromeRepository;
        this.airspaceRepository = airspaceRepository;
        this.navdbUtils = navdbUtils;
        this.unspecifiedDepartureDestinationLocationRepository = unspecifiedDepartureDestinationLocationRepository;
    }

    /**
     * Parse a string, return a LocalTime object or null of unparsable.
     */
    public static LocalTime tryParseTime(final String s) {
        try {
            return DateTimeMapperUtils.parseSystemTime (s);
        }
        catch (final DateTimeParseException x) {
            return null;
        }
    }

    /**
     * Parse cruising speed, return value as meters per second, or null if unparsable
     */
    public static Double tryParseCruisingSpeed(final String s) {
        if (s == null) {
            return null;
        }
        LOG.trace ("trying to parse cruising speed {}", s);
        final Matcher m = RE_SPEED.matcher (s);
        if (!m.matches()) {
            return null;
        }
        final String prefix = m.group(1) == null ? "N" : m.group(1);
        try {
            final double value = Double.parseDouble(m.group(2));
            if (prefix.equals("K")) { // kilometers per hour
                return value * 1000 / 3600;
            }
            if (prefix.equals("N")) { // knots = nautical mile per hour; nautical mile = 1852 meters
                return value * 1852 / 3600;
            }
            else { // "M": hundredths of mach number
                // "Mach number" is speed of sound, 1 mach = 343 m/s
                return value / 100 * 343;
            }
        }
        // This should never happen because the regex has only digits here
        catch (final NumberFormatException x) {
            LOG.error ("unreachable code!");
        }
        return null;
    }
    private static final Pattern RE_SPEED = Pattern.compile ("^\\s*([KNM])?(\\d{1,5})\\s*$");

    /**
     * Resolve an aerdrome code to a geographical location. Will try aerodromes
     * and unspecified_departure_destination_locations tables, as well as NAVDB.
     */
    public Coordinate tryResolveAerodrome(final String aerodromeCode) {

        // Try to find it in abms.aerodromes table first
        LOG.trace ("trying to resolve aerodrome {}", aerodromeCode);
        final Aerodrome aerodrome = aerodromeRepository.findByAerodromeName (aerodromeCode);
        if (aerodrome != null && aerodrome.getGeometry() != null) {
            final Coordinate coord = aerodrome.getGeometry().getCentroid().getCoordinate();
            LOG.debug ("Resolved aerodrome {} to coordinates {} using ABMS aerodromes table", aerodromeCode, coord);
            return coord;
        }

        // Try to find it in unspecified locations with valid lat/long
        UnspecifiedDepartureDestinationLocation unspec = findUnspecifiedDepartureDestinationLocaftion(aerodromeCode);
        if (unspec != null) {
            final Coordinate coord = new Coordinate (unspec.getLongitude(), unspec.getLatitude());
            LOG.debug ("Resolved aerodrome {} to coordinates {} using ABMS unspecified_departure_destination_locations table",
                    aerodromeCode, coord);
            return coord;
        }

        // Try to find it in NavDB
        final CoordinatesVO vo = this.navdbUtils.getCoordinatesFromAirportNAVDB (aerodromeCode);
        if (vo != null && isValidLongitude (vo.getLongitude()) && isValidLatitude (vo.getLatitude())) {
            final Coordinate coord = new Coordinate (vo.getLongitude(), vo.getLatitude());
            LOG.debug ("Resolved aerodrome {} to coordinates {} using NAVDB",
                    aerodromeCode, coord);
            return coord;
        }

        LOG.debug ("Unable to resolve aerodrome {} to coordinates", aerodromeCode);
        return null;
    }

    /**
     * Resolve a waypoint to coordinates
     */
    public Coordinate tryResolveEntryWaypoint(final String waypoint, final Coordinate departureCoord) {
        Coordinate coord;

        LOG.trace ("trying to resolve entry point {}", waypoint);

        // In case it's coordinate, just parse it directly
        coord = GeometryUtils.parseAviationCoordinate (waypoint);
        if (coord != null) {
            LOG.trace ("Resolved waypoint {} to coordinates {}", waypoint, coord);
            return coord;
        }

        // Try to find it in unspecified locations with valid lat/long
        final UnspecifiedDepartureDestinationLocation unspec = findUnspecifiedDepartureDestinationLocaftion(waypoint);
        if (unspec != null) {
            coord = new Coordinate (unspec.getLongitude(), unspec.getLatitude());
            LOG.trace ("Resolved waypoint {} to coordinates {} using ABMS unspecified_departure_destination_locations table",
                    waypoint, coord);
            return coord;
        }

        // Try to find it in navdb: waypoints are not unique, so look for one closest to departure coordinate
        coord = navdbUtils.findClosestSignificantPoint (waypoint, departureCoord, airspaceRepository.getLocalAirspaceUnionGeom(),
                SEGMENT_LENGTH_METERS, WAYPOINT_TO_AIRSPACE_BORDER_DISTANCE_METERS);
        if (coord != null) {
            LOG.trace ("Resolved waypoint {} to coordinates {} using NAVDB", waypoint, coord);
            return coord;
        }

        return null;
    }

    /**
     * Returns an unspecified departure/destination locatino that matches the identifier and contains
     * valid lat/long values.
     */
    private UnspecifiedDepartureDestinationLocation findUnspecifiedDepartureDestinationLocaftion(String identifier) {

        // find by first textIdentifier with valid lat/long values
        // order by id for consistency
        List<UnspecifiedDepartureDestinationLocation> uDDLs = unspecifiedDepartureDestinationLocationRepository
            .findAllByTextIdentifierOrderById(identifier);
        for (UnspecifiedDepartureDestinationLocation uDDL : uDDLs) {
            if (uDDL != null && isValidLatitude(uDDL.getLatitude()) && isValidLongitude(uDDL.getLongitude()))
                return uDDL;
        }

        // else find by first aerodromeIdentifier second with valid lat/long values
        // order by id for consistency
        uDDLs = unspecifiedDepartureDestinationLocationRepository
            .findAllByAerodromeIdentifierAerodromeNameOrderById(identifier);
        for (UnspecifiedDepartureDestinationLocation uDDL : uDDLs) {
            if (uDDL != null && isValidLatitude(uDDL.getLatitude()) && isValidLongitude(uDDL.getLongitude()))
                return uDDL;
        }

        return null;
    }

    /**
     * Return true if the provided longitude value is within range
     */
    private static boolean isValidLongitude (final Double lng) {
        return lng != null && lng >= -180d && lng <= 180.0d;
    }

    /**
     * Return true if the provided latitude value is within range
     */
    private static boolean isValidLatitude (final Double lat) {
        return lat != null && lat >= -90d && lat <= 90.0d;
    }

    /**
     * Calculate a great circle distance between 2 points
     */
    public Double getGeoidDistance (final Coordinate a, final Coordinate b) {
        LOG.trace ("trying to calculate great circle distance between {} and {}", a, b);
        try {
            return navdbUtils.getGreatCircleDistance (a, b);
        }
        catch (final Exception e) {
            // This should never happen
            LOG.error ("failed to calculate great circle distance between points {} and {}: {}", a, b, e.getMessage(), e);
        }
        return null;
    }
}
