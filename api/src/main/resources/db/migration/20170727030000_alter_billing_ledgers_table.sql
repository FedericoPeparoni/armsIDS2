alter table billing_ledgers add column payment_mode varchar(10),
    add constraint billing_ledgers_payment_mode_ck
        check (payment_mode in ('cash', 'credit'));