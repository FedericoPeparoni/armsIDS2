-- add columns to billing_centers table to manage extra sequence numbers if feature enable

alter table billing_centers add column iata_invoice_sequence_number  int not null default 0;
alter table billing_centers add column receipt_cheque_sequence_number int not null default 0;
alter table billing_centers add column receipt_wire_sequence_number int not null default 0;

