create table billing_ledger_types
(
    id   serial primary key,
    name varchar(25) not null unique
);

insert into billing_ledger_types (name) values ('aviation');
insert into billing_ledger_types (name) values ('non-aviation');

create table invoice_state_types
(
    id   serial primary key,
    name varchar(25) not null unique
);

insert into invoice_state_types (name) values ('new');
insert into invoice_state_types (name) values ('approved');
insert into invoice_state_types (name) values ('published');
insert into invoice_state_types (name) values ('paid');


create table billing_ledgers (
    id                      serial primary key,
    account_id              int not null references accounts (id),
    invoice_period_or_date  timestamp not null,
    aviation                bool not null,
    invoice_state_type_id   int references invoice_state_types (id),
    payment_due_date        timestamp not null,
    user_id                 int not null,
    invoice_document        bytea not null,
    invoice_amount          double precision not null,
    invoice_currency        int not null references currencies(id),
    invoice_exchange_to_usd double precision not null,
    invoice_date_of_issue   timestamp not null,
    payment_amount          double precision not null,
    payment_currency        int not null references currencies(id),
    payment_exchange_to_usd double precision not null,
    payment_date            timestamp not null,
    exported                bool not null,
    created_at              timestamp not null default now(),
    created_by              varchar(50) not null,
    updated_at              timestamp,
    updated_by              varchar(50)
);
