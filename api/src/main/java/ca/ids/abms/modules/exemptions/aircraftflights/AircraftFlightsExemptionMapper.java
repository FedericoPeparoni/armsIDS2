package ca.ids.abms.modules.exemptions.aircraftflights;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface AircraftFlightsExemptionMapper {

    List<AircraftFlightsExemptionViewModel> toViewModel(Iterable<AircraftFlightsExemption> items);
    
    AircraftFlightsExemptionViewModel toViewModel(AircraftFlightsExemption item);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    AircraftFlightsExemption toModel(AircraftFlightsExemptionViewModel dto);

    AircraftFlightsExemptionCsvExportModel toCsvModel(AircraftFlightsExemption item);

    List<AircraftFlightsExemptionCsvExportModel> toCsvModel(Iterable<AircraftFlightsExemption> items);

}
