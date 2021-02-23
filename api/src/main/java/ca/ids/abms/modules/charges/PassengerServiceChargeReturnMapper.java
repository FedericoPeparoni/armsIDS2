package ca.ids.abms.modules.charges;

import static ca.ids.abms.modules.common.mappers.DateTimeMapperUtils.parseCustomLocalDateToLocalDateTime;
import static ca.ids.abms.modules.common.mappers.DateTimeMapperUtils.parseISODateTime;

import java.util.List;
import java.util.Locale;

import ca.ids.abms.modules.accounts.AccountMapper;
import ca.ids.abms.modules.common.services.EntityResolver;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import ca.ids.abms.modules.common.mappers.ABMSMapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(uses = {EntityResolver.class, AccountMapper.class})
public abstract class PassengerServiceChargeReturnMapper extends ABMSMapper {

    @Autowired
    private EntityResolver entityResolver;

    public abstract List<PassengerServiceChargeReturnViewModel> toViewModel(Iterable<PassengerServiceChargeReturn> items);

    @Mapping(target = "documentContents", ignore = true)
    public abstract PassengerServiceChargeReturnViewModel toViewModel (PassengerServiceChargeReturn item);

    @AfterMapping
    public void parseDateTime(final PassengerServiceChargeReturnViewModel source, @MappingTarget PassengerServiceChargeReturn result) {
        result.setDayOfFlight(parseISODateTime(source.getDayOfFlight()).toLocalDate().atStartOfDay());
    }

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "dayOfFlight", ignore = true)
    @Mapping(target = "documentContents", ignore = true)
    @Mapping(target = "documentMimeType", ignore = true)
    @Mapping(target = "documentFilename", ignore = true)
    public abstract PassengerServiceChargeReturn toModel(PassengerServiceChargeReturnViewModel dto);

    @Mapping(target = "account", source = "account.name")
    public abstract PassengerServiceChargeReturnCsvExportModel toCsvModel(PassengerServiceChargeReturn passengerServiceChargeReturn);

    public abstract List<PassengerServiceChargeReturnCsvExportModel> toCsvModel(Iterable<PassengerServiceChargeReturn> passengerServiceChargeReturn);

    @AfterMapping
    public void parseDateTimeFromCSV (final PassengerServiceChargeReturnCsvViewModel source, @MappingTarget PassengerServiceChargeReturn result) {
        result.setDayOfFlight(parseCustomLocalDateToLocalDateTime(source.getDayOfFlight(), "dd/MM/yyyy", Locale.ENGLISH));
    }

    @AfterMapping
    public void resolveAccount (final PassengerServiceChargeReturnCsvViewModel source, @MappingTarget PassengerServiceChargeReturn result) {
        if (source.getAccountName() != null) {
            result.setAccount(entityResolver.resolveAccountByAccountName( source.getAccountName() ));
        } else {
            result.setAccount(null);
        }
    }

    public abstract List<PassengerServiceChargeReturn> toModel(Iterable<PassengerServiceChargeReturnCsvViewModel> items);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "dayOfFlight", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "documentContents", ignore = true)
    @Mapping(target = "documentMimeType", ignore = true)
    @Mapping(target = "documentFilename", ignore = true)
    @Mapping(target = "createdBySelfCare", ignore = true)
    public abstract PassengerServiceChargeReturn toModel(PassengerServiceChargeReturnCsvViewModel dto);
}
