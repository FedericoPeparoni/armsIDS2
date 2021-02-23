ALTER TABLE airspaces
    ADD COLUMN airspace_included boolean default true;
    
ALTER TABLE airspaces
    ADD COLUMN airspace_ceiling double precision;
    
ALTER TABLE nominal_routes
    ADD COLUMN nominal_route_floor double precision;
    
ALTER TABLE nominal_routes
    ADD COLUMN nominal_route_ceiling double precision;
    
ALTER TABLE exempt_flight_routes
    ADD COLUMN exempt_route_floor double precision;
    
ALTER TABLE exempt_flight_routes
    ADD COLUMN exempt_route_ceiling double precision;
    
ALTER TABLE route_segments
    ADD COLUMN flight_level double precision;
    
ALTER TABLE route_cache
    ADD COLUMN flight_level double precision;
    