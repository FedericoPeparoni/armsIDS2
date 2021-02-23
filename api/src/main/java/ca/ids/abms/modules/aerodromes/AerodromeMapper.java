package ca.ids.abms.modules.aerodromes;

import ca.ids.abms.modules.aerodromeservicetypes.AerodromeServiceTypeMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(uses = { AerodromeServiceTypeMapper.class })
public interface AerodromeMapper {

    List<AerodromeViewModel> toViewModel(Iterable<Aerodrome> aerodromes);

    AerodromeViewModel toViewModel(Aerodrome aerodrome);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "unspecifiedDepartureDestinationLocations", ignore = true)
    @Mapping(target = "aerodromeServiceOutage", ignore = true)
    Aerodrome toModel(AerodromeViewModel dto);

    @Mapping(target = "aerodromeCategory", source = "aerodromeCategory.categoryName")
    @Mapping(target = "billingCenter", source = "billingCenter.name")
    AerodromeCsvExportModel toCsvModel(Aerodrome item);

    List<AerodromeCsvExportModel> toCsvModel(Iterable<Aerodrome> items);

    @AfterMapping
    default void resolveCsvExportModel(final Aerodrome source,
                                       @MappingTarget final AerodromeCsvExportModel target) {
        target.setLatitude(source.getGeometry().getCoordinate().y);
        target.setLongitude(source.getGeometry().getCoordinate().x);
    }

    AerodromeComboViewModel toComboViewModel(Aerodrome aerodrome);

    List<AerodromeComboViewModel> toComboViewModel(Iterable<Aerodrome> aerodromes);
}
