package com.ubitech.aim.routeparser;

public enum RouteAirspaceCollocationType {
    UNDETERMINED, // 'resolveCollocation()' either did not run yet OR could not identify
                  // collocation type due to incorrect format of input data.
    DISJOINT, // Route and Airspace do not share any space (are disjoint).
    OVERFLIGHT, // Departure and Arrival aerodromes are both outside Airspace. Route crosses
                // Airspace border even number of times.
    ARRIVAL, // Departure aerodrome is outside and Arrival aerodrome is inside Airspace.
             // Route crosses Airspace border uneven number of times.
    DEPARTURE, // Departure aerodrome is inside and Arrival aerodrome is outside Airspace.
               // Route crosses Airspace border uneven number of times.
    DOMESTIC // Departure and Arrival aerodromes are both inside Airspace. Route crosses
             // Airspace border zero or even number of times.
}
