package ca.ids.abms.modules.aerodromeoperationalhours;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface AerodromeOperationalHoursMapper {

    List<AerodromeOperationalHoursViewModel> toViewModel(Iterable<AerodromeOperationalHours> aerodromeOperationalHours);

    AerodromeOperationalHoursViewModel toViewModel(AerodromeOperationalHours aerodromeOperationalHours);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    AerodromeOperationalHours toModel(AerodromeOperationalHoursViewModel dto);

    @Mapping(target = "aerodrome", source = "aerodrome.aerodromeName")
    AerodromeOperationalHoursCsvExportModel toCsvModel(AerodromeOperationalHours item);

    List<AerodromeOperationalHoursCsvExportModel> toCsvModel(Iterable<AerodromeOperationalHours> items);
}
