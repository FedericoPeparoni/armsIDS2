create table regional_countries (
    id                 serial primary key,
    country_id         int unique not null references countries (id),
    created_at         timestamp    not null default now(),
    created_by         varchar(50)  not null,
    updated_at         timestamp,
    updated_by         varchar(50)
);
