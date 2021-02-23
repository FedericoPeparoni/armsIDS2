create table transaction_types
(
    id   serial primary key,
    name varchar(25) not null unique
);

insert into transaction_types (name) values ('credit');
insert into transaction_types (name) values ('debit');

create table transactions (
    id                    serial primary key,
    account_id            int not null references accounts (id),
    transaction_date_time timestamp    not null,
    description           varchar(100) not null unique,
    transaction_type_id   int references transaction_types (id),
    amount                double precision not null,
    currency_id           int not null references currencies(id),
    exchange_rate_to_usd  double precision not null,
    balance               double precision not null,
    exported              bool not null,
    created_at            timestamp   not null default now(),
    created_by            varchar(50) not null,
    updated_at            timestamp,
    updated_by            varchar(50)
);
