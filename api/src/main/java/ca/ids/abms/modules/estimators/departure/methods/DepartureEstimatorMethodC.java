package ca.ids.abms.modules.estimators.departure.methods;

import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.aerodromes.AerodromeRepository;
import ca.ids.abms.modules.airspaces.AirspaceRepository;
import ca.ids.abms.modules.common.mappers.DateTimeMapperUtils;
import ca.ids.abms.modules.estimators.departure.DepartureEstimatorModel;
import ca.ids.abms.modules.estimators.departure.DepartureEstimatorResult;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocation;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocationRepository;
import ca.ids.abms.modules.util.models.NavDBUtils;
import ca.ids.abms.modules.util.models.geometry.CoordinatesVO;
import ca.ids.abms.util.GeometryUtils;
import com.vividsolutions.jts.geom.Coordinate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Algorithm For Calculating Day-Of-Flight and Departure-Time
 *
 * Method C – Estimation for Waypoint Location (Radar Strips and ATC Logs)
 *
 * Use this if the only information available is:
 * - Flight Id
 * - Departure aerodrome
 * - Contact date/time
 * - Aircraft speed or registration number or aircraft type
 * - Waypoint location and time
 *
 * Calculate distance between departure and way point location.
 *
 * Determine the airspeed from one the following in order of precedence:
 * - Air speed given
 * - Air speed of any flight matching this registration number;
 * - Airspeed of any flight matching this aircraft type.
 *
 * Determine elapsed time by dividing distance by speed at waypoint.
 *
 * Subtract elapsed time from contact date and time at first waypoint to determine day-of-flight and departure-time.
 */
@Component
public class DepartureEstimatorMethodC {

    private static final Logger LOG = LoggerFactory.getLogger(DepartureEstimatorMethodC.class);

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
    private final FlightMovementRepository flightMovementRepository;
    private final UnspecifiedDepartureDestinationLocationRepository unspecifiedDepartureDestinationLocationRepository;

    DepartureEstimatorMethodC(
        final AerodromeRepository aerodromeRepository,
        final AirspaceRepository airspaceRepository,
        final FlightMovementRepository flightMovementRepository,
        final NavDBUtils navdbUtils,
        final UnspecifiedDepartureDestinationLocationRepository unspecifiedDepartureDestinationLocationRepository
    ) {
        this.aerodromeRepository = aerodromeRepository;
        this.airspaceRepository = airspaceRepository;
        this.flightMovementRepository = flightMovementRepository;
        this.navdbUtils = navdbUtils;
        this.unspecifiedDepartureDestinationLocationRepository = unspecifiedDepartureDestinationLocationRepository;
    }

    /**
     * Guess departure time and day of flight based on waypoints. Returns result if
     * dayOfFlight or departureTime have been estimated.
     */
    public DepartureEstimatorResult estimateDepartureTime(final DepartureEstimatorModel model) {

        // model name string for log messages
        final String displayName = model.getDisplayName();
        LOG.debug ("METHOD C: Trying to estimate departure time for {}", displayName);

        // return null if model does not contain enough information
        // to use method C when determining departure time
        if (!hasRequiredFields(model)) {
            LOG.debug ("METHOD C: unable to estimate departure time for {}: one of required " +
                "fields is missing or blank", displayName);
            return null;
        }

        // parse entry time
        final LocalTime contactTime = tryParseTime (model.getFirEntryTime());
        if (contactTime == null) {
            LOG.debug ("METHOD C: unable to estimate departure time for {}: entry time \"{}\" is invalid",
                    displayName, model.getFirEntryTime());
            return null;
        }

        // resolve cruising speed by given speed or existing flight movements
        final String cruisingSpeed = tryResolveCruisingSpeed(model);

        // parse cruising speed into a "meters per second" value
        final Double speedMetersPerSecond = tryParseCruisingSpeed (cruisingSpeed);
        if (speedMetersPerSecond == null) {
            LOG.debug ("METHOD C: unable to estimate departure time for {}: cruising speed \"{}\" is invalid",
                    displayName, model.getCruisingSpeed());
            return null;
        }
        LOG.trace ("METHOD C: speed={} m/s", speedMetersPerSecond);

        // resolve departure aerodrome to a geographical location
        final Coordinate departureCoord = tryResolveAerodrome (model.getDepAd());
        if (departureCoord == null) {
            LOG.debug ("METHOD C: unable to estimate departure time for {}: departure aerodrome \"{}\" can't be resolved to a geographical location",
                    displayName, model.getDepAd());
            return null;
        }
        LOG.trace ("METHOD C: departureCoord={}", departureCoord);

        // resolve waypoint to a geographical location
        final Coordinate entryCoord = tryResolveEntryWaypoint (model.getFirEntryPoint(), departureCoord);
        if (entryCoord == null) {
            LOG.debug ("METHOD C: unable to estimate departure time for {}: entry waypoint \"{}\" can't be resolved to a geographical location",
                    displayName, model.getFirEntryPoint());
            return null;
        }
        LOG.trace ("METHOD C: entryCoord={}", entryCoord);

        // get the distance between departure and entry points in meters
        final Double distanceMeters = getGeoidDistance (departureCoord, entryCoord);
        if (distanceMeters == null) {
            LOG.debug ("METHOD C: unable to estimate departure time for {}: couldn't calculate distance between departure and entry points", displayName);
            return null;
        }
        LOG.trace ("METHOD C: distance={} m", distanceMeters);

        // elapsed time in seconds
        final long secondsSinceDeparture = (long)(distanceMeters / speedMetersPerSecond);
        LOG.trace ("METHOD C: secondsSinceDeparture = distance/speed = {}", secondsSinceDeparture);

        // full date/time
        final LocalDateTime contactDateTime = LocalDateTime.of (model.getDateOfContact().toLocalDate(), contactTime);

        // estimated departure time
        final LocalDateTime estimatedDepartureDateTime = contactDateTime.minusSeconds (secondsSinceDeparture);
        LOG.debug ("METHOD C: estimatedDepartureTime = {}", estimatedDepartureDateTime);

        // derive day of flight and departure time from estimated departure
        final LocalDate dayOfFlight = estimatedDepartureDateTime.toLocalDate();
        final LocalTime departureTime = estimatedDepartureDateTime.toLocalTime();

        // done - return result
        return new DepartureEstimatorResult.Builder()
            .setDayOfFlight(dayOfFlight.atTime (0, 0))
            .setDepTime(formatDepartureTime(departureTime))
            .build();
    }

