alter table billing_ledgers alter column invoice_document drop not null;
alter table billing_ledgers add column invoice_document_type varchar(128);
