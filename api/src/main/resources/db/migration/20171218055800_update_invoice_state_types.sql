ALTER TABLE billing_ledgers ADD COLUMN invoice_state_tmp VARCHAR;

UPDATE billing_ledgers AS bl SET invoice_state_tmp = ist.name FROM invoice_state_types AS ist WHERE bl.invoice_state_type_id = ist.id;

ALTER TABLE billing_ledgers ALTER COLUMN invoice_state_tmp SET NOT NULL;

ALTER TABLE billing_ledgers DROP COLUMN invoice_state_type_id;

ALTER TABLE billing_ledgers RENAME COLUMN invoice_state_tmp TO invoice_state_type;

DROP TABLE invoice_state_types;

