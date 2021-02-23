package ca.ids.abms.modules.reports2.template;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface ReportTemplateMapper {

    List<ReportTemplateViewModel> toViewModel(Iterable<ReportTemplate> models);

    @Mapping(target = "documentContents", ignore = true)
    @Mapping(target = "documentMimeType", source = "mimeType")
    @Mapping(target = "documentFilename", source = "reportFilename")
    ReportTemplateViewModel toViewModel(ReportTemplate model);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "templateDocument", ignore = true)
    @Mapping(target = "mimeType", ignore = true)
    @Mapping(target = "reportFilename", ignore = true)
    ReportTemplate toModel(ReportTemplateViewModel dto);

    ReportTemplateCsvExportModel toCsvModel(ReportTemplate item);

    List<ReportTemplateCsvExportModel> toCsvModel(Iterable<ReportTemplate> items);

}
