-- add transaction_description column to hold debit note description
ALTER TABLE billing_ledgers
    ADD COLUMN transaction_description CHARACTER VARYING(100);
