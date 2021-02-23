-- change unique index on invoice_number adding new column invoice_seq_number_type

ALTER TABLE billing_ledgers
    ADD COLUMN invoice_seq_number_type character varying(30) NOT NULL DEFAULT 'GENERIC';

DROP INDEX IF EXISTS billing_ledgers_invoice_number_i1;
CREATE INDEX billing_ledgers_invoice_number_i1
    ON billing_ledgers (invoice_number, invoice_seq_number_type);
