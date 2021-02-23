-- remove user constraint from billing ledgers to account for automated processing
 ALTER TABLE billing_ledgers ALTER COLUMN user_id DROP NOT NULL
