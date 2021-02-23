-- Remove all inconsisten transaction payments
delete from transaction_payments where transaction_id is null;

-- Add a not null constraint into the transaction payments
alter table transaction_payments alter column transaction_id set not null;
