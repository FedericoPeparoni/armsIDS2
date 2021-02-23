package ca.ids.abms.modules.formulas.enroute;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ca.ids.abms.modules.flightmovements.category.FlightmovementCategoryMapper;

@Mapper(uses={FlightmovementCategoryMapper.class})
public interface EnrouteAirNavigationChargeFormulaMapper {

    List<EnrouteAirNavigationChargeFormulaViewModel> toViewModel(Iterable<EnrouteAirNavigationChargeFormula> enrouteAirNavigationChargeFormulas);

    EnrouteAirNavigationChargeFormulaViewModel toViewModel(EnrouteAirNavigationChargeFormula enrouteAirNavigationChargeFormula);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "enrouteChargeCategory", ignore = true)
    EnrouteAirNavigationChargeFormula toModel(EnrouteAirNavigationChargeFormulaViewModel dto);
}
