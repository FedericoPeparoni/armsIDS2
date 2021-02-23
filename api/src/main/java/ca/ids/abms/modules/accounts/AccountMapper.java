package ca.ids.abms.modules.accounts;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryNationality;
import ca.ids.abms.modules.users.UserMapper;

@Mapper(uses = { UserMapper.class })
public interface AccountMapper {

    List<AccountViewModel> toViewModel(Iterable<Account> accounts);

    @Mapping(target = "scRequestId", ignore = true)
    @Mapping(target = "scRequestType", ignore = true)
    @Mapping(target = "accountExternalChargeCategories", ignore = true)
    AccountViewModel toViewModel(Account account);

    Collection<AccountComboViewModel> toComboViewModel(Collection<Account> accounts);

    AccountComboViewModel toComboViewModel(Account account);

    Account toModel(AccountComboViewModel account);

    @Mapping(target = "flightSchedules", ignore = true)
    @Mapping(target = "aircraftRegistrations", ignore = true)
    @Mapping(target = "billingLedgers", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "listOfEventsAccountNotified", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "selfCarePortalApprovalRequest", ignore = true)
    Account toModel(AccountViewModel dto);

    List<AccountEventMapViewModel> toViewModel(List<AccountEventMap> listOfEventsAccountNotified);

    Collection<AccountEventMapViewModel> toViewModel(Collection<AccountEventMap> listOfEventsAccountNotified);

    @Mapping(target = "account", source = "account.id")
    @Mapping(target = "notificationEventType", source = "notificationEventType.id")
    AccountEventMapViewModel toViewModel(AccountEventMap listOfEventsAccountNotified);

    AccountCsvExportModel toCsvModel(Account accounts);

    List<AccountCsvExportModel> toCsvModel(Iterable<Account> accounts);

    AccountCsvExportModel toCsvModelFromViewModel (AccountViewModel accounts);

    List<AccountCsvExportModel> toCsvModelFromViewModel(Iterable<AccountViewModel> accounts);

    default String mapNationality(final FlightmovementCategoryNationality nationality) {
        return nationality != null ? nationality.toValue() : null;
    }

    default FlightmovementCategoryNationality mapNationality(final String nationality) {
        return nationality != null ? FlightmovementCategoryNationality.forValue(nationality) : null;
    }
}
