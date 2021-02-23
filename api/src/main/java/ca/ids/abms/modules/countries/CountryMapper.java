package ca.ids.abms.modules.countries;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface CountryMapper {
    List<CountryViewModel> toViewModel(Iterable<Country> countries);

    @Mapping(target = "aircraftRegistrations", ignore = true)
    @Mapping(target = "currencies", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Country toModel(CountryViewModel dto);

    @Mapping(target = "pattern", ignore = true)
    CountryViewModel toViewModel(Country country);

    @Mapping(target = "aircraftRegistrationPrefixes", ignore = true)
    @Mapping(target = "aerodromePrefixes", ignore = true)
    CountryCsvExportModel toCsvModel(Country item);

    List<CountryCsvExportModel> toCsvModel(Iterable<Country> items);

    @AfterMapping
    default void resolveAircraftRegistrationPrefixes(final Country source, @MappingTarget CountryCsvExportModel target) {
        List<String> list = new ArrayList<>();
        source.getAircraftRegistrationPrefixes().forEach(a -> list.add(a.getAircraftRegistrationPrefix()));
        target.setAircraftRegistrationPrefixes(list.toString().replaceAll("[\\[\\]]", ""));
    }

    @AfterMapping
    default void resolveAerodromePrefixes(final Country source, @MappingTarget CountryCsvExportModel target) {
        List<String> list = new ArrayList<>();
        source.getAerodromePrefixes().forEach(a -> list.add(a.getAerodromePrefix()));
        target.setAerodromePrefixes(list.toString().replaceAll("[\\[\\]]", ""));
    }
}
