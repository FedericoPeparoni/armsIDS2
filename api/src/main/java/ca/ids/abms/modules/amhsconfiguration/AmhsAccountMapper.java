package ca.ids.abms.modules.amhsconfiguration;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)

public abstract class AmhsAccountMapper {
    public abstract AmhsAccountViewModel toViewModel(AmhsAccount amhsAccount);

    public abstract AmhsAccount toModel(AmhsAccountViewModel amhsAccount);
}
