package ca.ids.abms.modules.aerodromes;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ca.ids.abms.modules.formulas.ldp.LdpBillingFormula;
import ca.ids.abms.modules.formulas.ldp.LdpBillingFormulaViewModel;

@Mapper
public interface AerodromeCategoryMapper {

    default Collection<String> mapAerodromes(Collection<Aerodrome> aerodromes) {
        return aerodromes.stream().map(Aerodrome::getAerodromeName).collect(Collectors.toList());
    }

    Set<LdpBillingFormulaViewModel> toViewModel(Set<LdpBillingFormula> ldpBillingFormulas);

    @Mapping(target = "documentContents", ignore = true)
    @Mapping(target = "documentMimeType", source = "spreadsheetContentType")
    @Mapping(target = "documentFilename", source = "spreadsheetFileName")
    LdpBillingFormulaViewModel toViewModel(LdpBillingFormula ldpBillingFormulas);

    List<AerodromeCategoryViewModel> toViewModel(Iterable<AerodromeCategory> aerodromeCategories);

    AerodromeCategoryViewModel toViewModel(AerodromeCategory aerodromeCategory);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "aerodromes", ignore = true)
    @Mapping(target = "ldpBillingFormulas", ignore = true)
    AerodromeCategory toModel(AerodromeCategoryViewModel dto);

    default java.lang.Integer map(ca.ids.abms.modules.formulas.ldp.LdpBillingFormulaKey value) {
        return value.getAerodromeCategoryId();
    }

    AerodromeCategoryCsvExportModel toCsvModel(AerodromeCategory item);

    List<AerodromeCategoryCsvExportModel> toCsvModel(Iterable<AerodromeCategory> items);
}
