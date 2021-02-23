package ca.ids.abms.modules.radarsummary;

import org.springframework.stereotype.Component;

@Component("FlightTravelCategoryMapper")
public class FlightTravelCategoryMapper {

    public FlightTravelCategory toFlightTravelCategory ( String flightTravelCategory ) {
        return FlightTravelCategory.mapFromValue(flightTravelCategory);
    }
}
