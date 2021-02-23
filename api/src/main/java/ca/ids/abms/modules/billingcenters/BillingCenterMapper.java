package ca.ids.abms.modules.billingcenters;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.users.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface BillingCenterMapper {

    default Collection<String> mapAerodromes(Collection<Aerodrome> aerodromes) {
        return aerodromes.stream().map(Aerodrome::getAerodromeName).collect(Collectors.toList());
    }

    default Collection<String> mapUsers(Collection<User> users) {
        return users.stream().map(User::getName).collect(Collectors.toList());
    }

    List<BillingCenterViewModel> toViewModel(Iterable<BillingCenter> billingCenters);

    BillingCenterViewModel toViewModel(BillingCenter billingCenter);

    @Mapping(target = "aerodromes", ignore = true)
    @Mapping(target = "billingLedgers", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    BillingCenter toModel(BillingCenterViewModel dto);

    BillingCenterCsvExportModel toCsvModel(BillingCenter billingCenter);

    List<BillingCenterCsvExportModel> toCsvModel(Iterable<BillingCenter> billingCenters);

    Collection<BillingCenterComboViewModel> toComboViewModel(Collection<BillingCenter> billingCenters);

    BillingCenterComboViewModel toComboViewModel(BillingCenter billingCenter);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "aerodromes", ignore = true)
    @Mapping(target = "invoiceSequenceNumber", ignore = true)
    @Mapping(target = "prefixInvoiceNumber", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "billingLedgers", ignore = true)
    @Mapping(target = "hq", ignore = true)
    @Mapping(target = "prefixReceiptNumber", ignore = true)
    @Mapping(target = "receiptSequenceNumber", ignore = true)
    @Mapping(target = "externalAccountingSystemIdentifier", ignore = true)
    BillingCenter toModel(BillingCenterComboViewModel dto);
}
