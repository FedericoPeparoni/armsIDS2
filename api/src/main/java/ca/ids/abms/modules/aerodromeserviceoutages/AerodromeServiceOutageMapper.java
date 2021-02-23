package ca.ids.abms.modules.aerodromeserviceoutages;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface AerodromeServiceOutageMapper {

    List<AerodromeServiceOutageViewModel> toViewModel(Iterable<AerodromeServiceOutage> aerodromeServiceOutage);

    @Mapping(target = "aerodrome", source = "aerodrome.aerodromeName")
    @Mapping(target = "aerodromeServiceType", source = "aerodromeServiceType.serviceName")
    AerodromeServiceOutageViewModel toViewModel(AerodromeServiceOutage aerodromeServiceOutage);

    @Mapping(target = "aerodrome", ignore = true)
    @Mapping(target = "aerodromeServiceType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    AerodromeServiceOutage toModel(AerodromeServiceOutageViewModel dto);

    AerodromeServiceOutageCsvExportModel toCsvModel(AerodromeServiceOutage aerodromeServiceOutage);

    List<AerodromeServiceOutageCsvExportModel> toCsvModel(Iterable<AerodromeServiceOutage> aerodromeServiceOutages);
}
