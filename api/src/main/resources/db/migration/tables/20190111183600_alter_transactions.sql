-- add bank account external accounting system id column
ALTER TABLE transactions
    ADD COLUMN bank_account_external_accounting_system_id character varying(20);
