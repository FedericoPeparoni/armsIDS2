create table tower_movement_logs (
        id                     serial primary key,
        flight_id              varchar(16) not null,
        day_of_flight          Date not null,
        departure_time         Time not null,
        registration           varchar(100),
        operator_icao_code     varchar(3) not null,
        departure_aerodrome    varchar(100) not null,
        destination_aerodrome  varchar(100) not null,
        route                  varchar(255),
        flight_crew            int not null,
        passengers             int not null,
        flight_category        varchar(10) not null,

        created_at                      timestamp not null default now(),
        created_by                      varchar(50) not null,
        updated_at                      timestamp,
        updated_by                      varchar(50)
)
