package ca.ids.abms.modules.cronos.v1;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import ca.ids.abms.modules.flightmovements.FlightMovement;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CronosFlightMovementMapper {

    @Mapping (target = "id", source = "id")
    @Mapping (target = "aircraftType", source = "aircraftType")
    @Mapping (target = "dateOfFlight", ignore = true) // convert 
    @Mapping (target = "depAd", source = "depAd")
    @Mapping (target = "depTime", source = "depTime")
    @Mapping (target = "estimatedElapsedTime", source = "estimatedElapsedTime")
    @Mapping (target = "destAd", source = "destAd")
    @Mapping (target = "flightId", source = "flightId")
    @Mapping (target = "flightType", source = "flightType")
    @Mapping (target = "fplRoute", source = "fplRoute")
    @Mapping (target = "item18", source = "otherInfo")
    @Mapping (target = "status", source = "status")
    @Mapping (target = "wakeTurb", source = "wakeTurb")
    @Mapping (target = "cruisingSpeedOrMachNumber", source = "cruisingSpeedOrMachNumber")
    @Mapping (target = "flightRules", source = "flightRules")
    @Mapping (target = "flightLevel", source = "flightLevel")
    @Mapping (target = "alternateAd", ignore = true) // missing
    @Mapping (target = "alternateAd2", ignore = true) // missing
    public CronosFlightMovementViewModel toViewModel (FlightMovement flightMovement);
    
    @AfterMapping
    public default void postProcessViewModel(@MappingTarget final CronosFlightMovementViewModel target, FlightMovement source) {
        target.setDateOfFlight (source.getDateOfFlight() == null ? null : source.getDateOfFlight().toLocalDate());
    }
    
}
