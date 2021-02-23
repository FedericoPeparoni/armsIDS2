drop table if exists exempt_flight_routes;

create table exempt_flight_routes (
        id                              serial primary key,
        departure_aerodrome             varchar(100) not null,
        destination_aerodrome           varchar(100) not null,
        exemption_in_either_direction   bool not null,
        enroute_fees_are_exempt         bool not null default false,
        late_arrival_fees_are_exempt    bool not null default false,
        late_departure_fees_are_exempt  bool not null default false,
        parking_fees_are_exempt         bool not null default false,
        approach_fees_are_exempt        bool not null default false,
        aerodrome_fees_are_exempt       bool not null default false,
        adult_passenger_fees_are_exempt bool not null default false,
        child_passenger_fees_are_exempt bool not null default true,
        flight_handling_indicator       varchar(15),
        flight_notes                    varchar(255),

        created_at                      timestamp not null default now(),
        created_by                      varchar(50) not null,
        updated_at                      timestamp,
        updated_by                      varchar(50)
)
