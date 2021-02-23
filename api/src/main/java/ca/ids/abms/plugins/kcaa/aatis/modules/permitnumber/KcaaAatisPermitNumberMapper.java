package ca.ids.abms.plugins.kcaa.aatis.modules.permitnumber;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface KcaaAatisPermitNumberMapper {

    List<KcaaAatisPermitNumberViewModel> toViewModel(Iterable<KcaaAatisPermitNumber> transactions);

    KcaaAatisPermitNumberViewModel toViewModel(KcaaAatisPermitNumber transaction);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    KcaaAatisPermitNumber toModel(KcaaAatisPermitNumberViewModel dto);
}
