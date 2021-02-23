package ca.ids.abms.modules.utilities.towns;

import ca.ids.abms.modules.utilities.schedules.UtilitiesSchedule;
import ca.ids.abms.modules.utilities.schedules.UtilitiesScheduleViewModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper
public abstract class UtilitiesTownsAndVillageMapper {

    public static final String SCHEDULE_CHARGE = "Schedule: %s / Charge: %s";

    public abstract List<UtilitiesTownsAndVillageViewModel> toViewModel(Iterable <UtilitiesTownsAndVillage> items);

    public abstract UtilitiesTownsAndVillageViewModel toViewModel(UtilitiesTownsAndVillage item);

    @Mapping(target = "utilitiesRangeBracket", ignore = true)
    @Mapping(target = "utilitiesWaterTownsAndVillage", ignore = true)
    @Mapping(target = "residentialElectricityUtilitySchedule", ignore = true)
    @Mapping(target = "commercialElectricityUtilitySchedule", ignore = true)
    public abstract UtilitiesScheduleViewModel toViewModel (UtilitiesSchedule schedule);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "utilitiesRangeBracket", ignore = true)
    @Mapping(target = "utilitiesWaterTownsAndVillage", ignore = true)
    @Mapping(target = "residentialElectricityUtilitySchedule", ignore = true)
    @Mapping(target = "commercialElectricityUtilitySchedule", ignore = true)
    public abstract UtilitiesSchedule toModel(UtilitiesScheduleViewModel dto);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    public abstract UtilitiesTownsAndVillage toModel (UtilitiesTownsAndVillageViewModel dto);

    @Mapping(target = "waterUtilitySchedule", ignore = true)
    @Mapping(target = "commercialElectricitySchedule", ignore = true)
    @Mapping(target = "residentialElectricitySchedule", ignore = true)
    public abstract UtilitiesTownsAndVillageCsvExportModel toCsvModel(UtilitiesTownsAndVillage item);

    public abstract List<UtilitiesTownsAndVillageCsvExportModel> toCsvModel(Iterable<UtilitiesTownsAndVillage> items);

    @AfterMapping
    void setCsvExportModel(final UtilitiesTownsAndVillage source, @MappingTarget UtilitiesTownsAndVillageCsvExportModel result) {
        final String waterUtilitySchedule = String.format(SCHEDULE_CHARGE, source.getWaterUtilitySchedule().getScheduleId(),
            source.getWaterUtilitySchedule().getMinimumCharge());
        final String commercialElectricitySchedule = String.format(SCHEDULE_CHARGE, source.getCommercialElectricityUtilitySchedule().getScheduleId(),
            source.getCommercialElectricityUtilitySchedule().getMinimumCharge());
        final String residentialElectricitySchedule = String.format(SCHEDULE_CHARGE, source.getResidentialElectricityUtilitySchedule().getScheduleId(),
            source.getResidentialElectricityUtilitySchedule().getMinimumCharge());

        result.setWaterUtilitySchedule(waterUtilitySchedule);
        result.setCommercialElectricitySchedule(commercialElectricitySchedule);
        result.setResidentialElectricitySchedule(residentialElectricitySchedule);
    }
}
