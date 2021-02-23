create table exempt_flight_routes (
        id                              serial primary key,
        departure_aerodrome_id          int not null,
        destination_aerodrome_id        int not null,
        exemption_in_either_direction   bool not null,
        enroute_fees_are_exempt         bool not null,
        landing_fees_are_exempt         bool not null,
        departure_fees_are_exempt       bool not null,
        parking_fees_are_exempt         bool not null,
        flight_notes                    varchar(255),
        created_at                      timestamp not null default now(),
        created_by                      varchar(50) not null,
        updated_at                      timestamp,
        updated_by                      varchar(50)
)
