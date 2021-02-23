package ca.ids.abms.modules.exemptions.flightroutes;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface ExemptFlightRouteMapper {

    List<ExemptFlightRouteViewModel> toViewModel(Iterable<ExemptFlightRoute> items);

    ExemptFlightRouteViewModel toViewModel(ExemptFlightRoute item);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    ExemptFlightRoute toModel(ExemptFlightRouteViewModel dto);

    ExemptFlightRouteCsvExportModel toCsvModel(ExemptFlightRoute item);

    List<ExemptFlightRouteCsvExportModel> toCsvModel(Iterable<ExemptFlightRoute> items);

}
