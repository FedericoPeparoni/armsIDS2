create table route_cache
(
    id                    serial primary key,
    departure_aerodrome   varchar not null,
    destination_aerodrome varchar not null,
    route_text            varchar not null,
    speed                 int,
    estimated_elapsed     int,
    parsed_route          bytea not null,
    created_at            timestamp with time zone not null default now(),
    created_by            varchar(50) not null,
    updated_at            timestamp,
    updated_by            varchar(50),
    unique (departure_aerodrome, destination_aerodrome, route_text, speed, estimated_elapsed)
);

create index route_cache_index on route_cache (departure_aerodrome, destination_aerodrome, route_text, speed, estimated_elapsed); 

drop table route_caches;