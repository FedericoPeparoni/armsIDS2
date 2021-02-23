create table passenger_manifests (
    document_number                 serial primary key,
    date_of_flight			        date not null,
    operator                        varchar(100) not null,
    type_of_flight                  varchar(15) not null,
    aircraft_type                   int not null references aircraft_types(id),
    registration_number             varchar(50),
    flight_id 				        varchar(7) not null,
    total_persons                   smallint not null,
    international_passengers        smallint not null,
    domestic_passengers             smallint not null,
    passenger_manifest_image        bytea,
    passenger_manifest_image_type   varchar(100),
    created_at                      timestamp    not null default now(),
    created_by                      varchar(50)  not null,
    updated_at                      timestamp,
    updated_by                      varchar(50)
)
