create table recurring_charges
(
    id                          serial primary key,
    service_charge_catalogue_id integer                  not null references service_charge_catalogues (id),
    account_id                  integer                  not null references accounts (id),
    start_date                  timestamp with time zone not null,
    end_date                    timestamp with time zone not null,
    created_at                  timestamp with time zone not null default now(),
    created_by                  varchar(50)              not null,
    updated_at                  timestamp with time zone,
    updated_by                  varchar(50)
)
