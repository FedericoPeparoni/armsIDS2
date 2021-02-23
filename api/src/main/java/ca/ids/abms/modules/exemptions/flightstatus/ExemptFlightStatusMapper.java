package ca.ids.abms.modules.exemptions.flightstatus;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface ExemptFlightStatusMapper {

    List<ExemptFlightStatusViewModel> toViewModel(Iterable<ExemptFlightStatus> items);

    ExemptFlightStatusViewModel toViewModel(ExemptFlightStatus item);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    ExemptFlightStatus toModel(ExemptFlightStatusViewModel dto);

    ExemptFlightStatusCsvExportModel toCsvModel(ExemptFlightStatus item);

    List<ExemptFlightStatusCsvExportModel> toCsvModel(Iterable<ExemptFlightStatus> items);
}
