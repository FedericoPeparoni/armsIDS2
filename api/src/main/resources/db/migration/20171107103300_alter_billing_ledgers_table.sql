alter table billing_ledgers add column invoice_type varchar(30);

update billing_ledgers set invoice_type = 'non-aviation'     where aviation = false;
update billing_ledgers set invoice_type = 'aviation-iata'    where aviation = true and invoice_document_type like '%pdf%';
update billing_ledgers set invoice_type = 'aviation-noniata' where aviation = true and invoice_document_type like '%spreadsheet%';

alter table billing_ledgers drop column aviation;

-- Please notice: if the below statement fails, the billing_ledgers table contains dirty records to resolve manually
-- before to execute again this script.
alter table billing_ledgers alter column invoice_type set not null;
