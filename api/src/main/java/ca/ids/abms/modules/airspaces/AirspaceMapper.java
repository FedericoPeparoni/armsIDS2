package ca.ids.abms.modules.airspaces;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AirspaceMapper {

    List<AirspaceViewModel> toViewModel(Iterable<Airspace> items);
    
    AirspaceViewModel toViewModel(Airspace item);
    
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "airspaceBoundary", ignore = true)
    Airspace toModel(AirspaceViewModel dto);

    AirspaceCsvExportModel toCsvModel(Airspace item);

    List<AirspaceCsvExportModel> toCsvModel(Iterable<Airspace> items);

}
