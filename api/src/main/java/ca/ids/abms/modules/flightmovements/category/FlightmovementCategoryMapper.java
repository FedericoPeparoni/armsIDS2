package ca.ids.abms.modules.flightmovements.category;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper
public interface FlightmovementCategoryMapper {

    List<FlightmovementCategoryViewModel> toViewModel(Iterable<FlightmovementCategory> flightmovementCategories);

    FlightmovementCategoryViewModel toViewModel(FlightmovementCategory flightmovementCategory);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "enrouteResultCurrency", ignore = true)
    @Mapping(target = "enrouteInvoiceCurrency", ignore = true)
    @Mapping(target = "flightmovementCategoryAttributes", ignore = true)
    FlightmovementCategory toModel(FlightmovementCategoryViewModel dto);
}
