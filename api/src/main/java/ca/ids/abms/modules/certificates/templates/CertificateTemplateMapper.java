package ca.ids.abms.modules.certificates.templates;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface CertificateTemplateMapper {

    List<CertificateTemplateViewModel> toViewModel(Iterable<CertificateTemplate> models);

    @Mapping(target = "documentContents", ignore = true)
    @Mapping(target = "documentMimeType", source = "templateDocumentType")
    @Mapping(target = "documentFilename", source = "certificateTemplateName")
    CertificateTemplateViewModel toViewModel(CertificateTemplate model);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "templateDocument", ignore = true)
    @Mapping(target = "templateDocumentType", ignore = true)
    CertificateTemplate toModel(CertificateTemplateViewModel dto);

}
