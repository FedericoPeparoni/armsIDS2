-- add missing columns to transactions table

--add invoice_exchange_to_ansp column
alter table billing_ledgers add column invoice_exchange_to_ansp double precision;
--update exisiting records
update billing_ledgers set invoice_exchange_to_ansp = 1/invoice_exchange_to_usd;
--set not null
alter table billing_ledgers alter column invoice_exchange_to_ansp set not null;

--add type_id column
alter table billing_ledgers add column type_id int references billing_ledger_types (id);
--update exisiting records to set some value instead of deleting them
update billing_ledgers set type_id=(select id from billing_ledger_types where name='aviation');
--set not null
alter table billing_ledgers alter column type_id set not null;

--add amount_owing column
alter table billing_ledgers add column amount_owing double precision;
--update exisiting records to set some value instead of deleting them
update billing_ledgers set amount_owing=invoice_amount;
--set not null
alter table billing_ledgers alter column amount_owing set not null;

--add final_payment_date column
alter table billing_ledgers add column final_payment_date timestamp;