    /**
     * Format LocalTime using DateTimeMapperUtils default formatter and return as String value.
     */
    private static String formatDepartureTime (final LocalTime time) {
        return DateTimeMapperUtils.parseSystemTime(time);
    }

    /**
     * Return true if model contains all fields required for method C calculation.
     */
    private static boolean hasRequiredFields (final DepartureEstimatorModel model) {
        return model != null &&
            model.getDateOfContact() != null &&
            StringUtils.isNotBlank(model.getFlightId()) &&
            StringUtils.isNotBlank(model.getDepAd()) && (
                StringUtils.isNotBlank(model.getCruisingSpeed()) || StringUtils.isNotBlank(model.getRegNum()) ||
                StringUtils.isNotBlank(model.getAircraftType())) &&
            StringUtils.isNotBlank(model.getFirEntryPoint()) &&
            StringUtils.isNotBlank(model.getFirEntryTime());
    }

    /**
     * Parse a string, return a LocalTime object or null of unparsable.
     */
    static LocalTime tryParseTime(final String s) {
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
    static Double tryParseCruisingSpeed(final String s) {
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
    Coordinate tryResolveAerodrome(final String aerodromeCode) {

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
     * Determine the airspeed from one the following in order of precedence:
     * - Air speed given
     * - Air speed of any flight matching this registration number;
     * - Airspeed of any flight matching this aircraft type.
     */
    String tryResolveCruisingSpeed(final DepartureEstimatorModel model) {

        // if cruising speed provided in model, use it and return
        if (StringUtils.isNotBlank(model.getCruisingSpeed())) {
            LOG.debug("Resolved cruising speed to '{}' using provided value", model.getCruisingSpeed());
            return model.getCruisingSpeed();
        }

        // determine cruising speed by existing flight movement with same registration number
        if (StringUtils.isNotBlank(model.getRegNum())) {
            String cruisingSpeed = flightMovementRepository.findLatestCruisingSpeedByRegistrationNumber(model.getRegNum());
            if (StringUtils.isNotBlank(cruisingSpeed)) {
                LOG.debug("Resolved cruising speed to '{}' using existing flight movement with registration number '{}'",
                    cruisingSpeed, model.getRegNum());
                return cruisingSpeed;
            }
        }

        // determine cruising speed by existing flight movement with same aircraft type
        if (StringUtils.isNotBlank(model.getAircraftType())) {
            String cruisingSpeed = flightMovementRepository.findLatestCruisingSpeedByAircraftType(model.getAircraftType());
            if (StringUtils.isNotBlank(cruisingSpeed)) {
                LOG.debug("Resolved cruising speed to '{}' using existing flight movement with aircraft type '{}'",
                    cruisingSpeed, model.getAircraftType());
                return cruisingSpeed;
            }
        }

        // return null indicating that no cruising speed could be resolved
        LOG.warn("Could not resolve cruising speed by provided value '{}' or an existing flight movement with " +
            "registration number '{}' or aircraft type '{}'", model.getCruisingSpeed(), model.getRegNum(), model.getAircraftType());
        return null;
    }

    /**
     * Resolve a waypoint to coordinates
     */
    Coordinate tryResolveEntryWaypoint(final String waypoint, final Coordinate departureCoord) {
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
    private Double getGeoidDistance (final Coordinate a, final Coordinate b) {
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
