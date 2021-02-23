package ca.ids.abms.modules.billings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import ca.ids.abms.modules.aerodromes.AerodromeMapper;
import ca.ids.abms.modules.charges.RecurringChargeMapper;
import ca.ids.abms.modules.charges.ServiceChargeCatalogueMapper;
import ca.ids.abms.modules.utilities.towns.UtilitiesTownsAndVillageMapper;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {
                AerodromeMapper.class,
                ServiceChargeCatalogueMapper.class,
                RecurringChargeMapper.class,
                UtilitiesTownsAndVillageMapper.class
        }
)
public interface InvoiceLineItemMapper {

    InvoiceLineItemViewModel toViewModel (InvoiceLineItem entity);
    
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    InvoiceLineItem toModel (InvoiceLineItemViewModel viewModel);

    @Mapping(target = "category", source = "serviceChargeCatalogue.category")
    @Mapping(target = "chargeClass", source = "serviceChargeCatalogue.chargeClass")
    @Mapping(target = "description", source = "serviceChargeCatalogue.description")
    InvoiceLineItemCsvExportModel toCsvModel(InvoiceLineItem invoiceLineItem);

    List<InvoiceLineItemCsvExportModel> toCsvModel(Iterable<InvoiceLineItem> invoiceLineItem);
}
