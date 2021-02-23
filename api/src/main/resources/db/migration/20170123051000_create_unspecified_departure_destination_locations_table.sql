create table unspecified_departure_destination_locations
(
    id                   serial primary key,
	text_identifier      varchar(255) not null,
    name                 varchar(100) not null,
    maintained           bool not null,
    aerodrome_identifier varchar(100) references aerodromes (aerodrome_name),
    latitude             double precision not null,
    longitude            double precision not null,
    status               varchar(30),         
    created_at 		     timestamp with time zone not null default now(),
    updated_at 		     timestamp with time zone,
    created_by           varchar(50),
    updated_by           varchar(50),
    unique (text_identifier, name)
);

