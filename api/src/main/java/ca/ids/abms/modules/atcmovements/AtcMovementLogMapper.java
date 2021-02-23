package ca.ids.abms.modules.atcmovements;

import ca.ids.abms.modules.common.enumerators.FlightType;
import ca.ids.abms.modules.common.mappers.ABMSMapper;
import ca.ids.abms.modules.common.services.MovementLogItemsResolver;
import static ca.ids.abms.modules.common.mappers.DateTimeMapperUtils.*;

import ca.ids.abms.modules.utilities.flights.FlightUtility;
import ca.ids.abms.util.converter.DateTimeParserStrategy;
import ca.ids.abms.util.converter.JSR310DateConverters;
import org.mapstruct.*;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

@Mapper(uses = {MovementLogItemsResolver.class})
public abstract class AtcMovementLogMapper extends ABMSMapper {

    /* Backend to Frontend mapping */
    public abstract List<AtcMovementLogViewModel> toViewModel(Iterable<AtcMovementLog> items);
    public abstract AtcMovementLogViewModel toViewModel(AtcMovementLog item);

    /* Frontend to backend mapping */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    public abstract AtcMovementLog toModel(AtcMovementLogViewModel dto);

    /* CSV file to backend mapping */
    public abstract List<AtcMovementLog> toModel(Iterable<AtcMovementLogCsvViewModel> items);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "dateOfContact", ignore = true)
    @Mapping(target = "dayOfFlight", ignore = true)
    @Mapping(target = "firEntryTime", ignore = true)
    @Mapping(target = "firMidTime", ignore = true)
    @Mapping(target = "firExitTime", ignore = true)
    @Mapping(target = "departureTime", ignore = true)
    @Mapping(target = "flightType", ignore = true)
    @Mapping(target = "version", ignore = true)
    public abstract AtcMovementLog toModel(AtcMovementLogCsvViewModel dto);

    public abstract AtcMovementLogCsvExportModel toCsvModel(AtcMovementLog atcMovementLog);

    abstract List<AtcMovementLogCsvExportModel> toCsvModel(Iterable<AtcMovementLog> atcMovementLog);

    @AfterMapping
    void parseDateTimeFromCSV(final AtcMovementLogCsvViewModel source, @MappingTarget AtcMovementLog result) {

        final LocalDateTime date = parseDate(source.getDateOfContact());
        final String depTime = normalizeTime(source.getDepartureTime());
        final String firEntryTime = normalizeTime(source.getFirEntryTime());
        final String firMidTime = normalizeTime(source.getFirMidTime());
        final String firExitTime = normalizeTime(source.getFirExitTime());

        result.setDateOfContact(date);
        result.setDepartureTime(depTime);
        result.setFirEntryTime(firEntryTime);
        result.setFirMidTime(firMidTime);
        result.setFirExitTime(firExitTime);

        // determining the dayOfFlight if the firEntryTime is earlier then departureTime, dayOfFlight is the day before
        // if day of flight cannot be determined, do not set and rely on departure estimator methods in AtcMovementLogService
        if(firEntryTime != null && depTime != null && date != null) {
            result.setDayOfFlight(FlightUtility.resolveDateOfFlight(depTime, firEntryTime, date));
        }
    }

    private static LocalDateTime parseDate (final String str) {
        return parseCustomLocalDateToLocalDateTime (
                normalizeDate(str, DateTimeParserStrategy.DATE_PATTERNS_ALL),
                JSR310DateConverters.DEFAULT_PATTERN_DATE, Locale.ENGLISH);   
    }
    
    @AfterMapping
    void resolveFlightType(@SuppressWarnings("unused") final AtcMovementLogCsvViewModel source, @MappingTarget AtcMovementLog result) {
        result.setFlightType(FlightType.NORMAL);
    }

    @AfterMapping
    void resolveFlightLevel(final AtcMovementLogCsvViewModel source, @MappingTarget AtcMovementLog result) {

        // list of possible values to loop through
        List<String> sourceList = Arrays.asList(
            source.getFlEntryTime(),
            source.getFlMidTime(),
            source.getFlExitTime());

        // list to store valid parsed values
        List<Double> resultList = new ArrayList<>();

        // loop through each possible value and
        // if valid, parse as Integer into result list
        // allow parse errors to be thrown
        for (String item : sourceList) {
            if (item != null && !item.trim().isEmpty()) {
                resultList.add(Double.valueOf(item));
            }
        }

        // if valid results, set Flight Level as max value from the collection
        // format to remove trailing decimal(s), rounds if any and prepend with F
        // which is the standard expression of level data, see Air Traffic Management
        // (PANS-ATM) A3-4.
        if (!resultList.isEmpty()) {
            DecimalFormat decimalFormat = new DecimalFormat("#");
            result.setFlightLevel("F" + decimalFormat.format(Collections.max(resultList)));
        }
    }
}
