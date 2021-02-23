package ca.ids.abms.modules.localaircraftregistry;

import ca.ids.abms.modules.common.mappers.ABMSMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

import static ca.ids.abms.modules.common.mappers.DateTimeMapperUtils.parseCustomLocalDateToLocalDateTime;

@Mapper
public abstract class LocalAircraftRegistryMapper extends ABMSMapper {

    private static final Logger LOG = LoggerFactory.getLogger(LocalAircraftRegistryMapper.class);

    /* Backend to Frontend mapping */
    public abstract List<LocalAircraftRegistryViewModel> toViewModel(Iterable<LocalAircraftRegistry> localAircraftRegistries);
    public abstract LocalAircraftRegistryViewModel toViewModel(LocalAircraftRegistry localAircraftRegistries);

    /* Frontend to backend mapping */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    public abstract LocalAircraftRegistry toModel(LocalAircraftRegistryViewModel dto);

    /* CSV file to backend mapping */
    public abstract List<LocalAircraftRegistry> toModel(Iterable<LocalAircraftRegistryCsvViewModel> items);
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "ownerName", ignore = true)
    @Mapping(target = "mtowWeight", ignore = true)
    @Mapping(target = "coaDateOfRenewal", ignore = true)
    @Mapping(target = "coaDateOfExpiry", ignore = true)
    @Mapping(target = "version", ignore = true)
    public abstract LocalAircraftRegistry toModel(LocalAircraftRegistryCsvViewModel dto);

    public abstract LocalAircraftRegistryCsvExportModel toCsvModel(LocalAircraftRegistry item);

    public abstract List<LocalAircraftRegistryCsvExportModel> toCsvModel(Iterable<LocalAircraftRegistry> items);

    @AfterMapping
    public void parseOwnerNameCSV (final LocalAircraftRegistryCsvViewModel source, @MappingTarget LocalAircraftRegistry result) {
        String string = source.getOwnerName().trim().replaceAll(" +", " ");
        if(string.length() > 300) {
            string =  string.substring(0, 300);
        }
        result.setOwnerName(string);
    }

    @AfterMapping
    public void parseMtowWeightCSV (final LocalAircraftRegistryCsvViewModel source, @MappingTarget LocalAircraftRegistry result) {
        NumberFormat format = NumberFormat.getInstance();
        Number number = null;
        try {
            number = format.parse(source.getMtowWeight());
        } catch (ParseException e) {
            LOG.debug(e.getLocalizedMessage());
        }

        if (number != null) {
            double weight = number.doubleValue();
            result.setMtowWeight(weight / 907.185);
        }
    }

    @AfterMapping
    public void parseDateOfRenewalCSV (final LocalAircraftRegistryCsvViewModel source, @MappingTarget LocalAircraftRegistry result) {
        result.setCoaDateOfRenewal(parseCustomLocalDateToLocalDateTime(source.getCoaDateOfRenewal(), "dd-MM-yyyy", Locale.ENGLISH));
    }

    // TODO: date comparison shouldn't rely on time
    // https://git.idscorporation.ca/abms/api/issues/10
    @AfterMapping
    public void parseDateOfExpiryFromCSV (final LocalAircraftRegistryCsvViewModel source, @MappingTarget LocalAircraftRegistry result) {
        result.setCoaDateOfExpiry(parseCustomLocalDateToLocalDateTime(source.getCoaDateOfExpiry(), "dd-MM-yyyy", Locale.ENGLISH).with(LocalTime.MAX));
    }

}
