package ca.ids.abms.modules.accounts;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)

public abstract class AccountExternalChargeCategoryMapper {
    public abstract AccountExternalChargeCategoryViewModel toViewModel(AccountExternalChargeCategory accountExternalChargeCategory);

    public abstract AccountExternalChargeCategory toModel(AccountExternalChargeCategoryViewModel accountExternalChargeCategory);
}
