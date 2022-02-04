package ca.ids.abms.modules.radarsummary;

import static ca.ids.abms.modules.common.mappers.DateTimeMapperUtils.normalizeDate;
import static ca.ids.abms.modules.common.mappers.DateTimeMapperUtils.normalizeTime;
import static ca.ids.abms.modules.common.mappers.DateTimeMapperUtils.parseCustomLocalDateToLocalDateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.ibm.icu.text.SimpleDateFormat;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import ca.ids.abms.modules.common.mappers.ABMSMapper;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.RouteCacheUtility;
import ca.ids.abms.modules.radarsummary.enumerators.RadarSummaryFormat;
import ca.ids.abms.modules.utilities.flights.FlightUtility;
import ca.ids.abms.util.converter.DateTimeParserStrategy;
import ca.ids.abms.util.converter.JSR310DateConverters;

@Mapper(uses = { FlightTravelCategoryMapper.class }, componentModel = "spring")
public abstract class RadarSummaryMapper extends ABMSMapper {

    public abstract List<RadarSummaryViewModel> toViewModel(Iterable<RadarSummary> radarSummaries);

    public abstract RadarSummaryViewModel toViewModel(RadarSummary radarSummary);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "flightType", ignore = true)
    @Mapping(target = "destTime", ignore = true)
    public abstract RadarSummary toModel(RadarSummaryViewModel dto);

    @Mapping(source = "flightIdentifier", target = "flightId")
    @Mapping(target = "rawText", ignore = true)
    @Mapping(target = "parsed", ignore = true)
    @Mapping(target = "line", ignore = true)
    @Mapping(target = "errorMessage", ignore = true)
    @Mapping(target = "operatorName", ignore = true)
    @Mapping(target = "operatorIcaoCode", ignore = true)
    @Mapping(target = "format", ignore = true)
    @Mapping(target = "fixes", ignore = true)
    public abstract RadarSummaryCsvViewModel toCsvViewModel(RadarSummary radarSummary);

    @Mapping(source = "flightId", target = "flightIdentifier")
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "route", ignore = true)
    @Mapping(target = "dayOfFlight", ignore = true)
    @Mapping(target = "firEntryDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "waypoints", ignore = true)
    public abstract RadarSummary toModel(RadarSummaryCsvViewModel dto);

    public abstract RadarSummaryCsvExportModel toCsvModel(RadarSummary radarSummary);

    public abstract List<RadarSummaryCsvExportModel> toCsvModel(Iterable<RadarSummary> radarSummary);

    @AfterMapping
    void cleanRoute(final RadarSummaryCsvViewModel source, @MappingTarget final RadarSummary result) {
        result.setRoute(RouteCacheUtility.removeFlightLevelsAndNormalize(source.getRoute()));
    }

    private static LocalDateTime parseDate (final String str) {
        return parseCustomLocalDateToLocalDateTime (
            normalizeDate(str, DateTimeParserStrategy.DATE_PATTERNS_YEAR_FIRST),
            JSR310DateConverters.DEFAULT_PATTERN_DATE, Locale.ENGLISH);
    }

    @AfterMapping
    void parseDateTimeFromCSV(final RadarSummaryCsvViewModel source, @MappingTarget RadarSummary result) {
        // radar summary date differs from other uploads as year is first
        LocalDateTime date = parseDate (source.getDate());
        final String depTime = normalizeTime(source.getDepartureTime());
        final String destTime = normalizeTime(source.getDestTime());
        final String firEntryTime = normalizeTime(source.getFirEntryTime());
        final String firExitTime = normalizeTime(source.getFirExitTime());
        final String flightRule = normalizeFlightRule (source.getFlightRule());
        LocalDateTime dayOfFlight = parseDate (source.getDayOfFlight());

        result.setDepartureTime(depTime);
        result.setDestTime(destTime);
        result.setFirEntryTime(firEntryTime);
        result.setFirExitTime(firExitTime);
        result.setFlightRule(flightRule);

        if (source.getFirEntryDate() != null) {
        	LocalDateTime firEntryDate = parseDate(source.getFirEntryDate());
            result.setFirEntryDate(firEntryDate);
        }  

        // For all formats other than INDRA_REC; or when dayOfFlight is unknown:
        if (source.getFormat() != RadarSummaryFormat.INDRA_REC || (date != null && dayOfFlight == null)) {
            // Adjust dayOfFlight: if firEntryTime is earlier then departureTime, dayOfFlight is the day before

            LocalDateTime firEntryDate = result.getFirEntryDate();

            if (firEntryDate != null)
                dayOfFlight = FlightUtility.resolveDateOfFlight(depTime, firEntryTime, firEntryDate);
            else
                dayOfFlight = FlightUtility.resolveDateOfFlight(depTime, firEntryTime, date);
        }

        result.setDayOfFlight(dayOfFlight);

        // If dayOfFlight is known, but date isn't, try to guess it
        if (dayOfFlight != null && date == null) {
            // if firEntryTime is earlier then departureTime, the flight probably started the day before
            date = FlightUtility.resolveDateOfContact(depTime, firEntryTime, dayOfFlight);
        }

        result.setDate(date);

        // Convert (unparsed) fixes to (parsed) waypoints with full date/time fields
        final List <RadarSummaryWaypoint> waypointList = createWaypointList (Fix.parseList (source.getFixes()), date);
        result.setWaypoints (waypointList);

        // If waypointList is empty, we really should re-calculate the entry/exit point fields, as well as the "route" text
        // in the result object. This is done elsewhere -- just before we save the record.
    }

    /**
     * Convert flight rules to the format acceptable to RadarSummary entity
     *
     * Flight processing code expects "IFR" and "VFR" here, not just "I" and "V".
     */
    private static String normalizeFlightRule (final String s) {
        if (s != null) {
            if (s.equals("I") || s.equals ("i")) {
                return "IFR";
            }
            if (s.equals("V") || s.equals ("v")) {
                return "VFR";
            }
        }
        return null;
    }

    /**
     * Parse fix time (string)
     */
    private static LocalTime getFixTime (final Fix fix) {
        if (fix != null) {
            final String timeString = normalizeTime (fix.time);
            if (timeString != null) {
                return LocalTime.parse (timeString, DateTimeFormatter.ofPattern(JSR310DateConverters.DEFAULT_PATTERN_TIME));
            }
        }
        return null;
    }

    /**
     * Convert a list of (unparsed) fixes to a list of waypoints. This function will try to fill in the missing date
     * portion of the waypoints, based on the provided radar contact time.
     */
    @SuppressWarnings ("squid:S1168")
    private static List <RadarSummaryWaypoint> createWaypointList (final List <Fix> fixes, final LocalDateTime radarContactDateTime) {
        // No fixes - return null
        if (fixes == null || fixes.isEmpty()) {
            return null;
        }

        // Base date is unknown -- can't construct full dates for each waypoint =>
        // create them with NULL dates
        if (radarContactDateTime == null) {
            return fixes.stream()
                .map(fix->new RadarSummaryWaypoint (null, fix.point, fix.level))
                .collect (Collectors.toList())
                ;
        }

        // Convert fixes to waypoints by calculating the full date for each fix
        // based on the provided radarContactDateTime.
        final List <RadarSummaryWaypoint> resultList = new ArrayList<> (fixes.size());
        LocalDate date = radarContactDateTime.toLocalDate();
        LocalTime prevTime = null; // fix time from previous iteration (used in the loop below)
        for (final Fix fix: fixes) {
            final LocalTime time = getFixTime (fix);
            LocalDateTime fixFullDateTime = null;
            if (time != null) {
                // if current point's time is before previous point's time,
                // it means we just crossed over midnight => increment date by one day.
                if (prevTime != null && time.isBefore (prevTime)) {
                    date = date.plusDays (1);
                }
                prevTime = time;
                // construct the full date for the waypoint inserted below
                fixFullDateTime = LocalDateTime.of (date, time);
            }
            final RadarSummaryWaypoint waypoint = new RadarSummaryWaypoint (fixFullDateTime, fix.point, fix.level);
            resultList.add (waypoint);
        }
        return resultList;
    }
}
