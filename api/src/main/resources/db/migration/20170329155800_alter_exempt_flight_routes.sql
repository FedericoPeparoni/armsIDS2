-- No longer needed according to project owner
alter table exempt_flight_routes
    drop column if exists flight_handling_indicator;
