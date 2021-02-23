package ca.ids.abms.modules.aircraft;

import static ca.ids.abms.modules.common.mappers.DateTimeMapperUtils.normalizeDate;
import static ca.ids.abms.modules.common.mappers.DateTimeMapperUtils.parseCustomLocalDateToLocalDateTime;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

import ca.ids.abms.modules.accounts.AccountMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.common.mappers.ABMSMapper;
import ca.ids.abms.modules.common.mappers.AccountResolver;
import ca.ids.abms.modules.common.mappers.AircraftTypeResolver;
import ca.ids.abms.modules.common.mappers.CountryResolver;
import ca.ids.abms.modules.common.services.EntityResolver;
import ca.ids.abms.util.converter.JSR310DateConverters;

@Mapper(uses = {EntityResolver.class, AccountMapper.class})
public abstract class AircraftRegistrationMapper extends ABMSMapper {

    public abstract List<AircraftRegistrationViewModel> toViewModel(Iterable<AircraftRegistration> aircraftRegistrations);

    @Mapping(target = "scRequestId", ignore = true)
    @Mapping(target = "scRequestType", ignore = true)
    public abstract AircraftRegistrationViewModel toViewModel(AircraftRegistration aircraftRegistrations);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    public abstract AircraftRegistration toModel(AircraftRegistrationViewModel dto);

    @AfterMapping
    public void parseAircraftRegistrationFromCSV(final AircraftRegistrationCsvViewModel source,
            @MappingTarget AircraftRegistration result) {
        try {
            final LocalDateTime startDate = parseCustomLocalDateToLocalDateTime(normalizeDate(source.getStartDate()),
                    JSR310DateConverters.DEFAULT_PATTERN_DATE, Locale.ENGLISH);
            result.setRegistrationStartDate(startDate);
        } catch (IllegalArgumentException iae) {
            throw new CustomParametrizedException(iae.getLocalizedMessage(), source.getStartDate(),
                    AircraftRegistrationMapper.class, "startDate");
        }
        // TODO: date comparison shouldn't rely on time
        // https://git.idscorporation.ca/abms/api/issues/10
        try {
            final LocalDateTime expiryDate = parseCustomLocalDateToLocalDateTime(normalizeDate(source.getExpiryDate()),
                    JSR310DateConverters.DEFAULT_PATTERN_DATE, Locale.ENGLISH);
            result.setRegistrationExpiryDate(expiryDate.with(LocalTime.MAX));
        } catch (IllegalArgumentException iae) {
            throw new CustomParametrizedException(iae.getLocalizedMessage(), source.getExpiryDate(),
                    AircraftRegistrationMapper.class, "expiryDate");
        }
        try {
            Double mtowOverride = Double.valueOf(source.getMtowOverride());
            result.setMtowOverride(mtowOverride);
        } catch (Exception e) {
            throw new CustomParametrizedException("Input value for MTOW is not correct", source.getMtowOverride(),
                    AircraftRegistrationMapper.class, "mtowOverride");
        }
    }

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "registrationStartDate", ignore = true)
    @Mapping(target = "registrationExpiryDate", ignore = true)
    @Mapping(target = "countryOverride", ignore = true)
    @Mapping(target = "mtowOverride", ignore = true)
    @Mapping(source = "registrationNumber", target = "countryOfRegistration", qualifiedBy = { CountryResolver.class })
    @Mapping(source = "accountName", target = "account", qualifiedBy = { AccountResolver.class })
    @Mapping(source = "aircraftType", target = "aircraftType", qualifiedBy = { AircraftTypeResolver.class })
    @Mapping(target = "createdBySelfCare", ignore = true)
    @Mapping(target = "isLocal", ignore = true)
    @Mapping(target = "aircraftServiceDate", ignore = true)
    @Mapping(target = "coaExpiryDate", ignore = true)
    @Mapping(target = "coaIssueDate", ignore = true)
    public abstract AircraftRegistration toModel(AircraftRegistrationCsvViewModel dto);

    /* CSV file to backend mapping */
    public abstract List<AircraftRegistration> toModel(Iterable<AircraftRegistrationCsvViewModel> items);

    @Mapping(target = "account", source = "account.name")
    @Mapping(target = "aircraftType", source = "aircraftType.aircraftType")
    public abstract AircraftRegistrationCsvExportModel toCsvModel(AircraftRegistration item);

    public abstract List<AircraftRegistrationCsvExportModel> toCsvModel(Iterable<AircraftRegistration> items);

    @Mapping(target = "account", source = "account.name")
    @Mapping(target = "aircraftType", source = "aircraftType.aircraftType")
    public abstract AircraftRegistrationCsvExportModel toCsvModelFromViewModel(AircraftRegistrationViewModel item);

    public abstract List<AircraftRegistrationCsvExportModel> toCsvModelFromViewModel(Iterable<AircraftRegistrationViewModel> items);


}
