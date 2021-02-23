
alter table nominal_routes alter column nominal_route_floor set not null;
alter table nominal_routes alter column nominal_route_ceiling set not null;

-- drop existing constraint 
alter table nominal_routes
    drop constraint if exists nominal_routes_uniq1;

-- create new constraint
alter table nominal_routes
    add constraint nominal_routes_uniq1 unique (pointa, pointb,nominal_route_floor,nominal_route_ceiling)
;

alter table exempt_flight_routes alter column exempt_route_floor set not null;
alter table exempt_flight_routes alter column exempt_route_ceiling set not null;

alter table exempt_flight_routes drop constraint if exists unique_departure_destination;

alter table exempt_flight_routes add constraint unique_departure_destination UNIQUE (departure_aerodrome, destination_aerodrome,exempt_route_floor,exempt_route_ceiling);