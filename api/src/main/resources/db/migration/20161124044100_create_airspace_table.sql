create table airspaces (
    id                serial primary key,
    airspace_name     varchar(50) unique not null,
    airspace_type     varchar(3) not null check (airspace_type in ('TMA','FIR')),
    airspace_boundary geometry(Multipolygon,4326) not null,
    created_at        timestamp not null default now(),
    created_by        varchar(50) not null,
    updated_at        timestamp,
    updated_by        varchar(50)
);