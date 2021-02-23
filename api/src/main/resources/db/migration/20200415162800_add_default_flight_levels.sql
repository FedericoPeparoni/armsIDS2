UPDATE airspaces SET airspace_included = true;

UPDATE nominal_routes SET nominal_route_floor = 0, nominal_route_ceiling = 999;

UPDATE exempt_flight_routes SET exempt_route_floor = 0, exempt_route_ceiling = 999;