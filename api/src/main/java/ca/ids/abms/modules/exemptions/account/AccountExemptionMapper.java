package ca.ids.abms.modules.exemptions.account;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface AccountExemptionMapper {

    List<AccountExemptionViewModel> toViewModel(Iterable<AccountExemption> items);

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "accountName", source = "account.name")
    AccountExemptionViewModel toViewModel (AccountExemption item);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "account", ignore = true)
    AccountExemption toModel(AccountExemptionViewModel dto);

    @Mapping(target = "account", source = "account.name")
    AccountExemptionCsvExportModel toCsvModel(AccountExemption item);

    List<AccountExemptionCsvExportModel> toCsvModel(Iterable<AccountExemption> items);
}
