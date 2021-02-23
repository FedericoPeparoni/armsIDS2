-- add bank account name and number columns
ALTER TABLE transactions
    ADD COLUMN bank_account_name character varying(60);
ALTER TABLE transactions
    ADD COLUMN bank_account_number character varying(30);
