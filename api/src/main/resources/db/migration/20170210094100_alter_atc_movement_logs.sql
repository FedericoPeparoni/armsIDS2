drop table atc_movement_logs;

create table atc_movement_logs (
        id                          serial primary key,
        date_of_contact             varchar(8) not null,
        flight_id                   varchar(16) unique not null,
        registration                varchar(100),
        aircraft_type               varchar(5) not null,
        operator_icao_code          varchar(3) not null,
        departure_aerodrome         varchar(100),
        destination_aerodrome       varchar(100),
        route                       varchar(255),
        flight_level                varchar(10),
        wake_turbolence             varchar(10),
        fir_entry_point             varchar(14),
        fir_entry_time              varchar(4),
        fir_mid_point               varchar(14),
        fir_mid_time                varchar(4),
        fir_exit_point              varchar(14),
        fir_exit_time               varchar(4),
        flight_category             varchar(10) not null,
        flight_type                 varchar(10) not null,
        day_of_flight               varchar(8),
        departure_time              varchar(4),
        created_at                  timestamp not null default now(),
        created_by                  varchar(50) not null,
        updated_at                  timestamp,
        updated_by                  varchar(50)
)
