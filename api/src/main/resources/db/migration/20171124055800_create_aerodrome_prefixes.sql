create table aerodrome_prefixes (
    id                            serial primary key,
    aerodrome_prefix              varchar(4) not null,
    country_code                  int not null references countries(id),
    created_at timestamp          not null default now(),
    created_by varchar(50)        not null,
    updated_at timestamp,
    updated_by varchar(50)
);
