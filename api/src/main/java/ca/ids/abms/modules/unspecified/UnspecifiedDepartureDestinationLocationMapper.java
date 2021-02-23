package ca.ids.abms.modules.unspecified;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper
public interface UnspecifiedDepartureDestinationLocationMapper {

    List<UnspecifiedDepartureDestinationLocationViewModel> toViewModel(Iterable<UnspecifiedDepartureDestinationLocation> aircraftTypes);

    @Mapping(target = "aerodromeIdentifier", source = "aerodromeIdentifier.aerodromeName")
    UnspecifiedDepartureDestinationLocationViewModel toViewModel(UnspecifiedDepartureDestinationLocation unspecifiedDepartureDestinationLocation);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "aerodromeIdentifier", ignore = true)
    UnspecifiedDepartureDestinationLocation toModel(UnspecifiedDepartureDestinationLocationViewModel dto);

    @Mapping(target = "countryCode", source = "countryCode.countryCode")
    @Mapping(target = "aerodromeIdentifier", source = "aerodromeIdentifier.aerodromeName")
    @Mapping(target = "status", ignore = true)
    UnspecifiedDepartureDestinationLocationCsvExportModel toCsvModel(UnspecifiedDepartureDestinationLocation item);

    List<UnspecifiedDepartureDestinationLocationCsvExportModel> toCsvModel(Iterable<UnspecifiedDepartureDestinationLocation> items);

    @AfterMapping
    default void resolveStatus(final UnspecifiedDepartureDestinationLocation source,
                               @MappingTarget final UnspecifiedDepartureDestinationLocationCsvExportModel target) {
        target.setStatus(source.getStatus().toJsonValue());
    }
}
