package ca.ids.abms.modules.route;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface NominalRouteMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    NominalRoute toModel(NominalRouteViewModel dto);

    List<NominalRouteViewModel> toViewModel(Iterable<NominalRoute> items);

    NominalRouteViewModel toViewModel(NominalRoute item);

    NominalRouteCsvExportModel toCsvModel(NominalRoute item);

    List<NominalRouteCsvExportModel> toCsvModel(Iterable<NominalRoute> items);

}
