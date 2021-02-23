create table unspecified_aircraft_types
(
    id                       serial primary key,
    text_identifier          varchar(255) unique,
    name                     varchar(100),
    mtow                     double precision,
    status                   varchar(30),
    created_at timestamp     not null default now(),
    created_by varchar(50)   not null,
    updated_at timestamp,
    updated_by varchar(50)
);
