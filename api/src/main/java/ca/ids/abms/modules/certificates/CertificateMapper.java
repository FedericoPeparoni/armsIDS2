package ca.ids.abms.modules.certificates;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Created by c.talpa on 14/12/2016.
 */
@Mapper
public interface CertificateMapper {

    List<CertificateViewModel> toViewModel(Iterable<Certificate> models);

    CertificateViewModel toViewModel(Certificate model);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "certificateImage", ignore = true)
    @Mapping(target = "certificateImageType", ignore = true)
    Certificate toModel(CertificateViewModel dto);
}
