package ca.ids.abms.plugins.kcaa.eaip.modules.requisitionnumbers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface KcaaEaipRequisitionNumberMapper {

    List<KcaaEaipRequisitionNumberViewModel> toViewModel(Iterable<KcaaEaipRequisitionNumber> transactions);

    KcaaEaipRequisitionNumberViewModel toViewModel(KcaaEaipRequisitionNumber transaction);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    KcaaEaipRequisitionNumber toModel(KcaaEaipRequisitionNumberViewModel dto);
}
