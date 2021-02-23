-- add new currency column, not null constraint will be added later
ALTER TABLE billing_ledgers ADD COLUMN point_of_sale boolean;

UPDATE billing_ledgers b
SET point_of_sale = a.cash_account
FROM accounts a
WHERE a.id = b.account_id;

-- add not null constraint to newly created currency columns
ALTER TABLE billing_ledgers ALTER COLUMN point_of_sale SET NOT NULL;
