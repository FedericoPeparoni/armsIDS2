create table average_mtow_factors (
    id                  serial primary key,
    upper_limit         double precision unique not null,
    average_mtow_factor double precision not null,
    created_at          timestamp not null default now(),
    created_by          varchar(50) not null,
    updated_at          timestamp,
    updated_by          varchar(50)
);
