package ca.ids.abms.modules.bankaccount;

import ca.ids.abms.modules.common.mappers.AbmsCrudMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
interface BankAccountMapper extends AbmsCrudMapper<BankAccount, BankAccountViewModel, BankAccountCsvExportModel> {

    List<BankAccountViewModel> toViewModel(List<BankAccount> bankAccounts);

    @Mapping(target = "currency", source = "currency.currencyCode")
    BankAccountCsvExportModel toCsvModel(BankAccount item);

    List<BankAccountCsvExportModel> toCsvModel(Iterable<BankAccount> items);
}
