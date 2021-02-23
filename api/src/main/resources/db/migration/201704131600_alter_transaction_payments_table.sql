--add column
alter table transaction_payments add column is_account_credit boolean;
--update existing records
update transaction_payments set is_account_credit=false;
--set not null
alter table transaction_payments alter column is_account_credit set not null;