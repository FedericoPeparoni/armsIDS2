package ca.ids.abms.modules.flight;

import static ca.ids.abms.modules.common.mappers.DateTimeMapperUtils.normalizeTime;

import java.util.List;
import java.util.StringTokenizer;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.common.enumerators.DailySchedule;
import ca.ids.abms.modules.common.mappers.ABMSMapper;
import ca.ids.abms.modules.flightmovementsbuilder.utility.FlightMovementBuilderUtility;

@Mapper
public abstract class FlightScheduleMapper extends ABMSMapper {

    @Autowired
    private FlightMovementBuilderUtility flightMovementBuilderUtility;

    /* Backend to Frontend mapping */
    public abstract List<FlightScheduleViewModel> toViewModel(Iterable<FlightSchedule> items);

    @Mapping(target = "missingFlightMovements", ignore = true)
    @Mapping(target = "unexpectedFlights", ignore = true)
    @Mapping(target = "scRequestId", ignore = true)
    @Mapping(target = "scRequestType", ignore = true)
    public abstract FlightScheduleViewModel toViewModel(FlightSchedule item);

    /* Frontend to backend mapping */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    public abstract FlightSchedule toModel(FlightScheduleViewModel dto);

    /* CSV file to backend mapping */
    public abstract List<FlightSchedule> toModel(Iterable<FlightScheduleCsvViewModel> items);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "selfCare", ignore = true)
    @Mapping(target = "depTime", ignore = true)
    @Mapping(target = "destTime", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "activeIndicator", ignore = true)
    public abstract FlightSchedule toModel(FlightScheduleCsvViewModel dto);

    @Mapping(target = "account", source = "account.name")
    @Mapping(target = "dailySchedule", ignore = true)
    public abstract FlightScheduleCsvExportModel toCsvModel(FlightSchedule flightSchedule);

    abstract List<FlightScheduleCsvExportModel> toCsvModel(Iterable<FlightSchedule> flightSchedules);

    @Mapping(target = "account", source = "account.name")
    @Mapping(target = "dailySchedule", ignore = true)
    public abstract FlightScheduleCsvExportModel toCsvModelFromViewModel (FlightScheduleViewModel flightSchedule);

    public abstract List<FlightScheduleCsvExportModel> toCsvModelFromViewModel(Iterable<FlightScheduleViewModel> flightSchedules);

    @AfterMapping
    void parseFlightScheduleToCSV(final FlightSchedule source, @MappingTarget FlightScheduleCsvExportModel target) {
        target.setDailySchedule(source.getDailySchedule() != null ? mapDailyScheduleForExportCSV(source.getDailySchedule()) : null);
    }

    @AfterMapping
    void parseFlightScheduleToCSV(final FlightScheduleViewModel source, @MappingTarget FlightScheduleCsvExportModel target) {
        target.setDailySchedule(source.getDailySchedule() != null ? mapDailyScheduleForExportCSV(source.getDailySchedule()) : null);
    }

    @AfterMapping
    public void parseFlightScheduleFromCSV(final FlightScheduleCsvViewModel source,
            @MappingTarget FlightSchedule result) {

        final String depTime = normalizeTime(source.getDepTime());
        final String destTime = normalizeTime(source.getDestTime());

        result.setDepTime(depTime);
        result.setDestTime(destTime);

        // TODO Self care is not set up until Sprint 2, so for now everything
        // will be false
        result.setSelfCare(false);

        Account acc = mapAccountFromFlightId(source.getFlightServiceNumber());
        result.setAccount(acc);

        String dailySchedule = mapDailySchedule(source.getDailySchedule());
        result.setDailySchedule(dailySchedule);

        result.setActiveIndicator(FlightScheduleService.ACTIVE);
    }

    private String mapDailyScheduleForExportCSV (final String source) {
        String target = null;
        if (source != null) {
            StringBuilder sb = new StringBuilder();
            StringTokenizer st = new StringTokenizer(source, ",");
            while (st.hasMoreTokens()) {
                String current = st.nextToken();
                String converted = DailySchedule.forValue(current).name();
                sb.append(converted);
                sb.append(",");
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
            }
            target = sb.toString();
        }
        return target;
    }

    private String mapDailySchedule(final String source) {
        String target = null;
        if (source != null) {
            StringBuilder sb = new StringBuilder();
            StringTokenizer st = new StringTokenizer(source, "/");
            while (st.hasMoreTokens()) {
                String current = st.nextToken();
                String converted = DailySchedule.valueOf(current).getValue();
                sb.append(converted);
                sb.append(",");
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
            }
            target = sb.toString();
        }
        return target;
    }

    private Account mapAccountFromFlightId(String flightId) {
        return flightMovementBuilderUtility.getAccountByFlightId(flightId);
    }
}
