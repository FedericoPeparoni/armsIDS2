create table utilities_schedules (
    schedule_id             serial primary key,
    schedule_type           varchar(10) not null,
    minimum_charge          integer not null,
    created_at timestamp    not null default now(),
    created_by varchar(50)  not null,
    updated_at timestamp,
    updated_by varchar(50)
);

create table utilities_range_brackets (
    id                      serial primary key,
    schedule_id             integer not null references utilities_schedules (schedule_id),
    range_top_end           double precision not null,
    unit_price              double precision not null
);
