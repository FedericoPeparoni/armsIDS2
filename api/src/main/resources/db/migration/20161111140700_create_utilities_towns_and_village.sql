create table utilities_towns_and_villages (
    id                              serial primary key,
    town_or_village_name            varchar(128) unique,
    water_utility_schedule          integer not null references utilities_schedules (schedule_id),
    electricity_utility_schedule    integer not null references utilities_schedules (schedule_id),
    created_at timestamp    not null default now(),
    created_by varchar(50)  not null,
    updated_at timestamp,
    updated_by varchar(50)
);
