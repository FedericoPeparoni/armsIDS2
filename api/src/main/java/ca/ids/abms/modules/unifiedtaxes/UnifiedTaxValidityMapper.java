package ca.ids.abms.modules.unifiedtaxes;

import java.util.List;

import org.mapstruct.Mapper;

import ca.ids.abms.modules.common.mappers.AbmsCrudMapper;

@Mapper
interface UnifiedTaxValidityMapper
        extends AbmsCrudMapper<UnifiedTaxValidity, UnifiedTaxValidityViewModel, UnifiedTaxValidityCsvExportModel> {
    
    List<UnifiedTaxValidityViewModel> toViewModel(List<UnifiedTaxValidity> unifiedTaxValidity);

}
