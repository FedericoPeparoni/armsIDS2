package ca.ids.abms.modules.utilities.schedules;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ca.ids.abms.modules.utilities.towns.UtilitiesTownsAndVillage;
import ca.ids.abms.modules.utilities.towns.UtilitiesTownsAndVillageViewModel;
import org.mapstruct.MappingTarget;

@Mapper
public interface UtilitiesSchedulerMapper {

    Collection<UtilitiesRangeBracketViewModel> toViewModel(Collection <UtilitiesRangeBracket> items);

    @Mapping(target = "scheduleId", source = "schedule.scheduleId")
    UtilitiesRangeBracketViewModel toViewModel(UtilitiesRangeBracket item);

    Set<UtilitiesTownsAndVillageViewModel> toViewModel(Set<UtilitiesTownsAndVillage> item);

    @Mapping(target = "waterUtilitySchedule", ignore = true)
    @Mapping(target = "residentialElectricityUtilitySchedule", ignore = true)
    @Mapping(target = "commercialElectricityUtilitySchedule", ignore = true)
    UtilitiesTownsAndVillageViewModel toViewModel(UtilitiesTownsAndVillage item);

    List<UtilitiesScheduleViewModel> toViewModel(Iterable<UtilitiesSchedule> schedules);

    @Mapping(target = "residentialElectricityUtilitySchedule", ignore = true)
    @Mapping(target = "commercialElectricityUtilitySchedule", ignore = true)
    UtilitiesScheduleViewModel toViewModel (UtilitiesSchedule schedule);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "utilitiesRangeBracket", ignore = true)
    @Mapping(target = "utilitiesWaterTownsAndVillage", ignore = true)
    @Mapping(target = "residentialElectricityUtilitySchedule", ignore = true)
    @Mapping(target = "commercialElectricityUtilitySchedule", ignore = true)
    UtilitiesSchedule toModel(UtilitiesScheduleViewModel dto);

    @Mapping(target = "schedule",  ignore = true)
    UtilitiesRangeBracket toModel(UtilitiesRangeBracketViewModel dto);

    UtilitiesScheduleCsvExportModel toCsvModel(UtilitiesSchedule item);

    List<UtilitiesScheduleCsvExportModel> toCsvModel(Iterable<UtilitiesSchedule> items);

    @AfterMapping
    default void resolveCsvExportModel(final UtilitiesSchedule source,
                                       @MappingTarget final UtilitiesScheduleCsvExportModel target) {
        String scheduleType = null;
        if (source.getScheduleType().equals(ScheduleType.WATER)){
            scheduleType = "Water";
        } else if (source.getScheduleType().equals(ScheduleType.ELECTRIC_COMM)) {
            scheduleType = "Commercial Electrical";
        } else if (source.getScheduleType().equals(ScheduleType.ELECTRIC_RES)) {
            scheduleType = "Residential Electrical";
        }
        target.setScheduleType(scheduleType);
    }
}
