create table passenger_service_charge_returns (
    id                                      serial primary key,
    flight_id                               varchar(16),
    day_of_flight                           date,
    departure_time                          time,
    transit_passengers                      smallint,
    joining_passengers                      smallint,
    children                                smallint,
    chargeable_itl_passengers               smallint,
    chargeable_domestic_passengers          smallint,
    created_at                              timestamp    not null default now(),
    created_by                              varchar(50)  not null,
    updated_at                              timestamp,
    updated_by                              varchar(50)
);
