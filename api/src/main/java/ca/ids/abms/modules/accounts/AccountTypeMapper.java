package ca.ids.abms.modules.accounts;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface AccountTypeMapper {

    AccountTypeViewModel toViewModel(AccountType accountType);

    List<AccountTypeViewModel> toViewModel(Iterable<AccountType> accountTypes);
}
