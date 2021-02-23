package ca.ids.abms.modules.formulas.enroute;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ca.ids.abms.modules.flightmovements.category.FlightmovementCategoryMapper;
import org.mapstruct.MappingTarget;


@Mapper(uses={FlightmovementCategoryMapper.class, EnrouteAirNavigationChargeFormulaMapper.class})
public interface EnrouteAirNavigationChargeCategoryMapper {
    
    List<EnrouteAirNavigationChargeCategoryViewModel> toViewModel(Iterable<EnrouteAirNavigationChargeCategory> enrouteAirNavigationChargeCategories);

    EnrouteAirNavigationChargeCategoryViewModel toViewModel(EnrouteAirNavigationChargeCategory enrouteAirNavigationChargeCategory);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    EnrouteAirNavigationChargeCategory toModel(EnrouteAirNavigationChargeCategoryViewModel dto);

    @Mapping(target = "enrouteAirNavigationChargeFormulas", ignore = true)
    EnrouteAirNavigationChargeCategoryCsvExportModel toCsvModel(EnrouteAirNavigationChargeCategory item);

    List<EnrouteAirNavigationChargeCategoryCsvExportModel> toCsvModel(Iterable<EnrouteAirNavigationChargeCategory> items);

    @AfterMapping
    default void resolveCsvExportModel(final EnrouteAirNavigationChargeCategory source,
                                       @MappingTarget final EnrouteAirNavigationChargeCategoryCsvExportModel target) {
        target.setMtowCategoryUpperLimit(source.getMtowCategoryUpperLimit());
        target.setwFactorFormula(source.getwFactorFormula());

        List<EnrouteAirNavigationChargeFormulas> list = new ArrayList<>();
        source.getEnrouteAirNavigationChargeFormulas().forEach(f -> {
            if (!f.getFlightmovementCategory().getName().equals("OTHER")) {
                EnrouteAirNavigationChargeFormulas formula = new EnrouteAirNavigationChargeFormulas();
                formula.flightCategory = WordUtils.capitalize(f.getFlightmovementCategory().getName().toLowerCase());
                formula.formula = f.getFormula();
                formula.dFormula = f.getdFactorFormula();
                list.add(formula);
            }
        });
        target.setEnrouteAirNavigationChargeFormulas(list);
    }

    class EnrouteAirNavigationChargeFormulas {
        String flightCategory;
        String formula;
        String dFormula;
    }
}
