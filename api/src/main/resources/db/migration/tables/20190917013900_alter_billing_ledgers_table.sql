-- add new currency column, not null constraint will be added later
ALTER TABLE billing_ledgers ADD COLUMN proforma boolean NOT NULL DEFAULT false;
