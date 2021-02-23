create table pending_transactions (
    id                              serial primary key,
    account_id                      int not null references accounts (id),
    transaction_date_time           timestamp with time zone not null,
    description                     text not null,
    transaction_type_id             int references transaction_types (id),
    local_amount                    double precision not null,
    local_currency_id               int not null references currencies(id),
    exchange_rate_to_usd            double precision not null,
    exchange_rate_to_ansp           double precision not null,
    exported                        bool not null,
    payment_mechanism               text not null,
    payment_reference_number        text not null,
    payment_amount                  double precision not null,
    payment_currency_id             int not null references currencies(id),
    payment_exchange_rate           double precision not null,
    current_approval_level          int references approval_workflows(id) not null,
    previous_approval_level         int references approval_workflows(id),
    related_invoices                text not null,
    created_at                      timestamp with time zone not null default now(),
    created_by                      varchar(50) not null,
    updated_at                      timestamp with time zone,
    updated_by                      varchar(50),
    version                         bigint not null default 0,
    CONSTRAINT pending_transactions_ck1 CHECK
        (payment_mechanism in ('cash', 'credit', 'debit', 'cheque', 'wire', 'invoice', 'adjustment'))
);

create table pending_charge_adjustments (
    id                              serial primary key,
    pending_transaction_id          int references pending_transactions(id) not null,
    invoice_type                    text not null,
    flight_id                       text,
    date                            timestamp with time zone,
    aerodrome                       text,
    charge_description              text,
    charge_amount                   double precision not null,
    created_at                      timestamp with time zone not null default now(),
    created_by                      varchar(50) not null,
    updated_at                      timestamp with time zone,
    updated_by                      varchar(50)
);
