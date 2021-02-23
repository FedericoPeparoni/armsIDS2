--drop obsolete constraint in transactions
alter table transactions drop constraint transactions_description_key;

--drop obsolete billing_ledgers columns 
alter table billing_ledgers drop column if exists type_id;
drop table billing_ledger_types;
alter table billing_ledgers drop column if exists payment_amount;
alter table billing_ledgers drop column if exists payment_currency;
alter table billing_ledgers drop column if exists payment_exchange_to_usd;
alter table billing_ledgers drop column if exists payment_date;
