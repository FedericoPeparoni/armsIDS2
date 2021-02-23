create table transaction_payments (
    id                    serial primary key,   
    transaction_id        int references transactions (id) on delete set null,
    billing_ledger_id     int references billing_ledgers (id) on delete set null,
    amount                double precision not null,
    currency_id           int not null references currencies(id),
    exchange_rate_to_usd  double precision not null,
    exchange_rate_to_ansp double precision not null,
    created_at            timestamp   not null default now(),
    created_by            varchar(50) not null,
    updated_at            timestamp,
    updated_by            varchar(50)
);