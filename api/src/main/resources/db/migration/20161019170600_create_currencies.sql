create table currencies (
    id                            serial primary key,
    currency_code                 varchar(3) unique not null,
    currency_name                 varchar(25) unique not null,
    country_code                  int not null references countries(id),
    decimal_places                int not null,
    symbol                        varchar(3) not null,
    exchange_rate_to_usd          double precision not null,
    exchange_rate_valid_from_date date,
    exchange_rate_valid_to_date   date,
    active                        bool not null,
    created_at timestamp          not null default now(),
    created_by varchar(50)        not null,
    updated_at timestamp,
    updated_by varchar(50)
);

insert into currencies (currency_code, currency_name, country_code, decimal_places, symbol, 
                        exchange_rate_to_usd, active, created_by)
            values     ('CAD', 'Canadian Dollar', 1, 2, '$', 0.75, true, 'system');
insert into currencies (currency_code, currency_name, country_code, decimal_places, symbol, 
                        exchange_rate_to_usd, active, created_by)
            values     ('USD', 'US Dollar', 2, 2, '$', 1, true, 'system');            
