update billing_ledgers set invoice_number = 'XXX_' || id where invoice_number is null;
alter table billing_ledgers alter column invoice_number set not null;
