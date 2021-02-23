package ca.ids.abms.modules.manifests;

import ca.ids.abms.modules.aircraft.AircraftType;
import ca.ids.abms.modules.aircraft.AircraftTypeViewModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface PassengerManifestMapper {

    List<PassengerManifestViewModel> toViewModel(Iterable<PassengerManifest> items);

    @Mapping(target = "documentContents", ignore = true)
    @Mapping(target = "documentMimeType", source = "passengerManifestImageType")
    @Mapping(target = "documentFilename", source = "documentNumber")
    PassengerManifestViewModel toViewModel(PassengerManifest item);

    @Mapping(target = "aircraftImage", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    AircraftTypeViewModel toViewModel(AircraftType item);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "passengerManifestImage", ignore = true)
    @Mapping(target = "passengerManifestImageType", ignore = true)
    PassengerManifest toModel(PassengerManifestViewModel dto);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "passengerManifestImage", ignore = true)
    @Mapping(target = "passengerManifestImageType", ignore = true)
    @Mapping(target = "version", ignore = true)
    PassengerManifest toModel(PassengerManifestCsvViewModel dto);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "aircraftType", ignore = true)
    @Mapping(target = "aircraftName", ignore = true)
    @Mapping(target = "aircraftImage", ignore = true)
    @Mapping(target = "manufacturer", ignore = true)
    @Mapping(target = "wakeTurbulenceCategory", ignore = true)
    @Mapping(target = "maximumTakeoffWeight", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "aircraftRegistrations", ignore = true)
    AircraftType toModel(AircraftTypeViewModel dto);
}
