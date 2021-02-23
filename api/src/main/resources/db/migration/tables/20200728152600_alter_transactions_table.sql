-- change unique index on receipt_number adding new column receipt_seq_number_type

ALTER TABLE transactions
    ADD COLUMN receipt_seq_number_type character varying(30) NOT NULL DEFAULT 'GENERIC';

DROP INDEX IF EXISTS transactions_receipt_number_i1;
CREATE UNIQUE INDEX transactions_receipt_number_i1
    ON transactions (receipt_number, receipt_seq_number_type);
