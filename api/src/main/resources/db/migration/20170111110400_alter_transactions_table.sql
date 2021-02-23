-- add missing columns to transactions table

-- clear table
delete from transactions;
--add columns
alter table transactions add column exchange_rate_to_ansp double precision not null;
alter table transactions add column receipt_document bytea;
alter table transactions add column receipt_document_type varchar(128);
alter table transactions add column payment_mechanism varchar(128) not null;
alter table transactions add column payment_reference_number varchar(128) not null;

alter table transactions
    add constraint payment_mechanism_ck1
        check (payment_mechanism in ('cash','credit','debit','cheque','wire','invoice','adjustment'))
;


