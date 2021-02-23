package ca.ids.abms.modules.aerodromeservicetypes;

import ca.ids.abms.modules.aerodromes.AerodromeMapper;
import ca.ids.abms.modules.aerodromeserviceoutages.AerodromeServiceOutage;
import ca.ids.abms.modules.aerodromeserviceoutages.AerodromeServiceOutageComboViewModel;
import ca.ids.abms.modules.aerodromeserviceoutages.AerodromeServiceOutageMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = { AerodromeServiceOutageMapper.class, AerodromeMapper.class })
public interface AerodromeServiceTypeMapper {

    List<AerodromeServiceTypeViewModel> toViewModel(Iterable<AerodromeServiceType> aerodromeServiceTypes);

    AerodromeServiceTypeViewModel toViewModel(AerodromeServiceType aerodromeServiceTypes);

    @Mapping(target = "aerodromeServiceOutage", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    AerodromeServiceType toModel(AerodromeServiceTypeViewModel dto);

    @Mapping(target = "aerodrome", source = "id.aerodrome.aerodromeName")
    @Mapping(target = "aerodromeServiceType", source = "id.aerodromeServiceType.serviceName")
    @Mapping(target = "outages", source = "aerodromeServiceOutages")
    AerodromeServiceTypeMapCsvExportModel toCsvModel(AerodromeServiceTypeMap aerodromeServiceTypeMap);

    List<AerodromeServiceTypeMapCsvExportModel> toCsvModel(Iterable<AerodromeServiceTypeMap> aerodromeServiceTypeMap);

    @Mapping(target = "aerodrome", source = "aerodrome.aerodromeName")
    @Mapping(target = "aerodromeServiceType", source = "aerodromeServiceType.serviceName")
    AerodromeServiceTypeMapViewModel toMapViewModel(AerodromeServiceTypeMap aerodromeServiceTypeMap);

    List<AerodromeServiceTypeMapViewModel> toMapViewModel(Iterable<AerodromeServiceTypeMap> aerodromeServiceTypeMap);

    @Mapping(target = "aerodrome", source = "aerodrome.aerodromeName")
    @Mapping(target = "aerodromeServiceType", source = "aerodromeServiceType.serviceName")
    AerodromeServiceOutageComboViewModel toComboModel(AerodromeServiceOutage aerodromeServiceOutage);

    List<AerodromeServiceOutageComboViewModel> toComboModel(Iterable<AerodromeServiceOutage> aerodromeServiceOutages);
}
