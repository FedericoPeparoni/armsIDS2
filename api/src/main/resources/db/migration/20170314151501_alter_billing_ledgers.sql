alter table billing_ledgers
    add column invoice_filename varchar(256);
    
alter table billing_ledgers
    add column invoice_number varchar(256);

