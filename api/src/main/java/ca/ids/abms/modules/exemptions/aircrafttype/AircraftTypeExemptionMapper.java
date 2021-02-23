package ca.ids.abms.modules.exemptions.aircrafttype;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface AircraftTypeExemptionMapper {

    List<AircraftTypeExemptionViewModel> toViewModel(Iterable<AircraftTypeExemption> items);

    @Mapping(target = "aircraftType", source = "aircraftType.aircraftType")
    AircraftTypeExemptionViewModel toViewModel(AircraftTypeExemption item);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "aircraftType", ignore = true)
    AircraftTypeExemption toModel(AircraftTypeExemptionViewModel dto);

    @Mapping(target = "aircraftType", source = "aircraftType.aircraftType")
    AircraftTypeExemptionCsvExportModel toCsvModel(AircraftTypeExemption item);

    List<AircraftTypeExemptionCsvExportModel> toCsvModel(Iterable<AircraftTypeExemption> items);

}
