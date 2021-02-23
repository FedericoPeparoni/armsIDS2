package ca.ids.abms.modules.flight;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface FlightReassignmentMapper {
    
    default Collection<String> mapAerodromeIdentifiers(
            Collection<FlightReassignmentAerodrome> aerodromeIdentifiers) {
        return aerodromeIdentifiers.stream().map(FlightReassignmentAerodrome::getAerodromeIdentifier)
                .collect(Collectors.toList());
    }

    /* Frontend to backend mapping */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "aerodromeIdentifiers", ignore = true)
    public FlightReassignment toModel(FlightReassignmentViewModel dto);
    
    /* Backend to Frontend mapping */
    List<FlightReassignmentViewModel> toViewModel(Iterable<FlightReassignment> items);

    FlightReassignmentViewModel toViewModel(FlightReassignment item);

    /* Csv Export mapping */
    @Mapping(target = "account", source = "account.name")
    FlightReassignmentCsvExportModel toCsvModel(FlightReassignment item);

    List<FlightReassignmentCsvExportModel> toCsvModel(Iterable<FlightReassignment> items);

    @AfterMapping
    default void resolveCsvExportModel(final FlightReassignment source,
                                       @MappingTarget final FlightReassignmentCsvExportModel target) {
        target.setAerodromes(setAerodromes(source));
        target.setFlightType(setFlightType(source));
        target.setFlightScope(setFlightScope(source));
        target.setAircraftNationality(setAircraftNationality(source));
    }

    default String setAerodromes(final FlightReassignment source) {
        List<String> list = new ArrayList<>();
        source.getAerodromeIdentifiers().stream().limit(14).forEach(r -> list.add(r.getAerodromeIdentifier()));
        return list.toString().replaceAll("[\\[\\]]", "");
    }

    default String setFlightType(final FlightReassignment source) {
        List<String> list = new ArrayList<>();
        if (source.getAppliesToTypeArrival()) {
            list.add("ARR");
        }
        if (source.getAppliesToTypeDeparture()) {
            list.add("DEP");
        }
        if (source.getAppliesToTypeDomestic()) {
            list.add("DOM");
        }
        if (source.getAppliesToTypeOverflight()) {
            list.add("OVF");
        }
        return list.toString().replaceAll("[\\[\\]]", "");
    }

    default String setFlightScope(final FlightReassignment source) {
        List<String> list = new ArrayList<>();
        if (source.getAppliesToScopeDomestic()) {
            list.add("DOM");
        }
        if (source.getAppliesToScopeInternational()) {
            list.add("INT");
        }
        if (source.getAppliesToScopeRegional()) {
            list.add("REG");
        }
        return list.toString().replaceAll("[\\[\\]]", "");
    }

    default String setAircraftNationality(final FlightReassignment source) {
        List<String> list = new ArrayList<>();
        if (source.getAppliesToNationalityForeign()) {
            list.add("INT");
        }
        if (source.getAppliesToNationalityNational()) {
            list.add("NAT");
        }
        return list.toString().replaceAll("[\\[\\]]", "");
    }
}
