package ca.ids.abms.modules.countries;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.List;

@Mapper
public interface RegionalCountryMapper {

    List<RegionalCountryViewModel> toViewModel(Iterable<RegionalCountry> items);

    RegionalCountryViewModel toViewModel(RegionalCountry item);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    RegionalCountry toModel(RegionalCountryViewModel dto);

    Collection<RegionalCountry> toModel(Collection<RegionalCountryViewModel> items);

    @Mapping(target = "countryName", source = "country.countryName")
    @Mapping(target = "countryCode", source = "country.countryCode")
    RegionalCountryCsvExportModel toCsvModel(RegionalCountry item);

    List<RegionalCountryCsvExportModel> toCsvModel(Iterable<RegionalCountry> items);

}
