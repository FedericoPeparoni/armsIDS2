package ca.ids.abms.modules.unifiedtaxes;

import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ca.ids.abms.modules.common.mappers.AbmsCrudMapper;

import javax.inject.Named;


@Mapper
interface UnifiedTaxMapper extends AbmsCrudMapper<UnifiedTax, UnifiedTaxViewModel, UnifiedTaxCsvExportModel> {

//    @Mapping(target = "validityId", source = "validity.id")
//    @Mapping(target = "fromValidityYear", source = "validity.fromValidityYear")
//    @Mapping(target = "toValidityYear", source = "validity.toValidityYear")
    @Mapping(source = "chargeFormula", target = "rate")
    @Named("mapToUnifiedTaxViewModelViewModel")
    UnifiedTaxViewModel toViewModel(UnifiedTax unifiedTax);
//
//    @Mapping(source = "validityId", target = "validity.id")
//    @Mapping(source = "fromValidityYear", target = "validity.fromValidityYear")
//    @Mapping(source = "toValidityYear", target = "validity.toValidityYear")
//    UnifiedTax toModel(UnifiedTaxViewModel unifiedTaxViewModel);
//
    @IterableMapping(qualifiedBy = Mapper.class)
    List<UnifiedTaxViewModel> toViewModel(List<UnifiedTax> unifiedTaxList);

}
