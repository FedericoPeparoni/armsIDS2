package ca.ids.abms.modules.towermovements;

import ca.ids.abms.modules.common.enumerators.FlightCategory;
import ca.ids.abms.modules.common.mappers.*;
import ca.ids.abms.modules.common.services.MovementLogItemsResolver;
import ca.ids.abms.util.converter.DateTimeParserStrategy;
import ca.ids.abms.util.converter.JSR310DateConverters;
import static ca.ids.abms.modules.common.mappers.DateTimeMapperUtils.*;

import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Mapper(uses = {MovementLogItemsResolver.class})
public abstract class TowerMovementLogMapper extends ABMSMapper {

    /* Backend to Frontend mapping */
    public abstract List<TowerMovementLogViewModel> toViewModel(Iterable<TowerMovementLog> items);
    public abstract TowerMovementLogViewModel toViewModel(TowerMovementLog item);

    /* Frontend to backend mapping */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    public abstract TowerMovementLog toModel(TowerMovementLogViewModel dto);

    /* CSV file to backend mapping */
    public abstract List<TowerMovementLog> toModel(List<TowerMovementLogCsvViewModel> items);

    @Mapping(target = "flightCrew", source = "flightCrew", defaultValue = "0")
    @Mapping(target = "passengers", source = "passengers", defaultValue = "0")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "dateOfContact", ignore = true)
    @Mapping(target = "dayOfFlight", ignore = true)
    @Mapping(target = "departureContactTime", ignore = true)
    @Mapping(target = "destinationContactTime", ignore = true)
    @Mapping(target = "version", ignore = true)
    public abstract TowerMovementLog toModel(TowerMovementLogCsvViewModel dto);

    public abstract TowerMovementLogCsvExportModel toCsvModel(TowerMovementLog towerMovementLog);

    abstract List<TowerMovementLogCsvExportModel> toCsvModel(Iterable<TowerMovementLog> towerMovementLog);

    @AfterMapping
    void parseDateTimeFromCSV(final TowerMovementLogCsvViewModel source, @MappingTarget TowerMovementLog result) {

        final LocalDateTime date = parseCustomLocalDateToLocalDateTime(
            normalizeDate(source.getDateOfContact(), DateTimeParserStrategy.DATE_PATTERNS_YEAR_LAST), JSR310DateConverters.DEFAULT_PATTERN_DATE, Locale.ENGLISH);
        final String depConTime = normalizeTime(source.getDepartureContactTime());
        final String destConTime = normalizeTime(source.getDestinationContactTime());

        // dep time should not be defined normally but here for historical data
        final String depTime = normalizeTime(source.getDepartureTime());

        // do not set the dayOfFlight and rely on departure estimator methods TowerMovementLogService
        result.setDateOfContact(date);
        result.setDepartureTime(depTime);
        result.setDepartureContactTime(depConTime);
        result.setDestinationContactTime(destConTime);
    }

    @AfterMapping
    void parseFlightCategory(final TowerMovementLogCsvViewModel source, @MappingTarget TowerMovementLog result) {
        FlightCategory flightCategory = ObjectUtils.firstNonNull(
            FlightCategory.mapFromValue(source.getFlightCategory()),
            FlightCategory.mapFromValue(source.getNonSchedule()),
            FlightCategory.NON_SCH);
        result.setFlightCategory(flightCategory);
    }
}
