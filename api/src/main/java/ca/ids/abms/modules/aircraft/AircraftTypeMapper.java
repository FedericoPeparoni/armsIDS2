package ca.ids.abms.modules.aircraft;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ca.ids.abms.modules.accounts.Account;

@Mapper
public interface AircraftTypeMapper {

    List<AircraftTypeViewModel> toViewModel(Iterable<AircraftType> aircraftTypes);

    AircraftTypeViewModel toViewModel(AircraftType aircraftTypes);

    default Collection<String> mapAccounts(Collection<Account> accounts) {
        return accounts.stream().map(Account::getName).collect(Collectors.toList());
    }
    
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "aircraftRegistrations", ignore = true)
    AircraftType toModel(AircraftTypeViewModel dto);

    AircraftTypeCsvExportModel toCsvModel(AircraftType item);

    List<AircraftTypeCsvExportModel> toCsvModel(Iterable<AircraftType> items);

    Collection<AircraftTypeComboViewModel> toComboViewModel(Collection<AircraftType> items);

    AircraftTypeComboViewModel toComboViewModel(AircraftType item);
}
