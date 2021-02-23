-- add required supporting document columns
ALTER TABLE transactions
    ADD COLUMN supporting_document bytea;

ALTER TABLE transactions
    ADD COLUMN supporting_document_name character varying(128);

ALTER TABLE transactions
    ADD COLUMN supporting_document_type character varying(128);

ALTER TABLE pending_transactions
    ADD COLUMN supporting_document bytea;

ALTER TABLE pending_transactions
    ADD COLUMN supporting_document_name character varying(128);

ALTER TABLE pending_transactions
    ADD COLUMN supporting_document_type character varying(128);
