create table exempt_flight_status (
        id                              serial primary key,
        flight_item_type                varchar(50) not null,
        flight_item_value               varchar(100) not null,
        exemption_in_either_direction   bool not null,
        enroute_fees_are_exempt         bool not null default(false),
        approach_fees_exempt            bool not null default(false),
        aerodrome_fees_exempt           bool not null default(false),
        late_departure_fees_exempt      bool not null default(false),
        late_arrival_fees_exempt        bool not null default(false),
        parking_fees_are_exempt         bool not null default(false),
        adult_passenger_fees_exempt     bool not null default(false),
        child_passenger_fees_exempt     bool not null default(true),
        flight_notes                    varchar(255),

        created_at                      timestamp not null default now(),
        created_by                      varchar(50) not null,
        updated_at                      timestamp,
        updated_by                      varchar(50)
)

