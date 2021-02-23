create table aircraft_type_exemptions (
    id                 serial primary key,
    aircraft_type_id   int unique not null references aircraft_types (id),
    enroute            bool not null default false,
    landing            bool not null default false,
    departure          bool not null default false,
    parking            bool not null default false,
    adult              bool not null default false,
    child              bool not null default false,
    flight_notes       varchar(255) not null,
    created_at         timestamp    not null default now(),
    created_by         varchar(50)  not null,
    updated_at         timestamp,
    updated_by         varchar(50)
);
