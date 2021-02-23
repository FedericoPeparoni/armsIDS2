ALTER TABLE approval_workflows ADD COLUMN approval_document_required Boolean default false;

ALTER TABLE transactions ADD COLUMN approval_document bytea;
ALTER TABLE pending_transactions ADD COLUMN approval_document bytea;

ALTER TABLE transactions ADD COLUMN approval_document_name varchar(128);
ALTER TABLE pending_transactions ADD COLUMN approval_document_name varchar(128);

ALTER TABLE transactions ADD COLUMN approval_document_type varchar(128);;
ALTER TABLE pending_transactions ADD COLUMN approval_document_type varchar(128);