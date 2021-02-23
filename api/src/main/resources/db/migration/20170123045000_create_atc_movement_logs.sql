create table atc_movement_logs (
        id                     serial primary key,
        flight_id              varchar(16) not null,
        day_of_flight          Date not null,
        departure_time         Time not null,
        registration           varchar(100),
        operator_icao_code     varchar(3) not null,
        departure_aerodrome    varchar(100) not null,
        destination_aerodrome  varchar(100) not null,

        fir_entry_point    varchar(14) not null,
        fir_entry_time     varchar(4) not null,
        fir_mid_point      varchar(14) not null,
        fir_mid_time       varchar(4) not null,
        fir_exit_point    varchar(14) not null,
        fir_exit_time     varchar(4) not null,

        flight_crew            int not null,
        passengers             int not null,
        flight_category        varchar(10) not null,
        flight_type            varchar(10) not null,

        created_at                      timestamp not null default now(),
        created_by                      varchar(50) not null,
        updated_at                      timestamp,
        updated_by                      varchar(50)
)
