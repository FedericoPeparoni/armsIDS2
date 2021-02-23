create table repositioning_aerodrome_clusters (
        id                                   serial primary key,
        repositioning_aerodrome_cluster_name varchar(50) unique not null,
        enroute_fees_are_exempt              bool not null default false,
        approach_fees_are_exempt             bool not null default false,
        aerodrome_fees_are_exempt            bool not null default false,
        late_arrival_fees_are_exempt         bool not null default false,
        late_departure_fees_are_exempt       bool not null default false,
        parking_fees_are_exempt              bool not null default false,
        adult_passenger_fees_are_exempt      bool not null default false,
        child_passenger_fees_are_exempt      bool not null default true,
        flight_notes                         varchar(255) not null,
        created_at                           timestamp not null default now(),
        created_by                           varchar(50) not null,
        updated_at                           timestamp,
        updated_by                           varchar(50)
)
