alter table currencies drop column if exists exchange_rate_to_usd restrict;
alter table currencies drop column if exists exchange_rate_valid_from_date restrict;
alter table currencies drop column if exists exchange_rate_valid_to_date restrict;

create table currency_exchange_rates (
    id                            serial primary key,
    currency                      int not null references currencies(id),
    exchange_rate_to_usd          double precision not null,
    exchange_rate_valid_from_date timestamp with time zone,
    exchange_rate_valid_to_date   timestamp with time zone,
    created_at timestamp          not null default now(),
    created_by varchar(50)        not null,
    updated_at timestamp,
    updated_by varchar(50)
);
