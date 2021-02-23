create table aircraft_registration_prefixes (
    id                            serial primary key,
    aircraft_registration_prefix  varchar(7) not null,
    country_code                  int not null references countries(id),
    pattern                       varchar(400) not null,
    created_at timestamp          not null default now(),
    created_by varchar(50)        not null,
    updated_at timestamp,
    updated_by varchar(50)
);