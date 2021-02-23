package ca.ids.abms.modules.formulas.navigation;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface NavigationBillingFormulaMapper {
    
    List<NavigationBillingFormulaViewModel> toViewModel(Iterable<NavigationBillingFormula> formula);
    
    NavigationBillingFormulaViewModel toViewModel (NavigationBillingFormula formula);
    
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    NavigationBillingFormula toModel(NavigationBillingFormulaViewModel dto);

}
