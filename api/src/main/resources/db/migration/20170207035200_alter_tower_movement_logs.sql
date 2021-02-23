drop table tower_movement_logs;

create table tower_movement_logs (
        id                          serial primary key,
        date_of_contact             varchar(8) not null,
        flight_id                   varchar(16) unique not null,
        registration                varchar(100),
        aircraft_type               varchar(5) not null,
        operator_icao_code          varchar(3) not null,
        departure_aerodrome         varchar(100) not null,
        departure_contact_time      varchar(4),
        destination_aerodrome       varchar(100) not null,
        destination_contact_time    varchar(4),
        route                       varchar(255),
        flight_level                varchar(10),
        flight_crew                 int not null,
        passengers                  int not null,
        flight_category             varchar(10) not null,
        day_of_flight               varchar(8),
        departure_time              varchar(4),
        created_at                  timestamp not null default now(),
        created_by                  varchar(50) not null,
        updated_at                  timestamp,
        updated_by                  varchar(50)
)
