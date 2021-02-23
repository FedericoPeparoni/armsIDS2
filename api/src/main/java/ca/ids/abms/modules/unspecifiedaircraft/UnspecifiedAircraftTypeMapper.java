package ca.ids.abms.modules.unspecifiedaircraft;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface UnspecifiedAircraftTypeMapper {

    List<UnspecifiedAircraftTypeViewModel> toViewModel(Iterable<UnspecifiedAircraftType> aircraftTypes);

    UnspecifiedAircraftTypeViewModel toViewModel(UnspecifiedAircraftType unspecifiedAircraftType);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    UnspecifiedAircraftType toModel(UnspecifiedAircraftTypeViewModel dto);

    @Mapping(target = "mtow", source = "MTOW")
    UnspecifiedAircraftTypeCsvExportModel toCsvModel(UnspecifiedAircraftType item);

    List<UnspecifiedAircraftTypeCsvExportModel> toCsvModel(Iterable<UnspecifiedAircraftType> items);
}
