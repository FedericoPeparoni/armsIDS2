package ca.ids.abms.modules.accounts;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.List;

@Mapper
public interface AccountEventMapMapper {

    Collection<AccountEventMapViewModel> toViewModel(Collection<AccountEventMap> events);

    List<AccountEventMapViewModel> toViewModel(Iterable<AccountEventMap> items);

    @Mapping(target = "account", source = "account.id")
    @Mapping(target = "notificationEventType", source = "notificationEventType.id")
    AccountEventMapViewModel toViewModel(AccountEventMap event);
}
