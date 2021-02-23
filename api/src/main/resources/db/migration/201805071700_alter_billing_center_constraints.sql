update billing_centers set invoice_sequence_number = 1
 where invoice_sequence_number is null;

alter table billing_centers
    alter column invoice_sequence_number set default 1,
    alter column invoice_sequence_number set not null
;
