create table flight_schedules
(
    id                    serial primary key,
    account_id            int not null references accounts (id),
    flight_service_number varchar not null,
    dep_ad                varchar not null,
    dep_time              varchar not null,
    dest_ad               varchar not null,
    dest_time             varchar not null,
    daily_schedule        varchar not null,
    self_care             bool not null,
    active_indicator      varchar,
    start_date            timestamp with time zone not null,
    end_date              timestamp with time zone,
    version               bigint not null default 0,
    created_at            timestamp with time zone not null default now(),
    created_by            varchar(50) not null,
    updated_at            timestamp with time zone,
    updated_by            varchar(50)
)
