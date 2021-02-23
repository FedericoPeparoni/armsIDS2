drop index billing_ledgers_invoice_number_i1;
create index billing_ledgers_invoice_number_i1
    on billing_ledgers (invoice_number);