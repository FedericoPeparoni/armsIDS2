package ca.ids.abms.modules.invoices;

import ca.ids.abms.modules.common.mappers.ABMSMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public abstract class InvoiceTemplateMapper extends ABMSMapper{

    abstract List<InvoiceTemplateViewModel> toViewModel(Iterable<InvoiceTemplate> items);

    @Mapping(target = "documentContents", ignore = true)
    @Mapping(target = "documentMimeType", source = "mimeType")
    @Mapping(target = "documentFilename", source = "invoiceFilename")
    abstract InvoiceTemplateViewModel toViewModel(InvoiceTemplate item);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "templateDocument", source = "documentContents")
    @Mapping(target = "mimeType", source = "documentMimeType")
    @Mapping(target = "invoiceFilename", source = "documentFilename")
    abstract InvoiceTemplate toModel(InvoiceTemplateViewModel dto);

    abstract InvoiceTemplateCsvExportModel toCsvModel(InvoiceTemplateViewModel item);

    abstract List<InvoiceTemplateCsvExportModel> toCsvModel(Iterable<InvoiceTemplateViewModel> items);

}
