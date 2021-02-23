package ca.ids.abms.modules.formulas.ldp;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.BreakIterator;
import org.apache.commons.lang.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper
public interface LdpBillingFormulaMapper {

    List<LdpBillingFormulaViewModel> toViewModel(Iterable<LdpBillingFormula> formula);

    @Mapping(target = "documentContents", ignore = true)
    @Mapping(target = "documentMimeType", source = "spreadsheetContentType")
    @Mapping(target = "documentFilename", source = "spreadsheetFileName")
    @Mapping(target = "id", source = "aerodromeCategoryId")
    LdpBillingFormulaViewModel toViewModel(LdpBillingFormula formula);

    default java.lang.Integer map(ca.ids.abms.modules.formulas.ldp.LdpBillingFormulaKey value) {
        return value.getAerodromeCategoryId();
    }

    @Mapping(target = "aerodromeCategory", source = "aerodromeCategory.categoryName")
    LdpBillingFormulaCsvExportModel toCsvModel(LdpBillingFormula item);

    List<LdpBillingFormulaCsvExportModel> toCsvModel(Iterable<LdpBillingFormula> items);

    @AfterMapping
    default void resolveCsvExportModel(final LdpBillingFormula source,
                                       @MappingTarget final LdpBillingFormulaCsvExportModel target) {

        String chargesType = UCharacter.toTitleCase(StringUtils.capitalize(source.getChargesType().name().
            replaceAll("_", " ")), BreakIterator.getTitleInstance());
        target.setChargesType(chargesType);
    }
}
