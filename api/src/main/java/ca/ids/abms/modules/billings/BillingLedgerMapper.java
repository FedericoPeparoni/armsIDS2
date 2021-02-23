package ca.ids.abms.modules.billings;

import ca.ids.abms.modules.accounts.AccountMapper;
import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.billingcenters.BillingCenterMapper;
import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.common.enumerators.InvoiceType;
import ca.ids.abms.modules.flight.FlightSchedule;
import ca.ids.abms.modules.transactions.Transaction;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(uses = { AccountMapper.class, BillingCenterMapper.class })
public interface BillingLedgerMapper {

    List<BillingLedgerViewModel> toViewModel(Iterable<BillingLedger> billingLedgers);

    @Mapping(target = "documentContents", ignore = true)
    @Mapping(target = "documentMimeType", source = "invoiceDocumentType")
    @Mapping(target = "documentFilename", source = "id")
    @Mapping(target = "user", source = "user.name")
    BillingLedgerViewModel toViewModel(BillingLedger billingLedger);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "invoiceDocument", ignore = true)
    @Mapping(target = "invoiceDocumentType", ignore = true)
    @Mapping(target = "invoiceFileName", ignore = true)
    @Mapping(target = "invoiceLineItems", ignore = true)
    @Mapping(target = "chargesAdjustment", ignore = true)
    @Mapping(target = "kcaaAatisPermitNumbers", ignore = true)
    @Mapping(target = "kcaaEaipRequisitionNumbers", ignore = true)
    @Mapping(target = "enrouteFlightMovements", ignore = true)
    @Mapping(target = "passengerFlightMovements", ignore = true)
    @Mapping(target = "otherFlightMovements", ignore = true)
    @Mapping(target = "user", ignore = true)
    BillingLedger toModel(BillingLedgerViewModel dto);

    @Mapping(target = "account", source = "account.name")
    @Mapping(target = "invoiceCurrency", source = "invoiceCurrency.currencyCode")
    @Mapping(target = "user", source = "user.name")
    @Mapping(target = "billingCentre", source = "billingCenter.name")
    BillingLedgerCsvExportModel toCsvModel(BillingLedger billingLedger);

    List<BillingLedgerCsvExportModel> toCsvModel(Iterable<BillingLedger> billingLedgers);

    default Collection<String> mapTransactions(Collection<Transaction> transactions) {
        return transactions.stream().map(Transaction::getDescription).collect(Collectors.toList());
    }

    default Collection<String> mapAircraftRegistrations(Collection<AircraftRegistration> aircraftRegistrations) {
        return aircraftRegistrations.stream().map(AircraftRegistration::getRegistrationNumber).collect(Collectors.toList());
    }

    default Collection<String> mapFlightSchedules(Collection<FlightSchedule> flightSchedules) {
        return flightSchedules.stream().map(FlightSchedule::getFlightServiceNumber).collect(Collectors.toList());
    }

    default InvoiceType mapInvoiceType(final String invoiceType) {
        return InvoiceType.forValue(invoiceType);
    }

    default String mapInvoiceType(final InvoiceType invoiceType) {
        return invoiceType != null ? invoiceType.toValue() : null;
    }

    default InvoiceStateType mapInvoiceState(final String invoiceState) {
        return InvoiceStateType.forValue(invoiceState);
    }

    default String mapInvoiceState(final InvoiceStateType invoiceStateType) {
        return invoiceStateType != null ? invoiceStateType.toValue() : null;
    }

	default Collection<Integer> mapBillingLedgers(Collection<BillingLedger> billingLedgers) {
	    return billingLedgers.stream().map(BillingLedger::getId).collect(Collectors.toList());
	}
	
}
